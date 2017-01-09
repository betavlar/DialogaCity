/*
 * Copyright © 2011-2016 HERE Global B.V. and its affiliate(s).
 * All rights reserved.
 * The use of this software is conditional upon having a separate agreement
 * with a HERE company for the use or utilization of this software. In the
 * absence of such agreement, the use of the software is not allowed.
 */

package com.here.android.tutorial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.WindowManager;

import com.here.android.mpa.ar.ARRadarItem;
import com.here.android.mpa.ar.ARRadarProperties;

/**
 * Sample Radar implementation class
 * Because radar data is updated frequently, rendering must be performed on a separate thread.
 * TextureView is the recommend option for implementing this feature since it behaves as a view
 * (supports animations etc) and also provides the support for rendering in a separate thread.
 */
public class ARRadar extends TextureView implements SurfaceTextureListener{

    private Paint paint;
    private Path path;

    private static final int VISUAL_CONE_AREA_ANGLE_DEGREES_PORTRAIT = 48; // 48 degrees for upper cone, only used in case camera fov value is incorrect
    private static final int VISUAL_CONE_AREA_ANGLE_DEGREES_LANDSCAPE = 64; // 64 degrees for upper cone, only used in case camera fov value is incorrect
    private static final int VISUAL_CONE_STEP = 4;
    private static final float VISUAL_CONE_ANGLE_RATIO = 1.6f;

    private static final int DIRECTION_CONE_LOWER_START_ANGLE = 262;
    private static final int DIRECTION_CONE_UPPER_START_ANGLE = 256;
    private static final int DIRECTION_CONE_STEP = 2;
    private static final int DIRECTION_CONE_LOWER_STEP_COUNT = 16;
    private static final int DIRECTION_CONE_UPPER_STEP_COUNT = 13;
    private static final float DIRECTION_CONE_PADDING_RATIO = 0.75f;

    private static final int SEMI_TRANPARENT = 125;
    private static final int OPAQUE = 255;
    private static final double RATIO = Math.PI / 180;
    private static final int BORDER_SIZE = 2;
    private static final int POSITION_RATIO = 9;
    private static final float NORTH_X_RATIO = 0.2f;
    private static final float NORTH_Y_RATIO = 0.32f;
    private static final int NORTH_TEXT_SIZE_RATIO = 10;
    private static final float DOT_RATIO = 0.018f;
    private static final float DOT_MIN_RADIUS = 1f;
    private static final float DOT_MAX_RADIUS = 4f;
    private double angle = 0;
    private Display m_display;

    /// Collection of the dots to display in the radar
    private SparseArray<ARRadarDot> itemToDot = new SparseArray<ARRadarDot>();
    private List<ARRadarDot> radarDots = Collections.synchronizedList(new ArrayList<ARRadarDot>());

    // Radar rendering thread
    private RadarThread radarThread;

    // Semaphore used for pausing the rendering thread while waiting for radar updates
    private Semaphore radarSemaphore;

    // Camera horizontal field of view
    private float m_horizontalFov;

    /**
     * Constructor
     */
    public ARRadar(Context context, float horizontalFov) {
        super(context);
        paint = new Paint();
        path = new Path();
        WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        m_display = mgr.getDefaultDisplay();
        setOpaque(false);
        radarSemaphore = new Semaphore(1);
        setSurfaceTextureListener(this);
        m_horizontalFov = horizontalFov;
    }

    /**
     * Constructor
     */
    public ARRadar(Context context, AttributeSet attrs, float horizontalFov) {
        super(context, attrs);
        paint = new Paint();
        path = new Path();
        WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        m_display = mgr.getDefaultDisplay();
        setOpaque(false);
        radarSemaphore = new Semaphore(1);
        setSurfaceTextureListener(this);
        m_horizontalFov = horizontalFov;
    }

    /**
     * SurfaceTextureListener methods implementation
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
            int height) {
        // Start the radar rendering thread
        radarThread = new RadarThread(this);
        radarThread.setActive(true);
        radarThread.start();
        radarSemaphore.release();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        // Stop the radar rendering thread
        if (radarThread == null || !radarThread.isActive()) {
            return true;
        }
        radarThread.setActive(false);
        radarSemaphore.release();
        try {
            radarThread.join(100);
            radarThread = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
            int height) {
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    /**
     * Radar rendering thread class
     */
    private class RadarThread extends Thread {

        private AtomicBoolean active = new AtomicBoolean(false);
        private final TextureView surface;

        /**
         * Constructor
         */
        public RadarThread(TextureView surface) {
            this.surface = surface;
        }

        /**
         * Set the active state of the thread
         */
        void setActive(boolean active) {
            this.active.set(active);
        }

        /**
         * Returns thread current active state
         */
        boolean isActive() {
            return active.get();
        }

        /**
         * Main loop of the rendering thread
         */
        @Override
        public void run() {
            while (true) {
                try {
                    // Wait for radar updates
                    radarSemaphore.acquire();
                    radarSemaphore.drainPermits();
                    if (!active.get()) {
                        break;
                    }
                    // Draw the radar content on the texture view canvas
                    final Canvas canvas = surface.lockCanvas();
                    if (canvas != null) {
                        doDraw(canvas);
                        surface.unlockCanvasAndPost(canvas);
                    }
                } catch (Exception ex) {
                    // This is bad, so we terminate the drawing thread
                    ex.printStackTrace();
                    setActive(false);
                    radarSemaphore.release();
                }
            }
        }
    }

    /**
     * Draw the radar content
     */
    private void doDraw(Canvas canvas) {
        // Clear canvas
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        // Draw radar content
        drawCompass(canvas);
        drawItems(canvas);
    }

    /**
     * Draw the compass part of the radar (radar circle, border, user position and field of view cone)
     */
    private void drawCompass(Canvas canvas) {
        path.reset();
        paint.reset();

        int size = this.getWidth() / 2;
        int positionSize = size / POSITION_RATIO;
        int conepadding = positionSize / 2 + 1;
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        // Circle fill
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setAlpha(SEMI_TRANPARENT);
        canvas.drawCircle(size, size, size, paint);

        // View Cone
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.LTGRAY);
        paint.setAlpha(SEMI_TRANPARENT);

        int rotation =  m_display.getRotation();
        boolean portrait = (rotation == Surface.ROTATION_0) || (rotation == Surface.ROTATION_180);

        final float VIEW_CONE_AREA_UPPER_ANGLE_DEGREES = m_horizontalFov != 0.0f ? m_horizontalFov
                : portrait ? VISUAL_CONE_AREA_ANGLE_DEGREES_PORTRAIT : VISUAL_CONE_AREA_ANGLE_DEGREES_LANDSCAPE;

        final float VIEW_CONE_AREA_LOWER_ANGLE_DEGREES = VIEW_CONE_AREA_UPPER_ANGLE_DEGREES * VISUAL_CONE_ANGLE_RATIO;

        double shortAngle = 270 - (VIEW_CONE_AREA_LOWER_ANGLE_DEGREES / 2);
        double longAngle = 270 - (VIEW_CONE_AREA_UPPER_ANGLE_DEGREES / 2);
        float lowerStepCount = VIEW_CONE_AREA_LOWER_ANGLE_DEGREES / VISUAL_CONE_STEP;
        float upperStepCount = VIEW_CONE_AREA_UPPER_ANGLE_DEGREES / VISUAL_CONE_STEP;
        float x, y;
        for (int i = 0; i <= lowerStepCount; i++, shortAngle += VISUAL_CONE_STEP) {
            x = size + (float) ((positionSize + conepadding) * Math.cos(shortAngle * RATIO));
            y = size + (float) ((positionSize + conepadding) * Math.sin(shortAngle * RATIO));
            if (i == 0) {

                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        longAngle += upperStepCount * VISUAL_CONE_STEP;
        for (int i = 0; i <= upperStepCount; i++, longAngle -= VISUAL_CONE_STEP) {
            x = size + (float) ((size - BORDER_SIZE) * Math.cos(longAngle * RATIO));
            y = size + (float) ((size - BORDER_SIZE) * Math.sin(longAngle * RATIO));
            path.lineTo(x, y);
        }
        path.close();
        canvas.drawPath(path, paint);

        // My position fill
        paint.setColor(Color.argb(OPAQUE, 47, 154, 45));
        paint.setStrokeWidth(5);
        paint.setAlpha(OPAQUE);
        canvas.drawCircle(size, size, positionSize, paint);

        // My position border
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        canvas.drawCircle(size, size, positionSize + BORDER_SIZE, paint);

        // Circle border
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        paint.setAlpha(SEMI_TRANPARENT);
        canvas.drawCircle(size, size, size - BORDER_SIZE / 2, paint);
    }

    /**
     * Draw the radar items (dots and north direction)
     */
    private void drawItems(Canvas canvas) {
        path.reset();
        paint.reset();
        int size = this.getWidth() / 2;
        // Rotate canvas with radar angle
        if (angle != 0) {
            canvas.rotate((float) angle, size, size);
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        int conepadding = (int) (size * DIRECTION_CONE_PADDING_RATIO);

        // North Cone
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
        paint.setAlpha(SEMI_TRANPARENT);

        float x, y, xInit, yInit, yUpInit = 0;
        int angle = DIRECTION_CONE_UPPER_START_ANGLE;
        xInit = size + (float) (conepadding * Math.cos(DIRECTION_CONE_LOWER_START_ANGLE * RATIO));
        yInit = size + (float) (conepadding * Math.sin(DIRECTION_CONE_LOWER_START_ANGLE * RATIO));
        path.moveTo(xInit, yInit);
        for (int i = 0; i <= DIRECTION_CONE_UPPER_STEP_COUNT; i++) {
            x = size + (float) ((size - BORDER_SIZE) * Math.cos(angle * RATIO));
            y = size + (float) ((size - BORDER_SIZE) * Math.sin(angle * RATIO));
            path.lineTo(x, y);
            if (i == 0) {
                yUpInit = y;
            }
            angle += DIRECTION_CONE_STEP;
        }
        x = size
                + (float) (conepadding * Math
                        .cos((DIRECTION_CONE_LOWER_START_ANGLE + DIRECTION_CONE_LOWER_STEP_COUNT)
                                * RATIO));
        y = size
                + (float) (conepadding * Math
                        .sin((DIRECTION_CONE_LOWER_START_ANGLE + DIRECTION_CONE_LOWER_STEP_COUNT)
                                * RATIO));
        path.lineTo(x, y);
        path.close();
        canvas.drawPath(path, paint);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAlpha(OPAQUE);
        paint.setStrokeWidth(1f);
        paint.setTextSize(this.getWidth() / NORTH_TEXT_SIZE_RATIO);
        int offsetX = (int) Math.floor((x - xInit) * NORTH_X_RATIO);
        int offsetY = (int) Math.floor((y - yUpInit) * NORTH_Y_RATIO);
        canvas.drawText("N", xInit + offsetX, y - offsetY, paint);

        // Draw Points
        paint.setStyle(Paint.Style.FILL);
        synchronized (radarDots) {
            for (ARRadarDot radarDot : radarDots) {

                if (!radarDot.getDimmed()) {
                    paint.setColor(Color.WHITE);
                } else {
                    paint.setColor(Color.argb(OPAQUE, 119, 119, 119));
                }
                float radius = Math.min(
                        Math.max(DOT_MIN_RADIUS, (float) DOT_RATIO * this.getWidth()),
                        DOT_MAX_RADIUS);
                canvas.drawCircle(size + (float) radarDot.getTranslateX(),
                        size + (float) radarDot.getTranslateY(), radius, paint);
            }
        }

    }

    /**
     * Update the radar properties(items and angle) and initiate rendering
     */
    public synchronized void Update(ARRadarProperties radarProperties) {
        UpdateRadarDots(radarProperties);
        angle = radarProperties.getAngle();
        radarSemaphore.release();
    }

    /**
     * Update radar items
     */
    private void UpdateRadarDots(ARRadarProperties radarProperties) {
        if (radarProperties.getItems() == null) {
            clear();
            return;
        }

        float dimmingLimit = radarProperties.getDimmingLimit();
        float areaStart = radarProperties.getFrontPlaneStart();
        float areaSize = radarProperties.getFrontPlaneEnd() - dimmingLimit;
        float radarRadius = this.getWidth() / 2;
        float positionSize = radarRadius / 9;
        float myPosRadius = positionSize + BORDER_SIZE + 5; // margin and dot's
                                                            // radius
                                                            // included
        float conepadding = positionSize / 2 + BORDER_SIZE;
        float scaledRadarRadius = radarRadius - myPosRadius - conepadding;
        List<ARRadarDot> existingDots = new ArrayList<ARRadarDot>(radarDots);
        List<ARRadarItem> items = radarProperties.getItems();

        for (ARRadarItem arItem : items) {
            // Scale down radius generally to avoid having markers on the circle
            // edge
            // when area size is 0, we only have one item. we display it in the
            // middle of the area
            float distanceRatio = areaSize == 0.0f ? 0.5f : Math.min(
                    (arItem.getSpreadDistance() -  areaStart) / areaSize, 1f);
            double factor = (scaledRadarRadius * distanceRatio) + myPosRadius;
            double itemAngleRad = Math.toRadians(arItem.getBearing());
            double itemAngle = itemAngleRad - Math.PI / 2;

            // Compute position
            double transX = factor * Math.cos(itemAngle);
            double transY = factor * Math.sin(itemAngle);
            boolean dimmed = arItem.getPanDistance() < dimmingLimit;

            if (arItem.getARObject() != null) {
                final ARRadarDot dotData;
                final int id = (int) arItem.getARObject().getUid();

                if (itemToDot.indexOfKey(id) >= 0) {
                    dotData = itemToDot.get(id);
                    dotData.setTranslateX(transX);
                    dotData.setTranslateY(transY);
                    dotData.setDimmed(dimmed);
                    existingDots.remove(dotData);
                } else {
                    dotData = new ARRadarDot(transX, transY, dimmed);
                    itemToDot.put(id, dotData);
                    radarDots.add(dotData);
                }
            }
        }
        // Clear lost items
        for (ARRadarDot radarDot : existingDots) {
            radarDots.remove(radarDot);
            int index = itemToDot.indexOfValue(radarDot);
            if (index >= 0) {
                itemToDot.removeAt(index);
            }
            radarDot = null;
        }
        existingDots.clear();
    }

    /**
     * Cleanup the radar information
     */
    public void clear() {
        itemToDot.clear();
        radarDots.clear();
    }

}

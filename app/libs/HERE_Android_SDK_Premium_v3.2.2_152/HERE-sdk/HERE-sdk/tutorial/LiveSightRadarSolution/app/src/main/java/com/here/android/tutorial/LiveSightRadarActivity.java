/*
 * Copyright © 2011-2016 HERE Global B.V. and its affiliate(s).
 * All rights reserved.
 * The use of this software is conditional upon having a separate agreement
 * with a HERE company for the use or utilization of this software. In the
 * absence of such agreement, the use of the software is not allowed.
 */

package com.here.android.tutorial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.here.android.mpa.ar.ARController;
import com.here.android.mpa.ar.ARController.Error;
import com.here.android.mpa.ar.ARController.OnCameraEnteredListener;
import com.here.android.mpa.ar.ARController.OnCameraExitedListener;
import com.here.android.mpa.ar.ARIconObject;
import com.here.android.mpa.ar.ARObject;
import com.here.android.mpa.ar.ARRadarProperties;
import com.here.android.mpa.ar.CompositeFragment;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;

import com.here.android.tutorial.R;

public class LiveSightRadarActivity extends Activity {

    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA };

    // Map embedded in the composite fragment
    private Map map;

    // Composite fragment embedded in this activity
    private CompositeFragment compositeFragment;

    // ARController is a facade for controlling LiveSight behavior
    private ARController arController;

    // Sample radar implementation
    private ARRadar m_radar;

    // Buttons which will allow the user to start LiveSight and add objects
    private Button startButton;
    private Button stopButton;
    private Button toggleObjectButton;

    // The image we will display in LiveSight
    private Image image;

    // ARIconObject represents the image model which LiveSight accepts for display
    private boolean objectAdded;

    // Radar size is 1/4 of the minimum between width and height of composite fragment
    private static final double RADAR_RATIO = 4d;

    // Center of map and AR
    private GeoCoordinate center;

    private List<ARObject> arObjects = new ArrayList<ARObject>();

    private Handler m_handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
    }

    private void initialize() {
        setContentView(R.layout.activity_main);

        center = new GeoCoordinate(49.279483, -123.116906, 0.0);

        // Search for the composite fragment to finish setup by calling init().
        compositeFragment = (CompositeFragment) getFragmentManager()
                .findFragmentById(R.id.compositefragment);
        compositeFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    // Retrieve a reference of the map from the composite fragment
                    map = compositeFragment.getMap();
                    // Set the map center coordinate to the Vancouver Downtown region (no animation)
                    map.setCenter(center, Map.Animation.NONE);
                    // Set the map zoom level to the average between min and max (no animation)
                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                    // LiveSight setup should be done after fragment init is complete
                    setupLiveSight();
                } else {
                    System.out.println("ERROR: Cannot initialize Composite Fragment");
                }
            }
        });

        // Hold references to the buttons for future use
        startButton = (Button) findViewById(R.id.startLiveSight);
        stopButton = (Button) findViewById(R.id.stopLiveSight);
        toggleObjectButton = (Button) findViewById(R.id.toggleObject);
    }

    /**
     * Checks the dynamically controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }

    private void setupLiveSight() {
        // ARController should not be used until fragment init has completed
        arController = compositeFragment.getARController();
        // Tells LiveSight to display icons while viewing the map (pitch down)
        arController.setUseDownIconsOnMap(true);
        // Tells LiveSight to use a static mock location instead of the devices GPS fix
        arController.setAlternativeCenter(new GeoCoordinate(49.279483, -123.116906, 0.0));

        // Application will listen for these event to show/hide radar
        arController.addOnCameraEnteredListener(onARStarted);
        arController.addOnCameraExitedListener(onARStopped);

        // Application will process radar updates
        arController.addOnRadarUpdateListener(onRadarUpdate);
    }

    public void startLiveSight(View view) {
        if (arController != null) {
            // Triggers the transition from Map mode to LiveSight mode
            Error error = arController.start();

            if (error == Error.NONE) {
                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Error starting LiveSight: " + error.toString(), Toast.LENGTH_LONG);
            }
        }
    }

    public void stopLiveSight(View view) {
        if (arController != null) {
            // Exits LiveSight mode and returns to Map mode with exit animation
            Error error = arController.stop(true);

            if (error == Error.NONE) {
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Error stopping LiveSight: " + error.toString(), Toast.LENGTH_LONG);
            }
        }
    }

    public void toggleObject(View view) {
        if (arController != null) {
            if (!objectAdded) {

                if (image == null) {
                    image = new Image();
                    try {
                        image.setImageResource(R.drawable.object_icon);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                double longitude = center.getLongitude();
                double latitude = center.getLatitude();
                for (int i = 0; i < 10; ++i) {

                    // Generate coordinate around center
                    double lat = Math.random() * 0.004 + latitude;
                    double lng = Math.random() * 0.004 + longitude;

                    ARIconObject object = new ARIconObject(new GeoCoordinate(lat, lng), (View) null,
                            image);

                    // Adds the icon object to LiveSight to be rendered
                    arController.addARObject(object);
                    arObjects.add(object);
                }
                objectAdded = true;
                toggleObjectButton.setText("Remove Objects");
            } else {
                for (ARObject object : arObjects) {
                    // Removes the icon object from LiveSight, it will no longer
                    // be rendered
                    arController.removeARObject(object);
                }
                arObjects.clear();
                objectAdded = false;
                toggleObjectButton.setText("Add Object");
            }
        }
    }

    @Override
    public void onDestroy() {
        if (arController != null) {
            arController.removeOnRadarUpdateListener(onRadarUpdate);
            arController.removeOnCameraEnteredListener(onARStarted);
            arController.removeOnCameraExitedListener(onARStopped);
        }
        super.onDestroy();
    }

    // Radar logic
    private ARController.OnRadarUpdateListener onRadarUpdate = new ARController.OnRadarUpdateListener() {

        @Override
        public void onRadarUpdate(ARRadarProperties radar) {
            if (m_radar != null && radar != null) {
                m_radar.Update(radar);
            }
        }
    };

    // Start radar when AR is going to camera mode
    private OnCameraEnteredListener onARStarted = new OnCameraEnteredListener() {

        @Override
        public void onCameraEntered() {
            startRadar();
        }
    };

    // Start radar when AR is going to map mode
    private OnCameraExitedListener onARStopped = new OnCameraExitedListener() {

        @Override
        public void onCameraExited() {
            stopRadar();
        }
    };

    private void startRadar() {

        if (arController == null || m_radar != null) {
            return;
        }

        final RelativeLayout layout = (RelativeLayout) LiveSightRadarActivity.this
                .findViewById(R.id.mainlayout);

        if (layout == null) {
            return;
        }

        m_handler.post(new Runnable() {
            public void run() {

                if (m_radar != null) {
                    // Animation conflict - fade out is still penging
                    m_radar.clearAnimation();
                }
                // Create radar
                m_radar = new ARRadar(getApplicationContext(),
                        arController.CameraParams.getHorizontalFov());

                final int width = compositeFragment.getWidth();
                final int height = compositeFragment.getHeight();

                // Calulate radar size
                final int size = (int) (Math.min(width, height) / RADAR_RATIO);

                // Add radar to top right corner of the main layout
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
                params.setMargins(0, 5, 5, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layout.addView(m_radar, params);

                // Animate radar fade in
                Animation animation;
                animation = new AlphaAnimation(0.0f, 1.0f);
                animation.setFillAfter(true);
                animation.setDuration(1000);
                m_radar.startAnimation(animation);
            }
        });
    }

    private void stopRadar() {

        final RelativeLayout layout = (RelativeLayout) LiveSightRadarActivity.this
                .findViewById(R.id.mainlayout);

        if (m_radar == null || layout == null) {
            return;
        }

        m_handler.post(new Runnable() {
            public void run() {

                m_radar.clearAnimation();

                // Animate radar fade out
                Animation animation;
                animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setFillAfter(true);
                animation.setDuration(1000);

                animation.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        if (m_radar == null || layout == null) {
                            return;
                        }
                        layout.removeView(m_radar);
                        m_radar.clear();
                        m_radar = null;
                    }

                    @Override
                    public void onAnimationRepeat(Animation arg0) {
                    }

                    @Override
                    public void onAnimationStart(Animation arg0) {
                    }

                });
                m_radar.startAnimation(animation);
            }
        });
    }
}

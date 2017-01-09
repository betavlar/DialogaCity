/*
 * Copyright © 2011-2016 HERE Global B.V. and its affiliate(s).
 * All rights reserved.
 * The use of this software is conditional upon having a separate agreement
 * with a HERE company for the use or utilization of this software. In the
 * absence of such agreement, the use of the software is not allowed.
 */

package com.here.android.tutorial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.streetlevel.StreetLevel;
import com.here.android.mpa.streetlevel.StreetLevelFragment;
import com.here.android.mpa.streetlevel.StreetLevelModel;
import com.here.android.mpa.streetlevel.StreetLevelIcon;
import com.here.android.mpa.streetlevel.StreetLevelSelectedObject;
import com.here.android.mpa.streetlevel.StreetLevelGesture;
import com.here.android.tutorial.R;

public class BasicSLIActivity extends Activity {

    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    // SLI Model embedded in the SLI fragment
    private StreetLevelModel sliModel = null;

    // SLI fragment embedded in this activity
    private StreetLevelFragment sliFragment = null;

    private Handler h = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
    }

    private void initialize() {
        setContentView(R.layout.activity_main);

        sliFragment = (StreetLevelFragment) getFragmentManager().findFragmentById(R.id.slifragment);
        sliFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(Error error) {
                if (error == Error.NONE) {
                    sliModel = sliFragment.getStreetLevelModel();
                    GeoCoordinate location = new GeoCoordinate(47.623167, -122.335843);
                    StreetLevel bubble = sliModel.getStreetLevel(location, 50);
                    sliModel.moveTo(bubble, true, StreetLevelModel.MOVE_PRESERVE_HEADING,
                            StreetLevelModel.MOVE_PRESERVE_PITCH,
                            StreetLevelModel.MOVE_PRESERVE_ZOOM);
                    sliModel.setNavigationArrowVisible(true);
                    if (sliFragment.getStreetLevelGesture() != null) {
                        sliFragment.getStreetLevelGesture().addOnGestureListener(m_sliListener);
                    }
                } else {
                    System.out.println("ERROR: Cannot initialize StreetLevelFragment");
                }
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private StreetLevelGesture.OnGestureListener m_sliListener = new StreetLevelGesture.OnGestureListener() {

        @Override
        public boolean onObjectsSelected(List<StreetLevelSelectedObject> selectedObjects) {
            return false;
        }

        @Override
        public boolean onCompassSelected() {
            return false;
        }

        @Override
        public boolean onTap(PointF p) {
            GeoCoordinate point = sliModel.pixelToGeo(p);
            if (m_sliIcon == null && point != null) {
                Image img = new Image();
                try {
                    img.setImageResource(R.drawable.test);
                } catch (Exception ex) {

                }
                m_sliIcon = new StreetLevelIcon(point, img);
                m_sliIcon.setPosition(point);
                sliModel.addStreetLevelObject(m_sliIcon);
                m_tapPoint = p;

                h.postDelayed(new Runnable() {
                    public void run() {
                        removeIcon();
                    }
                }, 100);
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(PointF p) {
            return false;
        }

        @Override
        public boolean onPinchZoom(float scaleFactor) {
            return false;
        }

        @Override
        public boolean onRotate(PointF from, PointF to) {
            return false;
        }

    };

    private void removeIcon() {
        sliModel.removeStreetLevelObject(m_sliIcon);
        m_sliIcon = null;
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                List<StreetLevelSelectedObject> objs = sliModel.getSelectedObjects(m_tapPoint);
                for (StreetLevelSelectedObject o : objs) {
                    android.util.Log.d("SLI", o.getObject().toString());
                }
                m_tapPoint = null;
            }
        }, 100);
    }

    private PointF m_tapPoint = null;
    private StreetLevelIcon m_sliIcon = null;
}
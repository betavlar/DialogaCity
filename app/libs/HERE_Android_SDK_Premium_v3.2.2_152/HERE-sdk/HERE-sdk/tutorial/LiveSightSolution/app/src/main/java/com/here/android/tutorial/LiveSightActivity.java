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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.here.android.mpa.ar.ARController;
import com.here.android.mpa.ar.ARController.Error;
import com.here.android.mpa.ar.ARIconObject;
import com.here.android.mpa.ar.CompositeFragment;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.tutorial.R;

public class LiveSightActivity extends Activity {

    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA };

    // map embedded in the composite fragment
    private Map map;

    // composite fragment embedded in this activity
    private CompositeFragment compositeFragment;

    // ARController is a facade for controlling LiveSight behavior
    private ARController arController;

    // buttons which will allow the user to start LiveSight and add objects
    private Button startButton;
    private Button stopButton;
    private Button toggleObjectButton;

    // the image we will display in LiveSight
    private Image image;

    // ARIconObject represents the image model which LiveSight accepts for display
    private ARIconObject arIconObject;
    private boolean objectAdded;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
    }

    private void initialize() {
        setContentView(R.layout.activity_main);

        // Search for the composite fragment to finish setup by calling init().
        compositeFragment = (CompositeFragment) getFragmentManager()
                .findFragmentById(R.id.compositefragment);
        compositeFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the composite fragment
                    map = compositeFragment.getMap();
                    // Set the map center coordinate to the Vancouver Downtown region (no animation)
                    map.setCenter(new GeoCoordinate(49.279483, -123.116906, 0.0),
                            Map.Animation.NONE);
                    // Set the map zoom level to the average between min and max (no animation)
                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                    // LiveSight setup should be done after fragment init is complete
                    setupLiveSight();
                } else {
                    System.out.println("ERROR: Cannot initialize Composite Fragment");
                }
            }
        });

        // hold references to the buttons for future use
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
        // tells LiveSight to display icons while viewing the map (pitch down)
        arController.setUseDownIconsOnMap(true);
        // tells LiveSight to use a static mock location instead of the devices GPS fix
        arController.setAlternativeCenter(new GeoCoordinate(49.279483, -123.116906, 0.0));
    }

    public void startLiveSight(View view) {
        if (arController != null) {
            // triggers the transition from Map mode to LiveSight mode
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
            // exits LiveSight mode and returns to Map mode with exit animation
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
                if (arIconObject == null) {

                    image = new Image();
                    try {
                        image.setImageResource(R.drawable.icon);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // creates a new icon object which uses the same image in up and down views
                    arIconObject = new ARIconObject(new GeoCoordinate(49.276744, -123.112049, 2.0),
                            (View) null, image);
                }

                // adds the icon object to LiveSight to be rendered
                arController.addARObject(arIconObject);
                objectAdded = true;
                toggleObjectButton.setText("Remove Object");
            } else {

                // removes the icon object from LiveSight, it will no longer be rendered
                arController.removeARObject(arIconObject);
                objectAdded = false;
                toggleObjectButton.setText("Add Object");
            }
        }
    }
}

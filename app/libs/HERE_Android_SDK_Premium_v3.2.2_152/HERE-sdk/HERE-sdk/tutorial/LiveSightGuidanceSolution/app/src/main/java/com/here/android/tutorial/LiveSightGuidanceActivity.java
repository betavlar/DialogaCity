/*
 * Copyright © 2011-2016 HERE Global B.V. and its affiliate(s).
 * All rights reserved.
 * The use of this software is conditional upon having a separate agreement
 * with a HERE company for the use or utilization of this software. In the
 * absence of such agreement, the use of the software is not allowed.
 */

package com.here.android.tutorial;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.here.android.mpa.ar.ARBillboardObject;
import com.here.android.mpa.ar.ARController;
import com.here.android.mpa.ar.ARMeshObject;
import com.here.android.mpa.ar.ARBillboardObject.Orientation;
import com.here.android.mpa.ar.ARController.Error;
import com.here.android.mpa.ar.ARController.OnLivesightStatusListener;
import com.here.android.mpa.ar.ARModelObject.ShadingMode;
import com.here.android.mpa.ar.CompositeFragment;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.Vector3f;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.guidance.NavigationManager.MapUpdateMode;
import com.here.android.mpa.guidance.NavigationManager.NavigationMode;
import com.here.android.mpa.guidance.NavigationManager.TrafficAvoidanceMode;
import com.here.android.mpa.mapping.LocalMesh;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.Maneuver;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.RoutingError;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteOptions.TransportMode;
import com.here.android.tutorial.R;

/**
 * LiveSight guidance tutorial demonstrates:
 * - Calculation of a route, showing it on the map
 * - Integration with navigation manager for guidance simulation
 * - Display of 3d guidance objects in LiveSight camera view
 *
 * Guidance 3d objects used:
 * - Way-pointer object that is always pointing to the next maneuver
 * - Billboard object representing the next maneuver itself
 *
 */
public class LiveSightGuidanceActivity extends Activity {
    private static String LOG_TAG = LiveSightGuidanceActivity.class.getSimpleName();

    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA };

    /**
     * Permissions check tracking. This is set to true when permissions have been verified.
     */
    private boolean m_hasPermissions = false;

    /** Initial map zoom level in map mode. **/
    private static final double INITIAL_ZOOM_LEVEL = 14.5;

    /** Guidance simulation speed expressed in meters-per-second. **/
    private static final int SIMULATION_SPEED = 2;

    /** Color of the route on the map. **/
    private static final int ROUTE_COLOR = android.graphics.Color.BLUE;

    /** Id of an icon representing a destination flag image resource. **/
    private static final int DESTINATION_FLAG_ICON_ID = 43;

    /** Billboard width expressed in meters. **/
    private static final float MANEUVER_BILLBOARD_WIDTH = 2.5f;

    /** ALtitude of the maneuver billboard expressed in meters. **/
    private static final float MANEUVER_BILLBOARD_ALTITUDE = 3f;

    /** Minimum-maximum visibility range of the maneuver expressed in meters **/
    private static final PointF MANEUVER_VISIBILITY_RANGE = new PointF(0f, 1000f);

    /**
     * Dynamic scale of the maneuver (minimum-maximum values) for the maneuver visibility range.
     * Full size is 1f.
     **/
    private static final PointF MANEUVER_DISTANCE_SCALE = new PointF(0.5f, 1f);

    // Waypointer positioning
    private static final float WAYPOINTER_Z_PORTRAIT = 3.0f;
    private static final float WAYPOINTER_Z_LANDSCAPE = 3.0f;
    private static final int WAYPOINTER_Y_PIXELS_PORTRAIT = 100;
    private static final int WAYPOINTER_Y_PIXELS_LANDSCAPE = 80;

    // Map embedded in the composite fragment
    private Map m_map;

    // Composite fragment embedded in this activity
    private CompositeFragment m_compositeFragment;

    // ARController is a facade for controlling LiveSight behavior
    private ARController m_arController;

    // Buttons which will allow the user to start LiveSight
    private Button m_startButton;
    private Button m_stopButton;

    // Mocked locations
    private GeoCoordinate m_currentLocation;
    private GeoCoordinate m_destination;

    // Navigation manager used in guidance
    private NavigationManager m_navigationManager;
    private EnumSet<NavigationManager.AudioEvent> m_audioFlags;

    // Routing
    private CoreRouter m_coreRouter;
    private MapRoute m_liveSightMapRoute;
    private RouteOptions m_routeOptions;

    // LiveSight 3d objects
    private ARMeshObject m_arWaypointerObject;
    private ARBillboardObject m_arNextManeuverBillboard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
    }

    private void initialize() {
        setContentView(R.layout.activity_main);

        // Search for the composite fragment to finish setup by calling init()
        m_compositeFragment = (CompositeFragment) getFragmentManager()
                .findFragmentById(R.id.compositefragment);
        m_compositeFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    // Initialize mocked locations
                    m_currentLocation = new GeoCoordinate(52.53098, 13.38479, 0.0);   // Invalidenstrasse
                                                                                      // 117, Berlin
                    m_destination = new GeoCoordinate(52.53017, 13.38807, 0.0);       // Restaurant
                                                                                      // Honigmond,
                                                                                      // Borsigstrasse
                                                                                      // 28, Berlin

                    // Retrieve a reference of the map from the composite fragment
                    m_map = m_compositeFragment.getMap();
                    // Set the map center coordinate to the mocked location (no animation)
                    m_map.setCenter(m_currentLocation, Map.Animation.NONE);
                    // Set the map zoom level to the average between min and max (no animation)
                    m_map.setZoomLevel(INITIAL_ZOOM_LEVEL);

                    // LiveSight setup should be done after fragment init is complete
                    setupLiveSight();

                } else {
                    System.out.println("ERROR: Cannot initialize Composite Fragment");
                }
            }
        });

        // Hold references to the buttons for future use
        m_startButton = (Button) findViewById(R.id.startLiveSight);
        m_stopButton = (Button) findViewById(R.id.stopLiveSight);
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
                m_hasPermissions = true;
                initialize();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!m_hasPermissions) {
            return;
        }

        detachNavigationListeners();

        if (m_navigationManager != null && m_navigationManager
                .getRunningState() == NavigationManager.NavigationState.RUNNING) {
            m_navigationManager.pause();

            hideWaypointerObject();
            hideNextManeuverBillboard();
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!m_hasPermissions) {
            return;
        }

        if (m_navigationManager != null && m_navigationManager
                .getRunningState() == NavigationManager.NavigationState.PAUSED) {
            attachNavigationListeners();

            NavigationManager.Error error = m_navigationManager.resume();
            if (error != NavigationManager.Error.NONE) {
                Toast.makeText(getApplicationContext(),
                        "NavigationManager resume failed: " + error.toString(), Toast.LENGTH_LONG)
                        .show();
                return;
            }

            // Notify navigation listener to update the maneuver billboard and show waypointer
            m_navigationNewInstructionListener.onNewInstructionEvent();
        }
    }

    /**
     * Common LiveSight setup.
     */
    private void setupLiveSight() {
        // ARController should not be used until fragment init has completed
        m_arController = m_compositeFragment.getARController();
        // Tells LiveSight to display icons while viewing the map (pitch down)
        m_arController.setUseDownIconsOnMap(true);
    }

    /**
     * Starts Livesight with route calculation and guidance simulation.
     *
     * @param view
     */
    public void startLiveSight(View view) {
        if (m_arController != null) {
            // Show position indicator on the map
            m_map.getPositionIndicator().setVisible(true);

            // Tells LiveSight to use a static mock location instead of the devices GPS fix
            m_arController.setAlternativeCenter(m_currentLocation);

            // Triggers the transition from Map mode to LiveSight mode
            Error error = m_arController.start();
            if (error == Error.NONE) {
                m_startButton.setVisibility(View.GONE);
                m_stopButton.setVisibility(View.VISIBLE);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Error starting LiveSight: " + error.toString(), Toast.LENGTH_LONG).show();
            }

            // Calculate route and start guidance
            startLiveSightRouting();
        }
    }

    /**
     * Stops LiveSight, guidance simulation, clears the route and returns to normal map mode.
     *
     * @param view
     */
    public void stopLiveSight(View view) {
        // Stop guidance
        stopLiveSightGuidance();

        if (m_arController != null) {
            m_arController.addOnLivesightStatusListener(new OnLivesightStatusListener() {
                @Override
                public void onLivesightStatus(Error error) {
                    m_arController.removeOnLivesightStatusListener(this);

                    // Hide position indicator on the map
                    m_map.getPositionIndicator().setVisible(false);

                    // Restore map center
                    m_map.setCenter(m_currentLocation, Map.Animation.NONE);
                }
            });

            // Exits LiveSight mode and returns to Map mode with exit animation
            Error error = m_arController.stop(true);
            if (error == Error.NONE) {
                m_startButton.setVisibility(View.VISIBLE);
                m_stopButton.setVisibility(View.GONE);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Error stopping LiveSight: " + error.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Starts route calculation and if successful starts guidance simulation afterwards.
     */
    private void startLiveSightRouting() {
        clearLiveSightRoute();

        // Create router
        m_coreRouter = new CoreRouter();

        // Setup route options
        m_routeOptions = new RouteOptions();
        m_routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        m_routeOptions.setTransportMode(TransportMode.PEDESTRIAN);

        // Set route counter to maximum supported routes
        m_routeOptions.setRouteCount(1);

        // Setup route plan
        RoutePlan routePlan = new RoutePlan();
        routePlan.setRouteOptions(m_routeOptions);
        routePlan.addWaypoint(m_currentLocation);
        routePlan.addWaypoint(m_destination);

        // Start route calculation
        m_coreRouter.calculateRoute(routePlan, m_routerListener);
    }

    /**
     * Clears the LiveSight route and removes maneuver billboard.
     */
    private void clearLiveSightRoute() {
        if (m_liveSightMapRoute != null) {
            m_map.removeMapObject(m_liveSightMapRoute);
            m_liveSightMapRoute = null;
        }

        hideNextManeuverBillboard();
    }

    /**
     * Displays calculated route on the map and zooms-in to route's bounding box.
     */
    private void showLiveSightRoute() {
        if (m_liveSightMapRoute == null) {
            Log.w(LOG_TAG, "Failed to show route.");
            return;
        }

        m_liveSightMapRoute.setColor(ROUTE_COLOR);
        m_map.addMapObject(m_liveSightMapRoute);

        // Zoom in
        Route mainRoute = m_liveSightMapRoute.getRoute();
        m_map.zoomTo(mainRoute.getBoundingBox(), Map.Animation.BOW, Map.MOVE_PRESERVE_ORIENTATION);
    }

    /**
     * Starts guidance simulation.
     */
    private void startLiveSightGuidance() {
        if (m_liveSightMapRoute == null) {
            return;
        }

        // Setup navigation manager
        m_navigationManager = NavigationManager.getInstance();
        m_navigationManager.setMap(m_map);
        m_navigationManager.setMapUpdateMode(MapUpdateMode.NONE); // No need to update map as
                                                                  // LiveSight already does so
        attachNavigationListeners();

        stopNavigationManager();

        // Disable navigation sounds
        m_audioFlags = m_navigationManager.getEnabledAudioEvents();
        m_navigationManager
                .setEnabledAudioEvents(EnumSet.noneOf(NavigationManager.AudioEvent.class));

        // Disable traffic avoidance mode as we use pedestrian guidance
        m_navigationManager.setTrafficAvoidanceMode(TrafficAvoidanceMode.DISABLE);

        // Start navigation simulation
        NavigationManager.Error error = m_navigationManager.simulate(m_liveSightMapRoute.getRoute(),
                SIMULATION_SPEED);
        if (error != NavigationManager.Error.NONE) {
            Toast.makeText(getApplicationContext(), "Failed to start navigation. Error: " + error,
                    Toast.LENGTH_LONG).show();
            m_navigationManager.setMap(null);
            return;
        }

        m_navigationManager
                .setNaturalGuidanceMode(EnumSet.of(NavigationManager.NaturalGuidanceMode.JUNCTION));
    }

    /**
     * Stops guidance simulation, removes the route from the map and all the guidance 3d objects.
     */
    private void stopLiveSightGuidance() {
        if (m_coreRouter != null) {
            m_coreRouter.cancel();
        }

        clearLiveSightRoute();

        // Restore navigation sounds
        if (m_navigationManager != null && m_audioFlags != null) {
            m_navigationManager.setEnabledAudioEvents(m_audioFlags);
            m_audioFlags = null;
        }

        detachNavigationListeners();
        stopNavigationManager();

        // Restore LiveSight alternative center
        if (m_arController != null) {
            m_arController.setAlternativeCenter(m_currentLocation);
        }

        hideWaypointerObject();
        hideNextManeuverBillboard();
    }

    /**
     * Stops navigation manager.
     */
    private void stopNavigationManager() {
        if (m_navigationManager == null) {
            return;
        }

        if (m_navigationManager.getRunningState() != NavigationManager.NavigationState.IDLE) {
            m_navigationManager.stop();
        }
    }

    /**
     * Attaches listeners to navigation manager.
     */
    private void attachNavigationListeners() {
        if (m_navigationManager != null) {
            m_navigationManager
                    .addPositionListener(new WeakReference<NavigationManager.PositionListener>(
                            m_navigationPositionListener));
            m_navigationManager.addNewInstructionEventListener(
                    new WeakReference<NavigationManager.NewInstructionEventListener>(
                            m_navigationNewInstructionListener));
            m_navigationManager.addNavigationManagerEventListener(
                    new WeakReference<NavigationManager.NavigationManagerEventListener>(
                            m_navigationListener));
            m_navigationManager
                    .addRerouteListener(new WeakReference<NavigationManager.RerouteListener>(
                            m_navigationRerouteListener));
        }
    }

    /**
     * Detaches listeners from navigation manager.
     */
    private void detachNavigationListeners() {
        if (m_navigationManager != null) {
            m_navigationManager.removeRerouteListener(m_navigationRerouteListener);
            m_navigationManager.removeNavigationManagerEventListener(m_navigationListener);
            m_navigationManager
                    .removeNewInstructionEventListener(m_navigationNewInstructionListener);
            m_navigationManager.removePositionListener(m_navigationPositionListener);
        }
    }

    /**
     * Shows a waypointer object. Creates it if not already created and updates its waypoint.
     */
    private void showWaypointerObject() {
        if (m_destination == null) {
            return;
        }

        // Get waypoint
        GeoCoordinate waypoint = m_destination;
        if (m_navigationManager != null) {
            final Maneuver maneuver = m_navigationManager.getNextManeuver();
            if (maneuver != null) {
                waypoint = maneuver.getCoordinate();
            }
        }

        // Add waypointer to LiveSight or update the waypoint of existing one
        if (m_arWaypointerObject == null) {
            createARWaypointerObject(waypoint);
            m_arController.addARObject(m_arWaypointerObject);

        } else {
            m_arWaypointerObject.setGeoDirection(waypoint);
        }
    }

    /**
     * Removes waypointer object from LiveSight.
     */
    private void hideWaypointerObject() {
        if (m_arController == null) {
            return;
        }

        if (m_arWaypointerObject != null) {
            m_arController.removeARObject(m_arWaypointerObject);
        }

        m_arWaypointerObject = null;
    }

    /**
     * Creates Livesight waypointer object.
     *
     * @param waypoint
     *            geo-cooridnate waypointer object is pointing to.
     */
    private void createARWaypointerObject(GeoCoordinate waypoint) {
        // Triangle vertex coordinates
        float[] vertices = { 0.0f, 0.0f, 1.0f, 0.66f, 0.0f, 0.33f, 0.33f, 0.0f, 0.33f, 0.33f, 0.0f,
                -1.0f, -0.33f, 0.0f, -1.0f, -0.33f, 0.0f, 0.33f, -0.66f, 0.0f, 0.33f, 0.0f, 0.3f,
                1.0f, 0.66f, 0.3f, 0.33f, 0.33f, 0.3f, 0.33f, 0.33f, 0.3f, -1.0f, -0.33f, 0.3f,
                -1.0f, -0.33f, 0.3f, 0.33f, -0.66f, 0.3f, 0.33f };

        // Triangle texture coordinates
        float[] uvCoordinates = { 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f };

        // Triangle indices
        int[] vertexIndices = { 0, 1, 6, 2, 3, 5, 3, 4, 5, 7, 8, 13, 9, 10, 12, 10, 11, 12, 0, 1, 7,
                1, 8, 7, 1, 2, 8, 2, 9, 8, 2, 3, 9, 3, 10, 9, 3, 4, 10, 4, 11, 10, 4, 5, 11, 5, 12,
                11, 5, 6, 12, 6, 13, 12, 13, 6, 0, 13, 0, 7 };

        // Create the mesh
        LocalMesh mesh = new LocalMesh();
        mesh.setTextureCoordinates(FloatBuffer.wrap(uvCoordinates));
        mesh.setVertices(FloatBuffer.wrap(vertices));
        mesh.setVertexIndices(IntBuffer.wrap(vertexIndices));

        // Load image from the drawable resource
        Image texture = new Image();
        try {
            texture.setImageResource(R.drawable.solid_blue);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Create the object
        Vector3f position = getARWaypointerPosition();
        m_arWaypointerObject = new ARMeshObject(position, waypoint, mesh, texture);
        m_arWaypointerObject.setShadingMode(ShadingMode.FLAT_TEXTURED);
        m_arWaypointerObject.scale(0.4f, 0.4f, 0.4f);
    }

    /**
     * Calculates position of the wayponter object.
     *
     * @return Position of waypointer.
     */
    private Vector3f getARWaypointerPosition() {
        if (m_arController == null) {
            return null;
        }

        float z = WAYPOINTER_Z_PORTRAIT;
        int yOffset = WAYPOINTER_Y_PIXELS_PORTRAIT;
        if (!isPortrait()) {
            z = WAYPOINTER_Z_LANDSCAPE;
            yOffset = WAYPOINTER_Y_PIXELS_LANDSCAPE;
        }

        Vector3f localPosition = new Vector3f(0, -1, z);
        m_arController.pixelTo3dPosition(z,
                new PointF(m_map.getWidth() * 0.5f, m_map.getHeight() - dpToPx(yOffset)),
                localPosition);

        return localPosition;
    }

    /**
     * Transforms device density independent pixels to pixels.
     *
     * @param dp
     *            Density independent pixels
     * @return Corresponding pixels
     */
    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    /**
     * Determines if current device orientation is portrait.
     *
     * @return true if device is in portrait mode.
     */
    private boolean isPortrait() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
                return false;
            }
            return true;
        }

        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            if (displayMetrics.widthPixels < displayMetrics.heightPixels) {
                return true;
            }
            return false;
        }

        return true;
    }

    /**
     * Creates the billboard object for the specified maneuver and shows it by adding it to
     * LiveSight. Hides the previous billboard object if present.
     *
     * @param maneuver
     */
    private void updateNextManeuverBillboard(final Maneuver maneuver) {
        final int resId = getManeuverIconResourceId(getApplicationContext(), maneuver);
        if (resId == 0) {
            Log.e(LOG_TAG, "Resource not found for maneuver: " + maneuver.toString());
            return;
        }

        // Hide the previous maneuver
        hideNextManeuverBillboard();

        // Set the billboard altitude
        final GeoCoordinate maneuverCoordinate = maneuver.getCoordinate();
        maneuverCoordinate.setAltitude(MANEUVER_BILLBOARD_ALTITUDE);

        addARBillboardFromResource(maneuverCoordinate, resId, MANEUVER_BILLBOARD_WIDTH);
    }

    /**
     * Creates and adds a geo-billboard object to LiveSight with a texture loaded from a specified
     * resource.
     *
     * @param location
     *            Geo-coordinate of a billboard.
     * @param iconResourceId
     *            Resource id of the texture for the billboard.
     * @param width
     *            Width of a billboard expressed in meters.
     */
    private void addARBillboardFromResource(GeoCoordinate location, int iconResourceId,
            float width) {
        final Image img = new Image();
        try {
            img.setImageResource(iconResourceId);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        addARBillboard(location, img, width);
    }

    /**
     * Creates and adds a geo-billboard object to LiveSight with a texture loaded from a specified
     * image.
     *
     * @param coordinate
     *            Geo-coordinate of a billboard.
     * @param img
     *            Image of the texture for the billboard.
     * @param width
     *            Width of a billboard expressed in meters.
     */
    private void addARBillboard(GeoCoordinate coordinate, Image img, float width) {
        // Calculate height of the billboard based on image width/height ratio
        float billboardHeight = width * img.getHeight() / img.getWidth();

        // Create the object
        m_arNextManeuverBillboard = new ARBillboardObject(coordinate, img);
        // Billboard always oriented towards the user/camera
        m_arNextManeuverBillboard.setOrientation(Orientation.BILLBOARD);
        m_arNextManeuverBillboard.setShadingMode(ShadingMode.FLAT_TEXTURED);
        m_arNextManeuverBillboard.setSize(new PointF(width, billboardHeight));
        m_arNextManeuverBillboard.setUpDirection(new Vector3f(0f, 1f, 0f));

        // Set visibility range and min-max sizes
        m_arNextManeuverBillboard.setVisibilityRange(MANEUVER_VISIBILITY_RANGE);
        m_arNextManeuverBillboard.setDynamicScale(MANEUVER_VISIBILITY_RANGE,
                MANEUVER_DISTANCE_SCALE);

        // Add to LiveSight
        m_arController.addARObject(m_arNextManeuverBillboard);
    }

    /**
     * Permanently removes guidance maneuver object from LiveSight.
     */
    private void hideNextManeuverBillboard() {
        if (m_arController != null && m_arNextManeuverBillboard != null) {
            m_arController.removeARObject(m_arNextManeuverBillboard);
        }

        m_arNextManeuverBillboard = null;
    }

    /**
     * Returns icon resource id for the specified maneuver.
     *
     * @param context
     * @param maneuver
     * @return returns 0 if no resource is found.
     */
    public static int getManeuverIconResourceId(Context context, Maneuver maneuver) {
        final Maneuver.Icon icon = maneuver.getIcon();
        final int iconId = maneuver.getAction() == Maneuver.Action.END ? DESTINATION_FLAG_ICON_ID
                : icon.ordinal();

        final String packageName = context.getPackageName();
        return context.getResources().getIdentifier("maneuver_icon_" + iconId, "drawable",
                packageName);
    }

    // Called on UI thread
    private final CoreRouter.Listener m_routerListener = new CoreRouter.Listener() {
        @Override
        public void onProgress(int percentage) {
        }

        @Override
        public void onCalculateRouteFinished(List<RouteResult> routeResults,
                RoutingError errorCode) {
            // NOTE: CoreRouter can send Error.NONE even when cancelled
            if (errorCode == RoutingError.ROUTING_CANCELLED) {
                return;
            }

            clearLiveSightRoute();

            // Error occurred
            if (errorCode != RoutingError.NONE || routeResults.size() == 0) {
                Log.i(LOG_TAG, "onCalculateRouteFinished with error");

                if (routeResults.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Route not found.", Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Failed to calculate route. Error: " + errorCode, Toast.LENGTH_LONG)
                            .show();
                }

                return;
            }

            // Get the first route returned
            m_liveSightMapRoute = new MapRoute(routeResults.get(0).getRoute());

            // Show route on the map
            showLiveSightRoute();

            hideNextManeuverBillboard();

            // Start guidance
            startLiveSightGuidance();
        }
    };

    // Called on UI thread
    private final NavigationManager.NavigationManagerEventListener m_navigationListener = new NavigationManager.NavigationManagerEventListener() {
        @Override
        public void onEnded(final NavigationMode mode) {
            // NOTE: this method is called in both cases when destination is reached and when
            // NavigationManager is stopped.
            Toast.makeText(getApplicationContext(), "Destination reached!", Toast.LENGTH_LONG)
                    .show();

            hideWaypointerObject();

            // Revert to default behavior
            m_navigationManager.setMapUpdateMode(MapUpdateMode.NONE);
            m_navigationManager.setTrafficAvoidanceMode(TrafficAvoidanceMode.DISABLE);
            m_navigationManager.setMap(null);
        }

        @Override
        public void onRouteUpdated(final Route updatedRoute) {
            // This does not happen on re-route
            Toast.makeText(getApplicationContext(), "Your route was udated!", Toast.LENGTH_LONG)
                    .show();

            m_map.removeMapObject(m_liveSightMapRoute);
            m_liveSightMapRoute = new MapRoute(updatedRoute);
            showLiveSightRoute();
        }
    };

    // Called on UI thread
    private final NavigationManager.RerouteListener m_navigationRerouteListener = new NavigationManager.RerouteListener() {
        @Override
        public void onRerouteEnd(Route route) {
            Toast.makeText(getApplicationContext(), "Your route was udated!", Toast.LENGTH_LONG)
                    .show();

            m_map.removeMapObject(m_liveSightMapRoute);
            m_liveSightMapRoute = new MapRoute(route);
            showLiveSightRoute();
        };
    };

    // Called on UI thread
    private final NavigationManager.PositionListener m_navigationPositionListener = new NavigationManager.PositionListener() {
        @Override
        public void onPositionUpdated(final GeoPosition loc) {
            if (m_navigationManager == null || m_arController == null) {
                return;
            }

            GeoCoordinate coord = loc.getCoordinate();
            if (coord == null || !coord.isValid()) {
                return;
            }

            // Due to false Altitude from map data
            coord.setAltitude(0);

            // Set LiveSight alternative center
            m_arController.setAlternativeCenter(coord);
        }
    };

    // Called on UI thread
    private final NavigationManager.NewInstructionEventListener m_navigationNewInstructionListener = new NavigationManager.NewInstructionEventListener() {
        @Override
        public void onNewInstructionEvent() {
            Log.d(LOG_TAG, "onNewInstructionEvent");

            final Maneuver maneuver = m_navigationManager.getNextManeuver();
            if (maneuver == null) {
                Log.e(LOG_TAG, "onNewInstructionEvent - invalid maneuver");
                return;
            }

            showWaypointerObject();

            // Show next maneuver
            updateNextManeuverBillboard(maneuver);
        }
    };
}

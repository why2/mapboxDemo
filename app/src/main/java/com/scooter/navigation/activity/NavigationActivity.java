package com.scooter.navigation.activity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.BannerInstructions;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.BannerInstructionsListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener;
import com.mapbox.services.android.navigation.ui.v5.voice.SpeechAnnouncement;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.scooter.navigation.R;
import com.scooter.navigation.base.BaseActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 导航
 */
public class NavigationActivity extends BaseActivity implements OnNavigationReadyCallback,
        NavigationListener, ProgressChangeListener, SpeechAnnouncementListener,
        BannerInstructionsListener, LocationEngineCallback<LocationEngineResult> {
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private static final int INITIAL_ZOOM = 16;
    private static Point ORIGIN = Point.fromLngLat(121.3311219571, 31.1517889257);
    private static Point DESTINATION = Point.fromLngLat(121.3149287837, 31.1393712551);
    private NavigationView navigationView;
    private LocationEngine locationEngine;
    private boolean navigating;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNightMode();
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_navigation);
        navigationView = findViewById(R.id.navigationView);
        navigationView.onCreate(savedInstanceState);
        alternateElementStyle();
        Bundle bundle = getIntent().getBundleExtra("data");
        double lat = bundle.getDouble("lat");
        double lng = bundle.getDouble("lng");
        DESTINATION = Point.fromLngLat(lng, lat);
        navigationView.showRouteInfo(false);
        initLocationEngine();
    }


    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);
        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();
        locationEngine.requestLocationUpdates(request, this, getMainLooper());
        locationEngine.getLastLocation(this);
    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        if (!isRunning) {
//            fetchRoute();
        }
        alternateElementStyle();
    }

    @Override
    public void onStart() {
        super.onStart();
        navigationView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        if (!navigationView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        navigationView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        navigationView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(this);
        }
        navigationView.onDestroy();
        if (isFinishing()) {
            saveNightModeToPreferences(AppCompatDelegate.MODE_NIGHT_AUTO);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        }
    }

    @Override
    public void onCancelNavigation() {
        finish();
    }

    @Override
    public void onNavigationFinished() {
        // TODO: 2019/9/4  
        finish();
    }

    @Override
    public void onNavigationRunning() {
        navigating = true;
    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
//        setSpeed(location);
    }


    @Override
    public SpeechAnnouncement willVoice(SpeechAnnouncement announcement) {
        return SpeechAnnouncement.builder().announcement("All announcements will be the same.").build();
    }

    @Override
    public BannerInstructions willDisplay(BannerInstructions instructions) {
        return instructions;
    }

    private void startNavigation(DirectionsRoute directionsRoute) {
        NavigationViewOptions.Builder options =
                NavigationViewOptions.builder()
                        .navigationListener(this)
                        .directionsRoute(directionsRoute)
                        .progressChangeListener(this)
                        .speechAnnouncementListener(this)
                        .bannerInstructionsListener(this);
        navigationView.startNavigation(options.build());
        navigationView.showRouteInfo(true);
    }

    private void fetchRoute() {
        NavigationRoute.builder(this)
                .accessToken(getString(R.string.access_token))
                /**
                 * 配置路线规划类型
                 */
                .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
                /**
                 * 排查高速等
                 * mapbox/driving	One of toll, motorway, or ferry
                 * mapbox/driving-traffic	One of toll, motorway, or ferry
                 */
                .exclude(DirectionsCriteria.EXCLUDE_MOTORWAY)
                .origin(ORIGIN)
                .destination(DESTINATION)
                .alternatives(false)
                .enableRefresh(false)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        try {
                            DirectionsRoute directionsRoute = response.body().routes().get(0);
                            startNavigation(directionsRoute);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                });
    }

    private void setSpeed(Location location) {
        String string = String.format("%d\nMPH", (int) (location.getSpeed() * 2.2369));
        int mphTextSize = getResources().getDimensionPixelSize(R.dimen.mph_text_size);
        int speedTextSize = getResources().getDimensionPixelSize(R.dimen.speed_text_size);
        SpannableString spannableString = new SpannableString(string);
        spannableString.setSpan(new AbsoluteSizeSpan(mphTextSize),
                string.length() - 4, string.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(speedTextSize),
                0, string.length() - 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }

    @Override
    public void onSuccess(LocationEngineResult result) {
        Location lastLocation = result.getLastLocation();
        if (lastLocation != null && !navigating) {
            ORIGIN = Point.fromLngLat(lastLocation.getLongitude(), lastLocation.getLatitude());
            CameraPosition initialPosition = new CameraPosition.Builder()
                    .target(new LatLng(ORIGIN.latitude(), ORIGIN.longitude()))
                    .zoom(INITIAL_ZOOM)
                    .build();
            navigationView.initialize(this, initialPosition);
            fetchRoute();
        }
    }

    @Override
    public void onFailure(@NonNull Exception exception) {
    }
}

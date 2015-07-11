package test.maps.app;

import android.content.IntentSender;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.android.airmapview.AirMapInterface;
import com.airbnb.android.airmapview.AirMapMarker;
import com.airbnb.android.airmapview.AirMapView;
import com.airbnb.android.airmapview.AirMapViewTypes;
import com.airbnb.android.airmapview.DefaultAirMapViewBuilder;
import com.airbnb.android.airmapview.listeners.OnCameraChangeListener;
import com.airbnb.android.airmapview.listeners.OnCameraMoveListener;
import com.airbnb.android.airmapview.listeners.OnLatLngScreenLocationCallback;
import com.airbnb.android.airmapview.listeners.OnInfoWindowClickListener;
import com.airbnb.android.airmapview.listeners.OnMapClickListener;
import com.airbnb.android.airmapview.listeners.OnMapInitializedListener;
import com.airbnb.android.airmapview.listeners.OnMapMarkerClickListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Random;


public class MapsActivity extends ActionBarActivity
        implements OnCameraChangeListener, OnMapInitializedListener,
        OnMapClickListener, OnCameraMoveListener, OnMapMarkerClickListener,
        OnInfoWindowClickListener, OnLatLngScreenLocationCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private AirMapView map;
    private DefaultAirMapViewBuilder mapViewBuilder;
    private TextView textLogs;
    private ScrollView logsScrollView;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapViewBuilder = new DefaultAirMapViewBuilder(this);
        map = (AirMapView) findViewById(R.id.map);
        textLogs = (TextView) findViewById(R.id.textLogs);
        logsScrollView = (ScrollView) findViewById(R.id.logsScrollView);

        map.setOnMapClickListener(this);
        map.setOnCameraChangeListener(this);
        map.setOnCameraMoveListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnMapInitializedListener(this);
        map.setOnInfoWindowClickListener(this);
        map.initialize(getSupportFragmentManager());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds



    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        AirMapInterface airMapInterface = null;

        if (id == R.id.action_native_map) {
            try {
                airMapInterface = mapViewBuilder.builder(AirMapViewTypes.NATIVE).build();
            } catch (UnsupportedOperationException e) {
                Toast.makeText(this, "Sorry, native Google Maps are not supported by this device. " +
                                "Please make sure you have Google Play Services installed.",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_web_map) {
            airMapInterface = mapViewBuilder.builder(AirMapViewTypes.WEB).build();
        } else if (id == R.id.action_clear_logs) {
            textLogs.setText("");
        }

        if (airMapInterface != null) {
            map.initialize(getSupportFragmentManager(), airMapInterface);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override public void onCameraChanged(LatLng latLng, int zoom) {
        appendLog("Map onCameraChanged triggered with lat: " + latLng.latitude + ", lng: "
                + latLng.longitude);
    }

    @Override public void onMapInitialized() {
        appendLog("Map onMapInitialized triggered");

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            // Blank for a moment...
        }
        else {
            handleNewLocation(location);
        };
    }

    private void addMarker(String title, LatLng latLng, int id) {
        map.addMarker(new AirMapMarker(latLng, id)
                .setTitle(title)
                .setIconId(R.mipmap.icon_location_pin));
    }

    private void handleNewLocation(Location location) {
        map.clearMarkers();
        Random t = new Random();
        final LatLng myLatLng = new LatLng(location.getLatitude(),location.getLongitude());
        addMarker("You are here", myLatLng, t.nextInt(100));
        map.animateCenterZoom(myLatLng, 30);
    }

    @Override public void onMapClick(LatLng latLng) {
        if (latLng != null) {
            appendLog(
                    "Map onMapClick triggered with lat: " + latLng.latitude + ", lng: "
                            + latLng.longitude);

            map.getMapInterface().getScreenLocation(latLng, this);
        } else {
            appendLog("Map onMapClick triggered with null latLng");
        }
    }

    @Override public void onCameraMove() {
        appendLog("Map onCameraMove triggered");
    }

    private void appendLog(String msg) {
        textLogs.setText(textLogs.getText() + "\n" + msg);
        logsScrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void onMapMarkerClick(long id) {
        appendLog("Map onMapMarkerClick triggered with id " + id);
    }

    @Override public void onMapMarkerClick(Marker marker) {
        appendLog("Map onMapMarkerClick triggered with marker " + marker.getId());
    }

    @Override public void onInfoWindowClick(long id) {
        appendLog("Map onInfoWindowClick triggered with id " + id);
    }

    @Override public void onInfoWindowClick(Marker marker) {
        appendLog("Map onInfoWindowClick triggered with marker " + marker.getId());
    }

    @Override public void onLatLngScreenLocationReady(Point point) {
        appendLog("LatLng location on screen (x,y): (" + point.x + "," + point.y + ")");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

//    public void animateMarker(final Marker marker, final LatLng toPosition,
//                              final boolean hideMarker) {
//        final Handler handler = new Handler();
//        final long start = SystemClock.uptimeMillis();
//        Projection proj =
//        Point startPoint = proj.toScreenLocation(marker.getPosition());
//        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
//        final long duration = 500;
//
//        final Interpolator interpolator = new LinearInterpolator();
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                long elapsed = SystemClock.uptimeMillis() - start;
//                float t = interpolator.getInterpolation((float) elapsed
//                        / duration);
//                double lng = t * toPosition.longitude + (1 - t)
//                        * startLatLng.longitude;
//                double lat = t * toPosition.latitude + (1 - t)
//                        * startLatLng.latitude;
//                marker.setPosition(new LatLng(lat, lng));
//
//                if (t < 1.0) {
//                    // Post again 16ms later.
//                    handler.postDelayed(this, 16);
//                } else {
//                    if (hideMarker) {
//                        marker.setVisible(false);
//                    } else {
//                        marker.setVisible(true);
//                    }
//                }
//            }
//        });
//    }
}

package test.maps.app.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import test.maps.app.MyActivity;
import test.maps.app.R;
import test.maps.app.receiver.MyResultReceiver;
import test.maps.app.receiver.MyResultReceiver.Receiver;
import test.maps.app.service.RidelyLocationService;

public class MapsFragment extends Fragment implements Receiver {

    // LogCat tag
    private static final String TAG = MyActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;


    private GoogleMap googleMap;

    public MyResultReceiver resultReceiver;

    Intent intent;

    public MapsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        resultReceiver = new MyResultReceiver(new Handler());
        resultReceiver.setReceiver(this);
        try {
            // Loading map
            initilizeMapAndStartService();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMapAndStartService() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // First we need to check availability of play services
            if (checkPlayServices()) {

                // Launch Service only if device is compatible
                intent = new Intent(getActivity().getApplicationContext(), RidelyLocationService.class);
                intent.putExtra("receiver", resultReceiver);
                getActivity().startService(intent);

            }

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        resultReceiver.setReceiver(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        resultReceiver.setReceiver(this);
    }
    /**
     * Method to display the location on UI
     * */
    private void plotOnMap(Double latitude, Double longitude) {
        googleMap.clear();
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Hello!");
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        googleMap.addMarker(marker);
        Log.v(TAG, latitude + ", " + longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(latitude, longitude)).zoom(16).build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    class UpdateUI implements Runnable
    {
        Double latitude;
        Double longitude;

        public UpdateUI(Double latitude, Double longitude)
        {
            this.latitude = latitude;
            this.longitude = longitude;
        }
        public void run() {
            //plot point map
            plotOnMap(latitude, longitude);
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if(resultCode == 100){
            getActivity().runOnUiThread(new UpdateUI(resultData.getDouble("latitude"), resultData.getDouble("longitude")));
        }
    }
}
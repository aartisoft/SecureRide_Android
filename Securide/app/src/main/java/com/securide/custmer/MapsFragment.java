package com.securide.custmer;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.securide.custmer.Util.Constants;
import com.securide.custmer.controllers.AddressController;
import com.securide.custmer.listeners.ILocationListener;

import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment implements LocationListener {
    private static final String TAG = "MapsFragment : ";
    private Context mContext = null;
    private Activity mActivity = null;
    // Google Map
    private GoogleMap mGoogleMap = null;
    private String mPickupAddress = null;
    private LocationManager mLocationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    int mHasAccessCoarseLocation = -1;
    int mHasFineLocation = -1;

    Marker pickupMarker, dropMarker;
    private LatLngBounds.Builder bld;
    Marker cabMarker;
    public MapsFragment() {
        // Required empty public constructor
    }


    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_maps, container, false);
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, mActivity, requestCode);
            dialog.show();
        } else {
            mGoogleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
//            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {

                }
            });
            //============================================Dynamic permission for locations=======================================================

//            mHasAccessCoarseLocation = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
//            mHasFineLocation = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
//            if (mHasAccessCoarseLocation == PackageManager.PERMISSION_GRANTED) {
//                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
//            } else {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//            }
//
//            if (mHasFineLocation == PackageManager.PERMISSION_GRANTED) {
//                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
//            } else {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            }
            //==============================================================================================================
        }
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = (Activity) context;
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mGoogleMap.animateCamera(cameraUpdate);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
//        pickupMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
        updatePickUpLocation(latLng);

//        mHasAccessCoarseLocation = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
//        mHasFineLocation = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
//        if (mHasAccessCoarseLocation == PackageManager.PERMISSION_GRANTED && mHasFineLocation == PackageManager.PERMISSION_GRANTED) {
//            mLocationManager.removeUpdates(this);
//        } else {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }
//        Constants.lat = location.getLatitude();
//        Constants.lng = location.getLongitude();
//        getAddress(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // call back indicating about GPS statuses.
        // ex : GPS_EVENT_STARTED, GPS_EVENT_STOPPED, GPS_EVENT_FIRST_FIX, GPS_EVENT_SATELLITE_STATUS
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


/*    private void getAddress(final Location location) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder;
                List<Address> address;
                geocoder = new Geocoder(mActivity, Locale.getDefault());
                try {
                    address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (address.size() > 0) {
                        String addr = address.get(0).getAddressLine(0);
                        String city = address.get(0).getAddressLine(1);
                        String country = address.get(0).getAddressLine(2);
                        mPickupAddress = addr.concat(city).concat(country);
                    }
                } catch (Exception e) {

                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AddressController.getInstance().setSelectedSourceAddress(mPickupAddress);
                        mActivity.onCurrentAddress(mPickupAddress);
                    }
                });
            }
        });
        thread.start();
    }*/

    void updatePickUpLocation(LatLng location) {
        if (pickupMarker == null) {
            pickupMarker = mGoogleMap.addMarker(new MarkerOptions().position(location));
        } else {
            pickupMarker.setPosition(location);
        }
        updateZoomLevel(location);
    }

    void updateDropLocation(LatLng location) {
        if (dropMarker == null) {
            dropMarker = mGoogleMap.addMarker(new MarkerOptions().position(location)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        } else {
            dropMarker.setPosition(location);
        }
        updateZoomLevel(location);
    }

    void updateZoomLevel(final LatLng location) {
        if (AddressController.getInstance().getDestinationLocaton() != null && AddressController.getInstance().getSourceLocaton() != null) {
            bld = null;
            bld = new LatLngBounds.Builder();

            if (pickupMarker != null) {
                bld.include(pickupMarker.getPosition());
            }
            if (dropMarker != null) {
                bld.include(dropMarker.getPosition());
            }
            try {
                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bld.build(),
                                200));
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{

            try {
                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 15);
                        mGoogleMap.animateCamera(cameraUpdate);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    void updateCabLocation(final LatLng location) {

        if(cabMarker == null) {
            cabMarker = mGoogleMap.addMarker(new MarkerOptions().position(location).
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_marker)));
        }else {
            cabMarker.setPosition(location);
        }
        try {
            mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 15);
                    mGoogleMap.animateCamera(cameraUpdate);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.securide.custmer.Util;

import java.util.Timer;
import java.util.TimerTask;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;

//From http://stackoverflow.com/questions/3145089/what-is-the-simplest-and-most-robust-way-to-get-the-users-current-location-in-a/

public class MyLocation {

    String TAG = "MyLocation";
    Timer timer1;
    LocationManager lm;
    LocationResult locationResult;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    int mHasAccessCoarseLocation = -1;
    int mHasFineLocation = -1;
    private static final long MIN_TIME = 10;
    private static final float MIN_DISTANCE = 10;

    Context context;

    public boolean getLocation(Context context, LocationResult result) {
        // I use LocationResult callback class to pass location value from
        // MyLocation to user code.
        this.context = context;
        locationResult = result;
        if (lm == null)
            lm = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);

        // exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        // don't start listeners if no provider is enabled
        if (!gps_enabled && !network_enabled)
            return false;

        if (gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10,
                    locationListenerGps);
        if (network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    locationListenerNetwork);

        timer1 = new Timer();
//		timer1.schedule(new GetLastLocation(), 5000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                locationResult.gotLocation(null);
            }
        }, 5000);
        return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Log.d(TAG, "Retrieved location : GPS Provider");
            timer1.cancel();
            locationResult.gotLocation(location);
//			lm.removeUpdates(this);
//			lm.removeUpdates(locationListenerNetwork);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Log.d(TAG, "Retrieved location : Network Provider");
            timer1.cancel();
            locationResult.gotLocation(location);
//			lm.removeUpdates(this);
//			lm.removeUpdates(locationListenerGps);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
//			lm.removeUpdates(locationListenerGps);
//			lm.removeUpdates(locationListenerNetwork);
            if (timer1 != null)
                timer1.cancel();

            Location net_loc = null, gps_loc = null;
            if (gps_enabled)
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (network_enabled)
                net_loc = lm
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            // if there are both values use the latest one
            if (gps_loc != null && net_loc != null) {
                if (gps_loc.getTime() > net_loc.getTime()) {
                    locationResult.gotLocation(gps_loc);
                    return;
                } else {
                    locationResult.gotLocation(net_loc);
                    return;
                }
            } else if (gps_loc != null) {
                locationResult.gotLocation(gps_loc);
                return;
            } else if (net_loc != null) {
                locationResult.gotLocation(net_loc);
                return;
            }
            locationResult.gotLocation(null);
        }
    }

    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }

    public void cancelTimer() {
        if (timer1 != null)
            timer1.cancel();
        if (lm != null) {
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);
        }
    }

    void checkForPermission() {
//        mHasAccessCoarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
//        mHasFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
//        if (mHasAccessCoarseLocation == PackageManager.PERMISSION_GRANTED) {
//            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
//        } else {
//            lm(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//        }
//
//        if (mHasFineLocation == PackageManager.PERMISSION_GRANTED) {
//            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
//        } else {
//            lm(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }
    }
}
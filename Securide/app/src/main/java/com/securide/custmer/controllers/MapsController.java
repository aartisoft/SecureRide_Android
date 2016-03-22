package com.securide.custmer.controllers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.util.List;
import java.util.Locale;

/**
 * Created by pradeep.kumar on 3/16/16.
 */
public class MapsController {
    public static final String TAG = "MapsController : ";
    private static MapsController mMapsController = new MapsController();
    private String currentAddress = "";


    public static MapsController getInstance(){
        return mMapsController;
    }

}

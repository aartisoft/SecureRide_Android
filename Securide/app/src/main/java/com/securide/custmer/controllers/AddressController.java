package com.securide.custmer.controllers;

import android.location.Address;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by pradeep.kumar on 3/14/16.
 */
public class AddressController {
    private static AddressController mInstance = new AddressController();
    private String mDestinationAddress = null;
    private String mSourceAddress = null;

    private LatLng SourceLocaton = null;
    private LatLng DestinationLocaton = null;

    private AddressController(){

    }

    public static AddressController getInstance(){
        return mInstance;
    }

    public void setSelectedDestinationAddress(String address){
        mDestinationAddress = address;
    }

    public String getSelectedDestinationAddress(){
        return mDestinationAddress;
    }

    public void setSelectedSourceAddress(String address){
        mSourceAddress = address;
    }

    public String getSelectedSourceAddress(){
        return mSourceAddress;
    }

    public LatLng getSourceLocaton() {
        return SourceLocaton;
    }

    public void setSourceLocaton(LatLng sourceLocaton) {
        SourceLocaton = sourceLocaton;
    }

    public LatLng getDestinationLocaton() {
        return DestinationLocaton;
    }

    public void setDestinationLocaton(LatLng destinationLocaton) {
        DestinationLocaton = destinationLocaton;
    }
}

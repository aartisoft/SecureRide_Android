package com.securide.custmer.controllers;

import android.location.Address;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.securide.custmer.model.AddressObject;

/**
 * Created by pradeep.kumar on 3/14/16.
 */
public class AddressController {
    private static AddressController mInstance = new AddressController();
    private AddressObject mDestinationAddress = null;
    private AddressObject mSourceAddress = null;

    private LatLng SourceLocaton = null;
    private LatLng DestinationLocaton = null;

    private AddressController(){

    }

    public static AddressController getInstance(){
        return mInstance;
    }

    public void setSelectedDestinationAddress(AddressObject address){
        mDestinationAddress = address;
    }

    public AddressObject getSelectedDestinationAddress(){
        return mDestinationAddress;
    }

    public void setSelectedSourceAddress(AddressObject address){
        mSourceAddress = address;
    }

    public AddressObject getSelectedSourceAddress(){
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

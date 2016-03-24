package com.securide.custmer.controllers;

import android.location.Address;

/**
 * Created by pradeep.kumar on 3/14/16.
 */
public class AddressController {
    private static AddressController mInstance = new AddressController();
    private String mAddress = null;

    private AddressController(){

    }

    public static AddressController getInstance(){
        return mInstance;
    }

    public void setSelectedDestinationAddress(String address){
        mAddress = address;
    }

    public String getSelectedDestinationAddress(){
        return mAddress;
    }
}

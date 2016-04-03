package com.securide.custmer.connection.core;

import android.util.Log;

import com.securide.custmer.model.AddressObject;
import com.securide.custmer.model.TaxiTrip;

/**
 * Created by android_studio on 3/29/16.
 */
public class JNIConnectionManager {

    static {
        try {
            System.loadLibrary("test");
        } catch (UnsatisfiedLinkError ule) {
            Log.e("HelloC", "WARNING: Could not load native library: " + ule.getMessage());
        }
    }
    public native int setupNativeTcpSocket();
    public native TaxiTrip getCabAvailability(int cabType,AddressObject source, AddressObject destinatin, TaxiTrip result);

    static JNIConnectionManager connectionManager = null;
    int socketId;
    public  static JNIConnectionManager getConnectionManager(){
        if (connectionManager == null){
            connectionManager = new JNIConnectionManager();
            connectionManager.socketId = 0;
        }
        return connectionManager;
    }
    public  void setupSocket(){
        if (this.socketId ==0){
            this.socketId = setupNativeTcpSocket();
        }
        Log.i("socketId",Integer.toString(this.socketId));

    }

    public int getSocketId() {
        return socketId;
    }

    public void JNIGetCabAvailability(int cabType, AddressObject source, AddressObject destinatin){

        TaxiTrip result =  getCabAvailability(cabType,source,destinatin,new TaxiTrip());
        Log.i("getCabAvailability--> result.getOpCode",result.getOpCode());
    }
    public native String getTaxiDetails();
    private native String getTaxiGpsMapDetails(int socketId);
    public  String JNIGetGpsMapDetails(){
        return getTaxiGpsMapDetails(socketId);
    }
}

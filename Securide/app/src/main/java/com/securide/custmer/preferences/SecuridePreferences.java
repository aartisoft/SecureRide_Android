package com.securide.custmer.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.securide.custmer.model.CustomerProfileObject;

public class SecuridePreferences {
    public static final String EMPTY_STRING_DEFAULT_VALUE = null;
    public static SharedPreferences PREFERENCE_PREFERENCE_STORE = null;
    public static final String STORE = "ALL_DETAILS";
    private static final String REGISTRATION_STATUS = "REGISTRATION_STATUS";
    private static final String USER_PROFILE = "USER_PROFILE";

    public static void init(Context ctx) {
        PREFERENCE_PREFERENCE_STORE = ctx.getSharedPreferences(STORE, Context.MODE_PRIVATE);
    }

    // setters
    public static void setRegistrationStatus(Boolean status) {
        SharedPreferences.Editor editor = PREFERENCE_PREFERENCE_STORE.edit();
        editor.putBoolean(REGISTRATION_STATUS, status);
        editor.commit();
    }

    // getters
    public static Boolean isRegistered() {
        return PREFERENCE_PREFERENCE_STORE.getBoolean(REGISTRATION_STATUS, false);
    }

    public static  void setCustomerProfile(CustomerProfileObject customerProfile){
        Gson gson = new Gson();
        String categoryListString = gson.toJson(customerProfile);
        SharedPreferences.Editor editor = PREFERENCE_PREFERENCE_STORE.edit();
        editor.putString(USER_PROFILE, categoryListString);
        editor.commit();
    }

    public static void setHomeAddress(String address, String location){
        SharedPreferences.Editor editor = PREFERENCE_PREFERENCE_STORE.edit();
        editor.putString("home_address", address);
        editor.putString("home_location", location);
        editor.commit();
    }
    public static String getHomeAdddress() {
        return PREFERENCE_PREFERENCE_STORE.getString("home_address","");
    }
    public static String getHomeLocation() {
        return PREFERENCE_PREFERENCE_STORE.getString("home_location","");
    }
    public static void setWorkAddress(String address,String location){
        SharedPreferences.Editor editor = PREFERENCE_PREFERENCE_STORE.edit();
        editor.putString("work_address", address);
        editor.putString("work_location", location);
        editor.commit();
    }
    public static String getWorkAdddress() {
        return PREFERENCE_PREFERENCE_STORE.getString("work_address","");
    }
    public static String getWorkLocation() {
        return PREFERENCE_PREFERENCE_STORE.getString("work_location","");
    }
}

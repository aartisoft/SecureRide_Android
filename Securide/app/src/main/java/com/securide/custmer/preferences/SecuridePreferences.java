package com.securide.custmer.preferences;

import android.content.Context;
import android.content.SharedPreferences;

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


}

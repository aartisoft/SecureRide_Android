package com.securide.custmer.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SecuridePreferences {
    public static final String EMPTY_STRING_DEFAULT_VALUE = null;
    public static SharedPreferences PREFERENCE_PREFERENCE_STORE = null;
    public static final String STORE = "ALL_DETAILS";
    private static final String REGISTRATION_STATUS = "REGISTRATION_STATUS";

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

}

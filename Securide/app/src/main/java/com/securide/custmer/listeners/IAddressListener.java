package com.securide.custmer.listeners;

import android.location.Address;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by pradeep.kumar on 3/13/16.
 */
public interface IAddressListener {
    void onAddressDispatch(List<String> list,JSONArray placePredsJsonArray);
}

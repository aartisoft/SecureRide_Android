package com.securide.custmer.listeners;

import android.location.Address;

import java.util.List;

/**
 * Created by pradeep.kumar on 3/13/16.
 */
public interface IAddressListener {
    void onAddressDispatch(List<Address> list);
}

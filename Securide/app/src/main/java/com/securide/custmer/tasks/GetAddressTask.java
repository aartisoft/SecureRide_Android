package com.securide.custmer.tasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.securide.custmer.listeners.IAddressListener;

import java.io.IOException;
import java.util.List;

/**
 * Created by pradeep.kumar on 3/13/16.
 */
public class GetAddressTask extends AsyncTask<Void, Void, List<Address>> {
    private String mLocationHint = null;
    private Context mContext = null;
    private IAddressListener mIAddressListener =null;

    public GetAddressTask(Context ctx, String locationHint, IAddressListener listener) {
        mLocationHint = locationHint;
        mContext = ctx;
        mIAddressListener = listener;
    }

    @Override
    protected List<Address> doInBackground(Void... params) {
        Geocoder geocoder = new Geocoder(mContext);
        List<Address> addresses = null;
        try {
            // Getting a maximum of 3 Address that matches the input text
            addresses = geocoder.getFromLocationName(mLocationHint, 4);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addresses;
    }

    @Override
    protected void onPostExecute(List<Address> addresses) {
        super.onPostExecute(addresses);

        if(mIAddressListener != null) {
            mIAddressListener.onAddressDispatch(addresses);
        }
    }
}

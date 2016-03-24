package com.securide.custmer.tasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.securide.custmer.Util.Constants;
import com.securide.custmer.Util.HTTPHandler;
import com.securide.custmer.listeners.IAddressListener;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pradeep.kumar on 3/13/16.
 */
public class GetAddressTask  {
    private Context mContext = null;
    private IAddressListener mIAddressListener =null;

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDAIb7josxX55yT-aam9XpCnbPgKWjwIjs";
    JSONArray placePredsJsonArray;
    public GetAddressTask(Context ctx, String locationHint, IAddressListener listener) {
        mContext = ctx;
        mIAddressListener = listener;

    }

    public void autocomplete(String input) {
        ArrayList<String> resultList = null;

        try {
            String location = Double.toString(Constants.lat)+","+Double.toString(Constants.lng);
            StringBuilder sb = new StringBuilder(PLACES_API_BASE
                    + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY+"&location="+location+"&radius=50000");
            // sb.append("&components=country:in");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            HTTPHandler handler = HTTPHandler.defaultHandler();
            // Create a JSON object hierarchy from the results
            List<NameValuePair> params = null;
            JSONObject jsonObj = handler.doGet(sb.toString(), params);
            if (jsonObj == null){
                if(mIAddressListener != null) {
                    mIAddressListener.onAddressDispatch(null,null);
                }
            }
            placePredsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(placePredsJsonArray.length());
            for (int i = 0; i < placePredsJsonArray.length(); i++) {
                System.out.println(placePredsJsonArray.getJSONObject(i)
                        .getString("description"));
                System.out
                        .println("============================================================");
                resultList.add(placePredsJsonArray.getJSONObject(i).getString(
                        "description"));
            }
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(mIAddressListener != null) {
            mIAddressListener.onAddressDispatch(resultList,placePredsJsonArray);
        }
    }
}

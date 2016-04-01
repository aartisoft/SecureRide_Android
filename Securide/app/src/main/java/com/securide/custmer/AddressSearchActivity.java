package com.securide.custmer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.securide.custmer.Util.Constants;
import com.securide.custmer.Util.HTTPHandler;
import com.securide.custmer.adapters.AddressListAdapter;
import com.securide.custmer.controllers.AddressController;
import com.securide.custmer.listeners.IAddressListener;
import com.securide.custmer.model.AddressObject;
import com.securide.custmer.preferences.SecuridePreferences;
import com.securide.custmer.tasks.GetAddressTask;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AddressSearchActivity extends AppCompatActivity implements View.OnClickListener, IAddressListener {
    public static final int DELAY_TIME = 600;
    private ListView mSearchListView = null;
    private EditText mAddressEditText = null;
    private Button mOkButton = null;
    private AddressListAdapter mAddressListAdapter = null;
    private Context mContext = AddressSearchActivity.this;
    private GetAddressTask mGetAddressTask = null;
    private String mKeyword = null;
    private Timer mSearchTextTimer = null;

    Boolean isPickUp = false;
    String mLattitude, mLongitude;
    JSONArray placePredsJsonArray;
    AddressObject addressObject;
    TextView add_home,add_work;
    RelativeLayout homeAddressLayout,workAddressLayout;
    TextView homeAddress, homeAddressChange,workAddress,workAddressChange;
    private  boolean homeAddressChangeClicked = false;
    private  boolean workAddressChangeClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isPickUp = getIntent().getExtras().getBoolean(Constants.Key_IsPickup, false);
        setContentView(R.layout.activity_address_search);
        mSearchListView = (ListView) findViewById(R.id.search_list);
        mSearchListView.setOnItemClickListener(onItemClickListener);
        mAddressEditText = (EditText) findViewById(R.id.search_text);
        mOkButton = (Button) findViewById(R.id.ok_btn);
        mOkButton.setOnClickListener(this);
        mAddressEditText.addTextChangedListener(mAddressKeywordListener);
        add_home = (TextView)findViewById(R.id.add_home);
        add_home.setOnClickListener(this);
        add_work = (TextView)findViewById(R.id.add_work);
        add_work.setOnClickListener(this);

        homeAddressLayout = (RelativeLayout) findViewById(R.id.homeAddressLayout);
        homeAddressLayout.setOnClickListener(this);
        homeAddress = (TextView) findViewById(R.id.homeAddress);
        homeAddressChange = (TextView)findViewById(R.id.homeAddressChange);
        homeAddressChange.setOnClickListener(this);
        if (!SecuridePreferences.getHomeAdddress().equals("")){
            homeAddressLayout.setVisibility(View.VISIBLE);
            homeAddress.setText(SecuridePreferences.getHomeAdddress());
            add_home.setVisibility(View.GONE);
        }

        workAddressLayout = (RelativeLayout) findViewById(R.id.workAddressLayout);
        workAddressLayout.setOnClickListener(this);
        workAddress = (TextView) findViewById(R.id.workAddress);
        workAddressChange = (TextView)findViewById(R.id.workAddressChange);
        workAddressChange.setOnClickListener(this);
        if (!SecuridePreferences.getWorkAdddress().equals("")){
            workAddressLayout.setVisibility(View.VISIBLE);
            workAddress.setText(SecuridePreferences.getWorkAdddress());
            add_work.setVisibility(View.GONE);
        }
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAddressListAdapter != null) {
                    mAddressListAdapter.setSelectedAddress(position);
                    closeKeyBoard();
                    setAddress();
                }
        }
    };

    TextWatcher mAddressKeywordListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //do noop

        }

        @Override
        public void onTextChanged(final CharSequence s, int start, int before, int count) {
            mKeyword = s.toString();
            // user is typing: reset already started timer (if existing)
            if (mSearchTextTimer != null) {
                mSearchTextTimer.cancel();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            findViewById(R.id.mProgressbar).setVisibility(View.VISIBLE);
            // user typed: start the timer
            mSearchTextTimer = new Timer();
            mSearchTextTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    initSearchTask(mKeyword);
                }
            }, DELAY_TIME); // 600ms delay before the timer executes the „run“ method from TimerTask
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn:
                setAddress();
                break;
            case R.id.add_home:
                homeAddressLayout.setVisibility(View.GONE);
                add_home.setVisibility(View.GONE);
                workAddressLayout.setVisibility(View.GONE);
                add_work.setVisibility(View.GONE);
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
                        toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                homeAddressChangeClicked = true;
                workAddressChangeClicked = false;
                break;
            case R.id.homeAddressChange:
                homeAddressLayout.setVisibility(View.GONE);
                add_home.setVisibility(View.GONE);
                workAddressLayout.setVisibility(View.GONE);
                add_work.setVisibility(View.GONE);
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
                        toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                homeAddressChangeClicked = true;
                workAddressChangeClicked = false;
                break;
            case R.id.add_work:
                workAddressLayout.setVisibility(View.GONE);
                add_work.setVisibility(View.GONE);
                homeAddressLayout.setVisibility(View.GONE);
                add_home.setVisibility(View.GONE);
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
                        toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                workAddressChangeClicked = true;
                homeAddressChangeClicked = false;
                break;
            case R.id.workAddressChange:
                workAddressLayout.setVisibility(View.GONE);
                add_work.setVisibility(View.GONE);
                homeAddressLayout.setVisibility(View.GONE);
                add_home.setVisibility(View.GONE);
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
                        toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                workAddressChangeClicked = true;
                homeAddressChangeClicked = false;
                break;
            case R.id.homeAddressLayout:
                String[] latlong =  SecuridePreferences.getHomeLocation().split(";");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                LatLng location = new LatLng(latitude,longitude);
                String address = SecuridePreferences.getHomeAdddress();
                AddressObject object = new AddressObject();
                object.setFormatedAddress(address);
                if (isPickUp) {
                    AddressController.getInstance().setSourceLocaton(location);
                    AddressController.getInstance().setSelectedSourceAddress(object);
                }else{
                    AddressController.getInstance().setDestinationLocaton(location);
                    AddressController.getInstance().setSelectedDestinationAddress(object);
                }
                finish();
                break;
            case R.id.workAddressLayout:
                String[] latlong1 =  SecuridePreferences.getWorkLocation().split(";");
                double latitude1 = Double.parseDouble(latlong1[0]);
                double longitude1 = Double.parseDouble(latlong1[1]);
                LatLng location1 = new LatLng(latitude1,longitude1);
                String address1 = SecuridePreferences.getWorkAdddress();
                AddressObject object1 = new AddressObject();
                object1.setFormatedAddress(address1);

                if (isPickUp) {
                    AddressController.getInstance().setSourceLocaton(location1);
                    AddressController.getInstance().setSelectedSourceAddress(object1);
                }else{
                    AddressController.getInstance().setDestinationLocaton(location1);
                    AddressController.getInstance().setSelectedDestinationAddress(object1);
                }
                finish();
                break;
        }
    }
    private void setAddress(){
        if (mAddressListAdapter != null) {
            String place_id = null;
//            if (isPickUp) {
//                AddressController.getInstance().setSelectedSourceAddress(mAddressListAdapter.getSelectedItem());
//            } else {
//                AddressController.getInstance().setSelectedDestinationAddress(mAddressListAdapter.getSelectedItem());
//            }
            try {
                place_id = placePredsJsonArray.getJSONObject(mAddressListAdapter.mSelectedAddressIndex).getString("place_id");
                new GeocodeAsnc().execute(place_id);
            } catch (JSONException e) {
                e.printStackTrace();
                finish();
            }
            // Toast.makeText(getActivity(), str+" : "+place_id,
            // Toast.LENGTH_SHORT).show();
        }

    }
    private void initSearchTask(String keyword) {
        if (mGetAddressTask == null) {
            mGetAddressTask = new GetAddressTask(mContext, keyword, this);
        }
        mGetAddressTask.autocomplete(keyword, !isPickUp);
    }

    private void setOrUpdateAdapter(final List<String> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.mProgressbar).setVisibility(View.GONE);
                if (mAddressListAdapter == null && list != null) {
                    mAddressListAdapter = new AddressListAdapter(mContext, 0, list);
                    mSearchListView.setAdapter(mAddressListAdapter);
                } else {
                    if (mAddressListAdapter != null) {
                        mAddressListAdapter.updateListView(list);
                    }
                }

            }
        });

    }


    @Override
    public void onAddressDispatch(List<String> list, JSONArray placePredsJsonArray) {
        this.placePredsJsonArray = placePredsJsonArray;
        setOrUpdateAdapter(list);
    }


    private void closeKeyBoard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public class GeocodeAsnc extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.mProgressbar).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="
                    + params[0] + "&key=" + Constants.API_KEY;
            HTTPHandler handler = HTTPHandler.defaultHandler();
            List<NameValuePair> params1 = null;
            JSONObject jsonObj = handler.doGet(url, params1);
            try {
                JSONObject routeObject = jsonObj.getJSONObject("result");

                mLattitude = routeObject.getJSONObject("geometry")
                        .getJSONObject("location").getString("lat");
                mLongitude = routeObject.getJSONObject("geometry")
                        .getJSONObject("location").getString("lng");
                addressObject = new AddressObject();
                addressObject.setLattitude(mLattitude);
                addressObject.setLongitude(mLongitude);
                addressObject.setFormatedAddress(routeObject.getString("formatted_address"));

                JSONArray address_components = routeObject.getJSONArray("address_components");
                for (int i = 0;i<address_components.length();i++){
                    JSONObject component = (JSONObject) address_components.get(i);
                    JSONArray types = component.getJSONArray("types");
                    String long_name = component.getString("long_name");
                    for (int j = 0 ; j<types.length();j++){
                        String type = (String) types.get(j);

                        if (type.equalsIgnoreCase("route")){
                            addressObject.setStreatName(long_name);
                            break;
                        }
                        if (type.equalsIgnoreCase("sublocality_level_2")){
                            addressObject.setStreatName(addressObject.getStreatName()+","+long_name);
                            break;
                        }
                        if (type.equalsIgnoreCase("sublocality_level_1")){
                            addressObject.setLandmark(long_name);
                            break;
                        }
                        if (type.equalsIgnoreCase("locality")){
                            addressObject.setCity(long_name);
                            break;
                        }
                        if (type.equalsIgnoreCase("postal_code")){
                            addressObject.setPincode(long_name);
                            break;
                        }
                    }

                }


                System.out.println("Lattitude : " + mLattitude);
                System.out.println("Longitude : " + mLongitude);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            findViewById(R.id.mProgressbar).setVisibility(View.GONE);
            LatLng location = new LatLng(Double.parseDouble(mLattitude),Double.parseDouble(mLongitude));
            if(mAddressListAdapter != null) {
                mAddressListAdapter.clearList();
                mAddressEditText.setText("");
            }
            if(homeAddressChangeClicked){
                SecuridePreferences.setHomeAddress(addressObject.getFormatedAddress(),mLattitude+";"+mLongitude);
                homeAddressLayout.setVisibility(View.VISIBLE);
                homeAddress.setText(SecuridePreferences.getHomeAdddress());
                add_home.setVisibility(View.GONE);
                if (!SecuridePreferences.getWorkAdddress().equals("")){
                    workAddressLayout.setVisibility(View.VISIBLE);
                    workAddress.setText(SecuridePreferences.getWorkAdddress());
                    add_work.setVisibility(View.GONE);
                }else {
                    workAddressLayout.setVisibility(View.GONE);
                    add_work.setVisibility(View.VISIBLE);
                }
            }else if(workAddressChangeClicked){
                SecuridePreferences.setWorkAddress(addressObject.getFormatedAddress(),mLattitude+";"+mLongitude);
                workAddressLayout.setVisibility(View.VISIBLE);
                workAddress.setText(SecuridePreferences.getWorkAdddress());
                add_work.setVisibility(View.GONE);
                if (!SecuridePreferences.getHomeAdddress().equals("")){
                    homeAddressLayout.setVisibility(View.VISIBLE);
                    homeAddress.setText(SecuridePreferences.getHomeAdddress());
                    add_home.setVisibility(View.GONE);
                }else {
                    homeAddressLayout.setVisibility(View.GONE);
                    add_home.setVisibility(View.VISIBLE);
                }

            }
            else {
                if (isPickUp) {
                    AddressController.getInstance().setSourceLocaton(location);
                    AddressController.getInstance().setSelectedSourceAddress(addressObject);
                }else{
                    AddressController.getInstance().setDestinationLocaton(location);
                    AddressController.getInstance().setSelectedDestinationAddress(addressObject);
                }
                finish();
            }
        }

    }
}

package com.securide.custmer;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.securide.custmer.Util.Constants;
import com.securide.custmer.Util.MyLocation;
import com.securide.custmer.Util.MyLocation.LocationResult;
import com.securide.custmer.controllers.AddressController;
import com.securide.custmer.listeners.IMapListener;
import com.securide.custmer.preferences.SecuridePreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements IMapListener {
    public static final String TAG = "MapsActivity : ";
    private EditText mPickupPoint = null;
    private EditText mDropPoint = null;
    private LinearLayout horizontalScrollView = null;
    private LayoutInflater mInflater = null;
    private Button confirmBtn = null;
    private Context mContext = MapsActivity.this;

    LinearLayout llFirstCab, llSecondCab, llThirdCabs;

    int selectedCabIndex = -1;
    MapsFragment mapFragment;

    MyLocation myLocation;
    LocationResult result;
    String mPickupAddress;
    LocationManager locMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLocation = new MyLocation();
        locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setContentView(R.layout.activity_maps);
        mInflater = LayoutInflater.from(mContext);
        llFirstCab = (LinearLayout) findViewById(R.id.llFirstCab);
        llSecondCab = (LinearLayout) findViewById(R.id.llSecondCab);
        llThirdCabs = (LinearLayout) findViewById(R.id.llThirdCabs);
        List<CabModel> cabs = prepareCabsList();
        prepareAndShowCabs(llFirstCab, cabs.get(0));
        prepareAndShowCabs(llSecondCab, cabs.get(1));
        prepareAndShowCabs(llThirdCabs, cabs.get(2));

        mPickupPoint = (EditText) findViewById(R.id.pick_point);
        mDropPoint = (EditText) findViewById(R.id.drop_point);
        horizontalScrollView = (LinearLayout) findViewById(R.id.carParent);
        confirmBtn = (Button) findViewById(R.id.btn_confirm);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mapFragment = MapsFragment.newInstance();
        fragmentTransaction.add(R.id.temp_fragment, mapFragment, "maps");
        fragmentTransaction.commitAllowingStateLoss();

//        prepareAndShowCabs();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                if (TextUtils.isEmpty(mPickupPoint.getText().toString())) {
                    Toast.makeText(mContext, "Please select Pick up point", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(mDropPoint.getText().toString())) {
                    Toast.makeText(mContext, "Please select Drop point", Toast.LENGTH_SHORT).show();
                } else if (selectedCabIndex == -1) {
                    Toast.makeText(mContext, "Please select Cab type", Toast.LENGTH_SHORT).show();
                } else {
                    if (!SecuridePreferences.isRegistered()) {
                        Intent intent = new Intent(mContext, PrimaryRegistrationActivity.class);
                        intent.putExtra(Constants.Key_RegistrationFromMap, true);
                        startActivity(intent);
                    } else {
                        i = new Intent(MapsActivity.this, CostDetailsActivity.class);
                        startActivity(i);
                    }

                }
            }
        });

        mDropPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddressSearchActivity.class);
                intent.putExtra(Constants.Key_IsPickup, false);
                startActivity(intent);
            }
        });
        mPickupPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddressSearchActivity.class);
                intent.putExtra(Constants.Key_IsPickup, true);
                startActivity(intent);
            }
        });

        llFirstCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCabIndex = 0;
                updateCabSelection();
            }
        });
        llSecondCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCabIndex = 1;
                updateCabSelection();
            }
        });
        llThirdCabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCabIndex = 2;
                updateCabSelection();

            }
        });

        getCurrentLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();

        String address = AddressController.getInstance().getSelectedDestinationAddress();
        mDropPoint.setText(address);
        mPickupPoint.setText(AddressController.getInstance().getSelectedSourceAddress());
        if (AddressController.getInstance().getSourceLocaton() != null) {
            mapFragment.updatePickUpLocation(AddressController.getInstance().getSourceLocaton());
        }
        if (AddressController.getInstance().getDestinationLocaton() != null) {
            mapFragment.updateDropLocation(AddressController.getInstance().getDestinationLocaton());
        }
    }

    private void prepareAndShowCabs(LinearLayout view, CabModel cabModel) {
        ((TextView) view.findViewById(R.id.car_type)).setText(cabModel.name);
        ((ImageView) view.findViewById(R.id.car_image)).setImageResource(cabModel.image);
    }

    private List<CabModel> prepareCabsList() {
        List<CabModel> list = new ArrayList<>(0);
        list.add(new CabModel("Limousine", R.drawable.limousine));
        list.add(new CabModel("Regular", R.drawable.regular));
        list.add(new CabModel("Special", R.drawable.special));
        return list;
    }

    void updateCabSelection() {
        View vwFirstCab = (View) llFirstCab.findViewById(R.id.selectionView);
        View vwSecondCab = (View) llSecondCab.findViewById(R.id.selectionView);
        View vwThirdCab = (View) llThirdCabs.findViewById(R.id.selectionView);
        vwFirstCab.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.white));
        vwSecondCab.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.white));
        vwThirdCab.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.white));
        if (selectedCabIndex == 0) {
            vwFirstCab.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.lighter_grey));
        } else if (selectedCabIndex == 1) {
            vwSecondCab.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.lighter_grey));
        } else if (selectedCabIndex == 2) {
            vwThirdCab.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.lighter_grey));
        }
    }

    @Override
    public void onCurrentAddress(String location) {
        mPickupPoint.setText(location);
    }

    private void getAddress(final Location location) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder;
                List<Address> address;
                geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                try {
                    address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (address.size() > 0) {
                        String addr = address.get(0).getAddressLine(0);
                        String city = address.get(0).getAddressLine(1);
                        String country = address.get(0).getAddressLine(2);
                        mPickupAddress = addr.concat(city).concat(country);
                    }
                } catch (Exception e) {

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AddressController.getInstance().setSelectedSourceAddress(mPickupAddress);
                        onCurrentAddress(mPickupAddress);
                    }
                });
            }
        });
        thread.start();
    }

    void getCurrentLocation() {
        MyLocation.LocationResult locationResult = new LocationResult() {
            @Override
            public void gotLocation(Location location) {

                if (location != null) {

                    Constants.lat = location.getLatitude();
                    Constants.lng = location.getLongitude();
                    getAddress(location);
                    LatLng latLng = new LatLng(Constants.lat, Constants.lng);
                    mapFragment.updatePickUpLocation(latLng);
                    myLocation.cancelTimer();
                    AddressController.getInstance().setSourceLocaton(latLng);
                } else {
                    Criteria criteria = new Criteria();
                    String curLoc = locMan.getBestProvider(criteria, true);
                    location = locMan.getLastKnownLocation(curLoc);
                    if (location != null) {
                        Constants.lat = location.getLatitude();
                        Constants.lng = location.getLongitude();
                        LatLng latLng = new LatLng(Constants.lat, Constants.lng);
                        mapFragment.updatePickUpLocation(latLng);
                        AddressController.getInstance().setSourceLocaton(latLng);
                        getAddress(location);
                    }

                }
            }
        };
        if (myLocation.getLocation(getApplicationContext(),
                locationResult)) {
        }
    }
}

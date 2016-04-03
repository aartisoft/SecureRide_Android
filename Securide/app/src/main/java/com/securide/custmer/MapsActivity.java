package com.securide.custmer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.securide.custmer.Util.HTTPHandler;
import com.securide.custmer.Util.MyLocation;
import com.securide.custmer.Util.MyLocation.LocationResult;
import com.securide.custmer.connection.core.JNIConnectionManager;
import com.securide.custmer.controllers.AddressController;
import com.securide.custmer.listeners.IMapListener;
import com.securide.custmer.model.AddressObject;
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
    private boolean isGpsDialogShown = false;

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
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);

        } else {
            getCurrentLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();

                } else {
                    finish();
                    Toast.makeText(MapsActivity.this, "SecuRide can't work without this Permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!HTTPHandler.defaultHandler().isConnectingToInternet(this)){
            HTTPHandler.defaultHandler().showDialog(this,"No Internet Connectivity Found");
            return;
        }
        if(!isGpsDialogShown) {
            checkLocationEnabled();
        }
        if (AddressController.getInstance().getSelectedDestinationAddress() != null) {
            String address = AddressController.getInstance().getSelectedDestinationAddress().getFormatedAddress();
            mDropPoint.setText(address);
        }
        if (AddressController.getInstance().getSelectedSourceAddress() != null) {
            mPickupPoint.setText(AddressController.getInstance().getSelectedSourceAddress().getFormatedAddress());
        }
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
        list.add(new CabModel("Limousine", R.drawable.car_limousin));
        list.add(new CabModel("Regular", R.drawable.car_regular));
        list.add(new CabModel("Special", R.drawable.car_spl));
        return list;
    }

    void updateCabSelection() {
        ImageView vwFirstCab = (ImageView) llFirstCab.findViewById(R.id.car_image);
        ImageView vwSecondCab = (ImageView) llSecondCab.findViewById(R.id.car_image);
        ImageView vwThirdCab = (ImageView) llThirdCabs.findViewById(R.id.car_image);
        vwFirstCab.setImageResource(R.drawable.car_limousin);
        vwSecondCab.setImageResource(R.drawable.car_regular);
        vwThirdCab.setImageResource(R.drawable.car_spl);
        if (selectedCabIndex == 0) {
            vwFirstCab.setImageResource(R.drawable.car_limousin_selected);
        } else if (selectedCabIndex == 1) {
            vwSecondCab.setImageResource(R.drawable.car_regular_selected);
        } else if (selectedCabIndex == 2) {
            vwThirdCab.setImageResource(R.drawable.car_spl_selected);
        }
        getCabAvailability();
    }

    void getCabAvailability() {
        int cabType = selectedCabIndex + 1;
        JNIConnectionManager.getConnectionManager().
                JNIGetCabAvailability(cabType,
                        AddressController.getInstance().getSelectedSourceAddress(),
                        AddressController.getInstance().getSelectedDestinationAddress());
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
                        AddressObject addressObject = new AddressObject();
                        String addr = address.get(0).getAddressLine(0);
                        String city = address.get(0).getAddressLine(1);
                        String country = address.get(0).getAddressLine(2);
                        addressObject.setStreatName(addr);
                        addressObject.setCity(city);
                        addressObject.setCountry(country);
                        mPickupAddress = addr.concat(city).concat(country);
                        addressObject.setFormatedAddress(mPickupAddress);
                        addressObject.setLattitude(Double.toString(location.getLatitude()));
                        addressObject.setLongitude(Double.toString(location.getLongitude()));
                        AddressController.getInstance().setSelectedSourceAddress(addressObject);
                    }
                } catch (Exception e) {

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

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

    private void checkLocationEnabled(){
        isGpsDialogShown = true;
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled) {
            // notify user
            final AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    paramDialogInterface.dismiss();
                }
            });
            dialog.show();
        }else {
            getCurrentLocation();
        }
    }
}

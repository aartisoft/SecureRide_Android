package com.securide.custmer;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
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

import com.securide.custmer.controllers.AddressController;
import com.securide.custmer.listeners.IMapListener;
import com.securide.custmer.preferences.SecuridePreferences;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements IMapListener {
    public static final String TAG = "MapsActivity : ";
    private EditText mPickupPoint = null;
    private EditText mDropPoint = null;
    private LinearLayout horizontalScrollView = null;
    private LayoutInflater mInflater = null;
    private Button confirmBtn = null;
    private Context mContext = MapsActivity.this;

    LinearLayout llFirstCab,llSecondCab,llThirdCabs;

    int selectedCabIndex = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mInflater = LayoutInflater.from(mContext);
        llFirstCab = (LinearLayout) findViewById(R.id.llFirstCab);
        llSecondCab = (LinearLayout) findViewById(R.id.llSecondCab);
        llThirdCabs = (LinearLayout) findViewById(R.id.llThirdCabs);
        List<CabModel> cabs = prepareCabsList();
        prepareAndShowCabs(llFirstCab,cabs.get(0));
        prepareAndShowCabs(llSecondCab,cabs.get(1));
        prepareAndShowCabs(llThirdCabs,cabs.get(2));

        mPickupPoint = (EditText) findViewById(R.id.pick_point);
        mDropPoint = (EditText) findViewById(R.id.drop_point);
        horizontalScrollView = (LinearLayout) findViewById(R.id.carParent);
        confirmBtn = (Button) findViewById(R.id.btn_confirm);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.temp_fragment, MapsFragment.newInstance(), "maps");
        fragmentTransaction.commitAllowingStateLoss();

//        prepareAndShowCabs();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                if (TextUtils.isEmpty(mDropPoint.getText().toString())) {
                    Toast.makeText(mContext, "Please select Drop point", Toast.LENGTH_SHORT).show();
                } else {
                    if (!SecuridePreferences.isRegistered()) {
                        i = new Intent(MapsActivity.this, SecondryRegistrationActivity.class);
                    } else {
                        i = new Intent(MapsActivity.this, CostDetailsActivity.class);
                    }
                    startActivity(i);
                }
            }
        });

        mDropPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, AddressSearchActivity.class));
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

    }

    @Override
    protected void onResume() {
        super.onResume();

        Address address = AddressController.getInstance().getSelectedDestinationAddress();
        if ( address != null && address.getMaxAddressLineIndex() > 0) {
          mDropPoint.setText(address.getAddressLine(0)+", "+address.getAddressLine(1) + ", " + address.getAddressLine(2));
        }
    }

    private void prepareAndShowCabs(LinearLayout view,CabModel cabModel) {
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

    void updateCabSelection(){
        llFirstCab.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.white));
        llSecondCab.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.white));
        llThirdCabs.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.white));
        if (selectedCabIndex ==0){
            llFirstCab.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.lighter_grey));
        }else if (selectedCabIndex ==1){
            llSecondCab.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.lighter_grey));
        }else if (selectedCabIndex ==2){
            llThirdCabs.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.lighter_grey));
        }
    }

    @Override
    public void onCurrentAddress(String location) {
        mPickupPoint.setText(location);
    }
}

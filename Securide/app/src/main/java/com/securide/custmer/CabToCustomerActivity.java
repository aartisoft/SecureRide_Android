package com.securide.custmer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.securide.custmer.connection.core.JNIConnectionManager;
import com.securide.custmer.controllers.AddressController;

/**
 * Created by pradeep.kumar on 3/16/16.
 */
public class CabToCustomerActivity extends FragmentActivity implements View.OnClickListener{
    public static final String TAG = "CabToCustomerActivity:";
    private final int MAP_UPDATE_DELAY = 5000;
    private ImageView back,phoneIcon;
    static MapsFragment mapFragment;
    private Handler handler = new Handler();
    private LatLng location;
    double lat[]= {12.980424,12.984272,12.987952,12.991799};
    double lon[] = {77.632914,77.644072,77.652998,77.662268};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_to_customer);

        back = (ImageView)findViewById(R.id.edit_profile_back_bttn);
        back.setOnClickListener(this);
        phoneIcon = (ImageView)findViewById(R.id.phoneIcon);
        phoneIcon.setOnClickListener(this);

        FragmentManager fm = getSupportFragmentManager();
        PaymentModeDialog paymentModeDialog = new PaymentModeDialog();
        paymentModeDialog.setCancelable(false);
        fm.beginTransaction().add(paymentModeDialog, "payment_mode_chooser").commitAllowingStateLoss();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mapFragment = MapsFragment.newInstance();
        fragmentTransaction.add(R.id.temp_fragment, mapFragment, "maps");
        fragmentTransaction.commitAllowingStateLoss();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.edit_profile_back_bttn){
            finish();
        }else if(v.getId() == R.id.phoneIcon){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:0123456789"));
            startActivity(intent);
        }
    }

    public  class PaymentModeDialog extends DialogFragment implements View.OnClickListener {
        private Button process;
        LinearLayout credit_card_Layout, payme_Cab_Layout;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.payment_mode_dialog_screen, container);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            getDialog().getWindow().setAttributes(params);
            params.y = 100;
            int divierId = getDialog().getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = getDialog().findViewById(divierId);
            try {
                divider.setBackgroundColor(Color.TRANSPARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            process = (Button)view.findViewById(R.id.process);
            process.setOnClickListener(this);
            payme_Cab_Layout = (LinearLayout)view.findViewById(R.id.payme_Cab_Layout);
            payme_Cab_Layout.setOnClickListener(this);
            credit_card_Layout = (LinearLayout)view.findViewById(R.id.credit_card_Layout);
            credit_card_Layout.setEnabled(false);
            return view;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.payme_Cab_Layout:

                    break;
                case R.id.process:
                   getDialog().cancel();
                   //Log.d(TAG, "" + AddressController.getInstance().getSourceLocaton());
                  // Log.d(TAG, "" + AddressController.getInstance().getDestinationLocaton());
                 //  mapFragment.updatePickUpLocation(AddressController.getInstance().getSourceLocaton());
                  // mapFragment.updateDropLocation(AddressController.getInstance().getDestinationLocaton());
                   handler.postDelayed(updateMarker, 0000);
                   break;
            }
        }

    }

        Runnable updateMarker = new Runnable() {
            @Override
            public void run() {

               String message = JNIConnectionManager.getConnectionManager().JNIGetGpsMapDetails();
                Log.e("Lat Long : ","Value: "+message);
                if (!message.contains(";")) {
                }else{
                    String[] coordinates = message.split(";");
                    int newSocketId = (Integer.valueOf(coordinates[0]));
                    int socketId = JNIConnectionManager.getConnectionManager().getSocketId();
                    if (socketId <= newSocketId) {
                        socketId = newSocketId;
                        double longitude = Integer.valueOf(coordinates[1]) + (Integer.valueOf(coordinates[2]) / 60);
                        double latitude = Integer.valueOf(coordinates[3]) + (Integer.valueOf(coordinates[4]) / 60);

                        location = new LatLng(longitude, latitude);
                        mapFragment.updateCabLocation(location);
                        handler.postDelayed(updateMarker, MAP_UPDATE_DELAY);

                    }
                }
            }
        };

}

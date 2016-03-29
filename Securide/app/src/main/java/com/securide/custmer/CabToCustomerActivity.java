package com.securide.custmer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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

import com.securide.custmer.controllers.AddressController;

/**
 * Created by pradeep.kumar on 3/16/16.
 */
public class CabToCustomerActivity extends FragmentActivity implements View.OnClickListener{
    public static final String TAG = "CabToCustomerActivity : ";
    private ImageView back,phoneIcon;
    static MapsFragment mapFragment;
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

    public static class PaymentModeDialog extends DialogFragment implements View.OnClickListener {
        private Button process;
        LinearLayout credit_card_Layout, payme_Cab_Layout;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.payment_mode_dialog_screen, container);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.y = 100;
            getDialog().getWindow().setAttributes(params);
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
                   Log.d("Lat",""+AddressController.getInstance().getSourceLocaton());
                    Log.d("Lon", "" + AddressController.getInstance().getDestinationLocaton());
                   mapFragment.updatePickUpLocation(AddressController.getInstance().getSourceLocaton());
                   mapFragment.updateDropLocation(AddressController.getInstance().getDestinationLocaton());

                    break;
            }
        }


    }
}

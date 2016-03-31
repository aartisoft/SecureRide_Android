package com.securide.custmer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.securide.custmer.controllers.AddressController;
import com.securide.custmer.preferences.SecuridePreferences;

public class CostDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnAccept,btnReject;
    private TextView tvPick_point,tvDrop_point;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cost_details);
        initilizeAllViews();
    }

    private void initilizeAllViews() {
        btnAccept = (Button)findViewById(R.id.Accept);
        btnAccept.setOnClickListener(this);
        btnReject = (Button)findViewById(R.id.Reject);
        btnReject.setOnClickListener(this);
        tvPick_point = (TextView)findViewById(R.id.pick_point);
        tvPick_point.setText(AddressController.getInstance().getSelectedSourceAddress().getFormatedAddress());
        tvDrop_point = (TextView)findViewById(R.id.drop_point);
        tvDrop_point.setText(AddressController.getInstance().getSelectedDestinationAddress().getFormatedAddress());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Accept:
                Intent intent = new Intent(CostDetailsActivity.this,CabToCustomerActivity.class);
                startActivity(intent);
                break;
            case R.id.Reject:
                finish();
                break;
        }
    }
}

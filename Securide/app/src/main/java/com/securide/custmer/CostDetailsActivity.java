package com.securide.custmer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.securide.custmer.connection.core.JNIConnectionManager;
import com.securide.custmer.controllers.AddressController;
import com.securide.custmer.preferences.SecuridePreferences;

public class CostDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAXI_TAG = "CabDetails";
    private Button btnAccept,btnReject;
    private TextView tvPick_point,tvDrop_point;
    private TextView tvArrival_time,tvEstimated_time,tvCab_number,tvDriver_name,tvTrip_cost;
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

        tvArrival_time = (TextView)findViewById(R.id.arrival_time);
        tvEstimated_time = (TextView)findViewById(R.id.estimated_time);
        tvCab_number = (TextView)findViewById(R.id.cab_number);
        tvDriver_name = (TextView)findViewById(R.id.driver_name);
        tvTrip_cost = (TextView)findViewById(R.id.trip_cost);

        //getCabInfo();
        new MyAsyncTask().execute();
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

    class  MyAsyncTask extends AsyncTask<Void,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.mProgressbar).setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(Void... params) {
            String taxiTrip = JNIConnectionManager.getConnectionManager().getTaxiDetails();

            return taxiTrip;
        }
        @Override
        protected void onPostExecute(String taxiTrip) {
            super.onPostExecute(taxiTrip);
            if (taxiTrip != null && !taxiTrip.isEmpty()) {
                Log.i(TAXI_TAG, "Taxi taxiTrip: " + taxiTrip);
                String[] result = taxiTrip.split(";");
                String taxiNumber = result[0];
                Log.i(TAXI_TAG, "Taxi Number: " + taxiNumber);
                tvCab_number.setText(taxiNumber);

                String driverNumber = result[1];
                Log.i(TAXI_TAG, "Taxi driverNumber: " + driverNumber);

                String driverName = result[2];
                Log.i(TAXI_TAG, "Taxi driverName: " + driverName);
                tvDriver_name.setText(driverName);


                String estimatedHours = result[3];
                String estimatedMinutes = result[4];
                String estimatedTime = estimatedHours + " hour " + estimatedMinutes + " minutes";
                Log.i(TAXI_TAG, "Taxi estimatedTime: " + estimatedTime);
                tvEstimated_time.setText(estimatedTime);

                String cabArrivalEstHrs = result[5];
                String cabArrivalEstMin = result[6];
                String cabArrivalTime = cabArrivalEstHrs + " hour " + cabArrivalEstMin + " minutes";
                Log.i(TAXI_TAG, "Taxi cabArrivalTime: " + cabArrivalTime);
                tvArrival_time.setText(cabArrivalTime);

                String estimatedCost = result[7];
                Log.i(TAXI_TAG, "Taxi estimatedCost: " + estimatedCost);
                tvTrip_cost.setText(estimatedCost);
            }
            findViewById(R.id.mProgressbar).setVisibility(View.GONE);
        }

    }

}

package com.securide.custmer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.securide.custmer.Util.Constants;
import com.securide.custmer.connection.core.ConnectionConstants;
import com.securide.custmer.connection.core.SecurideClient;
import com.securide.custmer.connection.core.SocketConnector;
import com.securide.custmer.controllers.AddressController;
import com.securide.custmer.preferences.SecuridePreferences;

public class FirstActivity extends FragmentActivity implements View.OnClickListener {

    Button mBookRide = null;
    Button mRegister = null;
    Context mContext = FirstActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        mBookRide = (Button) findViewById(R.id.book_ride);
        mRegister = (Button) findViewById(R.id.register_now);

        mBookRide.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        if ( SecuridePreferences.isRegistered()){
            startActivity(new Intent(mContext, MapsActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int socket = -1;
        switch (v.getId()) {
            case R.id.book_ride:
                //SocketConnector.processUserRequest(socket, ConnectionConstants.TAXI_REQUEST);
                startActivity(new Intent(mContext, MapsActivity.class));
                break;
            case R.id.register_now:
                /*Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SocketConnector socketConnector = new SocketConnector();
                         socketConnector.openSocket("50.255.26.100", 14001);
                    }
                });
                t.start();*/

                //socket = SocketConnector.setupTcpSocket(14001, ipToLong("100.26.255.50"));
                //SocketConnector.init();

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SecurideClient.main();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                //t.start();
                Intent intent = new Intent(mContext, PrimaryRegistrationActivity.class);
                intent.putExtra(Constants.Key_RegistrationFromMap,false);
                startActivity(intent);
                break;
        }
    }

    // example : 192.168.1.2
    public long ipToLong(String ipAddress) {

        // ipAddressInArray[0] = 192
        String[] ipAddressInArray = ipAddress.split("\\.");

        long result = 0;
        for (int i = 0; i < ipAddressInArray.length; i++) {

            int power = 3 - i;
            int ip = Integer.parseInt(ipAddressInArray[i]);

            // 1. 192 * 256^3
            // 2. 168 * 256^2
            // 3. 1 * 256^1
            // 4. 2 * 256^0
            result += ip * Math.pow(256, power);

        }

        return result;

    }

    @Override
    protected void onResume() {
        super.onResume();
        AddressController.getInstance().setDestinationLocaton(null);
        AddressController.getInstance().setSourceLocaton(null);
        AddressController.getInstance().setSelectedSourceAddress(null);
        AddressController.getInstance().setSelectedDestinationAddress(null);
    }
}

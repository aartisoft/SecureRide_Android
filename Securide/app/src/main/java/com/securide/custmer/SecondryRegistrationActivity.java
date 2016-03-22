package com.securide.custmer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.securide.custmer.preferences.SecuridePreferences;

public class SecondryRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondry_registration);

        findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecuridePreferences.setRegistrationStatus(true);
                startActivity(new Intent(SecondryRegistrationActivity.this, CostDetailsActivity.class));
                finish();
            }
        });

    }
}

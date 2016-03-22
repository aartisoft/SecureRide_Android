package com.securide.custmer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.securide.custmer.preferences.SecuridePreferences;

public class PrimaryRegistrationActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText mName = null;
    private EditText mAddress = null;
    private EditText mPhoneNo = null;
    private EditText mEmail = null;
    private EditText mPassword = null;
    private EditText mConfirmPassword= null;
    private Button mNextBtn = null;
    private Context mContext = PrimaryRegistrationActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_registration);
        mName = (EditText) findViewById(R.id.name);
        mAddress = (EditText) findViewById(R.id.address);
        mPhoneNo = (EditText) findViewById(R.id.phno);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mConfirmPassword = (EditText) findViewById(R.id.confirm_password);
        mNextBtn = (Button) findViewById(R.id.next_btn);
        mNextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next_btn:
                if(!TextUtils.isEmpty(mName.getText()) && !TextUtils.isEmpty(mAddress.getText()) &&
                        !TextUtils.isEmpty(mPhoneNo.getText()) && !TextUtils.isEmpty(mEmail.getText())
                        && !TextUtils.isEmpty(mPassword.getText()) && !TextUtils.isEmpty(mConfirmPassword.getText())) {
                    SecuridePreferences.setRegistrationStatus(true);
                    startActivity(new Intent(mContext, MapsActivity.class));
                    finish();
                    Toast.makeText(mContext, "Registration success", Toast.LENGTH_LONG).show();
                } else {
                    if(TextUtils.isEmpty(mName.getText())) mName.setError(" Field is empty");
                    if(TextUtils.isEmpty(mAddress.getText())) mAddress.setError(" Field is empty");
                    if(TextUtils.isEmpty(mPhoneNo.getText())) mPhoneNo.setError(" Field is empty");
                    if(TextUtils.isEmpty(mEmail.getText())) mEmail.setError(" Field is empty");
                    if(TextUtils.isEmpty(mPassword.getText())) mPassword.setError(" Field is empty");
                    if(TextUtils.isEmpty(mConfirmPassword.getText())) mConfirmPassword.setError(" Field is empty");
                }
                break;
        }
    }
}

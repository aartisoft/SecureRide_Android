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

import com.securide.custmer.Util.Constants;
import com.securide.custmer.model.CustomerProfileObject;
import com.securide.custmer.preferences.SecuridePreferences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrimaryRegistrationActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText fname,middleName,lname;
    private EditText mAddress = null;
    private EditText mPhoneNo = null;
    private EditText mEmail = null;
    private EditText mPassword = null;
    private EditText mConfirmPassword= null;
    private Button mNextBtn = null;
    private Context mContext = PrimaryRegistrationActivity.this;
    Boolean isFromMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromMap  = getIntent().getExtras().getBoolean(Constants.Key_RegistrationFromMap, false);
        setContentView(R.layout.activity_primary_registration);
        fname = (EditText) findViewById(R.id.fname);
        middleName = (EditText) findViewById(R.id.middleName);
        lname = (EditText) findViewById(R.id.lname);
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
                if(!TextUtils.isEmpty(fname.getText())
                        && !TextUtils.isEmpty(lname.getText())
                        && !TextUtils.isEmpty(mAddress.getText())
                        && !TextUtils.isEmpty(mPhoneNo.getText())
                        && !TextUtils.isEmpty(mEmail.getText())
                        && !TextUtils.isEmpty(mPassword.getText())
                        && !TextUtils.isEmpty(mConfirmPassword.getText())) {
                    SecuridePreferences.setRegistrationStatus(true);
                    startActivity(new Intent(mContext, MapsActivity.class));
                    finish();
                    Toast.makeText(mContext, "Registration success", Toast.LENGTH_LONG).show();
                } else {
                    if(TextUtils.isEmpty(fname.getText())) fname.setError(" Field is empty");
                    if(TextUtils.isEmpty(lname.getText())) lname.setError(" Field is empty");
                    if(TextUtils.isEmpty(mAddress.getText())) mAddress.setError(" Field is empty");
                    if(TextUtils.isEmpty(mPhoneNo.getText())) mPhoneNo.setError(" Field is empty");
                    if(TextUtils.isEmpty(mEmail.getText())) mEmail.setError(" Field is empty");
                    if(TextUtils.isEmpty(mPassword.getText())) mPassword.setError(" Field is empty");
                    if(TextUtils.isEmpty(mConfirmPassword.getText())) mConfirmPassword.setError(" Field is empty");
                }
                Boolean isFormError = false;
                if (!TextUtils.isEmpty(fname.getText())){
                    fname.setError(null);
                }else{
                    fname.setError(" First Name is empty");
                    isFormError = true;
                }
                if (!TextUtils.isEmpty(lname.getText())){
                    lname.setError(null);
                }else{
                    lname.setError(" Last Name is empty");
                    isFormError = true;
                }
                if (!TextUtils.isEmpty(mAddress.getText())){
                    mAddress.setError(null);
                }else{
                    mAddress.setError(" Address is empty");
                    isFormError = true;
                }
                if (!TextUtils.isEmpty(mPhoneNo.getText()) && mPhoneNo.getText().toString().length() == 8){
                    mPhoneNo.setError(null);
                }else{
                    mPhoneNo.setError(" Invalid phone number");
                    isFormError = true;
                }
                if (!TextUtils.isEmpty(mEmail.getText()) && isEmailValid(mEmail.getText().toString())){
                    mEmail.setError(null);
                }else{
                    mEmail.setError(" Invalid email id");
                    isFormError = true;
                }
                if (!TextUtils.isEmpty(mPassword.getText())){
                    mPassword.setError(null);
                }else{
                    mPassword.setError(" Password is empty");
                    isFormError = true;
                }
                if (!TextUtils.isEmpty(mConfirmPassword.getText())){
                    if (mPassword.getText().toString().compareToIgnoreCase(mConfirmPassword.getText().toString())!= 0){
                        mConfirmPassword.setError(" Password doesn't match");
                        isFormError = true;
                    }else{
                        mConfirmPassword.setError(null);
                    }

                }else{
                    mConfirmPassword.setError(" Password is empty");
                    isFormError = true;
                }
                if (!isFormError){
                    CustomerProfileObject customerProfileObject = new CustomerProfileObject();
                    customerProfileObject.setFname(fname.getText().toString());
                    customerProfileObject.setMiddleName(middleName.getText().toString());
                    customerProfileObject.setLname(lname.getText().toString());
                    customerProfileObject.setAddress(mAddress.getText().toString());
                    customerProfileObject.setEmailid(mEmail.getText().toString());
                    customerProfileObject.setPassword(mPassword.getText().toString());

                    SecuridePreferences.setRegistrationStatus(true);
                    SecuridePreferences.setCustomerProfile(customerProfileObject);

                    if (isFromMap){
                        startActivity(new Intent(PrimaryRegistrationActivity.this, CostDetailsActivity.class));
                    }else{
                        startActivity(new Intent(mContext, MapsActivity.class));
                    }

                    finish();
                    Toast.makeText(mContext, "Registration success", Toast.LENGTH_LONG).show();

                }
                break;
        }

    }
    public boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}

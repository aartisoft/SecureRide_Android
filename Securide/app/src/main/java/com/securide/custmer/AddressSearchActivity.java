package com.securide.custmer;

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.securide.custmer.adapters.AddressListAdapter;
import com.securide.custmer.controllers.AddressController;
import com.securide.custmer.listeners.IAddressListener;
import com.securide.custmer.tasks.GetAddressTask;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AddressSearchActivity extends AppCompatActivity implements View.OnClickListener, IAddressListener{
    public static final int DELAY_TIME = 600;
    private ListView mSearchListView = null;
    private EditText mAddressEditText = null;
    private Button mOkButton = null;
    private AddressListAdapter mAddressListAdapter = null;
    private Context mContext = AddressSearchActivity.this;
    private GetAddressTask mGetAddressTask = null;
    private String mKeyword = null;
    private Timer mSearchTextTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_search);
        mSearchListView = (ListView) findViewById(R.id.search_list);
        mSearchListView.setOnItemClickListener(onItemClickListener);
        mAddressEditText = (EditText) findViewById(R.id.search_text);
        mOkButton = (Button) findViewById(R.id.ok_btn);
        mOkButton.setOnClickListener(this);
        mAddressEditText.addTextChangedListener(mAddressKeywordListener);
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(mAddressListAdapter != null) {
                mAddressListAdapter.setSelectedAddress(position);
                closeKeyBoard();
            }
        }
    };

    TextWatcher mAddressKeywordListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //do noop
        }

        @Override
        public void onTextChanged(final CharSequence s, int start, int before, int count) {
            mKeyword = s.toString();
            // user is typing: reset already started timer (if existing)
            if (mSearchTextTimer != null) {
                mSearchTextTimer.cancel();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // user typed: start the timer
            mSearchTextTimer = new Timer();
            mSearchTextTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    initSearchTask(mKeyword);
                }
            }, DELAY_TIME); // 600ms delay before the timer executes the „run“ method from TimerTask
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ok_btn:
                if(mAddressListAdapter != null ) {
                    AddressController.getInstance().setSelectedDestinationAddress(mAddressListAdapter.getSelectedItem());
                }
                finish();
                break;
        }
    }

    private void initSearchTask(String keyword){
        if(mGetAddressTask == null) {
            mGetAddressTask = new GetAddressTask(mContext, keyword, this);
            mGetAddressTask.execute();
        }
    }

    private void  setOrUpdateAdapter(List<Address> list){
        if(mAddressListAdapter == null && list != null) {
            mAddressListAdapter = new AddressListAdapter(mContext, 0, list);
            mSearchListView.setAdapter(mAddressListAdapter);
        } else {
            if (mAddressListAdapter != null ){
                mAddressListAdapter.updateListView(list);
            }
        }
    }


    @Override
    public void onAddressDispatch(List<Address> list) {
        if(mGetAddressTask != null && mGetAddressTask.getStatus() == AsyncTask.Status.RUNNING) {
            mGetAddressTask.cancel(true);
            mGetAddressTask = null;
        }

        setOrUpdateAdapter(list);
    }


    private void closeKeyBoard(){
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

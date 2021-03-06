package com.securide.custmer.adapters;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.securide.custmer.R;
import com.securide.custmer.listeners.IAddressListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pradeep.kumar on 3/13/16.
 */
public class AddressListAdapter extends ArrayAdapter<String> {

    private List<String> mAddressList = new ArrayList<>(0);
    private LayoutInflater mInflater = null;
    private String mSelectedAddress = null;
    public int mSelectedAddressIndex = 0;
    private Context mContext = null;

    public AddressListAdapter(Context context, int resource, List<String> ls) {
        super(context, resource, ls);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mAddressList.clear();
        mAddressList.addAll(ls);
    }

    @Override
    public int getCount() {
        return mAddressList.size();
    }

    public void updateListView(List<String> addressList) {
        mAddressList.clear();
        mAddressList.addAll(addressList);
        notifyDataSetChanged();
    }
    public void clearList(){
        mAddressList.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.adapter_address, null);
        TextView title = (TextView) convertView.findViewById(R.id.adapter_title);
        TextView details = (TextView) convertView.findViewById(R.id.adapter_details);

        String address = mAddressList.get(position);
        title.setText(address);
//        if (address.getMaxAddressLineIndex() > 0) {
//            title.setText(address.getAddressLine(0));
//            details.setText(address.getAddressLine(1)+", "+address.getAddressLine(2));
//        }

       /* if (mSelectedAddressIndex == position) {
            convertView.setBackgroundResource(R.drawable.address_selected_box);
            title.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            details.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));

        }*/

        return convertView;
    }

    public void setSelectedAddress(int i) {
        mSelectedAddressIndex = i;
        mSelectedAddress = mAddressList.get(i);
        notifyDataSetChanged();
    }

    public String getSelectedItem() {
        return mSelectedAddress;
    }
}

package com.securide.custmer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.securide.custmer.CabModel;
import com.securide.custmer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pradeep.kumar on 3/11/16.
 */
public class CabsAdapter extends ArrayAdapter {
    List<CabModel> mLCabsList = new ArrayList<>(0);
    LayoutInflater mInflater = null;

    public CabsAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
        mInflater = LayoutInflater.from(context);
        mLCabsList.clear();
        prepareCabsList();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.adapter_cabmodel, null);
        TextView name = (TextView) convertView.findViewById(R.id.car_type);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.car_image);
        name.setText(mLCabsList.get(position).name);
        imageView.setImageResource(mLCabsList.get(position).image);
        return convertView;
    }

    private void prepareCabsList() {
        mLCabsList.add(new CabModel("Limousine", R.drawable.limousine));
        mLCabsList.add(new CabModel("Regular", R.drawable.regular));
        mLCabsList.add(new CabModel("Special", R.drawable.special));

    }
}

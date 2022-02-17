package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanclasses.SuppStationBean;
import com.stavigilmonitoring.R;

import java.util.ArrayList;

public class SuppStationAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    Context context;
    ArrayList<SuppStationBean> lststns = new ArrayList<SuppStationBean>();

    public SuppStationAdapter(Context parent, ArrayList<SuppStationBean> results) {
        this.context = parent;
        mInflater = LayoutInflater.from(parent);
        lststns = results;
    }

    @Override
    public int getCount() {
        return lststns.size();
    }

    @Override
    public Object getItem(int position) {
        return lststns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.supp_station_adapter,null);
            holder = new ViewHolder();

            holder.txtsupporter = (TextView)convertView.findViewById(R.id.txtsupporter);
            holder.txtstation = (TextView)convertView.findViewById(R.id.txtstation);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtsupporter.setText(lststns.get(position).getSubnetworkcode());
        holder.txtstation.setText(lststns.get(position).getInstallationdesc());

        return convertView;
    }

    static class ViewHolder {
        TextView txtsupporter,txtstation;
    }
}

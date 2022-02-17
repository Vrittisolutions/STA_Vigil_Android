package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.AdvFirstPlayClipRprt;
import com.stavigilmonitoring.R;

import java.util.ArrayList;

public class AdvStationListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    Context context;
    ArrayList<AdvFirstPlayClipRprt> lststns = new ArrayList<AdvFirstPlayClipRprt>();

    public AdvStationListAdapter(Context parent, ArrayList<AdvFirstPlayClipRprt> results) {
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
            convertView = mInflater.inflate(R.layout.adv_stations_list,null);
            holder = new ViewHolder();

            holder.txtstnname = (TextView)convertView.findViewById(R.id.txtstnname);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtstnname.setText(lststns.get(position).getStationName());

        return convertView;
    }

    static class ViewHolder {
        TextView txtstnname;
    }
}

package com.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanclasses.AdvVideoDataBean;
import com.stavigilmonitoring.R;

import java.util.ArrayList;
import java.util.Locale;

public class AdvStationsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    Context context;
    ArrayList<AdvVideoDataBean> lststns = new ArrayList<AdvVideoDataBean>();
    private ArrayList<AdvVideoDataBean> arraylist;
    String[] parts;

    public AdvStationsAdapter(Context parent, ArrayList<AdvVideoDataBean> results) {
        this.context = parent;
        mInflater = LayoutInflater.from(parent);
        lststns = results;
        arraylist = new ArrayList<AdvVideoDataBean>();
        arraylist.addAll(results);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.advdtl_stnsadapter,null);
            holder = new ViewHolder();

            holder.txtstationname = (TextView)convertView.findViewById(R.id.txtstationname);
            holder.txteffromdate = (TextView)convertView.findViewById(R.id.txteffromdate);
            holder.txtefftodate = (TextView)convertView.findViewById(R.id.txtefftodate);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtstationname.setText(lststns.get(position).getInstalationName());
        holder.txteffromdate.setText(lststns.get(position).getEffectiveDatefrom());
        holder.txtefftodate.setText(lststns.get(position).getEffectiveDateTo());

        return convertView;
    }

    static class ViewHolder {
        TextView txtstationname,txteffromdate,txtefftodate;
    }

    /*public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        lststns.clear();
        if (charText.length() == 0) {
            lststns.addAll(arraylist);
        } else {
            for (AdvVideoDataBean wp : arraylist) {
                if (wp.getNetworkCode().toLowerCase(Locale.getDefault()).contains(charText) ||
                        wp.getAdvertisementCode().toLowerCase(Locale.getDefault()).contains(charText) ||
                        wp.getAdvertisementDesc().toLowerCase(Locale.getDefault()).contains(charText) ||
                        wp.getNetworkCode().toLowerCase(Locale.getDefault()).contains(charText)) {
                    lststns.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }*/
}

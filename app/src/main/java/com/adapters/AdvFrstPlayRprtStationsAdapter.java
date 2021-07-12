package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.AdvFirstPlayClipRprt;
import com.beanclasses.NonrepeatedAdHelper;
import com.stavigilmonitoring.R;

import java.util.ArrayList;

public class AdvFrstPlayRprtStationsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    Context context;
    ArrayList<AdvFirstPlayClipRprt> lststns = new ArrayList<AdvFirstPlayClipRprt>();

    public AdvFrstPlayRprtStationsAdapter(Context parent, ArrayList<AdvFirstPlayClipRprt> results) {
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
            convertView = mInflater.inflate(R.layout.adv_first_play_report_station,null);
            holder = new ViewHolder();

            holder.txtfilename = (TextView)convertView.findViewById(R.id.txtfilename);
            holder.txtstnname = (TextView)convertView.findViewById(R.id.txtstnname);
            holder.txtisdwnld = (TextView)convertView.findViewById(R.id.txtisdwnld);
            holder.txtschdltime = (TextView)convertView.findViewById(R.id.txtschdltime);
            holder.txtremark = (TextView)convertView.findViewById(R.id.txtremark);
            holder.txtplaytime = (TextView)convertView.findViewById(R.id.txtplaytime);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtfilename.setText(lststns.get(position).getFileName());
        holder.txtstnname.setText(lststns.get(position).getStationName());

        String isDownload = String.valueOf(lststns.get(position).isDownloaded());

        if(isDownload.equalsIgnoreCase("true")){
            isDownload = "Yes";
        }else {
            isDownload = "No";
        }

        holder.txtisdwnld.setText(isDownload);
        holder.txtschdltime.setText(lststns.get(position).getSchedule_time_date());
        holder.txtremark.setText(lststns.get(position).getRemark());
        holder.txtplaytime.setText(lststns.get(position).getFirstPlayTime());

        return convertView;
    }

    static class ViewHolder {
        TextView txtfilename, txtstnname, txtisdwnld, txtschdltime, txtremark, txtplaytime;
    }
}

package com.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.AdvFirstPlayClipRprt;
import com.stavigilmonitoring.R;

import java.util.ArrayList;

public class AdvTimingDtlsRprtAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    Context context;
    ArrayList<AdvFirstPlayClipRprt> lststns ;

    public AdvTimingDtlsRprtAdapter(Context parent, ArrayList<AdvFirstPlayClipRprt> results) {
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
    public int getViewTypeCount() {

        int count;
        if (lststns.size() > 0) {
            count = getCount();
        } else {
            //Toast.makeText(parent,"No items in Ordered Items list",Toast.LENGTH_SHORT).show();
            count = 1;
        }
        return count;

        //  return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int pos = position;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.adv_timing_dtls_report,null);
            holder = new ViewHolder();

            holder.txtschdtime = (TextView)convertView.findViewById(R.id.txtschdtime);
            holder.txtacttime = (TextView)convertView.findViewById(R.id.txtacttime);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();

        }

        AdvFirstPlayClipRprt report = lststns.get(pos);

        holder.txtacttime.setTag(lststns.get(pos));

        String ScheduleTime, ActualPlayTime;
        ScheduleTime = lststns.get(position).getSchedule_time_date();
        ActualPlayTime = lststns.get(position).getActual_time_date();

        holder.txtschdtime.setText(lststns.get(position).getSchedule_time_date());
        holder.txtacttime.setText(lststns.get(position).getActual_time_date());

        if(!ActualPlayTime.equalsIgnoreCase(ScheduleTime)){
            holder.txtacttime.setTextColor(Color.parseColor("#FE2E2E"));
        }

        return convertView;
    }

    static class ViewHolder {
        TextView txtschdtime, txtacttime;
    }
}

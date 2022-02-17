package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.StationPerformance;
import com.stavigilmonitoring.R;

import java.util.ArrayList;

public class StnPerfSeven_DayValue_Adapter extends BaseAdapter {

    private LayoutInflater mInflater;
    Context context;
    ArrayList<StationPerformance> lststns = new ArrayList<StationPerformance>();

    public StnPerfSeven_DayValue_Adapter(Context parent, ArrayList<StationPerformance> results) {
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
            convertView = mInflater.inflate(R.layout.stperf_7_stnlist,null);
            holder = new ViewHolder();

            holder.txtstnname = (TextView)convertView.findViewById(R.id.txtstnname);
            holder.txtval1 = (TextView)convertView.findViewById(R.id.txtval1);
            holder.txtval2 = (TextView)convertView.findViewById(R.id.txtval2);
            holder.txtval3 = (TextView)convertView.findViewById(R.id.txtval3);
            holder.txtval4 = (TextView)convertView.findViewById(R.id.txtval4);
            holder.txtval5 = (TextView)convertView.findViewById(R.id.txtval5);
            holder.txtval6 = (TextView)convertView.findViewById(R.id.txtval6);
            holder.txtval7 = (TextView)convertView.findViewById(R.id.txtval7);
            holder.txtavg = (TextView)convertView.findViewById(R.id.txtavg);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    static class ViewHolder{
        TextView txtstnname, txtval1, txtval2, txtval3, txtval4, txtval5, txtval6, txtval7, txtavg;
    }
}

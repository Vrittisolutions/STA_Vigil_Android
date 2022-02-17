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

public class StationPerforDetails_Adapter extends BaseAdapter {

    private LayoutInflater mInflater;
    Context context;
    ArrayList<StationPerformance> lststns = new ArrayList<StationPerformance>();

    public StationPerforDetails_Adapter(Context parent, ArrayList<StationPerformance> results) {
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
            convertView = mInflater.inflate(R.layout.stnperformance,null);
            holder = new ViewHolder();

            holder.txtstnname = (TextView)convertView.findViewById(R.id.txtstnname);
            holder.txtadvdesc = (TextView)convertView.findViewById(R.id.txtadvdesc);
            holder.txtexp_repititions = (TextView)convertView.findViewById(R.id.txtexp_repititions);
            holder.txtact_repititions = (TextView)convertView.findViewById(R.id.txtact_repititions);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    static class ViewHolder{
        TextView txtstnname, txtadvdesc,txtexp_repititions, txtact_repititions;
    }
}

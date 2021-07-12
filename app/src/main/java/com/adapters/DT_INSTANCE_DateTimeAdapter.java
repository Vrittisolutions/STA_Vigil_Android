package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.StateList;

import java.util.ArrayList;

public class DT_INSTANCE_DateTimeAdapter extends BaseAdapter {
    private ArrayList<StateList> list;
    private Context parent;
    private LayoutInflater mInflater;
    private ArrayList<StateList> arraylist;

    public DT_INSTANCE_DateTimeAdapter(Context parent,
                                       ArrayList<StateList> soundlevelBeanslist) {
        this.parent = parent;
        this.list = soundlevelBeanslist;
        arraylist = new ArrayList<StateList>();
        arraylist.addAll(soundlevelBeanslist);
        //arraylist=new ArrayList<com.vstaproject.TvStatusMain.StateList>();
        //arraylist.addAll(searchResults);
        mInflater = LayoutInflater.from(parent);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.activity_dt_instance_daytime, null);
            holder = new ViewHolder();

            //holder.txtntwrkname = (TextView)convertView.findViewById(R.id.txtntwrkname);
           // holder.txtstationname = (TextView) convertView.findViewById(R.id.txtstationname);
            holder.txtaddeddate = (TextView)convertView.findViewById(com.stavigilmonitoring.R.id.txtaddeddate);
            holder.txtdowntime = (TextView)convertView.findViewById(com.stavigilmonitoring.R.id.txtdowntime);
            holder.txtstarttime = (TextView)convertView.findViewById(com.stavigilmonitoring.R.id.txtstarttime);
            holder.txtendtime = (TextView)convertView.findViewById(com.stavigilmonitoring.R.id.txtendtime);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String networkname = list.get(position).getNetworkcode();
        String installationName = list.get(position).getStatioName();
        //String date = list.get(position).GetAddedDate();

        String date = String.valueOf(list.get(position).getDdate());
        String DATA[] = date.split(" ");

        String DAY = DATA[2];
        String MONTH = DATA[1];
        String YEAR = DATA[5];
        String dispdate = DAY +" " + MONTH + " " + YEAR;

        String DownTime = list.get(position).getDownTime();
        String StartTime = list.get(position).getStartTime();
        String EndTime = list.get(position).getEndTime();
        String LS_EC_Key = list.get(position).getLS_EC_Key();

       // holder.txtntwrkname.setText(networkname);
        //holder.txtstationname.setText(installationName);
        holder.txtaddeddate.setText(/*date*/dispdate);
        holder.txtdowntime.setText(DownTime);
        holder.txtstarttime.setText(StartTime);
        holder.txtendtime.setText(EndTime);
        //holder.txtectime.setText(time +" "+ AmPm);

        return convertView;
    }

    static class ViewHolder {
        TextView txtstationname, txtdate, txtectime, txtectitle,
                txtntwrkname, txtaddeddate, txtdowntime, txtstarttime,txtendtime;
    }

    /*public void filter(String charText) {

//	charText = charText.toLowerCase(Locale.getDefault());
        list.clear();
        if(charText.length()==0)
        {
            list.addAll(arraylist);
        }
        else
        {
            for (StateList wp : arraylist)
            {
                if (wp.getStatioName().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    list.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }*/
}

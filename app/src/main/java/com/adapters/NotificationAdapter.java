package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.NotificationBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Admin-3 on 7/28/2017.
 */

public class NotificationAdapter extends BaseAdapter {
    private ArrayList<NotificationBean> list;
    ViewHolder holder;
    private Context parent;
    NotificationBean notificationBean;
    private LayoutInflater mInflater;

    public NotificationAdapter(Context parent, ArrayList<NotificationBean> list) {
        this.list = list;

        this.parent = parent;
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
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(com.stavigilmonitoring.R.layout.custom_notifications, viewGroup, false);

            holder.message = (TextView) view.findViewById(com.stavigilmonitoring.R.id.txt_message);
            holder.Stn = (TextView) view.findViewById(com.stavigilmonitoring.R.id.txt_StnName);
            holder.date = (TextView) view.findViewById(com.stavigilmonitoring.R.id.txt_notification_date);

            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();

        }

        holder.Stn.setText(list.get(position).getStationName() );
        holder.message.setText(list.get(position).getMessage() );
          holder.date.setText(getDateinFormat(list.get(position).getAddedDt()));


        return view;
    }

    private String getDateinFormat(String amcExpireDt) {
        String result= null;
        //SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm aa");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM, yyyy hh:mm aa");
        try {
            Date date2 = dateFormat3.parse(amcExpireDt);
           // result = dateFormat3.format(date2);
           // date2 = dateFormat3.parse(result);
            result = dateFormat2.format(date2);
        }catch( Exception e){
            e.printStackTrace();
        }
        return result;
    }

    private static class ViewHolder {
        TextView message, date, Stn;


    }
}


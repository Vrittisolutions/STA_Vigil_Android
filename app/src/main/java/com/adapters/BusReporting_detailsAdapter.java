package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.StationEnqBusBean;
import com.stavigilmonitoring.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BusReporting_detailsAdapter extends BaseAdapter {
    private Context parent;
    ArrayList<StationEnqBusBean> busRepDtlsList;
    LayoutInflater inflater;

    public BusReporting_detailsAdapter(Context context, ArrayList<StationEnqBusBean> detailsList) {
        parent = context;
        busRepDtlsList = detailsList;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return busRepDtlsList.size();
    }

    @Override
    public Object getItem(int position) {
        return busRepDtlsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.busrep_details_adapter, null);
            holder = new ViewHolder();

            holder.txtfrmstn = (TextView) convertView.findViewById(R.id.txtfromstn);
            holder.txttostn = (TextView) convertView.findViewById(R.id.txttostn);
            holder.txttime = (TextView) convertView.findViewById(R.id.txt_time);
            holder.txtBusNo = (TextView) convertView.findViewById(R.id.txtbusno);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String time = busRepDtlsList.get(position).getReportingtime();
        String[] data = time.split("T");
        String[] TIME = data[1].split("/+");

        //   time = split_time(time);

        holder.txtfrmstn.setText(busRepDtlsList.get(position).getSource());
        holder.txttostn.setText(busRepDtlsList.get(position).getDestination());
        holder.txttime.setText(TIME[0].substring(0, TIME[0].indexOf("+"))/*time*/);
        holder.txtBusNo.setText(busRepDtlsList.get(position).getBusno());
        return convertView;
    }

    public class ViewHolder {
        TextView txttime, txtfrmstn, txttostn, txtBusNo;

    }

    private String split_time(String data) {
        // TODO Auto-generated method stub
        Date conn1 = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss a");// 2016-05-12T20:36:08+05:30//09/05/2016

            conn1 = dateFormat.parse(data);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
        String time = dateFormat1.format(conn1);

        return time;

    }
}

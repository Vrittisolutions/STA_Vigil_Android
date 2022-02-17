package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.StateList;

import java.util.ArrayList;
import java.util.Locale;

public class DT_StationList_Adapter extends BaseAdapter {
    private ArrayList<StateList> list;
    private Context parent;
    private LayoutInflater mInflater;
    private ArrayList<StateList> arraylist;

    public DT_StationList_Adapter(Context parent,
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
            convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.activity_dt__stations_list, null);
            holder = new ViewHolder();

            holder.textstationname = (TextView) convertView
                    .findViewById(com.stavigilmonitoring.R.id.tvstnnamedownmain);
            holder.tvstncount = (TextView)convertView.findViewById(com.stavigilmonitoring.R.id.tvstncount);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try{
            holder.textstationname.setText(list.get(position).getStatioName());

        String LSECKey = list.get(position).getLS_EC_Key();

        if(LSECKey.contains("LS") || LSECKey.contains("EC")){

            String count = String.valueOf(list.get(position).Getcount());
            holder.tvstncount.setText(count);

        }else if(LSECKey.contains("DT_INSTANCE")){

            String TotDisconCnt = String.valueOf(list.get(position).getTotDisconCnt());
            String TotDiscn_30minCntinst = String.valueOf(list.get(position).getTotDiscn_30minCntinst());
            holder.tvstncount.setText(TotDiscn_30minCntinst +"/"+ TotDisconCnt);
        }

        }catch (Exception e){
            e.printStackTrace();
        }

        return convertView;
    }

    static class ViewHolder {
        TextView textstationname, tvstncount;
    }

    public void filter(String charText) {

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
    }
}

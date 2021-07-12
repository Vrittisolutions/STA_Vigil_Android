package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.SupportersNames;

import java.util.List;

public class SupportersListAdapter extends BaseAdapter {
    private static List<SupportersNames> searchArrayList;
    private LayoutInflater mInflater;
    Context context;

    public SupportersListAdapter(Context parent, List<SupportersNames> searchResults) {
        context = parent;
        mInflater = LayoutInflater.from(context);
        searchArrayList = searchResults;
    }

    @Override
    public int getCount() {
        return searchArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.supp_list_adapter, null);
            holder = new ViewHolder();

            holder.txtsupname = (TextView) convertView
                    .findViewById(com.stavigilmonitoring.R.id.txtsupName);
            holder.txtsupcnt = (TextView) convertView
                    .findViewById(com.stavigilmonitoring.R.id.txtsupCntc);
            holder.txtsupcnt.setVisibility(View.GONE);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        String supprtrname = searchArrayList.get(position).getSupp_Name();
        holder.txtsupname.setText(supprtrname);

       /* if (s.equals("")) {
            holder.tvNetworkCode.setText("No Station Found..");
            holder.tvCount.setText("");

        } else {

            holder.tvNetworkCode.setText(searchArrayList.get(position).getNetworkcode());
            holder.tvCount.setText(searchArrayList.get(position).Getcount()+"");

        }*/
        return convertView;
    }

    static class ViewHolder {

        TextView txtsupname;
        TextView txtsupcnt;

    }
}

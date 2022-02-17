package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.adapters.StationEnquiryStnAdap;
import com.beanclasses.StateList;

import java.util.ArrayList;
import java.util.Locale;

public class SupEnqStationsListAdapter extends BaseAdapter {
    private ArrayList<StateList> list;
    private Context parent;
    private LayoutInflater mInflater;
    private ArrayList<StateList> arraylist;

    public SupEnqStationsListAdapter(Context context, ArrayList<StateList> searchResults) {
        this.parent = context;
        this.list = searchResults;
        arraylist = new ArrayList<StateList>();
        arraylist.addAll(searchResults);
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.materialitem, null);
            holder = new ViewHolder();

            holder.textstationname = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvstnnamedownmain);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textstationname.setText(list.get(position).getStatioName());

        return convertView;
    }

    static class ViewHolder {
        TextView textstationname;

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
                if (wp.getStatioName().toLowerCase(Locale.getDefault()).startsWith(charText))
                {
                    list.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}

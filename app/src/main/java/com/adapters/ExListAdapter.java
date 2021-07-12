package com.adapters;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.beanclasses.ExListChildBean;
import com.beanclasses.ExListHeaderBean;

public class ExListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<ExListHeaderBean> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<ExListHeaderBean,List<ExListChildBean>> _listDataChild;

    public ExListAdapter(Context context, List<ExListHeaderBean> listDataHeader,
                         HashMap<ExListHeaderBean,List<ExListChildBean>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        //final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(com.stavigilmonitoring.R.layout.exlist_item, null);
        }

        TextView item_desc = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.item_desc);
        TextView item_date = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.item_date);
        TextView item_stn = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.item_stn);

        List<ExListChildBean> rlist = this._listDataChild.get(this._listDataHeader.get(groupPosition));

        item_desc.setText(rlist.get(childPosition).Getdesc());
        item_stn.setText(rlist.get(childPosition).GetstnName());
        item_date.setText(rlist.get(childPosition).Getdate());
        return convertView;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public int getChildrenCount(int groupPosition) {
        final int mysize = this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        if (mysize==0){
            ((Activity) _context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(_context,"No List",Toast.LENGTH_SHORT).show();
                }
            });
        }
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        //String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(com.stavigilmonitoring.R.layout.exlist_group, null);
        }

        TextView tvsNamec = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvsNamec);
        TextView tvsCntc = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvsCntc);

        tvsNamec.setTypeface(null, Typeface.BOLD);
        tvsNamec.setText(this._listDataHeader.get(groupPosition).GetModuleName());
        tvsCntc.setText(String.valueOf(this._listDataHeader.get(groupPosition).GetMCount()));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

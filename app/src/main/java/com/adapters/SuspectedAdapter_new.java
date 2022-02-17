package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.beanclasses.SuspectedHelper;
import com.stavigilmonitoring.R;

import java.util.ArrayList;

public class SuspectedAdapter_new extends BaseAdapter {

	private static ArrayList<SuspectedHelper> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public SuspectedAdapter_new(Context context1, ArrayList<SuspectedHelper> results) {
		searchArrayList = results;
		mInflater = LayoutInflater.from(context1);
		context = context1;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return searchArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return searchArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.suspected_stnlist_one, null);

			holder = new ViewHolder();
			
			holder.stname = (TextView) convertView.findViewById(R.id.stname);
			holder.spotperctg = (TextView) convertView.findViewById(R.id.spotperctg);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

			holder.stname.setText(searchArrayList.get(position).getStationName());
			holder.spotperctg.setText(searchArrayList.get(position).getSpotWisePercentage()+"%");

		return convertView;
	}

	static class ViewHolder {
		TextView stname, spotperctg;
	}

}

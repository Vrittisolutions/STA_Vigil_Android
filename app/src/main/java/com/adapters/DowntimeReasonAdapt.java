package com.adapters;

import java.util.ArrayList;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.DowntimeReasonHelper1;

public class DowntimeReasonAdapt extends BaseAdapter {

	private static ArrayList<DowntimeReasonHelper1> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public DowntimeReasonAdapt(Context context1, ArrayList<DowntimeReasonHelper1> results) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.downtimereasonupdated, null);
			holder = new ViewHolder();
			

			holder.starttime = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvdowntimereasonfrom);
			holder.endtime = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvdowntimereasonto);

			holder.datedown = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvdowntimereasondate);

			
			
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getStartTime();

		if (s.equals("")) {
			holder.starttime.setText("No Information available..");

		} else {

			
			holder.starttime.setText(searchArrayList.get(position)
					.getStartTime());
			holder.endtime.setText(searchArrayList.get(position)
					.getEndTime());
			holder.datedown.setText(searchArrayList.get(position)
					.getdateDown());
			
			
			
		}

		return convertView;
	}

	static class ViewHolder {
		
		TextView starttime;
		TextView endtime;
	
		TextView datedown;

	}

}

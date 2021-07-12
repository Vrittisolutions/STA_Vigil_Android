package com.adapters;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.MyBeanconn;
import com.stavigilmonitoring.StationEnquiry;

public class StationEnqLastThree extends BaseAdapter {

	private static ArrayList<MyBeanconn> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public StationEnqLastThree(Context context1, ArrayList<MyBeanconn> results) {
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
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.stationenqlastthreeitem, null);
			holder = new ViewHolder();
// aBustime, aActann, afrombus, aTobus, aBusaddate, bLastadv, bAdvname, bAdvcode, bAudiolevel;

			holder.aBustime = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.bustime);
			holder.aActann = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.anntime);
			holder.afrombus = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.fromstn);
			holder.aTobus = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tostn);
			holder.aBusaddate = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.addeddate);
			holder.bLastadv = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.advdate);
			holder.bAdvname = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.advname);
			holder.bAdvcode = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.advcode);
			holder.bAudiolevel = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.audioop);
			

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.aBustime.setText(searchArrayList.get(position)
				.getaBustime());
		holder.aActann.setText(searchArrayList.get(position)
				.getaActann());
		holder.afrombus.setText(searchArrayList.get(position)
				.getAfrombus());
		holder.aTobus.setText(searchArrayList.get(position)
				.getaTobus());
		holder.aBusaddate.setText(searchArrayList.get(position)
				.getaBusaddate());
		holder.bLastadv.setText(searchArrayList.get(position)
				.getbLastadv());
		holder.bAdvname.setText(searchArrayList.get(position)
				.getbAdvname());
		holder.bAdvcode.setText(searchArrayList.get(position)
				.getbAdvcode());
		holder.bAudiolevel.setText(searchArrayList.get(position)
				.getbAudiolevel());
		
		
	

	/*	String s = searchArrayList.get(position).getScheduleDate();

		if (!(searchArrayList.size() > 0)) {
			holder.ScheduleDate.setText("No information available..");
			holder.ScheduleTime.setText("");
			holder.Standard.setText("");
			holder.Actual.setText("");
		} else {

			holder.ScheduleDate.setText(searchArrayList.get(position)
					.getScheduleDate());

			holder.ScheduleTime.setText(searchArrayList.get(position)
					.getScheduleTime());

			// holder.AO.setText(searchArrayList.get(position).getaudioOutput());
			holder.Standard
					.setText(searchArrayList.get(position).getStandard());
			holder.Actual.setText(searchArrayList.get(position).getActual());

			
		}*/
		return convertView;
	}

	static class ViewHolder {

		TextView aBustime, aActann, afrombus, aTobus, aBusaddate, bLastadv, bAdvname, bAdvcode, bAudiolevel;
	}
}

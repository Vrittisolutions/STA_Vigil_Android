package com.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.UserList;

public class SupporterMaterialDispalyAdapter extends BaseAdapter {

	private static ArrayList<UserList> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	private ArrayList<UserList> arraylist;

	public SupporterMaterialDispalyAdapter(Context c,
			ArrayList<UserList> searchResults) {
		context = c;
		mInflater = LayoutInflater.from(context);
		searchArrayList = searchResults;
		arraylist = new ArrayList<UserList>();
		arraylist.addAll(searchResults);

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
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.supportermaterialitem, null);
			holder = new ViewHolder();
			holder.Station = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.mat);
			holder.material = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.matedisplay);// reporteedisplay
			holder.matremark = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.matremDisplay);
			holder.date = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.dateDisplay);
			holder.mobile = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.mobDisplay);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.Station.setText(searchArrayList.get(position).getStationName());
		//holder.worktype.setText(searchArrayList.get(position).getWorkType()
		//		+ "");
		//holder.workrem.setText(searchArrayList.get(position).getRemarks());
		 holder.material.setText(searchArrayList.get(position).getWorkType());
		 holder.matremark.setText(searchArrayList.get(position).getRemarks());
		holder.date.setText(searchArrayList.get(position).getCurrentDate());
		holder.mobile.setText(searchArrayList.get(position).getMobile());
		

		return convertView;
	}

	static class ViewHolder {
		TextView Station, worktype, workrem, material, matremark, date, mobile,
				location;
		TextView tvsStcnt;
	}

}

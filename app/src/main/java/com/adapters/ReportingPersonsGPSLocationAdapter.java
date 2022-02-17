package com.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.GPSLocationTimeBean;

public class ReportingPersonsGPSLocationAdapter extends BaseAdapter {
	private static ArrayList<GPSLocationTimeBean> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public ReportingPersonsGPSLocationAdapter(Context context1,
			ArrayList<GPSLocationTimeBean> results) {
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
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return searchArrayList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.custom_gpslocation, null);
			holder = new ViewHolder();
			holder.txtlocationame = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.locationgps);

			holder.txtdatetime = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.datetime);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getLocationName();

		if (s.equals("")) {
			holder.txtlocationame.setText("No User Found..");
		} else {
			holder.txtlocationame.setText(searchArrayList.get(position)
					.getLocationName());
			holder.txtdatetime.setText(searchArrayList.get(position)
					.getAddedDt());
		}

		return convertView;
	}

}

class ViewHolder {

	TextView txtlocationame;
	TextView txtdatetime;

}

package com.adapters;

import java.util.ArrayList;


import com.beanclasses.DowntimeHelper;
import com.stavigilmonitoring.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DowntimeStateAdpt extends BaseAdapter {

	private static ArrayList<DowntimeHelper> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public DowntimeStateAdpt(Context context1, ArrayList<DowntimeHelper> results) {
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
			convertView = mInflater.inflate(R.layout.downtimeupdatedrelative, null);
			holder = new ViewHolder();
			

			holder.current = (TextView) convertView
					.findViewById(R.id.tvdowntimelast1);
			holder.dateday = (TextView) convertView
					.findViewById(R.id.tvdowntimedateday1);

			
			holder.lastSeven = (TextView) convertView
					.findViewById(R.id.tvdowntimeseven1);
			
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getstnname();

		if (s.equals("")) {
			holder.current.setText("No Information available..");

		} else {

			
			holder.current.setText(searchArrayList.get(position)
					.getcurrent());
			holder.dateday.setText(searchArrayList.get(position)
					.getdateDay());

			holder.lastSeven.setText(searchArrayList.get(position)
					.getlastseven());
			
			
		}

		return convertView;
	}

	static class ViewHolder {
		
		TextView current;
		TextView dateday;
		TextView lastSeven;
	
		

	}

}

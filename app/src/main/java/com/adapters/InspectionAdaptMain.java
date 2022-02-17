package com.adapters;

import java.util.ArrayList;


import com.beanclasses.InspectionHelper;
import com.stavigilmonitoring.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class InspectionAdaptMain extends BaseAdapter {

	private static ArrayList<InspectionHelper> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public InspectionAdaptMain(Context context1, ArrayList<InspectionHelper> results) {
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
			convertView = mInflater.inflate(R.layout.inspectionupdatedmain, null);
			holder = new ViewHolder();
			

			holder.inspectionStation = (TextView) convertView
					.findViewById(R.id.tvinspectionstation);
		
		
//			holder.starttime = (TextView) convertView
//					.findViewById(R.id.tvstarttimemain);
//		
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getinsStation();

		if (s.equals("")) {
			holder.inspectionStation.setText("No Station Found..");

		} else {

			
			holder.inspectionStation.setText(searchArrayList.get(position)
					.getinsStation());
			
		
			
			//holder.servertime.setText(searchArrayList.get(position)
			//		.getservertime());

		//	holder.starttime.setText(searchArrayList.get(position)
				//	.getStartTime());
			
		}

		return convertView;
	}

	static class ViewHolder {
		
		TextView inspectionStation;
	
	
	}

}

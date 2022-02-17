package com.adapters;

import java.util.ArrayList;


import com.stavigilmonitoring.R;
import com.beanclasses.SuspectedHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SuspectedAdaptMain1 extends BaseAdapter {

	private static ArrayList<SuspectedHelper> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public SuspectedAdaptMain1(Context context1, ArrayList<SuspectedHelper> results) {
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
			convertView = mInflater.inflate(R.layout.suspectedupdatedmain, null);
			holder = new ViewHolder();
			

			holder.stationname = (TextView) convertView
					.findViewById(R.id.tvsusatationmain);
		
//			holder.suscount = (TextView) convertView
//					.findViewById(R.id.tvsuspectedcount);
//			holder.starttime = (TextView) convertView
//					.findViewById(R.id.tvstarttimemain);
//		
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getStationName();

		if (s.equals("")) {
			holder.stationname.setText("No Station Found....");

		} else {

			
			holder.stationname.setText(searchArrayList.get(position)
					.getStationName());
			
//			holder.suscount.setText(searchArrayList.get(position)
//					.getsuscount());
			//holder.servertime.setText(searchArrayList.get(position)
			//		.getservertime());

		//	holder.starttime.setText(searchArrayList.get(position)
				//	.getStartTime());
			
		}

		return convertView;
	}

	static class ViewHolder {
		
		TextView stationname;
		TextView suscount;
 
	}

}

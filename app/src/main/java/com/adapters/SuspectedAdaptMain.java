package com.adapters;

import java.util.ArrayList;



import com.database.DBInterface;
import com.stavigilmonitoring.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SuspectedAdaptMain extends BaseAdapter {

	private static ArrayList<String> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public SuspectedAdaptMain(Context context1, ArrayList<String> results) {
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
		
		//	holder.suscount = (TextView) convertView
				//	.findViewById(R.id.tvsuspectedcount);
//			holder.starttime = (TextView) convertView
//					.findViewById(R.id.tvstarttimemain);
//		
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).toString();

		if (s.equals("")) {
			holder.stationname.setText("No Station Found....");

		} else {

			
			holder.stationname.setText(searchArrayList.get(position).toString());
			holder.stationname.setTag(searchArrayList.get(position).toString());
			
			//holder.suscount.setText(searchArrayList.get(position)
			//		.toString());
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

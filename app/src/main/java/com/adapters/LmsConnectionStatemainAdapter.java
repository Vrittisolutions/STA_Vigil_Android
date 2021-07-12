package com.adapters;

import java.util.ArrayList;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.ConnectionstatusHelper;
import com.beanclasses.LmsconnectionStatusmainBean;


public class LmsConnectionStatemainAdapter extends BaseAdapter {

	private static ArrayList<LmsconnectionStatusmainBean> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	private ArrayList<ConnectionstatusHelper> arraylist;
	
	public LmsConnectionStatemainAdapter(Context context1, ArrayList<LmsconnectionStatusmainBean> results) {
		searchArrayList = results;
		mInflater = LayoutInflater.from(context1);
		context = context1;
		//arraylist=new ArrayList<ConnectionstatusHelper>();
	//	arraylist.addAll(results);
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
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.connectionstatusupdatedmain, null);
			holder = new ViewHolder();
			

			holder.installationid = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvinstallationidmain);
			holder.servertime = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvservertimemain);
		
			holder.noReason = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvreasonupdated);
//		
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.installationid.setText(searchArrayList.get(position).getStationName());
		holder.servertime.setText(searchArrayList.get(position).getDiff()+"");
			
		

		return convertView;
	}

	static class ViewHolder {
		
		TextView installationid;
		TextView servertime;
		TextView noReason;
	
	}
	
	/*public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		searchArrayList.clear();
		if (charText.length() == 0) {
			searchArrayList.addAll(arraylist);
		} 
		else 
		{
			for (ConnectionstatusHelper wp : arraylist) 
			{
				if (wp.getinstallationId().toLowerCase(Locale.getDefault()).contains(charText)) 
				{
					searchArrayList.add(wp);
				}
			}
		}
		notifyDataSetChanged();
	}*/
	

}

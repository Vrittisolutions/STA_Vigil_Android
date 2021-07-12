package com.adapters;

import java.util.ArrayList;
import java.util.Locale;


import com.beanclasses.ConnectionstatusHelper;
import com.stavigilmonitoring.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ConnectionstatusAdaptMain extends BaseAdapter {

	private static ArrayList<ConnectionstatusHelper> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	private ArrayList<ConnectionstatusHelper> arraylist;
	
	public ConnectionstatusAdaptMain(Context context1, ArrayList<ConnectionstatusHelper> results) {
		searchArrayList = results;
		mInflater = LayoutInflater.from(context1);
		context = context1;
		arraylist=new ArrayList<ConnectionstatusHelper>();
		arraylist.addAll(results);
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
			convertView = mInflater.inflate(R.layout.connectionstatusupdatedmain, null);
			holder = new ViewHolder();

			holder.installationid = (TextView) convertView
					.findViewById(R.id.tvinstallationidmain);
			holder.servertime = (TextView) convertView
					.findViewById(R.id.tvservertimemain);
		
			holder.noReason = (TextView) convertView
					.findViewById(R.id.tvreasonupdated);
//		
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getinstallationId();

		if (s.equals("")) {

			holder.installationid.setText("No Station Found..");
			holder.servertime.setText("");
			holder.noReason.setText("");

		} else {

			holder.installationid.setText(searchArrayList.get(position)
					.getinstallationId());
			
			holder.servertime.setText(searchArrayList.get(position).gettymdiff());
			String s1 = searchArrayList.get(position).getreason();
			if (s1.equals("No Reason Found")) {
				holder.noReason.setText("");

			}
			else{
			holder.noReason.setText(searchArrayList.get(position)
					.getreason());
			}
		//	holder.starttime.setText(searchArrayList.get(position)
				//	.getStartTime());
			
		}

		return convertView;
	}

	static class ViewHolder {
		
		TextView installationid;
		TextView servertime;
		TextView noReason;
	
	}
	
	public void filter(String charText) {
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
	}
	

}

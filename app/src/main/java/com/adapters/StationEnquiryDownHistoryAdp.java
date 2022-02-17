package com.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StationEnquiryDownHistoryAdp extends BaseAdapter{


	private static ArrayList<String> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	private ArrayList<String> arraylist;

	public StationEnquiryDownHistoryAdp(Context context1, ArrayList<String> Results) {
		searchArrayList = Results;
		mInflater = LayoutInflater.from(context1);
		context = context1;
		arraylist=new ArrayList<String>();
		arraylist.addAll(Results);
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
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.reasonitem, null);
			holder = new ViewHolder();
			holder.stnname = (TextView) convertView	.findViewById(com.stavigilmonitoring.R.id.tvReason);
			
			
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).toString();

		if (s.equals("")) {
			holder.stnname.setText("No Information available..");

		} else {

			holder.stnname
					.setText(searchArrayList.get(position).toString());
		

			
			
			
		}

		return convertView;
	}

	static class ViewHolder {
		TextView stnname;
		
	
		

	}

	public void filter(String charText) {
		
		charText =charText.toLowerCase(Locale.getDefault());
		searchArrayList.clear();
		if(charText.length()==0)
		{
			searchArrayList.addAll(arraylist);
		}
		else
		{
			for (String wp : arraylist) 
			{
				if (wp.toLowerCase(Locale.getDefault()).contains(charText)) 
				{
					searchArrayList.add(wp);
				}
			}
		}
		notifyDataSetChanged();
		
	}
	
		
	

}

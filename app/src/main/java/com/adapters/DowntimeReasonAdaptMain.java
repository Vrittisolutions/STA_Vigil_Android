package com.adapters;

import java.util.ArrayList;
import java.util.Locale;



import com.database.DBInterface;


import com.stavigilmonitoring.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DowntimeReasonAdaptMain extends BaseAdapter {

	ArrayList<String> searchArrayList;

	private static ArrayList<String> arraylist;

	private LayoutInflater mInflater;
	Context context;

	public DowntimeReasonAdaptMain(Context context1, ArrayList<String> searchResults) {
		searchArrayList = searchResults;
		mInflater = LayoutInflater.from(context1);
		arraylist=new ArrayList<String>();
		arraylist.addAll(searchResults);
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
			convertView = mInflater.inflate(R.layout.downtimeupdatedmain, null);
			holder = new ViewHolder();
			holder.stnname = (TextView) convertView
					.findViewById(R.id.tvstnnamedownmain);

	

			
			
			
			
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
	static class ViewHolder {
		TextView stnname;
		
	
		

	}

}

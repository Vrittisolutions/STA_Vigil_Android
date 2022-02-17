package com.adapters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import com.beanclasses.NonreportedList;
import com.stavigilmonitoring.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NonrepeatedAdAdaptMain extends BaseAdapter  {

	private static List<NonreportedList> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	private ArrayList<NonreportedList> arraylist;
	
	public NonrepeatedAdAdaptMain(Context context1, List<NonreportedList> results) {
		searchArrayList = results;
		mInflater = LayoutInflater.from(context1);
		context = context1;
		arraylist=new ArrayList<NonreportedList>();
		arraylist.addAll(searchArrayList);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.nonrepeatedadupdatedmain, null);
			holder = new ViewHolder();
			holder.InstallationDesc = (TextView) convertView
					.findViewById(R.id.tvadvcodemain);

			holder.InstallationCnt = (TextView) convertView
					.findViewById(R.id.tvinstCount);
		
		holder.LastSeen = (TextView) convertView
				.findViewById(R.id.tvlastseen);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).toString();

		if (s.equals("")) {
			holder.InstallationDesc.setText("No StationFound..");
			holder.InstallationCnt.setText("");
			holder.LastSeen.setText("");

		} else {

			holder.InstallationDesc
					.setText(searchArrayList.get(position).getInstallationDesc());
			holder.InstallationCnt.setText(searchArrayList.get(position).getInstallationCount());
			//holder.LastSeen.setText(searchArrayList.get(position).getLastServerTime());
			
			String tf = searchArrayList.get(position).getLastServerTime();
			String tfdateday = searchArrayList.get(position).getLastServerTime();
			String tftym = searchArrayList.get(position).getLastServerTime();
	
			if(!tf.trim().equalsIgnoreCase(""))
			{
				String v2 = splittime1(tf);		
				String[] v1 = splitfrom1(tf);			
				
				holder.LastSeen.setText(v2+" "+v1[0]);
			}
			else
				holder.LastSeen.setText("");
		}

		return convertView;
	}

	static class ViewHolder {
		TextView InstallationDesc;
		TextView InstallationCnt;
		TextView LastSeen;	
	}

	private String splittime1(String tf) {
		
		 long diffDays=0;
		System.out.println("---value of tf for date...."+tf);
		String fromtimetw = "";
		
		String k = tf.substring(0, tf.length() - 11);
		System.out.println("---value of k for date server date..."+k);

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(k);
			System.out.println("..........value of my date after conv"+myDate);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy");
		String finalDate = timeFormat.format(myDate);
		System.out.println("..............final date of update link>>>>>"+finalDate);
		
		final String dateStart = finalDate;
		  DateFormat dateFormat1 = new SimpleDateFormat("dd MMM, yyyy");
		  Date date = new Date();
		  System.out.println("date format of system......................"+dateFormat1.format(date));
		  System.out.println("<<<<<<<<<<<<<<<<date format startdate......................"+dateStart);
		 System.out.println("date format of web tym......................"+date);
		  final String dateStop =dateFormat1.format(date);
		  System.out.println("<<<<<<<<<<<<<<<<date format dateStop......................"+dateStop);
		  Date d1 = null;
			Date d2 = null;
			String diffTym="";
			
			try {
				d1 = dateFormat1.parse(dateStart);
				d2 = dateFormat1.parse(dateStop);
				 System.out.println("d2......................"+d2);
				//in milliseconds
				long diff = d2.getTime() - d1.getTime();
	 
				diffDays = diff / (24 * 60 * 60 * 1000);
		
			} catch (Exception e) {
				e.printStackTrace();
			}
			

			if (diffDays == 0) {
				return "Today";

			} else if (diffDays == 1) {
				return "Yesterday";
			} else {
				return finalDate;
			}

	}
	
	private String[] splitfrom1(String tf) {		
		String fromtimetw = "";
		String k = tf.substring(9, tf.length() - 0);		
		String[] v1 = { k };	
	
			return v1;
	}
	
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		searchArrayList.clear();
		if (charText.length() == 0) {
			searchArrayList.addAll(arraylist);
		} 
		else 
		{
			for (NonreportedList wp : arraylist) 
			{
				if (wp.getInstallationDesc().toLowerCase(Locale.getDefault()).contains(charText)) 
				{
					searchArrayList.add(wp);
				}
			}
		}
		notifyDataSetChanged();
	}
	

	
}

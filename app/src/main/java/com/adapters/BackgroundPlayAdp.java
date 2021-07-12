package com.adapters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.stavigilmonitoring.BackgroundPlaylistData;
import com.stavigilmonitoring.BackgroundPlaylistDetail;
import com.beanclasses.StatelevelList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BackgroundPlayAdp extends BaseAdapter {

	private static List<StatelevelList> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	private ArrayList<StatelevelList> arraylist;

	public BackgroundPlayAdp(Context context1, List<StatelevelList> results) {
		searchArrayList = results;
		mInflater = LayoutInflater.from(context1);
		context = context1;
		arraylist = new ArrayList<StatelevelList>();
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
			convertView = mInflater
					.inflate(com.stavigilmonitoring.R.layout.bgplaylistsationitem, null);
			holder = new ViewHolder();
			holder.InstallationDesc = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvinstCount);
			holder.LastSeen = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvlastseen);
			holder.detail_click = (ImageView) convertView.findViewById(com.stavigilmonitoring.R.id.opendetail);
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

			holder.InstallationDesc.setText(searchArrayList.get(position)
					.getStatioName());
			holder.LastSeen
					.setText(searchArrayList.get(position).getTimeData());
			holder.detail_click.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context, BackgroundPlaylistDetail.class);
				    intent.putExtra("InstallationID",searchArrayList.get(position).getInstallationId());
				    intent.putExtra("StationName",searchArrayList.get(position).getStatioName());
				  
				    context.startActivity(intent);
					
					
				}
			});
			holder.InstallationDesc.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context, BackgroundPlaylistData.class);
				    intent.putExtra("InstallationID",searchArrayList.get(position).getInstallationId());
				    intent.putExtra("StationName",searchArrayList.get(position).getStatioName());
				    context.startActivity(intent);
				}
			});
			holder.detail_click.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					Toast toast = Toast.makeText(context, " Open Detail", Toast.LENGTH_SHORT);
					toast.show();
					return true;
				}
			});
			/*
			 * //holder.LastSeen.setText(searchArrayList.get(position).
			 * getLastServerTime());
			 * 
			 * String tf = searchArrayList.get(position).getLastServerTime();
			 * String tfdateday =
			 * searchArrayList.get(position).getLastServerTime(); String tftym =
			 * searchArrayList.get(position).getLastServerTime();
			 * 
			 * if(!tf.trim().equalsIgnoreCase("")) { String v2 = splittime1(tf);
			 * String[] v1 = splitfrom1(tf);
			 * 
			 * holder.LastSeen.setText(v2+" "+v1[0]); } else
			 * holder.LastSeen.setText("");
			 * 
			 * 
			 * 
			 * }
			 */

		}
		return convertView;
	}

	static class ViewHolder {
		TextView InstallationDesc;
		TextView InstallationCnt;
		TextView LastSeen;
		ImageView detail_click;
	}

	private String splittime1(String tf) {

		long diffDays = 0;
		System.out.println("---value of tf for date...." + tf);
		String fromtimetw = "";

		String k = tf.substring(0, tf.length() - 11);
		System.out.println("---value of k for date server date..." + k);

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(k);
			System.out
					.println("..........value of my date after conv" + myDate);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy");
		String finalDate = timeFormat.format(myDate);
		System.out.println("..............final date of update link>>>>>"
				+ finalDate);

		final String dateStart = finalDate;
		DateFormat dateFormat1 = new SimpleDateFormat("dd MMM, yyyy");
		Date date = new Date();
		System.out.println("date format of system......................"
				+ dateFormat1.format(date));
		System.out
				.println("<<<<<<<<<<<<<<<<date format startdate......................"
						+ dateStart);
		System.out.println("date format of web tym......................"
				+ date);
		final String dateStop = dateFormat1.format(date);
		System.out
				.println("<<<<<<<<<<<<<<<<date format dateStop......................"
						+ dateStop);
		Date d1 = null;
		Date d2 = null;
		String diffTym = "";

		try {
			d1 = dateFormat1.parse(dateStart);
			d2 = dateFormat1.parse(dateStop);
			System.out.println("d2......................" + d2);
			// in milliseconds
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
		} else {
			for (StatelevelList wp : arraylist) {
				if (wp.getStatioName().toLowerCase(Locale.getDefault())
						.contains(charText)) {
					searchArrayList.add(wp);
				}
			}
		}
		notifyDataSetChanged();
	}

}

package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanclasses.StnEnqAdvList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StationEnqAdvDetailsAdapter extends BaseAdapter  {

	private static List<StnEnqAdvList> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	private ArrayList<StnEnqAdvList> arraylist;

	public StationEnqAdvDetailsAdapter(Context context1, List<StnEnqAdvList> results) {
		searchArrayList = results;
		mInflater = LayoutInflater.from(context1);
		context = context1;
		arraylist=new ArrayList<StnEnqAdvList>();
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
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.stationenqlastthreeitem1, null);
			holder = new ViewHolder();


			holder.LastSeen = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.advdate1);
			holder.InstallationDesc = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.advname1);
			holder.InstallationCnt = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.advcode1);
			holder.AudioOutPut = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.audioop1);
			holder.iv = (ImageView) convertView
					.findViewById(com.stavigilmonitoring.R.id.audioopimg1);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).toString();

		if (s.equals("")) {
			holder.InstallationDesc.setText("No StationFound..");
			holder.InstallationCnt.setText("");
			holder.LastSeen.setText("");
			holder.AudioOutPut.setText("");

		} else {

			holder.InstallationDesc.setText(searchArrayList.get(position).getInstallationDesc());
			holder.InstallationCnt.setText(searchArrayList.get(position).getInstallationCount());
			holder.LastSeen.setText(covertdateformtotime(searchArrayList.get(position).getLastServerTime()));
			if(searchArrayList.get(position).getAudioOutPut().equals("NA")){
				holder.AudioOutPut.setVisibility(View.GONE);
				holder.iv.setVisibility(View.GONE);
			}else {
				holder.AudioOutPut.setText(searchArrayList.get(position).getAudioOutPut());
			}
		}

		return convertView;
	}

	static class ViewHolder {
		TextView InstallationDesc;
		TextView InstallationCnt;
		TextView LastSeen;
		TextView AudioOutPut;
		ImageView iv;

		TextView aBustime,aActann,afrombus,aTobus,aBusaddate;

	}

	private String covertdateform(String amcExpireDt) {
		String result= null;
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+05:30");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM,yyyy   hh:mm aa");
		try {
			Date date2 = dateFormat1.parse(amcExpireDt);
			result = dateFormat2.format(date2);
		}catch( Exception e){
			e.printStackTrace();
		}
		return result;
	}

	private String covertdateformtotime(String amcExpireDt) {//07-26-2018 7:15:12 PM
		String result= null;
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+05:30");

		SimpleDateFormat dateFormat3 = new SimpleDateFormat("M/dd/yyyy h:mm:ss aa");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm aa");
		try {
			Date date2 = dateFormat3.parse(amcExpireDt);
			result = dateFormat2.format(date2);
		}catch( Exception e){
			e.printStackTrace();
		}
		return result;
	}

	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		searchArrayList.clear();
		if (charText.length() == 0) {
			searchArrayList.addAll(arraylist);
		} 
		else 
		{
			for (StnEnqAdvList wp : arraylist)
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

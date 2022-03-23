package com.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.beanclasses.SuspectedHelper;
import com.stavigilmonitoring.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class SuspectedAdapter_new extends BaseAdapter {

	private static List<SuspectedHelper> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	private List<SuspectedHelper> arraylist;



	public SuspectedAdapter_new(Context context1,List<SuspectedHelper> results) {
		searchArrayList =  results;
		mInflater = LayoutInflater.from(context1);
		context = context1;
		arraylist=new ArrayList<>();
		arraylist.addAll(results);
		Log.e("SUSP_ARRAYLIST"," results--> "+results.size());
		Log.e("SUSP_ARRAYLIST"," --> "+arraylist.size());
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
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.suspected_stnlist_one, null);

			holder = new ViewHolder();
			
			holder.stname = (TextView) convertView.findViewById(R.id.stname);
			holder.spotperctg = (TextView) convertView.findViewById(R.id.spotperctg);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

			holder.stname.setText(searchArrayList.get(position).getStationName());
			holder.spotperctg.setText(searchArrayList.get(position).getSpotWisePercentage()+"%");

		return convertView;
	}

	public void filter_details(String charText) {

		searchArrayList.clear();
		if(charText.length()==0)
		{
			searchArrayList.addAll(arraylist);
		} else {
			for (SuspectedHelper wp : arraylist)
			{
				if (wp.getStationName().toLowerCase(Locale.getDefault()).startsWith(charText.toLowerCase(Locale.getDefault()))
						||
						wp.getStationName().toLowerCase(Locale.getDefault()).contains(charText.toLowerCase(Locale.getDefault())))
				{
					searchArrayList.add(wp);
				}
			}
		}
		notifyDataSetChanged();

	}

    static class ViewHolder {
		TextView stname, spotperctg;
	}

/*	 Collections.sort(arraylist, new Comparator<SuspectedHelper>(){
		public int compare(SuspectedHelper d1, SuspectedHelper d2){

			return (d1.getDataUsagePercent()) - Integer.parseInt(d2.getDataUsagePercent()));
		}

	});*/



}

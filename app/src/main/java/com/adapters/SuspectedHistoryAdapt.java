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

public class SuspectedHistoryAdapt extends BaseAdapter {

	private static ArrayList<SuspectedHelper> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public SuspectedHistoryAdapt(Context context1, ArrayList<SuspectedHelper> results) {
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
			convertView = mInflater.inflate(R.layout.suspected_stnlist_dtl, null);
			holder = new ViewHolder();
			

			holder.advdesc = convertView.findViewById(R.id.advdesc);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

			holder.advdesc.setText(searchArrayList.get(position).getAdvertisementName());

		return convertView;
	}

	static class ViewHolder {
		
		TextView advdesc;
	
		

	}

}

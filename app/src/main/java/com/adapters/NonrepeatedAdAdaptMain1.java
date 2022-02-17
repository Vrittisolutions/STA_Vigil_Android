package com.adapters;

import java.util.ArrayList;


import com.beanclasses.NonrepeatedAdHelper;
import com.stavigilmonitoring.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NonrepeatedAdAdaptMain1 extends BaseAdapter {

	private static ArrayList<NonrepeatedAdHelper> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public NonrepeatedAdAdaptMain1(Context context1, ArrayList<NonrepeatedAdHelper> results) {
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
			convertView = mInflater.inflate(R.layout.nonrepeatedadupdatedmain, null);
			holder = new ViewHolder();
			holder.adcode = (TextView) convertView
					.findViewById(R.id.tvadvcodemain);

//			holder.addesc = (TextView) convertView
//					.findViewById(R.id.tvadnamemain);
			
//			holder.noncount = (TextView) convertView
//					.findViewById(R.id.tvnonreportedcount);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getadvcode();

		if (s.equals("")) {
			holder.adcode.setText("No StationFound..");

		} else {

			holder.adcode
					.setText(searchArrayList.get(position).getadvcode());
//			holder.addesc.setText(searchArrayList.get(position)
//					.getadvName());
			
//			holder.noncount.setText(searchArrayList.get(position)
//					.getcsncount());
//			
//			
			
		}

		return convertView;
	}

	static class ViewHolder {
		TextView adcode;
		TextView addesc;
		TextView noncount;
	
	}

}

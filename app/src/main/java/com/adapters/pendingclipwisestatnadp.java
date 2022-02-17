package com.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.NonRepStateBean;

public class pendingclipwisestatnadp extends BaseAdapter {

	private static List<NonRepStateBean> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	
	public pendingclipwisestatnadp(Context c,List<NonRepStateBean> searchResults)
	{
		context=c;
		mInflater = LayoutInflater.from(context);
		searchArrayList=searchResults;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.statelistitem, null);
			holder = new ViewHolder();
			holder.tvsName = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvsName);
			holder.tvsStcnt = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvsAdcnt);//tvsAdcnt
			holder.tvsAdCnt = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvsCnt);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String s = searchArrayList.get(position).GetStateName();
		if(s.equalsIgnoreCase(""))
		{
			holder.tvsName.setText("No Station Found..");
		}
		else{
		
		holder.tvsName.setText(searchArrayList.get(position).GetStateName());
		holder.tvsStcnt.setText("Pending Clips Stations : "+searchArrayList.get(position).GetSCount()+"");
		holder.tvsAdCnt.setText(searchArrayList.get(position).GetAdCnt()+"");
		}
		
		return convertView;
	}
	static class ViewHolder {
		TextView tvsName;
		TextView tvsAdCnt;
		TextView tvsStcnt;	
	}



}

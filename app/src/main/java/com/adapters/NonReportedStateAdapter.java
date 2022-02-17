package com.adapters;

import java.util.List;

import com.beanclasses.NonRepStateBean;
import com.stavigilmonitoring.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NonReportedStateAdapter extends BaseAdapter {
	private static List<NonRepStateBean> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public NonReportedStateAdapter(Context c, List<NonRepStateBean> data) {
		context = c;
		mInflater = LayoutInflater.from(context);
		searchArrayList = data;
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
			convertView = mInflater.inflate(R.layout.statelistitem, null);
			holder = new ViewHolder();
			holder.tvsName = (TextView) convertView.findViewById(R.id.tvsName);
			holder.tvsStcnt = (TextView) convertView.findViewById(R.id.tvsCnt);
			holder.tvsAdCnt = (TextView) convertView
					.findViewById(R.id.tvsAdcnt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).GetStateName();
		if (s.equalsIgnoreCase("")) {
			holder.tvsName.setText("No Station Found..");
		} else {

			holder.tvsName
					.setText(searchArrayList.get(position).GetStateName());
			holder.tvsStcnt.setText(searchArrayList.get(position).GetSCount()
					+ "");
			holder.tvsAdCnt.setText("Non-Reported Ads : "
					+ searchArrayList.get(position).GetAdCnt() + "");
		}

		return convertView;
	}

	static class ViewHolder {
		TextView tvsName;
		TextView tvsAdCnt;
		TextView tvsStcnt;
	}

}

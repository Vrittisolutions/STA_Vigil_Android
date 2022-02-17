package com.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.StateList;

public class ClipPendingClipStationAdp extends BaseAdapter {

	private static List<StateList> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public ClipPendingClipStationAdp(Context c,
			List<StateList> searchResults) {
		context = c;
		mInflater = LayoutInflater.from(context);
		searchArrayList = searchResults;
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
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.clipwisestationitem, null);
			holder = new ViewHolder();
			holder.tvsCode = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvcode);
			holder.advsreleas = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.Advreleasdisplay);
			holder.AdvtvStart = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.advStartdisplay);
			holder.tvsStatus = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvnonlasttime);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tvsCode.setText(searchArrayList.get(position).GetAdsDesc() + "");
		holder.advsreleas.setText(searchArrayList.get(position).getClrDate()
				+ "");
		holder.AdvtvStart.setText(searchArrayList.get(position).GetAddedDate()
				+ "");
		holder.tvsStatus
				.setText(searchArrayList.get(position).GetStatus() + "");

		// holder.tvsAdCnt.setText("Non-Reported Ads : "+searchArrayList.get(position).GetAdCnt()+"");

		return convertView;
	}

	static class ViewHolder {
		TextView tvsCode, advsreleas, AdvtvStart, tvsStatus;

	}

}

package com.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.StateList;

public class StationEnquiryfilteradp extends BaseAdapter {

	private static ArrayList<StateList> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public StationEnquiryfilteradp(Context c,
			ArrayList<StateList> searchResults) {
		context = c;
		mInflater = LayoutInflater.from(context);
		// searchResults.notify();
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
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.sounditemlist, null);
			holder = new ViewHolder();

			holder.tvNetworkCode = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvsNamec);
			holder.tvCount = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvsCntc);
			//holder.tvCount.setVisibility(View.GONE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getNetworkcode();

		if (s.equals("")) {
			holder.tvNetworkCode.setText("No Station Found..");
			holder.tvCount.setText("");

		} else {

			holder.tvNetworkCode.setText(searchArrayList.get(position).getNetworkcode());
			holder.tvCount.setText(searchArrayList.get(position).Getcount() + "");

		}

		return convertView;

	}

	static class ViewHolder {

		TextView tvNetworkCode;
		TextView tvCount;

	}

}

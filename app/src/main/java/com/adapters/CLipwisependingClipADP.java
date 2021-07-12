package com.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.NonrepeatedAdHelper;

public class CLipwisependingClipADP extends BaseAdapter {

	private static ArrayList<NonrepeatedAdHelper> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public CLipwisependingClipADP(Context context1,
			ArrayList<NonrepeatedAdHelper> results) {
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
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.advnonreportedadvitem,
					null);
			holder = new ViewHolder();
			holder.advcode = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvadname);
			
			holder.advdesk = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvstationname);
			
			holder.Advdate = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvnonDatefrom);
			holder.AdvFrom = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvnontimefrom);
			holder.AdvFrom.setVisibility(View.GONE);
			holder.advCnt = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvtotalcount);
			holder.Advcnt = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvnoncounnt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getadvName();

		if (s.equals("")) {
			holder.advcode.setText("No Information Available....");

		} else {
			//String Filename[] = searchArrayList.get(position).getfilename();

			holder.advdesk.setText(searchArrayList.get(position).getadvName());
			holder.advcode.setText(searchArrayList.get(position).getadvcode());
			holder.Advdate.setText(searchArrayList.get(position).getdateFrom());
			holder.Advcnt.setText(searchArrayList.get(position).getcsncount());
			holder.advCnt.setText(searchArrayList.get(position).getAdvCnt());
		}

		return convertView;
	}

	static class ViewHolder {

		TextView advCnt;
		TextView advcode;
		TextView advdesk;
		TextView Advdate;
		TextView AdvFrom;
		TextView Advcnt;

	}

}

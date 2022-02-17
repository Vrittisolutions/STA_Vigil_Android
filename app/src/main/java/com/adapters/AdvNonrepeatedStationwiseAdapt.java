package com.adapters;

import java.util.ArrayList;

import com.beanclasses.NonrepeatedAdHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdvNonrepeatedStationwiseAdapt extends BaseAdapter {

	private static ArrayList<NonrepeatedAdHelper> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public AdvNonrepeatedStationwiseAdapt(Context context1,
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
			convertView = mInflater
					.inflate(com.stavigilmonitoring.R.layout.nonrepstation, null);
			holder = new ViewHolder();
            holder.addesc = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvadname);
			holder.instdesc = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvstationname);
			
			holder.fromdate = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvdatefrom);
			holder.todate = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvdateto);
			holder.masterrecord = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvmasterrecord);
			holder.detailrecord = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvdetailrecord);
			holder.clipmaster = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvclipmaster);

			holder.fromtime = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvnontimefrom);

			holder.totime = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvnontimeto);

			holder.firstDate = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvnonfirstdate);

			holder.firstTime = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvnonfirsttime);

			holder.lastDate = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvnonlastdate);

			holder.lastTime = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvnonlasttime);

			holder.CSR = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvCSRv);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getadvcode();

		if (s.equals("")) {
			holder.addesc.setText("No Information Available....");

		} else {

			holder.addesc.setText(searchArrayList.get(position).getadvcode());
			

			holder.fromdate
					.setText(searchArrayList.get(position).getdateFrom());

			holder.todate.setText(searchArrayList.get(position).getdateTo());
			holder.masterrecord.setText(searchArrayList.get(position)
					.getmasterrecord());

			holder.detailrecord.setText(searchArrayList.get(position)
					.getdetailrecord());

			holder.clipmaster.setText(searchArrayList.get(position)
					.getclipmaster());

			holder.fromtime
					.setText(searchArrayList.get(position).gettimefrom());

			holder.totime.setText(searchArrayList.get(position).gettimeto());

			holder.firstDate.setText(searchArrayList.get(position)
					.getfirstdate());

			holder.firstTime.setText(searchArrayList.get(position)
					.getfirsttime());

			holder.lastDate
					.setText(searchArrayList.get(position).getlastdate());

			holder.lastTime
					.setText(searchArrayList.get(position).getlasttime());
			holder.CSR.setText(searchArrayList.get(position).getCSR());
		}

		return convertView;
	}

	static class ViewHolder {

		TextView addesc;
		TextView instdesc;
		TextView fromdate;
		TextView fromtime;
		TextView todate;
		TextView totime;
		TextView masterrecord;
		TextView detailrecord;
		TextView clipmaster;
		TextView firstDate;
		TextView firstTime;
		TextView lastDate;
		TextView lastTime;
		TextView CSR;
	}

}
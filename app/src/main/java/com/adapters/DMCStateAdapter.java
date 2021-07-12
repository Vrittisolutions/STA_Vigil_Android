package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.stavigilmonitoring.DmCstnwiseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DMCStateAdapter extends BaseAdapter {
	private static List<DmCstnwiseActivity.StateList> searchArrayList;
	private ArrayList<DmCstnwiseActivity.StateList> arraylist;

	private LayoutInflater mInflater;
	Context context;

	public DMCStateAdapter(Context c, List<DmCstnwiseActivity.StateList> data)
	{
		context=c;
		mInflater = LayoutInflater.from(context);
		searchArrayList=data;
		arraylist=new ArrayList<DmCstnwiseActivity.StateList>();
		arraylist.addAll(data);
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
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.csnstatelstitem, null);
			holder = new ViewHolder();
			holder.tvsName = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvsNamec);
			holder.tvsStcnt = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvsCntc);
			holder.imageann = (ImageView) convertView.findViewById(com.stavigilmonitoring.R.id.imageinsteaddaycount);
			holder.tvdayscount = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.dayscounttv);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(searchArrayList.get(position).GetDMDesc()!=null) {
			holder.imageann.setVisibility(View.GONE);
			holder.tvdayscount.setVisibility(View.VISIBLE);
			holder.tvdayscount.setText(String.valueOf(searchArrayList.get(position).Getdayscount()));
			String dmdesc = searchArrayList.get(position).GetDMDesc();
			String[] parts = dmdesc.split("/");
			holder.tvsName.setText(searchArrayList.get(position).GetCertificateDate()+"        "+searchArrayList.get(position).GetStateName()
					+"\n"+parts[2]+"\nAdv. Date : "+searchArrayList.get(position).GetAdvDate());
		}else{
			holder.tvsName.setText(searchArrayList.get(position).GetStateName());
		}
		holder.tvsStcnt.setText(searchArrayList.get(position).GetOverdueCnt()+"/"+searchArrayList.get(position).GetSCount());
		//holder.tvsAdCnt.setText("Non-Reported Ads : "+searchArrayList.get(position).GetAdCnt()+"");
		return convertView;
	}

	public void filter_DMDesc(String charText) {
		//charText =charText.toLowerCase(Locale.getDefault());
		searchArrayList.clear();
		if(charText.length()==0)
		{
			searchArrayList.addAll(arraylist);
		}
		else
		{
			for (DmCstnwiseActivity.StateList wp : arraylist)
			{
				if (wp.GetDMDesc().toLowerCase(Locale.getDefault()).contains(charText))
				{
					searchArrayList.add(wp);
				}
			}
		}
		//notifyDataSetChanged();
	}

	public void filter_Station(String charText) {
		//charText =charText.toLowerCase(Locale.getDefault());
		searchArrayList.clear();
		if(charText.length()==0)
		{
			searchArrayList.addAll(arraylist);
		}
		else
		{
			for (DmCstnwiseActivity.StateList wp : arraylist)
			{
				if (wp.GetStateName().toLowerCase(Locale.getDefault()).contains(charText))
				{
					searchArrayList.add(wp);
				}
			}
		}
		//notifyDataSetChanged();
	}

	static class ViewHolder {
		TextView tvsName;		
		TextView tvsStcnt, tvdayscount;
		ImageView imageann;
	}
}


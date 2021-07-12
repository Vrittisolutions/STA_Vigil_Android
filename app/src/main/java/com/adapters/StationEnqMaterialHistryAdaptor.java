package com.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.StationEnquiryMaterialBean;

public class StationEnqMaterialHistryAdaptor extends BaseAdapter {

	private static ArrayList<StationEnquiryMaterialBean> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	private ArrayList<StationEnquiryMaterialBean> arraylist;
	public StationEnqMaterialHistryAdaptor(Context c,ArrayList<StationEnquiryMaterialBean> searchResults)
	{
		context=c;
		mInflater = LayoutInflater.from(context);
		searchArrayList=searchResults;
		arraylist=new ArrayList<StationEnquiryMaterialBean>();
		arraylist.addAll(searchResults);
		
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
public void filter(String charText) {
		
		//charText =charText.toLowerCase(Locale.getDefault());
		searchArrayList.clear();
		if(charText.length()==0)
		{
			searchArrayList.addAll(arraylist);
		}
		else
		{
			for (StationEnquiryMaterialBean wp : arraylist) 
			{
				if (wp.getStationname().toLowerCase(Locale.getDefault()).contains(charText)) 
				{
					searchArrayList.add(wp); 
				}
			}
		}
		notifyDataSetChanged();
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.materialhistryitem, null);
			holder = new ViewHolder();
			holder.matnamr = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.matdisplay);
			holder.qnt = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvsCntc);
			holder.reson = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.resondisplay);
			holder.reportee = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.reporteedisplay);//reporteedisplay
			holder.date = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.dateDisplay);
			holder.status = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.StatusDispaly);
			holder.orderby = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.snderdisplay);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.matnamr.setText(searchArrayList.get(position).getMaterialname());
		holder.qnt.setText(searchArrayList.get(position).getQty()+"");
		holder.reson.setText(searchArrayList.get(position).getReason());
		holder.reportee.setText(searchArrayList.get(position).getReporteename());
		holder.date.setText(searchArrayList.get(position).getAddedtdt());
		holder.status.setText(searchArrayList.get(position).getStatusname());
		holder.orderby.setText(searchArrayList.get(position).getSendername());
		
		return convertView;
	}
	static class ViewHolder {
		TextView matnamr,qnt,reson,orderby,reportee,date,status;		
		TextView tvsStcnt;	
	}
}

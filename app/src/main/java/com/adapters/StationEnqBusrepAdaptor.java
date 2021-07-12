package com.adapters;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.StationEnqBusBean;

public class StationEnqBusrepAdaptor extends BaseAdapter {


	private static ArrayList<StationEnqBusBean> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	private ArrayList<StationEnqBusBean> arraylist;
	public StationEnqBusrepAdaptor(Context c,ArrayList<StationEnqBusBean> searchResults)
	{
		context=c;
		mInflater = LayoutInflater.from(context);
		searchArrayList=searchResults;
		arraylist=new ArrayList<StationEnqBusBean>();
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


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.busreportingitem, null);
			holder = new ViewHolder();
		holder.BusDate = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.datebus);
		holder.tvsStcnt = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.cnt);
			/*holder.workrem = (TextView) convertView.findViewById(R.id.wkremdisplay);
			holder.material = (TextView) convertView.findViewById(R.id.matedisplay);//reporteedisplay
			holder.matremark = (TextView) convertView.findViewById(R.id.matremDisplay);
			holder.date = (TextView) convertView.findViewById(R.id.dateDisplay);
			holder.mobile = (TextView) convertView.findViewById(R.id.mobDisplay);
			holder.location = (TextView) convertView.findViewById(R.id.LocationDispaly);*/
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.BusDate.setText(searchArrayList.get(position).getDate());
		holder.tvsStcnt.setText(searchArrayList.get(position).getBusCnt()+"");
		/*holder.workrem.setText(searchArrayList.get(position).getWorkRemark());
		holder.material.setText(searchArrayList.get(position).getMaterialname());
		holder.matremark.setText(searchArrayList.get(position).getMatremark());
		holder.date.setText(searchArrayList.get(position).getDate());
		holder.mobile.setText(searchArrayList.get(position).getMobno());
		holder.location.setText(searchArrayList.get(position).getLocation());*/
		
		return convertView;
	}
	static class ViewHolder {
		TextView BusDate;		
		TextView tvsStcnt;	
	}
}

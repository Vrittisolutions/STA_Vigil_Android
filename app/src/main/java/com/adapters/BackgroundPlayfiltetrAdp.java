package com.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.StatelevelList;

public class BackgroundPlayfiltetrAdp extends BaseAdapter {


	private static List<StatelevelList> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	
	public BackgroundPlayfiltetrAdp(Context c,List<StatelevelList> data)
	{
		context=c;
		mInflater = LayoutInflater.from(context);
		searchArrayList=data;
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
			//holder.tvsAdCnt = (TextView) convertView.findViewById(R.id.tvsAdcnt);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvsName.setText(searchArrayList.get(position).getSubNetworkcode());
		holder.tvsStcnt.setText(searchArrayList.get(position).Getcount()+"");
		//holder.tvsAdCnt.setText("Non-Reported Ads : "+searchArrayList.get(position).GetAdCnt()+"");
		
		return convertView;
	}
	static class ViewHolder {
		TextView tvsName;		
		TextView tvsStcnt;	
	}


	


}

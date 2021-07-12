package com.adapters;

import java.util.List;

import com.stavigilmonitoring.R;
import com.beanclasses.StateList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CSNStateAdapter extends BaseAdapter {
	private static List<StateList> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	
	public CSNStateAdapter(Context c,List<StateList> data)
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
			convertView = mInflater.inflate(R.layout.csnstatelstitem, null);
			holder = new ViewHolder();
			holder.tvsName = (TextView) convertView.findViewById(R.id.tvsNamec);
			holder.tvsStcnt = (TextView) convertView.findViewById(R.id.tvsCntc);
			//holder.tvsAdCnt = (TextView) convertView.findViewById(R.id.tvsAdcnt);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvsName.setText(searchArrayList.get(position).getStateName());
		holder.tvsStcnt.setText(searchArrayList.get(position).GetSCount()+"");
		//holder.tvsAdCnt.setText("Non-Reported Ads : "+searchArrayList.get(position).GetAdCnt()+"");
		
		return convertView;
	}
	static class ViewHolder {
		TextView tvsName;		
		TextView tvsStcnt;	
	}

}


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

public class PendingClipslistAdapt extends BaseAdapter {

	private static List<StateList> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	
	public PendingClipslistAdapt(Context c,List<StateList> searchResults)
	{
		context=c;
		mInflater = LayoutInflater.from(context);
		searchArrayList=searchResults;
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
			convertView = mInflater.inflate(R.layout.pendingclipslist, null);
			holder = new ViewHolder();
			holder.tvsDsce = (TextView) convertView.findViewById(R.id.tvadname);
			holder.tvsCode = (TextView) convertView.findViewById(R.id.tvcode);
			holder.advsreleas = (TextView) convertView.findViewById(R.id.Advreleasdisplay);
			holder.AdvtvStart = (TextView) convertView.findViewById(R.id.advStartdisplay);
			holder.tvsStatus = (TextView) convertView.findViewById(R.id.tvnonlasttime);
			
			convertView.setTag(holder);
			//holder.tvsAdCnt = (TextView) convertView.findViewById(R.id.tvsAdcnt);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvsDsce.setText(searchArrayList.get(position).GetAdsDesc());
		holder.tvsCode.setText(searchArrayList.get(position).GetAdsCode()+"");
		holder.advsreleas.setText(searchArrayList.get(position).getClrDate()+"\t");
		holder.AdvtvStart.setText(searchArrayList.get(position).GetAddedDate()+"\t");
		holder.tvsStatus.setText(searchArrayList.get(position).GetStatus()+"");
		
		//holder.tvsAdCnt.setText("Non-Reported Ads : "+searchArrayList.get(position).GetAdCnt()+"");
		
		return convertView;
	}

	static class ViewHolder {
		TextView tvsDsce,tvsCode,advsreleas,AdvtvStart,tvsStatus;
	}
}


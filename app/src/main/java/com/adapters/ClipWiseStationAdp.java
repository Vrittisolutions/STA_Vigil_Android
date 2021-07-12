package com.adapters;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.PendingStateList;

public class ClipWiseStationAdp extends BaseAdapter {
	private static List<PendingStateList> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	private ArrayList<PendingStateList> arraylist;
	public ClipWiseStationAdp(Context c,List<PendingStateList> searchResults)
	{
		context=c;
		mInflater = LayoutInflater.from(context);
		searchArrayList=searchResults;
		arraylist=new ArrayList<PendingStateList>();
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
/*public void filter(String charText) {
		
		//charText =charText.toLowerCase(Locale.getDefault());
		searchArrayList.clear();
		if(charText.length()==0)
		{
			searchArrayList.addAll(arraylist);
		}
		else
		{
			for (com.beanclasses.PendingStateList wp : arraylist)
			{
				if (wp.GetStateName().toLowerCase(Locale.getDefault()).contains(charText)) 
				{
					searchArrayList.add(wp); 
				}
			}
		}
		notifyDataSetChanged();
		
	}*/

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.pendingclipsmainitem, null);
			holder = new ViewHolder();
			holder.tvsName = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvsNamec);
			holder.tvsStcnt = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvsCntc);
			holder.textservertime = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.textservertime);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvsName.setText(searchArrayList.get(position).GetStateName());
		holder.tvsStcnt.setText(searchArrayList.get(position).GetSCount()+"");
		holder.textservertime.setText(searchArrayList.get(position).getServerTime());
		
		return convertView;
	}
	static class ViewHolder {
		TextView tvsName,textservertime;		
		TextView tvsStcnt;	
	}

}

package com.adapters;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.AlrtStateList;
import com.stavigilmonitoring.R;


public class AlrtStatewiseAdptr extends BaseAdapter {
	


	private static ArrayList<AlrtStateList> searchArrayList;
	private ArrayList<AlrtStateList> arraylist;

	private LayoutInflater mInflater;
	Context context;
	
	public AlrtStatewiseAdptr(Context c,ArrayList<AlrtStateList> mSearchList)
	{
		context=c;
		mInflater = LayoutInflater.from(context);
		//searchResults.notify();
		searchArrayList=mSearchList;
		arraylist=new ArrayList<AlrtStateList>();
		arraylist.addAll(mSearchList);
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
			convertView = mInflater.inflate(R.layout.sounditemlist, null);
			holder = new ViewHolder();
			
          	holder.tvNetworkCode = (TextView) convertView
					.findViewById(R.id.tvsNamec);
          	holder.tvCount = (TextView) convertView
					.findViewById(R.id.tvsCntc);
          //	holder.tvCount.setVisibility(View.GONE);
			convertView.setTag(holder);
		} 
		else 
		{
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getNetworkcode();

		if (s.equals("")) {
			holder.tvNetworkCode.setText("No Station Found..");
			holder.tvCount.setText("");

		} else {
			holder.tvNetworkCode.setText(searchArrayList.get(position).getNetworkcode());
			if(searchArrayList.get(position).Getact().equalsIgnoreCase("Work")) {
				holder.tvCount.setText(searchArrayList.get(position).GetOverdueCnt()+ "/" +searchArrayList.get(position).Getcount() );
			}else if(searchArrayList.get(position).Getact().equalsIgnoreCase("Alert")) {
				holder.tvCount.setText(searchArrayList.get(position).Getcount() + "");
			}

		}

		return convertView;
		
	}
	static class ViewHolder {
		
		
		TextView tvNetworkCode;	
		TextView tvCount;
		
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
			for (AlrtStateList wp : arraylist)
			{
				if (wp.getNetworkcode().toLowerCase(Locale.getDefault()).contains(charText))
				{
					searchArrayList.add(wp);
				}
			}
		}
		notifyDataSetChanged();

	}



}

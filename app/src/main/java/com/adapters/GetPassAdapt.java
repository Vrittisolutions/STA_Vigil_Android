package com.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.stavigilmonitoring.R;
import com.beanclasses.StateList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
public class GetPassAdapt extends BaseAdapter {

	private static List<StateList> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	private ArrayList<StateList>  arraylist;
	
	public GetPassAdapt(Context c,List<StateList> searchResults)
	{
		context=c;
		mInflater = LayoutInflater.from(context);
		searchArrayList=searchResults;
		arraylist=new ArrayList<StateList>();
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
			convertView = mInflater.inflate(R.layout.reasonitem, null);
			holder = new ViewHolder();
			
          	holder.StationName = (TextView) convertView
					.findViewById(R.id.tvReason);
          
			convertView.setTag(holder);
		} 
		else 
		{
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getStatioName();

		if (s.equals("")) {
			holder.StationName.setText("No Station Found..");
			
		} else {

			
			holder.StationName.setText(searchArrayList.get(position).getStatioName());

		}

		return convertView;
		
	}
	static class ViewHolder {
		
		
		TextView StationName;	
	
		
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
			for (StateList wp : arraylist)
			{
				if (wp.getStatioName().toLowerCase(Locale.getDefault()).contains(charText))
				{
					searchArrayList.add(wp);
				}
			}
		}
		notifyDataSetChanged();
		
	}


}

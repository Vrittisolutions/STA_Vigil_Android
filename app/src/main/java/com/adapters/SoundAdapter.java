package com.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.StateList;
import com.stavigilmonitoring.R;

public class SoundAdapter extends BaseAdapter {

	private static List<StateList> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	
	public SoundAdapter(Context c,List<StateList> searchResults)
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
			convertView = mInflater.inflate(R.layout.sounditemlist, null);
			holder = new ViewHolder();
			
          	holder.tvNetworkCode = (TextView) convertView
					.findViewById(R.id.tvsNamec);
          	holder.tvCount = (TextView) convertView
					.findViewById(R.id.tvsCntc);
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
			holder.tvCount.setText(searchArrayList.get(position).Getcount()+"");
		}

		return convertView;
		
	}
	static class ViewHolder {
		
		
		TextView tvNetworkCode;	
		TextView tvCount;
		
	}


}

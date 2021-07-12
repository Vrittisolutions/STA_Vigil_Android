package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stavigilmonitoring.R;
import com.beanclasses.SupportEnquiryHelper;

import java.util.ArrayList;

public class FirstPlayRprtNwAdptr extends BaseAdapter {

	private static ArrayList<SupportEnquiryHelper> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public FirstPlayRprtNwAdptr(Context c, ArrayList<SupportEnquiryHelper> searchResults)
	{
		context=c;
		mInflater = LayoutInflater.from(context);
		//searchResults.notify();
		searchArrayList=searchResults;
		notifyDataSetChanged();
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
            holder.tvCount.setVisibility(View.GONE);
			convertView.setTag(holder);
		} 
		else 
		{
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getSubnetwok();

		if (s.equals("")) {
			holder.tvNetworkCode.setText("No Station Found..");
		//	holder.tvCount.setText("");

		} else {

			holder.tvNetworkCode.setText(searchArrayList.get(position).getSubnetwok());
			//holder.tvCount.setText(searchArrayList.get(position).Getcount()+"");
		}

		return convertView;
		
	}
	static class ViewHolder {
		
		
		TextView tvNetworkCode;	
		TextView tvCount;
		
	}






}

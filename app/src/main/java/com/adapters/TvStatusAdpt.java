package com.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.TvStatusStateBean;
import com.stavigilmonitoring.R;

public class TvStatusAdpt extends BaseAdapter {

	private static List<TvStatusStateBean> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	
	public TvStatusAdpt(Context c,List<TvStatusStateBean> searchResults)
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
			convertView = mInflater.inflate(R.layout.tvitem, null);
			holder = new ViewHolder();
			
          	holder.tvNetworkCode = (TextView) convertView
					.findViewById(R.id.tvsNamec);
          	holder.tvCount = (TextView) convertView
					.findViewById(R.id.tvsCntc);
          /*	holder.tvtotalstation = (TextView) convertView
					.findViewById(R.id.tvtotalstation);*/
        
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
		//	holder.tvtotalstation.setText("");

		} else {

			
			holder.tvNetworkCode.setText(searchArrayList.get(position).getNetworkcode());
			holder.tvCount.setText(searchArrayList.get(position).GetScount()+"/"+searchArrayList.get(position).GettotalStation());
			//holder.tvtotalstation.setText(searchArrayList.get(position).GettotalStation()+"");
		
			
			
	
			
		}

		return convertView;
		
	}
	static class ViewHolder {
		
		
		TextView tvNetworkCode;	
		TextView tvCount; // tvtotalstation;
		
		
	}


}

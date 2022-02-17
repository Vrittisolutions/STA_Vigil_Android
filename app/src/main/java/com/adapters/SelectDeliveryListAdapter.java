package com.adapters;

import java.util.ArrayList;
import java.util.Locale;

import com.beanclasses.MaterialListBean;
import com.stavigilmonitoring.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SelectDeliveryListAdapter extends BaseAdapter {

	//private ArrayList<MaterialReqAckBean> list;
	private Context parent;
	
	private ArrayList<MaterialListBean> list;

	private static ArrayList<MaterialListBean> arraylist;

	private LayoutInflater mInflater;
	//Context context;
	
	public  SelectDeliveryListAdapter(Context c,ArrayList<MaterialListBean> searchResults)
	{
		parent=c;
		this.list = searchResults;
		arraylist = new ArrayList<MaterialListBean>();
		arraylist.addAll(searchResults);

		mInflater = LayoutInflater.from(parent);
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.material_list, null);
			holder = new ViewHolder();
			
          	holder.txtmaterialName = (TextView) convertView
					.findViewById(R.id.txtmaterialName);
          
			convertView.setTag(holder);
		} 
		else 
		{
			holder = (ViewHolder) convertView.getTag();
		}

		String s = list.get(position).getItemdesc();

		if (s.equals("")) {
			holder.txtmaterialName.setText("No Station Found..");
			

		} else {

			
			holder.txtmaterialName.setText(list.get(position).getItemdesc());

		}

		return convertView;
		
	}
	static class ViewHolder {
		
		
		TextView txtmaterialName;	
		
		
	}
	public void filter(String charText) {

		charText = charText.toLowerCase(Locale.getDefault());
		list.clear();
		if (charText.length() == 0) {
			list.addAll(arraylist);
		} else {
			for (MaterialListBean wp : arraylist) {
				if (wp.getItemdesc().toLowerCase(Locale.getDefault())
						.contains(charText)) {
					list.add(wp);
				}
			}
		}
		notifyDataSetChanged();

	}

}

package com.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.StationInventoryItemBean;

public class StationInventoryItemListAdapter extends BaseAdapter {
	private ArrayList<StationInventoryItemBean> list;
	private Context parent;
	private LayoutInflater mInflater;
	private ArrayList<StationInventoryItemBean> arraylist;

	public StationInventoryItemListAdapter(Context parent,
			ArrayList<StationInventoryItemBean> StationInventoryItemBeanlist) {
		this.parent = parent;
		this.list = StationInventoryItemBeanlist;
		arraylist = new ArrayList<StationInventoryItemBean>();
		arraylist.addAll(StationInventoryItemBeanlist);

		mInflater = LayoutInflater.from(parent);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.inventinvtrylistitem,	null);
			holder = new ViewHolder();

			holder.itemName = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.ItemName);
			holder.addedDT = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.AddedBy);
			holder.serialNo = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.SrNo);
			holder.addedBy = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.Addeddt);
			holder.reMark = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.ReMark);

			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
			holder.itemName.setText(list.get(position).getItemname());
			//holder.installationId.setText(list.get(position).getInstallationId());
			//holder.inventoryId.setText(list.get(position).getInventoryId());
			holder.addedDT.setText(list.get(position).getAddedDt());
			holder.serialNo.setText(list.get(position).getSrNo());			
			holder.addedBy.setText(list.get(position).getAddedBy());
			holder.reMark.setText(list.get(position).getReMark());			
		
		return convertView;
	}
		
		static class ViewHolder {
			public TextView addedBy;
			public TextView serialNo;
			public TextView addedDT;
			public TextView reMark;
			public TextView itemName;
		}
		
		public void filter(String charText) {
			
			charText = charText.toLowerCase(Locale.getDefault());
			list.clear();
				if(charText.length()==0)
				{
					list.addAll(arraylist);
				}
				else
				{
					for (StationInventoryItemBean wp : arraylist) 
					{
						if (wp.getItemname().toLowerCase(Locale.getDefault()).contains(charText)) 
						{
							list.add(wp);
						}
					}
				}
				notifyDataSetChanged();
				
			}

}

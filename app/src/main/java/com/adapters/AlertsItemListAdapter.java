package com.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.AlertsItemBean;

public class AlertsItemListAdapter extends BaseAdapter {
	private ArrayList<AlertsItemBean> list;
	private Context parent;
	private LayoutInflater mInflater;
	private ArrayList<AlertsItemBean> arraylist;

	public AlertsItemListAdapter(Context parent,
			ArrayList<AlertsItemBean> alertsItemBeanlist) {
		this.parent = parent;
		this.list = alertsItemBeanlist;
		arraylist = new ArrayList<AlertsItemBean>();
		arraylist.addAll(alertsItemBeanlist);

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
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.alertlistitem,	null);
			holder = new ViewHolder();

			holder.AlertId = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.AlertId);
			holder.addedDT = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.AddedDt);
			holder.AlertDesc = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.StationName);

			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
			holder.AlertId.setText(list.get(position).getAlertId());
			//holder.installationId.setText(list.get(position).getInstallationId());
			//holder.inventoryId.setText(list.get(position).getInventoryId());
			holder.addedDT.setText(list.get(position).getAddedDt());
			holder.AlertDesc.setText(list.get(position).getAlertDesc());	
		
		return convertView;
	}
		
		static class ViewHolder {
			public TextView AlertId;
			public TextView AlertDesc;
			public TextView addedDT;
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
					for (AlertsItemBean wp : arraylist) 
					{
						if (wp.getAlertDesc().toLowerCase(Locale.getDefault()).contains(charText)) 
						{
							list.add(wp);
						}
					}
				}
				notifyDataSetChanged();
				
			}

}

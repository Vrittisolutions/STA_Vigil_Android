package com.adapters;

import java.util.ArrayList;
import java.util.Locale;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.StateList;

public class AlrtStnAdap extends BaseAdapter{


	private ArrayList<StateList> list;
	private Context parent;
	private LayoutInflater mInflater;
	private ArrayList<StateList> arraylist;

	public AlrtStnAdap(Context parent,
			ArrayList<StateList> soundlevelBeanslist) {
		this.parent = parent;
		this.list = soundlevelBeanslist;
		arraylist = new ArrayList<StateList>();
		arraylist.addAll(soundlevelBeanslist);
//		arraylist=new ArrayList<com.vstaproject.TvStatusMain.StateList>();
//		arraylist.addAll(searchResults);
		mInflater = LayoutInflater.from(parent);
	}

	@Override
	public int getCount() {
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
	public View getView(final int position, View convertView,
			ViewGroup viewGroup) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.alrtconnectionstatusupdatedmain, null);
			holder = new ViewHolder();			
			holder.installationid = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvinstallationidmain);
			holder.time = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvservertimemain);
			holder.alrtaddedby = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvaddedby);
			holder.addSupName = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvaddSupName);
			holder.tvAlertDesc = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvAlertDesc);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.installationid.setText(list.get(position).getStatioName());
		holder.time.setText(list.get(position).getDiff());
		Log.e("Alrt ka time",list.get(position).getDiff().toString());
		Log.e("Alrt ka naam",list.get(position).getAlrtAddedByName().toString());
		holder.alrtaddedby.setText("Alert by :"+"   "+list.get(position).getAlrtAddedByName());
		Log.e("Sprt ka naam",list.get(position).getStnSupName().toString());
		holder.addSupName.setText(list.get(position).getStnSupName());
		holder.tvAlertDesc.setText("Issue:"+   "   "+list.get(position).getAlertDesc());

		return convertView;
	}

	static class ViewHolder {
		TextView installationid, time, alrtaddedby, addSupName, tvAlertDesc ;

	}

public void filter(String charText) {
		
//	charText = charText.toLowerCase(Locale.getDefault());
	list.clear();
		if(charText.length()==0)
		{
			list.addAll(arraylist);
		}
		else
		{
			for (StateList wp : arraylist) 
			{
				if (wp.getStatioName().toLowerCase(Locale.getDefault()).startsWith(charText)) 
				{
					list.add(wp);
				}
			}
		}
		notifyDataSetChanged();
	}
}

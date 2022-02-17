package com.adapters;

import java.util.ArrayList;
import java.util.Locale;

import com.beanclasses.SoundlevelBean;
import com.stavigilmonitoring.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SoundlevelAdapter extends BaseAdapter {
	private ArrayList<SoundlevelBean> list;
	private Context parent;
	private LayoutInflater mInflater;
	private ArrayList<SoundlevelBean> arraylist;

	public SoundlevelAdapter(Context parent,
			ArrayList<SoundlevelBean> soundlevelBeanslist) {
		this.parent = parent;
		this.list = soundlevelBeanslist;
		arraylist = new ArrayList<SoundlevelBean>();
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
			convertView = mInflater.inflate(R.layout.soundlevemainitem, null);
			holder = new ViewHolder();

			holder.textstationname = (TextView) convertView
					.findViewById(R.id.tvstnnamedownmain);
			holder.txtcalibrationdate = (TextView) convertView
					.findViewById(R.id.txtlastcalibrationtime);
			holder.txtcalibrationvalue = (TextView) convertView
					.findViewById(R.id.txtlastfivecalibration);
			holder.txtconnectiondate = (TextView) convertView
					.findViewById(R.id.txtlastconnectiondatetime);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.textstationname.setText(list.get(position).getStationname());
		holder.txtconnectiondate.setText(list.get(position)
				.getLastconnectiontime());
		holder.txtcalibrationdate.setText(list.get(position)
				.getLastcalibrationtime());
		holder.txtcalibrationvalue.setText(list.get(position)
				.getLast5calibrationvalue());

		return convertView;
	}

	static class ViewHolder {
		TextView textstationname, txtcalibrationdate, txtcalibrationvalue,
				txtconnectiondate;

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
			for (SoundlevelBean wp : arraylist) 
			{
				if (wp.getStationname().toLowerCase(Locale.getDefault()).contains(charText)) 
				{
					list.add(wp);
				}
			}
		}
		notifyDataSetChanged();
		
	}


}

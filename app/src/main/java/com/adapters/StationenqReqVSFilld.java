package com.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.Material;
import com.stavigilmonitoring.StationEnquiryMaterialHiscom;

public class StationenqReqVSFilld extends BaseAdapter {

	private ArrayList<Material> list;
	private Context parent;
	private LayoutInflater mInflater;
	private ArrayList<Material> arraylist;

	public StationenqReqVSFilld(Context parent,
			ArrayList<Material> soundlevelBeanslist) {
		this.parent = parent;
		this.list = soundlevelBeanslist;
		arraylist = new ArrayList<Material>();
		arraylist.addAll(soundlevelBeanslist);
		// arraylist=new ArrayList<com.vstaproject.TvStatusMain.StateList>();
		// arraylist.addAll(searchResults);
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
			convertView = mInflater
					.inflate(com.stavigilmonitoring.R.layout.stationenquiymatitem, null);
			holder = new ViewHolder();

			holder.rcvDate = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.reqdate);//reqdate
			holder.rcvmatname = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.reqmaterial);
			holder.rcvTOname = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.requsedname);
			holder.rcvtomob = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.rcvmob);
			holder.fillDate = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.filldate);
			holder.fillmat = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.filldmaterial);
			holder.fillmob = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.fillmob);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.rcvDate.setText(list.get(position).getMaterialreceived());

		holder.rcvmatname.setText(list.get(position).getReceivername());

		holder.rcvTOname.setText(list.get(position).getReceiverdate());

		holder.rcvtomob.setText(list.get(position).getReceivedmob());

		holder.fillDate.setText(list.get(position).getCurrentdate());

		holder.fillmat.setText(list.get(position).getFilldmaterial());

		holder.fillmob.setText(list.get(position).getFillmob());
		notifyDataSetChanged();

		return convertView;
	}

	static class ViewHolder {
		TextView rcvDate;
		TextView rcvmatname;
		TextView rcvTOname;
		TextView rcvtomob;
		TextView fillDate;
		TextView fillmat;
		TextView fillmob;

	}

}

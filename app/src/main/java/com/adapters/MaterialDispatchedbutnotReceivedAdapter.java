package com.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.MaterialDispatchedbutnotReceivedBean;
import com.stavigilmonitoring.R;


public class MaterialDispatchedbutnotReceivedAdapter extends BaseAdapter {
	private ArrayList<MaterialDispatchedbutnotReceivedBean> list;
	private Context parent;
	private LayoutInflater mInflater;
	private ArrayList<MaterialDispatchedbutnotReceivedBean> arraylist;

	public MaterialDispatchedbutnotReceivedAdapter(Context parent,
			ArrayList<MaterialDispatchedbutnotReceivedBean> materialPendingBeanslist) {
		this.parent = parent;
		this.list = materialPendingBeanslist;
		arraylist = new ArrayList<MaterialDispatchedbutnotReceivedBean>();
		arraylist.addAll(materialPendingBeanslist);

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
			convertView = mInflater.inflate(R.layout.custom_material_disp_not_rcvd,
					null);
			holder = new ViewHolder();

			holder.textname = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtname);
			holder.txtstation = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtstation);
			holder.txtreqby = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtreqby);
			holder.txtnetwrk = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtnetwrk);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		/*holder.textname.setText(list.get(position).getMaterialname() + " for "
				+ list.get(position).getStationname() +" station : " + "Request from "+
				list.get(position).getMatreqfrom()+" - "+list.get(position).getNetworkCode());*/

		holder.textname.setText(list.get(position).getMaterialname());
		holder.txtstation.setText(list.get(position).getStationname());
		holder.txtreqby.setText(list.get(position).getMatreqfrom());
		holder.txtnetwrk.setText(list.get(position).getNetworkCode());

		return convertView;
	}

	static class ViewHolder {
		TextView textname, txtstation,txtreqby, txtnetwrk;

	}

	public void filter(String charText) {

		charText = charText.toLowerCase(Locale.getDefault());
		list.clear();
		if (charText.length() == 0) {
			list.addAll(arraylist);
		} else {
			for (MaterialDispatchedbutnotReceivedBean wp : arraylist) {
				if (wp.getStationname().toLowerCase(Locale.getDefault()).contains(charText) ||
						wp.getMaterialname().toLowerCase(Locale.getDefault()).contains(charText) ||
						wp.getMatreqfrom().toLowerCase(Locale.getDefault()).contains(charText) ||
						wp.getNetworkCode().toLowerCase(Locale.getDefault()).contains(charText)) {
					list.add(wp);
				}
			}
		}
		notifyDataSetChanged();

	}

}


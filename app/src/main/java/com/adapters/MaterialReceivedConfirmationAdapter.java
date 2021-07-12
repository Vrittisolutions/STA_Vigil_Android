package com.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.MaterialReceivedConfirmationBean;


public class MaterialReceivedConfirmationAdapter  extends BaseAdapter {
	private ArrayList<MaterialReceivedConfirmationBean> list;
	private Context parent;
	private LayoutInflater mInflater;
	private ArrayList<MaterialReceivedConfirmationBean> arraylist;

	public MaterialReceivedConfirmationAdapter(Context parent,
			ArrayList<MaterialReceivedConfirmationBean> materialPendingBeanslist) {
		this.parent = parent;
		this.list = materialPendingBeanslist;
		arraylist = new ArrayList<MaterialReceivedConfirmationBean>();
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
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.custom_material_delivery,
					null);
			holder = new ViewHolder();

			holder.textname = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtname);
			holder.txtforstatn = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtforstatn);
			holder.txtaddeddate = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtaddeddate);
			holder.txtaddeddate.setVisibility(View.VISIBLE);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		//holder.textname.setText(list.get(position).getMaterialname() + " sent by "	+ list.get(position).getReporteename());
		holder.textname.setText(list.get(position).getMaterialname());
		holder.txtforstatn.setText(list.get(position).getStationname());
		holder.txtaddeddate.setText(list.get(position).getDate());

		return convertView;
	}

	static class ViewHolder {
		TextView textname, txtforstatn, txtaddeddate;

	}

	public void filter(String charText) {

		charText = charText.toLowerCase(Locale.getDefault());
		list.clear();
		if (charText.length() == 0) {
			list.addAll(arraylist);
		} else {
			for (MaterialReceivedConfirmationBean wp : arraylist) {
				if (wp.getMaterialname().toLowerCase(Locale.getDefault()).contains(charText) ) {
					list.add(wp);
				}
			}
		}
		notifyDataSetChanged();

	}

}

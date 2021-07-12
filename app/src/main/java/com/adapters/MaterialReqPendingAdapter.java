package com.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.MaterialReqAckBean;

public class MaterialReqPendingAdapter extends BaseAdapter {
	private ArrayList<MaterialReqAckBean> list;
	private Context parent;
	private LayoutInflater mInflater;
	private ArrayList<MaterialReqAckBean> arraylist;

	public MaterialReqPendingAdapter(Context parent,
			ArrayList<MaterialReqAckBean> materialPendingBeanslist) {
		this.parent = parent;
		this.list = materialPendingBeanslist;
		arraylist = new ArrayList<MaterialReqAckBean>();
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

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.textname.setText(list.get(position).getMaterialname() + " for "
				+ list.get(position).getStationname());

		return convertView;
	}

	static class ViewHolder {
		TextView textname;

	}

	public void filter(String charText) {

		charText = charText.toLowerCase(Locale.getDefault());
		list.clear();
		if (charText.length() == 0) {
			list.addAll(arraylist);
		} else {
			for (MaterialReqAckBean wp : arraylist) {
				if (wp.getStationname().toLowerCase(Locale.getDefault())
						.contains(charText)) {
					list.add(wp);
				}
			}
		}
		notifyDataSetChanged();

	}

}

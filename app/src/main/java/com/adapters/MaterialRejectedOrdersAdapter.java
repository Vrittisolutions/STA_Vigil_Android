package com.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.MaterialRejectedOrdersBean;

public class MaterialRejectedOrdersAdapter extends BaseAdapter {
	private ArrayList<MaterialRejectedOrdersBean> list;
	private Context parent;
	private LayoutInflater mInflater;
	private ArrayList<MaterialRejectedOrdersBean> arraylist;

	public MaterialRejectedOrdersAdapter(Context parent,
			ArrayList<MaterialRejectedOrdersBean> materialPendingBeanslist) {
		this.parent = parent;
		this.list = materialPendingBeanslist;
		arraylist = new ArrayList<MaterialRejectedOrdersBean>();
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
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.custom_material_rejected,
					null);
			holder = new ViewHolder();

			holder.textname = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtname_rejected);
			holder.txtforstn = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtforstn);
			holder.txtrejby = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtrejby);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		/*holder.textname.setText(list.get(position).getMaterialName() + " for "
				+ list.get(position).getStationName() + " rejected by "+list.get(position).getReporteeName());*/

		holder.textname.setText(list.get(position).getMaterialName());
		holder.txtforstn.setText(list.get(position).getStationName());
		holder.txtrejby.setText(list.get(position).getReporteeName());

		return convertView;
	}

	static class ViewHolder {
		TextView textname,txtforstn,txtrejby;

	}

	public void filter(String charText) {

		charText = charText.toLowerCase(Locale.getDefault());
		list.clear();
		if (charText.length() == 0) {
			list.addAll(arraylist);
		} else {
			for (MaterialRejectedOrdersBean wp : arraylist) {
				if (wp.getStationName().toLowerCase(Locale.getDefault()).contains(charText) ||
						wp.getMaterialName().toLowerCase(Locale.getDefault()).contains(charText)) {
					list.add(wp);
				}
			}
		}
		notifyDataSetChanged();
	}
}

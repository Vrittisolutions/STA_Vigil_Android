package com.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.MaterialReasons;
import com.stavigilmonitoring.MaterialReason;
import com.stavigilmonitoring.R;

public class MaterialReasonAdapter extends BaseAdapter {
	private ArrayList<MaterialReasons> list;
	private Context parent;
	private LayoutInflater mInflater;
	private ArrayList<MaterialReasons> arraylist;

	public MaterialReasonAdapter(Context parent,
			ArrayList<MaterialReasons> soundlevelBeanslist) {
		this.parent = parent;
		this.list = soundlevelBeanslist;
		arraylist = new ArrayList<MaterialReasons>();
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
			convertView = mInflater.inflate(R.layout.custom_materialreason, null);
			holder = new ViewHolder();

			holder.textstationname = (TextView) convertView
					.findViewById(R.id.txtmaterialreason);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.textstationname.setText(list.get(position).getMaterialReason());

		return convertView;
	}

	static class ViewHolder {
		TextView textstationname;

	}

	public void filter(String charText) {

		// charText = charText.toLowerCase(Locale.getDefault());
		list.clear();
		if (charText.length() == 0) {
			list.addAll(arraylist);
		} else {
			for (MaterialReasons wp : arraylist) {
				if (wp.getMaterialReason().toLowerCase(Locale.getDefault())
						.contains(charText)) {
					list.add(wp);
				}
			}
		}
		notifyDataSetChanged();

	}

}

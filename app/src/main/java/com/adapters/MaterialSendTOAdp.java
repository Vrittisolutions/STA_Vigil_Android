package com.adapters;

import java.util.List;

import com.beanclasses.reporteeBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MaterialSendTOAdp extends BaseAdapter {
	private static List<reporteeBean> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public MaterialSendTOAdp(Context c, List<reporteeBean> searchResults) {
		context = c;
		mInflater = LayoutInflater.from(context);
		searchArrayList = searchResults;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return searchArrayList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return searchArrayList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.reporteeitem, null);
			holder = new ViewHolder();
			holder.tvsName = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvsName);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvsName.setText(searchArrayList.get(position).getReporteeName());

		return convertView;
	}

	static class ViewHolder {
		TextView tvsName;

	}

}

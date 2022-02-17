package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanclasses.MaterialReqAckBean;
import com.stavigilmonitoring.R;

import java.util.ArrayList;
import java.util.Locale;

public class DispMaterialAdapter extends BaseAdapter {
	private ArrayList<String> list;
	private Context parent;
	private LayoutInflater mInflater;
	private ArrayList<String> arraylist;

	public DispMaterialAdapter(Context parent, ArrayList<String> materialPendingBeanslist) {
		this.parent = parent;
		this.list = materialPendingBeanslist;
		arraylist = new ArrayList<String>();
		arraylist.addAll(materialPendingBeanslist);

		mInflater = LayoutInflater.from(parent);
	}

	@Override
	public int getCount() {
		return 2/*list.size()*/;
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
			convertView = mInflater.inflate(R.layout.material_dispatch, null);
			holder = new ViewHolder();
			holder.txtmaterial = convertView.findViewById(com.stavigilmonitoring.R.id.txtmaterial);
			holder.txtstation = convertView.findViewById(com.stavigilmonitoring.R.id.txtstation);
			holder.imgdispatch = convertView.findViewById(com.stavigilmonitoring.R.id.imgdispatch);
			holder.imgdispatch.setAlpha((float) 0.3);
			holder.imgack = convertView.findViewById(com.stavigilmonitoring.R.id.imgack);
			holder.imgack.setAlpha((float) 0.3);
			holder.imgrepair = convertView.findViewById(com.stavigilmonitoring.R.id.imgrepair);
			holder.imgrepair.setAlpha((float) 0.3);
			holder.view1 = convertView.findViewById(com.stavigilmonitoring.R.id.view1);
			holder.view1.setAlpha((float) 0.3);
			holder.view2 = convertView.findViewById(com.stavigilmonitoring.R.id.view2);
			holder.view2.setAlpha((float) 0.3);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		return convertView;
	}

	static class ViewHolder {
		TextView txtmaterial,txtstation;
		ImageView imgdispatch,imgack,imgrepair;
		View view1, view2;
	}

	/*public void filter(String charText) {

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
	}*/

}

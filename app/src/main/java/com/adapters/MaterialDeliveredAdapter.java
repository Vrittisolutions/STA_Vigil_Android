package com.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beanclasses.MaterialDeliveredBean;
import com.stavigilmonitoring.R;

public class MaterialDeliveredAdapter extends BaseAdapter {
	private ArrayList<MaterialDeliveredBean> list;
	private Context parent;
	private LayoutInflater mInflater;
	private ArrayList<MaterialDeliveredBean> arraylist;

	public MaterialDeliveredAdapter(Context parent,
			ArrayList<MaterialDeliveredBean> materialDeliveredBeanslist) {
		this.parent = parent;
		this.list = materialDeliveredBeanslist;
		arraylist = new ArrayList<MaterialDeliveredBean>();
		arraylist.addAll(materialDeliveredBeanslist);

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
			convertView = mInflater.inflate(R.layout.material_pending_dispatches,
					null);
			holder = new ViewHolder();

			holder.textname = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtname);
			holder.txtforstn = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtforstn);
			holder.txtbysupporter = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtbysupporter);
			holder.cv = convertView.findViewById(R.id.cv);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		/*holder.textname.setText(list.get(position).getMaterialname() + " for "
				+ list.get(position).getStationname() + " by "+ list.get(position).getAddedby());*/

		holder.textname.setText(list.get(position).getMaterialname());
		holder.txtforstn.setText(list.get(position).getStationname());
		holder.txtbysupporter.setText(list.get(position).getAddedby());

		if(list.get(position).getApproveCategory().equalsIgnoreCase("Repair")){
			holder.cv.setBackgroundColor(Color.parseColor("#e5c986"));	//brown
		}else if(list.get(position).getApproveCategory().equalsIgnoreCase("New")){
			holder.cv.setBackgroundColor(Color.parseColor("#a7e8e6"));	//blue
		}else if(list.get(position).getApproveCategory().equalsIgnoreCase("0")) {
			holder.cv.setBackgroundColor(Color.parseColor("#FFFFFF"));
		}

		return convertView;
	}

	static class ViewHolder {
		TextView textname, txtforstn,txtbysupporter;
		LinearLayout cv;

	}

	public void filter(String charText) {

		charText = charText.toLowerCase(Locale.getDefault());
		list.clear();
		if (charText.length() == 0) {
			list.addAll(arraylist);
		} else {
			for (MaterialDeliveredBean wp : arraylist) {
				if (wp.getMaterialname().toLowerCase(Locale.getDefault()).contains(charText) ||
						wp.getStationname().toLowerCase(Locale.getDefault()).contains(charText) ||
						wp.getAddedby().toLowerCase(Locale.getDefault()).contains(charText)) {
					list.add(wp);
				}
			}
		}
		notifyDataSetChanged();

	}

}

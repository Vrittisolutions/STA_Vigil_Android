package com.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.WorkTypeselect;
import com.stavigilmonitoring.WorkDoneTypeselect;

public class WorkDoneTypeselectAdp extends BaseAdapter {

	private ArrayList<WorkTypeselect> list;
	private Context parent;
	private LayoutInflater mInflater;
	private ArrayList<WorkTypeselect> arraylist;

	public WorkDoneTypeselectAdp(Context parent,
			ArrayList<WorkTypeselect> soundlevelBeanslist) {
		this.parent = parent;
		this.list = soundlevelBeanslist;
		arraylist = new ArrayList<WorkTypeselect>();
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
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.materialitem, null);
			holder = new ViewHolder();

			holder.textstationname = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvstnnamedownmain);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.textstationname.setText(list.get(position).getWorktype());
		

		return convertView;
	}

	static class ViewHolder {
		TextView textstationname;

	}

}

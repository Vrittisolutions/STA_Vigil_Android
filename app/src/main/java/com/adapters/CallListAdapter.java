package com.adapters;

import java.util.List;

import com.stavigilmonitoring.R;
import com.beanclasses.StationCall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CallListAdapter extends BaseAdapter {
	List<StationCall> list;
	Context con;
	LayoutInflater mInflater;
	public CallListAdapter(Context c,List<StationCall> list_call)
	{
		con=c;
		list=list_call;
		mInflater = LayoutInflater.from(con);
	}

	@Override
	public int getCount() {		
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {		
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder view;
		if(convertView == null)
		{
			convertView = mInflater.inflate(R.layout.call, null);
			view=new ViewHolder();
			view.tvname=(TextView)convertView.findViewById(R.id.tvcallname);
			view.tvnumber=(TextView)convertView.findViewById(R.id.tvcallno);
			
			convertView.setTag(view);
			
		}
		else
		{
			view=(ViewHolder)convertView.getTag();
		}
		view.tvname.setText(list.get(position).getName());
		view.tvnumber.setText(list.get(position).getnumber());
		
		return convertView;
	}

	private class ViewHolder
	{
		TextView tvname;
		TextView tvnumber;
	}
}

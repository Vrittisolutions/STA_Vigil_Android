package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.StateList;
import com.stavigilmonitoring.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class Notify_StationList extends BaseAdapter{

	private ArrayList<StateList> list;
	private Context parent;
	private LayoutInflater mInflater;
	private ArrayList<StateList> arraylist;

	public Notify_StationList(Context parent,
                              ArrayList<StateList> soundlevelBeanslist) {
		this.parent = parent;
		this.list = soundlevelBeanslist;
		arraylist = new ArrayList<StateList>();
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
			convertView = mInflater.inflate(R.layout.notifictnstn, null);
			holder = new ViewHolder();

			holder.textstationname = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvstnnamedownmain);
			holder.tvnotfctncnt = (TextView)convertView.findViewById(R.id.tvnotfctncnt);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.textstationname.setText(list.get(position).getStatioName());
		holder.tvnotfctncnt.setText(list.get(position).getNotificationCnt());

		return convertView;
	}

	static class ViewHolder {
		TextView textstationname, tvnotfctncnt;
	}

public void filter(String charText) {
		
//	charText = charText.toLowerCase(Locale.getDefault());
	list.clear();
		if(charText.length()==0)
		{
			list.addAll(arraylist);
		}
		else
		{
			for (StateList wp : arraylist) 
			{
				if (wp.getStatioName().toLowerCase(Locale.getDefault()).startsWith(charText)) 
				{
					list.add(wp);
				}
			}
		}
		notifyDataSetChanged();
	}
}

package com.adapters;

import java.util.List;

import com.beanclasses.SoundLevelBeanSort;
import com.stavigilmonitoring.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SoundLevelAdapt extends BaseAdapter {

	private static List<SoundLevelBeanSort> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public SoundLevelAdapt(Context context1, List<SoundLevelBeanSort> results) {
		searchArrayList = results;
		mInflater = LayoutInflater.from(context1);
		context = context1;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return searchArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return searchArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.soundleveldupdated, null);
			holder = new ViewHolder();

			holder.ScheduleDate = (TextView) convertView
					.findViewById(R.id.tvsounddate);
			holder.ScheduleTime = (TextView) convertView
					.findViewById(R.id.txtsoundtime);
			//

			// holder.AO = (TextView) convertView
			// .findViewById(R.id.tvsoundaudio);
			holder.Standard = (TextView) convertView
					.findViewById(R.id.tvsoundstd);
			holder.Actual = (TextView) convertView
					.findViewById(R.id.tvsoundactual);
			holder.Percentage = (TextView) convertView
					.findViewById(R.id.tvsoundpercentage);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getScheduleDate();

		if (!(searchArrayList.size() > 0)) {
			holder.ScheduleDate.setText("  No information available..");
			holder.ScheduleTime.setText("");
			holder.Standard.setText("");
			holder.Actual.setText("");
		} else {

			holder.ScheduleDate.setText(searchArrayList.get(position)
					.getScheduleDate());

			holder.ScheduleTime.setText(searchArrayList.get(position)
					.getScheduleTime());

			// holder.AO.setText(searchArrayList.get(position).getaudioOutput());
			holder.Standard
					.setText(searchArrayList.get(position).getStandard());
			holder.Actual.setText(searchArrayList.get(position).getActual());

			int perc = Integer.parseInt(searchArrayList.get(position)
					.getPercentage());
			// 100
			if (perc > 100) {
				// searchArrayList.get(position)
				// .getPercentage()
				holder.Percentage.setText(searchArrayList.get(position)
						.getPercentage());
				holder.Percentage.setTextColor(Color.BLUE);
			} else if (perc < 100 && perc > 0) {
				// 115
				holder.Percentage.setText(searchArrayList.get(position)
						.getPercentage());
				holder.Percentage.setTextColor(Color.RED);
			} else if (perc == 0) {
				holder.Percentage.setText(searchArrayList.get(position)
						.getPercentage());
				holder.Percentage.setTextColor(Color.MAGENTA);
			} else {
				holder.Percentage.setText(searchArrayList.get(position)
						.getPercentage());

			}
		}
		return convertView;
	}

	static class ViewHolder {

		TextView ScheduleDate;
		TextView ScheduleTime;
		// TextView AO;
		TextView Standard;
		TextView Actual;
		TextView Percentage;

	}

}

package com.adapters;

import java.util.ArrayList;

import com.beanclasses.ConnectionstatusHelper;
import com.stavigilmonitoring.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ConnectionstatusAdapt extends BaseAdapter {

	private static ArrayList<ConnectionstatusHelper> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public ConnectionstatusAdapt(Context context1,
			ArrayList<ConnectionstatusHelper> results) {
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
			convertView = mInflater.inflate(R.layout.connectionstatusupdated, null);
			holder = new ViewHolder();

			holder.servertime = (TextView) convertView
					.findViewById(R.id.tvservertime);

			holder.dateday = (TextView) convertView
					.findViewById(R.id.tvdatedaycsn);

			holder.starttime = (TextView) convertView
					.findViewById(R.id.tvstarttime);
			holder.endtime = (TextView) convertView
					.findViewById(R.id.tvworkspacewiseadaptertassigned);

			holder.qhstatus = (TextView) convertView
					.findViewById(R.id.tvqhstatus);
			holder.version = (TextView) convertView
					.findViewById(R.id.tvversion);
			holder.diff = (TextView) convertView.findViewById(R.id.tvtymdiff);
			holder.remarks = (TextView) convertView
					.findViewById(R.id.tvremarks);
			holder.Reason = (TextView) convertView
					.findViewById(R.id.tvcsnreason);
			// holder.personName = (TextView) convertView
			// .findViewById(R.id.txtcsncontactpersonname);
			// holder.personNumber = (TextView) convertView
			// .findViewById(R.id.txtcsncontactpersonnumber);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getinstallationId();

		if (s.equals("")) {
			holder.servertime.setText("No information available..");

		} else {

			holder.servertime.setText(searchArrayList.get(position)
					.getservertime());
			holder.dateday.setText(searchArrayList.get(position).getdateDay());
			holder.starttime.setText(searchArrayList.get(position)
					.getStartTime());

			Log.e("...cs......", "----------------------------------"
					+ searchArrayList.get(position).getStartTime());

			holder.endtime.setText(searchArrayList.get(position).getEndTime());

			holder.qhstatus.setText(searchArrayList.get(position).getStatus());
			String version = searchArrayList.get(position).getVersion()
					.toString();

			holder.version.setText(searchArrayList.get(position).getVersion());
			holder.diff.setText(searchArrayList.get(position).gettymdiff());
			holder.remarks.setText(searchArrayList.get(position).getRemarks());
			String s1 = searchArrayList.get(position).getreason();
			if (s1.equals("")) {
				holder.Reason.setText("No Reason Updated.");

			} else {
				holder.Reason
						.setText(searchArrayList.get(position).getreason());
			}
			// holder.personName.setText(searchArrayList.get(position)
			// .getpersonDetails());
			// holder.personNumber.setText(searchArrayList.get(position)
			// .getpersonnumber());
		}
		return convertView;
	}

	static class ViewHolder {

		TextView installationid;

		TextView servertime;
		TextView dateday;
		TextView starttime;
		TextView startend;
		TextView endtime;
		TextView remarks;
		TextView qhstatus;
		TextView version;
		TextView diff;
		TextView Reason;
		TextView personName;
		TextView personNumber;
	}

}

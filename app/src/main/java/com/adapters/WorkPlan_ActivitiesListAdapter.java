package com.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beanclasses.StateDetailsList;
import com.stavigilmonitoring.DmCStateStnSoNoDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkPlan_ActivitiesListAdapter extends BaseAdapter {
	private static List<StateDetailsList> searchArrayList;
	private ArrayList<StateDetailsList> arraylist;
	private LayoutInflater mInflater;
	Context context;

	public WorkPlan_ActivitiesListAdapter(Context c, List<StateDetailsList> data)
	{
		context=c;
		mInflater = LayoutInflater.from(context);
		searchArrayList=data;
		arraylist=new ArrayList<StateDetailsList>();
		arraylist.addAll(data);
	}


	public void filter_details(String charText) {

		searchArrayList.clear();
		if(charText.length()==0)
		{
			searchArrayList.addAll(arraylist);
		}
		else
		{
			for (StateDetailsList wp : arraylist)
			{
				if (wp.GetDMDesc().toLowerCase(Locale.getDefault()).contains(charText))
				{
					searchArrayList.add(wp);
				}
			}
		}
		notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		return searchArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return searchArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		//pos = position;
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Date EndDate = null, Todaydate = null, StartDate = null;
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdfdisplay = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat sdf3 = new SimpleDateFormat("dd MMM,yyyy");
		//pos= position;
		if (convertView == null) {
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.adapter_activities_wrkplan, null);
			holder = new ViewHolder();
			holder.evenodd = (LinearLayout) convertView.findViewById(com.stavigilmonitoring.R.id.row_layout);
			holder.colorll = (LinearLayout) convertView.findViewById(com.stavigilmonitoring.R.id.lay_PriorityIndex);
			holder.txtdmdesc = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tv_activity_desc);
			holder.txtstatus = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tv_workspace);
			holder.txtassignby = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tv_assignedBy);
			holder.txtstn = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tv_activityCode);
			holder.txtstartdt = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tv_endDate1);
			holder.txtenddt = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tv_ConsigneeName);
			holder.tv_endDate = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tv_endDate);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txtdmdesc.setText(searchArrayList.get(position).GetDMDesc());
		holder.txtstatus.setText(searchArrayList.get(position).Getdmcstatus());
		holder.txtassignby.setText("By "+ searchArrayList.get(position).GetGenrateFileName());
		//boolean overduechk = checkOverdue(searchArrayList.get(position).GetActualEndDate());
		if ((position%2)==0){
			holder.evenodd.setBackgroundColor(Color.parseColor("#B2DDFF"));
		}else {
			holder.evenodd.setBackgroundColor(Color.parseColor("#CCE8FF"));
		}
		//holder.txtstartdt.setText(""+searchArrayList.get(position).GetActualStartDate());
		holder.txtenddt.setText("-  "+searchArrayList.get(position).GetActualEndDate());

		//holder.txtstn.setText(searchArrayList.get(position).GetInstallationIdForStateDetailsList()+" ");
		holder.txtstn.setText(searchArrayList.get(position).getStationName()+" ");

		String jsonEDate = searchArrayList.get(position).GetActualEndDate();
		String jsonSDate = searchArrayList.get(position).GetActualStartDate();
		String endDate = "", todayDate, startDate = "",endDatedisplay = "",startDatedisplay = "",boxstartDate = "";
		try {
			EndDate = sdf3.parse(jsonEDate);
			Todaydate = new Date();

			todayDate = sdf.format(Todaydate);
			endDate = sdf.format(EndDate);
			endDatedisplay = sdfdisplay.format(EndDate);
			EndDate = sdf.parse(endDate);
			Todaydate = sdf.parse(todayDate);

			StartDate = sdf3.parse(jsonSDate);
			boxstartDate = sdf1.format(StartDate);
			startDate = sdf.format(StartDate);
			startDatedisplay = sdfdisplay.format(StartDate);
			StartDate = sdf.parse(startDate);
			System.out.println("Result Date: " + endDate);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		holder.txtstartdt.setText(boxstartDate);

		if (EndDate.before(Todaydate)) {
			holder.tv_endDate.setText("Delayed");
			holder.tv_endDate.setTextColor(Color.parseColor("#F05050"));
			holder.colorll.setBackgroundColor(Color.parseColor("#F05050"));
		} else if (StartDate.equals(Todaydate) && EndDate.equals(Todaydate)) {
			holder.tv_endDate.setText("Ends");
			holder.tv_endDate.setTextColor(Color.parseColor("#27C24C"));
			holder.colorll.setBackgroundColor(Color.parseColor("#27C24C"));
		} else if (StartDate.before(Todaydate) && EndDate.equals(Todaydate)) {
			holder.tv_endDate.setText("Ends on Today");
			holder.tv_endDate.setTextColor(Color.parseColor("#27C24C"));
			holder.colorll.setBackgroundColor(Color.parseColor("#27C24C"));
		} else if (StartDate.before(Todaydate) && EndDate.after(Todaydate)) {
			holder.tv_endDate.setText("Ends on " + endDatedisplay);
			holder.tv_endDate.setTextColor(Color.parseColor("#27C24C"));
			holder.colorll.setBackgroundColor(Color.parseColor("#27C24C"));
		}else if (StartDate.equals(Todaydate) && EndDate.after(Todaydate)) {
			holder.tv_endDate.setText("Ends on " + endDatedisplay);
			holder.tv_endDate.setTextColor(Color.parseColor("#27C24C"));
			holder.colorll.setBackgroundColor(Color.parseColor("#27C24C"));
		}else if (StartDate.after(Todaydate) && EndDate.after(Todaydate)) {
			holder.tv_endDate.setText("Starts on " +startDatedisplay);
			holder.tv_endDate.setTextColor(Color.parseColor("#FF902B"));
			holder.colorll.setBackgroundColor(Color.parseColor("#FF902B"));
		}

		return convertView;
	}
	static class ViewHolder {
		TextView txtdmdesc,txtstatus, txtassignby, txtstn;
		TextView txtstartdt, txtenddt, tv_endDate;
		LinearLayout colorll, evenodd;
	}
	private boolean checkOverdue(String amcExpireDt) {
		String[] parts = amcExpireDt.split("T");
		amcExpireDt = parts[0];
		boolean result = false;
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM,yyyy");
		try {
			Date date2 = dateFormat2.parse(amcExpireDt);
			Date date = new Date();
			String res = dateFormat2.format(date);
			date = dateFormat2.parse(res);
			if (date2.equals(date)){
				result = false;
			} else if (date.after(date2)){
				result = true;
			} else if (date2.after(date)){
				result = false;
			}
		}catch( Exception e){
			e.printStackTrace();
		}
		return result;
	}
}



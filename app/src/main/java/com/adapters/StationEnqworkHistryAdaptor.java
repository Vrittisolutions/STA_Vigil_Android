package com.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanclasses.StationEnquiryworkBean;
import com.stavigilmonitoring.StationEnquiryWorkHistory;

public class StationEnqworkHistryAdaptor extends BaseAdapter {


	private static ArrayList<StationEnquiryworkBean> searchArrayList;

	private LayoutInflater mInflater;
	Context context;
	String Actualremark, fileUrl , Status="NA";
	private ArrayList<StationEnquiryworkBean> arraylist;
	public StationEnqworkHistryAdaptor(Context c,ArrayList<StationEnquiryworkBean> searchResults)
	{
		context=c;
		mInflater = LayoutInflater.from(context);
		searchArrayList=searchResults;
		arraylist=new ArrayList<StationEnquiryworkBean>();
		arraylist.addAll(searchResults);
		Status="NA";
		
	}


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
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
		return position;
	}
public void filter(String charText) {
		
		//charText =charText.toLowerCase(Locale.getDefault());
		searchArrayList.clear();
		if(charText.length()==0)
		{
			searchArrayList.addAll(arraylist);
		}
		else
		{
			for (StationEnquiryworkBean wp : arraylist) 
			{
				if (wp.getStationName().toLowerCase(Locale.getDefault()).contains(charText)) 
				{
					searchArrayList.add(wp); 
				}
			}
		}
		notifyDataSetChanged();
		
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		Status = searchArrayList.get(position).getFile_url();
		if (convertView == null) {
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.workhistoryitem, null);
			holder = new ViewHolder();
			holder.Activity = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.timeshtdisplay);
			holder.worktype = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.workdisplay);
			holder.workrem = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.wkremdisplay);
			holder.material = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.matedisplay);//reporteedisplay
			holder.matremark = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.matremDisplay);
			holder.date = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.dateDisplay);
			holder.mobile = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.mobDisplay);
			holder.location = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.LocationDispaly);
			if(!Status.equalsIgnoreCase("NA")) {
				holder.img = (ImageView) convertView.findViewById(com.stavigilmonitoring.R.id.imgavailable);
			}
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.Activity.setText(searchArrayList.get(position).getTimesheetActivity());
		holder.worktype.setText(searchArrayList.get(position).getWorktype()+"");
		if(Status.equalsIgnoreCase("NA")){
			holder.workrem.setText(searchArrayList.get(position).getWorkRemark());

		}else {
			String mob = searchArrayList.get(position).getWorkRemark();
			Actualremark = mob;
			String[] parts = mob.split("/");
			mob = parts[0];
			holder.workrem.setText(mob);
			holder.img.setVisibility(View.VISIBLE);
			holder.img.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					// TODO Auto-generated method stub
					//holder.img.getTag(position);
					//int pos=(Integer)v.getTag();
					fileUrl  = searchArrayList.get(position).getFile_url();
					final String url = "http://ktc.vritti.co/Attachments/"+fileUrl;
					if(context instanceof StationEnquiryWorkHistory){
						((StationEnquiryWorkHistory)context).toggle_icon_received_status(url);
					}
				}
			});
		}
		holder.material.setText(searchArrayList.get(position).getMaterialname());
		holder.matremark.setText(searchArrayList.get(position).getMatremark());
		holder.date.setText(searchArrayList.get(position).getDate());
		holder.mobile.setText(searchArrayList.get(position).getMobno());
		holder.location.setText(searchArrayList.get(position).getLocation());
		//holder.img.setTag(position);

		
		return convertView;
	}


	static class ViewHolder {
		TextView Activity,worktype,workrem,material,matremark,date,mobile,location;		
		TextView tvsStcnt;
		ImageView img;
	}


	public void showCustomDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LayoutInflater inf = LayoutInflater.from(context);
				//(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inf.inflate(com.stavigilmonitoring.R.layout.img_layout, null);
		builder.setView(view);
		final ImageView imageView  = (ImageView) view.findViewById(com.stavigilmonitoring.R.id.imagedisp);
		//final RadioButton radioFemale = (RadioButton) view.findViewById(R.id.gender_female);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				//Click listner
			}
		});
		builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				//click listner.
			}
		});
		builder.show();
	}

}

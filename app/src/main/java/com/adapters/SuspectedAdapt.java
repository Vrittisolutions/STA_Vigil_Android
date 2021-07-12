package com.adapters;

import java.util.ArrayList;


import com.stavigilmonitoring.R;
import com.beanclasses.SuspectedHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class SuspectedAdapt extends BaseAdapter {

	private static ArrayList<SuspectedHelper> searchArrayList;

	private LayoutInflater mInflater;
	Context context;

	public SuspectedAdapt(Context context1, ArrayList<SuspectedHelper> results) {
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
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.suspectedupdated, null);
			
			
			holder = new ViewHolder();
			
			holder.advcode = (TextView) convertView
					.findViewById(R.id.tvsusadvcode);
			
			holder.advname = (TextView) convertView
					.findViewById(R.id.tvsusadvname);

			holder.susdatefrom = (TextView) convertView
					.findViewById(R.id.tvsusdatefrom);
			holder.susdateto = (TextView) convertView
					.findViewById(R.id.tvsusdateto);
			
			
//			holder.repetitions = (TextView) convertView
//					.findViewById(R.id.tvsusrepeatition);
//			holder.actrepetitions = (TextView) convertView
//					.findViewById(R.id.tvsusrepet);
			holder.stsationspot = (TextView) convertView
					.findViewById(R.id.tvsusstationspot);
			holder.totalspot = (TextView) convertView
					.findViewById(R.id.tvsustotalspot);
			
			holder.spercentage = (TextView) convertView
					.findViewById(R.id.tvspotwiseper);
			holder.repetitions = (TextView) convertView
					.findViewById(R.id.tvsusrepeatition);
			holder.actrepetitions = (TextView) convertView
					.findViewById(R.id.tvsusrepet);
			holder.totalper = (TextView) convertView
					.findViewById(R.id.tvsusper);
//			holder.btnhistory = (Button) convertView
//					.findViewById(R.id.btnsushistory);
//			Button historysus=(Button)convertView.findViewById(R.id.btnsushistory);
//			historysus.setOnClickListener(new OnClickListener() {
//				
//							@Override
//							public void onClick(View v) {
//								// TODO Auto-generated method stub
//				
//								Toast.makeText(parent.getContext(), "button clicked: " , Toast.LENGTH_SHORT).show();
////								
////								Suspected sus=new Suspected();
////								Object o = historysus.getItemAtPosition(position);
////								ConnectionstatusHelper fullObject = (ConnectionstatusHelper) o;
////								SuspectedHelper sus=new SuspectedHelper();
////							String s=	sus.getAdvertisementName();
////							Toast.makeText(parent.getContext(), "view clicked: " + s, Toast.LENGTH_SHORT).show();
////								editActivity(fullObject.getinstallationId());
//							}
//
//							private Context getApplicationContext() {
//								// TODO Auto-generated method stub
//								return null;
//							}
//						});
			
	     
//			holder.fromtime = (TextView) convertView
//					.findViewById(R.id.tvsustimefrom);
//			
//			holder.totime = (TextView) convertView
//					.findViewById(R.id.tvsustimeto);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getAdvertisementName();

		if (s.equals("")) {
			holder.advcode.setText("No information available..");

		} else {

			holder.advcode.setText(searchArrayList.get(position)
					.getAdvertisementcode());
			
			holder.advname.setText(searchArrayList.get(position)
					.getAdvertisementName());

			holder.susdatefrom.setText(searchArrayList.get(position)
					.getEffectiveDateFrom());
			
			holder.susdateto.setText(searchArrayList.get(position)
					.getEffectiveDateTo());
			

			
			
		
//			holder.repetitions.setText(searchArrayList.get(position)
//					.getDayRepeatitions());
//			holder.actrepetitions.setText(searchArrayList.get(position)
//					.getActRepeatitions());
			holder.stsationspot.setText(searchArrayList.get(position)
					.getStationSpots());
			holder.totalspot.setText(searchArrayList.get(position)
					.getTotalSpot());
			holder.spercentage.setText(searchArrayList.get(position)
					.getSpotWisePercentage());
			holder.repetitions.setText(searchArrayList.get(position)
					.getDayRepeatitions());
			holder.actrepetitions.setText(searchArrayList.get(position)
					.getActRepeatitions());
			holder.totalper.setText(searchArrayList.get(position)
					.getPercentage());
			
//			holder.fromtime.setText(searchArrayList.get(position)
//					.gettimefrom());
//			
//			holder.totime.setText(searchArrayList.get(position)
//					.gettimeto());
		
		}

		return convertView;
	}

	static class ViewHolder {
		TextView advcode;
		TextView advname;
		TextView todate;
		TextView totime;
		TextView fromtime;
		TextView susdatefrom;
		TextView susdateto;
		TextView repetitions;
		TextView actrepetitions;
		TextView stsationspot;
		TextView totalspot;
		TextView spercentage;
		TextView totalper;
		Button btnhistory;

	}

}

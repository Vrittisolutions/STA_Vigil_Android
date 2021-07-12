package com.adapters;

import java.util.ArrayList;
import java.util.Locale;

import com.beanclasses.TvStatusStateList;
import com.stavigilmonitoring.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class TvStatusMainAdpt extends BaseAdapter {

	private static ArrayList<TvStatusStateList> searchArrayList;
	private LayoutInflater mInflater;
	Context context;
	private ArrayList<TvStatusStateList> arraylist;

	public TvStatusMainAdpt(Context context1, ArrayList<TvStatusStateList> searchResults) {
		context=context1;
		mInflater = LayoutInflater.from(context);
		searchArrayList=searchResults;
		arraylist=new ArrayList<TvStatusStateList>();
		arraylist.addAll(searchResults);
		
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
			convertView = mInflater.inflate(R.layout.soundlevelupdatedmain, null);
			holder = new ViewHolder();
			holder.stnname = (TextView) convertView.findViewById(R.id.tvstnnamesoundlevel);
			holder.tvreasonupdated=(TextView) convertView.findViewById(R.id.tvreasonupdated);
			holder.cnt=(TextView) convertView.findViewById(R.id.tvsCntc);
			holder.img=(ImageView) convertView.findViewById(R.id.imgTV);
			holder.img1=(ImageView) convertView.findViewById(R.id.imgTV1);
			holder.img2=(ImageView) convertView.findViewById(R.id.imgTV2);
			holder.img3=(ImageView) convertView.findViewById(R.id.imgTV3);
			holder.img4=(ImageView) convertView.findViewById(R.id.imgTV4);
			holder.img5=(ImageView) convertView.findViewById(R.id.imgTV5);
			holder.img6=(ImageView) convertView.findViewById(R.id.imgTV6);
			holder.img7=(ImageView) convertView.findViewById(R.id.imgTV7);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
     	holder.stnname.setText(searchArrayList.get(position).GetStateName());
     	String s=searchArrayList.get(position).GetTVReason();
     	if(s.equalsIgnoreCase("No Reason Found"))
     	{
     		holder.tvreasonupdated.setText("");
     		
     	}
     	else{
     		holder.tvreasonupdated.setText(searchArrayList.get(position).GetTVReason());
         	
     	}
		//holder.cnt.setText(searchArrayList.get(position).GetSCount()+"");
		holder.img.setVisibility(View.GONE);
		holder.img1.setVisibility(View.GONE);
		holder.img2.setVisibility(View.GONE);
		holder.img3.setVisibility(View.GONE);
		holder.img4.setVisibility(View.GONE);
		holder.img5.setVisibility(View.GONE);
		holder.img6.setVisibility(View.GONE);
		holder.img7.setVisibility(View.GONE);
		
		String TotalTV = searchArrayList.get(position).Gettotaltv();
		String CSNStatus = searchArrayList.get(position).getCSNStatus();
		int i=0;
		for (char d : TotalTV.toCharArray()) {
			switch (i) {
			case 0:
				if(d=='1')
				{
					holder.img.setBackground(context.getResources().getDrawable(R.drawable.tvon));
				} else
					holder.img.setBackground(context.getResources().getDrawable(R.drawable.tvoff));

				if(CSNStatus.equalsIgnoreCase("Y")){
					holder.img.setBackground(context.getResources().getDrawable(R.drawable.tvoffgrey));
				}

				holder.img.setVisibility(View.VISIBLE);
				break;
				
			case 1:
				if(d=='1')
				{
					holder.img1.setBackground(context.getResources().getDrawable(R.drawable.tvon));
				}
				else
					holder.img1.setBackground(context.getResources().getDrawable(R.drawable.tvoff));

				if(CSNStatus.equalsIgnoreCase("Y")){
					holder.img1.setBackground(context.getResources().getDrawable(R.drawable.tvoffgrey));
				}
				holder.img1.setVisibility(View.VISIBLE);
				break;
				
			case 2:
				if(d=='1')
				{
					holder.img2.setBackground(context.getResources().getDrawable(R.drawable.tvon));
				} else
					holder.img2.setBackground(context.getResources().getDrawable(R.drawable.tvoff));

				if(CSNStatus.equalsIgnoreCase("Y")){
					holder.img2.setBackground(context.getResources().getDrawable(R.drawable.tvoffgrey));
				}

				holder.img2.setVisibility(View.VISIBLE);
				break;
				
			case 3:
				if(d=='1')
				{
					holder.img3.setBackground(context.getResources().getDrawable(R.drawable.tvon));
				} else
					holder.img3.setBackground(context.getResources().getDrawable(R.drawable.tvoff));

				if(CSNStatus.equalsIgnoreCase("Y")){
					holder.img3.setBackground(context.getResources().getDrawable(R.drawable.tvoffgrey));
				}

				holder.img3.setVisibility(View.VISIBLE);
				break;
				
			case 4:
				if(d=='1')
				{
					holder.img4.setBackground(context.getResources().getDrawable(R.drawable.tvon));
				} else
					holder.img4.setBackground(context.getResources().getDrawable(R.drawable.tvoff));

				if(CSNStatus.equalsIgnoreCase("Y")){
					holder.img4.setBackground(context.getResources().getDrawable(R.drawable.tvoffgrey));
				}

				holder.img4.setVisibility(View.VISIBLE);
				break;
				
			case 5:
				if(d=='1')
				{
					holder.img5.setBackground(context.getResources().getDrawable(R.drawable.tvon));
				} else
					holder.img5.setBackground(context.getResources().getDrawable(R.drawable.tvoff));

				if(CSNStatus.equalsIgnoreCase("Y")){
					holder.img5.setBackground(context.getResources().getDrawable(R.drawable.tvoffgrey));
				}

				holder.img5.setVisibility(View.VISIBLE);
				break;
				
			case 6:
				if(d=='1')
				{
					holder.img6.setBackground(context.getResources().getDrawable(R.drawable.tvon));
				} else
					holder.img6.setBackground(context.getResources().getDrawable(R.drawable.tvoff));

				if(CSNStatus.equalsIgnoreCase("Y")){
					holder.img6.setBackground(context.getResources().getDrawable(R.drawable.tvoffgrey));
				}

				holder.img6.setVisibility(View.VISIBLE);
				break;
				
			case 7:
				if(d=='1')
				{
					holder.img7.setBackground(context.getResources().getDrawable(R.drawable.tvon));
				} else
					holder.img7.setBackground(context.getResources().getDrawable(R.drawable.tvoff));

				if(CSNStatus.equalsIgnoreCase("Y")){
					holder.img7.setBackground(context.getResources().getDrawable(R.drawable.tvoffgrey));
				}

				holder.img7.setVisibility(View.VISIBLE);
				break;
			}
				i++;
				
		}

		//getcsnstatus and if station present then set all values as grey in that station
		return convertView;
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
			for (TvStatusStateList wp : arraylist) 
			{
				if (wp.GetStateName().toLowerCase(Locale.getDefault()).contains(charText)) 
				{
					searchArrayList.add(wp);
				}
			}
		}
		notifyDataSetChanged();
		
	}

	static class ViewHolder {
		TextView stnname;
		TextView cnt;
		ImageView img;
		ImageView img1;
		ImageView img2;
		ImageView img3;
		ImageView img4;
		ImageView img5;
		ImageView img6;
		ImageView img7;
	TextView tvreasonupdated;
		

	}

	
	
		
	}





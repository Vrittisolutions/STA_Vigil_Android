package com.adapters;

import java.util.List;

import com.beanclasses.StationCall;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SupporterListAdptr extends BaseAdapter {

	private static List<StationCall> searchArrayList;
	private LayoutInflater mInflater;
	Context context;

	public SupporterListAdptr(Context c, List<StationCall> searchResults) {
		context = c;
		mInflater = LayoutInflater.from(context);
		// searchResults.notify();
		searchArrayList = searchResults;
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

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.supporterlistitem, null);
			holder = new ViewHolder();

			holder.tvNetworkCode = (TextView) convertView
					.findViewById(com.stavigilmonitoring.R.id.tvsNamec);
			holder.tvCount = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.tvsCntc);
			holder.mCall = (ImageView) convertView.findViewById(com.stavigilmonitoring.R.id.phone);
			holder.image = (ImageView) convertView
					.findViewById(com.stavigilmonitoring.R.id.imageView1);
			holder.tvCount.setVisibility(View.VISIBLE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String s = searchArrayList.get(position).getName();

		if (s.equals("")) {
			holder.tvNetworkCode.setText("No Station Found..");
			holder.tvCount.setText("");

		} else if (s
				.equalsIgnoreCase("Click and Hold The Item For More Option")) {

			holder.tvNetworkCode.setText(searchArrayList.get(position)
					.getName());
	//		holder.tvNetworkCode.setTextColor(Color.CYAN);
			holder.tvCount.setText(searchArrayList.get(position).getnumber());
			holder.image.setVisibility(View.INVISIBLE);
			holder.mCall.setVisibility(View.INVISIBLE);
			
		} else {
			holder.image.setVisibility(View.VISIBLE);
			holder.mCall.setVisibility(View.VISIBLE);
			holder.tvNetworkCode.setText(searchArrayList.get(position)
					.getName());
			holder.tvCount.setText(searchArrayList.get(position).getnumber());

		}

		holder.mCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String num = searchArrayList.get(position).getnumber();
				if (num.contains("-"))
					num = num.replace("-", "");
				else
					num = "+91" + num;

				Intent callIntent = new Intent(Intent.ACTION_DIAL);
				callIntent.setData(Uri.parse("tel:" + num));
				context.startActivity(callIntent);

			}
		});

		return convertView;

	}

	static class ViewHolder {

		TextView tvNetworkCode;
		TextView tvCount;
		ImageView mCall;
		ImageView image;

	}

}

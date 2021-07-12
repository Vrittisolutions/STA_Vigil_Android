package com.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beanclasses.STA_Visit_Questions;
import com.stavigilmonitoring.R;

import java.util.ArrayList;

//import com.google.android.gms.vision.barcode.Barcode;

public class SendEmailPdfAdapter extends BaseAdapter {
	private ArrayList<STA_Visit_Questions> list;
	private Context parent;
	private LayoutInflater mInflater;
	private ArrayList<STA_Visit_Questions> arraylist;

	public SendEmailPdfAdapter(Context parent,
							   ArrayList<STA_Visit_Questions> questionsBeanList) {
		this.parent = parent;
		this.list = questionsBeanList;
		arraylist = new ArrayList<STA_Visit_Questions>();
		arraylist.addAll(questionsBeanList);
		mInflater = LayoutInflater.from(parent);
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
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return arraylist.size();
	}

	@Override
	public int getItemViewType(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final int pos = position;
		final ViewHolder holder;
		STA_Visit_Questions sta_visit_questions= (STA_Visit_Questions) getItem(position);
		String selection, qusID, question, resptype, minvalue, maxvalue;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.send_email_adapter, null);
			holder = new ViewHolder();
			holder.txtquestion = (TextView)convertView.findViewById(com.stavigilmonitoring.R.id.txtquestion);
			holder.txtanswer = (TextView)convertView.findViewById(com.stavigilmonitoring.R.id.txtanswer);

			 selection = list.get(pos).getSelectionText();
			 qusID = list.get(pos).getQuesID();
			 question = list.get(pos).getQuestion();
			 resptype = list.get(pos).getResponseType();
			 minvalue = list.get(pos).getValuemin();
			 maxvalue = list.get(pos).getValueMax();

			holder.txtquestion.setText(question);

			String ans = list.get(pos).getAnswer();

			if(ans.equalsIgnoreCase("NotAnswered")){
				holder.txtanswer.setText("NA");
			}else {
				holder.txtanswer.setText(ans);
				holder.txtanswer.setTextColor(Color.parseColor("#6cad1c"));
			}


			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		return convertView;
	}

	static class ViewHolder {
		TextView txtquestion, txtanswer;
	}

}



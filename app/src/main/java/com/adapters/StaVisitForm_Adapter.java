package com.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

//import com.google.android.gms.vision.barcode.Barcode;

import com.beanclasses.STA_Visit_Questions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class StaVisitForm_Adapter extends BaseAdapter {
	private ArrayList<STA_Visit_Questions> list;
	private Context parent;
	private LayoutInflater mInflater;
	private ArrayList<STA_Visit_Questions> arraylist;

	public StaVisitForm_Adapter(Context parent,
								   ArrayList<STA_Visit_Questions> questionsBeanList) {
		this.parent = parent;
		this.list = questionsBeanList;
		arraylist = new ArrayList<STA_Visit_Questions>();
		arraylist.addAll(questionsBeanList);
		//arraylist=new ArrayList<com.vstaproject.TvStatusMain.StateList>();
		//arraylist.addAll(searchResults);
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

		/*int count;
		if (list.size() > 0) {
			count = getCount();
		} else {
			//Toast.makeText(parent,"No items in Ordered Items list",Toast.LENGTH_SHORT).show();
			count = 1;
		}*/
		return arraylist.size();

		//  return getCount();
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
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.sta_visit_ques, null);
			holder = new ViewHolder();
			holder.txtquestion = (TextView)convertView.findViewById(com.stavigilmonitoring.R.id.txtquestion);
			holder.txt_edtselection = (EditText)convertView.findViewById(com.stavigilmonitoring.R.id.txt_edtselection);
			holder.txt_edtselection.setVisibility(View.GONE);
			holder.edttxtselection = (EditText)convertView.findViewById(com.stavigilmonitoring.R.id.edttxtselection);
			holder.spinner_yes_no = (Spinner)convertView.findViewById(com.stavigilmonitoring.R.id.spinner_yes_no);

			 selection = list.get(pos).getSelectionText();
			 qusID = list.get(pos).getQuesID();
			 question = list.get(pos).getQuestion();
			 resptype = list.get(pos).getResponseType();
			 minvalue = list.get(pos).getValuemin();
			 maxvalue = list.get(pos).getValueMax();

			holder.spinner_yes_no.setTag(list.get(pos));

			try{
				if(resptype.equalsIgnoreCase("Numeric")){
					holder.edttxtselection.setVisibility(View.VISIBLE);
					holder.edttxtselection.setRawInputType(InputType.TYPE_CLASS_PHONE);

					holder.spinner_yes_no.setVisibility(View.GONE);
					holder.txt_edtselection.setVisibility(View.GONE);

					holder.edttxtselection.setTag(list.get(pos));
					//holder.edttxtselection.setTag(list.get(position));

				}else if(resptype.equalsIgnoreCase("Text")){
					holder.txt_edtselection.setVisibility(View.GONE);
					holder.spinner_yes_no.setVisibility(View.GONE);

					holder.edttxtselection.setVisibility(View.VISIBLE);
					holder.edttxtselection.setRawInputType(InputType.TYPE_CLASS_TEXT);
					holder.edttxtselection.setTag(list.get(pos));
					//holder.txt_edtselection.setTag(list.get(pos));

					try{
						Calendar c = Calendar.getInstance();
						if(question.trim().equalsIgnoreCase("Date of Visit")){
							holder.edttxtselection.setClickable(false);
							SimpleDateFormat sdf = new SimpleDateFormat("dd MMM YYYY");
							String addedDt = sdf.format(c.getTime());
							list.get(pos).setANSWER(addedDt);
							holder.edttxtselection.setText(list.get(pos).getQuesCode());
						}else if(question.trim().equalsIgnoreCase("Time Of visit")){
							holder.edttxtselection.setClickable(false);
							SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
							String time = sdf1.format(c.getTime());
							list.get(pos).setANSWER(time);
							holder.edttxtselection.setText(list.get(pos).getANSWER());
						}else {
							holder.edttxtselection.setClickable(true);
						}
					}catch (Exception e) {
						e.printStackTrace();
					}

				}else if(resptype.equalsIgnoreCase("Selection")){
					holder.spinner_yes_no.setVisibility(View.VISIBLE);
					holder.spinner_yes_no.setTag(list.get(pos));

					holder.txt_edtselection.setVisibility(View.GONE);
					holder.edttxtselection.setVisibility(View.GONE);

					//String a = "Yes|No|Not Applicable";
					String x[] = selection.split("\\|");

					ArrayList<String> data = new ArrayList<String>();

					try{
						for(int i=0; i<=x.length; i++){
							data.add(x[i]);
						}
					}catch (Exception e){
						e.printStackTrace();
					}

					ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.parent, android.R.layout.simple_spinner_item, data);
					adapter = new ArrayAdapter<String>(this.parent, com.stavigilmonitoring.R.layout.spinnertext, data);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					holder.spinner_yes_no.setAdapter(adapter);
					holder.spinner_yes_no.setSelection(0);
				}
			}catch (Exception e){
				e.printStackTrace();
			}

			//spinner on click listener
			holder.spinner_yes_no.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int spin_position, long id) {

						String spin_ans = holder.spinner_yes_no.getSelectedItem().toString();
						String txt2 = parent.getItemAtPosition(spin_position).toString();
						list.get(pos).setANSWER(spin_ans);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					//TODO Auto-generated method stub
				}
			});

			//edittext addtextchnage listener listener
			holder.edttxtselection.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {

					if (((s.toString().trim() == "") || (s.toString() == null) ||
							(s.toString().length() == 0))){

						list.get(pos).setANSWER("NotAnswered");
						//holder.edttxtselection.setText(" ");
					}else {
						holder.edttxtselection.setTag(list.get(pos));
						String ans = s.toString();
						list.get(pos).setANSWER(ans);
					}
				}

				@Override
				public void afterTextChanged(Editable s) {
					if(s.toString().trim() == "" || s.toString() == null ||  s.toString().length() == 0){
                        list.get(pos).setANSWER("NotAnswered");
					}else {
						//holder.edttxtselection.setTag(list.get(pos));
						//holder.edttxtselection.setText(list.get(pos).getANSWER());
						String ans = s.toString();
						list.get(pos).setANSWER(ans);
					}
				}
			});

			String name = list.get(pos).getANSWER();
			holder.txtquestion.setText(/*list.get(pos).getQuesCode() + "/"+*/question.trim());
			holder.edttxtselection.setText(list.get(pos).getANSWER());

			/*Calendar c = Calendar.getInstance();
			if(question.trim().equalsIgnoreCase("Date of Visit")){
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMM YYYY");
				String addedDt = sdf.format(c.getTime());
				holder.edttxtselection.setText(addedDt);
			}else if(question.trim().equalsIgnoreCase("Time Of visit")){
				SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
				String time = sdf1.format(c.getTime());
				holder.edttxtselection.setText(time);
			}*/

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		return convertView;
	}

	static class ViewHolder {
		TextView txtquesid, txtquestion, txtresptype, txtminval, txtmaxval;
		EditText edttxtselection, txt_edtselection;
		Spinner spinner_yes_no;
	}


	public ArrayList<STA_Visit_Questions> getAllqueansList() {
		ArrayList<STA_Visit_Questions> list = new ArrayList<>();
		for (int i = 0; i < arraylist.size(); i++) {
            /*//if (arrayList.get(i).getIsChecked())
            list.add(arrayList.get(i));*/
            int value = Integer.parseInt(arraylist.get(i).getANSWER());
			boolean a1 = (value != 0);

			if(value != 0){
				list.add(arraylist.get(i));
			}else{
				//list.add(arraylist.get(i));
			}
		}
		return list;
	}

}



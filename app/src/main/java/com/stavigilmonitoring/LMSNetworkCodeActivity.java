package com.stavigilmonitoring;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.adapters.LmsConnectionStatewiseAdapter;
import com.adapters.MaterialReqMainAdapter;
import com.beanclasses.LmsConnectionStatewiseBean;
import com.database.DBInterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class LMSNetworkCodeActivity extends Activity{
	
	  List<LmsConnectionStatewiseBean> searchResults;
	  int stncnt;
	public String filter;
	MaterialReqMainAdapter materialReqMainAdapter;
	
	TextView title;
	ImageView iv;
	private GridView lstcsn;
	com.stavigilmonitoring.utility ut;
	static SimpleDateFormat dff;
	static String Ldate;
	String Type;
	String mobno, link;
	DatabaseHandler db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.activity_material_detail_main);
		lstcsn =  findViewById(com.stavigilmonitoring.R.id.materialreq_main);
		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_connection_main);
		title = (TextView) findViewById(com.stavigilmonitoring.R.id.materialreq);
		searchResults = new ArrayList<LmsConnectionStatewiseBean>();

		Intent intent = getIntent();
		ut = new utility();
		Type = intent.getStringExtra("Type");
		title.setText(Type + "- Material Requirement");
		
		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		
		if (dbvalue()) {
			updatelist();
		}
		
		((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext_search))
		.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start,
					int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

				filter = s.toString().trim();
				materialReqMainAdapter.filter((filter)
						.toLowerCase(Locale.getDefault()));

			}
		});

		
	}
	
	public void FilterClick(View v) {
		if (((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext_search)).getVisibility() == View.VISIBLE) {
			((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext_search))
					.setVisibility(View.GONE);
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		} else if (((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext_search))
				.getVisibility() == View.GONE) {
			((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext_search))
					.setVisibility(View.VISIBLE);
			EditText textView = (EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext_search);
			textView.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
		}

	}
 
	private boolean dbvalue() {
		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery(
					"SELECT *   FROM LmsConnectionStatus", null);
			if (cursor != null && cursor.getCount() > 0) {
				if (cursor.getColumnIndex("NetworkCode") < 0) {
					cursor.close();
					return false;
				} else {
					cursor.close();
					return true;
				}
			} else {
				cursor.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
			dff = new SimpleDateFormat("HH:mm:ss");
			Ldate = dff.format(new Date());

			StackTraceElement l = new Exception().getStackTrace()[0];
			System.out.println(l.getClassName() + "/" + l.getMethodName() + ":"
					+ l.getLineNumber());
			
			if (!ut.checkErrLogFile()) {

				ut.ErrLogFile();
			}
			if (ut.checkErrLogFile()) {
				ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":"
						+ l.getLineNumber() + "	" + e.getMessage() + " "
						+ Ldate);
			}

		}
		return false;
	}

	private void updatelist() {
		searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor c = sql.rawQuery(
				"SELECT DISTINCT NetworkCode FROM LmsConnectionStatus ORDER BY NetworkCode", null);
		int con = c.getCount();
	//	String colum = c.getString(c.getColumnIndex("NetworkCode"));
		if (c.getCount() > 0) {
			c.moveToFirst();
			int i = c.getPosition();
			LmsConnectionStatewiseBean sitem= new LmsConnectionStatewiseBean();/*
			sitem.setNetcode("Extra");
			sitem.setNetcodeCount(stncnt);
			searchResults.add(sitem);
			c.moveToPosition(i+1);*/
			do {
				i = c.getPosition();
				stncnt = 0;
				String Type = c.getString(0);

				Type = Type.replaceAll("0", "");
				Type = Type.replaceAll("1", "");
				if (!Type.trim().equalsIgnoreCase("")) {
				    sitem= new LmsConnectionStatewiseBean();
					sitem.setNetcode(Type);
					sitem.setNetcodeCount(stncnt);
					searchResults.add(sitem);
				}
			} while (c.moveToNext());

			c.close();

		}
		lstcsn.setAdapter(new LmsConnectionStatewiseAdapter(LMSNetworkCodeActivity.this,
				searchResults));
		
		/*int scount = 0;
		for (int i = 0; i < searchResults.size(); i++){
			scount = scount + searchResults.get(i).getNetcodeCount();
		}
		
		SharedPreferences preflmsconn = getApplicationContext()
				.getSharedPreferences("PrefLmsCount",
						Context.MODE_PRIVATE);
		Editor editorlmsConne = preflmsconn.edit();
		
		editorlmsConne.putString("LmsCount", scount+"");

		editorlmsConne.commit();*/

	}
			

}

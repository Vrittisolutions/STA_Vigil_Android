package com.stavigilmonitoring;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapters.StationEnquiryStnAdap;
import com.beanclasses.StateList;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class WorkPlanAllStationsList extends Activity{
	private static InventAllStation mInventAllStation;
	private ImageView ivRefresh, ivFilter;
	private TextView tvfilter;
	private ProgressBar mProgressBar;
	private GridView mListView;
	private String sop, Ldate, dff, filter;
	ArrayList<StateList> searchResults;
	private StationEnquiryStnAdap StationAdaptor;
	private String mType, mobno, resposmsg;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String ButtonClickKey;
	DatabaseHandler db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.inventallstationlist);
		
		ivRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_invent);
		ivFilter = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_invent_filter);
		mProgressBar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressinvent);
		mListView = findViewById(com.stavigilmonitoring.R.id.listInvent);
		tvfilter = (TextView) findViewById(com.stavigilmonitoring.R.id.edfitertext_search_in_invt);
		
		searchResults = new ArrayList<StateList>();
		Intent i = getIntent();
		mType = i.getStringExtra("Type");
		ButtonClickKey = i.getStringExtra("ButtonClickKey");
		
		((TextView) findViewById(com.stavigilmonitoring.R.id.stationInvent)).setText("Station Wise Work plan - " + mType);

		db = new DatabaseHandler(getApplicationContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

				Log.e("Check List item Click", searchResults.get(position).getStatioName());
				String installationName = searchResults.get(position).getStatioName();
				//Toast.makeText(getApplicationContext(),"Station - "+ installationName +" clicked",Toast.LENGTH_SHORT).show();

				//new activity showing activities against selected station
				Intent intent = new Intent(getApplicationContext(), WrkPlan_stationRelatedActivities.class);
				intent.putExtra("Type", searchResults.get(position).getStatioName());
				intent.putExtra("InstallationId", searchResults.get(position).getInstallationId());
				intent.putExtra("InstallationName",searchResults.get(position).getStatioName());
				intent.putExtra("ButtonClickKey",ButtonClickKey);
				startActivity(intent);
				
			}
		});
		
		ivRefresh.setOnClickListener(new  OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if( ut.isnet(getApplicationContext())){
					mInventAllStation = null;
					mInventAllStation = new InventAllStation();
					mInventAllStation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					//fetchdata();
				} else {
					ut.showD(WorkPlanAllStationsList.this, "nonet");
				}
			}
		});
		
		ivFilter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (tvfilter.getVisibility() == View.VISIBLE) {
					tvfilter.setVisibility(View.GONE);
					InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getCurrentFocus()
							.getWindowToken(), 0);
				} else if (tvfilter.getVisibility() == View.GONE) {
					tvfilter.setVisibility(View.VISIBLE);
					tvfilter.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(tvfilter,
							InputMethodManager.SHOW_IMPLICIT);
				}
			}
		});
		
		tvfilter.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				filter = s.toString().trim();
				StationAdaptor.filter((filter).toLowerCase(Locale
						.getDefault()));
			}
		});
		
		if (dbvalue()){
			updatelist();			
		} else if (ut.isnet(getApplicationContext())){
			fetchdata();
		} else {
			ut.showD(WorkPlanAllStationsList.this, "nonet");
		}	
		
	}

	private void fetchdata() {
		// TODO Auto-generated method stub
		mInventAllStation = null;
		if (mInventAllStation == null) {
			ivRefresh.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);

			Log.e("async", "null");
			mInventAllStation = new InventAllStation();
			mInventAllStation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			if (mInventAllStation.getStatus() == AsyncTask.Status.RUNNING) {
				Log.e("async", "running");
				ivRefresh.setVisibility(View.GONE);
				mProgressBar.setVisibility(View.VISIBLE);
			}
		}
		
	}

	private void updatelist() {
		// TODO Auto-generated method stub
		searchResults.clear();
	//	DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		int count = 0;
		Cursor c = sql.rawQuery("Select distinct InstallationDesc,InstalationId from ConnectionStatusFiltermob where NetworkCode='"+mType+"' Order by InstallationDesc",
				null);
		
		if (c.getCount() > 0) {
			c.moveToFirst();
			do {
				count++;
				String StationName = c.getString(c
						.getColumnIndex("InstallationDesc"));
				String InstallationId = c.getString(c
						.getColumnIndex("InstalationId"));
				count = c.getCount();
				
				StateList sitem = new StateList();

				sitem.setStatioName(StationName);
				sitem.setInstallationId(InstallationId);
				sitem.Setcount(count);
				searchResults.add(sitem);

			} while (c.moveToNext());

		}
		StationAdaptor = new StationEnquiryStnAdap(WorkPlanAllStationsList.this, searchResults);
		mListView.setAdapter(StationAdaptor);
	}

	private boolean dbvalue() {
		// TODO Auto-generated method stub
		try{
	//	DatabaseHandler Db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql.rawQuery("Select distinct InstallationDesc from ConnectionStatusFiltermob", null);
		
		if (cursor != null && cursor.getCount() > 0) {

			cursor.close();
			return true;

		} else {

			cursor.close();
			return false;
		}
		}catch (Exception e){
			StackTraceElement l = new Exception().getStackTrace()[0];
			System.out.println(l.getClassName() + "/" + l.getMethodName() + ":"
					+ l.getLineNumber());
			ut = new com.stavigilmonitoring.utility();
			if (!ut.checkErrLogFile()) {

				ut.ErrLogFile();
			}
			if (ut.checkErrLogFile()) {
				ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":"
						+ l.getLineNumber() + "	" + e.getMessage() + " "
						+ Ldate);
			}		
		return false;
		}
	}
	
	public class InventAllStation extends AsyncTask<String, Void, String>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			ivRefresh.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				if (sop.equals("valid")) {
					updatelist();
				} else {
					ut.showD(WorkPlanAllStationsList.this, "invalid");
				}
				ivRefresh.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);

			} catch (Exception e) {
				e.printStackTrace();
				// dff = new SimpleDateFormat("HH:mm:ss");
				// Ldate = dff.format(new Date());

				StackTraceElement l = new Exception().getStackTrace()[0];
				System.out.println(l.getClassName() + "/" + l.getMethodName()
						+ ":" + l.getLineNumber());
				ut = new utility();
				if (!ut.checkErrLogFile()) {

					ut.ErrLogFile();
				}
				if (ut.checkErrLogFile()) {
					ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
							+ ":" + l.getLineNumber() + "	" + e.getMessage()
							+ " " + Ldate);
				}

			}

		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String Url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMasterMobile?Mobile="+mobno;
			Log.e("All Station", "Url=" + Url);

			try {
				resposmsg = ut.httpGet(Url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (resposmsg.contains("<InstalationId>")) {
				sop = "valid";
			//	DatabaseHandler db = new DatabaseHandler(getBaseContext());
				SQLiteDatabase sql = db.getWritableDatabase();
				String columnName, columnValue;
				//sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusFiltermob");
				//sql.execSQL(ut.getConnectionStatusFiltermob());
				sql.delete("ConnectionStatusFiltermob",null,null);

				Cursor cur1 = sql.rawQuery(
						"SELECT * FROM ConnectionStatusFiltermob", null);
				cur1.getCount();
				ContentValues values2 = new ContentValues();
				NodeList nl2 = ut.getnode(resposmsg, "Table");

				Log.e("All Station Data ", "get length : " + nl2.getLength());
				for (int i = 0; i < nl2.getLength(); i++) {
					Log.e("All Station Data ", "length : " + nl2.getLength());
					Element e = (Element) nl2.item(i);
					for (int j = 0; j < cur1.getColumnCount(); j++) {
						columnName = cur1.getColumnName(j);
						columnValue = ut.getValue(e, columnName);
						Log.e("All Station Data ", "column Name : "
								+ columnName);
						Log.e("All Station Data ", "column value : "
								+ columnValue);

						values2.put(columnName, columnValue);

					}
					sql.insert("ConnectionStatusFiltermob", null, values2);
				}

				cur1.close();

			} else {
				sop = "invalid";
				System.out.println("--------- invalid for project list --- ");
			}
			return sop;

		}
		
	}

}

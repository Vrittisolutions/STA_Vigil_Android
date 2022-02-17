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

import com.adapters.Notify_StationList;
import com.beanclasses.StateList;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class STNotifyStnListAll extends Activity{
	private static InventAllStation mInventAllStation;
	private ImageView ivRefresh, ivFilter;
	private TextView tvfilter;
	private ProgressBar mProgressBar;
	private GridView mListView;
	private String sop, Ldate, dff, filter;
	ArrayList<StateList> searchResults;
	private Notify_StationList StationAdaptor;
	private String mType, mobno, resposmsg;
	utility ut = new utility();
	DatabaseHandler db;
	String intentFROM = "", notificationType="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.stn_notify_stations);

		init();

		db = new DatabaseHandler(getApplicationContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();

		if (dbvalue()){
			updatelist();
		} else if (ut.isnet(getApplicationContext())){
			fetchdata();
		} else {
			try{
				ut.showD(STNotifyStnListAll.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		setListeners();
	}

	public void init(){
		ivRefresh = (ImageView) findViewById(R.id.button_refresh_invent);
		ivFilter = (ImageView) findViewById(R.id.button_invent_filter);
		mProgressBar = (ProgressBar) findViewById(R.id.progressinvent);
		mListView = findViewById(R.id.listInvent);
		tvfilter = (TextView) findViewById(R.id.edfitertext_search_in_invt);

		searchResults = new ArrayList<StateList>();
		Intent i = getIntent();
		mType = i.getStringExtra("Type");

		((TextView) findViewById(R.id.stationInvent)).setText("Stations  - " + mType);

		Intent intent = getIntent();
		intentFROM = intent.getStringExtra("intentFrom");

		if(!intentFROM.equalsIgnoreCase("StationWiseNotification")){
			notificationType = intent.getStringExtra("MsgType");
		}else if(intentFROM.equalsIgnoreCase("StationWiseNotification")){
			notificationType = "";
		}

	}

	public void setListeners(){

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

				Log.e("Check List item Click", searchResults.get(position).getStatioName());

				if(intentFROM.equalsIgnoreCase("StationWiseNotification")){
					Intent intent = new Intent(STNotifyStnListAll.this, NotificationsType.class);
					intent.putExtra("InstallationID",searchResults.get(position).getInstallationId());
					intent.putExtra("Station",searchResults.get(position).getStatioName());
					startActivity(intent);
				}else {
					//open notifications list activity
					Intent intent = new Intent(STNotifyStnListAll.this, NotificationsList.class);
					intent.putExtra("InstallationID",searchResults.get(position).getInstallationId());
					intent.putExtra("MsgType",notificationType);
					intent.putExtra("Station",searchResults.get(position).getStatioName());
					startActivity(intent);
				}
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
					try{
						ut.showD(STNotifyStnListAll.this, "nonet");
					}catch (Exception e){
						e.printStackTrace();
					}
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
				StationAdaptor.filter((filter).toLowerCase(Locale.getDefault()));
			}
		});

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

		try{
			// TODO Auto-generated method stub
			searchResults.clear();
			//DatabaseHandler db = new DatabaseHandler(this);
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

					String curcnt = "", notifCount = "0";

					if(!intentFROM.equalsIgnoreCase("StationWiseNotification")){
						curcnt = "Select * from TableNotifications WHERE InstallationId='"+InstallationId+"'" + " AND MsgType='"+notificationType+"'";
					}else if(intentFROM.equalsIgnoreCase("StationWiseNotification")){
						curcnt = "Select * from TableNotifications WHERE InstallationId ='"+InstallationId+"'";
					}

					Cursor cnt_notfcn = sql.rawQuery(curcnt, null);
					if(cnt_notfcn.getCount() > 0){
						notifCount = String.valueOf(cnt_notfcn.getCount());
					}else {
						notifCount = "0";
					}

					StateList sitem = new StateList();

					sitem.setStatioName(StationName);
					sitem.setInstallationId(InstallationId);
					sitem.Setcount(count);
					sitem.setNotificationCnt(notifCount);
					searchResults.add(sitem);

					Collections.sort(searchResults, new Comparator<StateList>() {
						@Override
						public int compare(StateList lhs, StateList rhs) {
							return rhs.getNotificationCnt().compareTo(lhs.getNotificationCnt());
						}
					});

				} while (c.moveToNext());

			}

			StationAdaptor = new Notify_StationList(STNotifyStnListAll.this, searchResults);
			mListView.setAdapter(StationAdaptor);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private boolean dbvalue() {
		// TODO Auto-generated method stub
		try{
		//DatabaseHandler Db1 = new DatabaseHandler(getBaseContext());
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
			ut = new utility();
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
					try{
						ut.showD(STNotifyStnListAll.this, "invalid");
					}catch (Exception e){
						e.printStackTrace();
					}
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
			String Url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMasterMobile?Mobile="+mobno;
			Log.e("All Station", "Url=" + Url);

			try {
				resposmsg = ut.httpGet(Url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (resposmsg.contains("<InstalationId>")) {
				sop = "valid";
				//DatabaseHandler db = new DatabaseHandler(getBaseContext());
				SQLiteDatabase sql = db.getWritableDatabase();
				String columnName, columnValue;
			//	sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusFiltermob");
			//	sql.execSQL(ut.getConnectionStatusFiltermob());
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

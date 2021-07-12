package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.StationEnquiryfilteradp;
import com.beanclasses.StateList;
import com.database.DBInterface;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class StationEnquiryStateFilter extends Activity {
	private ImageView mRefresh;
	private ProgressBar mprogress;
	private GridView mList;
	private ArrayList<StateList> mSearchlist;
	private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	static SimpleDateFormat dff;
	static String Ldate;
	private LinearLayout mAllnet;
	private TextView mAllCount,mText;
	String resposmsg,sop,type,mobno;
	int scount = 0;
	private static DownloadnetWork asynk;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.stationenquirystatewisefilter);

		mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_Stn_Enq);
		mprogress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBarstnenq);
		mList =  findViewById(com.stavigilmonitoring.R.id.lststnenq);
		mAllnet = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.networksumstn);
		mText = (TextView) findViewById(com.stavigilmonitoring.R.id.stnName);
		mAllCount = (TextView) findViewById(com.stavigilmonitoring.R.id.stnCntc);
		mSearchlist = new ArrayList<StateList>();
		Intent i = getIntent();
		type = i.getStringExtra("Type");
		mText.setText(type+"-All");

		db = new DatabaseHandler(getApplicationContext());

			DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();

		mAllnet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),StationEnquiryStnListAll.class);
				intent.putExtra("Type", type);
				startActivity(intent);

			}
		});
		mRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				      if (ut.isnet(getApplicationContext())) {
						fetchdata();
					} else {
				      	try{
							ut.showD(StationEnquiryStateFilter.this,"nonet");
						}catch (Exception e){
				      		e.printStackTrace();
						}
					}
			}
		});
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), StationEnquiryStationList.class);
				i.putExtra("Type", mSearchlist.get(position).getNetworkcode());
				i.putExtra("Network", type);
				startActivity(i);
				
			}
		});
		
		if (dbvalue()) {
			updatelist();
		} else if (ut.isnet(getApplicationContext())) {
			fetchdata();
		} else {
			try{
				ut.showD(StationEnquiryStateFilter.this,"nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		
		
	}
	private void updatelist() {
	mSearchlist.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		
		Cursor c = sql
				.rawQuery(
						"SELECT DISTINCT SubNetworkCode FROM ConnectionStatusFiltermob where NetworkCode='"
								+ type + "' ORDER BY SubNetworkCode",
						null);
		// ,InstallationId
		if (c.getCount() > 0) {
			c.moveToFirst();
			do {
				int count = 0;
				String StationName = c.getString(c
						.getColumnIndex("SubNetworkCode"));
			 Cursor c1 = sql.rawQuery("SELECT DISTINCT InstallationDesc FROM ConnectionStatusFiltermob WHERE SubNetworkCode='"+StationName+"'",null);
             count = c1.getCount();
				// Type = Type.replaceAll("0", "");
				// Type = Type.replaceAll("1", "");
				// if (!Type.trim().equalsIgnoreCase("")) {
				StateList sitem = new StateList();
                sitem.SetNetworkCode(StationName);
                sitem.Setcount(count);				
                mSearchlist.add(sitem);

				// }
			} while (c.moveToNext());

		}
		for (int i = 0; i < mSearchlist.size(); i++)
			scount = scount + mSearchlist.get(i).Getcount();
			mAllCount.setText(""+scount);

		StationEnquiryfilteradp adp  = new StationEnquiryfilteradp(StationEnquiryStateFilter.this, mSearchlist);
		adp.notifyDataSetChanged();
	//	mList.invalidate();
		mList.setAdapter(adp);

	}
	private void fetchdata() {
		// new DownloadxmlsDataURL_new().execute();
		if (asynk == null) {
			mRefresh.setVisibility(View.VISIBLE);
			mprogress
					.setVisibility(View.GONE);

			Log.e("async", "null");
			asynk = new DownloadnetWork();
			asynk.execute();
		} else {
			if (asynk.getStatus() == AsyncTask.Status.RUNNING) {
				Log.e("async", "running");
				mRefresh.setVisibility(View.GONE);
				mprogress
						.setVisibility(View.VISIBLE);
			}
		}

	}
	private boolean dbvalue() {
		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery(
					"SELECT  NetworkCode FROM ConnectionStatusFiltermob", null);
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
			ut = new com.stavigilmonitoring.utility();
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
	public class DownloadnetWork extends AsyncTask<String, Void, String> {

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

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mRefresh.setVisibility(View.GONE);
			mprogress.setVisibility(View.VISIBLE);

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
						ut.showD(StationEnquiryStateFilter.this, "invalid");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				mRefresh.setVisibility(View.VISIBLE);
				mprogress.setVisibility(View.GONE);

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

	}
	
}

package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapters.StationEnqMaterialHistryAdaptor;
import com.beanclasses.StationEnquiryMaterialBean;

public class StationEnquiryMaterialRequest extends Activity {
	private ArrayList<StationEnquiryMaterialBean> searchResults;
	private ImageView mRefresh;
	private ListView mListView;
	private ProgressBar mProgressBar;
	private LinearLayout mAllnet;
	private TextView mText;
	private TextView mAllCount;
	private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	static SimpleDateFormat dff;
	static String Ldate;
	private String mobno, type, conn;
	static DownloadxmlsDataURL_new asynk;
	String responsemsg, Syncdate, sop, urlnet;
	int scount = 0;
	private StationEnqMaterialHistryAdaptor adapter;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.stationenquirymaterialrequest);

		mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.buttn_refresh_mat_his);
		mProgressBar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar_material);
		mText = (TextView) findViewById(com.stavigilmonitoring.R.id.material_Station);
		mListView = (ListView) findViewById(com.stavigilmonitoring.R.id.Mat_his_list);
		searchResults = new ArrayList<StationEnquiryMaterialBean>();
		Intent i = getIntent();
		conn = i.getStringExtra("stnname");
		type = i.getStringExtra("stninst");
		mText.append(" "+conn);

		db = new DatabaseHandler(getApplicationContext());

		mRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 if (ut.isnet(getApplicationContext())) {
						fetchdata();
					} else {
				 	try{
						ut.showD(StationEnquiryMaterialRequest.this, "nonet");
					}catch (Exception e){
				 		e.printStackTrace();
					}
					}

			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

			}
		});
		if (dbvalue()) {
			updatelist();
		} else if (ut.isnet(getApplicationContext())) {
			fetchdata();
		} else {
			try{
				ut.showD(StationEnquiryMaterialRequest.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}

	}

	private void updatelist() {
		// TODO Auto-generated method stub
		searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql
				.rawQuery(
						"SELECT DISTINCT statusname,senderMobNo,materialname,reason,qty,stationname,reporteename,addedtdt,sendername FROM MaterialHistory WHERE stationname='"
								+ conn + "' ORDER BY addedtdt", null);
	//	c.moveToFirst();
		int cnt = 0;
		Log.e("Pending n/w count", "" + c.getCount());
		if (c.getCount() == 0) {

			c.close();
		} else {
			c.moveToFirst();
			do {

				String Status = c.getString(c.getColumnIndex("statusname"));
				String mob = c.getString(c.getColumnIndex("senderMobNo"));
				String qty = c.getString(c.getColumnIndex("qty"));
				String MaterialName = c.getString(c
						.getColumnIndex("materialname"));
				String reson = c.getString(c.getColumnIndex("reason"));
				String stationname = c.getString(c
						.getColumnIndex("stationname"));
				String reporty = c.getString(c.getColumnIndex("reporteename"));
				String date = c.getString(c.getColumnIndex("addedtdt"));
				String sender = c.getString(c.getColumnIndex("sendername"));

				StationEnquiryMaterialBean bean = new StationEnquiryMaterialBean();

				bean.setStatusname(Status);
				bean.setQty(qty);
				bean.setSenderMobNo(mob);
				bean.setMaterialname(MaterialName);
				bean.setReason(reson);
				bean.setStationname(stationname);
				bean.setReporteename(reporty);
				bean.setAddedtdt(date);
				bean.setSendername(sender);
				searchResults.add(bean);

			} while (c.moveToNext());
		}

		adapter = new StationEnqMaterialHistryAdaptor(getApplicationContext(),
				searchResults);
		mListView.setAdapter(adapter);

	}

	private void fetchdata() {
		// TODO Auto-generated method stub
		asynk = null;

		asynk = new DownloadxmlsDataURL_new();
		asynk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private boolean dbvalue() {
		// TODO Auto-generated method stub

		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery("SELECT  * FROM MaterialHistory WHERE stationname='"
								+ conn + "'", null);

			System.out.println("----------  dbvalue screen cursor count -- "
					+ cursor.getCount());

			if (cursor != null && cursor.getCount() > 0) {
				cursor.close();
				return true;

			} else {

				cursor.close();
				return false;
			}
		} catch (Exception e) {
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

			return false;
		}
	
	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/getMaterialHistory?stationname="
					+ conn;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = com.stavigilmonitoring.utility.httpGet(url);
				Log.e("csn status", "resmsg : " + responsemsg);
				//sql.execSQL("DROP TABLE IF EXISTS MaterialHistory");
				//sql.execSQL(ut.getMaterialHistory());
				sql.delete("MaterialHistory",null,null);

				Cursor cur = sql
						.rawQuery("SELECT * FROM MaterialHistory", null);
				Log.e("Counr----------", "" + cur.getCount());

				if (responsemsg.contains("<stationname>")) {
					sop = "valid";
					String columnName, columnValue;
					/*
					 * DatabaseHandler db = new
					 * DatabaseHandler(getBaseContext()); SQLiteDatabase sql =
					 * db.getWritableDatabase();
					 */

					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table1");
					// String msg = "";
					// String columnName, columnValue;
					Log.e("All Station data...",
							" fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							values1.put(columnName, columnValue);

							// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
						}
						sql.insert("MaterialHistory", null, values1);
					}

					cur.close();

				} else {
					sop = "invalid";
					cur.close();

				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				dff = new SimpleDateFormat("HH:mm:ss");
				Ldate = dff.format(new Date());

				StackTraceElement l = new Exception().getStackTrace()[0];
				System.out.println(l.getClassName() + "/" + l.getMethodName()
						+ ":" + l.getLineNumber());
				ut = new com.stavigilmonitoring.utility();
				if (!ut.checkErrLogFile()) {

					ut.ErrLogFile();
				}
				if (ut.checkErrLogFile()) {
					ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
							+ ":" + l.getLineNumber() + "	" + e.getMessage()
							+ " " + Ldate);
				}

			} catch (IOException e) {
				e.printStackTrace();
				dff = new SimpleDateFormat("HH:mm:ss");
				Ldate = dff.format(new Date());

				StackTraceElement l = new Exception().getStackTrace()[0];
				System.out.println(l.getClassName() + "/" + l.getMethodName()
						+ ":" + l.getLineNumber());
				ut = new com.stavigilmonitoring.utility();
				if (!ut.checkErrLogFile()) {

					ut.ErrLogFile();
				}
				if (ut.checkErrLogFile()) {
					ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
							+ ":" + l.getLineNumber() + "	" + e.getMessage()
							+ " " + Ldate);
				}

			}
			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				if (sop == "valid") {
					updatelist();
				} else {
					try{
						ut.showD(StationEnquiryMaterialRequest.this,"nodata");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				mRefresh.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);

			} catch (NullPointerException e) {
				e.printStackTrace();
				dff = new SimpleDateFormat("HH:mm:ss");
				Ldate = dff.format(new Date());

				StackTraceElement l = new Exception().getStackTrace()[0];
				System.out.println(l.getClassName() + "/" + l.getMethodName()
						+ ":" + l.getLineNumber());
				ut = new com.stavigilmonitoring.utility();
				if (!ut.checkErrLogFile()) {

					ut.ErrLogFile();
				}
				if (ut.checkErrLogFile()) {
					ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
							+ ":" + l.getLineNumber() + "	" + e.getMessage()
							+ " " + Ldate);
				}

			} catch (Exception e) {
				e.printStackTrace();
				dff = new SimpleDateFormat("HH:mm:ss");
				Ldate = dff.format(new Date());

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
		protected void onPreExecute() {
			super.onPreExecute();
			mRefresh.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		}

	}

}

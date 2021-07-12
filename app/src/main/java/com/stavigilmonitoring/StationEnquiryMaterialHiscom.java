package com.stavigilmonitoring;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.StationenqReqVSFilld;
import com.beanclasses.Material;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class StationEnquiryMaterialHiscom extends Activity {
	private ImageView mRefreshTyp;
	private ImageView msort;
	private ProgressBar mProgress;
	private TextView mText;
	private ListView mList;
	private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	private String sop, conn, type;
	private String dff, Ldate, mobno;
	private ArrayList<Material> searchResults;
	private static Downloadreqvsfill asynwork;
	StationenqReqVSFilld Adp;
	int flag = 1;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.stationenqreqvsfilled);

		msort = (ImageView) findViewById(com.stavigilmonitoring.R.id.sortbtn);
		searchResults = new ArrayList<Material>();
		mRefreshTyp = (ImageView) findViewById(com.stavigilmonitoring.R.id.btnfill);
		mProgress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.prgfill);
		mList = (ListView) findViewById(com.stavigilmonitoring.R.id.lstfill);
		mText = (TextView) findViewById(com.stavigilmonitoring.R.id.onactivitynamereassign);

		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();

		Intent i = getIntent();
		try{
			conn = i.getStringExtra("stnname");
			type = i.getStringExtra("stninst");
			mText.append(" " + conn);
		}catch (Exception e){
			e.printStackTrace();
		}

		if (dbvalue()) {
			updatelist();
		} else if (ut.isnet(getApplicationContext())) {
			fetchdata();
		} else {
			try{
				ut.showD(StationEnquiryMaterialHiscom.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		mRefreshTyp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ut.isnet(getApplicationContext())) {
					fetchdata();
				} else {
					try{
						ut.showD(StationEnquiryMaterialHiscom.this, "nonet");
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

			}
		});
		msort.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (flag == 1) {
					flag = 2;
					updatelist();
				} else if (flag == 2) {
					flag = 1;
					updatelist();
				}
			}
		});
	}

	private void fetchdata() {
		// new DownloadxmlsDataURL_new().execute();

		asynwork = new Downloadreqvsfill();
		asynwork.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public class Downloadreqvsfill extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mRefreshTyp.setVisibility(View.GONE);
			mProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {

			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String columnName, columnValue;
			String urlnet = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/getmaterialRelWorkdone?stationmasterid="
					+ type;

			urlnet = urlnet.replaceAll(" ", "%20");
			Log.e("work type", "6th" + urlnet);

			try {
				String responsemsg = ut.httpGet(urlnet);
				NodeList NL = ut.getnode(responsemsg, "Table1");
				Log.e("SubnetCount", "len :" + NL.getLength());
				//sql.execSQL("DROP TABLE IF EXISTS reqVsFilledMaterial");
				//sql.execSQL(ut.getReqVsFilldMat());
				sql.delete("reqVsFilledMaterial",null,null);

				Cursor cur1 = sql.rawQuery("SELECT * FROM reqVsFilledMaterial",
						null);

				if (responsemsg.contains("<stationname>")) {
					sop = "valid";

					ContentValues values2 = new ContentValues();

					Log.e("data...", " fetch data : " + NL.getLength());
					for (int i = 0; i < NL.getLength(); i++) {
						Element e = (Element) NL.item(i);
						Log.e(" rowcount...", " fetch data : " + i);
						for (int j = 0; j < cur1.getColumnCount(); j++) {
							columnName = cur1.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							Log.e(" columnValue...", " fetch data : "
									+ columnValue);
							values2.put(columnName, columnValue);
							// SubnetString = "Valid";

						}
						long ad = sql.insert("reqVsFilledMaterial", null,
								values2);
						Log.e(" tableinsert...", " fetch data : " + ad);
					}

					//cur1.close();

				} else {
					sop = "Invalid";
					//cur1.close();
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				sop = "Invalid";
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (sop.equalsIgnoreCase("valid")) {
				updatelist();

			} else {
				try{
					ut.showD(StationEnquiryMaterialHiscom.this, "nodata");
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			mRefreshTyp.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);
		}

	}

	private void updatelist() {
		searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		int count = 0;
		Cursor c = null;
		if (flag == 1) {
			c = sql.rawQuery(
					"SELECT stationname,stationmasterid,addedtdt,sendermobno,receiveddate,sendername,materialname,qty,materialname1,stationname1,mobileno,currentdate FROM reqVsFilledMaterial WHERE stationmasterid='"
							+ type + "' ORDER BY receiveddate DESC", null);
		} else if (flag == 2) {
			c = sql.rawQuery(
					"SELECT stationname,stationmasterid,addedtdt,sendermobno,receiveddate,sendername,materialname,qty,materialname1,stationname1,mobileno,currentdate FROM reqVsFilledMaterial WHERE stationmasterid='"
							+ type + "' ORDER BY materialname", null);
		}

		if (c.getCount() > 0) {
			c.moveToFirst();
			do {
				String Stationname = c.getString(c
						.getColumnIndex("stationname"));
				String stationmasterid = c.getString(c
						.getColumnIndex("stationmasterid"));
				String materialfilleddate = c.getString(c
						.getColumnIndex("addedtdt"));
				String receivedmob = c.getString(c
						.getColumnIndex("sendermobno"));
				String materialreceiveddate = c.getString(c
						.getColumnIndex("receiveddate"));
				materialreceiveddate = splitdate(materialreceiveddate);
				String receiverdate = c.getString(c
						.getColumnIndex("sendername"));
				String receivername = c.getString(c
						.getColumnIndex("materialname"));
				String quty = c.getString(c.getColumnIndex("qty"));
				String currentdate = c.getString(c
						.getColumnIndex("currentdate"));// currentdate

				currentdate = split(currentdate);
				String filldmaterial = c.getString(c
						.getColumnIndex("materialname1"));
				String fillmob = c.getString(c.getColumnIndex("mobileno"));
				Material bean = new Material();
				bean.setStationname(Stationname);
				bean.setStationmasterid(stationmasterid);
				bean.setMaterialfilleddate(materialfilleddate);
				bean.setReceivedmob(receivedmob);
				bean.setMaterialreceived(materialreceiveddate);
				bean.setReceiverdate(receiverdate);
				bean.setReceivername(receivername);
				bean.setQuty(quty);
				bean.setFilldmaterial(filldmaterial);
				bean.setFillmob(fillmob);
				bean.setCurrentdate(currentdate);

				searchResults.add(bean);

			} while (c.moveToNext());

			//c.close();
		}

		Adp = new StationenqReqVSFilld(getApplicationContext(), searchResults);
		mList.setAdapter(Adp);

	}

	private String splitdate(String data) {
		// TODO Auto-generated method stub
		if (data == null || data.equalsIgnoreCase("")) {
			return "";

		} else {
			// data = data.replace("T", " ");
			// String s = data.substring(0, data.indexOf("+"));
			Date conn = null;
			String dat = "";
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"MM/dd/yyyy hh:mm:ss aa");// 2016-05-12T20:36:08+05:30

				conn = dateFormat.parse(data);
				SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd MMM yyyy  hh:mm:ss aa");
				dat = dateFormat1.format(conn);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return dat;

		}
	}

	private String split(String data) {
		// TODO Auto-generated method stub
		if (data == null || data.equalsIgnoreCase("")) {
			return "";

		} else {
			data = data.replace("T", " ");
			String s = data.substring(0, data.indexOf("+"));

			Date conn = null;
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");// 2016-05-12T20:36:08+05:30

				conn = dateFormat.parse(s);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SimpleDateFormat dateFormat1 = new SimpleDateFormat(
					"dd MMM yyyy  hh:mm:ss aa");
			String dat = dateFormat1.format(conn);

			return dat;

		}
	}

	private boolean dbvalue() {

		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			// sql.execSQL(ut.getReqVsFilldMat());
			Cursor cursor = sql.rawQuery(
					"SELECT * FROM reqVsFilledMaterial WHERE stationmasterid='"
							+ type + "'", null);// SoundLevel_new

			System.out.println("----------  dbvalue screen cursor count -- "
					+ cursor.getCount());

			if (cursor != null && cursor.getCount() > 0) {

				//cursor.close();
				return true;

			} else {

				//cursor.close();
				return false;
			}
		} catch (Exception e) {

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

}

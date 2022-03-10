package com.stavigilmonitoring;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.StationEnqBusrepAdaptor;
import com.beanclasses.StationEnqBusBean;
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
import android.widget.Toast;

public class StationEnquiryBusReporting extends Activity {
	private ArrayList<StationEnqBusBean> searchResults;
	private ImageView mRefresh;
	private GridView mListView;
	ImageView busrprt_30days, busrprt_7days;
	private ProgressBar mProgressBar;
	private LinearLayout mAllnet, lay_7_30_days_rprt;
	private TextView mText, txt_loadprevious;
	private TextView mAllCount;
	private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	static SimpleDateFormat dff;
	static String Ldate;
	private String mobno, type, conn, DAYS_cnt;
	static DownloadxmlsDataURL_new asynk;
	String responsemsg, Syncdate, sop, urlnet;
	int scount = 0;
	private StationEnqBusrepAdaptor adapter;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-gener2ated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.stationenqbusreporting);

		mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.buttn_refresh_work_his);
		mProgressBar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar_work);
		mText = (TextView) findViewById(com.stavigilmonitoring.R.id.work_Station);
		mListView = findViewById(com.stavigilmonitoring.R.id.worklist);
		mListView.setVisibility(View.VISIBLE);
        txt_loadprevious = (TextView)findViewById(R.id.txt_loadprevious);
		txt_loadprevious.setVisibility(View.GONE);

		lay_7_30_days_rprt = (LinearLayout)findViewById(R.id.lay_7_30_days_rprt);
		lay_7_30_days_rprt.setVisibility(View.GONE);

		busrprt_7days = (ImageView)findViewById(R.id.busrprt_7days);
		busrprt_30days = (ImageView)findViewById(R.id.busrprt_30days);

		searchResults = new ArrayList<StationEnqBusBean>();

		db = new DatabaseHandler(getApplicationContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();

		Intent i = getIntent();
		conn = i.getStringExtra("stnname");
		type = i.getStringExtra("stninst");
		mText.append(" " + conn);

		mRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ut.isnet(getApplicationContext())) {
					fetchdata(DAYS_cnt);
				} else {
					ut.showD(StationEnquiryBusReporting.this, "nonet");
				}
 			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				String InstId = type;
				String date = searchResults.get(position).getDate();
				SimpleDateFormat spf=new SimpleDateFormat("dd MMM, yyyy");
				Date newDate= null;
				try {
					newDate = spf.parse(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				spf= new SimpleDateFormat("yyyy-MM-dd");
				date = spf.format(newDate);
				System.out.println(date);
				int cnt = searchResults.get(position).getBusCnt();
				Intent intent = new Intent(StationEnquiryBusReporting.this, BusReportingDetails.class);
				intent.putExtra("date",date);
				intent.putExtra("InstallationId",type);
				intent.putExtra("stnname",conn);
				startActivity(intent);
			}
		});

        txt_loadprevious.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Load previous records...",Toast.LENGTH_SHORT).show();
            }
        });

		/*busrprt_30days.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListView.setVisibility(View.VISIBLE);
				//searchResults.clear();
				Toast.makeText(StationEnquiryBusReporting.this,
						"30 Days Bus Reporting", Toast.LENGTH_SHORT).show();
				//DAYS_cnt = "30";
				fetchdata("30");
			}
		});

		busrprt_7days.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//searchResults.clear();
				mListView.setVisibility(View.VISIBLE);
				Toast.makeText(StationEnquiryBusReporting.this,
						"7 Days Bus Reporting", Toast.LENGTH_SHORT).show();
				//DAYS_cnt = "7";
				fetchdata("7");
			}
		});*/

		if (dbvalue()) {
			updatelist();
		} else if (ut.isnet(getApplicationContext())) {
			fetchdata("7");
		} else {
			ut.showD(StationEnquiryBusReporting.this, "nonet");
		}
	}

	private void updatelist() {
		// TODO Auto-generated method stub
		searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery(
				"SELECT dateofbus,countofbus FROM BusReporting WHERE Installationid='"
						+ type + "' ORDER BY dateofbus Desc", null);

		// c.moveToFirst();
		int cnt = 0;
		Log.e("Pending n/w count", "" + c.getCount());
		if (c.getCount() == 0) {

			c.close();
		} else {
			c.moveToFirst();
			do {

				String Status = c.getString(c.getColumnIndex("dateofbus"));
				int mob = c.getInt(c.getColumnIndex("countofbus"));

				String date1 = split(Status);

				StationEnqBusBean bean = new StationEnqBusBean();

				bean.setDate(date1);
				bean.setBusCnt(mob);
				searchResults.add(bean);

			} while (c.moveToNext());
		}

		adapter = new StationEnqBusrepAdaptor(getApplicationContext(), searchResults);
		mListView.setAdapter(adapter);

	}
	private String splitdata(String data) {
		// TODO Auto-generated method stub
		Date conn1 = null;
		try {
			data = data.substring(0,data.indexOf("T"));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 2016-08-01T00:00:00+05:30
		//	dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			conn1 = dateFormat.parse(data);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		String dat = dateFormat1.format(conn1);

		return dat;

	}
	private String split(String data) {
		// TODO Auto-generated method stub
		Date conn1 = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 2016-05-12T20:36:08+05:30//09/05/2016

			conn1 = dateFormat.parse(data);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd MMM, yyyy");
		String dat = dateFormat1.format(conn1);

		return dat;

	}

	private void fetchdata(String days) {
		// TODO Auto-generated method stub
		asynk = null;
		DAYS_cnt = days;
		asynk = new DownloadxmlsDataURL_new(DAYS_cnt);
		asynk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private boolean dbvalue() {
		// TODO Auto-generated method stub

		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery(
					"SELECT  * FROM BusReporting Where Installationid='" + type
							+ "'", null);

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

		public DownloadxmlsDataURL_new(String days_cnt) {
			DAYS_cnt = days_cnt;
		}

		@Override
		protected String doInBackground(String... params) {

			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetBustreportingsavendatecount?InstallationId="
					+ type + "&days="+DAYS_cnt;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = com.stavigilmonitoring.utility.httpGet(url);
				Log.e("csn status", "resmsg : " + responsemsg);
				//sql.execSQL("DROP TABLE IF EXISTS BusReporting");
				//sql.execSQL(ut.getBusReporting());
				sql.delete("BusReporting",null,null);

				Cursor cur = sql.rawQuery("SELECT * FROM BusReporting", null);
				Log.e("Counr----------", "" + cur.getCount());

				if (responsemsg.contains("<dateofbus>")) {
					sop = "valid";
					String columnName, columnValue;
					/*
					 * DatabaseHandler db = new
					 * DatabaseHandler(getBaseContext()); SQLiteDatabase sql =
					 * db.getWritableDatabase();
					 */

					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table");
					// String msg = "";
					// String columnName, columnValue;
					Log.e("All Station data...",
							" fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);
							columnValue = ut.getValue(e, columnName);
							if (columnName.equalsIgnoreCase("dateofbus")) {
								//co
								columnValue = splitdata(columnValue);

							}

							if (columnValue == null
									|| columnValue.equalsIgnoreCase("")
									|| columnValue.equalsIgnoreCase("null")) {
								columnValue = "No Info";
							}
							values1.put(columnName, columnValue);

							// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
						}
						sql.insert("BusReporting", null, values1);
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
					ut.showD(StationEnquiryBusReporting.this, "nodata");
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

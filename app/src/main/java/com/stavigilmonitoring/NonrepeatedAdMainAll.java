package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.NonrepeatedAdAdaptMain;
import com.beanclasses.NonrepeatedAdHelper;
import com.beanclasses.NonreportedList;
import com.database.DBInterface;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NonrepeatedAdMainAll extends Activity {

	NonrepeatedAdAdaptMain listAdapter;
	ListView workspacewisedetail;
	String mobno, link;
	// ArrayList<String>arrlist=new ArrayList<String>();
	List<NonreportedList> lst_nonrpt = new ArrayList<NonreportedList>();
	AsyncTask depattask;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv,btnadd;
	static SimpleDateFormat dff;
	static String Ldate;
	static DownloadxmlsDataURL_new asyncfetch_non;
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;

	String daterestr;

	private ListView nonrepeated;
	private String st;
	private String cou;
	private String adds;
	private String sv, Type, CallFrom;
	String z = "";
	private String z1, subType;
	private int nonreportadCount = 0;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.nonrepeatedadmain);
		Bundle extras = getIntent().getExtras();
		Type = extras.getString("Type");
		subType = extras.getString("SubType");
		CallFrom = extras.getString("CallFrom");

		((TextView) findViewById(com.stavigilmonitoring.R.id.onactivitynamereassign)).setText("Non Reported Advertisements - "+ Type);
		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_nonrepeated_main);
		btnadd = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_alert_add);
		btnadd.setImageResource(com.stavigilmonitoring.R.drawable.work_assign);
		btnadd.setVisibility(View.VISIBLE);
		nonrepeated = (ListView) findViewById(com.stavigilmonitoring.R.id.nonrepeatedadmain);

		db = new DatabaseHandler(getApplicationContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();

		if (asyncfetch_non != null
				&& asyncfetch_non.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

		if (dbvalue()) {
			updatelist();
		} else if (ut.isnet(getApplicationContext())) {
			fetchdata();
		} else {
			try{
				ut.showD(NonrepeatedAdMainAll.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
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

						listAdapter
								.filter(((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
										.getText().toString().trim()
										.toLowerCase(Locale.getDefault()));
					}
				});

		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (ut.isnet(getApplicationContext())) {
					asyncfetch_non = null;
					asyncfetch_non = new DownloadxmlsDataURL_new();
					asyncfetch_non.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

					// fetchdata();
				} else {
					try{
						ut.showD(NonrepeatedAdMainAll.this, "nonet");
					}catch (Exception e){
						e.printStackTrace();
					}
				}

			}
		});
		btnadd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),WorkAssign_AssignActivity.class);
				intent.putExtra("Activity", "NonReportedAdStatewise");
				intent.putExtra("Type", "");
				startActivity(intent);
				finish();
			}
		});

		try{
			if(CallFrom.equalsIgnoreCase("SupporterListAll")){
				nonrepeated.setClickable(false);
			}else if(CallFrom.equalsIgnoreCase("NonrepeatedAdFilter")){
				nonrepeated.setClickable(true);
				nonrepeated.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> a, View v, int position,
											long id) {

						if (stationpresent()) {

							NonreportedList item = lst_nonrpt.get(position);
							Bundle dataBundle = new Bundle();
							dataBundle.putString("stnname", item.getInstallationDesc());
							Intent myIntent = new Intent();
							myIntent.setClass(getApplicationContext(),
									com.stavigilmonitoring.NonrepeatedAd.class);
							myIntent.putExtras(dataBundle);
							startActivity(myIntent);

						} else {
							Toast.makeText(getBaseContext(),
									"No Advertisement Present..", Toast.LENGTH_LONG)
									.show();
						}
					}
				});
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}



	public void editActivity(String StationName) {
		int i = 0;
		for (i = 1; i <= StationName.length(); i++) {
			char k = StationName.charAt(i - 1);
			sv = String.valueOf(k);
			System.out.println("...................value of sv is" + sv);
			if (sv.equals("\n")) {
				break;
			} else {
				z = z + sv;
				z1 = z;
			}
		}
		//DatabaseHandler db1 = new DatabaseHandler(this);
		SQLiteDatabase sqldb = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = z1;
		Cursor c2 = sqldb.rawQuery(
						"SELECT * FROM NonrepeatedAd where InstallationDesc=? ",
						params);
		String stnname = "";

		if (c2.getCount() == 0) {
			c2.close();
		} else {
			c2.moveToFirst();
			stnname = c2.getString(c2.getColumnIndex("InstallationDesc"));
			c2.moveToLast();
			c2.close();
		}

		Bundle dataBundle = new Bundle();
		dataBundle.putString("stnname", stnname);
		Intent myIntent = new Intent();
		myIntent.setClass(getApplicationContext(), NonrepeatedAd.class);

		myIntent.putExtras(dataBundle);
		startActivity(myIntent);

	}

	protected boolean stationpresent() {
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM NonrepeatedAd", null);

		int count = c.getCount();

		c.close();

		if (count == 0) {
			return false;
		} else {
			return true;
		}

	}

	private boolean dbvalue() {
		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery("SELECT *   FROM NonrepeatedAd", null);

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

	public void FilterClick(View v) {
		if (((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext)).getVisibility() == View.VISIBLE) {
			((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
					.setVisibility(View.GONE);
		} else if (((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext)).getVisibility() == View.GONE) {
			((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
					.setVisibility(View.VISIBLE);
			EditText textView = (EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext);
			textView.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
		}

	}

	private void updatelist() {
		final List<NonreportedList> searchResults = getDetail();
		Log.e("non reported.......", "count : " + searchResults.size());
		// listAdapter=null;
		if(searchResults.isEmpty()){
			try{
				ut.showD(this,"nodata");
			}catch (Exception e){
				e.printStackTrace();
			}
		}else {
			listAdapter = new NonrepeatedAdAdaptMain(this, searchResults);
			nonrepeated.setAdapter(listAdapter);
		}


		// List<String> items=new ArrayList<String>();
		/*
		 * for(int i=0;i<searchResults.size(); i++) {
		 * items.add(searchResults.get(i).getInstallationDesc()); }
		 * MySpinnerAdapter customAdcity = new
		 * MySpinnerAdapter(NonrepeatedAdMain.this, R.layout.filterview,items );
		 * ((AutoCompleteTextView)findViewById(R.id.edfitertext)).setAdapter(
		 * customAdcity);
		 * ((AutoCompleteTextView)findViewById(R.id.edfitertext)).
		 * setThreshold(1);
		 */

	}

	private static class MySpinnerAdapter extends ArrayAdapter<String> {
		// Initialise custom font, for example:
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"font/BOOKOS.TTF");

		private MySpinnerAdapter(Context context, int resource,
				List<String> items) {
			super(context, resource, items);
		}

		// Affects default (closed) state of the spinner
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = (TextView) super.getView(position, convertView,
					parent);
			view.setTypeface(font);
			return view;
		}

		// Affects opened state of the spinner
		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			TextView view = (TextView) super.getDropDownView(position,
					convertView, parent);
			view.setTypeface(font);
			return view;
		}
	}

	private List<NonreportedList> getDetail() {

		lst_nonrpt.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		// SELECT DISTINCT InstallationDesc, InstallationCount FROM
		// NonrepeatedAd
		/*Cursor c = sql
				.rawQuery(
						"SELECT * FROM NonrepeatedAd a INNER JOIN ConnectionStatusFilter b ON a.StationMasterId=b.InstalationId WHERE b.NetworkCode='"
								+ Type + "' ORDER BY a.InstallationDesc",
						null);*/

		Cursor c = sql
				.rawQuery(
						"SELECT * FROM NonrepeatedAd a INNER JOIN ConnectionStatusFilter b ON a.StationMasterId=b.InstalationId WHERE b.NetworkCode='"
								+ Type + "' ORDER BY a.LastServerTime",
						null);

		// Select DISTINCT s.InstallationDesc , s.CallibrationDate, s.AO ,
		// s.InstalationId ,s.ServerTime from SoundLevel_new s INNER JOIN
		// SoundLevel s1 ON s1.InstallationId = s.InstalationId INNER JOIN
		// ConnectionStatusFilter S2 ON S2.InstalationId=s.InstalationId WHERE
		// S2.SubNetworkCode='"+ subType + "' ORDER BY s.InstallationDesc
		if (c.getCount() == 0) {
			lst_nonrpt.clear();
			c.close();

			return lst_nonrpt;
		} else {
			Log.e("get details.....", "cursor stn count : " + c.getCount());
			c.moveToFirst();

			int column = 0;
			do {
				st = c.getString(c.getColumnIndex("InstallationDesc"));
				cou = c.getString(c.getColumnIndex("InstallationCount"));
				adds = st + "\n" + cou;

				if (lst_nonrpt.size() == 0) {
					Log.e("k ins cnt",
							" "
									+ c.getString(c
											.getColumnIndex("InstallationCount")));
					lst_nonrpt.add(new NonreportedList(c.getString(c
							.getColumnIndex("InstallationDesc")), c.getString(c
							.getColumnIndex("InstallationCount")), c
							.getString(c.getColumnIndex("LastServerTime"))));
					if (!c.getString(c.getColumnIndex("InstallationCount"))
							.trim().equalsIgnoreCase(""))
						nonreportadCount = nonreportadCount
								+ Integer.parseInt(c.getString(c
										.getColumnIndex("InstallationCount")));

					Log.e("get details.....",
							"add STn : "
									+ c.getString(c
											.getColumnIndex("InstallationDesc")));
				} else {
					int dflag = 0;
					for (int j = 0; j < lst_nonrpt.size(); j++) {
						if (lst_nonrpt
								.get(j)
								.getInstallationDesc()
								.equalsIgnoreCase(
										c.getString(c
												.getColumnIndex("InstallationDesc")))) {
							dflag = 1;
							break;
						}
					}
					if (dflag == 0) {
						lst_nonrpt
								.add(new NonreportedList(
										c.getString(c
												.getColumnIndex("InstallationDesc")),
										c.getString(c
												.getColumnIndex("InstallationCount")),
										c.getString(c
												.getColumnIndex("LastServerTime"))));
						Log.e("get details.....",
								"add STn : "
										+ c.getString(c
												.getColumnIndex("InstallationDesc")));

						nonreportadCount = nonreportadCount
								+ Integer.parseInt(c.getString(c
										.getColumnIndex("InstallationCount")));
					}
				}

			} while (c.moveToNext());

			c.close();

		}

		Log.e("get details.....", "lst stn count : " + lst_nonrpt.size());

		return lst_nonrpt;
	}

	private ArrayList<NonrepeatedAdHelper> GetDetail() {
		ArrayList<NonrepeatedAdHelper> results = new ArrayList<NonrepeatedAdHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM NonrepeatedAd", null);
		if (c.getCount() == 0) {
			NonrepeatedAdHelper sr = new NonrepeatedAdHelper();
			sr.setadvcode("");
			sr.setadvName("");
			results.add(sr);
			c.close();

			return results;
		} else {

			c.moveToFirst();

			int column = 0;
			do {

				NonrepeatedAdHelper sr = new NonrepeatedAdHelper();
				String s = sr.setadvcode(c.getString(c
						.getColumnIndex("InstallationDesc")));

				String n1 = findCount(s);

			} while (c.moveToNext());

			c.close();
		}
		return results;

	}

	private String findCount(String s) {
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = s;
		Cursor c2 = sql
				.rawQuery(
						"SELECT * FROM NonrepeatedAd where InstallationDesc=? ",
						params);
		int count = c2.getCount();

		String n = String.valueOf(count);
		c2.close();
		return n;
	}

	private void fetchdata() {
		// new DownloadxmlsDataURL_new().execute();
		asyncfetch_non = new DownloadxmlsDataURL_new();
		asyncfetch_non.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String xx = "";

			// String
			// url="http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetNonReportedAdvt_Android_new?Mobile="+mobno;
			String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetNonReportedAdvt_Android_new?Mobile="
					+ mobno;
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);

				if (responsemsg.contains("<A>")) {
					sop = "valid";
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());

					SQLiteDatabase sql = db.getWritableDatabase();

					//sql.execSQL("DROP TABLE IF EXISTS NonrepeatedAd");
					//sql.execSQL(ut.getNonrepeatedAd());
					sql.delete("NonrepeatedAd",null,null);

					Cursor c = sql.rawQuery("SELECT *  FROM NonrepeatedAd",
							null);

					ContentValues values = new ContentValues();
					NodeList nl = ut.getnode(responsemsg, "Table1");
					String msg = "";
					String columnName, columnValue;
					Log.e("non reported.....in back..","...count : " + nl.getLength());

					for (int i = 0; i < nl.getLength(); i++) {
						Element e = (Element) nl.item(i);
						for (int j = 0; j < c.getColumnCount(); j++) {

							columnName = c.getColumnName(j);

							String ncolumnname = "";
							if (columnName.equalsIgnoreCase("StationMasterId"))
								ncolumnname = "A";
							else if (columnName
									.equalsIgnoreCase("AdvertisementCode"))
								ncolumnname = "B";
							else if (columnName
									.equalsIgnoreCase("AdvertisementDesc"))
								ncolumnname = "C";
							else if (columnName
									.equalsIgnoreCase("InstallationDesc"))
								ncolumnname = "D";
							else if (columnName
									.equalsIgnoreCase("EffectiveDateFrom"))
								ncolumnname = "E";
							else if (columnName
									.equalsIgnoreCase("EffectiveDateTo"))
								ncolumnname = "F";
							else if (columnName.equalsIgnoreCase("Type"))
								ncolumnname = "G";
							else if (columnName.equalsIgnoreCase("ClipId"))
								ncolumnname = "H";
							else if (columnName
									.equalsIgnoreCase("IsmasterRecordDownloaded"))
								ncolumnname = "I";
							else if (columnName
									.equalsIgnoreCase("IsDetailRecordDownloaded"))
								ncolumnname = "J";
							else if (columnName
									.equalsIgnoreCase("IsClipMasterRecordDownloaded"))
								ncolumnname = "K";
							else if (columnName
									.equalsIgnoreCase("InstallationCount"))
								ncolumnname = "L";
							else if (columnName
									.equalsIgnoreCase("LastServerTime"))
								ncolumnname = "M";
							else if (columnName
									.equalsIgnoreCase("FirstReportingDate"))
								ncolumnname = "N";
							else if (columnName
									.equalsIgnoreCase("LatestAddeDate"))
								ncolumnname = "O";
							else if (columnName.equalsIgnoreCase("CSR"))
								ncolumnname = "CSR";
							else if (columnName.equalsIgnoreCase("LA"))
								ncolumnname = "LA";
							else if (columnName.equalsIgnoreCase("LB"))
								ncolumnname = "LB";
							else if (columnName.equalsIgnoreCase("LBR"))
								ncolumnname = "LBR";

							columnValue = ut.getValue(e, ncolumnname);
							values.put(columnName, columnValue);
						}
						sql.insert("NonrepeatedAd", null, values);
						Log.d("test", "data NonrepeatedAd:" + values);
					}

					c.close();

				} else if (responsemsg.contains("<NewDataSet/>")) {
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());
					SQLiteDatabase sql = db.getWritableDatabase();
					//sql.execSQL("DROP TABLE IF EXISTS NonrepeatedAd");
					//sql.execSQL(ut.getNonrepeatedAd());
					sql.delete("NonrepeatedAd",null,null);

					sop = "valid";
				} else {
					sop = "invalid";
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

			return sop;
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
						ut.showD(NonrepeatedAdMainAll.this, "nodata");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
						.setVisibility(View.GONE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}
	}

	/*
	 * private void updaterefreshdate() { // TODO Auto-generated method stub
	 * 
	 * Calendar c = Calendar.getInstance();
	 * 
	 * SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); String
	 * formattedDate = df.format(c.getTime());
	 * 
	 * System.out.println("------ curdaterefresh " + formattedDate);
	 * 
	 * String[] aDate = { formattedDate };
	 * 
	 * DBInterface db = new DBInterface(getBaseContext());
	 * db.SetDaterefresh(aDate); db.Close();
	 * 
	 * filldaterefresh();
	 * 
	 * }
	 */

	/*
	 * private void filldaterefresh() { // TODO Auto-generated method stub
	 * 
	 * System.out.println("-------  filldateref " + daterestr);
	 * 
	 * if (daterestr.equals("1")) { txtdate.setVisibility(View.INVISIBLE);
	 * txtdaterefresh.setVisibility(View.INVISIBLE); } else {
	 * 
	 * try {
	 * 
	 * String olddate = getolddate();
	 * 
	 * System.out.println("-------  olddate " + olddate);
	 * 
	 * Calendar c = Calendar.getInstance();
	 * 
	 * SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
	 * String formattedDate = df.format(c.getTime());
	 * 
	 * System.out.println("------ curdaterefresh " + formattedDate); String diff
	 * = getTimeDiff(olddate, formattedDate); System.out.println("----- ##### "
	 * + diff);
	 * 
	 * if ((diff.contains("seconds ago")) || (diff.contains("minutes ago"))) {
	 * txtdate.setVisibility(View.INVISIBLE);
	 * txtdaterefresh.setVisibility(View.INVISIBLE);
	 * 
	 * } else { System.out.println("----- ##### 2 " + diff);
	 * 
	 * if (diff.equals("yesterday")) { String refdate = "1 day old data";
	 * txtdate.setText(refdate); } else if (diff.contains("ago")) {
	 * 
	 * String[] sar = diff.split(" "); String a = sar[0].toString(); int i =
	 * Integer.parseInt(a);
	 * 
	 * if (i > 8) { txtdate.setText(" 1 day old data"); } else { String ref[] =
	 * diff.split("ago");
	 * 
	 * String refdate = ref[0].toString();
	 * System.out.println("--- #### refdate " + refdate);
	 * 
	 * txtdate.setText(refdate + "old data"); }
	 * 
	 * } else { txtdate.setText(diff + "old data"); } }
	 * 
	 * } catch (Exception e) { dff = new SimpleDateFormat("HH:mm:ss"); Ldate =
	 * dff.format(new Date());
	 * 
	 * StackTraceElement l = new Exception().getStackTrace()[0];
	 * System.out.println(l.getClassName() + "/" + l.getMethodName() + ":" +
	 * l.getLineNumber()); ut = new utility(); if (!ut.checkErrLogFile()) {
	 * 
	 * ut.ErrLogFile(); } if (ut.checkErrLogFile()) {
	 * ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":" +
	 * l.getLineNumber() + "	" + e.getMessage() + " " + Ldate); }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * }
	 */

	/*
	 * private String getTimeDiff(String time, String curTime) throws
	 * ParseException { DateFormat formatter; Date curDate; Date oldDate;
	 * formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); curDate = (Date)
	 * formatter.parse(curTime); oldDate = (Date) formatter.parse(time); long
	 * oldMillis = oldDate.getTime(); long curMillis = curDate.getTime(); //
	 * Log.d("CaseListAdapter", "Date-Milli:Now:"+curDate.toString()+":" //
	 * +curMillis +" old:"+oldDate.toString()+":" +oldMillis); CharSequence text
	 * = DateUtils.getRelativeTimeSpanString(oldMillis, curMillis, 0); return
	 * text.toString(); }
	 */

	/*
	 * private String getolddate() { // TODO Auto-generated method stub
	 * 
	 * DBInterface dbi = new DBInterface(getBaseContext()); String dateref =
	 * dbi.GetDateRefresg(); dbi.Close(); return dateref; }
	 */

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = getApplicationContext()
				.getSharedPreferences("SCROLLNON", Context.MODE_PRIVATE);
		int scroll = preferences.getInt("ScrollValueNon", 0);
		System.out
				.println(".............value of scroll at resume>>>>>>>>>>>>>>>>>>>"
						+ scroll);
		// connectionstatus.scrollTo(0, scroll);
		nonrepeated.smoothScrollToPosition(scroll);
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SharedPreferences preferences = getApplicationContext()
				.getSharedPreferences("SCROLLNON", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		int scroll = nonrepeated.getFirstVisiblePosition();
		// int scrollx = connectionstatus.getScrollX();
		System.out.println(".............value of scroll>>>>>>>>>>>>>>>>>>>"
				+ scroll);
		editor.putInt("ScrollValueNon", scroll);
		editor.commit();
		// finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		/*
		 * try{
		 * 
		 * SharedPreferences pref =
		 * getApplicationContext().getSharedPreferences("MyPrefnon",
		 * MODE_PRIVATE); // 0 - for private mode Editor editor = pref.edit();
		 * System
		 * .out.println("----------total number of item in list"+nonrepeated
		 * .getCount() ); icount=nonrepeated.getCount();
		 * scountnon=String.valueOf(icount);
		 * 
		 * editor.putString("nonreportedStatus", scountnon);
		 * editor.putString("advCount", nonreportadCount+""); editor.commit();
		 * System.out.println("----------scount"+scountnon ); }catch(Exception
		 * edf) {
		 * 
		 * } // Bundle dataBundle = new Bundle(); //
		 * dataBundle.putString("ActivityId", ActivityId); //
		 * dataBundle.putString("ActivityName", ActivityName); Intent i = new
		 * Intent(getBaseContext(), SelectMenu.class);
		 * i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //
		 * i.putExtras(dataBundle); getBaseContext().startActivity(i); finish();
		 */

		/*
		 * Intent i = new Intent(getBaseContext(),
		 * NonReportedAdStatewise.class);
		 * i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(i);
		 */
	}

}

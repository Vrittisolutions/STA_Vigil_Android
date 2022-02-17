package com.stavigilmonitoring;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.DowntimeAdaptMain;
import com.beanclasses.DowntimeHelper;
import com.database.DBInterface;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DowntimeMain extends Activity {
	
	ProgressDialog pd;
	static SimpleDateFormat dff;
	static String Ldate;
	String mobno, link;
	AsyncTask depattask;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv;
DowntimeAdaptMain listAdapter;
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;
	private ListView connectionstatus;
	private ListView nonrepeated;
	ArrayList<String> assignedlist = new ArrayList<String>();
	ArrayList<String> requirelist = new ArrayList<String>();
	String Type;
	static DownloadxmlsDataURL asyncfetch;
	ArrayList<String>arrlist=new ArrayList<String>();
	private TextView title;
	private AutoCompleteTextView autoReason;
    Intent intent;
	private ListView downtime;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.downtimemain);

		downtime = (ListView) findViewById(R.id.downtimetimelist);
		iv=(ImageView)findViewById(R.id.button_refresh_downtime_main);
		title=(TextView)findViewById(R.id.title);
		
//		reason=(TextView)findViewById(R.id.tvdowntimereason);
//		autoReason=(AutoCompleteTextView)findViewById(R.id.autocompletereason);
//		Bundle extras = getIntent().getExtras();
//		ActivityName = extras.getString("ActivityName");

		intent=getIntent();
		Type=intent.getStringExtra("Type");
		title.setText(Type+"-Downtime Station");

		db = new DatabaseHandler(getApplicationContext());
		
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		//link = dbi.GetUrl();
//		daterestr = dbi.GetDateRefresg();
		dbi.Close();
		if(asyncfetch!=null && asyncfetch.getStatus() == AsyncTask.Status.RUNNING)
		{
			Log.e("async","running");
			iv.setVisibility(View.GONE);
			((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
		}
		
	//	System.out.print("Link value"+link);
		
//		if (net()) {

			if (dbvalue()) {
				updatelist();
			} else if(isnet()){

			fetchdata();
			}
			else {
				showD("nonet");
			}
			
//		} else {
//			showD("nonet");
//		}
		
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isnet()) {
					asyncfetch=null;
					asyncfetch=new DownloadxmlsDataURL();
					asyncfetch.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					showD("nonet");
				}
				
			}
		});
		
		downtime.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {

				if (dbvalue()) {
//					Object o = nonrepeated.getItemAtPosition(position);
//					DowntimeHelper fullObject = (DowntimeHelper) o;
					
					
					 String s = arrlist.get(position);
					
					editActivity(s);

				} else {

					Toast.makeText(getBaseContext(), "No Advertisement Present..",
							Toast.LENGTH_LONG).show();
				}

			}
		});
		
		
((EditText)findViewById(R.id.edfitertext)).addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {					
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {					
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				//listAdapter=new DowntimeAdaptMain(DowntimeMain.this,arrlist);
				listAdapter.filter(((EditText)findViewById(R.id.edfitertext)).getText().toString().trim().toLowerCase(Locale.getDefault()));
			}
		});
		
//		filldaterefresh();
//		
//		iv.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//				if (net()) {
//
//					fetchfromserver();
//
//				} else {
//					showD("nonet");
//				}
//
//			}
//		});
	}
	
	public void FilterClick(View v)
	{
		if(  ((EditText)findViewById(R.id.edfitertext)).getVisibility() == View.VISIBLE )
		{
			((EditText)findViewById(R.id.edfitertext)).setVisibility(View.GONE);
			InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
		else 	if(  ((EditText)findViewById(R.id.edfitertext)).getVisibility() == View.GONE )
		{
			((EditText)findViewById(R.id.edfitertext)).setVisibility(View.VISIBLE);
			EditText textView = (EditText ) findViewById(R.id.edfitertext);
			textView.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
		}
		
	}
	
	private boolean isnet() {
		// TODO Auto-generated method stub
		Context context = this.getApplicationContext();
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
	public void editActivity(String StationName) {
		
		System.out.println("==========@#@# actid " + StationName);
		//DatabaseHandler db1 = new DatabaseHandler(this);
		SQLiteDatabase sqldb = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = StationName;
		Cursor c2 = sqldb.rawQuery(
				"SELECT * FROM Downtime where InstallationDesc='"+StationName+"'",null);
		String stnname = "";
		
		if(c2.getCount()==0)
		{
			c2.close();/*
			db.close();
			db1.close();*/
	
		}
		else
		{

			c2.moveToFirst();
			stnname = c2.getString(c2.getColumnIndex("InstallationDesc"));
			
			c2.moveToLast();

			c2.close();/*
			db.close();
			db1.close();*/
	
			
		}

		
		// MOMA --- mom attend request
		// System.out.println("----------  type --- " + type);

		Bundle dataBundle = new Bundle();
		dataBundle.putString("stnname", StationName);
		//dataBundle.putString("ActivityName", ActivityName);

		// finish();
		Intent myIntent = new Intent();
		myIntent.setClass(getApplicationContext(), Downtime.class);

		myIntent.putExtras(dataBundle);
		startActivity(myIntent);
		//finish();
		// System.out.println("------------- 1");

	}
	
	private boolean dbvalue() {
		// TODO Auto-generated method stub
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql.rawQuery(
				"SELECT *   FROM Downtime", null);

		System.out.println("----------  dbvalue screen cursor count -- "
				+ cursor.getCount());

		if (cursor != null && cursor.getCount() > 0) {

			// do your action
			// Fetch your data

			cursor.close();/*
			sql.close();
			db1.close();*/
			return true;

		} else {

			cursor.close();/*
			sql.close();
			db1.close();*/
			return false;
		}

	}
	private void updatelist() {
		// TODO Auto-generated method stub
  //  startService(new Intent(getBaseContext(),SynchDtataCount.class));
		System.out.println("====#$#$#$#$#$#$  in update list downtime");
		final ArrayList<String> searchResults = getDetail();

		listAdapter=new DowntimeAdaptMain(this, searchResults);
		downtime.setAdapter(listAdapter);


	}
	

	
	
	public ArrayList<String> getDetail()
	{
		
		
		
		arrlist.clear();
		//ArrayList<String> results = new ArrayList<String>();
		//ArrayList<DowntimeHelper> results = new ArrayList<DowntimeHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor c1 = sql.rawQuery("select * from Downtime", null);
		Log.e("Downtime Stn clmn cnt.", ""+c1.getCount());
		Cursor c = sql.rawQuery("Select distinct InstallationDesc from Downtime c1 inner join AllStation c2 on c1.InstalationId=c2.InstallationId where c2.NetworkCode='"+Type+"'", null);
		Log.e("Downtime Stn clmn cnt.", ""+c.getCount());
		if (c.getCount() == 0) {
			
			

			
			
//			sr.setStartEnd("");
//			sr.setRemarks("");

			arrlist.add("");

			c.close();/*
			sql.close();
			db.close();*/

			return arrlist;
		} else {

			c.moveToFirst();

			int column = 0;
			do {
				String s = c.getString(c.getColumnIndex("InstallationDesc"));
				
				
				if(arrlist.contains(s))
				{
					System.out.println("=@#@#  already present in list downtime");
				}
				else
				{
					arrlist.add(c.getString(c.getColumnIndex("InstallationDesc")));
				}
				
				

			} while (c.moveToNext());

			c.close();/*
			sql.close();
			db.close();*/
		}
		return arrlist;

	}

	private ArrayList<DowntimeHelper> GetDetail() {
		ArrayList<DowntimeHelper> results = new ArrayList<DowntimeHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM Downtime", null);
		if (c.getCount() == 0) {
			DowntimeHelper sr = new DowntimeHelper();
			sr.setstnname("");
			

			
			
//			sr.setStartEnd("");
//			sr.setRemarks("");

			results.add(sr);

			c.close();/*
			sql.close();
			db.close();*/

			return results;
		} else {

			c.moveToFirst();

			int column = 0;
			do {

				DowntimeHelper sr = new DowntimeHelper();

				// column = c.getColumnIndex("UserName");

				sr.setstnname(c.getString(c.getColumnIndex("InstallationDesc")));
				//sr.setcurrent(c.getString(c.getColumnIndex("AddedDate")));
				
				
				
				results.add(sr);

			} while (c.moveToNext());

			c.close();/*
			sql.close();
			db.close();*/
		}
		return results;

	}
	
	private String calculatediff(String datedb) {
		System.out.println("date db......................"+datedb);
		// TODO Auto-generated method stub
		long diffInMillisec = 0;
		long diffInDays = 0;
		try {
			// Create two calendars instances

			// System.out.println("---##### calculatediff 0 " + datedb);
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
			// System.out.println("---##### calculatediff 0 " +
			// sdf.format(date));

			Date datestop = sdf.parse(datedb);

			long diff = date.getTime() - datestop.getTime();

			diffInDays = diff / (24 * 60 * 60 * 1000);

			// System.out.println(" #####  calculatediff 1 " + diffInDays);

		} catch (Exception ex) {
			ex.printStackTrace();
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
						+ l.getLineNumber() + "	" + ex.getMessage() + " "
						+ Ldate);
			}



		}

		if (diffInDays == 0) {
			return "Today";

		} else if (diffInDays == 1) {
			return "Yesterday";
		} else {
			return datedb;
		}

	}
	
	private String calculatedifftime(String datedb) {
		System.out.println("date db......................"+datedb);
		// TODO Auto-generated method stub
		long diffInMillisec = 0;
		long diffInDays = 0;
		long diff = 0;
		try {
			// Create two calendars instances

			// System.out.println("---##### calculatediff 0 " + datedb);
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm ");
			 System.out.println("---##### sdf 0 " +
					 sdf);

			Date datestop = sdf.parse(datedb);
			System.out.println("---value of datestop...."+datestop);
			 diff = date.getTime() - datestop.getTime();

		//	diffInDays = diff / (24 * 60 * 60 * 1000);

			// System.out.println(" #####  calculatediff 1 " + diffInDays);

		} catch (Exception ex) {
			ex.printStackTrace();
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
						+ l.getLineNumber() + "	" + ex.getMessage() + " "
						+ Ldate);
			}



		}

//		if (diffInDays == 0) {
//			return "Today";
//
//		} else if (diffInDays == 1) {
//			return "Yesterday";
//		} else {
//			return datedb;
//		}
		String s=String.valueOf(diff);
		return s;

	}
	private String[] splitfrom(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf down...."+tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 11);
		System.out.println("---value of kdown..."+k);
//		String m = k.replace("T", " From ");
//		System.out.println("---value of m..."+m);
//		String[] n = m.split(" From");
//
//		// System.out.println("--------n[1]" + n[1].trim());
//
//		try {
//			SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
//			SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm ");
//			Date date = parseFormat.parse(n[1] + " ");
//
//			// System.out.println(parseFormat.format(date) + " = "
//			// + displayFormat.format(date));
//
//			fromtimetw = displayFormat.format(date);
//
//		} catch (Exception e) {
//
//		}
//
//		String v = n[0].trim();

		//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(k);
			System.out.println("..........value of my date after conv"+myDate);

		} catch (ParseException e) {
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM");
		String finalDate = timeFormat.format(myDate);

 System.out.println("time------"+fromtimetw);
		//String[] v1 = { finalDate };
		String[] v1={  finalDate };
		
	
			return v1;
	}
	
	
	
	private String[] splittodate(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...."+tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 11);
		System.out.println("---value of k..."+k);

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(k);
			System.out.println("..........value of my date after conv"+myDate);

		} catch (ParseException e) {
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM");
		String finalDate = timeFormat.format(myDate);

 System.out.println("time------"+fromtimetw);
		//String[] v1 = { finalDate };
		String[] v2={  finalDate };
		
	
			return v2;
	}
	private String[] splitfromto(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...."+tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 9);
		System.out.println("---value of k..."+k);
		String m = k.replace("T", " From ");
		System.out.println("---value of m..."+m);
		String[] n = m.split(" From");

		// System.out.println("--------n[1]" + n[1].trim());

		try {
			SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
			SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm ");
			Date date = parseFormat.parse(n[1] + " ");

			// System.out.println(parseFormat.format(date) + " = "
			// + displayFormat.format(date));

			fromtimetw = displayFormat.format(date);

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




		}

		String v = n[0].trim();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(v);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM yyyy");
		String finalDate = timeFormat.format(myDate);

 System.out.println("--------------date-----"+finalDate);

 fromtimetw=m.substring(15, tf.length() - 8);

 System.out.println("time------"+fromtimetw);
		String[] vto = { finalDate };
		String[] v2={  fromtimetw };
		
	
			return vto;
	}
	
	private String[] splitfromtotime(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...."+tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 9);
		System.out.println("---value of k..."+k);
		String m = k.replace("T", " From ");
		System.out.println("---value of m..."+m);
		String[] n = m.split(" From");

		// System.out.println("--------n[1]" + n[1].trim());

		try {
			SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
			SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm ");
			Date date = parseFormat.parse(n[1] + " ");

			// System.out.println(parseFormat.format(date) + " = "
			// + displayFormat.format(date));

			fromtimetw = displayFormat.format(date);

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




		}

		String v = n[0].trim();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(v);

		} catch (ParseException e) {
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

		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM yyyy");
		String finalDate = timeFormat.format(myDate);

 System.out.println("--------------date-----"+finalDate);

 fromtimetw=m.substring(15, tf.length() - 8);

 System.out.println("time------"+fromtimetw);
		String[] vto = { finalDate };
		String[] vtotym={  fromtimetw };
		
	
			return vtotym;
	}
	private String[] splitfromtym(String tym) {
		// TODO Auto-generated method stub
		System.out.println("---value of tym...."+tym);
		String fromtimetw = "";
		String k = tym.substring(0, tym.length() - 9);
		System.out.println("---value of kym..."+k);
		String m = k.replace("T", " ");
		System.out.println("---value of mym..."+m);
		
		
		
		final String dateStart = m;
		  //final String dateStop = "01/15/2012 10:31:48";
		  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  Date date = new Date();
		//  System.out.println("date format of system......................"+dateFormat.format(date));
		  final String dateStop =dateFormat.format(date);
		  Date d1 = null;
			Date d2 = null;
			String diffTym="";
			
			try {
				d1 = dateFormat.parse(dateStart);
				d2 = dateFormat.parse(dateStop);
	 
				//in milliseconds
				long diff = d2.getTime() - d1.getTime();
	 
				long diffSeconds = diff / 1000 % 60;
				long diffMinutes = diff / (60 * 1000) % 60;
				long diffHours = diff / (60 * 60 * 1000) % 24;
				long diffDays = diff / (24 * 60 * 60 * 1000);
	  diffTym=diffDays + " Days "+diffHours+" Hours "+diffMinutes+" Minutes "+diffSeconds+" Seconds";
				System.out.print(diffDays + " days, ");
				System.out.print(diffHours + " hours, ");
				System.out.print(diffMinutes + " minutes, ");
				System.out.print(diffSeconds + " seconds.");
	 
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

			String[] s = { diffTym };
			return s;
	}
	
	private String[] splitfromtime(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...."+tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 9);
		System.out.println("---value of k..."+k);
		String m = k.replace("T", " From ");
		System.out.println("---value of m..."+m);
		String[] n = m.split(" From");

		// System.out.println("--------n[1]" + n[1].trim());

		try {
			SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
			SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm ");
			Date date = parseFormat.parse(n[1] + " ");

			// System.out.println(parseFormat.format(date) + " = "
			// + displayFormat.format(date));

			fromtimetw = displayFormat.format(date);

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




		}

		String v = n[0].trim();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(v);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy");
		String finalDate = timeFormat.format(myDate);

 System.out.println("--------------date-----"+finalDate);

 fromtimetw=m.substring(15, tf.length() - 4);

 System.out.println("time------"+fromtimetw);
		String[] v1 = { finalDate };
		String[] v1time={  fromtimetw };
		
	
			return v1time;
	}
	private void fetchdata() {
		if(asyncfetch == null)
		{
			iv.setVisibility(View.VISIBLE);
			((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.GONE);
			
			Log.e("async","null");
			asyncfetch=new DownloadxmlsDataURL();
			asyncfetch.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			if(asyncfetch.getStatus() == AsyncTask.Status.RUNNING)
			{
				Log.e("async","running");
				iv.setVisibility(View.GONE);
				((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
			}
		}
	}

	public class DownloadxmlsDataURL extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			//GetStationDowntime30Days_Android
		
				String url="http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetStationDowntime_Android?Mobile="+mobno;
url = url.replaceAll(" ", "%20");
				
				
				System.out.println("============ internet reg url " + url);

				try {
					System.out.println("-------  activity url --- " + url);
					responsemsg = ut.httpGet(url);

					System.out.println("-------------  xx vale of non repeated-- "
							+ responsemsg);
				
//
				if (responsemsg.contains("<InstalationId>")) {
					sop = "valid";
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());
					System.out.println("------------- 1-- ");
					SQLiteDatabase sql = db.getWritableDatabase();
					System.out.println("------------- 2-- ");
					//sql.execSQL("DROP TABLE IF EXISTS Downtime");
					System.out.println("------------- 3-- ");
					//sql.execSQL(ut.getDowntime());
					sql.delete("Downtime",null,null);

					System.out.println("------------- 4-- ");
					System.out.println("------------- 5-- ");

					Cursor c = sql.rawQuery("SELECT *   FROM Downtime",
							null);
					System.out.println("------------- 6-- ");
					ContentValues values = new ContentValues();
					System.out.println("------------- 7-- ");
					NodeList nl = ut.getnode(responsemsg, "Table1");
					String msg = "";
					String columnName, columnValue;
					for (int i = 0; i < nl.getLength(); i++) {
						Element e = (Element) nl.item(i);
						for (int j = 0; j < c.getColumnCount(); j++) {

							columnName = c.getColumnName(j);
							columnValue = ut.getValue(e, columnName);

							
							
							System.out.println("-------------column name"+ columnName);
							System.out.println("-------------column value"+ columnValue);
							
							values.put(columnName, columnValue);

						}

						sql.insert("Downtime", null, values);

					}

					c.close();/*
					sql.close();
					db.close();*/

				} else {
					sop = "invalid";
					System.out
							.println("--------- invalid for AD list --- ");
				}

				
				
				
				} 
				catch(NullPointerException e)
				{
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
				catch (IOException e) {
					e.printStackTrace();

					responsemsg = "wrong" + e.toString();
					System.out
					.println("--------- invalid for message type list --- "+responsemsg);
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
				
				
			
			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//pd.cancel();
			try{
			System.out.println("...............value of sop"+ sop);
			if (sop.equals("valid")) {

				updatelist();


			} else {

				showD("invalid");
			}
			
			

			iv.setVisibility(View.VISIBLE);
			((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.GONE);
		
			}catch(Exception e)
			{
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
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			/*pd=new ProgressDialog(ConnectionStatusMain.this);
			pd.setTitle("Please Wait....");
			pd.setMessage("Fetching Data from Server..");
			pd.show();*/
			
		iv.setVisibility(View.GONE);
		((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
		
		
		}

	}
	
	private void updaterefreshdate() {
		// TODO Auto-generated method stub

		Calendar c = Calendar.getInstance();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = df.format(c.getTime());

		System.out.println("------ curdaterefresh " + formattedDate);

		String[] aDate = { formattedDate };

		DBInterface db = new DBInterface(getBaseContext());
		db.SetDaterefresh(aDate);
		//db.Close();

		filldaterefresh();

	}
	
	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(DowntimeMain.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);

		TextView txt = (TextView) myDialog
				.findViewById(R.id.dialoginfogototextsmall);
		if (string.equals("empty")) {
			myDialog.setTitle("Error...");
			txt.setText("Please Fill required data..");
		} else if (string.equals("nonet")) {
			myDialog.setTitle("Error...");
			txt.setText("No Internet Connection Found.Please Activate internet Connectin on Device..");
		} else if (string.equals("invalid")) {
			myDialog.setTitle(" ");
			txt.setText("No Refresh Data Available. Please check internet connection...");
		}

		Button btn = (Button) myDialog
				.findViewById(R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();
				// finish();

			}
		});

		myDialog.show();

	}

	protected boolean net() {
		// TODO Auto-generated method stub
		Context context = this.getApplicationContext();
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
	private void filldaterefresh() {
		// TODO Auto-generated method stub

		System.out.println("-------  filldateref " + daterestr);

		if (daterestr.equals("1")) {
			txtdate.setVisibility(View.INVISIBLE);
			txtdaterefresh.setVisibility(View.INVISIBLE);
		} else {

			try {

				String olddate = getolddate();

				System.out.println("-------  olddate " + olddate);

				Calendar c = Calendar.getInstance();

				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String formattedDate = df.format(c.getTime());

				System.out.println("------ curdaterefresh " + formattedDate);
				String diff = getTimeDiff(olddate, formattedDate);
				System.out.println("----- ##### " + diff);

				if ((diff.contains("seconds ago"))
						|| (diff.contains("minutes ago"))) {
					txtdate.setVisibility(View.INVISIBLE);
					txtdaterefresh.setVisibility(View.INVISIBLE);

				} else {
					System.out.println("----- ##### 2 " + diff);

					if (diff.equals("yesterday")) {
						String refdate = "1 day old data";
						txtdate.setText(refdate);
					} else if (diff.contains("ago")) {

						String[] sar = diff.split(" ");
						String a = sar[0].toString();
						int i = Integer.parseInt(a);

						if (i > 8) {
							txtdate.setText(" 1 day old data");
						} else {
							String ref[] = diff.split("ago");

							String refdate = ref[0].toString();
							System.out.println("--- #### refdate " + refdate);

							txtdate.setText(refdate + "old data");
						}

					} else {
						txtdate.setText(diff + "old data");
					}
				}

			} catch (Exception e) {
				dff = new SimpleDateFormat("HH:mm:ss");
				Ldate = dff.format(new Date());

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



			}

		}

	}
	
	private String getTimeDiff(String time, String curTime)
			throws ParseException {
		DateFormat formatter;
		Date curDate;
		Date oldDate;
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		curDate = (Date) formatter.parse(curTime);
		oldDate = (Date) formatter.parse(time);
		long oldMillis = oldDate.getTime();
		long curMillis = curDate.getTime();
		// Log.d("CaseListAdapter", "Date-Milli:Now:"+curDate.toString()+":"
		// +curMillis +" old:"+oldDate.toString()+":" +oldMillis);
		CharSequence text = DateUtils.getRelativeTimeSpanString(oldMillis,
				curMillis, 0);
		return text.toString();
	}

	private String getolddate() {
		// TODO Auto-generated method stub

		DBInterface dbi = new DBInterface(getBaseContext());
		String dateref = dbi.GetDateRefresg();
		dbi.Close();
		return dateref;
	}
	
	  @Override
	   protected void onResume() {
	      super.onResume();
	   SharedPreferences preferences = getApplicationContext().getSharedPreferences("SCROLLDOWNTIME", Context.MODE_PRIVATE);
	      int scroll = preferences.getInt("ScrollValueDowntime", 0);
	      
	      System.out.println(".............value of scroll at resume>>>>>>>>>>>>>>>>>>>"+scroll);
	    //  connectionstatus.scrollTo(0, scroll); 
	      downtime.smoothScrollToPosition(scroll-1);
	 }
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		 SharedPreferences preferences = getApplicationContext().getSharedPreferences("SCROLLDOWNTIME", Context.MODE_PRIVATE);
		    SharedPreferences.Editor editor = preferences.edit();
		    int scroll = downtime.getFirstVisiblePosition();
		  //  int scrollx = connectionstatus.getScrollX();
		    System.out.println(".............value of scroll>>>>>>>>>>>>>>>>>>>"+scroll);
		    editor.putInt("ScrollValueDowntime", scroll);
		    editor.commit();
		// finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
        /*Intent i = new Intent(getBaseContext(), DowntimeStateWise.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getBaseContext().startActivity(i);*/
		finish();
	}

}

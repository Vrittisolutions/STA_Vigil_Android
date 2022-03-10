package com.stavigilmonitoring;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.adapters.DowntimeStateAdpt;
import com.beanclasses.DowntimeHelper;
import com.database.DBInterface;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Downtime extends Activity {
	private static final String Days = null;
	private static final String Hours = null;
	private static final String Minutes = null;
	private static final String Seconds = null;
	ProgressDialog pd;
	ListView workspacewisedetail;
	String mobno, link;
	AsyncTask depattask;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv;
	static SimpleDateFormat dff;
	static String Ldate;
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;
	private ListView connectionstatus;
	private ListView nonrepeated;
	private String stnnAme;
	private TextView downtimestnnsme;
	private Button barChart;
	private String z="";
	String z1;
	private String ztf1="";
	String z2;
	String tf="";
	String tf1="";
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.downtime);

		downtimestnnsme=(TextView)findViewById(R.id.tvdowntimesstnname);
		Bundle extras = getIntent().getExtras();
		stnnAme = extras.getString("stnname");
		downtimestnnsme.setText(stnnAme);
//		ActivityId = extras.getString("ActivityId");
		iv=(ImageView)findViewById(R.id.button_refresh_downtime);
		barChart=(Button)findViewById(R.id.btndowntimebarchart);
		nonrepeated = (ListView) findViewById(R.id.downtime);

		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();
			if (dbvalue()) {
				updatelist();
			} else {

			fetchdata();
			}

			 OnClickListener clickListener = new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// Draw the Income vs Expense Chart
						//openChart();	
						
						 Intent i= new  Intent(Downtime.this,BarChart.class);
						i.putExtra("stnname", stnnAme);
						startActivity(i); 
					}
				};
				
				// Setting event click listener for the button btn_chart of the MainActivity layout
				barChart.setOnClickListener(clickListener);
//		} else {
//			showD("nonet");
//		}
		
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isnet()) {

					fetchdata();
				} else {
					showD("nonet");
				}
				

				
			}
		});
		
//		barChart.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//				Bundle dataBundle = new Bundle();
//				dataBundle.putString("stnname", stnnAme);
//				//dataBundle.putString("ActivityName", ActivityName);
//
//				// finish();
//				Intent myIntent = new Intent();
//				//myIntent.setClass(getApplicationContext(), BarChart.class);
//				myIntent.setClass(getApplicationContext(), BarChart.class);
//
//				myIntent.putExtras(dataBundle);
//				startActivity(myIntent);
//				finish();
//
//				
//			}
//		});
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

	
	 private void openChart(){

	    	//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String[] params = new String[1];
			params[0] = stnnAme;
			Cursor cursor = sql.rawQuery(
					"SELECT * FROM Downtime where InstallationDesc=? ORDER BY StationDownTime DESC ", params);

			System.out.println("----------  dbvalue screen cursor count -- "
					+ cursor.getCount());

			if (cursor.getCount() == 0) {

				// do your action
				// Fetch your data

				cursor.close();
				

			} else {

				cursor.moveToFirst();

				int column = 0;
				
				do {
	System.out.println("..................do.........with ................");

					int column1 = cursor.getColumnIndex("StationDownTime");
					
					System.out.println("..................do.........with .......column1.........");
				//	tf = tf+","+cursor.getString(column1);
					tf = cursor.getString(column1);
					String []sp=tf.split(":");
					int i=Integer.parseInt(sp[0])*60+Integer.parseInt(sp[1]);
					String tfc=String.valueOf(i);
					z=tfc+","+z;
					z1=z;
					
					
					System.out.println("..........value of tf for z1----------------"+z1);
					int column2 = cursor.getColumnIndex("AddedDate");
				//	tf1 = tf1+","+cursor.getString(column2);
					tf1 = cursor.getString(column2);
					ztf1=tf1+","+ztf1;
					z2=ztf1;
					System.out.println("..........value of tf1-----for z2------------"+z2);


				} while (cursor.moveToNext());

				cursor.close();
			}

	    	String []DownTime=z1.split(",");
	    	String []AddedDate={z2};
	    	System.out.println("............down time added date");

	    	//START
	    	    final int[] ints = new int[DownTime.length];
	    	    for (int i=0; i < DownTime.length; i++) {
	    	        ints[i] = Integer.parseInt(DownTime[i]);
	    	        System.out.println("----------------"+Integer.parseInt(DownTime[i]));
	    	    }
	    	    //END

	    	XYMultipleSeriesRenderer incomeSeries  = new XYMultipleSeriesRenderer();
	    	System.out.println("............Xy multiplierSeries..............");
	    	//XYSeries AddedDate  = new XYSeries("AddedDate");
	    	
	    	XYSeries incomeSeries1 = new XYSeries("Downtime");
	    	System.out.println("...........incomeseries..............");

	    	//end
	    	for(int i=0; i<ints.length; i++)
	        	{
	        		incomeSeries1.add(i,ints[i]);
//	        		System.out.println("......downtime in array..."+DownTime[i]);
	        	}
	    	

	    	
	    	// Creating a dataset to hold each series
	    	XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	    	// Adding Income Series to the dataset
	    	dataset.addSeries(incomeSeries1);
	    	XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
	    	System.out.println(".........2.........");
	    	incomeRenderer.setColor(Color.rgb(220, 80, 80));
	    	incomeRenderer.setChartValuesTextAlign(Align.CENTER);

	    	System.out.println(".........3.........");
	    	incomeRenderer.setFillPoints(true);
	    	
	    	System.out.println(".........4.........");
	    	incomeRenderer.setLineWidth(6);
	    	System.out.println(".........6.........");
	    	incomeRenderer.setDisplayChartValues(true);
	    	System.out.println(".........7.........");
	    	XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
	    	System.out.println(".........14.........");
	    	multiRenderer.setXLabels(0);
	    	
	    	System.out.println(".........15.........");
	    	multiRenderer.setChartTitle("Down Time Chart");
	    	System.out.println(".........16.........");
	    	multiRenderer.setXTitle("Year 2014");
	    	System.out.println(".........17.........");
	    	multiRenderer.setYTitle("Time in Minutes");
	    	System.out.println(".........18.........");
	    	multiRenderer.setZoomButtonsVisible(true);    
	    	//START
	    	for(int i=0; i< AddedDate.length;i++){
	    		System.out.println("......addeddate in array..."+AddedDate[i]);
	    		multiRenderer.addXTextLabel(i, AddedDate[i]);    		
	    	}    	
	    	//END
	    	
	    	
	    	
	    	System.out.println("...........19......");
	    	// Adding incomeRenderer and expenseRenderer to multipleRenderer
	    	// Note: The order of adding dataseries to dataset and renderers to multipleRenderer
	    	// should be same
	    	multiRenderer.addSeriesRenderer(incomeRenderer);
	    	//multiRenderer.addSeriesRenderer(expenseRenderer);
	      	System.out.println("...........20......");
	    	//multiRenderer.setOrientation(Orientation.HORIZONTAL);
	    	
	    	// Creating an intent to plot bar chart using dataset and multipleRenderer    	
	    	Intent intent = ChartFactory.getBarChartIntent(getBaseContext(), dataset, multiRenderer, Type.DEFAULT);
	    	System.out.println("...........21......");
	    	// Start Activity
	    	startActivity(intent);
	    	
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

			cursor.close();
			return true;

		} else {

			cursor.close();
			return false;
		}

	}
	private void updatelist() {
		// TODO Auto-generated method stub

		final ArrayList<DowntimeHelper> searchResults = GetDetail();

	nonrepeated.setAdapter(new DowntimeStateAdpt(this, searchResults));


	}
	

	private ArrayList<DowntimeHelper> GetDetail() {
		ArrayList<DowntimeHelper> results = new ArrayList<DowntimeHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = stnnAme;
		Cursor c = sql.rawQuery("SELECT * FROM Downtime where InstallationDesc='"+params[0]+"' ORDER BY StationDownTime DESC", null);
		if (c.getCount() == 0) {
			DowntimeHelper sr = new DowntimeHelper();
			//sr.setstnname("");
			sr.setcurrent("");
			sr.setdateDay("");
			sr.setlastseven("");

			

			results.add(sr);

			c.close();

			return results;
		} else {

			c.moveToFirst();

			int column = 0;
			do {

				DowntimeHelper sr = new DowntimeHelper();

				// column = c.getColumnIndex("UserName");

				sr.setstnname(c.getString(c.getColumnIndex("InstallationDesc")));
				int column1 = c.getColumnIndex("AddedDate");
				String tf = c.getString(column1);
				String tfday = c.getString(column1);
				String[] v1 = splitfrom(tf);
				String[] v1day = splitfromday(tfday);
				sr.setdateDay(v1day[0]);
				sr.setcurrent(v1[0]);
				sr.setlastseven(c.getString(c.getColumnIndex("StationDownTime")));

				results.add(sr);

			} while (c.moveToNext());

			c.close();
		}
		return results;

	}
	
	private String[] splitfromtohr(String tfy) {
		// TODO Auto-generated method stub
		String newv="";
		String newvv="";
		String kfy= tfy;
		if(kfy.contains("."))
		{
			newv=kfy.replace(".", ":");
			
			
			if(newv.startsWith(":"))
			{
				newvv="00"+newv;
			}
			
		}
		String s="00:48";
		SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm ");
		SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm ");
		String finalv=parseFormat.format(s);
		System.out.println("..................finav"+finalv);
		
		return null;
	}


	private String calculatediff(String datedb) {
		System.out.println("date db......................"+datedb);
		// TODO Auto-generated method stub
		long diffInMillisec = 0;
		long diffInDays = 0;
		try {

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
			Date datestop = sdf.parse(datedb);

			long diff = date.getTime() - datestop.getTime();

			diffInDays = diff / (24 * 60 * 60 * 1000);

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
	
	
	private String[] splitfrom(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf down...."+tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 13);
		System.out.println("---value of kdown..."+k);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		Date myDate = null;
		try {
			myDate = dateFormat1.parse(k);
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy");
		String finalDate = timeFormat.format(myDate);

 System.out.println("time------"+fromtimetw);
		//String[] v1 = { finalDate };
		String[] v1={  finalDate };
		
	
			return v1;
	}
	
	
	private String[] splitfromday(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf down...."+tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 13);
		System.out.println("---value of kdown..."+k);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		Date myDate = null;
		try {
			myDate = dateFormat1.parse(k);
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("EEE");
		String finalDate = timeFormat.format(myDate);

 System.out.println("time day------"+finalDate);
		//String[] v1 = { finalDate };
		String[] v1day={  finalDate };
		
	
			return v1day;
	}
	
	
	private String[] splitfromsd(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tfsd down...."+tf);
		String s=tf+"Hours";
		System.out.println("---value of tfsd down1...."+s);
		String[] v2={  s };
			return v2;
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


		try {
			SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
			SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm ");
			Date date = parseFormat.parse(n[1] + " ");


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
		// TODO Auto-generated method stub

		pd = ProgressDialog.show(Downtime.this,
				"Fetching Data from Server..", "Please Wait....", true, true,
				new OnCancelListener() {

					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						if (depattask != null
								&& depattask.getStatus() != AsyncTask.Status.FINISHED) {
							depattask.cancel(true);
						}
					}
				});

		depattask = new DownloadxmlsDataURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public class DownloadxmlsDataURL extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

				String xx = "";

					//GetStationDowntime30Days_Android
				String url="http://sta.vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetStationDowntime_Android?Mobile="+mobno;
url = url.replaceAll(" ", "%20");
				
				
				System.out.println("============ internet reg url " + url);

				try {
					System.out.println("-------  activity url --- " + url);
					responsemsg = ut.httpGet(url);

					System.out.println("-------------  xx vale of non repeated-- "
							+ responsemsg);

				if (responsemsg.contains("<InstalationId>")) {
					sop = "valid";
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());
					SQLiteDatabase sql = db.getWritableDatabase();
					//sql.execSQL("DROP TABLE IF EXISTS Downtime");
					//sql.execSQL(ut.getDowntime());
					sql.delete("Downtime",null,null);

					Cursor c = sql.rawQuery("SELECT *   FROM Downtime",
							null);
					ContentValues values = new ContentValues();
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

					c.close();

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
			try{
			pd.cancel();
			System.out.println("...............value of sop"+ sop);
			if (sop.equals("valid")) {

				updatelist();


			} else {

				showD("invalid");
			}
			
			}catch(Exception e){
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

		final Dialog myDialog = new Dialog(Downtime.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(R.layout.dialoginfosmall);
		myDialog.setCancelable(true);

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
			txt.setText("No Refresh data  Available. Please check internet connection...");
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
	   SharedPreferences preferences = getApplicationContext().getSharedPreferences("SCROLLDOWNTIME1", Context.MODE_PRIVATE);
	      int scroll = preferences.getInt("ScrollValueDowntime1", 0);
	      System.out.println(".............value of scroll at resume>>>>>>>>>>>>>>>>>>>"+scroll);

	      nonrepeated.smoothScrollToPosition(scroll+8);
	     
	 }
	
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		 SharedPreferences preferences = getApplicationContext().getSharedPreferences("SCROLLDOWNTIME1", Context.MODE_PRIVATE);
		    SharedPreferences.Editor editor = preferences.edit();
		   int scroll = nonrepeated.getFirstVisiblePosition();

		    System.out.println(".............value of scroll>>>>>>>>>>>>>>>>>>>"+scroll);
		    editor.putInt("ScrollValueDowntime1", scroll);
		    editor.commit();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		Intent i = new Intent(Downtime.this, DowntimeMain.class);
		startActivity(i);

	}

}

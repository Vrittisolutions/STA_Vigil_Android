package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.adapters.DowntimeReasonAdapt;
import com.beanclasses.DowntimeReasonHelper1;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DowntimeReason extends Activity {
	private static final String Days = null;
	private static final String Hours = null;
	private static final String Minutes = null;
	private static final String Seconds = null;
	//private static final String addedDate = null;
	ProgressDialog pd;
	ListView workspacewisedetail;
	String mobno, link;
	//AsyncTask depattask;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv;
	static SimpleDateFormat dff;
	static String Ldate;
	String responsemsg = "k";
	String responsemsg1 = "k";
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;
	private ListView connectionstatus;
	private ListView nonrepeated;

	ArrayList<String>arrlist=new ArrayList<String>();
	private ListView downtimereason1;
	private String StationName;
	private TextView stations;
	private String addedDate;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(com.stavigilmonitoring.R.layout.downtimereason1);
		stations=(TextView)findViewById(com.stavigilmonitoring.R.id.tvdowntimereasonsstnnsame);
		
		Bundle extras = getIntent().getExtras();
		StationName = extras.getString("stnname");
		stations.setText(StationName);
//		ActivityId = extras.getString("ActivityId");
		iv=(ImageView)findViewById(com.stavigilmonitoring.R.id.button_refresh_downtime_reason1);
		downtimereason1 = (ListView) findViewById(com.stavigilmonitoring.R.id.lvdowntimereason1);
		//iv = (ImageView) findViewById(R.id.button_refresh_workspace);
//		txtdate = (TextView) findViewById(R.id.txtdaterefreshworkspace);
//		txtdaterefresh = (TextView) findViewById(R.id.txtdaterefreshlinkworkspace);
//		actname = extras.getString("fromactivity");

		db = new DatabaseHandler(getBaseContext());
		
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		//link = dbi.GetUrl();
//		daterestr = dbi.GetDateRefresg();
		dbi.Close();
	//	System.out.print("Link value"+link);
		
//		if (net()) {

			if (dbvalue()) {
				updatelist();
			} else {

			fetchdata();
			}
			
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
		
		downtimereason1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {

				if (dbvalue()) {
					Object o = downtimereason1.getItemAtPosition(position);
					DowntimeReasonHelper1 fullObject = (DowntimeReasonHelper1) o;
					editActivity(fullObject.getStartTime());

				} else {

					Toast.makeText(getBaseContext(), "No Station Present..",
							Toast.LENGTH_LONG).show();
				}

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
	public void editActivity(String starttime) {
		
		System.out.println("==========@#@# start time " + starttime);
		//DatabaseHandler db1 = new DatabaseHandler(this);
		SQLiteDatabase sqldb = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = starttime;
		System.out.println("==========1-------------------------------- ");
		Cursor c2 = sqldb.rawQuery(
				"SELECT * FROM DownTimeRason where StartTime=? ",
				params);
		System.out.println("==========2-------------------------------- ");
		String startTime = "";
		String endTime = "";
		String stationdowntimeId = "";
		String installationId="";
		String v1="";
		System.out.println("==========3-------------------------------- ");
		if(c2.getCount()==0)
		{
			c2.close();/*
			db.close();
			db1.close();*/
	
		}
		else
		{

			c2.moveToFirst();
			startTime = c2.getString(c2.getColumnIndex("StartTime"));
			System.out.println("==========StartTime------------------------------- "+startTime);
			endTime = c2.getString(c2.getColumnIndex("EndTime"));
			System.out.println("==========EndTime-------------------------------- "+endTime);
			stationdowntimeId = c2.getString(c2.getColumnIndex("StationDownTimeID"));
			System.out.println("==========StationDownTimeID-------------------------------- "+stationdowntimeId);
			installationId = c2.getString(c2.getColumnIndex("InstallationId"));
			System.out.println("==========InstallationId-------------------------------- "+installationId);
//			addedDate = c2.getString(c2.getColumnIndex("AddedDate"));
//			System.out.println("==========AddedDate-------------------------------- "+addedDate);
			int column2 = c2.getColumnIndex("AddedDate");
			String tf2 = c2.getString(column2);
			v1 = splitfromdate(tf2);
			c2.moveToLast();

			c2.close();/*
			db.close();
			db1.close();*/
		}

		// MOMA --- mom attend request
		// System.out.println("----------  type --- " + type);

		Bundle dataBundle = new Bundle();
		dataBundle.putString("stnname", StationName);
		dataBundle.putString("startTime", startTime);
		dataBundle.putString("endTime", endTime);
		dataBundle.putString("stationdowntimeId", stationdowntimeId);
		dataBundle.putString("installationId", installationId);
		//dataBundle.putString("ActivityName", ActivityName);
		dataBundle.putString("addDate", v1);

		// finish();
		Intent myIntent = new Intent();
		myIntent.setClass(getApplicationContext(), DowntimeReasonFill.class);

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
				"SELECT *   FROM DownTimeRason", null);

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

		
		final ArrayList<DowntimeReasonHelper1> searchResults = GetDetail();

		downtimereason1.setAdapter(new DowntimeReasonAdapt(this, searchResults));

		//workspacewisedetail.setOnItemClickListener(new OnItemClickListener() {

		//	@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1,
//					int position, long arg3) {
//				// TODO Auto-generated method stub
//				Object o = workspacewisedetail.getItemAtPosition(position);
//				WorkspacewiseHelper fullObject = (WorkspacewiseHelper) o;
//				editActivity(fullObject.getworkspacename());
//			}
//
//		});
	}
	
//	public void editActivity(String workspaceName) {
//		String mobileno = "";
//		String deptid = "";
//		
//	//	String depart = workspacewisedetail.getSelectedItem().toString();
//		System.out.println("workspace detail are....."+ workspaceName);
////		try {
////			DatabaseHandler db1 = new DatabaseHandler(this);
////			SQLiteDatabase db = db1.getWritableDatabase();
////			String[] params = new String[1];
////			params[0] = username;
////			Cursor c2 = db.rawQuery(
////					"SELECT * FROM UserMaster where UserName=? ", params);
////
////			c2.moveToFirst();
////			mobileno = c2.getString(c2.getColumnIndex("Mobile"));
////			userid = c2.getString(c2.getColumnIndex("UserMasterId"));
////			c2.moveToLast();
////
////			c2.close();
////			db.close();
////			db1.close();
////		} catch (Exception e) {
////
////		}
//		try {
//
//			DatabaseHandler db1 = new DatabaseHandler(this);
//			SQLiteDatabase db = db1.getWritableDatabase();
//			String[] params = new String[1];
//			params[0] = workspaceName;
//			Cursor c2 = db.rawQuery(
//					"SELECT * FROM WorkspacewiseActivities where ProjectName=? ",
//					params);
//
//			c2.moveToFirst();
//			deptid = c2.getString(c2.getColumnIndex("ProjectId"));
//			c2.moveToLast();
//
//			c2.close();
//			db.close();
//			db1.close();
//		} catch (Exception e) {
//
//		}
//		System.out.println(".....worksapce activity id is..."+ deptid);
////
////		System.out.println("---------- mobileno " + mobileno);
////		System.out.println("---------- deptid " + deptid);
////		System.out.println("---------- deptnamr " + depart);
////		System.out.println("---------- userid " + userid);
////
//		Bundle dataBundle = new Bundle();
//		dataBundle.putString("ActivityId", ActivityId);
//		dataBundle.putString("ActivityName", ActivityName);
//		dataBundle.putString("fromactivity", actname);
//		dataBundle.putString("mobile", mobileno);
//		dataBundle.putString("deptid", deptid);
//
//		
//		
//
//		Intent myIntent = new Intent();
//		myIntent.setClass(getApplicationContext(), WorkspaceActlist.class);
//		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		myIntent.putExtras(dataBundle);
//
//		startActivity(myIntent);
//
//		finish();
//
//	}
	
	
	public ArrayList<String> getDetail()
	{
		
		
		
		arrlist.clear();
		//ArrayList<String> results = new ArrayList<String>();
		//ArrayList<DowntimeHelper> results = new ArrayList<DowntimeHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM Downtime", null);
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

				

				// column = c.getColumnIndex("UserName");

				
				String s = c.getString(c.getColumnIndex("InstallationDesc"));
				
				
				if(arrlist.contains(s))
				{
					System.out.println("=@#@#  already present in list downtime");
				}
				else
				{
					arrlist.add(c.getString(c.getColumnIndex("InstallationDesc")));
				}
				
				
				
				
				//sr.setcurrent(c.getString(c.getColumnIndex("AddedDate")));
				
				
				
				

			} while (c.moveToNext());

			c.close();
			/*sql.close();
			db.close();*/
		}
		return arrlist;

	
		
		
		
		
		
		
	}
	
	
	
	
	
	
	void ReasonList()
	{
		
	}
	
	
	
	private ArrayList<DowntimeReasonHelper1> GetDetail() {
		ArrayList<DowntimeReasonHelper1> results = new ArrayList<DowntimeReasonHelper1>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = StationName;
		Cursor c = sql.rawQuery("SELECT * FROM DownTimeRason  where InstallationName=? ORDER BY AddedDate DESC", params);
		if (c.getCount() == 0) {
			DowntimeReasonHelper1 sr = new DowntimeReasonHelper1();
			//sr.setstnname("");
			

			
			sr.setdateDown("");
			sr.setStartTime("");
			sr.setEndTime("");
			
			results.add(sr);

			c.close();
			/*sql.close();
			db.close();*/

			return results;
		} else {

			c.moveToFirst();

			int column = 0;
			do {

				DowntimeReasonHelper1 sr = new DowntimeReasonHelper1();

				// column = c.getColumnIndex("UserName");
				int column1 = c.getColumnIndex("AddedDate");
				String tf = c.getString(column1);
				String[] v1 = splitfrom(tf);
				sr.setdateDown(v1[0]);
				sr.setStartTime(c.getString(c.getColumnIndex("StartTime")));
//				int column2 = c.getColumnIndex("StartTime");
//				String tf2 = c.getString(column2);
//				String[] v2 = splitfromstarttime(tf2);
//				sr.setStartTime(v2[0]);
				sr.setEndTime(c.getString(c.getColumnIndex("EndTime")));
//				int column3 = c.getColumnIndex("EndTime");
//				String tf3 = c.getString(column3);
//				String[] v3 = splitfromendtime(tf3);
//				sr.setEndTime(v3[0]);
				
				
				
				results.add(sr);

			} while (c.moveToNext());

			c.close();
			/*sql.close();
			db.close();*/
		}
		return results;

	}
	
	private String[] splitfromstarttime(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...."+tf);
		String fromtimetw = "";
		String k = tf.substring(9, tf.length() - 0);
		System.out.println("---value of k for time..."+k);
//		String m = k.replace("T", " From ");
//		System.out.println("---value of m..."+m);
//		String[] n = m.split(" From");

		// System.out.println("--------n[1]" + n[1].trim());
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

//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		Date myDate = null;
//		try {
//			myDate = dateFormat.parse(v);
//
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//
//		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM yyyy");
//		String finalDate = timeFormat.format(myDate);
//
// System.out.println("--------------date-----"+finalDate);
//
// fromtimetw=m.substring(15, tf.length() - 8);

 System.out.println("time------"+fromtimetw);
		String[] v2 = { k };
		//String[] v2={  fromtimetw };
		
	
			return v2;
	}
	
	private String[] splitfromendtime(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...."+tf);
		String fromtimetw = "";
		String k = tf.substring(9, tf.length() - 0);
		System.out.println("---value of k for time..."+k);
//		String m = k.replace("T", " From ");
//		System.out.println("---value of m..."+m);
//		String[] n = m.split(" From");

		// System.out.println("--------n[1]" + n[1].trim());
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

//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		Date myDate = null;
//		try {
//			myDate = dateFormat.parse(v);
//
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//
//		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM yyyy");
//		String finalDate = timeFormat.format(myDate);
//
// System.out.println("--------------date-----"+finalDate);
//
// fromtimetw=m.substring(15, tf.length() - 8);

 System.out.println("time------"+fromtimetw);
		String[] v3 = { k };
		//String[] v2={  fromtimetw };
		
	
			return v3;
	}
	
	private String[] splitfrom(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...."+tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 11);
		System.out.println("---value of k for time..."+k);
		 System.out.println("time------"+k);
//			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
//			Date myDate = null;
//			try {
//				myDate = dateFormat.parse(k);
//				System.out.println("..........value of my date after conv"+myDate);
//
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy");
//			String finalDate = timeFormat.format(myDate);
//			String[] v1 = { finalDate };
			String[] v1={  k };
//			
		
				return v1;
		}
	
	private String splitfromdate(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...."+tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 11);
		System.out.println("---value of k for time..."+k);
		 System.out.println("time------"+k);
			
			//String[] v2={  fromtimetw };
			
		
				return k;
		}
		
	private void fetchdata() {
		// TODO Auto-generated method stub

		pd = ProgressDialog.show(DowntimeReason.this,
				"Fetching Data from Server..", "Please Wait....", true, true,
				new OnCancelListener() {

					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						/*if (depattask != null
								&& depattask.getStatus() != AsyncTask.Status.FINISHED) {
							depattask.cancel(true);
						}*/
					}
				});

		//depattask = new DownloadxmlsDataURL().execute();
		new DownloadxmlsDataURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public class DownloadxmlsDataURL extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			
//			try {
//				DatabaseHandler db = new DatabaseHandler(getBaseContext());
//				SQLiteDatabase sql = db.getWritableDatabase();
//
			String xx = "";

			//String url;

			// http://vritti.co/vrittiportal/webservice/DepartmentwiseActWebService.asmx/FillOldGrid?Mobileno=9922708394&DeptId=Report&DeptName=Employee%20Reporting%20to%20Me
//			url = "http://intranet.vritti.co/VWBTest/webservice/ActivityWebService.asmx/WorkspaceWiseActivities?"
//					+ "Mobileno=" + mobno;
	//		String url="http://vritti.co/imedia/WdbIntMgmtNew.asmx/GetNonReportedAdvt_Android?UserName="+link+"&Mobile="+mobno;
			String url="http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetCurrentlyStationDowntime_Android?Mobile="+mobno;
url = url.replaceAll(" ", "%20");
			
			
			System.out.println("============ internet reg url " + url);

			try {
				System.out.println("-------  activity url --- " + url);
				responsemsg = ut.httpGet(url);

				System.out.println("-------------  xx vale of non repeated-- "
						+ responsemsg);
		
//
			if (responsemsg.contains("<StationDownTimeID>")) {
				sop = "valid";
				//DatabaseHandler db = new DatabaseHandler(getBaseContext());
				System.out.println("------------- 1-- ");
				SQLiteDatabase sql = db.getWritableDatabase();
				
				//sql.execSQL("DROP TABLE IF EXISTS DownTimeRason");
				//sql.execSQL(ut.getDownTimeRason());
				sql.delete("DownTimeRason",null,null);
				
				Cursor c = sql.rawQuery("SELECT *   FROM DownTimeRason",
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

					sql.insert("DownTimeRason", null, values);

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
	
	
	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(DowntimeReason.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);

		TextView txt = (TextView) myDialog
				.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);
		if (string.equals("empty")) {
			myDialog.setTitle("Error...");
			txt.setText("Please Fill required data..");
		} else if (string.equals("nonet")) {
			myDialog.setTitle("Error...");
			txt.setText("No Internet Connection Found.Please Activate internet Connectin on Device..");
		} else if (string.equals("invalid")) {
			myDialog.setTitle(" ");
			txt.setText("No Refresh data Available.Please check internet connection........");
		}

		Button btn = (Button) myDialog
				.findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
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
	
	@Override
	   protected void onResume() {
	      super.onResume();
	   SharedPreferences preferences = getApplicationContext().getSharedPreferences("SCROLLDOWNTIMEREASON1", Context.MODE_PRIVATE);
	      int scroll = preferences.getInt("ScrollValueDowntimeReaosn1", 0);
	      System.out.println(".............value of scroll at resume>>>>>>>>>>>>>>>>>>>"+scroll);
	    //  connectionstatus.scrollTo(0, scroll); 
	      downtimereason1.smoothScrollToPosition(scroll);
	 }
	
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		 SharedPreferences preferences = getApplicationContext().getSharedPreferences("SCROLLDOWNTIMEREASON1", Context.MODE_PRIVATE);
		    SharedPreferences.Editor editor = preferences.edit();
		    int scroll = downtimereason1.getFirstVisiblePosition();
		  //  int scrollx = connectionstatus.getScrollX();
		    System.out.println(".............value of scroll>>>>>>>>>>>>>>>>>>>"+scroll);
		    editor.putInt("ScrollValueDowntimeReaosn1", scroll);
		    editor.commit();
		// finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		Bundle dataBundle = new Bundle();
		dataBundle.putString("stnname", StationName);
//		dataBundle.putString("ActivityName", ActivityName);
		Intent i = new Intent(DowntimeReason.this, DowntimeReasonMain.class);
		//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		i.putExtras(dataBundle);
		startActivity(i);
		//finish();

	}

}

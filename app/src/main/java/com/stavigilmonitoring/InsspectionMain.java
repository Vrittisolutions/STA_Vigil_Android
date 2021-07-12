package com.stavigilmonitoring;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.InspectionAdaptMain;
import com.beanclasses.ConnectionstatusHelper;
import com.beanclasses.InspectionHelper;
import com.database.DBInterface;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class InsspectionMain extends Activity {
	private static final String Days = null;
	private static final String Hours = null;
	private static final String Minutes = null;
	private static final String Seconds = null;
	ProgressDialog pd;
	ListView workspacewisedetail;
	String mobno, link;
	AsyncTask depattask;
	// ExpandableListAdapter listAdapter;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	static SimpleDateFormat dff;
	static String Ldate;
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv;
	String countConn = "";
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;
	private ListView connectionstatus;
	private ExpandableListView expListView;
	HashMap<String, List<String>> listDataChild;
	List<String> listDataHeader;
	private String stnnAME;
	private TextView csnstatus;
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String Name = "csnStatus";
	SharedPreferences sharedpreferences;
	private int icount;
	private String scount;
	private ListView inspections;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.inspectionmain);
		// SharedPreferences pref =
		// getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for
		// private mode
		// Editor editor = pref.edit();
		// Bundle extras = getIntent().getExtras();
		// ActivityName = extras.getString("ActivityName");
		// ActivityId = extras.getString("ActivityId");
		iv = (ImageView) findViewById(R.id.button_refresh_inspection_main);
		inspections = (ListView) findViewById(R.id.lvinspection);
		// expListView = (ExpandableListView) findViewById(R.id.lvExpconnect);
		// iv = (ImageView) findViewById(R.id.button_refresh_workspace);
		// txtdate = (TextView) findViewById(R.id.txtdaterefreshworkspace);
		// txtdaterefresh = (TextView)
		// findViewById(R.id.txtdaterefreshlinkworkspace);

		// actname = extras.getString("fromactivity");
		// service start

		// long aTime = 1000 * 60 * 9;
		//
		// Intent igpsalarm = new Intent(getBaseContext(),
		// com.services.SendTimeSheet.class);
		// PendingIntent piHeartBeatService =
		// PendingIntent.getService(getBaseContext(), 0,
		// igpsalarm, PendingIntent.FLAG_UPDATE_CURRENT);
		// AlarmManager alarmManager = (AlarmManager) getBaseContext()
		// .getSystemService(Context.ALARM_SERVICE);
		//
		// alarmManager.cancel(piHeartBeatService);
		// alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
		// System.currentTimeMillis(), aTime, piHeartBeatService);
		// startService(igpsalarm);
		// finish();
		// Service end
		//
		// Thread t = new Thread(new Runnable() {
		// public void run()
		// {
		// // Insert some method call here.
		//
		// }
		// });
		db = new DatabaseHandler(getApplicationContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		// link = dbi.GetUrl();
		// daterestr = dbi.GetDateRefresg();
		dbi.Close();
		// System.out.print("Link value"+link);

		// if (net()) {
		//
		//

		if (dbvalue()) {
			updatelist();
			// prepareListData();
		} else {

			fetchdata();
		}
		// } else {
		// //showD("nonet");
		// System.out.println("no internet available..........");
		// }

		// System.out.println("----------total number of item in list"+connectionstatus.getCount()
		// );
		// icount=connectionstatus.getCount();
		// scount=String.valueOf(icount);
		// editor.putString("csnStatuss", scount);
		// editor.commit();
		// System.out.println("----------scount"+scount );
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isnet()) {

					fetchdata();
				} else {
					showD("nonet");
				}
				// fetchdata();

			}
		});
		inspections.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {

				if (stationpresent()) {
					Object o = inspections.getItemAtPosition(position);
					InspectionHelper fullObject = (InspectionHelper) o;
					editActivity(fullObject.getinsStation());

				} else {

					Toast.makeText(getBaseContext(), "No Station Present..",
							Toast.LENGTH_LONG).show();
				}

			}
		});
		// filldaterefresh();
		//
		// iv.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		//
		// if (net()) {
		//
		// fetchfromserver();
		//
		// } else {
		// showD("nonet");
		// }
		//
		// }
		// });

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

		System.out.println("==========@#@# actid " + ActivityId);
		//DatabaseHandler db1 = new DatabaseHandler(this);
		SQLiteDatabase sqldb = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = StationName;
		Cursor c2 = sqldb.rawQuery(
				"SELECT * FROM ConnectionStatusUser where InstallationDesc=? ",
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

		// MOMA --- mom attend request
		// System.out.println("----------  type --- " + type);

		Bundle dataBundle = new Bundle();
		dataBundle.putString("stnname", stnname);
		// dataBundle.putString("ActivityName", ActivityName);

		// finish();
		Intent myIntent = new Intent();
		myIntent.setClass(getApplicationContext(), Inspection.class);

		myIntent.putExtras(dataBundle);
		startActivity(myIntent);
		finish();
		// System.out.println("------------- 1");

	}

	protected boolean stationpresent() {
		// TODO Auto-generated method stub

		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM ConnectionStatusUser", null);

		int count = c.getCount();

		c.close();

		if (count == 0) {
			return false;
		} else {

			return true;
		}

	}

	private boolean dbvalue() {
		// TODO Auto-generated method stub
		DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db1.getWritableDatabase();
		Cursor cursor = sql.rawQuery("SELECT *   FROM ConnectionStatusUser",
				null);

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

		final ArrayList<InspectionHelper> searchResults = GetDetail();
		InspectionHelper sr = new InspectionHelper();

		inspections.setAdapter(new InspectionAdaptMain(this, searchResults));

		// workspacewisedetail.setOnItemClickListener(new OnItemClickListener()
		// {

		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1,
		// int position, long arg3) {
		// // TODO Auto-generated method stub
		// Object o = workspacewisedetail.getItemAtPosition(position);
		// WorkspacewiseHelper fullObject = (WorkspacewiseHelper) o;
		// editActivity(fullObject.getworkspacename());
		// }
		//
		// });
	}

	// public void editActivity(String workspaceName) {
	// String mobileno = "";
	// String deptid = "";
	//
	// // String depart = workspacewisedetail.getSelectedItem().toString();
	// System.out.println("workspace detail are....."+ workspaceName);
	// // try {
	// // DatabaseHandler db1 = new DatabaseHandler(this);
	// // SQLiteDatabase db = db1.getWritableDatabase();
	// // String[] params = new String[1];
	// // params[0] = username;
	// // Cursor c2 = db.rawQuery(
	// // "SELECT * FROM UserMaster where UserName=? ", params);
	// //
	// // c2.moveToFirst();
	// // mobileno = c2.getString(c2.getColumnIndex("Mobile"));
	// // userid = c2.getString(c2.getColumnIndex("UserMasterId"));
	// // c2.moveToLast();
	// //
	// // c2.close();
	// // db.close();
	// // db1.close();
	// // } catch (Exception e) {
	// //
	// // }
	// try {
	//
	// DatabaseHandler db1 = new DatabaseHandler(this);
	// SQLiteDatabase db = db1.getWritableDatabase();
	// String[] params = new String[1];
	// params[0] = workspaceName;
	// Cursor c2 = db.rawQuery(
	// "SELECT * FROM WorkspacewiseActivities where ProjectName=? ",
	// params);
	//
	// c2.moveToFirst();
	// deptid = c2.getString(c2.getColumnIndex("ProjectId"));
	// c2.moveToLast();
	//
	// c2.close();
	// db.close();
	// db1.close();
	// } catch (Exception e) {
	//
	// }
	// System.out.println(".....worksapce activity id is..."+ deptid);
	// //
	// // System.out.println("---------- mobileno " + mobileno);
	// // System.out.println("---------- deptid " + deptid);
	// // System.out.println("---------- deptnamr " + depart);
	// // System.out.println("---------- userid " + userid);
	// //
	// Bundle dataBundle = new Bundle();
	// dataBundle.putString("ActivityId", ActivityId);
	// dataBundle.putString("ActivityName", ActivityName);
	// dataBundle.putString("fromactivity", actname);
	// dataBundle.putString("mobile", mobileno);
	// dataBundle.putString("deptid", deptid);
	//
	//
	//
	//
	// Intent myIntent = new Intent();
	// myIntent.setClass(getApplicationContext(), WorkspaceActlist.class);
	// myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// myIntent.putExtras(dataBundle);
	//
	// startActivity(myIntent);
	//
	// finish();
	//
	// }
	private ArrayList<InspectionHelper> GetDetail() {
		ArrayList<InspectionHelper> results = new ArrayList<InspectionHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM ConnectionStatusUser", null);
		if (c.getCount() == 0) {
			InspectionHelper sr = new InspectionHelper();
			// sr.setcsId("");

			// sr.setservertime("");
			//
			// sr.setStartTime("");
			// sr.setEndTime("");
			sr.setinsStation("");
			// sr.setStartEnd("");
			// sr.setRemarks("");

			results.add(sr);

			c.close();

			return results;
		} else {

			c.moveToFirst();

			int column = 0;
			do {

				InspectionHelper sr = new InspectionHelper();

				// column = c.getColumnIndex("UserName");

				sr.setinsStation(c.getString(c
						.getColumnIndex("InstallationDesc")));
				//
				//
				//
				results.add(sr);

			} while (c.moveToNext());

			c.close();
		}
		return results;

	}

	// private String splitIp(String datedb) {
	// String retval="";
	// for (retval: datedb.split("(")){
	// System.out.println(retval);
	// }
	// String v3[]={retval};
	// return v3;
	// }

	private String calculatediff(String datedb) {
		System.out.println("date db......................" + datedb);
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

		if (diffInDays == 0) {
			return "Today";

		} else if (diffInDays == 1) {
			return "Yesterday";
		} else {
			return datedb;
		}

	}

	private String calculatedifftime(String datedb) {
		System.out.println("date db......................" + datedb);
		// TODO Auto-generated method stub
		long diffInMillisec = 0;
		long diffInDays = 0;
		long diff = 0;
		try {
			// Create two calendars instances

			// System.out.println("---##### calculatediff 0 " + datedb);
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm ");
			System.out.println("---##### sdf 0 " + sdf);

			Date datestop = sdf.parse(datedb);
			System.out.println("---value of datestop...." + datestop);
			diff = date.getTime() - datestop.getTime();

			// diffInDays = diff / (24 * 60 * 60 * 1000);

			// System.out.println(" #####  calculatediff 1 " + diffInDays);

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

		// if (diffInDays == 0) {
		// return "Today";
		//
		// } else if (diffInDays == 1) {
		// return "Yesterday";
		// } else {
		// return datedb;
		// }
		String s = String.valueOf(diff);
		return s;

	}

	private String[] splitfrom(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";
		String k = tf.substring(9, tf.length() - 0);
		System.out.println("---value of k for time..." + k);
		// String m = k.replace("T", " From ");
		// System.out.println("---value of m..."+m);
		// String[] n = m.split(" From");

		// System.out.println("--------n[1]" + n[1].trim());
		//
		// try {
		// SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
		// SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm ");
		// Date date = parseFormat.parse(n[1] + " ");
		//
		// // System.out.println(parseFormat.format(date) + " = "
		// // + displayFormat.format(date));
		//
		// fromtimetw = displayFormat.format(date);
		//
		// } catch (Exception e) {
		//
		// }
		//
		// String v = n[0].trim();

		// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// Date myDate = null;
		// try {
		// myDate = dateFormat.parse(v);
		//
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
		//
		// SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM yyyy");
		// String finalDate = timeFormat.format(myDate);
		//
		// System.out.println("--------------date-----"+finalDate);
		//
		// fromtimetw=m.substring(15, tf.length() - 8);

		System.out.println("time------" + k);
		String[] v1 = { k };
		// String[] v2={ fromtimetw };

		return v1;
	}

	private String[] splitfromtym(String tym) {
		// TODO Auto-generated method stub
		System.out.println("---value of tym differ...." + tym);
		String fromtimetw = "";
		// String k = tym.substring(0, tym.length() - 11);
		// System.out.println("---value of kym differ..."+k);
		// String m = k.replace("T", " ");
		// System.out.println("---value of mym..."+m);

		final String dateStart = tym;
		// final String dateStop = "01/15/2012 10:31:48";
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		Date date = new Date();
		System.out.println("date format of system......................"
				+ dateFormat.format(date));
		System.out
				.println("date format of web tym......................" + tym);
		final String dateStop = dateFormat.format(date);

		Date d1 = null;
		Date d2 = null;
		String diffTym = "";

		try {
			d1 = dateFormat.parse(dateStart);
			d2 = dateFormat.parse(dateStop);
			System.out.println("d2......................" + d2);
			// in milliseconds
			long diff = d2.getTime() - d1.getTime();

			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			String days = String.valueOf(diffDays);
			String hours = String.valueOf(diffHours);
			String minutes = String.valueOf(diffMinutes);
			if (days.equals("0")) {
				if (hours.equals("0")) {
					// add code for minutes

					int i = Integer.parseInt(minutes);
					if (i >= 30) {
						// end
						diffTym = diffMinutes + " Minutes ";
					} else {
						diffTym = "";
					}
				} else {
					// int i=Integer.parseInt(minutes);
					// if(i>=30)
					{
						// end

						// diffTym=diffHours+" Hours "+diffMinutes+" Minutes ";
						diffTym = diffHours + " Hours ";
					}
					// else
					// {
					// diffTym=diffHours+" Hours ";
					// }

				}

			} else {
				// if(hours.equals("0")){
				// int i=Integer.parseInt(minutes);
				// if(i>=30)
				// {
				// //end
				//
				// //diffTym=diffDays + " Days " + diffMinutes+" Minutes ";
				// diffTym=diffDays + " Days ";
				// }
				// else
				// {
				// diffTym=diffDays + " Days ";
				// }
				//
				//
				// }
				// else{
				//
				// int i=Integer.parseInt(minutes);
				// if(i>=30)
				// {
				// //end
				//
				// //diffTym=diffDays +
				// " Days "+diffHours+" Hours "+diffMinutes+" Minutes ";
				// diffTym=diffDays + " Days ";
				// }
				// else
				// {
				// //diffTym=diffDays + " Days "+diffHours+" Hours ";
				// diffTym=diffDays + " Days ";
				// }
				//
				//
				// }
				diffTym = diffDays + " Days ";
			}

			System.out.print(diffDays + " days, ");
			System.out.print(diffHours + " hours, ");
			System.out.print(diffMinutes + " minutes, ");
			System.out.print(diffSeconds + " seconds.");

		} catch (Exception e) {
			e.printStackTrace();
		}

		String[] s = { diffTym };
		return s;
	}

	private String[] splittime(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf for date...." + tf);
		String fromtimetw = "";

		String k = tf.substring(0, tf.length() - 11);
		System.out.println("---value of k for date..." + k);
		// String m = k.replace("T", " From ");
		// System.out.println("---value of m..."+m);
		// String[] n = k;
		//
		// // System.out.println("--------n[1]" + n[1].trim());
		//
		// try {
		// SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
		// SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm ");
		// Date date = parseFormat.parse(n[1] + " ");
		//
		// // System.out.println(parseFormat.format(date) + " = "
		// // + displayFormat.format(date));
		//
		// fromtimetw = displayFormat.format(date);
		//
		// } catch (Exception e) {
		//
		// }
		// //
		// STRING V = K[0].TRIM();
		//
		// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(k);
			System.out
					.println("..........value of my date after conv" + myDate);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM");
		String finalDate = timeFormat.format(myDate);

		System.out.println("----------final----date-----" + finalDate);
		//
		// fromtimetw=m.substring(15, tf.length() - 7);

		// System.out.println("time------"+fromtimetw);
		// String[] v1 = { finalDate };
		String[] v2 = { finalDate };

		return v2;
	}

	// private void fetchData() {
	//
	// String
	// url="http://vritti.co/imedia/WdbIntMgmtNew.asmx/GetCSNStatus_Android?UserName="+link+"&Mobile="+mobno;
	//
	// url = url.replaceAll(" ", "%20");
	//
	//
	// System.out.println("============ internet reg url " + url);
	//
	// try {
	// System.out.println("-------  activity url --- " + url);
	// responsemsg = ut.httpGet(url);
	//
	// System.out.println("-------------  xx vale-- "
	// + responsemsg);
	// } catch (IOException e) {
	// e.printStackTrace();
	//
	// responsemsg = "wrong" + e.toString();
	// System.out
	// .println("--------- invalid for message type list --- "+responsemsg);
	//
	// }
	//
	// //
	// if (responsemsg.contains("<ServerTime>")) {
	//
	// DatabaseHandler db = new DatabaseHandler(getBaseContext());
	// System.out.println("------------- 1-- ");
	// SQLiteDatabase sql = db.getWritableDatabase();
	// System.out.println("------------- 2-- ");
	// sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser");
	// System.out.println("------------- 3-- ");
	// sql.execSQL(ut.getConnectionStatusUser());
	// System.out.println("------------- 4-- ");
	// System.out.println("------------- 5-- ");
	//
	// Cursor c = sql.rawQuery("SELECT *   FROM ConnectionStatusUser",
	// null);
	// System.out.println("------------- 6-- ");
	// ContentValues values = new ContentValues();
	// System.out.println("------------- 7-- ");
	// NodeList nl = ut.getnode(responsemsg, "Table1");
	// System.out.println("------------- 8-- ");
	// String msg = "";
	// System.out.println("------------- 9-- ");
	// String columnName, columnValue;
	// for (int i = 0; i < nl.getLength(); i++) {
	// Element e = (Element) nl.item(i);
	// System.out.println("------------- 10-- ");
	// for (int j = 0; j < c.getColumnCount(); j++) {
	// System.out.println("------------- 11-- ");
	// columnName = c.getColumnName(j);
	// columnValue = ut.getValue(e, columnName);
	//
	//
	//
	// System.out.println("-------------column name"+ columnName);
	// System.out.println("-------------column value"+ columnValue);
	//
	// values.put(columnName, columnValue);
	//
	// }
	//
	//
	// sql.insert("ConnectionStatusUser", null, values);
	// System.out.println("---------------inserted into connection status");
	//
	// }
	//
	// c.close();
	// sql.close();
	// db.close();
	//
	// } else {
	// System.out
	// .println("--------- invalid for project list --- ");
	// }
	// //updatelist();
	//
	//
	// }

	private void fetchdata() {
		// TODO Auto-generated method stub

		pd = ProgressDialog.show(InsspectionMain.this,
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

		depattask = new DownloadxmlsDataURL_new().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



		// depattask = new DownloadxmlsDataURL().execute();
	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String xx = "";
			// String
			// url="http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetCSNStatus_Android_new?Mobile="+mobno;
			String url = "http://vritti.co/iMedia/STA_Vigile_AndroidService_Test/WdbIntMgmtNew.asmx/GetCSNStatus_Android_new?Mobile="
					+ mobno;

			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);

				if (responsemsg.contains("<A>")) {
					sop = "valid";
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());

					SQLiteDatabase sql = db.getWritableDatabase();
					//sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser");
					//sql.execSQL(ut.getConnectionStatusUser());
					sql.delete("ConnectionStatusUser",null,null);

					Cursor c = sql.rawQuery(
							"SELECT *   FROM ConnectionStatusUser", null);

					ContentValues values = new ContentValues();
					NodeList nl = ut.getnode(responsemsg, "Table1");
					String msg = "";
					String columnName, columnValue;
					for (int i = 0; i < nl.getLength(); i++) {
						Element e = (Element) nl.item(i);
						for (int j = 0; j < c.getColumnCount(); j++) {
							columnName = c.getColumnName(j);

							String ncolumnname = "";
							if (columnName.equalsIgnoreCase("InstallationId"))
								ncolumnname = "A";
							else if (columnName.equalsIgnoreCase("ServerTime"))
								ncolumnname = "B";
							else if (columnName.equalsIgnoreCase("StartTime"))
								ncolumnname = "C";
							else if (columnName.equalsIgnoreCase("EndTime"))
								ncolumnname = "D";
							else if (columnName.equalsIgnoreCase("Remarks"))
								ncolumnname = "E";
							else if (columnName
									.equalsIgnoreCase("InstallationDesc"))
								ncolumnname = "F";
							else if (columnName.equalsIgnoreCase("TVStatus"))
								ncolumnname = "G";
							else if (columnName
									.equalsIgnoreCase("Last7DaysPerFormance"))
								ncolumnname = "H";
							else if (columnName
									.equalsIgnoreCase("QuickHealStatus"))
								ncolumnname = "I";
							else if (columnName.equalsIgnoreCase("STAVersion"))
								ncolumnname = "J";
							else if (columnName
									.equalsIgnoreCase("AscOrderServerTime"))
								ncolumnname = "K";
							else if (columnName
									.equalsIgnoreCase("LatestDowntimeReason"))
								ncolumnname = "L";
							else if (columnName.equalsIgnoreCase("UserName"))
								ncolumnname = "M";
							else if (columnName.equalsIgnoreCase("MobileNo"))
								ncolumnname = "N";
							else if (columnName
									.equalsIgnoreCase("SubHeadPH_No"))
								ncolumnname = "O";
							else if (columnName
									.equalsIgnoreCase("SupportAgencyName"))
								ncolumnname = "P";

							columnValue = ut.getValue(e, ncolumnname);
							values.put(columnName, columnValue);
						}
						sql.insert("ConnectionStatusUser", null, values);
					}

					c.close();

				} else {
					sop = "invalid";
					System.out
							.println("--------- invalid for project list --- ");
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

			}

			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.cancel();

			System.out.println("...............value of sop" + sop);
			if (sop.equals("valid")) {

				updatelist();

			} else {

				showD("invalid");
			}
		}

	}

	private void prepareListData() {
		// TODO Auto-generated method stub

		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		int c1 = 0;
		// listDataHeader.add("TimeSheet Entry");

		List<String> top250 = new ArrayList<String>();
		List<String> nowshowing = new ArrayList<String>();

		ArrayList<ConnectionstatusHelper> results = new ArrayList<ConnectionstatusHelper>();

		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM ConnectionStatusUser", null);
		//

		// System.out.println("------------ timeshet cursor count --- "
		// + c.getCount());

		if (c.getCount() == 0) {
			listDataHeader.add("No Station Available");

			c.close();

		}

		else {

			// c.moveToFirst();

			c.moveToFirst();

			// int column = 0;
			do {

				ConnectionstatusHelper sr = new ConnectionstatusHelper();

				// column = c.getColumnIndex("UserName");

				// sr.setinstallationId(c.getString(c.getColumnIndex("InstallationDesc")));

				int columnStn = c.getColumnIndex("InstallationDesc");
				stnnAME = c.getString(columnStn);
				System.out.println("------------stnsame" + stnnAME);
				listDataHeader.add(stnnAME);

				// Latest
				// int column1 = c.getColumnIndex("ServerTime");
				// String tf = c.getString(column1);
				// String tftym = c.getString(column1);
				// String[] tym = splitfromtym(tftym);
				// String[] v1 = splitfrom(tf);
				// String[] v2 = splittime(tf);
				// System.out.println("----value of v1"+v1);
				// sr.setStartTime(v1 [0]);
				// sr.setservertime(v2[0]);
				// sr.settymdiff(tym[0]);
				// sr.setEndTime(c.getString(c.getColumnIndex("Last7DaysPerFormance")));
				// String dates=v1[0];
				// String time=v2[0];
				// endlatest
				// String diffdate = calculatediff(dates);
				// String difftime = calculatediff(time);
				// System.out.println("-----------diffdatetime............."+
				// tym);

				// sr.setEndTime(tym[0]);
				// sr.setStartEnd(difftime);
				sr.setRemarks(c.getString(c.getColumnIndex("Remarks")));
				// ip address code
				// int column3 = c.getColumnIndex("Remarks");
				// String tf3 = c.getString(column3);
				// String[] v3 = splitIp(tf3);
				// end
				// String q=c.getString(c.getColumnIndex("QuickHealStatus"));
				// String p= c.getString(c.getColumnIndex("STAVersion"));
				//
				// top250.add(p);
				// listDataChild.put(listDataHeader.get(0), top250);
				//DatabaseHandler db1 = new DatabaseHandler(this);
				SQLiteDatabase db2 = db.getWritableDatabase();
				String[] params = new String[1];
				params[0] = stnnAME;
				Cursor c2 = db2
						.rawQuery(
								"SELECT * FROM ConnectionStatusUser where InstallationDesc=? ",
								params);
				String remarks = "";
				if (c2.getCount() == 0) {
					c2.close();

				} else {

					c2.moveToFirst();
					remarks = c2.getString(c2.getColumnIndex("STAVersion"));
					// remarks = c2.getString(c2.getColumnIndex("SourceType"));
					c2.moveToLast();

					c2.close();

				}

				// String p= c.getString(c.getColumnIndex("STAVersion"));

				top250.add(remarks);
				listDataChild.put(listDataHeader.get(c1), top250);
				++c1;

			} while (c.moveToNext());

			c.close();
		}
		//

		// listDataChild.put(listDataHeader.get(c1), top250); // Header, Child
		// data

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
		db.Close();

		filldaterefresh();

	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(InsspectionMain.this);
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
			txt.setText("No Refresh Data Available. Please check internet connection....");
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

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		// finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		try {
			SharedPreferences pref = getApplicationContext()
					.getSharedPreferences("MyPref", 0); // 0 - for private mode
			Editor editor = pref.edit();

			System.out.println("----------total number of item in list"
					+ connectionstatus.getCount());
			icount = connectionstatus.getCount();
			scount = String.valueOf(icount);
			editor.putString("csnStatuss", scount);
			editor.commit();
			System.out.println("----------scount" + scount);
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
		// dataBundle.putString("ActivityName", ActivityName);
		Intent i = new Intent(getBaseContext(), SelectMenu.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// // i.putExtras(dataBundle);
		getBaseContext().startActivity(i);
		finish();

	}

}

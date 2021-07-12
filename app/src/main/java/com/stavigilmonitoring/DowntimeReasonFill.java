package com.stavigilmonitoring;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.database.DBInterface;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DowntimeReasonFill extends Activity {
	private static final String Days = null;
	private static final String Hours = null;
	private static final String Minutes = null;
	private static final String Seconds = null;
	ProgressDialog pd, progressdialogupdateserver;
	String responsesoap = "Updated";
	ListView workspacewisedetail;
	String mobno, link;
	// AsyncTask depattask, refreshasyncupdateserver;
	String responsemsg1 = "k";
	static SimpleDateFormat dff;
	static String Ldate;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv;
	String reasonCode = "";
	static DownloadxmlsDataURL asyncfetch;
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;
	private ListView connectionstatus;
	private ListView nonrepeated;
	ArrayList<String> assignedlist = new ArrayList<String>();

	ArrayList<String> arrlist = new ArrayList<String>();
	private ListView downtimereason;
	private String stnname;
	private String startTime;
	private String endTime;
	private String stationdowntimeId;
	private AutoCompleteTextView reason;
	private TextView from;
	private TextView to;
	private String installationId;
	private Button save;
	private Button cancel;
	private TextView stnnamedownreason;
	private Spinner reasons;
	private String addDate;
	int requestCode = 111;
	private TextView dates;
	EditText edreason;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.downtimereasonfill);

		reasons = (Spinner) findViewById(R.id.spinnerreasonfill);
		save = (Button) findViewById(R.id.btndwontimereasonsave);
		cancel = (Button) findViewById(R.id.btndowntimereasoncancel);
		edreason = (EditText) findViewById(R.id.edReason);
		from = (TextView) findViewById(R.id.tvdowntimereasonfromfill);
		to = (TextView) findViewById(R.id.tvdowntimereasontofill);
		dates = (TextView) findViewById(R.id.tvdowntimereasonfilldate);
		stnnamedownreason = (TextView) findViewById(R.id.tvdowntimereasonfillsstnname);

		Bundle extras = getIntent().getExtras();
		stnname = extras.getString("stnname");
		stnnamedownreason.setText(stnname);
		System.out.println("..........stnname" + stnname);
		startTime = extras.getString("startTime");
		System.out.println("..........startTime" + startTime);
		endTime = extras.getString("endTime");
		System.out.println("..........endTime" + endTime);
		stationdowntimeId = extras.getString("stationdowntimeId");
		System.out.println("..........stationdowntimeId" + stationdowntimeId);
		installationId = extras.getString("installationId");
		System.out.println("..........installation id" + installationId);
		addDate = extras.getString("addDate");
		System.out.println("..........stationdowntimeId" + addDate);
		dates.setText(addDate);
		from.setText(startTime);
		to.setText(endTime);
		iv = (ImageView) findViewById(R.id.button_refresh_downtime_reason_fill);

		db = new DatabaseHandler(getApplicationContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		// link = dbi.GetUrl();
		// daterestr = dbi.GetDateRefresg();
		dbi.Close();
		// System.out.print("Link value"+link);

		// if (net()) {

		if (dbvalue()) {
			updatelist();
		} else {

			fetchdata();
		}

		// } else {
		// showD("nonet");
		// }

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

		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				reasonCode = reasons.getSelectedItem().toString();

				System.out.println("......reason desc is" + reasonCode);

				if ((reasonCode.length() > 0))

				{

					if (net()) {
						updateReason();

					} else {
						showD("nonet");
					}

				} else {

					// show dialog here

					showD("empty");

				}

			}
		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Bundle dataBundle = new Bundle();
				dataBundle.putString("stnname", stnname);
				dataBundle.putString("startTime", startTime);
				dataBundle.putString("endTime", endTime);
				dataBundle.putString("stationdowntimeId", stationdowntimeId);
				dataBundle.putString("installationId", installationId);
				finish();
				Intent myIntent = new Intent();
				myIntent.setClass(getApplicationContext(), DowntimeReason.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				myIntent.putExtras(dataBundle);

				startActivity(myIntent);

			}
		});
		//
		//
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

	public void updateReason() {

		progressdialogupdateserver = ProgressDialog.show(
				DowntimeReasonFill.this, "Update Reason.......",
				"Please Wait....", true, true, new OnCancelListener() {

					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						/*
						 * if (refreshasyncupdateserver != null &&
						 * refreshasyncupdateserver.getStatus() !=
						 * AsyncTask.Status.FINISHED) {
						 * refreshasyncupdateserver.cancel(true); }
						 */
					}
				});

		// refreshasyncupdateserver = new Updatetoserver().execute();
		new Updatetoserver().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public void ReasonList(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		Intent intent = new Intent(DowntimeReasonFill.this,
				DowntimeReasonList.class);
		startActivityForResult(intent, requestCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			reasonCode = data.getExtras().getString("ReasonDesc");

			edreason.setText(reasonCode);
		}
	}

	public class Updatetoserver extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... paramss) {
			// TODO Auto-generated method stub

			System.out.println("------ in back --- ");

			try {

				//DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
				SQLiteDatabase sqldb = db.getWritableDatabase();
				String[] params = new String[1];
				params[0] = reasonCode;
				System.out
						.println("==========1-------------------------------- ");
				Cursor c2 = sqldb.rawQuery(
								"SELECT * FROM DownTimeRasonFill where ReasonDescription=? ",
								params);
				System.out
						.println("==========2-------------------------------- ");
				String reasonCodeDesc = "";
				System.out
						.println("==========3-------------------------------- ");
				if (c2.getCount() == 0) {
					c2.close();

				} else {

					c2.moveToFirst();
					reasonCodeDesc = c2.getString(c2
							.getColumnIndex("ReasonCode"));
					System.out
							.println("...................reason code value is.............."
									+ reasonCodeDesc);
					c2.moveToLast();

					c2.close();

				}

				String xx = "";
				System.out.println("............1...........");
				// String url;

				// http://vritti.co/vrittiportal/webservice/DepartmentwiseActWebService.asmx/FillOldGrid?Mobileno=9922708394&DeptId=Report&DeptName=Employee%20Reporting%20to%20Me
				// url =
				// "http://intranet.vritti.co/VWBTest/webservice/ActivityWebService.asmx/WorkspaceWiseActivities?"
				// + "Mobileno=" + mobno;
				// String
				// url="http://vritti.co/imedia/WdbIntMgmtNew.asmx/GetNonReportedAdvt_Android?UserName="+link+"&Mobile="+mobno;
				String url = "http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/UpdateDownTimeReason_Android?InstallationId="
						+ installationId
						+ "&ReasonCode="
						+ reasonCodeDesc
						+ "&StationDownTimeMasterID="
						+ stationdowntimeId
						+ "&Reason=" + reasonCode;
				url = url.replaceAll(" ", "%20");

				System.out.println("============ internet reg url "
						+ url.length());

				try {
					System.out.println("-------  activity url --- " + url);
					responsemsg = ut.httpGet(url);

					System.out
							.println("-------------  xx vale of non repeated-- "
									+ responsemsg);
				} catch (NullPointerException e) {
					e.printStackTrace();
					dff = new SimpleDateFormat("HH:mm:ss");
					Ldate = dff.format(new Date());

					StackTraceElement l = new Exception().getStackTrace()[0];
					System.out.println(l.getClassName() + "/"
							+ l.getMethodName() + ":" + l.getLineNumber());
					ut = new com.stavigilmonitoring.utility();
					if (!ut.checkErrLogFile()) {

						ut.ErrLogFile();
					}
					if (ut.checkErrLogFile()) {
						ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
								+ ":" + l.getLineNumber() + "	"
								+ e.getMessage() + " " + Ldate);
					}

				} catch (IOException e) {
					e.printStackTrace();

					responsemsg = "wrong" + e.toString();
					System.out
							.println("--------- invalid for message type list --- "
									+ responsemsg);
					dff = new SimpleDateFormat("HH:mm:ss");
					Ldate = dff.format(new Date());

					StackTraceElement l = new Exception().getStackTrace()[0];
					System.out.println(l.getClassName() + "/"
							+ l.getMethodName() + ":" + l.getLineNumber());
					ut = new com.stavigilmonitoring.utility();
					if (!ut.checkErrLogFile()) {

						ut.ErrLogFile();
					}
					if (ut.checkErrLogFile()) {
						ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
								+ ":" + l.getLineNumber() + "	"
								+ e.getMessage() + " " + Ldate);
					}

				}

				//

				// /////////// fetching count ///////////

				// if ((samedate()) && (cbfirsthalf.isChecked() == false)
				// && (cbsecondhalf.isChecked() == false)
				// && (cbsecondhalftodate.isChecked() == false)) {
				//
				// System.out.println("-----  count loopp 1 ----");
				//
				// count = 1.00;
				// } else if ((samedate()) && (cbfirsthalf.isChecked() == true))
				// {
				// System.out.println("-----  count loopp 2 ----");
				// count = 0.5;
				// } else if ((samedate()) && cbsecondhalf.isChecked() == true)
				// {
				// System.out.println("-----  count loopp 3 ----");
				// count = 0.5;
				// } else if ((samedate()) && (cbtodate.isChecked() == true)
				// && (cbsecondhalftodate.isChecked() == true)) {
				// System.out.println("-----  count loopp 4 ----");
				// count = 0.5;
				// } else if ((samedate()) && (cbtodate.isChecked() == false)
				// && (cbsecondhalftodate.isChecked() == false)
				// && (cbsecondhalf.isChecked() == false)) {
				// System.out.println("-----  count loopp 5 ----");
				// count = 1.0;
				// } else if ((cbsecondhalf.isChecked() == true)
				// && (cbtodate.isChecked() == true)
				// && (cbsecondhalftodate.isChecked() == true)) {
				// System.out.println("-----  count loopp 6 ----");
				// count = (diffdate() + 0.5) - 0.5;
				//
				// } else if ((cbsecondhalf.isChecked() == true)
				// && (cbtodate.isChecked() == true)) {
				// System.out.println("-----  count loopp 7 ----");
				//
				// count = (diffdate() + 0.5);
				//
				// } else {
				// System.out.println("-----  count loopp 8 ----");
				// count = (diffdate() + 1);
				// }

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

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			progressdialogupdateserver.cancel();

			if (responsesoap.equals("Updated")) {
				// showD("done");

				Toast.makeText(getApplicationContext(),
						"Reason Updated Successfully..!", Toast.LENGTH_LONG)
						.show();

				// Bundle dataBundle = new Bundle();
				// dataBundle.putString("ActivityId", ActivityId);
				// dataBundle.putString("ActivityName", ActivityName);
				// dataBundle.putString("fromactivity", actname);
				//
				Intent myIntent = new Intent();
				myIntent.setClass(getApplicationContext(), SelectMenu.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// myIntent.putExtras(dataBundle);

				finish();
				startActivity(myIntent);

			} else {
				Toast.makeText(getApplicationContext(), "Server Error..",
						Toast.LENGTH_LONG).show();
			}

			// Toast.makeText(getApplicationContext(),
			// "Leave assigned Successfully..!!", Toast.LENGTH_LONG)
			// .show();

		}
	}

	private boolean dbvalue() {
		// TODO Auto-generated method stub
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql.rawQuery("SELECT *   FROM DownTimeRasonFill", null);

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

		assignedlist.clear();
		assignedlist.add("Select Reason");

		//DatabaseHandler db1 = new DatabaseHandler(this);
		SQLiteDatabase sqldb = db.getWritableDatabase();

		Cursor c2 = sqldb.query("DownTimeRasonFill",
				new String[] { "ReasonDescription" }, null, null, null, null,
				null);

		System.out.println("---------  cursor count --- " + c2.getCount());

		if (c2.getCount() == 0) {
			assignedlist.add("No Reason Added");
			c2.close();
		}

		else {

			c2.moveToFirst();

			do {

				assignedlist.add(c2.getString(0));
				// System.out.println("--- " + c2.getString(0));

			} while (c2.moveToNext());

			c2.close();
		}

		Collections.sort(assignedlist, String.CASE_INSENSITIVE_ORDER);
		String[] items1 = assignedlist.toArray(new String[assignedlist.size()]);

		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, items1);
		adapter1 = new ArrayAdapter<String>(this, R.layout.spinnertext, items1);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		reasons.setAdapter(adapter1);
	}

	//

	private void fetchdata() {
		if (asyncfetch == null) {
			iv.setVisibility(View.VISIBLE);
			((ProgressBar) findViewById(R.id.progressinvent1)).setVisibility(View.GONE);
			asyncfetch = new DownloadxmlsDataURL();
			asyncfetch.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			Log.e("async", "null");

		} else {
			if (asyncfetch.getStatus() == AsyncTask.Status.RUNNING) {
				Log.e("async", "running");
				iv.setVisibility(View.GONE);
				((ProgressBar) findViewById(R.id.progressinvent1))
						.setVisibility(View.VISIBLE);
			}
		}
	}

	public class DownloadxmlsDataURL extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			// try {
			// DatabaseHandler db = new DatabaseHandler(getBaseContext());
			// SQLiteDatabase sql = db.getWritableDatabase();
			//
			String xx = "";

			// String url;

			// http://vritti.co/vrittiportal/webservice/DepartmentwiseActWebService.asmx/FillOldGrid?Mobileno=9922708394&DeptId=Report&DeptName=Employee%20Reporting%20to%20Me
			// url =
			// "http://intranet.vritti.co/VWBTest/webservice/ActivityWebService.asmx/WorkspaceWiseActivities?"
			// + "Mobileno=" + mobno;
			// String
			// url="http://vritti.co/imedia/WdbIntMgmtNew.asmx/GetNonReportedAdvt_Android?UserName="+link+"&Mobile="+mobno;
			String url = "http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetReasonDownTime_Android?Mobile="
					+ mobno;
			url = url.replaceAll(" ", "%20");

			System.out.println("============ internet reg url " + url);

			try {
				System.out.println("-------  activity url --- " + url);
				responsemsg = ut.httpGet(url);

				System.out.println("-------------  xx vale of non repeated-- "
						+ responsemsg);

				//
				if (responsemsg.contains("<ReasonCode>")) {

					//DatabaseHandler db = new DatabaseHandler(getBaseContext());
					System.out.println("------------- 1-- ");
					SQLiteDatabase sql = db.getWritableDatabase();
					System.out.println("------------- 2-- ");
					//sql.execSQL("DROP TABLE IF EXISTS DownTimeRasonFill");
					System.out.println("------------- 3-- ");
					//sql.execSQL(ut.getDownTimeRasonFill());
					sql.delete("DownTimeRasonFill",null,null);

					System.out.println("------------- 4-- ");
					System.out.println("------------- 5-- ");

					Cursor c = sql.rawQuery(
							"SELECT *   FROM DownTimeRasonFill", null);
					System.out.println("------------- 6-- ");
					ContentValues values = new ContentValues();
					System.out.println("------------- 7-- ");
					NodeList nl = ut.getnode(responsemsg, "Table");
					String msg = "";
					String columnName, columnValue;
					for (int i = 0; i < nl.getLength(); i++) {
						Element e = (Element) nl.item(i);
						for (int j = 0; j < c.getColumnCount(); j++) {

							columnName = c.getColumnName(j);
							columnValue = ut.getValue(e, columnName);

							System.out.println("-------------column name"
									+ columnName);
							System.out.println("-------------column value"
									+ columnValue);

							values.put(columnName, columnValue);

						}

						sql.insert("DownTimeRasonFill", null, values);

					}

					c.close();

				} else {
					System.out.println("--------- invalid for AD list --- ");
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

				responsemsg = "wrong" + e.toString();
				System.out
						.println("--------- invalid for message type list --- "
								+ responsemsg);
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

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			try {
				// pd.cancel();
				updatelist();

				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(R.id.progressinvent1))
						.setVisibility(View.GONE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressinvent1))
					.setVisibility(View.VISIBLE);
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
		db.Close();

		filldaterefresh();

	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(DowntimeReasonFill.this);
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
			txt.setText("No Data Available for Workspacewise Management.");
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

		Bundle dataBundle = new Bundle();
		dataBundle.putString("stnname", stnname);
		dataBundle.putString("startTime", startTime);
		dataBundle.putString("endTime", endTime);
		dataBundle.putString("stationdowntimeId", stationdowntimeId);
		dataBundle.putString("installationId", installationId);
		Intent i = new Intent(DowntimeReasonFill.this, DowntimeReason.class);
		// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtras(dataBundle);
		startActivity(i);
		finish();

	}

}

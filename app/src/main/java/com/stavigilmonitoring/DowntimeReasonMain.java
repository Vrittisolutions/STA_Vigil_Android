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

import com.adapters.DowntimeReasonAdaptMain;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DowntimeReasonMain extends Activity {
	private static final String Days = null;
	private static final String Hours = null;
	private static final String Minutes = null;
	private static final String Seconds = null;
	ProgressDialog pd;
	ListView workspacewisedetail;
	String mobno, link;
	// AsyncTask depattask;
	String responsemsg1 = "k";
	static SimpleDateFormat dff;
	static String Ldate;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv;
	String Type;
	DownloadxmlsDataURL asyncfetch;
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	DowntimeReasonAdaptMain listAdpt = null;
	String daterestr;
	private ListView connectionstatus;
	private ListView nonrepeated;
	Intent intent;

	ArrayList<String> arrlist = new ArrayList<String>();
	private ListView downtimereason;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.downtimereasonmain);
		// Bundle extras = getIntent().getExtras();
		// ActivityName = extras.getString("ActivityName");
		// ActivityId = extras.getString("ActivityId");
		iv = (ImageView) findViewById(R.id.button_refresh_downtime_reason_main);
		downtimereason = (ListView) findViewById(R.id.lvdowntimereasonmain);
		// iv = (ImageView) findViewById(R.id.button_refresh_workspace);
		// txtdate = (TextView) findViewById(R.id.txtdaterefreshworkspace);
		// txtdaterefresh = (TextView)
		// findViewById(R.id.txtdaterefreshlinkworkspace);
		// actname = extras.getString("fromactivity");

		intent = getIntent();
		Type = intent.getStringExtra("Type");
		TextView tvstation = (TextView) findViewById(R.id.onactivitynamereassign);
		tvstation.setText(Type + "-Downtime Station");

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
		} else if (isnet()) {

			fetchdata();
		} else {
			showD("nonet");
		}

		// } else {
		// showD("nonet");
		// }

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isnet()) {
					asyncfetch = null;
					asyncfetch = new DownloadxmlsDataURL();
					asyncfetch.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					showD("nonet");
				}

			}
		});
		//
		downtimereason.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {

				if (dbvalue()) {
					// Object o = nonrepeated.getItemAtPosition(position);
					// DowntimeHelper fullObject = (DowntimeHelper) o;

					String s = arrlist.get(position);

					editActivity(s);

				} else {

					Toast.makeText(getBaseContext(),
							"No Information available..", Toast.LENGTH_LONG)
							.show();
				}

			}
		});
		((EditText) findViewById(R.id.edfitertext))
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

						listAdpt.filter(((EditText) findViewById(R.id.edfitertext))
								.getText().toString().trim()
								.toLowerCase(Locale.getDefault()));
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

	public void editActivity(String s) {

		System.out.println("==========@#@# actid " + s);
		//DatabaseHandler db1 = new DatabaseHandler(this);
		SQLiteDatabase sqldb = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = s;
		// params[0] = s;
		Cursor c2 = sqldb.rawQuery(
						"SELECT * FROM DownTimeRason where InstallationName=? ",
						params);
		String stnname = "";

		if (c2.getCount() == 0) {
			c2.close();

		} else {

			c2.moveToFirst();
			stnname = c2.getString(c2.getColumnIndex("InstallationName"));

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
		myIntent.setClass(getApplicationContext(), DowntimeReason.class);

		myIntent.putExtras(dataBundle);
		startActivity(myIntent);
		// finish();
		// System.out.println("------------- 1");

	}

	public void FilterClick(View v) {
		if (((EditText) findViewById(R.id.edfitertext)).getVisibility() == View.VISIBLE) {
			((EditText) findViewById(R.id.edfitertext))
					.setVisibility(View.GONE);
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		} else if (((EditText) findViewById(R.id.edfitertext)).getVisibility() == View.GONE) {
			((EditText) findViewById(R.id.edfitertext))
					.setVisibility(View.VISIBLE);
			EditText textView = (EditText) findViewById(R.id.edfitertext);
			textView.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
		}

	}

	private boolean dbvalue() {
		// TODO Auto-generated method stub
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql.rawQuery("SELECT *   FROM DownTimeRason", null);

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

		System.out.println("====#$#$#$#$#$#$  in update list downtime");
		final ArrayList<String> searchResults = getDetail();
		// DowntimeReasonHelper1 d=new DowntimeReasonHelper1();
		listAdpt = new DowntimeReasonAdaptMain(this, searchResults);
		downtimereason.setAdapter(listAdpt);

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

	public ArrayList<String> getDetail() {

		arrlist.clear();
		// ArrayList<String> results = new ArrayList<String>();
		// ArrayList<DowntimeHelper> results = new ArrayList<DowntimeHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor c1 = sql.rawQuery("select * from DownTimeRason", null);
		Log.e("Downtime Station cnt...", "" + c1.getCount());
		Cursor c = sql
				.rawQuery(
						"Select distinct InstallationName from DownTimeRason c1 inner join AllStation c2 on c1.InstallationId=c2.InstallationId where c2.NetworkCode='"
								+ Type + "'", null);
		Log.e("Downtime Station cnt...", "" + c.getCount());
		if (c.getCount() == 0) {

			// sr.setStartEnd("");
			// sr.setRemarks("");
			arrlist.add("");

			// arrlist.addAll("");

			c.close();

			return arrlist;
		} else {

			c.moveToFirst();

			int column = 0;
			do {
				String s = c.getString(c.getColumnIndex("InstallationName"));

				if (arrlist.contains(s)) {
					System.out
							.println("=@#@#  already present in list downtime");
				} else {
					arrlist.add(c.getString(c
							.getColumnIndex("InstallationName")));
				}

			} while (c.moveToNext());

			c.close();
		}
		return arrlist;

	}

	private void fetchdata() {
		if (asyncfetch == null) {
			iv.setVisibility(View.VISIBLE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.GONE);
			asyncfetch = new DownloadxmlsDataURL();
			asyncfetch.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

			Log.e("async", "null");

		} else {
			if (asyncfetch.getStatus() == AsyncTask.Status.RUNNING) {
				Log.e("async", "running");
				iv.setVisibility(View.GONE);
				((ProgressBar) findViewById(R.id.progressBar1))
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
			String url = "http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetCurrentlyStationDowntime_Android?Mobile="
					+ mobno;
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
					System.out.println("------------- 2-- ");
					//sql.execSQL("DROP TABLE IF EXISTS DownTimeRason");
					System.out.println("------------- 3-- ");
					//sql.execSQL(ut.getDownTimeRason());
					sql.delete("DownTimeRason",null,null);

					System.out.println("------------- 4-- ");
					System.out.println("------------- 5-- ");

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

							System.out.println("-------------column name"
									+ columnName);
							System.out.println("-------------column value"
									+ columnValue);

							values.put(columnName, columnValue);

						}

						sql.insert("DownTimeRason", null, values);

					}

					c.close();

				} else {
					sop = "invalid";
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

			return sop;
		}

		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				// pd.cancel();
				updatelist();

				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.GONE);
			} catch (Exception e) {
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
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
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

		final Dialog myDialog = new Dialog(DowntimeReasonMain.this);
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
			txt.setText("No Refresh data Available. Please check internet connection...");
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

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = getApplicationContext()
				.getSharedPreferences("SCROLLDOWNTIMEREASON",
						Context.MODE_PRIVATE);
		int scroll = preferences.getInt("ScrollValueDowntimeReaosn", 0);
		System.out
				.println(".............value of scroll at resume>>>>>>>>>>>>>>>>>>>"
						+ scroll);
		// connectionstatus.scrollTo(0, scroll);
		downtimereason.smoothScrollToPosition(scroll);
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SharedPreferences preferences = getApplicationContext()
				.getSharedPreferences("SCROLLDOWNTIMEREASON",
						Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		int scroll = downtimereason.getFirstVisiblePosition();
		// int scrollx = connectionstatus.getScrollX();
		System.out.println(".............value of scroll>>>>>>>>>>>>>>>>>>>"
				+ scroll);
		editor.putInt("ScrollValueDowntimeReaosn", scroll);
		editor.commit();
		// finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		// Bundle dataBundle = new Bundle();
		// dataBundle.putString("ActivityId", ActivityId);
		// dataBundle.putString("ActivityName", ActivityName);
		Intent i = new Intent(getBaseContext(),
				DowntimeReasonFillStateWise.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// i.putExtras(dataBundle);
		getBaseContext().startActivity(i);
		finish();

	}

}

package com.stavigilmonitoring;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.PendingClipsMainAdapt;
import com.beanclasses.PendingStateList;
import com.database.DBInterface;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.AdapterView.OnItemClickListener;

public class PendingClipsMain extends Activity {
	List<PendingStateList> searchResults;
	ImageView iv,btnadd;
	String sop, responsemsg, mobno;
	static DownloadxmlsDataURL_new asyncfetch_csnstate;
	ListView lstcsn;
	String Type, CallFrom;
	PendingClipsMainAdapt ClipsAdapt;
	String finalDate, Subtype;
	static SimpleDateFormat dff;
	static String Ldate;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pendingclipmain);

		searchResults = new ArrayList<PendingStateList>();
		lstcsn = (ListView) findViewById(R.id.lstcsn);
		iv = (ImageView) findViewById(R.id.button_refresh_tvstatus_main);
		btnadd = (ImageView) findViewById(R.id.button_alert_add);
		btnadd.setImageResource(R.drawable.work_assign);
		btnadd.setVisibility(View.VISIBLE);
		Bundle extras = getIntent().getExtras();

		Type = extras.getString("Type");
		Subtype = extras.getString("SubType");
		CallFrom= extras.getString("CallFrom");

		TextView tvstation = (TextView) findViewById(R.id.tvpendingclipstitle);
		tvstation.setText("Pending Clips - "+ Subtype);

		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();

		if (asyncfetch_csnstate != null
				&& asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}
		Log.e("Downtime...", " dbval : " + dbvalue());

		if (dbvalue()) {
			updatelist();

		} else if (ut.isnet(getApplicationContext())) {
			fetchdata();

		} else {
			ut.showD(PendingClipsMain.this, "nonet");
		}

		if(CallFrom.equalsIgnoreCase("SupporterList")){
			lstcsn.setClickable(false);
		}else if(CallFrom.equalsIgnoreCase("PendingClipStateFilter")){
			lstcsn.setClickable(true);
			lstcsn.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
					Intent i = new Intent(PendingClipsMain.this, PendingClips.class);
					i.putExtra("Station", searchResults.get(position).GetStateName());
					startActivity(i);
				}
			});
		}

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ut.isnet(getApplicationContext())) {
					asyncfetch_csnstate = null;
					asyncfetch_csnstate = new DownloadxmlsDataURL_new();
					asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					// fetchdata();
				} else {
					ut.showD(PendingClipsMain.this, "nonet");
				}
			}
		});
		btnadd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),WorkAssign_AssignActivity.class);
				intent.putExtra("Activity", "PendingClipsStateWise");
				intent.putExtra("Type", "");
				startActivity(intent);
				finish();
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

						ClipsAdapt
								.filter(((EditText) findViewById(R.id.edfitertext))
										.getText().toString().trim()
										.toLowerCase(Locale.getDefault()));
					}
				});

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
		try {

			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			// Cursor cursor = sql.rawQuery("SELECT *   FROM AllStation", null);
			Cursor c = sql.rawQuery("SELECT * FROM PendingClips", null);
			if (c != null && c.getCount() > 0) {
				if (c.getColumnIndex("instalationid") < 0) {
					c.close();
					return false;
				} else {
					c.close();
					return true;
				}
			} else {
				c.close();
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

	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		/*
		 * Intent i = new Intent(getBaseContext(), PendingClipsStateWise.class);
		 * i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * getBaseContext().startActivity(i); finish();
		 */

	}

	private void fetchdata() {
		// new DownloadxmlsDataURL_new().execute();
		if (asyncfetch_csnstate == null) {
			iv.setVisibility(View.VISIBLE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.GONE);

			Log.e("async", "null");
			asyncfetch_csnstate = new DownloadxmlsDataURL_new();
			asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			if (asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
				Log.e("async", "running");
				iv.setVisibility(View.GONE);
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.VISIBLE);
			}
		}

	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String url = "http://sta.vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetListOfPendingDownloadingAdvertisment?Mobile="
					+ mobno + "&NetworkCode='ksrtc'";

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = com.stavigilmonitoring.utility.httpGet(url);
				Log.e("csn status", "resmsg : " + responsemsg);

				if (responsemsg.contains("<instalationid>")) {
					sop = "valid";
					String columnName, columnValue;
					/*
					 * DatabaseHandler db = new
					 * DatabaseHandler(getBaseContext()); SQLiteDatabase sql =
					 * db.getWritableDatabase();
					 */

					//sql.execSQL("DROP TABLE IF EXISTS PendingClips");
					//sql.execSQL(ut.getPendingClips());
					sql.delete("PendingClips",null,null);

					Cursor cur = sql.rawQuery("SELECT * FROM PendingClips",
							null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table1");
					// String msg = "";
					// String columnName, columnValue;
					/*Log.e("All Station data...",
							" fetch data : " + nl1.getLength());*/
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							values1.put(columnName, columnValue);

							// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
						}
						sql.insert("PendingClips", null, values1);
					}

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
					ut.showD(PendingClipsMain.this, "nonet");
				}
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.GONE);

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
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

	}

	private void updatelist() {
		searchResults.clear();
		String sda = "-";
		String v1 = " ";
		String v = " ";
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor c = sql.rawQuery(
						"SELECT DISTINCT PendingClips.InstallationDesc FROM PendingClips INNER JOIN ConnectionStatusFilter ON ConnectionStatusFilter.InstalationId=PendingClips.instalationid WHERE ConnectionStatusFilter.SubNetworkCode='"
								+ Subtype + "'", null);
		c.moveToFirst();
		int cnt = 0;
		Log.e("Pending n/w count", "" + c.getCount());
		if (c.getCount() > 0) {
			do {

				String station = c.getString(c
						.getColumnIndex("InstallationDesc"));

				Cursor cr = sql
						.rawQuery(
								"SELECT s.InstallationId,ServerTime,s1.InstallationDesc FROM ConnectionStatusUser s "
										+ " INNER JOIN ConnectionStatusUser1 s1 ON s.InstallationId=s1.InstallationId where s1.InstallationDesc='"
										+ station + "'", null);
				// Select DISTINCT s.InstallationDesc , s.CallibrationDate, s.AO
				// , s.InstalationId ,s.ServerTime from SoundLevel_new s INNER
				// JOIN SoundLevel s1 ON s1.InstallationId = s.InstalationId
				// INNER JOIN ConnectionStatusFilter S2 ON
				// S2.InstalationId=s.InstalationId WHERE S2.SubNetworkCode='"
				// + subType + "' ORDER BY s.InstallationDesc

				Log.e("Pending n/w count", "" + cr.getCount());
				if (cr.getCount() > 0) {
					cr.moveToFirst();
					sda = cr.getString(cr.getColumnIndex("ServerTime"));
					// int column2 = c.getColumnIndex("ServerTime");
					 System.out.println("ServerTime " + sda);
					// String tf_connection = c.getString(column2);

					v1 = splitConnectionDT(sda);
					System.out.println("v1 " + v1);
					v = splitfrom1(sda);
					System.out.println("v " +v);
				} else {
					v = "";
					v1 = "Connected";
				}

				Cursor c1 = sql.rawQuery(
						"SELECT * FROM PendingClips WHERE InstallationDesc='"
								+ station + "'", null);
				Log.e("Pending n/w count", "" + c1.getCount());

				if (c1.getCount() > 0) {
					c1.moveToFirst();

					int column = 0;
					// do {

					int Count = c1.getCount();

					PendingStateList s = new PendingStateList();
					// String sd =
					// c1.getString(c1.getColumnIndex("ServerTime"));
					// int column1 = c1.getColumnIndex("ServerTime");
					// s.setServerTime(c1.getString(column1));
					// String[] tym = splitConnectionDT(c1.getString(column1));

					s.SetStateName(station);
					s.SetCount(Count);
					s.setServerTime(v1 + " " + v);
					searchResults.add(s);
					Count = 0;
					// } while (c1.moveToNext());
				} else {

				}
			} while (c.moveToNext());

		}

		if(searchResults.isEmpty()){
			showD("NoPendingClips");
		}else {
			ClipsAdapt = new PendingClipsMainAdapt(getApplicationContext(), searchResults);
			lstcsn.setAdapter(ClipsAdapt);
		}

	}

	private String splitConnectionDT(String tf) {
		long diffDays = 0;
		System.out.println("---value of tf for date...." + tf);
		String fromtimetw = "";

		String k = tf.substring(0, tf.length() - 11);
		System.out.println("---value of k for date server date..." + k);
		// MM/dd/yyyy hh:mm:ss a
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(k);
			System.out
					.println("..........value of my date after conv" + myDate);

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
		System.out.println("..............final date of update link>>>>>"
				+ finalDate);

		final String dateStart = finalDate;
		DateFormat dateFormat1 = new SimpleDateFormat("dd MMM, yyyy");
		Date date = new Date();
		System.out.println("date format of system......................"
				+ dateFormat1.format(date));
		System.out
				.println("<<<<<<<<<<<<<<<<date format startdate......................"
						+ dateStart);
		System.out.println("date format of web tym......................"
				+ date);
		final String dateStop = dateFormat1.format(date);
		System.out
				.println("<<<<<<<<<<<<<<<<date format dateStop......................"
						+ dateStop);
		Date d1 = null;
		Date d2 = null;
		String diffTym = "";

		try {
			d1 = dateFormat1.parse(dateStart);
			d2 = dateFormat1.parse(dateStop);
			System.out.println("d2......................" + d2);
			// in milliseconds
			long diff = d2.getTime() - d1.getTime();

			diffDays = diff / (24 * 60 * 60 * 1000);

		} catch (Exception e) {
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

		if (diffDays == 0) {
			return "Today";

		} else if (diffDays == 1) {
			return "Yesterday";
		} else {
			return finalDate;
		}

	}

	private String splitfrom1(String tf) {
		String fromtimetw = "";
		//String k = tf.substring(9, tf.length() - 0);
		String k[] = tf.split(" ");
		String v1 = k[1];

		return v1;
	}

	protected void showD(final String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(PendingClipsMain.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);

		final TextView txt = (TextView) myDialog.findViewById(R.id.dialoginfogototextsmall);
		if (string.equals("Done")) {
			myDialog.setTitle(" ");
			txt.setText("Data Saved");
		} else if (string.equals("Error")) {
			myDialog.setTitle(" ");
			txt.setText("Server Error.. Please try after some time");
		} else if (string.equals("Issue")) {
			myDialog.setTitle(" ");
			txt.setText("Sorry you cannot complete this activity");
		}else if(string.equals("NoPendingClips")){
			txt.setText("No pending clips");
		}

		Button btn = (Button) myDialog
				.findViewById(R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				myDialog.dismiss();
			}
		});

		myDialog.show();
	}

	/*
	 * class StateList { String StateName; int scount; String ServerTime;
	 * 
	 * public String getServerTime() { return ServerTime; }
	 * 
	 * public void setServerTime(String serverTime) { ServerTime = serverTime; }
	 * 
	 * public StateList() { }
	 * 
	 * public String GetStateName() { return StateName; }
	 * 
	 * public void SetStateName(String s) { this.StateName = s; }
	 * 
	 * public int GetSCount() { return scount; }
	 * 
	 * public void SetCount(int s) { this.scount = s; }
	 * 
	 * }
	 */
}

package com.stavigilmonitoring;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.CSNStateAdapter;
import com.beanclasses.StateList;
import com.database.DBInterface;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ConnectionStatusStatewise extends Activity {
	List<StateList> searchResults;
	ImageView iv, btnadd;
	String sop, responsemsg, mobno;
	static DownloadxmlsDataURL_new asyncfetch_csnstate;
	//ListView lstcsn;
	GridView lstcsn;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	static SimpleDateFormat dff;
	static String Ldate;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.csnstatewise);

		searchResults = new ArrayList<StateList>();
		lstcsn = findViewById(R.id.lstcsn);
		iv = (ImageView) findViewById(R.id.button_refresh_nonrepeated_main);
		btnadd = (ImageView) findViewById(R.id.button_alert_add);
		btnadd.setImageResource(R.drawable.work_assign);
		btnadd.setVisibility(View.VISIBLE);

		db = new DatabaseHandler(getApplicationContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		if (asyncfetch_csnstate != null
				&& asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

		if (dbvalue()) {
			updatelist();
		} else if (ut.isnet(getApplicationContext())) {
			fetchdata();
		} else {
			try{
				ut.showD(ConnectionStatusStatewise.this,"nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		lstcsn.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Boolean value = subnetworkcheck(searchResults.get(position)
						.getStateName());
				Boolean check = value;
				if (value) {
                  Intent i = new Intent(ConnectionStatusStatewise.this, ConnectionStatusStateFilter.class);
					i.putExtra("Type", searchResults.get(position).getStateName());
					i.putExtra("Count", searchResults.get(position).GetSCount());
					startActivity(i);

				} else {
					if (stationpresent()) {

						String Flag = "NoSubnetWork";
						Intent intent = new Intent(getApplicationContext(), ConnectionStatusMain.class);
						intent.putExtra("Type", searchResults.get(position).getStateName());
						intent.putExtra("subType", searchResults.get(position).getStateName());
						intent.putExtra("NoSubnetWork", Flag);
						// subType = intent.getStringExtra("subType");

						startActivity(intent);
						/*Toast.makeText(getBaseContext(),
								"No SubnetWork Present..", Toast.LENGTH_LONG)
								.show();*/

					} else {

						Toast.makeText(getBaseContext(),
								"No Station Present..", Toast.LENGTH_LONG)
								.show();
					}
				}
			}
		});

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ut.isnet(getApplicationContext())) {
					asyncfetch_csnstate = null;
					asyncfetch_csnstate = new DownloadxmlsDataURL_new();
					asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					// fetchdata();
				} else {
					try{
						ut.showD(ConnectionStatusStatewise.this,"nonet");
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
				intent.putExtra("Activity", "ConnectionStatusStatewise");
				intent.putExtra("Type", "");
				startActivity(intent);
			//	finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.runFinalization();
	    Runtime.getRuntime().gc();
	    System.gc();
	}

	protected boolean stationpresent() {
		// TODO Auto-generated method stub

		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM ConnectionStatusUser", null);

		int count = c.getCount();

		c.close();
		/*sql.close();
		db.close();*/

		if (count == 0) {
			return false;
		} else {

			return true;
		}

	}

	private boolean subnetworkcheck(String type) {
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		ArrayList<String> mSubnetworklist = new ArrayList<String>();
		Cursor c = sql.rawQuery(
				"SELECT DISTINCT SubNetworkCode FROM ConnectionStatusUser where Type='"
						+ type + "'", null);

		if (c.getCount() == 0) {
			c.close();
			/*db1.close();
			sql.close();*/
			return false;

		} else if (c.getCount() > 0) {

			c.moveToFirst();
			do {

				int stncnt = 0;
				String Type = c.getString(0);

				mSubnetworklist.add(Type);

			} while (c.moveToNext());
			c.close();
			/*db1.close();
			sql.close();*/
		}

		if (mSubnetworklist.contains(type) && mSubnetworklist.size() > 1) {
			return true;
		} else {
			
			if (mSubnetworklist.contains(type)) {
				return false;
			} else {
				return true;
			}

		}

	}

	private boolean dbvalue() {
		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery(
					"SELECT *   FROM ConnectionStatusUser", null);
			if (cursor != null && cursor.getCount() > 0) {
				if (cursor.getColumnIndex("Type") < 0) {
					//cursor.close();
					/*sql.close();
					db1.close();*/
					return false;
				} else {
					//cursor.close();
					/*sql.close();
					db1.close();*/
					return true;
				}
			} else {
				//cursor.close();
				/*sql.close();
				db1.close();*/

			}
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
		return false;
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

	public class DownloadxmlsDataURL_new extends AsyncTask<String, Void, String> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(ConnectionStatusStatewise.this);
			progressDialog.setMessage("Updating database...");
			//progressDialog.setCanceledOnTouchOutside(false);
			//progressDialog.setCancelable(false);
			progressDialog.show();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();

			String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetCSNStatus_Android_new?Mobile="
					+ mobno;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);
				Log.i("csn status", "resmsg : " + responsemsg);
				Log.d("csn status", "resmsg : " + responsemsg);
				Log.e("csn status", "resmsg : " + responsemsg);

				if (responsemsg.contains("<A>")) {
					sop = "valid";
					String columnName, columnValue;
					/*
					 * DatabaseHandler db = new
					 * DatabaseHandler(getBaseContext()); SQLiteDatabase sql =
					 * db.getWritableDatabase();
					 */

					sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser");
					sql.execSQL(ut.getConnectionStatusUser());
					Cursor cur = sql.rawQuery(
							"SELECT *   FROM ConnectionStatusUser", null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table1");
					// String msg = "";
					// String columnName, columnValue;
					Log.e("sts main...", " fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						String conn = "invalid";
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);

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
							// else
							// if(columnName.equalsIgnoreCase("Last7DaysPerFormance"))
							// ncolumnname="H";
							// else
							// if(columnName.equalsIgnoreCase("QuickHealStatus"))
							// ncolumnname="I";
							else if (columnName.equalsIgnoreCase("STAVersion"))
								ncolumnname = "J";
							else if (columnName
									.equalsIgnoreCase("AscOrderServerTime"))
								ncolumnname = "K";
							else if (columnName
									.equalsIgnoreCase("LatestDowntimeReason"))
								ncolumnname = "L";
							// else if(columnName.equalsIgnoreCase("UserName"))
							// ncolumnname="M";
							// else
							// if(columnName.equalsIgnoreCase("SubHeadPH_No"))
							// ncolumnname="O";
							// else
							// if(columnName.equalsIgnoreCase("SupportAgencyName"))
							// ncolumnname="P";
							else if (columnName.equalsIgnoreCase("Type"))
								ncolumnname = "N";
							else if (columnName
									.equalsIgnoreCase("SubNetworkCode"))
								ncolumnname = "R";
							// String tftym=ut.getValue(e, "B");

							columnValue = ut.getValue(e, ncolumnname);

							if (columnName.equalsIgnoreCase("ServerTime")) {
								try {
									Calendar cal = Calendar.getInstance();
									// SimpleDateFormat format = new
									// SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa");
									//2017-03-17 08:53:26
									/*SimpleDateFormat format = new SimpleDateFormat(
											"yyyy-MM-dd hh:mm:ss");
									
									SimpleDateFormat format = new SimpleDateFormat(
											"dd-MM-yyyy hh:mm:ss");*/

									SimpleDateFormat format = new SimpleDateFormat(
											"MM/dd/yyyy hh:mm:ss aa",Locale.ENGLISH);
									 Log.e("columnValue...","diffDays: "+
											 columnValue);
									 Log.e("columnValue...","diffDays: "+
											 "MM/dd/yyyy hh:mm:ss aa");
									Date Startdate = format.parse(columnValue);
									Date Enddate = cal.getTime();
									long diff = Enddate.getTime()
											- Startdate.getTime();
									long diffSeconds = diff / 1000 % 60;
									long diffMinutes = diff / (60 * 1000) % 60;
									long diffHours = diff / (60 * 60 * 1000)
											% 24;
									long diffDays = diff
											/ (24 * 60 * 60 * 1000);

									Log.e("getdetails", "sd : " + Startdate
											+ " ed: " + Enddate + " d: "
											+ diffDays + " h: " + diffHours
											+ " m:" + diffMinutes);
									/*
									 * Log.e("printdiff.........","diffDays: "+
									 * diffDays);
									 * Log.e("printdiff.........","diffHours: "
									 * +diffHours);
									 * Log.e("printdiff.........","diffMinutes: "
									 * +diffMinutes);
									 * Log.e("printdiff.........",
									 * "diffSeconds: "+diffSeconds);
									 */

									if (diffDays == 0 && diffHours == 0
											&& diffMinutes <= 15) {

									} else {
										conn = "valid";
									}
								} catch (Exception ex) {
									ex.printStackTrace();
									dff = new SimpleDateFormat("HH:mm:ss");
									Ldate = dff.format(new Date());

									StackTraceElement l = new Exception()
											.getStackTrace()[0];
									System.out.println(l.getClassName() + "/"
											+ l.getMethodName() + ":"
											+ l.getLineNumber());
									ut = new com.stavigilmonitoring.utility();
									if (!ut.checkErrLogFile()) {

										ut.ErrLogFile();
									}
									if (ut.checkErrLogFile()) {
										ut.addErrLog(l.getClassName() + "/"
												+ l.getMethodName() + ":"
												+ l.getLineNumber() + "	"
												+ ex.getMessage() + " " + Ldate);
									}

								}
							}

							values1.put(columnName, columnValue);

							// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
						}
						if (conn == "valid")
							Log.e("csn status", "resmsg : " + values1);
							sql.insert("ConnectionStatusUser", null, values1);
					}

					//cur.close();
					/*sql.close();
					db.close();*/

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
			}

			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {

				if (sop.equals("valid")) {
					updatelist();
				} else {
					try{
						ut.showD(ConnectionStatusStatewise.this,"invalid");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.GONE);

				DateFormat dateFormat = new SimpleDateFormat(
						"dd-MMM-yyyy HH:mm:ss aa", Locale.ENGLISH);
				Date date = new Date();
				String datestring = dateFormat.format(date);
				SharedPreferences prefDate = getApplicationContext()
						.getSharedPreferences("MyPrefDate",
								Context.MODE_PRIVATE); // 0 - for private mode
				Editor editorDate = prefDate.edit();
				editorDate.putString("Dates", datestring);
				editorDate.commit();
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
			progressDialog.dismiss();
		}

	}

	private void updatelist() {
		try{
			searchResults.clear();
			//DatabaseHandler db = new DatabaseHandler(this);
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor c = sql.rawQuery(
					"SELECT DISTINCT Type FROM ConnectionStatusUser ORDER BY Type", null);
			if (c.getCount() > 0) {
				c.moveToFirst();
				do {
					int stncnt = 0;
					String Type = c.getString(0);

					Cursor c1 = sql
							.rawQuery(
									"SELECT DISTINCT InstallationId,ServerTime , Remarks,Last7DaysPerFormance,QuickHealStatus,STAVersion ,TVStatus,LatestDowntimeReason,Type  FROM ConnectionStatusUser WHERE Type='"
											+ c.getString(0)
											+ "' ORDER BY Type Desc", null);
					if (c1.getCount() > 0) {
						c1.moveToFirst();
						do {
							int column1 = c1.getColumnIndex("ServerTime");
							// String[] tym = splitfromtym(c1.getString(column1));
							try {
								Calendar cal = Calendar.getInstance();
								//17-03-2017 14:22:52
							/*SimpleDateFormat format = new SimpleDateFormat(
									"dd-MM-yyyy hh:mm:ss");*/
								SimpleDateFormat format = new SimpleDateFormat(
										"MM/dd/yyyy hh:mm:ss aa",Locale.ENGLISH);
								String srt = c1.getString(column1);
								Date Startdate = format.parse(srt);
								Date Enddate = cal.getTime();
								long diff = Enddate.getTime() - Startdate.getTime();
								long diffSeconds = diff / 1000 % 60;
								long diffMinutes = diff / (60 * 1000) % 60;
								long diffHours = diff / (60 * 60 * 1000) % 24;
								long diffDays = diff / (24 * 60 * 60 * 1000);

								if (diffDays == 0 && diffHours == 0
										&& diffMinutes <= 15) {

								} else {
									stncnt = stncnt + 1;
								}
							} catch (Exception ex) {
								dff = new SimpleDateFormat("HH:mm:ss");
								Ldate = dff.format(new Date());

								StackTraceElement l = new Exception()
										.getStackTrace()[0];
								System.out.println(l.getClassName() + "/"
										+ l.getMethodName() + ":"
										+ l.getLineNumber());
								ut = new com.stavigilmonitoring.utility();
								if (!ut.checkErrLogFile()) {

									ut.ErrLogFile();
								}
								if (ut.checkErrLogFile()) {
									ut.addErrLog(l.getClassName() + "/"
											+ l.getMethodName() + ":"
											+ l.getLineNumber() + "	"
											+ ex.getMessage() + " " + Ldate);
								}
							}

						} while (c1.moveToNext());
					}
					//Type = Type.replaceAll("0", "");
					//Type = Type.replaceAll("1", "");
					if (!Type.trim().equalsIgnoreCase("")) {
						StateList sitem = new StateList();
						sitem.setStateName(Type);
						sitem.SetCount(stncnt);
						searchResults.add(sitem);
					}
				} while (c.moveToNext());

			}
			lstcsn.setAdapter(new CSNStateAdapter(ConnectionStatusStatewise.this, searchResults));
		}catch (Exception e){
			e.printStackTrace();
		}
	}

/*	class StateList {
		String StateName;
		int scount;

		public StateList() {
		}

		public String GetStateName() {
			return StateName;
		}

		public void SetStateName(String s) {
			this.StateName = s;
		}

		public int GetSCount() {
			return scount;
		}

		public void SetCount(int s) {
			this.scount = s;
		}

	}*/

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		int scount = 0;
		for (int i = 0; i < searchResults.size(); i++)
			scount = scount + searchResults.get(i).GetSCount();

		try {
			// /////////*********connection Status Count
			// *********//////////////////
			SharedPreferences pref = getApplicationContext()
					.getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0

			Editor editor = pref.edit();
			editor.putString("csnStatusCount", scount + "");
			editor.commit();
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

		Intent i = new Intent(ConnectionStatusStatewise.this, SelectMenu.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
	}

}

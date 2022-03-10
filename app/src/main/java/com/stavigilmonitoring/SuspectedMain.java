package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.StationEnquiryAdptr;
import com.adapters.SuspectedAdaptMain;
import com.beanclasses.StateList;
import com.beanclasses.SuspectedHelper;
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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SuspectedMain extends Activity {
	private static final String Days = null;
	private static final String Hours = null;
	private static final String Minutes = null;
	private static final String Seconds = null;
	ProgressDialog pd;
	ListView workspacewisedetail;
	String mobno, link;
	static SimpleDateFormat dff;
	static String Ldate;
	AsyncTask depattask;
	// ExpandableListAdapter listAdapter;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv;
	ArrayList<String> arrlist = new ArrayList<String>();
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
	private GridView suspectedad;
	private String s;
	private String n1;
	private String z = "";
	private String st;
	private String cou;
	private String adds;
	private String sv;
	private String z1;
	private String resposmsg="";
	DatabaseHandler db;
	private ArrayList<StateList> mSearchlist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.suspectedmain);

		iv = (ImageView) findViewById(R.id.button_refresh_suspected_main);
		suspectedad = findViewById(R.id.suspecteddetailmain);

		mSearchlist = new ArrayList<StateList>();

		db = new DatabaseHandler(getApplicationContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();

		dbi.Close();

		if (dbvalue()) {
			updatelist();
			// prepareListData();
		} else if (isnet()) {

			fetchdata();
		} else {
			showD("nonet");
		}

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
		suspectedad.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {

				/*String s = arrlist.get(position);
				s = s.substring(0, s.indexOf("\n"));*/
				Bundle dataBundle = new Bundle();
				dataBundle.putString("stnname", mSearchlist.get(position).getStatioName());
				dataBundle.putString("network", mSearchlist.get(position).getNetworkcode());
				Intent myIntent = new Intent();
				myIntent.setClass(getApplicationContext(), Suspected.class);
				myIntent.putExtras(dataBundle);
				startActivity(myIntent);

			}
		});
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

	protected boolean stationpresent() {
		// TODO Auto-generated method stub

		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM Suspected", null);

		int count = c.getCount();

		c.close();
		sql.close();
		//db.close();

		if (count == 0) {
			return false;
		} else {
			return true;
		}

	}

	private boolean dbvalue() {
		// TODO Auto-generated method stub
	//	DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql.rawQuery("SELECT *   FROM Suspected", null);

		System.out.println("----------  dbvalue screen cursor count -- "
				+ cursor.getCount());

		if (cursor != null && cursor.getCount() > 0) {

			// do your action
			// Fetch your data

			cursor.close();
			sql.close();
			//db1.close();
			return true;

		} else {

			cursor.close();
			sql.close();
			//db1.close();
			return false;
		}

	}

	private void updatelist() {

		/*System.out.println("====#$#$#$#$#$#$  in update list downtime");
		final ArrayList<String> searchResults = getDetail();

		suspectedad.setAdapter(new SuspectedAdaptMain(this, searchResults));*/

		mSearchlist.clear();
		//	DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql
				.rawQuery(
						"Select distinct NetworkCode from ConnectionStatusFiltermob Order by NetworkCode",
						null);
		// ,InstallationId
		if (c.getCount() > 0) {
			c.moveToFirst();
			do {
				int count = 0;
				String StationName = c.getString(c
						.getColumnIndex("NetworkCode"));
				Cursor c1 = sql.rawQuery("select distinct InstallationDesc from ConnectionStatusFiltermob where NetworkCode='"+StationName+"'",null);
				count = c1.getCount();


				StateList sitem = new StateList();
				sitem.SetNetworkCode(StationName);
				sitem.Setcount(count);
				mSearchlist.add(sitem);

				// }
			} while (c.moveToNext());

		}

		StationEnquiryAdptr adp = new StationEnquiryAdptr(SuspectedMain.this, mSearchlist,"SuspectStation");
		adp.notifyDataSetChanged();
		suspectedad.setAdapter(adp);


	}

	private ArrayList<SuspectedHelper> GetDetail() {
		ArrayList<SuspectedHelper> results = new ArrayList<SuspectedHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM Suspected", null);
		if (c.getCount() == 0) {
			SuspectedHelper sr = new SuspectedHelper();
			sr.setStationName("");
			sr.setsuscount("");
			// sr.setinstallationname("");
			//
			// sr.setdateFrom("");
			// sr.setdateTo("");
			//
			// sr.setStartEnd("");
			// sr.setRemarks("");

			results.add(sr);

			c.close();
			sql.close();
			//db.close();

			return results;
		} else {

			c.moveToFirst();

			int column = 0;
			do {

				SuspectedHelper sr = new SuspectedHelper();

				// column = c.getColumnIndex("UserName");
				String s = sr.setStationName(c.getString(c
						.getColumnIndex("StationName")));
				System.out.println("...............s value..........." + s);
				String n1 = findCount(s);
				System.out.println("...............n1 value..........." + n1);
				sr.setsuscount(n1);

				// String[] params = new String[1];
				// params[0] = s;
				// Cursor c2 = sql.rawQuery(
				// "SELECT * FROM ConnectionStatusUser where InstallationDesc=? ",
				// params);
				// int count = c2.getCount();
				//
				// String n=String.valueOf(count);
				// sr.setcsncount(n1);
				// sr.setEndTime(tym[0]);
				// //sr.setStartEnd(difftime);
				// sr.setRemarks(c.getString(c.getColumnIndex("Remarks")));
				results.add(sr);

			} while (c.moveToNext());

			c.close();
			sql.close();
			//db.close();
		}
		return results;

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
	// // //db.close();
	// // //db1.close();
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
	// //db.close();
	// //db1.close();
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
	private ArrayList<String> getDetail() {

		arrlist.clear();
		// ArrayList<SuspectedHelper> results = new
		// ArrayList<SuspectedHelper>();
	//	DatabaseHandler db = Handler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM Suspected ORDER BY Count DESC",
				null);
		if (c.getCount() == 0) {
			// SuspectedHelper sr = new SuspectedHelper();
			// sr.setcsId("");
			// sr.setStationName("");
			//

			arrlist.add("");

			c.close();
			sql.close();
			//db.close();

			return arrlist;
		} else {

			c.moveToFirst();

			int column = 0;
			do { // SuspectedHelper sr = new SuspectedHelper();

				// column = c.getColumnIndex("UserName");

				// sr.setStationName(c.getString(c.getColumnIndex("StationName")));
				st = c.getString(c.getColumnIndex("StationName"));
				cou = c.getString(c.getColumnIndex("Count"));
				adds = st + "\n" + cou;
				// n1= findCount(st);
				// z= st + " " + n1;

				if (arrlist.contains(adds)) {
					System.out
							.println("=@#@#  already present in list downtime");
				} else {

					// arrlist.add(c.getString(c.getColumnIndex("StationName")));
					// String s1=c.getString(c.getColumnIndex("StationName"));
					// System.out.println("...............s value..........."+s);
					arrlist.add(adds);
					// arrlist.add(c.getString(c.getColumnIndex("StationName")));
					// String [] z={s,n1};
					// arrlist.addAll(Arrays.asList(z));
					// System.out.println("...............n1 value..........."+n1);
				}
				// ;}
				// results.add(sr);

			} while (c.moveToNext());

			c.close();
			sql.close();
			//db.close();
		}
		return arrlist;

	}

	private String findCount(String co) {
		// TODO Auto-generated method stub
	//	DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = co;
		Cursor c2 = sql.rawQuery(
				"SELECT * FROM Suspected where StationName=? ", params);
		int count = c2.getCount();

		String n = String.valueOf(count);
		c2.close();
		sql.close();
		//db.close();
		return n;
	}

	// private String splitIp(String datedb) {
	// String retval="";
	// for (retval: datedb.split("(")){
	// System.out.println(retval);
	// }
	// String v3[]={retval};
	// return v3;
	// }

	private void fetchdata() {
		// TODO Auto-generated method stub

		pd = ProgressDialog.show(SuspectedMain.this,
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

		//depattask = new DownloadxmlsDataURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		depattask = new DownloadnetWork().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public class DownloadxmlsDataURL extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String xx = "";

			String url = "http://sta.vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetSuspectedStn_android?Mobile="
					+ mobno;

			url = url.replaceAll(" ", "%20");

			System.out.println("============ internet reg url " + url);

			try {
				System.out.println("-------  activity url --- " + url);
				responsemsg = ut.httpGet(url);

				System.out.println("-------------  xx vale-- " + responsemsg);

				//
				if (responsemsg.contains("<AdvertisementName>")) {
					sop = "valid";
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());
					System.out.println("------------- 1-- ");
					SQLiteDatabase sql = db.getWritableDatabase();
					System.out.println("------------- 2-- ");
				//	sql.execSQL("DROP TABLE IF EXISTS Suspected");
					System.out.println("------------- 3-- ");
				//	sql.execSQL(ut.getSuspected());
					sql.delete("Suspected",null,null);

					System.out.println("------------- 4-- ");
					System.out.println("------------- 5-- ");

					Cursor c = sql.rawQuery("SELECT *   FROM Suspected", null);
					System.out.println("------------- 6-- ");
					ContentValues values = new ContentValues();
					System.out.println("------------- 7-- ");
					NodeList nl = ut.getnode(responsemsg, "Table1");
					System.out.println("------------- 8-- ");
					String msg = "";
					System.out.println("------------- 9-- ");
					String columnName, columnValue;
					for (int i = 0; i < nl.getLength(); i++) {
						Element e = (Element) nl.item(i);
						System.out.println("------------- 10-- ");
						for (int j = 0; j < c.getColumnCount(); j++) {
							System.out.println("------------- 11-- ");
							columnName = c.getColumnName(j);
							columnValue = ut.getValue(e, columnName);

							System.out.println("-------------column name" + columnName);
							System.out.println("-------------column value" + columnValue);

							values.put(columnName, columnValue);

						}

						sql.insert("Suspected", null, values);
						System.out
								.println("---------------inserted into suspected status");

					}

					c.close();
					sql.close();
					//db.close();

				} else {
					sop = "invalid";
					System.out
							.println("--------- invalid for project list --- ");
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

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			try {
				pd.cancel();
				System.out.println("...............value of sop" + sop);
				if (sop.equals("valid")) {

					updatelist();

				} else {

					showD("invalid");
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

	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(SuspectedMain.this);
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
			txt.setText("No Refresh data Available. Please check internet connection");
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

	public class DownloadnetWork extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String Url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMasterMobile?Mobile="+mobno;
			Log.e("All Station", "Url=" + Url);

			try {
				resposmsg = ut.httpGet(Url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (resposmsg.contains("<InstalationId>")) {
				sop = "valid";
				//DatabaseHandler db = new DatabaseHandler(getBaseContext());
				SQLiteDatabase sql = db.getWritableDatabase();
				String columnName, columnValue;
				//sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusFiltermob");
				//sql.execSQL(ut.getConnectionStatusFiltermob());
				sql.delete("ConnectionStatusFiltermob",null,null);

				Cursor cur1 = sql.rawQuery(
						"SELECT * FROM ConnectionStatusFiltermob", null);
				cur1.getCount();
				ContentValues values2 = new ContentValues();
				NodeList nl2 = ut.getnode(resposmsg, "Table");

				Log.e("All Station Data ", "get length : " + nl2.getLength());
				for (int i = 0; i < nl2.getLength(); i++) {
					Log.e("All Station Data ", "length : " + nl2.getLength());
					Element e = (Element) nl2.item(i);
					for (int j = 0; j < cur1.getColumnCount(); j++) {
						columnName = cur1.getColumnName(j);
						columnValue = ut.getValue(e, columnName);
						Log.e("All Station Data ", "column Name : "
								+ columnName);
						Log.e("All Station Data ", "column value : "
								+ columnValue);

						values2.put(columnName, columnValue);

					}
					sql.insert("ConnectionStatusFiltermob", null, values2);
				}

				cur1.close();

			} else {
				sop = "invalid";
				System.out.println("--------- invalid for project list --- ");
			}
			return sop;

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			/*mRefresh.setVisibility(View.GONE);
			mprogress.setVisibility(View.VISIBLE);*/

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
						ut.showD(SuspectedMain.this, "invalid");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				/*mRefresh.setVisibility(View.VISIBLE);
				mprogress.setVisibility(View.GONE);*/
				pd.dismiss();

			} catch (Exception e) {
				e.printStackTrace();
				// dff = new SimpleDateFormat("HH:mm:ss");
				// Ldate = dff.format(new Date());

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

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = getApplicationContext()
				.getSharedPreferences("SCROLLSUS", Context.MODE_PRIVATE);
		int scroll = preferences.getInt("ScrollValueSus", 0);
		System.out
				.println(".............value of scroll at resume>>>>>>>>>>>>>>>>>>>"
						+ scroll);
		// connectionstatus.scrollTo(0, scroll);
		suspectedad.smoothScrollToPosition(scroll);
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SharedPreferences preferences = getApplicationContext()
				.getSharedPreferences("SCROLLSUS", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		int scroll = suspectedad.getFirstVisiblePosition();
		// int scrollx = connectionstatus.getScrollX();
		System.out.println(".............value of scroll>>>>>>>>>>>>>>>>>>>"
				+ scroll);
		editor.putInt("ScrollValueSus", scroll);
		editor.commit();
		// finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		// dataBundle.putString("ActivityName", ActivityName);
		/*Intent i = new Intent(getBaseContext(), SelectMenu.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// // i.putExtras(dataBundle);
		getBaseContext().startActivity(i);*/
		finish();

	}

}

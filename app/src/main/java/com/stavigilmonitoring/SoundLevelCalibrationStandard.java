package com.stavigilmonitoring;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.ExpandableListAdapter;
import com.beanclasses.SounsLevelCalibrationstdBean;
import com.database.DBInterface;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;

public class SoundLevelCalibrationStandard extends Activity {
	TextView mTextView;
	String mMobNo;
	String mInstallation;
	String mResponsemsg = "K";
	String sop = "Invalid";
	ListView mListView;
	AsyncTask mAsyncTask;
	static SimpleDateFormat dff;
	static String Ldate;
	ImageView iv,btnadd;
	
	List<String> listDataHeader;
	HashMap<String, List<SounsLevelCalibrationstdBean>> listDataChild;
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.soundlevelcalibrationdate);

		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_calibration_std);
		Intent intent = getIntent();
		final String StaionName = intent.getStringExtra("Stationname");
		mInstallation = intent.getStringExtra("InstallationID");//InstallationID//InstallationID
		mTextView = (TextView) findViewById(com.stavigilmonitoring.R.id.calibrationcityname);
		mTextView.setText(StaionName);
		expListView = (ExpandableListView) findViewById(com.stavigilmonitoring.R.id.lvExp);
		btnadd = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_alert_add);
		btnadd.setImageResource(com.stavigilmonitoring.R.drawable.work_assign);
		btnadd.setVisibility(View.VISIBLE);
		// mTextView.append(Installation);
		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mMobNo = dbi.GetPhno();
		dbi.Close();
		// listDataHeader = new ArrayList<String>();
	
		// listDataChild = new HashMap<String,
		// List<SounsLevelCalibrationstdBean>>();
		// mTextView.append(mMobNo);
		// mAsyncTask = new DownLoadXML_Calibration().execute();

		if (dbvalue()) {
			updatelist();

		} else if (isnet()) {
			mAsyncTask = new DownLoadXML_Calibration().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		} else {
			showD("nonet");
		}

		expListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				expListView.expandGroup(groupPosition);
				return true;
			}
		});

		// Listview Group expanded listener
		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				/*
				 * Toast.makeText(getApplicationContext(),
				 * listDataHeader.get(groupPosition) + " Expanded",
				 * Toast.LENGTH_SHORT).show();
				 */
			}
		});

		// Listview Group collasped listener
		expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				/*
				 * Toast.makeText(getApplicationContext(),
				 * listDataHeader.get(groupPosition) + " Collapsed",
				 * Toast.LENGTH_SHORT).show();
				 */

			}
		});

		// Listview on child click listener
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				/*
				 * Toast.makeText( getApplicationContext(),
				 * listDataHeader.get(groupPosition) + " : " +
				 * listDataChild.get( listDataHeader.get(groupPosition)).get(
				 * childPosition), Toast.LENGTH_SHORT) .show();
				 */
				return false;
			}
		});

		btnadd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),WorkAssign_AssignActivity.class);
				intent.putExtra("Activity", "SoundLevelStateWise");
				intent.putExtra("Type", "");
				startActivity(intent);
				finish();
			}
		});

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isnet()) {

					mAsyncTask = new DownLoadXML_Calibration().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

				} else {
					showD("nonet");
				}
			}
		});
	}

	/*
	 * mListView.setOnItemClickListener(new OnItemClickListener() {
	 * 
	 * @Override public void onItemClick(AdapterView<?> parent, View view, int
	 * position, long id) { // TODO Auto-generated method stub
	 * 
	 * Intent intent = new Intent(getApplicationContext(),
	 * SoundLevelCalibrationStdDetail.class);
	 * searchResults.get(position).getCallibrationDate();
	 * intent.putExtra("CalibrationDate", searchResults.get(position)
	 * .getCallibrationDate()); intent.putExtra("StationName", StaionName);
	 * intent.putExtra("mInstallation", mInstallation); // intent.p
	 * 
	 * startActivity(intent); } });
	 */

	private boolean isnet() {
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
		try {

			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			// Cursor cursor = sql.rawQuery("SELECT *   FROM AllStation", null);
			Cursor c = sql.rawQuery(
					"SELECT * FROM CalibrationStandard Where InstallationId='"
							+ mInstallation + "'", null);
			if (c != null && c.getCount() > 0) {
				if (c.getColumnIndex("AudioMonitorDetailsID") < 0) {// AudioMonitorDetailsID
					/*c.close();
					sql.close();
					db.close();*/
					return false;
				} else {
					/*c.close();
					sql.close();
					db.close();*/
					return true;
				}
			} else {
				/*c.close();
				sql.close();
				db.close();*/
				return false;
			}
		} catch (Exception e) {
			dff = new SimpleDateFormat("HH:mm:ss");
			Ldate = dff.format(new Date());
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
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

	private String[] splitfromto(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 9);
		System.out.println("---value of k..." + k);
		String m = k.replace("T", " From ");
		System.out.println("---value of m..." + m);
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

		System.out.println("--------------date-----" + finalDate);

		fromtimetw = m.substring(15, tf.length() - 8);

		System.out.println("time------" + fromtimetw);
		String[] vto = { finalDate };
		String[] v2 = { fromtimetw };

		return vto;
	}

	private void updatelist() {
		String date;
		String AudioMonitorDetailtime, Callibrationstd, Callibrationvolume, Callibrationsysvol, CallibrationVolume;
		String SystemVolume, InstallationId, InstallationDesc, CallibrationDate = null;
		ArrayList<List<SounsLevelCalibrationstdBean>> dataclect =new ArrayList<List<SounsLevelCalibrationstdBean>>();
		dataclect.clear();
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<SounsLevelCalibrationstdBean>>();
		//listDataChild = new HashMap<String, List<SounsLevelCalibrationstdBean>>();
		// listDataHeader.clear();
		// listDataChild.clear();
		// searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		com.stavigilmonitoring.utility mUt = new com.stavigilmonitoring.utility();
		
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql
				.rawQuery(
						"SELECT DISTINCT CallibrationDate FROM CalibrationStandard Where InstallationId="
								+ mInstallation, null);

		Log.e("Pending n/w count", "" + c.getCount());
		
		if (c.getCount() > 0) {
			c.moveToFirst();
			do {

				CallibrationDate = c.getString(c
						.getColumnIndex("CallibrationDate"));
				Cursor c1 = sql.rawQuery(
						"SELECT CTIME,Standard,CallibrationVolume,SystemVolume FROM CalibrationStandard where CallibrationDate='"
								+ CallibrationDate + "' Order by CTIME DESC", null);
				List<SounsLevelCalibrationstdBean> searchResults= new ArrayList<SounsLevelCalibrationstdBean>();
				listDataHeader.add(CallibrationDate);
				if (c1.getCount() > 0) {
					c1.moveToFirst();
					do {
					   
						Log.e("Pending n/w count", "" + c1.getCount());
						int Count = c1.getCount();
						int asd = c1.getColumnIndex("CTIME");
						AudioMonitorDetailtime = c1.getString(0);
						Callibrationstd = c1.getString(c1
								.getColumnIndex("Standard"));

						long intval = Integer.parseInt(Callibrationstd);
						long intvalue = intval / 100000;
						String Calstd = Integer.toString((int) intvalue);

						Callibrationvolume = c1.getString(c1
								.getColumnIndex("CallibrationVolume"));
						Callibrationsysvol = c1.getString(c1
								.getColumnIndex("SystemVolume"));
						SounsLevelCalibrationstdBean sc = new SounsLevelCalibrationstdBean();

						sc.setCTIME(AudioMonitorDetailtime);
						sc.setStandard(Calstd);
						sc.setCallibrationVolume(Callibrationvolume);
						sc.setSystemVolume(Callibrationsysvol);
						searchResults.add(sc);
						
						date = CallibrationDate;

					} while (c1.moveToNext());
					listDataChild.put(date, searchResults);
					dataclect.add(searchResults);

					c1.close();
				}

			} while (c.moveToNext());

			db.close();
			sql.close();
			c.close();
			
			/*for(int i =0;i<listDataHeader.size();i++){
				
				listDataChild.put(listDataHeader.get(i), dataclect.get(i));
			}*/

			listAdapter = new ExpandableListAdapter(this, listDataHeader,listDataChild);

			// setting list adapter
			expListView.setAdapter(listAdapter);
		}

	}

	public class DownLoadXML_Calibration extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String xx = "";

			String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetStationAudioMonitorDetails?Mobile="
					+ mMobNo + "&InstallationId=" + mInstallation;
			url = url.replaceAll(" ", "%20");

		//	DatabaseHandler mDb = new DatabaseHandler(getApplicationContext());
			com.stavigilmonitoring.utility mUt = new com.stavigilmonitoring.utility();

			System.out.println("============ internet reg url " + url);

			try {
				System.out.println("-------  activity url --- " + url);
				mResponsemsg = mUt.httpGet(url);

				Log.e("csn status", "resmsg : " + mResponsemsg);

				if (mResponsemsg.contains("<Table>")) {
					sop = "valid";
					String columnName, columnValue;
					/*
					 * DatabaseHandler db = new
					 * DatabaseHandler(getBaseContext()); SQLiteDatabase sql =
					 * db.getWritableDatabase();
					 */

					SQLiteDatabase sql = db.getWritableDatabase();
					//sql.execSQL("DROP TABLE IF EXISTS CalibrationStandard");
					//sql.execSQL(mUt.getSoundLevelCalibrationStandard());
					sql.delete("CalibrationStandard",null,null);

					Cursor cur = sql.rawQuery(
							"SELECT * FROM CalibrationStandard", null);
					ContentValues values = new ContentValues();
					NodeList nl1 = mUt.getnode(mResponsemsg, "Table");
					// String msg = "";
					// String columnName, columnValue;
					Log.e("All Station data...",
							" fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							// String CalDate1 ="Null";
							columnName = cur.getColumnName(j);
							if (mUt.getValue(e, columnName) == null
									|| mUt.getValue(e, columnName) == "") {
								columnValue = "Null";
							} else {
								columnValue = mUt.getValue(e, columnName);
							}
							// columnValue = mUt.getValue(e, columnName);

							System.out.println("-------------column name"
									+ columnName);
							System.out.println("-------------column value"
									+ columnValue);
							// CallibrationDate=Oct 7 2015 7:05AM
							if (columnName.equalsIgnoreCase("CallibrationDate")) {

								String CalDate1 = null;
								try {
									// String CalDate1 ="Null";
									columnValue = mUt.getValue(e, columnName);
									String[] CalDate = splitfromto(columnValue);
									CalDate1 = CalDate[0];
								} catch (Exception ex) {
									ex.printStackTrace();
								}

								if (!(CalDate1 == null)) {

									values.put(columnName, CalDate1);

								} else {
									String s = "NO date Available";
								}

							} else {
								values.put(columnName, columnValue);
							}

							// values.put(columnName, columnValue);
							Log.d("test", "values :" + values);
						}

						long a = sql
								.insert("CalibrationStandard", null, values);
						Log.d("test", "SoundLevel_new " + values.size());
					}

					//close calls

				}
			} catch (Exception e) {
				e.printStackTrace();

			}
			return sop;

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.pogrssCal))
					.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				if (sop == "valid") {
					updatelist();
					// String zString = "0";
				} else {
					showD("empty");
				}
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(com.stavigilmonitoring.R.id.pogrssCal))
						.setVisibility(View.GONE);

			} catch (NullPointerException e) {
				e.printStackTrace();
				dff = new SimpleDateFormat("HH:mm:ss");
				Ldate = dff.format(new Date());
				com.stavigilmonitoring.utility mUt = new com.stavigilmonitoring.utility();
				StackTraceElement l = new Exception().getStackTrace()[0];
				System.out.println(l.getClassName() + "/" + l.getMethodName()
						+ ":" + l.getLineNumber());

				if (!mUt.checkErrLogFile()) {

					mUt.ErrLogFile();
				}
				if (mUt.checkErrLogFile()) {
					mUt.addErrLog(l.getClassName() + "/" + l.getMethodName()
							+ ":" + l.getLineNumber() + "	" + e.getMessage()
							+ " " + Ldate);
				}

			} catch (Exception e) {
				e.printStackTrace();
				dff = new SimpleDateFormat("HH:mm:ss");
				Ldate = dff.format(new Date());
				com.stavigilmonitoring.utility mUt = new utility();
				StackTraceElement l = new Exception().getStackTrace()[0];
				System.out.println(l.getClassName() + "/" + l.getMethodName()
						+ ":" + l.getLineNumber());

				if (!mUt.checkErrLogFile()) {

					mUt.ErrLogFile();
				}
				if (mUt.checkErrLogFile()) {
					mUt.addErrLog(l.getClassName() + "/" + l.getMethodName()
							+ ":" + l.getLineNumber() + "	" + e.getMessage()
							+ " " + Ldate);
				}

			}

		}
	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(SoundLevelCalibrationStandard.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);

		TextView txt = (TextView) myDialog
				.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);
		if (string.equals("empty")) {
			myDialog.setTitle("Error...");
			txt.setText("No Data Available...");
		} else if (string.equals("nonet")) {
			myDialog.setTitle("Error...");
			txt.setText("No Internet Connection Found.Please Activate internet Connectin on Device..");
		} else if (string.equals("invalid")) {
			myDialog.setTitle(" ");
			txt.setText("No Data Available...");
		}

		Button btn = (Button) myDialog
				.findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();
				// finish();

			}
		});

		myDialog.show();

	}

	public class Datecal {
		String date;

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

	}
}

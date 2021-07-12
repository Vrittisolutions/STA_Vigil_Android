package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.TvStatusAdpt;
import com.beanclasses.TvStatusStateBean;
import com.database.DBInterface;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class TvStatusStateWise extends Activity {

	List<TvStatusStateBean> searchResults;
	ImageView iv;
	String sop, responsemsg, mobno;
	static DownloadxmlsDataURL_new asyncfetch_csnstate;
	GridView lstcsn;
	static SimpleDateFormat dff;
	static String Ldate;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tvstatussatewise);

		searchResults = new ArrayList<TvStatusStateBean>();
		lstcsn = findViewById(R.id.lstcsn);
		iv = (ImageView) findViewById(R.id.button_refresh_tvstatus_main);

		db = new DatabaseHandler(getBaseContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();

		if (asyncfetch_csnstate != null
				&& asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}
		Log.e("Downtime...", " dbval : " + dbvalue());

		if (dbSubnet()) {
			if (dbvalue()) {
				updatelist();
			} else if (ut.isnet(getApplicationContext())) {
				fetchdata();
			} else {
				try{
					ut.showD(TvStatusStateWise.this, "nonet");
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		} else if (ut.isnet(TvStatusStateWise.this)) {
			new DownloadSubStatetv().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			try{
				ut.showD(TvStatusStateWise.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		lstcsn.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Boolean value = subnetworkcheck(searchResults.get(position)
						.getNetworkcode());
				if (value) {
					Intent i = new Intent(TvStatusStateWise.this, TvStatusStateFilter.class);
					i.putExtra("Type", searchResults.get(position).getNetworkcode());
					// i.putExtra("Count", searchResults.get(position).ge);

					startActivity(i);
					finish();

				} else {
					Intent i = new Intent(TvStatusStateWise.this,
							TvStatusMain.class);
					i.putExtra("Type", searchResults.get(position)
							.getNetworkcode());
					i.putExtra("SubType", searchResults.get(position)
							.getNetworkcode());
					startActivity(i);

				}
			}
		});

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ut.isnet(TvStatusStateWise.this)) {
				//	DatabaseHandler db = new DatabaseHandler(getBaseContext());
					SQLiteDatabase sql = db.getWritableDatabase();
					Cursor c = sql.rawQuery("DELETE FROM TvStatus", null);
					int ct = c.getCount();
					c.close();
					//db.close();
					sql.close();
					new DownloadSubStatetv().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					try{
						ut.showD(TvStatusStateWise.this, "nonet");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			}
		});
	}

	private boolean subnetworkcheck(String type) {
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		ArrayList<String> mSubnetworklist = new ArrayList<String>();
		try {

			String qrery = "SELECT DISTINCT ConnectionStatusFilter.SubNetworkCode FROM ConnectionStatusFilter INNER JOIN TvStatus ON ConnectionStatusFilter.InstalationId=TvStatus.InstallationId WHERE TvStatus.Type='"
					+ type + "'";

			Cursor c = sql.rawQuery(qrery, null);
			c.getCount();

			if (c.getCount() == 0) {
				c.close();
				//db1.close();
				sql.close();
				return false;

			} else if (c.getCount() > 0) {

				c.moveToFirst();
				do {

					int stncnt = 0;
					String Type = c.getString(0);

					mSubnetworklist.add(Type);

				} while (c.moveToNext());
				c.close();
				//db1.close();
				sql.close();

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;

		}

		if (mSubnetworklist.contains(type) && mSubnetworklist.size() > 1) {
			return true;
		} else {
			// return false;

			if (mSubnetworklist.contains(type)) {
				return false;
			} else {
				return true;
			}

		}

	}

	private Boolean dbSubnet() {

		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery(
					"SELECT * FROM ConnectionStatusFilter", null);// SoundLevel_new

			System.out.println("----------  dbSubnet screen cursor count -- "
					+ cursor.getCount());

			if (cursor != null && cursor.getCount() > 0) {

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
		} catch (Exception e) {
			dff = new SimpleDateFormat("HH:mm:ss");
			Ldate = dff.format(new Date());

			StackTraceElement l = new Exception().getStackTrace()[0];
			System.out.println(l.getClassName() + "/" + l.getMethodName() + ":"
					+ l.getLineNumber());
			// ut = new utility();
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

	private boolean dbvalue() {
		try {

		//	DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			// Cursor cursor = sql.rawQuery("SELECT *   FROM AllStation", null);
			Cursor c = sql.rawQuery("SELECT * FROM TvStatus", null);
			if (c != null && c.getCount() > 0) {
				if (c.getColumnIndex("TVStatus") < 0) {
					c.close();
					sql.close();
					//db1.close();
					return false;
				} else {
					c.close();
					sql.close();
					//db1.close();
					return true;
				}
			} else {
				c.close();
				sql.close();
				//db1.close();
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

	private void fetchdata() {
		// new DownloadxmlsDataURL_new().execute();
		asyncfetch_csnstate = null;
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
			String url = "http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetAllStation_Android";

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = com.stavigilmonitoring.utility.httpGet(url);
				Log.e("csn status", "resmsg : " + responsemsg);

				if (responsemsg.contains("<NetworkCode>")) {
					sop = "valid";
					String columnName, columnValue;
					//sql.execSQL("DROP TABLE IF EXISTS AllStation");
					//sql.execSQL(ut.getAllStation());
					sql.delete("AllStation",null,null);

					Cursor cur = sql.rawQuery("SELECT * FROM AllStation", null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table1");
					// String msg = "";
					// String columnName, columnValue;
					Log.e("All Station data...",
							" fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							values1.put(columnName, columnValue);

							Log.e("DownloadxmlsDataU.",
									" count i: " + i + "  j:" + j);
						}
						sql.insert("AllStation", null, values1);
					}

					cur.close();

					url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetTVStatus_Android_new?Mobile="
							+ mobno;

					Log.e("csn status", "url : " + url);
					url = url.replaceAll(" ", "%20");
					try {
						responsemsg = ut.httpGet(url);
						Log.e("csn status", "resmsg : " + responsemsg);
					//	sql.execSQL("DROP TABLE IF EXISTS TvStatus");
					//	sql.execSQL(ut.getTvStatus());
						sql.delete("TvStatus",null,null);

						cur = sql.rawQuery("SELECT *   FROM TvStatus", null);
						if (responsemsg.contains("<A>")) {
							values1 = new ContentValues();
							nl1 = ut.getnode(responsemsg, "Table1");
							Log.e("sts main...",
									" fetch data : " + nl1.getLength());
							for (int i = 0; i < nl1.getLength(); i++) {
								Element e = (Element) nl1.item(i);
								for (int j = 0; j < cur.getColumnCount(); j++) {
									columnName = cur.getColumnName(j);

									String ncolumnname = "";
									if (columnName
											.equalsIgnoreCase("InstallationId"))
										ncolumnname = "A";
									else if (columnName
											.equalsIgnoreCase("TVStatusReason"))
										ncolumnname = "G";
									else if (columnName
											.equalsIgnoreCase("TVStatus"))
										ncolumnname = "J";
									else if (columnName
											.equalsIgnoreCase("Type"))
										ncolumnname = "P";
									else
										ncolumnname = columnName;
									columnValue = ut.getValue(e, ncolumnname);
									values1.put(columnName, columnValue);

									Log.e("DownloadxmlsDa",
											" count i: " + i + "  j:" + j);
								}
								sql.insert("TvStatus", null, values1);
							}

							cur.close();
							sql.close();
							//db.close();

						} else {
							System.out
									.println("--------- invalid for project list --- ");
						}

					}

					catch (NullPointerException e) {
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
							ut.addErrLog(l.getClassName() + "/"
									+ l.getMethodName() + ":"
									+ l.getLineNumber() + "	" + e.getMessage()
									+ " " + Ldate);
						}

					} catch (IOException e) {
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
							ut.addErrLog(l.getClassName() + "/"
									+ l.getMethodName() + ":"
									+ l.getLineNumber() + "	" + e.getMessage()
									+ " " + Ldate);
						}

					}

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
					try{
						ut.showD(TvStatusStateWise.this, "nonet");
					}catch (Exception e){
						e.printStackTrace();
					}
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

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

	}

	public void onBackPressed() {
		super.onBackPressed();

		int scount = 0;
		for (int i = 0; i < searchResults.size(); i++)
			scount = scount + searchResults.get(i).Getcount();

		try {
			SharedPreferences pref = getApplicationContext()
					.getSharedPreferences("PrefTVStatus", Context.MODE_PRIVATE); // 0
																					// -
																					// for
																					// private
																					// mode
			Editor editor = pref.edit();
			editor.putString("TVStatus", scount + "");
			editor.commit();
		} catch (Exception edf) {
		}

		/*Intent i = new Intent(getBaseContext(), SelectMenu.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);*/
		finish();

		/*
		 * Intent i = new Intent(getBaseContext(), SelectMenu.class);
		 * i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * getBaseContext().startActivity(i); finish();
		 */
	}

	private void updatelist() {
		searchResults.clear();
		// arrlist.clear();
		int cnt = 0;

		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c1 = sql.rawQuery(
				"select distinct Type from TvStatus ORDER BY Type", null);
		int Scnt = 0;
		c1.moveToFirst();
		do {
			Log.e("Table count", "" + c1.getCount());
			String Type = c1.getString(c1.getColumnIndex("Type"));

			Cursor c2 = sql.rawQuery("select * from AllStation", null);
			c2.getCount();

			Cursor c = sql
					.rawQuery(
							"Select distinct c1.InstallationId,c1.TVStatus,c1.flg from TvStatus c1  inner join AllStation c2  on c1.InstallationId=c2.InstallationId where c1.Type='"
									+ Type + "'", null);
			c.getCount();
		
			if (c.getCount() == 0) {
c.close();
			} else {
				TvStatusStateBean sitem = new TvStatusStateBean();
				c.moveToFirst();
				int column = 0;
				do {

					int i = 0;
					int s = c.getInt(c.getColumnIndex("flg"));
					String s1 = c.getString(c.getColumnIndex("TVStatus"));

					for (char d : s1.toCharArray()) {
						if (d == '0') {
							cnt++;
						}
					}

					if (!Type.trim().equalsIgnoreCase("") && s != 0) {// if
						// (!Type.trim().equalsIgnoreCase("")
						// && cnt != 8) {

						column++;
						sitem.Setcount(cnt);
						int a = sitem.Getcount();
						if (a != 0) {
							Scnt++;
						}
						sitem.SetNetworkCode(Type);
						sitem.SetScount(Scnt);
					}

					cnt = 0;
				
				} while (c.moveToNext());

				if (column != 0) {
					sitem.SettotalStation(column);
					searchResults.add(sitem);
				}
				Log.e("TV count", "" + column);
				Scnt = 0;
			}
			
		} while (c1.moveToNext());
		c1.close();

		sql.close();
		//db.close();
		lstcsn.setAdapter(new TvStatusAdpt(TvStatusStateWise.this, searchResults));

	}

	public class DownloadSubStatetv extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {

			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String columnName, columnValue;
			String urlnet = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMaster";
			urlnet = urlnet.replaceAll(" ", "%20");
			Log.e("installation for Subnet", "6th" + urlnet);

			try {
				responsemsg = ut.httpGet(urlnet);
				NodeList NL = ut.getnode(responsemsg, "Table");
				Log.e("SubnetCount", "len :" + NL.getLength());

				if (responsemsg.contains("<Table>")) {
					sop = "valid";
					//sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusFilter");
					//sql.execSQL(ut.getConnectionStatusFilter());
					sql.delete("ConnectionStatusFilter",null,null);

					Cursor cur1 = sql.rawQuery(
							"SELECT * FROM ConnectionStatusFilter", null);
					ContentValues values2 = new ContentValues();

					Log.e("ConnectionFilter ",
							" fetch data : " + NL.getLength());
					for (int i = 0; i < NL.getLength(); i++) {
						Element e = (Element) NL.item(i);
						for (int j = 0; j < cur1.getColumnCount(); j++) {
							columnName = cur1.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							Log.e("ConnectionFi.", " fetch data : "
									+ columnValue);
							values2.put(columnName, columnValue);
							// SubnetString = "Valid";

						}
						long ad = sql.insert("ConnectionStatusFilter", null,
								values2);
					}

					cur1.close();
					sql.close();
					//db.close();

				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();

				sql.close();
				//db.close();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (dbvalue()) {
				updatelist();
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.GONE);
			} else if (ut.isnet(getApplicationContext())) {
				fetchdata();
			} else {
				try{
					ut.showD(TvStatusStateWise.this, "nonet");
					iv.setVisibility(View.VISIBLE);
					((ProgressBar) findViewById(R.id.progressBar1))
							.setVisibility(View.GONE);
				}catch (Exception e){
					e.printStackTrace();
				}
			}

		}

	}
}

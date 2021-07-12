package com.stavigilmonitoring;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adapters.ReportingPersonsGPSLocationAdapter;
import com.beanclasses.GPSLocationTimeBean;

public class SupporterGpsLocation extends Activity {

	String mobno, link;
	// DatabaseCreateTables databaseTables = new DatabaseCreateTables();
	String sop = "no";
	SimpleDateFormat dff;
	ImageView iv;
	
	private String supporterName,supporterMobno;
	ProgressBar bar;
	ListView gpsdetail;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	private Calendar cal_L, cal_H;
	Date currentLocalTime;

	private int mPpageno = 0;
	private DateFormat dateFormat;
	private Date date;
	String Date_L;
	String Date_H;
	ArrayList<GPSLocationTimeBean> userdatagps;
	private String mIfDate;
	private String Date_CheckString = null;
	private Date Date_CheckDate;
	private TextView mheader;
	private Button load;
	private String mStartDate, mEndDate, mindATE;
	Date d_e;
	Date d_s;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.supportergpslocation);
		userdatagps = new ArrayList<GPSLocationTimeBean>();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_gps_main);
		bar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar_gps);
		gpsdetail = (ListView) findViewById(com.stavigilmonitoring.R.id.gpsdetail);
		load = (Button) findViewById(com.stavigilmonitoring.R.id.txtload_earlierdata);
		mheader = (TextView) findViewById(com.stavigilmonitoring.R.id.onactivitynamereassign);
		Intent i = getIntent();
		supporterName = i.getStringExtra("PersonName");//PersonNumber
		supporterMobno = i.getStringExtra("PersonNumber");
	    mheader.append("-"+supporterName);

		db = new DatabaseHandler(getApplicationContext());
		DateSetting();
		if (dbvalue()) {
			
			updaterefreshlist();
		//	new Downloadxml_FirstWithRefresh().execute();
			
		} else {

			new Downloadxml_FirstWithRefresh().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		}
		
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DateSetting();
				new Downloadxml_FirstWithRefresh().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		});
		
		load.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new DownloadxmlsURL_Load().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		});

	}

	private void DateSetting() {
		// TODO Auto-generated method stub

		cal_L = Calendar.getInstance();
		Date today = cal_L.getTime();
		cal_H = Calendar.getInstance();
		Date toadayH = cal_H.getTime();
		cal_H.add(Calendar.DATE, 1);
		Date nextday = cal_H.getTime();

		Date_L = dateFormat.format(today);
		Date_H = dateFormat.format(nextday);

	}

	
private void updatelist_Load() {
		userdatagps.clear();
		//DatabaseHandler sql = new DatabaseHandler(getApplicationContext());
		SQLiteDatabase sqldb = db.getWritableDatabase();
		ArrayList<String> ls = new ArrayList<String>();
		Cursor c = sqldb.rawQuery("SELECT * FROM GPSrecords WHERE MobileNo='"
				+ supporterMobno + "'  ORDER BY AddedDt DESC", null);
		Log.e("Count", c.getCount() + "");
		Cursor c1 = sqldb.rawQuery(
				"SELECT MIN(AddedDt) FROM GPSrecords WHERE MobileNo='"
						+ supporterMobno + "'", null);
		// mindATE = c1.getString(0);
		Log.e("MINDATE", mindATE);
		if (c1.getCount() == 0) {

			c1.close();
		} else {
			c1.moveToFirst();
			do {
				// ls.add(networkodelist.setNetworkCode(cursor.getString(0)));
				ls.add(c1.getString(0));

			} while (c1.moveToNext());
			c1.close();
			mindATE = ls.get(0);
		}

		if (c.getCount() == 0) {
			c.close();
		}

		else {
			c.moveToFirst();
			do {

				String schTime = (c.getString(c.getColumnIndex("AddedDt")));

				userdatagps.add(new GPSLocationTimeBean(c.getString(0), c
						.getString(1), c.getString(2), c.getString(3), c
						.getString(4), c.getString(5), c.getString(6)));

			} while (c.moveToNext());
		}
		c.close();

		int currentPosition = gpsdetail.getFirstVisiblePosition();
		gpsdetail.setAdapter(new ReportingPersonsGPSLocationAdapter(this,
				userdatagps));

		gpsdetail.setSelectionFromTop(currentPosition + 1, 0);

	}

	private void updaterefreshlist() {
		// TODO Auto-generated method stub
		userdatagps.clear();
		//DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
		SQLiteDatabase sqldb = db.getWritableDatabase();
		ArrayList<String> ls = new ArrayList<String>();
		Cursor c = sqldb.rawQuery("SELECT * FROM GPSrecords WHERE MobileNo='"
				+ supporterMobno + "' ORDER BY AddedDt DESC ", null);
		/*
		 * Cursor c2 = db.rawQuery(
		 * "SELECT * FROM GPSrecords WHERE MobileNo='8390199115' ORDER BY AddedDt DESC "
		 * , null);
		 */
		Log.e("Count", c.getCount() + "");
		Cursor c1 = sqldb.rawQuery(
				"SELECT MIN(AddedDt) FROM GPSrecords WHERE MobileNo='"
						+ supporterMobno + "'", null);
		/*
		 * Cursor c3 = db.rawQuery(
		 * "SELECT MIN(AddedDt) FROM GPSrecords WHERE MobileNo='8390199115'",
		 * null);
		 */
		Log.e("Count", c1.getCount() + "");

		if (c1.getCount() == 0) {

			c1.close();
		} else {
			c1.moveToFirst();
			do {
				// ls.add(networkodelist.setNetworkCode(cursor.getString(0)));
				ls.add(c1.getString(0));

			} while (c1.moveToNext());
			c1.close();
			mindATE = ls.get(0);
		}

		if (c.moveToFirst()) {
			do {

				String schTime = (c.getString(c.getColumnIndex("AddedDt")));

				userdatagps.add(new GPSLocationTimeBean(c.getString(0), c
						.getString(1), c.getString(2), c.getString(3), c
						.getString(4), c.getString(5), c.getString(6)));

			} while (c.moveToNext());
		}
		c.close();
		gpsdetail.setAdapter(new ReportingPersonsGPSLocationAdapter(SupporterGpsLocation.this, userdatagps));
	}

	public class DownloadxmlsURL_Load extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			bar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {

			int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
			String s = mindATE;// 05-17-2016 10:22:04
			if (s == null || s.equalsIgnoreCase(" ")) {
				sop = "over";
			} else {
				SimpleDateFormat nextdt = new SimpleDateFormat(
						"MM-dd-yyyy hh:mm:ss");
				Date min;
				try {
					min = nextdt.parse(s);
					SimpleDateFormat fom = new SimpleDateFormat("yyyy-MM-dd");
					Date_H = fom.format(min);
					;
					Date_L = fom.format(min.getTime() - MILLIS_IN_DAY);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					String url;
					// LDate=2016-05-20&Hdate=2016-05-21
					url ="http://vritti.ekatm.co.in/VWB/webservice/ActivityWebservice.asmx/getGpscordinates?Mobileno="
							+ supporterMobno + "&LDate=" + Date_L + "&Hdate="
							+ Date_H;

					String res = ut.httpGet(url);

					if (checkRecord(res)) {
						mPpageno = mPpageno + 1;
						sop = "valid";
						NodeList nl = ut.getnode(res, "Table");
						Log.e("nodelist", "nl.getLength() : " + nl.getLength());
						for (int i = 0; i < nl.getLength(); i++) {
							Element e = (Element) nl.item(i);

							String location = ut.getValue(e,
									"locationName");
							String LocationName = null;
							if (location.equalsIgnoreCase("Location Not Found")
									|| location.equalsIgnoreCase("null")) {
								String lat = ut.getValue(e, "latitude");
								Double Latitud = Double.valueOf(lat)
										.doubleValue();
								String longt = ut.getValue(e,
										"longitude");
								Double longitude = Double.valueOf(longt)
										.doubleValue();
								String LocationName2;
								try {
									Geocoder geocoder = new Geocoder(
											SupporterGpsLocation.this,
											Locale.getDefault());
									List<Address> addressList = geocoder
											.getFromLocation(Latitud,
													longitude, 1);
									if (addressList != null
											&& addressList.size() > 0) {
										Address address = addressList.get(0);
										StringBuilder sb = new StringBuilder();
										for (int v = 0; v < address
												.getMaxAddressLineIndex(); v++) {
											sb.append(address.getAddressLine(v));

										}

										LocationName2 = sb.toString();

										adduserdatagps(new GPSLocationTimeBean(
												ut.getValue(e, "GPSID"),
												ut.getValue(e,
														"MobileNo"),
														ut.getValue(e,
														"latitude"),
														ut.getValue(e,
														"longitude"),
												LocationName2,
												ut
														.getValue(e, "AddedDt"),
														ut.getValue(e, "num")));

									}

								} catch (Exception err) {
									err.printStackTrace();
									LocationName2 = " ";
								}

							} else {
								Log.e("update master........",
										"users : name: "
												+ ut.getValue(e,
														"username")
												+ "  psd : "
												+ ut.getValue(e,
														"userpass"));
								adduserdatagps(new GPSLocationTimeBean(
										ut.getValue(e, "GPSID"),
										ut.getValue(e, "MobileNo"),
										ut.getValue(e, "latitude"),
										ut.getValue(e, "longitude"),
										ut.getValue(e, "locationName"),
										ut.getValue(e, "AddedDt"),
										ut.getValue(e, "num")));
							}
						}
					} else {
						sop = "over";
					}
				} catch (Exception e) {
					sop = "invalid";

					Log.e("error ", " msg : " + e.getMessage());
				}

			}

			// Date_L = dateFormat.format(today);

			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			iv.setVisibility(View.VISIBLE);
			bar.setVisibility(View.INVISIBLE);
			if (sop.equals("valid")) {

				// String Date_L = result[0];
				// String Date_H = result[1];
				updatelist_Load();
				// updaterefreshlist();
			} else if (sop.equals("over")) {
			/*	ut.showCustomMessageDialog(
						"No more records to display.", "Done", parent);*/
				// Utilities.showMessageDialog("over", parent);
			} else {
				Toast.makeText(
						getApplicationContext(),
						"Internet speed is slow,data will get displayed within sometime",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public class Downloadxml_FirstWithRefresh extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*progressDialog = new ProgressDialog(parent);
			progressDialog.setMessage("Loading...");
			progressDialog.show();*/
			iv.setVisibility(View.GONE);
			bar.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {
			try {

				String url;
				// &Date=2015-10-07
				url = "http://vritti.ekatm.co.in/VWB/webservice/ActivityWebservice.asmx/getGpscordinates?Mobileno="
						+ supporterMobno + "&LDate=" + Date_L + "&Hdate="
						+ Date_H;


				String res = ut.httpGetvb(url);
				if (checkRecord(res)) {
                    sop = "valid";
				//DatabaseHandler db = new DatabaseHandler(getApplicationContext());
					SQLiteDatabase sql = db.getWritableDatabase();
					//sql.execSQL("DROP TABLE IF EXISTS GPSrecords");
					//sql.execSQL(ut.getGPSrecords());
					sql.delete("GPSrecords",null,null);

					NodeList nl = ut.getnode(res, "Table");
					Log.e("nodelist", "nl.getLength() : " + nl.getLength());
					for (int i = 0; i < nl.getLength(); i++) {
						
						
						Element e = (Element) nl.item(i);
						String location = ut.getValue(e, "locationName");

						if (location.equalsIgnoreCase("Location Not Found")
								|| location.equalsIgnoreCase("null")) {
							String lat = ut.getValue(e, "latitude");
							Double Latitud = Double.valueOf(lat).doubleValue();
							String longt = ut.getValue(e, "longitude");
							Double longitude = Double.valueOf(longt)
									.doubleValue();
							String LocationName2;
							try {
								Geocoder geocoder = new Geocoder(
										SupporterGpsLocation.this,
										Locale.getDefault());
								List<Address> addressList = geocoder
										.getFromLocation(Latitud, longitude, 1);
								if (addressList != null
										&& addressList.size() > 0) {
									Address address = addressList.get(0);
									StringBuilder sb = new StringBuilder();
									for (int v = 0; v < address
											.getMaxAddressLineIndex(); v++) {
										sb.append(address.getAddressLine(v));

									}

									LocationName2 = sb.toString();

									adduserdatagps(new GPSLocationTimeBean(
											ut.getValue(e, "GPSID"),
											ut.getValue(e, "MobileNo"),
											ut.getValue(e, "latitude"),
											ut.getValue(e, "longitude"),
											LocationName2, ut.getValue(
													e, "AddedDt"), ut
													.getValue(e, "num")));

								}

							} catch (Exception err) {
								err.printStackTrace();
								LocationName2 = " ";
							}

						} else {
							String columnName = ut
									.getValue(e, "AddedDt");
							Log.e("update master........",
									"users : name: "
											+ ut.getValue(e, "username")
											+ "  psd : "
											+ ut.getValue(e, "userpass"));
							// if(columnName.equalsIgnoreCase("AddedDt")){
							// //yyyy-MM-dd'T'HH:mm:ss.SSSZ
							// }
							
							adduserdatagps(new GPSLocationTimeBean(
									ut.getValue(e, "GPSID"), ut
											.getValue(e, "MobileNo"), ut
											.getValue(e, "latitude"), ut
											.getValue(e, "longitude"),
											ut.getValue(e, "locationName"),
											ut.getValue(e, "AddedDt"), ut
											.getValue(e, "num")));
						}
					}
				} else {
					sop = "over";
				}

			} catch (Exception e) {
				sop = "invalid";
				Log.e("error ", " msg : " + e.getMessage());
			}
			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		
			if (sop.equals("valid")) {
				iv.setVisibility(View.VISIBLE);
				bar.setVisibility(View.GONE);
			
				updaterefreshlist();
			
			} else if (sop.equals("over")) {
				/*
				 * Utilities.showCustomMessageDialog("No Records to display.",
				 * "Done", parent);
				 */
				Download_Date ada = new Download_Date();
				ada.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				iv.setVisibility(View.VISIBLE);
				bar.setVisibility(View.GONE);
				Toast.makeText(
						getApplicationContext(),
						"Internet speed is slow,data will get displayed within sometime",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public class Download_Date extends AsyncTask<String, Void, String> {

		String Soup;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// progressDialog = new ProgressDialog(parent);
			// progressDialog.setMessage("Loading...");
			// progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url;
			// &Date=2015-10-07
			Soup = "INVALID";
			url = "http://vritti.ekatm.co.in/VWB/webservice/ActivityWebservice.asmx/getGpsCordinate_date?mobileno="
					+ supporterMobno;
			String res = null;
			try {
				res = ut.httpGet(url);

				if (res.contains("</string>")) {

					Soup = "VALID";
					res = res.substring(res.indexOf(">") + 1);
					res = res.substring(res.indexOf(">") + 1);
					res = res.substring(0, res.indexOf("<")); // 7/6/2016
																// 2:50:31 PM
					String Data = res;
					res = Data;
				} else {
					Soup = "INVALID";

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Soup = "INVALID";
			}
			return res;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (Soup.equalsIgnoreCase("VALID")) {

				SimpleDateFormat date = new SimpleDateFormat(
						"MM/dd/yyyy hh:mm:ss aa");// 7/6/2016 2:50:31 PM
				Date ss = null;
				try {
					ss = date.parse(result);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Date_L = dateFormat.format(ss);// 2016-07-06

				Calendar c;
				c = Calendar.getInstance();
				c.setTime(ss);
				c.add(Calendar.DATE, 1);
				Date nextday = c.getTime();
				Date_H = dateFormat.format(nextday);
				new Downloadxml_FirstWithRefresh().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

			} else if (Soup.equalsIgnoreCase("INVALID")) {
              ut.showD(SupporterGpsLocation.this, "nodata");
				iv.setVisibility(View.VISIBLE);
				bar.setVisibility(View.GONE);

			}
		}

	}

	private boolean dbvalue() {
		try {

			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			// Cursor cursor = sql.rawQuery("SELECT *   FROM AllStation", null);
			Cursor c = sql.rawQuery("SELECT * FROM GPSrecords WHERE MobileNo='"
					+ supporterMobno + "'", null);
			if (c != null && c.getCount() > 0) {

				c.close();
				return true;

			} else {
				c.close();
				return false;
			}
		} catch (Exception e) {
			dff = new SimpleDateFormat("HH:mm:ss");
			String Ldate = dff.format(new Date());

			StackTraceElement l = new Exception().getStackTrace()[0];
			System.out.println(l.getClassName() + "/" + l.getMethodName() + ":"
					+ l.getLineNumber());
			com.stavigilmonitoring.utility ut = new utility();
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
	
	public void adduserdatagps(GPSLocationTimeBean Tb) {
		
	   // DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("GPSID", Tb.getGPSID());
		values.put("MobileNo", Tb.getMobileNo());
		values.put("latitude", Tb.getLatitude());
		values.put("longitude", Tb.getLongitude());
		values.put("locationName", Tb.getLocationName());
		values.put("AddedDt", Tb.getAddedDt());
		values.put("num", Tb.getNum());

		long a =sql.insert("GPSrecords", null, values);

		Cursor c = sql.rawQuery("SELECT * FROM GPSrecords", null);
		Log.e("Countdata", c.getCount() +"  insert data"+ a);

		c.close();

	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		finish();
	}
	public void ClearDB() {
		
	//	DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		sql.delete("Gpsreportingto", null, null);

		Cursor c = sql.rawQuery("select * from Gpsreportingto", null);
		Log.e("cleardb", "user : " + c.getCount());
		c = null;
		c.close();
	}
	public boolean checkRecord(String xml) {
		String columnName, response = null;
		if (xml.contains("<response>")) {
			NodeList nl = ut.getnode(xml, "Table");
			for (int i = 0; i < nl.getLength(); i++) {
				Element e = (Element) nl.item(i);
				columnName = "response";
				response = ut.getValue(e, columnName);
			}
			if (response.equalsIgnoreCase("No record found")
					|| response.equalsIgnoreCase(null)) {
				return false;
			}
		} else if (xml.contains("<UserMasterId>")) {
			return true;
		}
		return true;
	}
}

package com.stavigilmonitoring;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.SupporterWorkDoneAdapter;
import com.beanclasses.StationCall;
import com.beanclasses.UserList;
import com.database.DBInterface;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

public class SupporterWorkDone extends Activity {

	// Table1
	ProgressDialog progressdialogupdateserver;
	ListView workspacewisedetail;
	String responsesoap = "Added";
	String mobno, link;
	AsyncTask depattask, refreshasyncupdateserver, depattask1;
	// ExpandableListAdapter listAdapter;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv;
	static SimpleDateFormat dff;
	static String Ldate;
	String ActivityName, ActivityId, actname;
	String SupporterName,SupporterMob;
	String z = "";
	String reasonCode = "";
	private ListView SubnetList;
	HashMap<String, List<String>> listDataChild;
	List<String> listDataHeader;
	private String Subnet;
	private TextView HeaderText;
	ArrayList<String> assignedlist = new ArrayList<String>();
	ArrayList<String> assignedlist1 = new ArrayList<String>();
	private String contactName;
	private String contactnum;
	List<StationCall> lstCall = new ArrayList<StationCall>();
	ArrayList<UserList> searchResult;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.sopporter);
		HeaderText = (TextView) findViewById(com.stavigilmonitoring.R.id.namesubnet);
		Intent extras = getIntent();
		SupporterName = extras.getStringExtra("PersonName");
		SupporterMob = extras.getStringExtra("PersonNumber");
		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_sup_Enq_sub);
		HeaderText.setText("Work Done -"+SupporterName);
		SubnetList = (ListView) findViewById(com.stavigilmonitoring.R.id.lstsupenq_SUBNET);

		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();

		if (dbvalue()) {
			updatelist();
			// fetchdata();

		} else if (isnet()) {

			fetchdata();
		} else {
			showD("nonet");
		}
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isnet()) {
					fetchdata();
				} else {
					showD("nonet");
				}

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

	protected String getctime() {
		// TODO Auto-generated method stub

		Calendar c = Calendar.getInstance();
		SimpleDateFormat df3 = new SimpleDateFormat("HH:mm a");
		String formattedDate3 = df3.format(c.getTime());

		return formattedDate3;
	}

	private boolean dbvalue() {
		// TODO Auto-generated method stub
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		try{
		Cursor cursor = sql.rawQuery("SELECT * FROM WorkMaterialSupporter WHERE Mobileno ='"+SupporterMob+"'",
				null);

		System.out.println("----------  dbvalue screen cursor count -- "
				+ cursor.getCount());

		if (cursor != null && cursor.getCount() > 0) {

			// UserList
			Cursor cursor1 = sql.rawQuery("SELECT * FROM UserList", null);
			if (cursor1 != null && cursor1.getCount() > 0) {
				Cursor cursor2 = sql.rawQuery(
						"SELECT * FROM ConnectionStatusFiltermob", null);
				if (cursor2 != null && cursor2.getCount() > 0) {
					cursor2.close();
					cursor1.close();
					cursor.close();
					sql.close();
					//db1.close();
					return true;

				} else {
					cursor2.close();
					cursor1.close();
					cursor.close();
					sql.close();
					//db1.close();
					return false;

				}

			} else {

				cursor1.close();
				cursor.close();
				sql.close();
				//db1.close();
				return false;

			}

		} else {

			cursor.close();
			sql.close();
			//db1.close();
			return false;
		}
		}catch(Exception e){
			e.printStackTrace();
			sql.close();
			//db1.close();
			return false;
			
		}

	}

	private void updatelist() {
		searchResult = GetDetail();

		SubnetList.setAdapter(new SupporterWorkDoneAdapter(this, searchResult));
		registerForContextMenu(SubnetList);

	}

	private ArrayList<UserList> GetDetail() {

		ArrayList<UserList> results = new ArrayList<UserList>();

		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c1 = sql.rawQuery("SELECT * FROM WorkMaterialSupporter WHERE " +
				"Mobileno ='"+SupporterMob+"'", null);

		if (c1.getCount() == 0) {

			c1.close();
			sql.close();
			//db.close();
		} else {

			c1.moveToFirst();

			int column = 0;
			do {

				String WorkTypeMasterId,mobile, WorkType,Station,Installation, Remarks, currentDate, currentLocation, latitude, longitude;
				Station = c1.getString(c1.getColumnIndex("StationName"));
				Installation = c1.getString(c1.getColumnIndex("InstallationId"));
				mobile = c1.getString(c1.getColumnIndex("Mobileno"));
				WorkTypeMasterId = c1.getString(c1.getColumnIndex("WorkTypeMasterId"));
				int WorkTypeMasterID = Integer.parseInt(WorkTypeMasterId);
				WorkType = c1.getString(c1.getColumnIndex("WorkType"));
				Remarks = c1.getString(c1.getColumnIndex("Remarks"));
				currentDate = c1.getString(c1.getColumnIndex("currentDate"));
				currentDate = splitDate(currentDate);
				currentLocation = c1.getString(c1
						.getColumnIndex("currentLocation"));
				Double lat_d,long_d;
				if (currentLocation.equalsIgnoreCase("")||currentLocation==null) {
					lat_d  =	Double.parseDouble(c1.getString(c1.getColumnIndex("latitude")));
					long_d =    Double.parseDouble(c1.getString(c1.getColumnIndex("longitude")));
					
					if(!(lat_d == 0.0 && long_d == 0.0)){
						
						try {
							Geocoder geocoder = new Geocoder(
									SupporterWorkDone.this,
									Locale.getDefault());
							List<Address> addressList = geocoder
									.getFromLocation(lat_d,
											long_d, 1);
							if (addressList != null
									&& addressList.size() > 0) {
								Address address = addressList.get(0);
								StringBuilder sb = new StringBuilder();
								for (int i = 0; i < address
										.getMaxAddressLineIndex(); i++) {
									sb.append(address.getAddressLine(i));

								}

								currentLocation = sb.toString();

							}

							// new GPSTask().execute();
						} catch (IOException e) {
							currentLocation = "No Info";
							Log.e("test", "Unable connect to Geocoder",
									e);
						}
					}else{
						
						currentLocation ="No Info";
					}
				} else {
					lat_d  =	Double.parseDouble(c1.getString(c1.getColumnIndex("latitude")));
					long_d =    Double.parseDouble(c1.getString(c1.getColumnIndex("longitude")));
				}
			
			

				UserList list = new UserList();
				list.setWorkTypeMasterId(WorkTypeMasterID);
				list.setInstallation(Installation);
				list.setStationName(Station);
				list.setMobile(mobile);
				list.setWorkType(WorkType);
				list.setRemarks(Remarks);
				list.setCurrentDate(currentDate);
				list.setCurrentLocation(currentLocation);
				list.setLatitude(lat_d);
				list.setLongitude(long_d);
				results.add(list);
			} while (c1.moveToNext());

			/*
			 * int i = user.size(); int n = ++i; arrUser = new String[n];
			 * for(int cnt=0;cnt<i;cnt++) { arrUser[cnt]
			 * =user.get(cnt).getMobile(); }
			 */

			c1.close();
			sql.close();
			//db.close();
		}

		return results;

	}

	private String splitDate(String currentDate) {
		// TODO Auto-generated method stub
		//2016-07-02T19:29:00+05:30
		currentDate = currentDate.replace("T"," ");
		currentDate = currentDate.substring(0,currentDate.indexOf("+"));
		 SimpleDateFormat dateFormat = new SimpleDateFormat(
	                "yyyy-MM-dd HH:mm:ss");
	        Date myDate = null;
	        try {
	            myDate = dateFormat.parse(currentDate);

	        } catch (ParseException e) {
	            e.printStackTrace();
	        }

	        SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
	        String finalDate = timeFormat.format(myDate);

	        System.out.println(finalDate);
	        return finalDate;
	}

	private void fetchdata() {
		new DownloadxmlsDataURL_new().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			try {
				String respons = "";

				String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/getWorkDoneMaterialUsedSupporter?mobileno="
						+ SupporterMob;
				Log.e("csn status", "url : " + url);
				url = url.replaceAll(" ", "%20");
				respons = ut.httpGet(url);
				Log.e("csn status", "resmsg : " + respons);
			//	DatabaseHandler db = new DatabaseHandler(getBaseContext());
				SQLiteDatabase sql = db.getWritableDatabase();
				if (respons.contains("<WorkTypeMasterId>")) {
					sop = "valid";
					//sql.execSQL("DROP TABLE IF EXISTS WorkMaterialSupporter");// UserList
					//sql.execSQL(ut.getWorkDoneAndMaterialSupporter());
					sql.delete("WorkMaterialSupporter",null,null);

					Cursor c1 = sql.rawQuery(
							"SELECT *   FROM WorkMaterialSupporter", null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(respons, "Table1");
					String msg = "";
					String columnName, columnValue;
					Log.e("sts main...", " fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < c1.getColumnCount(); j++) {
							columnName = c1.getColumnName(j);
							columnValue = ut.getValue(e, columnName);

							values1.put(columnName, columnValue);

						}
						sql.insert("WorkMaterialSupporter", null, values1);
					}

					c1.close();
					sql.close();
					//db.close();

				}

			} catch (Exception e) {

				e.printStackTrace();
				sop = "invalid";
			}

			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				System.out.println("...............value of sop" + sop);
				if (sop.equalsIgnoreCase("valid")) {

					updatelist();
					// updatelist1();

				} else {

					showD("invalid");
				}
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBarsupenq_sub))
						.setVisibility(View.GONE);
			} catch (Exception e) {
				sop = "invalid";
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
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBarsupenq_sub))
					.setVisibility(View.VISIBLE);

		}

	}

	protected void showD(String string) {
		final Dialog myDialog = new Dialog(SupporterWorkDone.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
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
			txt.setText("No Refresh data Available...");
		}

		Button btn = (Button) myDialog
				.findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				myDialog.dismiss();
			}
		});

		myDialog.show();

	}

	/*public void updatelist1() {
		DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = new String[1];
		// params[0] = Stationname;

		Cursor c2 = sql
				.rawQuery(
						"SELECT s.InstallationId,ServerTime,Remarks,Last7DaysPerFormance, QuickHealStatus, STAVersion, LatestDowntimeReason,InstallationDesc ,UserName,MobileNo,SUP FROM ConnectionStatusUser s "
								+ " INNER JOIN ConnectionStatusUser1 s1 ON s.InstallationId=s1.InstallationId where s1.InstallationDesc=?",
						params);

		if (c2.getCount() <= 0) {
			c2.close();
			sql.close();
			//db.close();
		} else {
			c2.moveToFirst();
			int column = 0;
			do {
				int columnContact = c2.getColumnIndex("UserName");
				contactName = c2.getString(columnContact);
				String[] arr = contactName.split("/");
				int columnnum = c2.getColumnIndex("MobileNo");
				contactnum = c2.getString(columnnum);
				String[] arr1 = contactnum.split("/");
				String stnno = c2.getString(c2.getColumnIndex("SUP"));

				Log.e("Station no", "kavi : " + stnno);

				if (stnno.contains("/")) {
					lstCall.add(new StationCall("Station Number", stnno
							.substring(0, stnno.indexOf("/"))));
				} else
					lstCall.add(new StationCall("Station Number", stnno));

				Log.e("val", "kavi : " + lstCall.size() + "," + stnno);
				for (int p = 0; p < arr.length; p++) {
					lstCall.add(new StationCall(arr[p], arr1[p]));
				}

				
				 * CallList.setAdapter(new
				 * CallListAdapter(ConnectionStatus.this, lstCall));
				 

			} while (c2.moveToNext());

			c2.close();
			sql.close();
			//db.close();
		}
	}*/

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
	public void onBackPressed() {
		super.onBackPressed();

	}

	/*class UserList {

		String Installation;
		String StationName;
		String Mobile;
		int WorkTypeMasterId;
		String WorkType;
		String Remarks;
		String currentDate;
		String currentLocation;
		Double latitude;
		Double longitude;
		public String getInstallation() {
			return Installation;
		}
		public void setInstallation(String installation) {
			Installation = installation;
		}
		public String getStationName() {
			return StationName;
		}
		public void setStationName(String stationName) {
			StationName = stationName;
		}
		public String getMobile() {
			return Mobile;
		}
		public void setMobile(String mobile) {
			Mobile = mobile;
		}
		public int getWorkTypeMasterId() {
			return WorkTypeMasterId;
		}
		public void setWorkTypeMasterId(int workTypeMasterId) {
			WorkTypeMasterId = workTypeMasterId;
		}
		public String getWorkType() {
			return WorkType;
		}
		public void setWorkType(String workType) {
			WorkType = workType;
		}
		public String getRemarks() {
			return Remarks;
		}
		public void setRemarks(String remarks) {
			Remarks = remarks;
		}
		public String getCurrentDate() {
			return currentDate;
		}
		public void setCurrentDate(String currentDate) {
			this.currentDate = currentDate;
		}
		public String getCurrentLocation() {
			return currentLocation;
		}
		public void setCurrentLocation(String currentLocation) {
			this.currentLocation = currentLocation;
		}
		public Double getLatitude() {
			return latitude;
		}
		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}
		public Double getLongitude() {
			return longitude;
		}
		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}

		

	}*/

}

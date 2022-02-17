package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.SupporterEnuiryAdptr;
import com.beanclasses.StationCall;
import com.beanclasses.SupportEnquiryHelper;
import com.database.DBInterface;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class SupporterEnquiryStateFilter extends Activity {

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
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	private TextView mText;
	private TextView txtdaterefresh;
	String daterestr;
	String z = "";
	String reasonCode = "";
	private GridView SubnetList;
	HashMap<String, List<String>> listDataChild;
	List<String> listDataHeader;
	private String Network,key;
	private TextView NetWorkText;
	private LinearLayout mAllnet;
	DatabaseHandler db;

	List<StationCall> lstCall = new ArrayList<StationCall>();
	ArrayList<SupportEnquiryHelper> searchResults =new ArrayList<SupportEnquiryHelper>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.suporterenquirysubnet);
		NetWorkText = (TextView) findViewById(com.stavigilmonitoring.R.id.namesubnet);
        Bundle extras = getIntent().getExtras();

		Network = extras.getString("Type");
		key = extras.getString("SupEnqKey");

		mAllnet = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.supportall);
		mText = (TextView) findViewById(com.stavigilmonitoring.R.id.tvName);
	    NetWorkText.append("-"+Network);
		mText.setText(Network+"-All");
        iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_sup_Enq_sub);
		SubnetList =  findViewById(com.stavigilmonitoring.R.id.lstsupenq_SUBNET);

		db = new DatabaseHandler(getBaseContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
        dbi.Close();

		if (dbvalue()) {
			updatelist();
			//updatelist1();
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
		
		mAllnet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(key.equalsIgnoreCase("supenq_STNWISE")){
					//display stations list
					Intent intent = new Intent(getApplicationContext(), SupporterEnquiryStationsList.class);
					intent.putExtra("Type",Network);
					intent.putExtra("SubType","NoSubNetwork");
					startActivity(intent);
				}else if(key.equalsIgnoreCase("supenq_SUPWISE")){
					//display supporterlist screen
					Intent intent = new Intent(getApplicationContext(), SupportListAll.class);
					intent.putExtra("Type", Network);
					startActivity(intent);
				}
			}
		});

		SubnetList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(key != null){
					if(key.equalsIgnoreCase("supenq_STNWISE")){
						//display stations list
						Intent intent = new Intent(getApplicationContext(), SupporterEnquiryStationsList.class);
						intent.putExtra("Type",Network);
						intent.putExtra("SubType",searchResults.get(arg2).getSubnetwok());
						startActivity(intent);

					}else if(key.equalsIgnoreCase("supenq_SUPWISE")){
						//display supporterlist screen

						Bundle dataBundle = new Bundle();
						dataBundle.putString("Subnet",searchResults.get(arg2).getSubnetwok());
						//	dataBundle.putString("frompage", frompage);
						dataBundle.putString("SubType", searchResults.get(arg2).getSubnetwok());
						dataBundle.putString("Type", Network); /*searchResults.get(arg2).getNetwork()*/

						Intent i = new Intent(getApplicationContext(), SupporterList.class);
						i.putExtras(dataBundle);
						startActivity(i);
					}
				}else {
					Toast.makeText(getApplicationContext(),"null key",Toast.LENGTH_SHORT).show();
				}

				/*//intent.putExtra("Type", type);
				intent.putExtra("subType", searchresult.get(position)
						.GetStateName());
				startActivity(intent);*/
			//	finish();
			}
		});

		
	}

	/*public void updatelist1() {
		DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = Stationname;

		Cursor c2 = sql
				.rawQuery(
						"SELECT s.InstallationId,ServerTime,Remarks,Last7DaysPerFormance, QuickHealStatus, STAVersion, LatestDowntimeReason,InstallationDesc ,UserName,MobileNo,SUP FROM ConnectionStatusUser s "
								+ " INNER JOIN ConnectionStatusUser1 s1 ON s.InstallationId=s1.InstallationId where s1.InstallationDesc=?",
						params);

		if (c2.getCount() <= 0) {
			c2.close();
			sql.close();
			db.close();
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

				SubnetList.setAdapter(new CallListAdapter(SupporterEnquiryStateFilter.this,
						lstCall));

			} while (c2.moveToNext());

			c2.close();
			sql.close();
			db.close();
		}*/
	

	

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
		
		Cursor cursor = sql.rawQuery("SELECT *   FROM ConnectionStatusUser1",
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
		}catch (Exception e) {
			// TODO: handle exception

			return false;
		}

	}

	private void updatelist() {
		 searchResults = GetDetail();

		SubnetList.setAdapter(new SupporterEnuiryAdptr(this,
				searchResults));

	}

	private ArrayList<SupportEnquiryHelper> GetDetail() {
		ArrayList<SupportEnquiryHelper> results = new ArrayList<SupportEnquiryHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = new String[1];
		
		Cursor c2 = sql
				.rawQuery(
						"SELECT DISTINCT SubNetworkCode FROM ConnectionStatusFiltermob WHERE NetworkCode='"+Network+"' ORDER BY SubNetworkCode",null);
	
		/*Cursor c3 = sql
				.rawQuery(
						"SELECT DISTINCT s.SubNetworkCode,s1.UserName,s1.MobileNo FROM ConnectionStatusFiltermob s "
								+ " INNER JOIN ConnectionStatusUser1 s1 ON s.InstalationId=s1.InstallationId WHERE s.NetworkCode='"+Subnet+"' ORDER BY s.SubNetworkCode DESC",null);*/
	/*	+ "ConnectionStatusUser1"
		+ "(InstallationId TEXT, InstallationDesc TEXT,UserName TEXT, MobileNo TEXT,SUP TEXT )";*/
	/*	ConnectionStatusFiltermob
		(InstalationId TEXT, InstalationName  TEXT,  InstallationDesc TEXT, Address TEXT, SubNetworkCode TEXT , NetworkCode TEXT,LastbusReporting TEXT,LastAdvDate TEXT,ServerTime TEXT)";// NetworkCode
		return ConnectionStatusFilter;*/
		
		
		
		if (c2.getCount() == 0) {
			SupportEnquiryHelper sr = new SupportEnquiryHelper();
			// sr.setcsId("");
			sr.setSubnetwok("");
			sr.setUsername("");
			sr.setMobileNo("");
			results.add(sr);

			c2.close();

			return results;
		} else {

			c2.moveToFirst();

			int column = 0;
			do {

				SupportEnquiryHelper sr = new SupportEnquiryHelper();
			     sr.setSubnetwok(c2.getString(c2.getColumnIndex("SubNetworkCode")));
				/*sr.setUsername(c2.getString(c2.getColumnIndex("UserName")));
				sr.setMobileNo(c2.getString(c2.getColumnIndex("MobileNo")));*/
		        results.add(sr);

			} while (c2.moveToNext());

			c2.close();
		}
		return results;

	}

	

	

	private void fetchdata() {
		new DownloadxmlsDataURL_new().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			try {
				String xx = "";

				String url = "http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetAllStationWithUserName_Android?Mobile="
						+ mobno;

				Log.e("csn status", "url : " + url);
				url = url.replaceAll(" ", "%20");
				responsemsg = ut.httpGet(url);
			    Log.e("csn status", "resmsg : " + responsemsg);
				//DatabaseHandler db = new DatabaseHandler(getBaseContext());
				SQLiteDatabase sql = db.getWritableDatabase();
				if (responsemsg.contains("<IId>")) {
					sop = "valid";
                    //sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser1");
					//sql.execSQL(ut.getConnectionStatusUser1());
					sql.delete("ConnectionStatusUser1",null,null);

					Cursor c1 = sql.rawQuery(
							"SELECT *   FROM ConnectionStatusUser1", null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table1");
					String msg = "";
					String columnName, columnValue;
					Log.e("sts main...", " fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < c1.getColumnCount(); j++) {
							columnName = c1.getColumnName(j);

							String ncolumnname = "";
							if (columnName.equalsIgnoreCase("InstallationId"))
								ncolumnname = "IId";
							else if (columnName
									.equalsIgnoreCase("InstallationDesc"))
								ncolumnname = "SN";
							else if (columnName.equalsIgnoreCase("UserName"))
								ncolumnname = "UN";
							else if (columnName.equalsIgnoreCase("MobileNo"))
								ncolumnname = "UN1";
							else if (columnName.equalsIgnoreCase("SUP"))
								ncolumnname = "SUP";

							columnValue = ut.getValue(e, ncolumnname);
							if(columnValue.contains(" /")){
								columnValue = columnValue.replaceAll(" /","/");
							}

							if (ncolumnname == "SUP"
									&& columnValue.contains(",")) {
								columnValue = columnValue.substring(0,
										columnValue.indexOf(","));
							}
							Log.e("download stn : ", columnName + " : "
									+ columnValue);
							values1.put(columnName, columnValue);

						}
						sql.insert("ConnectionStatusUser1", null, values1);
					}

					c1.close();

				}
				

			}catch(Exception e){
				
				e.printStackTrace();
				sop = "invalid";
			}
			
		
			String Url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMasterMobile?Mobile="+mobno;
			Log.e("All Station", "Url=" + Url);
			String	resposmsg= "";
			try {
			resposmsg = ut.httpGet(Url);
			
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
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
				//	updatelist1();

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
		final Dialog myDialog = new Dialog(SupporterEnquiryStateFilter.this);
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
			txt.setText("No Refresh data Available. Please check Internet connection...");
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
		Intent intent = new Intent(getApplicationContext(), SupporterEnquiryStatewise.class);
		intent.putExtra("SupEnqKey", key);
		startActivity(intent);
		finish();
	}
}

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

import com.adapters.SupporterListAdptr;
import com.beanclasses.StationCall;
import com.beanclasses.SupportEnquiryHelper;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class SupportListAll extends Activity {

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
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;
	String z = "";
	String reasonCode = "";
	private ListView SubnetList;
	HashMap<String, List<String>> listDataChild;
	List<String> listDataHeader;
	private String Subnet;
	private TextView NetWorkText;
	private LinearLayout contact;
	ArrayList<String> assignedlist = new ArrayList<String>();
	ArrayList<String> assignedlist1 = new ArrayList<String>();
	private Spinner rbfh;
	private String contactName;
	private String contactnum, Network;
	private LinearLayout contact1;
	private LinearLayout contact2;
	private String number1 = "";
	private LinearLayout contact3;
	private String frompage, Type;
	ArrayList<StationCall> lstCall = new ArrayList<StationCall>();
	ArrayList<StationCall> lstCallall = new ArrayList<StationCall>();
	// lstCallall
	List<StationCall> searchResult;
	DatabaseHandler db;

	LinearLayout Sup_connectionstatus, Sup_tvstatus, Sup_nonrepeatedadadv,
			Sup_pendingclipsClipwise, Sup_alerts, Sup_workassign, Sup_dmc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.supporterlistdisplay);
		NetWorkText = (TextView) findViewById(com.stavigilmonitoring.R.id.namesubnet);

		Bundle extras = getIntent().getExtras();

		Network = extras.getString("Type");
		NetWorkText.append("-" + Network);

		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_sup_Enq_sub);
		iv.setVisibility(View.GONE);

		SubnetList = (ListView) findViewById(com.stavigilmonitoring.R.id.lstsupenq_SUBNET);
		SubnetList.setVisibility(View.GONE);

		Sup_connectionstatus = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.sup_connectionstatus);
		Sup_tvstatus = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.sup_tvstatus);
		Sup_nonrepeatedadadv = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.sup_nonrepeatedadadv);
		Sup_pendingclipsClipwise = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.sup_pendingclipsClipwise);
		Sup_alerts = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.sup_alerts);
		Sup_alerts.setVisibility(View.GONE);
		Sup_workassign = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.sup_workassign);
		Sup_workassign.setVisibility(View.GONE);
		Sup_dmc = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.sup_dmc);
		Sup_dmc.setVisibility(View.GONE);

		db = new DatabaseHandler(getBaseContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();

		/*if (dbvalue()) {
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
		});*/

		SubnetList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Bundle dataBundle = new Bundle();
				// dataBundle.putString("stnname", Stationname);
				// dataBundle.putString("frompage", frompage);
				// dataBundle.putString("SubType", subType);
				// dataBundle.putString("Type", Type);
				/*
				 * Intent i = new Intent(getApplicationContext(),
				 * SupporterList.class);
				 */
				/*
				 * i.putExtras(dataBundle); startActivity(i); finish();
				 */
			}
		});

		SubnetList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				return false;
			}
		});

		Sup_connectionstatus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				String Flag = "SUPNoSubnetWork";
				Intent intent = new Intent(getApplicationContext(),	ConnectionStatusMainAll.class);
				intent.putExtra("Type",Network);
				//intent.putExtra("subType",Subnet);
				intent.putExtra("NoSubnetWork",Flag);
				intent.putExtra("CallFrom","SupporterListAll");
				startActivity(intent);
			}
		});

		Sup_tvstatus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(),	TvStatusMainAll.class);
				intent.putExtra("Type", Network);
				intent.putExtra("CallFrom","SupporterListAll");
				startActivity(intent);
			}
		});

		Sup_nonrepeatedadadv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), NonrepeatedAdMainAll.class);
				intent.putExtra("Type", Network);
				intent.putExtra("CallFrom","SupporterListAll");
				startActivity(intent);
			}
		});

		Sup_pendingclipsClipwise.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PendingClipsMainAll.class);
                intent.putExtra("Type", Network);
                intent.putExtra("CallFrom","SupporterListAll");
                startActivity(intent);
            }
        });

	}

	/*
	 * public void updatelist1() { DatabaseHandler db = new
	 * DatabaseHandler(this); SQLiteDatabase sql = db.getWritableDatabase();
	 * String[] params = new String[1]; params[0] = Stationname;
	 * 
	 * Cursor c2 = sql .rawQuery(
	 * "SELECT s.InstallationId,ServerTime,Remarks,Last7DaysPerFormance, QuickHealStatus, STAVersion, LatestDowntimeReason,InstallationDesc ,UserName,MobileNo,SUP FROM ConnectionStatusUser s "
	 * +
	 * " INNER JOIN ConnectionStatusUser1 s1 ON s.InstallationId=s1.InstallationId where s1.InstallationDesc=?"
	 * , params);
	 * 
	 * if (c2.getCount() <= 0) { c2.close(); sql.close(); //db1.close(); } else {
	 * c2.moveToFirst(); int column = 0; do { int columnContact =
	 * c2.getColumnIndex("UserName"); contactName = c2.getString(columnContact);
	 * String[] arr = contactName.split("/"); int columnnum =
	 * c2.getColumnIndex("MobileNo"); contactnum = c2.getString(columnnum);
	 * String[] arr1 = contactnum.split("/"); String stnno =
	 * c2.getString(c2.getColumnIndex("SUP"));
	 * 
	 * Log.e("Station no", "kavi : " + stnno);
	 * 
	 * if (stnno.contains("/")) { lstCall.add(new StationCall("Station Number",
	 * stnno .substring(0, stnno.indexOf("/")))); } else lstCall.add(new
	 * StationCall("Station Number", stnno));
	 * 
	 * Log.e("val", "kavi : " + lstCall.size() + "," + stnno); for (int p = 0; p
	 * < arr.length; p++) { lstCall.add(new StationCall(arr[p], arr1[p])); }
	 * 
	 * SubnetList.setAdapter(new
	 * CallListAdapter(SupporterEnquiryStateFilter.this, lstCall));
	 * 
	 * } while (c2.moveToNext());
	 * 
	 * c2.close(); sql.close(); //db1.close(); }
	 */

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
					////db1.close();
					return true;

				} else {
					cursor2.close();
					cursor1.close();
					cursor.close();
					sql.close();
					////db1.close();
					return false;

				}

			} else {

				cursor1.close();
				cursor.close();
				sql.close();
				////db1.close();
				return false;

			}

		} else {

			cursor.close();
			sql.close();
			////db1.close();
			return false;
		}
		
		}catch(Exception e){
			
		
			sql.close();
			////db1.close();
			return false;
		}

	}

	private void updatelist() {
		searchResult = GetDetail();
		StationCall bean = new StationCall(
				"Click and Hold The Item For More Option", "");
		searchResult.add(bean);
		SubnetList.setAdapter(new SupporterListAdptr(this, searchResult));
		registerForContextMenu(SubnetList);

	}

	private List<StationCall> GetDetail() {
		lstCall.clear();
		ArrayList<SupportEnquiryHelper> results = new ArrayList<SupportEnquiryHelper>();
		ArrayList<UserList> user = new ArrayList<UserList>();
		ArrayList<String> Userarr = new ArrayList<String>();
		ArrayList<String> Userarrlist = new ArrayList<String>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] arrUser;
		Cursor c1 = sql.rawQuery("SELECT * FROM UserList", null);

		if (c1.getCount() == 0) {

			c1.close();
			sql.close();
			//db1.close();
			user.clear();
		} else {

			c1.moveToFirst();

			int column = 0;
			do {
				UserList list = new UserList();

				list.setMobile(c1.getString(c1.getColumnIndex("Mobile")));
				Userarr.add(c1.getString(c1.getColumnIndex("Mobile")));
				list.setSerial(c1.getString(c1.getColumnIndex("SrNo")));
				user.add(list);

			} while (c1.moveToNext());

			/*
			 * int i = user.size(); int n = ++i; arrUser = new String[n];
			 * for(int cnt=0;cnt<i;cnt++) { arrUser[cnt]
			 * =user.get(cnt).getMobile(); }
			 */

			c1.close();
			sql.close();
			//db1.close();
		}

		//DatabaseHandler db1 = new DatabaseHandler(this);
		SQLiteDatabase sql1 = db.getWritableDatabase();
		Cursor c2 = sql1
				.rawQuery(
						"SELECT DISTINCT s.SubNetworkCode,s1.UserName,s1.MobileNo FROM ConnectionStatusFiltermob s "
								+ " INNER JOIN ConnectionStatusUser1 s1 ON s.InstalationId=s1.InstallationId WHERE s.NetworkCode='"
								+ Network + "'", null);

		if (c2.getCount() == 0) {
			SupportEnquiryHelper sr = new SupportEnquiryHelper();
			// sr.setcsId("");
			sr.setSubnetwok("");
			sr.setUsername("");
			sr.setMobileNo("");
			results.add(sr);

			c2.close();
			sql1.close();
			////db1.close();
			lstCall.clear();

			return lstCall;
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

				/*
				 * SupportEnquiryHelper sr = new SupportEnquiryHelper();
				 * sr.setSubnetwok
				 * (c2.getString(c2.getColumnIndex("SubNetworkCode")));
				 * sr.setUsername(c2.getString(c2.getColumnIndex("UserName")));
				 * sr.setMobileNo(c2.getString(c2.getColumnIndex("MobileNo")));
				 * results.add(sr);
				 */
				for (int p = 0; p < arr.length; p++) {

					if (arr[p].equalsIgnoreCase("")) {

					} else {
						// for (UserList wp : user) {
						// contains(arr1[p]

						if ((Userarr.contains(arr1[p]))) {// 9561068567
															// 9762259197

						} else {
							StationCall bean = new StationCall(arr[p], arr1[p]);

							for (int i = 0; i < lstCallall.size(); i++) {

								Userarrlist.add(lstCallall.get(i).getnumber());
							}
							if (!Userarrlist.contains(arr1[p])) {
								lstCallall.add(bean);
							}

						}
					}
				}

				// }
			} while (c2.moveToNext());

			c2.close();
			sql1.close();
			////db1.close();

		}

		for (int i = 0; i < lstCallall.size(); i++) {
			for (int j = 0; j < lstCallall.size(); j++) {
				if (i != j) {

					if (lstCallall.get(i).equals(lstCallall.get(j))) {
						lstCallall.remove(j);
					}
				}
			}
		}
		lstCall = lstCallall;

		return lstCall;

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

				String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/getHideUserList";

				Log.e("csn status", "url : " + url);
				url = url.replaceAll(" ", "%20");
				respons = ut.httpGet(url);
				Log.e("csn status", "resmsg : " + respons);
				//DatabaseHandler db = new DatabaseHandler(getBaseContext());
				SQLiteDatabase sql = db.getWritableDatabase();
				if (respons.contains("<SrNo>")) {
					sop = "valid";
					//sql.execSQL("DROP TABLE IF EXISTS UserList");// UserList
					//sql.execSQL(ut.GetUserList());
					sql.delete("UserList",null,null);

					Cursor c1 = sql.rawQuery("SELECT *   FROM UserList", null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(respons, "VrittiT");
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
						sql.insert("UserList", null, values1);
					}

					c1.close();
					sql.close();
					//db1.close();

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
				sop = "invalid";
			}

			try {
				String xx = "";

				String url = "http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetAllStationWithUserName_Android?Mobile="
						+ mobno;

				Log.e("csn status", "url : " + url);
				url = url.replaceAll(" ", "%20");
				String responsemsg = ut.httpGet(url);
				Log.e("csn status", "resmsg : " + responsemsg);
				//DatabaseHandler db = new DatabaseHandler(getBaseContext());
				SQLiteDatabase sql = db.getWritableDatabase();
				if (responsemsg.contains("<IId>")) {
					sop = "valid";
					//sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser1");// UserList
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
							else if (columnName.equalsIgnoreCase("InstallationDesc"))
								ncolumnname = "SN";
							else if (columnName.equalsIgnoreCase("UserName"))
								ncolumnname = "UN";
							else if (columnName.equalsIgnoreCase("MobileNo"))
								ncolumnname = "Mob";
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
					sql.close();
					//db1.close();

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
				sop = "invalid";
			}

			String Url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMasterMobile?Mobile="
					+ mobno;
			Log.e("All Station", "Url=" + Url);
			String resposmsg = "";
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

					Log.e("All Station Data ",
							"get length : " + nl2.getLength());
					for (int i = 0; i < nl2.getLength(); i++) {
						Log.e("All Station Data ",
								"length : " + nl2.getLength());
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
					sql.close();
					//db1.close();

				} else {
					sop = "invalid";
					System.out
							.println("--------- invalid for project list --- ");
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
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
					.setVisibility(View.GONE);

		}

	}

	protected void showD(String string) {
		final Dialog myDialog = new Dialog(SupportListAll.this);
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

	public void updatelist1() {
		//DatabaseHandler db = new DatabaseHandler(this);
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
			//db1.close();
		} else {
			c2.moveToFirst();
			String mycount = Integer.toString(c2.getCount());
			Log.e("COUNT ALA", "C2 : "+mycount);
			
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

				/*
				 * CallList.setAdapter(new
				 * CallListAdapter(ConnectionStatus.this, lstCall));
				 */

			} while (c2.moveToNext());

			c2.close();
			sql.close();
			//db1.close();
		}
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
		finish();
	}

	/*class UserList {

		String Serial;
		String Mobile;

		public String getSerial() {
			return Serial;
		}

		public void setSerial(String serial) {
			Serial = serial;
		}

		public String getMobile() {
			return Mobile;
		}

		public void setMobile(String mobile) {
			Mobile = mobile;
		}

	}*/

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		if (!((searchResult.get(info.position).getName()).equalsIgnoreCase("Click and Hold The Item For More Option"))) {
			if (v.getId() == com.stavigilmonitoring.R.id.lstsupenq_SUBNET) {
				menu.setHeaderTitle(searchResult.get(info.position).getName());
				String[] menuItems = getResources()
						.getStringArray(com.stavigilmonitoring.R.array.menu);
				for (int i = 0; i < menuItems.length; i++) {
					menu.add(Menu.NONE, i, i, menuItems[i]);
				}
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();
		String[] menuItems = getResources().getStringArray(com.stavigilmonitoring.R.array.menu);
		String menuItemName = menuItems[menuItemIndex];
		String listItemName = searchResult.get(info.position).getName();

		int selectpos = info.position; // position in the adapter
		switch (item.getItemId()) {
		case 0:
			Intent intent = new Intent(getApplicationContext(),
					SupporterWorkDone.class);

			intent.putExtra("PersonName", searchResult.get(info.position)
					.getName());
			intent.putExtra("PersonNumber", searchResult.get(info.position)
					.getnumber());
			startActivity(intent);
			break;
		case 1:
			Intent intent2 = new Intent(getApplicationContext(),
					SupporterMaterialDisplay.class);
			intent2.putExtra("PersonName", searchResult.get(info.position)
					.getName());
			intent2.putExtra("PersonNumber", searchResult.get(info.position)
					.getnumber());
			startActivity(intent2);
			break;

		case 2:
			Intent intent3 = new Intent(getApplicationContext(),
					SupporterGpsLocation.class);
			intent3.putExtra("PersonName", searchResult.get(info.position)
					.getName());
			intent3.putExtra("PersonNumber", searchResult.get(info.position)
					.getnumber());
			startActivity(intent3);
			break;
		}
		// return super.onContextItemSelected(item);
		final TextView text = (TextView) findViewById(com.stavigilmonitoring.R.id.footer);
		text.setText(String.format("Selected %s for item %s", menuItemName,
				listItemName));
		AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0.0f);
		alphaAnim.setDuration(400);
		alphaAnim.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				// make invisible when animation completes, you could also
				// remove the view from the layout
				text.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				// text.setBackgroundColor(R.color.)

			}
		});

		text.startAnimation(alphaAnim);

		return true;
	}

}

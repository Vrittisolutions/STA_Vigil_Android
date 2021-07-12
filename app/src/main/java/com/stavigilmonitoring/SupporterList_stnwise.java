package com.stavigilmonitoring;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.adapters.SupporterListAdptr;
import com.beanclasses.ConnectionstatusHelper;
import com.beanclasses.PendingStateList;
import com.beanclasses.StationCall;
import com.beanclasses.SupportEnquiryHelper;
import com.beanclasses.UserList;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SupporterList_stnwise extends Activity {

	String responsesoap = "Added";
	String mobno, link;
	com.stavigilmonitoring.utility ut;
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv;
	static SimpleDateFormat dff;
	static String Ldate;
	String ActivityName, ActivityId, actname;
	String daterestr;
	String z = "";
	String reasonCode = "";
	private ListView SubnetList;
	HashMap<String, List<String>> listDataChild;
	List<String> listDataHeader;
	private String Subnet;
	private TextView NetWorkText;
	private String contactName;
	private String contactnum;
	List<StationCall> lstCall = new ArrayList<StationCall>();
	ArrayList<StationCall> lstCallall = new ArrayList<StationCall>();
	List<StationCall> searchResult;
	LinearLayout Sup_connectionstatus, Sup_tvstatus, Sup_nonrepeatedadadv,
			Sup_pendingclipsClipwise, Sup_alerts, Sup_workassign, Sup_dmc;
	String Type, subType, CallFrom, StationName,responsemsg,Installationid;
	TextView txtreason,tvcsnstatus, txtreason_tvstatus;
	TextView txt_nonreprtdcnt, txt_pdcclipcnt,txt_alrtcnt,txt_wrkassncnt,txt_dmcertfctcnt;
	ImageView imgTV,imgTV1,imgTV2,imgTV3,imgTV4,imgTV5,imgTV6,imgTV7;
	ConnectionstatusHelper sr;
	DatabaseHandler db;
	SQLiteDatabase sql;
	String InstID, InstDesc, TV_Status, TVStatusReason, conn_Server_Time,
			Connstat_Reason, NWCode, SubNWCode, conn, NonReportedCount, AlertCount;
	int PDCcnt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.supporterlistdisplay_stnwise);

		init();

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();

		//fetchdata();

		if (isnet()) {
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

		// setListener();

	}

	public void init(){
		Bundle extras = getIntent().getExtras();

		Subnet = extras.getString("Subnet");
		Type = extras.getString("Type");
		subType = extras.getString("subType");
		StationName = extras.getString("StationName");
		Installationid = extras.getString("Installationid");

		ut = new com.stavigilmonitoring.utility();
		db = new DatabaseHandler(SupporterList_stnwise.this);

		NetWorkText = (TextView) findViewById(com.stavigilmonitoring.R.id.namesubnet);
		NetWorkText.setText("Supporter Enquiry - "+ StationName);

		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_sup_Enq_sub);
		iv.setVisibility(View.GONE);

		SubnetList = (ListView) findViewById(com.stavigilmonitoring.R.id.lstsupenq_SUBNET);
		SubnetList.setVisibility(View.GONE);

		txtreason = (TextView)findViewById(com.stavigilmonitoring.R.id.txtreason);
		tvcsnstatus = (TextView)findViewById(com.stavigilmonitoring.R.id.tvcsnstatus);
		txtreason_tvstatus = (TextView)findViewById(com.stavigilmonitoring.R.id.txtreason_tvstatus);

		txt_nonreprtdcnt = (TextView)findViewById(com.stavigilmonitoring.R.id.txt_nonreprtdcnt);
		txt_pdcclipcnt = (TextView)findViewById(com.stavigilmonitoring.R.id.txt_pdcclipcnt);
		txt_alrtcnt = (TextView)findViewById(com.stavigilmonitoring.R.id.txt_alrtcnt);
		txt_wrkassncnt = (TextView)findViewById(com.stavigilmonitoring.R.id.txt_wrkassncnt);
		txt_dmcertfctcnt = (TextView)findViewById(com.stavigilmonitoring.R.id.txt_dmcertfctcnt);

		Sup_connectionstatus = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.sup_connectionstatus);
		Sup_tvstatus = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.sup_tvstatus);
		Sup_nonrepeatedadadv = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.sup_nonrepeatedadadv);
		Sup_pendingclipsClipwise = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.sup_pendingclipsClipwise);
		Sup_alerts = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.sup_alerts);
		Sup_workassign = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.sup_workassign);
		Sup_dmc = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.sup_dmc);

		//TV status imageviews
		imgTV = (ImageView)findViewById(com.stavigilmonitoring.R.id.imgTV);
		imgTV.setVisibility(View.GONE);
		imgTV1 = (ImageView)findViewById(com.stavigilmonitoring.R.id.imgTV1);
		imgTV1.setVisibility(View.GONE);
		imgTV2 = (ImageView)findViewById(com.stavigilmonitoring.R.id.imgTV2);
		imgTV2.setVisibility(View.GONE);
		imgTV3 = (ImageView)findViewById(com.stavigilmonitoring.R.id.imgTV3);
		imgTV3.setVisibility(View.GONE);
		imgTV4 = (ImageView)findViewById(com.stavigilmonitoring.R.id.imgTV4);
		imgTV4.setVisibility(View.GONE);
		imgTV5 = (ImageView)findViewById(com.stavigilmonitoring.R.id.imgTV5);
		imgTV5.setVisibility(View.GONE);
		imgTV6 = (ImageView)findViewById(com.stavigilmonitoring.R.id.imgTV6);
		imgTV6.setVisibility(View.GONE);
		imgTV7 = (ImageView)findViewById(com.stavigilmonitoring.R.id.imgTV7);
		imgTV7.setVisibility(View.GONE);

		sr = new ConnectionstatusHelper();

	}

	public void setListener(){
		Sup_connectionstatus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				String Flag = "Sup_connectionstatus";
				Intent intent = new Intent(getApplicationContext(), SupEnq_ConnStatus.class);
				intent.putExtra("Type",Type);
				intent.putExtra("subType",Subnet);
				intent.putExtra("LayoutFlag",Flag);
				startActivity(intent);

			}
		});

		Sup_tvstatus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				Intent intent = new Intent(getApplicationContext(),TvStatusMain.class);
				intent.putExtra("Type", Type);
				intent.putExtra("SubType",Subnet);
				intent.putExtra("CallFrom","SupporterList");
				startActivity(intent);
			}
		});

		Sup_nonrepeatedadadv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(),	NonrepeatedAdMain.class);
				intent.putExtra("Type", Type);
				intent.putExtra("SubType", Subnet);
				intent.putExtra("CallFrom","SupporterList");
				startActivity(intent);
			}
		});

		Sup_pendingclipsClipwise.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(),	PendingClipsMain.class);
				intent.putExtra("Type", Type);
				intent.putExtra("SubType", Subnet);
				intent.putExtra("CallFrom","SupporterList");
				startActivity(intent);
			}
		});

		Sup_workassign.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), WorkAssignStation_ActivityDetails.class);
				intent.putExtra("Activity", "SupporterList");
				intent.putExtra("Type", Type);
				intent.putExtra("SubType", Subnet);
				startActivity(intent);
			}
		});

		Sup_dmc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), DmCStateStnSoNoDetails.class);
				i.putExtra("Network", Type);
				i.putExtra("SubNetwork", Subnet);
				i.putExtra("Activity","SupporterList");
				startActivity(i);
			}
		});

		Sup_alerts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(),AlrtsStnListAll.class);
				intent.putExtra("Type", Type);
				intent.putExtra("SubNetwork", Subnet);
				intent.putExtra("Activity","SupporterList");
				startActivity(intent);
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
			//db.close();
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
			//db.close();
		}
		//DatabaseHandler db1 = new DatabaseHandler(this);
		SQLiteDatabase sql1 = db.getWritableDatabase();
		Cursor c2 = sql1
				.rawQuery(
						"SELECT DISTINCT s.SubNetworkCode,s1.UserName,s1.MobileNo FROM ConnectionStatusFiltermob s "
								+ " INNER JOIN ConnectionStatusUser1 s1 ON s.InstalationId=s1.InstallationId WHERE s.SubNetworkCode='"
								+ Subnet + "'", null);

		if (c2.getCount() == 0) {
			SupportEnquiryHelper sr = new SupportEnquiryHelper();
			// sr.setcsId("");
			sr.setSubnetwok("");
			sr.setUsername("");
			sr.setMobileNo("");
			results.add(sr);

			c2.close();
			sql1.close();
			//db1.close();
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
			} while (c2.moveToNext());

			c2.close();
			sql1.close();
			//db1.close();

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
		ProgressDialog progressDialog;

		@Override
		protected String doInBackground(String... params) {
			ut = new com.stavigilmonitoring.utility();

			//connection status and tv status url
			String url_stn_details = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetStationDetails?Installationid="+Installationid+"&mobileno="+mobno;

			Log.e("Data", url_stn_details);
			url_stn_details = url_stn_details.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url_stn_details);

				if (responsemsg.contains("<ServerTime>")) {
					sop = "valid";

					NodeList nl1 = ut.getnode(responsemsg, "TableResult");

					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						InstID = ut.getValue(e,"InstallationID");
						InstDesc = ut.getValue(e,"InstallationDesc");
						TV_Status = ut.getValue(e,"TVStatus");
						TVStatusReason = ut.getValue(e,"TVStatusReason");
						conn_Server_Time = ut.getValue(e,"ServerTime");
						Connstat_Reason = ut.getValue(e,"Reason");
						NWCode = ut.getValue(e,"NetworkCode");
						SubNWCode = ut.getValue(e,"SunNetwork");
						NonReportedCount = ut.getValue(e,"NonReportedCount");
						AlertCount = ut.getValue(e,"AlertCount");
					}

				} else {
					sop = "invalid";
					System.out
							.println("--------- invalid for project list --- ");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {

				if (sop.equals("valid")) {
					//disp counts

					//get CSN status and server time
					Calculate_TimeDiff(conn_Server_Time);
					txtreason.setText(Connstat_Reason);
                   // tvcsnstatus.setText(sr.gettymdiff());

                    if(sr.gettymdiff().equalsIgnoreCase("") ||
							sr.gettymdiff() == null ||
							sr.gettymdiff().equalsIgnoreCase("0")){
						tvcsnstatus.setText("Connected");
						}else {
						tvcsnstatus.setText(sr.gettymdiff());
					}

                    //get TV status and reason
					String TVStatuscnt = TV_Status;
					displ_Tvstatusdata(TVStatuscnt);
					txtreason_tvstatus.setText(TVStatusReason);

					txt_nonreprtdcnt.setText(NonReportedCount);
					txt_alrtcnt.setText(AlertCount);

					getPDCClipCnt();
					getWorkAssignCnt();
					getDMCertificateCnt();

				} else {
					try{
						ut.showD(SupporterList_stnwise.this,"invalid");
					}catch (Exception e){
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();

			}
			progressDialog.dismiss();

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(SupporterList_stnwise.this);
			progressDialog.setMessage("Wait for a while data is loading...");
			//progressDialog.setCanceledOnTouchOutside(false);
			//progressDialog.setCancelable(false);
			progressDialog.show();
			//iv.setVisibility(View.GONE);
			//((ProgressBar) findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
		}
	}

	protected void showD(String string) {
		final Dialog myDialog = new Dialog(SupporterList_stnwise.this);
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

	//connection status time difference
	public void Calculate_TimeDiff(String servTime){
		sr = new ConnectionstatusHelper();

		String conn_Server_Time = servTime;

		String[] tym = splitfromtym(conn_Server_Time);

		try {
			Calendar cal = Calendar.getInstance();

			SimpleDateFormat format = new SimpleDateFormat(
					"MM/dd/yyyy hh:mm:ss aa", Locale.ENGLISH);

			Date Startdate = format.parse(conn_Server_Time);
			Date Enddate = cal.getTime();
			long diff = Enddate.getTime() - Startdate.getTime();
			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);

			if (!(Enddate.getTime() - Startdate.getTime() < 0)) {
				if (diffDays == 0 && diffHours == 0
						&& diffMinutes <= 15) {

				} else {
                                   /* String s = sr.setinstallationId(c.getString(c
                                            .getColumnIndex("InstallationDesc")));*/
					String diffstr = "";
					if (diffDays == 0 && diffHours == 0)
						diffstr = diffMinutes + "Min";
					else if (diffDays == 0)
						diffstr = diffHours + "hr";
					else {
						if (diffDays >= 32) {
							long yc = diffDays / 30;
							if (yc >= 12)
								diffstr = (yc / 12) + " Year";
							else
								diffstr = yc + " Month";
						} else
							diffstr = diffDays + "days ";
					}

					sr.settymdiff(diffstr);
				}
			}
		} catch (Exception ex) {
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
				ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
						+ ":" + l.getLineNumber() + "	"
						+ ex.getMessage() + " " + Ldate);
			}

		}

	}

	private String[] splitfromtym(String tym) {
		System.out.println("---value of tym differ...." + tym);
		String fromtimetw = "";
		final String dateStart = tym;
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.ENGLISH);
		Date date = new Date();
		System.out.println("date format of system......................"
				+ dateFormat.format(date));
		System.out
				.println("date format of web tym......................" + tym);

		final String dateStop = dateFormat.format(date);
		Date d1 = null;
		Date d2 = null;
		String diffTym = "";

		try {
			d1 = dateFormat.parse(dateStart);
			d2 = dateFormat.parse(dateStop);
			System.out.println("d2......................" + d2);
			// in milliseconds
			long diff = d2.getTime() - d1.getTime();

			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			String days = String.valueOf(diffDays);
			String hours = String.valueOf(diffHours);
			String minutes = String.valueOf(diffMinutes);
			if (days.equals("0")) {
				if (hours.equals("0")) {
					// add code for minutes

					int i = Integer.parseInt(minutes);
					if (i >= 30) {
						// end
						diffTym = diffMinutes + " Minutes ";
					} else {
						diffTym = "";
					}
				}
			} else {
				diffTym = diffDays + " Days ";
			}

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

		String[] s = { diffTym };
		return s;
	}

	//tvstatus data details
	public void displ_Tvstatusdata(String TotalTV){
		int i=0;
		for (char d : TotalTV.toCharArray()) {
			switch (i) {
				case 0:
					if(d=='1')
					{
						imgTV.setBackground(getResources().getDrawable(com.stavigilmonitoring.R.drawable.tvon));
					}
					else
						imgTV.setBackground(getResources().getDrawable(com.stavigilmonitoring.R.drawable.tvoff));
					imgTV.setVisibility(View.VISIBLE);
					break;

				case 1:
					if(d=='1')
					{
						imgTV1.setBackground(getResources().getDrawable(com.stavigilmonitoring.R.drawable.tvon));
					}
					else
						imgTV1.setBackground(getResources().getDrawable(com.stavigilmonitoring.R.drawable.tvoff));
					imgTV1.setVisibility(View.VISIBLE);
					break;

				case 2:
					if(d=='1')
					{
						imgTV2.setBackground(getResources().getDrawable(com.stavigilmonitoring.R.drawable.tvon));
					}
					else
						imgTV2.setBackground(getResources().getDrawable(com.stavigilmonitoring.R.drawable.tvoff));
					imgTV2.setVisibility(View.VISIBLE);
					break;

				case 3:
					if(d=='1')
					{
						imgTV3.setBackground(getResources().getDrawable(com.stavigilmonitoring.R.drawable.tvon));
					}
					else
						imgTV3.setBackground(getResources().getDrawable(com.stavigilmonitoring.R.drawable.tvoff));
					imgTV3.setVisibility(View.VISIBLE);
					break;

				case 4:
					if(d=='1')
					{
						imgTV4.setBackground(getResources().getDrawable(com.stavigilmonitoring.R.drawable.tvon));
					}
					else
						imgTV4.setBackground(getResources().getDrawable(com.stavigilmonitoring.R.drawable.tvoff));
					imgTV4.setVisibility(View.VISIBLE);
					break;

				case 5:
					if(d=='1')
					{
						imgTV5.setBackground(getResources().getDrawable(com.stavigilmonitoring.R.drawable.tvon));
					}
					else
						imgTV5.setBackground(getResources().getDrawable(com.stavigilmonitoring.R.drawable.tvoff));
					imgTV5.setVisibility(View.VISIBLE);
					break;

				case 6:
					if(d=='1')
					{
						imgTV6.setBackground(getResources().getDrawable(com.stavigilmonitoring.R.drawable.tvon));
					}
					else
						imgTV6.setBackground(getResources().getDrawable(com.stavigilmonitoring.R.drawable.tvoff));
					imgTV6.setVisibility(View.VISIBLE);
					break;

				case 7:
					if(d=='1')
					{
						imgTV7.setBackground(getResources().getDrawable(com.stavigilmonitoring.R.drawable.tvon));
					}
					else
						imgTV7.setBackground(getResources().getDrawable(com.stavigilmonitoring.R.drawable.tvoff));
					imgTV7.setVisibility(View.VISIBLE);
					break;
			}
			i++;
		}
	}

	public void getPDCClipCnt(){
		sql = db.getWritableDatabase();

		int Count = 0;
		String cnt = null;

		Cursor c = sql.rawQuery(
				"SELECT DISTINCT PendingClips.InstallationDesc FROM PendingClips INNER JOIN ConnectionStatusFilter ON ConnectionStatusFilter.InstalationId=PendingClips.instalationid WHERE ConnectionStatusFilter.InstallationDesc='"
						+ InstDesc + "' ORDER BY PendingClips.InstallationDesc", null);
		c.moveToFirst();
		Log.e("Pending n/w count", "" + c.getCount());
		if (c.getCount() > 0) {
			do {

				String station = c.getString(c.getColumnIndex("InstallationDesc"));

				Cursor cr = sql
						.rawQuery(
								"SELECT s.InstallationId,ServerTime,s1.InstallationDesc FROM ConnectionStatusUser s "
										+ " INNER JOIN ConnectionStatusUser1 s1 ON s.InstallationId=s1.InstallationId where s1.InstallationDesc='"
										+ station + "'", null);

				/*Log.e("Pending n/w count", "" + cr.getCount());
				if (cr.getCount() > 0) {
					cr.moveToFirst();
					sda = cr.getString(cr.getColumnIndex("ServerTime"));
					// int column2 = c.getColumnIndex("ServerTime");
					// Log.e("ServerTime", "" + sda);
					// String tf_connection = c.getString(column2);

					v1 = splitConnectionDT(sda);
					v = splitfrom1(sda);
				} else {
					v = "";
					v1 = "Connected";
				}*/

				Cursor c1 = sql.rawQuery(
						"SELECT * FROM PendingClips WHERE InstallationDesc='"
								+ station + "'", null);
				Log.e("Pending n/w count", "" + c1.getCount());

				if (c1.getCount() > 0) {
					c1.moveToFirst();

					int column = 0;
					// do {

					Count = c1.getCount();
					cnt = String.valueOf(Count);
					PendingStateList s = new PendingStateList();
					s.SetStateName(station);
					s.SetCount(Count);
					//s.setServerTime(v1 + " " + v);
					// } while (c1.moveToNext());
				} else {

				}
				txt_pdcclipcnt.setText(cnt);
			} while (c.moveToNext());
		}
	}

	public void getWorkAssignCnt(){
		sql = db.getWritableDatabase();
		int stncnt = 0,overduecnt=0;

		/*Cursor c = sql.rawQuery(
				"SELECT Distinct StationName FROM WorkAssignedTable WHERE NetworkCode='"
						+NWCode+"' and SubNetworkCode='"
						+ SubNWCode
						+ "' ORDER BY ActualEndDate", null);*/
		Cursor c = sql.rawQuery(
				"SELECT Distinct StationName FROM WorkAssignedTable WHERE StationName='"
						+InstDesc+ "' ORDER BY ActualEndDate", null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			do {

				String Type = c.getString(0);

				Cursor c1 = sql
						.rawQuery(
								"SELECT ActivityId,ActualEndDate FROM WorkAssignedTable WHERE NetworkCode='"
										+NWCode+"' and SubNetworkCode='"
										+SubNWCode+"' and StationName='"
										+ c.getString(0)
										+ "' ORDER BY ActualEndDate", null);
				if (c1.getCount() > 0) {
					c1.moveToFirst();
					do {
						String column1 = c1.getString(c1.getColumnIndex("ActualEndDate"));
						if (checkOverdue(column1)){
							overduecnt = overduecnt + 1;
						}
						stncnt = stncnt + 1;

					} while (c1.moveToNext());
				}
				if (!Type.trim().equalsIgnoreCase("")) {

					com.stavigilmonitoring.DmCstnwiseActivity.StateList sitem = new com.stavigilmonitoring.DmCstnwiseActivity.StateList();
					sitem.SetStateName(Type);
					sitem.SetCount(stncnt);
					sitem.SetOverdueCnt(overduecnt);
				}

				txt_wrkassncnt.setText(overduecnt +"/"+stncnt);

			} while (c.moveToNext());


		}else {

		}
	}

	public void getDMCertificateCnt(){
		sql = db.getWritableDatabase();

		com.stavigilmonitoring.DmCstnwiseActivity.StateList sitem = new DmCstnwiseActivity.StateList();

		Cursor c = sql.rawQuery(
				"SELECT Distinct StationName FROM DmCertificateTable WHERE StationName='"
						+InstDesc+"' ORDER BY ActualEndDate", null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			do {
				int stncnt = 0,overduecnt=0;
				String Type = c.getString(0);

				Cursor c1 = sql
						.rawQuery(
								"SELECT SoNumber,ActualEndDate FROM DmCertificateTable WHERE NetworkCode='"
										+NWCode+"' and SubNetworkCode='"+SubNWCode+"' and StationName='"
										+ c.getString(0)
										+ "' ORDER BY ActualEndDate", null);
				if (c1.getCount() > 0) {
					c1.moveToFirst();
					do {
						String column1 = c1.getString(c1.getColumnIndex("ActualEndDate"));
						if (checkOverdue(column1)){
							overduecnt = overduecnt + 1;
						}
						stncnt = stncnt + 1;

					} while (c1.moveToNext());
				}

				if (!Type.trim().equalsIgnoreCase("")) {

					sitem.SetStateName(Type);
					sitem.SetCount(stncnt);
					sitem.SetOverdueCnt(overduecnt);
				}

				txt_dmcertfctcnt.setText(overduecnt +"/"+stncnt);

			} while (c.moveToNext());
		}
	}

	private boolean checkOverdue(String amcExpireDt) {
		String[] parts = amcExpireDt.split("T");
		amcExpireDt = parts[0];
		boolean result = false;
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date2 = dateFormat2.parse(amcExpireDt);
			Date date = new Date();
			String res = dateFormat2.format(date);
			date = dateFormat2.parse(res);
			if (date2.equals(date)){
				result = false;
			} else if (date.after(date2)){
				result = true;
			} else if (date2.after(date)){
				result = false;
			}
		}catch( Exception e){
			e.printStackTrace();
		}
		return result;
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

				/*
				 * CallList.setAdapter(new
				 * CallListAdapter(ConnectionStatus.this, lstCall));
				 */

			} while (c2.moveToNext());

			c2.close();
			sql.close();
			//db.close();
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
		String SetItem;

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

		public String getSetItem() {
			return SetItem;
		}

		public void setSetItem(String setItem) {
			SetItem = setItem;
		}

	}*/

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		if (!((searchResult.get(info.position).getName())
				.equalsIgnoreCase("Click and Hold The Item For More Option"))) {
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

		return true;
	}
}

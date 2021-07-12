package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.PendingClipslistAdapt;
import com.beanclasses.StateList;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PendingClips extends Activity {
	List<StateList> searchResults;
	ImageView iv,btnadd;
	String sop, responsemsg, mobno;
	static DownloadxmlsDataURL_new asyncfetch_csnstate;
	ListView lstcsn;
	String Station;
	static SimpleDateFormat dff;
	static String Ldate;
	com.stavigilmonitoring.utility ut;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pendingclipsdtl);

		searchResults = new ArrayList<StateList>();
		lstcsn = (ListView) findViewById(R.id.lstcsn);
		iv = (ImageView) findViewById(R.id.button_refresh_tvstatus_main);
		btnadd = (ImageView) findViewById(R.id.button_alert_add);
		btnadd.setImageResource(R.drawable.work_assign);
		btnadd.setVisibility(View.VISIBLE);
		Bundle extras = getIntent().getExtras();
		Station = extras.getString("Station");
		TextView tvStation = (TextView) findViewById(R.id.tvpendingclipstitle);
		tvStation.setText(Station);

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

		Log.e("Downtime...", " dbval : " + dbvalue());

		if (dbvalue()) {
			updatelist();

		} else if (isnet()) {
			fetchdata();

		} else {
			showD("nonet");
		}

		lstcsn.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Intent i=new
				// Intent(PendingClipsStateWise.this,TvStatusMain.class);

				// startActivity(i);
			}
		});

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isnet()) {
					asyncfetch_csnstate = null;
					asyncfetch_csnstate = new DownloadxmlsDataURL_new();
					asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					// fetchdata();
				} else {
					showD("nonet");
				}
			}
		});

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
			String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetListOfPendingDownloadingAdvertisment?Mobile="
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
					Log.e("All Station data...",
							" fetch data : " + nl1.getLength());
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
					showD("nonet");
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
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String AdsDsce, AdsCode, AddedDate, Status,clrDate;
		String AddeddateSplit,clrDatesplit;
		Cursor c1 = sql.rawQuery(
				"SELECT * FROM PendingClips WHERE InstallationDesc='" + Station
						+ "' ORDER BY FileName DESC", null);
		c1.moveToFirst();
		do {

			Log.e("Pending n/w count", "" + c1.getCount());
			int Count = c1.getCount();
			AdsDsce = c1.getString(c1.getColumnIndex("AdvertisementDesc"));
			AdsCode = c1.getString(c1.getColumnIndex("FileName"));
			int index = AdsCode.indexOf(".");
			AdsCode = AdsCode.substring(0, index);
			AddedDate = c1.getString(c1.getColumnIndex("AddedDT"));
			AddeddateSplit = Split(AddedDate);
			clrDate = c1.getString(c1.getColumnIndex("CLR"));
			clrDatesplit = Split(clrDate);
			Status = c1.getString(c1.getColumnIndex("IsTransfer"));

			

			StateList s = new StateList();
			s.SetAdsCode(AdsCode);
			s.SetAdsDesc(AdsDsce);
			s.SetAddedDate(AddeddateSplit);
            s.setClrDate(clrDatesplit);
			s.SetStatus(Status);
			searchResults.add(s);
			Count = 0;

		} while (c1.moveToNext());

		lstcsn.setAdapter(new PendingClipslistAdapt(PendingClips.this,
				searchResults));

	}
	public String Split(String AddedDate){
		try {
			String datestr = AddedDate.substring(0, AddedDate.indexOf(" "));
			int a = AddedDate.indexOf(" ");

			// DateFormat formatter;
			String date;
			Date date1;
			SimpleDateFormat dateformatyyyyMMdd = new SimpleDateFormat(
					"MM/dd/yyyy");
			date1 = dateformatyyyyMMdd.parse(datestr);

			SimpleDateFormat formatter = new SimpleDateFormat(
					"dd MMM yyyy");
			date = formatter.format(date1);

			AddedDate = date + " "
					+ AddedDate.substring(AddedDate.indexOf(" "));
			// fdhr=fd;
		} catch (Exception e) {
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
		return AddedDate;
		
	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(PendingClips.this);
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
			txt.setText("No Refresh Data Available.Please check internet connection...");
		}

		Button btn = (Button) myDialog
				.findViewById(R.id.gotobtndialoginfosmall);
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

	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		/*Intent i = new Intent(getBaseContext(), PendingClipsMain.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getBaseContext().startActivity(i);
		finish();*/

	}

	/*class StateList {
		String AdsDesc, AdsCode, AddedDate,clrDate, Status;

		public String getClrDate() {
			return clrDate;
		}

		public void setClrDate(String clrDate) {
			this.clrDate = clrDate;
		}

		public StateList() {
		}

		public String GetAdsDesc() {
			return AdsDesc;
		}

		public void SetAdsDesc(String s) {
			this.AdsDesc = s;
		}

		public String GetAdsCode() {
			return AdsCode;
		}

		public void SetAdsCode(String s) {
			this.AdsCode = s;
		}

		public void SetAddedDate(String s) {
			this.AddedDate = s;
		}

		public String GetAddedDate() {
			return AddedDate;
		}

		public void SetStatus(String s) {
			this.Status = s;
		}

		public String GetStatus() {
			return Status;
		}

	}*/

}

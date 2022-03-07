package com.stavigilmonitoring;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.StationEnqworkHistryAdaptor;
import com.beanclasses.StationEnquiryworkBean;
import com.database.DBInterface;
import com.squareup.picasso.Picasso;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class StationEnquiryWorkHistory extends AppCompatActivity  {
	private ArrayList<StationEnquiryworkBean> searchResults;
	private ImageView mRefresh;
	private ListView mListView;
	private ProgressBar mProgressBar;
	private LinearLayout mAllnet;
	private TextView mText;
	private TextView mAllCount;
	private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	static SimpleDateFormat dff;
	static String Ldate;
	private String mobno, type, conn;
	static DownloadxmlsDataURL_new asynk;
	String responsemsg, Syncdate, sop, urlnet,Actualremark, fileUrl="NA";
	int scount = 0;
	private StationEnqworkHistryAdaptor adapter;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.stationenquiryworkhistory);

		mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.buttn_refresh_work_his);
		mProgressBar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar_work);
		mText = (TextView) findViewById(com.stavigilmonitoring.R.id.work_Station);
		mListView = (ListView) findViewById(com.stavigilmonitoring.R.id.worklist);
		searchResults = new ArrayList<StationEnquiryworkBean>();

		DBInterface dbi = new DBInterface(getApplicationContext());
		//mobno = dbi.GetPhno();
		mobno = "0";
		dbi.Close();

		Intent i = getIntent();
		conn = i.getStringExtra("stnname");
		type = i.getStringExtra("stninst");
		mText.append(" " + conn);
		db = new DatabaseHandler(getApplicationContext());

		if (dbvalue()) {
			updatelist();
		} else if (ut.isnet(getApplicationContext())) {
			fetchdata();
		} else {
			ut.showD(StationEnquiryWorkHistory.this, "nonet");
		}

		mRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ut.isnet(getApplicationContext())) {
					fetchdata();
				} else {
					try{
						ut.showD(StationEnquiryWorkHistory.this, "nonet");
					}catch (Exception e){
						e.printStackTrace();
					}
				}

			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void updatelist() {
		// TODO Auto-generated method stub
		searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql
				.rawQuery(
						"SELECT DISTINCT WorkType,Remarks,MaterialName,StationName,Mobileno,currentDate,remarksMaterial,currentLocation,ActivityName FROM WorkTypeHistory WHERE StationName='"+conn+"' ORDER BY currentDate desc", null);
		
		//c.moveToFirst();
		int cnt = 0;
		Log.e("Pending n/w count", "" + c.getCount());
		if (c.getCount() == 0) {

			c.close();
		} else {
			c.moveToFirst();
			do {
				fileUrl = "NA";

				String Status = c.getString(c.getColumnIndex("WorkType"));
				String mob = c.getString(c.getColumnIndex("Remarks"));
				if (mob.contains("WDIMG")){
					//holder.img.setVisibility(View.VISIBLE);
					Actualremark = mob;
					String[] parts = mob.split("/");
					mob = parts[0];
					fileUrl = parts[2];
				}
				String qty = c.getString(c.getColumnIndex("MaterialName"));
				String MaterialName = c.getString(c
						.getColumnIndex("StationName"));
				String reson = c.getString(c.getColumnIndex("Mobileno"));
				String stationname = c.getString(c
						.getColumnIndex("currentDate"));
				String s = split(stationname); 
				String reporty = c.getString(c.getColumnIndex("remarksMaterial"));
				String date = c.getString(c.getColumnIndex("currentLocation"));
				String sender = c.getString(c.getColumnIndex("ActivityName"));
				

				StationEnquiryworkBean bean = new StationEnquiryworkBean();

				bean.setWorktype(Status);
				bean.setMaterialname(qty);
				bean.setWorkRemark(mob);
				bean.setFile_url(fileUrl);
				bean.setMatremark(reporty);
				bean.setDate(s);
				bean.setMobno(reson);
				bean.setTimesheetActivity(sender);
				bean.setLocation(date);
				
				searchResults.add(bean);

			} while (c.moveToNext());
		}

		adapter = new StationEnqworkHistryAdaptor(StationEnquiryWorkHistory.this, searchResults);
		mListView.setAdapter(adapter);

	}
	private String split(String data) {
		// TODO Auto-generated method stub
		if (data == null || data.equalsIgnoreCase("")) {
			return "";

		} else {
			data = data.replace("T", " ");
			String s = data.substring(0,data.indexOf("+"));
			
			Date conn = null;
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");// 2016-05-12T20:36:08+05:30

				conn = dateFormat.parse(s);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SimpleDateFormat dateFormat1 = new SimpleDateFormat(
					"dd MMM yyyy  hh:mm:ss aa");
			String dat = dateFormat1.format(conn);

			return dat;
		}
}

	private void fetchdata() {
		// TODO Auto-generated method stub
		asynk = null;
		asynk = new DownloadxmlsDataURL_new();
		asynk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private boolean dbvalue() {
		// TODO Auto-generated method stub

		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery(
					"SELECT  * FROM WorkTypeHistory where InstallationId='"+type+"'", null);

			System.out.println("----------  dbvalue screen cursor count -- "
					+ cursor.getCount());

			if (cursor != null && cursor.getCount() > 0) {
				cursor.close();
				return true;

			} else {

				cursor.close();
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

	/*private void loadImageFromUrl( final String Url){
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.img_layout, null);


				//this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

				AlertDialog.Builder myDialog = new AlertDialog.Builder(context);
				//final Dialog myDialog = new Dialog(context);
				//myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				//myDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		myDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
				myDialog.setView(promptsView);
				myDialog.setCancelable(true);
				myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialogInterface) {
						//nothing;
					}
				});

				final ImageView imageView  = (ImageView) promptsView
						.findViewById(R.id.imagedisp);
				Picasso.with(context).load(Url).placeholder(R.drawable.stationenqu)
						.error(R.drawable.alertimg)
						.into(imageView,new com.squareup.picasso.Callback(){

							@Override
							public void onSuccess() {

							}

							@Override
							public void onError() {

							}
						});
				AlertDialog alertDialog = myDialog.create();
				alertDialog.show();
			}
		});

	}*/

	public void toggle_icon_received_status(final String Url) {

		// TODO Auto-generated method stub
		final Dialog myDialog = new Dialog(StationEnquiryWorkHistory.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		myDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.BLACK));
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.img_layout);
		myDialog.setCancelable(true);
		myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialogInterface) {
				//nothing;
			}
		});

		final ImageView imageView  = (ImageView) myDialog.findViewById(com.stavigilmonitoring.R.id.imagedisp);
		Picasso.with(StationEnquiryWorkHistory.this).load(Url).placeholder(com.stavigilmonitoring.R.drawable.progressanimation)
				.error(com.stavigilmonitoring.R.drawable.no_image)
				.into(imageView,new com.squareup.picasso.Callback(){

					@Override
					public void onSuccess() {

					}

					@Override
					public void onError() {

					}
				});

		myDialog.show();

	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/getWorkDoneHistory?mobileno="
					+mobno+"&stationname="+conn;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = com.stavigilmonitoring.utility.httpGet(url);
				Log.e("csn status", "resmsg : " + responsemsg);
				//sql.execSQL("DROP TABLE IF EXISTS WorkTypeHistory");
				//sql.execSQL(ut.getWorkTypeHistory());
				sql.delete("WorkTypeHistory",null,null);

				Cursor cur = sql
						.rawQuery("SELECT * FROM WorkTypeHistory", null);
				Log.e("Counr----------", "" + cur.getCount());

				if (responsemsg.contains("<WorkTypeMasterId>")) {
					sop = "valid";
					String columnName, columnValue;
					/*
					 * DatabaseHandler db = new
					 * DatabaseHandler(getBaseContext()); SQLiteDatabase sql =
					 * db.getWritableDatabase();
					 */

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
							if(columnValue==null||columnValue.equalsIgnoreCase("")||columnValue.equalsIgnoreCase("null")){
								columnValue="No Info";
							}
							values1.put(columnName, columnValue);

							// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
						}
						sql.insert("WorkTypeHistory", null, values1);
					}

					cur.close();

				} else {
					sop = "invalid";
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

			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				if (sop == "valid") {
					updatelist();
				} else {
					ut.showD(StationEnquiryWorkHistory.this, "nodata");
				}
				mRefresh.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);

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
			mRefresh.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		}

	}
}

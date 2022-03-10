package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.LmsConnectionStatewiseAdapter;
import com.beanclasses.LmsConnectionStatewiseBean;

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
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LmsConnectionStateWise extends Activity {

  private ImageView mRefresh;
  private ListView mListView;
  private ProgressBar mProgress;
  private com.stavigilmonitoring.utility mUtil;
  private Context parent;
  static SimpleDateFormat dff;
  static String Ldate;
  static DownloadLmsConn mLmsConn;
  String responsemsg;
  String Postckeck;
  List<LmsConnectionStatewiseBean> searchResults;
  int stncnt;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.lmsconnectionstatewise);

		parent= LmsConnectionStateWise.this;

		mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_connection_lms);
		mProgress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.Connectionlmsprog);
		mListView = (ListView) findViewById(com.stavigilmonitoring.R.id.listconnectionlms);
		mUtil = new com.stavigilmonitoring.utility();
		searchResults = new ArrayList<LmsConnectionStatewiseBean>();
		db = new DatabaseHandler(getApplicationContext());


		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		if (dbvalue()) {
			updatelist();
		} else if (mUtil.isnet(parent)) {
			fetchdata();
		} else {
			
			mUtil.showD(parent, "nonet");
		}
		
		mRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			mLmsConn = null;
			mRefresh.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);

			Log.e("async", "null");
			mLmsConn = new DownloadLmsConn();
			mLmsConn.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				
			}
		});
		
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(getApplicationContext(),LmsConnectionStatusMain.class);
				intent.putExtra("NetWorkType",searchResults.get(position).getNetcode());
				startActivity(intent);
				
			}
		});
	}
	private boolean dbvalue() {
		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery(
					"SELECT *   FROM LmsConnectionStatus", null);
			if (cursor != null && cursor.getCount() > 0) {
				if (cursor.getColumnIndex("NetworkCode") < 0) {
					cursor.close();
					return false;
				} else {
					cursor.close();
					return true;
				}
			} else {
				cursor.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
			dff = new SimpleDateFormat("HH:mm:ss");
			Ldate = dff.format(new Date());

			StackTraceElement l = new Exception().getStackTrace()[0];
			System.out.println(l.getClassName() + "/" + l.getMethodName() + ":"
					+ l.getLineNumber());
			
			if (!mUtil.checkErrLogFile()) {

				mUtil.ErrLogFile();
			}
			if (mUtil.checkErrLogFile()) {
				mUtil.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":"
						+ l.getLineNumber() + "	" + e.getMessage() + " "
						+ Ldate);
			}

		}
		return false;
	}

	private void fetchdata() {
		 //new DownloadxmlsDataURL_new().execute();
		//new DownloadLmsConn().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		if (mLmsConn == null) {
			mRefresh.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);

			Log.e("async", "null");
			mLmsConn = new DownloadLmsConn();
			mLmsConn.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		} else {
			if (mLmsConn.getStatus() == AsyncTask.Status.RUNNING) {
				Log.e("async", "running");
				mRefresh.setVisibility(View.GONE);
				mProgress.setVisibility(View.VISIBLE);
				Toast.makeText(getApplicationContext(),"Sorry! Data is not available", Toast.LENGTH_SHORT).show();
			}
		}

	}
	
	
	private void updatelist() {
		searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor c = sql.rawQuery(
				"SELECT DISTINCT NetworkCode FROM LmsConnectionStatus ORDER BY NetworkCode", null);
		int con = c.getCount();
	//	String colum = c.getString(c.getColumnIndex("NetworkCode"));
		if (c.getCount() > 0) {
			c.moveToFirst();
			do {
				 stncnt = 0;
				String Type = c.getString(0);

				Cursor c1 = sql
						.rawQuery(
								"SELECT LastConnectionTime  FROM LmsConnectionStatus WHERE NetworkCode='"
										+ c.getString(0)
										+ "' ORDER BY NetworkCode Desc", null);
				if (c1.getCount() > 0) {
					c1.moveToFirst();
					do {
						String column1 = c1.getString(c1.getColumnIndex("LastConnectionTime"));
						// String[] tym = splitfromtym(c1.getString(column1));
						try {
							String s = column1.substring(0, column1.indexOf("."));
							Calendar cal = Calendar.getInstance();
							SimpleDateFormat format = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							
							Date Startdate = format.parse(s);
							Date Enddate = cal.getTime();
							long diff = Enddate.getTime() - Startdate.getTime();
							long diffSeconds = diff / 1000 % 60;
							long diffMinutes = diff / (60 * 1000) % 60;
							long diffHours = diff / (60 * 60 * 1000) % 24;
							long diffDays = diff / (24 * 60 * 60 * 1000);

							if (diffDays == 0 && diffHours == 0
									&& diffMinutes <= 30) {

							} else {
								stncnt = stncnt + 1;
							}
						} catch (Exception ex) {
							dff = new SimpleDateFormat("HH:mm:ss");
							Ldate = dff.format(new Date());

							StackTraceElement l = new Exception()
									.getStackTrace()[0];
							System.out.println(l.getClassName() + "/"
									+ l.getMethodName() + ":"
									+ l.getLineNumber());
							
							if (!mUtil.checkErrLogFile()) {

								mUtil.ErrLogFile();
							}
							if (mUtil.checkErrLogFile()) {
								mUtil.addErrLog(l.getClassName() + "/"
										+ l.getMethodName() + ":"
										+ l.getLineNumber() + "	"
										+ ex.getMessage() + " " + Ldate);
							}
						}

					} while (c1.moveToNext());
					
					c1.close();
				}
				//Type = Type.replaceAll("0", "");
				//Type = Type.replaceAll("1", "");
				if (!Type.trim().equalsIgnoreCase("")) {
					LmsConnectionStatewiseBean sitem= new LmsConnectionStatewiseBean();
					sitem.setNetcode(Type);
					sitem.setNetcodeCount(stncnt);
					searchResults.add(sitem);
				}
			} while (c.moveToNext());

			c.close();

		}
		mListView.setAdapter(new LmsConnectionStatewiseAdapter(LmsConnectionStateWise.this,
				searchResults));
		
		int scount = 0;
		for (int i = 0; i < searchResults.size(); i++){
			scount = scount + searchResults.get(i).getNetcodeCount();
		}
		
		SharedPreferences preflmsconn = getApplicationContext()
				.getSharedPreferences("PrefLmsCount",
						Context.MODE_PRIVATE);
		Editor editorlmsConne = preflmsconn.edit();
		
		editorlmsConne.putString("LmsCount", scount+"");

		editorlmsConne.commit();

	}
	
	class DownloadLmsConn extends AsyncTask<String, Void, String>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mRefresh.setVisibility(View.GONE);
			mProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
           // DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();

			String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetCSNForWebLMS";

			Log.e("Lms status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = mUtil.httpGet(url);
				Log.e("csn status", "resmsg : " + responsemsg);
				//sql.execSQL("DROP TABLE IF EXISTS LmsConnectionStatus");
				//sql.execSQL(mUtil.getLmsConnectionStatus());
				sql.delete("LmsConnectionStatus",null,null);

            if (responsemsg.contains("<Table>")) {
					Postckeck = "valid";
					String columnName, columnValue;/****/
				    Cursor cur = sql.rawQuery(
							"SELECT *   FROM LmsConnectionStatus", null);
				    int lmscount = cur.getCount();
				    Log.e("lmscount","lmscount"+lmscount);/******/
					ContentValues values1 = new ContentValues();
					NodeList nl1 = mUtil.getnode(responsemsg, "Table");
					Log.e("sts main...", " fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						String connlms = "invalid";
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);
                            columnValue = mUtil.getValue(e, columnName);

							if (columnName.equalsIgnoreCase("LastConnectionTime")) {
								try {
									String val = mUtil.getValue(e, "LastConnectionTime");
									String s = val.substring(0, val.indexOf("."));
									Calendar cal = Calendar.getInstance();
									SimpleDateFormat format = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss");
									Date Startdate = format.parse(val);
									Date Enddate = cal.getTime();
									long diff = Enddate.getTime()
											- Startdate.getTime();
									long diffSeconds = diff / 1000 % 60;
									long diffMinutes = diff / (60 * 1000) % 60;
									long diffHours = diff / (60 * 60 * 1000)
											% 24;
									long diffDays = diff
											/ (24 * 60 * 60 * 1000);

									Log.e("getdetails", "sd : " + Startdate
											+ " ed: " + Enddate + " d: "
											+ diffDays + " h: " + diffHours
											+ " m:" + diffMinutes);
									

									if (diffDays == 0 && diffHours == 0
											&& diffMinutes <= 30) {
										connlms = "Invalid";
									} else {
										connlms = "valid";
									}
								} catch (Exception ex) {
									dff = new SimpleDateFormat("HH:mm:ss");
									Ldate = dff.format(new Date());

									StackTraceElement l = new Exception()
											.getStackTrace()[0];
									System.out.println(l.getClassName() + "/"
											+ l.getMethodName() + ":"
											+ l.getLineNumber());
									mUtil = new com.stavigilmonitoring.utility();
									if (!mUtil.checkErrLogFile()) {

										mUtil.ErrLogFile();
									}
									if (mUtil.checkErrLogFile()) {
										mUtil.addErrLog(l.getClassName() + "/"
												+ l.getMethodName() + ":"
												+ l.getLineNumber() + "	"
												+ ex.getMessage() + " " + Ldate);
									}

								}
							}

							values1.put(columnName, columnValue);

							// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
						}
						
						Log.e("ckeck......", connlms);
						
						if (connlms.equalsIgnoreCase("valid")){
							long inst = sql.insert("LmsConnectionStatus", null, values1);
						}
					}

					cur.close();

				} else {
					Postckeck = "invalid";
					

					System.out
							.println("--------- invalid for project list --- ");
				}

			} catch (IOException e) {
				e.printStackTrace();
				dff = new SimpleDateFormat("HH:mm:ss");
				Ldate = dff.format(new Date());

				StackTraceElement l = new Exception().getStackTrace()[0];
				System.out.println(l.getClassName() + "/" + l.getMethodName()
						+ ":" + l.getLineNumber());
				mUtil = new utility();
				if (!mUtil.checkErrLogFile()) {

					mUtil.ErrLogFile();
				}
				if (mUtil.checkErrLogFile()) {
					mUtil.addErrLog(l.getClassName() + "/" + l.getMethodName()
							+ ":" + l.getLineNumber() + "	" + e.getMessage()
							+ " " + Ldate);
				}
			}

			return Postckeck;
		
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			try {

				if (Postckeck.equals("valid")) {
					updatelist();
				} else {
					mUtil.showD(parent,"invalid");
					Toast.makeText(getApplicationContext(),"Sorry ! Data is not availbale", Toast.LENGTH_SHORT);
				}
				mRefresh.setVisibility(View.VISIBLE);
				mProgress.setVisibility(View.GONE);

		
				
			} catch (Exception e) {
				e.printStackTrace();
				dff = new SimpleDateFormat("HH:mm:ss");
				Ldate = dff.format(new Date());

				StackTraceElement l = new Exception().getStackTrace()[0];
				System.out.println(l.getClassName() + "/" + l.getMethodName()
						+ ":" + l.getLineNumber());
				
				if (!mUtil.checkErrLogFile()) {

					mUtil.ErrLogFile();
				}
				if (mUtil.checkErrLogFile()) {
					mUtil.addErrLog(l.getClassName() + "/" + l.getMethodName()
							+ ":" + l.getLineNumber() + "	" + e.getMessage()
							+ " " + Ldate);
				}
			}
		}
	}
	@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();
			/*Intent i = new Intent(getBaseContext(), SelectMenu.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getBaseContext().startActivity(i);*/
			finish();
		}
	
}

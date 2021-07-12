package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.TvStatusMainAdpt;
import com.beanclasses.TvStatusStateList;
import com.database.DBInterface;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TvStatusMain extends Activity {

	String Type, StationName, CallFrom;
	String mobno, link;
	AsyncTask depattask;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	static DownloadxmlsDataURL asyncfetch_csnstate;
	String responsemsg = "k";
	String ActivityName, ActivityId, actname,subType;
	static SimpleDateFormat dff;
	static String Ldate;
	private TextView txtdaterefresh;
	String daterestr;
	TvStatusMainAdpt listAdapter;
	ArrayList<TvStatusStateList> arrlist = new ArrayList<TvStatusStateList>();
	TextView title;
	ImageView iv;
	private ListView sound;
	DatabaseHandler db;

	View sheetview;
	LinearLayout laytvstatus;
	static Button btmsheetedsationname;
	TextView txt_stn_name_btmsht;
	static TextView txtpass;
	int requestCode = 11;
	ProgressDialog progressdialogupdateserver;
	AsyncTask refreshasyncupdateserver;
	String reasonCode = "", reasonDesc = "";
	private String installationId;
	Button edt_btmsht_reasonname, btn_tvstatressave,btn_tvstatrescancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tvstatusmain);

		sound = (ListView) findViewById(R.id.soundlevelmainlist);
		iv = (ImageView) findViewById(R.id.button_refresh_connection_main);
		title = (TextView) findViewById(R.id.title);
		laytvstatus = findViewById(R.id.laytvstatus);
		laytvstatus.setVisibility(View.VISIBLE);

		Intent intent = getIntent();

		Type = intent.getStringExtra("Type");//SubType
		subType = intent.getStringExtra("SubType");
		title.setText("TV Status - "+ subType);
		CallFrom = intent.getStringExtra("CallFrom");

		db = new DatabaseHandler(TvStatusMain.this);
		DBInterface dbi = new DBInterface(TvStatusMain.this);
		mobno = dbi.GetPhno();

		if (asyncfetch_csnstate != null
				&& asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

		if (dbvalue()) {
			updatelist();
			// fetchdata();
		} else if (ut.isnet(getApplicationContext())) {
			fetchdata();
		} else {
			ut.showD(TvStatusMain.this,"nonet");
		}

		dbi.Close();

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (ut.isnet(getApplicationContext())) {
					asyncfetch_csnstate = null;
					asyncfetch_csnstate = new DownloadxmlsDataURL();
					asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					ut.showD(TvStatusMain.this,"nonet");
				}
			}
		});

		if(CallFrom.equalsIgnoreCase("SupporterList")){
			//Toast.makeText(parent,"Not clickable",Toast.LENGTH_SHORT).show();
			sound.setClickable(false);
		}else if(CallFrom.equalsIgnoreCase("TVStatusStateFilter")){
			sound.setClickable(true);
			sound.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> a, View v, int position,
										long id) {

					/*Intent i = new Intent(getApplicationContext(),
							TVStatusfillReason.class);
					i.putExtra("StationName", arrlist.get(position).GetStateName());
					i.putExtra("Type", Type);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
					//	finish();

					*//*
					 * Object o = connectionstatus.getItemAtPosition(position);
					 * ConnectionstatusHelper fullObject = (ConnectionstatusHelper)
					 * o; editActivity(fullObject.getinstallationId());
					 */

					final BottomSheetDialog btmsheetdialog = new BottomSheetDialog(TvStatusMain.this);
					sheetview = getLayoutInflater().inflate(R.layout.bottomsheet_tvstatusfillreason, null);
					btmsheetdialog.setContentView(sheetview);
					btmsheetdialog.show();
					btmsheetdialog.setCanceledOnTouchOutside(false);

					edt_btmsht_reasonname = sheetview.findViewById(R.id.edt_btmsht_reasonname);
					btn_tvstatressave = sheetview.findViewById(R.id.btn_tvstatressave);
					btn_tvstatrescancel = sheetview.findViewById(R.id.btn_tvstatrescancel);
					txt_stn_name_btmsht = sheetview.findViewById(R.id.txt_stn_name_btmsht);

					StationName = arrlist.get(position).GetStateName();
					txt_stn_name_btmsht.setText("Fill TV status reason of  "+ StationName +" here...");

					edt_btmsht_reasonname.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							//get reasons list
							InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

							if(TvStatusMain.this.getCurrentFocus() != null)
							{
								imm.hideSoftInputFromWindow(TvStatusMain.this.getCurrentFocus().getWindowToken(), 0);
							}else {
								//Toast.makeText(SelectMenu.this,"Token is null",Toast.LENGTH_SHORT).show();
							}

							Intent intent = new Intent(TvStatusMain.this,TvStatusReasonList.class);
							intent.putExtra("StationName", StationName);
							startActivityForResult(intent, requestCode);
						}
					});

					btn_tvstatressave.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							//save status
							progressdialogupdateserver = ProgressDialog.show(
									TvStatusMain.this, "Update Reason.......",
									"Please Wait....", true, true, new DialogInterface.OnCancelListener() {

										public void onCancel(DialogInterface dialog) {
											// TODO Auto-generated method stub
											if (refreshasyncupdateserver != null
													&& refreshasyncupdateserver.getStatus() != AsyncTask.Status.FINISHED) {
												refreshasyncupdateserver.cancel(true);
											}
										}
									});

							refreshasyncupdateserver = new Updatetoserver_TVStatus().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

							btmsheetdialog.dismiss();
						}
					});

					btn_tvstatrescancel.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							btmsheetdialog.dismiss();
						}
					});

				}
			});
		}


		((EditText) findViewById(R.id.edfitertext))
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {

						listAdapter.filter(((EditText) findViewById(R.id.edfitertext))
										.getText().toString().trim()
										.toLowerCase(Locale.getDefault()));
					}
				});

	}

	public void FilterClick(View v) {
		if (((EditText) findViewById(R.id.edfitertext)).getVisibility() == View.VISIBLE) {
			((EditText) findViewById(R.id.edfitertext))
					.setVisibility(View.GONE);
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		} else if (((EditText) findViewById(R.id.edfitertext)).getVisibility() == View.GONE) {
			((EditText) findViewById(R.id.edfitertext))
					.setVisibility(View.VISIBLE);
			EditText textView = (EditText) findViewById(R.id.edfitertext);
			textView.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	private boolean dbvalue() {
		try {
			// TODO Auto-generated method stub
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery(
					"SELECT *   FROM ConnectionStatusUser", null);

			System.out.println("----------  dbvalue screen cursor count -- "
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

	private void updatelist() {
		// TODO Auto-generated method stub

		System.out.println("====#$#$#$#$#$#$  in update list TvStatus");
		final ArrayList<TvStatusStateList> searchResults = getDetail();

		listAdapter = new TvStatusMainAdpt(this, searchResults);
		sound.setAdapter(listAdapter);
		// sound.setAdapter(new TvStatusMainAdpt(this, searchResults));

	}

	public ArrayList<TvStatusStateList> getDetail() {
		arrlist.clear();
		int cnt = 0;

		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		//SELECT DISTINCT ConnectionStatusFilter.SubNetworkCode FROM ConnectionStatusFilter INNER JOIN TvStatus ON ConnectionStatusFilter.InstalationId=TvStatus.InstallationId WHERE TvStatus.Type='"
		//+ Type + "'
		Cursor c1 = sql.rawQuery("select distinct InstallationId from TvStatus", null);

		if (c1.getCount() != 0) {
			Cursor c = sql.rawQuery(
					"Select distinct c1.InstallationId,c2.InstallationDesc,c1.TVStatus,c1.TVStatusReason,c1.flg from TvStatus c1 " +
							" inner join ConnectionStatusFilter c2  on c1.InstallationId=c2.InstalationId where c2.SubNetworkCode='"
									+ subType + "'ORDER BY c2.InstallationDesc ", null);

			c1.moveToFirst();
			c.moveToFirst();

			int column = 0;
			do {
				String s = c.getString(c.getColumnIndex("InstallationDesc"));
				String instId = c.getString(c.getColumnIndex("InstallationId"));

				int i = 0;
				int flag = c.getInt(c.getColumnIndex("flg"));
				String s1 = c.getString(c.getColumnIndex("TVStatus"));
				String reason = c.getString(c.getColumnIndex("TVStatusReason"));

				TvStatusStateList sitem = new TvStatusStateList();

				for (char d : s1.toCharArray()) {
					if (d == '0') {
						cnt++;
					}
				}

				if (!s.trim().equalsIgnoreCase("")&& flag!=0) {//	if (!s.trim().equalsIgnoreCase("") && cnt != 8) {

					sitem.SetStateName(s);
					sitem.SetCount(cnt);
					sitem.Settotaltv(s1);
					sitem.SetTVReason(reason);

					//get data from csn status
					String qry = "Select InstallationId from ConnectionStatusUser WHERE InstallationId='"+instId+"'";
					Cursor chk_c = sql.rawQuery(qry,null);
					if(chk_c.getCount() > 0){
						chk_c.moveToFirst();
						String id = chk_c.getString(chk_c.getColumnIndex("InstallationId"));
						sitem.setCSNStatus("Y");
					}else {
						sitem.setCSNStatus("N");
					}
					arrlist.add(sitem);
				}
				cnt = 0;

			} while (c.moveToNext() && c1.moveToNext());

			c.close();
			sql.close();
			//db.close();
		}
		return arrlist;

	}

	/*private ArrayList<DowntimeHelper> GetDetail() {
		ArrayList<DowntimeHelper> results = new ArrayList<DowntimeHelper>();
		DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM ConnectionStatusUser", null);
		if (c.getCount() == 0) {
			DowntimeHelper sr = new DowntimeHelper();
			sr.setstnname("");

			results.add(sr);

			c.close();
			sql.close();
			//db.close();

			return results;
		} else {

			c.moveToFirst();

			int column = 0;
			do {

				DowntimeHelper sr = new DowntimeHelper();

				sr.setstnname(c.getString(c.getColumnIndex("InstallationId")));

				results.add(sr);

			} while (c.moveToNext());

			c.close();
			sql.close();
			//db.close();
		}
		return results;

	}*/

	private void fetchdata() {
		if (asyncfetch_csnstate == null) {
			iv.setVisibility(View.VISIBLE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.GONE);

			Log.e("async", "null");
			asyncfetch_csnstate = new DownloadxmlsDataURL();
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

	public class DownloadxmlsDataURL extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetTVStatus_Android_new?Mobile="
					+ mobno;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);
				Log.e("csn status", "resmsg : " + responsemsg);

				if (responsemsg.contains("<A>")) {
					sop = "valid";
					String columnName, columnValue;
				//	DatabaseHandler db = new DatabaseHandler(getBaseContext());
					SQLiteDatabase sql = db.getWritableDatabase();

					//sql.execSQL("DROP TABLE IF EXISTS TvStatus");
					//sql.execSQL(ut.getTvStatus());
					sql.delete("TvStatus",null,null);

					Cursor cur = sql.rawQuery("SELECT *   FROM TvStatus", null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table1");
					// String msg = "";
					// String columnName, columnValue;
					Log.e("sts main...", " fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);

							String ncolumnname = "";
							if (columnName.equalsIgnoreCase("InstallationId"))
								ncolumnname = "A";
							else if (columnName.equalsIgnoreCase("TVStatusReason"))
								ncolumnname = "G";
							else if (columnName.equalsIgnoreCase("TVStatus"))
								ncolumnname = "J";
							else if (columnName.equalsIgnoreCase("Type"))
								ncolumnname = "P";
							else
								ncolumnname = columnName;
							columnValue = ut.getValue(e, ncolumnname);
							values1.put(columnName, columnValue);

							Log.e("DownloadxmlsDataURL_new",
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
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// pd.cancel();
			try {
				System.out.println("...............value of sop" + sop);
				if (sop.equals("valid")) {
					updatelist();

				} else {
					ut.showD(TvStatusMain.this,"invalid");
				}

				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.GONE);
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
			// TODO Auto-generated method stub
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = getApplicationContext()
				.getSharedPreferences("SCROLLSOUND", Context.MODE_PRIVATE);
		int scroll = preferences.getInt("ScrollValueSound", 0);
		System.out
				.println(".............value of scroll at resume>>>>>>>>>>>>>>>>>>>"
						+ scroll);
		// connectionstatus.scrollTo(0, scroll);
		sound.smoothScrollToPosition(scroll);
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SharedPreferences preferences = getApplicationContext()
				.getSharedPreferences("SCROLLSOUND", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		int scroll = sound.getFirstVisiblePosition();
		// int scrollx = connectionstatus.getScrollX();
		System.out.println(".............value of scroll>>>>>>>>>>>>>>>>>>>"
				+ scroll);
		editor.putInt("ScrollValueSound", scroll);
		editor.commit();

		// finish();
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		/*Intent i = new Intent(getBaseContext(), TvStatusStateWise.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getBaseContext().startActivity(i);
		finish();*/
		finish();
	}

	class Updatetoserver_TVStatus extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... paramss) {
			try {
				//DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
				SQLiteDatabase sqldb = db.getWritableDatabase();

				Cursor c2 = sqldb.rawQuery(
						"SELECT * FROM TVStatusReason where ReasonDescription='"
								+ reasonDesc + "' ", null);

				reasonCode = "";
				if (c2.getCount() == 0) {
					c2.close();
					//db.close();
					//db1.close();
				} else {
					c2.moveToFirst();
					reasonCode = c2.getString(c2.getColumnIndex("ReasonCode"));
					System.out
							.println("...................reason code value is.............."
									+ reasonCode);
					c2.moveToLast();
					c2.close();
					//db.close();
					//db1.close();

				}
			} catch (Exception e) {
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
			try {

				//DatabaseHandler db2 = new DatabaseHandler(getApplicationContext());
				SQLiteDatabase dbf = db.getWritableDatabase();

				Cursor cf = dbf.rawQuery("SELECT * FROM ConnectionStatusUser1 where InstallationDesc='"
						+ StationName + "' ", null);
				if (cf.getCount() == 0) {

				} else {
					cf.moveToFirst();
					installationId = cf.getString(cf.getColumnIndex("InstallationId"));

					cf.moveToLast();


				}
			} catch (Exception e) {
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
			Log.e("Details", "" + installationId + "" + reasonCode + ""
					+ reasonDesc + "" + mobno);
			if (ut.isnet(getApplicationContext())) {
				String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/UpdateTVStatusReason_Android?InstallationId="
						+ installationId
						+ "&MobileNo="
						+ mobno
						+ "&ReasonCode="
						+ reasonCode
						+ "&ReasonDesc="
						+ reasonDesc;
				url = url.replaceAll(" ", "%20");
				try {
					responsemsg = ut.httpGet(url);
				} catch (NullPointerException e) {
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
						ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
								+ ":" + l.getLineNumber() + "	"
								+ e.getMessage() + " " + Ldate);
					}

				} catch (IOException e) {
					e.printStackTrace();
					responsemsg = "wrong" + e.toString();
					dff = new SimpleDateFormat("HH:mm:ss");
					Ldate = dff.format(new Date());

					StackTraceElement l = new Exception().getStackTrace()[0];
					System.out.println(l.getClassName() + "/"
							+ l.getMethodName() + ":" + l.getLineNumber());
					ut = new utility();
					if (!ut.checkErrLogFile()) {

						ut.ErrLogFile();
					}
					if (ut.checkErrLogFile()) {
						ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
								+ ":" + l.getLineNumber() + "	"
								+ e.getMessage() + " " + Ldate);
					}

				}
			} else {
				ut.showD(TvStatusMain.this,"nonet");
			}

			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (responsemsg.contains("Added")) {

				new DownloadxmlsDataURL_TVStatus().executeOnExecutor(THREAD_POOL_EXECUTOR);

			} else {
				Toast.makeText(getApplicationContext(), "Server Error..",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public class DownloadxmlsDataURL_TVStatus extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetTVStatus_Android_new?Mobile="
					+ mobno;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);
				Log.e("csn status", "resmsg : " + responsemsg);

				if (responsemsg.contains("<A>")) {
					sop = "valid";
					String columnName, columnValue;
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());
					SQLiteDatabase sql = db.getWritableDatabase();

					//sql.execSQL("DROP TABLE IF EXISTS TvStatus");
					//sql.execSQL(ut.getTvStatus());
					sql.delete("TvStatus",null,null);

					Cursor cur = sql.rawQuery("SELECT *   FROM TvStatus", null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table1");
					// String msg = "";
					// String columnName, columnValue;
					Log.e("sts main...", " fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);

							String ncolumnname = "";
							if (columnName.equalsIgnoreCase("InstallationId"))
								ncolumnname = "A";
							else if (columnName
									.equalsIgnoreCase("TVStatusReason"))
								ncolumnname = "G";
							else if (columnName.equalsIgnoreCase("TVStatus"))
								ncolumnname = "J";
							else if (columnName.equalsIgnoreCase("Type"))
								ncolumnname = "P";

							columnValue = ut.getValue(e, ncolumnname);
							values1.put(columnName, columnValue);

							// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
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

			progressdialogupdateserver.cancel();

			if (result.equalsIgnoreCase("valid")) {
				Toast.makeText(getApplicationContext(),
						"Reason Updated Successfully..!", Toast.LENGTH_LONG)
						.show();
				//finish();

			} else {

			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			reasonDesc = data.getExtras().getString("ReasonDesc");
			edt_btmsht_reasonname.setText(reasonDesc);
		}
	}

}

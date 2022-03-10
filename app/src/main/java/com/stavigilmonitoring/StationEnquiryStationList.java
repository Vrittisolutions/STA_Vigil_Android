package com.stavigilmonitoring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.StationEnquiryStnAdap;
import com.beanclasses.StateList;
import com.database.DBInterface;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class StationEnquiryStationList extends Activity {
	private static DownloadStation mDownloadStation;
	private String resposmsg;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	private TextView mTextview,mTextHeader;
	private ImageView mImage;
	private ProgressBar mProgress;
	private ImageView mImageFilter;
	private GridView mListView;
	private String sop, Ldate, dff;
	ArrayList<StateList> searchResults;
	private StationEnquiryStnAdap StationAdaptor;
	private String filter,mobno;
	private String mType,network;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.stationenquirystationlist);

		mTextview = (TextView) findViewById(com.stavigilmonitoring.R.id.edfitertext_search);//stationEnquiry
		mTextHeader = (TextView) findViewById(com.stavigilmonitoring.R.id.stationEnquiry);
		mImageFilter = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_Enquiry_filter);
		mImage = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_enquiry);
		mProgress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressenqry);
		mListView = findViewById(com.stavigilmonitoring.R.id.listEnquiry);
		searchResults = new ArrayList<StateList>();
		Intent i = getIntent();
		mType = i.getStringExtra("Type");
		network = i.getStringExtra("Network");
		mTextHeader.append(mType);

		db = new DatabaseHandler(getApplicationContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();
		/*
		 * ActionBar bar = getActionBar(); bar.setBackgroundDrawable(new
		 * ColorDrawable(Color.parseColor("#0b6bb7")));//
		 * getActionBar().setTitle(
		 * Html.fromHtml("<font color='#ffffff'>STA Vigil</font>"));
		 * getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		 * requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		 */
        mImageFilter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mTextview.getVisibility() == View.VISIBLE) {
					mTextview.setVisibility(View.GONE);
					InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getCurrentFocus()
							.getWindowToken(), 0);
				} else if (mTextview.getVisibility() == View.GONE) {
					mTextview.setVisibility(View.VISIBLE);
					mTextview.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(mTextview,
							InputMethodManager.SHOW_IMPLICIT);
				}

			}
		});

		mImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ut.isnet(getApplicationContext())) {

					fetchdata();
				} else {
					try{
						ut.showD(StationEnquiryStationList.this, "nonet");
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
				Bundle dataBundle = new Bundle();
				dataBundle.putString("stnname", searchResults.get(position).getStatioName());
				dataBundle.putString("stninst", searchResults.get(position).getInstallationId());
				dataBundle.putString("Network", network);

				Intent i = new Intent(getApplicationContext(), StationEnquiry.class);
				i.putExtras(dataBundle);
				startActivity(i);

			}
		});

		mTextview.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				filter = s.toString().trim();
				StationAdaptor.filter((filter).toLowerCase(Locale
						.getDefault()));
			}
		});

		if (dbvalue()) {
			updatelist();
		} else if (ut.isnet(getApplicationContext())) {
			fetchdata();
		} else {
			try{
				ut.showD(StationEnquiryStationList.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}

	}

	private void fetchdata() {
		mDownloadStation = null;
		if (mDownloadStation == null) {
			mImage.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);

			Log.e("async", "null");
			mDownloadStation = new DownloadStation();
			mDownloadStation.execute();
		} else {
			if (mDownloadStation.getStatus() == AsyncTask.Status.RUNNING) {
				Log.e("async", "running");
				mImage.setVisibility(View.GONE);
				mProgress.setVisibility(View.VISIBLE);
			}
		}

	}

	private boolean dbvalue() {

		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql
					.rawQuery(
							"Select distinct InstallationDesc from ConnectionStatusFiltermob",
							null);// SoundLevel_new

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
		searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		int count = 0;
		Cursor c = sql
				.rawQuery(
						"Select distinct InstallationDesc,InstalationId from ConnectionStatusFiltermob where SubNetworkCode='"+mType+"' Order by InstallationDesc",
						null);
		// ,InstallationId
		if (c.getCount() > 0) {
			c.moveToFirst();
			do {
				count++;
				String StationName = c.getString(c
						.getColumnIndex("InstallationDesc"));
				String InstallationId = c.getString(c
						.getColumnIndex("InstalationId"));
				count = c.getCount();

				// Type = Type.replaceAll("0", "");
				// Type = Type.replaceAll("1", "");
				// if (!Type.trim().equalsIgnoreCase("")) {
				StateList sitem = new StateList();

				sitem.setStatioName(StationName);
				sitem.setInstallationId(InstallationId);
				sitem.Setcount(count);
				searchResults.add(sitem);

				// }
			} while (c.moveToNext());

		}

		StationAdaptor = new StationEnquiryStnAdap(StationEnquiryStationList.this, searchResults);
		mListView.setAdapter(StationAdaptor);

	}

	public class DownloadStation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String Url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMasterMobile?Mobile="+mobno;
			Log.e("All Station", "Url=" + Url);

			try {
				resposmsg = ut.httpGet(Url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (resposmsg.contains("<InstalationId>")) {
				sop = "valid";
			//	DatabaseHandler db = new DatabaseHandler(getBaseContext());
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
			return sop;

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mImage.setVisibility(View.GONE);
			mProgress.setVisibility(View.VISIBLE);

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				if (sop.equals("valid")) {
					updatelist();
				} else {
					try{
						ut.showD(StationEnquiryStationList.this, "invalid");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				mImage.setVisibility(View.VISIBLE);
				mProgress.setVisibility(View.GONE);

			} catch (Exception e) {
				e.printStackTrace();
				// dff = new SimpleDateFormat("HH:mm:ss");
				// Ldate = dff.format(new Date());

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

	}

}

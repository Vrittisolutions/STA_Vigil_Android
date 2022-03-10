package com.stavigilmonitoring;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.StationInventoryItemListAdapter;
import com.beanclasses.StationInventoryItemBean;
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
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class StationInventoryItemList extends Activity{
	private TextView tvhead;
	private EditText SearchFilterText;
	private Button btnaddItem;
	private ImageView btnfilter, btnrefresh, btnaddItem2;
	private String mType, mobno, InstallationID1;	
	String sop = "no";
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	public String filter;
	static SimpleDateFormat dff;
	static String Ldate;
	Bundle dataBundle = new Bundle();
	private ProgressBar mprogressBar;
	private static InventItemListURL async;
	private ListView invtlist;
	String conn = "invalid";
	DatabaseHandler db;
	ArrayList<StationInventoryItemBean> StationInventoryItemBeanlist;
	StationInventoryItemBean StnInventoryItemBean;
	StationInventoryItemListAdapter stationInventoryItemListAdapter;
	String responsemsg = "k";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.inventinvtrylist);
		
		tvhead = (TextView) findViewById(com.stavigilmonitoring.R.id.stationInvent);
		btnaddItem = (Button) findViewById(com.stavigilmonitoring.R.id.txtAddInvtItem);
		btnaddItem2= (ImageView) findViewById(com.stavigilmonitoring.R.id.button_invent_add);
		btnfilter = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_invent_filter);
		btnrefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_invent);
		invtlist = (ListView) findViewById(com.stavigilmonitoring.R.id.listInventitems);
		SearchFilterText = (EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext);
		mprogressBar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressinvent1);
		

		StationInventoryItemBeanlist = new ArrayList<StationInventoryItemBean>();
		ut = new com.stavigilmonitoring.utility();
		
		Intent i = getIntent();
		mType = i.getStringExtra("Type");
		InstallationID1 = i.getStringExtra("InstallationId");
		Log.e("Type", mType);
		tvhead.setText("Station Inventory - " + mType);
		
		db = new DatabaseHandler(getBaseContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		
		if (async != null
				&& async.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			btnrefresh.setVisibility(View.GONE);
			mprogressBar.setVisibility(View.VISIBLE);
		}
		
		btnrefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				btnrefresh.setVisibility(View.GONE);
				mprogressBar.setVisibility(View.VISIBLE);
				
				fetchdata();
				
			}
		});
		
		btnaddItem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dataBundle.putString("Option", "Add");	
				dataBundle.putString("InstallationId", InstallationID1);
				AddEditItems(dataBundle);								
			}
		});
		
		btnaddItem2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dataBundle.putString("Option", "Add");
				dataBundle.putString("InstallationId", InstallationID1);
				AddEditItems(dataBundle);								
			}
		});
		
		SearchFilterText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				filter = s.toString();
				stationInventoryItemListAdapter.filter(SearchFilterText.getText().toString().trim()
						.toLowerCase(Locale.getDefault()));				
			}
		});
		
		btnfilter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (SearchFilterText.getVisibility() == View.VISIBLE) {
					SearchFilterText.setVisibility(View.GONE);
					InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
				} else if (SearchFilterText.getVisibility() == View.GONE) {
					SearchFilterText.setVisibility(View.VISIBLE);
					SearchFilterText.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(SearchFilterText,
							InputMethodManager.SHOW_IMPLICIT);
				}
			}
		});
		

		invtlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,	long id) {
				dataBundle.putString("InventoryId", StationInventoryItemBeanlist.get(position).getInventoryId());
				dataBundle.putString("ItemName", StationInventoryItemBeanlist.get(position).getItemname());
				dataBundle.putString("SerialNum", StationInventoryItemBeanlist.get(position).getSrNo());
				dataBundle.putString("Remark",StationInventoryItemBeanlist.get(position).getReMark());
				dataBundle.putString("InstallationId", InstallationID1);
				dataBundle.putString("Option", "Edit");	
				AddEditItems(dataBundle);
			}
			
		});

/*		if (dbvalue()) {
		// fetchdata();
			updatelist();
	} else*/ if (ut.isnet(getApplicationContext())) {

			fetchdata();
			} 
		else {
			try{
				ut.showD(StationInventoryItemList.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
			}
//	}
//		
	}
	
	private boolean dbvalue() {
		// TODO Auto-generated method stub
	try{
		//DatabaseHandler Db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql.rawQuery("Select distinct InstallationId from StationInventory", null);
		
		if (cursor != null && cursor.getCount() > 0) {

			cursor.close();
			return true;

		} else {

			cursor.close();
			return false;
		}
		}catch (Exception e){
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
		// TODO Auto-generated method stub
			async = null;
			if (async == null) {
				try{
				btnrefresh.setVisibility(View.VISIBLE);
				mprogressBar.setVisibility(View.GONE);

				Log.e("async", "null");
				async = new InventItemListURL();
				async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}catch(Exception e){
					e.printStackTrace();
				}
			} else {
				if (async.getStatus() == AsyncTask.Status.RUNNING) {
					Log.e("async", "running");
					btnrefresh.setVisibility(View.GONE);
					mprogressBar.setVisibility(View.VISIBLE);
				}
			}		
	}

	protected void AddEditItems(Bundle dataBundle2) {
		// TODO Auto-generated method stub
		/*Intent intent = new Intent(getApplicationContext(),StationInventoryAddEditItems.class);	
		
		startActivity(intent);	*/	
		Intent intent = new Intent(StationInventoryItemList.this, StationInventoryAddEditItems.class);
		intent.putExtras(dataBundle2);
		startActivityForResult(intent,Common.InvtAddEdit);
	}

	public class InventItemListURL extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String url = "http://sta.vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetStaInventory?InstallationId="
			+InstallationID1
			+"&Mobile="
			+ "";

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);
				//sql.execSQL("DROP TABLE IF EXISTS StationInventory");
				//sql.execSQL(ut.getStationInventory());
				sql.delete("StationInventory",null,null);

				Log.e("csn status", "resmsg : " + responsemsg);

				if (responsemsg.contains("<InventoryId>")) {
					sop = "valid";
					String columnName, columnValue;
					/*
					 * DatabaseHandler db = new
					 * DatabaseHandler(getBaseContext()); SQLiteDatabase sql =
					 * db.getWritableDatabase();
					 */

					
					Cursor cur = sql.rawQuery("SELECT * FROM StationInventory",
							null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table");
					// String msg = "";
					// String columnName, columnValue;
					Log.e("All Station Inventory..",
							" fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							values1.put(columnName, columnValue);

							// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
						}
						sql.insert("StationInventory",
								null, values1);
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
					try{
						ut.showD(StationInventoryItemList.this,"nodata");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				btnrefresh.setVisibility(View.VISIBLE);
				mprogressBar
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
			btnrefresh.setVisibility(View.GONE);
			mprogressBar.setVisibility(View.VISIBLE);
		}


		
	}

	public void updatelist() {
		// TODO Auto-generated method stub
	//	DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		StationInventoryItemBeanlist = new ArrayList<StationInventoryItemBean>();
		StationInventoryItemBeanlist.clear();
		
		Cursor c = sql.rawQuery("Select * from StationInventory", null);
		if (c.getCount()== 0){
			c.close();
		}else{
			c.moveToFirst();
			int column = 0;
			do{
				String InventoryId  = c.getString( c.getColumnIndex("InventoryId") );
				String ItemName  = c.getString( c.getColumnIndex("ItemName") );
				String InstallationId  = c.getString( c.getColumnIndex("InstallationId") );
				String ItemSrNo  = c.getString( c.getColumnIndex("ItemSrNo") );
				String AddedBy  = c.getString( c.getColumnIndex("AddedBy") );
				String DateTime  = c.getString( c.getColumnIndex("AddedDt") );
				String[] Parts = DateTime.split("T");
				String date = Parts[0];
				String[] newparts = Parts[1].split("\\.");
				String time = newparts[0];
				String AddedDt = date+" "+time;
				String Mobile  = c.getString( c.getColumnIndex("Mobile") );
				String Remarks  = c.getString( c.getColumnIndex("Remarks") );
				String IsDeleted = c.getString( c.getColumnIndex("IsDeleted") );
				
				StnInventoryItemBean = new StationInventoryItemBean();
				StnInventoryItemBean.setInventoryId(InventoryId);
				StnInventoryItemBean.setItemName(ItemName);
				StnInventoryItemBean.setInstallationId(InstallationId);
				StnInventoryItemBean.setSrNo(ItemSrNo);
				StnInventoryItemBean.setAddedBy(AddedBy);
				StnInventoryItemBean.setAddedDt(AddedDt);
				StnInventoryItemBean.setMobile(Mobile);
				StnInventoryItemBean.setReMark(Remarks);
				StnInventoryItemBean.setIsDeleted(IsDeleted);
				StationInventoryItemBeanlist.add(StnInventoryItemBean);				
			}while(c.moveToNext());
			c.close();
		}
		
		stationInventoryItemListAdapter = new StationInventoryItemListAdapter(getApplicationContext(), StationInventoryItemBeanlist);
		invtlist.setAdapter(stationInventoryItemListAdapter);
		
	}
	
	private String[] splittime(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf for date...." + tf);
		String fromtimetw = "";

		String k = tf.substring(0, tf.indexOf(" "));
		System.out.println("---value of k for date..." + k);

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(k);
			System.out
					.println("..........value of my date after conv" + myDate);

		} catch (ParseException e) {
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

		}
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
		String finalDate = timeFormat.format(myDate);

		String[] v2 = { finalDate };

		return v2;
	}
	
	private String[] splitfrom(String tf) {
		// TODO Auto-generated method stub
		// 12/31/2015 7:05:00 AM---
		String time1, time2 = null;
		String time[];
		String k = "";
		// String str = "18/01/2013 5:00:00 pm";
		SimpleDateFormat input = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		Date dt;
		try {
			dt = input.parse(tf);

			SimpleDateFormat output = new SimpleDateFormat("MM/dd/yyyy HH:mm");// 2016/02/18
			String formattedDate = output.format(dt); // contains 18/01/2013
														// 17:00:00
														// String k = 01/14/2016
														// 07:05
														// formattedDate.substring(formattedDate.indexOf(" "));
			// fromtimetw = k.substring(0, 5); // = "ab"

			time = formattedDate.split(" ");
			time1 = time[0];
			time2 = time[1];

		} catch (ParseException e) {
			// TODO Auto-generated catch block
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

		String[] v1 = { time2 };

		return v1;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Common.InvtAddEdit) {
			fetchdata();
		}
	}

}

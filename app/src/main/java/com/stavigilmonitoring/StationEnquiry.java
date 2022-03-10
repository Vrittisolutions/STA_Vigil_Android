package com.stavigilmonitoring;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adapters.StationEnqLastThree;
import com.beanclasses.MyBeanconn;
import com.database.DBInterface;

public class StationEnquiry extends Activity {

	private LinearLayout mStnCla;
	private LinearLayout msndlvl;
	private LinearLayout mDwmHistory;
	private LinearLayout mMatreq;
	private LinearLayout mWrkDone;
	private LinearLayout mbusRep,madvhistory,madvschedule;
	private LinearLayout mMathis;
	private LinearLayout mStnExt;
	private TextView mStnname;
	private TextView mConnTime;
	private TextView mLastAdv;
	private TextView mLastAnn, mStn;
	private ImageView mRefresh, callimg;
	private ProgressBar mprogress;
	private String StnName, InstId;
	private String Mobile, Extension, Ext1, mobno;
	private String ConnTime, LastAdv, LastAnn, mType, mNet, sop;
	private static DwnloadXML asynck;
	ListView mList;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	private ArrayList<MyBeanconn> mRecords;
	private ArrayList<MyBeanconn> mRecordsdispaly;
	private ArrayList<MyBean> mAddlist;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.stationenquiry);

		mStnCla = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.stdcal);
		msndlvl = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.sndlvl);
		mDwmHistory = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.dwnhistory);
		mMatreq = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.mtrequest);
		mWrkDone = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.wrkdn);
		mbusRep = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.busenq);
		madvhistory = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.advhistory);
		madvschedule = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.advschedule);
		mMathis = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.mathistory);
		mStnExt = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.stnextcall);
		callimg = (ImageView) findViewById(com.stavigilmonitoring.R.id.callimg);
		mStnname = (TextView) findViewById(com.stavigilmonitoring.R.id.StanEnqname);
		mConnTime = (TextView) findViewById(com.stavigilmonitoring.R.id.Advconnection);
		mLastAdv = (TextView) findViewById(com.stavigilmonitoring.R.id.advlast);
		mLastAnn = (TextView) findViewById(com.stavigilmonitoring.R.id.Advann);
		mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.btnref);
		mprogress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.prg);
		// mStn = (TextView) findViewById(R.id.StanEnqname);
		mList = (ListView) findViewById(com.stavigilmonitoring.R.id.listaddd);
		mAddlist = new ArrayList<MyBean>();
		mRecords = new ArrayList<MyBeanconn>();
		mRecordsdispaly = new ArrayList<MyBeanconn>();

		db = new DatabaseHandler(getApplicationContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();

		Intent i = getIntent();
		StnName = i.getStringExtra("stnname");
		InstId = i.getStringExtra("stninst");
		mType = i.getStringExtra("Type");
		mNet = i.getStringExtra("Network");//
		mStnname.append("- " + StnName);

		if (ut.isnet(StationEnquiry.this)) {
			new StnExtentionXML().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

			fetchdata();              

		} else {
			try{
				ut.showD(StationEnquiry.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}

		}

		mStnCla.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stube
				Intent i = new Intent(getApplicationContext(), SoundLevelCalibrationStandard.class);
				i.putExtra("Stationname", StnName);
				i.putExtra("InstallationID", InstId);
				startActivity(i);

			}
		});
		msndlvl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),
						StationEnquirySoundLevel.class);
				i.putExtra("stnname", StnName);
				i.putExtra("stninst", InstId);
				i.putExtra("Network", mNet);
				startActivity(i);

			}
		});
		mDwmHistory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), StationEnquiryDowntimeHistory.class);
				i.putExtra("stnname", StnName);
				i.putExtra("stninst", InstId);
				i.putExtra("Network", mNet);
				startActivity(i);

			}
		});
		mMatreq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), StationEnquiryMaterialRequest.class);
				i.putExtra("stnname", StnName);
				i.putExtra("stninst", InstId);
				startActivity(i);

			}
		});
		mWrkDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), StationEnquiryWorkHistory.class);
				i.putExtra("stnname", StnName);
				i.putExtra("stninst", InstId);
				startActivity(i);

			}
		});

		mbusRep.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), StationEnquiryBusReporting.class);
				i.putExtra("stnname", StnName);
				i.putExtra("stninst", InstId);
				startActivity(i);
			}
		});

		madvhistory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(StationEnquiry.this, StationEnquiryAdvHistory.class);
				i.putExtra("stnname", StnName);
				i.putExtra("stninst", InstId);
				startActivity(i);
			}
		});

		madvschedule.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(StationEnquiry.this, StationEnquiryAdvSchedule.class);
				i.putExtra("stnname", StnName);
				i.putExtra("stninst", InstId);
				startActivity(i);
			}
		});

		mMathis.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), StationEnquiryMaterialHiscom.class);
				i.putExtra("stnname", StnName);
				i.putExtra("stninst", InstId);
				startActivity(i);
			}
		});

		mStnExt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				/*String Num = "2230148429";
				String ext1 = "3";
				String ext2 = "101";*/
			String Num = Mobile; 	//sanket shende mobile
			String ext1 = Ext1;
			String ext2 = Extension;
			String SupporteeNumber = "9764179265";
			String SanketNumber = mobno ;

			String URL = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/ClikToCall" +
					"?agentnumber=+91"+Mobile+"&callernumber=+91"+mobno;

			new CallTriggerXML().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

			//Intent callIntent = new Intent(Intent.ACTION_DIAL);
			//remove this line and add api hit (parameters  - num )
				//http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/ClikToCall?agentnumber= + Mobile + &callernumber=+SupporteeNumber;
			//callIntent.setData(Uri.parse("tel:+91" + Num + PhoneNumberUtils.WAIT + ext1 + PhoneNumberUtils.PAUSE + ext2));

				//startActivity(callIntent);

				//finish();
			}
		});

		mRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fetchdata();
			}
		});
	}

	private void fetchdata() {
		// TODO Auto-generated method stub

		try{
			asynck = new DwnloadXML();
			asynck.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void UpdatedataAdv() {
		// TODO Auto-generated method stub
		mRecords.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String aBustime, aActann, afrombus, aTobus, aBusaddate, bLastadv, bAdvname, bAdvcode, bAudiolevel;
		int count = 0;
		Cursor c = sql
				.rawQuery(
						"Select distinct a.BusTime,a.ActualAnntime,a.startingStation,a.endStation,a.ServerAddedDT" +
								",b.LastAdvDate" +
								",b.AdvertisementDesc" +
								",b.AdvertisementCode,b.AudioOutPut" +
								"  from LastthreeAnn a " +
								"inner join LastthreeAdv b on a.InstallationId=b.InstalationId  " +
								"where " +  "a.InstallationId='"
								//"b.InstalationId='"
								+ InstId + "'", null);
		if (c.getCount() < 9) {

			c.close();

		} else {
			c.moveToFirst();
			do {
				count = c.getCount();
				aBustime = c.getString(c.getColumnIndex("BusTime"));
				aActann = c.getString(c.getColumnIndex("ActualAnntime"));
				afrombus = c.getString(c.getColumnIndex("startingStation"));
				aTobus = c.getString(c.getColumnIndex("endStation"));
				aBusaddate = c.getString(c.getColumnIndex("ServerAddedDT"));
				bLastadv = c.getString(c.getColumnIndex("LastAdvDate"));
				bAdvcode = c.getString(c.getColumnIndex("AdvertisementCode"));
				bAudiolevel = c.getString(c.getColumnIndex("AudioOutPut"));
				bAdvname = c.getString(c.getColumnIndex("AdvertisementDesc"));

				aBustime = split(aBusaddate);
				aActann = split(aActann);
				aBusaddate = split(aBusaddate);
				bLastadv = split(bLastadv);
				MyBeanconn bean = new MyBeanconn();
				bean.setaBustime(aBustime);
				bean.setaActann(aActann);
				bean.setAfrombus(afrombus);
				bean.setaTobus(aTobus);
				bean.setaBusaddate(aBusaddate);
				bean.setbLastadv(bLastadv);
				bean.setbAdvname(bAdvname);
				bean.setbAdvcode(bAdvcode);
				bean.setbAudiolevel(bAudiolevel);
				mRecords.add(bean);

			} while (c.moveToNext());

			c.close();
			mRecordsdispaly.clear();
			String s1 = mRecords.get(0).getaBusaddate();
			String s2 = mRecords.get(0).getbAdvname();
			String s3 = mRecords.get(1).getaBusaddate();
			String s4 = mRecords.get(1).getbAdvname();
			String s5 = mRecords.get(2).getaBusaddate();
			String s6 = mRecords.get(2).getbAdvname();
			String s7 = mRecords.get(3).getaBusaddate();
			String s8 = mRecords.get(3).getbAdvname();
			String s9 = mRecords.get(4).getaBusaddate();
			String s0 = mRecords.get(4).getbAdvname();
			String sa = mRecords.get(5).getaBusaddate();
			String ss = mRecords.get(5).getbAdvname();
			String sf = mRecords.get(6).getaBusaddate();
			String sh = mRecords.get(6).getbAdvname();
			String sr = mRecords.get(7).getaBusaddate();
			String sj = mRecords.get(7).getbAdvname();
			String sp = mRecords.get(8).getaBusaddate();
			String se = mRecords.get(8).getbAdvname();

			mRecordsdispaly.add(mRecords.get(0));
			mRecordsdispaly.add(mRecords.get(4));
			mRecordsdispaly.add(mRecords.get(8));
		}

		StationEnqLastThree adp = new StationEnqLastThree(this, mRecordsdispaly);
		mList.setAdapter(adp);
	}

	private void Updatedataconn() { // TODO Auto-generated method stub
		mAddlist.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		int count = 0;
		Cursor c = sql.rawQuery(
				"Select distinct ServerTime,LastAdvDate,LastbusReporting from " +
						"PerticularConnection where InstalationId='"
								+ InstId + "'", null);
		if (c.getCount() == 0) {

			c.close();

		} else {
			c.moveToFirst();
			do {
				count = c.getCount();
				ConnTime = c.getString(c.getColumnIndex("ServerTime"));
				LastAdv = c.getString(c.getColumnIndex("LastAdvDate"));
				LastAnn = c.getString(c.getColumnIndex("LastbusReporting"));
				MyBean bean = new MyBean();
				bean.setConntime(ConnTime);
				bean.setLastAdv(LastAdv);
				bean.setLastann(LastAnn);
				mAddlist.add(bean);

			} while (c.moveToNext());

			c.close();
			String date1 = splitserver(mAddlist.get(0).getConntime());
			String date2 = split(mAddlist.get(0).getLastAdv());
			String date3 = split(mAddlist.get(0).getLastann());
			mConnTime.setText(date1);
			mLastAdv.setText(date2);
			mLastAnn.setText(date3);
		}

	}

	class DwnloadXML extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mRefresh.setVisibility(View.GONE);
			mprogress.setVisibility(View.VISIBLE);
			sop = "";
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationMasterforPerticular?Installationid="
					+ InstId;
			Log.e("station wise data", "url : " + url);
			url = url.replaceAll(" ", "%20");

			String responsemsg;
			try {
				responsemsg = com.stavigilmonitoring.utility.httpGet(url);
				Log.e("station wise data", "resmsg : " + responsemsg);

				if (responsemsg.contains("<InstalationId>")) {
					sop = "valid";
					String columnName, columnValue;
					//sql.execSQL("DROP TABLE IF EXISTS PerticularConnection");
					//sql.execSQL(ut.getpeticularconnection());
					sql.delete("PerticularConnection",null,null);

					Cursor cur = sql.rawQuery(
							"SELECT * FROM PerticularConnection", null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table");
					// String msg = "";
					// String columnName, columnValue;
					Log.e("All Station data...",
							" fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {

							Log.e("dwnloadXML...", " count i: " + i
									+ "  j:" + j);
							columnName = cur.getColumnName(j);
							Log.e("dwnloadXML...", "name"
									+ columnName);
							columnValue = ut.getValue(e, columnName);
							Log.e("dwnloadXML...", "value"
									+ columnValue);
							values1.put(columnName, columnValue);

						}
						long a = sql.insert("PerticularConnection", null,
								values1);
						Log.e("table insert", "value" + a);

					}

					cur.close();

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				sop = "invalid";
			}

			// /////////////////////////////////////////////////////////////////////////

			url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationMasterAnnouncementScheduleHistory_V1?Installationid="
					+ InstId;

			Log.e("last 3 ADV", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = com.stavigilmonitoring.utility.httpGet(url);
				Log.e("last 3 ADV", "resmsg : " + responsemsg);

				if (responsemsg.contains("<InstalationId>")) {
					sop = "valid";
					String columnName, columnValue;
					//sql.execSQL("DROP TABLE IF EXISTS LastthreeAdv");
					//sql.execSQL(ut.getLastThreeADV());
					sql.delete("LastthreeAdv",null,null);

					Cursor cur = sql.rawQuery("SELECT * FROM LastthreeAdv",
							null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table");
					// String msg = "";
					// String columnName, columnValue;
					Log.e("All Station data...",
							" fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {

							Log.e("dwnloadXML...", " count i: " + i
									+ "  j:" + j);
							columnName = cur.getColumnName(j);
							Log.e("dwnloadXML...", "name"
									+ columnName);
							columnValue = ut.getValue(e, columnName);
							Log.e("dwnloadXML...", "value"
									+ columnValue);
							values1.put(columnName, columnValue);

						}
						long a = sql.insert("LastthreeAdv", null, values1);
						Log.e("table insert", "value" + a);

					}

					cur.close();

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				sop = "invalid";
			}
			// //////////////////////////////////////////////////////////////////////////////////////////////
			url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationMasterBusReporting?Installationid="
					+ InstId;

			Log.e("last 3 ann", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = com.stavigilmonitoring.utility.httpGet(url);
				Log.e("last 3 ann", "resmsg : " + responsemsg);

				if (responsemsg.contains("<BusTime>")) {
					sop = "valid";
					String columnName, columnValue, ncolumnName;
					//sql.execSQL("DROP TABLE IF EXISTS LastthreeAnn");
					//sql.execSQL(ut.getLastAnn());
					sql.delete("LastthreeAnn",null,null);

					Cursor cur = sql.rawQuery("SELECT * FROM LastthreeAnn",
							null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table");
					// String msg = "";
					// String columnName, columnValue;
					Log.e("All Station data...",
							" fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {

							Log.e("dwnloadXML...", " count i: " + i
									+ "  j:" + j);
							columnName = cur.getColumnName(j);
							Log.e("dwnloadXML...", "name"
									+ columnName);
							if (columnName.equalsIgnoreCase("startingStation")) {
								ncolumnName = "From";

							} else if (columnName
									.equalsIgnoreCase("endStation")) {
								ncolumnName = "To";

							} else {
								ncolumnName = columnName;
							}
							columnValue = ut.getValue(e, ncolumnName);
							Log.e("dwnloadXML...", "value"
									+ columnValue);
							values1.put(columnName, columnValue);

						}
						long a = sql.insert("LastthreeAnn", null, values1);
						Log.e("table insert", "value" + a);

					}

					cur.close();

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				sop = "invalid";
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			try{
				// sop = "valid";
				if (sop.equalsIgnoreCase("valid")) {
					Updatedataconn();
					UpdatedataAdv();
				}else if(sop.equalsIgnoreCase("invalid") || sop.equalsIgnoreCase(null)
						|| sop.equalsIgnoreCase("")) {
					Toast.makeText(getParent(),"No data found", Toast.LENGTH_SHORT).show();
				}
				mRefresh.setVisibility(View.VISIBLE);
				mprogress.setVisibility(View.GONE);
			}catch (Exception e){
				e.printStackTrace();
			}

		}
	}

    class StnExtentionXML extends AsyncTask<Void, Void, String> {

        ProgressDialog progressDialog;
        String exceptionString = "ok", resposmsg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRefresh.setVisibility(View.GONE);
            mprogress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String Url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetStationExtension?InstallationId=" + InstId;

            Url = Url.replaceAll(" ", "%20");

            try {
                resposmsg = ut.httpGet(Url);
                Log.e("Response", resposmsg);
            } catch (IOException e) {
                sop = "ServerError";
                e.printStackTrace();

            }

			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			//sql.execSQL("DROP TABLE IF EXISTS StnExtTable");
			//sql.execSQL(ut.getStnExtTable());
			sql.delete("StnExtTable",null,null);

			try{

            if (resposmsg.contains("No data ")) {
                //sumdata2 = "0";
                sop = "nodata";
                //up

            } else if (resposmsg.contains("No record")) {
				//sumdata2 = "0";
				sop = "nodata";
				//up

			}else if (resposmsg.contains("<MobileNo>")) {
				sop = "valid";

				String columnName, columnValue;
				//sql.execSQL("DROP TABLE IF EXISTS StnExtTable");
				//sql.execSQL(ut.getStnExtTable());
				sql.delete("StnExtTable",null,null);

				Cursor cur1 = sql.rawQuery("SELECT * FROM StnExtTable", null);
				int count = cur1.getCount();
				ContentValues values2 = new ContentValues();
				NodeList nl2 = ut.getnode(resposmsg, "TableResult");

				for (int i = 0; i < nl2.getLength(); i++) {
					Element e = (Element) nl2.item(i);

					for (int j = 0; j < cur1.getColumnCount(); j++) {
						columnName = cur1.getColumnName(j);
						/*if(columnName.equals("Extension")) {
							Extension = ut.getValue(e, columnName);
						}else if(columnName.equals("Ext1")){
							Ext1 = ut.getValue(e, columnName);
						}*/if(columnName.equals("InstallationId")) {
							Extension = ut.getValue(e, columnName);
							String instID = ut.getValue(e, columnName);
						}else if(columnName.equals("InstalationName")){
							Ext1 = ut.getValue(e, columnName);
							String instName = ut.getValue(e, columnName);
						}else if(columnName.equals("MobileNo")){
							Mobile = ut.getValue(e, columnName);
						}
					}
				}
				cur1.close();

			}/*else if (resposmsg.contains("<Extension>")) {
                sop = "valid";

                String columnName, columnValue;
                sql.execSQL("DROP TABLE IF EXISTS StnExtTable");
                sql.execSQL(ut.getStnExtTable());
                Cursor cur1 = sql.rawQuery("SELECT * FROM StnExtTable", null);
                int count = cur1.getCount();
                ContentValues values2 = new ContentValues();
                NodeList nl2 = ut.getnode(resposmsg, "TableResult");

                for (int i = 0; i < nl2.getLength(); i++) {
                    Element e = (Element) nl2.item(i);
                    for (int j = 0; j < cur1.getColumnCount(); j++) {
                        columnName = cur1.getColumnName(j);
                        if(columnName.equals("Extension")) {
							Extension = ut.getValue(e, columnName);
						}else if(columnName.equals("Ext1")){
							Ext1 = ut.getValue(e, columnName);
						}else if(columnName.equals("MobileNo")){
							Mobile = ut.getValue(e, columnName);
						}
                    }
                }
                cur1.close();

            }*/ else {
                sop = "invalid";
            }
			}catch (Exception e){
				e.printStackTrace();
			}

            return sop;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                mRefresh.setVisibility(View.VISIBLE);
                mprogress.setVisibility(View.GONE);
                if(resposmsg.contains("No data ")||resposmsg.contains("No record")){
					Toast.makeText(StationEnquiry.this, "Extension not available", Toast.LENGTH_SHORT).show();
					////
					callimg.setBackgroundResource(com.stavigilmonitoring.R.drawable.extcalldisable);
					mStnExt.setClickable(false);
				}

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

	class CallTriggerXML extends AsyncTask<Void, Void, String> {

		ProgressDialog progressDialog;
		String exceptionString = "ok", resposmsg;
		String Station_AgentNumber = Mobile;
		String LoggedIn_SupporteeNo = mobno;
		String mobno1 = "8669196847";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mRefresh.setVisibility(View.GONE);
			mprogress.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			/*String Url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/ClikToCall" +
					"?agentnumber=+91"+Mobile+"&callernumber=+91"+mobno;
*/
			String Url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/ClikToCall" +
					"?agentnumber="+Mobile+"&callernumber="+mobno;

			Url = Url.replaceAll(" ", "%20");

			try {
				resposmsg = ut.httpGet(Url);
				Log.e("Response", resposmsg);
				sop = "valid";

			/*	if(resposmsg.contains("<root>")){
					NodeList nl1 = ut.getnode(resposmsg, "root");
					for(int i =0; i<= nl1.getLength(); i++){
						Element e = (Element) nl1.item(i);
						String data = ut.getValue(e,"status");
					}
				}*/

			} catch (IOException e) {
				sop = "invalid";
				e.printStackTrace();
			}

			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mRefresh.setVisibility(View.VISIBLE);
			mprogress.setVisibility(View.GONE);
			  if (sop.contains("valid")) {
				  Toast.makeText(StationEnquiry.this, "Call Triggered Successfully", Toast.LENGTH_SHORT).show();

			}else if(sop.contains("invalid")){
				  Toast.makeText(StationEnquiry.this, "Sorry! Call not sent", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private String splitserver(String data) {
		// TODO Auto-generated method stub
		if (data == null || data.equalsIgnoreCase("")) {
			return "";

		} else {
			data = data.replace("T", " ");
			String s = data.substring(0, data.indexOf("."));

			Date conn = null;
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 2016-05-12T20:34:06.647+05:30

				conn = dateFormat.parse(s);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
			String dat = dateFormat1.format(conn);

			return dat;
		}

	}

	private boolean dbvalue2(){
        try{
           // DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
            SQLiteDatabase sql = db.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select * from StnExtTable", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                cursor.close();
                return true;
            }else{
                cursor.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void getStnExtensionNo(){
        try{
            //DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
            SQLiteDatabase sql = db.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select Extension, MobileNo, Ext1 from StnExtTable where NetworkCode='"+mNet+"' and InstalationName='"+StnName+"'", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                Extension = cursor.getString(cursor.getColumnIndex("Extension"));
                Mobile = cursor.getString(cursor.getColumnIndex("MobileNo"));
                Ext1 = cursor.getString(cursor.getColumnIndex("Ext1"));

                cursor.close();
            }else{
                cursor.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
	}

	private String split(String data) {
		// TODO Auto-generated method stub
		if (data == null || data.equalsIgnoreCase("")) {
			return "";

		} else {
			data = data.replace("T", " ");
			String s = data.substring(0, data.indexOf("+"));

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

	private boolean dbvalue() {
		try {
			// TODO Auto-generated method stub
			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery("SELECT * FROM PerticularConnection WHERE InstalationId='"
								+ InstId + "' ",
					null);
			System.out.println("----------  dbvalue screen cursor count -- "
					+ cursor.getCount());

			if (cursor != null && cursor.getCount() > 0) {
				Cursor c = sql.rawQuery("SELECT * FROM LastthreeAdv", null);
				if (c != null && cursor.getCount() > 0) {
					Cursor c1 = sql
							.rawQuery("SELECT * FROM LastthreeAnn", null);
					if (c1 != null && cursor.getCount() > 0) {

						cursor.close();
						return true;

					} else {

						cursor.close();
						return false;
					}

				} else {

					cursor.close();
					return false;
				}

			} else {

				cursor.close();
				return false;
			}
		} catch (Exception e) {
			SimpleDateFormat dff = new SimpleDateFormat("HH:mm:ss");
			String Ldate = dff.format(new Date());

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

			return false;
		}

	}

	private class MyBean {
		private String conntime;
		private String LastAdv;
		private String Lastann;

		public String getConntime() {
			return conntime;
		}

		public void setConntime(String conntime) {
			this.conntime = conntime;
		}

		public String getLastAdv() {
			return LastAdv;
		}

		public void setLastAdv(String lastAdv) {
			LastAdv = lastAdv;
		}

		public String getLastann() {
			return Lastann;
		}

		public void setLastann(String lastann) {
			Lastann = lastann;
		}

	}

	/*class MyBeanconn {
		// String
		// aBustime,aActann,afrombus,aTobus,aBusaddate,bLastadv,bAdvname,bAdvcode,bAudiolevel;
		private String aBustime;
		private String aActann;
		private String afrombus;
		private String aTobus;
		private String aBusaddate;
		private String bLastadv;
		private String bAdvname;
		private String bAdvcode;
		private String bAudiolevel;

		public String getaBustime() {
			return aBustime;
		}

		public void setaBustime(String aBustime) {
			this.aBustime = aBustime;
		}

		public String getaActann() {
			return aActann;
		}

		public void setaActann(String aActann) {
			this.aActann = aActann;
		}

		public String getAfrombus() {
			return afrombus;
		}

		public void setAfrombus(String afrombus) {
			this.afrombus = afrombus;
		}

		public String getaTobus() {
			return aTobus;
		}

		public void setaTobus(String aTobus) {
			this.aTobus = aTobus;
		}

		public String getaBusaddate() {
			return aBusaddate;
		}

		public void setaBusaddate(String aBusaddate) {
			this.aBusaddate = aBusaddate;
		}

		public String getbLastadv() {
			return bLastadv;
		}

		public void setbLastadv(String bLastadv) {
			this.bLastadv = bLastadv;
		}

		public String getbAdvname() {
			return bAdvname;
		}

		public void setbAdvname(String bAdvname) {
			this.bAdvname = bAdvname;
		}

		public String getbAdvcode() {
			return bAdvcode;
		}

		public void setbAdvcode(String bAdvcode) {
			this.bAdvcode = bAdvcode;
		}

		public String getbAudiolevel() {
			return bAudiolevel;
		}

		public void setbAudiolevel(String bAudiolevel) {
			this.bAudiolevel = bAudiolevel;
		}

	}*/

}

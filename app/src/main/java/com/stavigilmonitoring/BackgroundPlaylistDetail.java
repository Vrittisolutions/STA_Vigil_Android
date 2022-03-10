package com.stavigilmonitoring;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.beanclasses.BackgroundPlaylistBean;
import com.database.DBInterface;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BackgroundPlaylistDetail extends Activity {

	// ProgressDialog pd;
	ListView workspacewisedetail;
	private Context parent;
	String mobno, link;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	//ArrayList<String> projectlist = new ArrayList<String>();
	static SimpleDateFormat dff;
	static String Ldate;
	ImageView iv;
	ArrayList<BackgroundPlaylistBean> PlayNameList,contentNameList;
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;
    private LinearLayout containerLayout,head;
	private ListView ListData;
	private String StationName, StationID;
	private TextView TextStationName, ConnStart, ConnServer, Connday;
	private TextView servertime, Playstarttime, Playservertime, playdata;
	private TextView dateday;
	String flag = "";
	private TextView starttime;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.backgroundplaylistdata);

		Intent extras = getIntent();
		StationName = extras.getStringExtra("StationName");
		StationID = extras.getStringExtra("InstallationID");
		parent = BackgroundPlaylistDetail.this;
		TextStationName = (TextView) findViewById(com.stavigilmonitoring.R.id.StationName);
	    head = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.display);
	    head.setVisibility(View.VISIBLE);

	    ConnStart = (TextView) findViewById(com.stavigilmonitoring.R.id.connstarttime);
		ConnServer = (TextView) findViewById(com.stavigilmonitoring.R.id.connsevertime);
		Connday = (TextView) findViewById(com.stavigilmonitoring.R.id.connday);
		Playstarttime = (TextView) findViewById(com.stavigilmonitoring.R.id.Playstarttime);
		Playservertime = (TextView) findViewById(com.stavigilmonitoring.R.id.playservertime);
		playdata = (TextView) findViewById(com.stavigilmonitoring.R.id.playday);
		containerLayout = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.lincontainer);
		TextStationName.setText(StationName);
		((LinearLayout) findViewById(com.stavigilmonitoring.R.id.llStationName)).setTag(StationName);
		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_bg_data);
		//ListData = (ListView) findViewById(R.id.bg_list);

		db = new DatabaseHandler(getBaseContext());

		 PlayNameList = new ArrayList<BackgroundPlaylistBean>();
		 contentNameList = new ArrayList<BackgroundPlaylistBean>();
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();
		if (dbvalue()) {
			updateLink();
			updatePlayList();
		} else {
			fetchdata();
		}
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isnet()) {

					fetchdata();
				} else {
					try{
						ut.showD(BackgroundPlaylistDetail.this,"nonet");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
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

	public void updateLink() {
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery(
				"SELECT  MAX(ServerTime)  FROM Backgroundplaylist WHERE InstalationId='"
						+ StationID + "'", null);
		if (c.getCount() == 0) {
			c.close();

		} else {
			c.moveToFirst();

			int column = 0;
			do {
			//	int column1 = c.getColumnIndex("ServerTime");
				String tf = c.getString(0);
				String tfdateday =c.getString(0);
				String tftym = c.getString(0);
				String v2 = splittime1(tf);

				String[] v1 = splitfrom1(tfdateday);
				String[] v2dd = splittimedateday(tftym);
				ConnStart.setText(splittime1(tftym));
				Connday.setText(v2dd[0]);
				ConnServer.setText(v1[0]);
			
			} while (c.moveToNext());
		
			c.close();
			
		}
		
		Cursor c1 = sql.rawQuery(
				"SELECT  MAX(PlayDate)  FROM Backgroundplaylist WHERE InstalationId='"
						+ StationID + "'", null);
		if (c1.getCount() == 0) {
			c1.close();

		} else {
			c1.moveToFirst();

			int column = 0;
			do {
			//	int column1 = c1.getColumnIndex("PlayDate");
			
				String tf = c1.getString(0);
				if(!tf.equalsIgnoreCase("")){
				String tfdateday = c1.getString(0);
				String tftym = c1.getString(0);
				String v2 = splittime1(tf);

				String[] v1 = splitfrom1(c1.getString(0));
				String[] v2dd = splittimedateday(c1.getString(0));
				Playstarttime.setText(splittime1(c1.getString(0)));
				Playservertime.setText(v1[0]);
				playdata.setText(v2dd[0]);
				}else{
					Playstarttime.setText("No Info");
					Playservertime.setText("No Info");
					playdata.setText("No Info");
					
				}
			} while (c1.moveToNext());
		
			c1.close();
			/*sql.close();
			db.close();*/
		}
		

	}
	
	
	public void updatePlayList() {
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		PlayNameList.clear();
		Cursor c = sql.rawQuery(
				"SELECT DISTINCT PlaylistName,EffectiveDateTo,EffectiveDateFrom  FROM Backgroundplaylist WHERE InstalationId='"
						+ StationID + "' ORDER BY EffectiveDateFrom", null);
		if (c.getCount() == 0) {
			c.close();

		} else {
			c.moveToFirst();

			int column = 0;
			do {
			 
				String PlaylistName = c.getString(c.getColumnIndex("PlaylistName"));
				String efffrom =c.getString(c.getColumnIndex("EffectiveDateFrom"));
				String effto = c.getString(c.getColumnIndex("EffectiveDateTo"));
				
				BackgroundPlaylistBean bean = new BackgroundPlaylistBean();
				bean.setPlaylistName(PlaylistName);
				bean.setEffectiveDateFrom(efffrom);
				bean.setEffectiveDateTo(effto);
				PlayNameList.add(bean);
			
			
			} while (c.moveToNext());
		    c.close();
		   
		}
		
		
		 showPlayListItems();

	}
	private void showPlayListItems() {
		if(PlayNameList.size()>0) {
			for (int i = 0; i < PlayNameList.size(); i++) {
				addView(i);
			}
		}
		else{

		}
	}
	private void showContentListItems(LinearLayout layout) {
		if(contentNameList.size()>0) {
			for (int i = 0; i < contentNameList.size(); i++) {
				addChildView(i,layout);
			}
		}
		else{

		}
	}
	
	private void addChildView(int i,LinearLayout layout) {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = (LayoutInflater) parent
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View baseView = layoutInflater.inflate(com.stavigilmonitoring.R.layout.bg_play_content_item,
				null);
		
		TextView textviewcontentName = (TextView) baseView
				.findViewById(com.stavigilmonitoring.R.id.contentname);
		TextView textviewflgSchedule = (TextView) baseView
				.findViewById(com.stavigilmonitoring.R.id.ScheduledFlag);
		TextView textviewflgmaster = (TextView) baseView
				.findViewById(com.stavigilmonitoring.R.id.MasterFlag);
		TextView textviewflgcontent = (TextView) baseView
				.findViewById(com.stavigilmonitoring.R.id.contentFlag);
		TextView textviewbgclip = (TextView) baseView
				.findViewById(com.stavigilmonitoring.R.id.clipFlag);
		
		textviewcontentName.setText(contentNameList.get(i)
				.getContentname());
		textviewflgSchedule.setText(contentNameList.get(i)
				.getFlagSchedule());
		textviewflgmaster.setText(contentNameList.get(i)
				.getFlagmaster());
		textviewflgcontent.setText(contentNameList.get(i)
				.getFlagcontent());
		textviewbgclip.setText(contentNameList.get(i)
				.getFlagBgClip());
		layout.addView(baseView);
	}

	private void addView(int i) {
		LayoutInflater layoutInflater = (LayoutInflater) parent
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View baseView = layoutInflater.inflate(com.stavigilmonitoring.R.layout.bg_play_item,
				null);
		TextView textviewPlaylistName = (TextView) baseView
				.findViewById(com.stavigilmonitoring.R.id.textview_playName_name);
LinearLayout  chiledContainer = (LinearLayout) baseView.findViewById(com.stavigilmonitoring.R.id.chiledcontainer);
		final TextView textviewEffFromDate = (TextView) baseView
				.findViewById(com.stavigilmonitoring.R.id.efftoDate);
		final TextView textviewEffFromtime = (TextView) baseView
				.findViewById(com.stavigilmonitoring.R.id.efftoTime);
		final TextView textviewEfftoDate = (TextView) baseView
				.findViewById(com.stavigilmonitoring.R.id.efftodate);
		final TextView textviewEfftotime = (TextView) baseView
				.findViewById(com.stavigilmonitoring.R.id.efftotime);
		
		textviewPlaylistName.setText(PlayNameList.get(i)
				.getPlaylistName());
	String[] V = splitEff(PlayNameList.get(i)
			.getEffectiveDateFrom());
   String[] V1 = splitEffto(PlayNameList.get(i)
			.getEffectiveDateTo());
		textviewEffFromDate.setText(V[0]);
		textviewEffFromtime.setVisibility(View.GONE);
		textviewEfftoDate.setText(V1[0]);
		textviewEfftotime.setVisibility(View.GONE);
	containerLayout.addView(baseView);
	FillContent(PlayNameList.get(i)
			.getPlaylistName(),chiledContainer);
	
	}
	private void FillContent(String PlaylistName,LinearLayout linearLayout) {
		// TODO Auto-generated method stub
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		contentNameList.clear();
		Cursor c = sql.rawQuery(
				"SELECT ContentName,IsDeleted,PSDownloadFlag,PMDownloadFlag,PCRecDownloadFlag,PCClipDownloadFlag FROM Backgroundplaylist WHERE InstalationId='"
						+ StationID + "' AND PlaylistName='"+PlaylistName+"'", null);
		if (c.getCount() == 0) {
			c.close();

		} else {
			c.moveToFirst();
         String contentname,flagisdeleted,flagSchedule,flagmaster,flagcontent,flagBgClip;
			int column = 0;
			do {
			 
				contentname = c.getString(c.getColumnIndex("ContentName"));
				flagisdeleted =c.getString(c.getColumnIndex("IsDeleted"));
				flagSchedule = c.getString(c.getColumnIndex("PSDownloadFlag"));
				flagmaster = c.getString(c.getColumnIndex("PMDownloadFlag"));
				flagcontent = c.getString(c.getColumnIndex("PCRecDownloadFlag"));
				flagBgClip = c.getString(c.getColumnIndex("PCClipDownloadFlag"));
				BackgroundPlaylistBean bean = new BackgroundPlaylistBean();
				bean.setContentname(contentname);
				bean.setFlagisdeleted(flagisdeleted);
				bean.setFlagSchedule(flagSchedule);
				bean.setFlagmaster(flagmaster);
				bean.setFlagcontent(flagcontent);
				bean.setFlagBgClip(flagBgClip);
				contentNameList.add(bean);
			
			
			} while (c.moveToNext());
		    c.close();
		   
		}
		showContentListItems(linearLayout);
		
	}

	private String splittime1(String tf) {
		// TODO Auto-generated method stub

		long diffDays = 0;
		System.out.println("---value of tf for date...." + tf);
		String fromtimetw = "";
		//2016-08-05T14:58:12.757+05:30
		String k = tf.substring(0, tf.indexOf("."));
		k = k.replace("T"," ");

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy");
		String finalDate = timeFormat.format(myDate);
		System.out.println("..............final date of update link>>>>>"
				+ finalDate);

		final String dateStart = finalDate;
		DateFormat dateFormat1 = new SimpleDateFormat("dd MMM, yyyy");
		Date date = new Date();
		System.out.println("date format of system......................"
				+ dateFormat1.format(date));
		System.out
				.println("<<<<<<<<<<<<<<<<date format startdate......................"
						+ dateStart);
		System.out.println("date format of web tym......................"
				+ date);
		final String dateStop = dateFormat1.format(date);
		System.out
				.println("<<<<<<<<<<<<<<<<date format dateStop......................"
						+ dateStop);
		Date d1 = null;
		Date d2 = null;
		String diffTym = "";

		try {
			d1 = dateFormat1.parse(dateStart);
			d2 = dateFormat1.parse(dateStop);
			System.out.println("d2......................" + d2);
			// in milliseconds
			long diff = d2.getTime() - d1.getTime();

			diffDays = diff / (24 * 60 * 60 * 1000);

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

		}

		if (diffDays == 0) {
			return "Today";

		} else if (diffDays == 1) {
			return "Yesterday";
		} else {
			return finalDate;
		}

	}

	private String[] splitfrom1(String tf) {
		// TODO Auto-generated method stub
		String k = tf.substring(0, tf.indexOf("."));
		k = k.replace("T"," ");

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa");
		String finalDate = timeFormat.format(myDate);
		System.out.println("---value of k for time..." + k);

	
		String[] v1 = { finalDate };
		// String[] v2={ fromtimetw };

		return v1;
	}
	
	
	private String[] splitEff(String tf) {
		// TODO Auto-generated method stub
		String k = tf.substring(0, tf.indexOf("+"));//2016-08-12T11:57:47.29+05:30  2016-07-01T00:00:00+05:30
		k = k.replace("T"," ");

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		
		SimpleDateFormat DateFormat = new SimpleDateFormat("dd MMM,yyyy hh:mm:ss aa");
		String finalDate = DateFormat.format(myDate);
		System.out.println("---value of k for time..." + k);
		SimpleDateFormat timeFormat = new SimpleDateFormat("");
		String finaltime = timeFormat.format(myDate);
		System.out.println("---value of k for time..." + k);

	
		String[] v1 = { finalDate };
		// String[] v2={ fromtimetw };

		return v1;
	}
	
	private String[] splitEffto(String tf) {
		// TODO Auto-generated method stub
		String k = tf.substring(0, tf.indexOf("+"));//2016-08-12T11:57:47.29+05:30  2016-07-01T00:00:00+05:30
		k = k.replace("T"," ");

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		
		SimpleDateFormat DateFormat = new SimpleDateFormat("dd MMM,yyyy hh:mm:ss aa");
		String finalDate = DateFormat.format(myDate);
		System.out.println("---value of k for time..." + k);
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa");
		String finaltime = timeFormat.format(myDate);
		System.out.println("---value of k for time..." + k);

	
		String[] v1 = { finalDate };
		// String[] v2={ fromtimetw };

		return v1;
	}

	private String[] splittimedateday(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf for date...." + tf);
		String fromtimetw = "";

		String k = tf.substring(0, tf.indexOf("."));
		k = k.replace("T"," ");
		System.out.println("---value of k for date..." + k);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("EEE");
		String finalDate = timeFormat.format(myDate);

		String[] v2dd = { finalDate };

		return v2dd;
	}

	private boolean dbvalue() {
		// TODO Auto-generated method stub
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql.rawQuery(
				"SELECT * FROM Backgroundplaylist WHERE InstalationId='"
						+ StationID + "'", null);

		System.out.println("----------  dbvalue screen cursor count -- "
				+ cursor.getCount());

		if (cursor != null && cursor.getCount() > 0) {

			cursor.close();
			/*sql.close();
			db1.close();*/
			return true;

		} else {

			cursor.close();
			/*sql.close();
			db1.close();*/
			return false;
		}

	}

	/*private void updatelist() {
		final ArrayList<NonrepeatedAdHelper> searchResults = GetDetail();
		nonrepeated.setAdapter(new NonrepeatedAdAdapt(this, searchResults));
	}*/

	/*private ArrayList<NonrepeatedAdHelper> GetDetail() {
		ArrayList<NonrepeatedAdHelper> results = new ArrayList<NonrepeatedAdHelper>();
		DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = Stationname;
		Cursor c = sql
				.rawQuery(
						"SELECT * FROM NonrepeatedAd  where InstallationDesc=? ORDER BY AdvertisementCode",
						params);
		if (c.getCount() == 0) {
			NonrepeatedAdHelper sr = new NonrepeatedAdHelper();
			sr.setadvcode("");
			sr.setadvName("");
			sr.setinstallationname("");

			sr.setdateFrom("");
			sr.setdateTo("");
			sr.settimefrom("");
			sr.settimeto("");
			sr.setfirstdate("");
			sr.setfirsttime("");
			sr.setlastdate("");
			sr.setlasttime("");
			sr.setmasterrecord("");
			sr.setdetailrecord("");
			sr.setclipmaster("");
			results.add(sr);

			c.close();
			sql.close();
			db.close();

			return results;
		} else {

			c.moveToFirst();

			int column = 0;
			do {

				NonrepeatedAdHelper sr = new NonrepeatedAdHelper();

				sr.setadvcode(c.getString(c.getColumnIndex("InstallationDesc")));

				sr.setadvName(c.getString(c.getColumnIndex("AdvertisementCode")));
				sr.setinstallationname(c.getString(c
						.getColumnIndex("AdvertisementDesc")));
				int column1 = c.getColumnIndex("EffectiveDateFrom");
				int column2 = c.getColumnIndex("EffectiveDateTo");
				String tf = c.getString(column1);
				String tfhr = c.getString(column1);
				String tfto = c.getString(column2);
				String tftohr = c.getString(column2);
				String tftym = c.getString(column1);
				String tftotym = c.getString(column2);
				String[] tym = splitfromtym(tftym);
				String[] v1 = splitfrom(tf);
				String[] v1hr = splitfromhr(tfhr);
				String[] v2 = splittodate(tfto);
				String[] v2hr = splittodatehr(tftohr);
				int column3 = c.getColumnIndex("FirstReportingDate");
				int column4 = c.getColumnIndex("LatestAddeDate");
				String fd = c.getString(column3);
				String fdhr = c.getString(column3);

				try {
					String datestr = fd.substring(0, tf.lastIndexOf(" ") + 1);
					int a = fd.indexOf(" ");
					// 08-Sep-2015 03:15:49 PM
					// Oct 15 2015 4:41PM
					DateFormat formatter;
					Date date;
					formatter = new SimpleDateFormat("MMM dd yyyy");
					date = (Date) formatter.parse(datestr);
					SimpleDateFormat dateformatyyyyMMdd = new SimpleDateFormat(
							"MM/dd/yyyy");

					fd = dateformatyyyyMMdd.format(date);
					// + fd.substring(fd.indexOf(" "));
					fdhr = fd;
				} catch (Exception e) {
				}

				String ld = c.getString(column4);
				String ldhr = c.getString(column4); // May 20 2016 10:10AM
				// ......kk
				if (fd.equals("")) {
					sr.setfirstdate("No info");

				} else {
					String[] vfd = splitfromdate(fd);
					sr.setfirstdate(vfd[0]);
				}
				if (fdhr.equals("")) {
					sr.setfirsttime("No info");

				} else {

					String[] vfhr = splitfromfhr(fdhr);
					sr.setfirsttime(vfhr[0]);
				}
				if (ld.equals("")) {
					sr.setlastdate("No info");

				} else {
					String[] lfd = splitfrommmm(ld);
					sr.setlastdate(lfd[0]);
				}
				if (ldhr.equals("")) {
					sr.setlasttime("No info");

				} else {

					String[] llhr = splitfromlasthr(ldhr);
					sr.setlasttime(llhr[0]);
				}
				sr.setCSR(c.getString(c.getColumnIndex("CSR")));
				sr.setdateFrom(v1[0]);
				sr.settimefrom(v1hr[0]);
				sr.setdateTo(v2[0]);
				sr.settimeto(v2hr[0]);
				sr.setmasterrecord(c.getString(c
						.getColumnIndex("IsmasterRecordDownloaded")));
				sr.setdetailrecord(c.getString(c
						.getColumnIndex("IsDetailRecordDownloaded")));
				sr.setclipmaster(c.getString(c
						.getColumnIndex("IsClipMasterRecordDownloaded")));
				results.add(sr);

			} while (c.moveToNext());

			c.close();
			sql.close();
			db.close();
		}
		return results;

	}*/

	private void fetchdata() {
		// TODO Auto-generated method stub
		new DownloadxmlsDataURL_new().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();

			String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetBGPlaylistContent?MobileNo="
					+ mobno;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				String responsemsg = ut.httpGet(url);
				Log.e("csn status", "resmsg : " + responsemsg);
				//sql.execSQL("DROP TABLE IF EXISTS Backgroundplaylist");
				//sql.execSQL(ut.Databg());
				sql.delete("Backgroundplaylist",null,null);

				Cursor cur = sql.rawQuery("SELECT *   FROM Backgroundplaylist",
						null);
				Log.e("Table values----", "" + cur.getCount());
				if (responsemsg.contains("<PlaylistName>")) {
					sop = "valid";
					String columnName, columnValue;
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table");
					Log.e("sts main...", " fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						String conn = "invalid";
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);
							columnValue = ut.getValue(e, columnName);
							values1.put(columnName, columnValue);
							Log.e("DownloadxmlsDataURL_new...on back....",
									" count i: " + i + "  j:" + j);
						}

						sql.insert("Backgroundplaylist", null, values1);
					}

					cur.close();
					/*sql.close();
					db.close();*/

				} else {
					sop = "invalid";
					System.out
							.println("--------- invalid for project list --- ");
				}

			} catch (IOException e) {
				e.printStackTrace();
				sop = "UnDefined";
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
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(com.stavigilmonitoring.R.id.button_prog_bg_data))
						.setVisibility(View.GONE);
				if (sop.equals("valid")) {
					updateLink();
				} else if (sop.equals("invalid")) {
					try{
						ut.showD(BackgroundPlaylistDetail.this, "NoPlay");
					}catch (Exception e){
						e.printStackTrace();
					}
				} else if (sop.equals("UnDefined")) {
					try{
						ut.showD(BackgroundPlaylistDetail.this, "ServerError");
					}catch (Exception e){
						e.printStackTrace();
					}
				} else {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Irrelevent error occurred", Toast.LENGTH_SHORT);
					toast.show();
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
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.button_prog_bg_data))
					.setVisibility(View.VISIBLE);
		}

	}

	private void updaterefreshdate() {
		// TODO Auto-generated method stub

		Calendar c = Calendar.getInstance();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = df.format(c.getTime());

		System.out.println("------ curdaterefresh " + formattedDate);

		String[] aDate = { formattedDate };

		DBInterface db = new DBInterface(getBaseContext());
		db.SetDaterefresh(aDate);
		db.Close();

		filldaterefresh();

	}

	/*protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(NonrepeatedAd.this);
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
			txt.setText("No Refresh Data Available. Please check Internet Connection");
		} else if (string.equals("nodata")) {
			myDialog.setTitle(" ");
			txt.setText("No data available");
		}
		Button btn = (Button) myDialog
				.findViewById(R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();
				// finish();

			}
		});

		myDialog.show();

	}*/

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

	private void filldaterefresh() {
		// TODO Auto-generated method stub

		System.out.println("-------  filldateref " + daterestr);

		if (daterestr.equals("1")) {
			txtdate.setVisibility(View.INVISIBLE);
			txtdaterefresh.setVisibility(View.INVISIBLE);
		} else {

			try {

				String olddate = getolddate();

				System.out.println("-------  olddate " + olddate);

				Calendar c = Calendar.getInstance();

				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String formattedDate = df.format(c.getTime());

				System.out.println("------ curdaterefresh " + formattedDate);
				String diff = getTimeDiff(olddate, formattedDate);
				System.out.println("----- ##### " + diff);

				if ((diff.contains("seconds ago"))
						|| (diff.contains("minutes ago"))) {
					txtdate.setVisibility(View.INVISIBLE);
					txtdaterefresh.setVisibility(View.INVISIBLE);

				} else {
					System.out.println("----- ##### 2 " + diff);

					if (diff.equals("yesterday")) {
						String refdate = "1 day old data";
						txtdate.setText(refdate);
					} else if (diff.contains("ago")) {

						String[] sar = diff.split(" ");
						String a = sar[0].toString();
						int i = Integer.parseInt(a);

						if (i > 8) {
							txtdate.setText(" 1 day old data");
						} else {
							String ref[] = diff.split("ago");

							String refdate = ref[0].toString();
							System.out.println("--- #### refdate " + refdate);

							txtdate.setText(refdate + "old data");
						}

					} else {
						txtdate.setText(diff + "old data");
					}
				}

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

		}

	}

	private String getTimeDiff(String time, String curTime)
			throws ParseException {
		DateFormat formatter;
		Date curDate;
		Date oldDate;
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		curDate = (Date) formatter.parse(curTime);
		oldDate = (Date) formatter.parse(time);
		long oldMillis = oldDate.getTime();
		long curMillis = curDate.getTime();
		// Log.d("CaseListAdapter", "Date-Milli:Now:"+curDate.toString()+":"
		// +curMillis +" old:"+oldDate.toString()+":" +oldMillis);
		CharSequence text = DateUtils.getRelativeTimeSpanString(oldMillis,
				curMillis, 0);
		return text.toString();
	}

	private String getolddate() {
		// TODO Auto-generated method stub

		DBInterface dbi = new DBInterface(getBaseContext());
		String dateref = dbi.GetDateRefresg();
		dbi.Close();
		return dateref;
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		// finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		/*
		 * Bundle dataBundle = new Bundle(); dataBundle.putString("stnname",
		 * Stationname);
		 * 
		 * Intent i = new Intent(NonrepeatedAd.this, NonrepeatedAdMain.class);
		 * 
		 * i.putExtras(dataBundle); startActivity(i);
		 */

	}



}

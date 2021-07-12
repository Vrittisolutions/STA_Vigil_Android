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

import com.adapters.AdvNonrepeatedAdAdapt;
import com.beanclasses.NonrepeatedAdHelper;
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
import android.os.StrictMode;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AdvNonRepAdvList extends Activity {

	ListView workspacewisedetail;
	String mobno, link;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	static SimpleDateFormat dff;
	static String Ldate;
	ImageView iv;

	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;

	private ListView nonrepeated;
	private String Network;
	private TextView nonreprtedstnname;
	private TextView servertime;
	private TextView dateday;
	String flag = "";
	ArrayList<NonrepeatedAdHelper> searchResults;
	private TextView starttime;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.nonrepadsdisplay);

		Intent extras = getIntent();
		Network = extras.getStringExtra("NetCode");

		nonreprtedstnname = (TextView) findViewById(com.stavigilmonitoring.R.id.netcode);
		nonreprtedstnname.append("-" + Network);
		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_nonrepeated);
		nonrepeated = findViewById(com.stavigilmonitoring.R.id.nonrepadvlist);
		searchResults = new ArrayList<NonrepeatedAdHelper>();

		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		if (dbvalue()) {
			// updateLink();
			updatelist();
		} else {
			fetchdata();
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

		nonrepeated.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				NonrepeatedAdHelper item = searchResults.get(arg2);
				Intent dataBundle = new Intent(getApplicationContext(),AdvNonRepStationList.class);
				dataBundle.putExtra("AdvCode", item.getadvcode());
				dataBundle.putExtra("AdvName", item.getadvName());
		        startActivity(dataBundle);
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

	private boolean dbvalue() {
		// TODO Auto-generated method stub
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = null;
		try {
			cursor = sql.rawQuery("SELECT *   FROM NonrepeatedAd", null);

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
		} catch (Exception e) {
			cursor.close();
			/*sql.close();
			db1.close();*/
			return false;
		}

	}

	private void updatelist() {
		searchResults = GetDetail();
		nonrepeated.setAdapter(new AdvNonrepeatedAdAdapt(this, searchResults));
	}

	private ArrayList<NonrepeatedAdHelper> GetDetail() {
		ArrayList<NonrepeatedAdHelper> results = new ArrayList<NonrepeatedAdHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = new String[1];
		String[] params1 = new String[1];
		params[0] = Network;
		Cursor c = sql
				.rawQuery(
						"SELECT DISTINCT AdvertisementCode,AdvertisementDesc,EffectiveDateFrom,AdvCnt  FROM NonrepeatedAd  where Type=? ORDER BY AdvertisementCode DESC",
						params);
		if (c.getCount() == 0) {

			try{
				NonrepeatedAdHelper sr = new NonrepeatedAdHelper();
				sr.setadvcode("");
				sr.setadvName("");
				sr.setAdvCnt("");
				sr.setdateFrom("");
				sr.settimefrom("");
				sr.setcsncount("");
				results.add(sr);

				c.close();
			/*sql.close();
			db.close();*/
			}catch (Exception e){
				e.printStackTrace();
			}
			return results;
		} else {

			try{
				c.moveToFirst();
				int column = 0;
				do {

					NonrepeatedAdHelper sr = new NonrepeatedAdHelper();
					sr.setadvcode(c.getString(c.getColumnIndex("AdvertisementCode")));// setadvcode
					sr.setadvName(c.getString(c.getColumnIndex("AdvertisementDesc")));
					sr.setAdvCnt(c.getString(c.getColumnIndex("AdvCnt")));
					int column1 = c.getColumnIndex("EffectiveDateFrom");

					String tf = c.getString(column1);
					String tfhr = c.getString(column1);
					String tftym = c.getString(column1);
					String[] tym = splitfromtym(tftym);
					String[] v1 = splitfrom(tf);
					String[] v1hr = splitfromhr(tfhr);
					sr.setdateFrom(v1[0]);
					sr.settimefrom(v1hr[0]);

					params1[0] = c.getString(c.getColumnIndex("AdvertisementCode"));
					Cursor c1 = sql
							.rawQuery(
									"SELECT StationMasterId FROM NonrepeatedAd where AdvertisementCode=?",
									params1);
					sr.setcsncount(""+c1.getCount());

					results.add(sr);

				} while (c.moveToNext());

				c.close();
			/*sql.close();
			db.close();*/
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return results;

	}

	private String[] splitfrom(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";

		String k = tf.substring(0, tf.length() - 9);
		System.out.println("---value of k..." + k);
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy");
		String finalDate = timeFormat.format(myDate);

		System.out.println("time------" + fromtimetw);
		// String[] v1 = { finalDate };
		String[] v1 = { finalDate };

		return v1;
	}

	private String[] splitfromhr(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";
		System.out.println("---21111111111111111111...." + tf);
		String k = tf.substring(11, tf.length() - 0);
		System.out.println("---value of khr..." + k);
		String[] v1hr = { k };

		return v1hr;
	}

	private String[] splitfromtym(String tym) {
		// TODO Auto-generated method stub
		System.out.println("---value of tym...." + tym);
		String fromtimetw = "";
		String k = tym.substring(0, tym.length() - 9);
		System.out.println("---value of kym..." + k);
		String m = k.replace("T", " ");
		System.out.println("---value of mym..." + m);// 09/14/2015
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String diffTym = null;
		try {
			Date datestop = sdf.parse(m);

			// final String dateStart = m;
			// final String dateStop = "01/15/2012 10:31:48";
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			final String dateStart = dateFormat.format(datestop);

			Date date = new Date();
			// System.out.println("date format of system......................"+dateFormat.format(date));
			final String dateStop = dateFormat.format(date);
			Date d1 = null;
			Date d2 = null;

			try {
				d1 = dateFormat.parse(dateStart);
				d2 = dateFormat.parse(dateStop);

				// in milliseconds
				long diff = d2.getTime() - d1.getTime();

				long diffSeconds = diff / 1000 % 60;
				long diffMinutes = diff / (60 * 1000) % 60;
				long diffHours = diff / (60 * 60 * 1000) % 24;
				long diffDays = diff / (24 * 60 * 60 * 1000);
				diffTym = diffDays + " Days " + diffHours + " Hours "
						+ diffMinutes + " Minutes " + diffSeconds + " Seconds";
				System.out.print(diffDays + " days, ");
				System.out.print(diffHours + " hours, ");
				System.out.print(diffMinutes + " minutes, ");
				System.out.print(diffSeconds + " seconds.");

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

		} catch (ParseException e) {
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

		}
		String[] s = { diffTym };
		return s;
	}

	private void fetchdata() {
		// TODO Auto-generated method stub

		new DownloadxmlsDataURL_new().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetNonReportedAdvt_Android_new?Mobile="
					+ mobno;

			url = url.replaceAll(" ", "%20");

			try {
				responsemsg = ut.httpGet(url);
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

			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();

			//sql.execSQL("DROP TABLE IF EXISTS NonrepeatedAd");
			//sql.execSQL(ut.getNonrepeatedAd());
			sql.delete("NonrepeatedAd",null,null);

			Cursor c = sql.rawQuery("SELECT *  FROM NonrepeatedAd", null);
			if (responsemsg.contains("<A>")) {
				sop = "valid";

				ContentValues values = new ContentValues();
				NodeList nl = ut.getnode(responsemsg, "Table1");
				String msg = "";
				String columnName, columnValue;
				for (int i = 0; i < nl.getLength(); i++) {
					Element e = (Element) nl.item(i);
					for (int j = 0; j < c.getColumnCount(); j++) {
						columnName = c.getColumnName(j);

						String ncolumnname = "";
						if (columnName.equalsIgnoreCase("StationMasterId"))
							ncolumnname = "A";
						else if (columnName
								.equalsIgnoreCase("AdvertisementCode"))
							ncolumnname = "B";
						else if (columnName
								.equalsIgnoreCase("AdvertisementDesc"))
							ncolumnname = "C";
						else if (columnName
								.equalsIgnoreCase("InstallationDesc"))
							ncolumnname = "D";
						else if (columnName
								.equalsIgnoreCase("EffectiveDateFrom"))
							ncolumnname = "E";
						else if (columnName.equalsIgnoreCase("EffectiveDateTo"))
							ncolumnname = "F";
						else if (columnName.equalsIgnoreCase("Type"))
							ncolumnname = "G";
						else if (columnName.equalsIgnoreCase("ClipId"))
							ncolumnname = "H";
						else if (columnName
								.equalsIgnoreCase("IsmasterRecordDownloaded"))
							ncolumnname = "I";
						else if (columnName
								.equalsIgnoreCase("IsDetailRecordDownloaded"))
							ncolumnname = "J";
						else if (columnName
								.equalsIgnoreCase("IsClipMasterRecordDownloaded"))
							ncolumnname = "K";
						else if (columnName
								.equalsIgnoreCase("InstallationCount"))
							ncolumnname = "L";
						else if (columnName.equalsIgnoreCase("LastServerTime"))
							ncolumnname = "M";
						else if (columnName
								.equalsIgnoreCase("FirstReportingDate")) // FirstReportingDate
							ncolumnname = "N";
						else if (columnName.equalsIgnoreCase("LatestAddeDate"))
							ncolumnname = "O";
						else if (columnName.equalsIgnoreCase("CSR"))
							ncolumnname = "CSR";
						else if (columnName.equalsIgnoreCase("LA"))
							ncolumnname = "LA";
						else if (columnName.equalsIgnoreCase("LB"))
							ncolumnname = "LB";
						else if (columnName.equalsIgnoreCase("LBR"))
							ncolumnname = "LBR";
						else if (columnName.equalsIgnoreCase("AdvCnt"))
							ncolumnname = "AdvCnt";

						columnValue = ut.getValue(e, ncolumnname);
						values.put(columnName, columnValue);
					}

					sql.insert("NonrepeatedAd", null, values);
					Log.d("test", "data :" + values);
				}

				c.close();
				/*sql.close();
				db.close();*/

			} else {
				sop = "invalid";
				System.out.println("--------- invalid for AD list --- ");
				/*sql.close();
				db.close();*/
			}
			//sql.close();
			//db.close();

			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// pd.cancel();
			if (sop.equals("valid")) {
				//updateLink();
				updatelist();
			} else {
				showD("nodata");
			}
			iv.setVisibility(View.VISIBLE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.GONE);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}
	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(AdvNonRepAdvList.this);
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
			txt.setText("No Refresh Data Available. Please check Internet Connection");
		} else if (string.equals("nodata")) {
			myDialog.setTitle(" ");
			txt.setText("No data available");
		}
		Button btn = (Button) myDialog
				.findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();
				// finish();

			}
		});

		myDialog.show();

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



}

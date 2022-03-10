package com.stavigilmonitoring;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.BackgroundPlayAdp;
import com.beanclasses.StatelevelList;
import com.database.DBInterface;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BackgroundPlaylistAll extends Activity {

	BackgroundPlayAdp listAdapter;
	ListView workspacewisedetail;
	String mobno, link;
	// ArrayList<String>arrlist=new ArrayList<String>();
	List<StatelevelList> list_data = new ArrayList<StatelevelList>();
	AsyncTask depattask;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv;
	static SimpleDateFormat dff;
	static String Ldate;
	static DownloadxmlsDataURL_new asyncfetch_non;
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;
	private ListView bg_list;
	private String st, Type;
	private String cou;
	private String sv;
	String z = "";
	private String z1;
	private int nonreportadCount = 0;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.nonrepeatedadmain);

		Intent extras = getIntent();
		Type = extras.getStringExtra("Type");

		((TextView) findViewById(com.stavigilmonitoring.R.id.onactivitynamereassign))
				.setText("Background Playlist -" + Type);
		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_nonrepeated_main);
		bg_list = (ListView) findViewById(com.stavigilmonitoring.R.id.nonrepeatedadmain);

		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();

		if (asyncfetch_non != null
				&& asyncfetch_non.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

		if (dbvalue()) {
			updatelist();
		} else if (isnet()) {
			fetchdata();
		} else {
			showD("nonet");
		}

		((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
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

						listAdapter
								.filter(((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
										.getText().toString().trim()
										.toLowerCase(Locale.getDefault()));
					}
				});

		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (isnet()) {
					asyncfetch_non = null;
					asyncfetch_non = new DownloadxmlsDataURL_new();
					asyncfetch_non.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

					// fetchdata();
				} else {
					showD("nonet");
				}

			}
		});

		/*bg_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {

				StatelevelList item = list_data.get(position);
				Intent dataBundle = new Intent();
				dataBundle.putExtra("InstallationID", item.getInstallationId());
				dataBundle.putExtra("StationName", item.getStatioName());
				dataBundle.setClass(getApplicationContext(),
						BackgroundPlaylistData.class);
                startActivity(dataBundle);

			}
		});*/
	}

	private void updatelist() {
		// TODO Auto-generated method stub

		list_data.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor c = sql
				.rawQuery(
						"SELECT DISTINCT  InstalationId,InstallationDesc FROM Backgroundplaylist where NetworkCode='"
								+ Type + "' ORDER BY InstallationDesc", null);

		if (c.getCount() == 0) {
			//db.close();
			/*sql.close();
			c.close();*/
		} else {
			c.moveToFirst();
			do {
				int stncnt = 0;
				String TypeID = c.getString(0);
				String Type = c.getString(1);

				Cursor c1 = sql.rawQuery(
						"SELECT MAX(ServerTime) FROM  Backgroundplaylist WHERE InstalationId='"
								+ c.getString(0) + "'", null);
				String srt = "";
				if (c1.getCount() > 0) {
					c1.moveToFirst();
					do {

						srt = c1.getString(0);

					} while (c1.moveToNext());
				}
				StatelevelList sitem = new StatelevelList();
				String diffstr = "";
				try {
					// srt = splitfrom(srt);// 2016-08-03T18:06:08.42+05:30

					System.out.println("---value of tf...." + srt);
					String fromtimetw = "";
					String k = srt.substring(0, srt.indexOf("."));// 2016-08-03T18:06:08.42+05:30
					System.out.println("---value of k..." + k);
					k = k.replace("T", " ");
					sitem.setServertime(k);
					Calendar cal = Calendar.getInstance();
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");// "2016-08-04 14:33:32" (at
													// offset 4)

					Date Startdate = format.parse(k);
					Date Enddate = cal.getTime();
					long diff = Enddate.getTime() - Startdate.getTime();
					long diffSeconds = diff / 1000 % 60;
					long diffMinutes = diff / (60 * 1000) % 60;
					long diffHours = diff / (60 * 60 * 1000) % 24;
					long diffDays = diff / (24 * 60 * 60 * 1000);

					if (diffDays == 0 && diffHours == 0 && diffMinutes <= 15) {
						diffstr = "Connected";
					} else {

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
					}

				} catch (Exception e) {

					e.printStackTrace();
				}

				sitem.setStatioName(Type);
				sitem.setInstallationId(TypeID);
				sitem.setTimeData(diffstr);

				list_data.add(sitem);

			} while (c.moveToNext());

		}
		/*
		 * Log.e("connection sts main", "cursor res : " + list_data.size());
		 * SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 * try { for (int i = 0; i < list_data.size(); i++) { for (int j = i +
		 * 1; j < list_data.size(); j++) { Date s1 =
		 * dff.parse(list_data.get(i).getServertime()); Date s2 =
		 * dff.parse(list_data.get(j).getServertime()); if (s1.compareTo(s2) >
		 * 0) { StatelevelList ci = list_data.get(i); StatelevelList cj =
		 * list_data.get(j); list_data.remove(i); list_data.add(i, cj);
		 * 
		 * list_data.remove(j); list_data.add(j, ci); } } } } catch (Exception
		 * ex) { dff = new SimpleDateFormat("HH:mm:ss"); Ldate = dff.format(new
		 * Date());
		 * 
		 * StackTraceElement l = new Exception().getStackTrace()[0];
		 * System.out.println(l.getClassName() + "/" + l.getMethodName() + ":" +
		 * l.getLineNumber()); ut = new utility(); if (!ut.checkErrLogFile()) {
		 * 
		 * ut.ErrLogFile(); } if (ut.checkErrLogFile()) {
		 * ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":" +
		 * l.getLineNumber() + "	" + ex.getMessage() + " " + Ldate); }
		 * 
		 * }
		 */
		listAdapter = new BackgroundPlayAdp(BackgroundPlaylistAll.this,
				list_data);
		bg_list.setAdapter(listAdapter);

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

	private boolean dbvalue() {
		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery(
					"SELECT *   FROM Backgroundplaylist WHERE NetworkCode='"
							+ Type + "'", null);

			System.out.println("--  dbvalue screen cursor count -- "
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

	public void FilterClick(View v) {
		if (((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext)).getVisibility() == View.VISIBLE) {
			((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
					.setVisibility(View.GONE);
		} else if (((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext)).getVisibility() == View.GONE) {
			((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
					.setVisibility(View.VISIBLE);
			EditText textView = (EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext);
			textView.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
		}

	}

	private static class MySpinnerAdapter extends ArrayAdapter<String> {
		// Initialise custom font, for example:
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"font/BOOKOS.TTF");

		private MySpinnerAdapter(Context context, int resource,
				List<String> items) {
			super(context, resource, items);
		}

		// Affects default (closed) state of the spinner
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = (TextView) super.getView(position, convertView,
					parent);
			view.setTypeface(font);
			return view;
		}

		// Affects opened state of the spinner
		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			TextView view = (TextView) super.getDropDownView(position,
					convertView, parent);
			view.setTypeface(font);
			return view;
		}
	}

	private String splitfrom(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.indexOf("."));// 2016-08-03T18:06:08.42+05:30
		System.out.println("---value of k..." + k);
		k = k.replace("T", " ");
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
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
		SimpleDateFormat timeFormat = new SimpleDateFormat(
				"dd MMM yyyy hh:mm:ss aa");
		String finalDate = timeFormat.format(myDate);

		System.out.println("time------" + fromtimetw);
		// String[] v1 = { finalDate };
		String[] v1 = { finalDate };

		return finalDate;
	}

	private void fetchdata() {

		asyncfetch_non = new DownloadxmlsDataURL_new();
		asyncfetch_non.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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
							Log.e("DownloadxmlsDataURL_new...on back...."," count i: " + i + "  j:" + j);
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
				((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
						.setVisibility(View.GONE);
				if (sop.equals("valid")) {
					updatelist();
				} else if (sop.equals("invalid")) {
					ut.showD(BackgroundPlaylistAll.this, "NoPlay");
				} else if (sop.equals("UnDefined")) {
					ut.showD(BackgroundPlaylistAll.this, "ServerError");
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
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
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
		//db.Close();

		filldaterefresh();

	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(BackgroundPlaylistAll.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);

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
			txt.setText("No Refresh Data Available.Please check internet connection...");
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

	}

}

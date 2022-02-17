package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.BackgroundPlayfiltetrAdp;
import com.beanclasses.StatelevelList;
import com.database.DBInterface;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BckgroundPlaylistStatefilter extends Activity {

	private List<StatelevelList> searchResults;
	private ImageView iv;
	private LinearLayout mAllnet;
	private ProgressBar Prog;
	private TextView head,mText,mAllCount;
	private String sop, mobno, Network;
	static DownloadxmlsDataURL_new asyncfetch_bgplay_filter;
	private GridView lstcsn;
	private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	static SimpleDateFormat dff;
	static String Ldate;
	private int scount= 0;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.connectionsatusstatefilter);

		searchResults = new ArrayList<StatelevelList>();
		lstcsn =  findViewById(com.stavigilmonitoring.R.id.listFiterSate);
		head = (TextView) findViewById(com.stavigilmonitoring.R.id.connectionfilter);
		Prog = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.ConnectFilterState);
		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_connect_filter);
		mAllnet = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.networksumconn);
		mText = (TextView) findViewById(com.stavigilmonitoring.R.id.tvNameconn);
		mAllCount = (TextView) findViewById(com.stavigilmonitoring.R.id.tvCntcon);

		Intent intent = getIntent();
		Network = intent.getStringExtra("Type");
	    head.setText("Background Playlist -" + Network);
		mText.setText(Network + "-All");

		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();
	
	
	
		if (asyncfetch_bgplay_filter != null
				&& asyncfetch_bgplay_filter.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			iv.setVisibility(View.GONE);
			Prog.setVisibility(View.VISIBLE);
		}

		if (dbvalue()) {
			updatelist();
		} else if (ut.isnet(getApplicationContext())) {
			fetchdata();
		} else {
			ut.showD(BckgroundPlaylistStatefilter.this, "nonet");
		}

		lstcsn.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent(getApplicationContext(), BackgroundPlaylist.class);
				intent.putExtra("Type", Network);
				intent.putExtra("subType", searchResults.get(position).getSubNetworkcode());
				startActivity(intent);
			}

		});

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ut.isnet(getApplicationContext())) {
					asyncfetch_bgplay_filter = null;
					asyncfetch_bgplay_filter = new DownloadxmlsDataURL_new();
					asyncfetch_bgplay_filter.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					// fetchdata();
				} else {
					ut.showD(BckgroundPlaylistStatefilter.this, "nonet");
				}
			}
		});
		
		mAllnet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						BackgroundPlaylistAll.class);
				intent.putExtra("Type", Network);
				startActivity(intent);

			}
		});
	}

	

	

	private boolean dbvalue() {
		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery("SELECT * FROM Backgroundplaylist WHERE NetworkCode='"+Network+"'",
					null);
			if (cursor != null && cursor.getCount() > 0) {

				cursor.close();
				/*sql.close();
				db1.close();*/
				return true;

			} else {
				cursor.close();
				/*sql.close();
				db1.close();
*/
			}
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
		return false;
	}

	private void fetchdata() {
		// new DownloadxmlsDataURL_new().execute();
		if (asyncfetch_bgplay_filter == null) {
			iv.setVisibility(View.VISIBLE);
			Prog.setVisibility(View.GONE);

			Log.e("async", "null");
			asyncfetch_bgplay_filter = new DownloadxmlsDataURL_new();
			asyncfetch_bgplay_filter.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			if (asyncfetch_bgplay_filter.getStatus() == AsyncTask.Status.RUNNING) {
				Log.e("async", "running");
				iv.setVisibility(View.GONE);
				Prog.setVisibility(View.VISIBLE);
			}
		}

	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();

			String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetBGPlaylistContent?MobileNo="
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
				Prog.setVisibility(View.GONE);
				if (sop.equals("valid")) {
					updatelist();
				} else if (sop.equals("invalid")) {
					ut.showD(BckgroundPlaylistStatefilter.this, "NoPlay");
				} else if (sop.equals("UnDefined")) {
					ut.showD(BckgroundPlaylistStatefilter.this, "ServerError");
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
			Prog.setVisibility(View.VISIBLE);
		}

	}

	private void updatelist() {
		searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor c = sql.rawQuery(
				"SELECT DISTINCT SubNetworkCode FROM Backgroundplaylist where NetworkCode='"
						+ Network + "' ORDER BY SubNetworkCode", null);

		if (c.getCount() == 0) {
			/*db.close();
			sql.close();*/
			c.close();
		} else {
			c.moveToFirst();
			do {
				int stncnt = 0;
				String Type = c.getString(0);//DISTINCT

				Cursor c1 = sql
						.rawQuery(
								"SELECT DISTINCT InstalationId,InstallationDesc FROM Backgroundplaylist WHERE SubNetworkCode='"
										+ c.getString(0)
										+ "' ORDER BY SubNetworkCode Desc",
								null);

				if (c1.getCount() > 0) {
					c1.moveToFirst();
					do {
						int column1 = c1.getColumnIndex("InstalationId");

						try {

							String srt = c1.getString(column1);

							stncnt = stncnt + 1;

						} catch (Exception ex) {
							dff = new SimpleDateFormat("HH:mm:ss");
							Ldate = dff.format(new Date());

							StackTraceElement l = new Exception()
									.getStackTrace()[0];
							System.out.println(l.getClassName() + "/"
									+ l.getMethodName() + ":"
									+ l.getLineNumber());
							ut = new utility();
							if (!ut.checkErrLogFile()) {

								ut.ErrLogFile();
							}
							if (ut.checkErrLogFile()) {
								ut.addErrLog(l.getClassName() + "/"
										+ l.getMethodName() + ":"
										+ l.getLineNumber() + "	"
										+ ex.getMessage() + " " + Ldate);
							}
						}

					} while (c1.moveToNext());
				}
				Type = Type.replaceAll("0", "");
				Type = Type.replaceAll("1", "");
				if (!Type.trim().equalsIgnoreCase("")) {
					StatelevelList sitem = new StatelevelList();
					sitem.setSubNetworkcode(Type);
					sitem.Setcount(stncnt);
					searchResults.add(sitem);
				}
			} while (c.moveToNext());

		}
		scount = 0;
		for (int i = 0; i < searchResults.size(); i++)
			scount = scount + searchResults.get(i).Getcount();
			mAllCount.setText(""+scount);
		lstcsn.setAdapter(new BackgroundPlayfiltetrAdp(
				BckgroundPlaylistStatefilter.this, searchResults));
	}

	

}

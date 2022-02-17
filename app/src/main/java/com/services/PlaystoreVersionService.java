package com.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.database.DBInterface;
import com.stavigilmonitoring.Common;
import com.stavigilmonitoring.DatabaseHandler;
import com.stavigilmonitoring.MyDialog;
import com.stavigilmonitoring.PlayStoreVersionDialogActivity;
import com.stavigilmonitoring.R;
import com.stavigilmonitoring.utility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.stavigilmonitoring.utility.OpenConnection;

public class PlaystoreVersionService extends Service {

	private static long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	public static long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
	private static long FASTEST_INTERVAL = 1000 * 60 * 2;
	private Handler mHandler = new Handler();
	private Timer mtimer = null;
	//String dialogopen="no";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		StartTimer(MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES);

		/*if (!Common.FlagDownloadgpsdetail) {
			new DownloadRefreshTime().execute();
		}else {
			StartTimer(MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES);

		}*/
	}

	public void StartTimer(long TimeInterval, long Distance) {
		if (mtimer != null) {
			// mtimer.cancel();
		} else {
			mtimer = new Timer();
			// Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
			mtimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, TimeInterval);
		}

	}

	class DownloadRefreshTime extends AsyncTask<Integer, Void, String> {
		String res;
		String RefershTime, distanceAccuracy;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();


		}

		@Override
		protected String doInBackground(Integer... params) {
			String url = Common.CompanyURL + Common.api_getdata;
			try {
				res = utility.OpenConnection(url/*, getApplicationContext()*/);
				res = res.replaceAll("\\\\", "");
				res = res.substring(1, res.length() - 1);
				ContentValues values = new ContentValues();
				JSONArray jResults = new JSONArray(res);
				String msg = "";
				String columnName, columnValue;
				JSONObject jorder = jResults.getJSONObject(0);
				RefershTime = jorder.getString("RefreshTime");
				distanceAccuracy = jorder.getString("DistanceAccuracy");

			} catch (Exception e) {
				e.printStackTrace();
				res = "error";
			}
			return res;
		}

		@Override
		protected void onPostExecute(String integer) {
			super.onPostExecute(integer);
			if (integer.contains("RefreshTime")) {
				Common.FlagDownloadgpsdetail = true;
				if (!(distanceAccuracy.equalsIgnoreCase("") && RefershTime.equalsIgnoreCase(""))) {
					int dis;
					int time;
					try {
						dis = Integer.parseInt(distanceAccuracy);
						time = Integer.parseInt(RefershTime);
					} catch (Exception e) {
						e.printStackTrace();
						dis = 10;
						time = 15;
					}

					MIN_DISTANCE_CHANGE_FOR_UPDATES = dis;
					MIN_TIME_BW_UPDATES = 1000 * 60 * time;
				} else {
					MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
					MIN_TIME_BW_UPDATES = 1000 * 60 * 15;
				}

			} else {
				Common.FlagDownloadgpsdetail = false;
				MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
				MIN_TIME_BW_UPDATES = 1000 * 60 * 15;
			}

			StartTimer(MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES);

		}

	}



	private void showUpdateDialog(String PSVersion) {
		try {
			AlertDialog dialog;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("New Update Available");
			builder.setMessage(" New STA Vigil " + PSVersion + " is on Playstore." +
					"\n(Note: In playstore 'OPEN' button is visible instead of 'UPDATE', Uninstall and Install app)");

			builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
							("market://details?id=com.stavigilmonitoring")));
					//dialogopen = "no";
					dialog.dismiss();
				}
			});

			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//background.start();
					//dialogopen = "no";
					dialog.dismiss();
				}
			});


			builder.setCancelable(false);


			dialog = builder.show();
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);


			//dialogopen = "yes";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class TimeDisplayTimerTask extends TimerTask {

		@Override
		public void run() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					/*dfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//yyyy-MM-dd HH:mm:ss
					DateNT = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss aa");
					mcontext = getApplicationContext();
					Calendar cal = Calendar.getInstance();
					int hour = cal.get(Calendar.HOUR_OF_DAY);
					Date date = new Date();
					CharSequence time = DateFormat.format("EEEE", date.getTime());*/
					utility ut = new utility();

					String PlayStoreVersion = null;
					String MyAppVersion = null;
					if(ut.isnet(getApplicationContext())) {
						try {
							MyAppVersion = (getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

							Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id="//com.stavigilmonitoring
									+ "com.stavigilmonitoring").get();
							String AllStr = doc.text();
							String parts[] = AllStr.split("Current Version");
							String newparts[] = parts[1].split("Requires Android");
							PlayStoreVersion = newparts[0].trim();
							if(!MyAppVersion.equals(PlayStoreVersion)){
								if(Common.dialogopen.equalsIgnoreCase("no")) {
								Common.PSVersion = PlayStoreVersion;
								Intent i = new Intent(getApplicationContext(), MyDialog.class);
								startActivity(i);
									//showUpdateDialog(PlayStoreVersion);
								}
							}

						} catch (IOException e) {
							e.printStackTrace();
						} catch (PackageManager.NameNotFoundException e) {
							e.printStackTrace();
						}catch (NullPointerException e){
							e.printStackTrace();
						}catch (Exception e){
							e.printStackTrace();
						}
					}

					/*if (!(time.equals("Sunday"))) {
						if (hour > 8 && hour < 20) {
							if (isGooglePlayServicesAvailable()) {
								getLocationPlayservice();
							}
						}
					}*/
				}
			});

		}

	}
}

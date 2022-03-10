package com.stavigilmonitoring;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings.Secure;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.database.DBInterface;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.google.firebase.iid.FirebaseInstanceId;
import com.receiver.AlarmManagerBroadcastReceiver;
import com.receiver.AlarmManagerBroadcastReceiverGPG;
import com.receiver.SoundLevelBrodcastReciver;
import com.services.JobService_DMCertificate;
import com.services.JobService_SyncDataCount;
import com.services.JobService_Test;
import com.services.MyFirebaseInstanceIDService;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class RegistrationUS extends Activity {

	public static FirebaseJobDispatcher dispatcher ;
	public static Job myJob = null;
	boolean AppCommon = false;

	public static DefaultHttpClient httpClient = new DefaultHttpClient();
	String paramToken = "";
	String urlStringToken = "";
	EditText editTextUrl, editTextlogin, editTextpassword, editTextMobile;
	Button send;
	String num, PERMISSION = "";
	String[] _Option1 = {"1", "1", "1", "1"};
	public static final String SERVER_NO = "54646";
	IntentFilter intentfilter = new IntentFilter();
	static com.stavigilmonitoring.utility ut;
	Dialog myDialog;
	MyCounter timer;
	static SimpleDateFormat dff;
	static String Ldate;
	ProgressBar pb;
	TextView txt1, txt2;
	Button btn;
    Dialog dialog;
	ProgressDialog pdreg;
	String responsemsg = "k", mobno, UserName;
	AsyncTask async;
	public static String regId;
	GoogleCloudMessaging gcm;
	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";
	String strurl, strurldb, strlogin, strpassword, strmobileno,dialogopen="no";
	public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
	Context context;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(com.stavigilmonitoring.R.layout.regus);

		context = RegistrationUS.this;
		editTextlogin = (EditText) findViewById(com.stavigilmonitoring.R.id.edit_login);
		editTextpassword = (EditText) findViewById(com.stavigilmonitoring.R.id.edit_password);
		editTextMobile = (EditText) findViewById(com.stavigilmonitoring.R.id.edit_mobile);
		send = (Button) findViewById(com.stavigilmonitoring.R.id.button_s);

		db = new DatabaseHandler(getBaseContext());

		boolean result = checkPermission();
		if (result) {
			PERMISSION = "Granted";
			createDb();
			DBInterface dbi = new DBInterface(getApplicationContext());
			String no = dbi.GetPhno();
			dbi.Close();

			ut = new com.stavigilmonitoring.utility();
			if (!ut.checkErrLogFile()) {
				ut.ErrLogFile();
			}

			Common.DEVICE_ID = Secure.getString(getBaseContext()
					.getContentResolver(), Secure.ANDROID_ID);
			//registerFCM();
			registerGCM();
			//saveUserNameToDb(UserName);
			if (!no.equals("1")) {    //if (imsi()) {
				SharedPreferences LoginPref = getApplicationContext()
						.getSharedPreferences("SetupPref",Context.MODE_PRIVATE); // 0 - for private mode
				Editor edtcv = LoginPref.edit();
				edtcv.putString("Dialog", "YesDialog");
				edtcv.commit();

				//callforplayStore();

				Intent i = new Intent(getBaseContext(), com.stavigilmonitoring.CSNBannerActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getBaseContext().startActivity(i);
				finish();
			/*	Intent i = new Intent(getBaseContext(), com.stavigilmonitoring.SelectMenu.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getBaseContext().startActivity(i);
				finish();*/
				//} //		else {	////				ShownochageD();//	//			}
			}
		}
	}

    private void showUpdateDialog(String PSVersion) {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New Update Available, Please update latest version");
            builder.setMessage(" New STA Vigil " + PSVersion + " is on Playstore."
                    /*"(Note: In playstore 'OPEN' button is visible instead of 'UPDATE', Uninstall and Install app)"*/);

            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                            ("market://details?id=com.stavigilmonitoring")));
                    dialogopen = "no";
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //background.start();
                    dialogopen = "no";
                    dialog.dismiss();
                }
            });

            builder.setCancelable(false);

            dialog = builder.show();
            dialogopen = "yes";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callforplayStore() {
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
                    if(dialogopen.equalsIgnoreCase("no")) {
                        showUpdateDialog(PlayStoreVersion);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public boolean checkPermission() {
		int currentAPIVersion = Build.VERSION.SDK_INT;
		if (currentAPIVersion >= Build.VERSION_CODES.M) {

			Toast toast = Toast.makeText(context, "PLEASE ALLOW ALL THE PERMISSIONS", Toast.LENGTH_SHORT);
			View view = toast.getView();
			//To change the Background of Toast
			//view.setBackgroundColor(Color.RED);
			// toast.show();

			//Permissions
			int permissionReadPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
			int permissionFinelocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
			int permissionWrtiestorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
			int permissionCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
			int permissionRecordaudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
			//int permissionGetaccount = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
			int permissionReadStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
			int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
			//Permissions end


			List<String> listPermissionsNeeded = new ArrayList<>();
			if (permissionReadPhone != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
			}
			if (permissionFinelocation != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
			}
			if (permissionWrtiestorage != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			}
			if (permissionCoarseLocation != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
			}
			if (permissionRecordaudio != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
			}
			/*if (permissionGetaccount != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(Manifest.permission.GET_ACCOUNTS);
			}*/
			if (permissionReadStorage != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
			}
			if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(Manifest.permission.CAMERA);
			}
			if (!listPermissionsNeeded.isEmpty()) {
				toast.show();
				ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
				return false;
			}
			if (listPermissionsNeeded.isEmpty()) {
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		//PERMISSION="Granted";
		switch (requestCode) {
			case REQUEST_ID_MULTIPLE_PERMISSIONS:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					PERMISSION = "Granted";
					//Toast.makeText(context, "Yes permissions granted", Toast.LENGTH_SHORT).show();
					createDb();

					DBInterface dbi = new DBInterface(getApplicationContext());
					String no = dbi.GetPhno();

					dbi.Close();
					ut = new com.stavigilmonitoring.utility();
					if (!ut.checkErrLogFile()) {

						ut.ErrLogFile();
					}
					registerGCM();

					if (!no.equals("1")) {

						SharedPreferences LoginPref = getApplicationContext()
								.getSharedPreferences("SetupPref",Context.MODE_PRIVATE); // 0 - for private mode
						Editor edtcv = LoginPref.edit();
						edtcv.putString("Dialog", "YesDialog");
						edtcv.commit();

						Intent i = new Intent(getBaseContext(), com.stavigilmonitoring.SelectMenu.class);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						getBaseContext().startActivity(i);
						finish();
					}

				} else {
					PERMISSION = "Deny";
					Toast toast = Toast.makeText(context, "PLEASE REOPEN YOUR APP AND ALLOW ALL THE PERMISSIONS", Toast.LENGTH_SHORT);
					View view = toast.getView();

					//To change the Background of Toast
					view.setBackgroundColor(Color.RED);
					toast.show();
					finish();
				}
				break;
		}
	}

	private String registerGCM() {
		/*gcm = GoogleCloudMessaging.getInstance(RegistrationUS.this);
		regId = getRegistrationId(getApplicationContext());*/

		//regId = Common.TOKEN;

		/*if (regId.isEmpty()) {
			registerInBackground();
			Log.d("test",
					"registerGCM - successfully registered with GCM server - regId: "
							+ regId);
		} else {
			Log.d("test",
					"RegId already available. RegId:RegId already available. RegId:"
							+ regId);
		}*/
		String refreshedToken = FirebaseInstanceId.getInstance().getToken();
		Log.d("TAG", "Refreshed token: " + refreshedToken);

		//sendRegistrationToServer(refreshedToken);
		Common.TOKEN = refreshedToken;
		regId = Common.TOKEN;
		//Toast.makeText(getBaseContext(), "FCM TOKEN : "+Common.TOKEN, Toast.LENGTH_LONG).show();
		//Toast.makeText(getBaseContext(), "GCM TOKEN : "+regId, Toast.LENGTH_LONG).show();
		return regId;
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging
								.getInstance(RegistrationUS.this);
					}
					regId = gcm.register(Config.GOOGLE_SENDER_ID);
					Log.d("test", "registerInBackground - regId: " + regId);
					msg = "Device registered, registration ID=" + regId;

					storeRegistrationId(RegistrationUS.this, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("test", "Error: " + msg);
					Log.d("test", "I never expected this! Going down, going down!" + ex);
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
								+ l.getLineNumber() + "	" + ex.getMessage() + " "
								+ Ldate);
					}
				}
				Log.d("test", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {

				Log.i("test", "Registered with GCM Server" + msg);
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null, null, null);
	}

	@SuppressLint("NewApi")
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getSharedPreferences(
				RegistrationUS.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i("test", "Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i("test", "App version changed.");
			return "";
		}
		return registrationId;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.d("test", "I never expected this! Going down, going down!" + e);
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
			throw new RuntimeException(e);
		}

	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getSharedPreferences(
				RegistrationUS.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		Log.i("test", "Saving regId on app version " + appVersion);
		Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}

	/*private void shownonetdialog() {
		
		final Dialog myDialog = new Dialog(RegistrationUS.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(R.layout.dialoginfosmall);
		myDialog.setCancelable(false);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);
		myDialog.setTitle("Error...");
		TextView txt = (TextView) myDialog
				.findViewById(R.id.dialoginfogototextsmall);

		txt.setText("No Internet Connection Found.Please Activate internet Connectin on Device..");

		Button btn = (Button) myDialog
				.findViewById(R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				

				myDialog.dismiss();
				finish();

			}
		});

		myDialog.show();

	}*/

	/*private boolean netcoonected() {
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
*/
	/*private void ShownochageD() {
		
		final Dialog myDialog = new Dialog(RegistrationUS.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(R.layout.simdialog);
		myDialog.setCancelable(false);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);
		myDialog.setTitle("Error...");
		TextView txt = (TextView) myDialog
				.findViewById(R.id.dialoginfogototextsmalls);

		txt.setText("You are using unregistered sim.You need to register the sim.Do you want to continue.");

		Button btnyes = (Button) myDialog.findViewById(R.id.simd_yes);
		btnyes.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String[] Setting_val = { "1", "1", "1", "1" };
				com.database.Database db = new com.database.Database(
						RegistrationUS.this);
				db.open();
				db.UpdateSetting(Setting_val);
				// db.SetSetting(Setting_val);
				db.close();
				myDialog.dismiss();
				Intent i = new Intent(RegistrationUS.this, RegistrationUS.class);
				startActivity(i);
				finish();

			}
		});

		Button btnno = (Button) myDialog.findViewById(R.id.simd_no);
		btnno.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				myDialog.dismiss();
				finish();
			}
		});

		myDialog.show();

	}*/

	/*private boolean imsi() {
		// TODO Auto-generated method stub
		//
		TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String serialNumber = tMgr.getSimSerialNumber();

		DBInterface dbi = new DBInterface(getApplicationContext());
		String imsid = dbi.GetImsi();
		dbi.Close();
		if (serialNumber == null) {
			Toast.makeText(getBaseContext(), "Sim has no network",
					Toast.LENGTH_LONG).show();
			System.exit(1);
		}
		if (imsid == null) {
			return false;
		}
		if (serialNumber.contains(imsid)) {

			// ("------ same imei-- ");
			return true;
		} else {
			return false;
		}

		// return true;

	}*/

	private void createDb() {

		String[] Setting_val = {"1", "1", "1", "1"};
		String[] Olddata = {"0", "0"};
		String[] TimeEntry = {"0", "1", "1"};
		String[] Alarm = {"30"};
		String[] Alarmsound = {"Sound 1"};
		String[] DateRefresh = {"1"};
		com.database.Database db = new com.database.Database(this);
		db.open();
		if (!db.ISDbCreated()) {
			db.SetSetting(Setting_val);
			db.SetGcm(new String[]{"1", "1", "0"});
			db.Setold(Olddata);

			db.Settimeentry(TimeEntry);
			db.Setalarmtime(Alarm);
			db.Setalarmsound(Alarmsound);
			db.SetDateRefresh(DateRefresh);
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
			// regservice();
			DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db1.getWritableDatabase();

			sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser");
			sql.execSQL(ut.getConnectionStatusUser());
			//sql.delete("ConnectionStatusUser",null,null);

			//sql.execSQL("DROP TABLE IF EXISTS NonrepeatedAd");
			//sql.execSQL(ut.getNonrepeatedAd());
			sql.delete("NonrepeatedAd",null,null);

			//sql.execSQL("DROP TABLE IF EXISTS Downtime");
			//sql.execSQL(ut.getDowntime());
			sql.delete("Downtime",null,null);

			//sql.execSQL("DROP TABLE IF EXISTS Suspected");
			//sql.execSQL(ut.getSuspected());
			sql.delete("Suspected",null,null);

			//sql.execSQL("DROP TABLE IF EXISTS SoundLevel");
			//sql.execSQL(ut.getSoundLevel());
			sql.delete("SoundLevel",null,null);

			//sql.execSQL("DROP TABLE IF EXISTS DownTimeRason");
			//sql.execSQL(ut.getDownTimeRason());
			sql.delete("DownTimeRason",null,null);

			//sql.execSQL("DROP TABLE IF EXISTS DownTimeRasonFill");
			//sql.execSQL(ut.getDownTimeRasonFill());
			sql.delete("DownTimeRasonFill",null,null);

			//sql.execSQL("DROP TABLE IF EXISTS SuspectedHistory");
			//sql.execSQL(ut.getSuspectedHistory());
			sql.delete("SuspectedHistory",null,null);

			//sql.execSQL("DROP TABLE IF EXISTS UpdateCount");
			//sql.execSQL(ut.getUpdateCount());
			sql.delete("UpdateCount",null,null);

			//sql.execSQL("DROP TABLE IF EXISTS Password");
			//sql.execSQL(ut.getPassword());
			sql.delete("Password",null,null);

			// sql.execSQL("DROP TABLE IF EXISTS Nonreported_LastAd");
			// sql.execSQL(ut.getn());


			// initializedatafromassetfile();
			// Intent i = new Intent(getBaseContext(), Adduserxmldb.class);
			// getBaseContext().startService(i);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void sendMessage(View send) {

		String mobno = editTextMobile.getText().toString().trim();
		String login = editTextlogin.getText().toString().trim();
		String pass = editTextpassword.getText().toString().trim();

		strlogin = login;
		strpassword = pass;
		strmobileno = mobno;

		startsmsoperation();
	}

	private void startsmsoperation() {

		pdreg = ProgressDialog.show(RegistrationUS.this,
				"Sending detail to server ..", "Please Wait....", true, true,
				new OnCancelListener() {

					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						if (async != null && async.getStatus() != AsyncTask.Status.FINISHED) {
							async.cancel(true);
						}
					}
				});


		if (PERMISSION.equals("Granted")) {

			async = new RegisterAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {

			Toast.makeText(context, "Please allow the permissions", Toast.LENGTH_SHORT).show();

		}

	}

	public class RegisterAsync extends AsyncTask<String, Void, String> {
		String mobno, login, pass;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			try {
				strlogin = login;
				strpassword = pass;
				strmobileno = mobno;

				System.out.println("===@#@# url login pass mobno " + strurl
						+ " " + strlogin + " " + strpassword + " "
						+ strmobileno);

				String pas = URLEncoder.encode(strpassword, "UTF-8");
				//String url = "http://sta.vritti.co/imedia/UserAuthentication_STA/UserAuthenticate.asmx/AuthenticateUser?"
				String url = "http://sta.vritti.co/imedia/UserAuthentication/UserAuthenticate.asmx/AuthenticateUser?"
						+ "Mobile_No="
						+ strmobileno
						+ "&LoginId="
						+ strlogin
						+ "&Password=" + pas;

				System.out.println("============ internet reg url " + url);

				try {
					System.out.println("-------  activity url --- " + url);
					responsemsg = ut.httpGet(url);

					System.out.println("-------------  xx vale-- "
							+ responsemsg);
				} catch (NullPointerException e) {
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
				} catch (IOException e) {
					e.printStackTrace();

					responsemsg = "wrong" + e.toString();
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

			} catch (Exception e) {

			}
			return responsemsg;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mobno = editTextMobile.getText().toString().trim();

			login = editTextlogin.getText().toString().trim();

			pass = editTextpassword.getText().toString().trim();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				pdreg.cancel();

				String rs = responsemsg;
				responsemsg = "k";

				if ((rs.equals("k")) || (rs.contains("wrong"))) {

					editTextlogin.setText("");
					editTextpassword.setText("");
					editTextMobile.setText("");
					showregfailmsg("noresponse");

				} else if (rs.contains("yes") || rs.contains("YES")) {

					if (rs.contains("yes/")) {
						String[] words = rs.split("yes/");
						String[] word = words[1].split("/");
						if (word.length==2){
							UserName = words[1];
							words = UserName.split("<");
							UserName = words[0];
							saveUserNameToDb(UserName);
							saveUserLoginToDb(login, pass);
						} else if(word.length==3){
							UserName = word[0];
							String Userpasseligible = word[1];
							words = Userpasseligible.split("<");
							Userpasseligible = words[0];
							saveUserNameToDb(UserName);
							saveUserLoginToDb(login, pass);
							saveUserpasseligibleToDb(Userpasseligible);
						}

					}

					SharedPreferences prefTV1 = getApplicationContext()
							.getSharedPreferences("cvNamepref",
									Context.MODE_PRIVATE); // 0 - for private mode
					Editor edtcv = prefTV1.edit();
					edtcv.putString("cvName", "Deepashree D Mulay");
					edtcv.commit();
					showregfailmsg("yes");

				} else if (rs.contains("not valid")) {
					showregfailmsgyesno("notvalid");
				} else {
					showregfailmsg("wrongdetail");
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
		}
	}

	protected void saveUserNameToDb(String Name) {
		ut = new com.stavigilmonitoring.utility();
		//Name = "Sachin Khedekar";
		//DatabaseHandler db = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		//sql.execSQL("DROP TABLE IF EXISTS UserNameTable");
		//sql.execSQL("CREATE TABLE IF NOT EXISTS UserNameTable(UserName TEXT)");
		sql.delete("UserNameTable", null, null);
		ContentValues cv = new ContentValues();
		cv.put("UserName", Name);
		sql.insert("UserNameTable", null, cv);
	}

	protected void saveUserpasseligibleToDb(String Name) {
		ut = new com.stavigilmonitoring.utility();
		//Name = "Sachin Khedekar";
		//DatabaseHandler db = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		//sql.execSQL("DROP TABLE IF EXISTS UserpassEligibleTable");
		//sql.execSQL("CREATE TABLE IF NOT EXISTS UserpassEligibleTable(UserpassEligible TEXT)");
		sql.delete("UserpassEligibleTable", null, null);
		ContentValues cv = new ContentValues();
		cv.put("UserpassEligible", Name);
		sql.insert("UserpassEligibleTable", null, cv);
	}

	protected void saveUserLoginToDb(String Name, String Pass) {
		ut = new com.stavigilmonitoring.utility();
		//Name = "Sachin Khedekar";
		//DatabaseHandler db = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		//sql.execSQL("DROP TABLE IF EXISTS UserLoginTable");
		//sql.execSQL("CREATE TABLE IF NOT EXISTS UserLoginTable(UserLogin TEXT,UserPass TEXT)");
		sql.delete("UserLoginTable", null, null);
		ContentValues cv = new ContentValues();
		cv.put("UserLogin", Name);
		cv.put("UserPass", Pass);
		sql.insert("UserLoginTable", null, cv);
	}

	protected boolean noadded() {
		// TODO Auto-generated method stube

		DBInterface dbi = new DBInterface(getBaseContext());
		String no = dbi.GetPhno();
		dbi.Close();

		// ("-------- noadded -- " + no);
		if (no.equalsIgnoreCase("1")) {
			return false;

		} else {
			return true;
		}

	}

	public void showregfailmsgyesno(final String res) {

		final Dialog myDialog = new Dialog(RegistrationUS.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoyesno);
		myDialog.setCancelable(false);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);

		TextView txt = (TextView) myDialog
				.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);

		if (res.equals("notvalid")) {
			myDialog.setTitle("Wrong Registration Service...");
			txt.setText("For your domain use Sms Service to Complete Authentication Process.Do you want to continue ? ");
		}
		Button btn = (Button) myDialog.findViewById(com.stavigilmonitoring.R.id.yesbtndialog);

		Button btnno = (Button) myDialog.findViewById(com.stavigilmonitoring.R.id.nobtndialog);

		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				smssend();

				myDialog.dismiss();

			}
		});

		btnno.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();
			}
		});

		myDialog.show();

	}

	protected void smssend() {
		// TODO Auto-generated method stub
		getsetting();

		String link = editTextUrl.getText().toString().toLowerCase().trim();
		link = link.replaceAll("\\s", "");

		if (link.contains("vritti.co")) {
			strurldb = "http://sta.vritti.co/Vrittiportal";
			strurl = "intranet.vritti.co";
		} else if (link.contains("vworkbench.com")) {
			if (link.contains("http://")) {
				strurldb = link;
				strurl = link.substring(7);

			} else {
				strurldb = "http://" + link;
				strurl = link;
			}
		} else {
			if (link.contains("http://")) {
				strurldb = link;
				strurl = link.substring(7);

			} else {
				strurldb = "http://" + link;
				strurl = link;
			}
		}

		_Option1[2] = strurldb;
		_Option1[3] = strurl;
		System.out.println("------@#@#@#  _Option[2]  "
				+ _Option1[2].toString());
		System.out.println("------@#@#@#  _Option[3]  "
				+ _Option1[3].toString());

		DBInterface dbit = new DBInterface(getApplicationContext());
		dbit.SetSetting(_Option1);
		dbit.Close();

		String msg = "GET Authvwb" + " " + _Option1[3].toString();

		System.out.println("---------------  msg --- " + msg);
		try {
			SmsManager smsManager = SmsManager.getDefault();

			smsManager.sendTextMessage(SERVER_NO, null, msg, null, null);

			showcounterDialog();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showcounterDialog() {


		myDialog = new Dialog(RegistrationUS.this);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialogcounter);
		myDialog.setCancelable(false);
		myDialog.setCanceledOnTouchOutside(false);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);
		myDialog.setTitle("Waiting for server response");
		// myDialog.setTitle("Connection Faild..");
		// TextView
		// txt=(TextView)myDialog.findViewById(R.id.dialoginfogototextsmall);
		// txt.setText("Waiting Server Response");

		pb = (ProgressBar) myDialog.findViewById(com.stavigilmonitoring.R.id.progressbard);

		txt1 = (TextView) myDialog
				.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmalltime);
		txt1.setText("sec remaing");

		timer = new MyCounter(300000, 1000);

		timer.start();
		btn = (Button) myDialog.findViewById(com.stavigilmonitoring.R.id.gotobtndialog);
		btn.setVisibility(View.INVISIBLE);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (noadded()) {
					myDialog.dismiss();



					SharedPreferences LoginPref = getApplicationContext()
							.getSharedPreferences("SetupPref",Context.MODE_PRIVATE); // 0 - for private mode
					Editor edtcv = LoginPref.edit();
					edtcv.putString("Dialog", "YesDialog");
					edtcv.commit();

					Intent i = new Intent(getBaseContext(), com.stavigilmonitoring.SelectMenu.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getBaseContext().startActivity(i);
					finish();
				} else {
					myDialog.dismiss();
					finish();

				}


			}
		});

		myDialog.show();

		// TODO Auto-generated method stub

	}

	public class MyCounter extends CountDownTimer {

		public MyCounter(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onFinish() {

			timer.cancel();


			if (noadded()) {
				pb.setVisibility(View.INVISIBLE);
				btn.setVisibility(View.VISIBLE);
				txt1.setText("Registration Successfull...!!");
			} else {
				pb.setVisibility(View.INVISIBLE);
				btn.setVisibility(View.VISIBLE);
				txt1.setText("Please Contact admin.." + responsemsg);
			}

			// if (noadded()) {
			//
			// pb.setVisibility(View.INVISIBLE);
			// btn.setVisibility(View.VISIBLE);
			// txt1.setText("Registration Successfull...!!");
			//
			// } else if (responsemsg.equals("NO")) {
			// pb.setVisibility(View.INVISIBLE);
			// btn.setVisibility(View.VISIBLE);
			// txt1.setText("You are not a registered User..Please Contact admin.");
			// } else if (responsemsg.equals("k")) {
			// pb.setVisibility(View.INVISIBLE);
			// btn.setVisibility(View.VISIBLE);
			// txt1.setText("Server not Responding Please try after some time");
			// }
			//
			// else {
			// pb.setVisibility(View.INVISIBLE);
			// btn.setVisibility(View.VISIBLE);
			// txt1.setText("Error : " + responsemsg);
			// }

		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			txt1.setText((millisUntilFinished / 1000) + " Sec Remaining");
			// ("Timer  : " + (millisUntilFinished / 1000));

		}

	}

	private BroadcastReceiver intentReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			try {

				String msg = intent.getExtras().getString("smsbody");

				System.out.println("-------  sms in register class onreceive-- " + msg);

				DBInterface dbi = new DBInterface(getApplicationContext());
				String s = dbi.GetRandomno();
				dbi.Close();

				// ("---------  randomno from db --- " + s);

				if (msg.contains(s)) {

					proceedtoreg(msg);

					timer.cancel();

					timer.onFinish();

				} else {
					responsemsg = msg;
					timer.cancel();
					timer.onFinish();
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

			}

		}

	};

	public void showregfailmsg(final String res) {

		final Dialog myDialog = new Dialog(RegistrationUS.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
		myDialog.setCancelable(false);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);

		TextView txt = (TextView) myDialog.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);

		if (res.equals("yes")) {

			try {
				Common.TOKEN = registerGCM();
				Common.UserName = GetUserName();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("Pkg_name", "com.stavigilmonitoring");
				jsonObject.put("Mobile", strmobileno);
				jsonObject.put("UserName", Common.UserName);
				jsonObject.put("Device_Id", Common.TOKEN);
				paramToken = jsonObject.toString();
				/*urlStringToken = "http://ccs.ekatm.com" //AdatSoftData.URL
						+ "/api/SaveData?"// AdatSoftData.METHOD_SAVE_DATA
				 +	"SessionId=" + "vigil"// AdatSoftData.SESSION_ID
						+ "&Handler=" + "0" //AdatSoftData.HANDLE
				 + "&Table=ALERT_DEVICEMASTER";*/
				urlStringToken = "http://punbus.vritti.co/api/Values/AddDevice?";
				new TokenRegIdAPI().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlStringToken, paramToken, null, null);
				//getCSNCountURL();
			} catch (Exception e) {
				e.printStackTrace();
			}
			myDialog.setTitle("Done...!!");
			txt.setText("Registration Successfull...");
		}
		if (res.equals("noresponse")) {
			myDialog.setTitle("Error...");
			txt.setText("Server not responding please try after some time...");
		}
		if (res.equals("notvalid")) {
			myDialog.setTitle("Wrong Registration Service...");
			txt.setText("For your domain use Sms Service to Complete Authentication Process.Do you want to continue ? ");
		}
		if (res.equals("wrongdetail")) {
			myDialog.setTitle("Registration Failed...");
			txt.setText("User Authentication failed.Please enter correct credentials...");
		}

		Button btn = (Button) myDialog
				.findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (res.equals("yes")) {
					regservicenonGPS();// gps location
					reg();
					regservice();// csn
					// regservicenon();//non reported
					regservicesound();

					com.stavigilmonitoring.utility ul = new com.stavigilmonitoring.utility();
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());
					SQLiteDatabase sql = db.getWritableDatabase();
					sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser1");
					sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser");
					sql.execSQL(ul.getConnectionStatusUser1());
					sql.execSQL(ul.getConnectionStatusUser());
					//sql.delete("ConnectionStatusUser1",null,null);
					//sql.delete("getConnectionStatusUser",null,null);

					SharedPreferences pref = getApplicationContext()
							.getSharedPreferences("MyPrefnon", MODE_PRIVATE); // 0
					// -
					// for
					// private
					// mode
					Editor editor = pref.edit();
					editor.putString("nonreportedStatus", "0");
					editor.putString("advCount", "0");
					editor.commit();

					//sendDeviceId();
					Intent i = new Intent(getBaseContext(), com.stavigilmonitoring.CSNBannerActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getBaseContext().startActivity(i);
					myDialog.dismiss();
					finish();


					/*Intent i = new Intent(getBaseContext(), com.stavigilmonitoring.SelectMenu.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getBaseContext().startActivity(i);

					myDialog.dismiss();
					finish();*/

				} else {
					myDialog.dismiss();
					// finish();
				}
			}
		});

		myDialog.show();

	}

	/*public void getCSNCountURL() {
		String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/AddDeviceRegId?"
				+ "mobileno=" + mobno + "&deviceregid=" + regId;

		try {
			responsemsg = ut.httpGet(url);
		} catch (IOException e) {
	}*/

	public String GetUserName() {
		String UserName = "";
		//DatabaseHandler db = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor c = sql.rawQuery("SELECT UserName FROM UserNameTable", null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			UserName = c.getString(0).trim();
		}
		return UserName;
	}

	protected void sendDeviceId() {
		//DatabaseHandler dbHandler = new DatabaseHandler(RegistrationUS.this);
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();

		// mobile_number = dbHandler.GetPhno();
		// link = dbi.GetUrl();
		if (!(regId.isEmpty()) && mobno.length() > 0) {
			DeviceIdAsyncTask devidasync = new DeviceIdAsyncTask();
			devidasync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	public class DeviceIdAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			try {
				String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/AddDeviceRegId?"
						+ "mobileno=" + mobno + "&deviceregid=" + regId;

				try {
					responsemsg = ut.httpGet(url);
				} catch (IOException e) {
					e.printStackTrace();
					responsemsg = "Error";
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
			}
			return responsemsg;
		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);

			pdreg.cancel();


			if (responsemsg.equalsIgnoreCase("Error")) {
				//sendDeviceId();
			} else {
			}
		}
	}

	class TokenRegIdAPI extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*progressDialog = new ProgressDialog(ConfirmOTPActivity.this);
			progressDialog.setMessage("Processing...");
			progressDialog.show();*/
		}

		@Override
		protected String doInBackground(String... params) {
			Object res;
       /* responsemsg = "";
        inwid = "";
        inwtab = "";*/
			try {
				res = OpenPostConnection(params[0], params[1]);
				responsemsg = res.toString();
			} catch (NullPointerException e) {
				responsemsg = "error";
				e.printStackTrace();
			} catch (Exception e) {
				responsemsg = "error";
				e.printStackTrace();
			}
			return responsemsg;
		}

		@Override
		protected void onPostExecute(String result) {
			String table = "";
			if (responsemsg.contains("error") || result.contains("E")) {
				Toast.makeText(getBaseContext(), "error!", Toast.LENGTH_LONG).show();
			} else if (responsemsg.contains("Y") || responsemsg.contains("y")) {
				//Toast.makeText(getBaseContext(), "Token Added Successfully..\n" + regId, Toast.LENGTH_LONG).show();
				Toast.makeText(getBaseContext(), "Token Added Successfully.", Toast.LENGTH_LONG).show();
			}
		}
	}

	public static Object OpenPostConnection(String url, String FinalObj) {
		String res = null;
		Object response = null;
		try {
			URL url1 = new URL(url);
			HttpPost httppost = new HttpPost(url.toString());
			StringEntity se = new StringEntity(FinalObj.toString());
			httppost.setEntity(se);
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Content-type", "application/json");
			ResponseHandler responseHandler = new BasicResponseHandler();
			response = httpClient.execute(httppost, responseHandler);
			Log.i("Common Data", response + "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	protected void reg() {
		// TODO Auto-generated method stub

		getsetting();

		TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}

		//String serialNumber = tMgr.getSimSerialNumber();
		String serialNumber = "";

		String simSerialNo="";

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

			SubscriptionManager subsManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

			List<SubscriptionInfo> subsList = subsManager.getActiveSubscriptionInfoList();

			if (subsList!=null) {
				for (SubscriptionInfo subsInfo : subsList) {
					if (subsInfo != null) {
						serialNumber  = subsInfo.getIccId();
					}
				}
			}
		} else {
			tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			serialNumber = tMgr.getSimSerialNumber();
		}

		_Option1[0] = serialNumber;
		_Option1[1] = strmobileno;
		_Option1[2] = strurldb;

		DBInterface dbi = new DBInterface(getApplicationContext());
		dbi.SetSetting(_Option1);
		dbi.Close();

	}

	/*        SYNC DATA SERVICE START         */
	protected void regservice() {
		// TODO Auto-generated method stub
		// System.out.println(".................2................");
		// Calendar cal = Calendar.getInstance();
		// int hour= cal.get(Calendar.HOUR_OF_DAY);
		// String hours=String.valueOf(hour);
		// //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		// int s1=6;
		// int s2=13;
		// if((hour > s1) && (hour < s2) )
		// {
		//DatabaseHandler db = new DatabaseHandler(RegistrationUS.this);

		setJobShedulder("SyncDataCountService");

		/*______________________________________________________________________________________*//*
		String val = db.getSetting();

		int itime = Integer.parseInt(val);

		long aTimenon = 1000 * 60 * itime;

		System.out.println("..........start");
		Intent myIntent = new Intent(this, AlarmManagerBroadcastReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 12345,
				myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		// am.setRepeating(AlarmManager.ELAPSED_REALTIME,
		// SystemClock.elapsedRealtime(), 1000 * 60 * 1, pendingIntent);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				aTimenon, pendingIntent);
		*//*______________________________________________________________________________________*/

	}

	/*        SOUND LEVEL SERVICE START       */
	protected void regservicesound() {

		setJobShedulder("SoundLevelService");

		/*________________________________________________________________________________________*//*
		//DatabaseHandler db = new DatabaseHandler(RegistrationUS.this);
		String val = db.getSetting();

		int itime = Integer.parseInt(val);
		long aTimenon = 1000 * 60 * itime;

		//long aTimenon = 1000 * 60 * 60 * 3;
		System.out.println("..........start");
		Intent myIntent = new Intent(this, SoundLevelBrodcastReciver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 12345, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		//this, 12345,
		//myIntent, PendingIntent.FLAG_UPDATE_CURRENT
		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				aTimenon, pendingIntent);
		*//*________________________________________________________________________________________*/
	}

	protected void regservicenonGPS() {
		// TODO Auto-generated method stub
		// System.out.println(".................2................");
		/*
		 * Calendar cal = Calendar.getInstance(); int hour=
		 * cal.get(Calendar.HOUR_OF_DAY); String hours=String.valueOf(hour);
		 * SimpleDateFormat sdf = new SimpleDateFormat("HH:mm"); int s1=8; int
		 * s2=20;
		 * 
		 * if((hour > s1) && (hour < s2) ) { aTimenon = 1000 * 60 * 30; } else {
		 * aTimenon = 1000 * 60 * 30; }
		 */
/*________________________________________________________________________________*/
		long aTimenon = 1000 * 60 * 30;
		System.out.println("..........start");
		Intent myIntent = new Intent(this,
				AlarmManagerBroadcastReceiverGPG.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 12345,
				myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				aTimenon, pendingIntent);
        /*________________________________________________________________________________*/

	}

	private void getsetting() {
		// TODO Auto-generated method stub
		com.database.DBInterface db = new com.database.DBInterface(
				getBaseContext());
		Cursor curs = db.GetSetting();
		if (curs.getCount() > 0) {
			curs.moveToFirst();
			for (int i = 0; i < _Option1.length; i++) {
				_Option1[i] = curs.getString(i);

			}
		}
		curs.close();

	}

	protected void showWdialog(String string) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		final Dialog myDialog = new Dialog(RegistrationUS.this);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
		myDialog.setTitle("Error...");
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);
		TextView txt = (TextView) myDialog
				.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);
		txt.setText(string);
		Button btn = (Button) myDialog
				.findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();

			}
		});

		myDialog.show();

	}

	protected void proceedtoreg(String msg) {
		// TODO Auto-generated method stub

		String[] k = msg.split("@");

		String k1 = k[1];

		// ("------- k1-- " + k1);

		getsetting();

		TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}

		//String serialNumber = tMgr.getSimSerialNumber();

		String serialNumber = "";

		String simSerialNo="";

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

			SubscriptionManager subsManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

			List<SubscriptionInfo> subsList = subsManager.getActiveSubscriptionInfoList();

			if (subsList!=null) {
				for (SubscriptionInfo subsInfo : subsList) {
					if (subsInfo != null) {
						serialNumber  = subsInfo.getIccId();
					}
				}
			}
		} else {
			tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			serialNumber = tMgr.getSimSerialNumber();
		}

		_Option1[0] = serialNumber;
		_Option1[1] = k1;

		DBInterface dbi = new DBInterface(getApplicationContext());
		dbi.SetSetting(_Option1);
		dbi.Close();

		Intent i = new Intent(getBaseContext(), com.stavigilmonitoring.SelectMenu.class);
		getBaseContext().startService(i);

	}

	private void showdialog(String string) {

		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		final Dialog myDialog = new Dialog(RegistrationUS.this);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);
		myDialog.setTitle("Done..!");
		TextView txt = (TextView) myDialog
				.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);
		txt.setText(string);
		Button btn = (Button) myDialog
				.findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				// Toast.makeText(getApplicationContext(),"you are  authorised",
				// Toast.LENGTH_LONG).show();

				Intent i = new Intent(getApplicationContext(), com.stavigilmonitoring.SelectMenu.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplicationContext().startActivity(i);

				myDialog.dismiss();

			}
		});

		myDialog.show();

	}

	private void showD(String string) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		final Dialog myDialog = new Dialog(RegistrationUS.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);
		myDialog.setTitle("Error...");
		TextView txt = (TextView) myDialog
				.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);

		if (string.equalsIgnoreCase("empty")) {

			txt.setText("Please fill all the fields..");
		}
		if (string.equalsIgnoreCase("wrongurl")) {
			txt.setText("Please Enter correct url..");
		}

		Button btn = (Button) myDialog
				.findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				// Toast.makeText(getApplicationContext(),"you are  authorised",
				// Toast.LENGTH_LONG).show();

				myDialog.dismiss();

			}
		});

		myDialog.show();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			// finish();
		} catch (Exception e) {

		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		finish();
	}

	private void setJobShedulder( String key) {

		// checkBatteryOptimized();
		if(myJob == null) {
			dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

			if(key.equalsIgnoreCase("DMCertificateService")){
				callJobDispacher_DMCertificate();

			}else if(key.equalsIgnoreCase("SoundLevelService")){
				callJobDispacher_soundlevel();

			}else if(key.equalsIgnoreCase("SyncDataCountService")){
				callJobDispacher_SyncDataCount();
			}

		}
		else{
			/*if(!AppCommon.getInstance(this).isServiceIsStart()){
				dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
				callJobDispacher();
			}else {
				dispatcher.cancelAll();
				dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
				myJob = null;
				callJobDispacher();
			}*/

			if(AppCommon){
				dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

				if(key.equalsIgnoreCase("DMCertificateService")){
					callJobDispacher_DMCertificate();

				}else if(key.equalsIgnoreCase("SoundLevelService")){
					callJobDispacher_soundlevel();

				}else if(key.equalsIgnoreCase("SyncDataCountService")){
					callJobDispacher_SyncDataCount();
				}

			}else {
				AppCommon = true;
				dispatcher.cancelAll();
				dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
				myJob = null;

				if(key.equalsIgnoreCase("DMCertificateService")){
					callJobDispacher_DMCertificate();

				}else if(key.equalsIgnoreCase("SoundLevelService")){
					callJobDispacher_soundlevel();

				}else if(key.equalsIgnoreCase("SyncDataCountService")){
					callJobDispacher_SyncDataCount();
				}
			}
		}
	}

	private void callJobDispacher_soundlevel() {
		myJob = dispatcher.newJobBuilder()
				// the JobService that will be called
				.setService(JobService_Test.class)
				// uniquely identifies the job
				.setTag("test")
				// one-off job
				.setRecurring(true)
				// don't persist past a device reboot
				.setLifetime(Lifetime.FOREVER)

				// start between 0 and 60 seconds from now
				.setTrigger(Trigger.executionWindow(180, 240))
				// don't overwrite an existing job with the same tag
				.setReplaceCurrent(true)
				// retry with exponential backoff
				.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
				// constraints that need to be satisfied for the job to run
				.setConstraints(

						// only run on an unmetered network
						Constraint.ON_ANY_NETWORK,
						// only run when the device is charging
						Constraint.DEVICE_IDLE
				)
				.build();

		dispatcher.mustSchedule(myJob);
		//AppCommon.getInstance(this).setServiceStarted(true);
	}

	/****************DMCertificate*********************************/
	private void callJobDispacher_DMCertificate() {
		myJob = dispatcher.newJobBuilder()
				// the JobService that will be called
				.setService(JobService_DMCertificate.class)
				// uniquely identifies the job
				.setTag("test")
				// one-off job
				.setRecurring(true)
				// don't persist past a device reboot
				.setLifetime(Lifetime.FOREVER)

				// start between 0 and 60 seconds from now
				.setTrigger(Trigger.executionWindow(180, 240))
				// don't overwrite an existing job with the same tag
				.setReplaceCurrent(true)
				// retry with exponential backoff
				.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
				// constraints that need to be satisfied for the job to run
				.setConstraints(

						// only run on an unmetered network
						Constraint.ON_ANY_NETWORK,
						// only run when the device is charging
						Constraint.DEVICE_IDLE
				)
				.build();

		dispatcher.mustSchedule(myJob);
		//AppCommon.getInstance(this).setServiceStarted(true);
	}

	/****************SyncDataCount*********************************/
	private void callJobDispacher_SyncDataCount() {
		myJob = dispatcher.newJobBuilder()
				// the JobService that will be called
				.setService(JobService_SyncDataCount.class)
				// uniquely identifies the job
				.setTag("test")
				// one-off job
				.setRecurring(true)
				// don't persist past a device reboot
				.setLifetime(Lifetime.FOREVER)

				// start between 0 and 60 seconds from now
				.setTrigger(Trigger.executionWindow(180, 240))
				// don't overwrite an existing job with the same tag
				.setReplaceCurrent(true)
				// retry with exponential backoff
				.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
				// constraints that need to be satisfied for the job to run
				.setConstraints(

						// only run on an unmetered network
						Constraint.ON_ANY_NETWORK,
						// only run when the device is charging
						Constraint.DEVICE_IDLE
				)
				.build();

		dispatcher.mustSchedule(myJob);
		//AppCommon.getInstance(this).setServiceStarted(true);
	}

}

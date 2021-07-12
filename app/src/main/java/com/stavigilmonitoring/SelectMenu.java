package com.stavigilmonitoring;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.beanclasses.StatelevelList;
import com.beanclasses.TvStatusStateBean;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.receiver.AlarmManagerBroadcastReceiver;
import com.receiver.SoundLevelBrodcastReciver;
import com.services.JobService_DMCertificate;
import com.services.JobService_PaidLocationFusedLocationTracker1;
import com.services.JobService_SyncDataCount;
import com.services.JobService_Test;
import com.services.WakeLocker;
import com.database.DBInterface;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.MenuItem;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.provider.CalendarContract.CalendarCache.URI;
import static com.stavigilmonitoring.FileHelper.c;
import static com.stavigilmonitoring.SelectMenu.btmsheetedsationname;
import static com.stavigilmonitoring.SelectMenu.myJob;

public class SelectMenu extends Activity {
	Dialog dialog;
    //String dialogopen ="no";
	public static FirebaseJobDispatcher dispatcher;
	public static Job myJob = null;
	boolean AppCommon = false;

	List<StatelevelList> searchResults;
	private LinearLayout followup;
	ProgressDialog pd;
	ArrayList<String> projectlist = new ArrayList<String>();
	String responsemsg = "k";
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	CharSequence dayname, Monthname, curDate;
	String times;
	private static String mobno;
	static SimpleDateFormat dff;
	static String Ldate;
	private String link;
	public static int cnt = 0;
	String res = "right";
	String sop, MySTAVigilVersion;
	ImageView ivref;
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String Name = "csnStatus";
	SharedPreferences sharedpreferences;
	Button btnupdate, btncancel;
//ProgressDialog progressDialog;

	private LinearLayout alerts, stnNotify, dmc, llgetpass, connectinStatus, downtimeReason, downtimeanalysis, nonrepeatedAd, suspectedAd,
			videoTuts, soundLevel, tvstatus, workdone, workassign, stnEnquiry, supEnquiry, stnInventory, NonRepAdv, pendinclips,
			Lmsconnection, PDCClipwise, Blog, materialrequest, bgplaylist, downtimereasonupdated, pwd, Advclip_1stplayrprt_sewise,
			stnperformance,advdetails,lay_unrel_advs;

	private TextView status, soundcunt, alertcounts, dmccounts, NonRepStationCount, NonRepAddCount, nonreportedStatus, datess,
			bg_palycount, bloglink, tvstaversion, tvcsnstatuscnt, tvclips, tvclipscnt, tvmaterialrequirementStationcount,
			PDCClipCount, PDCStatncount, LmsconnectionStatuscount,txtusername,txtmob;

	private ImageView personalReport;
	MenuItem miActionProgressItem;
	String finalLattitude, finalLongitude, InstallationId = "";
	private String nonreptAdvCount, ClipCnt, StnClipCnt;
	DatabaseHandler db;
	SQLiteDatabase sql;
	StatelevelList sitem;
	int totalstation = 0;
	int totalstation_bg = 0;
	Date date;
	String urllms;
	String enddate, startdate, Syncdate, dialogopen = "no";
	DateFormat dateFormat;
	String SubnetString;

	private Timer autoUpdate, autoUpdate2;
	static int Year, month, day;
	String TODAYDATE, FLAG_PSTORE;

	View sheetview;
	static Button btmsheetedsationname;
	Button btngetpwd, btndismiss;
	static TextView txtpass;
	GetStationPassword password;

	EditText edtime_setrefresh;
	Button btnSave_setrfrsh;
	LinearLayout llscroll;
	Bitmap bitmap;

	public void onCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.selectmenu);
		//setContentView(R.layout.testmenu);

		init();

		AutoRefreshData();

		SharedPreferences sp = getSharedPreferences("SetupPref", Context.MODE_PRIVATE);
		String diayn = sp.getString("Dialog", "NoDialog");
		String dicurDate = sp.getString("TodaysDate", TODAYDATE);

		// ////////********Connection Status ***********//////////////
		SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		String CSNStatusCount = pref.getString("csnStatusCount", null);

		// ////////********Connection Status ***********//////////////
		SharedPreferences prefDate = getApplicationContext().getSharedPreferences("MyPrefDate", Context.MODE_PRIVATE);
		Editor editorDate = prefDate.edit();
		String valuesDate = prefDate.getString("Dates", "");

		// ////////********Connection Status ***********//////////////
		SharedPreferences pref1 = getApplicationContext().getSharedPreferences("MyPrefnon", Context.MODE_PRIVATE); // 0 - for private mode
		Editor editor1 = pref1.edit();
		String values1 = pref1.getString("nonreportedStatus", null);
		nonreptAdvCount = pref1.getString("advCount", null);

		SharedPreferences prefclips = getApplicationContext().getSharedPreferences("MyPrefclips", Context.MODE_PRIVATE); // 0
		Editor editorclips = prefclips.edit();
		String valuesclips = prefclips.getString("clips", null);
		ClipCnt = prefclips.getString("ClipCnt", null);

		/*SharedPreferences stnprefclips = getApplicationContext()
				.getSharedPreferences("StnMyPrefclips", Context.MODE_PRIVATE); // 0
		Editor stneditorclips = stnprefclips.edit();
		String stnvaluesclips = stnprefclips.getString("clips", null);
		StnClipCnt = stnprefclips.getString("StnClipCnt", null);*/

		SharedPreferences prefTV1 = getApplicationContext().getSharedPreferences("PrefTVStatus", Context.MODE_PRIVATE);
		Editor editorTV1 = prefTV1.edit();
		String TVStatus = prefTV1.getString("TVStatus", null);

		SharedPreferences prefsound = getApplicationContext().getSharedPreferences("PrefSound", Context.MODE_PRIVATE);
		Editor editorsound = prefsound.edit();
		String sound = prefsound.getString("TVSound", null);

		SharedPreferences prefDMC = getApplicationContext().getSharedPreferences("PrefDMC", Context.MODE_PRIVATE);
		Editor editorDMC = prefDMC.edit();
		String DMC = prefDMC.getString("DMC", null);

		SharedPreferences prefalertcount = getApplicationContext().getSharedPreferences("Prefalertcount", Context.MODE_PRIVATE);
		Editor editoralertcount = prefalertcount.edit();
		// String TVsound = prefsound.getString("TVSound", "");
		String sumdata = prefalertcount.getString("AlertCount", null);
		alertcounts.setText(sumdata);
		//editoralertcount.apply();

		SharedPreferences prefmaterial = getApplicationContext().getSharedPreferences("PrefMaterial", Context.MODE_PRIVATE);
		Editor editorMaterial = prefmaterial.edit();
		String soundMaterial = prefmaterial.getString("TVMaterial", null);

		//////********LMS Connection Count*******/////
		SharedPreferences preflmsconn = getApplicationContext().getSharedPreferences("PrefLmsCount", Context.MODE_PRIVATE);
		Editor editorlmsConne = preflmsconn.edit();
		String LMsCount = preflmsconn.getString("LmsCount", null);
		LmsconnectionStatuscount.setText(LMsCount);

		//////********LMS Connection Count*******/////
		SharedPreferences Prefbgplay = getApplicationContext().getSharedPreferences("bgpref", Context.MODE_PRIVATE);
		Editor edtbg = Prefbgplay.edit();// bgprefbgPlayCount
		String BgPaly = Prefbgplay.getString("bgPlayCount", null);
		bg_palycount.setText(BgPaly);
		// ***********************************************************************//

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		// new DownloadxmlsDataURL_new().execute();

		Common.UserName = GetUserName();
		txtusername.setText(Common.UserName);
		//Common.UserpassEligible = GetUserpassEligible();
		GetUserLogin();
		/*if (Common.UserpassEligible.equalsIgnoreCase("")){
			llgetpass.setVisibility(View.GONE);
		}*/

		ArrayList<String> packagenameArrayList = new ArrayList<String>();
		List<PackageInfo> packageInfoList = getPackageManager().getInstalledPackages(0);
		for (int i = 0
			 ; i < packageInfoList.size(); i++) {
			PackageInfo packageInfo = packageInfoList.get(i);
			String packagename = packageInfo.packageName;
			packagenameArrayList.add(packagename);
			if (packagename.contains("com.stavigilmonitoring")) {
				MySTAVigilVersion = packageInfo.versionName;
			}
		}
		if (packagenameArrayList.contains("vworkbench7.vritti.com.vworkbench7")) {

		} else if (packagenameArrayList.contains("vcrm7.vritti.com.vcrm7")) {

		} else {
			regservicenonGPS();
		}

		if ((CSNStatusCount == null || values1 == null //|| valuesclips == null
				|| sound == null /*|| LMsCount == null || BgPaly == null || sumdata == null*/)
				&& ut.isnet(SelectMenu.this)) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					new DownloadxmlsDataURL_new().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			});
		} else if ((CSNStatusCount.equalsIgnoreCase("")
				|| values1.equalsIgnoreCase("") || sound.equalsIgnoreCase(""))
				&& ut.isnet(SelectMenu.this)) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					new DownloadxmlsDataURL_new().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			});
		} else {
			((TextView) findViewById(R.id.tvnon2)).setText("Non-Reported Ads :"
					+ nonreptAdvCount);
			/***/((TextView) findViewById(R.id.tvclipcnt))
					.setText("Pending Download Clips :" + ClipCnt);
		}

		status.setText(CSNStatusCount);
		alertcounts.setText(sumdata);
		tvmaterialrequirementStationcount.setText(soundMaterial);
		soundcunt.setText(sound);
		nonreportedStatus.setText(values1);
		tvcsnstatuscnt.setText(TVStatus);//values1
		NonRepAddCount.setText(nonreptAdvCount);
		NonRepStationCount.setText("Non-Reported Stations : " + values1);
		datess.setText(valuesDate);
		tvstaversion.setText("Current Version : " + MySTAVigilVersion);
		tvclips.setText(valuesclips);
		/***/tvclipscnt.setText("Pending Clips : " + ClipCnt);
		PDCClipCount.setText(ClipCnt);
		PDCStatncount.setText("Pending Clips Stations : " + valuesclips);

		try{
			DBInterface dbi = new DBInterface(getApplicationContext());
			mobno = dbi.GetPhno();
			txtmob.setText(mobno);
			dbi.Close();
		}catch (Exception e){
			e.printStackTrace();
		}

		/*Common.UserName = GetUserName();
		GetUserLogin();*/

		//setJobShedulder();		//JobScheduler service for testing

		timerMethod();
		timerMethod2();
		timerMethod3();

		if (diayn.equalsIgnoreCase("YesDialog")) {

			Date date = new Date();
			final Calendar c = Calendar.getInstance();

			Year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);

			String New_TodayDate = day + "-"
					+ (month + 1) + "-" + Year;

			if (New_TodayDate.equalsIgnoreCase(dicurDate)) {
				FLAG_PSTORE = "DONT_SHOW_DIALOG";
			} else {
				FLAG_PSTORE = "SHOW_DIALOG";
			}

			if (FLAG_PSTORE.equalsIgnoreCase("SHOW_DIALOG")) {
				//if date not matched then call
				callforplayStore();
				//Toast.makeText(getApplicationContext(),"Playstore update popup showing once", Toast.LENGTH_SHORT).show();
			} else {
				//Toast.makeText(getApplicationContext(),"Already cancelled playstore update popup", Toast.LENGTH_SHORT).show();
			}
		}
		setListeners();
	}

	public void init() {
		cnt = 0;
		try{
			db = new DatabaseHandler(this);
			sql = db.getWritableDatabase();
		}catch (Exception e){
			e.printStackTrace();
		}

		//registerReceiver(mHandleMessageReceiver, new IntentFilter(
		//Config.DISPLAY_MESSAGE_ACTION));
		//String val=db.getSetting();

		Calendar cal1 = Calendar.getInstance();

		llscroll = (LinearLayout) findViewById(R.id.llscroll);
		llgetpass = (LinearLayout) findViewById(R.id.llgetpass);
		videoTuts = (LinearLayout) findViewById(R.id.videoTuts);
		stnNotify = (LinearLayout) findViewById(R.id.stnNotify);
		alerts = (LinearLayout) findViewById(R.id.alerts);
		dmc = (LinearLayout) findViewById(R.id.dmc);
		connectinStatus = (LinearLayout) findViewById(R.id.connectionstatus);
		pendinclips = (LinearLayout) findViewById(R.id.pendingclips);
		downtimeReason = (LinearLayout) findViewById(R.id.downtimereason);
		downtimeanalysis = (LinearLayout) findViewById(R.id.downtimeanalysis);
		nonrepeatedAd = (LinearLayout) findViewById(R.id.nonrepeatedad);
		suspectedAd = (LinearLayout) findViewById(R.id.suspectedad);
		soundLevel = (LinearLayout) findViewById(R.id.soundlevel);
		workdone = (LinearLayout) findViewById(R.id.workdone);
		workassign = (LinearLayout) findViewById(R.id.workassign);
		stnEnquiry = (LinearLayout) findViewById(R.id.StationEnquiry);
		stnInventory = (LinearLayout) findViewById(R.id.invent);
		supEnquiry = (LinearLayout) findViewById(R.id.SuporterEnquiry);
		bgplaylist = (LinearLayout) findViewById(R.id.bgPlaylist);
		Lmsconnection = (LinearLayout) findViewById(R.id.Lmsconnection);
		downtimereasonupdated = (LinearLayout) findViewById(R.id.downtimereasonupdated);
		pwd = (LinearLayout) findViewById(R.id.getpwdstavigil);
		Blog = (LinearLayout) findViewById(R.id.vigilblog);
		tvstatus = (LinearLayout) findViewById(R.id.tvstatus);
		materialrequest = (LinearLayout) findViewById(R.id.materialrequest);
		NonRepAdv = (LinearLayout) findViewById(R.id.nonrepeatedadadv);
		PDCClipwise = (LinearLayout) findViewById(R.id.pendingclipsClipwise);
		Advclip_1stplayrprt_sewise = (LinearLayout) findViewById(R.id.clipdtlsewise);
		stnperformance = (LinearLayout) findViewById(R.id.stnperformance);
		stnperformance.setVisibility(View.VISIBLE);
		stnperformance.setEnabled(false);
		advdetails = findViewById(R.id.advdetails);
        lay_unrel_advs = findViewById(R.id.lay_unrel_advs);

		tvcsnstatuscnt = (TextView) findViewById(R.id.tvcsnstatuscnt);
		tvclips = (TextView) findViewById(R.id.tvclipscnt);
		tvclipscnt = (TextView) findViewById(R.id.tvclipcnt);
		NonRepAddCount = (TextView) findViewById(R.id.tvnonreportedstatusadv);
		NonRepStationCount = (TextView) findViewById(R.id.tvnonadv1);
		tvmaterialrequirementStationcount = (TextView) findViewById(R.id.tvmaterialrequirementStationcount);
		bg_palycount = (TextView) findViewById(R.id.bg12);
		LmsconnectionStatuscount = (TextView) findViewById(R.id.LMSConnectionstatuscount);
		PDCClipCount = (TextView) findViewById(R.id.tvclipcount);//PDCClipCount,PDCStatncount
		PDCStatncount = (TextView) findViewById(R.id.Stncount);
		status = (TextView) findViewById(R.id.tvcsnstatus);
		alertcounts = (TextView) findViewById(R.id.tvalertcounts);
		dmccounts = (TextView) findViewById(R.id.tvdmccounts);
		soundcunt = (TextView) findViewById(R.id.tvsoundcunt);
		datess = (TextView) findViewById(R.id.tvcsnsynchDate);
		tvstaversion = (TextView) findViewById(R.id.tvstaversion);
		nonreportedStatus = (TextView) findViewById(R.id.tvnonreportedstatus);
		txtusername = findViewById(R.id.txtusername);
		txtmob = findViewById(R.id.txtmob);

		personalReport = (ImageView) findViewById(R.id.personalReport);
		personalReport.setVisibility(View.GONE);

		searchResults = new ArrayList<StatelevelList>();
		// netfilterclass = new mDownloadsubFilter();
		// netfilterclass.execute();

	}

	public void setListeners() {

		Blog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://imedia.vritti.co/Faq.aspx"));
				startActivity(viewIntent);
			}
		});

		stnNotify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//Intent intent = new Intent(SelectMenu.this,NotifyMultiLevelActivity.class);
				//Intent intent = new Intent(SelectMenu.this, NotificationActivity.class);
				//	String devid = Common.TOKEN;
				Intent intent = new Intent(SelectMenu.this, NotificationsNewActivity.class);
				startActivity(intent);
			}
		});

		videoTuts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//Intent intent = new Intent(SelectMenu.this,NotifyMultiLevelActivity.class);
				/*Intent intent = new Intent(SelectMenu.this, VideoListActivity.class);
				startActivity(intent);*/
				Intent intent = new Intent(SelectMenu.this, LangVideoActivity.class);
				startActivity(intent);
			}
		});

		personalReport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SelectMenu.this, PersonalReortActivity.class);
				startActivity(intent);
				/*finish();
				ut.showD(SelectMenu.this,"Done");*/
			}
		});

		alerts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SelectMenu.this, AlrtListActivity.class);
				startActivity(intent);
			}
		});

		dmc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), DmCcategorizeActivity.class);
				startActivity(intent);
			}
		});

		connectinStatus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), ConnectionStatusStatewise.class);
				startActivity(intent);
			}
		});

		workdone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), WorkDoneStation.class);
				startActivity(i);

			}
		});

		workassign.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), WorkAssigncategorizeActivity.class);
				startActivity(i);
			}
		});

		downtimeReason.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/*Intent intent = new Intent(getApplicationContext(),
						DowntimeStateWise.class);
				startActivity(intent);*/
			}
		});

		downtimeanalysis.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), DownTimeAnalysis.class);
				startActivity(intent);
			}
		});

		pendinclips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), PendingClipsStateWise.class);
				startActivity(intent);
			}
		});

		tvstatus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), TvStatusStateWise.class);
				startActivity(intent);
			}
		});

		nonrepeatedAd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), NonReportedAdStatewise.class);
				startActivity(intent);
			}
		});

		suspectedAd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), SuspectedMain.class);
				startActivity(intent);
			}
		});

		soundLevel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), SoundLevelStateWise.class);
				startActivity(intent);
			}
		});

		downtimereasonupdated.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), DowntimeReasonFillStateWise.class);
				startActivity(intent);
			}
		});

		stnEnquiry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), StationEnquiryStatewise.class);
				startActivity(intent);
			}
		});

		stnInventory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), StationInventoryStatewise.class);
				//Intent intent = new Intent(getApplicationContext(), Inventory_WH_Stock_StationActivity.class);
				/*Intent intent = new Intent(getApplicationContext(),StationInventoryCategory.class);*/
				startActivity(intent);
			}
		});

		supEnquiry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent intent = new Intent(getApplicationContext(),SupporterEnquiryStatewise.class);
				Intent intent = new Intent(getApplicationContext(), SupporterEnquiry_New.class);
				startActivity(intent);
			}
		});

		pwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/*Intent intent = new Intent(getApplicationContext(), GetPassMain.class);
				startActivity(intent);*/

				final BottomSheetDialog btmsheetdialog = new BottomSheetDialog(SelectMenu.this);
				sheetview = getLayoutInflater().inflate(R.layout.yourview, null);
				btmsheetdialog.setContentView(sheetview);
				btmsheetdialog.show();
				btmsheetdialog.setCanceledOnTouchOutside(false);

				btngetpwd = sheetview.findViewById(R.id.btngetpwd);
				btndismiss = sheetview.findViewById(R.id.btndismiss);
				btmsheetedsationname = sheetview.findViewById(R.id.btmsheetedsationname);
				txtpass = sheetview.findViewById(R.id.txtgetpwd);

				btmsheetedsationname.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						int requestCode = 111;
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

						if (SelectMenu.this.getCurrentFocus() != null) {
							imm.hideSoftInputFromWindow(SelectMenu.this.getCurrentFocus().getWindowToken(), 0);
						} else {
							//Toast.makeText(SelectMenu.this,"Token is null",Toast.LENGTH_SHORT).show();
						}

						Intent intent = new Intent(SelectMenu.this, GetpassList.class);
						intent.putExtra("mobno", mobno);
						startActivityForResult(intent, requestCode);
					}
				});

				btngetpwd.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//Toast.makeText(SelectMenu.this,"Btn getpwd clicked",Toast.LENGTH_SHORT).show();
						txtpass.setText("");
						String station = btmsheetedsationname.getText().toString();

						if (isnet()) {
							if (!station.equalsIgnoreCase("")) {

								password = new GetStationPassword();
								password.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

							} else {
								Toast.makeText(SelectMenu.this, "select station name", Toast.LENGTH_LONG).show();
							}
						} else {
							//GetPassMain.showD("nonet");
						}
						//finish();
					}
				});

				btndismiss.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						btmsheetdialog.dismiss();
					}
				});
			}
		});

		materialrequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent intent = new Intent(getApplicationContext(), SelectMaterialReqType.class);
				Intent intent = new Intent(getApplicationContext(), NewMaterialRequirementActivity.class);
				startActivity(intent);
			}
		});

		/*Lmsconnection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				*//*Intent intent = new Intent(getApplicationContext(),
						LmsConnectionStateWise.class);
				startActivity(intent);*//*
				//finish();
			}
		});*/

		bgplaylist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), BackgroundPlaylistStatewise.class);
				startActivity(intent);
			}
		});

		NonRepAdv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), AdvNonReportedStatewise.class);
				startActivity(intent);
			}
		});

		PDCClipwise.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), ClipwisePendingClipsState.class);
				startActivity(intent);
			}
		});

		Advclip_1stplayrprt_sewise.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//cal for new screens
				//Intent intent = new Intent(SelectMenu.this, AdvFirstPlayReport_NetworkActivity.class);
				Intent intent = new Intent(SelectMenu.this, AdvClipsReports.class);
				startActivity(intent);
			}
		});

		/*stnperformance.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SelectMenu.this, StationPerformance.class);
				startActivity(intent);
			}
		});*/

		advdetails.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectMenu.this,AdvDetailActivity.class);
                startActivity(intent);
            }
        });

		lay_unrel_advs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SelectMenu.this,UnreleasedAdvertisements.class);
				startActivity(intent);
			}
		});
	}

	public String GetUserName() {
		String UserName = "";
		//DatabaseHandler db = new DatabaseHandler(getBaseContext());
		//SQLiteDatabase sql = db.getWritableDatabase();
		Cursor c = sql.rawQuery("SELECT UserName FROM UserNameTable", null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			UserName = c.getString(0).trim();
		}
		return UserName;
	}

	public String GetUserpassEligible() {
		String UserName = "";
		try {
			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			//SQLiteDatabase sql = db.getWritableDatabase();
			Cursor c = sql.rawQuery("SELECT UserpassEligible FROM UserpassEligibleTable", null);
			if (c.getCount() > 0) {
				c.moveToFirst();
				UserName = c.getString(0).trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return UserName;
	}

	public void GetUserLogin() {
		String UserName = "";
		//DatabaseHandler db = new DatabaseHandler(getBaseContext());
		//SQLiteDatabase sql = db.getWritableDatabase();
		Cursor c = sql.rawQuery("SELECT UserLogin,UserPass FROM UserLoginTable", null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			Common.UserLogin = c.getString(0).trim();
			Common.UserPass = c.getString(1).trim();
		}
	}

	public void timerMethod() {

		setJobShedulder("DMCertificateService");

		/* *//*____________________________________________________________________________________________*//*

		//DatabaseHandler db = new DatabaseHandler(SelectMenu.this);
		String val = db.getSetting();

		int itime = Integer.parseInt(val);

		long aTime = 1000 * 60 * itime;

		//long aTime = 1000 * 60 * 15;
		Intent igpsalarm = new Intent(getBaseContext(),	com.services.DmCertificateService.class);
				//com.services.SynchCSNnNonRepoCount.class);
		PendingIntent piHeartBeatService = PendingIntent.getService(
				getBaseContext(), 0, igpsalarm,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getBaseContext()
				.getSystemService(Context.ALARM_SERVICE);

		alarmManager.cancel(piHeartBeatService);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), aTime, piHeartBeatService);
        *//*____________________________________________________________________________________________*/

	}

	public void timerMethod2() {

		setJobShedulder("SoundLevelService");

		/*  *//*_____________________________________________________________________________________*//*
        //DatabaseHandler db = new DatabaseHandler(SelectMenu.this);
		String val = db.getSetting();

		int itime = Integer.parseInt(val);

		long aTime = 1000 * 60 * itime;

		//long aTime = 1000 * 60 * 15;
		Intent igpsalarm = new Intent(getBaseContext(),
				com.services.SoundLevelService.class);
		PendingIntent piHeartBeatService = PendingIntent.getService(
				getBaseContext(), 0, igpsalarm,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getBaseContext()
				.getSystemService(Context.ALARM_SERVICE);

		alarmManager.cancel(piHeartBeatService);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), aTime, piHeartBeatService);
        *//*_____________________________________________________________________________________*/

	}

	public void timerMethod3() {

		setJobShedulder("SyncDataCountService");

		/*____________________________________________________________________________________________*//*

		//DatabaseHandler db = new DatabaseHandler(SelectMenu.this);
		String val = db.getSetting();

		int itime = Integer.parseInt(val);

		long aTime = 1000 * 60 * itime;

		//long aTime = 1000 * 60 * 30;
		Intent igpsalarm = new Intent(getBaseContext(),
				com.services.SynchDtataCount.class);
		PendingIntent piHeartBeatService = PendingIntent.getService(
				getBaseContext(), 0, igpsalarm,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getBaseContext()
				.getSystemService(Context.ALARM_SERVICE);

		alarmManager.cancel(piHeartBeatService);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), aTime, piHeartBeatService);
        *//*____________________________________________________________________________________________*/

	}

	protected boolean isnet() {
		// TODO Auto-generated method stub
		Context context = this.getApplicationContext();
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	protected void regservicenonGPS() {
		// TODO Auto-generated method stub

		setJobShedulder("PaidLocationFusedLocation");

		/*______________________________________________________________________________________________________*//*
			long aTimenon = 1000 * 60 * 15;
			System.out.println("..........start");
			Intent myIntent = new Intent(SelectMenu.this, AlarmManagerBroadcastReceiverGPSL.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 12345,
					myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
			am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
					aTimenon, pendingIntent);
			*//*______________________________________________________________________________________________________*/
	}

	private void showUpdateDialog(String PSVersion) {
		try {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("New Update Available!");
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
		}
	}

	public void Settings(View v) {
		/*Intent i = new Intent(SelectMenu.this, SettingsActivity.class);
		startActivity(i);*/

		final BottomSheetDialog btmsheetdialog = new BottomSheetDialog(SelectMenu.this);
		sheetview = getLayoutInflater().inflate(R.layout.setrefreshtime, null);
		btmsheetdialog.setContentView(sheetview);
		btmsheetdialog.show();
		btmsheetdialog.setCanceledOnTouchOutside(false);

		edtime_setrefresh = sheetview.findViewById(R.id.edtime_setrefresh);
		btnSave_setrfrsh = sheetview.findViewById(R.id.btnSave_setrfrsh);

		String data = edtime_setrefresh.getText().toString();

		edtime_setrefresh.setText(db.getSetting());

		btnSave_setrfrsh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				String value = edtime_setrefresh.getText().toString().trim();

				db.UpdateSetting(value);

				//	regservice();   //syncdataservice call
				//	regservicesound();  //soundlevelservice call

				setJobShedulder("SyncDataCountService");
				setJobShedulder("SoundLevelService");

				Toast.makeText(SelectMenu.this, "Your settings have been save successfully", Toast.LENGTH_LONG).show();

				btmsheetdialog.dismiss();

			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// ***Change Here***
		startActivity(intent);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		String sumdata2 = "1";
		//ProgressDialog progressdialog;
		ProgressDialog progressDialog;

		@Override
		protected String doInBackground(String... params) {
			/*DBInterface dbi = new DBInterface(getApplicationContext());
			mobno = dbi.GetPhno();
			dbi.Close();*/

			String xx = "";

			String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/AlertCount?Mobile=" + mobno;

			Log.e("Alert Count", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = com.stavigilmonitoring.utility.httpGet(url);

				Log.e("Alert Count", "resmsg : " + responsemsg);
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
			}

			//String columnName, columnValue;

			//sql.execSQL("DROP TABLE IF EXISTS AlrtCountTable");
			//sql.execSQL(ut.getAlrtCountTable());

			sql.delete("AlrtCountTable", null, null);

			if (responsemsg.contains("<InstalationId>")) {
				String columnName, columnValue;
				Cursor cur = sql.rawQuery("SELECT * FROM AlrtCountTable", null);
				ContentValues values1 = new ContentValues();
				NodeList nl1 = ut.getnode(responsemsg, "TableResult");
				// String msg = "";
				// String columnName, columnValue;
				//Log.e("All Station data...",
				//" fetch data : " + nl1.getLength());
				for (int i = 0; i < nl1.getLength(); i++) {
					Element e = (Element) nl1.item(i);
					for (int j = 0; j < cur.getColumnCount(); j++) {
						columnName = cur.getColumnName(j);

						columnValue = ut.getValue(e, columnName);
						values1.put(columnName, columnValue);

						// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
					}
					sql.insert("AlrtCountTable", null, values1);
				}
			} else if (responsemsg.contains("Record are not Found...!")) {
				sumdata2 = "0";
			}

			/*SharedPreferences pref = getApplicationContext()
					.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
			Editor editor = pref.edit();
			editor.putString("csnStatusCount", String.valueOf(lstStn.size()));
			editor.apply();
			Log.e("get details.....", "---kk add STn : " + lstStn.size());*/
			//updateAlertCount();

			// String
			// url="http://vritti.co/iMedia/STA_Vigile_AndroidService_Test/WdbIntMgmtNew.asmx/GetCSNStatus_Android_new?Mobile="+mobno;
			url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetCSNStatus_Android_new?Mobile="
					+ mobno;
			Log.e("connection status...", "url : " + url);

			url = url.replaceAll(" ", "%20");

			try {
				responsemsg = ut.httpGet(url);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
							+ e1.getMessage() + " " + Ldate);
				}
			}
			// responsemsg.replaceAll(" ", "");
			// Log.e("main...back sta..","msg"+responsemsg);
			int Tcnt = 0;
			NodeList nl = ut.getnode(responsemsg, "Table1");
			List<String> lstStn = new ArrayList<String>();
			Log.e("main...back sta..", "len : " + nl.getLength());
			//Log.e("syncdcnt 1", " " + nl.getLength());
			for (int i = 0; i < nl.getLength(); i++) {
				Element e = (Element) nl.item(i);
				String val = ut.getValue(e, "A");
				// String tvcnt=ut.getValue(e, "G");
				//Log.e("val",						"val: " + val + "  servertime : " + ut.getValue(e, "B"));
				String tftym = ut.getValue(e, "B");

				try {
					Calendar cal = Calendar.getInstance();
					// SimpleDateFormat format = new
					// SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa");

					SimpleDateFormat format = new SimpleDateFormat(
							"MM/dd/yyyy hh:mm:ss aa", Locale.ENGLISH);

					Date Startdate = format.parse(tftym);
					Date Enddate = cal.getTime();
					long diff = Enddate.getTime() - Startdate.getTime();
					long diffSeconds = diff / 1000 % 60;
					long diffMinutes = diff / (60 * 1000) % 60;
					long diffHours = diff / (60 * 60 * 1000) % 24;
					long diffDays = diff / (24 * 60 * 60 * 1000);

					/*Log.e("getdetails", "sd : " + Startdate + " ed: " + Enddate
							+ " d: " + diffDays + " h: " + diffHours + " m:"
							+ diffMinutes);*/
					/*
					 * Log.e("printdiff.........","diffDays: "+diffDays);
					 * Log.e("printdiff.........","diffHours: "+diffHours);
					 * Log.e("printdiff.........","diffMinutes: "+diffMinutes);
					 * Log.e("printdiff.........","diffSeconds: "+diffSeconds);
					 */

					if (diffDays == 0 && diffHours == 0 && diffMinutes <= 15) {

					} else {
						lstStn.add(val);
					}
				} catch (Exception ex) {
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
								+ ex.getMessage() + " " + Ldate);
					}

				}

			}
			// ////////********Connection Status ***********//////////////
			SharedPreferences pref = getApplicationContext()
					.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
			Editor editor = pref.edit();
			editor.putString("csnStatusCount", String.valueOf(lstStn.size()));
			editor.apply();
			//Log.e("get details.....", "---kk add STn : " + lstStn.size());

			/*********************************************************************************************/

			// url="http://vritti.co/iMedia/STA_Vigile_AndroidService_Test/WdbIntMgmtNew.asmx/GetCSNStatus_Android_new?Mobile="+mobno;
			// url =
			// "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetStationCallibration_Android?Mobile="
			// + mobno;

			date = new Date();
			dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);

			String bb = "";

			String urls = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetStationCallibrationNew?Mobile="
					+ mobno + "&NetworkCode=" + bb + "&InstallationId=" + bb;
			url = url.replaceAll(" ", "%20");

			System.out.println("============ internet reg url " + urls);

			try {
				System.out.println("-------  activity url --- " + urls);
				responsemsg = ut.httpGet(urls);

				System.out.println("-------------  xx vale of non repeated-- "
						+ responsemsg);

				System.out.println("------------- 1-- ");

				System.out.println("------------- 2-- ");
				//sql.execSQL("DROP TABLE IF EXISTS SoundLevel_new");
				System.out.println("------------- 3-- ");
				//sql.execSQL(ut.getSoundLevel_new());
				sql.delete("SoundLevel_new", null, null);

				System.out.println("------------- 4-- ");
				System.out.println("------------- 5-- ");

				// InstalationId
				if (responsemsg.contains("<InstalationId>")) {
					// sop = "valid";

					Cursor c = sql.rawQuery("SELECT * FROM SoundLevel_new",
							null);
					System.out.println("------------- 6-- ");
					ContentValues values = new ContentValues();
					System.out.println("------------- 7-- ");
					NodeList nl1 = ut.getnode(responsemsg, "Table");
					//Log.e("get SoundLevel_new node...", " fetch data : " + nl);
					String msg = "";
					String columnName, columnValue;
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < c.getColumnCount(); j++) {

							columnName = c.getColumnName(j);
							columnValue = ut.getValue(e, columnName);

							System.out.println("-------------column name"
									+ columnName);
							System.out.println("-------------column value"
									+ columnValue);
							// CallibrationDate=Oct 7 2015 7:05AM
							if (columnName.equalsIgnoreCase("CallibrationDate")) {
								try {
									Calendar cal = Calendar.getInstance();
									// "MM/dd/yyyy hh:mm:ss aa"
									/*SimpleDateFormat format = new SimpleDateFormat(
											"MMM dd yyyy hh:mm", Locale.ENGLISH);*/

									SimpleDateFormat format = new SimpleDateFormat(
											"yyyy-MM-dd hh:mm:ss");
									columnValue = columnValue.replace("T", " ");
									columnValue = columnValue.replace("+", "a");
									String part[] = columnValue.split("a");
									columnValue = part[0];

									Date Startdate = format.parse(columnValue);
									Date Enddate = cal.getTime();
									long diff = Enddate.getTime()
											- Startdate.getTime();
									long diffSeconds = diff / 1000 % 60;
									long diffMinutes = diff / (60 * 1000) % 60;
									long diffHours = diff / (60 * 60 * 1000)
											% 24;
									long diffDays = diff
											/ (24 * 60 * 60 * 1000);

									/*Log.e("getdetails", "sd : " + Startdate
											+ " ed: " + Enddate + " d: "
											+ diffDays + " h: " + diffHours
											+ " m:" + diffMinutes);*/

									if (diffDays == 0 && diffHours == 0
											&& diffMinutes <= 30) {

									} else {
										String conn = "valid";
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}

							values.put(columnName, columnValue);
							Log.d("test", "values :" + values);
						}

						long A = sql.insert("SoundLevel_new", null, values);
						Log.d("test", "SoundLevel_new " + values.size());
					}

					c.close();

				} else {
					// sop = "invalid";
					System.out.println("--------- invalid for AD list --- ");
				}

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

				responsemsg = "wrong" + e.toString();
				System.out
						.println("--------- invalid for message type list --- "
								+ responsemsg);

			}

			url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetTVStatus_Android_new?Mobile="
					+ mobno;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);
				//Log.e("csn status", "resmsg : " + responsemsg);

				if (responsemsg.contains("<A>")) {
					String columnName, columnValue;
					/*
					 * DatabaseHandler db = new
					 * DatabaseHandler(getBaseContext()); SQLiteDatabase sql =
					 * db.getWritableDatabase();
					 */
					//sql.execSQL("DROP TABLE IF EXISTS TvStatus");
					//sql.execSQL(ut.getTvStatus());
					sql.delete("TvStatus", null, null);

					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table1");
					Cursor cur = sql.rawQuery("SELECT *   FROM TvStatus", null);
					values1 = new ContentValues();
					nl1 = ut.getnode(responsemsg, "Table1");
					// String msg = "";
					// String columnName, columnValue;
					//Log.e("sts main...", " fetch data : " + nl1.getLength());
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
							else
								ncolumnname = columnName;
							columnValue = ut.getValue(e, ncolumnname);
							values1.put(columnName, columnValue);

							/*Log.e("DownloadxmlsDataURL_new...on back....",
									" count i: " + i + "  j:" + j);*/
						}
						sql.insert("TvStatus", null, values1);
					}

					cur.close();

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

			url = "http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetAllStation_Android";

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = com.stavigilmonitoring.utility.httpGet(url);
				//Log.e("csn status", "resmsg : " + responsemsg);
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

			}

			if (responsemsg.contains("<NetworkCode>")) {

				String columnName, columnValue;

				//sql.execSQL("DROP TABLE IF EXISTS AllStation");
				//sql.execSQL(ut.getAllStation());
				sql.delete("AllStation", null, null);

				Cursor cur = sql.rawQuery("SELECT * FROM AllStation", null);
				ContentValues values1 = new ContentValues();
				NodeList nl1 = ut.getnode(responsemsg, "Table1");
				// String msg = "";
				// String columnName, columnValue;
				Log.e("All Station data...",
						" fetch data : " + nl1.getLength());
				for (int i = 0; i < nl1.getLength(); i++) {
					Element e = (Element) nl1.item(i);
					for (int j = 0; j < cur.getColumnCount(); j++) {
						columnName = cur.getColumnName(j);

						columnValue = ut.getValue(e, columnName);
						values1.put(columnName, columnValue);

						// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
					}
					sql.insert("AllStation", null, values1);
				}
			}


			url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetListOfPendingDownloadingAdvertisment?Mobile="
					+ mobno + "&NetworkCode='ksrtc'";

			url = url.replaceAll(" ", "%20");
			Log.e("TV status pending Clips", "4th" + url);
			try {
				List<String> lstnon = new ArrayList<String>();
				lstnon.clear();
				responsemsg = ut.httpGet(url);
				int clipcnt = 0;
				NodeList n = ut.getnode(responsemsg, "Table1");
				clipcnt = n.getLength();
				for (int i = 0; i < n.getLength(); i++) {
					Element e = (Element) n.item(i);

					String clip = ut.getValue(e, "InstallationDesc");

					if (!lstnon.contains(clip))
						lstnon.add(clip);
					else {//Log.e("Already exist", clip);
					}

				}
				SharedPreferences prefclips = getApplicationContext()
						.getSharedPreferences("MyPrefclips",
								Context.MODE_PRIVATE); // 0 - for private mode
				Editor editorclips = prefclips.edit();
				editorclips.putString("clips", "" + lstnon.size());

				editorclips.putString("ClipCnt", "" + clipcnt);

				editorclips.apply();

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

			// url="http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetNonReportedAdvt_Android_new?Mobile="+mobno;
			url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetNonReportedAdvt_Android_new?Mobile="
					+ mobno;

			url = url.replaceAll(" ", "%20");
			Log.e("TV status pending Clips", "4th" + url);
			int noncount = 0;
			try {
				responsemsg = ut.httpGet(url);

				NodeList n = ut.getnode(responsemsg, "Table1");
				List<String> lstnon = new ArrayList<String>();
				for (int i = 0; i < n.getLength(); i++) {
					Element e = (Element) n.item(i);
					String val = ut.getValue(e, "D");
					if (lstnon.size() == 0) {
						lstnon.add(val);
						noncount = noncount
								+ Integer.parseInt(ut.getValue(e, "L"));
					} else {

						int dflag = 0;
						for (int j = 0; j < lstnon.size(); j++) {
							if (lstnon.get(j).equalsIgnoreCase(val)) {
								dflag = 1;
								break;
							}
						}
						if (dflag == 0) {
							lstnon.add(val);
							noncount = noncount
									+ Integer.parseInt(ut.getValue(e, "L"));
							// Log.e("get details.....",
							// "add STn : "+c.getString(c.getColumnIndex("InstallationDesc"))
							// );
						}
					}
				}
				SharedPreferences pref1 = getApplicationContext()
						.getSharedPreferences("MyPrefnon", Context.MODE_PRIVATE); // 0
				// -
				// for
				// private
				// mode
				Editor editor1 = pref1.edit();
				editor1.putString("nonreportedStatus",
						String.valueOf(lstnon.size()));
				editor1.putString("advCount", String.valueOf(noncount));
				editor1.apply();

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
			// ////////////////////////////////////////////////////////////////////////////////////

			url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/AlertGet?InstallationId="
					+ ""
					+ "&AddedBy="
					+ mobno;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				String responsemsg = ut.httpGet(url);
				//sql.execSQL("DROP TABLE IF EXISTS AlrtListTable");
				//sql.execSQL(ut.getAlrtListTable());
				sql.delete("AlrtListTable", null, null);

				Log.e("ALERT GETs", "resmsg : " + responsemsg);

				if (responsemsg.contains("<AlertId>")) {
					sop = "valid";
					String columnName, columnValue;
					/*
					 * DatabaseHandler db = new
					 * DatabaseHandler(getBaseContext()); SQLiteDatabase sql =
					 * db.getWritableDatabase();
					 */

					Cursor cur = sql.rawQuery("SELECT * FROM AlrtListTable", null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "TableResult");
					Log.e("AlrtListTable data...",
							" fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							values1.put(columnName, columnValue);

							// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
						}
						sql.insert("AlrtListTable",
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

			/********************************************************************************/


			url = "http://vritti.co/imedia/STA_Announcement/DmCertificate.asmx/GetListOfPendingDM?Mobile="
					+ mobno;

			Log.e("dm certificate", "dm certificate : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				String responsemsg = ut.httpGet(url);
				//sql.execSQL("DROP TABLE IF EXISTS DmCertificateTable");
				//sql.execSQL(ut.getDmCertificateTable());
				sql.delete("DmCertificateTable", null, null);

				Log.e("dm certificate", "resmsg : " + responsemsg);

				if (responsemsg.contains("<DMHeaderId>")) {
					sop = "valid";
					String columnName, columnValue;
					/*
					 * DatabaseHandler db = new
					 * DatabaseHandler(getBaseContext()); SQLiteDatabase sql =
					 * db.getWritableDatabase();
					 */


					Cursor cur = sql.rawQuery("SELECT * FROM DmCertificateTable", null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table1");
					Log.e("DmCertificate data...",
							" fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							values1.put(columnName, columnValue);

							// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
						}
						sql.insert("DmCertificateTable",
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

			/*****************************************************************************************/
			url = "http://vritti.co/imedia/STA_Announcement/DmCertificate.asmx/GetWorkAssignList?Mobile="
					+ mobno;

			url = url.replaceAll(" ", "%20");
			try {
				String responsemsg = ut.httpGet(url);
				//sql.execSQL("DROP TABLE IF EXISTS WorkAssignedTable");
				//sql.execSQL(ut.getWorkAssignList());
				sql.delete("WorkAssignedTable", null, null);

				Log.e("work assign", "resmsg : " + responsemsg);

				if (responsemsg.contains("<DMHeaderId>")) {
					sop = "valid";
					String columnName, columnValue;
					Cursor cur = sql.rawQuery("SELECT * FROM WorkAssignedTable", null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table1");
					Log.e("WorkAssignedTable data",
							" fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							values1.put(columnName, columnValue);
						}
						sql.insert("WorkAssignedTable",
								null, values1);
					}

					//	cur.close();

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

			/*****************************************************************************************/


			url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetCSNForWebLMS";
			url = url.replaceAll(" ", "%20");
			Log.e("lms connection", "5th" + url);
			int Lmsconnectioncount = 0;
			try {
				responsemsg = ut.httpGet(url);

				NodeList NL = ut.getnode(responsemsg, "Table");
				Log.e("lmsconut", "len :" + NL.getLength());

				List<String> lstlms = new ArrayList<String>();
				for (int i = 0; i < NL.getLength(); i++) {
					Element e = (Element) NL.item(i);
					String val = ut.getValue(e, "LastConnectionTime");

					String s = val.substring(0, val.indexOf("."));
					Calendar cal = Calendar.getInstance();
					// SimpleDateFormat format = new
					// SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa");
					try {
						SimpleDateFormat format = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

						Date Startdate = format.parse(s);

						Date Enddate = cal.getTime();
						long diff = Enddate.getTime() - Startdate.getTime();
						long diffSeconds = diff / 1000 % 60;
						long diffMinutes = diff / (60 * 1000) % 60;
						long diffHours = diff / (60 * 60 * 1000) % 24;
						long diffDays = diff / (24 * 60 * 60 * 1000);

						/*Log.e("getdetails", "sd : " + Startdate + " ed: "
								+ Enddate + " d: " + diffDays + " h: "
								+ diffHours + " m:" + diffMinutes);

						Log.e("printdiff.........", "diffDays: " + diffDays);
						Log.e("printdiff.........", "diffHours: " + diffHours);
						Log.e("printdiff.........", "diffMinutes: "
								+ diffMinutes);
						Log.e("printdiff.........", "diffSeconds: "
								+ diffSeconds);*/

						if (diffDays == 0 && diffHours == 0
								&& diffMinutes <= 30) {
							String Null = "Invalid";
						} else {
							lstlms.add(val);
							Lmsconnectioncount = Lmsconnectioncount + 1;
						}

					} catch (Exception Ex) {

						Ex.printStackTrace();
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
									+ Ex.getMessage() + " " + Ldate);
						}
					}

				}
				SharedPreferences preflmsconn = getApplicationContext()
						.getSharedPreferences("PrefLmsCount",
								Context.MODE_PRIVATE);
				Editor editorlmsConne = preflmsconn.edit();
				editorlmsConne.putString("LmsCount",
						String.valueOf(lstlms.size()));

				editorlmsConne.apply();

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

			url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMaster";
			url = url.replaceAll(" ", "%20");
			Log.e("installation for Subnet", "6th" + url);

			try {
				responsemsg = ut.httpGet(url);
				NodeList NL = ut.getnode(responsemsg, "Table");
				Log.e("SubnetCount", "len :" + NL.getLength());
				// DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
				// SQLiteDatabase sq = db.getWritableDatabase();
				if (responsemsg.contains("<Table>")) {
					sop = "valid";
					//sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusFilter");
					//sql.execSQL(ut.getConnectionStatusFilter());
					sql.delete("ConnectionStatusFilter", null, null);

					Cursor cur1 = sql.rawQuery(
							"SELECT * FROM ConnectionStatusFilter", null);
					ContentValues values2 = new ContentValues();

					/*Log.e("ConnectionFilter data...",
							" fetch data : " + NL.getLength());*/
					for (int i = 0; i < NL.getLength(); i++) {
						Element e = (Element) NL.item(i);
						String columnName, columnValue;
						for (int j = 0; j < cur1.getColumnCount(); j++) {
							columnName = cur1.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							values2.put(columnName, columnValue);
							SubnetString = "Valid";

						}
						long ad = sql.insert("ConnectionStatusFilter", null,
								values2);
					}

					cur1.close();

				}

			} catch (Exception e) {
				// TODO: handle exception
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
			}

			String Url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMasterMobile?Mobile=" + mobno;
			Log.e("All Station", "Url=" + Url);

			try {
				String resposmsg = ut.httpGet(Url);


				if (resposmsg.contains("<InstalationId>")) {
					/*sop = "valid";*/
					//DatabaseHandler db2 = new DatabaseHandler(getBaseContext());
					//SQLiteDatabase sql1 = db.getWritableDatabase();
					String columnName, columnValue;

					//sql1.execSQL("DROP TABLE IF EXISTS ConnectionStatusFiltermob");
					//sql1.execSQL(ut.getConnectionStatusFiltermob());
					sql.delete("ConnectionStatusFiltermob", null, null);

					Cursor cur1 = sql.rawQuery(
							"SELECT * FROM ConnectionStatusFiltermob", null);
					cur1.getCount();
					ContentValues values2 = new ContentValues();
					NodeList nl2 = ut.getnode(resposmsg, "Table");

					Log.e("All Station Data ", "get length : " + nl2.getLength());
					for (int i = 0; i < nl2.getLength(); i++) {
						//Log.e("All Station Data ", "length : " + nl2.getLength());
						Element e = (Element) nl2.item(i);
						for (int j = 0; j < cur1.getColumnCount(); j++) {
							columnName = cur1.getColumnName(j);
							columnValue = ut.getValue(e, columnName);
						/*Log.e("All Station Data ", "column Name : "
								+ columnName);
						Log.e("All Station Data ", "column value : "
								+ columnValue);*/

							values2.put(columnName, columnValue);

						}
						sql.insert("ConnectionStatusFiltermob", null, values2);
					}

					cur1.close();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
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
			}

			url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetBGPlaylistContent?MobileNo="
					+ mobno;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				String responsemsg = ut.httpGet(url);
				//Log.e("csn status", "resmsg : " + responsemsg);

				//sql.execSQL("DROP TABLE IF EXISTS Backgroundplaylist");
				//sql.execSQL(ut.Databg());
				sql.delete("Backgroundplaylist", null, null);

				Cursor cur = sql.rawQuery("SELECT *   FROM Backgroundplaylist",
						null);
				Log.e("Table values----", "" + cur.getCount());
				if (responsemsg.contains("<PlaylistName>")) {
					sop = "valid";
					String columnName, columnValue;
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table");
					//Log.e("sts main...", " fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						String conn = "invalid";
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);
							columnValue = ut.getValue(e, columnName);
							values1.put(columnName, columnValue);
							/*Log.e("DownloadxmlsDataURL_new...on back....",
									" count i: " + i + "  j:" + j);*/
						}

						sql.insert("Backgroundplaylist", null, values1);
					}

					cur.close();

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

			return null;
		}

		public void updatelist_bg() {
			searchResults.clear();
			//SQLiteDatabase sql = db.getWritableDatabase();
			int count = 0;
			Cursor c = sql
					.rawQuery(
							"SELECT DISTINCT NetworkCode FROM Backgroundplaylist",
							null);

			if (c.getCount() == 0) {
				totalstation_bg = 0;

			} else
				c.moveToFirst();
			do {
				try{

					count = 0;
					String Type = c.getString(0);

					Cursor c1 = sql.rawQuery(
							"SELECT DISTINCT InstalationId FROM Backgroundplaylist WHERE NetworkCode='"
									+ Type + "'", null);
					count = c1.getCount();

					Type = Type.replaceAll("0", "");
					Type = Type.replaceAll("1", "");
					if (!Type.trim().equalsIgnoreCase("")) {
						StatelevelList sitem = new StatelevelList();
						sitem.SetNetworkCode(Type);
						sitem.Setcount(count);
						searchResults.add(sitem);

					}
					totalstation_bg = totalstation_bg + count;
					c1.close();
				}catch (Exception e){
					e.printStackTrace();
				}
			} while (c.moveToNext());

		}

		public void updateAlertCount() {
			// searchResults.clear();
			searchResults.clear();
			//SQLiteDatabase sql = db.getWritableDatabase();
			int count = 0;
			//sumdata2 = "0";
			if (sumdata2.equals("1")){
				Cursor c = sql.rawQuery(
						"SELECT SUM(CAST(cnt AS INT)) as sumdata FROM AlrtCountTable", null);
				Log.e("cursor", String.valueOf("IF Part"));
				Log.e("COUNT Alert", String.valueOf(c.getCount()));
				//Log.e("cursor", String.valueOf(c.getString(c.getColumnIndex("sumdata"))));
				if (c.moveToFirst()){
					do{
						sumdata2 = c.getString(c.getColumnIndex("sumdata"));
						Log.e("cursor", String.valueOf(c.getString(c.getColumnIndex("sumdata"))));
						// do what ever you want here
					}while(c.moveToNext());


					SharedPreferences prefalertcount = getApplicationContext()
							.getSharedPreferences("Prefalertcount", Context.MODE_PRIVATE);
					Editor editoralertcount = prefalertcount.edit();
					// String TVsound = prefsound.getString("TVSound", "");
					editoralertcount.putString("AlertCount", sumdata2);
					editoralertcount.apply();
					//Log.e("get details.....", "---kk add STn : " + totalstation);
					alertcounts.setText(String.valueOf(sumdata2));
				}
				c.close();
			}else if (sumdata2.equals("0")){
				Log.e("cursor", String.valueOf("ELSE Part"));
				SharedPreferences prefalertcount = getApplicationContext()
						.getSharedPreferences("Prefalertcount", Context.MODE_PRIVATE);
				Editor editoralertcount = prefalertcount.edit();
				// String TVsound = prefsound.getString("TVSound", "");
				editoralertcount.putString("AlertCount", sumdata2);
				editoralertcount.apply();
				//Log.e("get details.....", "---kk add STn : " + totalstation);
				alertcounts.setText(String.valueOf(sumdata2));
			}
		}

		public void updatelist() {
			// searchResults.clear();
			searchResults.clear();
			//SQLiteDatabase sql = db.getWritableDatabase();
			int count = 0;
			Cursor c = sql.rawQuery(
					"SELECT DISTINCT NetworkCode FROM SoundLevel_new", null);
			if (c.getCount() > 0) {
				c.moveToFirst();
				do {

					String Type = c.getString(0);

					Cursor c1 = sql.rawQuery(
							"Select distinct InstallationDesc from SoundLevel_new Where NetworkCode='"
									+ Type + "'", null);
					count = c1.getCount();

					Type = Type.replaceAll("0", "");
					Type = Type.replaceAll("1", "");
					if (!Type.trim().equalsIgnoreCase("")) {
						StatelevelList sitem = new StatelevelList();
						sitem.SetNetworkCode(Type);
						sitem.Setcount(count);
						searchResults.add(sitem);

					}
					totalstation = totalstation + count;
					c1.close();
				} while (c.moveToNext());

				SharedPreferences prefsound = getApplicationContext()
						.getSharedPreferences("PrefSound", Context.MODE_PRIVATE);
				Editor editorsound = prefsound.edit();
				// String TVsound = prefsound.getString("TVSound", "");
				editorsound.putString("TVSound", String.valueOf(totalstation));
				editorsound.apply();
				//Log.e("get details.....", "---kk add STn : " + totalstation);
				soundcunt.setText(String.valueOf(totalstation));

				String totalDMC =  DmCstnwiseActivity.dbvalueDMC(getApplicationContext());
				SharedPreferences prefDMC = getApplicationContext()
						.getSharedPreferences("PrefDMC", Context.MODE_PRIVATE);
				Editor editorDMC = prefDMC.edit();
				editorDMC.putString("DMC",String.valueOf(totalDMC));
				editorDMC.apply();
				dmccounts.setText(String.valueOf(totalDMC));
			}
			c.close();
		}

		/*SharedPreferences pref = getApplicationContext().getSharedPreferences("bgpref", Context.MODE_PRIVATE); // bgpref
		// bgPlayCount

		Editor editor = pref.edit();// bgprefbgPlayCount
			editor.putString("bgPlayCount",String.valueOf(totalstation_bg));
			editor.apply();

		//Log.e("get details.....", "---kk add STn : " + totalstation_bg);
			bg_palycount.setText(String.valueOf(totalstation_bg));
			c.close();*/

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			SharedPreferences pref = getApplicationContext()
					.getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0
			// -
			// for
			// private
			// mode
			Editor editor = pref.edit();
			String values = pref.getString("csnStatusCount", "");
			status.setText(values);

			SharedPreferences pref1 = getApplicationContext()
					.getSharedPreferences("MyPrefnon", 0); // 0 - for private
			// mode
			Editor editor1 = pref1.edit();
			String values1 = pref1.getString("nonreportedStatus", null);
			String nonval = pref1.getString("advCount", null);
			nonreportedStatus.setText(values1);
			((TextView) findViewById(R.id.tvnon2)).setText("Non-Reported Ads : " + nonval);
			NonRepAddCount.setText(nonval);
			NonRepStationCount.setText("Non-Reported Stations : " + values1);
			tvCount();
			SharedPreferences prefTV1 = getApplicationContext()
					.getSharedPreferences("PrefTVStatus", Context.MODE_PRIVATE); //PrefTVStatus
			Editor editorTV1 = prefTV1.edit();
			String TVStatus = prefTV1.getString("TVStatus", "");
			tvcsnstatuscnt.setText(TVStatus);


			// //********LMS Connection Count*******/////
			SharedPreferences preflmsconn = getApplicationContext()
					.getSharedPreferences("PrefLmsCount", Context.MODE_PRIVATE);
			Editor editorlmsConne = preflmsconn.edit();
			String LMsCount = preflmsconn.getString("LmsCount", "");
			LmsconnectionStatuscount.setText(LMsCount);
			// ***************************************************************//

			SharedPreferences prefclips = getApplicationContext()
					.getSharedPreferences("MyPrefclips", Context.MODE_PRIVATE);
			Editor editorclips = prefclips.edit();
			String valuesclips = prefclips.getString("clips", null);
			String ClipCnt = prefclips.getString("ClipCnt", null);
			tvclips.setText(valuesclips);
			((TextView) findViewById(R.id.tvclipcnt)).setText("Pending Clips : " + ClipCnt);
			PDCClipCount.setText(ClipCnt);
			PDCStatncount.setText("Pending Clips Stations : " + valuesclips);
			// setProgressBarIndeterminateVisibility(false);
			// hideProgressBar();

			((ImageView) findViewById(R.id.ivProcess))
					.setBackgroundDrawable(null);

			updateAlertCount();
			updatelist();
			updatelist_bg();
			progressDialog.dismiss();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			// pb.setProgress(progress);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			Log.e("UploadDataBack", "onPreExecute");
			progressDialog = new ProgressDialog(SelectMenu.this);
			progressDialog.setMessage("Updating database...");

			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);

			progressDialog.show();

			Animation animation = AnimationUtils.loadAnimation(SelectMenu.this,
					R.anim.rotation);
			// setProgressBarIndeterminateVisibility(true);
			// And when you want to turn it off
			// showProgressBar();

			((ImageView) findViewById(R.id.ivProcess))
					.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.loading));
			((ImageView) findViewById(R.id.ivProcess))
					.startAnimation(animation);

		}

	}

	private void tvCount() {
		try {

			// arrlist.clear();
			int cnt = 0;
			List<TvStatusStateBean> searchResults = new ArrayList<TvStatusStateBean>();
			//DatabaseHandler db = new DatabaseHandler(this);
			//SQLiteDatabase sql = db.getWritableDatabase();

			Cursor c1 = sql
					.rawQuery("select distinct Type from TvStatus", null);
			int Scnt = 0;
			c1.moveToFirst();
			do {
				Log.e("Table count", "" + c1.getCount());
				String Type = c1.getString(c1.getColumnIndex("Type"));
				// String Type = c1.getString(c1.getColumnIndex("N"));
				Cursor c = sql
						.rawQuery(
								"Select distinct c1.InstallationId,c1.TVStatus,c1.flg from TvStatus c1  inner join AllStation c2  on c1.InstallationId=c2.InstallationId where c2.NetworkCode='"
										+ Type + "'", null);//AllStation

				if (c.getCount() == 0) {
					c.close();
				} else {
					TvStatusStateBean sitem = new TvStatusStateBean();
					c.moveToFirst();
					int column = 0;
					do {

						int i = 0;
						int s = c.getInt(c.getColumnIndex("flg"));
						String s1 = c.getString(c.getColumnIndex("TVStatus"));

						for (char d : s1.toCharArray()) {
							if (d == '0') {
								cnt++;
							}
						}

						if (!Type.trim().equalsIgnoreCase("") && s != 0) {// if
							// (!Type.trim().equalsIgnoreCase("")
							// && cnt != 8) {

							column++;
							sitem.Setcount(cnt);
							int a = sitem.Getcount();
							if (a != 0) {
								Scnt++;
							}
							sitem.SetNetworkCode(Type);
							sitem.SetScount(Scnt);
						}

						cnt = 0;

					} while (c.moveToNext());

					if (column != 0) {
						sitem.SettotalStation(column);
						searchResults.add(sitem);
					}
					Log.e("TV count", "" + column);
					Scnt = 0;
				}

			} while (c1.moveToNext());

			c1.close();

			int scount = 0;
			for (int i = 0; i < searchResults.size(); i++)
				scount = scount + searchResults.get(i).Getcount();

			SharedPreferences prefTV1 = getApplicationContext()
					.getSharedPreferences("PrefTVStatus", Context.MODE_PRIVATE);
			Editor editorTV1 = prefTV1.edit();
			editorTV1.putString("TVStatus", String.valueOf(scount));//TVStatus
            editorTV1.apply();
          //  tvcsnstatuscnt.setText(scount);//tvcsnstatuscnt
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

	public static void regservicenonGPS(Context mcontext) {
	}

	private String[] splitfromtym(String tym) {
		String fromtimetw = "";

		final String dateStart = tym;
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.ENGLISH);
		Date date = new Date();
		final String dateStop = dateFormat.format(date);

		Date d1 = null;
		Date d2 = null;
		String diffTym = "";

		try {
			d1 = dateFormat.parse(dateStart);
			d2 = dateFormat.parse(dateStop);

			long diff = d2.getTime() - d1.getTime();

			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			String days = String.valueOf(diffDays);
			String hours = String.valueOf(diffHours);
			String minutes = String.valueOf(diffMinutes);
			if (days.equals("0")) {
				if (hours.equals("0")) {
					int i = Integer.parseInt(minutes);
					if (i >= 30) {
						diffTym = diffMinutes + " Minutes ";
					} else {
						diffTym = "";
					}
				} else {

					// diffTym=diffHours+" Hours "+diffMinutes+" Minutes ";
					diffTym = diffHours + " Hours ";

				}

			} else {
				diffTym = diffDays + " Days ";
			}

			System.out.print(diffDays + " days, ");
			System.out.print(diffHours + " hours, ");
			System.out.print(diffMinutes + " minutes, ");
			System.out.print(diffSeconds + " seconds.");

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

		String[] s = { diffTym };
		return s;
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

						Date date = new Date();
						final Calendar c = Calendar.getInstance();

						Year = c.get(Calendar.YEAR);
						month = c.get(Calendar.MONTH);
						day = c.get(Calendar.DAY_OF_MONTH);

						TODAYDATE = day + "-"
								+ (month + 1) + "-" + Year;

						SharedPreferences LoginPref = getApplicationContext()
								.getSharedPreferences("SetupPref",Context.MODE_PRIVATE); // 0 - for private mode
						Editor edtcv = LoginPref.edit();
						edtcv.putString("Dialog", "NoDialog");
						edtcv.putString("TodaysDate",TODAYDATE);
						edtcv.apply();

                        showUpdateDialog(PlayStoreVersion);
                    }
                }

            } catch (IOException e) {
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
            } catch (PackageManager.NameNotFoundException e) {
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
            }catch (NullPointerException e){
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
            }catch (Exception e){
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
            }
        }
    }

    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(
					Config.EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message depending upon your app
			 * requirement For now i am just displaying it on the screen
			 * */

			// Showing received message
			// lblMessage.append(newMessage + "\n");
			Toast.makeText(getApplicationContext(),
					"New Message: " + newMessage, Toast.LENGTH_LONG).show();

			// Releasing wake lock
			WakeLocker.release();
		}
	};

	public void AutoRefreshData(){

		try{
			long aTime = 1000 * 60 * 180;  //1000*60 = 1min

			autoUpdate = new Timer();
			autoUpdate.schedule(new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						public void run() {

							refreshConnectionStatus();

							SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
							String CSNStatusCount = pref.getString("csnStatusCount", null);
							status.setText(CSNStatusCount);

							//////////********Connection Status***********//////////
							SharedPreferences prefDate = getApplicationContext().getSharedPreferences("MyPrefDate", Context.MODE_PRIVATE);
							String valuesDate = prefDate.getString("Dates", "");
							datess.setText(valuesDate);
							/////////////********Connection Status********/////////////
							SharedPreferences pref1 = getApplicationContext().getSharedPreferences("MyPrefnon", Context.MODE_PRIVATE); //0 - for private mode
							String values1 = pref1.getString("nonreportedStatus", null);
							nonreptAdvCount = pref1.getString("advCount", null);
							NonRepAddCount.setText(nonreptAdvCount);
							NonRepStationCount.setText("Non-Reported Stations : " + values1);

							SharedPreferences prefDMC = getApplicationContext().getSharedPreferences("PrefDMC", Context.MODE_PRIVATE);
							Editor editorDMC = prefDMC.edit();
							String DMC = prefDMC.getString("DMC", null);
							dmccounts.setText(DMC);

							SharedPreferences prefclips = getApplicationContext().getSharedPreferences("MyPrefclips", Context.MODE_PRIVATE); // 0
							String valuesclips = prefclips.getString("clips", null);
							ClipCnt = prefclips.getString("ClipCnt", null);
							tvclips.setText(valuesclips);
							/***/tvclipscnt.setText("Pending Clips : " + ClipCnt);
							PDCClipCount.setText(ClipCnt);
							PDCStatncount.setText("Pending Clips Stations : " + valuesclips);

							SharedPreferences prefTV1 = getApplicationContext().getSharedPreferences("PrefTVStatus", Context.MODE_PRIVATE);
							String TVStatus = prefTV1.getString("TVStatus", null);
							tvcsnstatuscnt.setText(TVStatus);//values1

							SharedPreferences prefsound = getApplicationContext().getSharedPreferences("PrefSound", Context.MODE_PRIVATE);
							String sound = prefsound.getString("TVSound", null);
							soundcunt.setText(sound);

							SharedPreferences prefalertcount = getApplicationContext().getSharedPreferences("Prefalertcount", Context.MODE_PRIVATE);
							String sumdata = prefalertcount.getString("AlertCount", null);
							alertcounts.setText(sumdata);
							//editoralertcount.apply();

							SharedPreferences prefmaterial = getApplicationContext().getSharedPreferences("PrefMaterial", Context.MODE_PRIVATE);
							String soundMaterial = prefmaterial.getString("TVMaterial", null);
							tvmaterialrequirementStationcount.setText(soundMaterial);

							//////********LMS Connection Count*******/////
							SharedPreferences preflmsconn = getApplicationContext().getSharedPreferences("PrefLmsCount", Context.MODE_PRIVATE);
							String LMsCount = preflmsconn.getString("LmsCount", null);
							LmsconnectionStatuscount.setText(LMsCount);

							//////********LMS Connection Count*******/////
							SharedPreferences Prefbgplay = getApplicationContext().getSharedPreferences("bgpref", Context.MODE_PRIVATE);
							String BgPaly = Prefbgplay.getString("bgPlayCount", null);
							bg_palycount.setText(BgPaly);
						}
					});

				}
			}, 0, 60000 * 5);//}, 0, 40000 ); // updates each 40 secs

			//refresh alert count
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public  void refreshConnectionStatus(){
		new DownloadxmlsDataURL_ConneStatus().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public class DownloadxmlsDataURL_ConneStatus extends AsyncTask<String, Void, String> {
		ProgressDialog progressDialog;

		@Override
		protected String doInBackground(String... params) {
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			//SQLiteDatabase sql = db.getWritableDatabase();

			String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetCSNStatus_Android_new?Mobile=" + mobno;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);
				Log.i("csn status", "resmsg : " + responsemsg);
				Log.d("csn status", "resmsg : " + responsemsg);
				Log.e("csn status", "resmsg : " + responsemsg);

				if (responsemsg.contains("<A>")) {
					sop = "valid";
					String columnName, columnValue;
					sql.delete("ConnectionStatusUser",null,null);

					Cursor cur = sql.rawQuery("SELECT *   FROM ConnectionStatusUser", null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table1");
					// String msg = "";
					// String columnName, columnValue;
					Log.e("sts main...", " fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						String conn = "invalid";
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);

							String ncolumnname = "";
							if (columnName.equalsIgnoreCase("InstallationId"))
								ncolumnname = "A";
							else if (columnName.equalsIgnoreCase("ServerTime"))
								ncolumnname = "B";
							else if (columnName.equalsIgnoreCase("StartTime"))
								ncolumnname = "C";
							else if (columnName.equalsIgnoreCase("EndTime"))
								ncolumnname = "D";
							else if (columnName.equalsIgnoreCase("Remarks"))
								ncolumnname = "E";
							else if (columnName
									.equalsIgnoreCase("InstallationDesc"))
								ncolumnname = "F";
							else if (columnName.equalsIgnoreCase("TVStatus"))
								ncolumnname = "G";
								// else
								// if(columnName.equalsIgnoreCase("Last7DaysPerFormance"))
								// ncolumnname="H";
								// else
								// if(columnName.equalsIgnoreCase("QuickHealStatus"))
								// ncolumnname="I";
							else if (columnName.equalsIgnoreCase("STAVersion"))
								ncolumnname = "J";
							else if (columnName
									.equalsIgnoreCase("AscOrderServerTime"))
								ncolumnname = "K";
							else if (columnName
									.equalsIgnoreCase("LatestDowntimeReason"))
								ncolumnname = "L";
								// else if(columnName.equalsIgnoreCase("UserName"))
								// ncolumnname="M";
								// else
								// if(columnName.equalsIgnoreCase("SubHeadPH_No"))
								// ncolumnname="O";
								// else
								// if(columnName.equalsIgnoreCase("SupportAgencyName"))
								// ncolumnname="P";
							else if (columnName.equalsIgnoreCase("Type"))
								ncolumnname = "N";
							else if (columnName
									.equalsIgnoreCase("SubNetworkCode"))
								ncolumnname = "R";
							// String tftym=ut.getValue(e, "B");

							columnValue = ut.getValue(e, ncolumnname);

							if (columnName.equalsIgnoreCase("ServerTime")) {
								try {
									Calendar cal = Calendar.getInstance();
									// SimpleDateFormat format = new
									// SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa");
									//2017-03-17 08:53:26
									/*SimpleDateFormat format = new SimpleDateFormat(
											"yyyy-MM-dd hh:mm:ss");

									SimpleDateFormat format = new SimpleDateFormat(
											"dd-MM-yyyy hh:mm:ss");*/

									SimpleDateFormat format = new SimpleDateFormat(
											"MM/dd/yyyy hh:mm:ss aa",Locale.ENGLISH);
									Log.e("columnValue...","diffDays: "+
											columnValue);
									Log.e("columnValue...","diffDays: "+
											"MM/dd/yyyy hh:mm:ss aa");
									Date Startdate = format.parse(columnValue);
									Date Enddate = cal.getTime();
									long diff = Enddate.getTime()
											- Startdate.getTime();
									long diffSeconds = diff / 1000 % 60;
									long diffMinutes = diff / (60 * 1000) % 60;
									long diffHours = diff / (60 * 60 * 1000)
											% 24;
									long diffDays = diff
											/ (24 * 60 * 60 * 1000);

									Log.e("getdetails", "sd : " + Startdate
											+ " ed: " + Enddate + " d: "
											+ diffDays + " h: " + diffHours
											+ " m:" + diffMinutes);

									if (diffDays == 0 && diffHours == 0
											&& diffMinutes <= 15) {

									} else {
										conn = "valid";
									}
								} catch (Exception ex) {
									ex.printStackTrace();
									dff = new SimpleDateFormat("HH:mm:ss");
									Ldate = dff.format(new Date());

									StackTraceElement l = new Exception()
											.getStackTrace()[0];
									System.out.println(l.getClassName() + "/"
											+ l.getMethodName() + ":"
											+ l.getLineNumber());
									ut = new com.stavigilmonitoring.utility();
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
							}

							values1.put(columnName, columnValue);

							// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
						}
						if (conn == "valid")
							Log.e("csn status", "resmsg : " + values1);
						sql.insert("ConnectionStatusUser", null, values1);
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

				if (sop.equals("valid")) {
					//updatelist();
				} else {
					try{
						ut.showD(SelectMenu.this,"invalid");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				//iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.GONE);

				DateFormat dateFormat = new SimpleDateFormat(
						"dd-MMM-yyyy HH:mm:ss aa", Locale.ENGLISH);
				Date date = new Date();
				String datestring = dateFormat.format(date);
				SharedPreferences prefDate = getApplicationContext()
						.getSharedPreferences("MyPrefDate",
								Context.MODE_PRIVATE); // 0 - for private mode
				Editor editorDate = prefDate.edit();
				editorDate.putString("Dates", datestring);
				editorDate.commit();
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
			progressDialog.dismiss();

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(SelectMenu.this);
			//progressDialog.setMessage("Updating data...");
			//progressDialog.setCanceledOnTouchOutside(false);
			//progressDialog.setCancelable(false);
			//progressDialog.show();
			//iv.setVisibility(View.GONE);
			/*((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);*/
		}
	}

	/*Get password methods*/
	public class GetStationPassword extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String result = "";
			@SuppressLint("WrongThread") String url = "http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetStationPassword_Android?Mobile=" +
					mobno + "&Station=" + btmsheetedsationname.getTag().toString();

			url = url.replaceAll(" ", "%20");

			try {
				System.out.println("-------  activity url --- " + url);
				result = ut.httpGet(url);
				Log.e("Data", result);

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

			} catch (Exception e) {
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

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (result == null || result == "" || result.equalsIgnoreCase("error")) {
				Toast.makeText(SelectMenu.this, "Server not responding, Try again later.", Toast.LENGTH_SHORT).show();
				pd.dismiss();

			} else {
				try {
					NodeList n1 = ut.getnode(result, "Table1");
					Element ele = (Element) n1.item(0);
					Log.e("ele", ele.toString());

					txtpass.setText(ut.getValue(ele, "Password"));
					pd.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(SelectMenu.this);
			/* pd.setTitle("Please Wait.."); */
			pd.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			pd.setMessage("Loading...");
			pd.setCancelable(false);
			pd.show();

		}

	}

	/*Set refresh time  methods*/

	protected void regservice() {
		// DatabaseHandler db=new DatabaseHandler(SettingsActivity.this);
		String val=db.getSetting();
		int itime=Integer.parseInt(val);
		long aTimenon = 1000 * 60 * itime;

		System.out.println("..........start");
		Intent myIntent = new Intent(this, AlarmManagerBroadcastReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 12345,
				myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
//	am.setRepeating(AlarmManager.ELAPSED_REALTIME,
//			SystemClock.elapsedRealtime(), 1000 * 60 * 1, pendingIntent);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), aTimenon, pendingIntent);

		//Toast.makeText(getBaseContext(), "started", Toast.LENGTH_LONG).show();
	}

	protected void regservicesound() {
		//DatabaseHandler db = new DatabaseHandler(SettingsActivity.this);
		String val = db.getSetting();

		int itime = Integer.parseInt(val);

		long aTimenon = 1000 * 60 * itime;

		//long aTimenon = 1000 * 60 * 60 * 3;
		System.out.println("..........start");
		Intent myIntent = new Intent(this, SoundLevelBrodcastReciver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this,12345,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);

		//this, 12345,
		//myIntent, PendingIntent.FLAG_UPDATE_CURRENT
		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), aTimenon, pendingIntent);
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

            }else if(key.equalsIgnoreCase("PaidLocationFusedLocation")){
				callJobDispacher_PaidLocationFusedLocation();
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

                }else if(key.equalsIgnoreCase("PaidLocationFusedLocation")){
					callJobDispacher_PaidLocationFusedLocation();
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

                }else if(key.equalsIgnoreCase("PaidLocationFusedLocation")){
					callJobDispacher_PaidLocationFusedLocation();

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

	private void callJobDispacher_PaidLocationFusedLocation() {
		myJob = dispatcher.newJobBuilder()
				// the JobService that will be called
				.setService(JobService_PaidLocationFusedLocationTracker1.class)
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

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    private String createPdf(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            document = new PdfDocument();
        }
        PdfDocument.PageInfo pageInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        }
        PdfDocument.Page page = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            page = document.startPage(pageInfo);
        }

        Canvas canvas = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            canvas = page.getCanvas();
        }

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            document.finishPage(page);
        }

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_YYYY");
		String addedDt = sdf.format(c.getTime());

        // write the document content
		final String fileName = addedDt+"_stvisit.pdf";
        String targetPdf = Environment.getExternalStorageDirectory().getAbsolutePath() +"/StationVisitForms/"+"TestMSRTC_"+fileName;
        String _fileName = "TestMSRTC_"+fileName;
        File filePath;
        filePath = new File(targetPdf);
        try {

			if (!filePath.exists()) {
				filePath.getParentFile().mkdirs();
				filePath.createNewFile();
			}

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				//FileOutputStream fileOutputStream = new FileOutputStream(filePath,true);
                document.writeTo(new FileOutputStream(filePath));
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            document.close();
        }
        Toast.makeText(this, "PDF of Scroll is created!!!", Toast.LENGTH_SHORT).show();

        return _fileName;

       // openGeneratedPDF();
    }

    private void openGeneratedPDF(){
        File file = new File("/sdcard/pdffromScroll.pdf");

        if (file.exists())
        {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try
            {
                startActivity(intent);
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(SelectMenu.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

	@Override
	public void onResume() {
		super.onResume();
		//refresh data
		AutoRefreshData();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		String pass, id;
		if (resultCode == Activity.RESULT_OK) {
			pass = data.getExtras().getString("station");
			id = data.getExtras().getString("InstallationId");

			btmsheetedsationname.setText(pass);
			btmsheetedsationname.setTag(id);
		}
	}

}

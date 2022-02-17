package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.AlrtStnAdap;
import com.beanclasses.StateList;
import com.database.DBInterface;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AlrtsStnListAll extends Activity {
    private static DownloadnetWork_New asynk_new;
    private ImageView ivRefresh, ivFilter;
    private TextView tvfilter, heading;
    private ProgressBar mProgressBar;
    private ListView mListView;
    private String sop, filter, responsemsg;
    static SimpleDateFormat dff;
    static String Ldate;
    String diffstr;
    private static AlrtListURL async;
    ArrayList<StateList> searchResults;
    private AlrtStnAdap StationAdaptor;
    private String mType, mobno, resposmsg, installationid, intentfrom, Activity, Subnetwork;
    com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
    Bundle dataBundle = new Bundle();
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.inventallstationlist_alert);

        heading = (TextView) findViewById(com.stavigilmonitoring.R.id.stationInvent);
        //heading.setText("Alert List");
        ivRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_invent);
        ivFilter = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_invent_filter);
        mProgressBar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressinvent);
        mListView = (ListView) findViewById(com.stavigilmonitoring.R.id.listInvent);
        tvfilter = (TextView) findViewById(com.stavigilmonitoring.R.id.edfitertext_search_in_invt);

        db = new DatabaseHandler(AlrtsStnListAll.this);
        searchResults = new ArrayList<StateList>();

        Intent i = getIntent();
        Activity = i.getStringExtra("Activity");

        if (Activity.equalsIgnoreCase("AlrtListActivity") ||
                Activity.equalsIgnoreCase("AlrtsStatewise")) {
            mType = i.getStringExtra("Type");
            intentfrom = i.getStringExtra("intentfrom");
            heading.setText("Alert List");
        } else if (Activity.equalsIgnoreCase("SupporterList")) {
            mType = i.getStringExtra("Type");
            Subnetwork = i.getStringExtra("SubNetwork");
            heading.setText("Alert List - " + Subnetwork);
        }

        //((TextView) findViewById(R.id.stationInvent)).setText("Alert List");

        DBInterface dbi = new DBInterface(AlrtsStnListAll.this);
        mobno = dbi.GetPhno();
        dbi.Close();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (Activity.equalsIgnoreCase("AlrtListActivity") ||
                Activity.equalsIgnoreCase("AlrtsStatewise")) {
            mListView.setClickable(true);

            mListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub

                    Log.e("Check List item Click", searchResults.get(position).getStatioName());
String alertId="";
                    //Intent intent = new Intent(AlrtsStnListAll.this,AlrtCreateActivity.class);
                    installationid = searchResults.get(position).getInstallationId();
                    alertId = searchResults.get(position).getAlertId();
                    updatelist3(installationid,alertId);
                    //Intent intent = new Intent(AlrtsStnListAll.this,AlrtListActivity.class);
				/*intent.putExtra("Type", searchResults.get(position).getStatioName());
				intent.putExtra("InstallationId", searchResults.get(position).getInstallationId());*/

                    //startActivity(intent);
                    //finish();

                }
            });

        } else if (Activity.equalsIgnoreCase("SupporterList")) {
            mListView.setClickable(false);

        }


        ivRefresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (ut.isnet(AlrtsStnListAll.this)) {
                    asynk_new = null;
                    asynk_new = new DownloadnetWork_New();
                    asynk_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    //fetchdata();
                } else {
                    try {
                        ut.showD(AlrtsStnListAll.this, "nonet");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        ivFilter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (tvfilter.getVisibility() == View.VISIBLE) {
                    tvfilter.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus()
                            .getWindowToken(), 0);
                } else if (tvfilter.getVisibility() == View.GONE) {
                    tvfilter.setVisibility(View.VISIBLE);
                    tvfilter.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(tvfilter,
                            InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        tvfilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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


        if (dbvalue()/*&& intentfrom.equals("0")*/) {

            if (Activity.equalsIgnoreCase("AlrtListActivity")) {
                updatelist2();
            } else if (Activity.equalsIgnoreCase("SupporterList")) {
                updatelist_supEnq();
            }

        } else if (ut.isnet(AlrtsStnListAll.this) || intentfrom.equals("1")) {
            fetchdata();
        } else {
            try {
                ut.showD(AlrtsStnListAll.this, "nonet");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
				/*Intent i = new Intent(AlrtsStnListAll.this,
				AlrtListActivity.class);
		//i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);*/
        finish();
    }

    private void fetchdata() {
        // TODO Auto-generated method stub
        asynk_new = null;
        /*if (asynk_new == null) {*/
        ivRefresh.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);

        Log.e("async", "null");
        asynk_new = null;
        asynk_new = new DownloadnetWork_New();
        asynk_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		/*} else {
			if (asynk_new.getStatus() == AsyncTask.Status.RUNNING) {
				Log.e("async", "running");
				ivRefresh.setVisibility(View.GONE);
				mProgressBar.setVisibility(View.VISIBLE);
			}
		}*/

    }
	
	/*@Override
	public void onResume(){
	    super.onResume();
	    // put your code here...

	    async = new AlrtListURL();
		async.execute();
	}*/

    private void updatelist_supEnq() {
        searchResults.clear();
        String StnSupName = null;
        //DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = db.getWritableDatabase();
        int count = 0;
        Cursor c = sql
                .rawQuery(
                        "Select distinct InstallationDesc,InstalationId,AlertId from AlrtCountTable where cnt != '0' and NetworkCode='" + mType + "' and SubNetworkCode='" + Subnetwork + "' Order by CAST(AlertId AS INT)",
                        //+ " Order by NetworkCode",
                        null);
        if (c.getCount() > 0) {
            Log.e("MY c COUNT", String.valueOf(c.getCount()));
            c.moveToFirst();
            do {
                count++;
                String StationName = c.getString(c
                        .getColumnIndex("InstallationDesc"));
                String InstallationId = c.getString(c
                        .getColumnIndex("InstalationId"));

                String Alertid = c.getString(c.getColumnIndex("AlertId"));
                Log.e("Alrt ka id", Alertid);
                StateList sitem = new StateList();
                //
                /*CAST(AlertId AS INT)*/
                Cursor c1 = sql.rawQuery("Select * from AlrtListTable where InstallationId = " + InstallationId + " and ConfirmBy=''", null);
                if (c1.getCount() == 0) {
                    c1.close();
                } else {
                    Log.e("MY c1 COUNT", String.valueOf(c1.getCount()));
                    c1.moveToFirst();
                    int column = 0;
                    do {
                        String column1 = c1.getString(c1.getColumnIndex("AddedDt"));
                        String AlrtAddedByName = c1.getString(c1.getColumnIndex("AddedBy"));
                        String AlertDesc = c1.getString(c1.getColumnIndex("AlertDesc"));
                        StnSupName = c1.getString(c1.getColumnIndex("SupporterName"));
                        String AlertType = c1.getString(c1.getColumnIndex("AlertType"));
                        Log.e("Alrt ka time id", c1.getString(c1.getColumnIndex("AlertId")));
                        Log.e("Added Date", column1);
                        Log.e("AlrtAddedByName", AlrtAddedByName);
                        try {
                            //LmsconnectionStatusmainBean bean = new LmsconnectionStatusmainBean();
                            //String s = column1.substring(0,column1.indexOf("."));
                            //bean.setServerTime(s);
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date Startdate = dateFormat.parse(column1);
                            Date Enddate = cal.getTime();
                            long diff = Enddate.getTime() - Startdate.getTime();
                            long diffSeconds = diff / 1000 % 60;
                            long diffMinutes = diff / (60 * 1000) % 60;
                            long diffHours = diff / (60 * 60 * 1000) % 24;
                            long diffDays = diff / (24 * 60 * 60 * 1000);
                            if (!(Enddate.getTime() - Startdate.getTime() < 0)) {
							/*if (diffDays == 0 && diffHours == 0
									&& diffMinutes <= 30) {

							} else {*/

                                //String Stationamedcnn = c.getString(c.getColumnIndex("Depot"));
                                diffstr = "";
                                if (diffDays == 0 && diffHours == 0) {
                                    diffstr = diffMinutes + "Min";
                                } else if (diffDays == 0) {
                                    diffstr = diffHours + "hr";
                                } else {
                                    if (diffDays >= 32) {
                                        long yc = diffDays / 30;
                                        if (yc >= 12) {
                                            diffstr = (yc / 12) + " Year";
                                        } else {
                                            diffstr = yc + " Month";
                                        }
                                    } else {
                                        diffstr = diffDays + "days ";
                                    }


                                }
                                sitem.setDiff(diffstr);
                                sitem.setStatioName(StationName);
                                sitem.setInstallationId(InstallationId);
                                sitem.setStnSupName(StnSupName);
                                sitem.setAlrtAddedByName(AlrtAddedByName);
                                sitem.setAlertDesc(AlertDesc);
                                sitem.setAlertType(AlertType);
                                Log.e("StnSupName", StnSupName);
                                sitem.Setcount(count);
                                searchResults.add(sitem);
                            }

                        } catch (Exception ex) {
                            dff = new SimpleDateFormat("HH:mm:ss");
                            Ldate = dff.format(new Date());

                            StackTraceElement l = new Exception()
                                    .getStackTrace()[0];
                            System.out.println(l.getClassName() + "/"
                                    + l.getMethodName() + ":"
                                    + l.getLineNumber());

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
                    c1.close();

                }
            } while (c.moveToNext());

            StationAdaptor = new AlrtStnAdap(AlrtsStnListAll.this, searchResults);
            mListView.setAdapter(StationAdaptor);

        } else {
            showD("NoAlert");
        }
        //c.close();
		/*sql.close();
		db.close();*/

		/*if(searchResults.isEmpty()){
			showD("NoAlert");
		}else {
			StationAdaptor = new AlrtStnAdap(AlrtsStnListAll.this, searchResults);
			mListView.setAdapter(StationAdaptor);
		}*/

    }

    private void updatelist2() {
        searchResults.clear();
        String StnSupName = null;
        //DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = db.getWritableDatabase();
        String StationName = "", InstallationId = "", networkCode = "",alertId="";
        int count = 0;
        Cursor c = sql
                .rawQuery(
                        "Select distinct InstallationDesc,InstalationId,AlertId from" +
                                " AlrtCountTable where cnt != '0' and NetworkCode='" + mType + "' Order by CAST(AlertId AS INT)",
                        //+ " Order by NetworkCode",
                        null);

        /***************************************************************************************/

			/*	ArrayList<StateList> tempArrayList = new ArrayList<>();


				Cursor c1 = sql.rawQuery("Select * from AlrtListTable"+
								//+ " where InstallationId = '" + InstallationId + "'",
						" where NetworkCode = '" + mType + "'",
						null);
				if (c1.getCount() == 0) {
					c1.close();
				} else {
					Log.e("MY c1 COUNT", String.valueOf(c1.getCount()));
					c1.moveToFirst();
					int column = 0;
					do {
						String column1 = c1.getString(c1.getColumnIndex("AddedDt"));
						String AlrtAddedByName = c1.getString(c1.getColumnIndex("AddedBy"));
						String AlertDesc = c1.getString(c1.getColumnIndex("AlertDesc"));
						StnSupName = c1.getString(c1.getColumnIndex("SupporterName"));
						StationName = c1.getString(c1.getColumnIndex("StationName"));
						InstallationId = c1.getString(c1.getColumnIndex("InstallationId"));
						networkCode = c1.getString(c1.getColumnIndex("NetworkCode"));
						Log.e("Alrt ka time id", c1.getString(c1.getColumnIndex("AlertId")));
						Log.e("Added Date", column1);
						Log.e("AlrtAddedByName", AlrtAddedByName);
						Log.e("Network Code", networkCode);
						try {
							//LmsconnectionStatusmainBean bean = new LmsconnectionStatusmainBean();
							//String s = column1.substring(0,column1.indexOf("."));
							//bean.setServerTime(s);
							Calendar cal = Calendar.getInstance();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date Startdate = dateFormat.parse(column1);
							Date Enddate = cal.getTime();
							long diff = Enddate.getTime() - Startdate.getTime();
							long diffSeconds = diff / 1000 % 60;
							long diffMinutes = diff / (60 * 1000) % 60;
							long diffHours = diff / (60 * 60 * 1000) % 24;
							long diffDays = diff / (24 * 60 * 60 * 1000);
							if (!(Enddate.getTime() - Startdate.getTime() < 0)) {
								diffstr = "";
								if (diffDays == 0 && diffHours == 0) {
									diffstr = diffMinutes + "Min";
								} else if (diffDays == 0) {
									diffstr = diffHours + "hr";
								} else {
									if (diffDays >= 32) {
										long yc = diffDays / 30;
										if (yc >= 12) {
											diffstr = (yc / 12) + " Year";
										} else {
											diffstr = yc + " Month";
										}
									} else {
										diffstr = diffDays + "days ";
									}


								}
								StateList sitem = new StateList();
								sitem.setDiff(diffstr);
								sitem.setStatioName(StationName);
								sitem.setInstallationId(InstallationId);
								sitem.setStnSupName(StnSupName);
								sitem.setAlrtAddedByName(AlrtAddedByName);
								sitem.setAlertDesc(AlertDesc);
								Log.e("StnSupName", StnSupName);
								sitem.Setcount(count);
								searchResults.add(sitem);
								//tempArrayList.add(sitem);

							}

						} catch (Exception ex) {
							dff = new SimpleDateFormat("HH:mm:ss");
							Ldate = dff.format(new Date());

							StackTraceElement l = new Exception()
									.getStackTrace()[0];
							System.out.println(l.getClassName() + "/"
									+ l.getMethodName() + ":"
									+ l.getLineNumber());

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
					c1.close();
				}*/
        /********************************************************/


        if (c.getCount() > 0) {
            Log.e("MY c COUNT", String.valueOf(c.getCount()));
            c.moveToFirst();
            do {
                count++;
                StationName = c.getString(c.getColumnIndex("InstallationDesc"));
                InstallationId = c.getString(c.getColumnIndex("InstalationId"));
                String Alertid = c.getString(c.getColumnIndex("AlertId"));
                Log.e("Alrt ka id", Alertid);

                ArrayList<StateList> tempArrayList = new ArrayList<>();


                //'"+ClipNo+"'
                //CAST(AlertId AS INT)
                Cursor c1 = sql.rawQuery("Select DISTINCT * from AlrtListTable " +
                        "where InstallationId = '" + InstallationId + "' and ConfirmBy=''", null);
                if (c1.getCount() == 0) {
                    c1.close();
                } else {
                    Log.e("MY c1 COUNT", String.valueOf(c1.getCount()));
                    c1.moveToFirst();
                    int column = 0;
                    do {
                        Log.e("Data print", "Data Print");
                        String column1 = c1.getString(c1.getColumnIndex("AddedDt"));
                        String AlrtAddedByName = c1.getString(c1.getColumnIndex("AddedBy"));
                        String AlertDesc = c1.getString(c1.getColumnIndex("AlertDesc"));
                        StnSupName = c1.getString(c1.getColumnIndex("SupporterName"));
                        alertId = c1.getString(c1.getColumnIndex("AlertId"));
                        alertId = c1.getString(c1.getColumnIndex("AlertId"));
                        String AlertType = c1.getString(c1.getColumnIndex("AlertType"));
                        Log.e("Alrt ka time id", c1.getString(c1.getColumnIndex("AlertId")));
                        Log.e("Added Date", column1);
                        Log.e("Alert Description", AlertDesc);
                        Log.e("AlrtAddedByName", AlrtAddedByName);
                        try {
                            //LmsconnectionStatusmainBean bean = new LmsconnectionStatusmainBean();
                            //String s = column1.substring(0,column1.indexOf("."));
                            //bean.setServerTime(s);
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date Startdate = dateFormat.parse(column1);
                            Date Enddate = cal.getTime();
                            long diff = Enddate.getTime() - Startdate.getTime();
                            long diffSeconds = diff / 1000 % 60;
                            long diffMinutes = diff / (60 * 1000) % 60;
                            long diffHours = diff / (60 * 60 * 1000) % 24;
                            long diffDays = diff / (24 * 60 * 60 * 1000);
                            if (!(Enddate.getTime() - Startdate.getTime() < 0)) {
                                diffstr = "";
                                if (diffDays == 0 && diffHours == 0) {
                                    diffstr = diffMinutes + "Min";
                                } else if (diffDays == 0) {
                                    diffstr = diffHours + "hr";
                                } else {
                                    if (diffDays >= 32) {
                                        long yc = diffDays / 30;
                                        if (yc >= 12) {
                                            diffstr = (yc / 12) + " Year";
                                        } else {
                                            diffstr = yc + " Month";
                                        }
                                    } else {
                                        diffstr = diffDays + "days ";
                                    }


                                }
                                StateList sitem = new StateList();
                                sitem.setDiff(diffstr);
                                sitem.setStatioName(StationName);
                                sitem.setInstallationId(InstallationId);
                                sitem.setStnSupName(StnSupName);
                                sitem.setAlrtAddedByName(AlrtAddedByName);
                                sitem.setAlertDesc(AlertDesc);
                                sitem.setAlertId(alertId);
                                sitem.setAlertType(AlertType);
                                Log.e("Alert Description",AlertDesc);
                                sitem.Setcount(count);

                                Log.e("Station Details",sitem.toString());

                                if (searchResults.size() != 0) {
                                    int pos = -1;
                                    for (int i = 0; i < searchResults.size(); i++) {
                                        if (searchResults.get(i).getAlertDesc().equals(AlertDesc)
                                                && searchResults.get(i).getStatioName().equals(StationName)) {
                                            pos = i;
                                            break;
                                        }
                                    }

                                    if (pos == -1) {
                                        searchResults.add(sitem);
                                    }
                                } else {
                                    searchResults.add(sitem);
                                }


							/*	sitem.setDiff(diffstr);
								sitem.setStatioName(StationName);
								sitem.setInstallationId(InstallationId);
								sitem.setStnSupName(StnSupName);
								sitem.setAlrtAddedByName(AlrtAddedByName);
								sitem.setAlertDesc(AlertDesc);
								Log.e("StnSupName", StnSupName);
								sitem.Setcount(count);
								searchResults.add(sitem);*/

                            }

                        } catch (Exception ex) {
                            dff = new SimpleDateFormat("HH:mm:ss");
                            Ldate = dff.format(new Date());

                            StackTraceElement l = new Exception()
                                    .getStackTrace()[0];
                            System.out.println(l.getClassName() + "/"
                                    + l.getMethodName() + ":"
                                    + l.getLineNumber());

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
                    c1.close();

                }
            } while (c.moveToNext());

        }
        c.close();
        sql.close();
        db.close();
        StationAdaptor = new AlrtStnAdap(AlrtsStnListAll.this, searchResults);
        mListView.setAdapter(StationAdaptor);
    }

    private boolean dbvalue() {
        // TODO Auto-generated method stub
        try {
            //DatabaseHandler Db1 = new DatabaseHandler(AlrtsStnListAll.this);
            SQLiteDatabase sql = db.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select distinct InstallationDesc from AlrtCountTable", null);

            if (cursor != null && cursor.getCount() > 0) {

                cursor.close();
			/*sql.close();
			Db1.close();*/
                return true;

            } else {

                cursor.close();
			/*sql.close();
			Db1.close();*/
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

    public class DownloadnetWork_New extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String Url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/AlertCount?Mobile=" + mobno;

            Log.e("Alert Count", "url : " + Url);
            Url = Url.replaceAll(" ", "%20");
            try {
                resposmsg = ut.httpGet(Url);
                Log.e("RESPONSE", resposmsg);


                if (resposmsg.contains("error")) {
                    sop = "error";
                    //ut.showD(AlrtsStnListAll.this,"ServerError");
                } else if (resposmsg.contains("<InstalationId>")) {
                    sop = "valid";
                    //DatabaseHandler db = new DatabaseHandler(AlrtsStnListAll.this);
                    SQLiteDatabase sql = db.getWritableDatabase();
                    String columnName, columnValue;

                    //sql.execSQL("DROP TABLE IF EXISTS AlrtCountTable");
                    //sql.execSQL(ut.getAlrtCountTable());
                    sql.delete("AlrtCountTable", null, null);

                    Cursor cur1 = sql.rawQuery("SELECT * FROM AlrtCountTable", null);
                    cur1.getCount();
                    ContentValues values2 = new ContentValues();
                    NodeList nl2 = ut.getnode(resposmsg, "TableResult");

                    for (int i = 0; i < nl2.getLength(); i++) {
                        Element e = (Element) nl2.item(i);
                        for (int j = 0; j < cur1.getColumnCount(); j++) {
                            columnName = cur1.getColumnName(j);
                            columnValue = ut.getValue(e, columnName);

                            values2.put(columnName, columnValue);
                        }
                        sql.insert("AlrtCountTable", null, values2);
                    }
                    cur1.close();
				/*sql.close();
				db.close();*/

                } else {
                    sop = "invalid";
                }

            } catch (NullPointerException e) {
                sop = "error";
                resposmsg = "error";
                Log.e("RESPONSE", resposmsg);
            } catch (IOException e) {
                sop = "error";
                resposmsg = "error";
                Log.e("RESPONSE", resposmsg);
                e.printStackTrace();
            } catch (Exception e) {
                sop = "error";
                resposmsg = "error";
                Log.e("RESPONSE", resposmsg);
            }


            return sop;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(AlrtsStnListAll.this);
            progressDialog.setMessage("Processing...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            ivRefresh.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                //if(sop.equals("validupdatelist2")){
                if (sop.equals("valid")) {

                    async = new AlrtListURL();
                    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    //updateAlertCount();
                } else if (sop.equals("error")) {
                    try {
                        ut.showD(AlrtsStnListAll.this, "ServerError");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        ut.showD(AlrtsStnListAll.this, "invalid");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                ivRefresh.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                progressDialog.dismiss();

            } catch (Exception e) {
                e.printStackTrace();
                StackTraceElement l = new Exception().getStackTrace()[0];

                ut = new com.stavigilmonitoring.utility();
                if (!ut.checkErrLogFile()) {
                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()) {
                    ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":" + l.getLineNumber() + "  " + e.getMessage() + " ");
                }
            }
        }

    }

    public class AlrtListURL extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
            //DatabaseHandler db = new DatabaseHandler(AlrtsStnListAll.this);
            SQLiteDatabase sql = db.getWritableDatabase();
            String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/AlertGet?InstallationId="
                    + ""
                    + "&AddedBy="
                    + mobno;

            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");
            try {
                responsemsg = ut.httpGet(url);
                sql.delete("AlrtListTable", null, null);

                Log.e("csn status", "resmsg : " + responsemsg);

                if (responsemsg.contains("<AlertId>")) {
                    sop = "valid";
                    String columnName, columnValue;
                    /*
                     * DatabaseHandler db = new
                     * DatabaseHandler(AlrtsStnListAll.this); SQLiteDatabase sql =
                     * db.getWritableDatabase();
                     */

                    Cursor cur = sql.rawQuery("SELECT * FROM AlrtListTable",
                            null);
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
                        sql.insert("AlrtListTable", null, values1);
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

                    if (Activity.equalsIgnoreCase("AlrtListActivity")) {
                        updatelist2();
                    } else if (Activity.equalsIgnoreCase("SupporterList")) {
                        updatelist_supEnq();
                    }

                    //updatelist2();
                    //updatelist3();
                } else {
                    try {
                        ut.showD(AlrtsStnListAll.this, "nodata");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                ivRefresh.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                progressDialog.dismiss();

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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ivRefresh.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            progressDialog = new ProgressDialog(AlrtsStnListAll.this);
            progressDialog.setMessage("Processing...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    public void updatelist3(String inId, String alertId) {
        // TODO Auto-generated method stub
        //DatabaseHandler db = new DatabaseHandler(AlrtsStnListAll.this);
        SQLiteDatabase sql = db.getWritableDatabase();
		/*alertsItemBeanlist = new ArrayList<AlertsItemBean>();
		alertsItemBeanlist.clear();*/

        Cursor c = sql.rawQuery("Select * from AlrtListTable where InstallationId = " + inId + " and ConfirmBy='' order by CAST(AlertId AS INT) desc", null);
        if (c.getCount() == 0) {
            c.close();
			/*sql.close();
			db.close();*/
        } else {
            c.moveToFirst();
            int column = 0;
            do {
                String AlertId = c.getString(c.getColumnIndex("AlertId"));
                String AlertDesc = c.getString(c.getColumnIndex("AlertDesc"));
                String InstallationId = c.getString(c.getColumnIndex("InstallationId"));
                String StationName = c.getString(c.getColumnIndex("StationName"));
                String AddedBy = c.getString(c.getColumnIndex("AddedBy"));
                String AddedDt = c.getString(c.getColumnIndex("AddedDt"));
                String ConfirmBy = c.getString(c.getColumnIndex("ConfirmBy"));
                String ConfirmDT = c.getString(c.getColumnIndex("ConfirmDt"));
                String ResolveBy = c.getString(c.getColumnIndex("ResolveBy"));
                String ResolveDT = c.getString(c.getColumnIndex("ResolveDt"));
                String ModifiedBy = c.getString(c.getColumnIndex("ModifiedBy"));
                String ModifiedDT = c.getString(c.getColumnIndex("ModifiedDt"));
                String RejectedBy = c.getString(c.getColumnIndex("RejectedBy"));
                String RejectedDT = c.getString(c.getColumnIndex("RejectedDt"));
                String Mobile = c.getString(c.getColumnIndex("Mobile"));
                String SupporterName = c.getString(c.getColumnIndex("SupporterName"));
                String AlertType = c.getString(c.getColumnIndex("AlertType"));

                dataBundle.putString("Type", mType);
                dataBundle.putString("AlertId", AlertId);
                dataBundle.putString("AlertDesc", AlertDesc);
                dataBundle.putString("StationName", StationName);
                dataBundle.putString("CreatedBy", AddedBy);
                dataBundle.putString("CreatedDt", AddedDt);
                dataBundle.putString("InstallationId", InstallationId);
                dataBundle.putString("isResolved", ResolveBy);
                dataBundle.putString("AlertByMobNo", Mobile);
                dataBundle.putString("SupporterName", SupporterName);
                dataBundle.putString("AlertType", AlertType);
                if(alertId.equals(AlertId)) {
                    AlertDetails(dataBundle);
                }
            } while (c.moveToNext());
            c.close();
			/*sql.close();
			db.close();*/
        }

        //alertsItemListAdapter = new AlertsItemListAdapter(AlrtsStnListAll.this, alertsItemBeanlist);

        //invtlist.setAdapter(alertsItemListAdapter);

    }

    protected void AlertDetails(Bundle dataBundle2) {
        // TODO Auto-generated method stub
		/*Intent intent = new Intent(AlrtsStnListAll.this,StationInventoryAddEditItems.class);	
		
		startActivity(intent);	*/
        Intent intent = new Intent(AlrtsStnListAll.this, AlrtDetailsWithCommentsActivity.class);
        intent.putExtras(dataBundle2);
        startActivity(intent);
        //finish();
    }

    protected void showD(final String string) {
        // TODO Auto-generated method stub

        final Dialog myDialog = new Dialog(AlrtsStnListAll.this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
        myDialog.setCancelable(true);
        // myDialog.getWindow().setGravity(Gravity.BOTTOM);

        final TextView txt = (TextView) myDialog
                .findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);
        if (string.equals("Done")) {
            myDialog.setTitle(" ");
            txt.setText("Data Saved");
        } else if (string.equals("Error")) {
            myDialog.setTitle(" ");
            txt.setText("Server Error.. Please try after some time");
        } else if (string.equals("Issue")) {
            myDialog.setTitle(" ");
            txt.setText("Sorry you cannot complete this activity");
        } else if (string.equals("NoAlert")) {
            txt.setText("No pending alerts");
        }

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


}

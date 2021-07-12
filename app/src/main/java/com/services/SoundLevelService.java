package com.services;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.database.DBInterface;
import com.stavigilmonitoring.DatabaseHandler;
import com.stavigilmonitoring.R;
import com.beanclasses.StatelevelList;
import com.stavigilmonitoring.utility;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import static com.stavigilmonitoring.utility.OpenConnection;

public class SoundLevelService extends Service {
   String mobno;
	String urlStringToken="", urlStringToken2="";
	private static final int NOTIFICATION = 1337;
	String responsemsg = "k";
	DatabaseHandler db;
	SQLiteDatabase sql;

//	private NotificationManager mNotificationManager;
//	private int notificationID = 100;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
        dbi.Close();

		db = new DatabaseHandler(getBaseContext());
		sql = db.getWritableDatabase();

		Calendar cal = Calendar.getInstance();
		if (cal.HOUR <= 20 && cal.HOUR >= 6 && isnet())
			new UploadTS_new().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
		// uploadtimesheet=new UploadTS_new().execute();

		return START_STICKY;
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

	public class UploadTS_new extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String	responsemsg;
				
           String bb= "";
           utility ut = new utility();
		   String urls = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetStationCallibrationNew?Mobile="
					+ mobno + "&NetworkCode="+bb+"&InstallationId="+bb;
			 urls = urls.replaceAll(" ", "%20");

					System.out.println("============ internet reg url " + urls);

			/*DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();*/
			//sql = db.getWritableDatabase();
			//sql.beginTransaction();

					try {
						System.out.println("-------  activity url --- " + urls);
						responsemsg = ut.httpGet(urls);

						System.out.println("-------------  xx vale of non repeated-- "
								+ responsemsg);
						
						//DatabaseHandler db = new DatabaseHandler(getBaseContext());
						System.out.println("------------- 1-- ");
						//SQLiteDatabase sql = db.getWritableDatabase();
						System.out.println("------------- 2-- ");
						//sql.execSQL("DROP TABLE IF EXISTS SoundLevel_new");	//commented
						System.out.println("------------- 3-- ");
						//sql.execSQL(ut.getSoundLevel_new());	//commented
						sql.delete("SoundLevel_new",null,null);

						System.out.println("------------- 4-- ");
						System.out.println("------------- 5-- ");

						// InstalationId
						if (responsemsg.contains("<InstalationId>")) {
						//	sop = "valid";
							Cursor c = sql.rawQuery("SELECT * FROM SoundLevel_new",
									null);
							System.out.println("------------- 6-- ");
							ContentValues values = new ContentValues();
							System.out.println("------------- 7-- ");
							NodeList nl = ut.getnode(responsemsg, "Table");
							Log.e("get SoundLevel_new node", " fetch data : " + nl);
							String msg = "";
							String columnName, columnValue;
							for (int i = 0; i < nl.getLength(); i++) {
								Element e = (Element) nl.item(i);
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
											//SimpleDateFormat format = new SimpleDateFormat(
													//"MMM dd yyyy hh:mm");
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

											Log.e("getdetails", "sd : " + Startdate
													+ " ed: " + Enddate + " d: "
													+ diffDays + " h: " + diffHours
													+ " m:" + diffMinutes);

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
						//	sop = "invalid";
							System.out.println("--------- invalid for AD list --- ");
						}

						//db.close();
						//sql.setTransactionSuccessful();

					} catch (NullPointerException e) {
						e.printStackTrace();
						//db.close();
					} catch (IOException e) {
						e.printStackTrace();

						responsemsg = "wrong" + e.toString();
						System.out
								.println("--------- invalid for message type list --- "
										+ responsemsg);
						//db.close();

					}finally {
						//sql.endTransaction();
					}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				
				
				CharSequence title = "STA";
				int icon = R.drawable.sta_logo;
				long time = System.currentTimeMillis();
				Calendar cal = Calendar.getInstance();
				int hour = cal.get(Calendar.HOUR_OF_DAY);
				String hours = String.valueOf(hour);
				int s1 = 6;
				int s2 = 22;
			
				int totalSoundlevel = dbvalueSoundLevel();
				CharSequence text = "Sound Level : "+totalSoundlevel;
				String z = String.valueOf(totalSoundlevel);
				SharedPreferences prefsound = getApplicationContext()
						.getSharedPreferences("PrefSound", Context.MODE_PRIVATE);
				Editor editorsound = prefsound.edit();

				editorsound.putString("TVSound",
						String.valueOf(totalSoundlevel));
				editorsound.commit();
				Log.e("get details.....", "---kk add STn : " + totalSoundlevel);
				// soundcunt.setText(String.valueOf(totalstation));
           if (isnet()) {

					if ((hour > s1) && (hour < s2)) {

						try {
							//Common.TOKEN = registerGCM();
							//Common.UserName = GetUserName();

							urlStringToken = "http://punbus.vritti.co/api/Values/AddNotification?"// AdatSoftData.METHOD_SAVE_DATA
									+	"Message=" +text
									+ "&PkgName=com.stavigilmonitoring"
									+ "&FromMobile=" + mobno
									+ "&ToMobile=" + mobno;
							urlStringToken2 = "http://punbus.vritti.co/api/Values/SendNotification?"// AdatSoftData.METHOD_SAVE_DATA
									+	"sessionId=" + "vigil"// AdatSoftData.SESSION_ID
									+ "&handler=" + "0" ;//AdatSoftData.HANDLE

							//new NotificationCreateAPI().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlStringToken);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						System.out.println("Alarm stop...........");
					}
				} else {
					System.out.println("No internet working...........");
				}

			} catch (Exception e) {
				e.printStackTrace();
				SimpleDateFormat	dff = new SimpleDateFormat("HH:mm:ss");
				String Ldate = dff.format(new Date());

				StackTraceElement l = new Exception().getStackTrace()[0];
				System.out.println(l.getClassName() + "/" + l.getMethodName()
						+ ":" + l.getLineNumber());
				utility	ut = new utility();
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

		class NotificationCreateAPI extends AsyncTask<String, Void, String> {
			Object res;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			/*progressDialog = new ProgressDialog(ConfirmOTPActivity.this);
			progressDialog.setMessage("Processing...");
			progressDialog.show();*/
			}
			@Override
			protected String doInBackground(String... params) {

       /* responsemsg = "";
        inwid = "";
        inwtab = "";*/
				try {
					Log.e("URL",params[0]);
					res = OpenConnection(params[0]);
					responsemsg = res.toString();
					Log.e("URL res",responsemsg);
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
				//String table = "";
				if (result.contains("error")||result.contains("E")) {
					Toast.makeText(getBaseContext(), "error!", Toast.LENGTH_LONG).show();
				} else if (result.contains("Y")) {
					try{
						Log.e("URL",urlStringToken2);
						res = OpenConnection(urlStringToken2);
						responsemsg = res.toString();
						Log.e("URL resp",responsemsg);
					} catch (NullPointerException e) {
						responsemsg = "error";
						e.printStackTrace();
					} catch (Exception e) {
						responsemsg = "error";
						e.printStackTrace();
					}
					//Toast.makeText(getBaseContext(), "Token Added Successfully..", Toast.LENGTH_LONG).show();
				}
			}
		}

		private int dbvalueSoundLevel() {
			int totalstation = 0;
			DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			int count = 0;
			Cursor c = sql.rawQuery(
					"SELECT DISTINCT NetworkCode FROM SoundLevel_new ORDER BY NetworkCode", null);
			if (c.getCount() > 0) {
				totalstation = 0;
				c.moveToFirst();
				do {

					String Type = c.getString(0);

					Cursor c1 = sql.rawQuery(
							"Select distinct InstallationDesc from SoundLevel_new  Where NetworkCode='"
								+ Type + "' ORDER BY NetworkCode Desc", null);
					count = c1.getCount();
					Type = Type.replaceAll("0", "");
					Type = Type.replaceAll("1", "");
					if (!Type.trim().equalsIgnoreCase("")) {
						StatelevelList sitem = new StatelevelList();
						sitem.SetNetworkCode(Type);
						sitem.Setcount(count);
						// searchResults.add(sitem);
					}
					totalstation = totalstation + count;

				} while (c.moveToNext());

			}
			return totalstation;
		}
	}
}

package com.services;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.database.DBInterface;
import com.stavigilmonitoring.utility;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class GPSServiceVigil extends Service implements LocationListener {
	protected LocationManager locationManager;
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	Location location; // location
	double latitude; // latitude
	double longitude; // longitude
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
	private String mobno;
	static SimpleDateFormat dff;
	static String Ldate;
	utility ut;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		String hours = String.valueOf(hour);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		int s1 = 8;
		int s2 = 20;

		if ((hour > s1) && (hour < s2))
		// if((hour <s1) && (hour >s2) )
		{

			DBInterface dbi = new DBInterface(getApplicationContext());
			mobno = dbi.GetPhno();
			dbi.Close();

			try {
				locationManager = (LocationManager) getApplicationContext()
						.getSystemService(LOCATION_SERVICE);

				isGPSEnabled = locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER);

				isNetworkEnabled = locationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

				if (!isGPSEnabled && !isNetworkEnabled) {

				} else {
					this.canGetLocation = true;
					// if GPS Enabled get lat/long using GPS Services
					if (isGPSEnabled) {
						if (location == null) {
							if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
								// TODO: Consider calling
								//    ActivityCompat#requestPermissions
								// here to request the missing permissions, and then overriding
								//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
								//                                          int[] grantResults)
								// to handle the case where the user grants the permission. See the documentation
								// for ActivityCompat#requestPermissions for more details.
								return START_STICKY;
							}
							locationManager.requestLocationUpdates(
									LocationManager.GPS_PROVIDER,
									MIN_TIME_BW_UPDATES,
									MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

							Log.d("GPS Enabled...", " GPS Enabled.."
									+ locationManager);
							if (locationManager != null) {
								location = locationManager
										.getLastKnownLocation(LocationManager.GPS_PROVIDER);
								Log.d("GPS Enabled...", " location.."
										+ location);
								if (location == null) {
									// Log.d("GPS Enabled...", "location null");
								} else {
									latitude = location.getLatitude();
									longitude = location.getLongitude();

									Log.d("GPS Enabled...", "lat : " + latitude
											+ " lon : " + longitude);
									if (isnet())
										new GPSTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, latitude + "",
												longitude + "");
								}
							}
						}
					} else if (isNetworkEnabled) {
						if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
							// TODO: Consider calling
							//    ActivityCompat#requestPermissions
							// here to request the missing permissions, and then overriding
							//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
							//                                          int[] grantResults)
							// to handle the case where the user grants the permission. See the documentation
							// for ActivityCompat#requestPermissions for more details.
							return START_STICKY;
						}
						locationManager.requestLocationUpdates(
								LocationManager.NETWORK_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("Network", "Network");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
							if (location == null) {

							} else {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
								if (isnet())
									new GPSTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,latitude + "",
											longitude + "");
							}
						}
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
				Log.e("error", "-----------------------------------");
				Log.e("error", "" + e.getMessage());
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

		/*
		 * GPSTracker gps; gps = new GPSTracker(GPSServiceVigil.this); // check
		 * if GPS enabled if(gps.canGetLocation()){ double latitude =
		 * gps.getLatitude(); double longitude = gps.getLongitude();
		 * finalLattitude = String.valueOf(latitude);
		 * finalLongitude=String.valueOf(longitude);
		 * LocationName=gps.LocationName;
		 * 
		 * //LocationName=gps.getLocationName(latitude, longitude);
		 * 
		 * // \n is for new line //Toast.makeText(getApplicationContext(),
		 * "Your Location is - \nLat: " + finalLattitude + "\nLong: " +
		 * finalLongitude, Toast.LENGTH_LONG).show(); }else{
		 * System.out.println(".....GPSServiceVigil.........6>>>>>>>>>>>>>>>>");
		 * // can't get location // GPS or Network is not enabled // Ask user to
		 * enable GPS/network in settings gps.showSettingsAlert();
		 * System.out.println("......GPSServiceVigil........7>>>>>>>>>>>>>>>>");
		 * }
		 * 
		 * //uploadtimesheet = new UploadTS().execute(); new
		 * UploadTS().execute();
		 */
		return START_STICKY;
	}

	private boolean isnet() {
		// TODO Auto-generated method stub
		Context context = this.getApplicationContext();
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		int flag = 0;
		if (mWifi.isConnected()) {
			flag = 1;
			// Toast.makeText(getApplicationContext(), "wifi coenected",
			// Toast.LENGTH_LONG).show();

		} else {
			// Toast.makeText(getApplicationContext(), "wifi coenected",
			// Toast.LENGTH_LONG).show();

		}
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			flag = 1;
			return true;
		}

		return false;

	}

	public class GPSTask extends android.os.AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			String LocationName = "null", res;
			utility ut = new utility();
			Log.e("", "do in background--GPSTask-----");
			String address = String.format(Locale.ENGLISH,
					"http://maps.googleapis.com/maps/api/geocode/json?latlng="
							+ params[0] + "," + params[1] + "&sensor=true");
			HttpGet httpGet = new HttpGet(address);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			StringBuilder stringBuilder = new StringBuilder();
			Log.e("", "do in background--GPSTask----2-");
			List<Address> retList = null;

			try {
				response = client.execute(httpGet);
				Log.e("", "do in background-------3");
				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int b;
				while ((b = stream.read()) != -1) {
					stringBuilder.append((char) b);
				}
				Log.e("", "do in background--GPSTask----3-");
				JSONObject jsonObject = new JSONObject();
				jsonObject = new JSONObject(stringBuilder.toString());
				Log.e("", "do in background--GPSTask---4--");

				retList = new ArrayList<Address>();

				if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
					JSONArray results = jsonObject.getJSONArray("results");
					for (int i = 0; i < results.length(); i++) {
						JSONObject result = results.getJSONObject(i);
						String indiStr = result.getString("formatted_address");
						Address addr = new Address(Locale.ITALY);
						addr.setAddressLine(0, indiStr);
						retList.add(addr);
					}
					LocationName = retList.get(0).getAddressLine(0);

					String[] aLocation = LocationName.split(",");
					LocationName = aLocation[2] + "," + aLocation[3] + ","
							+ aLocation[4];
					String url = "http://vritti.co/imedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/InsertGPSData?"
							+ "MobileNo="
							+ mobno
							+ "&LocationName="
							+ LocationName
							+ "&Latitude="
							+ params[0]
							+ "&Longitude=" + params[1];
					url = url.replace(" ", "%20");
					res = ut.httpGet(url);
				}
			} catch (ClientProtocolException e) {
				Log.e("", "Error calling Google geocode webservice.", e);
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

				return "ERROR_IN_CODE";
			} catch (IOException e) {
				Log.e("", "Error calling Google geocode webservice.", e);
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

				return "ERROR_IN_CODE";
			} catch (JSONException e) {
				Log.e("", "Error parsing Google geocode webservice response.",
						e);
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

				return "ERROR_IN_CODE";
			} catch (NullPointerException e) {
				e.printStackTrace();
				// TODO: handle exception
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

			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
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

			return LocationName;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// Toast.makeText(getApplicationContext(), "Location : "+result,
			// Toast.LENGTH_LONG).show();
			stopSelf();
		}
	}

	/*
	 * public class UploadTS extends android.os.AsyncTask<String, Void, String>
	 * {
	 * 
	 * @Override protected String doInBackground(String... params) {
	 * 
	 * try {
	 * 
	 * 
	 * Log.e("GPS.........","Location Name : "+LocationName);
	 * System.out.println("..............1>>>>>>>>>>>>>>>>"); String
	 * eUrl=java.net
	 * .URLEncoder.encode("MobileNo="+mobno+"&LocationName="+LocationName
	 * +"&Latitude="+finalLattitude+"&Longitude="+finalLongitude);
	 * 
	 * String url=
	 * "http://vritti.co/imedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/InsertGPSData?"
	 * +eUrl;
	 * System.out.println("....GPSServiceVigil..........1>>GPS  >>>>>>>>>>>>>>"
	 * ); System.out.println(".............."+url); //url = url.replaceAll(" ",
	 * "%20");
	 * 
	 * 
	 * System.out.println("============ internet reg url " + url);
	 * 
	 * try { System.out.println("---GPSServiceVigil----  activity url --- " +
	 * url); responsemsg = ut.httpGet(url);
	 * 
	 * System.out.println("-------------  xx vale-- " + responsemsg); } catch
	 * (IOException e) { e.printStackTrace();
	 * 
	 * responsemsg = "wrong" + e.toString(); System.out
	 * .println("--------- invalid for message type list --- "+responsemsg);
	 * 
	 * }
	 * 
	 * //
	 * 
	 * System.out.println("................service started----------------");
	 * 
	 * } catch (Exception e) {
	 * 
	 * }
	 * 
	 * return null; }
	 * 
	 * @Override protected void onPostExecute(String result) { // TODO
	 * Auto-generated method stub super.onPostExecute(result);
	 * 
	 * 
	 * stopSelf();
	 * 
	 * }
	 * 
	 * }
	 */

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}

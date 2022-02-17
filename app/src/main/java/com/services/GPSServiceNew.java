package com.services;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;

import com.database.DBInterface;
import com.stavigilmonitoring.DatabaseHandler;
import com.stavigilmonitoring.utility;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

public class GPSServiceNew extends Service implements LocationListener {
	LocationManager locationManager;
	private utility ut = new utility();
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	Location location; // location
	public static double latitude; // latitude
	public static double longitude; // longitude
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 15; // in mili
	// sec
	public String mobno, LocationName, link;
	public MyLocationListener listener;
	private static final int UPDATE_MINUTES = 1000 * 60 * 10;
	Intent intent;
	Context mContext;
	public Location previousBestLocation = null;
	String res;

	@Override
	public IBinder onBind(Intent intent) {
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
		Date date = new Date();
		CharSequence time = DateFormat.format("EEEE", date.getTime());
		// if (!(time.equals("Sunday"))) {
		if (hour > s1 && hour < s2) {
			try {

				DBInterface dbi = new DBInterface(getApplicationContext());
				mobno = dbi.GetPhno();
				dbi.Close();
				locationManager = (LocationManager) getApplicationContext()
						.getSystemService(LOCATION_SERVICE);

				isGPSEnabled = locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER);

				isNetworkEnabled = locationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
				/*
				 * if (locationManager != null) { location =
				 * locationManager.getLastKnownLocation
				 * (LocationManager.NETWORK_PROVIDER); if (location != null) {
				 * onLocationChanged(location); } else if (locationManager !=
				 * null) location =
				 * locationManager.getLastKnownLocation(LocationManager
				 * .GPS_PROVIDER); if (location != null) {
				 * onLocationChanged(location); } }
				 */
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

							Log.d("GPS Enabled...", " GPS Enabled..");
							if (locationManager != null) {
								location = locationManager
										.getLastKnownLocation(LocationManager.GPS_PROVIDER);
								if (location != null) {

									latitude = location.getLatitude();
									longitude = location.getLongitude();

									Log.d("GPS Enabled...", "lat : " + latitude
											+ " lon : " + longitude);

									try {
										if (isNetworkEnabled) {
											Geocoder geocoder = new Geocoder(
													GPSServiceNew.this,
													Locale.getDefault());
											List<Address> addressList = geocoder
													.getFromLocation(latitude,
															longitude, 1);
											if (addressList != null
													&& addressList.size() > 0) {
												Address address = addressList
														.get(0);
												StringBuilder sb = new StringBuilder();
												for (int i = 0; i < address
														.getMaxAddressLineIndex(); i++) {
													sb.append(address
															.getAddressLine(i));
													/*
													 * sb.append(address.
													 * getAddressLine
													 * (i)).append("\n"); }
													 * sb.append
													 * (address.getLocality());
													 * //.append("\n"); //
													 * sb.append
													 * (address.getPostalCode
													 * ()).append("\n"); //
													 * sb.append
													 * (address.getCountryName
													 * ()); result =
													 * sb.toString();
													 */

												}
												LocationName = sb.toString();

											}

											/*
											 * Geocoder geocoder = new
											 * Geocoder(context,
											 * Locale.getDefault()); String
											 * result = null; try {
											 * List<Address> addressList =
											 * geocoder.getFromLocation(
											 * latitude, longitude, 1); if
											 * (addressList != null &&
											 * addressList.size() > 0) { Address
											 * address = addressList.get(0);
											 * StringBuilder sb = new
											 * StringBuilder(); for (int i = 0;
											 * i <
											 * address.getMaxAddressLineIndex();
											 * i++) {
											 * sb.append(address.getAddressLine
											 * (i)).append("\n"); }
											 * sb.append(address.getLocality());
											 * //.append("\n"); //
											 * sb.append(address
											 * .getPostalCode()).append("\n");
											 * //
											 * sb.append(address.getCountryName
											 * ()); result = sb.toString();
											 */

											//
										}
										// new GPSTask().execute();
									} catch (IOException e) {

										LocationName = "Location Not Found";
										Log.e("test",
												"Unable connect to Geocoder", e);
									}
									new GPSTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
							if (location != null) {

								latitude = location.getLatitude();
								longitude = location.getLongitude();
								// Toast.makeText(
								// getApplicationContext(),
								// "lat : " + latitude + " lon : "
								// + longitude, Toast.LENGTH_SHORT)
								// .show();
								try {
									Geocoder geocoder = new Geocoder(
											GPSServiceNew.this,
											Locale.getDefault());
									List<Address> addressList = geocoder
											.getFromLocation(latitude,
													longitude, 1);
									if (addressList != null
											&& addressList.size() > 0) {
										Address address = addressList.get(0);
										StringBuilder sb = new StringBuilder();
										for (int i = 0; i < address
												.getMaxAddressLineIndex(); i++) {
											sb.append(address.getAddressLine(i));

										}

										LocationName = sb.toString();

									}

									// new GPSTask().execute();
								} catch (IOException e) {
									LocationName = "Location Not Found";
									Log.e("test", "Unable connect to Geocoder",
											e);
								}

								new GPSTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							}
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				Log.e("error", "-----------------------------------");
				Log.e("error", "" + e.getMessage());
			}
		}

		return START_STICKY;
	}

	public class GPSTask extends android.os.AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {

			DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();

			try {
				// LocationName = LocationName.replace(" ", "%20");
				String url = link
						+ "/vwb/webservice/ActivityWebservice.asmx/SaveGpscordinates?"
						+ "MobileNo=" + mobno + "&Latitude=" + latitude
						+ "&Longitude=" + longitude + "&LocationName="
						+ LocationName;

				url = url.replace(" ", "%20");
				Log.e("GPS task url", " " + url);
				res = ut.httpGet(url);
				Log.d("GPS response", " " + res);

			} catch (ClientProtocolException e) {
				Log.e("", "Error calling Google geocode webservice.", e);
				LocationName = "No Location found";
			} catch (IOException e) {
				Log.e("", "Error calling Google geocode webservice.", e);
				LocationName = "No Location found";
			}

			return LocationName;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			stopSelf();
			/*
			 * if (LocationName.equalsIgnoreCase("No Location found")) { new
			 * GPSTask().execute(); } else { stopSelf(); }
			 */
		}
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

	protected boolean isBetterLocation(Location location,
									   Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > UPDATE_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -UPDATE_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	@Override
	public void onDestroy() {
		// handler.removeCallbacks(sendUpdatesToUI);
		super.onDestroy();
		// Log.v("STOP_SERVICE", "DONE");
		// locationManager.removeUpdates(listener);
	}

	public static Thread performOnBackgroundThread(final Runnable runnable) {
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					runnable.run();
				} finally {

				}
			}
		};
		t.start();
		return t;
	}

	public class MyLocationListener implements LocationListener {

		public void onLocationChanged(final Location loc) {
			Log.i("********************", "Location changed");

			latitude = loc.getLatitude();
			longitude = loc.getLongitude();
			if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			previousBestLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (isBetterLocation(loc, previousBestLocation)) {
				Toast.makeText(getApplicationContext(), "Location changed",
						Toast.LENGTH_SHORT).show();

				try {

					if (isNetworkEnabled) {
						Geocoder geocoder = new Geocoder(
								getApplicationContext(), Locale.getDefault());
						List<Address> addressList = geocoder.getFromLocation(
								latitude, longitude, 1);
						if (addressList != null && addressList.size() > 0) {
							Address address = addressList.get(0);
							StringBuilder sb = new StringBuilder();
							for (int i = 0; i < address
									.getMaxAddressLineIndex(); i++) {
								sb.append(address.getAddressLine(i));
							}

							LocationName = sb.toString();
							// Toast.makeText(getApplicationContext(),
							// "location : " + LocationName,
							// Toast.LENGTH_SHORT).show();
							// new GPSTask().execute();
						}
					}
				} catch (IOException e) {
					Log.e("test", "Unable connect to Geocoder", e);
					LocationName = "No Location found";
				}

				// if (NetworkUtilities
				// .isInternetAvailable(getApplicationContext())) {
				// new GPSTask().execute(latitude + "", longitude + "");
				// }
				intent.putExtra("Latitude", latitude);
				intent.putExtra("Longitude", longitude);
				intent.putExtra("Provider", loc.getProvider());
				sendBroadcast(intent);

			}
		}

		public void onProviderDisabled(String provider) {
			// Toast.makeText( getApplicationContext(), "Gps Disabled",
			// Toast.LENGTH_SHORT ).show();
		}

		public void onProviderEnabled(String provider) {
			// Toast.makeText( getApplicationContext(), "Gps Enabled",
			// Toast.LENGTH_SHORT).show();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	}
}

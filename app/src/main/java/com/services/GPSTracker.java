package com.services;

import com.stavigilmonitoring.WorkDoneFillDetail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GPSTracker extends Service implements LocationListener{
	
	 Context mcontext;
	 // flag for gps status
	 boolean isGPSEnabled = false;
	 // flag fornework status
	 
	 boolean isNetworkEnabled = false;
	
	 boolean canGetLocation = false;
	 
	 Location location;
	 
	 double Latitude;
	 double longitude;
	
	 private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	 
	 private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
	 protected LocationManager locationManager;
	 
	 public GPSTracker(Context Context) {
		// TODO Auto-generated constructor stub
		 super();
		 this.mcontext=Context;
		 getLocation();
	}

	public GPSTracker( ){
		super();
	}
	 
	 public Location getLocation() {
	        try {
	            locationManager = (LocationManager) mcontext
	                    .getSystemService(LOCATION_SERVICE);
	 
	            // getting GPS status
	            isGPSEnabled = locationManager
	                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
	 
	            // getting network status
	            isNetworkEnabled = locationManager
	                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	 
	            if (!isGPSEnabled && !isNetworkEnabled) {
	                // no network provider is enabled
	            	//showSettingsAlert();
	            	//showD("nonet");
	            } else {
	                this.canGetLocation = true;
	                // First get location from Network Provider
	                if (isNetworkEnabled) {
	                    locationManager.requestLocationUpdates(
	                            LocationManager.NETWORK_PROVIDER,
	                            MIN_TIME_BW_UPDATES,
	                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
	                    Log.d("Network", "Network");
	                    if (locationManager != null) {
	                        location = locationManager
	                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	                        if (location != null) {
	                            Latitude = location.getLatitude();
	                            longitude = location.getLongitude();
	                        }
	                    }
	                }
	                // if GPS Enabled get lat/long using GPS Services
	                if (isGPSEnabled) {
	                    if (location == null) {
	                        locationManager.requestLocationUpdates(
	                                LocationManager.GPS_PROVIDER,
	                                MIN_TIME_BW_UPDATES,
	                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
	                        Log.d("GPS Enabled", "GPS Enabled");
	                        if (locationManager != null) {
	                            location = locationManager
	                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                            if (location != null) {
	                                Latitude = location.getLatitude();
	                                longitude = location.getLongitude();
	                            }
	                        }
	                    }
	                }
	            }
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return location;
	    }

	 public double getLatitude(){
	        if(location != null){
	            Latitude = location.getLatitude();
	        }
	         
	        // return latitude
	        return Latitude;
	    }
	 
	 public double getLongitude(){
	        if(location != null){
	            longitude = location.getLongitude();
	        }
	         
	        // return longitude
	        return longitude;
	    }
	 
	 
	 /**
	     * Function to check GPS/wifi enabled
	     * @return boolean
	     * */
	    public boolean canGetLocation() {
	        return this.canGetLocation;
	    }
	    
	    /**
	     * Function to show settings alert dialog
	     * On pressing Settings button will lauch Settings Options
	     * */
	    public void showSettingsAlert(){
	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mcontext);
	      
	        // Setting Dialog Title
	        alertDialog.setTitle("GPS is settings");
	  
	        // Setting Dialog Message
	        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
	  
	        // On pressing Settings button
	        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog,int which) {
	                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                mcontext.startActivity(intent);
	              
	            }
	        });
	  
	        // on pressing cancel button
	        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            dialog.cancel();
	            
	            }
	        });
	  
	        // Showing Alert Message
	       alertDialog.show();
	    }
	    
	    
	   /* protected void showD(String string) {
			// TODO Auto-generated method stub

			final Dialog myDialog = new Dialog(GPSTracker.this);
			myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			myDialog.setContentView(R.layout.dialog_message_dialog);
			myDialog.setCancelable(true);
			// myDialog.getWindow().setGravity(Gravity.BOTTOM);

			TextView txt = (TextView) myDialog
					.findViewById(R.id.textview_message_dialog_message);
			if (string.equals("empty")) {
				myDialog.setTitle("Error...");
				txt.setText("Please Fill required data..");
			} else if (string.equals("nonet")) {
				myDialog.setTitle("Nonet");
				txt.setText("No Internet Connection Found.Please Activate internet Connectin on Device..");
			} else if (string.equals("invalid")) {
				myDialog.setTitle("Error...");
				txt.setText("Search contains more station names.... Please enter specified text to search in order to optimise search.");
			}

			Button btn = (Button) myDialog
					.findViewById(R.id.gotobtndialoginfosmall);
			btn.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub

					myDialog.dismiss();
					// finish();

				}
			});

			myDialog.show();

		}*/
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
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

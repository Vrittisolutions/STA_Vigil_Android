package com.stavigilmonitoring;

import com.receiver.AlarmManagerBroadcastReceiver;
import com.receiver.SoundLevelBrodcastReciver;
import com.stavigilmonitoring.R;
import com.stavigilmonitoring.SelectMenu;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	DatabaseHandler db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting);

		db=new DatabaseHandler(SettingsActivity.this);
		
		((EditText)findViewById(R.id.edtime)).setText(db.getSetting());
	}

	public void SaveClick(View v)
	{
		//DatabaseHandler db=new DatabaseHandler(SettingsActivity.this);
		String value=((EditText)findViewById(R.id.edtime)).getText().toString().trim();
	
		db.UpdateSetting(value);
		
		regservice();
		regservicesound();
		
		Toast.makeText(v.getContext(),"Your settings have been save successfully", Toast.LENGTH_LONG).show();

		Intent i=new Intent(SettingsActivity.this,SelectMenu.class);
		startActivity(i);
	}

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
	am.setRepeating(AlarmManager.RTC_WAKEUP,
			System.currentTimeMillis(), aTimenon, pendingIntent);

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
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				aTimenon, pendingIntent);

	}

	
/*	protected void regservicenon() {
	 DatabaseHandler db=new DatabaseHandler(SettingsActivity.this);	
			String val=db.getSetting();
			 int itime=Integer.parseInt(val);
			 
		long aTimenon = 1000 * 60 * itime;
		System.out.println("..........start");
		Intent myIntent = new Intent(this, AlarmManagerBroadcastReceiverNon.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 12345,
				myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), aTimenon, pendingIntent);

		//Toast.makeText(getBaseContext(), "started", Toast.LENGTH_LONG).show();

	}*/
}

package com.stavigilmonitoring;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.location.Address;

import com.stavigilmonitoring.utility;

public class MyTimerTask extends TimerTask {
	static SimpleDateFormat dff;
	static String Ldate;
	com.stavigilmonitoring.utility ut;

	@Override
	public void run() {
		// System.out.println("Timer task started at:"+csnService());
		// csnService();
		com.stavigilmonitoring.SelectMenu obj = new com.stavigilmonitoring.SelectMenu();
		obj.timerMethod3();
		obj.timerMethod2();
		// Intent igpsalarm = new Intent(null, com.services.SynchDtataCount);
		completeTask();
		// System.out.println("Timer task finished at:"+csnService());
	}

	private void completeTask() {
		try {
			// assuming it takes 20 secs to complete the task
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
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
	}
}

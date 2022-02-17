package com.database;

import android.content.Context;
import android.database.Cursor;

public class DBInterface {

	Context context;
	Database db;

	public DBInterface(Context c1) {
		context = c1;
		db = new Database(context);
		db.open();
	}

	public Cursor GetSetting() {
		return db.GetSetting();
	}

	public long SetSetting(String aVal[]) {
		return db.UpdateSetting(aVal);
	}

	public Cursor GetGcm() {
		return db.GetGcm();
	}

	public long SetGcm(String aVal[]) {
		return db.UpdateGcm(aVal);
	}

	public Cursor GetOld() {
		return db.Getold();
	}

	public long SetOld(String aVal[]) {
		return db.Updateold(aVal);
	}

	public Cursor GetTimeentry() {
		return db.Gettimeentry();
	}

	public long SetTimeEntry(String aVal[]) {
		return db.Updattimeentry(aVal);
	}

	public Cursor Getalarmtime() {
		return db.Getalarmtime();
	}

	public long Setalarm(String aVal[]) {
		return db.Updatealarmtime(aVal);
	}

	public Cursor Getalarmsound() {
		return db.Getalarmsound();
	}

	public long Setalarmsound(String aVal[]) {
		return db.Updatealarmsound(aVal);
	}

	public Cursor GetDateRefresh() {
		return db.GetDateRefresh();
	}

	public long SetDaterefresh(String aVal[]) {
		return db.UpdateDateRefresh(aVal);
	}

	public String Getalarmtimesoundname() {
		String IsReg = null;

		Cursor curs = db.Getalarmsound();
		if (curs.getCount() > 0) {
			curs.moveToFirst();
			IsReg = curs.getString(0).toString();
			System.out.println("----------phno db "
					+ curs.getString(0).toString());
		}

		curs.close();
		return IsReg;
		// return db.GetHang();
	}

	public String GetDateRefresg() {
		String IsReg = null;

		Cursor curs = db.GetDateRefresh();
		if (curs.getCount() > 0) {
			curs.moveToFirst();
			IsReg = curs.getString(0).toString();
			System.out.println("----------phno db "
					+ curs.getString(0).toString());
		}

		curs.close();
		return IsReg;

	}

	public String Getalarmtimevalue() {
		String IsReg = null;

		Cursor curs = db.Getalarmtime();
		if (curs.getCount() > 0) {
			curs.moveToFirst();
			IsReg = curs.getString(0).toString();
			System.out.println("----------phno db "
					+ curs.getString(0).toString());
		}

		curs.close();
		return IsReg;
		// return db.GetHang();
	}

	public String Getfornew() {
		String IsReg = null;

		Cursor curs = db.Getold();
		if (curs.getCount() > 0) {
			curs.moveToFirst();
			IsReg = curs.getString(0).toString();
			System.out.println("----------phno db "
					+ curs.getString(0).toString());
		}

		curs.close();
		return IsReg;
		// return db.GetHang();
	}

	public boolean Getfortimeflag(String Actid) {
		String Isflag = null;
		String Isactid = null;
		boolean b = false;
		Cursor curs = db.Gettimeentry();
		if (curs.getCount() > 0) {
			curs.moveToFirst();
			Isactid = curs.getString(1).toString();

			if (Isactid.equals(Actid)) {

				Isflag = curs.getString(0).toString();

				if (Isflag.equals("1")) {
					b = true;
				} else

				{
					b = false;
				}
			} else {
				b = false;
			}

			System.out.println("----------phno db "
					+ curs.getString(0).toString());
		}

		curs.close();
		return b;
		// return db.GetHang();
	}

	public boolean Gettnflag() {
		String IsReg = "2";

		Cursor curs = db.Gettimeentry();
		if (curs.getCount() > 0) {
			curs.moveToFirst();
			IsReg = curs.getString(0).toString();
			System.out.println("----------phno db "
					+ curs.getString(0).toString());
		}

		curs.close();
		if (IsReg.equals("1")) {
			return true;
		} else {
			return false;
		}

		// return db.GetHang();
	}

	public String Getactid() {
		String IsReg = null;

		Cursor curs = db.Gettimeentry();
		if (curs.getCount() > 0) {
			curs.moveToFirst();
			IsReg = curs.getString(1).toString();
			System.out.println("----------phno db "
					+ curs.getString(1).toString());
		}

		curs.close();
		return IsReg;
		// return db.GetHang();
	}

	public String Getstarttimetentry() {
		String IsReg = null;

		Cursor curs = db.Gettimeentry();
		if (curs.getCount() > 0) {
			curs.moveToFirst();
			IsReg = curs.getString(2).toString();
			System.out.println("----------phno db "
					+ curs.getString(2).toString());
		}

		curs.close();
		return IsReg;
		// return db.GetHang();
	}

	public String Getforusermaster() {
		String IsReg = null;

		Cursor curs = db.Getold();
		if (curs.getCount() > 0) {
			curs.moveToFirst();
			IsReg = curs.getString(1).toString();
			System.out.println("----------phno db "
					+ curs.getString(1).toString());
		}

		curs.close();
		return IsReg;
		// return db.GetHang();
	}

	public String Getregid() {
		String IsReg = null;

		Cursor curs = db.GetGcm();
		if (curs.getCount() > 0) {
			curs.moveToFirst();
			IsReg = curs.getString(0).toString();
			System.out.println("----------phno db "
					+ curs.getString(0).toString());
		}

		curs.close();
		return IsReg;
		// return db.GetHang();
	}

	public String Getisregserver() {
		String IsReg = null;

		Cursor curs = db.GetGcm();
		if (curs.getCount() > 0) {
			curs.moveToFirst();
			IsReg = curs.getString(2).toString();
			System.out.println("----------phno db "
					+ curs.getString(2).toString());
		}

		curs.close();
		return IsReg;
		// return db.GetHang();
	}

	public String GetPhno() {
		String IsReg = null;
		Cursor curs = db.GetSetting();
		if (curs.getCount() > 0) {
			curs.moveToFirst();
			IsReg = curs.getString(1);
			System.out.println("----------phno db "
					+ curs.getString(1));
		}
		curs.close();
		return IsReg;
		// return db.GetHang();
	}

	public String GetImsi() {
		String IsReg = null;

		Cursor curs = db.GetSetting();
		if (curs.getCount() > 0) {
			curs.moveToFirst();
			IsReg = curs.getString(0);
			System.out.println("----------imsi db "
					+ curs.getString(0));
		}

		curs.close();
		return IsReg;
		// return db.GetHang();
	}

//	public String GetUrl() {
//		String IsReg = null;
//
//		Cursor curs = db.GetSetting();
//		if (curs.getCount() > 0) {
//			curs.moveToFirst();
//			IsReg = curs.getString(2).toString();
//			System.out.println("----------url db "
//					+ curs.getString(2).toString());
//		}
//
//		curs.close();
//		return IsReg;
//	}

	public String GetRandomno() {
		String IsReg = null;

		Cursor curs = db.GetSetting();
		if (curs.getCount() > 0) {
			curs.moveToFirst();
			IsReg = curs.getString(3).toString();
			System.out.println("----------url db "
					+ curs.getString(3).toString());
		}

		curs.close();
		return IsReg;
	}

	public void Close() {
		//db.close();
	}
}

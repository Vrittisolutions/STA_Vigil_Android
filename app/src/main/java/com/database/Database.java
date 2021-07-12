package com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class Database {
	private DatabaseHelper dbHelper;
	private static SQLiteDatabase sqLiteDb;

	private Context HCtx = null;

	private static final String DATABASE_NAME = "VWB_STA";
	private static final int DATABASE_VERSION = 4;
	static final String Setting_table = "tbl_setting";// //
	static final String Gcm_table = "tbl_gcm";
	static final String OldData_table = "tbl_old";
	static final String TimeEntry_table = "tbl_timeentry";
	static final String Alarm_table = "tbl_alarm";
	static final String Alarmsound_table = "tbl_alarmsound";
	static final String Daterefresh_table = "tbl_daterefresh";

    public static final String TABLE_Notification = "NotificationTable";

	String[] aSetting = { "Imsi", "Phno", "Url", "RandomNo" };
	String[] aGcm = { "Regid", "Appid", "Isregserver" };
	String[] aOlddata = { "FornewAct", "ForUMaster" };

	String[] aTimeentry = { "Flag", "ActivityId", "StartTime" };
	String[] aAlarm = { "Alarmtime" };
	String[] aAlarmsound = { "Alarmsound" };
	String[] aDate = { "DateRefresh" };

	private static final String TABLE_SETTING_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ Setting_table
			+ " (Imsi text , Phno text , Url text , RandomNo text);";

	private static final String TABLE_GCM_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ Gcm_table + " (Regid text , Appid text , Isregserver text);";

	private static final String TABLE_OLD_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ OldData_table + " (FornewAct text , ForUMaster text);";

	private static final String TABLE_TimeEntry_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TimeEntry_table
			+ " (Flag text , ActivityId text , StartTime text);";

	private static final String TABLE_Alarm_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ Alarm_table + " (Alarmtime text);";

	private static final String TABLE_Alarmsound_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ Alarmsound_table + " (Alarmsound text);";

	private static final String TABLE_DATEREFRESH_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ Daterefresh_table + " (DateRefresh text);";

	public long SetSetting(String aVal[]) {

		ContentValues vals = new ContentValues();
		for (int i = 0; i < aVal.length; i++)
			vals.put(aSetting[i], aVal[i]);

		return sqLiteDb.insert(Setting_table, null, vals);
	}

	public long UpdateSetting(String aVal[]) {

		ContentValues vals = new ContentValues();
		for (int i = 0; i < aVal.length; i++)
			vals.put(aSetting[i], aVal[i]);

		return sqLiteDb.update(Setting_table, vals, null, null);
	}

	public Cursor GetSetting() {

		try {
			return sqLiteDb.query(Setting_table, aSetting, null, null, null,
					null, null);

		} catch (Exception e) {
			Log.e("Error in fetching Date", e.getMessage());
			return null;
		}
	}

	public long SetGcm(String aVal[]) {

		ContentValues vals = new ContentValues();
		for (int i = 0; i < aVal.length; i++)
			vals.put(aGcm[i], aVal[i]);

		return sqLiteDb.insert(Gcm_table, null, vals);
	}

	public long UpdateGcm(String aVal[]) {

		ContentValues vals = new ContentValues();
		for (int i = 0; i < aVal.length; i++)
			vals.put(aGcm[i], aVal[i]);

		return sqLiteDb.update(Gcm_table, vals, null, null);
	}
	 
	public Cursor GetGcm() {

		try {
			return sqLiteDb
					.query(Gcm_table, aGcm, null, null, null, null, null);

		} catch (Exception e) {
			Log.e("Error in fetching Date", e.getMessage());
			return null;
		}
	}

	public long Setold(String aVal[]) {

		ContentValues vals = new ContentValues();
		for (int i = 0; i < aVal.length; i++)
			vals.put(aOlddata[i], aVal[i]);

		return sqLiteDb.insert(OldData_table, null, vals);
	}

	public long Updateold(String aVal[]) {

		ContentValues vals = new ContentValues();
		for (int i = 0; i < aVal.length; i++)
			vals.put(aOlddata[i], aVal[i]);

		return sqLiteDb.update(OldData_table, vals, null, null);
	}

	public Cursor Getold() {

		try {
			return sqLiteDb.query(OldData_table, aOlddata, null, null, null,
					null, null);

		} catch (Exception e) {
			Log.e("Error in fetching Date", e.getMessage());
			return null;
		}
	}

	public long Settimeentry(String aVal[]) {

		ContentValues vals = new ContentValues();
		for (int i = 0; i < aVal.length; i++)
			vals.put(aTimeentry[i], aVal[i]);

		return sqLiteDb.insert(TimeEntry_table, null, vals);
	}

	public long Updattimeentry(String aVal[]) {

		ContentValues vals = new ContentValues();
		for (int i = 0; i < aVal.length; i++)
			vals.put(aTimeentry[i], aVal[i]);

		return sqLiteDb.update(TimeEntry_table, vals, null, null);
	}

	public Cursor Gettimeentry() {

		try {
			return sqLiteDb.query(TimeEntry_table, aTimeentry, null, null,
					null, null, null);

		} catch (Exception e) {
			Log.e("Error in fetching Date", e.getMessage());
			return null;
		}
	}

	public long Setalarmtime(String aVal[]) {

		ContentValues vals = new ContentValues();
		for (int i = 0; i < aVal.length; i++)
			vals.put(aAlarm[i], aVal[i]);

		return sqLiteDb.insert(Alarm_table, null, vals);
	}

	public long Updatealarmtime(String aVal[]) {

		ContentValues vals = new ContentValues();
		for (int i = 0; i < aVal.length; i++)
			vals.put(aAlarm[i], aVal[i]);

		return sqLiteDb.update(Alarm_table, vals, null, null);
	}

	public Cursor Getalarmtime() {

		try {
			return sqLiteDb.query(Alarm_table, aAlarm, null, null, null, null,
					null);

		} catch (Exception e) {
			Log.e("Error in fetching Date", e.getMessage());
			return null;
		}
	}

	public long SetDateRefresh(String aVal[]) {

		ContentValues vals = new ContentValues();
		for (int i = 0; i < aVal.length; i++)
			vals.put(aDate[i], aVal[i]);

		return sqLiteDb.insert(Daterefresh_table, null, vals);
	}

	public long UpdateDateRefresh(String aVal[]) {

		ContentValues vals = new ContentValues();
		for (int i = 0; i < aVal.length; i++)
			vals.put(aDate[i], aVal[i]);

		return sqLiteDb.update(Daterefresh_table, vals, null, null);
	}

	public Cursor GetDateRefresh() {

		try {
			return sqLiteDb.query(Daterefresh_table, aDate, null, null, null,
					null, null);

		} catch (Exception e) {
			Log.e("Error in fetching Date", e.getMessage());
			return null;
		}
	}

	public long Setalarmsound(String aVal[]) {

		ContentValues vals = new ContentValues();
		for (int i = 0; i < aVal.length; i++)
			vals.put(aAlarmsound[i], aVal[i]);

		return sqLiteDb.insert(Alarmsound_table, null, vals);
	}

	public long Updatealarmsound(String aVal[]) {

		ContentValues vals = new ContentValues();
		for (int i = 0; i < aVal.length; i++)
			vals.put(aAlarmsound[i], aVal[i]);

		return sqLiteDb.update(Alarmsound_table, vals, null, null);
	}

	public Cursor Getalarmsound() {

		try {
			return sqLiteDb.query(Alarmsound_table, aAlarmsound, null, null,
					null, null, null);

		} catch (Exception e) {
			Log.e("Error in fetching Date", e.getMessage());
			return null;
		}
	}

	public boolean ISDbCreated() {

		Cursor cur_hang = GetSetting();
		int i = cur_hang.getCount();
		cur_hang.close();

		if (i == 0)
			return false;
		else
			return true;

	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context ctx) {
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(TABLE_SETTING_CREATE);
				db.execSQL(TABLE_GCM_CREATE);
				db.execSQL(TABLE_OLD_CREATE);
				db.execSQL(TABLE_TimeEntry_CREATE);
				db.execSQL(TABLE_Alarm_CREATE);
				db.execSQL(TABLE_Alarmsound_CREATE);
				db.execSQL(TABLE_DATEREFRESH_CREATE);
				
				 
			} catch (Exception e) {
				System.out.println("Error in creating table " + e.toString());
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			db.execSQL("DROP TABLE IF EXISTS " + Setting_table);
			db.execSQL("DROP TABLE IF EXISTS " + Gcm_table);
			db.execSQL("DROP TABLE IF EXISTS " + OldData_table);
			db.execSQL("DROP TABLE IF EXISTS " + TimeEntry_table);
			db.execSQL("DROP TABLE IF EXISTS " + Alarm_table);
			
			db.execSQL("DROP TABLE IF EXISTS " + Alarmsound_table);
			db.execSQL("DROP TABLE IF EXISTS " + Daterefresh_table);
			onCreate(db);

		}
	}

	/** Constructor */
	public Database(Context ctx) {
		HCtx = ctx;
	}

	public Database open() throws SQLException {
		dbHelper = new DatabaseHelper(HCtx);
		sqLiteDb = dbHelper.getWritableDatabase();
		return this;
	}

	public void clean() {

		sqLiteDb.delete(Setting_table, null, null);
		sqLiteDb.delete(Gcm_table, null, null);
		sqLiteDb.delete(OldData_table, null, null);
		sqLiteDb.delete(TimeEntry_table, null, null);
		sqLiteDb.delete(Alarm_table, null, null);
		sqLiteDb.delete(Alarmsound_table, null, null);
		sqLiteDb.delete(Daterefresh_table, null, null);

	}

	public void cleanTable(int tableNo) {

		sqLiteDb.delete(Setting_table, null, null);
		sqLiteDb.delete(Gcm_table, null, null);
		sqLiteDb.delete(OldData_table, null, null);
		sqLiteDb.delete(TimeEntry_table, null, null);
		sqLiteDb.delete(Alarm_table, null, null);
		sqLiteDb.delete(Alarmsound_table, null, null);
		sqLiteDb.delete(Daterefresh_table, null, null);
	}

	public boolean deleteAlert(String DATABASE_TABLE, int tableNo,
			String whereCause) {

		return sqLiteDb.delete(DATABASE_TABLE, whereCause, null) > 0;

	}

	public void close() {
		dbHelper.close();
	}

}

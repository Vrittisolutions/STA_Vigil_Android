package com.stavigilmonitoring;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.beanclasses.Contact;
import com.beanclasses.SoundLevelBeanSort;

public class DatabaseHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 39;

	// Database Name
	private static final String DATABASE_NAME = "vWB";

	// Contacts table name
	private static final String TABLE_CONTACTS = "config";
	private static final String TABLE_STA_VISIT_FORM = "sta_visit_form";
	private static final String TABLE_NOTIFICATIONS = "TableNotifications";
	private static final String TABLE_ADVDETAILS = "AdvAudioClipDtls";
	private static final String TABLE_TEAMLEADERS = "TeamLeaders";

	// Contacts Table Columns names
	private static final String KEY_SERIALNUMBER = "serialnumber";
	private static final String KEY_PH_NO = "phone_number";
	private static final String KEY_NUMBER = "number";
	private static final String KEY_URL = "url";
	private static final String KEY_ENVIRONMENT = "environment";
	private static final String KEY_PLANT = "plant";

	public DatabaseHandler(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static final String TABLE_GPSRECORDS = "GPSrecords";
	String CREATE_GPSRECORDS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_GPSRECORDS
			+ "(GPSID TEXT,MobileNo TEXT,latitude TEXT,longitude TEXT,"
			+ "locationName TEXT,AddedDt TEXT,num TEXT)";

	public static final String TABLE_ADD_GPSRECORDS = "Add_GPSrecords";//(Local records)
	String CREATE_GPSRECORDS_ADD_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_ADD_GPSRECORDS
			+ "(GPSID INTEGER PRIMARY KEY AUTOINCREMENT,MobileNo TEXT,latitude TEXT,longitude TEXT,"
			+ "locationName TEXT,GpsAddedDt TEXT,UserMasterID TEXT,isUploaded TEXT)";

	public static final String TABLE_GPS_SEND_NOTIFICATION = "Gpsnotification";

	String CREATE_TABLE_GPS_SEND_NOTIFICATION = "CREATE TABLE IF NOT EXISTS " + TABLE_GPS_SEND_NOTIFICATION + "(GId INTEGER PRIMARY KEY AUTOINCREMENT," +
			"        ToUsermatserID TEXT," +
			"        FromUserMasterID TEXT," +
			"        Date TEXT," +
			"        MSG TEXT,isUploaded TEXT)";

	public static final String TABLE_LEAVE_REPORTING_TO = "Leave_Reportinh_To";


	String CREATE_LEAVE_REPORTING_TO_TABLE = "CREATE TABLE " + TABLE_LEAVE_REPORTING_TO + " (UserLoginId TEXT,UserName TEXT )";
    public static final String CREATE_TABLE_STA_VISIT_FORM = "CREATE TABLE "
            + TABLE_STA_VISIT_FORM
            + "(PKQuesId TEXT, QuesText TEXT, ResponseType TEXT, SelectionText TEXT,ValueMin TEXT,ValueMax TEXT, QuesCode TEXT)";

	public static final String CREATE_TABLE_NOTIFICATION =
			"CREATE TABLE  "+TABLE_NOTIFICATIONS+ "(NotifyID TEXT, InstallationId TEXT, StationName TEXT, " +
			"AddedDt TEXT, Message TEXT, MsgType TEXT, MsgVal TEXT, MsgText TEXT)";

	public static final String CREATE_TABLE_ADVDETAILS = "CREATE TABLE "+TABLE_ADVDETAILS+"(NetworkCode TEXT,InstalationId TEXT," +
			"InstalationName TEXT,AdvertisementCode TEXT,AdvertisementDesc TEXT,URL_clipPath TEXT,EffectiveDateTo TEXT,EffectiveDatefrom TEXT)";

	public static final String CREATE_TABLE_TEAMLEADERS ="CREATE TABLE "+TABLE_TEAMLEADERS+"(username TEXT,usermasterid TEXT)";

	@Override
	public void onCreate(SQLiteDatabase db) {
		com.stavigilmonitoring.utility ut = new utility();

        db.execSQL(CREATE_TABLE_STA_VISIT_FORM);
        db.execSQL(CREATE_TABLE_NOTIFICATION);
        db.execSQL(CREATE_TABLE_ADVDETAILS);
        db.execSQL(CREATE_TABLE_TEAMLEADERS);
		db.execSQL(ut.getSoundLevel_new());
		db.execSQL(ut.getDmCertificateTable()); 	//newly added
		db.execSQL(ut.getAllStation());
		db.execSQL(ut.getConnectionStatusUser());
		db.execSQL(ut.getNonrepeatedAd());
		db.execSQL(ut.getConnectionStatusUser1());
		db.execSQL(ut.getPendingClips());
		db.execSQL(ut.getTvStatus());
		db.execSQL(ut.getAlrtCountTable());
		db.execSQL(ut.getAlrtListTable());
		db.execSQL(ut.getConnectionStatusFilter());
		db.execSQL(ut.getConnectionStatusFiltermob());
		db.execSQL(ut.Databg());
		db.execSQL(ut.getDMCUsersTable());
		db.execSQL(ut.getDownTimeRasonFill());
		db.execSQL(ut.getDowntime());
		db.execSQL(ut.getDownTimeRason());
		db.execSQL(ut.getLmsConnectionStatus());
		db.execSQL(ut.getDeliveredRequests());
		db.execSQL(ut.getDispatchedbutnotReceived());
		db.execSQL(ut.getMaterialReqList());
		db.execSQL(ut.getMaterialReason());
		db.execSQL(ut.getReceivedConfirmation());
		db.execSQL(ut.getRejectedRequests());
		db.execSQL(ut.getMyorders());
		db.execSQL(ut.getPendingRequests());
		db.execSQL(ut.getSuspected());
		db.execSQL(ut.getSoundLevel());
		db.execSQL(ut.getSuspectedHistory());
		db.execSQL(ut.getUpdateCount());
		db.execSQL(ut.getPassword());
		db.execSQL(ut.getSoundLevelCalibrationStandard());
		db.execSQL(ut.getLastThreeADV());
		db.execSQL(ut.getLastAnn());
		db.execSQL(ut.getStnExtTable());
		db.execSQL(ut.getpeticularconnection());
		db.execSQL(ut.getAdvDetails());
		db.execSQL(ut.getBusReporting());
		db.execSQL(ut.getReqVsFilldMat());
		db.execSQL(ut.getMaterialHistory());
		db.execSQL(ut.getSoundLevel_StnEnq());
		db.execSQL(ut.getWorkTypeHistory());
		db.execSQL(ut.getStationInventory());
		db.execSQL(ut.GetUserList());
		db.execSQL(ut.getWorkDoneAndMaterialSupporter());
		db.execSQL(ut.getTVStatusReason());
		db.execSQL(ut.getActivityAllocationMaster());
		db.execSQL(ut.getWorkType());
		db.execSQL(ut.getTVReason());
		db.execSQL(ut.getUserNameTable());
		db.execSQL(ut.getUserpassEligibleTable());
		db.execSQL(ut.getUserLoginTable());
		db.execSQL(ut.getCommunicationTable());
		db.execSQL(ut.getWorkDone());
		db.execSQL(ut.getGPSrecords());
		db.execSQL(ut.getNotificationTable());
		db.execSQL(ut.getWorkAssignSupporter());
		db.execSQL(ut.getWorkAssignList());
		//db.execSQL(ut.getNotification_TableNew());

		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
				+ KEY_SERIALNUMBER + " TEXT," + KEY_PH_NO + " TEXT,"
				+ KEY_NUMBER + " TEXT," + KEY_URL + " TEXT," + KEY_ENVIRONMENT
				+ " TEXT," + KEY_PLANT + " TEXT" + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);

		String CREATE_CUST_TABLE = "CREATE TABLE " + "Pwd"
				+ "(Password TEXT, Count TEXT)";
		db.execSQL(CREATE_CUST_TABLE);
		String ClaimHeaderMaster = "CREATE TABLE "
				+ "ClaimHeaderMaster"
				+ "(ClaimHeaderId  TEXT, ClaimCode TEXT ,EMPID TEXT, PlantID TEXT ,AuthorizedById  TEXT, Purpose TEXT ,Date TEXT, TotalAmt TEXT ,AdvanceTaken  TEXT, Balance TEXT ,PaidAmount TEXT, Remark TEXT ,IsDeleted  TEXT, AddedBy TEXT , AddedDt TEXT ,ModifiedBy  TEXT, ModifiedDt TEXT ,CreationLevel TEXT, UserLevel TEXT ,SyncInfo  TEXT, Approved TEXT , ActivityId TEXT ,Status TEXT, ProjectId TEXT ,ClaimReimburse  TEXT, CostCtrMasterId TEXT)";
		db.execSQL(ClaimHeaderMaster);

		String ClaimDetailMaster = "CREATE TABLE "
				+ "ClaimDetailMaster"
				+ "(ClaimDetailId  TEXT, ClaimHeaderId TEXT ,Mode TEXT, Dt TEXT ,FromLocation  TEXT, ToLocation TEXT ,Exp1 TEXT, Exp2 TEXT ,Exp3  TEXT, Exp4 TEXT ,Exp5 TEXT, Exp6 TEXT ,Exp7  TEXT, Exp8 TEXT , Exp9 TEXT ,Exp10  TEXT, Total TEXT ,IsDeleted TEXT, AddedBy TEXT ,AddedDt  TEXT, ModifiedBy TEXT , ModifiedDt TEXT ,CreationLevel TEXT, UserLevel TEXT ,SyncInfo  TEXT, MainGroupId TEXT , SubGroupId TEXT ,Remark TEXT, ProjectId TEXT)";
		db.execSQL(ClaimDetailMaster);

		String ManageLeaveMaster = "CREATE TABLE "
				+ "ManageLeaveMaster"
				+ "(MLId  TEXT, UserMasterId TEXT ,ApprovedDt TEXT, StartDt TEXT ,EndDt  TEXT, Status TEXT ,ApprovedBy TEXT, Reason TEXT ,Address  TEXT, Contact TEXT ,LeaveType TEXT, IsDeleted TEXT ,AddedBy  TEXT, AddedDt TEXT , ModifiedBy TEXT ,CreationLevel  TEXT, UserLevel TEXT ,SyncInfo TEXT, ModifiedDt TEXT ,HalfLeaveOption  TEXT, LeaveSource TEXT , HalfLeaveOptionTo TEXT ,LeaveCount TEXT, CancelStatus TEXT ,LeaveMethod  TEXT)";
		db.execSQL(ManageLeaveMaster);
		db.execSQL("Create table Settings ( SettingName TEXT,SettingValue TEXT)");

		db.execSQL("Create table Reporting ( ReportingId TEXT , ReportingName TEXT)"); //, email TEXT

		db.execSQL(CREATE_GPSRECORDS_TABLE);
		db.execSQL(CREATE_GPSRECORDS_ADD_TABLE);
		db.execSQL(CREATE_TABLE_GPS_SEND_NOTIFICATION);//TABLE_GPS_SEND_NOTIFICATION
		db.execSQL(CREATE_LEAVE_REPORTING_TO_TABLE);//TABLE_LEAVE_REPORTING_TO

		ContentValues values = new ContentValues();
		values.put("SettingName", "rTime");
		values.put("SettingValue", "15");
		db.insert("Settings", null, values);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_STA_VISIT_FORM);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_ADVDETAILS);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_TEAMLEADERS);
		db.execSQL(" DROP TABLE IF EXISTS Pwd");
		db.execSQL(" DROP TABLE IF EXISTS ClaimHeaderMaster");
		db.execSQL(" DROP TABLE IF EXISTS ClaimDetailMaster");
		db.execSQL(" DROP TABLE IF EXISTS ManageLeaveMaster");
		db.execSQL(" DROP TABLE IF EXISTS Settings");
		db.execSQL(" DROP TABLE IF EXISTS SoundLevel_new");
		db.execSQL(" DROP TABLE IF EXISTS DmCertificateTable"); 	//newly added
		db.execSQL(" DROP TABLE IF EXISTS ConnectionStatusUser");
		db.execSQL(" DROP TABLE IF EXISTS AllStation");
		db.execSQL(" DROP TABLE IF EXISTS SoundLevel");
		db.execSQL(" DROP TABLE IF EXISTS ConnectionStatusUser1");
		db.execSQL(" DROP TABLE IF EXISTS AlrtCountTable");
		db.execSQL(" DROP TABLE IF EXISTS AlrtListTable");
		db.execSQL(" DROP TABLE IF EXISTS ConnectionStatusFilter");
		db.execSQL(" DROP TABLE IF EXISTS ConnectionStatusFiltermob");
		db.execSQL(" DROP TABLE IF EXISTS Backgroundplaylist");
		db.execSQL(" DROP TABLE IF EXISTS DMCUsersTable");
		db.execSQL(" DROP TABLE IF EXISTS LmsConnectionStatus");
		db.execSQL(" DROP TABLE IF EXISTS DispatchedbutnotReceived");
		db.execSQL(" DROP TABLE IF EXISTS ReceivedConfirmation");
		db.execSQL(" DROP TABLE IF EXISTS RejectedRequests");
		db.execSQL(" DROP TABLE IF EXISTS Myorders");
		db.execSQL(" DROP TABLE IF EXISTS Suspected");
		db.execSQL(" DROP TABLE IF EXISTS CalibrationStandard");
		db.execSQL(" DROP TABLE IF EXISTS LastthreeAdv");
		db.execSQL(" DROP TABLE IF EXISTS LastthreeAnn");
		db.execSQL(" DROP TABLE IF EXISTS StnExtTable");
		db.execSQL(" DROP TABLE IF EXISTS PerticularConnection");
		db.execSQL(" DROP TABLE IF EXISTS AdvDetailsTable");
		db.execSQL(" DROP TABLE IF EXISTS BusReporting");
		db.execSQL(" DROP TABLE IF EXISTS reqVsFilledMaterial");
		db.execSQL(" DROP TABLE IF EXISTS MaterialHistory");
		db.execSQL(" DROP TABLE IF EXISTS SoundLevel_StnEnq");
		db.execSQL(" DROP TABLE IF EXISTS WorkTypeHistory");
		db.execSQL(" DROP TABLE IF EXISTS StationInventory");
		db.execSQL(" DROP TABLE IF EXISTS GPSrecords");
		db.execSQL(" DROP TABLE IF EXISTS UserList");
		db.execSQL(" DROP TABLE IF EXISTS WorkMaterialSupporter");
		db.execSQL(" DROP TABLE IF EXISTS WorkAssignSupporter");
		db.execSQL(" DROP TABLE IF EXISTS ActivityAllocationMaster");
		db.execSQL(" DROP TABLE IF EXISTS WorkType");
		db.execSQL(" DROP TABLE IF EXISTS UserpassEligibleTable");
		db.execSQL(" DROP TABLE IF EXISTS UserLoginTable");

		db.execSQL(" DROP TABLE IF EXISTS Reporting");
		db.execSQL(" DROP TABLE IF EXISTS MaterialReqList");
		db.execSQL(" DROP TABLE IF EXISTS MaterialReason");
		db.execSQL(" DROP TABLE IF EXISTS DeliveredRequests");
		db.execSQL(" DROP TABLE IF EXISTS PendingRequests");
		db.execSQL(" DROP TABLE IF EXISTS WorkDonetable");                                 
		db.execSQL(" DROP TABLE IF EXISTS UpdateCount");
		db.execSQL(" DROP TABLE IF EXISTS AllStation");
		db.execSQL(" DROP TABLE IF EXISTS PendingClips");
		db.execSQL(" DROP TABLE IF EXISTS TvStatus");
		db.execSQL(" DROP TABLE IF EXISTS NonrepeatedAd");
		db.execSQL(" DROP TABLE IF EXISTS Downtime");
		db.execSQL(" DROP TABLE IF EXISTS DownTimeRason");
		db.execSQL(" DROP TABLE IF EXISTS DownTimeRasonFill");
		db.execSQL(" DROP TABLE IF EXISTS NotificationTable");
		db.execSQL(" DROP TABLE IF EXISTS TVStatusReason");
		db.execSQL(" DROP TABLE IF EXISTS getTVReason");
		db.execSQL(" DROP TABLE IF EXISTS SuspectedHistory");
		db.execSQL(" DROP TABLE IF EXISTS Password");
		db.execSQL(" DROP TABLE IF EXISTS CommunicationTable");
		db.execSQL(" DROP TABLE IF EXISTS UserNameTable");
		//db.execSQL(" DROP TABLE IF EXISTS TableNotifications");
		db.execSQL(" DROP TABLE IF EXISTS WorkAssignedTable");

		db.execSQL("alter table " + TABLE_GPSRECORDS + " RENAME TO temp;");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPSRECORDS);
		db.execSQL(CREATE_GPSRECORDS_TABLE);//TABLE_GPSRECORDS
		db.execSQL("insert into " + TABLE_GPSRECORDS + " select * from temp;");
		db.execSQL("DROP TABLE IF EXISTS temp ");

		db.execSQL("alter table " + TABLE_ADD_GPSRECORDS + " RENAME TO temp;");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADD_GPSRECORDS);
		db.execSQL(CREATE_GPSRECORDS_ADD_TABLE);//TABLE_ADD_GPSRECORDS
		db.execSQL("insert into " + TABLE_ADD_GPSRECORDS + " select * from temp;");
		db.execSQL("DROP TABLE IF EXISTS temp ");

		db.execSQL("alter table " + TABLE_GPS_SEND_NOTIFICATION + " RENAME TO temp;");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPS_SEND_NOTIFICATION);
		db.execSQL(CREATE_TABLE_GPS_SEND_NOTIFICATION);//TABLE_GPS_SEND_NOTIFICATION
		db.execSQL("insert into " + TABLE_GPS_SEND_NOTIFICATION + " select * from temp;");
		db.execSQL("DROP TABLE IF EXISTS temp ");


		db.execSQL("alter table " + TABLE_LEAVE_REPORTING_TO + " RENAME TO temp;");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEAVE_REPORTING_TO);
		db.execSQL(CREATE_LEAVE_REPORTING_TO_TABLE);//TABLE_LEAVE_REPORTING_TO
		db.execSQL("insert into " + TABLE_LEAVE_REPORTING_TO + " select * from temp;");
		db.execSQL("DROP TABLE IF EXISTS temp ");

		// Create tables again
		onCreate(db);
	}

    public void add_sta_visit_form_data(String QuesID, String Question, String Ans_RespType, String SelectionText,
                                        String MinValue, String MaxValue, String QuesCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PKQuesId", QuesID);
        cv.put("QuesText", Question);
        cv.put("ResponseType", Ans_RespType);
        cv.put("SelectionText", SelectionText);
        cv.put("ValueMin", MinValue);
        cv.put("ValueMax", MaxValue);
        cv.put("QuesCode",QuesCode);
        long a = db.insert(TABLE_STA_VISIT_FORM, null, cv);
        Log.e("Data - ", String.valueOf(a));
    }

    public int get_Sta_Visit_form_data() {
        String countQuery = "SELECT  * FROM " + TABLE_STA_VISIT_FORM ;
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor != null && !cursor.isClosed()) {
            count = cursor.getCount();
            cursor.close();
        }
        Log.e("cnt", String.valueOf(count));
        return count;
    }

	public void addGpsNotification(String FromUserMAster, String toUsermaster, String date, String msg) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		// values.put("DId", datasheet.getId());
		values.put("ToUsermatserID", toUsermaster);
		values.put("FromUserMasterID", FromUserMAster);
		values.put("Date", date);
		values.put("MSG", msg);
		values.put("isUploaded", "No");
		long a = db.insert(TABLE_GPS_SEND_NOTIFICATION, null, values);
		Log.d("test", " values " + values);
		//db.close();
	}

	public void UpdateSetting(String value) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("Update Settings SET SettingValue='" + value
				+ "' where SettingName='rTime'");
		Log.e("DBHandler", "set Settings : " + value);
		//db.close();
	}

	public String getSetting() {
		String val = "15";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"Select SettingName,SettingValue from Settings", null);
		cursor.moveToFirst();
		val = cursor.getString(1);
		Log.e("DBHandler", "get Settings : " + val);
		return val;
	}

	public void addRecord(String Password, String Count) {
		SQLiteDatabase db = this.getWritableDatabase();
		// Toast.makeText(getApplicationContext(), "inserted",
		// Toast.LENGTH_LONG).show();

		ContentValues values = new ContentValues();
		values.put("Password", Password);
		values.put("Count", Count);

		db.insert("Pwd", null, values);

		//db.close(); // Closing database connection
	}

	public void addReporting(String id, String Name, String email) {
		SQLiteDatabase db = this.getWritableDatabase();
		// Toast.makeText(getApplicationContext(), "inserted",
		// Toast.LENGTH_LONG).show();

		ContentValues values = new ContentValues();

		values.put("ReportingId", id);
		values.put("ReportingName", Name);
		//values.put("email",email);

		db.insert("Reporting", null, values);

		//db.close(); // Closing database connection
	}

	// /////////////////////////////////////////////

	public void addContact(Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SERIALNUMBER, contact.getSerailNumber());
		values.put(KEY_PH_NO, contact.getPhoneNumber());// Contact Name
		values.put(KEY_NUMBER, contact.getNumber()); // Contact Phone Number
		values.put(KEY_URL, contact.geturl());
		values.put(KEY_ENVIRONMENT, contact.getenvironment());
		values.put(KEY_PLANT, contact.getplant());
		// Inserting Row
		db.insert(TABLE_CONTACTS, null, values);
		//db.close(); // Closing database connection
	}

	public void addGps(String MobileNo, String latitude, String longitude,
					   String locationName, String GPSaddedDate, String UserMasterID, String isUploaded) {
		SQLiteDatabase db = this.getWritableDatabase();

		if(MobileNo==null || MobileNo.equalsIgnoreCase("")||
				GPSaddedDate==null || GPSaddedDate.equalsIgnoreCase("")||
				UserMasterID==null || UserMasterID.equalsIgnoreCase("")){}
		else {
			if (!(UserMasterID.equalsIgnoreCase(Common.MyLogin))) {
				ContentValues values = new ContentValues();
				values.put("MobileNo", MobileNo);
				values.put("latitude", latitude);
				values.put("longitude", longitude);
				values.put("locationName", locationName);
				values.put("GpsAddedDt", GPSaddedDate);
				values.put("UserMasterID", UserMasterID);
				values.put("isUploaded", isUploaded);
				long a = db.insert(TABLE_ADD_GPSRECORDS, null, values);
				Log.e("data", "insert" + a);
			}
		}
		//db.close();
	}

	public List<Contact> getAllContacts() {
		List<Contact> contactList = new ArrayList<Contact>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Contact contact = new Contact();
				contact.setSerialNumber(cursor.getString(0));
				contact.setPhoneNumber(cursor.getString(1));
				contact.setNumber(cursor.getString(2));
				contact.seturl(cursor.getString(3));
				contact.setenvironment(cursor.getString(4));
				contact.setplant(cursor.getString(5));
				// Adding contact to list
				contactList.add(contact);
			} while (cursor.moveToNext());
		}

		// return contact list
		return contactList;
	}
	
	public void add_Notification_Table(String Notification_id, String From,
            String Time, String Message){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("fromid", Notification_id);
		cv.put("messfrom", From);
		//Date datetime = SplashActivity.getDateTime(Time);
		cv.put("time",Time);
		cv.put("message", Message);
		long a = db.insert("NotificationTable", null, cv);
		
		// Log.e("*//****************************//*","**********************************");
		Log.e("NOTIFICATION TABLE ", "ENTRY ZALI : " + String.valueOf(a));
		//  Log.e("****************************","**********************************");
	}

	public void addSoundLevel(SoundLevelBeanSort contact) {
		try {
			SQLiteDatabase sql = this.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put("InstallationId", contact.getInstallationId());
			values.put("StationName", contact.getStationName());
			values.put("CallibrationDate", contact.getCallibrationDate());
			values.put("ScheduletimeDate", contact.getScheduleDate());
			values.put("ScheduletimeTime", contact.getScheduleTime());
			values.put("AO", contact.getAO());
			String Stand = contact.getStandard();
			long intval = Integer.parseInt(Stand);
			long intvalue = intval / 100000 ;
			String std = Integer.toString((int) intvalue);
			values.put("Standard", std);
			String Actual = contact.getActual();
			long Actualint = Integer.parseInt(Actual);
			long Actualintres = Actualint / 100000 ;
			String StringAct = Integer.toString((int) Actualintres);
			values.put("Actual", StringAct);
			values.put("Percentage", contact.getPercentage());
			values.put("NetworkCode", contact.getNetworkCode());

			long a = sql.insert("SoundLevel", null, values);
			//sql.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addNotification(String NotifyID,String InstallationId, String StationName, String AddedDt,
								String Message, String MsgType, String MsgVal, String MsgText) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put("NotifyID", NotifyID);
		cv.put("InstallationId", InstallationId);
		cv.put("StationName", StationName);
		cv.put("AddedDt", AddedDt);
		cv.put("Message", Message);
		cv.put("MsgType", MsgType);
		cv.put("MsgVal", MsgVal);
		cv.put("MsgText", MsgText);
		long a = db.insert("TableNotifications", null, cv);
	}

	public void addAdvDetail(String NW, String instId, String instName, String advCode, String advDesc,
							 String clipPath, String effTo, String effFrom){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put("NetworkCode", NW);
		cv.put("InstalationId", instId);
		cv.put("InstalationName", instName);
		cv.put("AdvertisementCode", advCode);
		cv.put("AdvertisementDesc", advDesc);
		cv.put("URL_clipPath", clipPath);
		cv.put("EffectiveDateTo", effTo);
		cv.put("EffectiveDatefrom", effFrom);
		long a = db.insert("AdvAudioClipDtls", null, cv);
	}

	public void addTeamLeaders(String username, String usermasterid){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("username", username);
		cv.put("usermasterid", usermasterid);
		long a = db.insert("TeamLeaders", null, cv);
	}
	
	public void addSoundLevelND(SoundLevelBeanSort contact) {
		try {

			SQLiteDatabase sql = this.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put("InstallationId", contact.getInstallationId());
			values.put("StationName", contact.getStationName());
			values.put("CallibrationDate", contact.getCallibrationDate());
			values.put("ScheduletimeDate", contact.getScheduleDate());
			values.put("ScheduletimeTime", contact.getScheduleTime());
			values.put("AO", contact.getAO());
			String Stand = contact.getStandard();
			long intval = Integer.parseInt(Stand);
			long intvalue = intval / 100000 ;
			String std = Integer.toString((int) intvalue);
			values.put("Standard", std);
			String Actual = contact.getActual();
			long Actualint = Integer.parseInt(Actual);
			long Actualintres = Actualint / 100000 ;
			String StringAct = Integer.toString((int) Actualintres);
			values.put("Actual", StringAct);
			values.put("Percentage", contact.getPercentage());
			values.put("NetworkCode", contact.getNetworkCode());

			long a = sql.insert("SoundLevelND", null, values);
			//sql.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public int updateContact(Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_PH_NO, contact.getPhoneNumber());
		values.put(KEY_NUMBER, contact.getNumber());
		values.put(KEY_URL, contact.geturl());
		values.put(KEY_ENVIRONMENT, contact.getenvironment());
		values.put(KEY_PLANT, contact.getplant());

		// updating row
		return db.update(TABLE_CONTACTS, values, KEY_SERIALNUMBER + " = ?",
				new String[] { String.valueOf(contact.getSerailNumber()) });
	}

	public int updatedb(String field, String fvalue) {
		SQLiteDatabase db = this.getWritableDatabase();

		String serial = getfield("serialnumber");
		// String serial = getfield(field);
		ContentValues values = new ContentValues();
		values.put(field, fvalue);
		return db.update(TABLE_CONTACTS, values, KEY_SERIALNUMBER + " = ?",
				new String[] { serial });
	}

	public String getfield(String field) {
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
		int fieldno = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (field == "serialnumber")
			fieldno = 0;
		if (field == "phone_number")
			fieldno = 1;
		if (field == "number")
			fieldno = 2;
		if (field == "url")
			fieldno = 3;
		if (field == "environment")
			fieldno = 4;
		if (field == "plant")
			fieldno = 5;
		if (cursor.moveToFirst()) {
			return cursor.getString(fieldno);
		}
		return "";
	}

	public void addSuspectedDetails(String InstalationId, String StnNm, String TotalSpt, String ActSpot, String SpotPerc,
							 String AdvtDesc){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put("InstalationId", InstalationId);
		cv.put("StationName", StnNm);
		cv.put("TotalSpot", TotalSpt);
		cv.put("ActRept", ActSpot);
		cv.put("SpotWisePercentage", SpotPerc);
		cv.put("AdvertisementName", AdvtDesc);
		long a = db.insert("Suspected", null, cv);
	}
}

package com.stavigilmonitoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import static android.content.Context.WINDOW_SERVICE;

public class utility {

	public static DefaultHttpClient httpClient = new DefaultHttpClient();
	private static final String IMAGE_DIRECTORY_NAME = "VigilLogs";

	public static String OpenConnection(String notificationurl) {
		String res = "";
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

			StrictMode.setThreadPolicy(policy);

		    URL url1 = new URL(notificationurl);

			HttpGet httppost = new HttpGet(URLEncoder.encode(notificationurl.toString(), "UTF-8"));
			HttpResponse response = httpClient.execute(httppost);

			HttpEntity entity = response.getEntity();
			System.out.println("Api entity"+entity.toString());
			res = EntityUtils.toString(entity);
			System.out.println("Api res"+res.toString());

		} catch (Exception e) {
			System.out.println("Api e"+e.toString());
			e.printStackTrace();
		}

		return res;
	}

	public static Object OpenPostConnectionNow(String url, String FinalObj) {
		String res = null;
		Object response = null;
		try {

			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

			StrictMode.setThreadPolicy(policy);
			URL url1 = new URL(url);

			HttpPost httppost = new HttpPost(url.toString());
			StringEntity se = new StringEntity(FinalObj.toString());
			httppost.setEntity(se);
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Content-type", "application/json");
			ResponseHandler responseHandler = new BasicResponseHandler();
			response = httpClient.execute(httppost, responseHandler);

			Log.i("Common Data", response + "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	public static Object OpenPostConnection(String url, JSONObject FinalObj) {
		String res = null;
		Object response = null;
		try {

			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

			StrictMode.setThreadPolicy(policy);
			URL url1 = new URL(url);
			HttpPost httppost = new HttpPost(url.toString());
			StringEntity se = new StringEntity(FinalObj.toString());
			httppost.setEntity(se);
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Content-type", "application/json");
			ResponseHandler responseHandler = new BasicResponseHandler();
			response = httpClient.execute(httppost, responseHandler);
			Log.i("Common Data", response + "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	public String getUpdateCount() {
		String UpdateCount = "CREATE TABLE IF NOT EXISTS " + "UpdateCount"
				+ "(updatevalue  TEXT)";
		return UpdateCount;
	}

	public String getConnectionStatusUser1() {
		String ConnectionStatusUser1 = "CREATE TABLE IF NOT EXISTS "
				+ "ConnectionStatusUser1"
				+ "(InstallationId TEXT, InstallationDesc TEXT,UserName TEXT, MobileNo TEXT,SUP TEXT )";
		return ConnectionStatusUser1;
	}

	public String getAllStation() {
		String AllStation = "CREATE TABLE IF NOT EXISTS " + "AllStation"
				+ "(InstallationId TEXT, StatioName TEXT, NetworkCode TEXT )";
		return AllStation;
	}

	public String getWorkAssignSupporter() {
		String AllStation = "CREATE TABLE IF NOT EXISTS " + "WorkAssignSupporter"
				+ "(UserMasterId TEXT,Support TEXT,Mobile TEXT )";
		return AllStation;
	}

	public String getWorkAssignList() {
		String AllStation = "CREATE TABLE IF NOT EXISTS " + "WorkAssignedTable"
				+ "(DMHeaderId TEXT, DMDesc TEXT,"
				+ "ActivityId TEXT,ActivityName TEXT,UserMasterId TEXT,ActualStartDate TEXT,ActualEndDate TEXT,IssuedTo TEXT,"
				+ "StationName TEXT, InstallationId TEXT,Status TEXT, NetworkCode TEXT,SubNetworkCode TEXT,UserName TEXT,IssuedUserName TEXT)";
		return AllStation;
	}

	public String GetUserList() {
		String ListUser = "CREATE TABLE IF NOT EXISTS " + "UserList"
				+ "(SrNo TEXT, Mobile TEXT)";
		return ListUser;
	}

	public String getWorkDone() {
		String AllWorkdone = "CREATE TABLE IF NOT EXISTS "
				+ "WorkDonetable"
				+ "(WkId INTEGER PRIMARY KEY AUTOINCREMENT,WorkType TEXT,WorkRemark TEXT,MatType TEXT,Station TEXT,StationInstal TEXT,Mob TEXT,Currentdate TEXT,MatRemark TEXT,ActivityID TEXT,MYLocation TEXT,ActName TEXT,Latitude DOUBLE,Longitude DOUBLE,isUpload TEXT)";
		return AllWorkdone;
	}

	public String Databg() {
		String Data = "CREATE TABLE IF NOT EXISTS "
				+ "Backgroundplaylist"
				+ "(InstalationId TEXT, InstallationDesc TEXT, NetworkCode TEXT, SubNetworkCode  TEXT, "
				+ "PlaylistName TEXT, PlaylistContent TEXT, EffectiveDateTo DATETIME, EffectiveDateFrom DATETIME,"
				+ "ContentName TEXT, IsDeleted TEXT, PSDownloadFlag TEXT, "
				+ "PMDownloadFlag TEXT, PCRecDownloadFlag TEXT, PCClipDownloadFlag TEXT,ServerTime DATETIME,PlayDate DATETIME)";
		return Data;
	}

	public String getActivityAllocationMaster() {
		String ActivityAllocationMaster = "CREATE TABLE IF NOT EXISTS "
				+ "ActivityAllocationMaster"
				+ "(ActivityId  TEXT,FormatStDt TEXT,SupportDesc TEXT,FormatEndDt TEXT, ActivityName TEXT ,"
				+ "UserMasterId TEXT, UnitId TEXT ,ActivityTypeId  TEXT, StartDt TEXT ,EndDt TEXT, DueDate TEXT ,"
				+ "IssuedTo  TEXT, Status TEXT ,Remarks TEXT, IsDeleted TEXT ,AddedBy  TEXT, AddedDt TEXT ,"
				+ " ModifiedBy TEXT ,ModifiedDt  TEXT, HoursRequired TEXT ,ProposedUserId TEXT, PriorityName TEXT ,"
				+ "ActualStartDate  TEXT, ActualEndDate TEXT , IsApproval TEXT ,IsApproved TEXT, ChargedAmount TEXT ,"
				+ "ApprovedAmount  TEXT,IsChargable TEXT , Reason TEXT ,ApprovalDt  TEXT, PeriodicBillId TEXT ,"
				+ "CompletionIntimate TEXT, Assigned_Count TEXT ,AttachmentName  TEXT, AttachmentContent TEXT , "
				+ "SourceType TEXT , SourceId TEXT , ExpectedComplete_Date TEXT ,ExecutionAmount  TEXT, "
				+ "ActivityCode TEXT ,ActualCompletionDate TEXT, ReassignedBy TEXT ,ReassignedDt  TEXT, "
				+ "Assigned_By TEXT, ProjectId TEXT,DeptDesc TEXT,ActivityDesc TEXT)";
		return ActivityAllocationMaster;
	}

	public String getPendingClips() {
		String PendingClips = "CREATE TABLE IF NOT EXISTS "
				+ "PendingClips"
				+ "(instalationid TEXT, InstallationDesc TEXT,AdvertisementDesc TEXT,FileName TEXT ,IsTransfer TEXT,AddedDT DATETIME,CLR DATETIME,NetworkCode TEXT, AdvCnt TEXT)";
		return PendingClips;

	}

	public String getStationInventory() {
		String StationInventory = "CREATE TABLE IF NOT EXISTS "
				+ "StationInventory"
				+ "(InventoryId TEXT, ItemName TEXT,InstallationId TEXT,ItemSrNo TEXT ,AddedBy TEXT,AddedDt DATETIME,Mobile TEXT,Remarks TEXT,IsDeleted TEXT)";
		return StationInventory;
	}
	
	public String getAlrtListTable() {
		String AlrtListTable = "CREATE TABLE IF NOT EXISTS "
				+ "AlrtListTable"
				+ "(AlertId TEXT, AlertDesc TEXT,StationName TEXT,InstallationId TEXT ,"
				+ "AddedBy TEXT,AddedDt DATETIME,ResolveBy TEXT,ResolveDt DATETIME,"
				+ "ConfirmBy TEXT,ConfirmDt DATETIME,ModifiedBy TEXT,ModifiedDt DATETIME,"
				+ "RejectedBy TEXT,RejectedDt DATETIME, Mobile TEXT, SupporterName TEXT)";

		return AlrtListTable;
	}
	/*",NetworkCode TEXT*/

	public String getDmCertificateTable() {
		String DmCertificateTable = "CREATE TABLE "
				+ "DmCertificateTable"
				+ "(DMHeaderId TEXT, DMDesc TEXT,SoNumber TEXT,AdvertisementCode TEXT ,"
				+ "ActivityId TEXT,ActivityName TEXT,UserMasterId TEXT,UserName TEXT,"
				+ "ActualStartDate TEXT,ActualEndDate TEXT,IssuedTo TEXT,IssuedUserName TEXT,"
				+ "Status TEXT,StationName TEXT, InstallationId TEXT, NetworkCode TEXT, "
				+ "SubNetworkCode TEXT, AdvertisementPlayURL TEXT, GenrateFileName TEXT, EffectiveDate TEXT)";
		return DmCertificateTable;
	}
	
	public String getCommunicationTable() {
		String CommunicationTable = "CREATE TABLE IF NOT EXISTS "
				+ "CommunicationTable"
				+ "(CommunicationId TEXT, AlertId TEXT, CommentDescription TEXT, AddedByOfAlert TEXT,"
				+ " AlertAddedDt TEXT, AlertResolveDt TEXT, ResolveBy TEXT, "
				+ "AlertDesc TEXT, cmdAddedBy TEXT, CmdAddedDt TEXT, ConnectionTime TEXT, StationName TEXT,InstallationId TEXT,status TEXT"
				+ ")";
		return CommunicationTable;

	}

	public String getMaterialHistory() {
		String MaterialHistory = "CREATE TABLE IF NOT EXISTS "
				+ "MaterialHistory"
				+ "(pkmaterialid TEXT, statusname TEXT,senderMobNo TEXT,materialname TEXT ,reason TEXT,qty TEXT,stationname TEXT,stationmasterid TEXT,reporteename TEXT,addedtdt TEXT,sendername TEXT)";
		return MaterialHistory;
	}

	public String getBusReporting() {
		String BusReporting = "CREATE TABLE IF NOT EXISTS "
				+ "BusReporting"
				+ "(dateofbus TEXT, countofbus INTEGER,Installationid TEXT,InstallationDesc TEXT ,InstalationName TEXT)";
		return BusReporting;
	}
	
	public String getConnectionStatusUserForAlert() {

		String ConnectionStatusUserForAlert = "CREATE TABLE IF NOT EXISTS "
				+ "ConnectionStatusUserForAlert"
				+ "(InstalationId TEXT, InstallationDesc TEXT, Type TEXT, SubNetworkCode TEXT)";// NetworkCode
		return ConnectionStatusUserForAlert;
	}

	public String getConnectionStatusUser() {
		/*
		 * String ConnectionStatusUser = "CREATE TABLE " +
		 * "ConnectionStatusUser" +
		 * "(InstallationId TEXT, ServerTime  TEXT, StartTime TEXT, EndTime TEXT, Remarks TEXT, InstallationDesc  TEXT, Last7DaysPerFormance TEXT, QuickHealStatus TEXT, STAVersion TEXT, LatestDowntimeReason TEXT, UserName TEXT, MobileNo TEXT)"
		 * ;
		 */
		String ConnectionStatusUser = "CREATE TABLE IF NOT EXISTS " + "ConnectionStatusUser"
				+ "(InstallationId TEXT, ServerTime  TEXT,  Remarks TEXT, Last7DaysPerFormance TEXT, QuickHealStatus TEXT, STAVersion TEXT,TVStatus TEXT, LatestDowntimeReason TEXT, Type TEXT,SubNetworkCode TEXT)";
		return ConnectionStatusUser;
	}

	public String getConnectionStatusFilter() {

		String ConnectionStatusFilter = "CREATE TABLE IF NOT EXISTS "
				+ "ConnectionStatusFilter"
				+ "(InstalationId TEXT, InstalationName  TEXT,  InstallationDesc TEXT, Address TEXT, SubNetworkCode TEXT , NetworkCode TEXT,LastbusReporting TEXT,LastAdvDate TEXT,ServerTime TEXT)";// NetworkCode
		return ConnectionStatusFilter;
	}

	public String getConnectionStatusFiltermob() {

		String ConnectionStatusFilter = "CREATE TABLE IF NOT EXISTS "
				+"ConnectionStatusFiltermob"
				+ "(InstalationId TEXT, InstalationName  TEXT,  InstallationDesc TEXT, Address TEXT, SubNetworkCode TEXT , NetworkCode TEXT,LastbusReporting TEXT,LastAdvDate TEXT,ServerTime TEXT)";// NetworkCode
		return ConnectionStatusFilter;
	}

	public String getpeticularconnection() {

		String peticularconnection = "CREATE TABLE IF NOT EXISTS "
				+ "PerticularConnection"
				+ "(InstalationId TEXT, InstalationName  TEXT,  InstallationDesc TEXT, Address TEXT, NetworkCode TEXT , LastbusReporting TEXT,LastAdvDate TEXT,ServerTime TEXT,SubNetworkCode TEXT)";// NetworkCode
		return peticularconnection;
	}

	public String getLastThreeADV() {

		String LastThreeADV = "CREATE TABLE IF NOT EXISTS "
				+ "LastthreeAdv"
				+ "(InstalationId TEXT, LastAdvDate  TEXT,  AdvertisementDesc TEXT, AdvertisementCode TEXT, AudioOutPut TEXT)";// NetworkCode
		return LastThreeADV;
	}

	public String getLastAnn() {

		String LastAnn = "CREATE TABLE IF NOT EXISTS "
				+ "LastthreeAnn"
				+ "(BusTime TEXT, ActualAnntime  TEXT, BUSNO TEXT, startingStation TEXT,endStation TEXT,SubNetworkCode TEXT,ServerAddedDT TEXT,InstallationId TEXT)";// NetworkCode
		return LastAnn;
	}

	public String getLmsConnectionStatus() {
		String LmsConnectionStatus = "CREATE TABLE IF NOT EXISTS "
				+ "LmsConnectionStatus"
				+ "(SrNo TEXT, DepotId  TEXT,  Depot TEXT, LastConnectionTime TEXT, LastConn TEXT , IPAddress TEXT ,NetworkCode TEXT)";
		return LmsConnectionStatus;
	}

	public String getTvStatus() {
		String TvStatus = "CREATE TABLE IF NOT EXISTS " + "TvStatus"
				+ "(InstallationId TEXT, TVStatusReason TEXT,TVStatus TEXT,Type TEXT,flg INTEGER)";
		return TvStatus;
	}

	public String getNonrepeatedAd() {
		String NonrepeatedAd = "CREATE TABLE IF NOT EXISTS "
				+ "NonrepeatedAd"
				+ "(StationMasterId TEXT, AdvertisementCode TEXT, AdvertisementDesc TEXT, InstallationDesc  TEXT, "
				+ "EffectiveDateFrom DATETIME, EffectiveDateTo TEXT, Type TEXT, ClipId TEXT , "
				+ "IsmasterRecordDownloaded TEXT, IsDetailRecordDownloaded TEXT, IsClipMasterRecordDownloaded TEXT, "
				+ "InstallationCount INTEGER, LastServerTime DATE, FirstReportingDate DATE, LatestAddeDate DATE,CSR TEXT,LA TEXT,LB TEXT,LBR TEXT,AdvCnt TEXT)";
		return NonrepeatedAd;
	}

	public String getDowntime() {
		String Downtime = "CREATE TABLE IF NOT EXISTS "
				+ "Downtime"
				+ "(InstalationId TEXT, InstallationDesc TEXT, AddedDate DATETIME, StationDownTime  INTEGER)";
		return Downtime;
	}

	public String getSuspected() {
		String Suspected = "CREATE TABLE IF NOT EXISTS "
				+ "Suspected"
				+ "(AdvertisementCode TEXT, AdvertisementName TEXT, StationName TEXT, InstalationId TEXT," +
				" EffectiveDateFrom  DATETIME, EffectiveDateTo TEXT, " +
				"DayRepeatitions TEXT, ActRept TEXT, StationSpots TEXT, TotalSpot TEXT, " +
				"SpotWisePercentage TEXT, Percentage TEXT, Count INTEGER)";
		return Suspected;
	}

	public String getSoundLevel() {
		String SoundLevel = "CREATE TABLE IF NOT EXISTS "
				+ "SoundLevel"// SoundLevel
				+ "(InstallationId TEXT, StationName TEXT, CallibrationDate TEXT, ScheduletimeDate  TEXT, ScheduletimeTime NUMERIC, AO TEXT, Standard TEXT, Actual TEXT, Percentage TEXT, NetworkCode TEXT)";
		return SoundLevel;

	}

	public String getSoundLevelND() {
		String SoundLevel = "CREATE TABLE IF NOT EXISTS "
				+ "SoundLevelND"// SoundLevel
				+ "(InstallationId TEXT, StationName TEXT, CallibrationDate TEXT, ScheduletimeDate  NUMERIC, ScheduletimeTime NUMERIC, AO TEXT, Standard TEXT, Actual TEXT, Percentage TEXT, NetworkCode TEXT)";
		return SoundLevel;
	}

	public String getSoundLevelCalibrationStandard() {
		String CalibrationStandard = "CREATE TABLE IF NOT EXISTS "
				+ "CalibrationStandard"// SoundLevel
				+ "(AudioMonitorDetailsID TEXT, CallibrationDate TEXT, CTIME NUMERIC, Standard  NUMERIC, CallibrationVolume NUMERIC, SystemVolume NUMERIC, InstallationId TEXT, InstallationDesc TEXT)";
		return CalibrationStandard;
	}

	public String getMaterialReqList() {
		String MaterialReqList = "CREATE TABLE IF NOT EXISTS " + "MaterialReqList" + "(itemmasterid TEXT, itemdesc TEXT)";
		return MaterialReqList;
	}

	public String getGPSrecords() {
		String GPSrecords = "CREATE TABLE IF NOT EXISTS " + "GPSrecords"
				+ "(GPSID TEXT,MobileNo TEXT,latitude TEXT,longitude TEXT,"
				+ "locationName TEXT,AddedDt TEXT,num TEXT)";
		return GPSrecords;
	}
	
	public String getNotificationTable() {
	 String CREATE_TABLE_Notification = "CREATE TABLE IF NOT EXISTS "
            + "NotificationTable"
            + "(fromid TEXT,"+
            "messfrom TEXT,"+
            "time DATETIME,"+
            "message TEXT)";
	 return CREATE_TABLE_Notification;
	}

	public String getWorkDoneAndMaterialSupporter() {
		String supporter = "CREATE TABLE IF NOT EXISTS "
				+ "WorkMaterialSupporter"
				+ "(WorkTypeMasterId TEXT,WorkType TEXT,Remarks TEXT,MaterialName TEXT,"
				+ "StationName TEXT,InstallationId TEXT,Mobileno TEXT,currentDate TEXT,remarksMaterial TEXT,ActivityId TEXT,currentLocation TEXT,ActivityName TEXT,latitude TEXT,longitude TEXT)";
		return supporter;
	}

	public String getMaterialReason() {
		String MaterialReason = "CREATE TABLE IF NOT EXISTS "
				+ "MaterialReason"
				+ "(ReasonMasterId TEXT, ReasonCode TEXT , ReasonDescription TEXT , SyncInfo TEXT)";
		return MaterialReason;
	}

	public String getSoundLevel_new() {
		String SoundLevel_new = "CREATE TABLE IF NOT EXISTS "
				+ "SoundLevel_new"// SoundLevel_new
				+ "(InstalationId TEXT, InstallationDesc TEXT, NetworkCode TEXT, CallibrationDate TEXT, AO TEXT, ServerTime TEXT,SubNetworkCode TEXT)";
		return SoundLevel_new;
	}

	public String getSoundLevel_StnEnq() {
		String SoundLevel_StnEnq = "CREATE TABLE IF NOT EXISTS "
				+ "SoundLevel_StnEnq"// SoundLevel_new
				+ "(InstalationId TEXT, InstallationDesc TEXT, NetworkCode TEXT, CallibrationDate TEXT, AO TEXT, ServerTime TEXT,SubNetworkCode TEXT)";
		return SoundLevel_StnEnq;
	}

	public String getWorkType() {
		String WorkType = "CREATE TABLE IF NOT EXISTS " + "WorkType"// SoundLevel_new
				+ "(WorkTypeId TEXT, WorkTypeName TEXT)";
		return WorkType;
	}

	public String getDMCUsersTable() {
		String DMCUsersTable = "CREATE TABLE IF NOT EXISTS "
				+ "DMCUsersTable "// SoundLevel_new
				+ "(UserId TEXT, UserName TEXT, Mobile TEXT)";
		return DMCUsersTable;
	}

	public String getStnExtTable() {
		String StnExtTable = "CREATE TABLE IF NOT EXISTS "
				+ "StnExtTable "// SoundLevel_new
				+ "(InstallationId TEXT, InstalationName TEXT, NetworkCode TEXT, " +
				 "SubNetworkCode TEXT, Extension TEXT, MobileNo TEXT, Ext1 TEXT)";
		return StnExtTable;
	}
	
	public String getAlrtCountTable() {
		String AlrtCountTable = "CREATE TABLE IF NOT EXISTS "
				+ "AlrtCountTable "// SoundLevel_new
				+ "(InstalationId TEXT, InstalationName TEXT, InstallationDesc TEXT, Address TEXT,"
				+ "NetworkCode TEXT, LastbusReporting TEXT, LastAdvDate TEXT, ServerTime TEXT,"
				+ "SubNetworkCode TEXT, cnt TEXT, AlertId TEXT)";
		return AlrtCountTable;
	}

	public String getWorkTypeHistory() {
		String WorkTypeHistory = "CREATE TABLE IF NOT EXISTS "
				+ "WorkTypeHistory"// SoundLevel_new
				+ "(WorkTypeMasterId TEXT, WorkType TEXT,Remarks TEXT,MaterialName TEXT,StationName TEXT,InstallationId TEXT,Mobileno TEXT,currentDate TEXT,remarksMaterial TEXT,ActivityId TEXT,currentLocation TEXT,ActivityName TEXT,latitude TEXT,longitude TEXT)";

		return WorkTypeHistory;
	}

	public String getAdvDetails() {
		String AdvDetails = "CREATE TABLE IF NOT EXISTS "
				+ "AdvDetailsTable"// SoundLevel_new
				+ "(ScheduleTime TEXT, AdvertisementCode TEXT, AdvertisementDesc TEXT, AudioOutPut TEXT)";

		return AdvDetails;
	}

	public String getReqVsFilldMat() {
		String ReqVsFilldMat = "CREATE TABLE IF NOT EXISTS "
				+ "reqVsFilledMaterial"// SoundLevel_new
				+ "(stationname TEXT, stationmasterid TEXT,addedtdt TEXT,sendermobno TEXT,receiveddate TEXT,sendername TEXT,materialname TEXT,qty TEXT,materialname1 TEXT,stationname1 TEXT,mobileno TEXT,currentdate TEXT)";

		return ReqVsFilldMat;
	}

	public String getDeliveredRequests() {
		String DeliveredRequests = "CREATE TABLE IF NOT EXISTS "
				+ "DeliveredRequests"
				+ "(pkmaterialid TEXT,senderMobNo TEXT, materialname TEXT, reason TEXT, qty TEXT, stationname TEXT,stationmasterid TEXT, reason1 TEXT,"
				+ " scraprepair TEXT, reporteename TEXT, reportingid TEXT,"
				+ " qty1 TEXT, addedtdt TEXT, addedby TEXT, statusflag TEXT,rejectedorder TEXT ,dispatchedorder TEXT, ApproveCategory TEXT)";/*, ApproveCategory TEXT*/
		return DeliveredRequests;
	}

	public String getPendingRequests() {
		String PendingRequests = "CREATE TABLE IF NOT EXISTS "
				+ "PendingRequests"
				+ "(pkmaterialid TEXT,senderMobNo TEXT, materialname TEXT, reason TEXT, qty TEXT, stationname TEXT,stationmasterid TEXT, reason1 TEXT,"
				+ " scraprepair TEXT, reporteename TEXT, reportingid TEXT,"
				+ " qty1 TEXT, addedtdt TEXT, addedby TEXT, statusflag TEXT,rejectedorder TEXT ,dispatchedorder TEXT )";
		return PendingRequests;
	}

	public String getRejectedRequests() {
		String RejectedRequests = "CREATE TABLE IF NOT EXISTS "
				+ "RejectedRequests"
				+ "(pkmaterialid TEXT, senderMobNo TEXT,materialname TEXT, reason TEXT, qty TEXT, stationname TEXT, stationmasterid TEXT, reason1 TEXT, scraprepair TEXT, "
				+ "reporteename TEXT, reportingid TEXT,"
				+ " qty1 TEXT, addedtdt TEXT, addedby TEXT, statusflag TEXT,rejectedorder TEXT ,dispatchedorder TEXT )";
		return RejectedRequests;
	}

	public String getReceivedConfirmation() {
		String ReceivedConfirmation = "CREATE TABLE IF NOT EXISTS "
				+ "ReceivedConfirmation"
				+ "(materialname TEXT,reason TEXT, reporteename TEXT, pkdispatchid TEXT, fkorderid TEXT, mode TEXT,docketno TEXT, date TEXT," +
				" imagename TEXT, imagepath TEXT, mobileno TEXT, senderName TEXT, stationmasterid TEXT, stationname TEXT)";
		return ReceivedConfirmation;
	}

	public String getAllRequests() {
		String AllRequests = "CREATE TABLE IF NOT EXISTS "
				+ "AllRequests"
				+ "(pkmaterialid TEXT,materialname TEXT, reason TEXT, qty TEXT, stationname TEXT, stationmasterid TEXT,reason1 TEXT, reason1 TEXT, scraprepair TEXT, "
				+ "reporteename TEXT, reportingid TEXT,"
				+ " qty1 TEXT, addedtdt TEXT, addedby TEXT, statusflag TEXT)";
		return AllRequests;
	}

	public String getDispatchedbutnotReceived() {
		String DispatchedbutnotReceived = "CREATE TABLE IF NOT EXISTS "
				+ "DispatchedbutnotReceived"
				+ "(matreqfrom TEXT, NetworkCode TEXT, pkmaterialid TEXT, senderMobNo TEXT,materialname TEXT, reason TEXT, qty TEXT, stationname TEXT, stationmasterid TEXT, reason1 TEXT, scraprepair TEXT, "
				+ "reporteename TEXT, reportingid TEXT,"
				+ " qty1 TEXT, addedtdt TEXT, addedby TEXT, statusflag TEXT,rejectedorder TEXT ,dispatchedorder TEXT )";
		return DispatchedbutnotReceived;
	}

	public String getMyorders() {
		String Myorders = "CREATE TABLE IF NOT EXISTS "
				+ "Myorders"
				+ "(pkmaterialid TEXT, senderMobNo TEXT,materialname TEXT, reason TEXT, qty TEXT, stationname TEXT, stationmasterid TEXT, reason1 TEXT, scraprepair TEXT, "
				+ "reporteename TEXT, reportingid TEXT,"
				+ " qty1 TEXT, addedtdt TEXT, addedby TEXT, statusflag TEXT, Warranty TEXT, SelectedDt TEXT)";
		return Myorders;
	}

	public String getDownTimeRason() {
		String DownTimeRason = "CREATE TABLE IF NOT EXISTS "
				+ "DownTimeRason"
				+ "(StationDownTimeID TEXT, InstallationId TEXT, InstallationName TEXT, AddedDate DATE, StartTime  TEXT, EndTime TEXT, StnDwnTime TEXT)";
		return DownTimeRason;
	}

	public String getDownTimeRasonFill() {
		String DownTimeRasonFill = "CREATE TABLE IF NOT EXISTS " + "DownTimeRasonFill"
				+ "(ReasonCode TEXT, ReasonDescription TEXT)";
		return DownTimeRasonFill;
	}

	public String getTVStatusReason() {
		String TVStatusReason = "CREATE TABLE IF NOT EXISTS " + "TVStatusReason"
				+ "(ReasonCode TEXT, ReasonDescription TEXT)";
		return TVStatusReason;
	}

	public String getTVReason() {
		String getTVReason = "CREATE TABLE IF NOT EXISTS " + "getTVReason"
				+ "(InstallationId TEXT,InstalationName TEXT, ReasonDesc TEXT)";
		return getTVReason;
	}

	public String getSuspectedHistory() {
		String SuspectedHistory = "CREATE TABLE IF NOT EXISTS "
				+ "SuspectedHistory"
				+ "(ScheduleTime TEXT, AdvertisementCode TEXT, AdvertisementDesc TEXT, InstallationDesc TEXT)";
		return SuspectedHistory;
	}

	public String getUserNameTable(){
		String UserNameTable = "CREATE TABLE IF NOT EXISTS UserNameTable(UserName TEXT)";
		return UserNameTable;
	}

	public String getUserpassEligibleTable(){
		String UserpassEligibleTable = "CREATE TABLE IF NOT EXISTS UserpassEligibleTable(UserpassEligible TEXT)";
		return UserpassEligibleTable;
	}

	public String getUserLoginTable(){
		String UserLoginTable = "CREATE TABLE IF NOT EXISTS UserLoginTable(UserLogin TEXT,UserPass TEXT)";
		return UserLoginTable;
	}

	public String getNotification_TableNew(){
		String Table_Notification = "CREATE TABLE IF NOT EXISTS TableNotifications(NotifyID TEXT, InstallationId TEXT, StationName TEXT, " +
				"AddedDt TEXT, Message TEXT, MsgType TEXT, MsgVal TEXT, MsgText TEXT)";
		return Table_Notification;
	}

	public void ErrLogFile() {
		SimpleDateFormat dff = new SimpleDateFormat("dd_MM_yyyy");
		String Logfile = dff.format(new Date());

		try {
		String imageFileName = Logfile+".txt";
		File storageDir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY_NAME);
		if (!storageDir.exists()){  // Checks that Directory/Folder Doesn't Exists!
			boolean result = storageDir.mkdir();
			if(result){ /*Toast.makeText(, "New Folder created!",Toast.LENGTH_SHORT).show();*/ }
		}
		File image = new File(storageDir+"/"+imageFileName);
		image.createNewFile();
		// Save a file: path for use with ACTION_VIEW intents
		//mCurrentPhotoPath = "file:" + image.getAbsolutePath();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		/*
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/Logs", Logfile + ".txt");
		Log.e("LOG FILE", file.toString());
		Log.e("LOG FILE PATH", file.getPath().toString());
		if (!file.exists()) {
			try {
                *//*if(file.createNewFile()) {//created successfully
                }else {//couldnt create
                    //show error message
                    Log.e("LOGFILE :", "LOGFILE CREATION ERROR");
                }*//*
                File.createTempFile(".txt",file.getPath());
				//check this
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}

	public void addErrLog(String err) {
		SimpleDateFormat dff = new SimpleDateFormat("dd_MM_yyyy");
		String Logfile = dff.format(new Date());
		File storageDir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY_NAME);
		File file = new File(storageDir+"/"+Logfile + ".txt");
		if (file.exists()) {
			try {
				FileOutputStream fOut = new FileOutputStream(file, true);
				OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
				myOutWriter.append("\n" + "*" + err + "\n");
				myOutWriter.close();
				fOut.close();

			} catch (Exception e) {

			}
		}

	}

	public boolean checkErrLogFile() {
		SimpleDateFormat dff = new SimpleDateFormat("dd_MM_yyyy");
		String Logfile = dff.format(new Date());
		File Logsdir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY_NAME);
		/*File Logsdir = new File(Environment.getExternalStorageDirectory()
				+ "/VigilLogs");*/

		if (!Logsdir.exists()) {
			Logsdir.mkdirs();
		}

		File file = new File(Logsdir+"/"+Logfile + ".txt");
		/*File file = new File(Environment.getExternalStorageDirectory()
				+ "/Logs", Logfile + ".txt");*/
		if (file.exists()) {
			return true;
		} else {
			return false;
		}

	}

	public String getPassword() {
		String Password = "CREATE TABLE IF NOT EXISTS " + "Password" + "(Password TEXT)";
		return Password;
	}

	public String xmessage(Context context1, String msstring) {
		AlertDialog alertDialog;
		alertDialog = new AlertDialog.Builder(context1).create();
		alertDialog.setTitle("Alert");
		alertDialog.setMessage(msstring);
		alertDialog.show();
		return "";
	}

	public static String httpGetvb(String urlString) throws IOException {
		URL url = new URL(urlString.replaceAll(" ", "%20"));

		// URL url = new
		// URL("http://vritti.vworkbench.com/webservice/ActivityWebservice.asmx/GetreportingGps?MobileNo=9890156056");
		Log.d("test", "url" + url);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		Log.d("test", "conn" + conn);
		// conn.connect();
		int resCode = conn.getResponseCode();
		// Check for successful response code or throw error
		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
			// return "0";
		}

		// Buffer the result into a string
		BufferedReader buffrd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = buffrd.readLine()) != null) {
			sb.append(line);
		}

		buffrd.close();

		conn.disconnect();
		return sb.toString();
	}

	public static String httpGet(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// Check for successful response code or throw error
		// if (conn.getResponseCode() != 200) {
		// throw new IOException(conn.getResponseMessage());
		// }

		// Buffer the result into a string
		BufferedReader buffrd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = buffrd.readLine()) != null) {
			sb.append(line);
		}

		buffrd.close();
		conn.disconnect();
		return sb.toString();
	}

	public NodeList getnode(String xml, String Tag) {
		//Log.e("get node", " xml :" + xml + " tag: " + Tag);

		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);
			//Log.e("get node", " doc: " + doc);

		} catch (ParserConfigurationException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}
		// return DOM
		NodeList nl = doc.getElementsByTagName(Tag);
		//Log.e("get node", " nl: " + nl);
		//Log.e("get node", " nl len: " + nl.getLength());
		return nl;
	}

	public String getValue(Element e, String str) {
		NodeList n = e.getElementsByTagName(str);
		return this.getElementValue(n.item(0));
	}

	public final String getElementValue(Node elem) {
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child
						.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	public String get12hrs(int hours, int minutes) {
		String hrs = "", min = "";
		if (hours < 12) {
			hrs = String.format("%2s", Integer.toString(hours)).replace(' ',
					'0');
			// hrs=Integer.toString(hours)+":"+Integer.toString(minutes)+" AM";
			min = String.format("%2s", Integer.toString(minutes)).replace(' ',
					'0');
			hrs = hrs + ":" + min + " AM";
		}
		if (hours == 12) {
			hrs = String.format("%2s", Integer.toString(hours)).replace(' ',
					'0');
			// hrs=Integer.toString(hours)+":"+Integer.toString(minutes)+" AM";
			min = String.format("%2s", Integer.toString(minutes)).replace(' ',
					'0');
			hrs = hrs + ":" + min + " PM";
		}
		if (hours > 12) {

			hrs = String.format("%2s", Integer.toString(hours - 12)).replace(
					' ', '0');
			// hrs=Integer.toString(hours-12)+":"+Integer.toString(minutes)+" PM";
			min = String.format("%2s", Integer.toString(minutes)).replace(' ',
					'0');
			hrs = hrs + ":" + min + " PM";
		}
		return hrs;
	}

	public String getDate(String s1) {

		String finalDate = "";
		String day, monthname, year;
		day = s1.substring(0, 2);
		int days = Integer.parseInt(day);
		// return day;
		monthname = s1.substring(3, 6).trim();

		// int months1= Integer.parseInt(months);
		// int months1=11;
		return getmonth(monthname);
	}

	public String getmonth(String monthName) {

		String mon = "";
		// monthName=Integer.toString(0);
		if (monthName == "Jan")
			mon = "01";
		if (monthName == "Feb")
			mon = "02";
		if (monthName == "Mar")
			mon = "03";
		if (monthName == "Apr")
			mon = "04";
		if (monthName == "May")
			mon = "05";
		if (monthName == "Jun")
			mon = "06";
		if (monthName == "Jul")
			mon = "07";
		if (monthName == "Aug")
			mon = "08";
		if (monthName == "Sep")
			mon = "09";
		if (monthName == "Oct")
			mon = "10";
		if (monthName == "Nov")
			mon = "11";
		if (monthName == "Dec")
			mon = "12";
		return mon;
	}

	public void showD(Context context, String string) {
		// TODO Auto-generated method stub
		Activity a = (Activity) context;
		while(a.getParent() != null) {
			Log.e("ActivityTree",a.getClass().getSimpleName());
			a = a.getParent();
		}

		final Dialog myDialog = new Dialog(context);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);
		// empty nonet invalid nodata
		TextView txt = (TextView) myDialog
				.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);
		if (string.equals("empty")) {
			myDialog.setTitle("Error...");
			txt.setText("Please Fill required data..");
		} else if (string.equals("nonet")) {
			myDialog.setTitle("Error...");
			txt.setText("No Internet Connection Found.Please Activate internet Connectin on Device..");
		} else if (string.equals("invalid")) {// invalid
			myDialog.setTitle(" ");
			txt.setText("No Data Available for Refresh....");
		} else if (string.equals("nodata")) {
			myDialog.setTitle(" ");
			txt.setText("No Data Available...");
		} else if (string.equals("Error")) {
			myDialog.setTitle(" ");
			txt.setText("Due to Internal Server Error...Data is not Saved.Please Try After Sometime..");
		} else if (string.equals("Done")) {
			myDialog.setTitle(" ");
			txt.setText("Data Saved successfully");
		} else if (string.equals("NoPlay")) {
			myDialog.setTitle(" ");
			txt.setText("Playlist Data haS Expired");
		} else if (string.equals("ServerError")) {
			myDialog.setTitle(" ");
			txt.setText("Server Error...");
		}else if (string.equals("novideo")) {
			myDialog.setTitle(" ");
			txt.setText("No videos available...");
		}

		Button btn = (Button) myDialog
				.findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();
				// finish();
			}
		});

		myDialog.show();
	}

	public boolean isnet(Context context) {
		boolean val = false;
		try{
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting()) {
				val = true;
			}
		}catch (Exception e){
			e.printStackTrace();
			val = false;
		}
		return val;
	}
}

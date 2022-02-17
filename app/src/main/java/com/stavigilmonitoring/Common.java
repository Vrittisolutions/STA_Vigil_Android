package com.stavigilmonitoring;

import java.util.ArrayList;

public class Common {
public static int MaterialName=1;
public static int MaterialReason=2;
public static int MaterialRqesendto=3;
public static int Name=4;
public static int InvtItemName=5;
public static int InvtAddEdit=6;
public static int MaterialStn1 = 71;
public static int MaterialStn2 = 72;
public static int AlertStn1 = 81;
public static int AlertStn2 = 82;

    public static int GPSStart = 0;

    public static int WorkAssignStn1 = 91;
    public static int WorkAssignStn2 = 92;
    public static int LOGIN_RUN = 0;
public static String TOKEN = null;
public static String DEVICE_ID = null;

public static String UserName = null;
    public static String UserpassEligible = null;
    public static String UserLogin = null;
    public static String UserPass = null;

    public static String PSVersion = null;
    public static String dialogopen="no";
    public static String MyLogin =    "300130";

    public static final String api_getdata ="/api/LoginAPI/GetModuleSetvalForGPS";

    public static  Boolean isRunningGPSsend= false;
    public   static Boolean FlagDownloadgpsdetail = false;

    public static final String api_PostGpsNot = "/api/GroupMasterAPI/POSTInsert_FCM_GPS";

    public static final String api_postGpsLocation = "/api/GroupMasterAPI/PostGps";

    public static final String api_getGpsLocation = "/api/TimesheetAPI/GetGpsCordinates";

    public static final String AppNameFCM = "vwb7";

    public static String CompanyURL =     "http://ktc.vritti.co";

    public static final String api_postAddGpsLocation = "/api/Values/AddGpsLocation";



    public static  ArrayList<String> listMessages=null;


}

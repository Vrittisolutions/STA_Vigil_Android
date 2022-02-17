package com.stavigilmonitoring;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beanclasses.NotificationBean;
import com.beanclasses.NotifyNetwork;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * @author manish
 *
 */
public class NotifyMultiLevelActivity extends Activity {

	private ArrayList<NotifyNetwork> pFirstLevelArrayList;
	private ArrayList<NotifyNetwork.NotifyStation> pSecondLevelArrayList;
	ArrayList<NotificationBean> mThirdLevelArray;

	private LinearLayout mLinearListView;
	boolean isFirstViewClick=false;
	boolean isSecondViewClick=false;
    DatabaseHandler db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.stavigilmonitoring.R.layout.notify_activity_main);
		mLinearListView = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.linear_listview);

        pFirstLevelArrayList=new ArrayList<NotifyNetwork>();
       // pSecondLevelArrayList=new ArrayList<NotifyNetwork.NotifyStation>();
       // mThirdLevelArray=new ArrayList<NotificationBean>();
        db = new DatabaseHandler(getApplicationContext());

		if(dbvalue()) {
            // generate ArrayLists
            generateArrayLists();
            // adding item into listview
            addlistView();
        }
	}

    /*private void tynchiArrayList() {

        ArrayList<ItemList> mItemListArray=new ArrayList<ItemList>();
        mItemListArray.add(new ItemList("Red", "20"));
        mItemListArray.add(new ItemList("Blue", "50"));
        mItemListArray.add(new ItemList("Red", "20"));
        mItemListArray.add(new ItemList("Blue", "50"));

        ArrayList<ItemList> mItemListArray2=new ArrayList<ItemList>();
        mItemListArray2.add(new ItemList("Pant", "2000"));
        mItemListArray2.add(new ItemList("Shirt", "1000"));
        mItemListArray2.add(new ItemList("Pant", "2000"));
        mItemListArray2.add(new ItemList("Shirt", "1000"));
        mItemListArray2.add(new ItemList("Pant", "2000"));
        mItemListArray2.add(new ItemList("Shirt", "1000"));


        *//**
         *
         *//*
        pSubItemArrayList=new ArrayList<SubCategory>();
        pSubItemArrayList2=new ArrayList<SubCategory>();
        pSubItemArrayList.add(new SubCategory("Color", mItemListArray));
        pSubItemArrayList2.add(new SubCategory("Cloths", mItemListArray2));
        pSubItemArrayList.add(new SubCategory("Color", mItemListArray));
        pSubItemArrayList2.add(new SubCategory("Cloths", mItemListArray2));
        *//**
         *
         *//*

        pProductArrayList=new ArrayList<NotifyProduct>();
        pProductArrayList.add(new NotifyProduct("Emotions", pSubItemArrayList));
        pProductArrayList.add(new NotifyProduct("Garments", pSubItemArrayList2));

    }*/

    private boolean dbvalue(){
        try{
           // DatabaseHandler db1 = new DatabaseHandler(this);
            SQLiteDatabase sql = db.getWritableDatabase();
            String CREATE_TABLE_NOTIFICATION = "CREATE TABLE IF NOT EXISTS "
                    + "TableNotifications"
                    + "(notificationNumber INTEGER PRIMARY KEY AUTOINCREMENT, InstallationId TEXT," +
                    "StationName TEXT,AddedDt TEXT," +
                    "Message TEXT)";
            sql.execSQL(CREATE_TABLE_NOTIFICATION);
            Cursor cursor = sql.rawQuery("Select * from TableNotifications", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                cursor.close();
                return true;
            }else{
                cursor.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void addlistView() {
        for (int i = 0; i < pFirstLevelArrayList.size(); i++) {

            LayoutInflater inflater = null;
            inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mLinearView = inflater.inflate(com.stavigilmonitoring.R.layout.notify_row_first, null);

            final TextView mProductName = (TextView) mLinearView.findViewById(com.stavigilmonitoring.R.id.textViewName);
            final RelativeLayout mLinearFirstArrow=(RelativeLayout)mLinearView.findViewById(com.stavigilmonitoring.R.id.linearFirst);
            final ImageView mImageArrowFirst=(ImageView)mLinearView.findViewById(com.stavigilmonitoring.R.id.imageFirstArrow);
            final LinearLayout mLinearScrollSecond=(LinearLayout)mLinearView.findViewById(com.stavigilmonitoring.R.id.linear_scroll);

            if(isFirstViewClick==false){
                mLinearScrollSecond.setVisibility(View.GONE);
                mImageArrowFirst.setBackgroundResource(com.stavigilmonitoring.R.drawable.arw_lt);
            }
            else{
                mLinearScrollSecond.setVisibility(View.VISIBLE);
                mImageArrowFirst.setBackgroundResource(com.stavigilmonitoring.R.drawable.arw_down);
            }

            mLinearFirstArrow.setOnTouchListener(new OnTouchListener() {
           // mImageArrowFirst.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if(isFirstViewClick==false){
                        isFirstViewClick=true;
                        mImageArrowFirst.setBackgroundResource(com.stavigilmonitoring.R.drawable.arw_down);
                        mLinearScrollSecond.setVisibility(View.VISIBLE);

                    }else{
                        isFirstViewClick=false;
                        mImageArrowFirst.setBackgroundResource(com.stavigilmonitoring.R.drawable.arw_lt);
                        mLinearScrollSecond.setVisibility(View.GONE);
                    }
                    return false;
                }
            });


            final String name = pFirstLevelArrayList.get(i).getpNetworkCode();
            mProductName.setText(name);

            /**
             *
             */
            for (int j = 0; j < pFirstLevelArrayList.get(i).getmNotifyStationList().size(); j++) {

                LayoutInflater inflater2 = null;
                inflater2 = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mLinearView2 = inflater2.inflate(com.stavigilmonitoring.R.layout.notify_row_second, null);

                TextView mSubItemName = (TextView) mLinearView2.findViewById(com.stavigilmonitoring.R.id.textViewTitle);
                final RelativeLayout mLinearSecondArrow=(RelativeLayout)mLinearView2.findViewById(com.stavigilmonitoring.R.id.linearSecond);
                final ImageView mImageArrowSecond=(ImageView)mLinearView2.findViewById(com.stavigilmonitoring.R.id.imageSecondArrow);
                final LinearLayout mLinearScrollThird=(LinearLayout)mLinearView2.findViewById(com.stavigilmonitoring.R.id.linear_scroll_third);

                if(isSecondViewClick==false){
                    mLinearScrollThird.setVisibility(View.GONE);
                    mImageArrowSecond.setBackgroundResource(com.stavigilmonitoring.R.drawable.arw_lt);
                    //mImageArrowSecond.setBackgroundResource(R.drawable.arw_lt);
                }
                else{
                    mLinearScrollThird.setVisibility(View.VISIBLE);
                    mImageArrowSecond.setBackgroundResource(com.stavigilmonitoring.R.drawable.arw_down);
                }

                //mLinearSecondArrow
                mImageArrowSecond.setOnTouchListener(new OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if(isSecondViewClick==false){
                            isSecondViewClick=true;
                            mImageArrowSecond.setBackgroundResource(com.stavigilmonitoring.R.drawable.arw_down);
                            mLinearScrollThird.setVisibility(View.VISIBLE);

                        }else{
                            isSecondViewClick=false;
                            mImageArrowSecond.setBackgroundResource(com.stavigilmonitoring.R.drawable.arw_lt);
                            mLinearScrollThird.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });


                final String catName = pFirstLevelArrayList.get(i).getmNotifyStationList().get(j).getpStationName();
                mSubItemName.setText(catName);
                /**
                 *
                 */
                for (int k = 0; k < pFirstLevelArrayList.get(i).getmNotifyStationList().get(j).getmNotificationListArray().size(); k++) {

                    LayoutInflater inflater3 = null;
                    inflater3 = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View mLinearView3 = inflater3.inflate(com.stavigilmonitoring.R.layout.notify_row_third, null);

                    TextView mItemName = (TextView) mLinearView3.findViewById(com.stavigilmonitoring.R.id.textViewItemName);
                    TextView mItemPrice = (TextView) mLinearView3.findViewById(com.stavigilmonitoring.R.id.textViewItemPrice);
                    mItemPrice.setVisibility(View.GONE);
                    final String itemName = pFirstLevelArrayList.get(i).getmNotifyStationList().get(j).getmNotificationListArray().get(k).getAddedDt();
                    //final String itemPrice = pFirstLevelArrayList.get(i).getmNotifyStationList().get(j).getmNotificationListArray().get(k).getMessage();
                    mItemName.setText(itemName);
                   // mItemPrice.setText(itemPrice);

                    mLinearScrollThird.addView(mLinearView3);
                }

                mLinearScrollSecond.addView(mLinearView2);

            }

            mLinearListView.addView(mLinearView);
        }
    }

    private String getDateinFormat(String amcExpireDt) {
        String result= null;
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
        try {
            Date date2 = dateFormat2.parse(amcExpireDt);
            result = dateFormat1.format(date2);
        }catch( Exception e){
            e.printStackTrace();
        }
        return result;
    }

    private void generateArrayLists() {

        String NetworkCode, InstallationId, StationName;

        //DatabaseHandler db = new DatabaseHandler(this);
        com.stavigilmonitoring.utility mUt = new utility();

        SQLiteDatabase sql = db.getWritableDatabase();

        String sqlstr = "DELETE FROM TableNotifications WHERE AddedDt <= date('now','-15 day')";
        sql.execSQL(sqlstr);

        Cursor c = sql.rawQuery(" SELECT DISTINCT ConnectionStatusFiltermob.NetworkCode FROM ConnectionStatusFiltermob " +
                "INNER JOIN TableNotifications ON " +
                "ConnectionStatusFiltermob.InstalationId = TableNotifications.InstallationId Order by TableNotifications.AddedDt DESC", null);

        Log.e("count", "" + c.getCount());

        if (c.getCount() > 0) {
            c.moveToFirst();
            pFirstLevelArrayList.clear();
            do {

                NetworkCode = c.getString(c
                        .getColumnIndex("NetworkCode"));
                Cursor c1 = sql.rawQuery(
                        "SELECT DISTINCT TableNotifications.InstallationId,TableNotifications.StationName" +
                                " FROM ConnectionStatusFiltermob " +
                                "INNER JOIN TableNotifications ON " +
                                "ConnectionStatusFiltermob.InstalationId = TableNotifications.InstallationId" +
                                " where ConnectionStatusFiltermob.NetworkCode='"
                                + NetworkCode + "' Order by TableNotifications.AddedDt DESC", null);
                if (c1.getCount() > 0) {
                    c1.moveToFirst();
                    pSecondLevelArrayList=new ArrayList<NotifyNetwork.NotifyStation>();
                    //pSecondLevelArrayList.clear();
                    do {
                        Log.e("count", "" + c1.getCount());
                        int Count = c1.getCount();
                        InstallationId = c1.getString(c1.getColumnIndex("InstallationId"));
                        StationName = c1.getString(c1.getColumnIndex("StationName"));
                        Cursor c2 = sql.rawQuery(
                                "SELECT  * FROM  TableNotifications " +
                                        " where StationName='" + StationName + "' Order by AddedDt DESC", null);
                        if (c2.getCount() > 0) {
                            c2.moveToFirst();
                            mThirdLevelArray=new ArrayList<NotificationBean>();
                            //mThirdLevelArray.clear();
                            do {

                                Log.e("count", "" + c2.getCount());
                                //int Count = c2.getCount();
                                NotificationBean nextsc = new NotificationBean();
                                nextsc.setInstallationId(c2.getString(c2.getColumnIndex("InstallationId")));
                                nextsc.setStationName(c2.getString(c2.getColumnIndex("StationName")));
                                nextsc.setAddedDt(c2.getString(c2.getColumnIndex("AddedDt")));
                                nextsc.setFormattedAddedDt(getDateinFormat(c2.getString(c2.getColumnIndex("AddedDt"))));
                                nextsc.setMessage(c2.getString(c2.getColumnIndex("Message")));
                                nextsc.setNetworkCode(NetworkCode);
                                mThirdLevelArray.add(nextsc);
                            } while (c2.moveToNext());
                            pSecondLevelArrayList.add(new NotifyNetwork.NotifyStation(StationName, mThirdLevelArray));
                            c2.close();
                        }
                    } while (c1.moveToNext());
                    pFirstLevelArrayList.add(new NotifyNetwork(NetworkCode, pSecondLevelArrayList));
                    c1.close();
                }
            } while (c.moveToNext());
            c.close();
        }
    }
}

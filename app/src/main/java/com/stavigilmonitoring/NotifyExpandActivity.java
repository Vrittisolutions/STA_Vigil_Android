package com.stavigilmonitoring;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.adapters.NotifyExpandableListAdapter;
import com.beanclasses.NotificationBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin-3 on 9/12/2017.
 */

public class NotifyExpandActivity extends Activity {

    ImageView iv;
    ExpandableListView expListView;
    //private ColorExpListAdapter colorExpListAdapter;
    List<String> listDataHeader,listDataSubHeader;
    NotifyExpandableListAdapter listAdapter;
    HashMap<String, List<NotificationBean>> listDataChild,listDataSubChild;

    ArrayList<List<NotificationBean>> nextdataclect =new ArrayList<List<NotificationBean>>();
    ArrayList<ArrayList<List<NotificationBean>>> dataselect= new ArrayList<ArrayList<List<NotificationBean>>>();
    ArrayList<ArrayList<ArrayList<List<NotificationBean>>>> maindataselect= new ArrayList<ArrayList<ArrayList<List<NotificationBean>>>>();
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.notify_expand_layout);
        iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_notify);
        expListView = (ExpandableListView) findViewById(com.stavigilmonitoring.R.id.expListView);
        //expListView.setVisibility(View.GONE);
        db = new DatabaseHandler(getApplicationContext());

       /* expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                NotifyExpandableListAdapter.CustExpListview SecondLevelexplv = new NotifyExpandableListAdapter.CustExpListview(getApplicationContext());
                SecondLevelexplv.setAdapter(new NotifyExpandableListAdapter.SecondLevelAdapter(getApplicationContext(),childPosition, listDataSubHeader,listDataSubChild));
                //notifyDataSetChanged();
                SecondLevelexplv.setGroupIndicator(null);
                return true;
            }
        });*/



        if (dbvalue()) {
            updatelist();
        }
    }

    private void updatelist() {
        String key,key2;
        String NetworkCode;
        String Message, InstallationId, StationName, AddedDt = null;
        ArrayList<List<NotificationBean>> dataclect =new ArrayList<List<NotificationBean>>();
        maindataselect.clear();
        //nextdataclect.clear();
        listDataSubHeader = new ArrayList<String>();
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<NotificationBean>>();
        listDataSubChild = new HashMap<String, List<NotificationBean>>();
        //DatabaseHandler db = new DatabaseHandler(this);
        com.stavigilmonitoring.utility mUt = new utility();

        SQLiteDatabase sql = db.getWritableDatabase();

        Cursor c = sql.rawQuery(" SELECT DISTINCT ConnectionStatusFiltermob.NetworkCode FROM ConnectionStatusFiltermob " +
                                "INNER JOIN TableNotifications ON " +
                                "ConnectionStatusFiltermob.InstalationId = TableNotifications.InstallationId", null);

        Log.e("count", "" + c.getCount());

        if (c.getCount() > 0) {
            c.moveToFirst();
            dataselect.clear();
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
                List<NotificationBean> searchResults= new ArrayList<NotificationBean>();
                listDataHeader.add(NetworkCode);
                if (c1.getCount() > 0) {
                    c1.moveToFirst();
                    nextdataclect.clear();
                    do {

                        Log.e("count", "" + c1.getCount());
                        int Count = c1.getCount();
                        InstallationId = c1.getString(c1.getColumnIndex("InstallationId"));
                        StationName = c1.getString(c1.getColumnIndex("StationName"));
                        //AddedDt = c1.getString(c1.getColumnIndex("AddedDt"));
                        //Message = c1.getString(c1.getColumnIndex("Message"));
                        listDataSubHeader.add(StationName);

                        NotificationBean sc = new NotificationBean();
                        sc.setInstallationId(InstallationId);
                        sc.setStationName(StationName);
                        //sc.setAddedDt(AddedDt);
                        //sc.setMessage(Message);
                        searchResults.add(sc);
                        key = NetworkCode;

                        Cursor c2 = sql.rawQuery(
                                "SELECT  * FROM  TableNotifications " +
                                        " where StationName='" + StationName + "' Order by AddedDt DESC", null);
                        List<NotificationBean> nextsearchResults = new ArrayList<NotificationBean>();
                        if (c2.getCount() > 0) {
                            c2.moveToFirst();
                            do {

                                Log.e("count", "" + c2.getCount());
                                //int Count = c2.getCount();
                                NotificationBean nextsc = new NotificationBean();
                                nextsc.setInstallationId(c2.getString(c2.getColumnIndex("InstallationId")));
                                nextsc.setStationName(c2.getString(c2.getColumnIndex("StationName")));
                                nextsc.setAddedDt(c2.getString(c2.getColumnIndex("AddedDt")));
                                nextsc.setMessage(c2.getString(c2.getColumnIndex("Message")));
                                nextsc.setNetworkCode(NetworkCode);
                                nextsearchResults.add(nextsc);
                                key2 = StationName;

                            } while (c2.moveToNext());
                            listDataSubChild.put(key2, nextsearchResults);
                            nextdataclect.add(nextsearchResults);
                            c2.close();
                        }
                    } while (c1.moveToNext());
                    listDataChild.put(key, searchResults);
                    //dataclect.add(searchResults);
                    dataselect.add(nextdataclect);
                    c1.close();
                }
            } while (c.moveToNext());
            maindataselect.add(dataselect);

            c.close();

			/*for(int i =0;i<listDataHeader.size();i++){

				listDataChild.put(listDataHeader.get(i), dataclect.get(i));
			}*/

            listAdapter = new NotifyExpandableListAdapter(this, listDataHeader,listDataChild,listDataSubHeader,listDataSubChild);

            // setting list adapter
            expListView.setAdapter(listAdapter);
            /*colorExpListAdapter =
                    new ColorExpListAdapter(
                            this,//expListView,
                            getExpandableListView(),
                            maindataselect
                    );
            setListAdapter( colorExpListAdapter );*/
        }

    }

    private boolean dbvalue() {
        try {

           // DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
            SQLiteDatabase sql = db.getWritableDatabase();
            // Cursor cursor = sql.rawQuery("SELECT *   FROM AllStation", null);
            String sqlstr = "DELETE FROM TableNotifications WHERE AddedDt <= date('now','-15 day')";
            sql.execSQL(sqlstr);
            Cursor c = sql.rawQuery("Select * from TableNotifications order by AddedDt desc", null);
            if (c != null && c.getCount() > 0) {
                    c.close();
                    return true;
            } else {
                c.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            }
            return false;
        }

}


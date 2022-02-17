package com.stavigilmonitoring;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.database.DBInterface;

/**
 * Created by Admin-3 on 1/17/2018.
 */

public class WorkAssignSuppNameListActivity  extends Activity {
    LinearLayout MyActivitylayout;
    TextView tvhead, myCount;
    Button btnaddItem;
    ImageView btnaddItem2, btnrefresh;
    ListView invtlist;
    com.stavigilmonitoring.utility ut;
    String mobno;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.workassignsupplist);
        initViews();
    }

    private void initViews() {
        tvhead = (TextView) findViewById(com.stavigilmonitoring.R.id.tvalertlist);
        tvhead.setText("Supporter List");
        MyActivitylayout = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.MyActivitylayout);

        btnaddItem = (Button) findViewById(com.stavigilmonitoring.R.id.txtCreateAlert);
        btnaddItem.setText("Assign Work");
        btnaddItem2= (ImageView) findViewById(com.stavigilmonitoring.R.id.button_alert_add);
        btnrefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_alert);
        invtlist = (ListView) findViewById(com.stavigilmonitoring.R.id.listAlertitems);
        myCount = (TextView) findViewById(com.stavigilmonitoring.R.id.tvsCntc);
        //mprogressBar = (ProgressBar) findViewById(R.id.progressinvent1);
        //mSearchList = new ArrayList<AlrtStateList>();
        ut = new utility();

        db = new DatabaseHandler(getBaseContext());
        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
    }

}

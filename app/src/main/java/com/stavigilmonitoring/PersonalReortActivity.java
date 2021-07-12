package com.stavigilmonitoring;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.adapters.ExListAdapter;
import com.beanclasses.ExListChildBean;
import com.beanclasses.ExListHeaderBean;
import com.database.DBInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PersonalReortActivity extends Activity {
    private String TAG = PersonalReortActivity.class.getSimpleName();
    //private ListView lv;
    String Mobno, responsemsg="k", jsonStr ="j";
    private ExpandableListView expListView;
    ExListAdapter listAdapter;
    SpotsDialog SPdialog2;
    private int lastExpandedPosition = -1;
    ArrayList<ExListHeaderBean> listDataHeader;
    HashMap<ExListHeaderBean,List<ExListChildBean>> listDataChild;

    int cnt_Alert=0, cnt_DMC=0, cnt_CSN=0, cnt_NonReport=0, cnt_Sound=0, cnt_TVS=0, cnt_Work=0, cnt_PDC=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.activity_notification_expand);

        expListView = (ExpandableListView) findViewById(com.stavigilmonitoring.R.id.lvExp);
        DBInterface dbi = new DBInterface(getApplicationContext());
        Mobno = dbi.GetPhno();

        new GetReports().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        /*expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });*/

    }

    private void prepareListData(JSONArray CSN, JSONArray SoundLevel,JSONArray Alert, JSONArray DMC, JSONArray WorkAssign) {
        listDataHeader = new ArrayList<ExListHeaderBean>();
        listDataChild = new HashMap<ExListHeaderBean, List<ExListChildBean>>();
        List<ExListChildBean> Nolist = new ArrayList<ExListChildBean>();

        /*ExListHeaderBean sitem = new ExListHeaderBean();
        sitem.SetModuleName("CSN");
        sitem.SetMCount(cnt_CSN);
        listDataHeader.add(sitem);
        listDataChild .put(listDataHeader.get(0),Nolist);*/

        List<ExListChildBean> Alertlist = new ArrayList<ExListChildBean>();
        try {
            for (int i = 0; i < Alert.length(); i++) {
                JSONObject c = Alert.getJSONObject(i);
                if(c.has("result")){
                    cnt_Alert = 0;
                }else {
                    ExListChildBean litem = new ExListChildBean();

                    litem.Setdesc(c.getString("AlertDesc"));
                    litem.Setdate(c.getString("AddedDt"));
                    litem.SetstnName(c.getString("StationName"));

                    // adding contact to contact list
                    Alertlist.add(litem);
                    cnt_Alert = Alert.length();
                }
            }

        }catch (Exception e){ e.printStackTrace(); }
        ExListHeaderBean sitem6 = new ExListHeaderBean();
        sitem6.SetModuleName("Alert");
        sitem6.SetMCount(cnt_Alert);
        listDataHeader.add(sitem6);
        listDataChild .put(listDataHeader.get(0),Alertlist);

        List<ExListChildBean> DMClist = new ArrayList<ExListChildBean>();
        try {
            for (int i = 0; i < DMC.length(); i++) {
                JSONObject c = DMC.getJSONObject(i);
                if(c.has("result")){
                    cnt_DMC = 0;
                }else {
                    ExListChildBean litem = new ExListChildBean();

                    litem.Setdesc(c.getString("DMDesc"));
                    litem.Setdate(c.getString("ActualEndDate"));
                    litem.SetstnName(c.getString("StationName"));
                    // adding contact to contact list
                    DMClist.add(litem);
                    cnt_DMC = DMC.length();
                }
            }
        }catch (Exception e){ e.printStackTrace(); }
        ExListHeaderBean sitem7 = new ExListHeaderBean();
        sitem7.SetModuleName("DM Certificate");
        sitem7.SetMCount(cnt_DMC);
        listDataHeader.add(sitem7);
        listDataChild .put(listDataHeader.get(1),DMClist);

        List<ExListChildBean> Worklist = new ArrayList<ExListChildBean>();
        try {
            for (int i = 0; i < WorkAssign.length(); i++) {
                JSONObject c = WorkAssign.getJSONObject(i);
                if(c.has("result")){
                    cnt_Work = 0;
                }else {
                    ExListChildBean litem = new ExListChildBean();

                    litem.Setdesc(c.getString("ActivityName"));
                    litem.Setdate(c.getString("ActualEndDate"));
                    litem.SetstnName(c.getString("StationName"));
                    // adding contact to contact list
                    Worklist.add(litem);
                    cnt_Work = WorkAssign.length();
                }
            }
        }catch (Exception e){ e.printStackTrace(); }
        ExListHeaderBean sitem8 = new ExListHeaderBean();
        sitem8.SetModuleName("Work Assign");
        sitem8.SetMCount(cnt_Work);
        listDataHeader.add(sitem8);
        listDataChild .put(listDataHeader.get(2),Worklist);

        List<ExListChildBean> CSNlist = new ArrayList<ExListChildBean>();
        try {
            for (int i = 0; i < CSN.length(); i++) {
                JSONObject c = CSN.getJSONObject(i);
                if(c.has("result")){
                    cnt_CSN = 0;
                }else {
                    ExListChildBean litem = new ExListChildBean();

                    litem.Setdesc(c.getString("LatestDescription"));
                    litem.Setdate(c.getString("ServerTime"));
                    litem.SetstnName(c.getString("InstallationDesc"));

                    // adding contact to contact list
                    CSNlist.add(litem);
                    cnt_CSN = CSN.length();
                }

            }
        }catch (Exception e){ e.printStackTrace(); }
        ExListHeaderBean sitem1 = new ExListHeaderBean();
        sitem1.SetModuleName("CSN");
        sitem1.SetMCount(cnt_CSN);
        listDataHeader.add(sitem1);
        listDataChild .put(listDataHeader.get(3),CSNlist);



        ExListHeaderBean sitem2 = new ExListHeaderBean();
        sitem2.SetModuleName("Non Reported Station");
        sitem2.SetMCount(cnt_NonReport);
        listDataHeader.add(sitem2);
        listDataChild .put(listDataHeader.get(4),Nolist);

        ExListHeaderBean sitem3 = new ExListHeaderBean();
        sitem3.SetModuleName("PDC Station");
        sitem3.SetMCount(cnt_PDC);
        listDataHeader.add(sitem3);
        listDataChild .put(listDataHeader.get(5),Nolist);

        List<ExListChildBean> Soundlist = new ArrayList<ExListChildBean>();
        try {
            for (int i = 0; i < SoundLevel.length(); i++) {
                JSONObject c = SoundLevel.getJSONObject(i);
                if(c.has("result")){
                    cnt_Sound = 0;
                }else {
                    ExListChildBean litem = new ExListChildBean();

                    litem.Setdesc(c.getString("AO"));
                    litem.Setdate(c.getString("CallibrationDate"));
                    litem.SetstnName(c.getString("InstallationDesc"));

                    // adding contact to contact list
                    Soundlist.add(litem);
                    cnt_Sound = SoundLevel.length();
                }

            }
        }catch (Exception e){ e.printStackTrace(); }
        ExListHeaderBean sitem4 = new ExListHeaderBean();
        sitem4.SetModuleName("Sound Level");
        sitem4.SetMCount(cnt_Sound);
        listDataHeader.add(sitem4);
        listDataChild .put(listDataHeader.get(6),Soundlist);

        ExListHeaderBean sitem5 = new ExListHeaderBean();
        sitem5.SetModuleName("TV Status");
        sitem5.SetMCount(cnt_TVS);
        listDataHeader.add(sitem5);
        listDataChild .put(listDataHeader.get(7),Nolist);


        SPdialog2.dismiss();

        listAdapter = new ExListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);


    }

    private class GetReports extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SPdialog2 = new SpotsDialog(PersonalReortActivity.this);//, R.style.Custom
            SPdialog2.show();
            //Toast.makeText(PersonalReortActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected String doInBackground(Void... arg0) {
            com.stavigilmonitoring.utility ut = new utility();
            //HttpHandler sh = new HttpHandler();
            //Toast.makeText(PersonalReortActivity.this,"URl is about to hit",Toast.LENGTH_LONG).show();

            // Making a request to url and getting response
            String url = "http://vritti.co/iMedia/STA_Announcement/DmCertificate.asmx/DailyReport?Mobileno="+Mobno;
            //String jsonStr = sh.makeServiceCall(url);

            url = url.replaceAll(" ", "%20");
            Log.e("Report ", "Report url: " + url);
            try {
                responsemsg = ut.httpGet(url);
                String[] parts = responsemsg.split("//tempuri.org/\">");
                parts = parts[1].split("</");
                responsemsg = parts[0];
                jsonStr = responsemsg;
                Log.e("Report ", "Report response : " + url);

            }catch (IOException e){
                e.printStackTrace();
                SPdialog2.dismiss();
            }


            return jsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            /*ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList,
                    R.layout.list_item, new String[]{ "email","mobile"},
                    new int[]{R.id.email, R.id.mobile});
            lv.setAdapter(adapter);*/
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray Alert = jsonObj.getJSONArray("Alerts");
                    JSONArray DMC = jsonObj.getJSONArray("DMCertificate");
                    JSONArray CSN = jsonObj.getJSONArray("CSN");
                    int NonReportedADV = jsonObj.getInt("NonReportedADV");
                    int PDC = jsonObj.getInt("PendingDownloadAdv");
                    JSONArray SoundLevel = jsonObj.getJSONArray("SoundLevel");
                    int TVS = jsonObj.getInt("TV_Status");
                   // cnt_CSN = jsonObj.getInt("CSN");
                    cnt_NonReport = jsonObj.getInt("NonReportedADV");
                    cnt_PDC = jsonObj.getInt("PendingDownloadAdv");
                   // cnt_Sound = jsonObj.getInt("SoundLevel");
                    cnt_TVS = jsonObj.getInt("TV_Status");
                    JSONArray WorkAssign = jsonObj.getJSONArray("WorkAssign");
                    prepareListData(CSN,SoundLevel,Alert,DMC,WorkAssign);



                    // looping through All Contacts
                   /* for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String address = c.getString("address");
                        String gender = c.getString("gender");

                        // Phone node is JSON Object
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", mobile);

                        // adding contact to contact list
                        contactList.add(contact);
                    }*/
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    SPdialog2.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                SPdialog2.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

}


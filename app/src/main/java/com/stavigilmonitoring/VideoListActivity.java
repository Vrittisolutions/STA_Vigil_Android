package com.stavigilmonitoring;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.adapters.ExListAdapter;
import com.adapters.VideoListAdapter;
import com.videocompression.MediaController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class VideoListActivity extends Activity {
    Context parent;
    private String TAG = VideoListActivity.class.getSimpleName();
    //private ListView lv;
    String Mobno, responsemsg="k", jsonStr ="j",path;
    private ExpandableListView expListView;
    ExListAdapter listAdapter;
    SpotsDialog SPdialog2;
    private int lastExpandedPosition = -1;
    ArrayList<String> listDataFileName;
    ArrayList<String> listDataFilePath;
    ItemClickListener onItemClickListener;

    ArrayList<String> listDataCompressFilePath;/*
    ArrayList<ExListChildBean> listDataHeader;
    HashMap<ExListHeaderBean,List<ExListChildBean>> listDataChild;*/

    static View.OnClickListener myOnClickListener;
    RecyclerView mRecyclerView;
    private GridLayoutManager gridLayoutManager;
    String LangCode = "";
    utility ut = new utility();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.activity_videolist);

        parent = VideoListActivity.this;
        mRecyclerView = findViewById(com.stavigilmonitoring.R.id.my_recycler_view);

        Intent intent = getIntent();
        LangCode = intent.getStringExtra("LangCode");

        new GetVideoList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

       /* mRecyclerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //removeItem(v);

            }});
*/
    }

    private void prepareListData(JSONArray Alert) {
        //listDataHeader = new ArrayList<ExListChildBean>();
        listDataFileName = new ArrayList<String>();
        listDataFilePath = new ArrayList<String>();
        listDataFileName.clear();
        listDataFilePath.clear();

        //List<ExListChildBean> Alertlist = new ArrayList<ExListChildBean>();
        try {
            for (int i = 0; i < Alert.length(); i++) {
                JSONObject c = Alert.getJSONObject(i);
                if(c.has("result")){
                }else {
                    /*ExListChildBean litem = new ExListChildBean();

                    litem.Setdesc(c.getString("path"));
                    litem.Setdate(c.getString("filename"));
                    litem.SetstnName(c.getString("size"));*/

                    // adding contact to contact list
                    if(c.getString("filename").contains("mp4")) {
                        listDataFileName.add(c.getString("filename"));
                        listDataFilePath.add(c.getString("path"));
                    }

                   // path=c.getString("path");
                    //listDataHeader.add(litem);

                }
            }

            SPdialog2.dismiss();

            gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
            mRecyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
            //  call the constructor of CustomAdapter to send the reference and data to Adapter
            VideoListAdapter customAdapter = new VideoListAdapter(VideoListActivity.this, listDataFileName,listDataFilePath);
            mRecyclerView.setAdapter(customAdapter);


            //new VideoCompressor().execute();
        }catch (Exception e){ e.printStackTrace(); }


    }

    public void setItemClickListener(ItemClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // Intent intent = new Intent(getApplicationContext(),SelectMenu.class);
       // startActivity(intent);
        finish();
    }

    private class VideoCompressor extends AsyncTask<ArrayList<String>, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(ArrayList<String>... params) {

            //listDataCompressFilePath = new ArrayList<String>();
            for (int i = 0; i < listDataFilePath.size(); i++) {
                String paths = listDataFilePath.get(i);

                if (paths.contains(".mp4")&&MediaController.getInstance().convertVideo(paths)) {
                    String path = MediaController.cachedFile.getPath();
                    listDataCompressFilePath.add(0, path);
                } else {
                    listDataCompressFilePath.add(i, listDataFilePath.get(i));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SPdialog2.dismiss();

            gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
            mRecyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
            //  call the constructor of CustomAdapter to send the reference and data to Adapter
            VideoListAdapter customAdapter = new VideoListAdapter(VideoListActivity.this, listDataFileName,listDataFilePath);
            mRecyclerView.setAdapter(customAdapter);
        }
    }

    private class GetVideoList extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SPdialog2 = new SpotsDialog(VideoListActivity.this);//, R.style.Custom
            SPdialog2.show();
            //Toast.makeText(PersonalReortActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            com.stavigilmonitoring.utility ut = new utility();

            String url = "http://sta.vritti.co/iMedia/STA_Announcement/DmCertificate.asmx/GetTrainingVideo?LangCode="+LangCode;
            //String jsonStr = sh.makeServiceCall(url);

            url = url.replaceAll(" ", "%20");
            Log.e("Report ", "Videolist url: " + url);
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

            if(jsonStr.contains("[]")){
                //no videos Dialog message sho
                SPdialog2.dismiss();

                Dialog dialog;
                try {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(VideoListActivity.this);
                    builder.setTitle("");
                    builder.setMessage("No videos available"
                            /*"(Note: In playstore 'OPEN' button is visible instead of 'UPDATE', Uninstall and Install app)"*/);

                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //background.start();
                            dialog.dismiss();
                            finish();
                        }
                    });

                    builder.setCancelable(false);
                    dialog = builder.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray Alert = jsonObj.getJSONArray("data");

                    prepareListData(Alert);

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


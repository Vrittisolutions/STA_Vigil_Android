package com.stavigilmonitoring;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adapters.AdvStationListAdapter;
import com.beanclasses.AdvFirstPlayClipRprt;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static com.stavigilmonitoring.WorkAssign_AssignActivity.Year;
import static com.stavigilmonitoring.WorkAssign_AssignActivity.day;
import static com.stavigilmonitoring.WorkAssign_AssignActivity.month;

public class Adv_StationsList extends Activity {
    private Context parent;

    TextView hdrnetwrks;
    GridView lst1playrprtstn;
    ImageView mRefresh;
    ProgressBar mprogress;
    String intntFrom, Network, ClipNo, Stationname;
    ArrayList<AdvFirstPlayClipRprt> listStations;
    String trnselectDate = "", SelectedDate = "", DateToPass = "";
    AdvStationListAdapter advAdapter;
    String sop;
    String responsemsg = "k";
    private String DateToStr;
    String mobno;
    utility ut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_adv_stations_list);

        init();

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();

        //fetchData();
        new DownloadxmlsDataURL_new().execute();

        setListeners();
    }

    public void init(){
        parent = Adv_StationsList.this;

        mRefresh = (ImageView) findViewById(R.id.imgbtnrfrsh);
        mprogress = (ProgressBar) findViewById(R.id.progressBar1);

        hdrnetwrks = (TextView)findViewById(R.id.hdrnetwrks);
        lst1playrprtstn = findViewById(R.id.lst1playrprtstn);

        listStations = new ArrayList<AdvFirstPlayClipRprt>();
        ut = new utility();

        Intent intent = getIntent();
        intntFrom = intent.getStringExtra("CallFrom");
        Network = intent.getStringExtra("Network");
        ClipNo = intent.getStringExtra("ClipNo");

        hdrnetwrks.setText("First Play Report"+" - "+Network+" - "+ClipNo);
    }

    public void setListeners(){

        mRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (ut.isnet(getApplicationContext())) {
                    //fetchData();
                    new DownloadxmlsDataURL_new().execute();
                } else {
                    ut.showD(Adv_StationsList.this, "nonet");
                }
            }
        });

            lst1playrprtstn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String InstallationName = listStations.get(position).getStationName();
                    String InstallationID = listStations.get(position).getInstallationID();

                        EditBox_Date(Network, InstallationName,InstallationID, ClipNo);

                }
            });
    }

    protected void EditBox_Date(final String NetworkCode, final String Stationname, final String InstallationID, final String ClipNo) {
        // TODO Auto-generated method stub

        final Dialog myDialog = new Dialog(Adv_StationsList.this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.dialogdatepicker);
        myDialog.setCancelable(true);
        // myDialog.getWindow().setGravity(Gravity.BOTTOM);

        TextView txt = (TextView) myDialog.findViewById(R.id.txtmsg);
        txt.setText(NetworkCode + " - " + Stationname + " - " + ClipNo);
        final Button btndate = (Button) myDialog.findViewById(R.id.btndate);

        final Button btn = (Button) myDialog .findViewById(R.id.gotodcliptls);

        btndate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //intent call

                Date date = new Date();
                final Calendar c = Calendar.getInstance();

                Year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(parent,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker datePicker, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox

                                btndate.setText(dayOfMonth + " - "
                                        + (monthOfYear + 1) + " - " + year);
                                trnselectDate = year + " - " + (monthOfYear + 1)  + " - " + dayOfMonth+ " 00:00:00.000";

                                String seldate = trnselectDate;
                                SelectedDate = (dayOfMonth + " - " + (monthOfYear + 1) + " - " + year);
                                DateToPass = (year+"-"+(monthOfYear + 1)+"-"+dayOfMonth);

                            }
                        }, Year, month, day);
                datePickerDialog.show();
            }
        });

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String btnval = btndate.getText().toString();

                    if(SelectedDate.equalsIgnoreCase("") || SelectedDate.equalsIgnoreCase(null)){
                        Toast.makeText(parent,"Please select date",Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(parent, AdvTimingDtlsReport.class);
                        intent.putExtra("CallFrom",intntFrom);
                        intent.putExtra("Network",NetworkCode);
                        intent.putExtra("ClipNo",ClipNo);
                        intent.putExtra("Stationname",Stationname);
                        intent.putExtra("SelectedDate",dateconvert(DateToPass));
                        intent.putExtra("DateToPass",DateToPass);
                        intent.putExtra("InstallationID",InstallationID);
                        startActivity(intent);

                        myDialog.dismiss();
                    }
                }
            });

        myDialog.show();
    }

    public class DownloadxmlsDataURL_new extends
            AsyncTask<String, Void, String> {
        String InstalationId, InstalationName, NetworkCode, WaveFileName, PlayTimeFrom, FirstTimePlay, IsTransfer,
                Remarks,Date, Time, AmPm;
        String count;
        Date date1;
        boolean IsDownload;

        @Override
        protected String doInBackground(String... params) {
            listStations.clear();
            utility ut = new utility();

            sop = "valid";

            String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetAdvFirstPlayReport" +
                    "?networkcode="+Network+"&clipid="+ClipNo+"&mobileno="+mobno;

                Log.e("csn status", "url : " + url);
                url = url.replaceAll(" ", "%20");

                try {
                    responsemsg = ut.httpGet(url);

                    if (responsemsg.contains("<InstalationId>")) {
                        sop = "valid";
                        NodeList nl1 = ut.getnode(responsemsg, "TableResult");

                        for (int i = 0; i < nl1.getLength(); i++) {

                            Element e = (Element) nl1.item(i);

                            InstalationId = ut.getValue(e,"InstalationId");
                            NetworkCode = ut.getValue(e,"NetworkCode");
                            InstalationName = ut.getValue(e,"InstalationName");
                            WaveFileName = ut.getValue(e,"WaveFileName");
                            PlayTimeFrom = ut.getValue(e,"PlayTimeFrom");
                            FirstTimePlay = ut.getValue(e,"FirstTimePlay");
                            IsTransfer = ut.getValue(e,"IsTransfer");
                            Remarks = ut.getValue(e,"Remarks");

                            AdvFirstPlayClipRprt advitem = new AdvFirstPlayClipRprt();
                            advitem.setInstallationID(InstalationId);
                            advitem.setStationName(InstalationName);
                            advitem.setFileName(WaveFileName);

                            listStations.add(advitem);

                            Collections.sort(listStations, new Comparator<AdvFirstPlayClipRprt>() {
                                public int compare(AdvFirstPlayClipRprt o1, AdvFirstPlayClipRprt o2) {
                                    if (o1.getStationName() == null || o2.getStationName() == null)
                                        return 0;
                                    return o2.getStationName().compareTo(o1.getStationName());
                                }
                            });

                         /*   Collections.sort(listStations, new Comparator<StateList>() {
                                public int compare(StateList o1, StateList o2) {
                                    if (o1.getDdate() == null || o2.getDdate() == null)
                                        return 0;
                                    return o2.getDdate().compareTo(o1.getDdate());
                                }
                            });*/
                        }

                    } else {
                        sop = "invalid";
                        System.out
                                .println("--------- invalid for project list --- ");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            return sop;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (sop.equals("valid")) {
                    //display list to adapter
                    advAdapter = new AdvStationListAdapter(parent, listStations);
                    lst1playrprtstn.setAdapter(advAdapter);

                } else {
                    ut.showD(parent,"nodata");
                }
                mRefresh.setVisibility(View.VISIBLE);
                mprogress.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRefresh.setVisibility(View.GONE);
            mprogress.setVisibility(View.VISIBLE);
        }
    }

    public String dateconvert(String Date_to_convert){

        SimpleDateFormat Format = new SimpleDateFormat("dd MMM yyyy");//Feb 23 2016 12:16PM
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        //SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        SimpleDateFormat toFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date d1 = null;

        try {
            d1 = format.parse(Date_to_convert);
            //DateToStr = toFormat.format(date);
            DateToStr = Format.format(d1);
            System.out.println(DateToStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateToStr;
    }

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {

        }

        return outputDate;

    }

    public String dateconvert_1(String Date_to_convert){ //5/31/2019 1:10:33 PM

        SimpleDateFormat Format = new SimpleDateFormat("dd MMM yyyy");//Feb 23 2016 12:16PM
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        //SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat toFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date d1 = null;

        try {
            //d1 = format.parse(DoAck);
            d1 = format.parse(Date_to_convert);
            //DateToStr = toFormat.format(date);
            DateToStr = Format.format(d1);
            System.out.println(DateToStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateToStr;
    }
}

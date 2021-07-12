package com.stavigilmonitoring;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.database.DBInterface;
import com.google.android.gms.wearable.Asset;

public class SendReqDispMaterial extends AppCompatActivity {
    private Context parent;
    Button btndispatch;
    TextView txtassetnum,txtmaterial,txtstation;
    EditText editTextQty;
    RadioButton radbtn_wh,radbtn_station;
    DatabaseHandler db;
    String mobno;
    String Stationid = "", MaterialID = "";
    String Optionnew = "", InstallationId = "",MaterialName = "",AssetNum = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send_req_disp_material);

        init();

        setListeners();
    }

    public void init(){
        parent = SendReqDispMaterial.this;

        db = new DatabaseHandler(getApplicationContext());
        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();

        txtmaterial = findViewById(R.id.txtmaterial);
        txtstation = findViewById(R.id.txtstation);
        btndispatch = findViewById(R.id.btndispatch);
        radbtn_station = findViewById(R.id.radbtn_station);
        radbtn_wh = findViewById(R.id.radbtn_wh);
        txtassetnum = findViewById(R.id.txtassetnum);
        editTextQty = findViewById(R.id.editTextQty);

        Intent i = getIntent();
        Bundle extras = getIntent().getExtras();
        Optionnew = extras.getString("Option");
        InstallationId = extras.getString("InstallationId");
        MaterialName = extras.getString("ItemName");
        AssetNum = extras.getString("SerialNum");

        txtmaterial.setText(MaterialName);
        txtassetnum.setText(AssetNum);

    }

    public void setListeners(){

        txtstation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SendReqDispMaterial.this, MaterialReqStatewiseActivity.class);
                intent.putExtra("mobileno", mobno);
                startActivityForResult(intent, Common.MaterialStn1);
            }
        });

        radbtn_wh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radbtn_wh.setChecked(true);
                radbtn_station.setChecked(false);
                txtstation.setVisibility(View.GONE);
            }
        });

        radbtn_station.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radbtn_wh.setChecked(false);
                radbtn_station.setChecked(true);
                txtstation.setVisibility(View.VISIBLE);
            }
        });

        btndispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send request same as material request
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == Common.MaterialStn1) {
                String MaterialStation = data.getStringExtra("StatioName");
                Stationid=data.getStringExtra("StatioNameID");
                txtstation.setText(MaterialStation);
            }else if (requestCode == Common.MaterialName) {
                MaterialID = data.getStringExtra("MaterialID");
                txtmaterial.setText(data.getStringExtra("MaterialName"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

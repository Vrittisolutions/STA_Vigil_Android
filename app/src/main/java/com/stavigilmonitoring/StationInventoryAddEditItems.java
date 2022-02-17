package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.database.DBInterface;
import com.stavigilmonitoring.utility;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class StationInventoryAddEditItems extends Activity {
	String Optionnew, ItemName, SerialNum, Remark, ItemSrNo, InventoryId;
	static String mobno, InstallationId, AddedBy, IsDeleted;
	private TextView tvhead;
	DownloadxmlsDataURL_new asyncfetch_csnstate;
	static SimpleDateFormat dff;
	com.stavigilmonitoring.utility ut;
	static String Ldate;
	String responsemsg = "k";
	private Button BtnSave, BtnRtn, BtnItemName;
	private ImageView BtnDelete;
	private EditText EdtSrNo, EdtRemark;
	String Retvalue="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.inventinvtryaddedititem);
		
		tvhead = (TextView) findViewById(com.stavigilmonitoring.R.id.stationInvent);
		BtnItemName = (Button) findViewById(com.stavigilmonitoring.R.id.editTextItemName);
		BtnSave = (Button) findViewById(com.stavigilmonitoring.R.id.button_save);
		EdtSrNo = (EditText) findViewById(com.stavigilmonitoring.R.id.editTextSRNo);
		EdtRemark = (EditText) findViewById(com.stavigilmonitoring.R.id.editTextRemark);
		BtnRtn = (Button) findViewById(com.stavigilmonitoring.R.id.button_return);
		BtnDelete = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_invent_delete);
		
		Intent i = getIntent();
		Bundle extras = getIntent().getExtras();
		Optionnew = extras.getString("Option");
		InstallationId = extras.getString("InstallationId");
		
		if (Optionnew.equalsIgnoreCase("Add")){
			try{
			BtnDelete.setVisibility(View.GONE);	
			BtnSave.setVisibility(View.VISIBLE);
			BtnRtn.setText("Cancel");
			tvhead.setText("Add Inventory");
			IsDeleted = "N";
			InventoryId = "0";
			}
			catch(Exception e){
				e.printStackTrace();
			}					
		}
		else if (Optionnew.equalsIgnoreCase("Edit")){
			try{
			tvhead.setText("Edit Inventory");
			BtnRtn.setText("Cancel");
			IsDeleted = "N";
			BtnDelete.setVisibility(View.VISIBLE);
			BtnSave.setVisibility(View.VISIBLE);
			InventoryId = extras.getString("InventoryId");
			ItemName = extras.getString("ItemName");
			SerialNum = extras.getString("SerialNum");
			Remark = extras.getString("Remark");
			BtnItemName.setText(ItemName);EdtSrNo.setText(SerialNum);EdtRemark.setText(Remark);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		BtnRtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				setResult(Common.InvtAddEdit, intent);
				StationInventoryAddEditItems.this.finish();				
			}
		});
		
		BtnItemName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(StationInventoryAddEditItems.this,
						StationInventoryItemNameListActivity.class);
				startActivityForResult(intent,Common.InvtItemName);
				
			}
		});
		
		BtnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isvalid()) {

					ItemName = BtnItemName.getText().toString().trim();
					ItemSrNo = EdtSrNo.getText().toString().trim();
					Remark = EdtRemark.getText().toString().trim();
					DBInterface dbi = new DBInterface(getApplicationContext());
					mobno = dbi.GetPhno();
					
					
					if (isnet()) {

						asyncfetch_csnstate = new DownloadxmlsDataURL_new();
						asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

					} else {
						showD("nonet");
					}
					
					Intent intent = new Intent();
					setResult(Common.InvtAddEdit, intent);
					StationInventoryAddEditItems.this.finish();
				}

				
					
			}
		});
		
				BtnDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDDel();				
			}
		});
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Common.InvtItemName) {
			BtnItemName.setText(data.getStringExtra("MaterialName"));
		}		
	}
	
	
	protected boolean isvalid() {
		// TODO Auto-generated method stub
		if (!(BtnItemName.getText().toString().length() > 0)) {
			Toast.makeText(StationInventoryAddEditItems.this, "Please Select Item Name",
					Toast.LENGTH_LONG).show();
			return false;
		} else if (!(EdtSrNo.getText().toString().length() > 0)) {
			Toast.makeText(StationInventoryAddEditItems.this, "Please Enter Serial No.",
					Toast.LENGTH_LONG).show();
			return false;
		} else if (!(EdtRemark.getText().toString().length() > 0)) {
			Toast.makeText(StationInventoryAddEditItems.this,
					"Please Enter Remark", Toast.LENGTH_LONG)
					.show();
			return false;
		}else{
			return true;
			}
		/*else if (ButtonStationNmae.getText().toString().length() > 0) {
			stationid = getStationId(ButtonStationNmae.getText().toString()
					.trim());
		}*/
		
	}


	public class DownloadxmlsDataURL_new extends AsyncTask<String, Void, String> {

@Override
protected String doInBackground(String... params) {
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();

	String url;

	url ="http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/AddSTAInventory?"+"ItemName="
			+ ItemName
			+ "&ItemSrNo="
			+ ItemSrNo
			+ "&AddedBy="
			+ ""
			+ "&InstallationId="
			+ InstallationId
			+ "&Mobile="
			+ mobno
			+ "&Remarks="
			+ Remark
			+ "&IsDeleted="
			+ IsDeleted
			+ "&InventoryId="
			+ InventoryId;
	
	
		Log.e("material ", "url : " + url);
		url = url.replaceAll(" ", "%20");
	try {
		System.out.println("-------  activity url --- " + url);
		responsemsg = ut.httpGet(url);

		System.out.println("-------------  xx vale-- " + responsemsg);

		responsemsg = responsemsg
				.substring(responsemsg.indexOf(">") + 1);
		responsemsg = responsemsg
				.substring(responsemsg.indexOf(">") + 1);
		responsemsg = responsemsg
				.substring(0, responsemsg.indexOf("<"));

	} catch (NullPointerException e) {
		responsemsg = "Error";
		e.printStackTrace();
		dff = new SimpleDateFormat("HH:mm:ss");
		Ldate = dff.format(new Date());

		StackTraceElement l = new Exception().getStackTrace()[0];
		System.out.println(l.getClassName() + "/" + l.getMethodName()
				+ ":" + l.getLineNumber());
		ut = new com.stavigilmonitoring.utility();
		if (!ut.checkErrLogFile()) {

			ut.ErrLogFile();
		}
		if (ut.checkErrLogFile()) {
			ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
					+ ":" + l.getLineNumber() + "	" + e.getMessage()
					+ " " + Ldate);
		}

	} catch (IOException e) {
		e.printStackTrace();

		responsemsg = "Error";
		dff = new SimpleDateFormat("HH:mm:ss");
		Ldate = dff.format(new Date());

		StackTraceElement l = new Exception().getStackTrace()[0];
		System.out.println(l.getClassName() + "/" + l.getMethodName()
				+ ":" + l.getLineNumber());
		ut = new com.stavigilmonitoring.utility();
		if (!ut.checkErrLogFile()) {

			ut.ErrLogFile();
		}
		if (ut.checkErrLogFile()) {
			ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
					+ ":" + l.getLineNumber() + "	" + e.getMessage()
					+ " " + Ldate);
		}

	}

	return responsemsg;
}
@Override
protected void onPostExecute(String result) {
	super.onPostExecute(result);
	try {
		if (responsemsg.equals("Error")) {
			showD("Error");
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.GONE);
			// Toast.makeText(MaterialRequest.this,
			// "Server Error...Please try after some time",
			// Toast.LENGTH_LONG).show();
		} else {
			// updateNotification(true);
			showD("Done");
			// Toast.makeText(MaterialRequest.this,
			// "Material request send successfully",
			// Toast.LENGTH_LONG).show();
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.GONE);
			// ButtonMaterialName.setText("");
			// ButtonMaterialReqTO.setText("");
			// edittextQty.setText("");
			// editTextRepair.setText("");
			// ButtonReason.setText("");
		}
	} catch (Exception e) {
		e.printStackTrace();
		dff = new SimpleDateFormat("HH:mm:ss");
		Ldate = dff.format(new Date());

		StackTraceElement l = new Exception().getStackTrace()[0];
		System.out.println(l.getClassName() + "/" + l.getMethodName()
				+ ":" + l.getLineNumber());
		ut = new utility();
		if (!ut.checkErrLogFile()) {

			ut.ErrLogFile();
		}
		if (ut.checkErrLogFile()) {
			ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
					+ ":" + l.getLineNumber() + "	" + e.getMessage()
					+ " " + Ldate);
		}

	}

}

@Override
protected void onPreExecute() {
	super.onPreExecute();
	// iv.setVisibility(View.GONE);
	/*((ProgressBar) findViewById(R.id.progressBar1))
			.setVisibility(View.VISIBLE);*/
}

}
	private boolean isnet() {
		Context context = this.getApplicationContext();
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(StationInventoryAddEditItems.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);

		TextView txt = (TextView) myDialog
				.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);
		if (string.equals("empty")) {
			myDialog.setTitle("Error...");
			txt.setText("Please Fill required data..");
		} else if (string.equals("nonet")) {
			myDialog.setTitle("Error...");
			txt.setText("No Internet Connection Found.Please Activate internet Connectin on Device..");
		} else if (string.equals("invalid")) {
			myDialog.setTitle(" ");
			txt.setText("No Refresh Data Available.Please check internet connection...");
		} else if (string.equals("Error")) {
			myDialog.setTitle(" ");
			txt.setText("Server Error.. Please try after some time");
		} else if (string.equals("Done")) {
			myDialog.setTitle(" ");
			txt.setText("Material request send successfully");
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

	protected void showDDel() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

	    builder.setTitle("Confirm");
	    builder.setMessage("Do You Want to Delete..?");

	    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

	        public void onClick(DialogInterface dialog, int which) {
	            // Do nothing but close the dialog
	        	// Delete Record from Inventory...
				IsDeleted = "Y";
				if (isvalid()) {

					ItemName = BtnItemName.getText().toString().trim();
					ItemSrNo = EdtSrNo.getText().toString().trim();
					Remark = EdtRemark.getText().toString().trim();
					DBInterface dbi = new DBInterface(getApplicationContext());
					mobno = dbi.GetPhno();					
					
					if (isnet()) {
						asyncfetch_csnstate = new DownloadxmlsDataURL_new();
						asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

					} else {
						showD("nonet");
					}
					
					Intent intent = new Intent();
					setResult(Common.InvtAddEdit, intent);
					StationInventoryAddEditItems.this.finish();
				}


	            dialog.dismiss();
	        }
	    });

	    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

	        @Override
	        public void onClick(DialogInterface dialog, int which) {

	            // Do nothing	        	
	            dialog.dismiss();
	            Intent intent = new Intent();
				setResult(Common.InvtAddEdit, intent);
				StationInventoryAddEditItems.this.finish();
	        }
	    });

	    AlertDialog alert = builder.create();
	    alert.show();
		

		/*TextView txt = (TextView) myDialog
				.findViewById(R.id.dialogdeleteitemtext);
		
		Button btnNO = (Button) myDialog
				.findViewById(R.id.gotobtndialoginfosmallNO);
		btnNO.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();
				Retvalue = "No";
				
				// finish();

			}
		});
		Button btnYES = (Button) myDialog
				.findViewById(R.id.gotobtndialoginfosmallNO);
		btnYES.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				
				Retvalue = "YES";
				
								
				// finish();

			}
		});

		myDialog.show();*/
		

	}



}

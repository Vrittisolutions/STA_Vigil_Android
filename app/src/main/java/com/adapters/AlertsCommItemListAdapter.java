package com.adapters;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import com.database.DBInterface;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.beanclasses.AlertsCommItemBean;
import com.stavigilmonitoring.AlrtDetailsWithCommentsActivity;
import com.stavigilmonitoring.Common;
import com.stavigilmonitoring.DatabaseHandler;
import com.stavigilmonitoring.utility;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlertsCommItemListAdapter extends BaseAdapter {
	private ArrayList<AlertsCommItemBean> list;
	Date date;
	private Context context;
	private LayoutInflater mInflater;
	static SimpleDateFormat dff;
	static String Ldate;
	private ArrayList<AlertsCommItemBean> arraylist;
	public String txtcmdAddedDt, cmdAddedBy, Status, imgComment, date1, time1;
	
	private String mobno, responsemsg = "k", sop;
	public String AlertMob =null, Mobno=null, AlertId =null, ResolveBy = null, ResolveDt= null, ConfirmBy = null, ConfirmDt =null, InstallationId= null, RejectedBy =null;
	private static AcceptNRejectURL async;
	private static AlrtDetailsWithCommentsActivity alrtDetailsWithCommentsActivity;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();

	public AlertsCommItemListAdapter(Context parent,
			ArrayList<AlertsCommItemBean> alertsCommItemBeanlist) {
		this.context = parent;
		//this.list.clear();
		this.list = alertsCommItemBeanlist;

		mInflater = LayoutInflater.from(parent);
		//mInflater = (LayoutInflater) parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		DatabaseHandler db = new DatabaseHandler(parent);
		DBInterface dbi = new DBInterface(parent);
		mobno = dbi.GetPhno();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Log.e("Size",String.valueOf(list.size()));
		return list.size();
	}
	
	@Override
	public int getItemViewType(int position) {
	  return position;
	}
	
	@Override
    public int getViewTypeCount() {
        return getCount();
    }

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		int type =0;// getItemViewType(position);
		cmdAddedBy = list.get(position).getcmdAddedBy();
		Status = list.get(position).getStatus();
		imgComment = list.get(position).getCommentDescription();

		if (convertView == null) {
			if (Status.equals("1")){
			convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.alertcommentitem_bubble_tag,	null);
			type =1;
			Log.e("bubble","tag");
				
			}else if(Status.equals("0")){
				if(cmdAddedBy.equals(Common.UserName)){
					//holder.bubble_layout.setBackgroundResource(R.drawable.outgoing_bubble);
					convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.alertcommentitem_bubble_out,	null);
					type =2;
					Log.e("bubble","out");
				}else if(!(cmdAddedBy.equals(Common.UserName))){
					convertView = mInflater.inflate(com.stavigilmonitoring.R.layout.alertcommentitem_bubble,	null);
					type =3;
					Log.e("bubble","in");
				}
			}			
			 
			   holder = new ViewHolder();
			   if (type == 1){
				   holder.tag_bubble_layout = (LinearLayout) convertView.findViewById(com.stavigilmonitoring.R.id.bubble_layout_tag);
					holder.txtcmntby = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtcmntby_tag_part1);
				   holder.cmntDT = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtcmntby_tag_part2);
					
			   }else if(type == 2){			   
				holder.bubble_layout = (LinearLayout) convertView.findViewById(com.stavigilmonitoring.R.id.bubble_layout_out);
				holder.txtcmntby = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtcmntby_out);
				holder.cmntDesc = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.cmntDesc_out);
				holder.cmntDT = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.cmntDT_out);
				holder.layouttxt2 = (LinearLayout) convertView.findViewById(com.stavigilmonitoring.R.id.layouttxt2_out);

				   holder.layoutimg1_out = (LinearLayout) convertView.findViewById(com.stavigilmonitoring.R.id.layoutimg1_out);
				   holder.img1_out = (ImageView) convertView.findViewById(com.stavigilmonitoring.R.id.img1_out);
				
			   }else if(type == 3){			   
					holder.bubble_layout = (LinearLayout) convertView.findViewById(com.stavigilmonitoring.R.id.bubble_layout);
					holder.txtcmntby = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtcmntby);
					holder.cmntDesc = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.cmntDesc);
					holder.cmntDT = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.cmntDT);
					holder.layouttxt2 = (LinearLayout) convertView.findViewById(com.stavigilmonitoring.R.id.layouttxt2);

				   holder.layoutimg1_in = (LinearLayout) convertView.findViewById(com.stavigilmonitoring.R.id.layoutimg1_in);
				   holder.img2_in = (ImageView) convertView.findViewById(com.stavigilmonitoring.R.id.img2_in);
					
			   }
			   
			   convertView.setTag(holder);	
			  
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		if (type == 1){
			holder.txtcmntby.setText(list.get(position).getCommentDescription()+" "+list.get(position).getcmdAddedBy());
			txtcmdAddedDt = list.get(position).getcmdAddedDt();
			holder.cmntDT.setText("on " +makedate(txtcmdAddedDt));
			//holder.txtcmntby.setText(list.get(position).getCommentDescription()+" "+list.get(position).getcmdAddedBy());
		}else if(type == 2){
			try{
				holder.txtcmntby.setText(list.get(position).getcmdAddedBy());
				InstallationId = list.get(position).getInstallationId();
				AlertId = list.get(position).getAlertId();

				txtcmdAddedDt = list.get(position).getcmdAddedDt();
				holder.cmntDT.setText(makedate(txtcmdAddedDt));

				if (imgComment.contains("ALERTIMGALERT")){
					String[] parts = imgComment.split("ALERTIMG");
					if(parts[0].equals("")){ holder.cmntDesc.setVisibility(View.GONE); }
					else { holder.cmntDesc.setText(parts[0]);}
					//holder.cmntDesc.setText(list.get(position).getCommentDescription()); }
					String[] words= parts[1].split(list.get(position).getcmdAddedBy());
					String[] word = words[1].split("jpg");
					String date = word[0].substring(0, 8);
					String time = word[0].substring(8, 14);
					String ImageUrl = "http://ktc.vritti.co/Attachments/"+
							"ALERT_"+AlertId+"_"+list.get(position).getcmdAddedBy()+"_"+date+"_"+time+".jpg";
				/*String ImageUrl = "http://ktc.vritti.co/AlertImages/"+
						"ALERT_"+AlertId+"_"+list.get(position).getcmdAddedBy()+"_"+date+"_"+time+".jpg";*/
					holder.layoutimg1_out.setVisibility(View.VISIBLE);

					Picasso.with(context).load(ImageUrl).placeholder(com.stavigilmonitoring.R.drawable.progressanimation)
							.error(com.stavigilmonitoring.R.drawable.no_image)
							.resize(700, 700)
							//.transform(new BlurTransformation(context))
							.into(holder.img1_out,new com.squareup.picasso.Callback(){
								@Override
								public void onSuccess() {

								}
								@Override
								public void onError() {

								}
							});
					holder.img1_out.setTag(position);
					holder.img1_out.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(final View v) {
							// TODO Auto-generated method stub
							String[] parts = list.get(position).getCommentDescription().split("ALERTIMG");
							String[] words= parts[1].split(list.get(position).getcmdAddedBy());
							String[] word = words[1].split("jpg");
							String date = word[0].substring(0, 8);
							String time = word[0].substring(8, 14);
							String fileUrl = "http://ktc.vritti.co/Attachments/"+
									"ALERT_"+AlertId+"_"+list.get(position).getcmdAddedBy()+"_"+date+"_"+time+".jpg";
						/*String fileUrl  = "http://ktc.vritti.co/AlertImages/"+
								"ALERT_"+AlertId+"_"+list.get(position).getcmdAddedBy()+"_"+date+"_"+time+".jpg";*/
							final String url = fileUrl;
							if(context instanceof AlrtDetailsWithCommentsActivity){
								((AlrtDetailsWithCommentsActivity)context).toggle_icon_received_status(url);
							}
						}
					});
				} else {
					holder.layoutimg1_out.setVisibility(View.GONE);
					holder.cmntDesc.setText(list.get(position).getCommentDescription());
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}else if(type == 3){
			try{
				holder.txtcmntby.setText(list.get(position).getcmdAddedBy());
				InstallationId = list.get(position).getInstallationId();
				AlertId = list.get(position).getAlertId();
				//holder.cmntDesc.setText(list.get(position).getCommentDescription());
				txtcmdAddedDt = list.get(position).getcmdAddedDt();
				holder.cmntDT.setText(makedate(txtcmdAddedDt));

				if (imgComment.contains("ALERTIMGALERT")){
					String[] parts = imgComment.split("ALERTIMG");
					if(parts[0].equals("")){ holder.cmntDesc.setVisibility(View.GONE); }
					else { holder.cmntDesc.setText(parts[0]); }
					//holder.cmntDesc.setText(list.get(position).getCommentDescription()); }
					String AddedBy = (list.get(position).getcmdAddedBy()).trim();
					if(AddedBy.contains(".")){
						AddedBy = AddedBy.replace(".","");
					}
					String[] words= parts[1].split(AddedBy);
					String[] word = words[1].split("jpg");
					final String date = word[0].substring(0, 8);
					final String time = word[0].substring(8, 14);

					String ImageUrl = "http://ktc.vritti.co/Attachments/"+
							"ALERT_"+AlertId+"_"+list.get(position).getcmdAddedBy()+"_"+date+"_"+time+".jpg";
				/*final String ImageUrl = "http://ktc.vritti.co/AlertImages/"+
						"ALERT_"+AlertId+"_"+AddedBy+"_"+date+"_"+time+".jpg";*/
					holder.layoutimg1_in.setVisibility(View.VISIBLE);

					Picasso.with(context).load(ImageUrl).placeholder(com.stavigilmonitoring.R.drawable.progressanimation)
							.error(com.stavigilmonitoring.R.drawable.no_image)
							.resize(700, 700)
							.into(holder.img2_in,new com.squareup.picasso.Callback(){
								@Override
								public void onSuccess() {

								}
								@Override
								public void onError() {

								}
							});
					holder.img2_in.setTag(position);
					holder.img2_in.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(final View v) {
							// TODO Auto-generated method stub
							String[] parts = list.get(position).getCommentDescription().split("ALERTIMG");
							String[] words= parts[1].split(list.get(position).getcmdAddedBy());
							String[] word = words[1].split("jpg");
							String date = word[0].substring(0, 8);
							String time = word[0].substring(8, 14);
							String fileUrl  = "http://ktc.vritti.co/Attachments/"+
									"ALERT_"+AlertId+"_"+list.get(position).getcmdAddedBy()+"_"+date+"_"+time+".jpg";
							final String url = fileUrl;
							if(context instanceof AlrtDetailsWithCommentsActivity){
								((AlrtDetailsWithCommentsActivity)context).toggle_icon_received_status(url);
							}
						}
					});
				} else {
					holder.layoutimg1_in.setVisibility(View.GONE);
					holder.cmntDesc.setText(list.get(position).getCommentDescription());
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	      
		return convertView;
	}
		
		private String makedate(String olddate) {
		// TODO Auto-generated method stub
			DateFormat originalFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
			DateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aa");			
			try {
				date = originalFormat.parse(olddate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String newdate = targetFormat.format(date);
		return newdate;
	}

		static class ViewHolder {
			public TextView txtcmntby;
			public TextView cmntDesc;
			public TextView cmntDT;
			public LinearLayout layouttxt2;
			public LinearLayout bubble_layout;
			public LinearLayout tag_bubble_layout;
			public LinearLayout layoutimg1_out;
			public LinearLayout layoutimg1_in;
			public ImageView img2_in;
			public ImageView img1_out;
		}	

		protected void showD(String string) {
			// TODO Auto-generated method stub

			final Dialog myDialog = new Dialog(context);
			myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
			myDialog.setCancelable(true);
			// myDialog.getWindow().setGravity(Gravity.BOTTOM);

			TextView txt = (TextView) myDialog.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);
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
				txt.setText("Alert send successfully");
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

		public class AcceptNRejectURL extends AsyncTask<String, Void, String>{

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
				DatabaseHandler db = new DatabaseHandler(context);
				SQLiteDatabase sql = db.getWritableDatabase();
				String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/AlertApproveAndRejected?AlertId="
				+AlertId
				+"&ResolveBy="
				+ ResolveBy
				+"&ResolveDt="
				+ ""
				+"&ConfirmBy="
				+ ConfirmBy
				+"&ConfirmDt="
				+ ""
				+"&InstallationId="
				+ InstallationId
				+"&RejectedBy="
				+ RejectedBy;

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
				} else if (responsemsg.equals("Not saved")) {
					showD("Error");
				} else {					
					showD("Done");
					//alrtDetailsWithCommentsActivity.CommunicationGetURL();
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
				/*btnrefresh.setVisibility(View.GONE);
				mprogressBar.setVisibility(View.VISIBLE);*/
			}		
		}

	    public class BlurTransformation implements Transformation {

		RenderScript rs;

		public BlurTransformation(Context context) {
			super();
			rs = RenderScript.create(context);
		}

		@Override
		public Bitmap transform(Bitmap bitmap) {


			// Create another bitmap that will hold the results of the filter.
			Bitmap blurredBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {

			// Allocate memory for Renderscript to work with
			Allocation input = Allocation.createFromBitmap(rs, blurredBitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
			Allocation output = Allocation.createTyped(rs, input.getType());

			// Load up an instance of the specific script that we want to use.    Element.U8_4(rs)
			ScriptIntrinsicBlur script = null;
				script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

			script.setInput(input);

			// Set the blur radius
			script.setRadius(20);

			// Start the ScriptIntrinisicBlur
			script.forEach(output);

			// Copy the output to the blurred bitmap
			output.copyTo(blurredBitmap);

			bitmap.recycle();
			}

			return blurredBitmap;
		}

		@Override
		public String key() {
			return "blur";
		}
	}
}

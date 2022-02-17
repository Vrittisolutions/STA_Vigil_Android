package com.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beanclasses.StateDetailsList;
import com.stavigilmonitoring.DmCSoNoStateStnDetails;
import com.stavigilmonitoring.DmCStateStnSoNoDetails;
import com.stavigilmonitoring.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VideoPhotoStateDetailsAdapter extends BaseAdapter {
	private static List<StateDetailsList> searchArrayList;
	//int pos;
	private ArrayList<StateDetailsList> arraylist;
	private LayoutInflater mInflater;
	Context context;
	private ViewHolder holder;
	private int position;
	String type="";

	public VideoPhotoStateDetailsAdapter(Context c, List<StateDetailsList> data)
	{
		context=c;
		mInflater = LayoutInflater.from(context);
		searchArrayList=data;
		arraylist=new ArrayList<StateDetailsList>();
		arraylist.addAll(data);
	}

	public VideoPhotoStateDetailsAdapter(Context c, List<StateDetailsList> data, String type1) {

		context=c;
		mInflater = LayoutInflater.from(context);
		searchArrayList=data;
		arraylist=new ArrayList<StateDetailsList>();
		arraylist.addAll(data);
		this.type = type1;

	}

	public void filter_details(String charText) {
		//charText =charText.toLowerCase(Locale.getDefault());
		searchArrayList.clear();
		if(charText.length()==0)
		{
			searchArrayList.addAll(arraylist);
		}
		else
		{
			for (StateDetailsList wp : arraylist)
			{
				if (wp.GetDMDesc().toLowerCase(Locale.getDefault()).contains(charText))
				{
					searchArrayList.add(wp);
				}
			}
		}
		//notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return searchArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return searchArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		//pos = position;
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		//ViewHolder holder;
		//pos= position;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.custom_videostnwisedetails, null);
			holder = new ViewHolder();
			holder.colorll = (LinearLayout) convertView.findViewById(com.stavigilmonitoring.R.id.colorll);
			holder.txtdmdesc = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtdmdesc);
			holder.btnpdfdownload = (ImageView) convertView.findViewById(com.stavigilmonitoring.R.id.btnpdfdownload);
		//	holder.btnpdfdownload.setImageResource(com.stavigilmonitoring.R.drawable.pdfdwnld);
			holder.btnpdfdownload.setTag(holder);
			holder.btnpdfdownload.setVisibility(View.GONE);

			holder.txtstatus = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtstatus);
			holder.txttitle = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txttitle);
			holder.txtstartdt = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtstartdt);
			holder.txtenddt = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtenddt);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txtdmdesc.setText(searchArrayList.get(position).GetDMDesc());
		holder.txtstatus.setText(searchArrayList.get(position).Getdmcstatus());
		boolean overduechk = checkOverdue(searchArrayList.get(position).GetActualEndDate());

		if (overduechk == true){
			//holder.colorll.setBackgroundColor(Color.parseColor("#ff7f7f"));
			holder.colorll.setBackgroundColor(Color.parseColor("#ffe5e5"));
		}

		holder.txtstartdt.setText(""+searchArrayList.get(position).GetActualStartDate());
		holder.txtenddt.setText("-  "+searchArrayList.get(position).GetActualEndDate());
		//holder.txttitle.setText(""+searchArrayList.get(position).GetActualEndDate());
		try{
			String[] file_Name = searchArrayList.get(position).GetGenrateFileName().split("/"); //KSRTCMandyaKASGSO19-20-010-08-09-2019.pdf
			String[] fName1 = file_Name[5].split("-");
			String endDate = fName1[3]+"-"+fName1[4]+"-"+fName1[5].split("\\.")[0];
			holder.txttitle.setText(ConvertDate(endDate));
			holder.btnpdfdownload.setTag(position);
		}catch (Exception e){
			e.printStackTrace();
		}

		/*String filedwnldstatus = searchArrayList.get(position).getIsFileDownload();

		if(!filedwnldstatus.equalsIgnoreCase("true") ||
				!filedwnldstatus.equalsIgnoreCase("") ||
				!filedwnldstatus.equalsIgnoreCase(null)){
			holder.btnpdfdownload.setImageResource(R.drawable.pdfdwnld);
		}else {
			holder.btnpdfdownload.setImageResource(R.drawable.dwnldedpdf);
		}*/

		holder.btnpdfdownload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try{
				//	int pos=(Integer)v.getTag();
					String filename = searchArrayList.get(position).GetGenrateFileName();
					if (filename.equalsIgnoreCase("No File Found")||filename.equals("")){
						Toast.makeText(context,"No File Found",Toast.LENGTH_SHORT).show();
					} else {
						//String fileName = "PPO-16-17-418.pdf";
						//String fileUrl = "http://www.androhub.com/demo/demo.pdf";
						String fileUrl = filename;
						String parts[] = filename.split("/");
						String fileName = parts[5];		//old = HSRTCNuhHRSGSO19-20-045 , new = HSRTCNuhHRSGSO19-20-045-20-01-2020
						//Toast.makeText(context,filename,Toast.LENGTH_SHORT).show();

						if(type != "" && type == "SoWise"){
							((DmCSoNoStateStnDetails)context).downloadAttachment(searchArrayList.get(position),fileUrl,fileName);
						}else{
							((DmCStateStnSoNoDetails)context).downloadAttachment(searchArrayList.get(position),fileUrl,fileName);
						}


						//new DownloadFile().execute(fileUrl, fileName);
					/*if (filename.contains("/MSRTC")) {
						String fileUrlMar = fileUrl.replace(".pdf", ".htm");
						String fileNameMar = fileName.replace(".pdf", ".htm");
						new DownloadFile().execute(fileUrlMar, fileNameMar);
					}*/
					}
				}catch (Exception e){
					e.printStackTrace();
					Toast.makeText(context,"No File Found",Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		return convertView;
	}

	static class ViewHolder {
		TextView txtdmdesc,txtstatus,txttitle;
		TextView txtstartdt, txtenddt;
		LinearLayout colorll;
		ImageView btnpdfdownload;
	}

	private String ConvertDate(String amcExpireDt) {
		String result= null;
		// 2017-10-30T00:00:00+05:30
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM,yyyy");
		try {
			Date date2 = dateFormat1.parse(amcExpireDt);
			result = dateFormat2.format(date2);
		}catch( Exception e){
			e.printStackTrace();
		}
		return result;
	}

	private boolean checkOverdue(String amcExpireDt) {
		String[] parts = amcExpireDt.split("T");
		amcExpireDt = parts[0];
		boolean result = false;
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM,yyyy");
		try {
			Date date2 = dateFormat2.parse(amcExpireDt);
			Date date = new Date();
			String res = dateFormat2.format(date);
			date = dateFormat2.parse(res);
			if (date2.equals(date)){
				result = false;
			} else if (date.after(date2)){
				result = true;
			} else if (date2.after(date)){
				result = false;
			}
		}catch( Exception e){
			e.printStackTrace();
		}
		return result;
	}

	private class DownloadFile extends AsyncTask<String, Void, Void> {
		String dwnlod;

		@Override
		protected Void doInBackground(String... strings) {
			String fileUrl = strings[0];   // -> http://www.androhub.com/demo/demo.pdf
			//fileUrl = "http://vritti.ekatm.co.in//certificatepdfs/MSRTCAmbajogaiPSOIM17-18-716.htm";
			String[] word = fileUrl.split("/");
			fileUrl = "https:"+"//vritti.ekatm.co.in/"+word[4]+"/"+word[5]; //word[0]
			String fileName = strings[1];  // -> demo.pdf
			//fileName = "MSRTCAmbajogaiPSOIM17-18-716.htm";
			String[] words = fileName.split("\\.");
			fileName = words[0];
			//fileName = "MarDemo";
			String suffix = words[1];
			//suffix = "htm";

			File storageDir = new File(Environment.getExternalStorageDirectory(), "DMCertificatepdf");
			if (!storageDir.exists()){  // Checks that Directory/Folder Doesn't Exists!
				storageDir.mkdir();
			}

			File pdfdown = new File(storageDir+"/"+fileName+"."+suffix);
			try {
				//pdfdown = File. createTempFile( fileName /* prefix */,".jpg", storageDir  /* directory */ );
				pdfdown.createNewFile();


			    dwnlod = downloadFile(fileUrl, pdfdown);
			   //DownloadMP3(fileUrl, Environment.getExternalStorageDirectory()+"/DMCertificatepdf/"+fileName+"."+suffix);

			} catch (IOException e) {
				dwnlod = "No File Found";
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			Toast.makeText(context,dwnlod,Toast.LENGTH_SHORT).show();

			/*if(!dwnlod.equalsIgnoreCase("No File Found")){
				holder.btnpdfdownload.setImageResource(R.drawable.dwnldedpdf);
				searchArrayList.get(position).setIsFileDownload(true);
			}else {
				searchArrayList.get(position).setIsFileDownload(false);
			}*/
		}

		private static final int MEGABYTE = 1024 * 1024;

		public String downloadFile(String fileUrl, File directory) {
			String isdownload;
			try {
				URL url = new URL(fileUrl);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				//urlConnection.setDoOutput(true);
				//urlConnection.connect();

				InputStream inputStream = urlConnection.getInputStream();
				FileOutputStream fileOutputStream = new FileOutputStream(directory);
				int totalSize = urlConnection.getContentLength();

				byte[] buffer = new byte[MEGABYTE];
				int bufferLength = 0;
				while ((bufferLength = inputStream.read(buffer,0,1024)) > 0) {
					fileOutputStream.write(buffer, 0, bufferLength);
				}
				fileOutputStream.close();

				isdownload =  "File Downloaded Successfully";
				//holder.btnpdfdownload.setImageResource(R.drawable.dwnldedpdf);
				//set file downloaded icon
                //set status of filedownload
			} catch (FileNotFoundException e) {
				isdownload =  "No File Found";
				e.printStackTrace();
			} catch (MalformedURLException e) {
				isdownload =  "No File Found";
				e.printStackTrace();
			} catch (IOException e) {
				isdownload =  "No File Found";
				e.printStackTrace();
			} catch (Exception e) {
				isdownload =  "No File Found";
				e.printStackTrace();
			}
			return isdownload;
		}
	}

	public void DownloadMP3(final String URL, final String Path) {
		final int[] downloaded = {0};


		new Thread(new Runnable() {
			public void run() {
				Boolean flag = false;
				try {

					java.net.URL url;
					url = new URL(URL);
					Log.e("In PlaylistDownload", "" + url);
					String path = Path;
					File file = new File(path);
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setRequestMethod("HEAD");
					long size = connection.getContentLength();
					connection.disconnect();
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					long filelengh = file.length();
					if (file.exists() && file.length() >= (size - 5000)) {
						Log.d("File already downloaded", file.getName());
					} else if (file.exists() && file.length() < size) {

						downloaded[0] = (int) file.length();

						connection.setRequestProperty("Range", "bytes=" + downloaded[0]
								+ "-" + size);
						flag = true;

					} else if (!file.exists()) {
						connection.setRequestProperty("Range", "bytes=" + downloaded[0]
								+ "-" + size);
						flag = true;
					}
					if (flag) {
						connection.setDoInput(true);
						connection.setDoOutput(true);

						BufferedInputStream in = new BufferedInputStream(
								url.openStream());
						FileOutputStream fos = (downloaded[0] == 0) ? new FileOutputStream(
								path) : new FileOutputStream(path, true);
						// FileOutputStream out=new FileOutputStream(Path);
						BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);

						byte[] data1 = new byte[1024];
						int x = 0;

						while ((x = in.read(data1, 0, 1024)) >= 0) {
							bout.write(data1, 0, x);
							downloaded[0] += x;

						}

						in.close();
						fos.flush();
						fos.close();
					}
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}).start();
	}

}



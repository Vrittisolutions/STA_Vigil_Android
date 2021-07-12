package com.stavigilmonitoring;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
import com.stavigilmonitoring.R;
import com.stavigilmonitoring.utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class BarChart extends Activity {
	ProgressDialog pd;
	//AsyncTask depattask;
	com.stavigilmonitoring.utility ut = new utility();
	String sop = "no";
	String tf="";
	String tf1="";
	String responsemsg = "k";
	/*private String[] mMonth = new String[] {
				"Jan", "Feb" , "Mar", "Apr", "May", "Jun",
				"Jul", "Aug" , "Sep", "Oct", "Nov", "Dec"
			};*/
	
	private String stnnAme;
	String z1;
	private String ztf1="";
	String z2;

	String[] axixdate;
	double[] axixMin;
	DatabaseHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barchart);      

        Bundle extras = getIntent().getExtras();
		stnnAme = extras.getString("stnname");

		db = new DatabaseHandler(getBaseContext());

		 openChart();        
    }

    @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		Bundle dataBundle = new Bundle();
		dataBundle.putString("stnname", stnnAme);
		//dataBundle.putString("ActivityName", ActivityName);
		/*Intent i = new Intent(BarChart.this, Downtime.class);
	//	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtras(dataBundle);
		startActivity(i);*/
		finish();

	}
   
	 private void openChart(){	    
	    	//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String[] params = new String[1];
			params[0] = stnnAme;
			Cursor cursor = sql.rawQuery(
					"SELECT * FROM Downtime where InstallationDesc=? ORDER BY StationDownTime DESC ", params);

			
			if (cursor.getCount() == 0) {
				cursor.close();
				/*sql.close();
				db1.close();*/
			} else {
				cursor.moveToFirst();
				//int column = 0;
				axixdate=new String[cursor.getCount()];
				axixMin=new double[cursor.getCount()];
				int j=0;
				do {
					int column1 = cursor.getColumnIndex("StationDownTime");
					tf = cursor.getString(column1);
					String []sp=tf.split(":");
//					int i=Integer.parseInt(sp[0])*60+Integer.parseInt(sp[1]);
//					String tfc=String.valueOf(i);
//					z=tfc+","+z;
//					z1=z;
//					//z1=z1.replace(',', ' ').trim();
//					
					//System.out.println("..........value of tf for z1----------------"+z1+"---i: "+i);
					int column2 = cursor.getColumnIndex("AddedDate");
					
				//	tf1 = tf1+","+cursor.getString(column2);
					tf1 = cursor.getString(column2);	
					ztf1=tf1+","+ztf1;
					z2=ztf1;				
					String sdate=tf1.substring(0, tf1.indexOf(" "));
					axixdate[j]=sdate.substring(0, sdate.lastIndexOf("/"));					
					String hr=sp[0]+"."+sp[1]+"";					
					axixMin[j]=Double.valueOf(hr);
					j++;

				} while (cursor.moveToNext());

				cursor.close();
				/*sql.close();
				db1.close();*/
			}
	    	    XYMultipleSeriesRenderer renderer = buildBarRenderer();
			    renderer.setOrientation(Orientation.VERTICAL);
			    setChartSettings(renderer, "Downtime for 2014", "Month", "", 0.5,
			        12.5, 0, 16, Color.WHITE, Color.WHITE);
			    renderer.setXLabels(1);
			    renderer.setYLabels(10);
			    renderer.setYLabelsPadding(15);
			    renderer.setXLabelsPadding(15);
			    for(int y=0;y<axixdate.length;y++)
			    {
			    	renderer.addXTextLabel(y, axixdate[y]+"      ");
			    }

			    renderer.setApplyBackgroundColor(true);
			    renderer.setChartValuesTextSize(20);
			    renderer.setLegendHeight(10);
			    
			    renderer.setBackgroundColor(Color.BLACK);
			    Typeface typeFace=Typeface.createFromAsset(getAssets(),"font/BOOKOS.TTF");
			    renderer.setTextTypeface(typeFace);
			    renderer.setBarSpacing(0.2);		   
			    renderer.setLabelsColor(Color.rgb(100, 175, 255));
			 
			    int length = renderer.getSeriesRendererCount();
			    for (int i = 0; i < length; i++) {
			      SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(i);
			      seriesRenderer.setDisplayChartValues(true);
			     // seriesRenderer.setch
			      seriesRenderer.setGradientEnabled(true);
			      seriesRenderer.setGradientStart(0, Color.RED);
			      seriesRenderer.setGradientStop(50, Color.CYAN);
			      seriesRenderer.setChartValuesSpacing(1);			      
			    }
			
			    XYSeries incomeSeries = new XYSeries("DownTime");
			 
	            for (int i = 0; i < axixMin.length; i++) {
	                incomeSeries.add(i, axixMin[i]);
	            }

			    XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	            mDataset.addSeries(incomeSeries);
	            renderer.setPanEnabled(false);
	            renderer.setYAxisMin(0);
	            renderer.setYAxisMax(16);
			    GraphicalView view=ChartFactory.getBarChartView(getBaseContext(), mDataset, renderer,  Type.DEFAULT);
			//startActivity(intt);
			
			((LinearLayout)findViewById(R.id.llchart)).addView(view, new LayoutParams(
	                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			
	    	
	    }
	 protected XYMultipleSeriesRenderer buildBarRenderer(){
		    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		    renderer.setAxisTitleTextSize(28);
		   
		    renderer.setChartTitleTextSize(30);
		    renderer.setLabelsTextSize(25);
		    renderer.setLabelsColor(Color.WHITE);
		    renderer.setLegendTextSize(25);
		  
		    //renderer.setLabelsColor(Color.YELLOW);	   
		      SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		      r.setColor( Color.rgb(100, 175, 255));
		    
		      renderer.addSeriesRenderer(r);	 
		      renderer.setMargins(new int[] {10, 15, 60, 15});
		    renderer.setLegendHeight(20);
		    return renderer;
		  }
		protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
			      String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
			      int labelsColor) {
			    renderer.setChartTitle(title);
			    renderer.setXTitle(xTitle);
			    renderer.setYTitle(yTitle);
			    renderer.setXAxisMin(xMin);
			    renderer.setXAxisMax(xMax);
			    renderer.setYAxisMin(yMin);
			    renderer.setYAxisMax(yMax);
			    renderer.setAxesColor(axesColor);
			    renderer.setLabelsColor(labelsColor);
			    renderer.setLabelsTextSize(17);
			    renderer.setXLabelsPadding(10);
			    renderer.setYLabelsPadding(14);
			    renderer.setPanEnabled(false, false);
			   
			  }
	 
}
package com.stavigilmonitoring;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.provider.CalendarContract.CalendarCache.URI;

public class FileHelper {

    static Calendar c = Calendar.getInstance();
    static SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_YYYY");
    static String addedDt = sdf.format(c.getTime());

    final static String fileName = addedDt+"_stvisit.txt";
    final static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/StationVisitForms/" ;
    final static String TAG = FileHelper.class.getName();

    public static  String ReadTextFile( Context context){
        String line = null;

        try {
            FileInputStream fileInputStream = new FileInputStream (new File(path + fileName));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ( (line = bufferedReader.readLine()) != null )
            {
                stringBuilder.append(line + System.getProperty("line.separator"));
            }
            fileInputStream.close();
            line = stringBuilder.toString();

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        }
        catch(IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
        return line;
    }

    public static /*boolean*/String[] saveTextFile(String data, String Station, String senderName, String Network){

        String file_namepath = "";

        try {
            new File(path).mkdir();
            File file = new File(path + Station+"_"+fileName);
            file_namepath = Station+"_"+fileName;
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file,true);
/*
          //data  = "|----------------------------------------------------------------------------------------------------\n";
            data  = "|____________________________________________________________________________________________________|\n";
            data += "|                                      Data Sheet Report                                             |\n";
            data += "|"+"                          "+"Station Inspection Form By Supporter"+"                          "+"|\n\n";

            if (Network.length() <= 17) {
                int diff = 17 - Network.length();
                if (diff > 17) {
                    for (int i = 0; i < diff; i++) {
                        Network = Network + " ";
                    }
                }
            }

            if (Station.length() <= 28) {
                int diff = 28 - Station.length();
                if (diff > 28) {
                    for (int i = 0; i < diff; i++) {
                        Station = Station + " ";
                    }
                }
            }

            if (senderName.length() <= 30) {
                int diff = 30 - senderName.length();
                if (diff > 30) {
                    for (int i = 0; i < diff; i++) {
                        senderName = senderName + " ";
                    }
                }
            }

            data += "|Network :  "+Network+"   Station :  "+Station+"               Supporter :  "+senderName+"           |\n";
            data += "|____________________________________________________________________________________________________|\n";
            data += "|  No.|  Question                                          |Answer              |Remarks             |\n";
            data += "|----------------------------------------------------------------------------------------------------|\n";
        //  data += "|  No.|  Question                                          |Answer              |Remarks             |\n";
            //for loop of questions and answers
            data += "|----------------------------------------------------------------------------------------------------|\n";
            data += "|____________________________________________________________________________________________________|\n";
    */

            data = "\n  Respected sir, \n";
            data +="\t\t Please find attached PDF file of Station Inspection Form submitted by  "+senderName+"\n";
            data +="\t\t Network : "+Network+"\n\t\t Station : "+Station+"\n";
            data += "\n\n\n";
            data += "\t Thanks & regards  \n";
            data += "\t "+senderName+"     \n";
            data += "\t Via STA Vigil      \n";

            fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());

            String arr[] = {data, file_namepath};
            return arr;

            //return data;
        }  catch(FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        }  catch(IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
        String arr[] = {data, file_namepath};
        return arr;
       // return  false;
    }

}

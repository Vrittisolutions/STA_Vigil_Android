package com.stavigilmonitoring;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;

public class MyDialog extends Activity {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            try {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("New Update Available");
                builder.setMessage(" New STA Vigil " + Common.PSVersion + " is on Playstore." +
                        "\n(Note: In playstore 'OPEN' button is visible instead of 'UPDATE', Uninstall and Install app)");

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                                ("market://details?id=com.stavigilmonitoring")));
                        Common.dialogopen = "no";
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //background.start();
                        Common.dialogopen = "no";
                        dialog.dismiss();
                    }
                });


                builder.setCancelable(false);


                dialog = builder.show();
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);


                Common.dialogopen = "yes";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}

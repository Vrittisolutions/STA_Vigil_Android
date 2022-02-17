package com.stavigilmonitoring;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by Admin-3 on 1/4/2018.
 */

public class PlayStoreVersionDialogActivity extends Activity{
    Dialog dialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        String PlaystoreVersion = i.getStringExtra("PlayStoreVersion");
        showUpdateDialog(PlaystoreVersion);
    }

    private void showUpdateDialog(String PSVersion){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Update Available");
        builder.setMessage(" New STA Vigil "+PSVersion+" is on Playstore."
				/*"(Note: In playstore 'OPEN' button is visible instead of 'UPDATE', Uninstall and Install app)"*/);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("market://details?id=com.stavigilmonitoring")));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //background.start();
            }
        });

        builder.setCancelable(false);
        dialog = builder.show();
    }

}

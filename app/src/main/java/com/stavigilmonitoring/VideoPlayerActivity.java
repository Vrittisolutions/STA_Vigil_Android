package com.stavigilmonitoring;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.VideoView;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class VideoPlayerActivity extends Activity {
    VideoView video;
    String video_url = "http://file2.video9.in/english/movie/2014/x-men-_days_of_future_past/X-Men-%20Days%20of%20Future%20Past%20Trailer%20-%20[Webmusic.IN].3gp";
    ProgressDialog pd;
    String FileName, VideoPath, ImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.activity_tut_video);

        onNewIntent(getIntent());
        Bundle extras = getIntent().getExtras();
        FileName = extras.getString("FileName");
        VideoPath = extras.getString("VideoPath");
        ImagePath = extras.getString("ImagePath");

        //video = (VideoView)findViewById(R.id.vv);

        JzvdStd jzvdStd = (JzvdStd) findViewById(com.stavigilmonitoring.R.id.vv);
        jzvdStd.setUp(VideoPath,
               FileName,
                Jzvd.SCREEN_WINDOW_NORMAL);
        //jzvdStd.thumbImageView.setImage();

        /*pd = new ProgressDialog(VideoPlayerActivity.this);
        pd.setMessage("Buffering video please wait...");
        pd.show();

        Uri uri = Uri.parse(video_url);
        video.setVideoURI(uri);
        video.start();

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //close the progress dialog when buffering is done
                pd.dismiss();
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }
}
package com.new_development;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.services.JobService_SyncDataCount;

import java.util.List;

public  class Util_New {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(Context context) {
        @SuppressLint("JobSchedulerService")
        ComponentName serviceComponent = new ComponentName(context, JobService_SyncDataCount.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(30 * 1000); // Wait at least 30s
        builder.setOverrideDeadline(60 * 1000); // Maximum delay 60s

        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());

       List jobs= jobScheduler.getAllPendingJobs();

        Log.e("PENDING_JOBS "," --> "+jobs);
    }

}

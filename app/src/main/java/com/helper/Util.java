package com.helper;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.services.JobService_Test;

public class Util {
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
			return;

		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
				MeasureSpec.UNSPECIFIED);
		int totalHeight = 0;
		View view = null;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			view = listAdapter.getView(i, view, listView);
			if (i == 0)
				view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
						LayoutParams.WRAP_CONTENT));

			view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += view.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public static void scheduleJob(Context context) {

		ComponentName serviceComponent = new ComponentName(context, JobService_Test.class);
		JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
		builder.setMinimumLatency(1 * 1000); // wait at least
		builder.setOverrideDeadline(3 * 1000); // maximum delay
		//builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
		//builder.setRequiresDeviceIdle(true); // device should be idle
		//builder.setRequiresCharging(false); // we don't care if the device is charging or not
		JobScheduler jobScheduler = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			jobScheduler = context.getSystemService(JobScheduler.class);
		}
		jobScheduler.schedule(builder.build());
    }
}

package org.mockup.wvuta;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {

	private static final String TAG = "WVUTA::UPDATESERVICE";
	private ReportReceiver reportReceiver;
	private TweetReceiver tweetReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "UpdateService onCreate");
		
		IntentFilter filter = new IntentFilter(ReportService.REPORTS);
		reportReceiver = new ReportReceiver();
		registerReceiver(reportReceiver, filter);

		filter = new IntentFilter(TweetService.TWEETS);
		registerReceiver(tweetReceiver, filter);

		Log.d(TAG, "Starting ReportService");
		Intent intent = new Intent(this, ReportService.class);
		startService(intent);
		super.onCreate();
	}
	
	

	@Override
	public void onDestroy() {
		Log.d(TAG, "UpdateService onDestroy");
	}



	private class ReportReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Starting TweetService");
			Intent i = new Intent(UpdateService.this, TweetService.class);
			startService(i);
		}

	}

	private class TweetReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Shutting Down");
			UpdateService.this.unregisterReceiver(reportReceiver);
			UpdateService.this.unregisterReceiver(tweetReceiver);
			stopSelf();
		}

	}
}

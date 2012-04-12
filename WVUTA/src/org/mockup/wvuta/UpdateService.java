package org.mockup.wvuta;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {

	private static final String TAG = "WVUTA::UPDATESERVICE";
	public static final int NOTIF_ID = 26505;
	private ReportReceiver reportReceiver;
	private TweetReceiver tweetReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "UpdateService onCreate");

		// Register receiver for ReportService
		reportReceiver = new ReportReceiver();
		IntentFilter filter = new IntentFilter(ReportService.REPORTS);
		registerReceiver(reportReceiver, filter);

		// Register receiver for TweetService
		tweetReceiver = new TweetReceiver();
		IntentFilter filter1 = new IntentFilter(TweetService.TWEETS);
		registerReceiver(tweetReceiver, filter1);

		// Start ReportService
		Log.d(TAG, "Starting ReportService");
		Intent intent = new Intent(this, ReportService.class);
		startService(intent);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "UpdateService onDestroy");
		this.unregisterReceiver(reportReceiver);
		this.unregisterReceiver(tweetReceiver);
	}

	private class ReportReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Start TweetService
			Log.d(TAG, "Received report broadcast, starting TweetService");
			Intent i = new Intent(UpdateService.this, TweetService.class);
			startService(i);
		}
	}

	private class TweetReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Received tweet broadcast");
			// Post notification if new update found
			if (intent.getBooleanExtra("updated", false)) {
				Notification newTweet = new Notification(R.drawable.ya,
						"New PRT Update", System.currentTimeMillis());

				Intent start = new Intent(getApplicationContext(), Main.class);
				PendingIntent pending = PendingIntent.getActivity(
						getApplicationContext(), 0, start, 0);

				newTweet.setLatestEventInfo(UpdateService.this,
						"New PRT Update", "Touch to check status", pending);
				newTweet.defaults |= Notification.DEFAULT_VIBRATE;
				NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				nm.notify(NOTIF_ID, newTweet);
			}
			// Stop UpdateService
			stopSelf();
		}
	}
}

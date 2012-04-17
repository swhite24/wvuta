package org.mockup.wvuta;

import java.util.ArrayList;
import java.util.List;

import winterwell.jtwitter.Twitter;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class BusTweetService extends Service {

	private static final String TAG = "WVUTA::BUSTWEETSERVICE";
	public static final String BUSTWEETS = "org.mockup.wvuta.BUSTWEETSERVICE";
	public static ArrayList<String> tweets;
	private BusTweetTask btask;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "BusTweetService onCreate");
	}	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		btask = new BusTweetTask();
		btask.execute((Void[]) null);
		
		return super.onStartCommand(intent, flags, startId);
	}

	private void announceResults() {
		Log.d(TAG, "Broadcasting bus tweets");
		Intent i = new Intent(BUSTWEETS);
		sendBroadcast(i);
		stopSelf();
	}

	private class BusTweetTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			tweets = new ArrayList<String>();
			Twitter mtwitter = new Twitter();
			try {
				List<winterwell.jtwitter.Status> mTweets = mtwitter
						.getUserTimeline("MLticker");
				for (int i = 0; i < mTweets.size(); i++){
					Log.d(TAG, "tweet " + i + ": " + mTweets.get(i).toString());
					tweets.add(mTweets.get(i).toString());
				}
			} catch (Exception e) {
				Log.e(TAG, "Error retrieving bus tweets.");
				tweets.add("No MLticker tweets available");
			}
			announceResults();
			return null;
		}

	}
}

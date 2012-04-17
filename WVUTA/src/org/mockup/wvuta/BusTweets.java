package org.mockup.wvuta;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BusTweets extends Activity {

	private static final String TAG = "WVUTA::BUSTWEETS";
	private ListView tweets;
	private BusTweetReceiver bReceiver;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bustweets);

		Log.d(TAG, "BusTweets onCreate");

		tweets = (ListView) findViewById(R.id.bus_tweet_list);
	}

	@Override
	protected void onPause() {
		unregisterReceiver(bReceiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter(BusTweetService.BUSTWEETS);
		bReceiver = new BusTweetReceiver();
		registerReceiver(bReceiver, filter);
		getTweets();
		super.onResume();
	}

	private void getTweets() {
		ArrayList<String> temp = new ArrayList<String>();
		temp.add("Retrieving MLticker tweets...");
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, temp);
		tweets.setAdapter(adapter);
		Intent i = new Intent(this, BusTweetService.class);
		startService(i);
	}

	private class BusTweetReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Received bus tweets broadcast");

			adapter = new ArrayAdapter<String>(BusTweets.this,
					android.R.layout.simple_list_item_1, android.R.id.text1,
					BusTweetService.tweets);

			tweets.setAdapter(adapter);
		}
	}
}

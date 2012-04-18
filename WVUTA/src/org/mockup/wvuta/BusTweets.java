package org.mockup.wvuta;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class BusTweets extends Activity {

	private static final String TAG = "WVUTA::BUSTWEETS";
	private ListView tweets;
	private BusTweetReceiver bReceiver;
	private BusTweetAdapter adapter;
	private ArrayList<BusTweet> tweet_list = new ArrayList<BusTweet>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bustweets);

		Log.d(TAG, "BusTweets onCreate");

		tweets = (ListView) findViewById(R.id.bus_tweet_list);
		adapter = new BusTweetAdapter(this, R.layout.tweet_row, tweet_list);
		tweets.setAdapter(adapter);

		tweets.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {
				Log.d(TAG, "initemclick");
				AlertDialog.Builder adb = new AlertDialog.Builder(
						BusTweets.this);
				StringBuilder output = new StringBuilder();
				BusTweet source = (BusTweet) tweets.getItemAtPosition(position);
				output.append("Source:  MLticker\n");
				output.append("Message:  " + source.getTweet() + "\n");
				output.append("Time:  " + source.getTime() + "\n");
				adb.setTitle("Tweet Summary");
				adb.setMessage(output.toString());
				adb.setPositiveButton("Done", null);
				adb.show();
			}
		});
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
		tweet_list = new ArrayList<BusTweet>();
		tweet_list.add(new BusTweet("Retrieving MLticker tweets...", null));
		adapter = new BusTweetAdapter(this, R.layout.tweet_row, tweet_list);
		tweets.setAdapter(adapter);
		Intent i = new Intent(this, BusTweetService.class);
		startService(i);
	}

	private class BusTweetReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Received bus tweets broadcast");

			adapter = new BusTweetAdapter(BusTweets.this, R.layout.tweet_row,
					BusTweetService.tweets);

			tweets.setAdapter(adapter);
		}
	}
}

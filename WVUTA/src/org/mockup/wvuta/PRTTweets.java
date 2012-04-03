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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PRTTweets extends Activity {

	private static final String TAG = "WVUTA::PRTTWEETS";
	private final ArrayList<Report> tweetArray = new ArrayList<Report>();
	private ListView tweet_lv;
	private RowAdapter rowAdapter = null;
	private ViewGroup header = null;
	private TweetReceiver tweetreceiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "PRTTweets onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prttweets);

		tweet_lv = (ListView) findViewById(R.id.tweetList);

		rowAdapter = new RowAdapter(this, R.layout.rowcustom, tweetArray);
		addHeader();
		tweet_lv.setAdapter(rowAdapter);

		tweet_lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {
				if (position != 0) {
					AlertDialog.Builder adb = new AlertDialog.Builder(
							PRTTweets.this);
					StringBuilder output = new StringBuilder();
					Report source = (Report) tweet_lv
							.getItemAtPosition(position);
					output.append("Source:  " + source.getNote() + "\n");
					output.append("Location:  " + source.getLocation() + "\n");
					output.append("Status:  " + source.getStatus() + "\n");
					output.append("Time:  " + source.getTime() + "\n");
					adb.setTitle("Report Summary");
					adb.setMessage(output.toString());
					adb.setPositiveButton("Done", null);
					adb.show();
				}
			}
		});
	}

	@Override
	protected void onPause() {
		unregisterReceiver(tweetreceiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter(TweetService.TWEETS);
		tweetreceiver = new TweetReceiver();
		registerReceiver(tweetreceiver, filter);
		getTweets();
		super.onResume();
	}

	private void getTweets() {
		Log.d(TAG, "Retrieving WVUDOT tweets.");
		defaultText();
		Intent i = new Intent(this, TweetService.class);
		startService(i);
	}

	/**
	 * Sets text in ListView to something to let user know system is working
	 */
	private void defaultText() {
		tweetArray.clear();
		Report temp = new Report("...Retrieving data from server...", null,
				null, null);
		tweetArray.add(temp);
		rowAdapter = new RowAdapter(PRTTweets.this, R.layout.rowcustom,
				tweetArray);
		tweet_lv.setAdapter(rowAdapter);
	}

	/**
	 * Add headers to the three columns in ListView
	 */
	private void addHeader() {
		LayoutInflater inflater = getLayoutInflater();
		header = (ViewGroup) inflater.inflate(R.layout.listheader, tweet_lv,
				false);
		tweet_lv.addHeaderView(header);
	}

	private class TweetReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			Log.d(TAG, "Received tweet broadcast");

			rowAdapter = new RowAdapter(PRTTweets.this, R.layout.rowcustom,
					TweetService.all_tweets);
			tweet_lv.setAdapter(rowAdapter);
		}

	}

}

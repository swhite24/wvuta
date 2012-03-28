package org.mockup.wvuta;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

public class PRTTweets extends Activity{

	private static final String TAG = "WVUTA::PRTTWEETS";
	private final ArrayList<Report> tweetArray = new ArrayList<Report>();
	private ListView tweet_lv;
	private RowAdapter rowAdapter = null;
	private ViewGroup header = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		Log.d(TAG, "PRTTweets onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prttweets);	
		
		tweet_lv = (ListView) findViewById(R.id.tweetList);
		

		rowAdapter = new RowAdapter(this, R.layout.rowcustom, tweetArray);
		addHeader();
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
	

}

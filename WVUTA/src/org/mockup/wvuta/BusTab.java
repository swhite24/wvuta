package org.mockup.wvuta;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class BusTab extends TabActivity {

	private static final String TAG = "WVUTA::BUSTAB";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "BusTab onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.bustab);

		TabHost tab_host = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		// add each tab
		intent = new Intent().setClass(this, BusTweets.class);
		spec = tab_host.newTabSpec("Tweets").setIndicator("Tweets")
				.setContent(intent);
		tab_host.addTab(spec);

		intent = new Intent().setClass(this, BusMap.class);
		spec = tab_host.newTabSpec("Map").setIndicator("Map")
				.setContent(intent);
		tab_host.addTab(spec);
		
		tab_host.setCurrentTab(0);
	}

}

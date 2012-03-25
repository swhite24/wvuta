package org.mockup.wvuta;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class PRTTab extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prttab);

		TabHost tab_host = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		// setup each tab's title & content
		intent = new Intent().setClass(this, PRTSummary.class);
		spec = tab_host.newTabSpec("Summary").setIndicator("Summary", null)
				.setContent(intent);
		tab_host.addTab(spec);

		intent = new Intent().setClass(this, PRTReports.class);
		spec = tab_host.newTabSpec("Reports").setIndicator("Reports", null)
				.setContent(intent);
		tab_host.addTab(spec);

		intent = new Intent().setClass(this, PRTMap.class);
		spec = tab_host.newTabSpec("Map").setIndicator("Map", null)
				.setContent(intent);
		tab_host.addTab(spec);
		
		tab_host.setCurrentTab(0);
	}

}

package org.mockup.wvuta;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class BusMap extends Activity {

	private static final String TAG = "WVUTA::BUSMAP";
	private static final int LOADING_DIALOG = 0;
	private static final String MAP_URL = "http://site.busride.org/mybus/mybus.gif";
	private ProgressDialog progress_dialog;
	private ImageView map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.busmap);

		Log.d(TAG, "BusMap onCreate");

		map = (ImageView) findViewById(R.id.busmap_mapimg);
		loadMap();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case LOADING_DIALOG:
			progress_dialog = new ProgressDialog(this,
					ProgressDialog.STYLE_SPINNER);
			progress_dialog.setMessage("Loading Map");
			progress_dialog.setCancelable(false);
			return progress_dialog;
		default:
			return null;
		}
	}

	private void loadMap() {
		showDialog(LOADING_DIALOG);
		map.setImageDrawable(fetchMap());
		dismissDialog(LOADING_DIALOG);
	}

	private Drawable fetchMap() {
		try {
			URL url = new URL(MAP_URL);
			Object content = url.getContent();
			InputStream is = (InputStream) content;
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			Log.e(TAG, "MalformedURL: " + e.getMessage());
		} catch (IOException e){
			Log.e(TAG, "IOException: " + e.getMessage());
		}

		return null;
	}

}

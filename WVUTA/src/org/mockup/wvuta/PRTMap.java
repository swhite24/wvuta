package org.mockup.wvuta;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class PRTMap extends MapActivity {

	private MapView map = null;
	private MapController mc = null;
	private PRTStationOverlay overlay = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.prtmap);

		map = (MapView) findViewById(R.id.prt_map);
		Thread thread = new Thread(null, background, "background");
		thread.start();

	}
	
	private Runnable background = new Runnable() {
		public void run() {
			initMap();
		}
	};
	
	/**
	 * Add overlay for each PRT station.
	 */
	private void initMap() {
		map.setClickable(true);
		mc = map.getController();
		// points display on map
		GeoPoint beechurst = new GeoPoint(Constants.bLat.intValue(),
				Constants.bLon.intValue());
		GeoPoint engineering = new GeoPoint(Constants.eLat.intValue(),
				Constants.eLon.intValue());
		GeoPoint medical = new GeoPoint(Constants.mLat.intValue(),
				Constants.mLon.intValue());
		GeoPoint towers = new GeoPoint(Constants.tLat.intValue(),
				Constants.tLon.intValue());
		GeoPoint walnut = new GeoPoint(Constants.wLat.intValue(),
				Constants.wLon.intValue());

		// center of map
		int centerLat = (Constants.mLat.intValue() + Constants.wLat.intValue()) / 2;
		int centerLon = (Constants.eLon.intValue() + Constants.bLon.intValue()) / 2;
		mc.setCenter(new GeoPoint(centerLat, centerLon));
		mc.setZoom(15);

		// add points to overlay to be drawn
		Bitmap temp = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(temp);
		RectF to_draw = new RectF(0, 0, 10, 10);
		Paint backpaint = new Paint();
		backpaint.setARGB(175, 0, 0, 128);
		backpaint.setAntiAlias(true);
		canvas.drawOval(to_draw, backpaint);

		Drawable d = new BitmapDrawable(temp);

		overlay = new PRTStationOverlay(d, this);
		overlay.addPoint(new OverlayItem(beechurst, "Beechurst",
				"Beechurst Station Details"));
		overlay.addPoint(new OverlayItem(engineering, "Engineering",
				"Engineering station details"));
		overlay.addPoint(new OverlayItem(medical, "Medical",
				"Medical station details"));
		overlay.addPoint(new OverlayItem(towers, "Towers",
				"Towers station details"));
		overlay.addPoint(new OverlayItem(walnut, "Walnut",
				"Walnut station details"));

		// add overlay to map
		List<Overlay> list = map.getOverlays();
		list.add(overlay);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}

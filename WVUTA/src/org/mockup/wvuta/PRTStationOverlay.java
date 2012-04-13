package org.mockup.wvuta;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class PRTStationOverlay extends ItemizedOverlay<OverlayItem> {

	private int radius = 5;
	private ArrayList<OverlayItem> overlays = null;
	private Context context;

	public PRTStationOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		overlays = new ArrayList<OverlayItem>();
		this.context = context;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Projection projection = mapView.getProjection();

		if (!shadow) {
			Paint paint = new Paint();
			paint.setARGB(250, 255, 255, 0);
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);

			Paint backpaint = new Paint();
			backpaint.setARGB(175, 0, 0, 128);
			backpaint.setAntiAlias(true);

			Iterator<OverlayItem> iterator = overlays.iterator();
			int count = 0;
			while (iterator.hasNext()) {
				GeoPoint geoPoint = iterator.next().getPoint();
				Point point = new Point();
				projection.toPixels(geoPoint, point);

				RectF backrect = new RectF(point.x + 2 + radius, point.y - 3
						* radius + 1, point.x + 67, point.y + radius);

				canvas.drawRoundRect(backrect, 5, 5, backpaint);
				canvas.drawText(getName(count), point.x + 2 * radius, point.y,
						paint);
				count++;
			}
		}
		super.draw(canvas, mapView, shadow);
	}

	private String getName(int count) {
		switch (count) {
		case 0:
			return Constants.BEECHURST;
		case 1:
			return Constants.ENGINEERING;
		case 2:
			return Constants.MEDICAL;
		case 3:
			return Constants.TOWERS;
		case 4:
			return Constants.WALNUT;
		}
		return null;
	}

	@Override
	public boolean onTap(int index) {
		Log.d("Overlay", "recieved tap on index: " + index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(getName(index) + " Station");

		DBHelper dbhelper = new DBHelper(context.getApplicationContext());
		SQLiteDatabase db = dbhelper.getReadableDatabase();

		String[] cols = { Constants.LOCATION_COL, Constants.STATUS_COL,
				Constants.SOURCE_COL };
		String where = Constants.LOCATION_COL + " = ?";
		String[] where_args = {getName(index).toUpperCase()};

		Cursor cursor = db.query(Constants.TABLE_NAME, cols, where, where_args, null,
				null, null);
		
		cursor.moveToNext();		
		
		StringBuilder sb = new StringBuilder();
		sb.append("Status: " + cursor.getString(1) + "\n");
		sb.append("Source: " + cursor.getString(2));
		dialog.setMessage(sb.toString());
		dialog.setPositiveButton("Done", null);
		
		db.close();
		dbhelper.close();
		dialog.show();
		
		return true;
	}

	public void addPoint(OverlayItem point) {
		overlays.add(point);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}

}

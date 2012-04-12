package org.mockup.wvuta;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class ReportService extends Service {

	private ReportLookupTask task = null;
	public static final String REPORTS = "org.mockup.wvuta.REPORTS";
	private static final String TAG = "WVUTA::REPORTSERVICE";
	private final ArrayList<String> reportArray = new ArrayList<String>();
	private ArrayList<String> times = new ArrayList<String>();
	private boolean error = false;
	private DBHelper dbhelper;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "ReportService onCreate");
		dbhelper = new DBHelper(getApplicationContext());
		getDBInfo();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		getDBInfo();
		return Service.START_STICKY;

	}

	/**
	 * Starts a new ReportLookupTask with provided filters.
	 */
	private void getDBInfo() {
		// create ASyncTask to put server communication in background
		if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)) {
			task = new ReportLookupTask();
			task.execute((Void[]) null);
		}
	}

	/**
	 * Broadcasts results of latest reports.
	 */
	private void announceResults() {
		// broadcast results
		Log.d(TAG, "Broadcasting report retrieval results");
		Intent intent;
		intent = new Intent(REPORTS);
		intent.putStringArrayListExtra("strings", reportArray);
		if (!times.isEmpty()) {
			updateLatest();
		}
		dump_db();
		dbhelper.close();
		sendBroadcast(intent);
		stopSelf();
	}

	/**
	 * Updates DB with latest status and source for each station.
	 */
	private void updateLatest() {
		if (!error) {
			SQLiteDatabase db = dbhelper.getWritableDatabase();

			String beech_status = null;
			String eng_status = null;
			String med_status = null;
			String tow_status = null;
			String wal_status = null;
			String b_source = null, e_source = null, m_source = null;
			String t_source = null, w_source = null;
			String b_time = null, e_time = null, m_time = null;
			String t_time = null, w_time = null;

			Iterator<String> report_it = reportArray.iterator();
			Iterator<String> time_it = times.iterator();
			// Retrieve information for each report of the current day and pull
			// latest for each station.
			while (report_it.hasNext()) {
				String status = report_it.next();
				report_it.next();
				String location = report_it.next();
				String source = report_it.next();
				String time = time_it.next();
				if (beech_status == null
						&& location.equalsIgnoreCase("beechurst")) {
					beech_status = status;
					b_source = source;
					b_time = time;
				}
				if (eng_status == null
						&& location.equalsIgnoreCase("engineering")) {
					eng_status = status;
					e_source = source;
					e_time = time;
				}
				if (med_status == null && location.equalsIgnoreCase("medical")) {
					med_status = status;
					m_source = source;
					m_time = time;
				}
				if (tow_status == null && location.equalsIgnoreCase("towers")) {
					tow_status = status;
					t_source = source;
					t_time = time;
				}
				if (wal_status == null && location.equalsIgnoreCase("walnut")) {
					wal_status = status;
					w_source = source;
					w_time = time;
				}
			}

			Date b_date = null, e_date = null, m_date = null, t_date = null, w_date = null;
			try {
				b_date = b_time == null ? null : Constants.TWEETFORMAT
						.parse(b_time);
				e_date = e_time == null ? null : Constants.TWEETFORMAT
						.parse(e_time);
				m_date = m_time == null ? null : Constants.TWEETFORMAT
						.parse(m_time);
				t_date = t_time == null ? null : Constants.TWEETFORMAT
						.parse(t_time);
				w_date = w_time == null ? null : Constants.TWEETFORMAT
						.parse(w_time);
			} catch (ParseException e) {
				Log.e(TAG, "Couldn't parse dates. top");
			}

			Date beech = null, eng = null, tow = null, med = null, wal = null;
			try {
				String[] cols = { Constants.LOCATION_COL, Constants.TIME_COL };
				String orderBy = Constants.LOCATION_COL + " ASC";
				Cursor times = db.query(Constants.TABLE_NAME, cols, null, null,
						null, null, orderBy);
				
				
				times.moveToNext();
				beech = Constants.TWEETFORMAT.parse(times.getString(1));				

				times.moveToNext();
				eng = Constants.TWEETFORMAT.parse(times.getString(1));

				times.moveToNext();
				med = Constants.TWEETFORMAT.parse(times.getString(1));

				times.moveToNext();
				tow = Constants.TWEETFORMAT.parse(times.getString(1));

				times.moveToNext();
				wal = Constants.TWEETFORMAT.parse(times.getString(1));
			} catch (Exception e) {
				Log.e(TAG, "Couldn't parse dates. bottom");
			}

			ContentValues values = new ContentValues();
			if (b_date != null && beech != null && b_date.after(beech)) {
				if (beech_status != null) {
					values.put(Constants.LOCATION_COL, "BEECHURST");
					values.put(Constants.STATUS_COL, beech_status);
					values.put(Constants.SOURCE_COL, b_source);
					values.put(Constants.TIME_COL, b_time);
					db.replace(Constants.TABLE_NAME, null, values);
					Log.d(TAG, "Updated BEECHURST");
				}
			}
			values = new ContentValues();
			if (e_date != null && eng != null && e_date.after(eng)) {
				if (eng_status != null) {
					values.put(Constants.LOCATION_COL, "ENGINEERING");
					values.put(Constants.STATUS_COL, eng_status);
					values.put(Constants.SOURCE_COL, e_source);
					values.put(Constants.TIME_COL, e_time);
					db.replace(Constants.TABLE_NAME, null, values);
					Log.d(TAG, "Updated ENGINEERING");
				}
			}
			values = new ContentValues();
			if (m_date != null && med != null && m_date.after(med)) {
				if (med_status != null) {
					values.put(Constants.LOCATION_COL, "MEDICAL");
					values.put(Constants.STATUS_COL, med_status);
					values.put(Constants.SOURCE_COL, m_source);
					values.put(Constants.TIME_COL, m_time);
					db.replace(Constants.TABLE_NAME, null, values);
					Log.d(TAG, "Updated MEDICAL");
				}
			}
			values = new ContentValues();
			if (t_date != null && tow != null && t_date.after(tow)) {
				if (tow_status != null) {
					values.put(Constants.LOCATION_COL, "TOWERS");
					values.put(Constants.STATUS_COL, tow_status);
					values.put(Constants.SOURCE_COL, t_source);
					values.put(Constants.TIME_COL, t_time);
					db.replace(Constants.TABLE_NAME, null, values);
					Log.d(TAG, "Updated TOWERS");
				}
			}
			values = new ContentValues();
			if (w_date != null && wal != null && w_date.after(wal)) {
				if (wal_status != null) {
					values.put(Constants.LOCATION_COL, "WALNUT");
					values.put(Constants.STATUS_COL, wal_status);
					values.put(Constants.SOURCE_COL, w_source);
					values.put(Constants.TIME_COL, w_time);
					db.replace(Constants.TABLE_NAME, null, values);
					Log.d(TAG, "Updated WALNUT");
				}
			}
			Log.d(TAG, "updated DB with reports");
			db.close();
		}
	}

	/**
	 * Show current contents of DB
	 */
	private void dump_db() {
		Log.d(TAG, "Dumping db");
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		String[] FROM = { Constants.LOCATION_COL, Constants.STATUS_COL,
				Constants.TIME_COL, Constants.SOURCE_COL };
		String orderBy = Constants.LOCATION_COL + " ASC";
		Cursor cursor = db.query(Constants.TABLE_NAME, FROM, null, null, null,
				null, orderBy);

		while (cursor.moveToNext()) {
			String location = cursor.getString(0);
			String status = cursor.getString(1);
			String time = cursor.getString(2);
			String source = cursor.getString(3);
			Log.d(TAG, "ROW: " + location + ", " + status + ", " + time + ", "
					+ source);
		}
		db.close();
	}

	private class ReportLookupTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			InputStream inStream = null;
			StringBuilder builder = new StringBuilder();
			StringBuilder output = new StringBuilder();
			String result = "";

			SharedPreferences prefs = getSharedPreferences(Constants.TABLE_NAME,
					Context.MODE_PRIVATE);

			// put filters, null or not, into ArrayList to send to server
			ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
			al.add(new BasicNameValuePair("location", prefs.getString(
					"location", null)));
			al.add(new BasicNameValuePair("status", prefs.getString("status",
					null)));

			// connect
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						"http://www.prtstatus.com/retrieveFilter.php");
				httppost.setEntity(new UrlEncodedFormEntity(al));
				HttpResponse httpresponse = httpclient.execute(httppost);
				HttpEntity entity = httpresponse.getEntity();
				inStream = entity.getContent();
			} catch (Exception e) {
				Log.e(TAG, "failed to connect: " + e.toString());
			}

			// read response
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inStream), 15);
				String line = null;
				while ((line = reader.readLine()) != null) {
					builder.append(line + "\n");
				}
				inStream.close();
			} catch (Exception e) {
				Log.e(TAG, "failed to read: " + e.toString());
			}
			output.append(String.format("%-15.15s%-15.15s\n", "Status:",
					"Location:"));
			result = builder.toString();
			// put results into usable format
			reportArray.clear();
			try {
				JSONArray jArray = new JSONArray(result);
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject jobj = jArray.getJSONObject(i);
					String source = jobj.getString("source");
					String location = jobj.getString("location");
					String status = jobj.getString("status");
					int space = jobj.getString("time").indexOf(" ");
					String time = jobj.getString("time").substring(space + 1);
					SimpleDateFormat df1 = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date date1 = df1.parse(jobj.getString("time"));
					date1.setHours(date1.getHours() + 3);

					SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
					Date date = df.parse(time);
					date.setHours(date.getHours() + 3);
					String newTime = DateFormat.getTimeInstance(
							DateFormat.SHORT).format(date);

					Calendar cal = Calendar.getInstance();
					if (cal.get(Calendar.DATE) == date1.getDate()
							&& cal.get(Calendar.MONTH) == date1.getMonth()) {
						reportArray.add(status);
						reportArray.add(newTime);
						reportArray.add(location);
						reportArray.add(source);
						times.add(Constants.TWEETFORMAT.format(date1));
					}

				}
			} catch (Exception e) {
				error = true;
				output.append("No results to display");
				reportArray.add(" ");
				reportArray.add(" ");
				reportArray.add("No results to display");
				reportArray.add(" ");
				Log.e(TAG, "failed to extract: " + e.toString());
			}

			result = output.toString();
			announceResults();
			return result;
		}
	};

}

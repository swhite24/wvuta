package org.mockup.wvuta;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private static final String TAG = "WVUTA::DBHELPER";
	private static final String DB_NAME = "CURRENT_STATUS_DB";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, 1);
		Log.d(TAG, "DBHelper constructor");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "DBHelper onCreate");
		// create table
		String create_table = "CREATE TABLE " + Constants.REPORT_TABLE + " ("
				+ Constants.LOCATION_COL + " TEXT PRIMARY KEY, "
				+ Constants.STATUS_COL + " TEXT NOT NULL, "
				+ Constants.SOURCE_COL + " TEXT NOT NULL, "
				+ Constants.TIME_COL + " TEXT NOT NULL);";
		Log.d(TAG, "CREATETABLE: " + create_table);
		db.execSQL(create_table);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "DBHelper onUpgrade");
		// drop old table then re-create new version
		db.execSQL("DROP TABLE IF EXISTS " + Constants.REPORT_TABLE);
		onCreate(db);
	}

}

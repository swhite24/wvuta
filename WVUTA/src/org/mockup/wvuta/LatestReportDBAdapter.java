package org.mockup.wvuta;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class LatestReportDBAdapter {

	private static final String DATABASE_NAME = "latest_reports.db";
	private static final String DATABASE_TABLE = "current_reports";
	private static final int DATABASE_VERSION = 1;
	
	private static final String KEY_LOCATION = "location";
	private static final String KEY_TIME = "time";
	private static final String KEY_STATUS = "status";
	
	private static final String DATABASE_CREATE = "create table " +
			DATABASE_TABLE + " (" + KEY_LOCATION + " text primary key, " +
			KEY_TIME + " date not null, " + KEY_STATUS + " text not null);";
	
	private SQLiteDatabase db;
	private final Context context;
	private DBHelper db_helper;
	
	public LatestReportDBAdapter(Context context){
		this.context = context;
		db_helper = new DBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public LatestReportDBAdapter open(){
		db = db_helper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		db.close();
	}
	
	
	private class DBHelper extends SQLiteOpenHelper{

		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table if exists " + DATABASE_TABLE);
			onCreate(db);
		}
		
	}
}

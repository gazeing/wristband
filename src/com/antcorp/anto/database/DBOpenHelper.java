package com.antcorp.anto.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	private static String NAME = "ant-o.db";
	private static int VERSION = 1;

	public DBOpenHelper(Context context) {
		super(context, NAME, null, VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE Version (current_version VARCHAR(32) NOT NULL)");
		db.execSQL("CREATE TABLE IF NOT EXISTS userinfo (infodata varchar(3999))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS Version");
		db.execSQL("DROP TABLE IF EXISTS userinfo");

		onCreate(db);
	}

}

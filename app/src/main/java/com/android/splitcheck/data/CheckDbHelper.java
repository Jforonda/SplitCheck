package com.android.splitcheck.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.splitcheck.data.CheckContract.CheckEntry;

public class CheckDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "check.db";

    private static final int DATABASE_VERSION = 1;

    public CheckDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_CHECK_TABLE = "CREATE TABLE " + CheckEntry.TABLE_NAME + " (" +
                CheckEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CheckEntry.NAME + " TEXT NOT NULL," +
                CheckEntry.TOTAL + " TEXT NOT NULL," +
                CheckEntry.PARTICIPANTS + " TEXT NOT NULL," +
                CheckEntry.ITEMS + " TEXT NOT NULL," +
                CheckEntry.TIME_CREATED + " INTEGER NOT NULL)";

        db.execSQL(SQL_CREATE_CHECK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CheckEntry.TABLE_NAME);
        onCreate(db);
    }
}

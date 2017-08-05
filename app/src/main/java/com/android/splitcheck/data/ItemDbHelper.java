package com.android.splitcheck.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.splitcheck.data.ItemContract.ItemEntry;

public class ItemDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "item.db";

    private static final int DATABASE_VERSION = 1;

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_CHECK_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +
                ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ItemEntry.NAME + " TEXT NOT NULL," +
                ItemEntry.COST + " INTEGER NOT NULL," +
                ItemEntry.CHECK_ID + " INTEGER NOT NULL)";

        db.execSQL(SQL_CREATE_CHECK_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME);
        onCreate(db);
    }
}

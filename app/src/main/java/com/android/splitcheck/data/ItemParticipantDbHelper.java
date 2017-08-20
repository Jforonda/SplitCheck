package com.android.splitcheck.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.splitcheck.data.ItemParticipantContract.ItemParticipantEntry;

public class ItemParticipantDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "item_participant.db";

    public static final int DATABASE_VERSION = 1;

    public ItemParticipantDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ITEM_PARTICIPANT_TABLE = "CREATE TABLE " +
                ItemParticipantEntry.TABLE_NAME + " (" +
                ItemParticipantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ItemParticipantEntry.CHECK_ID + " INTEGER NOT NULL," +
                ItemParticipantEntry.ITEM_ID + " INTEGER NOT NULL," +
                ItemParticipantEntry.PARTICIPANT_ID + " INTEGER NOT NULL," +
                ItemParticipantEntry.PARTICIPANT_NAME + " TEXT NOT NULL," +
                ItemParticipantEntry.IS_CHECKED + " INTEGER NOT NULL)";

        db.execSQL(SQL_CREATE_ITEM_PARTICIPANT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ItemParticipantEntry.TABLE_NAME);
        onCreate(db);
    }
}

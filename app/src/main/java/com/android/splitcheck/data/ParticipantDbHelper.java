package com.android.splitcheck.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.splitcheck.data.ParticipantContract.ParticipantEntry;

public class ParticipantDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "participant.db";

    private static final int DATABASE_VERSION = 1;

    public ParticipantDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_PARTICIPANT_TABLE = "CREATE TABLE " +
                ParticipantEntry.TABLE_NAME + " (" +
                ParticipantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ParticipantEntry.FIRST_NAME + " TEXT NOT NULL," +
                ParticipantEntry.LAST_NAME + " TEXT," +
                ParticipantEntry.CHECK_ID + " INTEGER NOT NULL)";

        db.execSQL(SQL_CREATE_PARTICIPANT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ParticipantEntry.TABLE_NAME);
        onCreate(db);
    }
}

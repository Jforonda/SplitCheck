package com.android.splitcheck.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.splitcheck.data.CheckParticipantContract.CheckParticipantEntry;

public class CheckParticipantDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "check_participant.db";

    public static final int DATABASE_VERSION = 1;

    public CheckParticipantDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_CHECK_PARTICIPANT_TABLE = "CREATE TABLE " +
                CheckParticipantEntry.TABLE_NAME + " (" +
                CheckParticipantEntry.CHECK_ID + " INTEGER NOT NULL," +
                CheckParticipantEntry.PARTICIPANT_ID + " INTEGER NOT NULL)";

        db.execSQL(SQL_CREATE_CHECK_PARTICIPANT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CheckParticipantEntry.TABLE_NAME);
        onCreate(db);
    }
}

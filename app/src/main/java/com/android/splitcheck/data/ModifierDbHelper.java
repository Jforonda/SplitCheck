package com.android.splitcheck.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.splitcheck.data.ModifierContract.ModifierEntry;

public class ModifierDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "modifier.db";

    private static final int DATABASE_VERSION = 1;

    public ModifierDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MODIFIER_TABLE = "CREATE TABLE " +
                ModifierEntry.TABLE_NAME + " (" +
                ModifierEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ModifierEntry.TAX + " INTEGER NOT NULL," +
                ModifierEntry.TAX_PERCENT + " INTEGER NOT NULL," +
                ModifierEntry.TIP + " INTEGER NOT NULL," +
                ModifierEntry.TIP_PERCENT + " INTEGER NOT NULL," +
                ModifierEntry.GRATUITY + " INTEGER NOT NULL," +
                ModifierEntry.GRATUITY_PERCENT + " INTEGER NOT NULL," +
                ModifierEntry.FEES + " INTEGER NOT NULL," +
                ModifierEntry.FEES_PERCENT + " INTEGER NOT NULL," +
                ModifierEntry.DISCOUNT + " INTEGER NOT NULL," +
                ModifierEntry.DISCOUNT_PERCENT + " INTEGER NOT NULL," +
                ModifierEntry.CHECK_ID + " INTEGER NOT NULL)";

        db.execSQL(SQL_CREATE_MODIFIER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ModifierEntry.TABLE_NAME);
        onCreate(db);
    }
}

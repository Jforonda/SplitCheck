package com.android.splitcheck.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Date;

public class Check {

    private String name;
    private int id;
    private String total;
    private long timeCreated;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d");
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    private SimpleDateFormat dateYearFormat = new SimpleDateFormat("MMM d, yyyy");
    private SimpleDateFormat dateFormatNoDay = new SimpleDateFormat("MMM d");
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);


    public Check() {

    }

    public Check(String name, int id, String total, long dateTime) {
        this.name = name;
        this.id = id;
        this.total = total;
        this.timeCreated = dateTime;
    }

    public Check(ContentResolver contentResolver, int checkId) {
        Uri uri = CheckContract.CheckEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(checkId)).build();
        Cursor c = contentResolver.query(uri, null, null, null, null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            this.name = c.getString(c.getColumnIndex(CheckContract.CheckEntry.NAME));
            this.id = c.getInt(c.getColumnIndex(CheckContract.CheckEntry._ID));
            this.total = c.getString(c.getColumnIndex(CheckContract.CheckEntry.TOTAL));
            this.timeCreated = c.getLong(c.getColumnIndex(CheckContract.CheckEntry.TIME_CREATED));
        }
    }

    // Methods for Date and Time

    private long getTimeCreated() {
        return System.currentTimeMillis();
    }

    private Date getCurrentDateTime() {
        return new Date(timeCreated);
    }

    public String getFormattedDate() {
        long currentDate = System.currentTimeMillis();
        String currentYear = yearFormat.format(currentDate);
        String checkYear = yearFormat.format(timeCreated);
        String currentMonth = monthFormat.format(currentDate);
        String checkMonth = monthFormat.format(timeCreated);
        if (Integer.valueOf(currentYear) > Integer.valueOf(checkYear)) {
            return dateYearFormat.format(timeCreated);
        } else {
            if (Integer.valueOf(currentMonth) > Integer.valueOf(checkMonth)) {
                return dateFormatNoDay.format(timeCreated);
            } else {
                return dateFormat.format(timeCreated);
            }
        }
    }

    // Getters and Setters

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    // Database Handlers

    public Uri addToDatabase(ContentResolver contentResolver, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CheckContract.CheckEntry.NAME,
                name);
        contentValues.put(CheckContract.CheckEntry.TOTAL,
                "0");
        contentValues.put(CheckContract.CheckEntry.TIME_CREATED,
                System.currentTimeMillis());
        return contentResolver.insert(CheckContract.CheckEntry.CONTENT_URI,
                contentValues);
    }

    public Uri deleteFromDatabase(ContentResolver contentResolver, int checkId) {
        // Deletes all related Items, CheckParticipants, Modifiers, and ItemParticipants
        Uri checkUri = CheckContract.CheckEntry.CONTENT_URI;
        checkUri = checkUri.buildUpon().appendPath(String.valueOf(checkId)).build();
        contentResolver.delete(checkUri, null, null);

        Uri itemUri = ItemContract.ItemEntry.CONTENT_URI;
        itemUri = itemUri.buildUpon().appendPath(String.valueOf(checkId)).build();
        contentResolver.delete(itemUri, null, null);

        Uri checkParticipantUri = CheckParticipantContract.CheckParticipantEntry.CONTENT_URI;
        checkParticipantUri = checkParticipantUri.buildUpon().appendPath(String.valueOf(checkId)).build();
        contentResolver.delete(checkParticipantUri, null, null);

        Uri modifierUri = ModifierContract.ModifierEntry.CONTENT_URI;
        modifierUri = modifierUri.buildUpon().appendPath(String.valueOf(checkId)).build();
        contentResolver.delete(modifierUri, null, null);

        Uri itemParticipantUri = ItemParticipantContract.ItemParticipantEntry.CONTENT_URI;
        itemParticipantUri = itemParticipantUri.buildUpon().appendPath(String.valueOf(checkId)).build();
        contentResolver.delete(itemParticipantUri, null, null);

        return checkUri;
    }

    public Uri deleteAllFromDatabase(ContentResolver contentResolver) {
        Uri checkUri = CheckContract.CheckEntry.CONTENT_URI;
        contentResolver.delete(checkUri, null, null);

        Uri itemUri = ItemContract.ItemEntry.CONTENT_URI;
        contentResolver.delete(itemUri, null, null);

        Uri checkParticipantUri = CheckParticipantContract.CheckParticipantEntry.CONTENT_URI;
        contentResolver.delete(checkParticipantUri, null, null);

        return checkUri;
    }

    public ArrayList<Integer> getListOfCheckIdsFromDatabase(ContentResolver contentResolver) {
        Uri uri = CheckContract.CheckEntry.CONTENT_URI;
        uri = uri.buildUpon().build();

        Cursor c = contentResolver.query(uri, null, null, null, null);
        c.moveToFirst();
        ArrayList<Integer> checkIds = new ArrayList<>();
        while(!c.isAfterLast()) {
            int id = c.getInt(c.getColumnIndex(CheckContract.CheckEntry._ID));
            checkIds.add(id);
            c.moveToNext();
        }
        return checkIds;
    }

    public ArrayList<Check> getListOfChecks(ContentResolver contentResolver) {
        // Query Database for all checks
        Uri uri = CheckContract.CheckEntry.CONTENT_URI;
        Cursor c = contentResolver.query(uri, null, null, null,
                CheckContract.CheckEntry.TIME_CREATED + " DESC");
        c.moveToFirst();
        ArrayList<Check> checks = new ArrayList<>();
        while(!c.isAfterLast()) {
            String name = c.getString(c.getColumnIndex(CheckContract.CheckEntry.NAME));
            int id = c.getInt(c.getColumnIndex(CheckContract.CheckEntry._ID));
            String total = c.getString(c.getColumnIndex(CheckContract.CheckEntry.TOTAL));
            long dateTime = c.getLong(c.getColumnIndex(CheckContract.CheckEntry.TIME_CREATED));
            Check check = new Check(name, id, total, dateTime);
            checks.add(check);
            c.moveToNext();
        }
        return checks;
    }

    public Check getCheckFromDatabaseFromId(ContentResolver contentResolver, int checkId) {
        Uri uri = CheckContract.CheckEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(checkId)).build();
        Cursor c = contentResolver.query(uri, null, null, null, null);
        c.moveToFirst();
        Check check;
        while(!c.isAfterLast()) {
            String name = c.getString(c.getColumnIndex(CheckContract.CheckEntry.NAME));
            int id = c.getInt(c.getColumnIndex(CheckContract.CheckEntry._ID));
            String total = c.getString(c.getColumnIndex(CheckContract.CheckEntry.TOTAL));
            long dateTime = c.getLong(c.getColumnIndex(CheckContract.CheckEntry.TIME_CREATED));
            check = new Check(name, id, total, dateTime);
            return check;
        }
        return null;
    }

    public int updateName(ContentResolver contentResolver, String checkName, int checkId) {
        ContentValues cv = new ContentValues();
        cv.put(CheckContract.CheckEntry.NAME, checkName);
        return contentResolver.update(CheckContract.CheckEntry.CONTENT_URI, cv, "_id=" + checkId,
                null);
    }

    public int updateTotal(ContentResolver contentResolver, int checkId, String total) {
        ContentValues cv = new ContentValues();
        cv.put(CheckContract.CheckEntry.TOTAL, total);
        return contentResolver.update(CheckContract.CheckEntry.CONTENT_URI, cv, "_id=" + checkId,
                null);
    }

}

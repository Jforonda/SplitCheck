package com.android.splitcheck.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Date;

public class Check {

    private String name;
    private ArrayList<Participant> participants;
    private ArrayList<Item> items;
    private int id;
    private String total;
    private long timeCreated;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // outputFormat: 8/11/2017 12:11 AM
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);


    public Check() {

    }

    public Check(String name, int id, String total, ArrayList<Participant> participants,
                 ArrayList<Item> items, long dateTime) {
        this.name = name;
        this.id = id;
        this.total = total;
        this.participants = participants;
        this.items = items;
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
            this.items = null;
            this.participants = null;
            this.timeCreated = c.getLong(c.getColumnIndex(CheckContract.CheckEntry.TIME_CREATED));
        }
    }

    // Methods for Date and Time

    private long getTimeCreated() {
        return System.currentTimeMillis();
    }

    private Date getCurrentDateTime() {
        Date currentDateTime = new Date(timeCreated);
        return currentDateTime;
    }

    private String formatDate(Date date) {
        return outputFormat.format(date);
    }

    public String getFormattedDate() {
        //return formatDate(dateTimeCreated);
        return formatDate(getCurrentDateTime());
    }

    // Methods for Check Total

    public String getTotal() {
        /**if (!items.isEmpty()) {
            int total = 0;
            for (int i = 0; i < items.size(); i++) {
                total += items.get(i).getCost();
            }

            return total;
        } else {
            return 0;
        }**/
        return total;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
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
                "10");
        // TODO: Check Remove Participants and Items
        contentValues.put(CheckContract.CheckEntry.PARTICIPANTS,
                "None");
        contentValues.put(CheckContract.CheckEntry.ITEMS,
                "None");
        contentValues.put(CheckContract.CheckEntry.TIME_CREATED,
                System.currentTimeMillis());
        Uri uri = contentResolver.insert(CheckContract.CheckEntry.CONTENT_URI,
                contentValues);
        return uri;
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

    public ArrayList<Check> getListOfChecksFromDatabase(ContentResolver contentResolver) {
        // Query Database for all checks
        //TODO Check: Set limit on time frame for checks?
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
            Check check = new Check(name, id, total, null, null, dateTime);
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
            check = new Check(name, id, total, null, null, dateTime);
            return check;
        }
        return null;
    }

    public int updateCheckInDatabaseWithId(ContentResolver contentResolver, String checkName,
                                             int checkId) {
        ContentValues cv = new ContentValues();
        cv.put(CheckContract.CheckEntry.NAME, checkName);
        return contentResolver.update(CheckContract.CheckEntry.CONTENT_URI, cv, "_id=" + checkId,
                null);
    }

    // public int updateTotal

}

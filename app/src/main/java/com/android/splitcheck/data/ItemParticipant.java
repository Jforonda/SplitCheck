package com.android.splitcheck.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

public class ItemParticipant {

    private int id;
    private int checkId;
    private int itemId;
    private int participantId;
    private String participantName;
    private int isChecked;

    public ItemParticipant() {

    }

    public ItemParticipant(int id, int checkId, int itemId, int participantId,
                           String participantName, int isChecked) {
        this.id = id;
        this.checkId = checkId;
        this.itemId = itemId;
        this.participantId = participantId;
        this.participantName = participantName;
        this.isChecked = isChecked;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getParticipantId() {
        return participantId;
    }

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public int getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }



    // Database Handlers
    // Insert
    public Uri addToDb(ContentResolver contentResolver, int checkId, int itemId,
                             int participantId, String participantName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemParticipantContract.ItemParticipantEntry.CHECK_ID, checkId);
        contentValues.put(ItemParticipantContract.ItemParticipantEntry.ITEM_ID, itemId);
        contentValues.put(ItemParticipantContract.ItemParticipantEntry.PARTICIPANT_ID,
                participantId);
        contentValues.put(ItemParticipantContract.ItemParticipantEntry.PARTICIPANT_NAME,
                participantName);
        contentValues.put(ItemParticipantContract.ItemParticipantEntry.IS_CHECKED, 0);
        Uri uri = contentResolver.insert(ItemParticipantContract.ItemParticipantEntry.CONTENT_URI,
                contentValues);

        return uri;
    }

    // Query
    public Cursor getItemCursorFromDb(ContentResolver contentResolver, int itemId) {
        Uri uri = ItemParticipantContract.ItemParticipantEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(itemId)).build();
        return contentResolver.query(uri, null, null, null, null);
    }

    public ArrayList<ItemParticipant> getItemListFromDbFromCheckId(ContentResolver contentResolver, int checkId) {
        ArrayList<ItemParticipant> itemParticipants = new ArrayList<>();
        Uri uri = ItemParticipantContract.ItemParticipantEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(checkId)).build();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndex(ItemParticipantContract.ItemParticipantEntry._ID));
            int itemId = cursor.getInt(cursor.getColumnIndex(ItemParticipantContract.ItemParticipantEntry.ITEM_ID));
            int participantId = cursor.getInt(cursor.getColumnIndex(ItemParticipantContract.ItemParticipantEntry.PARTICIPANT_ID));
            String participantName = cursor.getString(cursor.getColumnIndex(ItemParticipantContract.ItemParticipantEntry.PARTICIPANT_NAME));
            int isChecked = cursor.getInt(cursor.getColumnIndex(ItemParticipantContract.ItemParticipantEntry.IS_CHECKED));
            ItemParticipant itemParticipant = new ItemParticipant(id, checkId, itemId, participantId, participantName, isChecked);
            itemParticipants.add(itemParticipant);
            cursor.moveToNext();
        }

        return itemParticipants;
    }

    // Delete
    public int deleteAllFromDb(ContentResolver contentResolver) {
        Uri uri = ItemParticipantContract.ItemParticipantEntry.CONTENT_URI;
        return contentResolver.delete(uri, null, null);
    }

    public int deleteCheckIdFromDb(ContentResolver contentResolver, int checkId) {
        Uri uri = ItemParticipantContract.ItemParticipantEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(checkId)).build();
        return contentResolver.delete(uri, null, null);
    }

    public int deleteIdFromDb(ContentResolver contentResolver, int id) {
        Uri uri = ItemParticipantContract.ItemParticipantEntry.CONTENT_URI;
        return contentResolver.delete(uri, "_id=" + String.valueOf(id), null);
    }

    public int deleteParticipantIdFromDb(ContentResolver contentResolver, int participantId) {
        Uri uri = ItemParticipantContract.ItemParticipantEntry.CONTENT_URI;
        return contentResolver.delete(uri, "participant_id=" + String.valueOf(participantId), null);
    }

    // Update
    public int updateIsChecked(ContentResolver contentResolver, int itemId, int participantId, int isChecked) {
        ContentValues contentValues= new ContentValues();
        contentValues.put(ItemParticipantContract.ItemParticipantEntry.IS_CHECKED, isChecked);
        return contentResolver.update(ItemParticipantContract.ItemParticipantEntry.CONTENT_URI,
                contentValues, "item_id=" + itemId + " AND participant_id=" + participantId, null);
    }

}

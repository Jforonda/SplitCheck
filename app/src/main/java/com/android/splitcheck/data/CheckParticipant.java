package com.android.splitcheck.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

public class CheckParticipant {

    private int checkId;
    private int participantId;
    private int total;

    public CheckParticipant() {

    }

    public CheckParticipant(int checkId, int participantId, int total) {
        this.checkId = checkId;
        this.participantId = participantId;
        this.total = total;
    }

    // Getters and Setters

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }

    public int getParticipantId() {
        return participantId;
    }

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    // Database Handlers

    public Uri addToDatabase(ContentResolver contentResolver, int checkId, int participantId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CheckParticipantContract.CheckParticipantEntry.CHECK_ID, checkId);
        contentValues.put(CheckParticipantContract.CheckParticipantEntry.PARTICIPANT_ID,
                participantId);
        contentValues.put(CheckParticipantContract.CheckParticipantEntry.TOTAL,
                0);

        return contentResolver.insert(CheckParticipantContract.CheckParticipantEntry.CONTENT_URI,
                contentValues);
    }

    public ArrayList<Participant> getListOfParticipantsFromDatabaseFromCheckId(ContentResolver contentResolver,
                                                                               int checkId) {
        ArrayList<Integer> participantIds = getListOfParticipantIdsFromDatabaseFromCheckId(contentResolver,
                checkId);
        ArrayList<Participant> participants = new ArrayList<>();
        for (int i = 0; i < participantIds.size(); i++) {

            Uri uri = ParticipantContract.ParticipantEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(String.valueOf(participantIds.get(i))).build();
            Cursor c = contentResolver.query(uri, null, null, null, null);

            if (c != null) {
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    String firstName = c.getString(c.getColumnIndex(ParticipantContract.ParticipantEntry.FIRST_NAME));
                    String lastName = c.getString(c.getColumnIndex(ParticipantContract.ParticipantEntry.LAST_NAME));
                    int id = c.getInt(c.getColumnIndex(ParticipantContract.ParticipantEntry._ID));
                    Participant participant = new Participant(firstName, lastName, id);
                    participants.add(participant);
                    c.moveToNext();
                }
            }
        }
        return participants;
    }

    public ArrayList<Integer> getListOfParticipantIdsFromDatabaseFromCheckId(ContentResolver contentResolver,
                                                                             int checkId) {
        Uri uri = CheckParticipantContract.CheckParticipantEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(checkId)).build();
        Cursor c = contentResolver.query(uri, null, null, null, null);

        ArrayList<Integer> participantIds = new ArrayList<>();
        if (c != null) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                int participantId = c.getInt(c.getColumnIndex(CheckParticipantContract.CheckParticipantEntry.PARTICIPANT_ID));
                participantIds.add(participantId);
                c.moveToNext();
            }
        }
        return participantIds;
    }

    public boolean checkParticipantIsEmpty(ContentResolver contentResolver, int checkId) {
        ArrayList<Participant> participants =
                getListOfParticipantsFromDatabaseFromCheckId(contentResolver, checkId);
        if (participants.size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public ArrayList<Participant> getParticipantsNotAdded(ContentResolver contentResolver,
                                                          int checkId) {
        ArrayList<Integer> added = getListOfParticipantIdsFromDatabaseFromCheckId(contentResolver, checkId);
        ArrayList<Integer> all = getListOfParticipantIdsFromDatabase(contentResolver);
        ArrayList<Participant> participantsNotAdded = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            boolean isInAdded = false;
            for (int j = 0; j < added.size(); j++) {
                if (added.get(j).equals(all.get(i))) {
                    isInAdded = true;
                }
            }
            if (!isInAdded) {
                Uri uri = ParticipantContract.ParticipantEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(String.valueOf(all.get(i))).build();
                Cursor c = contentResolver.query(uri, null, null, null, null);

                if (c != null) {
                    c.moveToFirst();
                    while (!c.isAfterLast()) {
                        String firstName = c.getString(c.getColumnIndex(ParticipantContract.ParticipantEntry.FIRST_NAME));
                        String lastName = c.getString(c.getColumnIndex(ParticipantContract.ParticipantEntry.LAST_NAME));
                        int id = c.getInt(c.getColumnIndex(ParticipantContract.ParticipantEntry._ID));
                        Participant participant = new Participant(firstName, lastName, id);
                        participantsNotAdded.add(participant);
                        c.moveToNext();
                    }
                }
            }
        }
        return participantsNotAdded;
    }

    public ArrayList<Integer> getListOfParticipantIdsFromDatabase(ContentResolver contentResolver) {
        // Returns List of ALL Participant IDS
        Uri uri = ParticipantContract.ParticipantEntry.CONTENT_URI;
        Cursor c = contentResolver.query(uri, null, null, null, null);

        ArrayList<Integer> participantIds = new ArrayList<>();
        if (c != null) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                int participantId = c.getInt(c.getColumnIndex(ParticipantContract.ParticipantEntry._ID));
                participantIds.add(participantId);
                c.moveToNext();
            }
        }
        return participantIds;
    }

    public Uri deleteFromDb(ContentResolver contentResolver, int checkId, int participantId) {
        Uri checkParticipantUri = CheckParticipantContract.CheckParticipantEntry.CONTENT_URI;
        contentResolver.delete(checkParticipantUri, CheckParticipantContract.CheckParticipantEntry
                .CHECK_ID + " = " + String.valueOf(checkId) + " AND " +
                CheckParticipantContract.CheckParticipantEntry
                .PARTICIPANT_ID + " = " + String.valueOf(participantId), null);

        Uri itemParticipantUri = ItemParticipantContract.ItemParticipantEntry.CONTENT_URI;
        contentResolver.delete(itemParticipantUri, CheckParticipantContract.CheckParticipantEntry
                .CHECK_ID + " = " + String.valueOf(checkId) + " AND " +
                ItemParticipantContract.ItemParticipantEntry
                .PARTICIPANT_ID + " = " + String.valueOf(participantId), null);
        return checkParticipantUri;
    }

    public int updateTotal(ContentResolver contentResolver, int checkId, int participantId, int total) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CheckParticipantContract.CheckParticipantEntry.TOTAL,
                total);
        Uri uri = CheckParticipantContract.CheckParticipantEntry.CONTENT_URI;
        return contentResolver.update(uri, contentValues,
                CheckParticipantContract.CheckParticipantEntry.CHECK_ID + " = " + checkId + " AND "
                        + CheckParticipantContract.CheckParticipantEntry.PARTICIPANT_ID + " = "
                        + participantId, null);
    }

    public ArrayList<CheckParticipant> updateCheckParticipantTotals(ContentResolver contentResolver, int checkId) {
        ArrayList<CheckParticipant> checkParticipants = new ArrayList<>();
        ArrayList<Integer> participantIds = getListOfParticipantIdsFromDatabaseFromCheckId(contentResolver,
                checkId);
        Item item = new Item();
        ArrayList<ItemParticipant> itemParticipants = new ArrayList<>();
        Uri uri = ItemParticipantContract.ItemParticipantEntry.CONTENT_URI;
        for (int i = 0; i < participantIds.size(); i++) {
            int participantId = participantIds.get(i);
            Cursor cursor = contentResolver.query(uri, null, "check_id=" + String.valueOf(checkId) + " AND participant_id=" + String.valueOf(participantId), null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(ItemParticipantContract.ItemParticipantEntry._ID));
                int itemId = cursor.getInt(cursor.getColumnIndex(ItemParticipantContract.ItemParticipantEntry.ITEM_ID));
                int tempParticipantId = cursor.getInt(cursor.getColumnIndex(ItemParticipantContract.ItemParticipantEntry.PARTICIPANT_ID));
                String participantName = cursor.getString(cursor.getColumnIndex(ItemParticipantContract.ItemParticipantEntry.PARTICIPANT_NAME));
                int isChecked = cursor.getInt(cursor.getColumnIndex(ItemParticipantContract.ItemParticipantEntry.IS_CHECKED));
                ItemParticipant itemParticipant = new ItemParticipant(id, checkId, itemId, tempParticipantId, participantName, isChecked);
                itemParticipants.add(itemParticipant);
                cursor.moveToNext();
            }
            int participantTotal = 0;
            for (int j = 0; j < itemParticipants.size(); j++) {
                if (itemParticipants.get(j).getIsChecked() == 1) {
                    participantTotal += item.getSplitAmountPerItem(contentResolver, itemParticipants.get(j).getItemId());
                }
            }
            itemParticipants.clear();
            updateTotal(contentResolver, checkId, participantId, participantTotal);
            checkParticipants.add(new CheckParticipant(checkId, participantId, participantTotal));
        }
        return checkParticipants;
    }

    public int nonZeroTotalParticipants(ContentResolver contentResolver, int checkId) {
        int nonZeroTotalCounter = 0;
        Uri uri = CheckParticipantContract.CheckParticipantEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(checkId)).build();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int total = cursor.getInt(cursor.getColumnIndex(CheckParticipantContract.CheckParticipantEntry.TOTAL));
            if (total != 0) {
                nonZeroTotalCounter++;
            }
            cursor.moveToNext();
        }
        return nonZeroTotalCounter;
    }

    public String getParticipantTotalWithModifier(ContentResolver contentResolver, int checkId, int participantId) {
        Uri uri = CheckParticipantContract.CheckParticipantEntry.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, CheckParticipantContract
                .CheckParticipantEntry.CHECK_ID + " = " + checkId + " AND "
                + CheckParticipantContract.CheckParticipantEntry.PARTICIPANT_ID
                + " = " + participantId, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            int total = cursor.getInt(cursor.getColumnIndex(CheckParticipantContract.CheckParticipantEntry.TOTAL));

            Modifier modifier = new Modifier(contentResolver, checkId);

            int totalAmount = 0, totalPercentage = 0;
            if (modifier.getTaxPercent()) {
                totalPercentage += modifier.getTax();
            } else {
                totalAmount += modifier.getTax();
            }
            if (modifier.getTipPercent()) {
                totalPercentage += modifier.getTip();
            } else {
                totalAmount += modifier.getTip();
            }
            if (modifier.getGratuityPercent()) {
                totalPercentage += modifier.getGratuity();
            } else {
                totalAmount += modifier.getGratuity();
            }
            if (modifier.getFeesPercent()) {
                totalPercentage += modifier.getFees();
            } else {
                totalAmount += modifier.getFees();
            }
            if (modifier.getDiscountPercent()) {
                if (modifier.getDiscount() >= 10000) {
                    totalPercentage -= total;
                } else {
                    totalPercentage -= modifier.getDiscount();
                }
            } else {
                if (modifier.getDiscount() >= total) {
                    totalAmount -= total;
                } else {
                    totalAmount -= modifier.getDiscount();
                }
            }
            BigDecimal bigDecimalPercentage = new BigDecimal(total * totalPercentage).divide(new BigDecimal(10000), BigDecimal.ROUND_HALF_UP);
            BigDecimal bigDecimalAmount;
            if (nonZeroTotalParticipants(contentResolver, checkId) > 1) {
                bigDecimalAmount = new BigDecimal(totalAmount).divide(new BigDecimal(nonZeroTotalParticipants(contentResolver, checkId)), BigDecimal.ROUND_HALF_UP).add(bigDecimalPercentage);
            } else {
                bigDecimalAmount = new BigDecimal(totalAmount).add(bigDecimalPercentage);
            }
            BigDecimal bigDecimalTotal = new BigDecimal(total).add(bigDecimalAmount).setScale(2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);

            return NumberFormat.getCurrencyInstance().format(bigDecimalTotal);
        } else {
            return NumberFormat.getCurrencyInstance().format(0);
        }

    }

}

package com.android.splitcheck.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

public class CheckParticipant {

    private int id;
    private int checkId;
    private int participantId;

    public CheckParticipant() {

    }

    public CheckParticipant(int id, int checkId, int participantId) {
        this.id = id;
        this.checkId = checkId;
        this.participantId = participantId;
    }

    // Getters and Setters

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

    public int getParticipantId() {
        return participantId;
    }

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
    }

    // Database Handlers

    public Uri addToDatabase(ContentResolver contentResolver, int checkId, int participantId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CheckParticipantContract.CheckParticipantEntry.CHECK_ID, checkId);
        contentValues.put(CheckParticipantContract.CheckParticipantEntry.PARTICIPANT_ID,
                participantId);
        Uri uri = contentResolver.insert(CheckParticipantContract.CheckParticipantEntry.CONTENT_URI,
                contentValues);

        return uri;
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

}

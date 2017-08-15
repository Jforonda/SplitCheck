package com.android.splitcheck.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Participant implements Parcelable {

    private String firstName;
    private String lastName;
    private String color;
    private int id;

    public Participant() {

    }

    public Participant(String firstName, String lastName, String color, int id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.color = color;
        this.id = id;
    }

    public Participant(String firstName, String lastName, int id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
    }

    // Getters and Setters

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Parcelable methods

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeString(color);
        out.writeInt(id);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Participant createFromParcel(Parcel in) {
            return new Participant(in);
        }
        public Participant[] newArray(int size) {
            return new Participant[size];
        }
    };

    private Participant(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.color = in.readString();
        this.id = in.readInt();
    }

    // Database Handler

    public Uri addToDatabase(ContentResolver contentResolver, String firstName, String lastName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ParticipantContract.ParticipantEntry.FIRST_NAME,
                firstName);
        contentValues.put(ParticipantContract.ParticipantEntry.LAST_NAME,
                lastName);
        Uri uri = contentResolver.insert(ParticipantContract.ParticipantEntry.CONTENT_URI,
                contentValues);
        return uri;
    }

    public ArrayList<Participant> getListOfParticipantsFromDatabase(ContentResolver
                                                                               contentResolver) {
        // Get full list of Database (Sort by number of uses?)
        Uri uri = ParticipantContract.ParticipantEntry.CONTENT_URI;
        Cursor c = contentResolver.query(uri, null, null, null, null);
        ArrayList<Participant> participants = new ArrayList<>();
        if (c != null) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String firstName = c.getString(c.getColumnIndex(ParticipantContract.
                        ParticipantEntry.FIRST_NAME));
                String lastName = c.getString(c.getColumnIndex(ParticipantContract.
                        ParticipantEntry.LAST_NAME));
                int id = c.getInt(c.getColumnIndex(ParticipantContract.
                        ParticipantEntry._ID));
                Participant participant = new Participant(firstName, lastName, id);
                participants.add(participant);
                c.moveToNext();
            }
        }
        return participants;
    }
}

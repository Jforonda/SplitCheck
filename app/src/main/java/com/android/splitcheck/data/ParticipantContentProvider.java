package com.android.splitcheck.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.splitcheck.data.ParticipantContract.ParticipantEntry;

public class ParticipantContentProvider extends ContentProvider {

    public static final int PARTICIPANT = 100;
    public static final int PARTICIPANT_WITH_ID = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher= new UriMatcher(UriMatcher.NO_MATCH);
        String authority = ParticipantContract.AUTHORITY;
        String pathParticipants = ParticipantContract.PATH_PARTICIPANTS;
        uriMatcher.addURI(authority, pathParticipants, PARTICIPANT);
        uriMatcher.addURI(authority, pathParticipants + "/#", PARTICIPANT_WITH_ID);
        return uriMatcher;
    }

    private ParticipantDbHelper mParticipantDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mParticipantDbHelper = new ParticipantDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mParticipantDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case PARTICIPANT:
                long id = db.insert(ParticipantEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(ParticipantEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mParticipantDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case PARTICIPANT:
                retCursor = db.query(ParticipantEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PARTICIPANT_WITH_ID:
                String id = uri.getPathSegments().get(1);
                selection = ParticipantEntry.TABLE_NAME + "." + ParticipantEntry.CHECK_ID
                        + " = " + id;
                retCursor = db.query(ParticipantEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mParticipantDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int participantsDeleted;

        switch (match) {
            case PARTICIPANT:
                participantsDeleted = db.delete(ParticipantEntry.TABLE_NAME, null, null);
                break;
            case PARTICIPANT_WITH_ID:
                String id = uri.getPathSegments().get(1);
                participantsDeleted = db.delete(ParticipantEntry.TABLE_NAME, "_id=?",
                        new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return participantsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mParticipantDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int participantsUpdated;

        switch (match) {
            case PARTICIPANT:
                participantsUpdated = db.update(ParticipantEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return participantsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PARTICIPANT:
                return "Participant";
            case PARTICIPANT_WITH_ID:
                return "Participant with info";
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


}

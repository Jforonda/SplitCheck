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

import com.android.splitcheck.data.CheckParticipantContract.CheckParticipantEntry;

public class CheckParticipantContentProvider extends ContentProvider {

    public static final int CHECK_PARTICIPANT = 100;
    public static final int CHECK_PARTICIPANT_WITH_ID = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = CheckParticipantContract.AUTHORITY;
        String pathCheckParticipants = CheckParticipantContract.PATH_CHECK_PARTICIPANTS;
        uriMatcher.addURI(authority, pathCheckParticipants, CHECK_PARTICIPANT);
        uriMatcher.addURI(authority, pathCheckParticipants + "/#", CHECK_PARTICIPANT_WITH_ID);
        return uriMatcher;
    }

    private CheckParticipantDbHelper mCheckParticipantDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mCheckParticipantDbHelper = new CheckParticipantDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mCheckParticipantDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;
        switch (match) {
            case CHECK_PARTICIPANT:
                long id = db.insert(CheckParticipantEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(CheckParticipantEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Unknown uri: " + uri);
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

        final SQLiteDatabase db = mCheckParticipantDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor retCursor;
        switch (match) {
            case CHECK_PARTICIPANT:
                retCursor = db.query(CheckParticipantEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CHECK_PARTICIPANT_WITH_ID:
                String id = uri.getPathSegments().get(1);
                selection = CheckParticipantEntry.TABLE_NAME + "." + CheckParticipantEntry.CHECK_ID
                        + " = " + id;
                retCursor = db.query(CheckParticipantEntry.TABLE_NAME,
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

        final SQLiteDatabase db = mCheckParticipantDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int checkParticipantsDeleted;
        switch (match) {
            case CHECK_PARTICIPANT:
                checkParticipantsDeleted = db.delete(CheckContract.CheckEntry.TABLE_NAME, null,
                        null);
                break;
            case CHECK_PARTICIPANT_WITH_ID:
                String id = uri.getPathSegments().get(1);
                checkParticipantsDeleted = db.delete(CheckParticipantEntry.TABLE_NAME, "check_id=?",
                        new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (checkParticipantsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return checkParticipantsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mCheckParticipantDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int checkParticipantsUpdated;
        switch (match) {
            case CHECK_PARTICIPANT:
                checkParticipantsUpdated = db.update(CheckParticipantEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return checkParticipantsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CHECK_PARTICIPANT:
                return "Check Participant";
            case CHECK_PARTICIPANT_WITH_ID:
                return "Check Participant with info";
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}

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

import static com.android.splitcheck.data.CheckContract.CheckEntry;

public class CheckContentProvider extends ContentProvider {

    public static final int CHECK = 100;
    public static final int CHECK_WITH_ID = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = CheckContract.AUTHORITY;
        String pathChecks = CheckContract.PATH_CHECKS;
        uriMatcher.addURI(authority, pathChecks, CHECK);
        uriMatcher.addURI(authority, pathChecks + "/#", CHECK_WITH_ID);
        return uriMatcher;
    }

    private CheckDbHelper mCheckDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mCheckDbHelper = new CheckDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mCheckDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case CHECK:
                long id = db.insert(CheckEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(CheckEntry.CONTENT_URI, id);
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

        final SQLiteDatabase db = mCheckDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case CHECK:
                retCursor = db.query(CheckEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CHECK_WITH_ID:
                String id = uri.getPathSegments().get(1);
                selection = CheckEntry.TABLE_NAME + "." + CheckEntry._ID
                        + " = " + id;
                retCursor = db.query(CheckEntry.TABLE_NAME,
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

        final SQLiteDatabase db = mCheckDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int checksDeleted;
        switch (match) {
            case CHECK:
                checksDeleted = db.delete(CheckEntry.TABLE_NAME, null, null);
                break;
            case CHECK_WITH_ID:
                String id = uri.getPathSegments().get(1);
                checksDeleted = db.delete(CheckEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (checksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return checksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mCheckDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int checksUpdated;
        switch (match) {
            case CHECK:
                checksUpdated = db.update(CheckEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return checksUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case CHECK:
                return "Check";
            case CHECK_WITH_ID:
                return "Check with info";
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}

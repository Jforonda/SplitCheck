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

import com.android.splitcheck.data.ModifierContract.ModifierEntry;

public class ModifierContentProvider extends ContentProvider{

    public static final int MODIFIER = 100;
    public static final int MODIFIER_WITH_ID = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = ModifierContract.AUTHORITY;
        String pathModifiers = ModifierContract.PATH_MODIFIERS;
        uriMatcher.addURI(authority, pathModifiers, MODIFIER);
        uriMatcher.addURI(authority, pathModifiers + "/#", MODIFIER_WITH_ID);
        return uriMatcher;
    }

    private ModifierDbHelper mModifierDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mModifierDbHelper = new ModifierDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mModifierDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MODIFIER:
                long id = db.insert(ModifierEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(ModifierEntry.CONTENT_URI, id);
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

        final SQLiteDatabase db = mModifierDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case MODIFIER:
                retCursor = db.query(ModifierEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MODIFIER_WITH_ID:
                String id = uri.getPathSegments().get(1);
                selection = ModifierEntry.TABLE_NAME + "." + ModifierEntry.CHECK_ID
                        + " = " + id;
                retCursor = db.query(ModifierEntry.TABLE_NAME,
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

        final SQLiteDatabase db = mModifierDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int modifiersDeleted;
        switch (match) {
            case MODIFIER:
                modifiersDeleted = db.delete(ModifierEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MODIFIER_WITH_ID:
                String id = uri.getPathSegments().get(1);
                modifiersDeleted = db.delete(ModifierEntry.TABLE_NAME, "check_id=?",
                        new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (modifiersDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return modifiersDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mModifierDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int modifiersUpdated;
        switch (match) {
            case MODIFIER:
                modifiersUpdated = db.update(ModifierEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return modifiersUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MODIFIER:
                return "Modifier";
            case MODIFIER_WITH_ID:
                return "Modifier with info";
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}

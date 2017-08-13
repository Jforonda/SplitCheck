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

import com.android.splitcheck.data.ItemContract.ItemEntry;

public class ItemContentProvider extends ContentProvider {

    public static final int ITEM = 100;
    public static final int ITEM_WITH_ID = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = ItemContract.AUTHORITY;
        String pathItems = ItemContract.PATH_ITEMS;
        uriMatcher.addURI(authority, pathItems, ITEM);
        uriMatcher.addURI(authority, pathItems + "/#", ITEM_WITH_ID);
        return uriMatcher;
    }

    private ItemDbHelper mItemDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mItemDbHelper = new ItemDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mItemDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case ITEM:
                long id = db.insert(ItemEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);
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

        final SQLiteDatabase db = mItemDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case ITEM:
                retCursor = db.query(ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ITEM_WITH_ID:
                String id = uri.getPathSegments().get(1);
                selection = ItemEntry.TABLE_NAME + "." + ItemEntry.CHECK_ID
                        + " = " + id;
                retCursor = db.query(ItemEntry.TABLE_NAME,
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

        final SQLiteDatabase db = mItemDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int itemsDeleted;

        switch (match) {
            case ITEM:
                itemsDeleted = db.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_WITH_ID:
                String id = uri.getPathSegments().get(1);
                itemsDeleted = db.delete(ItemEntry.TABLE_NAME, "check_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (itemsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return itemsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mItemDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int itemsUpdated;

        switch (match) {
            case ITEM:
                itemsUpdated = db.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return itemsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case ITEM:
                return "Item";
            case ITEM_WITH_ID:
                return "Item with info";
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}

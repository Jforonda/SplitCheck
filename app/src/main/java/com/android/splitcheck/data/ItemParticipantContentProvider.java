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

import com.android.splitcheck.data.ItemParticipantContract.ItemParticipantEntry;

public class ItemParticipantContentProvider extends ContentProvider {

    public static final int ITEM_PARTICIPANT = 100;
    public static final int ITEM_PARTICIPANT_WITH_ID = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = ItemParticipantContract.AUTHORITY;
        String pathItemParticipants = ItemParticipantContract.PATH_ITEM_PARTICIPANTS;
        uriMatcher.addURI(authority, pathItemParticipants, ITEM_PARTICIPANT);
        uriMatcher.addURI(authority, pathItemParticipants + "/#", ITEM_PARTICIPANT_WITH_ID);
        return uriMatcher;
    }

    private ItemParticipantDbHelper mItemParticipantDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mItemParticipantDbHelper = new ItemParticipantDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mItemParticipantDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;
        switch (match) {
            case ITEM_PARTICIPANT:
                long id = db.insert(ItemParticipantEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(ItemParticipantEntry.CONTENT_URI, id);
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

        final SQLiteDatabase db = mItemParticipantDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor retCursor;
        switch (match) {
            case ITEM_PARTICIPANT:
                retCursor = db.query(ItemParticipantEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ITEM_PARTICIPANT_WITH_ID:
                String id = uri.getPathSegments().get(1);
                selection = ItemParticipantEntry.TABLE_NAME + "." + ItemParticipantEntry.ITEM_ID
                        + " = " + id;
                retCursor = db.query(ItemParticipantEntry.TABLE_NAME,
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

        final SQLiteDatabase db = mItemParticipantDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int itemParticipantsDeleted;
        switch (match) {
            case ITEM_PARTICIPANT:
                itemParticipantsDeleted = db.delete(ItemParticipantEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case ITEM_PARTICIPANT_WITH_ID:
                String id = uri.getPathSegments().get(1);
                itemParticipantsDeleted = db.delete(ItemParticipantEntry.TABLE_NAME, "check_id=?",
                        new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (itemParticipantsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return itemParticipantsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mItemParticipantDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int itemParticipantsUpdated;
        switch (match) {
            case ITEM_PARTICIPANT:
                itemParticipantsUpdated = db.update(ItemParticipantEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return itemParticipantsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM_PARTICIPANT:
                return "Item Participant";
            case ITEM_PARTICIPANT_WITH_ID:
                return "Item Participant with info";
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}

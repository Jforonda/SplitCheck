package com.android.splitcheck.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ItemContract {

    public static final String AUTHORITY = "com.android.splitcheck.data.ItemContract";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_ITEMS = "items";

    public static final long INVALID_ITEM_ID = -1;

    public static final class ItemEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEMS).build();

        public static final String TABLE_NAME = "items";
        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String COST = "cost";
        public static final String CHECK_ID = "check_id";
    }
}

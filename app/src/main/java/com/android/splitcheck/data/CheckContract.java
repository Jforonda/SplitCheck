package com.android.splitcheck.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class CheckContract {

    public static final String AUTHORITY = "com.android.splitcheck.data.CheckContract";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_CHECKS = "checks";

    public static final long INVALID_CHECK_ID = -1;

    public static final class CheckEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHECKS).build();

        public static final String TABLE_NAME = "checks";
        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String TOTAL = "total";
        public static final String PARTICIPANTS = "participants";
        public static final String ITEMS = "items";
        public static final String TIME_CREATED = "time_created";
    }
}

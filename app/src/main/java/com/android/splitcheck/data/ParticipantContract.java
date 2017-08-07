package com.android.splitcheck.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ParticipantContract {

    public static final String AUTHORITY = "com.android.splitcheck.data.ParticipantContract";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_PARTICIPANTS = "participants";

    public static final long INVALID_PARTICIPANT_ID = -1;

    public static final class ParticipantEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PARTICIPANTS).build();

        public static final String TABLE_NAME = "participants";
        public static final String _ID = "_id";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String CHECK_ID = "check_id";
    }
}

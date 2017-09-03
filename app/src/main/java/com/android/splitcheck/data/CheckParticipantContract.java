package com.android.splitcheck.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class CheckParticipantContract {

    public static final String AUTHORITY = "com.android.splitcheck.data.CheckParticipantContract";

    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static String PATH_CHECK_PARTICIPANTS = "checkParticipants";

    public static final long INVALID_CHECK_PARTICIPANT_ID = -1;

    public static final class CheckParticipantEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHECK_PARTICIPANTS).build();

        public static final String TABLE_NAME = "check_participants";
        public static final String _ID = "_id";
        public static final String CHECK_ID = "check_id";
        public static final String PARTICIPANT_ID = "participant_id";
        public static final String TOTAL = "total";
    }
}

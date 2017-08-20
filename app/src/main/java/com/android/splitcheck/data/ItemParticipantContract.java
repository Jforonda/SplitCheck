package com.android.splitcheck.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ItemParticipantContract {

    public static final String AUTHORITY ="com.android.splitcheck.data.ItemParticipantContract";

    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static String PATH_ITEM_PARTICIPANTS = "itemParticipants";

    public static final long INVALID_ITEM_PARTICIPANT_ID = -1;

    public static final class ItemParticipantEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM_PARTICIPANTS).build();

        public static final String TABLE_NAME = "item_participants";
        public static final String _ID = "_id";
        public static final String CHECK_ID = "check_id";
        public static final String ITEM_ID = "item_id";
        public static final String PARTICIPANT_ID = "participant_id";
        public static final String PARTICIPANT_NAME = "participant_name";
        public static final String IS_CHECKED = "is_checked";
    }
}

package com.android.splitcheck.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ModifierContract {

    public static final String AUTHORITY = "com.android.splitcheck.data.ModifierContract";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MODIFIERS = "modifiers";

    public static final long INVALID_MODIFIER_ID = -1;

    public static final class ModifierEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MODIFIERS).build();

        public static final String TABLE_NAME = "modifiers";
        public static final String _ID = "_id";
        public static final String TAX = "tax";
        public static final String TAX_PERCENT = "tax_percent";
        public static final String TIP = "tip";
        public static final String TIP_PERCENT = "tip_percent";
        public static final String GRATUITY = "gratuity";
        public static final String GRATUITY_PERCENT = "gratuity_percent";
        public static final String FEES = "fees";
        public static final String FEES_PERCENT = "fees_percent";
        public static final String DISCOUNT = "discount";
        public static final String DISCOUNT_PERCENT = "discount_percent";
        public static final String CHECK_ID = "check_id";
    }
}

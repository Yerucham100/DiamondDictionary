package com.example.diacious.diamonddictionary;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by DIACIOUS on 6/1/2018.
 */

public class SearchHistoryContract
{
    private SearchHistoryContract(){}//Should never be instantiated

    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.example.diacious.diamonddictionary";
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);
    public static final String SEARCH_HISTORY_PATH = "search_history";

    public static class SearchHistory implements BaseColumns
    {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(SEARCH_HISTORY_PATH)
                .build();

        public static final String TABLE_NAME = "search_history";
        public static final String COLUMN_WORD = "word_col";
        public static final String COLUMN_DEFINITION = "def_col";
        public static final String COLUMN_SEARCH_FREQUENCY = "search_freq_col";
        public static final String COLUMN_LAST_SEARCHED = "last_time_searched_col";
    }
}

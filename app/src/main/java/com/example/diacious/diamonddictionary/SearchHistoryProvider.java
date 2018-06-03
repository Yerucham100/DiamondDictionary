package com.example.diacious.diamonddictionary;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by DIACIOUS on 6/1/2018.
 */

public class SearchHistoryProvider extends ContentProvider
{
    private SearchHistoryDbHelper mDbHelper;
    private static final int SINGLE_SEARCH = 0;
    private static final int ALL_SEARCHES = 1;
    private static final UriMatcher sURI_MATCHER = getURI_MATCHER();

    @Override
    public boolean onCreate()
    {
        mDbHelper = new SearchHistoryDbHelper(getContext());
        return true;
    }

    private static UriMatcher getURI_MATCHER()
    {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(SearchHistoryContract.AUTHORITY, SearchHistoryContract.SEARCH_HISTORY_PATH + "/#", SINGLE_SEARCH);
        matcher.addURI(SearchHistoryContract.AUTHORITY, SearchHistoryContract.SEARCH_HISTORY_PATH, ALL_SEARCHES);

        return matcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int uriType = sURI_MATCHER.match(uri);
        Cursor result;

        switch (uriType)
        {
            case SINGLE_SEARCH:
                String id = uri.getPathSegments().get(1);

                result = db.query(SearchHistoryContract.SearchHistory.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{id},
                        null,
                        null,
                        sortOrder);

                break;
            case ALL_SEARCHES:
                result = db.query(SearchHistoryContract.SearchHistory.TABLE_NAME,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            default:throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        int uriType = sURI_MATCHER.match(uri);
        Uri retUri;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (uriType)
        {
            case ALL_SEARCHES:
                long id = db.insert(SearchHistoryContract.SearchHistory.TABLE_NAME, null, values);
                if (id > 0)
                    retUri = ContentUris.withAppendedId(SearchHistoryContract.SearchHistory.CONTENT_URI, id);
                else
                    throw new SQLException("Insert failed at: " + uri);
                break;
            default:throw new UnsupportedOperationException("Invalid Uri for Insert Method: " + uri);
        }
        return retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        int uriType = sURI_MATCHER.match(uri);
        int numUpdated;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (uriType)
        {
            case SINGLE_SEARCH:
                String id = uri.getPathSegments().get(1);
                numUpdated = db.update(SearchHistoryContract.SearchHistory.TABLE_NAME,
                        values,
                        "_id=?",
                        new String[]{id});
                break;
            default:throw new UnsupportedOperationException("Invalid Uri for update: "+ uri);
        }
        return numUpdated;
    }
}

package com.example.diacious.diamonddictionary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DIACIOUS on 6/1/2018.
 */

public class SearchHistoryDbHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "diamond_dictionary.db";
    public static final int DATABASE_VERSION = 2;

    public SearchHistoryDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String CREATE_TABLE = "CREATE TABLE "
                + SearchHistoryContract.SearchHistory.TABLE_NAME
                + "("
                + SearchHistoryContract.SearchHistory._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SearchHistoryContract.SearchHistory.COLUMN_WORD + " TEXT NOT NULL, "
                + SearchHistoryContract.SearchHistory.COLUMN_DEFINITION + " TEXT NOT NULL, "
                + SearchHistoryContract.SearchHistory.COLUMN_SEARCH_FREQUENCY + " INTEGER NOT NULL, "
                + SearchHistoryContract.SearchHistory.COLUMN_LAST_SEARCHED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                + ");";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        final String DROP_TABLE = "DROP TABLE IF EXISTS " + SearchHistoryContract.SearchHistory.TABLE_NAME;
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}

package com.example.diacious.diamonddictionary.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.diacious.diamonddictionary.SearchHistoryContract;

/**
 * Created by DIACIOUS on 6/3/2018.
 */

public class DbUtils
{
    private static String meaning = null;
    private static String lastSearched = null;
    private static int searchFrequency = 0;
    /**
     * Method to search for the information of a word from the database
     * @param word The word to be searched for
     * @return true if the word is found else false
     */
    public static boolean getInfoFromDatabase(String word, Context context)
    {
        ContentResolver resolver = context.getContentResolver();
        Cursor data = resolver.query(SearchHistoryContract.SearchHistory.CONTENT_URI,
                null,
                SearchHistoryContract.SearchHistory.COLUMN_WORD + "=?",
                new String[]{word},
                null);

        if (data == null)
        {
            return false;
        }

        else
        {
            int definitionCol = data.getColumnIndex(SearchHistoryContract.SearchHistory.COLUMN_DEFINITION);
            int lastSearchedCol = data.getColumnIndex(SearchHistoryContract.SearchHistory.COLUMN_LAST_SEARCHED);
            int searchFreqCol = data.getColumnIndex(SearchHistoryContract.SearchHistory.COLUMN_SEARCH_FREQUENCY);

            if (!data.moveToFirst())
                return false;
            meaning = data.getString(definitionCol);
            lastSearched = data.getString(lastSearchedCol);
            searchFrequency = data.getInt(searchFreqCol);
        }
        return true;
    }


    /**
     * Method to insert a new word into the database
     * @param word The word to be inserted
     * @param meaning The definition of the word
     * @return true if insert is successful else false
     */
    public static boolean insertWordInDatabase(String word, String meaning, Context context)
    {
        ContentResolver resolver = context.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(SearchHistoryContract.SearchHistory.COLUMN_WORD, word);
        cv.put(SearchHistoryContract.SearchHistory.COLUMN_DEFINITION, meaning);
        cv.put(SearchHistoryContract.SearchHistory.COLUMN_SEARCH_FREQUENCY, 1);

        Uri uri = resolver.insert(SearchHistoryContract.SearchHistory.CONTENT_URI, cv);

        resolver.notifyChange(uri, null);
        return true;
    }
    /**
     * Method to update the lastSearched and searchFrequency of a word
     */
    public static void updateWord()
    {

    }

    /**
     * Method to get the meaning of the word
     * @return The definition of the word
     */
    public static String getMeaning()
    {
        Log.d("MEANING", "Meaning searched: " + meaning);
        return meaning;
    }

    /**
     * Method to get the date the word was last searched
     * @return The date the word was last searched
     */
    public static String getLastSearched()
    {
        return lastSearched;
    }

    /**
     * Method to get the number of times a word has been looked up
     * @return The number of times a word has been searched
     */
    public static String getSearchFrequency()
    {
        return Integer.toString(searchFrequency);
    }

}
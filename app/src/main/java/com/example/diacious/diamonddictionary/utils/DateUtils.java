package com.example.diacious.diamonddictionary.utils;

/**
 * Created by DIACIOUS on 6/5/2018.
 */

public class DateUtils
{
    /**
     * Method to get date in YYYY-MM-DD format
     * @param databaseTimeStampDate The data stored in the database
     * @return The needed format
     */
    public static String getDateInStandardFormat(String databaseTimeStampDate)
    {
        return databaseTimeStampDate.substring(0,10);
    }
}

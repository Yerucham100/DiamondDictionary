package com.example.diacious.diamonddictionary.utils;
import android.net.Uri;
import android.support.v4.content.Loader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;
/**
 * Created by DIACIOUS on 5/27/2018.
 */

public class NetworkUtils
{
    public static final String ONLINE_DICTIONARY_BASE_URL = "https://od-api.oxforddictionaries.com:443/api/v1/entries";
    public static final String URL_FOR_OXFORD = "https://en.oxforddictionaries.com";
    public static final String LANGUAGE = "en";
    public static final String WORD_NOT_FOUND = "word_not_found";
    private static final String testWord = "car";
    public static final String NO_NETWORK = "no_network";


    /**
     * Method to build a Uri for looking up the meaning of a word
     * @param searchTerm The word to be looked up
     * @return The built Uri
     */
    public static Uri buildUri(String searchTerm)
    {
        Uri builtUri = Uri.parse(ONLINE_DICTIONARY_BASE_URL)
                .buildUpon()
                .appendPath(LANGUAGE)
                .appendPath(searchTerm.toLowerCase())
                .build();

        return builtUri;
    }

    /**
     * Method to get JSON Object from oxforddictionaries
     * @param url The URL
     * @return the JSON Object
     * @throws IOException
     */
    public static String getResponseFromUrl(URL url) throws IOException
    {
        HttpURLConnection urlConnection =  (HttpURLConnection) url.openConnection();

        if (!testConnection())
            return NO_NETWORK;

        final String app_id = "242e8819";
        final String app_key = "8b3130d87f62115a16a8b93ebdb616b2";
        urlConnection.setRequestProperty("Accept","application/json");
        urlConnection.setRequestProperty("app_id",app_id);
        urlConnection.setRequestProperty("app_key",app_key);

        try
        {
         InputStream inputStream = urlConnection.getInputStream();

          Scanner sc = new Scanner(inputStream);
          sc.useDelimiter("\\A");

            boolean hasData = sc.hasNext();
            if (hasData)
                return sc.next();
            else
                return "Scanner could not read from stream";
        }
        finally
        {
            urlConnection.disconnect();
        }
    }

    /**
     * Method to parse the definitions of a word from a JSON String
     * @param jsonString The JSON String
     * @return A string containing the definitions
     */
    public static String getDefinitions(String jsonString)
    {
        JSONObject dictionary;
        String definition = "";
        int definitionNumber = 1;

        try
        {
            dictionary = new JSONObject(jsonString);
            JSONArray results = dictionary.getJSONArray("results");

            for (int i = 0;i < results.length();i++)
            {
                JSONObject object = results.getJSONObject(i);
                JSONArray lexicalEntries = object.getJSONArray("lexicalEntries");
                for (int j = 0; j < lexicalEntries.length();j++)
                {
                    JSONObject object1 = lexicalEntries.getJSONObject(j);
                    JSONArray entries = object1.getJSONArray("entries");

                    for (int k = 0;k < entries.length();k++)
                    {
                        JSONObject object2 = entries.getJSONObject(k);
                        JSONArray senses = object2.getJSONArray("senses");
                        for (int l = 0;l < senses.length();l++)
                        {
                            JSONObject def = senses.getJSONObject(l);
                            JSONArray defs = def.getJSONArray("definitions");
                            definition += (definitionNumber++ + ". " + defs.getString(0) + "\n");
                        }

                    }
                }

            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return definition;
    }

    /**
     * Method to Test if network is available
     * @return true if network is available else false
     */
    private static final boolean testConnection()
    {
        Uri uri = buildUri(testWord);
        URL url;
        try
        {
            url = new URL(uri.toString());
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return false;
        }

        HttpURLConnection connection;
        try
        {
            connection = (HttpURLConnection) url.openConnection();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        final String app_id = "242e8819";
        final String app_key = "8b3130d87f62115a16a8b93ebdb616b2";
        connection.setRequestProperty("Accept","application/json");
        connection.setRequestProperty("app_id",app_id);
        connection.setRequestProperty("app_key",app_key);
        int responseCode;
        try
        {
            responseCode = connection.getResponseCode();
        }

        catch (IOException e)
        {
            return false;
        }
        if (responseCode == HttpURLConnection.HTTP_OK)
            return true;

        else
            return false;
    }


    /**
     * Method to get the HTML of oxforddictionaries online homepage
     * @return the HTML of oxforddictionaries online homepage
     * @throws IOException if connection error occurs
     */
    public static String getHTMLForWordOfTheDay() throws IOException
    {
        URL url;
        try
        {
            url = new URL(URL_FOR_OXFORD);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return null;
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        InputStream stream = connection.getInputStream();
        Scanner sc = new Scanner(stream);
        sc.useDelimiter("\\A");

        if (sc.hasNext())
            return sc.next();

        return null;
    }

    /**
     * Method to get the Word of the Day
     * @param html a string containing the html contents of the web page
     * @return The Word of the Day
     */
    public static String getWordOfTheDay(String html)
    {
        if (html == null)
            return null;

        //Attempting to get the substring within the html corresponding to the Word of the Day
        String startPoint = "find out what it means";
        int start = html.indexOf(startPoint);
        int newStart = html.indexOf("definition", start) + 11;
        int end = html.indexOf(">", newStart) - 1;

        return html.substring(newStart, end);
    }



}

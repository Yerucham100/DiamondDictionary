package com.example.diacious.diamonddictionary.utils;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by DIACIOUS on 5/27/2018.
 */

public class NetworkUtils
{
    public static final String ONLINE_DICTIONARY_BASE_URL = "https://od-api.oxforddictionaries.com:443/api/v1/entries";
    public final static String LANGUAGE = "en";
    public final static String PARAM_QUERY = "q";


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
                return "no next";
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


}

package com.example.diacious.diamonddictionary;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.diacious.diamonddictionary.utils.DbUtils;
import com.example.diacious.diamonddictionary.utils.NetworkUtils;
import com.example.diacious.diamonddictionary.utils.NotificationUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by DIACIOUS on 6/9/2018.
 */

public class AddWordIntentService extends IntentService
{
    public AddWordIntentService()
    {
        super("AddWordIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        String action = intent.getAction();//ACTION = MainActivity.SEARCH_BOX_EXTRA
        NotificationUtils.clearNotifications(this);
        if (action != null)
        {
            insertNewWord(action, this);
            return;
        }

        if (intent != null)
        {
            String word = intent.getStringExtra(MainActivity.SEARCH_BOX_EXTRA);
            String definition = intent.getStringExtra(MainActivity.DEFINITION_EXTRA);
            DbUtils.insertWordInDatabase(word, definition, this);
        }
    }

    private void insertNewWord(String word, Context context)
    {
        URL url;
        String definitionJSON;

        try
        {
            url = new URL(NetworkUtils.buildUri(word).toString());
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return;
        }

        try
        {
          definitionJSON = NetworkUtils.getResponseFromUrl(url);
          DbUtils.insertWordInDatabase(word, definitionJSON, context);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

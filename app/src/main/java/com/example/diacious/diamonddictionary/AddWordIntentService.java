package com.example.diacious.diamonddictionary;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.diacious.diamonddictionary.utils.DbUtils;

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
        if (intent != null)
        {
            String word = intent.getStringExtra(MainActivity.SEARCH_BOX_EXTRA);
            String definition = intent.getStringExtra(MainActivity.DEFINITION_EXTRA);
            DbUtils.insertWordInDatabase(word, definition, this);
        }
    }
}

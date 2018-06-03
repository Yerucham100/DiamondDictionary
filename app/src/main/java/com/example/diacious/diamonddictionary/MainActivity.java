package com.example.diacious.diamonddictionary;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.diacious.diamonddictionary.utils.DbUtils;
import com.example.diacious.diamonddictionary.utils.NetworkUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>
{

    private EditText searchBoxEditText;
    private Button searchButton;
    private TextView displayTextView;
    private TextView noNetworkTextView;
    private ProgressBar loadingProgressBar;
    private final int LOADER_ID = 132;
    private final String SEARCH_QUERY_URL_EXTRA = "search_query_url";
    private final String SEARCH_WORD_EXTRA = "search_word_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBoxEditText = (EditText) findViewById(R.id.search_box);
        searchButton = (Button) findViewById(R.id.search_btn);
        displayTextView = (TextView) findViewById(R.id.display_tv);
        noNetworkTextView = (TextView) findViewById(R.id.no_network_tv);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loading_pb);

        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startSearch();
            }
        });
    }

    /**
     * Method to search for the word
     */
    public void startSearch()
    {
        String word = searchBoxEditText.getText().toString().toLowerCase();
        Bundle bundle = new Bundle();
        try
        {
            URL url = new URL(NetworkUtils.buildUri(word).toString());
            bundle.putString(SEARCH_QUERY_URL_EXTRA, url.toString());
            bundle.putString(SEARCH_WORD_EXTRA, word);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> myLoader = loaderManager.getLoader(LOADER_ID);

        if (myLoader == null)
            loaderManager.initLoader(LOADER_ID, bundle, this);
        else
            loaderManager.restartLoader(LOADER_ID, bundle, this);
    }
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args)
    {
        return new AsyncTaskLoader<String>(this)
        {
            @Override
            protected void onStartLoading()
            {
                if (args == null)
                    return;

                super.onStartLoading();
                loadingProgressBar.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public String loadInBackground()
            {
                URL searchUrl;
                String word;
                try
                {
                    searchUrl = new URL(args.getString(SEARCH_QUERY_URL_EXTRA));
                    word = args.getString(SEARCH_WORD_EXTRA);
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                    return null;
                }
                String meaning = null;
                try
                {
                    if (DbUtils.getInfoFromDatabase(word, MainActivity.this))
                    {
                        Log.d("WORD FOUND=", word);
                        meaning = DbUtils.getMeaning();
                        DbUtils.updateWord();
                        return meaning;
                    }

                    //If word is not found, search online and insert it in the database
                    meaning = NetworkUtils.getResponseFromUrl(searchUrl);

                    if (!(meaning.equals(NetworkUtils.NO_NETWORK) || meaning.equals(NetworkUtils.WORD_NOT_FOUND)))
                    {
                        DbUtils.insertWordInDatabase(word, meaning, MainActivity.this);//TODO This should be done by a service
                    }

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                return meaning;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data)
    {
        loadingProgressBar.setVisibility(View.INVISIBLE);

        String definition;
        if (data == null)
            displayTextView.setText(getString(R.string.word_not_found));

        else if (data.equals(NetworkUtils.NO_NETWORK))
        {
            displayTextView.setVisibility(View.INVISIBLE);
            noNetworkTextView.setVisibility(View.VISIBLE);
        }

        else if (data != null && data != "")
        {
            displayTextView.setVisibility(View.VISIBLE);
            noNetworkTextView.setVisibility(View.INVISIBLE);
            definition = NetworkUtils.getDefinitions(data);
            displayTextView.setText(definition);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader)
    {

    }

}

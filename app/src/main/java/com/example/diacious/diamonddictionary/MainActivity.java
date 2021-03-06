package com.example.diacious.diamonddictionary;


import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.diacious.diamonddictionary.utils.DateUtils;
import com.example.diacious.diamonddictionary.utils.DbUtils;
import com.example.diacious.diamonddictionary.utils.NetworkUtils;
import com.example.diacious.diamonddictionary.utils.NotificationUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String[]>
{

    private EditText searchBoxEditText;
    private Button searchButton;
    private ScrollView displayScrollView;
    private TextView displayTextView;
    private TextView noNetworkTextView;
    private TextView lastSearchedTextView;
    private TextView searchFreqTextView;
    private TextView lastSearchedLabelTextView;
    private TextView searchFreqLabelTextView;
    private ProgressBar loadingProgressBar;
    private final int LOADER_ID = 132;
    private final String SEARCH_QUERY_URL_EXTRA = "search_query_url";
    private final String SEARCH_WORD_EXTRA = "search_word_extra";

    public static final String SEARCH_BOX_EXTRA = "search_box";
    public static final String DEFINITION_EXTRA = "def_extra";

    private String currentWord = "";
    private boolean wordInDatabase = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBoxEditText = (EditText) findViewById(R.id.search_box);
        searchButton = (Button) findViewById(R.id.search_btn);
        displayTextView = (TextView) findViewById(R.id.display_tv);
        noNetworkTextView = (TextView) findViewById(R.id.no_network_tv);
        lastSearchedTextView = (TextView) findViewById(R.id.last_searched_tv);
        searchFreqTextView = (TextView) findViewById(R.id.search_freq_tv);
        lastSearchedLabelTextView = (TextView) findViewById(R.id.last_searched_label_tv);
        searchFreqLabelTextView = (TextView) findViewById(R.id.search_freq_label_tv);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loading_pb);

        displayScrollView = (ScrollView) findViewById(R.id.display_sv);

        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String word = searchBoxEditText.getText().toString().toLowerCase();
                startSearch(word);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        WordOfTheDayUtilities.scheduleWordOfTheDayReminder(this);

        if (savedInstanceState != null)
        {
            if (savedInstanceState.containsKey(SEARCH_BOX_EXTRA))
                searchBoxEditText.setText(savedInstanceState.getString(SEARCH_BOX_EXTRA));
            if (savedInstanceState.containsKey(DEFINITION_EXTRA))
                displayTextView.setText(savedInstanceState.getString(DEFINITION_EXTRA));
        }

        Intent thatStartedThisActivity = getIntent();
        if (thatStartedThisActivity != null)
            if (thatStartedThisActivity.hasExtra(SEARCH_BOX_EXTRA))
            {
                NotificationUtils.clearNotifications(this);
                String word = thatStartedThisActivity.getStringExtra(SEARCH_BOX_EXTRA);
                searchBoxEditText.setText(word);
                startSearch(word);
            }


    }

    /**
     * Method to search for the word
     * @param word The word to be searched for
     */
    public void startSearch(String word)
    {
        if (currentWord.equals(word))
            return;

        currentWord = word;

        if (word.equals("") || TextUtils.isEmpty(word))
            return;

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
    public Loader<String[]> onCreateLoader(int id, final Bundle args)
    {
        return new AsyncTaskLoader<String[]>(this)
        {
            String[] searchCache = null;
            @Override
            protected void onStartLoading()
            {
                if (args == null)
                    return;

                if (searchCache != null)
                    return;//deliverResult(searchCache);


                loadingProgressBar.setVisibility(View.VISIBLE);
                searchFreqTextView.setVisibility(View.INVISIBLE);
                searchFreqLabelTextView.setVisibility(View.INVISIBLE);
                lastSearchedTextView.setVisibility(View.INVISIBLE);
                lastSearchedLabelTextView.setVisibility(View.INVISIBLE);
                forceLoad();
            }

            @Override
            public String[] loadInBackground()
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
                String frequency = null;
                String lastSearched = null;
                try
                {
                    if (DbUtils.getInfoFromDatabase(word, MainActivity.this))
                    {
                        meaning = DbUtils.getMeaning();
                        frequency = DbUtils.getSearchFrequency();
                        lastSearched = DbUtils.getLastSearched();

                        DbUtils.updateWord(MainActivity.this);
                        return new String[]{meaning, frequency, lastSearched};
                    }

                    //If word is not found, search online and insert it in the database
                    meaning = NetworkUtils.getResponseFromUrl(searchUrl);
                    frequency = getString(R.string.an_integer, 1);
                    String timeStamp = new Timestamp(System.currentTimeMillis()).toString();
                    lastSearched = DateUtils.getDateInStandardFormat(timeStamp);
                    if (!(meaning.equals(NetworkUtils.NO_NETWORK) || meaning.equals(NetworkUtils.WORD_NOT_FOUND)))
                    {
                        wordInDatabase = false;
                    }

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                return new String[]{meaning, frequency, lastSearched};
            }

            @Override
            public void deliverResult(String[] data)
            {
                super.deliverResult(data);
                searchCache = data;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data)
    {
        loadingProgressBar.setVisibility(View.INVISIBLE);
        String definition;
        if (data[0] == null)
        {
            displayTextView.setVisibility(View.VISIBLE);
            displayTextView.setText(getString(R.string.word_not_found, currentWord));
        }

        else if (data[0].equals(NetworkUtils.NO_NETWORK))
        {
            displayTextView.setVisibility(View.INVISIBLE);
            noNetworkTextView.setVisibility(View.VISIBLE);
        }

        else if (data[0] != null && data[0] != "")
        {
            addToDataBase(currentWord, data[0]);

            displayTextView.setVisibility(View.VISIBLE);
            noNetworkTextView.setVisibility(View.INVISIBLE);
            definition = NetworkUtils.getDefinitions(data[0]);
            displayTextView.setText(definition);

            searchFreqTextView.setVisibility(View.VISIBLE);
            searchFreqLabelTextView.setVisibility(View.VISIBLE);
            lastSearchedTextView.setVisibility(View.VISIBLE);
            lastSearchedLabelTextView.setVisibility(View.VISIBLE);

            searchFreqTextView.setText(data[1]);
            lastSearchedTextView.setText(data[2]);
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader)
    {

    }

    /**
     * Method to launch SearchHistoryActivity
     */
    private void openSearchHistory()
    {
        Intent intent = new Intent(this, SearchHistoryActivity.class);
        startActivity(intent);
    }

    /**
     * Method to open AboutActivity
     */
    private void openAbout()
    {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.search_hist_menu_item:
                openSearchHistory();
                break;
            case R.id.about_menu_item:
                openAbout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_BOX_EXTRA, searchBoxEditText.getText().toString());
        outState.putString(DEFINITION_EXTRA, displayTextView.getText().toString());
    }

    /**
     * Method to add to the database
     * @param word the word to be added
     * @param meaning the meaning of the word
     */
    private void addToDataBase(String word, String meaning)
    {
        if (wordInDatabase)
            return;
        Intent intent = new Intent(this, AddWordIntentService.class);
        intent.putExtra(SEARCH_BOX_EXTRA, word);
        intent.putExtra(DEFINITION_EXTRA, meaning);

        startService(intent);
    }
}

package com.example.diacious.diamonddictionary;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class SearchHistoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchHistoryAdapter.ListItemClickListener

{

    private RecyclerView searchHistoryRecyclerView;
    private SearchHistoryAdapter adapter;
    private Cursor searchHistoryCursor;

    private final int LOADER_ID = 133;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);

        searchHistoryRecyclerView = (RecyclerView) findViewById(R.id.search_history_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        searchHistoryRecyclerView.setLayoutManager(layoutManager);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader searchLoader = loaderManager.getLoader(LOADER_ID);

        loaderManager.restartLoader(LOADER_ID, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new AsyncTaskLoader<Cursor>(this)
        {
            @Override
            protected void onStartLoading()
            {
                if (searchHistoryCursor != null)
                    return;

                super.onStartLoading();
                forceLoad();
            }

            @Override
            public Cursor loadInBackground()
            {
                ContentResolver resolver = getContentResolver();
                Cursor cursor = resolver.query(SearchHistoryContract.SearchHistory.CONTENT_URI,
                        null,
                        null,
                        null,
                        SearchHistoryContract.SearchHistory.COLUMN_LAST_SEARCHED + " ASC");

                return cursor;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        searchHistoryCursor = data;
        adapter = new SearchHistoryAdapter(searchHistoryCursor, this);

        searchHistoryRecyclerView.setAdapter(adapter);
        searchHistoryRecyclerView.setHasFixedSize(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }

    @Override
    public void onListItemClicked(String word)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.SEARCH_BOX_EXTRA, word);
        startActivity(intent);
    }
}

package com.example.diacious.diamonddictionary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class SearchHistoryActivity extends AppCompatActivity
{

    private RecyclerView searchHistoryRecyclerView;
    private SearchHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);

        searchHistoryRecyclerView = (RecyclerView) findViewById(R.id.search_history_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        searchHistoryRecyclerView.setLayoutManager(layoutManager);

        adapter = new SearchHistoryAdapter(null);//TODO Create a loader to source this from the db, this should be in MainActivity
        //TODO and sent here via intent
        searchHistoryRecyclerView.setAdapter(adapter);
        searchHistoryRecyclerView.setHasFixedSize(false);

    }
}

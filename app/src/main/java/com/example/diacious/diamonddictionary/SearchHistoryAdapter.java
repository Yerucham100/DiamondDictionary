package com.example.diacious.diamonddictionary;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by DIACIOUS on 6/5/2018.
 */

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.SearchItemViewHolder>
{
    private Cursor mCursor;
    private ListItemClickListener mListItemClickListener;

    public SearchHistoryAdapter(Cursor cursor, ListItemClickListener listener)
    {
        mCursor = cursor;
        mListItemClickListener = listener;
    }
    @Override
    public SearchItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        int idForSearchHistoryView = R.layout.search_item_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(idForSearchHistoryView, parent, shouldAttachToParentImmediately);

        return new SearchItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchItemViewHolder holder, int position)
    {
        int column_word = mCursor.getColumnIndex(SearchHistoryContract.SearchHistory.COLUMN_WORD);
        int column_last_searched = mCursor.getColumnIndex(SearchHistoryContract.SearchHistory.COLUMN_LAST_SEARCHED);
        int column_search_freq = mCursor.getColumnIndex(SearchHistoryContract.SearchHistory.COLUMN_SEARCH_FREQUENCY);

        mCursor.moveToPosition(position);
        String word = mCursor.getString(column_word);
        String last_searched = mCursor.getString(column_last_searched);
        String search_freq = String.valueOf(mCursor.getInt(column_search_freq));

        holder.searchItemTextView.setText(word);
        holder.lastSearchTextView.setText(last_searched);
        holder.searchFreqTextView.setText(search_freq);
    }

    @Override
    public int getItemCount()
    {
        return mCursor.getCount();
    }

    public interface ListItemClickListener
    {
        void onListItemClicked(String word);
    }

    public class SearchItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView searchItemTextView;
        private TextView lastSearchTextView;
        private TextView searchFreqTextView;

        public SearchItemViewHolder(View view)
        {
            super(view);
            searchItemTextView = (TextView) view.findViewById(R.id.search_word_tv);
            lastSearchTextView = (TextView) view.findViewById(R.id.last_search_tv);
            searchFreqTextView = (TextView) view.findViewById(R.id.search_freq_tv_4_search_history_activity);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            mListItemClickListener.onListItemClicked(searchItemTextView.getText().toString());
        }
    }
}

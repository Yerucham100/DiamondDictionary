package com.example.diacious.diamonddictionary;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.diacious.diamonddictionary.utils.NetworkUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
{

    private EditText searchBoxEditText;
    private Button searchButton;
    private TextView displayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBoxEditText = (EditText) findViewById(R.id.search_box);
        searchButton = (Button) findViewById(R.id.search_btn);
        displayTextView = (TextView) findViewById(R.id.display_tv);

        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String word = searchBoxEditText.getText().toString();
                try
                {
                    URL url = new URL(NetworkUtils.buildUri(word).toString());
                    new InternetSearchTask().execute(url);
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public class InternetSearchTask extends AsyncTask<URL, Void, String>
    {
        @Override
        protected String doInBackground(URL... params)
        {
            URL url = params[0];
            String meaning = "nothing found!";
            try
            {
                meaning = NetworkUtils.getResponseFromUrl(url);
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
            return meaning;
        }


        @Override
        protected void onPostExecute(String s)
        {
            displayTextView.setText(NetworkUtils.getDefinitions(s));//TODO Too condensed, work on this
        }
    }
}

package com.example.diacious.diamonddictionary;

import android.os.AsyncTask;

import com.example.diacious.diamonddictionary.utils.NotificationUtils;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by DIACIOUS on 6/9/2018.
 */

public class WordOfTheDayService extends JobService
{
    private AsyncTask mAsyncTask;

    @Override
    public boolean onStartJob(final JobParameters job)
    {
        mAsyncTask = new AsyncTask()
        {
            @Override
            protected Object doInBackground(Object[] params)
            {
                NotificationUtils.informUserOfWordOfTheDay(WordOfTheDayService.this);
                return null;
            }

            @Override
            protected void onPostExecute(Object o)
            {
                super.onPostExecute(o);
                jobFinished(job, false);
            }
        };
        mAsyncTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job)
    {
        if (mAsyncTask != null)
            mAsyncTask.cancel(true);
        return true;
    }
}

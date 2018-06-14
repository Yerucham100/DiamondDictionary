package com.example.diacious.diamonddictionary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Window;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by DIACIOUS on 6/9/2018.
 */

public class WordOfTheDayUtilities
{
    private static final int REMINDER_INTERVAL_HOURS = 24;
    private static final int REMINDER_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(REMINDER_INTERVAL_HOURS);
    private static final int SYNC_FLEX_TIME = (int) TimeUnit.MINUTES.toSeconds(15);

    private static final String JOB_TAG = "get-word-of-the-day";
    private static boolean jobInitialized;


    synchronized public static void scheduleWordOfTheDayReminder(@NonNull final Context context)
    {
        if (jobInitialized)
            return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job job = dispatcher
                .newJobBuilder()
                .setService(WordOfTheDayService.class)
                .setTag(JOB_TAG)
                .setTrigger(Trigger.executionWindow(REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEX_TIME))
                .setLifetime(Lifetime.FOREVER)
                .setReplaceCurrent(true)
                .setRecurring(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .addConstraint(Constraint.ON_ANY_NETWORK)
                .build();

        dispatcher.schedule(job);
        jobInitialized = true;
    }

}

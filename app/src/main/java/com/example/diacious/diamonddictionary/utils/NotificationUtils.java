package com.example.diacious.diamonddictionary.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.example.diacious.diamonddictionary.AddWordIntentService;
import com.example.diacious.diamonddictionary.MainActivity;
import com.example.diacious.diamonddictionary.R;
import java.io.IOException;

/**
 * Created by DIACIOUS on 6/9/2018.
 */

public class NotificationUtils
{
    private static final int PENDING_INTENT_ID = 1215225;//If you know, you know Lol :) Hint = 4L
    private static final int PENDING_INTENT_ID_FOR_NOTIFICATION_ACTION = 2342;
    private static String todaysWord = null;
    private static final int NOTIFICATION_ID = 13;

    /**
     * Method to issue notification on Word of the day
     * @param context the calling Activity/Service Context
     */
    public static void informUserOfWordOfTheDay(Context context)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
                .setLargeIcon(getLargeIcon(context))
                .setContentTitle(context.getString(R.string.word_of_the_day))
                .setContentText(context.getString(R.string.todays_word_is, getTodaysWord()))
                .setContentIntent(getPendingIntent(context, todaysWord))
                .setDefaults(Notification.DEFAULT_SOUND)
                .addAction(getSaveAndDismissAction(context))
                .setAutoCancel(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }

    /**
     * Method to create a pending intent for Notification
     * @param context The calling Activity/Service context
     * @param word The word of the day
     * @return The pending intent
     */
    private static PendingIntent getPendingIntent(Context context, String word)
    {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.SEARCH_BOX_EXTRA, word);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                PENDING_INTENT_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    /**
     * Method to get LargeIcon for notification
     * @param context The calling Activity/Service context
     * @return The large icon
     */
    private static Bitmap getLargeIcon(Context context)
    {
        Resources resources = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(resources, android.R.drawable.ic_menu_my_calendar);

        return largeIcon;
    }

    /**
     * Method to get Word of the Day
     * @return The word of the day
     */
    private static String getTodaysWord()
    {
        try
        {
            String word = NetworkUtils.getWordOfTheDay(NetworkUtils.getHTMLForWordOfTheDay());
            todaysWord = word;
            return word;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Method to create Notification Action that will save a word of the day in the database
     * @param context The calling Activity/Service context
     * @return The Notification action
     */
    private static NotificationCompat.Action getSaveAndDismissAction(Context context)
    {
        Intent intent = new Intent(context, AddWordIntentService.class);
        intent.setAction(MainActivity.SEARCH_BOX_EXTRA);

        PendingIntent pendingIntent = PendingIntent.getService(context,
                PENDING_INTENT_ID_FOR_NOTIFICATION_ACTION,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action(android.R.drawable.ic_menu_my_calendar,
                context.getString(R.string.save_word_and_dismiss), pendingIntent);
        return action;
    }

    /**
     * Method to clear all notifications
     * @param context The calling Activity/Service context
     */
    public static void clearNotifications(Context context)
    {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

}

package com.droidloft.gasup;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        SharedPreferences lastDatePrefs = context.getSharedPreferences("last_days_key", context.MODE_PRIVATE);
        String lastGasDate = lastDatePrefs.getString("last_days_key", null);

        String lastGasDay = lastGasDate.substring(3, 5);
        String lastGasMonth = lastGasDate.substring(0, 2);
        int lastGasDayInt = Integer.parseInt(lastGasDay);
        int lastGasMonthInt = Integer.parseInt(lastGasMonth);

        Calendar theLastDay = Calendar.getInstance();
        theLastDay.set(Calendar.DAY_OF_MONTH, lastGasDayInt);
        theLastDay.set(Calendar.MONTH, lastGasMonthInt - 1);

        Calendar today = Calendar.getInstance();
        long diff = today.getTimeInMillis() - theLastDay.getTimeInMillis();
        long days = ((diff / (24 * 60 * 60 * 1000)));
        int daysint = (int)days;
        String daysSince = Integer.toString(daysint);
        views.setTextViewText(R.id.widget_days_textView, daysSince);

        if(daysint >= 8) {
            views.setTextColor(R.id.widget_days_textView, Color.RED);
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);



        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);


            SharedPreferences lastDatePrefs = context.getSharedPreferences("last_days_key", context.MODE_PRIVATE);
            String lastGasDate = lastDatePrefs.getString("last_days_key", null);

            String lastGasDay = lastGasDate.substring(3, 5);
            String lastGasMonth = lastGasDate.substring(0, 2);
            int lastGasDayInt = Integer.parseInt(lastGasDay);
            int lastGasMonthInt = Integer.parseInt(lastGasMonth);

            Calendar theLastDay = Calendar.getInstance();
            theLastDay.set(Calendar.DAY_OF_MONTH, lastGasDayInt);
            theLastDay.set(Calendar.MONTH, lastGasMonthInt - 1);

            Calendar today = Calendar.getInstance();
            long diff = today.getTimeInMillis() - theLastDay.getTimeInMillis();
            long days = ((diff / (24 * 60 * 60 * 1000)));
            int daysint = (int)days;
            String daysSince = Integer.toString(daysint);
            views.setTextViewText(R.id.widget_days_textView, daysSince);

            if(daysint >= 8) {
                views.setTextColor(R.id.widget_days_textView, Color.RED);
            }

            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);


            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


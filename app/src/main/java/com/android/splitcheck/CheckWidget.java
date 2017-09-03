package com.android.splitcheck;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.android.splitcheck.data.Check;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class CheckWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        Check check = new Check();
        ArrayList<Check> checks = check.getListOfChecks(context.getContentResolver());
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.check_widget);
        if (checks.size() > 0) {
            Check currentCheck = checks.get(checks.size()-1);
//            views.setTextViewText(R.id.appwidget_text, widgetText);
            views.setTextViewText(R.id.widget_title_check_item, currentCheck.getName());
            views.setTextViewText(R.id.widget_date_time_check_item, currentCheck.getFormattedDate());
            views.setTextViewText(R.id.widget_total_check_item, currentCheck.getTotal());

            Intent intent = new Intent(context, CheckDetailActivity.class);
            intent.putExtra("checkId", currentCheck.getId());
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.appwidget_layout, pendingIntent);
        } else {
            Intent intent = new Intent(context, CheckListActivity.class);
            context.startActivity(intent);
        }


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
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


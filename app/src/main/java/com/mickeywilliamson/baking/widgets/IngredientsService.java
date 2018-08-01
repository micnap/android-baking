package com.mickeywilliamson.baking.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViewsService;

import com.mickeywilliamson.baking.models.Ingredient;

public class IngredientsService extends RemoteViewsService {



    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        return (new IngredientsRemoteViewsFactory(this.getApplicationContext(), intent));
    }
}

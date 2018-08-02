package com.mickeywilliamson.baking.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViewsService;

public class IngredientsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new IngredientsRemoteViewsFactory(this.getApplicationContext(), intent));
    }
}

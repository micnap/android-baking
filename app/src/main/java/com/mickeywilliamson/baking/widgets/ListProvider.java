package com.mickeywilliamson.baking.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.mickeywilliamson.baking.R;
import com.mickeywilliamson.baking.models.Ingredient;

import java.util.ArrayList;

public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<Ingredient> mIngredients;
    private Context context;
    private int appWidgetId;

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        if (RemoteFetchService.sIngredients != null) {
            mIngredients = (ArrayList<Ingredient>) RemoteFetchService.sIngredients.clone();
        } else {
            mIngredients = new ArrayList<Ingredient>();
        }

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mIngredients.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_row);
        Ingredient ingredient = mIngredients.get(position);
        remoteViews.setTextViewText(R.id.ingredient_quantity, ingredient.getQuantity());
        remoteViews.setTextViewText(R.id.ingredient_measure, ingredient.getMeasure());
        remoteViews.setTextViewText(R.id.ingredient_name, ingredient.getIngredient());

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

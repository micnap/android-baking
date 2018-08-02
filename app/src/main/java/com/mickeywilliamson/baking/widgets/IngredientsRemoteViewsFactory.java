package com.mickeywilliamson.baking.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.mickeywilliamson.baking.R;
import com.mickeywilliamson.baking.models.Ingredient;
import com.mickeywilliamson.baking.models.Recipe;

import java.util.ArrayList;

/**
 * This class acts as the adapter for the widget's listview.  The listview widget displays the list
 * of ingredients for the chosen recipe in the app widget.
 */
class IngredientsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Recipe mRecipe;
    private ArrayList<Ingredient> mIngredients;
    private Context mContext;

    public IngredientsRemoteViewsFactory(Context context, Intent intent) {
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        mRecipe = RecipeWidgetConfigureActivity.loadRecipePref(context, appWidgetId);
        if (mRecipe != null) {
            mIngredients = mRecipe.getIngredients();
        }
        mContext = context;
    }

    @Override
    public int getCount() {
        return mIngredients.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {

        // Sets the values for the ingredient rows in the widget.
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget_ingredient_row);
        Ingredient ingredient = mIngredients.get(i);
        remoteViews.setTextViewText(R.id.quantity, ingredient.getQuantity());
        remoteViews.setTextViewText(R.id.measure, ingredient.getMeasure());
        remoteViews.setTextViewText(R.id.ingredient, ingredient.getIngredient());

        // Clicking on an item in the listview of ingredients will launch the app
        // and load the recipe in the RecipeDetailActivity.
        // Converts the Recipe object to JSON for passing to the activity.
        // This sidesteps a bug in Android that prevents objects from being passed from
        // app widgets to activities.
        Bundle extras = new Bundle();
        extras.putString(Recipe.RECIPE, Recipe.convertToJsonString(mRecipe));
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        remoteViews.setOnClickFillInIntent(R.id.quantity, fillInIntent);
        remoteViews.setOnClickFillInIntent(R.id.measure, fillInIntent);
        remoteViews.setOnClickFillInIntent(R.id.ingredient, fillInIntent);

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
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
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
}

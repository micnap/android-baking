package com.mickeywilliamson.baking.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.mickeywilliamson.baking.R;
import com.mickeywilliamson.baking.activities.RecipeDetailActivity;
import com.mickeywilliamson.baking.activities.RecipeListActivity;
import com.mickeywilliamson.baking.models.Ingredient;
import com.mickeywilliamson.baking.models.Recipe;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link RecipeWidgetConfigureActivity RecipeWidgetConfigureActivity}
 *
 * Allows the user to choose what recipe they'd like to see in the home screen widget.
 */
public class RecipeWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        Intent intent = new Intent(context, IngredientsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setRemoteAdapter(R.id.ingredients_listview, intent);
        views.setEmptyView(R.id.ingredients_listview, R.id.empty_text);

        // Get the chosen recipe from sharedpreferences.
        Recipe recipe = RecipeWidgetConfigureActivity.loadRecipePref(context, appWidgetId);

        if (recipe != null) {
            views.setTextViewText(R.id.recipe_widget_title, recipe.getName());
        }

        Intent openAppIntent;
        // Open RecipeListActivity if recipe happens to be empty.
        if (recipe == null) {
            openAppIntent = new Intent(context, RecipeListActivity.class);
        // Otherwise, open the specific recipe in its RecipeDetailActivity.
        } else {
            openAppIntent = new Intent(context, RecipeDetailActivity.class);
            openAppIntent.putExtra(Recipe.RECIPE, recipe);
        }
        // When the user clicks on the title of the recipe in the widget, open up the app's
        // RecipeDetailScreen with the recipe loaded.
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.recipe_widget_title, pendingIntent);

        // Set a pendingIntentTemplate for the items in the listview that display the list
        // of ingredients.  (More performant)
        views.setPendingIntentTemplate(R.id.ingredients_listview, pendingIntent);

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
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            RecipeWidgetConfigureActivity.deleteRecipePref(context, appWidgetId);
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


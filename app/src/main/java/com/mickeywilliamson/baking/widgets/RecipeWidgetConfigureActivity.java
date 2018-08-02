package com.mickeywilliamson.baking.widgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.mickeywilliamson.baking.R;
import com.mickeywilliamson.baking.data.RequestInterface;
import com.mickeywilliamson.baking.models.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The configuration screen for the {@link RecipeWidget RecipeWidget} AppWidget.
 */
public class RecipeWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.mickeywilliamson.baking.widgets.RecipeWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final String PREF_RECIPE = "_recipe";
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Spinner mRecipeChoices;
    private RecipeSpinnerAdapter mAdapter;

    public RecipeWidgetConfigureActivity() {
        super();
    }

    // When the ADD WIDGET button is clicked in the config screen,
    // create the app widget.
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = RecipeWidgetConfigureActivity.this;

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RecipeWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.recipe_widget_configure);

        mRecipeChoices = (Spinner) findViewById(R.id.spinner_recipe_choices);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Load the list of recipes for the dropdown list in the config screen from the web.
        loadJSON(this, mRecipeChoices);

        // When an item is selected in the list, store the choice in a sharedPreference.
        mRecipeChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                Recipe chosenRecipe = RecipeSpinnerAdapter.sRecipes.get(pos);
                saveRecipePref(parent.getContext(), mAppWidgetId, chosenRecipe);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }

    // Convert the chosen recipe to a JSON string for storage in sharedPreferences.
    private static void saveRecipePref(Context context, int appWidgetId, Recipe recipe) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        String jsonRecipe = Recipe.convertToJsonString(recipe);
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + PREF_RECIPE, jsonRecipe);
        prefs.apply();
    }

    // Get the chosen recipe from sharedPreferences and convert from a JSON string to a Recipe object.
    static Recipe loadRecipePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String json = prefs.getString(PREF_PREFIX_KEY + appWidgetId + PREF_RECIPE, null);
        return Recipe.convertFromJsonString(json);
    }

    // Delete the recipe from sharedPreferences
    static void deleteRecipePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + PREF_RECIPE);
        prefs.apply();
    }

    // Load the list of recipes a web endpoint using Retrofit.
    private void loadJSON(final RecipeWidgetConfigureActivity parent, final Spinner spinner) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Recipe.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<ArrayList<Recipe>> call = request.getJSON();
        call.enqueue(new Callback<ArrayList<Recipe>>() {

            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {

                // Load the list of recipes into the config screen's dropdown.
                mAdapter = new RecipeSpinnerAdapter(response.body(), parent);
                spinner.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }
}


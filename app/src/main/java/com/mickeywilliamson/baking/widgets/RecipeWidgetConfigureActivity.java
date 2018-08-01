package com.mickeywilliamson.baking.widgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mickeywilliamson.baking.R;
import com.mickeywilliamson.baking.activities.RecipeListActivity;
import com.mickeywilliamson.baking.data.RequestInterface;
import com.mickeywilliamson.baking.models.Ingredient;
import com.mickeywilliamson.baking.models.Recipe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
    private static final String PREF_INGREDIENTS = "_ingredients";
    private static final String PREF_RECIPE = "_recipe_id";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText;
    Spinner mRecipeChoices;
    private RecipeSpinnerAdapter mAdapter;
    ArrayList<Ingredient> ingredients;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = RecipeWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            //String widgetText = mAppWidgetText.getText().toString();

            //saveTitlePref(context, mAppWidgetId, widgetText);

            //saveIngredientsPref(parent.getContext(), mAppWidgetId, ingredients);

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

    public RecipeWidgetConfigureActivity() {
        super();
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.recipe_widget_configure);

        mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);
        mRecipeChoices = (Spinner) findViewById(R.id.spinner_recipe_choices);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        loadJSON(this, mRecipeChoices);


        mRecipeChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                Recipe chosenRecipe = RecipeSpinnerAdapter.sRecipes.get(pos);

                String recipeTitle = chosenRecipe.getName();
                saveTitlePref(parent.getContext(), mAppWidgetId, recipeTitle);


                ingredients = chosenRecipe.getIngredients();
                saveIngredientsPref(parent.getContext(), mAppWidgetId, ingredients);

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

        mAppWidgetText.setText(loadTitlePref(RecipeWidgetConfigureActivity.this, mAppWidgetId));
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveIngredientsPref(Context context, int appWidgetId, ArrayList<Ingredient> ingredients) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        String strIngredients = ingredients == null ? null : new Gson().toJson(ingredients);
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + PREF_INGREDIENTS, strIngredients);
        prefs.apply();
    }

    static ArrayList<Ingredient> loadIngredientsPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String ingredientsJson = prefs.getString(PREF_PREFIX_KEY + appWidgetId + PREF_INGREDIENTS, null);
        if (ingredientsJson != null) {
            //https://stackoverflow.com/questions/14981233/android-arraylist-of-custom-objects-save-to-sharedpreferences-serializable/40237149
            Type type = new TypeToken<List<Ingredient>>(){}.getType();
            return new Gson().fromJson(ingredientsJson, type);
        }
        return null;
    }

    static void deleteIngredientsPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + PREF_INGREDIENTS);
        prefs.apply();
    }

    static void saveRecipePref(Context context, int appWidgetId, Recipe recipe) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        String jsonRecipe = Recipe.convertToJsonString(recipe);
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + PREF_RECIPE, jsonRecipe);
        prefs.apply();
    }

    static Recipe loadRecipePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String json = prefs.getString(PREF_PREFIX_KEY + appWidgetId + PREF_RECIPE, null);
        Recipe recipe = Recipe.convertFromJsonString(json);
        return recipe;
    }

    static void deleteRecipePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + PREF_RECIPE);
        prefs.apply();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

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

                ArrayList<Recipe> recipes = response.body();
                mAdapter = new RecipeSpinnerAdapter(recipes, parent);
                spinner.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }
}


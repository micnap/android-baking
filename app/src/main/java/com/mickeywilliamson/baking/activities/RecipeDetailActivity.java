package com.mickeywilliamson.baking.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.mickeywilliamson.baking.R;
import com.mickeywilliamson.baking.adapters.RecipeExpandableListAdapter;
import com.mickeywilliamson.baking.fragments.StepDetailFragment;
import com.mickeywilliamson.baking.models.Recipe;

import java.util.ArrayList;

/**
 * This activity represents the details of a recipe. It has different presentations for
 * phone and tablet-size devices. On handsets, the activity presents a collapsible
 * list of ingredients and directions. The directions, when touched, lead to a
 * {@link StepDetailActivity} representing the directions for the recipe. On tablets,
 * the activity presents the list of ingredients/directions lists and direction
 * details side-by-side using two vertical panes.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    // The toggle used to determine whether details should fill the screen or split the screen.
    private boolean mTwoPane;
    private Recipe mRecipe;
    private Parcelable mListInstanceState;
    private int mRecipeId;

    private ExpandableListView expListView;
    private RecipeExpandableListAdapter mAdapter;
    private ArrayList<String> headerList;

    private static final String LIST_INSTANCE_STATE = "listview_state";
    public static final String EXTRA_RECIPE = "com.mickeywilliamson.baking.extra.RECIPE";
    private static final String TAG = RecipeDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // If a config change was made, restore the recipe from the savedInstanceState
        if (savedInstanceState != null) {
            if (mRecipe == null) {
                mRecipe = savedInstanceState.getParcelable(Recipe.RECIPE);
            }
            // Restore the scrolling position of the ExpandableListView.
            mListInstanceState = savedInstanceState.getParcelable(LIST_INSTANCE_STATE);
        // If the click to get here came from the RecipeDetailList, get the recipe
        // from the intent.
        } else if (getIntent().getParcelableExtra(Recipe.RECIPE) != null) {
            mRecipe = getIntent().getParcelableExtra(Recipe.RECIPE);
        // If the click to get here came from the app widget, the recipe will be
        // in JSON form and needs to be converted to a Recipe object.  The object
        // couldn't be added as a Parcelable because of a bug in sending parcelables from
        // intents to activities.
        } else if (getIntent().getStringExtra(Recipe.RECIPE) != null) {
            mRecipe = Recipe.convertFromJsonString(getIntent().getStringExtra(Recipe.RECIPE));
        }

        setTitle(mRecipe.getName());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set the the screen to two panes if the container exists in the display.
        if (findViewById(R.id.recipe_detail_container) != null) {

            // The detail container view will be present only in the large-screen layouts
            // (res/values-w900dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
        }

        // If we have room for two panes, display the recipe directions from the StepDetailFragment
        // on the right side of the screen in the recipe_detail_container view.
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(Recipe.RECIPE, mRecipe);
            arguments.putInt(Recipe.STEP, 0);
            StepDetailFragment fragment = new StepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_container, fragment)
                    .commit();
        }

        // Set up the ExpandableListView.  The ExpandableListView displays the Ingredients and the
        // Directions in two lists, separated by collapsible headers.  This way, the ingredients
        // can be collapsed to view just the directions and vice versa.
        expListView = (ExpandableListView) findViewById(R.id.recipe_detail_list);
        headerList = new ArrayList<String>();
        headerList.add("Ingredients");
        headerList.add("Directions");
        mAdapter = new RecipeExpandableListAdapter(this, headerList, mRecipe.getIngredients(), mRecipe.getSteps());
        expListView.setAdapter(mAdapter);

        // Set both sections as expanded by default.
        expListView.expandGroup(0);
        expListView.expandGroup(1);

        // Restore the state of the ExpandedListView after rotation (scroll position,
        // expanded/collapsed headers).
        if (mListInstanceState != null) {
            expListView.onRestoreInstanceState(mListInstanceState);
        }

        // Only directions are clickable (groupPosition = 1).  Ingredients are not clickable.
        // If a direction is clicked, the directions are displayed in a new activity on smaller
        // devices and as a fragment on the right side of the screen on large devices.
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                // If a large device, load the clicked direction into right side of screen.
                if (mTwoPane) {
                    // Only Directions should be clickable.  groupPosition of directions is 1.
                    if (groupPosition == 1) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable(Recipe.RECIPE, mRecipe);
                        arguments.putInt(Recipe.STEP, childPosition);
                        StepDetailFragment fragment = new StepDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.recipe_detail_container, fragment)
                                //.addToBackStack(null)
                                .commit();
                    }
                // On small devices, load the direction into a new screen.
                } else {
                    if (groupPosition == 1) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, StepDetailActivity.class);
                        intent.putExtra(Recipe.RECIPE, mRecipe);
                        intent.putExtra(Recipe.STEP, childPosition);

                        context.startActivity(intent);
                    }
                }

                return false;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Recipe.RECIPE, mRecipe);
        outState.putParcelable(LIST_INSTANCE_STATE, expListView.onSaveInstanceState());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {

            // Go back to the recipe listing screen.
            navigateUpTo(new Intent(this, RecipeListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

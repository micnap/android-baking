package com.mickeywilliamson.baking;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.mickeywilliamson.baking.Models.Recipe;

import java.util.ArrayList;

/**
 * An activity representing a single Recipe detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeListActivity}.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    private boolean mTwoPane;

    private Recipe mRecipe;

    private ExpandableListView expListView;
    private RecipeExpandableListAdapter mAdapter;
    private ArrayList<String> headerList;

    private static final String LIST_INSTANCE_STATE = "listview_state";
    private Parcelable mListInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        mRecipe = getIntent().getParcelableExtra(Recipe.RECIPE);
        if (savedInstanceState != null) {
            if (mRecipe == null) {
                mRecipe = savedInstanceState.getParcelable(Recipe.RECIPE);
            }
            mListInstanceState = savedInstanceState.getParcelable(LIST_INSTANCE_STATE);
        }

        setTitle(mRecipe.getName());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        if (findViewById(R.id.recipe_detail_container) != null) {

            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            Log.d("TWOSPANEXISTS", "WHERE IS THE FRAGMENT?");
            Log.d("DETAILACTIVITY", "TWOSPAN IS TRUE");
        } else {
            Log.d("DETAILACTIVITY", "TWOSPAN IS FALSE");
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        /*if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(Recipe.RECIPE,
                    getIntent().getParcelableExtra(Recipe.RECIPE));
            StepDetailFragment fragment = new StepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_container, fragment)
                    .commit();
        }*/

        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(Recipe.RECIPE, mRecipe);
            arguments.putInt(Recipe.STEP, 0);
            StepDetailFragment fragment = new StepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_container, fragment)
                    .commit();
            Log.d("TWOSPANEXISTS", "WHERE IS THE FRAGMENT?");
        }


        expListView = (ExpandableListView) findViewById(R.id.recipe_detail_list);

        headerList = new ArrayList<String>();
        headerList.add("Ingredients");
        headerList.add("Directions");
        mAdapter = new RecipeExpandableListAdapter(this, headerList, mRecipe.getIngredients(), mRecipe.getSteps());
        expListView.setAdapter(mAdapter);
        expListView.expandGroup(0);
        expListView.expandGroup(1);

        if (mListInstanceState != null) {
            expListView.onRestoreInstanceState(mListInstanceState);
        }


        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

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

                        //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    }

                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, StepDetailActivity.class);
                    intent.putExtra(Recipe.RECIPE, mRecipe);
                    intent.putExtra(Recipe.STEP, childPosition);

                    context.startActivity(intent);
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

        Log.d("JJJJJJJJJ", String.valueOf(id));
        if (id == android.R.id.home) {

            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, RecipeListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

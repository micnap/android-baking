package com.mickeywilliamson.baking.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.MenuItem;

import com.mickeywilliamson.baking.R;
import com.mickeywilliamson.baking.fragments.StepDetailFragment;
import com.mickeywilliamson.baking.models.Recipe;

/**
 * An activity representing a single Direction detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * direction details are presented side-by-side with a list of items
 * in a {@link RecipeDetailActivity}.
 */
public class StepDetailActivity extends AppCompatActivity {

    private Recipe mRecipe;
    private int mStep;

    private static final String TAG = StepDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        // Get the recipe and step sent in from the RecipeDetailActivity.
        mRecipe = getIntent().getParcelableExtra(Recipe.RECIPE);
        mStep = getIntent().getIntExtra(Recipe.STEP, 0);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(mRecipe.getSteps().get(mStep).getDescription());

        // Load the directions details via the StepDetailFragment.
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(Recipe.RECIPE, mRecipe);
            arguments.putInt(Recipe.STEP, getIntent().getIntExtra(Recipe.STEP, 0));
            StepDetailFragment fragment = new StepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.step_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            // Up button takes us back to the RecipeDetailActivity.
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra(Recipe.RECIPE, mRecipe);
            navigateUpTo(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

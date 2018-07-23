package com.mickeywilliamson.baking;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mickeywilliamson.baking.dummy.DummyContent;

/**
 * A fragment representing a single Recipe detail screen.
 * This fragment is either contained in a {@link RecipeListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String RECIPE = "recipe";

    /**
     * The dummy content this fragment is presenting.
     */
    private Recipe mRecipe;

    private RecyclerView rvStepsList;

    private StepsListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(RECIPE)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mRecipe = getArguments().getParcelable(RECIPE);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mRecipe.getName());
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail, container, false);

        if (mRecipe != null) {
            ((TextView) rootView.findViewById(R.id.recipe_servings)).setText("Serving size: " + String.valueOf(mRecipe.getServings()));

            IngredientsListAdapter ingredientsListAdapter = new IngredientsListAdapter(getActivity(), mRecipe.getIngredients());
            ((ListView) rootView.findViewById(R.id.ingredients_list)).setAdapter(ingredientsListAdapter);

            rvStepsList = (RecyclerView) rootView.findViewById(R.id.steps_list);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            rvStepsList.setLayoutManager(layoutManager);
            rvStepsList.setHasFixedSize(true);
            mAdapter = new StepsListAdapter(mRecipe.getSteps());
            Log.d("STEPS", String.valueOf(mRecipe.getSteps().size()));
            rvStepsList.setAdapter(mAdapter);

        }

        return rootView;
    }
}

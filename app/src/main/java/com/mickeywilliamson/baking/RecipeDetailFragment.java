package com.mickeywilliamson.baking;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private ExpandableListView expListView;
    private RecipeExpandableListAdapter mAdapter;
    private ArrayList<String> headerList;


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

            Toolbar toolbar = (Toolbar) activity.findViewById(R.id.detail_toolbar);
            ((AppCompatActivity)activity).setSupportActionBar(toolbar);
            if (toolbar != null) {
                toolbar.setTitle(mRecipe.getName());
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail, container, false);

        if (mRecipe == null) {
            return rootView;
        }

        //((TextView) rootView.findViewById(R.id.recipe_servings)).setText("Serves: " + String.valueOf(mRecipe.getServings()));

        expListView = (ExpandableListView) rootView.findViewById(R.id.recipe_detail_list);

        headerList = new ArrayList<String>();
        headerList.add("Ingredients");
        headerList.add("Directions");
        mAdapter = new RecipeExpandableListAdapter(getActivity(), headerList, mRecipe.getIngredients(), mRecipe.getSteps());
        expListView.setAdapter(mAdapter);
        expListView.expandGroup(0);
        expListView.expandGroup(1);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                // Only Directions should be clickable.  groupPosition of directions is 1.
                if (groupPosition == 1) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(RecipeDetailFragment.RECIPE, mRecipe);
                    arguments.putInt(Recipe.STEP, childPosition);
                    StepDetailFragment fragment = new StepDetailFragment();
                    fragment.setArguments(arguments);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recipe_detail_container, fragment)
                            .commit();
                }
                return false;
            }
        });





        return rootView;
    }
}

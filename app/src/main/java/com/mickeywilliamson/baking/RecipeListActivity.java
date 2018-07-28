package com.mickeywilliamson.baking;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mickeywilliamson.baking.Models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private ArrayList<Recipe> recipes;
    private SimpleItemRecyclerViewAdapter adapter;

    private static final String TAG = RecipeListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.recipe_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            //mTwoPane = true;
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recipe_list);
        assert recyclerView != null;

        loadJSON(this, recyclerView);

        int spanCount = 2;
        //if (mTwoPane) {
            //spanCount = 1;
        //}
        GridLayoutManager manager = new GridLayoutManager(this, calculateNumberOfColumns(2));
        recyclerView.setLayoutManager(manager);

    }

    // https://medium.com/@dds861/json-parsing-using-retrofit-and-recycleview-2300d9fdcf15
    private void loadJSON(final RecipeListActivity parent, final RecyclerView recyclerView) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Recipe.class, new RecipeDeserializer())
                .create();
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

                adapter = new SimpleItemRecyclerViewAdapter(parent, recipes, mTwoPane);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final RecipeListActivity mParentActivity;
        private final boolean mTwoPane;
        private ArrayList<Recipe> mRecipes;

        SimpleItemRecyclerViewAdapter(RecipeListActivity parent,
                                      ArrayList<Recipe> recipes,
                                      boolean twoPane) {
            mRecipes = recipes;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_list_row, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.mRecipeNameView.setText(mRecipes.get(position).getName());


            String image = mRecipes.get(position).getImage();

            // An empty image will cause a crash by Picasso on load.  If image is null, it supplies the error image.
            Picasso.get()
                    .load(image.isEmpty() ? null : image)
                    .placeholder(Recipe.getPlaceholderImage(mRecipes.get(position).getId()))
                    .error(Recipe.getPlaceholderImage(mRecipes.get(position).getId()))
                    .into(holder.mRecipeImageView);

            holder.itemView.setTag(mRecipes.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mRecipes.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            final TextView mRecipeNameView;
            final ImageView mRecipeImageView;


            ViewHolder(View view) {
                super(view);
                mRecipeNameView = (TextView) view.findViewById(R.id.recipe_name);
                mRecipeImageView = (ImageView) view.findViewById(R.id.recipe_image);
            }
        }

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recipe recipe = (Recipe) view.getTag();
                /*if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(Recipe.RECIPE, recipe);
                    RecipeDetailFragment fragment = new RecipeDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recipe_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, RecipeDetailActivity.class);
                    intent.putExtra(Recipe.RECIPE, recipe);

                    context.startActivity(intent);
                }*/
                Context context = view.getContext();
                Intent intent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtra(Recipe.RECIPE, recipe);

                context.startActivity(intent);
            }
        };
    }


    //The following 3 methods derived from https://android--examples.blogspot.com/2017/03/android-recyclerview-in-different.html
    // Custom method to calculate number of columns for grid type recycler view
    protected int calculateNumberOfColumns(int base){
        int columns = base;
        String screenSize = getScreenSizeCategory();

        if(screenSize.equals("small")){
            if(base!=1){
                columns = columns-1;
            }
        }else if (screenSize.equals("normal")){
            // Do nothing
        }else if(screenSize.equals("large")){
            columns += 0;
        }else if (screenSize.equals("xlarge")){
            columns += 1;
        }

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            columns = (int) (columns * 1.5);
        }

        return columns;
    }

    // Custom method to get screen current orientation
    protected String getScreenOrientation(){
        String orientation = "undefined";

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            orientation = "landscape";
        }else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            orientation = "portrait";
        }

        return orientation;
    }

    // Custom method to get screen size category
    protected String getScreenSizeCategory(){
        int screenLayout = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        switch(screenLayout){
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                // small screens are at least 426dp x 320dp
                return "small";
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                // normal screens are at least 470dp x 320dp
                return "normal";
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                // large screens are at least 640dp x 480dp
                return "large";
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                // xlarge screens are at least 960dp x 720dp
                return "xlarge";
            default:
                return "undefined";
        }
    }

}

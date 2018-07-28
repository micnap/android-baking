package com.mickeywilliamson.baking.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import com.mickeywilliamson.baking.R;
import com.mickeywilliamson.baking.data.RequestInterface;
import com.mickeywilliamson.baking.data.RecipeDeserializer;
import com.mickeywilliamson.baking.models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An activity representing a list of recipes. This activity is used on both
 * narrow  and wide width devices. The number of recipes per row is generated
 * dynamically based on device size. When a recipe is clicked, recipe details
 * are displayed in {@link RecipeDetailActivity}.
 */
public class RecipeListActivity extends AppCompatActivity {

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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recipe_list);
        assert recyclerView != null;

        loadJSON(this, recyclerView);

        // Uses a grid to display the recipes.
        GridLayoutManager manager = new GridLayoutManager(this, calculateNumberOfColumns(2));
        recyclerView.setLayoutManager(manager);
    }

    /**
     * Pulls recipe data in from network location, parses the JSON into Recipe objects,
     * and attaches the list of recipes to the recyclerview..
     * (Derived from https://medium.com/@dds861/json-parsing-using-retrofit-and-recycleview-2300d9fdcf15)
     *
     * @param parent
     *      The context
     * @param recyclerView
     *      The recyclerView
     */
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
                adapter = new SimpleItemRecyclerViewAdapter(parent, recipes);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }

    /**
     * The adapter for the recipe recyclerview.
     */
    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final RecipeListActivity mParentActivity;
        private ArrayList<Recipe> mRecipes;

        // Constructor.
        SimpleItemRecyclerViewAdapter(RecipeListActivity parent, ArrayList<Recipe> recipes) {
            mRecipes = recipes;
            mParentActivity = parent;

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

        // Click listener - when the user clicks on a recipe, they are sent to the recipe's details.
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Recipe recipe = (Recipe) view.getTag();
                Context context = view.getContext();
                Intent intent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtra(Recipe.RECIPE, recipe);
                context.startActivity(intent);
            }
        };
    }

    /**
     * Utility methods for determining the number of recipes to show in a row based
     * on the size and orientation of the device.
     *
     * @param base
     *      The starting point for number of items in a row.  This is the number of recipes
     *      displayed on the smallest screen.  The number is increases according to screen size.
     * @return
     *      Returns the number of recipes to show in a row.
     *
     * This and the following two methods derived from
     * https://android--examples.blogspot.com/2017/03/android-recyclerview-in-different.html
     */
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

        // If the devices is in landscape mode, increase the number of recipes displayed by 1.5.
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            columns = (int) (columns * 1.5);
        }

        return columns;
    }

    /**
     * Utility method to get screen orientation.
     *
     * @return
     *      Returns the orientation as a string (landscape, portrait).
     */
    protected String getScreenOrientation(){
        String orientation = "undefined";

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            orientation = "landscape";
        }else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            orientation = "portrait";
        }

        return orientation;
    }

    /**
     * Utility method to determine the device size.
     *
     * @return
     *      Returns the size of the device as a string (small, normal, large, xlarge).
     */
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

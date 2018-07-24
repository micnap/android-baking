package com.mickeywilliamson.baking;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.io.IOException;
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

    private static final String TAG = StepsListAdapter.class.getSimpleName();

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
            mTwoPane = true;
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recipe_list);
        assert recyclerView != null;

        loadJSON(this, recyclerView);


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
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(RecipeDetailFragment.RECIPE, recipe);
                    RecipeDetailFragment fragment = new RecipeDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recipe_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, RecipeDetailActivity.class);
                    intent.putExtra(RecipeDetailFragment.RECIPE, recipe);

                    context.startActivity(intent);
                }
            }
        };
    }
}

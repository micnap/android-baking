package com.mickeywilliamson.baking.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.mickeywilliamson.baking.R;
import com.mickeywilliamson.baking.models.Recipe;

import java.util.ArrayList;

/**
 * Adapter for spinner dropdown from which the user can choose a recipe on the
 * configuration screen.
 */
class RecipeSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    public static ArrayList<Recipe> sRecipes;
    private Context mContext;


    public RecipeSpinnerAdapter(ArrayList<Recipe> recipes, Context context) {
        sRecipes = recipes;
        mContext = context;
    }

    @Override
    public int getCount() {
        return sRecipes.size();
    }

    @Override
    public Object getItem(int i) {
        return sRecipes.get(i).getName();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        return createView(i, convertView, viewGroup);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int i, View view, ViewGroup viewGroup) {
        TextView tvRecipeName;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.recipe_widget_spinner_row, viewGroup, false);

            tvRecipeName = (TextView) view.findViewById(R.id.recipe_name);

            tvRecipeName.setText(sRecipes.get(i).getName());
        }
        return view;
    }
}

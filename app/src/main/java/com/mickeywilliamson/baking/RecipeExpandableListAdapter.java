package com.mickeywilliamson.baking;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeExpandableListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = RecipeExpandableListAdapter.class.getSimpleName();

    Context mContext;
    ArrayList<String> mHeaders;
    ArrayList<Ingredient> mIngredients;
    ArrayList<Step> mSteps;

    public RecipeExpandableListAdapter(Context context, ArrayList<String> headerList, ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {
        mContext = context;
        mHeaders = headerList;
        mIngredients = ingredients;
        mSteps = steps;
    }

    @Override
    public int getGroupCount() {
        return mHeaders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == 0) {
            Log.d("WHICH GROUP", String.valueOf(groupPosition));
            Log.d("SIZE", String.valueOf(mIngredients.size()));
            return mIngredients.size();
        } else {
            Log.d("WHICH GROUP", String.valueOf(groupPosition));
            Log.d("SIZE", String.valueOf(mSteps.size()));
            return mSteps.size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mHeaders.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (groupPosition == 0) {
            //Log.d("Ingredient", mIngredients.get(childPosition).getIngredient());
            return mIngredients.get(childPosition);
        } else {
            //Log.d("Step", mSteps.get(childPosition - mIngredients.size()).getShortDescription());
            return mSteps.get(childPosition);
        }
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {

        String headerText = (String) getGroup(groupPosition);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.recipe_detail_header, null);
        }

        TextView tvHeader = (TextView) view.findViewById(R.id.recipe_detail_header);
        tvHeader.setText(headerText);

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastchild, View view, ViewGroup parent) {

        Object child = getChild(groupPosition, childPosition);

        Log.d("GroupPosition", String.valueOf(groupPosition));
        Log.d("childPosition", String.valueOf(childPosition));

        int layout = -1;
        Ingredient ingredient = null;
        Step step = null;

        if (groupPosition == 0) {
            layout = R.layout.recipe_detail_child_ingredient;
            ingredient = (Ingredient) child;
        } else {
            layout = R.layout.recipe_detail_child_step;
            step = (Step) child;
        }




            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);


        if  (groupPosition == 0) {

            TextView tvQuantity = (TextView) view.findViewById(R.id.ingredient_quantity);
            TextView tvMeasure = (TextView) view.findViewById(R.id.ingredient_measure);
            TextView tvIngredient = (TextView) view.findViewById(R.id.ingredient_name);

            tvQuantity.setText(ingredient.getQuantity());
            tvMeasure.setText(ingredient.getMeasure());
            tvIngredient.setText(ingredient.getIngredient());

        } else {

            TextView tvId = (TextView) view.findViewById(R.id.step_id);
            TextView tvShortDescription = (TextView) view.findViewById(R.id.step_short_description);

            tvId.setText(String.valueOf(step.getStepId()) + ". ");
            tvShortDescription.setText(step.getShortDescription());

        }
        return view;

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}

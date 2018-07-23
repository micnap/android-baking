package com.mickeywilliamson.baking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class IngredientsListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Ingredient> mIngredients;

    public IngredientsListAdapter(Context context, ArrayList<Ingredient> ingredients) {
        mContext = context;
        mIngredients = ingredients;
    }

    @Override
    public int getCount() {
        return mIngredients.size();
    }

    @Override
    public Object getItem(int i) {
        return mIngredients.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        ViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.ingredient_row, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Ingredient currentIngredient = (Ingredient) getItem(i);

        viewHolder.tvIngredientQuantity.setText(String.valueOf(currentIngredient.getQuantity()));
        viewHolder.tvIngredientMeasure.setText(currentIngredient.getMeasure());
        viewHolder.tvIngredientName.setText(currentIngredient.getIngredient());

        return view;
    }

    private class ViewHolder {
        TextView tvIngredientQuantity;
        TextView tvIngredientMeasure;
        TextView tvIngredientName;

        public ViewHolder(View view) {
            tvIngredientQuantity = (TextView) view.findViewById(R.id.ingredient_quantity);
            tvIngredientMeasure = (TextView) view.findViewById(R.id.ingredient_measure);
            tvIngredientName = (TextView) view.findViewById(R.id.ingredient_name);
        }
    }
}

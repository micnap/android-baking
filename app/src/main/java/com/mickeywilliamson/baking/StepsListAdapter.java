package com.mickeywilliamson.baking;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class StepsListAdapter extends RecyclerView.Adapter<StepsListAdapter.StepsViewHolder> {

    private static final String TAG = StepsListAdapter.class.getSimpleName();
    private ArrayList<Step> mSteps;

    public StepsListAdapter(ArrayList<Step> steps) {
        mSteps = steps;
    }


    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.step_row, parent, false);
        StepsViewHolder viewHolder = new StepsViewHolder(v);



        //LayoutInflater inflater = LayoutInflater.from(context);
        //View view = inflater.inflate(R.layout.step_row, parent, false);
        //StepsViewHolder viewHolder = new StepsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StepsViewHolder holder, int position) {
        holder.tvStepId.setText(String.valueOf(mSteps.get(position).getStepId() + 1));
        holder.tvStepShortDescription.setText(mSteps.get(position).getShortDescription());
    }

    @Override
    public int getItemCount() {
        return mSteps.size();
    }

    public static class StepsViewHolder extends RecyclerView.ViewHolder {
        TextView tvStepId;
        TextView tvStepShortDescription;
        TextView tvStepDescription;


        public StepsViewHolder(View itemView) {
            super(itemView);
            tvStepId =itemView.findViewById(R.id.step_id);
            tvStepShortDescription = itemView.findViewById(R.id.step_short_description);
        }
    }
}

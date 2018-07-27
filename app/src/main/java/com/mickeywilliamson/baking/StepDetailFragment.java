package com.mickeywilliamson.baking;


import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.mickeywilliamson.baking.Models.Recipe;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StepDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepDetailFragment extends Fragment {

    private Recipe mRecipe;
    private int mStep;

    private TextView tvShortDescription;
    private TextView tvDescription;
    private ImageView ivImage;
    private Button btnPrevious;
    private Button btnNext;
    private LinearLayout llStepDetailHolder;
    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoPlayer;
    private android.support.design.widget.CoordinatorLayout mActivityContainer;


    public StepDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     *
     * @return A new instance of fragment StepDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StepDetailFragment newInstance(Recipe recipe, int step) {
        StepDetailFragment fragment = new StepDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(Recipe.RECIPE, recipe);
        args.putInt(Recipe.STEP, step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipe = getArguments().getParcelable(Recipe.RECIPE);
            mStep = getArguments().getInt(Recipe.STEP);
        }

        //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mStep = savedInstanceState.getInt(Recipe.STEP);
        }



        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step_detail, container, false);
        tvShortDescription = view.findViewById(R.id.step_short_description);
        tvDescription = view.findViewById(R.id.step_description);
        mPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.exo_player);
        llStepDetailHolder = (LinearLayout) view.findViewById(R.id.ll_step_detail_holder);
        ivImage = (ImageView) view.findViewById(R.id.step_image);

        mActivityContainer = getActivity().findViewById(R.id.detail_parent_layout);

        btnPrevious = view.findViewById(R.id.btn_previous);
        if (mStep <= 0) {
            btnPrevious.setEnabled(false);
        } else {
            btnPrevious.setOnClickListener(btnPreviousClickListener);
        }
        btnNext = view.findViewById(R.id.btn_next);
        if (mStep >= mRecipe.getSteps().size() - 1) {
            btnNext.setEnabled(false);
        } else {
            btnNext.setOnClickListener(btnNextClickListener);
        }


        tvShortDescription.setText(mRecipe.getSteps().get(mStep).getShortDescription());
        tvDescription.setText(mRecipe.getSteps().get(mStep).getDescription());

        String imgUrl = mRecipe.getSteps().get(mStep).getThumbnailURL();
        if (imgUrl !=null && !imgUrl.isEmpty()) {
            ivImage.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(imgUrl)
                    .placeholder(R.drawable.brownie)
                    .error(R.drawable.brownie)
                    .into(ivImage);
        }

        String videoUrl = mRecipe.getSteps().get(mStep).getVideoURL();
        if (videoUrl != null && !videoUrl.isEmpty()) {
            initializePlayer(Uri.parse(videoUrl));
        } else {
            mPlayerView.setVisibility(View.GONE);
        }

        Log.d("GRRRR", "STEP DETAIL FRAGMENT");

        return view;
    }

    /**
     * Initialize ExoPlayer.
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getActivity(), "ClassicalMusicQuiz");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private View.OnClickListener btnPreviousClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mStep <= 0) {
                return;
            }
            mStep = mStep - 1;
            tvShortDescription.setText(mRecipe.getSteps().get(mStep).getShortDescription());
            tvDescription.setText(mRecipe.getSteps().get(mStep).getDescription());


            if (mStep <= 0) {
                btnPrevious.setEnabled(false);
            } else {
                btnPrevious.setEnabled(true);
                btnPrevious.setOnClickListener(btnPreviousClickListener);
            }

            if (mStep >= mRecipe.getSteps().size() - 1) {
                btnNext.setEnabled(false);
            } else {
                btnNext.setEnabled(true);
                btnNext.setOnClickListener(btnNextClickListener);
            }

            String imgUrl = mRecipe.getSteps().get(mStep).getThumbnailURL();
            if (imgUrl !=null && !imgUrl.isEmpty()) {
                ivImage.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(imgUrl)
                        .placeholder(R.drawable.brownie)
                        .error(R.drawable.brownie)
                        .into(ivImage);
            } else {
                ivImage.setVisibility(View.GONE);
            }

            String videoUrl = mRecipe.getSteps().get(mStep).getVideoURL();
            Log.d("VIDEOURL", videoUrl);
            if (videoUrl != null && !videoUrl.isEmpty()) {
                if (mPlayerView.getVisibility() == View.GONE) {
                    mPlayerView.setVisibility(View.VISIBLE);
                } else {
                    releasePlayer();
                }
                initializePlayer(Uri.parse(videoUrl));
            } else {
                Log.d("VIDEOURL", videoUrl);
                if (mPlayerView.getVisibility() != View.GONE) {
                    releasePlayer();
                }
                mPlayerView.setVisibility(View.GONE);

            }
        }
    };

    private View.OnClickListener btnNextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mStep >= mRecipe.getSteps().size() - 1) {
                return;
            }

            mStep = mStep + 1;
            tvShortDescription.setText(mRecipe.getSteps().get(mStep).getShortDescription());
            tvDescription.setText(mRecipe.getSteps().get(mStep).getDescription());

            if (mStep <= 0) {
                btnPrevious.setEnabled(false);
            } else {
                btnPrevious.setEnabled(true);
                btnPrevious.setOnClickListener(btnPreviousClickListener);
            }

            if (mStep >= mRecipe.getSteps().size() - 1) {
                btnNext.setEnabled(false);
            } else {
                btnNext.setEnabled(true);
                btnNext.setOnClickListener(btnNextClickListener);
            }

            String imgUrl = mRecipe.getSteps().get(mStep).getThumbnailURL();
            if (imgUrl !=null && !imgUrl.isEmpty()) {
                ivImage.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(imgUrl)
                        .placeholder(R.drawable.brownie)
                        .error(R.drawable.brownie)
                        .into(ivImage);
            } else {
                ivImage.setVisibility(View.GONE);
            }

            String videoUrl = mRecipe.getSteps().get(mStep).getVideoURL();
            if (videoUrl != null && !videoUrl.isEmpty()) {
                if (mPlayerView.getVisibility() == View.GONE) {
                    mPlayerView.setVisibility(View.VISIBLE);
                } else {
                    releasePlayer();
                }
                initializePlayer(Uri.parse(videoUrl));
            } else {
                Log.d("VIDEOURL", videoUrl);
                if (mPlayerView.getVisibility() != View.GONE) {
                    releasePlayer();
                }

                mPlayerView.setVisibility(View.GONE);

            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(Recipe.STEP, mStep);
    }


    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }
    /**
     * Release the player when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayerView.getVisibility() != View.GONE) {
            releasePlayer();
        }

    }
    // Derived from https://medium.com/fungjai/playing-video-by-exoplayer-b97903be0b33
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int currentOrientation = getResources().getConfiguration().orientation;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tvShortDescription.setVisibility(View.GONE);
            tvDescription.setVisibility((View.GONE));
            btnPrevious.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
            mActivityContainer.setFitsSystemWindows(false);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPlayerView.getLayoutParams();
            params.width=params.MATCH_PARENT;
            params.height=params.MATCH_PARENT;
            mPlayerView.setLayoutParams(params);
            if(((AppCompatActivity) getActivity()).getSupportActionBar()!=null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
            llStepDetailHolder.setPadding(0,0,0,0);
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } else {
            //unhide your objects here.
            tvShortDescription.setVisibility(View.VISIBLE);
            tvDescription.setVisibility((View.VISIBLE));
            btnPrevious.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            mActivityContainer.setFitsSystemWindows(true);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPlayerView.getLayoutParams();
            params.width=params.MATCH_PARENT;
            params.height=600;
            mPlayerView.setLayoutParams(params);
            if(((AppCompatActivity) getActivity()).getSupportActionBar()!=null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            }

            // Restore padding to the LinearLayout
            // https://stackoverflow.com/questions/4275797/view-setpadding-accepts-only-in-px-is-there-anyway-to-setpadding-in-dp
            int padding_in_dp = 30;  // 6 dps
            final float scale = getResources().getDisplayMetrics().density;
            int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
            llStepDetailHolder.setPadding(padding_in_px, padding_in_px, padding_in_px, padding_in_px);

            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Log.d("HHHHHHHHH", String.valueOf(id));
        if (id == android.R.id.home) {


            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //

            //Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
            //intent.putExtra(Recipe.RECIPE, mRecipe);
            //getActivity().navigateUpTo(intent);
            getFragmentManager().popBackStack();
            //getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            return true;
        //
        }
        return super.onOptionsItemSelected(item);
    }
}

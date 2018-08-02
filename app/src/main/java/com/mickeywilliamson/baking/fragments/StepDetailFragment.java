package com.mickeywilliamson.baking.fragments;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.mickeywilliamson.baking.R;
import com.mickeywilliamson.baking.activities.RecipeDetailActivity;
import com.mickeywilliamson.baking.activities.StepDetailActivity;
import com.mickeywilliamson.baking.models.Recipe;
import com.squareup.picasso.Picasso;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link RecipeDetailActivity}
 * in two-pane mode (on tablets) or a {@link StepDetailActivity}
 * on phones.
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

    private static final String TAG = StepDetailFragment.class.getSimpleName();

    // Required empty public constructor
    public StepDetailFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recipe
     *          The Recipe object that contains all the recipe details including
     *          all the steps.
     * @param step
     *          The id of the step (direction) that is to be displayed in this fragment.
     *
     * @return A new instance of fragment StepDetailFragment.
     */
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mStep = savedInstanceState.getInt(Recipe.STEP);
        }

        getActivity().setTitle(mRecipe.getName() + ": " + mRecipe.getSteps().get(mStep).getDescription());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step_detail, container, false);
        tvShortDescription = view.findViewById(R.id.step_short_description);
        tvDescription = view.findViewById(R.id.step_description);
        mPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.exo_player);
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

    // Click listener for Previous button.
    private View.OnClickListener btnPreviousClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (mStep <= 0) {
                return;
            }

            adjustForClick(-1);
        }
    };

    // Click listener for Next button.
    private View.OnClickListener btnNextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mStep >= mRecipe.getSteps().size() - 1) {
                return;
            }

            adjustForClick(1);
        }
    };

    /**
     * When the user clicks next or previous, the screen is updated to show the appropriate step.
     * Previous clicks decrease step by one.
     * Next clicks increase step by one.
     * All views are then updated to reflect the new step.
     *
     * @param direction
     *          1 indicates next, -1 indicated previous
     */
    private void adjustForClick(int direction) {

        mStep = mStep + direction;
        tvShortDescription.setText(mRecipe.getSteps().get(mStep).getShortDescription());
        tvDescription.setText(mRecipe.getSteps().get(mStep).getDescription());

        getActivity().setTitle(mRecipe.getName() + ": " + mRecipe.getSteps().get(mStep).getShortDescription());

        // If we've reached the beginning of the steps, disable the previous button.
        // If we've reached the end of the steps, disable the next button.
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

        // If an image is provided in the data, load the image and display it. Otherwise, hide the view.
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

        // If a video is provided in the data, load and display it.  Otherwise, hide the view.
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(Recipe.STEP, mStep);
    }

    /**
     * Utility function to stop and release the player.
     */
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

    // Hides and restores views when the device is rotated into landscape mode
    // and the video player goes full screen.
    // Derived from https://medium.com/fungjai/playing-video-by-exoplayer-b97903be0b33
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mPlayerView.getVisibility() == View.GONE) {
            return;
        }
        int currentOrientation = getResources().getConfiguration().orientation;

        // Hide all views but the video player.
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tvShortDescription.setVisibility(View.GONE);
            tvDescription.setVisibility((View.GONE));
            btnPrevious.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
            mActivityContainer.setFitsSystemWindows(false);

            if(((AppCompatActivity) getActivity()).getSupportActionBar()!=null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }

            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // Restore the views and config.
        } else {
            tvShortDescription.setVisibility(View.VISIBLE);
            tvDescription.setVisibility((View.VISIBLE));
            btnPrevious.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            mActivityContainer.setFitsSystemWindows(true);

            if(((AppCompatActivity) getActivity()).getSupportActionBar()!=null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            }

            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}

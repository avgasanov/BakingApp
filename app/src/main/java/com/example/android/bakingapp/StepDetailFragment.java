package com.example.android.bakingapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.bakingapp.BakingUtils.NetworkUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StepDetailFragment extends Fragment implements View.OnClickListener , RequestListener<Drawable> {
    public static final String URI_EXTRA_KEY = "param1";
    public static final String DESCRIPTION_EXTRA_KEY = "param2";
    public static final String POSITION_EXTRA_KEY = "param3";
    private static final String TAG = StepDetailFragment.class.getSimpleName();
    public static String LAST_POSITION = "param4";
    public static final String THUMBNAIL_IMAGE_EXTRA_KEY = "param6";
    private static final String PLAYER_AUTO_PLAY = "param8";
    private static final String PLAYER_WINDOW = "param9";
    private static final String PLAYER_POSITION = "param10";


    private static final String USER_AGENT = "BakingApp";
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final DefaultHttpDataSourceFactory DATA_SOURCE_FACTORY =
            new DefaultHttpDataSourceFactory(USER_AGENT, BANDWIDTH_METER);

    private Uri mVideoUri;
    private String mDescription;
    private Uri mThumbnailUri;
    private int mPosition;
    private int mLastPosition;
    private SimpleExoPlayer mPlayer;
    private long mPlayerPosition = 0;
    private boolean mPlayerAutoPlay;
    private int mPlayerStartWindow;

    private OnStepArrowPress mCallBack;

    @BindView(R.id.playerView)
    PlayerView playerView;
    @BindView(R.id.right_arrow_button)
    FloatingActionButton mRightArrowButton;
    @BindView(R.id.left_arrow_button)
    FloatingActionButton mLeftArrowButton;
    @BindView(R.id.no_internet_iv)
    ImageView noInternetImageView;
    @BindView(R.id.recipe_thumbnail_iv)
    ImageView mThumbnailImageView;

    public StepDetailFragment() {
        // Required empty public constructor
    }

    public static StepDetailFragment newInstance(String mediaUri,
                                                 String description,
                                                 int position,
                                                 int lastPosition)  {

        StepDetailFragment fragment = new StepDetailFragment();
        Bundle args = new Bundle();
        args.putString(URI_EXTRA_KEY, mediaUri);
        args.putString(DESCRIPTION_EXTRA_KEY, description);
        args.putInt(POSITION_EXTRA_KEY, position);
        args.putInt(LAST_POSITION, lastPosition);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            initLocalVariables(getArguments());
        } else {
            initLocalVariables(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mVideoUri != null) {
            outState.putString(URI_EXTRA_KEY, mVideoUri.toString());
        }
        if (mThumbnailUri != null) {
            outState.putString(THUMBNAIL_IMAGE_EXTRA_KEY, mThumbnailUri.toString());
        }
        outState.putString(DESCRIPTION_EXTRA_KEY, mDescription);
        outState.putInt(POSITION_EXTRA_KEY, mPosition);
        outState.putInt(LAST_POSITION, mLastPosition);

        //additional
        if (mPlayer != null) {
            updatePlayerState();
            outState.putBoolean(PLAYER_AUTO_PLAY, mPlayerAutoPlay);
            outState.putInt(PLAYER_WINDOW, mPlayerStartWindow);
            outState.putLong(PLAYER_POSITION, mPlayerPosition);
        }
    }

    private void updatePlayerState() {
        if (mPlayer == null) return;
        mPlayerAutoPlay = mPlayer.getPlayWhenReady();
        mPlayerStartWindow = mPlayer.getCurrentWindowIndex();
        mPlayerPosition = Math.max(0, mPlayer.getContentPosition());
    }

    private void initLocalVariables(Bundle arguments) {
        String uriVideo = arguments.getString(URI_EXTRA_KEY);
        String uriThumbnail = arguments.getString(THUMBNAIL_IMAGE_EXTRA_KEY);
        if (arguments != null) {

            if (uriVideo == null || !URLUtil.isValidUrl(uriVideo)) {
                mVideoUri = null;
            } else {
                mVideoUri = Uri.parse(uriVideo).buildUpon().build();
            }

            if (uriThumbnail == null || !URLUtil.isValidUrl(uriThumbnail)) {
                mThumbnailUri = null;
            } else {
                mThumbnailUri = Uri.parse(uriThumbnail).buildUpon().build();
            }

            mDescription= arguments.getString(DESCRIPTION_EXTRA_KEY);
            mPosition = arguments.getInt(POSITION_EXTRA_KEY);
            mLastPosition = arguments.getInt(LAST_POSITION);

            if (arguments.containsKey(PLAYER_POSITION)) {
                mPlayerPosition = arguments.getLong(PLAYER_POSITION);
                mPlayerAutoPlay = arguments.getBoolean(PLAYER_AUTO_PLAY);
                mPlayerStartWindow = arguments.getInt(PLAYER_WINDOW);
            } else {
                mPlayerPosition = C.TIME_UNSET;
                mPlayerAutoPlay = true;
                mPlayerStartWindow = C.INDEX_UNSET;
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater
                .inflate(R.layout.fragment_step_detail, container, false);

        ButterKnife.bind(this, view);

        initializeView(view);

        return view;
    }

    private void initializeView(View view) {
        boolean isConnected = NetworkUtils.isConnected(getContext());

        initializePlayerView();

        TextView stepDesc = view.findViewById(R.id.step_description_tv);

        if (mDescription != null) {
            stepDesc.setText(mDescription);
        } else {
            stepDesc.setVisibility(View.GONE);
        }

        mRightArrowButton.setOnClickListener(this);
        mLeftArrowButton.setOnClickListener(this);

        mRightArrowButton.setVisibility(mLastPosition == mPosition ? View.GONE : View.VISIBLE);
        //check whether there is next position: mLastPosition == mPosition
        mLeftArrowButton.setVisibility(mPosition == 0 ? View.GONE : View.VISIBLE);

        if(mThumbnailUri != null && isConnected) {
            Glide.with(this).load(mThumbnailUri).listener(this).submit();
        } else {
            mThumbnailImageView.setVisibility(View.GONE);
        }
    }

    private void initializePlayerView() {
        boolean isConnected = NetworkUtils.isConnected(getContext());
        if (mVideoUri != null && isConnected) {
            noInternetImageView.setVisibility(View.GONE);
            initializePlayer();
        } else {
            playerView.setVisibility(View.GONE);
            noInternetImageView.setVisibility(
                    mVideoUri != null ? View.VISIBLE : View.GONE);
        }
    }

    private void initializePlayer() {

        DefaultTrackSelector trackSelector = new DefaultTrackSelector(BANDWIDTH_METER);
        RenderersFactory renderersFactory = new DefaultRenderersFactory(getContext());
        mPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);

        MediaSource mediaSource = new ExtractorMediaSource.Factory(DATA_SOURCE_FACTORY).createMediaSource(mVideoUri);
        mPlayer.prepare(mediaSource);
        mPlayer.setPlayWhenReady(mPlayerAutoPlay);

        playerView.setPlayer(mPlayer);
        playerView.setVisibility(View.VISIBLE);

        boolean haveStartPosition = mPlayerStartWindow != C.INDEX_UNSET;
        if (haveStartPosition) {
            mPlayer.seekTo(mPlayerStartWindow, mPlayerPosition);
        }
    }

    private void releasePlayer() {
        if (mPlayer == null) return;
        updatePlayerState();
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStepArrowPress) {
            mCallBack = (OnStepArrowPress) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
        releasePlayer();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Bundle bundle;
        if (id == R.id.right_arrow_button) {
            bundle = mCallBack.OnStepArrowPress(mPosition + 1);
        } else {
            bundle = mCallBack.OnStepArrowPress(mPosition - 1);
        }
        updateFragment(bundle);
    }

    public void updateFragment(Bundle bundle) {
        releasePlayer();
        initLocalVariables(bundle);
        initializeView(getView());
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
        mThumbnailImageView.setVisibility(View.GONE);
        return true;
    }

    @Override
    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
        mThumbnailImageView.setVisibility(View.VISIBLE);
        mThumbnailImageView.setImageDrawable(resource);
        return true;
    }

    public interface OnStepArrowPress {
        Bundle OnStepArrowPress(int position);
    }

    //This is very similar to demo application of exo player
    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayerView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || mPlayer == null) {
            initializePlayerView();
        }
    }
}

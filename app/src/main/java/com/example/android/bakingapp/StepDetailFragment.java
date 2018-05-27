package com.example.android.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.BakingUtils.NetworkUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;


public class StepDetailFragment extends Fragment implements View.OnClickListener{

    public static final String URI_EXTRA_KEY = "param1";
    public static final String DESCRIPTION_EXTRA_KEY = "param2";
    public static final String POSITION_EXTRA_KEY = "param3";
    public static String LAST_POSITION = "param4";
    private static final String PLAYER_POSITION_EXTRA_KEY = "param5";

    private static final String USER_AGENT = "BakingApp";
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final DefaultHttpDataSourceFactory DATA_SOURCE_FACTORY =
            new DefaultHttpDataSourceFactory(USER_AGENT, BANDWIDTH_METER);

    private Uri mUri;
    private String mDescription;
    private int mPosition;
    private int mLastPosition;
    private SimpleExoPlayer mPlayer;
    private long mPlayerPosition = 0;

    private OnStepArrowPress mCallBack;

    @BindView(R.id.playerView)
    PlayerView playerView;
    @BindView(R.id.right_arrow_button)
    FloatingActionButton mRightArrowButton;
    @BindView(R.id.left_arrow_button)
    FloatingActionButton mLeftArrowButton;
    @BindView(R.id.no_internet_iv)
    ImageView noInternetImageView;

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
        outState.putString(URI_EXTRA_KEY, mUri.toString());
        outState.putString(DESCRIPTION_EXTRA_KEY, mDescription);
        outState.putInt(POSITION_EXTRA_KEY, mPosition);
        outState.putInt(LAST_POSITION, mLastPosition);

        //additional
        outState.putLong(PLAYER_POSITION_EXTRA_KEY, mPlayer.getCurrentPosition());
    }

    private void initLocalVariables(Bundle arguments) {
        String uriExtra = arguments.getString(URI_EXTRA_KEY);
        if (arguments != null) {
            if (uriExtra == null || !URLUtil.isValidUrl(uriExtra)) {
                mUri = null;
            } else {
                mUri = Uri.parse(uriExtra).buildUpon().build();
            }
            mDescription= arguments.getString(DESCRIPTION_EXTRA_KEY);
            mPosition = arguments.getInt(POSITION_EXTRA_KEY);
            mLastPosition = arguments.getInt(LAST_POSITION);

            if (arguments.containsKey(PLAYER_POSITION_EXTRA_KEY)) {
                mPlayerPosition = arguments.getLong(PLAYER_POSITION_EXTRA_KEY);
            } else {
                mPlayerPosition = 0;
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
        if (mUri != null && isConnected) {
            noInternetImageView.setVisibility(View.GONE);
            initializePlayer();
        } else {
            playerView.setVisibility(View.GONE);
            noInternetImageView.setVisibility(
                    mUri != null ? View.VISIBLE : View.GONE);
        }

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
    }

    private void initializePlayer() {

        DefaultTrackSelector trackSelector = new DefaultTrackSelector(BANDWIDTH_METER);
        RenderersFactory renderersFactory = new DefaultRenderersFactory(getContext());
        mPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);

        MediaSource mediaSource = new ExtractorMediaSource.Factory(DATA_SOURCE_FACTORY).createMediaSource(mUri);
        mPlayer.prepare(mediaSource);
        mPlayer.setPlayWhenReady(true);

        playerView.setPlayer(mPlayer);
        playerView.setVisibility(View.VISIBLE);

        mPlayer.seekTo(mPlayerPosition);
    }

    private void releasePlayer() {
        if (mPlayer == null) return;
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

    public interface OnStepArrowPress {
        Bundle OnStepArrowPress(int position);
    }
}

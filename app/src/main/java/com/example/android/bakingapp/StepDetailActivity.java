package com.example.android.bakingapp;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.bakingapp.BakingData.Step;

public class StepDetailActivity extends AppCompatActivity implements StepDetailFragment.OnStepArrowPress{

    private static final String TAG = StepDetailActivity.class.getSimpleName();
    public static final String STEPS_ARRAY_EXTRA_KEY = "steps_array";
    public static final String STEP_POSITION = "step_position";
    private Step[] mSteps;
    private int mPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        if (savedInstanceState == null) {
            Parcelable[] objects = getIntent()
                    .getParcelableArrayExtra(STEPS_ARRAY_EXTRA_KEY);
            initStepsArray(objects);

            mPosition = getIntent().getIntExtra(StepDetailActivity.STEP_POSITION, 1);

            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setArguments(buildBundle());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.step_container, stepDetailFragment)
                    .commit();
        } else {
            Parcelable[] objects = savedInstanceState.getParcelableArray(STEPS_ARRAY_EXTRA_KEY);
            initStepsArray(objects);

            mPosition = savedInstanceState.getInt(STEP_POSITION);
        }
    }

    @Override
    public Bundle OnStepArrowPress(int position) {
        mPosition = position;
        if (position >= mSteps.length) {
            return null;
        } else {
            return buildBundle();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArray(STEPS_ARRAY_EXTRA_KEY, mSteps);
        outState.putInt(STEP_POSITION, mPosition);
    }

    Bundle buildBundle() {
        Bundle result = new Bundle();
        result.putInt(StepDetailFragment.POSITION_EXTRA_KEY, mPosition);
        result.putInt(StepDetailFragment.LAST_POSITION, mSteps.length - 1);
        //mSteps.length - 1: because for arrays last_index = array_length - 1
        result.putString(StepDetailFragment.URI_EXTRA_KEY, mSteps[mPosition].getVideoURL());
        result.putString(StepDetailFragment.DESCRIPTION_EXTRA_KEY, mSteps[mPosition].getDescription());
        return result;
    }


    //https://stackoverflow.com/questions/28720062/class-cast-exception-when-passing-array-of-serializables-from-one-activity-to-an/28720450
    private void initStepsArray(Parcelable[] objects) {
        try {
            mSteps = new Step[objects.length];
            System.arraycopy(objects, 0, mSteps, 0, objects.length);
            //mSteps = Arrays.copyOf(objects, objects.length, Step[].class);
        } catch (ClassCastException e) {
            Log.d(TAG, "Wrong extra for key: "
                    + STEPS_ARRAY_EXTRA_KEY);
            throw new ClassCastException();
        }
    }
}

package com.example.android.bakingapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.bakingapp.BakingData.BakingAdapter;
import com.example.android.bakingapp.BakingData.Ingredient;
import com.example.android.bakingapp.BakingData.Step;
import com.example.android.bakingapp.BakingData.StepAdapter;

public class MasterListFragmentSteps extends MasterListFragmentBase{

    static public final String STEPS_ARRAY_KEY = "recipes_array";
    static public final String INGREDIENTS_ARRAY_KEY = "ingredients_array";

    private static final String TAG = MasterListFragment.class.getSimpleName();
    private Step[] mSteps;
    private Ingredient[] mIngredients;

    protected void initLocalVariables(Bundle args) {
        if (args.containsKey(STEPS_ARRAY_KEY) && args.containsKey(INGREDIENTS_ARRAY_KEY)) {
            mSteps = (Step[]) args.getParcelableArray(STEPS_ARRAY_KEY);
            mIngredients = (Ingredient[]) args.getParcelableArray(INGREDIENTS_ARRAY_KEY);
        } else {
            Log.d(TAG, "Args don't contain recipes key");
        }
    }

    @Override
    protected void restoreInstanceState(Bundle savedInstanceState) {
        mSteps = (Step[]) savedInstanceState.getParcelableArray(STEPS_ARRAY_KEY);
        mIngredients = (Ingredient[]) savedInstanceState.getParcelableArray(INGREDIENTS_ARRAY_KEY);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(STEPS_ARRAY_KEY, mSteps);
        outState.putParcelableArray(INGREDIENTS_ARRAY_KEY, mIngredients);
    }

    public void resetAdapter(Step[] steps, Ingredient[] ingredients) {
        mSteps = steps;
        mIngredients = ingredients;
        mRecipeAdapter.swapData(steps);
    }

    protected BakingAdapter getAdapter() {
        return new StepAdapter(getContext(), mSteps, mCallback);
    }
}

package com.example.android.bakingapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.WindowManager;

import com.example.android.bakingapp.BakingData.BakingAdapter;
import com.example.android.bakingapp.BakingData.Recipe;
import com.example.android.bakingapp.BakingUtils.BakingUtils;


public class MasterListFragment extends MasterListFragmentBase {

    static public final String RECIPES_ARRAY_KEY = "recipes_array";
    private static final String TAG = MasterListFragment.class.getSimpleName();
    private Recipe[] mRecipes;

    protected void initLocalVariables(Bundle args) {
        if (args.containsKey(RECIPES_ARRAY_KEY)) {
            mRecipes = (Recipe[]) args.getParcelableArray(RECIPES_ARRAY_KEY);
        } else {
            Log.d(TAG, "Args don't contain recipes key");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(RECIPES_ARRAY_KEY, mRecipes);
    }

    public void resetAdapter(Recipe[] recipes) {
        mRecipes = recipes;
        mRecipeAdapter.swapData(recipes);
    }

    protected BakingAdapter getAdapter() {
        return new BakingAdapter(getContext(), mRecipes, mCallback);
    }

    @Override
    protected void restoreInstanceState(Bundle savedInstanceState) {
        mRecipes = (Recipe[]) savedInstanceState.getParcelableArray(RECIPES_ARRAY_KEY);
        mRecipeAdapter = getAdapter();
        mRecyclerView.setAdapter(mRecipeAdapter);
    }

    //if tablet return 3, otherwise 1
    //this method overrides base getGridColCount (in MasterListFragmentBase)
    //which by default sets grid to be only one column
    @Override
    protected int getGridColCount() {
        if (getActivity() == null) return 1;

        WindowManager windowManager = getActivity().getWindowManager();
        if (windowManager != null && BakingUtils.isSW600(windowManager)) {
            return 3;
        } else return 1;
    }
}

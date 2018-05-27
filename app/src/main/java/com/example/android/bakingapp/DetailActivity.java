package com.example.android.bakingapp;

import android.content.Intent;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.bakingapp.BakingData.BakingAdapter;
import com.example.android.bakingapp.BakingData.Ingredient;
import com.example.android.bakingapp.BakingData.Step;
import com.example.android.bakingapp.BakingUtils.BakingUtils;

public class DetailActivity extends AppCompatActivity implements BakingAdapter.onDataItemClickListener,
        StepDetailFragment.OnStepArrowPress{

    public static final String INGREDIENTS_ARRAY_INSTANCE = "ingredients_array";
    public static final String STEPS_ARRAY_INSTANCE = "steps_array";
    private static final String MAIN_FRAGMENT_TAG = "detail_fragment";
    private static final String INGREDIENT_FRAGMENT_TAG = "ingredient_fragment";
    private static final String STEP_FRAGMENT_TAG = "step_fragment";
    private Step[] mSteps;
    private Ingredient[] mIngredients;
    private boolean isTablet;
    private int mPosition;

    public static final String TAG = DetailActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        isTablet = BakingUtils.isSW600(getWindowManager());

        if (savedInstanceState == null) {
            initLocalVariables();
            createMasterListFragment();
            mPosition = 0;
            if(isTablet) createIngredientFragment();
        }
    }

    private void createStepDetailFragment() {
        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setArguments(buildBundleForStepDetail());

        getSupportFragmentManager().beginTransaction()
                .add(R.id.step_detail_container, stepDetailFragment, STEP_FRAGMENT_TAG)
                .commit();
    }

    private void createIngredientFragment() {
        IngredientsFragment ingredientsFragment = new IngredientsFragment();
        String ingredients = BakingUtils.getFullIngredients(mIngredients);

        Bundle bundle = new Bundle();
        bundle.putString(IngredientsFragment.INGREDIENTS_EXTRA_KEY, ingredients);

        ingredientsFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.step_detail_container, ingredientsFragment, INGREDIENT_FRAGMENT_TAG)
                .commit();
    }

    private Bundle buildBundleForStepDetail() {
        Bundle result = new Bundle();
        result.putInt(StepDetailFragment.POSITION_EXTRA_KEY, mPosition);
        result.putInt(StepDetailFragment.LAST_POSITION, mSteps.length - 1);
        //mSteps.length - 1: because for arrays last_index = array_length - 1
        result.putString(StepDetailFragment.URI_EXTRA_KEY, mSteps[mPosition].getVideoURL());
        result.putString(StepDetailFragment.DESCRIPTION_EXTRA_KEY, mSteps[mPosition].getDescription());
        return result;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSteps = (Step[]) savedInstanceState.getParcelableArray(STEPS_ARRAY_INSTANCE);
        mIngredients = (Ingredient[]) savedInstanceState.getParcelableArray(INGREDIENTS_ARRAY_INSTANCE);
        createMasterListFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveInstanceStateCommonCode(outState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        saveInstanceStateCommonCode(outState);
    }

    private void saveInstanceStateCommonCode(Bundle outState) {
        outState.putParcelableArray(STEPS_ARRAY_INSTANCE, mSteps);
        outState.putParcelableArray(INGREDIENTS_ARRAY_INSTANCE, mIngredients);
    }

    private void initLocalVariables() {

        try {
            Parcelable[] objects = getIntent()
                    .getParcelableArrayExtra(STEPS_ARRAY_INSTANCE);
            mSteps = new Step[objects.length];
            System.arraycopy(objects, 0, mSteps, 0, objects.length);
            //mSteps = Arrays.copyOf(objects, objects.length, Step[].class);
        } catch (ClassCastException e) {
            Log.d(TAG, "Wrong extra for key: "
                    + STEPS_ARRAY_INSTANCE);
            throw new ClassCastException();
        }

        try {
            Parcelable[] objects = getIntent()
                    .getParcelableArrayExtra(INGREDIENTS_ARRAY_INSTANCE);
            mIngredients = new Ingredient[objects.length];
            System.arraycopy(objects, 0, mIngredients, 0, objects.length);
            //mIngredients = Arrays.copyOf(objects, objects.length, Ingredient[].class);
        } catch (ClassCastException e) {
            Log.d(TAG, "Wrong extra key: "
                    + INGREDIENTS_ARRAY_INSTANCE);
            throw new ClassCastException();
        }

    }

    private void createMasterListFragment() {
        MasterListFragmentSteps mMasterListFragmentSteps;
        try {
            mMasterListFragmentSteps =
                    (MasterListFragmentSteps) getSupportFragmentManager()
                            .findFragmentByTag(MAIN_FRAGMENT_TAG);
        } catch (ClassCastException e) {
            Log.d(TAG, "cannot cast " + MAIN_FRAGMENT_TAG);
            throw new ClassCastException();
        }

        if (mMasterListFragmentSteps != null) return;

        mMasterListFragmentSteps = new MasterListFragmentSteps();
        Bundle stepsBundle = new Bundle();
        stepsBundle.putParcelableArray(MasterListFragmentSteps.STEPS_ARRAY_KEY, mSteps);
        stepsBundle.putParcelableArray(MasterListFragmentSteps.INGREDIENTS_ARRAY_KEY, mIngredients);
        mMasterListFragmentSteps.setArguments(stepsBundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.master_list_container_detail, mMasterListFragmentSteps, MAIN_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onDataItemClick(int position) {
        if (isTablet) {
            onDataItemClickTablet(position);
        } else {
            onDataItemClickPhone(position);
        }
    }

    private void onDataItemClickPhone(int position) {
        if (position == 0) {
            Intent intent = new Intent(this, IngredientsActivity.class);
            String ingredients = BakingUtils.getFullIngredients(mIngredients);
            intent.putExtra(IngredientsFragment.INGREDIENTS_EXTRA_KEY, ingredients);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putExtra(StepDetailActivity.STEPS_ARRAY_EXTRA_KEY, mSteps);
            intent.putExtra(StepDetailActivity.STEP_POSITION, position-1);
            //position -1 : because the first position holds recipes ingredients, and the rest are
            //recipe steps
            startActivity(intent);
        }
    }

    /**
     * if we press 0 position in list ("Ingredients") this method checks if currently fragment
     * with ingredients tag is not removed. If removed:
     *       1) if step_detail fragment is currently active,
     * then this method removes step_detail and creates ingredients fragment
     *      2) otherwise simply creates ingredients fragment
     *
     * if we press 0+ position in list (Recipe baking step) this method checks if currently fragment
     * with step_detail tag is not removed:
     *      1) If not removed it simply updates fragment by creating and
     * passing bundle.
     *      2)If removed then this method creates fragment.
     * @param position
     */
    private void onDataItemClickTablet(int position) {
        if (position == 0) {
            if (getSupportFragmentManager().findFragmentByTag(INGREDIENT_FRAGMENT_TAG) == null) {
                    Fragment stepDetailFragment = getSupportFragmentManager()
                                    .findFragmentByTag(STEP_FRAGMENT_TAG);
                    if (stepDetailFragment != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(stepDetailFragment)
                                .commit();
                    }
                createIngredientFragment();
            }
        } else {
            Fragment ingredientsFragment = getSupportFragmentManager()
                    .findFragmentByTag(INGREDIENT_FRAGMENT_TAG);
            if (ingredientsFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(ingredientsFragment)
                        .commit();
            }
            mPosition = position - 1;
            //position -1 : because the first position holds recipes ingredients, and the rest are
            //recipe steps
            StepDetailFragment stepDetailFragment;
            try {
                stepDetailFragment =
                        (StepDetailFragment) getSupportFragmentManager()
                                .findFragmentByTag(STEP_FRAGMENT_TAG);
            } catch (ClassCastException e) {
                Log.d(TAG, "Wrong class: " + STEP_FRAGMENT_TAG);
                throw new ClassCastException();
            }
            if (stepDetailFragment != null) {
                Bundle bundle = buildBundleForStepDetail();
                stepDetailFragment.updateFragment(bundle);
            } else {
                createStepDetailFragment();
            }
        }
    }

    @Override
    public Bundle OnStepArrowPress(int position) {
        mPosition = position;
        if (position >= mSteps.length) {
            return null;
        } else {
            return buildBundleForStepDetail();
        }
    }
}

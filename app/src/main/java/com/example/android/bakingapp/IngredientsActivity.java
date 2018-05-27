package com.example.android.bakingapp;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class IngredientsActivity extends AppCompatActivity {
    private static final String MAIN_FRAGMENT_TAG = "main_fragment_ingredients";
    private static final String INGREDIENTS_STRING_INSTANCESTATE = "ingredients_string";
    private static final String TAG = IngredientsActivity.class.getSimpleName();
    private String mIngredients;
    private IngredientsFragment mMainFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        mIngredients = getIntent().getStringExtra(IngredientsFragment.INGREDIENTS_EXTRA_KEY);

        if(savedInstanceState == null) {
            IngredientsFragment ingredientsFragment = new IngredientsFragment();

            Bundle bundle = new Bundle();
            bundle.putString(IngredientsFragment.INGREDIENTS_EXTRA_KEY, mIngredients);

            ingredientsFragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction().add(R.id.ingredients_container, ingredientsFragment, MAIN_FRAGMENT_TAG)
                    .commit();
        } else {
            mIngredients = savedInstanceState.getString(INGREDIENTS_STRING_INSTANCESTATE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        saveInstanceStateCommon(outState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveInstanceStateCommon(outState);
    }

    private void saveInstanceStateCommon(Bundle outState) {
        outState.putString(INGREDIENTS_STRING_INSTANCESTATE, mIngredients);
    }
}

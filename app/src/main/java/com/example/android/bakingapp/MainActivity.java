package com.example.android.bakingapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.bakingapp.BakingData.BakingAdapter;
import com.example.android.bakingapp.BakingData.Recipe;
import com.example.android.bakingapp.BakingData.RecipeDataLoader;
import com.example.android.bakingapp.BakingUtils.NetworkUtils;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks, BakingAdapter.onDataItemClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String RECIPES_FRAGMENT_TAG = "recipes_fragment";
    private Recipe[] mRecipes;
    private MasterListFragment mMasterListFragment;

    public static final String STEPS_ARRAY_EXTRA_KEY = "steps_array";
    public static final String INGREDIENTS_ARRAY_EXTRA_KEY ="ingredients_array";

    public static final String RECIPES_ARRAY_EXTRA_KEY = "recipes_array";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            mMasterListFragment = new MasterListFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.master_list_container, mMasterListFragment, RECIPES_FRAGMENT_TAG)
                    .commit();
            initRecipeNetworkLoader();
        } else {
            Parcelable[] objects = savedInstanceState.getParcelableArray(RECIPES_ARRAY_EXTRA_KEY);
            if (objects == null) return;
            initRecipesArray(objects);
            try {
                mMasterListFragment = (MasterListFragment)
                        getSupportFragmentManager().findFragmentByTag(RECIPES_FRAGMENT_TAG);
            } catch (ClassCastException e) {
                Log.d(TAG, "Wrong fragment found by TAG: " + RECIPES_FRAGMENT_TAG);
                throw new ClassCastException();
            }
        }
    }

    //Both overloaded onSaveInstanceState should be overrided
    // i've used helper method to avoid copy-pasting and potential
    // errors
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
        outState.putParcelableArray(RECIPES_ARRAY_EXTRA_KEY, mRecipes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_reload) {
            initRecipeNetworkLoader();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case RecipeDataLoader.LOADER_ID:
                return new RecipeDataLoader(this);
                default:
                    throw new UnsupportedOperationException("Invalid loader id: " + String.valueOf(id));
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case RecipeDataLoader.LOADER_ID:
                if(data == null) {
                    showError();
                } else {
                    mRecipes = (Recipe[]) data;
                    showFragments();
                }

        }
    }

    private void initRecipeNetworkLoader() {
        LoaderManager loaderManager = getLoaderManager();
        Loader loader = loaderManager.getLoader(RecipeDataLoader.LOADER_ID);
        if (loader == null) {
            loaderManager.initLoader(RecipeDataLoader.LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(RecipeDataLoader.LOADER_ID, null, this);
        }
    }

    private void showError() {
        if(!NetworkUtils.isConnected(this)) {
            Toast.makeText(this,
                    "No internet connection. And database seems to be empty",
                    Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(this,
                    "Json parsing error..",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void showFragments() {
        mMasterListFragment.resetAdapter(mRecipes);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onDataItemClick(int position) {
        Intent detailActivityIntent = new Intent(this, DetailActivity.class);

        detailActivityIntent.putExtra(
                DetailActivity.STEPS_ARRAY_INSTANCE, mRecipes[position].getStep());
        detailActivityIntent.putExtra(
                DetailActivity.INGREDIENTS_ARRAY_INSTANCE, mRecipes[position].getIngredients());

        startActivity(detailActivityIntent);

    }

    private void initRecipesArray(Parcelable[] objects) {
        try {
            mRecipes = new Recipe[objects.length];
            System.arraycopy(objects, 0, mRecipes, 0, objects.length);
            //mSteps = Arrays.copyOf(objects, objects.length, Step[].class);
        } catch (ClassCastException e) {
            Log.d(TAG, "Wrong extra for key: "
                    + RECIPES_ARRAY_EXTRA_KEY);
            throw new ClassCastException();
        }
    }
}

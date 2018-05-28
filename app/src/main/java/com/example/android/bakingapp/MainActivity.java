package com.example.android.bakingapp;

import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.bakingapp.BakingData.BakingAdapter;
import com.example.android.bakingapp.BakingData.Recipe;
import com.example.android.bakingapp.BakingData.RecipeDataLoader;
import com.example.android.bakingapp.BakingUtils.BakingUtils;
import com.example.android.bakingapp.BakingUtils.NetworkUtils;
import com.example.android.bakingapp.Widget.BakingAppWidgetProvider;
import com.example.android.bakingapp.Widget.GridRemoteViewService;
import com.google.android.exoplayer2.util.Util;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks, BakingAdapter.onDataItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

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
        String reload = getString(R.string.reload);
        String error;
        if(!NetworkUtils.isConnected(this)) {
            error = getString(R.string.no_internet_connection);
        } else {
            error = getString(R.string.json_parsing_error);
        }

        String no_internet = getString(R.string.no_internet_connection);
        View view = findViewById(R.id.master_list_container);
        Snackbar snackbar = Snackbar
                .make(view, error, Snackbar.LENGTH_INDEFINITE)
                .setAction(reload, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initRecipeNetworkLoader();
                    }
                });
        snackbar.show();
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
        updateWidget(position + 1); // recipeIds begins from 1

        startActivity(detailActivityIntent);

    }

    private void updateWidget(int recipeId) {
        PreferenceManager
                .getDefaultSharedPreferences(this)
                .edit()
                .putInt(GridRemoteViewService.RECIPE_ID_WIDGET_KEY, recipeId)
                .apply();
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key == GridRemoteViewService.RECIPE_ID_WIDGET_KEY) {
            Intent intent = new Intent(this, BakingAppWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds
                    (new ComponentName(getApplication(), BakingAppWidgetProvider.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            sendBroadcast(intent);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widget_gv);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            PreferenceManager
                    .getDefaultSharedPreferences(this)
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            PreferenceManager
                    .getDefaultSharedPreferences(this)
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager
                .getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager
                .getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }
}

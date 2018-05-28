package com.example.android.bakingapp.Widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.BakingData.Ingredient;
import com.example.android.bakingapp.BakingData.Recipe;
import com.example.android.bakingapp.BakingUtils.BakingUtils;
import com.example.android.bakingapp.BakingUtils.JsonUtils;
import com.example.android.bakingapp.BakingUtils.NetworkUtils;
import com.example.android.bakingapp.IngredientsFragment;
import com.example.android.bakingapp.R;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class GridRemoteViewService extends RemoteViewsService {
    public static String RECIPE_ID_WIDGET_KEY = "recipe_widget_key";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }

    class GridRemoteViewsFactory implements RemoteViewsFactory {
        private final String TAG = GridRemoteViewsFactory.class.getSimpleName();
        private Context mContext;
        private Ingredient[] mRecipes;

        public GridRemoteViewsFactory(Context context) {
            mContext = context;
        }

        @Override
        public void onCreate() {
        }

        //Tries to retrieve data from database. In case if database is empty retrieves data
        // from network.
        @Override
        public void onDataSetChanged() {
            int recipeId =
                    PreferenceManager
                            .getDefaultSharedPreferences(mContext)
                            .getInt(RECIPE_ID_WIDGET_KEY, 0);
            mRecipes = BakingUtils.getIngredients(recipeId, mContext);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return mRecipes == null ? 0 : mRecipes.length;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Log.v(TAG, "binding)))");
            if (mRecipes == null || mRecipes.length == 0) return null;

            RemoteViews remoteViews =
                    new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget_item);
            remoteViews.setTextViewText(
                    R.id.recipe_widget_item_tv, mRecipes[position].getFullDescription());

            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }

}

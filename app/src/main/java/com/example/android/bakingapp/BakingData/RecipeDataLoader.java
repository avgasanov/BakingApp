package com.example.android.bakingapp.BakingData;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.bakingapp.BakingUtils.BakingUtils;
import com.example.android.bakingapp.BakingUtils.JsonUtils;
import com.example.android.bakingapp.BakingUtils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
/**
 * This loader is used to load data either from network or from database.
 * I use md5 hash to check whether json retrieved from network changed or didn't.
 * If json file has changed since last download, loader updates database and returns Recipe[] array
 * If json file didn't change loader loads data from database
 */
public class RecipeDataLoader extends AsyncTaskLoader<Recipe[]> {
    static private final String TAG = "RECLOADTAG";
    static public final int LOADER_ID = 100;
    private Recipe[] result;
    private Context mContext;

    public RecipeDataLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (result != null) {
            deliverResult(result);
        } else {
            forceLoad();
        }
    }

    @Override
    public Recipe[] loadInBackground() {
        boolean isConnected = NetworkUtils.isConnected(mContext);
        if (!isConnected) {
            result = BakingUtils.retrieveRecipesFromDb(mContext);
            Log.v(TAG, "No network connection. Retrieved from database.");
        }
        URL jsonRequestUrl =
                NetworkUtils.buildRequestUrlFromString(NetworkUtils.RECEPIES_REQUEST_URL);

        if (jsonRequestUrl == null) return null;

        String jsonResponse;
        try {
            jsonResponse = NetworkUtils.getJsonResponseFromUrl(jsonRequestUrl);
            if (jsonResponse != null) {
                String responseMD5 = BakingUtils.generateMD5(jsonResponse);
                String persistedResponseMD5 =
                        PreferenceManager
                                .getDefaultSharedPreferences(mContext)
                                .getString(BakingUtils.JSON_RESPONSE_MD5_PREF, "0");

                if (!persistedResponseMD5.equals(responseMD5)) {
                    BakingUtils.clearDatabase(mContext);
                    result = JsonUtils.parseBakingJson(jsonResponse);
                    BakingUtils.insertRecipesIntoDatabase(result, mContext);
                    PreferenceManager
                            .getDefaultSharedPreferences(mContext)
                            .edit()
                            .putString(BakingUtils.JSON_RESPONSE_MD5_PREF, responseMD5)
                            .apply();
                    Log.v(TAG, "Retrieved from network");
                    Log.v(TAG, "Response MD5: "
                            + responseMD5
                            + " Persisted MD5: "
                            + persistedResponseMD5);
                } else {
                    result = BakingUtils.retrieveRecipesFromDb(mContext);
                    Log.v(TAG, "Retrieved from database");
                }
            }
            return result;
        } catch (IOException |JSONException e) {
            Log.e(TAG, "Network\\json parsing error");
            return null;
        }
    }

}

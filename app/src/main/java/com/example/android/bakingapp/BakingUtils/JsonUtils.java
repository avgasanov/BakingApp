package com.example.android.bakingapp.BakingUtils;

import android.util.Log;

import com.example.android.bakingapp.BakingData.Ingredient;
import com.example.android.bakingapp.BakingData.Recipe;
import com.example.android.bakingapp.BakingData.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class JsonUtils {
    static private final String TAG = "JSONUTILS";
    static public final String JSON_RECIPE_ID = "id";
    static public final String JSON_RECIPE_NAME = "name";
    static public final String JSON_RECIPE_SERVINGS = "servings";

    static public final String JSON_INGREDIENTS = "ingredients";
    static public final String JSON_INGREDIENT_QUANTITY = "quantity";
    static public final String JSON_INGREDIENT_MEASURE = "measure";
    static public final String JSON_INGREDIENT_INGREDIENT = "ingredient";

    static public final String JSON_STEPS = "steps";
    static public final String JSON_STEP_ID = "id";
    static public final String JSON_STEP_SHORT_DESCRIPTION = "shortDescription";
    static public final String JSON_STEP_DESCRIPTION = "description";
    static public final String JSON_STEP_VIDEO_URL = "videoURL";
    static public final String JSON_STEP_THUMBNAIL_URL = "thumbnailURL";

    public static Recipe[] parseBakingJson(String jsonResponse) throws JSONException {
        if (jsonResponse == null) return null;

        JSONArray recipesJSONArray = new JSONArray(jsonResponse);
        Recipe[] result = new Recipe[recipesJSONArray.length()];

        for (int i=0; i < recipesJSONArray.length(); i++) {
            JSONObject oneRecipeFromArray = recipesJSONArray.getJSONObject(i);

            int id = oneRecipeFromArray.optInt(JSON_RECIPE_ID);
            String name = oneRecipeFromArray.optString(JSON_RECIPE_NAME);
            Ingredient[] ingredients =
                    getIngredients(oneRecipeFromArray.getJSONArray(JSON_INGREDIENTS));
            Step[] steps =
                    getSteps(oneRecipeFromArray.getJSONArray(JSON_STEPS));
            int servings = oneRecipeFromArray.getInt(JSON_RECIPE_SERVINGS);

            result[i] = new Recipe(id, name, ingredients, steps, servings);
        }

        return result;
    }

    private static Ingredient[] getIngredients(JSONArray ingredientsJSONArray) throws JSONException {
        Ingredient[] result = new Ingredient[ingredientsJSONArray.length()];

        for (int i=0; i < result.length; i++) {
            JSONObject oneIngredientFromArray = ingredientsJSONArray.getJSONObject(i);

            double quantity = oneIngredientFromArray.optDouble(JSON_INGREDIENT_QUANTITY);
            String measure = oneIngredientFromArray.optString(JSON_INGREDIENT_MEASURE);
            String ingredient = oneIngredientFromArray.optString(JSON_INGREDIENT_INGREDIENT);

            result[i] = new Ingredient(quantity, measure, ingredient);
        }

        return result;
    }

    private static Step[] getSteps(JSONArray stepJsonArray) throws JSONException {
        Step[] result = new Step[stepJsonArray.length()];

        for (int i=0; i < result.length; i++) {
            JSONObject oneStepFromArray = stepJsonArray.getJSONObject(i);

            int id = oneStepFromArray.optInt(JSON_STEP_ID);
            String shortDescription = oneStepFromArray.optString(JSON_STEP_SHORT_DESCRIPTION);
            String description = oneStepFromArray.optString(JSON_STEP_DESCRIPTION);
            String videoURL =
                    processVideoURL(oneStepFromArray.optString(JSON_STEP_VIDEO_URL));
            //there is no example image in json file and also there is no documentation for api
            //so i've decided to use glide library's error callbacks in application to
            //handle errors.

            String thumbnailUrl = oneStepFromArray.optString(JSON_STEP_THUMBNAIL_URL);

            result[i] = new Step(id, shortDescription, description, videoURL, thumbnailUrl);
        }

        return result;
    }

    private static String processVideoURL(String s) {
        if (s == null || s.isEmpty()) return "";

        if (s.length() - 4 == s.lastIndexOf(".mp4")) {
            return s;
        } else {
            Log.e(TAG, "videoURL is invalid: " + s);
            return "";
        }
    }

    private static boolean validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return false;
        try {
            URLConnection connection = new URL(imageUrl).openConnection();
            String contentType = connection.getHeaderField("Content-Type");
            return contentType.startsWith("image/");
        } catch (MalformedURLException e) {
            Log.d(TAG, "Invalid image url: " + imageUrl);
            return false;
        } catch (IOException e) {
            Log.d(TAG, "Connection error. Try again");
            return false;
        }
    }
}

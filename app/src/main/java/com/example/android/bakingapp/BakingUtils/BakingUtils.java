package com.example.android.bakingapp.BakingUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.example.android.bakingapp.BakingData.Ingredient;
import com.example.android.bakingapp.BakingData.Recipe;
import com.example.android.bakingapp.BakingData.Step;
import com.example.android.bakingapp.BakingDatabase.BakingProvider;
import com.example.android.bakingapp.BakingDatabase.Ingredients;
import com.example.android.bakingapp.BakingDatabase.Recipes;
import com.example.android.bakingapp.BakingDatabase.Steps;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//https://www.liammoat.com/blog/2017/pull-an-sqlite-database-file-from-an-android-device-for-debugging

//i suppose, there is no need actually to pass whole array of ingredients.
    //That is why i've created this helper method. It creates full "ingredients"
    //text for textview. I've preserved Ingredient class just to keep things organized
    public class BakingUtils {
    private static String TAG = BakingUtils.class.getSimpleName();

    public static String getFullIngredients(Ingredient[] ingredients) {
            StringBuilder result = new StringBuilder();
            for (Ingredient ingredient : ingredients) {
                result.append(ingredient.getFullDescription() + "\n");
            }
            return result.toString();
        }

    //in AndroidMe application (Fragments Lesson), tablet is defined as android device with sw > 600
    // in that application main activity layout name was used to figure out device type (phone/tablet)
    // In my application main layout stays the same both for phone and tablet. So i've decide to
    // create helper method to figure out device type
    //Helped me to create this method:
    //https://stackoverflow.com/questions/15055458/detect-7-inch-and-10-inch-tablet-programmatically?
    public static boolean isSW600(WindowManager windowManager) {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float scaleFactor = metrics.density;
        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;
        float smallestWidth = Math.min(widthDp, heightDp);
        return  smallestWidth > 600;
    }

    public static Recipe[] retrieveRecipesFromDb(Context context) {
        Cursor recipesCursor = context.getContentResolver().query(BakingProvider.RecipesTable.CONTENT_URI,
                null,
                null,
                null,
                Recipes._ID);
        if (recipesCursor.getCount() == 0) return null;

        Recipe[] recipes = new Recipe[recipesCursor.getCount()];
        for (int j=0; recipesCursor.moveToNext(); j++) {
                int idIdx = recipesCursor.getColumnIndex(Recipes._ID);
                int nameIdx = recipesCursor.getColumnIndex(Recipes.NAME);
                int servingsIdx = recipesCursor.getColumnIndex(Recipes.SERVINGS);

                int recipeId = recipesCursor.getInt(idIdx);
                String recipeName = recipesCursor.getString(nameIdx);
                int recipeServings = recipesCursor.getInt(servingsIdx);

                Ingredient[] ingredients = getIngredients(recipeId, context);
                Step[] steps = getSteps(recipeId, context);

                recipes[j] = new Recipe(recipeId, recipeName, ingredients, steps, recipeServings);
            }
            recipesCursor.close();
            return recipes;
    }

    private static Step[] getSteps(int recipe_id, Context context) {
        Cursor stepCursor = context.getContentResolver().query(
                BakingProvider.StepsTable.withRecipeId(recipe_id),
                null,
                null,
                null,
                Steps.ORDERING_ID
        );
        Step[] steps = new Step[stepCursor.getCount()];
        for (int i=0; stepCursor.moveToNext(); i++) {
            int orderingIdIdx = stepCursor.getColumnIndex(Steps.ORDERING_ID);
            int shortDescIdx = stepCursor.getColumnIndex(Steps.SHORT_DESCRIPTION);
            int descIdx = stepCursor.getColumnIndex(Steps.DESCRIPTION);
            int videoIdx = stepCursor.getColumnIndex(Steps.VIDEO_URL);

            int orderingId = stepCursor.getInt(orderingIdIdx);
            String shortDesc = stepCursor.getString(shortDescIdx);
            String desc = stepCursor.getString(descIdx);
            String video = stepCursor.getString(videoIdx);

            steps[i] = new Step(orderingId, shortDesc, desc, video);
        }
        stepCursor.close();
        return steps;
    }

    private static Ingredient[] getIngredients(int recipe_id, Context context) {
        Cursor ingredientsCursor = context.getContentResolver().query(
                BakingProvider.IngredientsTable.withId(recipe_id),
                null,
                null,
                null,
                null);
        Ingredient[] ingredients = new Ingredient[ingredientsCursor.getCount()];
        for (int i=0; ingredientsCursor.moveToNext(); i++) {
            int quantityIdx = ingredientsCursor.getColumnIndex(Ingredients.QUANTITY);
            int measureIdx = ingredientsCursor.getColumnIndex(Ingredients.MEASURE);
            int ingredientIdx = ingredientsCursor.getColumnIndex(Ingredients.INGREDIENT);

            double quantity = ingredientsCursor.getDouble(quantityIdx);
            String measure = ingredientsCursor.getString(measureIdx);
            String ingredient = ingredientsCursor.getString(ingredientIdx);
            ingredients[i] = new Ingredient(quantity, measure, ingredient);
        }
        ingredientsCursor.close();
        return ingredients;
    }

    //There are only 4 recipe in json file. So i've decided to use bulk insert only for ingredients
    //and steps.
    public static void insertRecipesIntoDatabase(Recipe[] recipes, Context context) {
            for (int i=0; i < recipes.length; i++) {
                int recipeId = recipes[i].getId();
                String recipeName = recipes[i].getName();
                int servings = recipes[i].getServings();

                ContentValues recipeContentValues = new ContentValues();
                recipeContentValues.put(Recipes._ID, recipeId);
                recipeContentValues.put(Recipes.NAME, recipeName);
                recipeContentValues.put(Recipes.SERVINGS, servings);

                Ingredient[] recipeIngredients = recipes[i].getIngredients();
                ContentValues[] ingredientContentValues = new ContentValues[recipeIngredients.length];
                for (int j=0; j < recipeIngredients.length; j ++) {
                    double quantity = recipeIngredients[j].getQuantity();
                    String measure = recipeIngredients[j].getMeasure();
                    String ingredient = recipeIngredients[j].getIngredient();

                    ingredientContentValues[j] = new ContentValues();
                    ingredientContentValues[j].put(Ingredients.RECIPE_ID, recipeId);
                    ingredientContentValues[j].put(Ingredients.QUANTITY, quantity);
                    ingredientContentValues[j].put(Ingredients.MEASURE, measure);
                    ingredientContentValues[j].put(Ingredients.INGREDIENT, ingredient);
                }

                Step[] recipeSteps = recipes[i].getStep();
                ContentValues[] stepsContentValues = new ContentValues[recipeSteps.length];
                for (int j=0; j < recipeSteps.length; j ++) {
                    int stepId = recipeSteps[j].getId();
                    String shortDescription = recipeSteps[j].getName();
                    String description = recipeSteps[j].getDescription();
                    String videoUrl = recipeSteps[j].getVideoURL();

                    stepsContentValues[j] = new ContentValues();
                    stepsContentValues[j].put(Steps.RECIPE_ID, recipeId);
                    stepsContentValues[j].put(Steps.ORDERING_ID, stepId);
                    stepsContentValues[j].put(Steps.SHORT_DESCRIPTION, shortDescription);
                    stepsContentValues[j].put(Steps.DESCRIPTION, description);
                    stepsContentValues[j].put(Steps.VIDEO_URL, videoUrl);
                }

                context.getContentResolver()
                        .insert(BakingProvider.RecipesTable.CONTENT_URI, recipeContentValues);
                context.getContentResolver()
                        .bulkInsert(BakingProvider.IngredientsTable.CONTENT_URI, ingredientContentValues);
                context.getContentResolver()
                        .bulkInsert(BakingProvider.StepsTable.CONTENT_URI, stepsContentValues);

            }


        }

        //source:
        // https://stackoverflow.com/questions/415953/how-can-i-generate-an-md5-hash
        public static String generateMD5(String plaintext) {
            MessageDigest m = null;
            try {
                m = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                Log.d(TAG, "MD5 algorithm not found");
                e.printStackTrace();
            }
            m.reset();
            m.update(plaintext.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            return bigInt.toString(16);
        }

        public static void clearDatabase(Context context) {
            context.getContentResolver()
                    .delete(BakingProvider.IngredientsTable.CONTENT_URI,
                            null,
                            null);
            context.getContentResolver()
                    .delete(BakingProvider.StepsTable.CONTENT_URI,
                            null,
                            null);
            context.getContentResolver()
                    .delete(BakingProvider.RecipesTable.CONTENT_URI,
                            null,
                            null);
        }

}

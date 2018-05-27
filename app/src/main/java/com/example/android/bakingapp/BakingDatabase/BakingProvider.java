package com.example.android.bakingapp.BakingDatabase;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = BakingProvider.AUTHORITY, database = BakingDatabase.class)
public final class BakingProvider {

    public static final String AUTHORITY = "com.example.android.bakingapp.BakingDatabase.BakingProvider";

    interface Path {
        String RECIPES = "recipes";
        String INGREDIENTS = "ingredients";
        String STEPS = "steps";
    }

    @TableEndpoint(table = BakingDatabase.RECIPES) public static class RecipesTable {

        @ContentUri(
                path = Path.RECIPES,
                type = "vnd.android.cursor.dir",
                defaultSort = Recipes._ID)
        public static final Uri CONTENT_URI =
                Uri.parse("content://"
                + AUTHORITY
                + "/"
                + Path.RECIPES);

        @InexactContentUri(
                path = Path.RECIPES + "/#",
                name = "RECIPE_ID",
                type = "vnd.android.cursor.item",
                whereColumn = Recipes._ID,
                pathSegment = 1)
        public static Uri withId(int id) {
            return Uri.parse("content://"
                    + AUTHORITY
                    + "/"
                    + Path.RECIPES
                    + "/"
                    + String.valueOf(id));
        }
    }

    @TableEndpoint(table = BakingDatabase.INGREDIENTS) public static class IngredientsTable {

        @ContentUri(
                path = Path.INGREDIENTS,
                type = "vnd.android.cursor.dir",
                defaultSort = Ingredients.RECIPE_ID)
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Path.INGREDIENTS);

        @InexactContentUri(
                path = Path.INGREDIENTS + "/#",
                name = "INGREDIENTS_RECIPE_ID",
                type = "vnd.android.cursor.dir",
                whereColumn = Ingredients.RECIPE_ID,
                pathSegment = 1)
        public static Uri withId(int id) {
            return Uri.parse("content://"
                    + AUTHORITY
                    + "/"
                    + Path.INGREDIENTS
                    + "/"
                    + String.valueOf(id));
        }
    }

    @TableEndpoint(table = BakingDatabase.STEPS) public static class StepsTable {

        @ContentUri(
                path = Path.STEPS,
                type = "vnd.android.cursor.dir",
                defaultSort = Steps.RECIPE_ID)
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Path.STEPS);

        @InexactContentUri(
                path = Path.STEPS + "/" + Path.RECIPES + "/#",
                name = "STEPS_RECIPE_ID",
                type = "vnd.android.cursor.dir",
                whereColumn = Steps.RECIPE_ID,
                pathSegment = 2)
        public static Uri withRecipeId(int id) {
            return Uri.parse("content://"
                    + AUTHORITY
                    + "/"
                    + Path.STEPS
                    + "/"
                    + Path.RECIPES
                    + "/"
                    + String.valueOf(id));
        }

        @InexactContentUri(
                path = Path.STEPS + "/#",
                name = "STEPS_ID",
                type = "vnd.android.cursor.item",
                whereColumn = Steps._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://"
                    + AUTHORITY + "/"
                    + Path.STEPS
                    + "/"
                    + id);
        }
    }

}

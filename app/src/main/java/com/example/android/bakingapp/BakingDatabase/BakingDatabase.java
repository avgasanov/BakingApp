package com.example.android.bakingapp.BakingDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.example.android.bakingapp.BakingData.Recipe;
import com.example.android.bakingapp.BakingData.RecipeDataLoader;
import com.example.android.bakingapp.BakingUtils.BakingUtils;
import com.example.android.bakingapp.MainActivity;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.IfNotExists;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

@Database(version = BakingDatabase.VERSION,
            fileName = BakingDatabase.NAME)
public final class BakingDatabase {

    static public final int VERSION = 6;
    static public final String NAME = "bakingDatabase";

    @Table(Recipes.class) @IfNotExists public static final String RECIPES = "recipes";

    @Table(Ingredients.class) @IfNotExists public static final String INGREDIENTS = "ingredients";

    @Table(Steps.class) @IfNotExists public static final String STEPS = "steps";

    @OnUpgrade public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                            int newVersion) {
        BakingUtils.resetSharedPreferenceJsonMD5(context);
    }

    @OnCreate public static void onCreate(Context context, SQLiteDatabase db) {

    }

}

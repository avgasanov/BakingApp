package com.example.android.bakingapp.BakingDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.IfNotExists;
import net.simonvt.schematic.annotation.Table;

@Database(version = BakingDatabase.VERSION)
public final class BakingDatabase {

    static public final int VERSION = 1;

    @Table(Recipes.class) @IfNotExists public static final String RECIPES = "recipes";

    @Table(Ingredients.class) @IfNotExists public static final String INGREDIENTS = "ingredients";

    @Table(Steps.class) @IfNotExists public static final String STEPS = "steps";

}

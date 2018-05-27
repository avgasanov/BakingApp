package com.example.android.bakingapp.BakingDatabase;

import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.Constraints;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.ForeignKeyConstraint;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.References;

@Constraints (
            foreignKey = {
                    @ForeignKeyConstraint(columns = {Ingredients.RECIPE_ID},
                        referencedTable = BakingDatabase.RECIPES,
                        referencedColumns = {Recipes._ID},
                        onConflict = ConflictResolutionType.REPLACE)}
        )

public interface Ingredients {
    @DataType(DataType.Type.INTEGER) @References(
            table = BakingDatabase.RECIPES,
            column = Recipes._ID) String RECIPE_ID = "recipe_id";

    @DataType(DataType.Type.REAL) String QUANTITY = "quantity";

    @DataType(DataType.Type.TEXT) String MEASURE = "measure";

    @DataType(DataType.Type.TEXT) @NotNull String INGREDIENT = "ingredient";

}

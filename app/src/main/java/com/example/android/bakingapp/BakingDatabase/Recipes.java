package com.example.android.bakingapp.BakingDatabase;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface Recipes {
    @DataType(DataType.Type.INTEGER) @PrimaryKey String _ID = "id";
    @DataType(DataType.Type.TEXT) @NotNull String NAME = "name";
    @DataType(DataType.Type.INTEGER) String SERVINGS = "servings";
    @DataType(DataType.Type.TEXT) String IMAGE = "image";
}

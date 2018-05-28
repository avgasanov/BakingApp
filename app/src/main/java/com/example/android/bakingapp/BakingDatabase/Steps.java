package com.example.android.bakingapp.BakingDatabase;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.Constraints;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.ForeignKeyConstraint;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

@Constraints (
        foreignKey = @ForeignKeyConstraint(columns = {Steps.RECIPE_ID},
                referencedTable = BakingDatabase.RECIPES,
                referencedColumns = {Recipes._ID},
                onConflict = ConflictResolutionType.REPLACE)
)

public interface Steps {

    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String _ID = "id";
    @DataType(DataType.Type.INTEGER) String ORDERING_ID = "ordering_id";

    @DataType(DataType.Type.INTEGER) @References(
            table = BakingDatabase.RECIPES,
            column = Recipes._ID
    ) String RECIPE_ID = "recipe_id";

    @DataType(DataType.Type.TEXT) String SHORT_DESCRIPTION = "short_description";
    @DataType(DataType.Type.TEXT) String DESCRIPTION = "description";
    @DataType(DataType.Type.TEXT) String VIDEO_URL = "video_url";
    @DataType(DataType.Type.TEXT) String THUMBNAIL_URL = "thumbnail_url";
}

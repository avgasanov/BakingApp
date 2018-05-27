package com.example.android.bakingapp.BakingData;

import android.os.Parcel;
import android.os.Parcelable;

public class Recipe implements Parcelable, BakingAdapter.Data {
    private final int mId;
    private final String mName;

    private final Ingredient[] mIngredients;

    private final Step[] mStep;
    private final int mServings;
    static private final int NULL_PARCELABLE = 0;

    static public final int DESCRIPTION = 3;
    public Recipe(int id, String name, Ingredient[] ingredients,
                  Step[] step, int servings) {
        mId = id;
        mName = name;
        mIngredients = ingredients;
        mStep = step;
        mServings = servings;
    }

    protected Recipe(Parcel in) {
        mId = in.readInt();
        mName = in.readString();

        if (in.readInt() == Ingredient.DESCRIPTION) {
//            Parcelable parcelable[] = in.readParcelable(Ingredient.class.getClassLoader());
//            mIngredients = (Ingredient[]) parcelable;
           // mIngredients.wr
            mIngredients = (Ingredient[]) in.readParcelableArray(Ingredient.class.getClassLoader());
        } else mIngredients = null;

        if (in.readInt() == Step.DESCRIPTION) {
            mStep = (Step[]) in.readParcelableArray(Step.class.getClassLoader());
        } else mStep = null;

        mServings = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);

        if (mIngredients != null) {
            dest.writeInt(Ingredient.DESCRIPTION);
            dest.writeParcelableArray(mIngredients, flags);
        } else dest.writeInt(NULL_PARCELABLE);

        if (mStep != null) {
            dest.writeInt(Step.DESCRIPTION);
            dest.writeParcelableArray(mStep, flags);
        } else dest.writeInt(NULL_PARCELABLE);

        dest.writeInt(mServings);
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Ingredient[] getIngredients() {
        return mIngredients;
    }

    public Step[] getStep() {
        return mStep;
    }

    public int getServings() {
        return mServings;
    }
}

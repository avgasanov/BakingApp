package com.example.android.bakingapp.BakingData;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingredient implements Parcelable {
    private final double mQuantity;
    private final String mMeasure;
    private final String mIngredient;

    static public final int DESCRIPTION = 1;

    public Ingredient(double quantity, String measure, String ingredient) {
        mQuantity = quantity;
        mMeasure = measure;
        mIngredient = ingredient;
    }

    protected Ingredient(Parcel in) {
        mQuantity = in.readDouble();
        mMeasure = in.readString();
        mIngredient = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mQuantity);
        dest.writeString(mMeasure);
        dest.writeString(mIngredient);
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public double getQuantity() {
        return mQuantity;
    }

    public String getMeasure() {
        return mMeasure;
    }

    public String getIngredient() {
        return mIngredient;
    }

    public String getFullDescription() {
        return "\u2022"
                + mIngredient
                + "\n"
                + "\t\t\tamount: "
                + String.valueOf(mQuantity)
                + " "
                + mMeasure;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

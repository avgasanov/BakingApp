package com.example.android.bakingapp.BakingData;

import android.os.Parcel;
import android.os.Parcelable;

public class Step implements Parcelable, BakingAdapter.Data {
    private final int mId;
    private final String mShortDescription;
    private final String mDescription;
    private final String mVideoURL;

    static public final int DESCRIPTION = 2;

    public Step(int id, String shortDescription, String description, String videoURL) {
        mId = id;
        mShortDescription = shortDescription;
        mDescription = description;
        mVideoURL = videoURL;
    }

    protected Step(Parcel in) {
        mId = in.readInt();
        mShortDescription = in.readString();
        mDescription = in.readString();
        mVideoURL = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mShortDescription);
        dest.writeString(mDescription);
        dest.writeString(mVideoURL);
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

    public int getId() {
        return mId;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getVideoURL() {
        return mVideoURL;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String getName() {
        return mShortDescription;
    }
}

package com.example.android.bakingapp.BakingData;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.android.bakingapp.R;

public class StepAdapter extends BakingAdapter {

    private Context mContext;


    public StepAdapter(Context context, Data[] data,
                       onDataItemClickListener callback) {

        super(context, data, callback);
        mContext = context;
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        } else {
            return mData.length + 1; //because first element is ingredient list
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name;
        if (position == 0) {
            name = mContext.getResources().getString(R.string.recipe_ingredients);
        } else {
            name = mData[position-1].getName(); //second position is for the first element of mData
        }
        holder.mDataTextView.setText(name);
    }

}

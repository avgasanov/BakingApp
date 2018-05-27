package com.example.android.bakingapp.BakingData;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BakingAdapter extends RecyclerView.Adapter<BakingAdapter.ViewHolder> {
        private final Context mContext;
        protected Data[] mData;
        protected onDataItemClickListener mCallback;


        public BakingAdapter(Context context, Data[] data, onDataItemClickListener callback) {
            mCallback = callback;
            mContext = context;
            mData = data;
        }

        @NonNull
        @Override
        public BakingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.recipe_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BakingAdapter.ViewHolder holder, int position) {
            String name = mData[position].getName();
            holder.mDataTextView.setText(name);
        }


        @Override
        public int getItemCount() {
            if (mData == null) {
                return 0;
            } else {
                return mData.length;
            }
        }

        public void swapData(Data[] data) {
            if (data == null) return;
            mData = data;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            @BindView(R.id.tv_name)
            TextView mDataTextView;
            @BindView(R.id.recipe_item_cv)
            CardView mRecipeCard;


            public ViewHolder(View itemView) {
                super(itemView);

                ButterKnife.bind(this, itemView);
                mDataTextView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                mCallback.onDataItemClick(position);
            }
        }

        public interface onDataItemClickListener {
            public void onDataItemClick(int position);
        }

        public interface Data {
            public String getName();
        }
}


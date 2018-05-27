package com.example.android.bakingapp.BakingData;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private final Context mContext;
    private Recipe[] mRecipes;
    private onRecipeItemClickListener mCallback;

    public RecipeAdapter(Context context, Recipe[] recipes, onRecipeItemClickListener callback) {
        mCallback = callback;
        mContext = context;
        mRecipes = recipes;
    }

    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recipe_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder holder, int position) {
        String recipeName = mRecipes[position].getName();
        holder.mRecipeNameTextView.setText(recipeName);
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null) {
            return 0;
        } else {
            return mRecipes.length;
        }
    }

    public void swapRecipes(Recipe[] recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.tv_name) TextView mRecipeNameTextView;


        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            mRecipeNameTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mCallback.onRecipeClick(position);
        }
    }

    public interface onRecipeItemClickListener {
        public void onRecipeClick(int position);
    }
}

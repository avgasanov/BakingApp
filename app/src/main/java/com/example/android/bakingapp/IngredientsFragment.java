package com.example.android.bakingapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsFragment extends Fragment {

    public static final String INGREDIENTS_EXTRA_KEY = "param1";
    private static final String TAG = IngredientsFragment.class.getSimpleName();
    private static final String INGREDIENTS_STRING_INSTANCESTATE = "ingredient_string";
    private static final String SCROLL_POSITION_INSTANCESTATE = "scroll_position";
    private String mIngredients;

    @BindView(R.id.ingredients_tv) TextView mIngredientsTextView;

    public IngredientsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater
                .inflate(R.layout.fragment_ingredients, container, false);
        ButterKnife.bind(this, view);

        if(savedInstanceState == null) {
            if (getArguments() != null) {
                mIngredients = getArguments().getString(INGREDIENTS_EXTRA_KEY);
            } else {
                Log.d(TAG, "Extras don't contain any ingredients");
            }
        } else {
            mIngredients = savedInstanceState.getString(INGREDIENTS_STRING_INSTANCESTATE);
            int scrollPosition = savedInstanceState.getInt(SCROLL_POSITION_INSTANCESTATE);
            mIngredientsTextView.setVerticalScrollbarPosition(scrollPosition);
        }

        mIngredientsTextView.setText(mIngredients);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INGREDIENTS_STRING_INSTANCESTATE, mIngredients);
        outState.putInt(SCROLL_POSITION_INSTANCESTATE, mIngredientsTextView.getScrollY());
    }

    public void updateText(String newText) {
        mIngredients = newText;
        mIngredientsTextView.setText(mIngredients);
    }
}

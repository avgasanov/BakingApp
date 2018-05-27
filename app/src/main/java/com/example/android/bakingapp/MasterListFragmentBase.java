package com.example.android.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.BakingData.BakingAdapter;

abstract public class MasterListFragmentBase extends Fragment {

    private static final String TAG = MasterListFragmentBase.class.getSimpleName();
    BakingAdapter.onDataItemClickListener mCallback;
    protected BakingAdapter mRecipeAdapter;
    protected RecyclerView mRecyclerView;

    private static final String ADAPTER_POSITION = "scroll_position";


    public MasterListFragmentBase() {
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args == null) return;
        initLocalVariables(args);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ADAPTER_POSITION, mRecyclerView.getVerticalScrollbarPosition());
    }

    abstract protected void initLocalVariables(Bundle args);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (BakingAdapter.onDataItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement onRecipeItemClickListener");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);
        mRecyclerView = rootView.findViewById(R.id.rv_master_list);

        int colCount = getGridColCount();
        RecyclerView.LayoutManager layoutManager =
                    new GridLayoutManager(getContext(),
                            colCount,
                            LinearLayoutManager.VERTICAL,
                            false);


        mRecipeAdapter = getAdapter();
        mRecyclerView.setAdapter(mRecipeAdapter);
        mRecyclerView.setLayoutManager(layoutManager);

        if (savedInstanceState != null) {
            mRecyclerView.scrollToPosition(savedInstanceState.getInt(ADAPTER_POSITION));
            restoreInstanceState(savedInstanceState);
        }

        return rootView;
    }

    protected abstract void restoreInstanceState(Bundle savedInstanceState);

    protected abstract BakingAdapter getAdapter();

    protected int getGridColCount() {return 1;}
}

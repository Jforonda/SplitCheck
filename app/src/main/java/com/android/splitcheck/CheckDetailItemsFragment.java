package com.android.splitcheck;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.splitcheck.data.Item;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class CheckDetailItemsFragment extends Fragment implements CreateItemFragment.
        CreateItemDialogListener {

    CheckItemAdapter mCheckItemAdapter;
    RecyclerView mRecyclerView;
    ImageView mAddItemImageView;
    FloatingActionButton mFloatingActionButton;
    ArrayList<Item> mItems;
    static int mCheckId;

    private final String CHECK_ITEM_RECYCKER_STATE = "check_item_recycler_state";
    private static Bundle mBundleRecyclerViewState;

    // Empty Constructor
    public CheckDetailItemsFragment() {

    }

    public static CheckDetailItemsFragment newInstance(int checkId) {
        CheckDetailItemsFragment fragment = new CheckDetailItemsFragment();
        mCheckId = checkId;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_check_detail_items, container,
                false);

        mCheckId = getArguments().getInt("checkId");

        mRecyclerView = ButterKnife.findById(rootView, R.id.recycler_view_edit_check_items);
        mAddItemImageView = ButterKnife.findById(rootView, R.id.image_view_edit_check_add_item);

        mAddItemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Old Add Item", Toast.LENGTH_SHORT).show();
            }
        });
        mFloatingActionButton = ButterKnife.findById(rootView, R.id.add_item_fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AddItem
                FragmentManager fm = getActivity().getSupportFragmentManager();
                CreateItemFragment createItemFragment = CreateItemFragment.newInstance("New Item Info",
                        mCheckId);
                createItemFragment.setTargetFragment(CheckDetailItemsFragment.this, 400);
                createItemFragment.show(fm, "New Item Info");
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    mFloatingActionButton.hide();
                } else if (dy < 0){
                    mFloatingActionButton.show();
                }
            }
        });
        updateUI();

        return rootView;
    }

    private void updateUI() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Item item = new Item();
        mItems = item.getListOfItemsFromDatabaseFromCheckId(getContext().getContentResolver(),
                mCheckId);
        mCheckItemAdapter = new CheckItemAdapter(getContext(), mItems);
        mRecyclerView.setAdapter(mCheckItemAdapter);
    }

    @Override
    public void onFinishCreateDialog(String inputText, int inputInt) {
        Toast.makeText(getContext(), "Created " + inputText
                + "\nID: " + inputInt, Toast.LENGTH_SHORT).show();
        updateUI();
    }

}
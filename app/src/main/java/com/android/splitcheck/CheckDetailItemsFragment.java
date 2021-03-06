package com.android.splitcheck;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.splitcheck.data.Item;
import com.android.splitcheck.data.ItemParticipant;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class CheckDetailItemsFragment extends Fragment implements
        CreateItemFragment.CreateItemDialogListener,
        EditItemFragment.EditItemDialogListener,
        AssignParticipantFragment.AssignParticipantDialogListener {

    ItemChangeListener listener;

    CheckItemAdapter mCheckItemAdapter;
    RecyclerView mRecyclerView;
    FloatingActionButton mFloatingActionButton;
    ArrayList<Item> mItems;
    ArrayList<ItemParticipant> mItemParticipants;
    static int mCheckId;

    private final String CHECK_ITEM_RECYCLER_STATE = "check_item_recycler_state";
    private static Bundle mBundleRecyclerViewState;

    // Empty Constructor
    public CheckDetailItemsFragment() {

    }

    public static CheckDetailItemsFragment newInstance(int checkId) {
        CheckDetailItemsFragment fragment = new CheckDetailItemsFragment();
        mCheckId = checkId;
        return fragment;
    }

    public interface ItemChangeListener {
        void onChangeItem();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (ItemChangeListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_check_detail_items, container,
                false);

        mCheckId = getArguments().getInt("checkId");

        mRecyclerView = ButterKnife.findById(rootView, R.id.recycler_view_edit_check_items);
        mFloatingActionButton = ButterKnife.findById(rootView, R.id.add_item_fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AddItem
                FragmentManager fm = getActivity().getSupportFragmentManager();
                CreateItemFragment createItemFragment = CreateItemFragment.newInstance(getString(R.string.item_new_item_title),
                        mCheckId);
                createItemFragment.setTargetFragment(CheckDetailItemsFragment.this, 400);
                createItemFragment.show(fm, getString(R.string.item_new_item_title));
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // Hide and unhide FAB on scroll
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
        ItemParticipant itemParticipant = new ItemParticipant();
        mItemParticipants = itemParticipant.getItemListFromDbFromCheckId(getContext().getContentResolver(),
                mCheckId);
        mCheckItemAdapter = new CheckItemAdapter(getContext(), mItems, mItemParticipants);
        mCheckItemAdapter.setOnCheckItemClickedListener(new CheckItemAdapter.OnCheckItemClickedListener() {
            @Override
            public void onCheckItemClickedListener(int itemId, String itemName) {
                String title = getString(R.string.item_assign_title, itemName);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                AssignParticipantFragment assignParticipantFragment = AssignParticipantFragment.newInstance(title, mCheckId, itemId);
                assignParticipantFragment.setTargetFragment(CheckDetailItemsFragment.this, 400);
                assignParticipantFragment.show(fm, title);
            }

            @Override
            public void onCheckItemEditListener(int itemId, String itemName, int itemCost) {
                String title = getString(R.string.item_edit_title);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                EditItemFragment editItemFragment = EditItemFragment.newInstance(title, mCheckId, itemId, itemName, itemCost);
                editItemFragment.setTargetFragment(CheckDetailItemsFragment.this, 400);
                editItemFragment.show(fm, title);
            }
        });
        mRecyclerView.setAdapter(mCheckItemAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();

        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(CHECK_ITEM_RECYCLER_STATE, listState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(CHECK_ITEM_RECYCLER_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    @Override
    public void onFinishCreateDialog(String inputText, int inputInt) {
        updateUI();
        listener.onChangeItem();
    }

    @Override
    public void onFinishEditDialog(String itemName, int itemCost) {
        updateUI();
        listener.onChangeItem();
    }

    @Override
    public void onFinishAssignParticipantDialog() {
        updateUI();
        listener.onChangeItem();
    }
}

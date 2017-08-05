package com.android.splitcheck;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.splitcheck.data.Check;
import com.android.splitcheck.data.CheckContract.CheckEntry;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class CheckListFragment extends Fragment implements CreateCheckFragment.CreateCheckDialogListener{

    private CheckListAdapter mCheckListAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Check> mChecks;

    private final String CHECK_LIST_RECYCLER_STATE = "check_list_recycler_state";
    private static Bundle mBundleRecyclerViewState;

    // Empty Constructor
    public CheckListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_check_list, container, false);

        // Handle Add Check Fab
        rootView.findViewById(R.id.add_check_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Dialog for new Check
                FragmentManager fm = getActivity().getSupportFragmentManager();
                CreateCheckFragment createCheckFragment = CreateCheckFragment.newInstance("Check Name");
                createCheckFragment.setTargetFragment(CheckListFragment.this, 300);
                createCheckFragment.show(fm, "Check Name");
            }
        });

        mRecyclerView = ButterKnife.findById(rootView, R.id.recycler_view_check_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Check check = new Check();
        mChecks = check.getListOfChecksFromDatabase(getContext().getContentResolver());
        mCheckListAdapter = new CheckListAdapter(getActivity(), mChecks);
        mRecyclerView.setAdapter(mCheckListAdapter);
        updateUI();

        return rootView;
    }

    private void updateUI() {
    }

    @Override
    public void onPause() {
        super.onPause();

//        mBundleRecyclerViewState = new Bundle();
//        Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
//        mBundleRecyclerViewState.putParcelable(CHECK_LIST_RECYCLER_STATE, listState);
    }

    @Override
    public void onResume() {
        super.onResume();

//        if (mBundleRecyclerViewState != null) {
//            Parcelable listState = mBundleRecyclerViewState.getParcelable(CHECK_LIST_RECYCLER_STATE);
//            mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
//        }
    }

    @Override
    public void onFinishCreateDialog(String inputText, int inputInt) {
        // Called after Dialog successfully creates check
        Toast.makeText(getContext(), "Created " + inputText
                + "\nID: " + inputInt, Toast.LENGTH_SHORT).show();
        updateUI();

    }
}

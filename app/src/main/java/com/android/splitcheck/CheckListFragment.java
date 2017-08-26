package com.android.splitcheck;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.splitcheck.data.Check;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class CheckListFragment extends Fragment implements CreateCheckFragment
        .CreateCheckDialogListener, EditCheckFragment.EditCheckDialogListener {

    private CheckListAdapter mCheckListAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Check> mChecks;
    private Toolbar mToolbar;
    private FloatingActionButton mFloatingActionButton;

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
        mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.add_check_fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Dialog for new Check
                //TODO Check: Move to its own method
                FragmentManager fm = getActivity().getSupportFragmentManager();
                CreateCheckFragment createCheckFragment = CreateCheckFragment
                        .newInstance("Check Information");
                createCheckFragment.setTargetFragment(CheckListFragment.this, 300);
                createCheckFragment.show(fm, "Check Information");
            }
        });

        mToolbar = ButterKnife.findById(rootView, R.id.check_list_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        mRecyclerView = ButterKnife.findById(rootView, R.id.recycler_view_check_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Check check = new Check();
        mChecks = check.getListOfChecks(getContext().getContentResolver());
        mCheckListAdapter = new CheckListAdapter(getActivity(), mChecks);
        mCheckListAdapter.setOnCheckItemEditClickedListener(new CheckListAdapter.OnCheckItemEditClickedListener() {
            @Override
            public void onCheckItemEditClickedListener(String checkName, int checkId) {
                Toast.makeText(getContext(), ""+checkId, Toast.LENGTH_SHORT).show();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                EditCheckFragment editCheckFragment = EditCheckFragment
                        .newInstance("Update Check Information", checkName, checkId);
                editCheckFragment.setTargetFragment(CheckListFragment.this, 300);
                editCheckFragment.show(fm, "Update Check Information");
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    mFloatingActionButton.hide();
                } else if (dy < 0) {
                    mFloatingActionButton.show();
                }
            }
        });
        mRecyclerView.setAdapter(mCheckListAdapter);

        return rootView;
    }

    private void updateUI() {
        Check check = new Check();
        mChecks = check.getListOfChecks(getContext().getContentResolver());
        mCheckListAdapter.update(mChecks);
    }

    @Override
    public void onPause() {
        super.onPause();

        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(CHECK_LIST_RECYCLER_STATE, listState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(CHECK_LIST_RECYCLER_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    @Override
    public void onFinishCreateDialog(String checkName, int checkId) {
        // Called after Dialog successfully creates check
        Toast.makeText(getContext(), "Created " + checkName
                + "\nID: " + checkId, Toast.LENGTH_SHORT).show();
        updateUI();

        Intent intentToStartEditCheckActivity = new Intent(getActivity(),
                CheckDetailActivity.class);
        intentToStartEditCheckActivity.putExtra("checkId",
                checkId);
        startActivity(intentToStartEditCheckActivity);
    }

    @Override
    public void onFinishEditDialog(String checkName, int checkId) {
        updateUI();
    }
}

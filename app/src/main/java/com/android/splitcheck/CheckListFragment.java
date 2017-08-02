package com.android.splitcheck;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class CheckListFragment extends Fragment {

    private CheckListAdapter mCheckListAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Check> mChecks;

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
                // Start Activity for new Check
                Intent intent = new Intent(getActivity(), CreateCheckActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView = ButterKnife.findById(rootView, R.id.recycler_view_check_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChecks = getChecksFromDatabase();
        mCheckListAdapter = new CheckListAdapter(getActivity(), mChecks);
        mRecyclerView.setAdapter(mCheckListAdapter);

        return rootView;
    }

    private ArrayList<Check> getChecksFromDatabase() {
        Uri uri = CheckEntry.CONTENT_URI;
        uri = uri.buildUpon().build();
        Cursor c = getActivity().getContentResolver().query(uri, null, null, null, null);
        ArrayList<Check> checks = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String name = c.getString(c.getColumnIndex(CheckEntry.NAME));
            int id = c.getInt(c.getColumnIndex(CheckEntry._ID));
            String total = c.getString(c.getColumnIndex(CheckEntry.TOTAL));
            long dateTime = c.getLong(c.getColumnIndex(CheckEntry.TIME_CREATED));
            Check check = new Check(name, id, total, null, null, dateTime);
            checks.add(check);
            c.moveToNext();
        }
        c.close();
        return checks;
    }
}

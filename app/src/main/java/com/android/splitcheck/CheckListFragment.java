package com.android.splitcheck;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class CheckListFragment extends Fragment {

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
                Toast.makeText(getContext(), "New Check", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

}

package com.android.splitcheck;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.splitcheck.data.Participant;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class EditCheckParticipantsFragment extends Fragment {

    //CheckParticipantAdapter mCheckParticipantAdapter;
    RecyclerView mRecyclerView;
    ImageView mAddParticipantImageView;
    int mCheckId;

    public EditCheckParticipantsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_check_participants, container,
                false);

        mCheckId = getArguments().getInt("checkId");

        mRecyclerView = ButterKnife.findById(rootView, R.id.recycler_view_edit_check_participants);
        mAddParticipantImageView = ButterKnife.findById(rootView,
                R.id.image_view_edit_check_add_participant);
        updateUI();

        return rootView;
    }

    private void updateUI() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO PARTICIPANT FRAGMENT
//        Participant participant = new Participant();
//        mParticipants = getParticipants
//        mCheckParticipantAdapter = new CheckItemAdapter(getContext(), mParticipants);
//        mRecyclerView.setAdapter(mCheckParticipantAdapter);
    }
}

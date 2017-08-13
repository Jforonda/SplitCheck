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

import com.android.splitcheck.data.Participant;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class CheckDetailParticipantsFragment extends Fragment {

    CheckParticipantAdapter mCheckParticipantAdapter;
    RecyclerView mRecyclerView;
    ImageView mAddParticipantImageView;
    FloatingActionButton mFloatingActionButton;
    ArrayList<Participant> mParticipants;
    int mCheckId;

    public CheckDetailParticipantsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_check_detail_participants, container,
                false);

        mCheckId = getArguments().getInt("checkId");

        mRecyclerView = ButterKnife.findById(rootView, R.id.recycler_view_edit_check_participants);
        mAddParticipantImageView = ButterKnife.findById(rootView,
                R.id.image_view_edit_check_add_participant);
        mAddParticipantImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Add Participant!", Toast.LENGTH_SHORT).show();
                //TODO Participant: On Click to Dialog. Change to FAB?
            }
        });
        mFloatingActionButton = ButterKnife.findById(rootView, R.id.add_participant_fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                AddParticipantFragment addParticipantFragment = AddParticipantFragment.newInstance(
                        "Add Participant", mCheckId);
                addParticipantFragment.setTargetFragment(CheckDetailParticipantsFragment.this, 500);
                addParticipantFragment.show(fm, "Add Participant");
            }
        });
        updateUI();

        return rootView;
    }

    private void updateUI() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Participant participant = new Participant();
        mParticipants = participant.getListOfParticipantsFromDatabaseFromCheckId(getContext().
                        getContentResolver(), mCheckId);
        mCheckParticipantAdapter = new CheckParticipantAdapter(getContext(), mParticipants);
        mRecyclerView.setAdapter(mCheckParticipantAdapter);
    }

    public void onFinishCreateDialog(String inputText, int inputInt) {
        //TODO Participant: Set up callback from Add Participant Dialog
        updateUI();
    }
}

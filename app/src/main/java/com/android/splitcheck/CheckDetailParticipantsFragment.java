package com.android.splitcheck;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.splitcheck.data.CheckParticipant;
import com.android.splitcheck.data.Participant;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class CheckDetailParticipantsFragment extends Fragment implements
        AddParticipantFragment.AddParticipantDialogListener, CreateParticipantFragment.CreateParticipantDialogListener{

    ParticipantChangeListener listener;

    CheckParticipantAdapter mCheckParticipantAdapter;
    RecyclerView mRecyclerView;
    TextView mTextViewEmptyParticipants;
    FloatingActionButton mFloatingActionButton;
    ArrayList<Participant> mParticipants;
    int mCheckId;

    public CheckDetailParticipantsFragment() {

    }

    public interface ParticipantChangeListener {
        void onChangeParticipant();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (ParticipantChangeListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_check_detail_participants, container,
                false);

        mCheckId = getArguments().getInt("checkId");
        CheckParticipant checkParticipant = new CheckParticipant();
        mRecyclerView = ButterKnife.findById(rootView, R.id.recycler_view_edit_check_participants);
        mTextViewEmptyParticipants = ButterKnife.findById(rootView,
                R.id.text_view_empty_participants);
        if (checkParticipant.checkParticipantIsEmpty(getActivity().getContentResolver(), mCheckId)) {
            mTextViewEmptyParticipants.setVisibility(View.VISIBLE);
        }
        mFloatingActionButton = ButterKnife.findById(rootView, R.id.add_participant_fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                AddParticipantFragment addParticipantFragment = AddParticipantFragment.newInstance(
                        "Add To Group:", mCheckId);
                addParticipantFragment.setTargetFragment(CheckDetailParticipantsFragment.this, 500);
                addParticipantFragment.show(fm, "Add To Group:");
            }
        });
        updateUI();

        return rootView;
    }

    private void updateUI() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        Participant participant = new Participant();
//        mParticipants = participant.getListOfParticipantsFromDatabase(getContext()
//                .getContentResolver());
        CheckParticipant checkParticipant = new CheckParticipant();
        mParticipants = checkParticipant.getListOfParticipantsFromDatabaseFromCheckId(getContext()
                .getContentResolver(), mCheckId);
        Toast.makeText(getActivity(), "Participants: " + mParticipants.size(), Toast.LENGTH_SHORT).show();
        mCheckParticipantAdapter = new CheckParticipantAdapter(getContext(), mParticipants, mCheckId);
        mCheckParticipantAdapter.setOnParticipantRemovedListener(new CheckParticipantAdapter.OnCheckParticipantRemovedListener() {
            @Override
            public void onCheckParticipantRemoved() {
                listener.onChangeParticipant();
            }
        });
        mRecyclerView.setAdapter(mCheckParticipantAdapter);

        if (checkParticipant.checkParticipantIsEmpty(getActivity().getContentResolver(), mCheckId)) {
            mTextViewEmptyParticipants.setVisibility(View.VISIBLE);
        } else {
            mTextViewEmptyParticipants.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFinishAddParticipantDialog() {
        // update UI
        updateUI();
        // should show updated list of participants which were checked
    }

    @Override
    public void startCreateParticipant() {
        // fragment to start CreateParticipantFragment (Dialog)
        FragmentManager fm = getActivity().getSupportFragmentManager();
        CreateParticipantFragment createParticipantFragment = CreateParticipantFragment.newInstance(
                "Create New Participant");
        createParticipantFragment.setTargetFragment(CheckDetailParticipantsFragment.this, 500);
        createParticipantFragment.show(fm, "Create New Participant");

    }

    @Override
    public void onFinishCreateParticipantDialog(String firstName, String lastName) {
        // start create dialog again?
        FragmentManager fm = getActivity().getSupportFragmentManager();
        AddParticipantFragment addParticipantFragment = AddParticipantFragment.newInstance(
                "Add To Group:", mCheckId);
        addParticipantFragment.setTargetFragment(CheckDetailParticipantsFragment.this, 500);
        addParticipantFragment.show(fm, "Add To Group:");
    }
}

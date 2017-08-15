package com.android.splitcheck;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.android.splitcheck.data.CheckParticipant;
import com.android.splitcheck.data.Participant;

import java.util.ArrayList;

public class AddParticipantFragment extends DialogFragment {

    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private AddParticipantDialogListener listener;

    private int mCheckId;
    private Context mContext;

    public AddParticipantFragment() {

    }

    public static AddParticipantFragment newInstance(String title, int checkId) {
        AddParticipantFragment frag = new AddParticipantFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("checkId", checkId);
        frag.setArguments(args);
        return frag;
    }

    public interface AddParticipantDialogListener {
        void onFinishAddParticipantDialog();
        void startCreateParticipant();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_participant_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        mCheckId = getArguments().getInt("checkId");
        final Context context = getContext();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Select participants:");
        // TODO Participant: List by Most Frequent?
        CheckParticipant checkParticipant = new CheckParticipant();
        final ArrayList<Participant> participants;
        ArrayList<Integer> participantIds = new ArrayList<>();
        participants = checkParticipant.getParticipantsNotAdded(getActivity().getContentResolver(),
                mCheckId);
        CharSequence[] charSequenceParticipants = new CharSequence[participants.size()];
        for (int i = 0; i < participants.size(); i++) {
            String firstName = participants.get(i).getFirstName();
            String lastName = participants.get(i).getLastName();

            if (lastName != null) {
                charSequenceParticipants[i] = firstName + " " + lastName;
            } else {
                charSequenceParticipants[i] = firstName;
            }

            int participantId = participants.get(i).getId();
            participantIds.add(participantId);
        }

        final CharSequence[] items = {"Test1", "Test2", "Test3", "Test4",
            "Name 1", "Name 2", "Name 3", "Name 4"};

        final ArrayList<Integer> addedParticipants = new ArrayList<>();
        alertDialogBuilder.setMultiChoiceItems(charSequenceParticipants, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                if (isChecked) {
                    addedParticipants.add(participants.get(indexSelected).getId());
                } else if (addedParticipants.contains(indexSelected)) {
                    //addedParticipants.remove(Integer.valueOf(indexSelected));
                }
            }
        });
        alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!addedParticipants.isEmpty()) {
                    // TODO Participant: Add Participants to CheckParticipant DB
                    for (int i = 0; i < addedParticipants.size(); i++) {
                        createCheckParticipant(mCheckId, addedParticipants.get(i));
                    }
                    sendBackResultParticipantAdded();
                    Toast.makeText(context, "Selected: " + addedParticipants.size(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "None Selected.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Add New", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendBackResultStartCreateParticipant();
            }
        });

        return alertDialogBuilder.create();
    }

    public void sendBackResultParticipantAdded() {
        AddParticipantDialogListener listener = (AddParticipantDialogListener) getTargetFragment();
        listener.onFinishAddParticipantDialog();
        dismiss();
    }

    private void sendBackResultStartCreateParticipant() {
        AddParticipantDialogListener listener = (AddParticipantDialogListener) getTargetFragment();
        listener.startCreateParticipant();
        dismiss();
    }

    private int createCheckParticipant(int checkId, int participantId) {
        CheckParticipant checkParticipant = new CheckParticipant();
        Uri uri = checkParticipant.addToDatabase(getContext().getContentResolver(), checkId,
                participantId);
        return ((int) ContentUris.parseId(uri));
    }
}

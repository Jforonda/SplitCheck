package com.android.splitcheck;

import android.app.Dialog;
import android.content.ContentResolver;
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
import android.widget.EditText;
import android.widget.Toast;

import com.android.splitcheck.data.CheckParticipant;
import com.android.splitcheck.data.Item;
import com.android.splitcheck.data.ItemParticipant;
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        mCheckId = getArguments().getInt("checkId");
        mContext = getContext();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        CheckParticipant checkParticipant = new CheckParticipant();
        final ArrayList<Participant> participants;
        ArrayList<Integer> participantIds = new ArrayList<>();
        participants = checkParticipant.getParticipantsNotAdded(getActivity().getContentResolver(),
                mCheckId);
        final CharSequence[] charSequenceParticipants = new CharSequence[participants.size()];
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

        final ArrayList<Integer> addedParticipants = new ArrayList<>();
        alertDialogBuilder.setMultiChoiceItems(charSequenceParticipants, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                if (isChecked) {
                    addedParticipants.add(participants.get(indexSelected).getId());
                } else if (addedParticipants.contains(indexSelected)) {

                }
            }
        });
        alertDialogBuilder.setPositiveButton(R.string.participant_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!addedParticipants.isEmpty()) {
                    for (int i = 0; i < addedParticipants.size(); i++) {
                        createCheckParticipant(mCheckId, addedParticipants.get(i));
                        createItemParticipant(mCheckId, addedParticipants.get(i), charSequenceParticipants[i].toString());
                    }
                    sendBackResultParticipantAdded();
                } else {
                    Toast.makeText(mContext, R.string.participant_no_one_added, Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.participant_add_new, new DialogInterface.OnClickListener() {
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

    private void createItemParticipant(int checkId, int participantId,
                                      String participantName) {
        ItemParticipant itemParticipant = new ItemParticipant();
        Item item = new Item();
        ContentResolver contentResolver = getActivity().getContentResolver();
        ArrayList<Item> items = item.getListOfItemsFromDatabaseFromCheckId(contentResolver, mCheckId);
        for (int i = 0; i < items.size(); i++) {
            Item currentItem = items.get(i);
            Uri uri = itemParticipant.addToDb(getActivity().getContentResolver(), checkId, currentItem.getId(), participantId, participantName);
        }
    }
}

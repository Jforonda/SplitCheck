package com.android.splitcheck;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
        void onFinishCreateDialog(String firstName, String lastName);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_check, container);
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
        // TODO Get item list from Participant DB, List by Most Frequent?
        final CharSequence[] items = {"Test1", "Test2", "Test3", "Test4",
            "Name 1", "Name 2", "Name 3", "Name 4"};
        final ArrayList<String> addedParticipants = new ArrayList<>();
        alertDialogBuilder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                if (isChecked) {
                    addedParticipants.add((String)items[indexSelected]);
                } else if (addedParticipants.contains(indexSelected)) {
                    addedParticipants.remove(Integer.valueOf(indexSelected));
                }
            }
        });
        alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!addedParticipants.isEmpty()) {
                    // TODO Add Participants to CheckParticipant DB
                    Toast.makeText(context, "Selected: " + addedParticipants.size(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "None Selected.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Add New", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return alertDialogBuilder.create();
    }

    public void sendBackResult() {

    }
}

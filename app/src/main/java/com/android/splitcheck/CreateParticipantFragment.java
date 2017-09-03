package com.android.splitcheck;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.splitcheck.data.Participant;

import butterknife.ButterKnife;

public class CreateParticipantFragment extends DialogFragment {

    private TextInputLayout mTextInputLayoutFirstName;
    private TextInputLayout mTextInputLayoutLastName;
    CreateParticipantDialogListener listener;

    private Context mContext;

    public CreateParticipantFragment() {

    }

    public static CreateParticipantFragment newInstance(String title) {
        CreateParticipantFragment frag = new CreateParticipantFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public interface CreateParticipantDialogListener {
        void onFinishCreateParticipantDialog(String firstName, String lastName);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getTargetFragment() != null) {
            listener = (CreateParticipantDialogListener) getTargetFragment();
        } else {
            listener = (CreateParticipantDialogListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_participant_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        mContext = getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final View promptView = layoutInflater.inflate(R.layout.fragment_create_participant_dialog,
                null);
        mTextInputLayoutFirstName = ButterKnife.findById(promptView,
                R.id.text_input_layout_participant_first_name);
        mTextInputLayoutLastName = ButterKnife.findById(promptView,
                R.id.text_input_layout_participant_last_name);
        final EditText editTextFirstName = ButterKnife.findById(promptView,
                R.id.edit_text_participant_first_name);
        final EditText editTextLastName = ButterKnife.findById(promptView,
                R.id.edit_text_participant_last_name);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setView(promptView);

        alertDialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!editTextFirstName.getText().toString().isEmpty()
                        && !editTextLastName.getText().toString().isEmpty()) {
                    String newFirstName = editTextFirstName.getText().toString();
                    String newLastName = editTextLastName.getText().toString();
                    int newId = createParticipant(newFirstName, newLastName);
                    sendBackResultParticipantCreated(newFirstName, newLastName, newId);
                } else {
                    Toast.makeText(mContext, "Error with input. No participant added", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return alertDialogBuilder.create();
    }

    public void sendBackResultParticipantCreated(String firstName, String lastName, int participantId) {
        listener.onFinishCreateParticipantDialog(firstName, lastName);
        dismiss();
    }

    private int createParticipant(String firstName, String lastName) {
        Participant participant = new Participant();
        Uri uri = participant.addToDatabase(getContext().getContentResolver(), firstName, lastName);
        return ((int) ContentUris.parseId(uri));
    }
}

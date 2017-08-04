package com.android.splitcheck;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.splitcheck.data.Check;

public class CreateCheckFragment extends DialogFragment {

    private EditText mEditText;

    private CreateCheckDialogListener listener;

    // Empty Constructor
    public CreateCheckFragment() {

    }

    public static CreateCheckFragment newInstance(String title) {
        CreateCheckFragment frag = new CreateCheckFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public interface CreateCheckDialogListener {
        void onFinishCreateDialog(String checkName, int checkId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_check,container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**mEditText = (EditText) view.findViewById(R.id.edit_text_create_check);
        mButton = (Button) view.findViewById(R.id.button_create_check);

        String title = getArguments().getString("title", "Check Name");
        getDialog().setTitle(title);

        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Created", Toast.LENGTH_SHORT).show();
            }
        });**/
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        // Set up Edit Text
        mEditText = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mEditText.setLayoutParams(lp);
        mEditText.setHint("Breakfast");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setView(mEditText);
        alertDialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Create Check and add to database if EditText is not empty
                if (!mEditText.getText().toString().isEmpty()) {
                    String newCheckName = mEditText.getText().toString();
                    int newCheckId = createCheck(newCheckName);
                    sendBackResult(newCheckName, newCheckId);
                    // Open edit check Activity
                    // Use last created Check

                }
            }
        });
        return alertDialogBuilder.create();
    }

    public void sendBackResult(String checkName, int checkId) {
        CreateCheckDialogListener listener = (CreateCheckDialogListener) getTargetFragment();
        listener.onFinishCreateDialog(checkName, checkId);
        dismiss();

    }

    public int createCheck(String checkName) {
        Check check = new Check();
        Uri uri = check.addToDatabase(getActivity().getContentResolver(),checkName);
        return ((int)ContentUris.parseId(uri));
    }
}

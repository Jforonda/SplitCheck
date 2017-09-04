package com.android.splitcheck;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
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

import com.android.splitcheck.data.Check;
import com.android.splitcheck.data.Modifier;

public class CreateCheckFragment extends DialogFragment {

    private TextInputLayout mTextInputLayoutName;
    private Context mContext;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        mContext = getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final View promptView = layoutInflater.inflate(R.layout.fragment_create_check_dialog, null);
        final EditText editTextName = (EditText) promptView.findViewById(R.id.edit_text_check_name);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setPositiveButton(R.string.check_create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Create Check and add to database if EditText is not empty
                if (!editTextName.getText().toString().isEmpty()) {
                    String newCheckName = editTextName.getText().toString();
                    int newCheckId = createCheck(newCheckName);
                    sendBackResult(newCheckName, newCheckId);
                }
            }
        });

        Dialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    public int createCheck(String checkName) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Check check = new Check();
        Uri uri = check.addToDatabase(contentResolver,checkName);
        Modifier modifier = new Modifier();
        Uri modifierUri = modifier.addDefaultToDatabase(contentResolver, (int)ContentUris.parseId(uri));
        return ((int)ContentUris.parseId(uri));
    }

    public void sendBackResult(String checkName, int checkId) {
        CreateCheckDialogListener listener = (CreateCheckDialogListener) getTargetFragment();
        listener.onFinishCreateDialog(checkName, checkId);
        dismiss();

    }
}

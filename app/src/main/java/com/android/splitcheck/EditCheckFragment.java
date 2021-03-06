package com.android.splitcheck;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.android.splitcheck.data.Check;

public class EditCheckFragment extends DialogFragment {

    private TextInputLayout mTextInputLayoutName;

    private String mCheckName;
    private int mCheckId;
    private Context mContext;

    public EditCheckFragment() {

    }

    public static EditCheckFragment newInstance(String title, String checkName, int checkId) {
        EditCheckFragment frag = new EditCheckFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("checkName", checkName);
        args.putInt("checkId", checkId);
        frag.setArguments(args);
        return frag;
    }

    public interface EditCheckDialogListener {
        void onFinishEditDialog(String checkName, int checkId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        mCheckName = getArguments().getString("checkName");
        mCheckId = getArguments().getInt("checkId");
        mContext = getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final View promptView = layoutInflater.inflate(R.layout.fragment_create_check_dialog, null);
        final EditText editTextName = (EditText) promptView.findViewById(R.id.edit_text_check_name);
        editTextName.setText(mCheckName);
        editTextName.selectAll();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setPositiveButton(R.string.check_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!editTextName.getText().toString().isEmpty()) {
                    String newCheckName = editTextName.getText().toString();
                    int newCheckId = updateCheck(newCheckName, mCheckId);
                    sendBackResult(newCheckName, newCheckId);
                }
            }
        });

        Dialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return dialog;
    }

    public int updateCheck(String checkName, int checkId) {
        Check check = new Check();
        return check.updateName(mContext.getContentResolver(),
                checkName, checkId);
    }

    public void sendBackResult(String checkName, int checkId) {
        EditCheckDialogListener listener = (EditCheckDialogListener) getTargetFragment();
        listener.onFinishEditDialog(checkName, checkId);
        dismiss();
    }
}

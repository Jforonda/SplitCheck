package com.android.splitcheck;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.android.splitcheck.data.Modifier;

import butterknife.ButterKnife;

public class ChangeModifierFragment extends DialogFragment {

    private TextInputLayout mTextInputLayoutAmount;

    private Context mContext;
    private int mCheckId;

    public ChangeModifierFragment() {

    }

    public static ChangeModifierFragment newInstance(String title, int checkId, int modifierType,
                                                     int currentAmount, boolean isPercent) {
        ChangeModifierFragment frag = new ChangeModifierFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("checkId", checkId);
        args.putInt("modifierType", modifierType);
        args.putInt("currentAmount", currentAmount);
        args.putBoolean("isPercent", isPercent);
        frag.setArguments(args);
        return frag;
    }

    public interface CreateModifierDialogListener {
        void onFinishChangeModifierDialog(int amount, int modifierType);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_modifier_dialog, container);
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
        mCheckId = getArguments().getInt("checkId");
        int currentAmount = getArguments().getInt("currentAmount");
        final boolean isPercent = getArguments().getBoolean("isPercent");
        final int modifierType = getArguments().getInt("modifierType");

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final View promptView = layoutInflater.inflate(R.layout.fragment_change_modifier_dialog,
                null);
        mTextInputLayoutAmount = ButterKnife.findById(promptView, R.id.text_input_layout_modifier);
        final EditText editTextAmount = ButterKnife.findById(promptView, R.id.edit_text_modifier);

        if (isPercent) mTextInputLayoutAmount.setHint("Percent");
        // If 0, leave amount blank.
        // If amount is > 0, set text as amount, highlighted
        if (currentAmount != 0) {
            editTextAmount.setText(String.valueOf(currentAmount));
            editTextAmount.selectAll();
        }
        editTextAmount.setKeyListener(DigitsKeyListener.getInstance());
        // TODO Modifier: Add a Text Input Listener for money input, or percentage
        // If percentage, allow decimals

        // If amount, allow up to two decimal points, show dollar sign

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setView(promptView);

        String positiveButtonText = isPercent ? "Set Percent" : "Set Amount";
        alertDialogBuilder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int userInput = Integer.parseInt(editTextAmount.getText().toString());
                Modifier modifier = new Modifier();
                ContentResolver contentResolver = getActivity().getContentResolver();
                switch (modifierType) {
                    case 0:
                        modifier.updateTax(contentResolver, mCheckId, userInput);
                        break;
                    case 1:
                        modifier.updateTip(contentResolver, mCheckId, userInput);
                        break;
                    case 2:
                        modifier.updateGratuity(contentResolver, mCheckId, userInput);
                        break;
                    case 3:
                        modifier.updateFees(contentResolver, mCheckId, userInput);
                        break;
                    case 4:
                        modifier.updateDiscount(contentResolver, mCheckId, userInput);
                        break;
                    default:
                        return;
                }
                sendBackResultChangeModifier(userInput, modifierType);
            }
        });
        return alertDialogBuilder.create();
    }

    public void sendBackResultChangeModifier(int amount, int modifierType) {
        CreateModifierDialogListener listener = (CreateModifierDialogListener) getTargetFragment();
        listener.onFinishChangeModifierDialog(amount, modifierType);
        dismiss();
    }

}

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
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.android.splitcheck.data.Modifier;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;

import butterknife.ButterKnife;

public class ChangeModifierFragment extends DialogFragment {

    private TextInputLayout mTextInputLayoutAmount;

    private Context mContext;
    private int mCheckId;
    private boolean mIsPercent;
    private int mCurrentAmount;
    private int count = 0;

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
        void onFinishChangeModifierDialog(int amount, int modifierType, boolean isPercent);
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
        mCurrentAmount = getArguments().getInt("currentAmount");
        mIsPercent = getArguments().getBoolean("isPercent");
        final int modifierType = getArguments().getInt("modifierType");

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final View promptView = layoutInflater.inflate(R.layout.fragment_change_modifier_dialog,
                null);
        mTextInputLayoutAmount = ButterKnife.findById(promptView, R.id.text_input_layout_modifier);
        final EditText editTextAmount = ButterKnife.findById(promptView, R.id.edit_text_modifier);

        if (mIsPercent) mTextInputLayoutAmount.setHint("Percent");

        // If amount, allow up to two decimal points, show dollar sign
        if (mIsPercent) {
            editTextAmount.addTextChangedListener(new PercentTextWatcher(editTextAmount));
            if (mCurrentAmount != 0) {
                editTextAmount.setText(String.valueOf(mCurrentAmount));
                editTextAmount.selectAll();
            }
        } else {
            editTextAmount.addTextChangedListener(new MoneyTextWatcher(editTextAmount));
            if (mCurrentAmount != 0) {
                editTextAmount.setText(String.valueOf(mCurrentAmount));
                editTextAmount.selectAll();
            }
            editTextAmount.setKeyListener(DigitsKeyListener.getInstance());
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setView(promptView);

        String positiveButtonText = mIsPercent ? "Set Percent" : "Set Amount";
        alertDialogBuilder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int userInput;
                if (mIsPercent) {
                    String input = editTextAmount.getText().toString();
                    if (input.contains(".")) {
                        input = input.replaceAll("[.]","");
                        userInput = Integer.parseInt(input);
                    } else {
                        userInput = Integer.parseInt(editTextAmount.getText().toString());
                    }

                } else {
                    userInput =  Integer.parseInt(editTextAmount.getText().toString().replaceAll("[$,.]",""));
                }
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

    /**
     * This method is credit to ToddH on stackoverflow
     * https://stackoverflow.com/questions/5107901/better-way-to-format-currency-input-edittext
     */
    public class MoneyTextWatcher implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public MoneyTextWatcher(EditText editText) {
            editTextWeakReference = new WeakReference<EditText>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            EditText editText = editTextWeakReference.get();
            if (editText == null) return;
            String s = editable.toString();
            editText.removeTextChangedListener(this);
            String cleanString = s.toString().replaceAll("[$,.]","");
            BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
            String formatted = NumberFormat.getCurrencyInstance().format(parsed);
            editText.setText(formatted);
            editText.setSelection(formatted.length());
            editText.addTextChangedListener(this);
        }
    }

    public class PercentTextWatcher implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public PercentTextWatcher(EditText editText) {
            editTextWeakReference = new WeakReference<EditText>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            final EditText editText = editTextWeakReference.get();
            if (editText == null) return;
            String formatted = "";
            if (editable.toString().startsWith("0")) {
                editable = editable.delete(0,0);
            }
            if (editable.length() != 0) {
                String s = editable.toString();
                editText.removeTextChangedListener(this);
                String cleanString = s.replaceAll("[,.]", "");
                int parsed;
                if (cleanString.isEmpty()) {
                    formatted = "";
                } else {
                    parsed = Integer.valueOf(cleanString);
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    nf.setMaximumFractionDigits(2);
                    formatted = nf.format(parsed);
                }
            }
            if (editable.length() == 0) {
                formatted = "";
            }
            if (editable.length() == 1) {
                if (editable.toString().equals(".")) {
                    formatted = "";
                } else if (editable.toString().equals("0")) {
                    formatted = "";
                } else {
                    formatted = "." + formatted;
                }
            }
            if (editable.length() == 2) {
                formatted = "." + formatted;
            }
            if (editable.length() > 2) {
                String beforeDecimal = formatted.substring(0, formatted.length()-2);
                String afterDecimal = formatted.substring(formatted.length()-2, formatted.length());
                formatted = beforeDecimal + "." + afterDecimal;
            }
            editText.setText(formatted);
            editText.setSelection(formatted.length());
            editText.addTextChangedListener(this);
        }
    }

    public void sendBackResultChangeModifier(int amount, int modifierType) {
        CreateModifierDialogListener listener = (CreateModifierDialogListener) getTargetFragment();
        listener.onFinishChangeModifierDialog(amount, modifierType, mIsPercent);
        dismiss();
    }

}

package com.android.splitcheck;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;

public class CreateItemFragment extends DialogFragment {

    private EditText mNameEditText;
    private EditText mCostEditText;

    private CreateItemDialogListener listener;

    public CreateItemFragment() {

    }

    public static CreateItemFragment newInstance(String title) {
        CreateItemFragment frag = new CreateItemFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public interface CreateItemDialogListener {
        void onFinishCreateDialog(String itemName, int itemCost);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_check, container);
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
        Context context = getContext();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        mNameEditText = new EditText(context);
        mNameEditText.setLayoutParams(lp);
        mNameEditText.setHint("Name");
        layout.addView(mNameEditText);

        mCostEditText = new EditText(context);
        mCostEditText.setLayoutParams(lp);
        mCostEditText.setHint("Cost");
        mCostEditText.setKeyListener(DigitsKeyListener.getInstance());
        mCostEditText.addTextChangedListener(new MoneyTextWatcher(mCostEditText));
        layout.addView(mCostEditText);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Create Check and add to database if EditText is not empty
                if (!mNameEditText.getText().toString().isEmpty()
                        && !mCostEditText.getText().toString().isEmpty()) {
                    String newItemName = mNameEditText.getText().toString();
//                    int newItemCost = Integer.parseInt(mCostEditText.getText().toString());
                    String itemCost = mCostEditText.getText().toString();
                    String parsedCost = itemCost.replaceAll("[$,.]","");
                    int newItemCost = Integer.parseInt(parsedCost);
                    sendBackResult(newItemName, newItemCost);
                }
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

    public void sendBackResult(String itemName, int itemCost) {
        CreateItemDialogListener listener = (CreateItemDialogListener) getTargetFragment();
        //listener.onFinishCreateDialog(itemName, itemCost);
        dismiss();
    }
}
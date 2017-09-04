package com.android.splitcheck;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.splitcheck.data.Item;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;

import butterknife.ButterKnife;

public class EditItemFragment extends DialogFragment {

    private EditText mNameEditText;
    private EditText mCostEditText;
    private EditItemDialogListener listener;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutCost;

    private int mCheckId;
    private int mItemId;
    private String mItemName;
    private int mItemCost;
    private Context mContext;

    public EditItemFragment() {

    }

    public static EditItemFragment newInstance(String title, int checkId, int itemId,
                                                 String itemName, int itemCost) {
        EditItemFragment frag = new EditItemFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("checkId", checkId);
        args.putInt("itemId", itemId);
        args.putString("itemName", itemName);
        args.putInt("itemCost", itemCost);
        frag.setArguments(args);
        return frag;
    }

    public interface EditItemDialogListener {
        void onFinishEditDialog(String itemName, int itemCost);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        mCheckId = getArguments().getInt("checkId");
        mItemId = getArguments().getInt("itemId");
        mItemName = getArguments().getString("itemName");
        mItemCost = getArguments().getInt("itemCost");
        mContext = getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final View promptView = layoutInflater.inflate(R.layout.fragment_create_item_dialog, null);
        textInputLayoutName = ButterKnife.findById(promptView, R.id.text_input_layout_item_name);
        textInputLayoutCost = ButterKnife.findById(promptView, R.id.text_input_layout_item_cost);
        final EditText editTextName = ButterKnife.findById(promptView, R.id.edit_text_item_name);
        final EditText editTextCost = ButterKnife.findById(promptView, R.id.edit_text_item_cost);
        editTextCost.setKeyListener(DigitsKeyListener.getInstance());
        editTextCost.addTextChangedListener(new MoneyTextWatcher(editTextCost));
        editTextName.setText(mItemName);
        editTextName.selectAll();
        editTextCost.setText(String.valueOf(mItemCost));
        editTextName.setSelectAllOnFocus(true);
        editTextCost.setSelectAllOnFocus(true);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setView(promptView);

        alertDialogBuilder.setPositiveButton(R.string.item_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!editTextName.getText().toString().isEmpty()
                        && !editTextCost.getText().toString().isEmpty()) {
                    String newItemName = editTextName.getText().toString();
                    String itemCost = editTextCost.getText().toString();
                    String parsedCost = itemCost.replaceAll("[$,.]","");
                    int newItemCost = Integer.parseInt(parsedCost);
                    updateItem(mItemId, newItemName, newItemCost);
                    sendBackResult(newItemName, newItemCost);
                } else if (editTextName.getText().toString().isEmpty()
                        && editTextCost.getText().toString().isEmpty()){
                    Toast.makeText(mContext, R.string.item_edit_error, Toast.LENGTH_SHORT).show();

                } else if (editTextName.getText().toString().isEmpty()
                        && !editTextCost.getText().toString().isEmpty()){
                    Toast.makeText(mContext, R.string.item_edit_error, Toast.LENGTH_SHORT).show();

                } else if (!editTextName.getText().toString().isEmpty()
                        && editTextCost.getText().toString().isEmpty()){
                    Toast.makeText(mContext, R.string.item_edit_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Dialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
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
            String cleanString = s.replaceAll("[$,.]","");
            BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
            String formatted = NumberFormat.getCurrencyInstance().format(parsed);
            editText.setText(formatted);
            editText.setSelection(formatted.length());
            editText.addTextChangedListener(this);
        }
    }

    public void sendBackResult(String itemName, int itemCost) {
        EditItemDialogListener listener = (EditItemDialogListener) getTargetFragment();
        listener.onFinishEditDialog(itemName, itemCost);
        dismiss();
    }

    private int updateItem(int id, String name, int cost) {
        Item item = new Item();
        return item.updateItem(mContext.getContentResolver(), id, name, cost);
    }


}

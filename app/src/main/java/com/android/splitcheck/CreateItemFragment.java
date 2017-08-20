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
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.splitcheck.data.CheckParticipant;
import com.android.splitcheck.data.Item;
import com.android.splitcheck.data.ItemParticipant;
import com.android.splitcheck.data.Participant;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

import butterknife.ButterKnife;

public class CreateItemFragment extends DialogFragment {

    private EditText mNameEditText;
    private EditText mCostEditText;
    private CreateItemDialogListener listener;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutCost;
    //EditText editTextName;

    private int mCheckId;
    private Context mContext;

    public CreateItemFragment() {

    }

    public static CreateItemFragment newInstance(String title, int checkId) {
        CreateItemFragment frag = new CreateItemFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("checkId", checkId);
        frag.setArguments(args);
        return frag;
    }

    public interface CreateItemDialogListener {
        void onFinishCreateDialog(String itemName, int itemCost);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_item_dialog, container);
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
        mCheckId = getArguments().getInt("checkId");
        mContext = getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final View promptView = layoutInflater.inflate(R.layout.fragment_create_item_dialog, null);
        textInputLayoutName = ButterKnife.findById(promptView, R.id.text_input_layout_item_name);
        textInputLayoutCost = ButterKnife.findById(promptView, R.id.text_input_layout_item_cost);
        final EditText editTextName = ButterKnife.findById(promptView, R.id.edit_text_item_name);
        final EditText editTextCost = ButterKnife.findById(promptView, R.id.edit_text_item_cost);
        editTextCost.setKeyListener(DigitsKeyListener.getInstance());
        editTextCost.addTextChangedListener(new MoneyTextWatcher(editTextCost));

        // Custom Dialog, Remove if needed
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);

        mNameEditText = new EditText(mContext);
        mNameEditText.setLayoutParams(lp);
        mNameEditText.setHint("Name");
        layout.addView(mNameEditText);

        mCostEditText = new EditText(mContext);
        mCostEditText.setLayoutParams(lp);
        mCostEditText.setHint("Cost");
        mCostEditText.setKeyListener(DigitsKeyListener.getInstance());
        mCostEditText.addTextChangedListener(new MoneyTextWatcher(mCostEditText));
        layout.addView(mCostEditText);
        // Custom Dialog, Remove if needed

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
//        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setView(promptView);


        // TODO Item: If possible, gray out Positive Button when fields are empty
        alertDialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Create Check and add to database if EditText is not empty
                if (!editTextName.getText().toString().isEmpty()
                        && !editTextCost.getText().toString().isEmpty()) {
                    //TODO Item: Handle Empty EditText. Snackbar warning?
                    String newItemName = editTextName.getText().toString();
                    String itemCost = editTextCost.getText().toString();
                    String parsedCost = itemCost.replaceAll("[$,.]","");
                    int newItemCost = Integer.parseInt(parsedCost);
                    int newId = createItem(newItemName, newItemCost);
                    createItemParticipants(newId);
                    sendBackResult(newItemName, mCheckId);
                } else if (editTextName.getText().toString().isEmpty()
                        && editTextCost.getText().toString().isEmpty()){
                    Toast.makeText(mContext, "Error with input. No item added.", Toast.LENGTH_SHORT).show();

                } else if (editTextName.getText().toString().isEmpty()
                        && !editTextCost.getText().toString().isEmpty()){
                    Toast.makeText(mContext, "Error with input. No item added", Toast.LENGTH_SHORT).show();

                } else if (!editTextName.getText().toString().isEmpty()
                        && editTextCost.getText().toString().isEmpty()){
                    Toast.makeText(mContext, "Error with input. No item added.", Toast.LENGTH_SHORT).show();
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
        // Send new item name and cost back to calling fragment (CheckDetailFragment)
        CreateItemDialogListener listener = (CreateItemDialogListener) getTargetFragment();
        listener.onFinishCreateDialog(itemName, itemCost);
        dismiss();
    }

    private int createItem(String name, int cost) {
        // Create Item from user input, add into Database
        Item item = new Item();
        Uri uri = item.addToDatabase(getContext().getContentResolver(), name, cost, mCheckId);
        return ((int) ContentUris.parseId(uri));
    }

    private void createItemParticipants(int itemId) {
        CheckParticipant checkParticipant = new CheckParticipant();
        ItemParticipant itemParticipant = new ItemParticipant();
        ContentResolver contentResolver = getActivity().getContentResolver();
        ArrayList<Participant> checkParticipants = checkParticipant.getListOfParticipantsFromDatabaseFromCheckId(contentResolver, mCheckId);
        if (checkParticipants.size() == 0) {
            Toast.makeText(mContext, "No CPs", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Adding IPs", Toast.LENGTH_SHORT).show();
        }
        for (int i = 0; i < checkParticipants.size(); i++ ) {
            Participant participant = checkParticipants.get(i);
            itemParticipant.addToDb(contentResolver, mCheckId, itemId, participant.getId(),
                    participant.getFirstName() + " " + participant.getLastName());
        }
    }

}

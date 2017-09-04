package com.android.splitcheck;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.splitcheck.data.ItemParticipant;
import com.android.splitcheck.data.ItemParticipantContract;

import java.util.ArrayList;

public class AssignParticipantFragment extends DialogFragment {

    private AssignParticipantDialogListener listener;
    private int mCheckId;
    private int mItemId;
    private Context mContext;
    private ArrayList<Integer> mSelectedItems;
    private int[] mSelectedArray;
    private Cursor mCursor;

    public AssignParticipantFragment() {

    }

    public static AssignParticipantFragment newInstance(String title, int checkId, int itemId) {
        AssignParticipantFragment frag = new AssignParticipantFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("checkId", checkId);
        args.putInt("itemId", itemId);
        frag.setArguments(args);
        return frag;
    }

    public interface AssignParticipantDialogListener {
        void onFinishAssignParticipantDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        mCheckId = getArguments().getInt("checkId");
        mItemId = getArguments().getInt("itemId");
        mContext = getContext();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        final ItemParticipant itemParticipant = new ItemParticipant();
        mCursor = itemParticipant.getItemCursorFromDb(getActivity().getContentResolver(), mItemId);
        mSelectedItems = new ArrayList();

        mSelectedArray = new int[mCursor.getCount()];
        for (int i = 0; i < mCursor.getCount(); i++) {
            mCursor.moveToPosition(i);
            mSelectedArray[i] = mCursor.getInt(mCursor.getColumnIndex("is_checked"));
        }
        alertDialogBuilder.setMultiChoiceItems(mCursor, "is_checked", "participant_name", new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    mSelectedArray[which] = 1;
                } else {
                    mSelectedArray[which] = 0;
                }
            }
        });
        alertDialogBuilder.setPositiveButton(R.string.participant_assign, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < mSelectedArray.length; i++) {
                    mCursor.moveToPosition(i);
                    int itemId = mCursor.getInt(mCursor.getColumnIndex(ItemParticipantContract.ItemParticipantEntry.ITEM_ID));
                    int participantId = mCursor.getInt(mCursor.getColumnIndex(ItemParticipantContract.ItemParticipantEntry.PARTICIPANT_ID));
                    itemParticipant.updateIsChecked(getActivity().getContentResolver(), itemId, participantId, mSelectedArray[i]);
                }
                mCursor.close();
                sendBackResult();
            }
        });

        return alertDialogBuilder.create();
    }

    public void sendBackResult() {
        AssignParticipantDialogListener listener = (AssignParticipantDialogListener) getTargetFragment();
        listener.onFinishAssignParticipantDialog();
        dismiss();
    }

}

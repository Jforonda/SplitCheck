package com.android.splitcheck;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.splitcheck.data.ItemParticipant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckItemParticipantAdapter extends ArrayAdapter<ItemParticipant> {
    static ArrayList<ItemParticipant> mItemParticipants;
    static Context mContext;

    public CheckItemParticipantAdapter(Context context, ArrayList<ItemParticipant> itemParticipants) {
        super(context, 0, itemParticipants);
        mContext = context;
        mItemParticipants = itemParticipants;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_participant_item, parent, false);
        TextView mTextView = ButterKnife.findById(convertView, R.id.text_view_item_participant_name);

        if (mItemParticipants.size() > 0) {
            ItemParticipant itemParticipant = mItemParticipants.get(position);
            mTextView.setText(itemParticipant.getParticipantName());
        }

        return convertView;
    }
}

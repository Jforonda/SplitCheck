package com.android.splitcheck;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.splitcheck.data.Participant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckParticipantAdapter extends
        RecyclerView.Adapter<CheckParticipantAdapter.ViewHolder> {
    static ArrayList<Participant> mParticipants;
    static Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view_participant_name) TextView mNameTextView;
        public LinearLayout mLinearLayout;
        public ViewHolder(LinearLayout v) {
            super(v);
            ButterKnife.bind(this, v);
            mLinearLayout = v;
        }
    }

    public CheckParticipantAdapter(Context context, ArrayList<Participant> participants) {
        mContext = context;
        mParticipants = participants;
    }

    @Override
    public CheckParticipantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.check_participant_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mNameTextView.setText(mParticipants.get(position).getFirstName());
    }

    @Override
    public int getItemCount() {
        return mParticipants.size();
    }
}

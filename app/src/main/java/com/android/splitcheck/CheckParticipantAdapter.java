package com.android.splitcheck;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.splitcheck.data.CheckParticipant;
import com.android.splitcheck.data.ItemParticipant;
import com.android.splitcheck.data.Participant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckParticipantAdapter extends
        RecyclerView.Adapter<CheckParticipantAdapter.ViewHolder> {
    static ArrayList<Participant> mParticipants;
    static Context mContext;
    static int mCheckId;
    private OnCheckParticipantRemovedListener listener;

    public interface OnCheckParticipantRemovedListener {
        void onCheckParticipantRemoved();
    }

    public void setOnParticipantRemovedListener(OnCheckParticipantRemovedListener l) {
        listener = l;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view_participant_name) TextView mNameTextView;
        @BindView(R.id.text_view_participant_total) TextView mTotalTextView;
        @BindView(R.id.options_icon_check_participant) ImageView mOptionsImageView;
        public LinearLayout mLinearLayout;
        public ViewHolder(LinearLayout v) {
            super(v);
            ButterKnife.bind(this, v);
            mLinearLayout = v;

            mOptionsImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext, v);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int currentParticipantId = mParticipants.get(getAdapterPosition()).getId();
                            String currentParticipantName = mParticipants
                                    .get(getAdapterPosition()).getFirstName() + " " +
                                    mParticipants.get(getAdapterPosition()).getLastName();
                            switch (item.getItemId()) {
                                case R.id.participant_item_delete:
                                    removeParticipantById(currentParticipantId);
                                    removeAt(getAdapterPosition());
                                    Snackbar
                                            .make(v, currentParticipantName + " Removed", Snackbar.LENGTH_LONG)
                                            .show();
                                    listener.onCheckParticipantRemoved();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.inflate(R.menu.menu_participant_item);
                    popupMenu.show();
                }
            });
        }
    }

    public CheckParticipantAdapter(Context context, ArrayList<Participant> participants, int checkId) {
        mContext = context;
        mParticipants = participants;
        mCheckId = checkId;
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
        Participant currentParticipant = mParticipants.get(position);
        String participantName = currentParticipant.getFirstName() + " " + currentParticipant.getLastName();
        holder.mNameTextView.setText(participantName);
        ItemParticipant itemParticipant = new ItemParticipant();
        String participantTotal = itemParticipant.getParticipantTotal(mContext.getContentResolver(), mCheckId, currentParticipant.getId());
        holder.mTotalTextView.setText(participantTotal);
        holder.mOptionsImageView.setColorFilter(R.color.colorBlack);
    }

    @Override
    public int getItemCount() {
        return mParticipants.size();
    }

    private Uri removeParticipantById(int id) {
        CheckParticipant checkParticipant = new CheckParticipant();
        return checkParticipant.deleteFromDb(mContext.getContentResolver(), mCheckId, id);
    }

    private void removeAt(int position) {
        mParticipants.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mParticipants.size());
    }
}

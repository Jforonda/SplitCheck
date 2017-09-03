package com.android.splitcheck;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.splitcheck.data.Participant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {
    ArrayList<Participant> mParticipants;
    Context mContext;
    OnParticipantDeletedListener listener;

    public interface OnParticipantDeletedListener {
        void onParticipantDeleted();
    }

    public void setOnParticipantDeletedListener(OnParticipantDeletedListener l) {
        listener = l;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view_search_participant_name) TextView mNameTextView;
        @BindView(R.id.options_icon_search_participant) ImageView mOptionsImageView;
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
                            final int currentParticipantId = mParticipants.get(getAdapterPosition()).getId();
                            final String currentParticipantName = mParticipants.get(getAdapterPosition()).getFirstName() + " " +
                                    mParticipants.get(getAdapterPosition()).getLastName();
                            switch (item.getItemId()) {
                                case R.id.search_participant_delete:
                                    if (currentParticipantId == 1) {
                                        Snackbar snackbar = Snackbar.make(v, "Can not delete yourself!", Snackbar.LENGTH_LONG);
                                        View sbView = snackbar.getView();
                                        sbView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                                        TextView tv = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                        tv.setTextColor(Color.WHITE);
                                        snackbar.show();
                                    } else {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                                        alertDialogBuilder.setTitle("Are you sure?");
                                        alertDialogBuilder.setMessage("Deleting " + currentParticipantName + " will remove them from all checks.");
                                        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteParticipantById(currentParticipantId);
                                                removeAt(getAdapterPosition());

                                                Snackbar snackbar = Snackbar.make(v, currentParticipantName + " Deleted", Snackbar.LENGTH_LONG);
                                                View sbView = snackbar.getView();
                                                sbView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                                                TextView tv = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                                tv.setTextColor(Color.WHITE);
                                                snackbar.show();

                                                listener.onParticipantDeleted();
                                            }
                                        });
                                        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        alertDialogBuilder.create();
                                        alertDialogBuilder.show();
                                    }
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.inflate(R.menu.menu_search_participant_item);
                    popupMenu.show();
                }
            });
        }
    }

    public ParticipantsAdapter(Context context, ArrayList<Participant> participants) {
        mContext = context;
        mParticipants = participants;
    }

    @Override
    public ParticipantsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.participant_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Participant currentParticipant = mParticipants.get(position);
        String participantName = currentParticipant.getFirstName() + " " + currentParticipant.getLastName();
        holder.mNameTextView.setText(participantName);
    }

    @Override
    public int getItemCount() {
        return mParticipants.size();
    }

    private void deleteParticipantById(int id) {
        Participant participant = new Participant();
        participant.deleteParticipant(mContext.getContentResolver(), id);
    }

    private void removeAt(int position) {
        mParticipants.remove(position);
        notifyItemChanged(position);
        notifyItemRangeChanged(position, mParticipants.size());
    }

    public void setParticipantData(ArrayList<Participant> participants) {
        mParticipants = participants;
        notifyDataSetChanged();
    }
}
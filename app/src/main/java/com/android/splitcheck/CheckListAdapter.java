package com.android.splitcheck;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.splitcheck.data.Check;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder> {
    static ArrayList<Check> mChecks;
    static Context mContext;
    private OnCheckItemEditClickedListener listener;

    public interface OnCheckItemEditClickedListener {
        void onCheckItemEditClickedListener(String checkName, int checkId);
    }

    public void setOnCheckItemEditClickedListener(OnCheckItemEditClickedListener l) {
        listener = l;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_view_check_item) CardView mCardView;
        @BindView(R.id.title_check_item) TextView mTitleTextView;
        @BindView(R.id.total_check_item) TextView mTotalTextView;
        @BindView(R.id.date_time_check_item) TextView mDateTimeTextView;
        @BindView(R.id.options_icon_check_item) ImageView mIconImageView;
        public LinearLayout mLinearLayout;
        public ViewHolder(LinearLayout v) {
            super(v);
            ButterKnife.bind(this, v);
            mLinearLayout = v;

            mIconImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext, v);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            final int currentCheckId = mChecks.get(getAdapterPosition()).getId();
                            final String currentCheckName = mChecks.get(getAdapterPosition()).getName();
                            switch (item.getItemId()) {
                                case R.id.check_item_edit:
                                    listener.onCheckItemEditClickedListener(currentCheckName,
                                            currentCheckId);
                                    return true;
                                case R.id.check_item_delete:
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                                    alertDialogBuilder.setTitle("Are you sure?");
                                    alertDialogBuilder.setMessage("Delete " + currentCheckName + " ?");
                                    alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteCheckById(currentCheckId);
                                            removeAt(getAdapterPosition());
                                            Snackbar snackbar = Snackbar.make(v, currentCheckName + " Deleted", Snackbar.LENGTH_LONG);
                                            View sbView = snackbar.getView();
                                            sbView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                                            TextView tv = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                            tv.setTextColor(Color.WHITE);
                                            snackbar.show();
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

                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.inflate(R.menu.menu_check_item);
                    popupMenu.show();
                }
            });
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentToStartEditCheckActivity = new Intent(mContext,
                            CheckDetailActivity.class);
                    intentToStartEditCheckActivity.putExtra("checkId",
                            mChecks.get(getAdapterPosition()).getId());
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(mContext, R.animator.anim_enter_left, R.animator.anim_exit_right);
                    mContext.startActivity(intentToStartEditCheckActivity, options.toBundle());
                }
            });
        }
    }

    public CheckListAdapter(Context context, ArrayList<Check> checks) {
        mContext = context;
        mChecks = checks;
    }

    @Override
    public CheckListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.check_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTitleTextView.setText(mChecks.get(position).getName());
        holder.mTotalTextView.setText(mChecks.get(position).getTotal());
        holder.mDateTimeTextView.setText(mChecks.get(position).getFormattedDate());
    }

    @Override
    public int getItemCount() {
        return mChecks.size();
    }

    public Uri deleteCheckById(int id) {
        Check check = new Check();
        return check.deleteFromDatabase(mContext.getContentResolver(), id);
    }

    public void removeAt(int position) {
        mChecks.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mChecks.size());
    }

    public void update(ArrayList<Check> checks) {
        mChecks.clear();
        mChecks.addAll(checks);
        notifyDataSetChanged();
    }
}

package com.android.splitcheck;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.android.splitcheck.data.Check;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder> {
    static ArrayList<Check> mChecks;
    static Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
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

            /** Implement Delete and Confirmation Dialog
            mCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Check check = new Check();
                    check.deleteFromDatabase(mContext.getContentResolver(),
                            mChecks.get(getAdapterPosition()).getId());
                    return false;
                }
            });**/
            mIconImageView.setColorFilter(R.color.colorBlack);
            mIconImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext, v);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.check_item_edit:
                                    Toast.makeText(mContext, "Editing", Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.check_item_delete:
                                    Toast.makeText(mContext, "Deleting", Toast.LENGTH_SHORT).show();
                                    return true;
                                default:
                                    Toast.makeText(mContext, "None", Toast.LENGTH_SHORT).show();
                                    return true;
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
                            EditCheckActivity.class);
                    intentToStartEditCheckActivity.putExtra("checkId",
                            mChecks.get(getAdapterPosition()).getId());
                    mContext.startActivity(intentToStartEditCheckActivity);
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
}

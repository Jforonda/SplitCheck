package com.android.splitcheck;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.splitcheck.data.Check;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder> {
    ArrayList<Check> mChecks;
    Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_view_check_item) CardView mCardView;
        @BindView(R.id.title_check_item) TextView mTitleTextView;
        @BindView(R.id.total_check_item) TextView mTotalTextView;
        @BindView(R.id.date_time_check_item) TextView mDateTimeTextView;
        public LinearLayout mLinearLayout;
        public ViewHolder(LinearLayout v) {
            super(v);
            ButterKnife.bind(this, v);
            mLinearLayout = v;
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

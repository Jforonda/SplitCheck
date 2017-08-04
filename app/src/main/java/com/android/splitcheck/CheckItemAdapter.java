package com.android.splitcheck;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.splitcheck.data.Item;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckItemAdapter extends RecyclerView.Adapter<CheckItemAdapter.ViewHolder> {
    static ArrayList<Item> mItems;
    static Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view_item_name) TextView mNameTextView;
        @BindView(R.id.text_view_item_cost) TextView mCostTextView;
        public LinearLayout mLinearLayout;
        public ViewHolder(LinearLayout v) {
            super(v);
            ButterKnife.bind(this, v);
            mLinearLayout = v;
        }
    }

    public CheckItemAdapter(Context context, ArrayList<Item> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public CheckItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.check_item_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mNameTextView.setText(mItems.get(position).getName());
        holder.mCostTextView.setText(mItems.get(position).getCost());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}

package com.android.splitcheck;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.splitcheck.data.Item;
import com.android.splitcheck.data.ItemParticipant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckItemAdapter extends RecyclerView.Adapter<CheckItemAdapter.ViewHolder> {
    static ArrayList<Item> mItems;
    static ArrayList<ItemParticipant> mItemParticipants;
    static Context mContext;
    private OnCheckItemClickedListener listener;
    private CheckItemParticipantAdapter mCheckItemParticipantAdapter;

    public interface OnCheckItemClickedListener {
        void onCheckItemClickedListener(int itemId, String itemName);
    }

    public void setOnCheckItemClickedListener(OnCheckItemClickedListener l) {
        listener = l;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view_item_name) TextView mNameTextView;
        @BindView(R.id.text_view_item_cost) TextView mCostTextView;
        @BindView(R.id.frame_layout_item_item) FrameLayout mFrameLayout;
        @BindView(R.id.list_view_item_participants) ListView mListView;
        public LinearLayout mLinearLayout;
        public ViewHolder(LinearLayout v) {
            super(v);
            ButterKnife.bind(this, v);
            mLinearLayout = v;

            mNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Item item = mItems.get(getAdapterPosition());
//                    listener.onCheckItemClickedListener(item.getId(),
//                            item.getName());
                }
            });
            mFrameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Item item = mItems.get(getAdapterPosition());
                    listener.onCheckItemClickedListener(item.getId(),
                            item.getName());
                }
            });
        }
    }

    public CheckItemAdapter(Context context, ArrayList<Item> items, ArrayList<ItemParticipant> itemParticipants) {
        mContext = context;
        mItems = items;
        mItemParticipants = itemParticipants;
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
        Item currentItem = mItems.get(position);
        holder.mNameTextView.setText(currentItem.getName() + currentItem.getId());
//        holder.mCostTextView.setText(String.valueOf(mItems.get(position).getCost()));
        holder.mCostTextView.setText(currentItem.getCostAsString());


        ArrayList<ItemParticipant> checkedParticipants = new ArrayList<>();
        for (int i = 0; i < mItemParticipants.size(); i++) {
            ItemParticipant currentItemParticipant = mItemParticipants.get(i);

            if (currentItemParticipant.getItemId() == currentItem.getId()) {
                if (currentItemParticipant.getIsChecked() == 1) {
                    checkedParticipants.add(currentItemParticipant);
                }
            }
        }
        mCheckItemParticipantAdapter = new CheckItemParticipantAdapter(mContext, checkedParticipants);
        holder.mListView.setAdapter(mCheckItemParticipantAdapter);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}

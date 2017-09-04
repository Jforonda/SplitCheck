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

import com.android.splitcheck.data.Item;
import com.android.splitcheck.data.ItemParticipant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class CheckItemAdapter extends RecyclerView.Adapter<CheckItemAdapter.ViewHolder> {
    static ArrayList<Item> mItems;
    static ArrayList<ItemParticipant> mItemParticipants;
    Context mContext;
    private OnCheckItemClickedListener listener;

    public interface OnCheckItemClickedListener {
        void onCheckItemClickedListener(int itemId, String itemName);
        void onCheckItemEditListener(int itemId, String itemName, int itemCost);
    }

    public void setOnCheckItemClickedListener(OnCheckItemClickedListener l) {
        listener = l;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view_item_name) TextView mNameTextView;
        @BindView(R.id.text_view_item_cost) TextView mCostTextView;
        @BindView(R.id.options_icon_check_item_item) ImageView mOptionsImageView;
        @BindView(R.id.linear_layout_item_item) LinearLayout mLinearLayout;
        @BindView(R.id.frame_layout_item_item) FrameLayout mFrameLayout;
        @BindView(R.id.text_view_item_split) TextView mSplitTextView;
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
                            final int currentItemId = mItems.get(getAdapterPosition()).getId();
                            final String currentItemName = mItems.get(getAdapterPosition()).getName();
                            int currentItemCost = mItems.get(getAdapterPosition()).getCost();
                            switch (item.getItemId()) {
                                case R.id.item_item_edit:
                                    listener.onCheckItemEditListener(currentItemId, currentItemName, currentItemCost);
                                    return true;
                                case R.id.item_item_delete:
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                                    alertDialogBuilder.setTitle(R.string.item_delete_confirmation_title);
                                    alertDialogBuilder.setMessage(mContext.getString(R.string.item_delete_name, currentItemName));
                                    alertDialogBuilder.setPositiveButton(R.string.item_delete, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            removeItemById(currentItemId);
                                            removeAt(getAdapterPosition());

                                            Snackbar snackbar = Snackbar.make(v, mContext.getString(R.string.item_deleted_name, currentItemName), Snackbar.LENGTH_LONG);
                                            View sbView = snackbar.getView();
                                            sbView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                                            TextView tv = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                            tv.setTextColor(Color.WHITE);
                                            snackbar.show();
                                        }
                                    });
                                    alertDialogBuilder.setNegativeButton(R.string.item_delete_cancel, new DialogInterface.OnClickListener() {
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
                    popupMenu.inflate(R.menu.menu_item_item);
                    popupMenu.show();
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
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Item currentItem = mItems.get(position);
        holder.mNameTextView.setText(currentItem.getName());
        holder.mCostTextView.setText(currentItem.getCostAsString());
        holder.mOptionsImageView.setColorFilter(R.color.colorBlack);


        ArrayList<ItemParticipant> checkedParticipants = new ArrayList<>();
        for (int i = 0; i < mItemParticipants.size(); i++) {
            ItemParticipant currentItemParticipant = mItemParticipants.get(i);
            if (currentItemParticipant.getItemId() == currentItem.getId()) {
                if (currentItemParticipant.getIsChecked() == 1) {
                    checkedParticipants.add(currentItemParticipant);
                }
            }
        }
        if (checkedParticipants.size() == 1) {
            holder.mSplitTextView.setText(checkedParticipants.get(0).getParticipantName());
        } else if (checkedParticipants.size() > 1) {
            String split = mContext.getString(R.string.item_split_amount, String.valueOf(checkedParticipants.size()));
            holder.mSplitTextView.setText(split);
        } else {
            holder.mSplitTextView.setVisibility(GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private Uri removeItemById(int id) {
        Item item = new Item();
        return item.deleteFromDb(mContext.getContentResolver(), id);
    }

    private void removeAt(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mItems.size());
    }

}

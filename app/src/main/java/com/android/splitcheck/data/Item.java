package com.android.splitcheck.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

public class Item implements Parcelable {

    private String name;
    private int cost;
    private int id;
    private int checkId;
    private ArrayList<Integer> participantIds;


    public Item() {

    }

    public Item (String name, int cost, int id, int checkId) {
        this.name = name;
        this.cost = cost;
        this.id = id;
        this.checkId = checkId;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }

    // Parcelable methods


    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeInt(cost);
        out.writeInt(id);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    private Item(Parcel in) {
        this.name = in.readString();
        this.cost = in.readInt();
        this.id = in.readInt();
    }

    // Database Handlers

    public Uri addToDatabase(ContentResolver contentResolver, String name, int cost, int checkId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemContract.ItemEntry.NAME,
                name);
        contentValues.put(ItemContract.ItemEntry.COST,
                cost);
        contentValues.put(ItemContract.ItemEntry.CHECK_ID,
                checkId);
        Uri uri = contentResolver.insert(ItemContract.ItemEntry.CONTENT_URI,
                contentValues);
        return uri;

    }

    public Uri deleteFromDb(ContentResolver contentResolver, int itemId) {
        Uri itemUri = ItemContract.ItemEntry.CONTENT_URI;
        contentResolver.delete(itemUri, ItemContract.ItemEntry._ID + " = " + String.valueOf(itemId),
                null);

        Uri itemParticipantUri = ItemParticipantContract.ItemParticipantEntry.CONTENT_URI;
        contentResolver.delete(itemParticipantUri, ItemParticipantContract.ItemParticipantEntry
                .ITEM_ID + " = " + String.valueOf(itemId), null);

        return itemUri;
    }

    public void deleteAllFromDb(ContentResolver contentResolver) {

    }

    public int updateItem(ContentResolver contentResolver, int id, String name, int cost) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemContract.ItemEntry.NAME,
                name);
        contentValues.put(ItemContract.ItemEntry.COST,
                cost);
        return contentResolver.update(ItemContract.ItemEntry.CONTENT_URI, contentValues,
                ItemContract.ItemEntry._ID + " = " + id, null);
    }

    public ArrayList<Item> getListOfItemsFromDatabaseFromCheckId(ContentResolver contentResolver,
                                                      int checkId) {
        Uri uri = ItemContract.ItemEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(checkId)).build();
        Cursor c = contentResolver.query(uri, null, null, null, null);
        ArrayList<Item> items = new ArrayList<>();
        if (c != null) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String name = c.getString(c.getColumnIndex(ItemContract.ItemEntry.NAME));
                int id = c.getInt(c.getColumnIndex(ItemContract.ItemEntry._ID));
                int cost = c.getInt(c.getColumnIndex(ItemContract.ItemEntry.COST));
                int tempCheckId = c.getInt(c.getColumnIndex(ItemContract.ItemEntry.CHECK_ID));
                Item item = new Item(name, cost, id, tempCheckId);
                items.add(item);
                c.moveToNext();
            }
        }
        return items;
    }

    public String getCostAsString() {
        String stringCost;
        stringCost = String.valueOf(cost);
        BigDecimal parsed = new BigDecimal(stringCost).setScale(2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
        return NumberFormat.getCurrencyInstance().format(parsed);
    }

    public String getTotalAsStringFromCheckId(ContentResolver contentResolver, int checkId) {
        ArrayList<Item> items = getListOfItemsFromDatabaseFromCheckId(contentResolver, checkId);
        int total = 0;
        String stringTotal;

        for (int i = 0; i < items.size(); i++) {
            total += items.get(i).getCost();
        }
        stringTotal = String.valueOf(total);
        BigDecimal parsed = new BigDecimal(stringTotal).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        return "Total: " + NumberFormat.getCurrencyInstance().format(parsed);
    }

    public String getSubtotal(ContentResolver contentResolver, int checkId) {
        String stringSubtotal = String.valueOf(getSubtotalAmount(contentResolver, checkId));
        BigDecimal parsed = new BigDecimal(stringSubtotal).setScale(2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
        return "Subtotal: " + NumberFormat.getCurrencyInstance().format(parsed);
    }

    public String getTotal(ContentResolver contentResolver, int checkId) {
        Modifier modifier = new Modifier(contentResolver, checkId);
        int subTotalAmount = getSubtotalAmount(contentResolver, checkId);
        /**int totalAmount = 0, totalPercentage = 0;

        if (modifier.getTaxPercent()) {
            totalPercentage += modifier.getTax();
        } else {
            totalAmount += modifier.getTax();
        }
        if (modifier.getTipPercent()) {
            totalPercentage += modifier.getTip();
        } else {
            totalAmount += modifier.getTip();
        }
        if (modifier.getGratuityPercent()) {
            totalPercentage += modifier.getGratuity();
        } else {
            totalAmount += modifier.getGratuity();
        }
        if (modifier.getFeesPercent()) {
            totalPercentage += modifier.getFees();
        } else {
            totalAmount += modifier.getFees();
        }
        if (modifier.getDiscountPercent()) {
            if (modifier.getDiscount() >= 100) {
                totalPercentage -= subTotalAmount;
            } else {
                totalPercentage -= modifier.getDiscount();
            }
        } else {
            if (modifier.getDiscount() >= subTotalAmount) {
                totalAmount -= subTotalAmount;
            } else {
                totalAmount -= modifier.getDiscount();
            }
        }

        BigDecimal bigDecimalPercentage = new BigDecimal(subTotalAmount * totalPercentage).divide(new BigDecimal(10000), BigDecimal.ROUND_HALF_UP);
        BigDecimal bigDecimalAmount = new BigDecimal(totalAmount).add(bigDecimalPercentage);
        BigDecimal bigDecimalTotal = new BigDecimal(subTotalAmount).add(bigDecimalAmount).setScale(2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
        return "Total: " + NumberFormat.getCurrencyInstance().format(bigDecimalTotal);**/
        BigDecimal total = applyModifiers(contentResolver, checkId, subTotalAmount);
        return "Total: " + NumberFormat.getCurrencyInstance().format(total);
    }

    public BigDecimal applyModifiers(ContentResolver contentResolver, int checkId, int subTotal) {
        Modifier modifier = new Modifier(contentResolver, checkId);
        int totalAmount = 0, totalPercentage = 0;
        if (modifier.getTaxPercent()) {
            totalPercentage += modifier.getTax();
        } else {
            totalAmount += modifier.getTax();
        }
        if (modifier.getTipPercent()) {
            totalPercentage += modifier.getTip();
        } else {
            totalAmount += modifier.getTip();
        }
        if (modifier.getGratuityPercent()) {
            totalPercentage += modifier.getGratuity();
        } else {
            totalAmount += modifier.getGratuity();
        }
        if (modifier.getFeesPercent()) {
            totalPercentage += modifier.getFees();
        } else {
            totalAmount += modifier.getFees();
        }
        if (modifier.getDiscountPercent()) {
            if (modifier.getDiscount() >= 100) {
                totalPercentage -= subTotal;
            } else {
                totalPercentage -= modifier.getDiscount();
            }
        } else {
            if (modifier.getDiscount() >= subTotal) {
                totalAmount -= subTotal;
            } else {
                totalAmount -= modifier.getDiscount();
            }
        }
        BigDecimal bigDecimalPercentage = new BigDecimal(subTotal * totalPercentage).divide(new BigDecimal(10000), BigDecimal.ROUND_HALF_UP);
        BigDecimal bigDecimalAmount = new BigDecimal(totalAmount).add(bigDecimalPercentage);
        BigDecimal bigDecimalTotal = new BigDecimal(subTotal).add(bigDecimalAmount).setScale(2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);

        return bigDecimalTotal;
    }

    public int getSubtotalAmount(ContentResolver contentResolver, int checkId) {
        ArrayList<Item> items = getListOfItemsFromDatabaseFromCheckId(contentResolver, checkId);
        int total = 0;

        for (int i = 0; i < items.size(); i++) {
            total += items.get(i).getCost();
        }
        return total;
    }

    public int getSplitAmountPerItem(ContentResolver contentResolver, int itemId) {
        Uri itemUri = ItemContract.ItemEntry.CONTENT_URI;
        Cursor itemCursor = contentResolver.query(itemUri, null, "_id=" + String.valueOf(itemId), null, null);
        itemCursor.moveToFirst();
        String currentName = itemCursor.getString(itemCursor.getColumnIndex(ItemContract.ItemEntry.NAME));
        int currentId = itemCursor.getInt(itemCursor.getColumnIndex(ItemContract.ItemEntry._ID));
        int currentCost = itemCursor.getInt(itemCursor.getColumnIndex(ItemContract.ItemEntry.COST));
        int currentCheckId = itemCursor.getInt(itemCursor.getColumnIndex(ItemContract.ItemEntry.CHECK_ID));
        Item item = new Item(currentName, currentCost, currentId, currentCheckId);

        Uri itemParticipantUri = ItemParticipantContract.ItemParticipantEntry.CONTENT_URI;
        Cursor itemParticipantCursor = contentResolver.query(itemParticipantUri, null, "item_id=" + String.valueOf(itemId), null, null);
        ArrayList<ItemParticipant> itemParticipants = new ArrayList<>();
        itemParticipantCursor.moveToFirst();
        while (!itemParticipantCursor.isAfterLast()) {
            int id = itemParticipantCursor.getInt(itemParticipantCursor.getColumnIndex(ItemParticipantContract.ItemParticipantEntry._ID));
            int participantId = itemParticipantCursor.getInt(itemParticipantCursor.getColumnIndex(ItemParticipantContract.ItemParticipantEntry.PARTICIPANT_ID));
            String participantName = itemParticipantCursor.getString(itemParticipantCursor.getColumnIndex(ItemParticipantContract.ItemParticipantEntry.PARTICIPANT_NAME));
            int isChecked = itemParticipantCursor.getInt(itemParticipantCursor.getColumnIndex(ItemParticipantContract.ItemParticipantEntry.IS_CHECKED));
            if (isChecked == 1) {
                ItemParticipant itemParticipant = new ItemParticipant(id, currentCheckId, itemId, participantId, participantName, isChecked);
                itemParticipants.add(itemParticipant);
            }
            itemParticipantCursor.moveToNext();
        }
        if (itemParticipants.size() > 0) {
            return item.getCost() / itemParticipants.size();
        } else {
            return 0;
        }
    }

}

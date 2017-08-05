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

    public void deleteFromDatabase(ContentResolver contentResolver, int checkId) {

    }

    public void deleteAllFromDatabase(ContentResolver contentResolver) {

    }

    public void updateDatabase() {

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
        BigDecimal parsed = new BigDecimal(stringCost).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        String formatted = NumberFormat.getCurrencyInstance().format(parsed);
        return formatted;
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
        String formatted = "Total: " + NumberFormat.getCurrencyInstance().format(parsed);
        return formatted;
    }

}

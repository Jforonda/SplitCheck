package com.android.splitcheck.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class Modifier {

    private int tax;
    private boolean taxPercent;
    private int tip;
    private boolean tipPercent;
    private int gratuity;
    private boolean gratuityPercent;
    private int fees;
    private boolean feesPercent;
    private int discount;
    private boolean discountPercent;
    private int checkId;

    public Modifier() {
        // Default
        // If percent is false, then its a set amount
        tax = 0;
        taxPercent = true;
        tip = 0;
        tipPercent = true;
        gratuity = 0;
        gratuityPercent = true;
        fees = 0;
        feesPercent = true;
        discount = 0;
        discountPercent = true;
    }

    public Modifier(int tax, boolean taxPercent, int tip, boolean tipPercent, int gratuity,
                    boolean gratuityPercent, int fees,  boolean feesPercent, int discount,
                    boolean discountPercent, int checkId) {
        this.tax = tax;
        this.taxPercent = taxPercent;
        this.tip = tip;
        this.tipPercent = tipPercent;
        this.gratuity = gratuity;
        this.gratuityPercent = gratuityPercent;
        this.fees = fees;
        this.feesPercent = feesPercent;
        this.discount = discount;
        this.discountPercent = discountPercent;
        this.checkId = checkId;
    }

    public Modifier(ContentResolver contentResolver, int checkId) {
        Uri uri = ModifierContract.ModifierEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(checkId)).build();
        Cursor c = contentResolver.query(uri, null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            this.tax = c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.TAX));
            this.taxPercent = (c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.TAX_PERCENT)) == 1);
            this.tip = c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.TIP));
            this.tipPercent = (c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.TIP_PERCENT)) == 1);
            this.gratuity = c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.GRATUITY));
            this.gratuityPercent = (c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.GRATUITY_PERCENT)) == 1);
            this.fees = c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.FEES));
            this.feesPercent = (c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.FEES_PERCENT)) == 1);
            this.discount = c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.DISCOUNT));
            this.discountPercent = (c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.DISCOUNT_PERCENT)) == 1);
            this.checkId = c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.CHECK_ID));
            c.close();
        }
    }

    // Getters and Setters

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public boolean getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(boolean taxPercent) {
        this.taxPercent = taxPercent;
    }

    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        this.tip = tip;
    }

    public boolean getTipPercent() {
        return tipPercent;
    }

    public void setTipPercent(boolean tipPercent) {
        this.tipPercent = tipPercent;
    }

    public int getGratuity() {
        return gratuity;
    }

    public void setGratuity(int gratuity) {
        this.gratuity = gratuity;
    }

    public boolean getGratuityPercent() {
        return gratuityPercent;
    }

    public void setGratuityPercent(boolean gratuityPercent) {
        this.gratuityPercent = gratuityPercent;
    }

    public int getFees() {
        return fees;
    }

    public void setFees(int fees) {
        this.fees = fees;
    }

    public boolean getFeesPercent() {
        return feesPercent;
    }

    public void setFeesPercent(boolean feesPercent) {
        this.feesPercent = feesPercent;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public boolean getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(boolean discountPercent) {
        this.discountPercent = discountPercent;
    }

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }

    // Database Handlers

    public Uri addDefaultToDatabase(ContentResolver contentResolver, int checkId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ModifierContract.ModifierEntry.TAX,
                0);
        contentValues.put(ModifierContract.ModifierEntry.TAX_PERCENT,
                1);
        contentValues.put(ModifierContract.ModifierEntry.TIP,
                0);
        contentValues.put(ModifierContract.ModifierEntry.TIP_PERCENT,
                1);
        contentValues.put(ModifierContract.ModifierEntry.GRATUITY,
                0);
        contentValues.put(ModifierContract.ModifierEntry.GRATUITY_PERCENT,
                1);
        contentValues.put(ModifierContract.ModifierEntry.FEES,
                0);
        contentValues.put(ModifierContract.ModifierEntry.FEES_PERCENT,
                1);
        contentValues.put(ModifierContract.ModifierEntry.DISCOUNT,
                0);
        contentValues.put(ModifierContract.ModifierEntry.DISCOUNT_PERCENT,
                1);
        contentValues.put(ModifierContract.ModifierEntry.CHECK_ID,
                checkId);
        Uri uri = contentResolver.insert(ModifierContract.ModifierEntry.CONTENT_URI,
                contentValues);
        return uri;
    }

    public int updateTax(ContentResolver contentResolver, int checkId, int tax) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ModifierContract.ModifierEntry.TAX, tax);
        return contentResolver.update(ModifierContract.ModifierEntry.CONTENT_URI, contentValues,
                "check_id=" + checkId, null);
    }

    public int updateTaxPercent(ContentResolver contentResolver, int checkId, boolean taxPercent) {
        ContentValues contentValues = new ContentValues();
        int taxPercentValue = taxPercent ? 1 : 0;
        contentValues.put(ModifierContract.ModifierEntry.TAX_PERCENT, taxPercentValue);
        return contentResolver.update(ModifierContract.ModifierEntry.CONTENT_URI, contentValues,
                "check_id=" + checkId, null);
    }

    public int updateTip(ContentResolver contentResolver, int checkId, int tip) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ModifierContract.ModifierEntry.TIP, tip);
        return contentResolver.update(ModifierContract.ModifierEntry.CONTENT_URI, contentValues,
                "check_id=" + checkId, null);
    }

    public int updateTipPercent(ContentResolver contentResolver, int checkId, boolean tipPercent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ModifierContract.ModifierEntry.TIP_PERCENT, tipPercent);
        return contentResolver.update(ModifierContract.ModifierEntry.CONTENT_URI, contentValues,
                "check_id=" + checkId, null);
    }

    public int updateGratuity(ContentResolver contentResolver, int checkId, int gratuity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ModifierContract.ModifierEntry.GRATUITY, gratuity);
        return contentResolver.update(ModifierContract.ModifierEntry.CONTENT_URI, contentValues,
                "check_id=" + checkId, null);
    }

    public int updateGratuityPercent(ContentResolver contentResolver, int checkId, boolean gratuityPercent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ModifierContract.ModifierEntry.GRATUITY_PERCENT, gratuityPercent);
        return contentResolver.update(ModifierContract.ModifierEntry.CONTENT_URI, contentValues,
                "check_id=" + checkId, null);
    }

    public int updateFees(ContentResolver contentResolver, int checkId, int fees) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ModifierContract.ModifierEntry.FEES, fees);
        return contentResolver.update(ModifierContract.ModifierEntry.CONTENT_URI, contentValues,
                "check_id=" + checkId, null);
    }

    public int updateFeesPercent(ContentResolver contentResolver, int checkId, boolean feesPercent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ModifierContract.ModifierEntry.FEES_PERCENT, feesPercent);
        return contentResolver.update(ModifierContract.ModifierEntry.CONTENT_URI, contentValues,
                "check_id=" + checkId, null);
    }

    public int updateDiscount(ContentResolver contentResolver, int checkId, int discount) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ModifierContract.ModifierEntry.DISCOUNT, discount);
        return contentResolver.update(ModifierContract.ModifierEntry.CONTENT_URI, contentValues,
                "check_id=" + checkId, null);
    }

    public int updateDiscountPercent(ContentResolver contentResolver, int checkId, boolean discountPercent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ModifierContract.ModifierEntry.DISCOUNT_PERCENT, discountPercent);
        return contentResolver.update(ModifierContract.ModifierEntry.CONTENT_URI, contentValues,
                "check_id=" + checkId, null);
    }

    public Modifier getModifierFromId(ContentResolver contentResolver, int checkId) {
        Uri uri = ModifierContract.ModifierEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(checkId)).build();
        Cursor c = contentResolver.query(uri, null, null, null, null);
        c.moveToFirst();
        int sTax = c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.TAX));
        boolean sTaxPercent = (c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.TAX_PERCENT)) == 1);
        int sTip = c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.TIP));
        boolean sTipPercent = (c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.TIP_PERCENT)) == 1);
        int sGratuity = c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.GRATUITY));
        boolean sGratuityPercent = (c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.GRATUITY_PERCENT)) == 1);
        int sFees = c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.FEES));
        boolean sFeesPercent = (c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.FEES_PERCENT)) == 1);
        int sDiscount = c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.DISCOUNT));
        boolean sDiscountPercent = (c.getInt(c.getColumnIndex(ModifierContract.ModifierEntry.DISCOUNT_PERCENT)) == 1);
        Modifier modifier = new Modifier(sTax, sTaxPercent, sTip, sTipPercent, sGratuity,
                sGratuityPercent, sFees, sFeesPercent, sDiscount, sDiscountPercent, checkId);
        c.close();
        return modifier;
    }

    public String getTaxString() {
        BigDecimal parsed = new BigDecimal(String.valueOf(tax)).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        return NumberFormat.getCurrencyInstance().format(parsed);
    }

    public String getTipString() {
        BigDecimal parsed = new BigDecimal(String.valueOf(tip)).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        return NumberFormat.getCurrencyInstance().format(parsed);
    }

    public String getGratuityString(){
        BigDecimal parsed = new BigDecimal(String.valueOf(gratuity)).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        return NumberFormat.getCurrencyInstance().format(parsed);
    }

    public String getFeesString() {
        BigDecimal parsed = new BigDecimal(String.valueOf(fees)).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        return NumberFormat.getCurrencyInstance().format(parsed);
    }

    public String getDiscountString() {
        BigDecimal parsed = new BigDecimal(String.valueOf(discount)).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        return NumberFormat.getCurrencyInstance().format(parsed);
    }

}

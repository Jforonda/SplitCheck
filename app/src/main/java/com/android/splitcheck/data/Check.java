package com.android.splitcheck.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Date;

public class Check {

    private String title;
    private ArrayList<Participant> participants;
    private ArrayList<Item> items;
    private int id;

    private int total;
    private long timeCreated;
    private Date dateTimeCreated;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // outputFormat: 8/11/2017 12:11 AM
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);



    public Check() {
        this.timeCreated = getTimeCreated();
        this.dateTimeCreated = getCurrentDateTime();
    }

    public Check(String title, ArrayList<Participant> participants, ArrayList<Item> items, int id) {
        this.title = title;
        this.participants = participants;
        this.items = items;
        this.id = id;
        this.timeCreated = getTimeCreated();
        this.dateTimeCreated = getCurrentDateTime();
    }

    // Methods for Date and Time

    private long getTimeCreated() {
        return System.currentTimeMillis();
    }

    private Date getCurrentDateTime() {
        Date currentDateTime = new Date(timeCreated);
        return currentDateTime;
    }

    private String formatDate(Date date) {
        return outputFormat.format(date);
    }

    public String getFormattedDate() {
        return formatDate(dateTimeCreated);
    }

    // Methods for Check Total

    private int getTotal() {
        int total = 0;
        for (int i = 0; i < items.size(); i++) {
            total += items.get(i).getCost();
        }

        return total;
    }

    // Getters and Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }
}

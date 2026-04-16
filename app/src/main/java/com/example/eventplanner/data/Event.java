package com.example.eventplanner.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "events")
public class Event {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String category;
    private String location;
    private long dateTime;

    public Event(String title, String category, String location, long dateTime) {
        this.title = title;
        this.category = category;
        this.location = location;
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
}
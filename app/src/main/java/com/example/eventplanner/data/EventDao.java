package com.example.eventplanner.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDao {

    @Insert
    void insertEvent(Event event);

    @Update
    void updateEvent(Event event);

    @Delete
    void deleteEvent(Event event);

    @Query("SELECT * FROM events WHERE dateTime >= :currentTime ORDER BY dateTime ASC")
    List<Event> getUpcomingEvents(long currentTime);

    @Query("SELECT * FROM events WHERE id = :eventId LIMIT 1")
    Event getEventById(int eventId);
}
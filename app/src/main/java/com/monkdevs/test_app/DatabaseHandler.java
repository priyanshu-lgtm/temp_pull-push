package com.monkdevs.test_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "eventManager";
    private static final String TABLE_EVENTS = "events";
    private static final String KEY_EVENT_ID = "event_id";
    private static final String KEY_EVENT_NAME = "event_name";
    private static final String KEY_EVENT_TIME = "event_time";
    private static final String KEY_EVENT_INTERVAL = "event_interval";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + KEY_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_EVENT_NAME + " TEXT,"
                + KEY_EVENT_TIME + " LONG,"
                + KEY_EVENT_INTERVAL + " INTEGER" + ")";
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new event
    void addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_NAME, event.event_name); // Event Name
        values.put(KEY_EVENT_TIME, event.event_timestamp);
        values.put(KEY_EVENT_INTERVAL,event.event_interval);

        // Inserting Row
        db.insert(TABLE_EVENTS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single event
    Event getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EVENTS, new String[] {KEY_EVENT_ID,
                        KEY_EVENT_NAME, KEY_EVENT_TIME, KEY_EVENT_INTERVAL}, KEY_EVENT_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, KEY_EVENT_TIME, null);
        if (cursor != null)
            cursor.moveToFirst();

        if(!cursor.moveToFirst()){
            return null;
        }



        Event event = new Event(cursor.getInt(0),
                cursor.getString(1), cursor.getLong(2), cursor.getInt(3));
        // return event
        return event;
    }

    // code to get all events in a list view
    public ArrayList<Event> getAllEvents() {
        ArrayList<Event> eventList = new ArrayList<Event>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.event_id=cursor.getInt(0);
                event.event_name=cursor.getString(1);
                event.event_timestamp=cursor.getLong(2);
                event.event_interval =cursor.getInt(3);

                // Adding event to list
                eventList.add(event);
            } while (cursor.moveToNext());
        }

        // return event list
        return eventList;
    }

    // code to update the single event
    public int updateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_NAME, event.event_name);
        values.put(KEY_EVENT_TIME, event.event_timestamp);
        values.put(KEY_EVENT_INTERVAL, event.event_interval);
        // updating row
        return db.update(TABLE_EVENTS, values, KEY_EVENT_ID + " = ?",
                new String[] { String.valueOf(event.event_id) });
    }

    // Deleting single event
    public void deleteEvent(Event event) {
        Log.e("DELETE","EXECUTED on "+event.event_id);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, KEY_EVENT_ID + " = ?",
                new String[] { String.valueOf(event.event_id) });
        db.close();
    }

    // Getting Eveents Count
    public int getEventsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_EVENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public int getRecentEventID() {
        String countQuery = "SELECT max(event_id) FROM "+ TABLE_EVENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor != null)
            cursor.moveToFirst();
        int c=cursor.getInt(0);
        cursor.close();

        // return count
        return c;
    }

}
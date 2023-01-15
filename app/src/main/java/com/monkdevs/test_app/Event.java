package com.monkdevs.test_app;

import java.util.HashMap;

public class Event {

    public Event(int event_id,String event_name,long event_timestamp,int event_interval){
        this.event_id=event_id;
        this.event_name=event_name;
        this.event_timestamp=event_timestamp;
        this.event_interval = event_interval;
    }

    public Event(String event_name,long event_timestamp,int event_interval){
        this.event_name=event_name;
        this.event_timestamp=event_timestamp;
        this.event_interval = event_interval;
    }

    public int event_id=0;
    public String event_name="";
    public long event_timestamp=0;
    public int event_interval =0;


    @Override
    public String toString() {
        return "";
    }

    Event(){

    }
}

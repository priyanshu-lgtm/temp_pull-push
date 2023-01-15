package com.monkdevs.test_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;

public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent_1) {
        Toast.makeText(context, "Test_App rebooted", Toast.LENGTH_LONG).show();
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        ArrayList<Event> events = databaseHandler.getAllEvents();
        for(Event e:events){
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("event_id", e.event_id);
            AlarmReceiver.add_next_event(context,intent,e);

        }
    }
}

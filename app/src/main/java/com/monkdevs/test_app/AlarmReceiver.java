package com.monkdevs.test_app;

import static android.content.Context.ALARM_SERVICE;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "this.is.my.channelId";//you can add any id you want

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.e("Alarm Receiver","Working");

        int event_id = intent.getIntExtra("event_id", -1);

        Log.e("Event Triggered",""+event_id);
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        if(databaseHandler.getEvent(event_id)==null){
            Log.e("Event Triggered",""+event_id);
            return;
        }
        Event event = databaseHandler.getEvent(event_id);
        String event_name = event.event_name;
        long event_timestamp = event.event_timestamp;
        int event_interval = event.event_interval;


        add_next_event(context, intent,event);

        Intent notificationIntent = new Intent(context, MainActivity.class);//on tap this activity will open

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);


        PendingIntent pendingIntent = stackBuilder.getPendingIntent(event_id, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);//getting the pendingIntent

        Notification.Builder builder = new Notification.Builder(context);//building the notification


        Notification notification = builder.setContentTitle("Reminder for your Event")
                .setContentText(event_name)
                .setSmallIcon(R.drawable.clock_clipart)
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker("New Message Alert!")
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent).build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//below creating notification channel, because of androids latest update, O is Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NotificationDemo",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(event_id, notification);
    }

    public static void add_next_event(Context context, Intent old_intent,Event event) {

        Log.e("AlarmReceiver","add_next_event");
        String[] radioList = new String[]{"Once", "Minute", "Hour", "Day","Week", "Month", "Year"};

        long event_timestamp = event.event_timestamp;
        int event_id = event.event_id;
        int event_interval = event.event_interval;

        Calendar calendarToday = Calendar.getInstance();
        Calendar calendarEvent = Calendar.getInstance();
        calendarEvent.setTimeInMillis(event_timestamp);

        if (calendarToday.getTimeInMillis() > calendarEvent.getTimeInMillis()) {
            switch (event_interval) {
                case 0://once
                    return;
                case 1://minute
                    calendarToday.set(Calendar.SECOND, 0);
                    calendarToday.set(Calendar.MILLISECOND, 0);
                    calendarToday.add(Calendar.MINUTE,1);
                    break;
                case 2://hour
                    calendarToday.set(Calendar.SECOND, 0);
                    calendarToday.set(Calendar.MILLISECOND, 0);
                    calendarToday.set(Calendar.MINUTE,calendarEvent.get(Calendar.MINUTE));
                    calendarToday.add(Calendar.HOUR,1);
                    break;
                case 3://day
                    calendarToday.set(Calendar.SECOND, 0);
                    calendarToday.set(Calendar.MILLISECOND, 0);
                    calendarToday.set(Calendar.MINUTE,calendarEvent.get(Calendar.MINUTE));
                    calendarToday.set(Calendar.HOUR,calendarEvent.get(Calendar.HOUR));
                    calendarToday.add(Calendar.DAY_OF_MONTH,1);
                    break;
                case 4://week
                    calendarToday.set(Calendar.MILLISECOND,0);
                    calendarToday.set(Calendar.SECOND,0);
                    calendarToday.set(Calendar.MINUTE,calendarEvent.get(Calendar.MINUTE));
                    calendarToday.set(Calendar.HOUR,calendarEvent.get(Calendar.HOUR));

                    int DAY_OF_WEEK=calendarEvent.get(Calendar.DAY_OF_WEEK);
                    calendarToday.add(Calendar.DAY_OF_MONTH,1);

                    while (calendarToday.get(Calendar.DAY_OF_WEEK)!=DAY_OF_WEEK){
                        calendarToday.add(Calendar.DAY_OF_YEAR,1);
                    }
                    break;
                case 5://Monthly
                    calendarToday.set(Calendar.SECOND, 0);
                    calendarToday.set(Calendar.MILLISECOND, 0);
                    calendarToday.set(Calendar.MINUTE,calendarEvent.get(Calendar.MINUTE));
                    calendarToday.set(Calendar.HOUR,calendarEvent.get(Calendar.HOUR));
                    calendarToday.set(Calendar.DATE,calendarEvent.get(Calendar.DATE));
                    calendarToday.add(Calendar.MONTH,1);
                    break;
                case 6:
                    calendarToday.set(Calendar.SECOND, 0);
                    calendarToday.set(Calendar.MILLISECOND, 0);
                    calendarToday.set(Calendar.MINUTE,calendarEvent.get(Calendar.MINUTE));
                    calendarToday.set(Calendar.HOUR,calendarEvent.get(Calendar.HOUR));
                    calendarToday.set(Calendar.DATE,calendarEvent.get(Calendar.DATE));
                    calendarToday.set(Calendar.MONTH,calendarEvent.get(Calendar.MONTH));
                    calendarToday.add(Calendar.YEAR,1);
                    break;
            }
        } else {
            calendarToday.setTimeInMillis(event_timestamp);
        }


        //Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, event_id, old_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarToday.getTimeInMillis(), pendingIntent);
        }
        Log.e("AlarmReceiver","Added next event");
    }

}

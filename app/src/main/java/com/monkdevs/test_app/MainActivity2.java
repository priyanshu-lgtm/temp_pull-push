package com.monkdevs.test_app;

import  androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity2 extends AppCompatActivity {

    EditText et_date;
    EditText et_name;
    RadioGroup radioGroup;
    Event event = new Event();
    int event_id;
    FloatingActionButton done, discard;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        done = findViewById(R.id.add_to_event);
        discard = findViewById(R.id.delete_event);
        et_name = findViewById(R.id.event_name_et);
        et_date = findViewById(R.id.event_date_view);
        radioGroup = findViewById(R.id.interval_radio_group);

        //Restoring old event if exist
        event_id = getIntent().getIntExtra("event_id", -1);
        Log.e("MainActivity2","Opened with event_id "+event_id);
        if (event_id == -1) {
            //do nothing
        } else {
            DatabaseHandler databaseHandler = new DatabaseHandler(this);
            event = databaseHandler.getEvent(event_id);
            databaseHandler.close();
            et_name.setText(event.event_name);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            calendar.setTimeInMillis(event.event_timestamp);
            String date_time=dateFormat.format(calendar.getTime());
            et_date.setText(date_time);
        }

        populateRadioGroup(radioGroup);


        et_date.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    timePick(i, i1);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                    String date_time=dateFormat.format(calendar.getTime());
                    et_date.setText(date_time);
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
            DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext());
            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    datePick(i, i1, i2);
                    timePickerDialog.show();
                }
            });
            datePickerDialog.show();
        });

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.test_array, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        done.setOnClickListener(view -> {
            if(et_name.getText().toString().length()<1){
                Toast.makeText(this, "Please enter an Event Name!", Toast.LENGTH_SHORT).show();
            }else {
                onDone();
                finish();
            }
        });

        discard.setOnClickListener(view -> {
            deleteEvents();
        });

    }

    public void deleteEvents() {
        if (event_id == -1)
            finish();
        else {
            DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
            databaseHandler.deleteEvent(event);
            databaseHandler.close();
/*
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            intent.putExtra("event_id", event.event_id);
            intent.putExtra("event_name", event.event_name);
            intent.putExtra("event_timestamp", event.event_timestamp);
            intent.putExtra("event_interval", event.event_interval);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), event_id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
          */
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onDone() {

        //Creating and Updating event if already exists
        DatabaseHandler databaseHandler = new DatabaseHandler(MainActivity2.this);
        event = new Event(event_id,et_name.getText().toString(), calendar.getTimeInMillis(), radioGroup.getCheckedRadioButtonId());
        if (event_id == -1) {
            databaseHandler.addEvent(event);
            event_id = databaseHandler.getRecentEventID();
        }
        else
            databaseHandler.updateEvent(event);
        event=databaseHandler.getEvent(event_id);
        databaseHandler.close();


        setAlarm(event);
        finish();
    }

    public void setAlarm(Event event){
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("event_id", event.event_id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), event.event_id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    Calendar calendar = Calendar.getInstance();
    int i, i1, i2;

    public void timePick(int i3, int i4) {
        calendar.set(i, i1, i2, i3, i4, 0);
        calendar.set(Calendar.MILLISECOND,0);
    }

    public void datePick(int i, int i1, int i2) {
        this.i = i;
        this.i1 = i1;
        this.i2 = i2;
    }


    //inflating radiolist with options
    public void populateRadioGroup(RadioGroup radioGroup) {
        String[] radioList = new String[]{"Once", "Minute", "Hour", "Day", "Week", "Month", "Year"};
        for (int i = 0; i < radioList.length; i++) {
            RadioButton radioButton = new RadioButton(MainActivity2.this);
            radioButton.setId(i);
            if (event.event_interval == i)
                radioButton.setChecked(true);
            radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
            radioButton.setText(radioList[i]);
            radioGroup.addView(radioButton);
        }
    }

}
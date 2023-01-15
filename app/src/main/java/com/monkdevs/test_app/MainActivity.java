package com.monkdevs.test_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    EventAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        ArrayList<Event> arrayList= databaseHandler.getAllEvents();

        for(Event e:arrayList){
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            intent.putExtra("event_id", e.event_id);
            AlarmReceiver.add_next_event(getApplicationContext(),intent,e);
        }

        floatingActionButton=findViewById(R.id.add_event_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                intent.putExtra("event_id",-1);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recycleView);

        adapter = new EventAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<Event> getEventsList(){
        DatabaseHandler databaseHandler = new DatabaseHandler(MainActivity.this);
        ArrayList<Event> events=databaseHandler.getAllEvents();
        Log.e("EVENT ID",databaseHandler.getRecentEventID()+"");
        databaseHandler.close();
        return events;

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.changeData();
    }
}
package com.monkdevs.test_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {

    ArrayList<Event> events;
    Context context;
    public EventAdapter(Context context){
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        this.events=databaseHandler.getAllEvents();
        this.context=context;

    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View eventView = inflater.inflate(R.layout.event_layout,parent,false);
        EventHolder holder = new EventHolder(eventView);
        return holder;
    }

    public void changeData(){
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        events=databaseHandler.getAllEvents();
        notifyDataSetChanged();
        for(Event e:events){
            Log.e("Events",e.event_id+" ");
        }
    }
    String[] radioList = new String[]{"Once", "Minute", "Hour", "Day","Week", "Month", "Year"};
    @Override
    public void onBindViewHolder(@NonNull EventHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tv1.setText(events.get(position).event_name);
        Date d=new Date(events.get(position).event_timestamp);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        String date=format.format(d);
        holder.tv2.setText(date);
        if(events.get(position).event_interval==0){
            holder.tv3.setText("Repeat " + radioList[events.get(position).event_interval]);
        }else {
            holder.tv3.setText("Repeat Every " + radioList[events.get(position).event_interval]);
        }
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context,MainActivity2.class);
                intent.putExtra("event_id",events.get(position).event_id);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

protected class EventHolder extends RecyclerView.ViewHolder{
    public TextView tv1,tv2,tv3;
    public View v;
        public EventHolder(@NonNull View itemView) {
        super(itemView);
            tv1=itemView.findViewById(R.id.event_list_name);
            tv2=itemView.findViewById(R.id.event_list_date);
            tv3=itemView.findViewById(R.id.event_list_interval);


            v=itemView.findViewById(R.id.event_list_root);
        }
}

}

package com.example.eventplanner.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList = new ArrayList<>();
    private final EventClickListener listener;

    public interface EventClickListener {
        void onEditClick(Event event);

        void onDeleteClick(Event event);
    }

    public EventAdapter(EventClickListener listener) {
        this.listener = listener;
    }

    public void setEventList(List<Event> events) {
        this.eventList = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.textViewTitle.setText(event.getTitle());
        holder.textViewCategory.setText("Category: " + event.getCategory());
        holder.textViewLocation.setText("Location: " + event.getLocation());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

        String formattedDate = dateFormat.format(new Date(event.getDateTime()));
        holder.textViewDateTime.setText("Date: " + formattedDate);

        holder.buttonEdit.setOnClickListener(v -> listener.onEditClick(event));

        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteClick(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        TextView textViewCategory;
        TextView textViewLocation;
        TextView textViewDateTime;
        Button buttonEdit;
        Button buttonDelete;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
package com.example.eventplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapter.EventAdapter;
import com.example.eventplanner.data.Event;
import com.example.eventplanner.data.EventDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventListFragment extends Fragment {

    private RecyclerView recyclerViewEvents;
    private TextView textViewEmpty;
    private EventAdapter eventAdapter;

    private EventDatabase eventDatabase;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public EventListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewEvents = view.findViewById(R.id.recyclerViewEvents);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);

        eventDatabase = EventDatabase.getInstance(requireContext());

        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(requireContext()));

        eventAdapter = new EventAdapter(new EventAdapter.EventClickListener() {
            @Override
            public void onEditClick(Event event) {
                Bundle bundle = new Bundle();
                bundle.putInt("eventId", event.getId());

                Navigation.findNavController(view).navigate(R.id.action_eventListFragment_to_addEditEventFragment, bundle);
            }

            @Override
            public void onDeleteClick(Event event) {
                deleteEvent(event);
            }
        });

        recyclerViewEvents.setAdapter(eventAdapter);

        loadEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        executorService.execute(() -> {
            long currentTime = System.currentTimeMillis();

            List<Event> events = eventDatabase.eventDao().getUpcomingEvents(currentTime);

            requireActivity().runOnUiThread(() -> {
                eventAdapter.setEventList(events);

                if (events.isEmpty()) {
                    textViewEmpty.setVisibility(View.VISIBLE);
                    recyclerViewEvents.setVisibility(View.GONE);
                } else {
                    textViewEmpty.setVisibility(View.GONE);
                    recyclerViewEvents.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void deleteEvent(Event event) {
        executorService.execute(() -> {
            eventDatabase.eventDao().deleteEvent(event);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();

                loadEvents();
            });
        });
    }
}
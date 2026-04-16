package com.example.eventplanner;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.eventplanner.R;
import com.example.eventplanner.data.Event;
import com.example.eventplanner.data.EventDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateEventFragment extends Fragment {


    private EditText editTextTitle;
    private EditText editTextLocation;
    private EditText editTextDateTime;
    private Spinner spinnerCategory;
    private Button buttonSave;
    private Button buttonDelete;

    private Button buttonBack;

    private EventDatabase eventDatabase;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Calendar selectedCalendar;
    private int eventId = -1;
    private Event currentEvent;

    public UpdateEventFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextLocation = view.findViewById(R.id.editTextLocation);
        editTextDateTime = view.findViewById(R.id.editTextDateTime);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        buttonBack = view.findViewById(R.id.buttonBack);

        selectedCalendar = Calendar.getInstance();

        eventDatabase = EventDatabase.getInstance(requireContext());

        setupCategorySpinner();

        if (getArguments() != null) {
            eventId = getArguments().getInt("eventId", -1);
        }

        if (eventId == -1) {
            buttonSave.setText("Add Event");
            buttonDelete.setVisibility(View.GONE);
        } else {
            buttonSave.setText("Update Event");
            buttonDelete.setVisibility(View.VISIBLE);
            loadEventDetails();
        }

        editTextDateTime.setOnClickListener(v -> showDatePicker());

        buttonSave.setOnClickListener(v -> saveEvent(view));

        buttonDelete.setOnClickListener(v -> deleteCurrentEvent(view));

        buttonBack.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.eventListFragment);
        });
    }

    private void setupCategorySpinner() {
        String[] categories = {"Work", "Social", "Travel", "Appointment", "Other"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCategory.setAdapter(adapter);
    }

    private void loadEventDetails() {
        executorService.execute(() -> {
            currentEvent = eventDatabase.eventDao().getEventById(eventId);

            requireActivity().runOnUiThread(() -> {
                if (currentEvent != null) {
                    editTextTitle.setText(currentEvent.getTitle());
                    editTextLocation.setText(currentEvent.getLocation());

                    selectedCalendar.setTimeInMillis(currentEvent.getDateTime());
                    updateDateTimeText();

                    setSpinnerSelection(currentEvent.getCategory());
                }
            });
        });
    }

    private void setSpinnerSelection(String category) {
        for (int i = 0; i < spinnerCategory.getCount(); i++) {
            String item = spinnerCategory.getItemAtPosition(i).toString();

            if (item.equals(category)) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    private void showDatePicker() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (datePicker, selectedYear, selectedMonth, selectedDay) -> {
            selectedCalendar.set(Calendar.YEAR, selectedYear);
            selectedCalendar.set(Calendar.MONTH, selectedMonth);
            selectedCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);

            showTimePicker();
        }, year, month, day);

        // Prevents selecting a past date from the calendar.
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void showTimePicker() {
        int hour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = selectedCalendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (timePicker, selectedHour, selectedMinute) -> {
            selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            selectedCalendar.set(Calendar.MINUTE, selectedMinute);
            selectedCalendar.set(Calendar.SECOND, 0);
            selectedCalendar.set(Calendar.MILLISECOND, 0);

            updateDateTimeText();
        }, hour, minute, false);

        timePickerDialog.show();
    }

    private void updateDateTimeText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

        editTextDateTime.setText(dateFormat.format(selectedCalendar.getTime()));
    }

    private void saveEvent(View view) {
        String title = editTextTitle.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String dateText = editTextDateTime.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(dateText)) {
            Toast.makeText(requireContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        long selectedDateTime = selectedCalendar.getTimeInMillis();

        if (selectedDateTime < System.currentTimeMillis()) {
            Toast.makeText(requireContext(), "Past date or time is not allowed", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(location)) {
            location = "No location added";
        }

        String finalLocation = location;

        if (eventId == -1) {
            Event newEvent = new Event(title, category, finalLocation, selectedDateTime);

            executorService.execute(() -> {
                eventDatabase.eventDao().insertEvent(newEvent);

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Event added successfully", Toast.LENGTH_SHORT).show();

                    Navigation.findNavController(view).navigate(R.id.eventListFragment);
                });
            });

        } else {
            Event updatedEvent = new Event(title, category, finalLocation, selectedDateTime);

            updatedEvent.setId(eventId);

            executorService.execute(() -> {
                eventDatabase.eventDao().updateEvent(updatedEvent);

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Event updated successfully", Toast.LENGTH_SHORT).show();

                    Navigation.findNavController(view).navigate(R.id.eventListFragment);
                });
            });
        }
    }

    private void deleteCurrentEvent(View view) {
        if (currentEvent == null) {
            Toast.makeText(requireContext(), "Event not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            eventDatabase.eventDao().deleteEvent(currentEvent);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();

                Navigation.findNavController(view).navigate(R.id.eventListFragment);
            });
        });
    }
}

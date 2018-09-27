package com.hse.touristhelper.qr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.hse.touristhelper.R;

import java.util.Calendar;
import java.util.Locale;

public class CalendarEventFragment extends Fragment {

    private static final String KEY = "key_qr_event";

    private String summary;
    private String description;
    private String location;
    private String organizer;
    private String status;
    private Barcode.CalendarDateTime start;
    private Barcode.CalendarDateTime end;

    public CalendarEventFragment() {
        // Required empty public constructor
    }

    public static CalendarEventFragment newInstance(Barcode barcode) {
        Bundle args = new Bundle();
        args.putParcelable(KEY, barcode);

        CalendarEventFragment fragment = new CalendarEventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Barcode mBarcode = getArguments().getParcelable(KEY);
        summary = mBarcode.calendarEvent.summary;
        description = mBarcode.calendarEvent.description;
        location = mBarcode.calendarEvent.location;
        organizer = mBarcode.calendarEvent.organizer;
        status = mBarcode.calendarEvent.status;
        start = mBarcode.calendarEvent.start;
        end = mBarcode.calendarEvent.end;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(start.year, start.month, start.day, start.hours, start.minutes);
        Calendar endTime = Calendar.getInstance();
        endTime.set(end.year, end.month, end.day, end.hours, end.minutes);

        View v = inflater.inflate(R.layout.fragment_calendar_event, container, false);
        setValue((TextView) v.findViewById(R.id.summary), "Summary: " + summary);
        setValue((TextView) v.findViewById(R.id.description), "Description: " + description);
        setValue((TextView) v.findViewById(R.id.location), "Location: " + location);
        setValue((TextView) v.findViewById(R.id.organizer), "Organizer: " + organizer);
        setValue((TextView) v.findViewById(R.id.status), "Status: " + status);
        setValue((TextView) v.findViewById(R.id.start_time), "Start time:\n" + beginTime.getTime().toString());
        setValue((TextView) v.findViewById(R.id.end_time), "End time:\n" + endTime.getTime().toString());

        v.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar beginTime = Calendar.getInstance();
                beginTime.set(start.year, start.month, start.day, start.hours, start.minutes);
                Calendar endTime = Calendar.getInstance();
                endTime.set(end.year, end.month, end.day, end.hours, end.minutes);
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, summary)
                        .putExtra(CalendarContract.Events.DESCRIPTION, description)
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                        .putExtra(CalendarContract.Events.STATUS, status)
                        .putExtra(CalendarContract.Events.ORGANIZER, organizer);
                startActivity(intent);
            }
        });
        return v;
    }

    public void setValue(TextView v, String value) {
        if (value != null && value.length() > 0) {
            v.setText(value);
        } else {
            v.setVisibility(View.GONE);
        }
    }
}

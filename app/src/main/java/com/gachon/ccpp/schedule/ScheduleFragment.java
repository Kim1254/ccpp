package com.gachon.ccpp.schedule;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gachon.ccpp.R;
import com.gachon.ccpp.parser.ListForm;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

public class ScheduleFragment extends Fragment {
    public Map<Integer,ArrayList<ListForm>> scheduleList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        scheduleList = (Map<Integer,ArrayList<ListForm>>) bundle.getSerializable("schedule");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        MaterialCalendarView cal = view.findViewById(R.id.calendar);
        cal.setSelectionColor(
                ResourcesCompat.getColor(getResources(), R.color.gcc_green, null));

        for(Integer key : scheduleList.keySet()) {
            CalendarDay calendarDay = CalendarDay.from(LocalDateTime.now().getYear(),LocalDateTime.now().getMonthValue()-1,key);
            cal.addDecorator(new EventDecorator(Color.RED, Collections.singleton(calendarDay)));
        }

        cal.addDecorators(
                new DateDecorator(Calendar.SATURDAY,
                        ResourcesCompat.getColor(getResources(), R.color.gcc_liteblue, null)),
                new DateDecorator(Calendar.SUNDAY,
                        ResourcesCompat.getColor(getResources(), R.color.gcc_orange, null))
        );

        cal.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Intent intent = new Intent(getContext(), PopupActivity.class);
                intent.putExtra("schedule",(Serializable) scheduleList.get(date.getDay()));
                startActivity(intent);
            }
        });

        return view;
    }

    static class DateDecorator implements DayViewDecorator {
        private final Calendar cal = Calendar.getInstance();

        private final int date;
        private final int color;

        DateDecorator(int date, int color) {
            this.date = date;
            this.color = color;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(cal);
            return cal.get(Calendar.DAY_OF_WEEK) == date;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(color));
        }
    }

    public class EventDecorator implements DayViewDecorator {

        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(10, color));
        }
    }

}
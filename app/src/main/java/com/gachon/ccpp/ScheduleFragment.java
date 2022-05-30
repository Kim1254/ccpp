package com.gachon.ccpp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        MaterialCalendarView cal = view.findViewById(R.id.calendar);
        cal.setSelectionColor(
                ResourcesCompat.getColor(getResources(), R.color.gcc_green, null));
        cal.setSelectedDate(CalendarDay.today());

        cal.addDecorators(
                new DateDecorator(Calendar.SATURDAY,
                        ResourcesCompat.getColor(getResources(), R.color.gcc_liteblue, null)),
                new DateDecorator(Calendar.SUNDAY,
                        ResourcesCompat.getColor(getResources(), R.color.gcc_orange, null))
        );

<<<<<<< Updated upstream
=======
        cal.setOnDateChangedListener((widget, date, selected) -> {
            String date_str = date.toString();
            String deadline = date_str.substring(date_str.indexOf("{") + 1,
                    date_str.indexOf("}"));

            Intent intent = new Intent(getContext(), DeadlinePopupActivity.class);
            intent.putExtra("date", deadline);
            startActivity(intent);
        });

>>>>>>> Stashed changes
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
}
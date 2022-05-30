package com.gachon.ccpp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;

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

        cal.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String date_str = date.toString();
                int index_a = date_str.indexOf("{");
                int index_b = date_str.indexOf("}");
                String deadline = date_str.substring(index_a+1, index_b);
                Toast.makeText(getContext(), "선택한 날짜는 " + deadline, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), DeadlinePopupActivity.class);
                intent.putExtra("date", deadline);
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



}
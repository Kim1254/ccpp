package com.gachon.ccpp.schedule;

import static com.gachon.ccpp.MainActivity.api;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.gachon.ccpp.dialog.LoadingDialog;
import com.gachon.ccpp.parser.HtmlParser;
import com.gachon.ccpp.parser.ListForm;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.jsoup.Jsoup;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleFragment extends Fragment {
    public Map<Integer,ArrayList<ListForm>> scheduleList;
    public LoadingDialog dialog;
    public int sizeOfData;
    public MaterialCalendarView cal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduleList = new HashMap<>();
        dialog = new LoadingDialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        sizeOfData = -1;
        requestSchedule();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        dialog.show();

        cal = view.findViewById(R.id.calendar);
        cal.setSelectionColor(
                ResourcesCompat.getColor(getResources(), R.color.gcc_green, null));

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

    // 일정이 있는날만 뽑아와서 requestDaySchedule 호출
    public void requestSchedule() {
        Call<ResponseBody> connect = api.getUri("calendar/view.php?view=month");
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        HtmlParser parser = new HtmlParser(Jsoup.parse(response.body().string()));
                        ArrayList<ListForm> monthList = parser.getMonthList();
                        sizeOfData = monthList.size();
                        for (ListForm l : monthList)
                            requestDaySchedule(l.date, l.link);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

    //일정이 있는날 과제만 파싱해서 가져옴
    public void requestDaySchedule(String day, String link) {
        Call<ResponseBody> connect = api.getUri(link);
        connect.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        HtmlParser parser = new HtmlParser(Jsoup.parse(response.body().string()));
                        ArrayList<ListForm> dayList = parser.getDayList();

                        for (ListForm l : dayList){
                            if(scheduleList.get(Integer.valueOf(day))==null)
                                scheduleList.put(Integer.valueOf(day), new ArrayList<ListForm>());
                            scheduleList.get(Integer.valueOf(day)).add(l);
                            CalendarDay calendarDay = CalendarDay.from(LocalDateTime.now().getYear(),LocalDateTime.now().getMonthValue()-1,Integer.valueOf(day));
                            cal.addDecorator(new EventDecorator(Color.RED, Collections.singleton(calendarDay)));
                        }
                        if(sizeOfData == scheduleList.size())
                            dialog.dismiss();
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

}
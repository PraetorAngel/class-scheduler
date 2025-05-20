package com.ogborn.c196final.helper;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static final int TODAY_ALARM_OFFSET = 15000;
    private static final int ALARM_TIME_IF_NOT_TODAY = 8;
    private static boolean isDatePickerOpen = false;

    public static void showDatePicker(Context context, EditText target) {
        if (isDatePickerOpen) return;
        isDatePickerOpen = true;

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(
                context,
                (view, year, month, day) -> {
                    String date = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
                    target.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        picker.setOnDismissListener(dialog -> isDatePickerOpen = false);
        picker.show();
    }

    public static Date parseDate(String input) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(input);
        } catch (ParseException e) {
            return new Date(0);
        }
    }

    public static long computeAlertTime(String dateString) {
        Date parsed = DateUtil.parseDate(dateString);
        if (parsed == null) return -1;

        Calendar fireTime = Calendar.getInstance();
        fireTime.setTime(parsed);

        Calendar now = Calendar.getInstance();

        if (fireTime.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                fireTime.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
            return System.currentTimeMillis() + TODAY_ALARM_OFFSET;
        } else {
            fireTime.set(Calendar.HOUR_OF_DAY, ALARM_TIME_IF_NOT_TODAY);
            fireTime.set(Calendar.MINUTE, 0);
            fireTime.set(Calendar.SECOND, 0);
            fireTime.set(Calendar.MILLISECOND, 0);
            return fireTime.getTimeInMillis();
        }
    }
}
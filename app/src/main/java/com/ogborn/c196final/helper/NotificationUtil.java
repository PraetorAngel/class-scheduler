package com.ogborn.c196final.helper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.ogborn.c196final.R;

public class NotificationUtil {

    private static final int COURSE_ALARM_OFFSET_BASE_MULT = 10000;
    private static final int ASSESSMENT_ALARM_OFFSET_BASE_MULT = 100;
    private static final int MIN_REQUIRED_SDK_FOR_PERMS_ASK = 33;

    public enum AlertDomain{
        COURSE,
        ASSESSMENT
    }

    public static int getOffset(AlertDomain domain) {
        switch (domain) {
            case COURSE: return COURSE_ALARM_OFFSET_BASE_MULT;
            case ASSESSMENT: return ASSESSMENT_ALARM_OFFSET_BASE_MULT;
            default: throw new IllegalArgumentException();
        }
    }

    public static void bindAlertCheckboxes(Context context, int id, CheckBox checkboxStart, CheckBox checkboxEnd) {
        SharedPreferences prefs = context.getSharedPreferences("alerts", Context.MODE_PRIVATE);

        checkboxStart.setChecked(prefs.getBoolean("alert" + id + "_start", false));
        checkboxEnd.setChecked(prefs.getBoolean("alert" + id + "_end", false));
    }

    public static void applyAlertPreferences(
            Context context,
            int id,
            String title,
            String startDate,
            String endDate,
            boolean startChecked,
            boolean endChecked,
            AlertDomain domain
    ) {
        if (!startChecked) {
            cancelAlert(context, id, true, domain);
        }
        if (!endChecked) {
            cancelAlert(context, id, false, domain);
        }

        int offset = getOffset(domain);
        boolean startSuccess = false;
        boolean endSuccess = false;

        if (startChecked) {
            long triggerStart = DateUtil.computeAlertTime(startDate);
            if (triggerStart != -1) {
                startSuccess = scheduleAlert(
                        context,
                        triggerStart,
                        context.getString(R.string.alert_starts_today, title),
                        id * offset,
                        domain,
                        id);
            }
        }

        if (endChecked) {
            long triggerEnd = DateUtil.computeAlertTime(endDate);
            if (triggerEnd != -1) {
                endSuccess = scheduleAlert(
                        context,
                        triggerEnd,
                        context.getString(R.string.alert_ends_today, title),
                        id * offset + 1,
                        domain,
                        id);
            }
        }

        SharedPreferences.Editor editor = context.getSharedPreferences("alerts", Context.MODE_PRIVATE).edit();
        editor.putBoolean("alert" + id + "_start", startSuccess);
        editor.putBoolean("alert" + id + "_end", endSuccess);
        editor.apply();
    }

    public static boolean scheduleAlert(Context context, long triggerTime, String message, int requestCode, AlertDomain  domain, int id) {
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("alertMessage", message);

        if (domain == AlertDomain.COURSE) {
            intent.putExtra("courseId", id);
        } else if (domain == AlertDomain.ASSESSMENT) {
            intent.putExtra("assessmentId", id);
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            return true;
        } catch (SecurityException e) {
            Toast.makeText(context, R.string.toast_permission, Toast.LENGTH_LONG).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Intent alarmIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(alarmIntent);
            }
            return false;
        }
    }

    public static void cancelAlert(Context context, int id, boolean isStart, AlertDomain domain) {
        Intent intent = new Intent(context, AlertReceiver.class);

        int requestCode = id * getOffset(domain) + (isStart ? 0 : 1);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );

        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
    }

    public static void requestNotificationPermissionOnce(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("perm_prefs", Context.MODE_PRIVATE);
        boolean hasAsked = prefs.getBoolean("asked_notifications", false);

        if (!hasAsked && Build.VERSION.SDK_INT >= MIN_REQUIRED_SDK_FOR_PERMS_ASK) {
            String permission = "android.permission.POST_NOTIFICATIONS";

            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{permission},
                    123
            );

            // ✅ Record that we asked — no matter what the user chooses
            prefs.edit().putBoolean("asked_notifications", true).apply();
        }
    }
}
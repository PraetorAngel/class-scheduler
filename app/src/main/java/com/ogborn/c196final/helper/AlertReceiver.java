package com.ogborn.c196final.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.ogborn.c196final.R;
import com.ogborn.c196final.ui.AssessmentDetailActivity;
import com.ogborn.c196final.ui.CourseDetailActivity;
import com.ogborn.c196final.ui.MainActivity;

public class AlertReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "course_alerts";

    @Override
    public void onReceive(Context context, Intent intent) {

        String message = intent.getStringExtra("alertMessage");
        int assessmentId = intent.getIntExtra("assessmentId", -1);
        int courseId = intent.getIntExtra("courseId", -1);

        Intent targetIntent;
        if (assessmentId != -1) {
            targetIntent = new Intent(context, AssessmentDetailActivity.class);
            targetIntent.putExtra("assessmentId", assessmentId);
        } else if (courseId != -1) {
            targetIntent = new Intent(context, CourseDetailActivity.class);
            targetIntent.putExtra("courseId", courseId);
        } else {
            targetIntent = new Intent(context, MainActivity.class);
        }

        PendingIntent pendingIntent = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(targetIntent)
                .getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_gary_was_here)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Course Alerts",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Alerts for course and assessment start/end dates");
        manager.createNotificationChannel(channel);

        if (Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}
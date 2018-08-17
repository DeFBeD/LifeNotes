package com.example.jburgos.life_notes.reminderNotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.evernote.android.job.DailyJob;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.example.jburgos.life_notes.MainActivity;
import com.example.jburgos.life_notes.R;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ReminderNotificationJob extends DailyJob {
    static final String TAG = "show_notification_job_tag";

    @NonNull
    @Override
    protected DailyJobResult onRunDailyJob(@NonNull final Params params) {
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0,
                new Intent(getContext(), MainActivity.class), 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(TAG, "Life-Notes Reminder", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Life-Notes Friendly Reminders");
            getContext().getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(getContext(), TAG)
                .setContentTitle("Life-Notes")
                .setContentText("Friendly Reminder to get your thoughts down!")
                .setAutoCancel(true)
                .setChannelId(TAG)
                .setSound(null)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setShowWhen(true)
                .setLocalOnly(true)
                .build();

        NotificationManagerCompat.from(getContext())
                .notify(new Random().nextInt(), notification);

        return DailyJobResult.SUCCESS;
    }

    public static void schedule() {

        if (!JobManager.instance().getAllJobRequestsForTag(TAG).isEmpty()) {
            // job is already scheduled, there is nothing to do
            return;
        }

        JobRequest.Builder builder = new JobRequest.Builder(ReminderNotificationJob.TAG)
                .setUpdateCurrent(true);

        // run job between 7pm and 8am
        DailyJob.schedule(builder, TimeUnit.HOURS.toMillis(7), TimeUnit.HOURS.toMillis(11));
    }

}



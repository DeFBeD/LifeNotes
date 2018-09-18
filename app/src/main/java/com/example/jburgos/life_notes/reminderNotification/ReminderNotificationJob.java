package com.example.jburgos.life_notes.reminderNotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.jburgos.life_notes.R;
import com.example.jburgos.life_notes.activities.MainActivity;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.Objects;
import java.util.Random;

public class ReminderNotificationJob extends JobService {
    static final String TAG = "show_notification";

    @Override
    public boolean onStartJob(final JobParameters params) {
        //Offloading work to a new thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                friendlyReminder(params);
            }
        }).start();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public void friendlyReminder(final JobParameters parameters) {

        try {

            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0,
                    new Intent(getBaseContext(), MainActivity.class), 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(TAG, "Life-Notes Reminder", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(getString(R.string.channel_description));
                Objects.requireNonNull(getBaseContext().getSystemService(NotificationManager.class)).createNotificationChannel(channel);
            }

            Notification notification = new NotificationCompat.Builder(getBaseContext(), TAG)
                    .setContentTitle(getString(R.string.notification_Title))
                    .setContentText(new ReminderProvider().changeDailyReminder())
                    .setAutoCancel(true)
                    .setChannelId(TAG)
                    .setSound(null)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setShowWhen(true)
                    .setLocalOnly(true)
                    .build();

            NotificationManagerCompat.from(getBaseContext())
                    .notify(new Random().nextInt(), notification);

        } finally {
            jobFinished(parameters, true);
        }
    }
}




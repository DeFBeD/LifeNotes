package com.example.jburgos.life_notes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ReminderNotificationJob extends Job {
    static final String TAG = "show_notification_job_tag";


    @NonNull
    @Override
    protected Result onRunJob(Params params) {

        createChannel(getContext());


        PendingIntent pi = PendingIntent.getActivity(getContext(), 0,
                new Intent(getContext(), MainActivity.class), 0);



        Notification notification = new NotificationCompat.Builder(getContext())
                .setContentTitle("Life-Notes")
                .setContentText("Friendly reminder to log your thoughts!")
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setShowWhen(true)
                .setColor(Color.GREEN)
                .setLocalOnly(true)
                .build();

        NotificationManagerCompat.from(getContext())
                .notify(new Random().nextInt(), notification);

        return Result.SUCCESS;
    }

    static void schedulePeriodic() {
        new JobRequest.Builder(ReminderNotificationJob.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(5))
                .setUpdateCurrent(true)
                .setPersisted(true)
                .build()
                .schedule();
    }

private void createChannel(Context context){

    NotificationManager notificationManager = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel mChannel = new NotificationChannel(
                "Life_notes",
                "Life-Notes reminder",
                NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(mChannel);
    }


    }
}



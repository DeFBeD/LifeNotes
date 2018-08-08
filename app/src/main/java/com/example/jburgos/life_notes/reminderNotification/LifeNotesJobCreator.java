package com.example.jburgos.life_notes.reminderNotification;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class LifeNotesJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case ReminderNotificationJob.TAG:
                return new ReminderNotificationJob();
            default:
                return null;
        }
    }
}
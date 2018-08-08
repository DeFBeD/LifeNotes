package com.example.jburgos.life_notes;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

class LifeNotesJobCreator implements JobCreator {

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
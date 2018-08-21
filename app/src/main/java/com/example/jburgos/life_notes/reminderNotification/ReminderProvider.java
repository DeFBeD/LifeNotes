package com.example.jburgos.life_notes.reminderNotification;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ReminderProvider {

    public String changeDailyReminder() {
        Random randomGenerator = new Random();
        List<String> uniqueReminders = Arrays.asList(
                "Writing down your thoughts is a been known to evoke mindfulness! \n\n Start now!",
                "Best way to achieve your goals is to write them down and reflect on them. \n\n Start now!",
                "Journaling is the best way to boost your emotional intelligence. \n\n Start now!",
                "Having a great routine will strengthen your Self-Discipline. \n\n take a minute to do that now! ",
                "The most creative people in history have been know to keep a journal. \n\n and yours is always in your pocket! "
        );
        int next = randomGenerator.nextInt(uniqueReminders.size());
        return uniqueReminders.get(next);
    }


}

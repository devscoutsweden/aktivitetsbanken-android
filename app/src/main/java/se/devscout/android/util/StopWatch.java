package se.devscout.android.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StopWatch {
    private static class Event {
        final long timeStamp;
        final String label;

        private Event(String label, long timeStamp) {
            this.label = label;
            this.timeStamp = timeStamp;
        }
    }

    private final List<Event> mEvents = new ArrayList<Event>();

    public StopWatch(String name) {
        logEvent("Starting stopwatch " + name);
    }

    public synchronized void logEvent(String label) {
        mEvents.add(new Event(label, System.nanoTime()));
    }

    public synchronized String getSummary() {
        StringBuilder sb = new StringBuilder("Measurements:");
        long lastTimeStamp = mEvents.get(0).timeStamp;
        for (Event event : mEvents) {
            sb.append(String.format(Locale.US, "%n|%,10d ms %s", ((event.timeStamp - lastTimeStamp) / 1000000), event.label));
            lastTimeStamp = event.timeStamp;
        }
        sb.append(String.format(Locale.US, "%n|%,10d ms %s", ((lastTimeStamp - mEvents.get(0).timeStamp) / 1000000), "Total"));
        return sb.toString();
    }
}
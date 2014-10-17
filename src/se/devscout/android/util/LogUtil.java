package se.devscout.android.util;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogUtil {

    private static final int MAX_FILES = 10;
    private static final SimpleDateFormat LOG_ENTRY_TIME_STAMP_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
    private static final int MAX_ENTRIES_PER_FILE = 100;
    private static final String CRASH_REPORT_FILE_NAME_PREFIX = "crash-report-";
    private static final String CRASH_REPORT_FILE_NAME_SUFFIX = ".log";
    private static final int MAX_DAYS_TO_KEEP_CRASH_REPORT_FILE = 7;
    private static final int MILLISECONDS_IN_DAY = 24 * 60 * 60 * 1000;
    private static LogUtil instance = null;

    private LogRecord[] mRecordsRecord = new LogRecord[MAX_ENTRIES_PER_FILE];

    private int pointer = 0;

    private LogUtil() {
    }

    private synchronized void close(File filesDir) {
        try {
            File[] existingFiles = getCrashReportFiles(filesDir);
            Arrays.sort(existingFiles, new Comparator<File>() {
                @Override
                public int compare(File file, File file2) {
                    return (int) (file2.lastModified() - file.lastModified());
                }
            });
            for (int i = 0; i < existingFiles.length; i++) {
                File existingFile = existingFiles[i];
                boolean tooManyFilesInTotal = i > MAX_FILES;
                boolean tooOldFile = (System.currentTimeMillis() - existingFile.lastModified()) > (MAX_DAYS_TO_KEEP_CRASH_REPORT_FILE * MILLISECONDS_IN_DAY);
                if (tooManyFilesInTotal || tooOldFile) {
                    existingFiles[i].delete();
                }
            }
        } catch (Exception e) {
            Log.e(LogUtil.class.getName(), "Could not purge old crash reports.", e);
        }
        File file = new File(filesDir, CRASH_REPORT_FILE_NAME_PREFIX + new Date().getTime() + CRASH_REPORT_FILE_NAME_SUFFIX);
        PrintWriter writer = null;
        try {
            if (file.createNewFile()) {
                writer = new PrintWriter(new FileWriter(file));
                for (int i = pointer; i < pointer + mRecordsRecord.length; i++) {
                    LogRecord record = mRecordsRecord[i % mRecordsRecord.length];
                    if (record != null) {
                        printLogRecord(record, writer);
                    }
                }
            } else {
                Log.e(LogUtil.class.getName(), "Could not create crash report file.");
            }
        } catch (IOException e) {
            Log.e(LogUtil.class.getName(), "Could not save crash report.", e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private void printLogRecord(LogRecord record, PrintWriter writer) {
        writer.print(LOG_ENTRY_TIME_STAMP_FORMAT.format(new Date(record.getMillis())));
        writer.print(" [thread ");
        writer.print(record.getThreadID());
        writer.print("] ");
        writer.print(record.getLoggerName());
        writer.print(": ");
        writer.println(record.getMessage());
        Throwable throwable = record.getThrown();
        if (throwable != null) {
            throwable.printStackTrace(writer);
        }
        writer.println();
    }

    public static File[] getCrashReportFiles(File filesDir) {
        return filesDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.startsWith(CRASH_REPORT_FILE_NAME_PREFIX);
            }
        });
    }

    public static File[] getCrashReportFiles(Context ctx) {
        return getCrashReportFiles(ctx.getFilesDir());
    }

    private synchronized void publish(LogRecord logRecord) {
        mRecordsRecord[pointer] = logRecord;
        pointer = (pointer + 1) % mRecordsRecord.length;
    }

    private class ExceptionHandler implements Thread.UncaughtExceptionHandler {

        private final Thread.UncaughtExceptionHandler mOriginalHandler;
        private final Context mCtx;

        private ExceptionHandler(Thread.UncaughtExceptionHandler handler, Context ctx) {
            mOriginalHandler = handler;
            mCtx = ctx;
        }

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            addToRollingLog(LogUtil.class.getName(), "Thread '" + thread.getName() + "' (id: " + thread.getId() + ") crashed due to uncaught " + throwable.getClass().getName(), Level.SEVERE, throwable);
            synchronized (LogUtil.this) {
                close(mCtx.getFilesDir());
            }
            mOriginalHandler.uncaughtException(thread, throwable);
        }

    }

    public synchronized static LogUtil getInstance() {
        if (instance == null) {
            instance = new LogUtil();
        }
        return instance;
    }

    public void initUncaughtExceptionHandler(Context ctx) {
        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        if (handler == null || !(handler instanceof ExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(handler, ctx));
        }
    }

    public static void initExceptionLogging(Context ctx) {
        LogUtil.getInstance().initUncaughtExceptionHandler(ctx);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
        addToRollingLog(tag, msg, Level.FINE, null);
    }

    public static void i(String tag, String msg) {
        i(tag, msg, null);
    }

    public static void i(String tag, String msg, Throwable throwable) {
        Log.i(tag, msg);
        addToRollingLog(tag, msg, Level.INFO, throwable);
    }

    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    public static void e(String tag, String msg, Throwable e) {
        Log.e(tag, msg, e);
        addToRollingLog(tag, msg, Level.SEVERE, e);
    }

    private static void addToRollingLog(String logger, String msg, Level level, Throwable throwable) {
        LogRecord record = new LogRecord(level, msg);
        record.setLoggerName(logger);
        record.setThreadID((int) Thread.currentThread().getId());
        record.setThrown(throwable);
        record.setMillis(System.currentTimeMillis());
        LogUtil.getInstance().publish(record);
    }
}

package experimental.guireloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * @author gmatoga
 */
public class PollingFileWatcher {
    File file;
    private ExecutorService singleThreadPool;
    private Future<?> submittedJob;
    private Callback callback;

    public PollingFileWatcher(String filename, Callback callback) {
        this(filename);
        this.callback = callback;
    }

    public PollingFileWatcher(File filename, Callback callback) {
        this(filename);
        this.callback = callback;
    }

    public PollingFileWatcher(File file) {
        this.file = file;
        singleThreadPool = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = Executors.defaultThreadFactory().newThread(runnable);
                thread.setDaemon(true);
                return thread;

            }
        });
    }

    public PollingFileWatcher(String filename) {
        this(new File(filename));
    }

    public static void main(String... args) throws IOException {
        final String fileToWatch = "C:\\Users\\" +
                "gmatoga\\Desktop\\" +
                "experimental\\xml-ref-test\\" +
                "src\\main\\java\\" +
                "experimental\\guireloader\\" +
                "PollingFileWatcher.java";

        final Callback callback = new Callback() {

            @Override
            public void fileChanged() {
                System.out.println("Callback called");
            }
        };

        final PollingFileWatcher fileWatcher = new PollingFileWatcher(fileToWatch, callback);
        fileWatcher.watch();

        new BufferedReader(new InputStreamReader(System.in)).readLine();
        fileWatcher.stop();
        fileWatcher.watch();
        new BufferedReader(new InputStreamReader(System.in)).readLine();
        fileWatcher.watch();
        new BufferedReader(new InputStreamReader(System.in)).readLine();

    }

    public void watch() {
        if (submittedJob != null)
            stop();
        submittedJob = singleThreadPool.submit(new PollingRunnable(file, callback));
    }

    public void stop() {
        if (submittedJob.isDone()) return;
        submittedJob.cancel(true);
    }

    public static interface Callback {
        void fileChanged();
    }

    private class PollingRunnable implements Runnable {

        public static final String INTERRUPTED_COOPERATIVELY = "Interrupted cooperatively";
        public static final String INTERRUPTED_WHILE_SLEEPING = "Interrupted while sleeping";
        public static final String TIME_STAMP_NOT_CHANGED = "Time stamp not changed";
        public static final String FIRST_TIME_STAMP_READING = "First TimeStamp reading";
        public static final String TIMESTAMP_CHANGE_DETECTED = "Timestamp change detected";
        public static final int REACT = 1;
        public static final int RESET = 2;
        public static final int SAVE = 3;
        public static final int NEW_FILE = 4;
        private final String filename;
        private final File file;
        private final Callback callback;
        private long lastModified = -1;
        private String lastEvent;
        private boolean newFileDetected = false;

        public PollingRunnable(File file, Callback callback) {
            this.filename = file.getName();
            this.callback = callback;
            this.file = file;

        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    react(file);

                    Thread.sleep(10);
                }
                interruptedCooperatively();
            } catch (InterruptedException e) {
                interruptedWhileSleeping();
                // cooperatively clear the interrupted flag
                Thread.currentThread().interrupt();
            }
        }

        private void react(File file) {
            if (file.exists())
                react(file.lastModified());
            else
                fileNotExists();
        }

        private void react(long actualLastModified) {
            if (lastModified != actualLastModified) {
                if (lastModified == -1)
                    timeStampFirstReading();
                else
                    timeStampChangeDetected();
            } else
                timeStampNotChanged();
            this.lastModified = actualLastModified;
        }

        private void fileNotExists() {
            pushEvent("file not exist: " + filename);
            lastModified = -1;
        }

        private int eventTransition(String a, String b) {
            if (TIMESTAMP_CHANGE_DETECTED.equals(b))
                return SAVE;
            else if (FIRST_TIME_STAMP_READING.equals(b))
                return NEW_FILE;
            else if (TIMESTAMP_CHANGE_DETECTED.equals(a) && TIME_STAMP_NOT_CHANGED.equals(b))
                return REACT;
            else
                return RESET;
        }

        private void pushEvent(String newEvent) {
            if (!TIME_STAMP_NOT_CHANGED.equals(newEvent)) {
                System.out.println(newEvent);
            }
            final int action = eventTransition(lastEvent, newEvent);
            switch (action) {
                case REACT:
                    finalStateAttained();
                    break;
                case SAVE:
                    lastEvent = newEvent;
                    break;
                case NEW_FILE:
                    newFileDetected();
                    lastEvent = newEvent;
                default:
                    reset();
                    break;
            }
        }

        private void newFileDetected() {
            if (newFileDetected)
                finalStateAttained();
            newFileDetected = true;
        }

        private void reset() {
            lastEvent = null;
        }

        private void finalStateAttained() {
            System.out.println("Meaningful change detected");
            if (callback != null)
                callback.fileChanged();
            reset();
        }

        private void interruptedCooperatively() {
            pushEvent(INTERRUPTED_COOPERATIVELY);
        }

        private void interruptedWhileSleeping() {
            pushEvent(INTERRUPTED_WHILE_SLEEPING);
        }

        private void timeStampNotChanged() {
            pushEvent(TIME_STAMP_NOT_CHANGED);
        }

        private void timeStampFirstReading() {
            pushEvent(FIRST_TIME_STAMP_READING);
        }

        private void timeStampChangeDetected() {
            pushEvent(TIMESTAMP_CHANGE_DETECTED);
        }
    }
}

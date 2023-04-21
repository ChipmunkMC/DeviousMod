package me.allinkdev.deviousmod.thread;

import lombok.Getter;
import me.allinkdev.deviousmod.util.ThrowableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public final class DeviousPuppy {
    private static final Runtime RUNTIME = Runtime.getRuntime();
    public static final DeviousPuppy INSTANCE = new DeviousPuppy();
    private static final Logger LOGGER = LoggerFactory.getLogger("deviouspuppy");
    private final Timer timer = new Timer("Puppy Timer", true);
    private final Map<Thread, ThreadMonitor> monitorMap = new ConcurrentHashMap<>();

    public DeviousPuppy() {
        if (INSTANCE != null) {
            throw new IllegalStateException("DeviousPuppy is a singleton!");
        }

        RUNTIME.addShutdownHook(new Thread(this::shutdown));
    }

    /**
     * Starts monitoring the provided thread. Please note that the timer will <strong>ONLY</strong> be stopped when the thread is interrupted.
     * Please remember to contact the thread monitor, or you may risk unexpected JVM terminations!
     * @param thread The thread to monitor
     */
    public void monitorThread(final Thread thread) {
        LOGGER.info("Started monitoring thread {}!", thread.getName());

        final ThreadMonitor threadMonitor = new ThreadMonitor(thread);
        this.addThreadMonitor(threadMonitor);
    }

    /**
     * Shorthand for calling {@link DeviousPuppy#monitorThread(Thread)} with the current thread
     */
    public void monitorMe() {
        final Thread currentThread = Thread.currentThread();

        this.monitorThread(currentThread);
    }

    public void contactMyThreadMonitor() {
        final Thread currentThread = Thread.currentThread();
        final ThreadMonitor threadMonitor;

        synchronized (this.monitorMap) {
            threadMonitor = monitorMap.get(currentThread);
        }

        if (threadMonitor == null) {
            LOGGER.warn("Thread monitor was null while {} was trying to contact it!", currentThread.getName());
            return;
        }

        try {
            threadMonitor.contact();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Somehow managed to call the current thread's ThreadMonitor from the wrong thread! Is there faulty logic somewhere, or is something trying to modify us?", e);
        }
    }

    private void addThreadMonitor(final ThreadMonitor threadMonitor) {
        final Thread monitoredThread = threadMonitor.getMonitoredThread();

        LOGGER.info("Adding monitored thread {} to lookup map!", monitoredThread.getName());

        synchronized (this.monitorMap) {
            this.monitorMap.put(monitoredThread, threadMonitor);
            this.timer.schedule(threadMonitor, 500, 1_000);
        }
    }

    private void dropThreadMonitor(final ThreadMonitor threadMonitor) {
        threadMonitor.cancel();

        final Thread monitoredThread = threadMonitor.getMonitoredThread();

        synchronized (this.monitorMap) {
            monitorMap.remove(monitoredThread);
        }
    }

    public static final class ThreadMonitor extends TimerTask {
        private static final String TRACE_SEPARATOR = "\n====================\n";
        private static final String MESSAGE = "\n==========THREAD FREEZE==========\nDeviousMod has detected that an important thread (%s) has frozen. The JVM will self-destruct in approximately 30 seconds of this thread not responding.\nHere is the stacktrace of the thread:\n %s";
        private static final Logger LOGGER = LoggerFactory.getLogger("Watchdog Thread Monitor");
        private static final String KILL_MESSAGE = "That is all. Good night.";
        private final Object lastContactTimestampLock = new Object();
        @Getter
        private final Thread monitoredThread;
        private long violationCount = 0;
        private long lastContactTimestamp = System.currentTimeMillis();
        private long lastStackTracePrintTimestamp = 0;

        private ThreadMonitor(final Thread monitoredThread) {
            this.monitoredThread = monitoredThread;
        }

        @Override
        public void run() {
            if (this.monitoredThread.isInterrupted()) {
                LOGGER.info("{} is interrupted, removing thread monitor!", this.monitoredThread);
                DeviousPuppy.INSTANCE.dropThreadMonitor(this);
                return;
            }

            final long now = System.currentTimeMillis();
            final long diff;

            synchronized (this.lastContactTimestampLock) {
                diff = now - this.lastContactTimestamp;
            }

            if (diff < 3_000) { // 3 seconds
                // Reset every value, as the thread is (now) responding
                this.lastStackTracePrintTimestamp = 0;
                this.violationCount = 0;
                return;
            }

            final long stackTracePrintDiff = now - this.lastStackTracePrintTimestamp;

            if (stackTracePrintDiff < 10_000) { // We don't want to spam the logs, after all
                return;
            }

            final StackTraceElement[] monitoredThreadTraceElements = this.monitoredThread.getStackTrace();
            final String monitoredThreadTrace = ThrowableUtil.getStackTrace(monitoredThreadTraceElements);

            final Thread currentThread = Thread.currentThread();
            final StackTraceElement[] currentThreadTraceElements = currentThread.getStackTrace();
            final String currentThreadTrace = ThrowableUtil.getStackTrace(currentThreadTraceElements);

            final String message = String.format(MESSAGE, this.monitoredThread.getName(), monitoredThreadTrace + TRACE_SEPARATOR +  currentThreadTrace);
            LOGGER.error(message);

            this.lastStackTracePrintTimestamp = now;
            this.violationCount++;

            if (this.violationCount >= 3) {
                killJvm();
            }
        }

        private static void killJvm() {
            LOGGER.error(KILL_MESSAGE);
            RUNTIME.halt(1337);
        }

        public void contact() throws IllegalAccessException {
            final Thread thread = Thread.currentThread();

            if (thread != this.monitoredThread) {
                final StackTraceElement[] threadTraceElements = thread.getStackTrace();
                final String trace = ThrowableUtil.getStackTrace(threadTraceElements);

                throw new IllegalAccessException("Tried to illegally contact ThreadMonitor from different thread (" + thread.getName() + ") " + "! Stacktrace is as follows: \n" + trace);
            }

            synchronized (this.lastContactTimestampLock) {
                this.lastContactTimestamp = System.currentTimeMillis();
            }
        }
    }

    private void shutdown() {
        this.timer.purge();
    }
}

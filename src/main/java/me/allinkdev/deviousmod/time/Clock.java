package me.allinkdev.deviousmod.time;

import me.allinkdev.deviousmod.event.time.second.ClientSecondEvent;
import me.allinkdev.deviousmod.util.EventUtil;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

public final class Clock {
    private final Timer timer = new Timer();

    public Clock() {
        this.scheduleRepeating(() -> EventUtil.postEvent(new ClientSecondEvent()), Duration.ofSeconds(1));
    }

    public TimerTask scheduleRepeating(final Runnable runnable, final Duration interval) {
        return this.scheduleRepeating(runnable, Duration.ZERO, interval);
    }

    public ClockTask scheduleRepeating(final Runnable runnable, final Duration delay, final Duration interval) {
        final ClockTask clockTask = new ClockTask(runnable);
        this.timer.schedule(clockTask, delay.toMillis(), interval.toMillis());
        return clockTask;
    }

    public TimerTask scheduleDelayed(final Runnable runnable, final Duration delay) {
        final ClockTask clockTask = new ClockTask(runnable);
        this.timer.schedule(clockTask, delay.toMillis());
        return clockTask;
    }

    public static final class ClockTask extends TimerTask {
        private final Runnable runnable;
        private final Object interruptLock = new Object();
        private boolean interrupted = false;

        ClockTask(final Runnable runnable) {
            this.runnable = runnable;
        }

        public void interrupt() {
            synchronized (this.interruptLock) {
                this.interrupted = true;
            }
        }

        @Override
        public void run() {
            synchronized (this.interruptLock) {
                if (this.interrupted) {
                    this.cancel();
                    return;
                }
            }

            this.runnable.run();
        }
    }
}

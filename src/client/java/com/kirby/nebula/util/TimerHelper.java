package com.kirby.nebula.util;

/**
 * Helper class for timing operations
 */
public class TimerHelper {
    private long lastTime;

    public TimerHelper() {
        reset();
    }

    /**
     * Reset timer to current time
     */
    public void reset() {
        lastTime = System.currentTimeMillis();
    }

    /**
     * Check if specified time has passed (in milliseconds)
     */
    public boolean hasReached(long time) {
        return System.currentTimeMillis() - lastTime >= time;
    }

    /**
     * Check if specified time has passed in seconds
     */
    public boolean hasReachedSeconds(double seconds) {
        return hasReached((long) (seconds * 1000));
    }

    /**
     * Get time passed since last reset (in milliseconds)
     */
    public long getTimePassed() {
        return System.currentTimeMillis() - lastTime;
    }

    /**
     * Get time passed in seconds
     */
    public double getTimePassedSeconds() {
        return getTimePassed() / 1000.0;
    }

    /**
     * Get time remaining until target (in milliseconds)
     */
    public long getTimeRemaining(long target) {
        long remaining = target - getTimePassed();
        return Math.max(0, remaining);
    }

    /**
     * Check if timer has passed and reset if true
     */
    public boolean hasReachedAndReset(long time) {
        if (hasReached(time)) {
            reset();
            return true;
        }
        return false;
    }

    /**
     * Set timer to specific time in the past
     */
    public void setTime(long time) {
        lastTime = System.currentTimeMillis() - time;
    }

    /**
     * Get last reset time
     */
    public long getLastTime() {
        return lastTime;
    }
}

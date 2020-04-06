package com.devebot.sample;

/**
 * Created by myasus on 4/3/20.
 */
public class RandomOptions {
    private int concurrentCalls = 100;
    private int exceptionTotal = 0;

    public RandomOptions() {
    }

    public RandomOptions(int concurrentCalls, int exceptionTotal) {
        if (concurrentCalls > 0) {
            this.concurrentCalls = concurrentCalls;
        }
        this.exceptionTotal = exceptionTotal;
    }

    public int getConcurrentCalls() {
        return concurrentCalls;
    }

    public int getExceptionTotal() {
        return exceptionTotal;
    }
}

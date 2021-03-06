package com.bank.rest.other;

import java.util.LinkedList;
import java.util.List;

public class ConcurrentTestUtil {

    static void runMultithreaded(Runnable runnable, int threadCount) throws InterruptedException {

        List<Thread> threadList = new LinkedList<>();

        for (int i = 0; i < threadCount; i++) {
            threadList.add(new Thread(runnable));
        }
        for (Thread t : threadList) {
            t.start();
        }
        for (Thread t : threadList) {
            t.join();
        }
    }
}

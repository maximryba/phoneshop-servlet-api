package com.es.phoneshop.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultDosProtectionService implements DosProtectionService {

    private static volatile DosProtectionService instance;
    private static final long THRESHOLD = 10;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Map<String, AtomicLong> countMap = new ConcurrentHashMap<>();

    public static DosProtectionService getInstance() {
        if (instance == null) {
            synchronized (DosProtectionService.class) {
                if (instance == null) {
                    instance = new DefaultDosProtectionService();
                }
            }
        }
        return instance;
    }

    private DefaultDosProtectionService() {
        scheduler.scheduleAtFixedRate(this::resetCounts, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public boolean isAllowed(String ip) {
        AtomicLong count = countMap.computeIfAbsent(ip, k -> new AtomicLong(0));
        long currentCount = count.incrementAndGet();

        if (currentCount > THRESHOLD) {
            return false;
        }
        return true;
    }

    private void resetCounts() {
        countMap.clear();
    }
}

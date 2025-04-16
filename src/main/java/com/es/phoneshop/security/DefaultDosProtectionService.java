package com.es.phoneshop.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DefaultDosProtectionService implements DosProtectionService {

    private static volatile DosProtectionService instance;
    private static final long THRESHOLD = 10;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Map<String, Long> countMap = new ConcurrentHashMap<>();

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
        Long count = countMap.get(ip);
        if (count == null) {
            count = 1L;
        } else {
            if (count > THRESHOLD) {
                return false;
            }
            count++;
        }
        countMap.put(ip, count);
        return true;
    }

    private void resetCounts() {
        countMap.clear();
    }
}

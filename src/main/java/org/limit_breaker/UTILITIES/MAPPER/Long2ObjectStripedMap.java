package org.limit_breaker.UTILITIES.MAPPER;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.concurrent.locks.StampedLock;
import java.util.function.LongFunction;
import java.util.function.Predicate;

public final class Long2ObjectStripedMap<V> {

    private static final int SEG_BITS = 8;
    private static final int SEG_COUNT = 1 << SEG_BITS;
    private static final int SEG_MASK = SEG_COUNT - 1;

    private final Long2ObjectOpenHashMap<V>[] segments;
    private final StampedLock[] locks;

    @SuppressWarnings("unchecked")
    public Long2ObjectStripedMap(int expectedCapacity) {
        int perSeg = Math.max(16, (expectedCapacity >> SEG_BITS) + 1);
        segments = new Long2ObjectOpenHashMap[SEG_COUNT];
        locks = new StampedLock[SEG_COUNT];
        for (int i = 0; i < SEG_COUNT; i++) {
            segments[i] = new Long2ObjectOpenHashMap<>(perSeg);
            locks[i] = new StampedLock();
        }
    }

    private int seg(long key) {
        return (int) ((key ^ (key >>> 32)) & SEG_MASK);
    }

    public V get(long key) {
        StampedLock l = locks[seg(key)];
        long stamp = l.tryOptimisticRead();
        V v;
        try {
            v = segments[seg(key)].get(key);
        } catch (ArrayIndexOutOfBoundsException e) {
            stamp = l.readLock();
            try {
                v = segments[seg(key)].get(key);
            } finally {
                l.unlockRead(stamp);
            }
            return v;
        }
        if (!l.validate(stamp)) {
            stamp = l.readLock();
            try {
                v = segments[seg(key)].get(key);
            } finally {
                l.unlockRead(stamp);
            }
        }
        return v;
    }

    public V put(long key, V val) {
        StampedLock l = locks[seg(key)];
        long stamp = l.writeLock();
        try {
            return segments[seg(key)].put(key, val);
        } finally {
            l.unlockWrite(stamp);
        }
    }

    public V remove(long key) {
        StampedLock l = locks[seg(key)];
        long stamp = l.writeLock();
        try {
            return segments[seg(key)].remove(key);
        } finally {
            l.unlockWrite(stamp);
        }
    }

    public V putIfAbsent(long key, V val) {
        StampedLock l = locks[seg(key)];
        long stamp = l.writeLock();
        try {
            return segments[seg(key)].putIfAbsent(key, val);
        } finally {
            l.unlockWrite(stamp);
        }
    }

    public V computeIfAbsent(long key, LongFunction<V> mapping) {
        StampedLock l = locks[seg(key)];
        long stamp = l.writeLock();
        try {
            Long2ObjectOpenHashMap<V> m = segments[seg(key)];
            V v = m.get(key);
            if (v == null) {
                v = mapping.apply(key);
                m.put(key, v);
            }
            return v;
        } finally {
            l.unlockWrite(stamp);
        }
    }

    public boolean containsKey(long key) {
        StampedLock l = locks[seg(key)];
        long stamp = l.readLock();
        try {
            return segments[seg(key)].containsKey(key);
        } finally {
            l.unlockRead(stamp);
        }
    }

    public void removeIf(Predicate<V> predicate) {
        for (int i = 0; i < SEG_COUNT; i++) {
            StampedLock l = locks[i];
            long stamp = l.writeLock();
            try {
                segments[i].values().removeIf(predicate);
            } finally {
                l.unlockWrite(stamp);
            }
        }
    }

    public int size() {
        int n = 0;
        for (int i = 0; i < SEG_COUNT; i++) {
            StampedLock l = locks[i];
            long stamp = l.readLock();
            try {
                n += segments[i].size();
            } finally {
                l.unlockRead(stamp);
            }
        }
        return n;
    }

    public boolean isEmpty() {
        for (int i = 0; i < SEG_COUNT; i++) {
            StampedLock l = locks[i];
            long stamp = l.readLock();
            try {
                if (!segments[i].isEmpty()) {
                    return false;
                }
            } finally {
                l.unlockRead(stamp);
            }
        }
        return true;
    }
}

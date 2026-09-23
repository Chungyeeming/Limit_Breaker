package org.limit_breaker.UTILITIES.MATHS;

public class HashMath {
    public static long hash(long x, long y, long z) {
        long xb = ((y & 0xFFFFFFFFL) << 32) | (z & 0xFFFFFFFFL);
        long ha = x & 0xFFFFFFFFL;
        ha ^= ha >>> 33;
        ha *= 0xFF51AFD7ED558CCDL;
        ha ^= ha >>> 33;
        ha *= 0xC4CEB9FE1A85EC53L;
        ha ^= ha >>> 33;

        long h = xb ^ ha;
        h ^= h >>> 33;
        h *= 0xFF51AFD7ED558CCDL;
        h ^= h >>> 33;
        h *= 0xC4CEB9FE1A85EC53L;
        h ^= h >>> 33;
        return h;
    }
}

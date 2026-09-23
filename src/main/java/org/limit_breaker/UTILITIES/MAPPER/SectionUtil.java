package org.limit_breaker.UTILITIES.MAPPER;


import org.limit_breaker.UTILITIES.POSITION.IntSectionPos;

public class SectionUtil {
    public static final Long2ObjectStripedMap<IntSectionPos> lookup = new Long2ObjectStripedMap<>(1 << 20);

    public static void put(long key, int x, int y, int z) {
        IntSectionPos prev = lookup.get(key);
        if (prev != null) {
            if (prev.x != x || prev.y != y || prev.z != z) {
                Common.conflict("section", key, prev.x, prev.y, prev.z, x, y, z);
            }
            return;
        }
        IntSectionPos np = new IntSectionPos(x, y, z);
        prev = lookup.putIfAbsent(key, np);
        if (prev != null) {
            if (prev.x != x || prev.y != y || prev.z != z) {
                Common.conflict("section", key, prev.x, prev.y, prev.z, x, y, z);
            }
        }
    }

    public static IntSectionPos get(long key) {
        return lookup.get(key);
    }

    public static int size() {
        return lookup.size();
    }

    public static void trim(long currentTick) {
        long cutoff = currentTick - 600;
        lookup.removeIf(p -> p.lastAccess < cutoff);
    }
}

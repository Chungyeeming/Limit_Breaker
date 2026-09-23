package org.limit_breaker.UTILITIES.MAPPER;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;


import static com.mojang.text2speech.Narrator.LOGGER;

public class Common {
    private static volatile long lastConflictInfo = 0;

    public static void conflict(String kind, long key, int ox, int oy, int oz, int nx, int ny, int nz) {
        long now = System.currentTimeMillis();
        if (now - lastConflictInfo < 1000) {
            return;
        }
        lastConflictInfo = now;
        LOGGER.warn("Hash Conflicted! {} key=0x{} old={},{},{} new={},{},{}",
                kind, Long.toHexString(key), ox, oy, oz, nx, ny, nz);
    }

    private record PendingKey(ResourceKey<Level> dimension, long chunkPos) {
    }
}

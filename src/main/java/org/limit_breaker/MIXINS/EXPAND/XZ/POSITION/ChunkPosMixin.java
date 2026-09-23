package org.limit_breaker.MIXINS.EXPAND.XZ.POSITION;

import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.*;

import static org.limit_breaker.UTILITIES.MainNumbers.MAX_BLOCK;

@Mixin(ChunkPos.class)
public abstract class ChunkPosMixin {

    @Final @Shadow public int x;
    @Final
    @Shadow public int z;

    @Unique
    private int shiftToBlockCoord(int coord) {
        long val = (long) coord << 4;
        if (val > (long) MAX_BLOCK - 15L) return MAX_BLOCK - 15;
        if (val < (long) ~MAX_BLOCK + 15L) return ~MAX_BLOCK + 15;
        return (int) val;
    }

    @Overwrite
    public int getMinBlockX() {
        return shiftToBlockCoord(this.x);
    }

    @Overwrite
    public int getMinBlockZ() {
        return shiftToBlockCoord(this.z);
    }

    @Overwrite
    public boolean isValid() {
        return true;
    }

    @Overwrite
    public static boolean isValid(int x, int z) {
        return true;
    }
}

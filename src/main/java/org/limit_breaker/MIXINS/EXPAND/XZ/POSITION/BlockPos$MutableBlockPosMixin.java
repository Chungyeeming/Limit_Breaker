package org.limit_breaker.MIXINS.EXPAND.XZ.POSITION;

import net.minecraft.core.BlockPos;
import org.limit_breaker.UTILITIES.POSITION.IntBlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockPos.MutableBlockPos.class)
public class BlockPos$MutableBlockPosMixin {
    @Overwrite
    public BlockPos.MutableBlockPos set(long packedPos) {
        IntBlockPos pos = IntBlockPos.getBlockPos(packedPos);
        BlockPos.MutableBlockPos self = (BlockPos.MutableBlockPos) (Object) this;
        return self.set(pos.x, pos.y, pos.z);
    }
}

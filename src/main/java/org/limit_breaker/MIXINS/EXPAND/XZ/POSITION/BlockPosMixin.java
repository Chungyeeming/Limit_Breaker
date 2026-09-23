package org.limit_breaker.MIXINS.EXPAND.XZ.POSITION;

import net.minecraft.core.BlockPos;
import org.limit_breaker.UTILITIES.MAPPER.BlockUtil;
import org.limit_breaker.UTILITIES.MATHS.HashMath;
import org.limit_breaker.UTILITIES.POSITION.IntBlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockPos.class)
public abstract class BlockPosMixin {

    @Inject(method = "asLong(III)J", at = @At("HEAD"), cancellable = true)
    private static void onAsLongStatic(int x, int y, int z, CallbackInfoReturnable<Long> cir) {
        long key = HashMath.hash(x, y, z);
        BlockUtil.put(key, x, y, z);
        cir.setReturnValue(key);
    }

    @Inject(method = "asLong()J", at = @At("HEAD"), cancellable = true)
    private void onAsLong(CallbackInfoReturnable<Long> cir) {
        BlockPos self = (BlockPos) (Object) this;
        long key = HashMath.hash(self.getX(), self.getY(), self.getZ());
        BlockUtil.put(key, self.getX(), self.getY(), self.getZ());
        cir.setReturnValue(key);
    }

    @Inject(method = "getX(J)I", at = @At("HEAD"), cancellable = true)
    private static void onGetX(long blockNode, CallbackInfoReturnable<Integer> cir) {
        IntBlockPos bp = BlockUtil.get(blockNode);
        cir.setReturnValue(bp != null ? bp.x : (int) (blockNode >> 38));
    }

    @Inject(method = "getY(J)I", at = @At("HEAD"), cancellable = true)
    private static void onGetY(long blockNode, CallbackInfoReturnable<Integer> cir) {
        IntBlockPos bp = BlockUtil.get(blockNode);
        cir.setReturnValue(bp != null ? bp.y : (int) ((blockNode << 52) >> 52));
    }

    @Inject(method = "getZ(J)I", at = @At("HEAD"), cancellable = true)
    private static void onGetZ(long blockNode, CallbackInfoReturnable<Integer> cir) {
        IntBlockPos bp = BlockUtil.get(blockNode);
        cir.setReturnValue(bp != null ? bp.z : (int) ((blockNode << 26) >> 38));
    }
}
package org.limit_breaker.MIXINS.FIX.OVERFLOW;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.limit_breaker.UTILITIES.WORLD.WorldBounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.limit_breaker.UTILITIES.MainNumbers.MAX_BLOCK;

@Mixin(BoundingBox.class)
public abstract class BoundingBoxMixin {

    @Shadow private int minX;
    @Shadow private int minY;
    @Shadow private int minZ;
    @Shadow private int maxX;
    @Shadow private int maxY;
    @Shadow private int maxZ;

    /**
     * 核心修復：直接攔截六參數建構子末端。
     * 當 maxX / maxZ 因 32 位元整數溢位變成負數時，
     * 原版的 Math.min 會把結構盒膨脹成覆蓋整個世界的 42 億格巨型反序盒。
     * 此處將溢位邊界強制截斷（Clamp）在世界物理邊界 MAX_BLOCK。
     */
    @Inject(method = "<init>(IIIIII)V", at = @At("TAIL"))
    private void fixConstructorOverflow(int rawMinX, int rawMinY, int rawMinZ, int rawMaxX, int rawMaxY, int rawMaxZ, CallbackInfo ci) {
        // 修復 X 軸跨越 32 位元上限的溢位
        if (rawMinX > 0 && rawMaxX < 0) {
            this.minX = rawMinX;
            this.maxX = MAX_BLOCK - 2;
        } else if (rawMinX < 0 && rawMaxX < 0 && rawMinX > rawMaxX) {
            // 修復負數邊緣的反向組件
            this.minX = rawMaxX;
            this.maxX = rawMinX;
        }

        // 修復 Z 軸跨越 32 位元上限的溢位
        if (rawMinZ > 0 && rawMaxZ < 0) {
            this.minZ = rawMinZ;
            this.maxZ = MAX_BLOCK - 2;
        } else if (rawMinZ < 0 && rawMaxZ < 0 && rawMinZ > rawMaxZ) {
            this.minZ = rawMaxZ;
            this.maxZ = rawMinZ;
        }
    }

    @Inject(method = "moved", at = @At("HEAD"), cancellable = true)
    private void safeMoved(int dx, int dy, int dz, CallbackInfoReturnable<BoundingBox> cir) {
        long newMinX = (long) this.minX + dx;
        long newMaxX = (long) this.maxX + dx;
        long newMinZ = (long) this.minZ + dz;
        long newMaxZ = (long) this.maxZ + dz;
        if (!WorldBounds.inBlock(newMinX) || !WorldBounds.inBlock(newMaxX)
                || !WorldBounds.inBlock(newMinZ) || !WorldBounds.inBlock(newMaxZ)) {
            cir.setReturnValue((BoundingBox) (Object) this);
            return;
        }
        if (newMinX > newMaxX || newMinZ > newMaxZ) {
            cir.setReturnValue((BoundingBox) (Object) this);
            return;
        }
    }

    @Inject(method = "fromCorners", at = @At("HEAD"), cancellable = true)
    private static void clampFromCorners(Vec3i pos0, Vec3i pos1, CallbackInfoReturnable<BoundingBox> cir) {
        // 在進入建構子前先做 64 位元防溢位排序與截斷
        long minX = Math.min((long) pos0.getX(), (long) pos1.getX());
        long maxX = Math.max((long) pos0.getX(), (long) pos1.getX());
        long minZ = Math.min((long) pos0.getZ(), (long) pos1.getZ());
        long maxZ = Math.max((long) pos0.getZ(), (long) pos1.getZ());

        int clampedMinX = (int) Math.max(minX, -MAX_BLOCK + 2);
        int clampedMaxX = (int) Math.min(maxX, MAX_BLOCK - 2);
        int clampedMinZ = (int) Math.max(minZ, -MAX_BLOCK + 2);
        int clampedMaxZ = (int) Math.min(maxZ, MAX_BLOCK - 2);

        int minY = Math.min(pos0.getY(), pos1.getY());
        int maxY = Math.max(pos0.getY(), pos1.getY());

        cir.setReturnValue(new BoundingBox(clampedMinX, minY, clampedMinZ, clampedMaxX, maxY, clampedMaxZ));
    }
}
package org.limit_breaker.MIXINS.FIX.OVERFLOW;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.limit_breaker.UTILITIES.WORLD.WorldBounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.limit_breaker.UTILITIES.MainNumbers.MAX_BLOCK;

/**
 * BoundingBox 极端坐标修复（moved 越界拒绝 + fromCorners max clamp）。
 */
@Mixin(BoundingBox.class)
public class BoundingBoxMixin {

    @Shadow
    private int minX, minY, minZ, maxX, maxY, maxZ;

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

    @Inject(method = "fromCorners", at = @At("RETURN"), cancellable = true)
    private static void clampFromCorners(Vec3i pos0, Vec3i pos1, CallbackInfoReturnable<BoundingBox> cir) {
        BoundingBox box = cir.getReturnValue();
        int newMaxX = Math.min(box.maxX(), MAX_BLOCK - 2);
        int newMaxY = Math.min(box.maxY(), MAX_BLOCK - 2);
        int newMaxZ = Math.min(box.maxZ(), MAX_BLOCK - 2);
        if (newMaxX == box.maxX() && newMaxY == box.maxY() && newMaxZ == box.maxZ()) {
            return; // 正常 box：放行
        }
        cir.setReturnValue(new BoundingBox(
                Math.min(box.minX(), newMaxX),
                Math.min(box.minY(), newMaxY),
                Math.min(box.minZ(), newMaxZ),
                newMaxX,
                newMaxY,
                newMaxZ));
    }
}

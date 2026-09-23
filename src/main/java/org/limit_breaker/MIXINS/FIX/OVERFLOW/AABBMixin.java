package org.limit_breaker.MIXINS.FIX.OVERFLOW;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static org.limit_breaker.UTILITIES.MainNumbers.MAX_BLOCK;

@Mixin(AABB.class)
public abstract class AABBMixin {

    @Inject(method = "of(Lnet/minecraft/world/level/levelgen/structure/BoundingBox;)Lnet/minecraft/world/phys/AABB;", at = @At("HEAD"), cancellable = true)
    private static void safeOf(BoundingBox box, CallbackInfoReturnable<AABB> cir) {
        // 任一 max 坐标 ≥ MAX_BLOCK-1 才需 long 化
        if (box.maxX() < MAX_BLOCK - 1 && box.maxY() < MAX_BLOCK - 1
                && box.maxZ() < MAX_BLOCK - 1) {
            return;
        }
        cir.setReturnValue(new AABB(
                (double) box.minX(),
                (double) box.minY(),
                (double) box.minZ(),
                (double) ((long) box.maxX() + 1L),
                (double) ((long) box.maxY() + 1L),
                (double) ((long) box.maxZ() + 1L)));
    }

    @Inject(method = "encapsulatingFullBlocks(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/AABB;", at = @At("HEAD"), cancellable = true)
    private static void safeEncapsulatingFullBlocks(BlockPos pos0, BlockPos pos1,
                                                    CallbackInfoReturnable<AABB> cir) {
        int maxX = Math.max(pos0.getX(), pos1.getX());
        int maxY = Math.max(pos0.getY(), pos1.getY());
        int maxZ = Math.max(pos0.getZ(), pos1.getZ());
        if (maxX < MAX_BLOCK - 1 && maxY < MAX_BLOCK - 1
                && maxZ < MAX_BLOCK - 1) {
            return;
        }
        cir.setReturnValue(new AABB(
                (double) Math.min(pos0.getX(), pos1.getX()),
                (double) Math.min(pos0.getY(), pos1.getY()),
                (double) Math.min(pos0.getZ(), pos1.getZ()),
                (double) ((long) maxX + 1L),
                (double) ((long) maxY + 1L),
                (double) ((long) maxZ + 1L)));
    }

    @ModifyArgs(method = "<init>(Lnet/minecraft/core/BlockPos;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;<init>(DDDDDD)V"))
    private static void fixOverflow(Args args) {
        double minX = args.<Double>get(0), minY = args.<Double>get(1), minZ = args.<Double>get(2);
        double maxX = args.<Double>get(3), maxY = args.<Double>get(4), maxZ = args.<Double>get(5);
        if (maxX < minX) {
            args.set(3, minX + 1.0); // maxX 溢出还原
        }
        if (maxY < minY) {
            args.set(4, minY + 1.0); // maxY
        }
        if (maxZ < minZ) {
            args.set(5, minZ + 1.0); // maxZ
        }
    }
}

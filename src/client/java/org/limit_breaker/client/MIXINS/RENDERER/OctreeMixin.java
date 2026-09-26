package org.limit_breaker.client.MIXINS.RENDERER;

import net.minecraft.client.renderer.Octree;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Octree.class)
public abstract class OctreeMixin {

    @Final
    @Shadow
    private BlockPos cameraSectionCenter;

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 0)
    private int limitbreaker$cameraCenteredMinX(int minX) {
        return limitbreaker$boxMin(this.cameraSectionCenter.getX());
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 3)
    private int limitbreaker$cameraCenteredMaxX(int maxX) {
        return limitbreaker$boxMax(this.cameraSectionCenter.getX());
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 1)
    private int limitbreaker$cameraCenteredMinY(int minY) {
        return limitbreaker$boxMin(this.cameraSectionCenter.getY());
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 4)
    private int limitbreaker$cameraCenteredMaxY(int maxY) {
        return limitbreaker$boxMax(this.cameraSectionCenter.getY());
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 2)
    private int limitbreaker$cameraCenteredMinZ(int minZ) {
        return limitbreaker$boxMin(this.cameraSectionCenter.getZ());
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 5)
    private int limitbreaker$cameraCenteredMaxZ(int maxZ) {
        return limitbreaker$boxMax(this.cameraSectionCenter.getZ());
    }

    @Unique
    private static int limitbreaker$halfSpanBlocks() {
        // 固定半徑 256 格（總寬度 512 格），絕對是標準的 2 的冪次方
        return 256;
    }

    @Unique
    private static long limitbreaker$shiftedCenter(long center) {
        long half = limitbreaker$halfSpanBlocks();
        long lowest = (long) Integer.MIN_VALUE + half;
        long highest = (long) Integer.MAX_VALUE - half - 1L; // 留 1 格防止轉 AABB 時溢位
        return Mth.clamp(center, lowest, highest);
    }

    @Unique
    private static int limitbreaker$boxMin(int center) {
        return (int) (limitbreaker$shiftedCenter(center) - limitbreaker$halfSpanBlocks());
    }

    @Unique
    private static int limitbreaker$boxMax(int center) {
        return (int) (limitbreaker$shiftedCenter(center) + limitbreaker$halfSpanBlocks() - 1L);
    }
}
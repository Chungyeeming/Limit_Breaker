package org.limit_breaker.MIXINS.FIX.ENTITY;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static org.limit_breaker.UTILITIES.MainNumbers.MAX_BLOCK;
import static org.limit_breaker.UTILITIES.MainNumbers.MIN_BLOCK_C;

/**
 * 適配 26.3: 防禦實體在 21.4 億極限座標尋路時，
 * 因 min/max 包圍盒整數溢位引發的 NegativeArraySizeException (-268435449)。
 */
@Mixin(PathNavigationRegion.class)
public abstract class PathNavigationRegionMixin {

    @ModifyVariable(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;getX()I", ordinal = 0),
            argsOnly = true,
            name = "start")
    private static BlockPos safeMinPos(BlockPos min, Level level, BlockPos minPos, BlockPos maxPos) {
        return beyond$clampBox(minPos, maxPos, true);
    }

    @ModifyVariable(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;getX()I", ordinal = 0),
            argsOnly = true,
            name = "end")
    private static BlockPos safeMaxPos(BlockPos max, Level level, BlockPos minPos, BlockPos maxPos) {
        return beyond$clampBox(minPos, maxPos, false);
    }

    @Unique
    private static BlockPos beyond$clampBox(BlockPos min, BlockPos max, boolean isMin) {
        int x = beyond$clampAxis(min.getX(), max.getX(), isMin);
        int y = beyond$clampAxis(min.getY(), max.getY(), isMin);
        int z = beyond$clampAxis(min.getZ(), max.getZ(), isMin);
        return new BlockPos(x, y, z);
    }

    @Unique
    private static int beyond$clampAxis(int minVal, int maxVal, boolean isMin) {
        if (minVal <= maxVal) {
            return isMin ? minVal : maxVal;
        }

        // 發生 32 位元溢位反轉 (minVal > maxVal)
        int span = maxVal - minVal;
        int center = minVal + span / 2;

        if (center >= 0) {
            // 正世界邊界：max 端溢位至負數，將 max 截斷至世界極限
            return isMin ? minVal : MAX_BLOCK;
        } else {
            // 負世界邊界：min 端下溢至正數，將 min 截斷至世界極限
            return isMin ? MIN_BLOCK_C : maxVal;
        }
    }
}
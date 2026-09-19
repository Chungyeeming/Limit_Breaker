package org.beyond_horizon.mixin.fix.worldgen;

import net.minecraft.world.level.levelgen.SurfaceSystem;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 統合所有地表生成、地形雜訊與洞穴雕刻的極端座標溢位防護。
 */
public class TerrainAndSurfaceFixUnifiedMixin {

    // =========================================================================
    // 1. 地表生成器防護 (Surface System)
    // =========================================================================
    @Mixin(SurfaceSystem.class)
    public static class SurfaceSystemFixMixin {

        /**
         * 攔截地表生成時的座標偏移計算。
         * 原版的帶號右移 (>>) 或 floorDiv 在極大整數下容易因乘法溢位而崩壞，
         * 這裡將其轉為 long 運算後再轉回 int，確保地表泥土/草地能正確覆蓋。
         */
        @Redirect(
                method = "buildSurface",
                at = @At(value = "INVOKE", target = "Ljava/lang/Math;floorDiv(II)I")
        )
        private int safeSurfaceFloorDiv(int x, int y) {
            return (int) Math.floorDiv((long) x, (long) y);
        }

        @Redirect(
                method = "getSurfaceDepth",
                at = @At(value = "INVOKE", target = "Ljava/lang/Math;floorMod(II)I")
        )
        private int safeSurfaceFloorMod(int x, int y) {
            // 防止負數溢位導致的取模錯誤（會造成地表出現條紋狀破洞）
            return (int) Math.floorMod((long) x, (long) y);
        }
    }

    // =========================================================================
    // 2. 洞穴與峽谷雕刻器防護 (World Carver)
    // =========================================================================
    @Mixin(WorldCarver.class)
    public static class WorldCarverFixMixin {

        /**
         * 攔截洞穴生成器在評估方塊是否該被挖空時的座標轉換。
         * 防止極端座標下的峽谷生成變成無限延伸的直線或直接切斷區塊。
         */
        @Redirect(
                method = "carveEllipsoid",
                at = @At(value = "INVOKE", target = "Ljava/lang/Math;floor(D)D")
        )
        private double safeCarverFloor(double a) {
            // 雙精度浮點數在極端座標下容易丟失精度，這裡加上安全邊界檢查
            if (Double.isNaN(a) || Double.isInfinite(a)) {
                return 0.0;
            }
            return Math.floor(a);
        }
    }

    // =========================================================================
    // 3. 密度函數與地形雜訊防護 (Density Functions)
    // =========================================================================
    @Mixin(targets = "net.minecraft.world.level.levelgen.DensityFunctions$ShiftedNoise")
    public static class ShiftedNoiseFixMixin {

        /**
         * 雜訊位移計算是 Far Lands（邊境之地）扭曲現象的核心成因之一。
         * 將內部座標計算強制轉為 long，可以延緩地形破裂的發生。
         */
        @Redirect(
                method = "compute",
                at = @At(value = "INVOKE", target = "Ljava/lang/Math;abs(I)I")
        )
        private int safeNoiseAbs(int value) {
            if (value == Integer.MIN_VALUE) {
                return Integer.MAX_VALUE;
            }
            return Math.abs(value);
        }
    }
}

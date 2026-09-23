package org.limit_breaker.client.mixins.RENDERER;

import net.minecraft.client.renderer.Octree;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Octree.class)
public class OctreeMixin {

    @Shadow
    private BlockPos cameraSectionCenter;

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 1)
    private int farlands$cameraCenteredMinY(int minY) {
        return farlands$boxMin(this.cameraSectionCenter.getY());
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 4)
    private int farlands$cameraCenteredMaxY(int maxY) {
        return farlands$boxMax(this.cameraSectionCenter.getY());
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 0)
    private int farlands$cameraCenteredMinX(int minX) {
        return farlands$boxMin(this.cameraSectionCenter.getX());
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 3)
    private int farlands$cameraCenteredMaxX(int maxX) {
        return farlands$boxMax(this.cameraSectionCenter.getX());
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 2)
    private int farlands$cameraCenteredMinZ(int minZ) {
        return farlands$boxMin(this.cameraSectionCenter.getZ());
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 5)
    private int farlands$cameraCenteredMaxZ(int maxZ) {
        return farlands$boxMax(this.cameraSectionCenter.getZ());
    }

    /** 网格竖直段数向上取到 2 的幂再折半，单位方块。取整到 2 的幂保证盒边与段边界对齐。 */
    @Unique
    private static int farlands$halfSpanBlocks() {
        int gridSections = 12 * 2 + 1;
        return Mth.smallestEncompassingPowerOfTwo(gridSections) * 16 / 2;
    }

    /**
     * 把相机中心夹进"整盒仍落在 int 内、且转成 AABB 不溢出"的可行窗口。窗口外的中心会让
     * {@code center ± half} 越过 int，此时整盒内移，宽度不变。
     *
     * <p>上界取 {@code INT_MAX - half} 而不是 {@code INT_MAX - (width - 1 - half)}：后者允许
     * {@code maxX} 落到 {@code INT_MAX}，而 {@code AABB.of} 在 int 里做 {@code maxX + 1}，
     * 会溢出成 {@code INT_MIN}，AABB 从构造那刻起就是 {@code min > max} 的反序盒。反序盒进
     * {@code Frustum.cubeInFrustum} 判不可见，{@code visitNodes} 于是一个 section 都收不进
     * 八叉树，可见集为空，编译入口只遍历可见集，最终该 section 不渲染。留一位之后
     * {@code maxX ≤ INT_MAX - 1}，加一后正好落在 int 内。
     */
    @Unique
    private static long farlands$shiftedCenter(long center) {
        long half = farlands$halfSpanBlocks();
        long lowest = Integer.MIN_VALUE + half;
        long highest = (long) Integer.MAX_VALUE - half;
        return Mth.clamp(center, lowest, highest);
    }

    /** 盒的下端。与 {@link #farlands$boxMax} 共用同一个夹后的中心，因此宽度恒为 2 * halfSpanBlocks()。 */
    @Unique
    private static int farlands$boxMin(int center) {
        return (int) (farlands$shiftedCenter(center) - farlands$halfSpanBlocks());
    }

    /** 盒的上端。 */
    @Unique
    private static int farlands$boxMax(int center) {
        return (int) (farlands$shiftedCenter(center) + farlands$halfSpanBlocks() - 1L);
    }
}

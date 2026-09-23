package org.limit_breaker.MIXINS.FIX.OVERFLOW;

import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(VoxelShape.class)
public abstract class VoxelShapeMixin {

    @Unique
    private static final double EPSILON = 1.0E-7;

    @Unique
    private static double fixedAdd(double value) {
        double base = value - EPSILON;
        return base + Math.max(EPSILON, Math.ulp(base));
    }

    @Unique
    private static double fixedSub(double value) {
        double base = value + EPSILON;
        return base - Math.max(EPSILON, Math.ulp(base));
    }

    // aAxis.min + 1.0E-7
    @ModifyArg(method = "collideX", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/shapes/VoxelShape;findIndex(Lnet/minecraft/core/Direction$Axis;D)I", ordinal = 0), index = 1)
    private double fixEpsilon0(double value) {
        return fixedAdd(value);
    }

    // aAxis.max - 1.0E-7
    @ModifyArg(method = "collideX", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/shapes/VoxelShape;findIndex(Lnet/minecraft/core/Direction$Axis;D)I", ordinal = 1), index = 1)
    private double fixEpsilon1(double value) {
        return fixedSub(value);
    }

    // bAxis.min + 1.0E-7
    @ModifyArg(method = "collideX", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/shapes/VoxelShape;findIndex(Lnet/minecraft/core/Direction$Axis;D)I", ordinal = 2), index = 1)
    private double fixEpsilon2(double value) {
        return fixedAdd(value);
    }

    // bAxis.max - 1.0E-7
    @ModifyArg(method = "collideX", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/shapes/VoxelShape;findIndex(Lnet/minecraft/core/Direction$Axis;D)I", ordinal = 3), index = 1)
    private double fixEpsilon3(double value) {
        return fixedSub(value);
    }

    // cAxis.min + 1.0E-7
    @ModifyArg(method = "collideX", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/shapes/VoxelShape;findIndex(Lnet/minecraft/core/Direction$Axis;D)I", ordinal = 4), index = 1)
    private double fixEpsilon4(double value) {
        return fixedAdd(value);
    }

    // cAxis.max - 1.0E-7
    @ModifyArg(method = "collideX", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/shapes/VoxelShape;findIndex(Lnet/minecraft/core/Direction$Axis;D)I", ordinal = 5), index = 1)
    private double fixEpsilon5(double value) {
        return fixedSub(value);
    }
}

package org.limit_breaker.MIXINS.EXPAND.XZ.WORLDBORDER;

import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.limit_breaker.UTILITIES.MainNumbers.MAX_BLOCK;

@Mixin(WorldBorder.class)
public class WorldBorderMixin {
    @Shadow
    private int absoluteMaxSize;

    @Unique
    private void setAbsoluteMaxSize(int value) {
        absoluteMaxSize = value;
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void OnInitA(CallbackInfo ci) {
        setAbsoluteMaxSize(MAX_BLOCK);
    }

    @ModifyConstant(method = "<init>(Lnet/minecraft/world/level/border/WorldBorder$Settings;)V", constant = @Constant(doubleValue = 5.9999968E7D))
    private double OnInitB(double value) {
        return (MAX_BLOCK - 1) * 2.0;
    }
}

package org.limit_breaker.MIXINS.EXPAND.XZ.WORLDBORDER;

import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static org.limit_breaker.UTILITIES.MainNumbers.MAX_BLOCK;

@Mixin(WorldBorder.Settings.class)
public class WorldBorder$SettingsMixin {

    @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = 5.9999968E7D))
    private static double onClassInitA(double value) {
        return (MAX_BLOCK - 15) * 2.0;
    }

    @ModifyConstant(method = "lambda$static$0", constant = @Constant(doubleValue = 2.9999984E7))
    private static double onClassInitB(double value) {
        return MAX_BLOCK - 15;
    }

    @ModifyConstant(method = "lambda$static$0", constant = @Constant(doubleValue = -2.9999984E7))
    private static double onClassInitC(double value) {
        return ~MAX_BLOCK + 15;
    }
}

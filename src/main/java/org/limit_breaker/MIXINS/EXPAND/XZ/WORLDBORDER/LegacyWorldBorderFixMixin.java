package org.limit_breaker.MIXINS.EXPAND.XZ.WORLDBORDER;

import net.minecraft.util.datafix.fixes.LegacyWorldBorderFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static org.limit_breaker.UTILITIES.MainNumbers.MAX_PLAYABLE_BLOCK;

@Mixin(LegacyWorldBorderFix.class)
public class LegacyWorldBorderFixMixin {
    @ModifyConstant(method = "lambda$makeRule$1", constant = @Constant(doubleValue = 5.9999968E7D))
    private static double maxSize(double value) {
        return (MAX_PLAYABLE_BLOCK) * 2.0;
    }
}

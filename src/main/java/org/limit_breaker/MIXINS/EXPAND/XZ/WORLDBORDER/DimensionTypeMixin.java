package org.limit_breaker.MIXINS.EXPAND.XZ.WORLDBORDER;

import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import static org.limit_breaker.UTILITIES.MainNumbers.MAX_PLAYABLE_BLOCK;

@Mixin(DimensionType.class)
public class DimensionTypeMixin {

    @ModifyConstant(method = "lambda$createDirectCodec$0", constant = @Constant(doubleValue = 3.0E7))
    private static double maxValue(double value) {
        return MAX_PLAYABLE_BLOCK;
    }
}

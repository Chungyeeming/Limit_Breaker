package org.limit_breaker.MIXINS.EXPAND.XZ.WORLDBORDER;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static org.limit_breaker.UTILITIES.MainNumbers.MAX_BLOCK;

@Mixin(Player.class)
public class PlayerMixin {
    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 2.9999999E7))
    private static double maxPos(double value) {
        return MAX_BLOCK - 1;
    }

    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = -2.9999999E7))
    private static double minPos(double value) {
        return ~MAX_BLOCK + 1;
    }
}

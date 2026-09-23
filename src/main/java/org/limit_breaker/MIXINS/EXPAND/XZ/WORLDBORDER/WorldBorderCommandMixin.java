package org.limit_breaker.MIXINS.EXPAND.XZ.WORLDBORDER;

import net.minecraft.server.commands.WorldBorderCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static org.limit_breaker.UTILITIES.MainNumbers.MAX_BLOCK;

@Mixin(WorldBorderCommand.class)
public class WorldBorderCommandMixin {
    @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = 5.9999968E7d))
    private static double onClassInitA(double value) {
        return (MAX_BLOCK - 16.0) * 2.0;
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = 2.9999984E7d))
    private static double onClassInitB(double value) {
        return MAX_BLOCK - 16.0;
    }

    @ModifyConstant(method = "register", constant = @Constant(doubleValue = 5.9999968E7d))
    private static double maxBlock(double value) {
        return (MAX_BLOCK - 16.0) * 2.0;
    }

    @ModifyConstant(method = "register", constant = @Constant(doubleValue = -5.9999968E7d))
    private static double minBlock(double value) {
        return (~MAX_BLOCK + 16.0) * 2.0;
    }

    @ModifyConstant(method = "setSize", constant = @Constant(doubleValue = 5.9999968E7d))
    private static double maxSize(double value) {
        return (MAX_BLOCK - 16.0) * 2.0;
    }

    @ModifyConstant(method = "setCenter", constant = @Constant(doubleValue = 2.9999984E7))
    private static double maxBlockA(double value) {
        return MAX_BLOCK - 16.0;
    }
}

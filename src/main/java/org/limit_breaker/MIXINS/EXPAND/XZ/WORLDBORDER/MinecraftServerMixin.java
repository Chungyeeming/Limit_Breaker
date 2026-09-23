package org.limit_breaker.MIXINS.EXPAND.XZ.WORLDBORDER;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static org.limit_breaker.UTILITIES.MainNumbers.MAX_BLOCK;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @ModifyConstant(method = "getAbsoluteMaxWorldSize", constant = @Constant(intValue = 29999984))
    private int absoluteMaxWorldSize(int value) {
        return MAX_BLOCK;
    }
}

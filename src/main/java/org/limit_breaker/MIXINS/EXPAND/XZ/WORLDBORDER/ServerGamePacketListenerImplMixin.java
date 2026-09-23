package org.limit_breaker.MIXINS.EXPAND.XZ.WORLDBORDER;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {

    @Inject(method = "clampHorizontal", at = @At("HEAD"), cancellable = true)
    private static void clampHorizontal(double value, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(value);
    }
}

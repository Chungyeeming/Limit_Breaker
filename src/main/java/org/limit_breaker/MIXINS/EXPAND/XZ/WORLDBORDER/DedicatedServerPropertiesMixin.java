package org.limit_breaker.MIXINS.EXPAND.XZ.WORLDBORDER;

import net.minecraft.server.dedicated.DedicatedServerProperties;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DedicatedServerProperties.class)
public class DedicatedServerPropertiesMixin {

    @Shadow
    @Final
    @Mutable
    private int maxWorldSize;

    @Unique
    private void setMaxWorldSize() {
        maxWorldSize = org.limit_breaker.UTILITIES.MainNumbers.MAX_PLAYABLE_BLOCK;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void OnInit(CallbackInfo ci) {
        setMaxWorldSize();
    }
}

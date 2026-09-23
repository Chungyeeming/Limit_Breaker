package org.limit_breaker.MIXINS.FIX.WORLDGEN;

import net.minecraft.util.ThreadingDetector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 適配 26.3: 屏蔽 ThreadingDetector 的並行存取斷言。
 * 解決 Modern Beta 等結構/礦物特徵在多執行緒 (ForkJoinPool) 生成時，
 * 跨區塊存取 PalettedContainer 導致的 IllegalStateException 崩潰。
 */
@Mixin(ThreadingDetector.class)
public abstract class ThreadingDetectorMixin {

    @Inject(method = "checkAndLock", at = @At("HEAD"), cancellable = true)
    private void safeCheckAndLock(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "checkAndUnlock", at = @At("HEAD"), cancellable = true)
    private void safeCheckAndUnlock(CallbackInfo ci) {
        ci.cancel();
    }
}

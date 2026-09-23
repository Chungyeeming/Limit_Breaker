package org.limit_breaker.client.mixins.FIX.RENDERER;

import net.minecraft.client.renderer.WeatherEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static org.limit_breaker.UTILITIES.MainNumbers.MAX_BLOCK;

/**
 * 天气渲染提取 extractRenderState 的 X/Z 遍历范围在相机坐标接近世界边界时溢出。
 */
@Mixin(WeatherEffectRenderer.class)
public class WeatherEffectRendererMixin {

    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I"))
    private static int floorSafe(double v) {
        int r = (int) Math.floor(v);
        if (r > MAX_BLOCK - 1) {
            return MAX_BLOCK - 1;
        }
        if (r < -MAX_BLOCK) {
            return -MAX_BLOCK;
        }
        return r;
    }
}

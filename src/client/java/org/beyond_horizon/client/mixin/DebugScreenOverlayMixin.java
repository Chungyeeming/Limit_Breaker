package org.beyond_horizon.client.mixin;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    /**
     * 攔截 F3 左側除錯資訊清單，加入玩家當前座標的 float 精度與 ULP 監控
     */
    @Inject(method = "", at = @At("RETURN"))
    protected void beyond$addFloatPrecisionInfo(CallbackInfoReturnable<List<String>> cir) {
        List<String> list = cir.getReturnValue();
        Entity camera = this.minecraft.getCameraEntity();

        if (camera != null) {
            double x = camera.getX();
            double y = camera.getY();
            double z = camera.getZ();

            float fx = (float) x;
            float fz = (float) z;

            // 計算最小解析步長 (ULP)
            float ulpX = Math.ulp(fx);
            float ulpZ = Math.ulp(fz);

            // 計算截斷誤差 (真實 double 與 float 的絕對差值)
            double errX = Math.abs((double) fx - x);
            double errZ = Math.abs((double) fz - z);

            // 狀態顏色：ULP >= 1 代表連整數方塊都無法精確區分，顯示為紅色
            String statusX = ulpX >= 1.0F ? "§c[嚴重缺失]" : (ulpX > 0.01F ? "§e[微小抖動]" : "§a[正常]");
            String statusZ = ulpZ >= 1.0F ? "§c[嚴重缺失]" : (ulpZ > 0.01F ? "§e[微小抖動]" : "§a[正常]");

            list.add("");
            list.add("§6[Float Precision Monitor]§r");
            list.add(String.format("X float: %.1f | ULP: %.4f | Err: %.4f %s", fx, ulpX, errX, statusX));
            list.add(String.format("Z float: %.1f | ULP: %.4f | Err: %.4f %s", fz, ulpZ, errZ, statusZ));
        }
    }
}

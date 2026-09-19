package org.beyond_horizon.mixin.fix.worldgen.chunk;

import net.minecraft.server.level.ChunkGenerationTask;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGenerationTask.class)
public abstract class ChunkGenerationTaskMixin {

    @Shadow @Final private ChunkPos pos;

    // 32 位元實體區塊極限 (134217727)，提早幾格攔截以策安全
    private static final int MAX_VALID_CHUNK = 134217727;

    /**
     * 攔截伺服器的無限等待迴圈 (假死/凍結的主因)
     * 當遊戲試圖等待極限座標外的區塊生成時，我們強制短路並回傳 true。
     */
    @Inject(method = "runUntilWait", at = @At("HEAD"), cancellable = true)
    private void forceCompleteVoidTasks(CallbackInfoReturnable<Boolean> cir) {
        if (Math.abs(this.pos.x()) > MAX_VALID_CHUNK || Math.abs(this.pos.z()) > MAX_VALID_CHUNK) {
            // 強制告訴伺服器主執行緒：「區塊已經處理完畢」
            // 這會瞬間斬斷死鎖，讓指令 (/gamemode) 和方塊更新立刻恢復運作！
            cir.setReturnValue(true);
        }
    }
}

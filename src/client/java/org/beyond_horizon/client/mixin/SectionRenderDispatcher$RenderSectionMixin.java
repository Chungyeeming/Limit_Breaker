package org.beyond_horizon.mixin.fix.render;

import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.SectionPos;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SectionRenderDispatcher.RenderSection.class)
public abstract class RenderSectionBoxFixMixin {

    @Shadow private AABB bb;

    /**
     * 攔截 RenderSection 的節點設定，修正極限座標下絕對 AABB 導致 Octree 發生 StackOverflow 的問題。
     */
    @Inject(method = "setSectionNode", at = @At("RETURN"))
    private void beyond$fixOctreeAABBOverflow(long sectionNode, CallbackInfo ci) {
        int x = SectionPos.sectionToBlockCoord(SectionPos.x(sectionNode));
        int y = SectionPos.sectionToBlockCoord(SectionPos.y(sectionNode));
        int z = SectionPos.sectionToBlockCoord(SectionPos.z(sectionNode));

        // 如果絕對座標超過了我們之前設定的安全邊界（例如 21.4 億扣掉緩衝區），
        // 我們將其 AABB 限制在安全範圍內，防止 Octree 進行極端深度的無效分割。
        int SAFE_LIMIT = 2147483447;
        if (Math.abs(x) > SAFE_LIMIT || Math.abs(z) > SAFE_LIMIT) {
            int clampedX = Math.max(-SAFE_LIMIT, Math.min(SAFE_LIMIT, x));
            int clampedZ = Math.max(-SAFE_LIMIT, Math.min(SAFE_LIMIT, z));
            this.bb = new AABB(clampedX, y, clampedZ, clampedX + 16, y + 16, clampedZ + 16);
        }
    }
}

package org.limit_breaker.MIXINS.EXPAND.XZ.STORAGE;

import net.minecraft.world.level.chunk.storage.SectionStorage;
import org.limit_breaker.UTILITIES.POSITION.IntSectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SectionStorage.class)
public class SectionStorageMixin {

    // 替换 setDirty 中的 SectionPos.x
    @Redirect(method = "setDirty", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;x(J)I"))
    private int redirectX(long sectionNode) {
        return IntSectionPos.getSectionPos(sectionNode).x;
    }

    @Redirect(method = "outsideStoredRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;y(J)I"))
    private int redirectY(long sectionNode) {
        return IntSectionPos.getSectionPos(sectionNode).y;
    }

    @Redirect(method = "setDirty", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;z(J)I"))
    private int redirectZ(long sectionNode) {
        return IntSectionPos.getSectionPos(sectionNode).z;
    }
}
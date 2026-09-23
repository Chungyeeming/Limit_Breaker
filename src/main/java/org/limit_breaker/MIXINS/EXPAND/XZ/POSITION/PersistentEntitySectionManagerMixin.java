package org.limit_breaker.MIXINS.EXPAND.XZ.POSITION;

import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.limit_breaker.UTILITIES.POSITION.IntSectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PersistentEntitySectionManager.class)
public class PersistentEntitySectionManagerMixin {

    @Redirect(method = "lambda$dumpSections$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;x(J)I"))
    private int redirectSectionX(long sectionKey) {
        return IntSectionPos.getSectionPos(sectionKey).x;
    }

    @Redirect(method = "lambda$dumpSections$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;y(J)I"))
    private int redirectSectionY(long sectionKey) {
        return IntSectionPos.getSectionPos(sectionKey).y;
    }

    @Redirect(method = "lambda$dumpSections$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;z(J)I"))
    private int redirectSectionZ(long sectionKey) {
        return IntSectionPos.getSectionPos(sectionKey).z;
    }
}
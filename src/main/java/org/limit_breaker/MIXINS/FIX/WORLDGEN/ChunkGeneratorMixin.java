package org.limit_breaker.MIXINS.FIX.WORLDGEN;

import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.limit_breaker.UTILITIES.WORLD.WorldBounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 缓冲带/超 chunk 域 chunk 跳过 applyBiomeDecoration。
 */
@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {

    @Inject(method = "applyBiomeDecoration", at = @At("HEAD"), cancellable = true)
    private void skipInFarlands(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager,
            CallbackInfo ci) {
        int cx = chunk.getPos().x();
        int cz = chunk.getPos().z();
        if (!WorldBounds.inChunkRange(cx, cz)) {
            ci.cancel();
        }
    }
}

package org.limit_breaker.MIXINS.FIX.WORLDGEN;

import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static org.limit_breaker.UTILITIES.MainNumbers.MINESHAFT_LIMIT_CHUNK;

@Mixin(MineshaftStructure.class)
public class MineshaftStructureMixin {

    @Inject(method = "findGenerationPoint", at = @At("HEAD"), cancellable = true)
    private void skipOutOfBounds(Structure.GenerationContext context,
            CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        int cx = context.chunkPos().x();
        int cz = context.chunkPos().z();
        if (cx > MINESHAFT_LIMIT_CHUNK || cx < ~MINESHAFT_LIMIT_CHUNK
                || cz > MINESHAFT_LIMIT_CHUNK || cz < ~MINESHAFT_LIMIT_CHUNK) {
            cir.setReturnValue(Optional.empty());
        }
    }
}

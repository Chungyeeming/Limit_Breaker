package org.limit_breaker.MIXINS.FIX.WORLDGEN;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.limit_breaker.UTILITIES.MainNumbers.MAX_BLOCK;


@Mixin(targets = "net.minecraft.world.level.levelgen.structure.structures.MineshaftPieces$MineShaftCorridor")
public abstract class MineshaftPieces$MineShaftCorridorMixin {

    @Inject(method = "addChildren", at = @At("HEAD"), cancellable = true)
    private void skipOverflowingBox(StructurePiece startPiece, StructurePiecesBuilder builder, RandomSource random, CallbackInfo ci) {
        BoundingBox bb = ((StructurePiece) (Object) this).getBoundingBox();
        if (bb.minX() >MAX_BLOCK - 100 || bb.maxX() > MAX_BLOCK - 100
                || bb.minZ() > MAX_BLOCK - 100 || bb.maxZ() > MAX_BLOCK - 100) {
            ci.cancel();
        }
    }
}

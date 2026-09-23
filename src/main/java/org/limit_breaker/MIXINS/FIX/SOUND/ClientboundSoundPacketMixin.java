package org.limit_breaker.MIXINS.FIX.SOUND;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundSoundPacket.class)
public abstract class ClientboundSoundPacketMixin {

    @Shadow
    @Final
    private Holder<SoundEvent> sound;

    @Shadow
    @Final
    private SoundSource source;

    @Shadow
    @Final
    private float volume;

    @Shadow
    @Final
    private float pitch;

    @Shadow
    @Final
    private long seed;

    @Unique
    private long farlands$x8;

    @Unique
    private long farlands$y8;

    @Unique
    private long farlands$z8;

    @Unique
    private int farlands$coordIndex;

    @Inject(method = "<init>(Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;DDDFFJ)V", at = @At("RETURN"))
    private void farlands$storeLongCoords(Holder<SoundEvent> sound, SoundSource source, double x, double y,
            double z, float volume, float pitch, long seed, CallbackInfo ci) {
        this.farlands$x8 = (long) (x * 8.0);
        this.farlands$y8 = (long) (y * 8.0);
        this.farlands$z8 = (long) (z * 8.0);
    }

    @Redirect(method = "<init>(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/RegistryFriendlyByteBuf;readInt()I"))
    private int farlands$readLongAsInt(RegistryFriendlyByteBuf buffer) {
        long v = buffer.readLong();
        switch (this.farlands$coordIndex) {
            case 0 -> this.farlands$x8 = v;
            case 1 -> this.farlands$y8 = v;
            default -> this.farlands$z8 = v;
        }
        this.farlands$coordIndex++;
        return (int) v;
    }

    @Overwrite
    private void write(RegistryFriendlyByteBuf buffer) {
        SoundEvent.STREAM_CODEC.encode(buffer, this.sound);
        //noinspection deprecation
        buffer.writeEnum(this.source);
        buffer.writeLong(this.farlands$x8);
        buffer.writeLong(this.farlands$y8);
        buffer.writeLong(this.farlands$z8);
        buffer.writeFloat(this.volume);
        buffer.writeFloat(this.pitch);
        buffer.writeLong(this.seed);
    }

    @Overwrite
    public double getX() {
        return (double) this.farlands$x8 / 8.0;
    }

    @Overwrite
    public double getY() {
        return (double) this.farlands$y8 / 8.0;
    }

    @Overwrite
    public double getZ() {
        return (double) this.farlands$z8 / 8.0;
    }

}

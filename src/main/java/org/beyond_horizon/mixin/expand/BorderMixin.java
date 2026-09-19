package org.beyond_horizon.mixin.fix.overflow;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ForceLoadCommand;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.util.datafix.fixes.LegacyWorldBorderFix;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class WorldBorderAndConfigUnifiedMixin {

    @Mixin(DedicatedServerProperties.class)
    public static class DedicatedServerPropertiesMixin {
        @Shadow
        @Final
        @Mutable private int maxWorldSize;
        @Inject(method = "<init>", at = @At("RETURN"))
        private void onInit(CallbackInfo ci) {
            this.maxWorldSize = Integer.MAX_VALUE - 16;
        }
    }

    @Mixin(DimensionType.class)
    public static class DimensionTypeMixin {
        @ModifyConstant(method = "lambda$createDirectCodec$0", constant = @Constant(doubleValue = 3.0E7))
        private static double maxValue(double value) {
            return (double) Integer.MAX_VALUE;
        }
    }

    @Mixin(ForceLoadCommand.class)
    public static class ForceLoadCommandMixin {
        @ModifyConstant(method = "changeForceLoad", constant = @Constant(intValue = 30000000))
        private static int maxBlock(int max) { return Integer.MAX_VALUE; }
        @ModifyConstant(method = "changeForceLoad", constant = @Constant(intValue = -30000000))
        private static int minBlock(int min) { return Integer.MIN_VALUE; }
    }

    @Mixin(LegacyWorldBorderFix.class)
    public static class LegacyWorldBorderFixMixin {
        @ModifyConstant(method = "lambda$makeRule$1", constant = @Constant(doubleValue = 5.9999968E7D))
        private static double maxSize(double value) {
            return (Integer.MAX_VALUE - 16.0) * 2.0;
        }
    }

    @Mixin(Level.class)
    public static class LevelMixin {
        @ModifyConstant(method = "isInWorldBoundsHorizontal", constant = @Constant(intValue = 30000000))
        private static int maxBlockA(int max) { return Integer.MAX_VALUE; }
        @ModifyConstant(method = "isInWorldBoundsHorizontal", constant = @Constant(intValue = -30000000))
        private static int minBlockA(int min) { return Integer.MIN_VALUE; }
        @ModifyConstant(method = "getHeight", constant = @Constant(intValue = 30000000))
        private static int maxBlockB(int max) { return Integer.MAX_VALUE; }
        @ModifyConstant(method = "getHeight", constant = @Constant(intValue = -30000000))
        private static int minBlockB(int min) { return Integer.MIN_VALUE; }
    }

    @Mixin(MinecraftServer.class)
    public static class MinecraftServerMixin {
        @ModifyConstant(method = "getAbsoluteMaxWorldSize", constant = @Constant(intValue = 29999984))
        private int absoluteMaxWorldSize(int value) {
            return Integer.MAX_VALUE;
        }
    }

    @Mixin(WorldBorder.class)
    public static class WorldBorderMixin {
        @Shadow private int absoluteMaxSize;
        @Inject(method = "<init>", at = @At("RETURN"))
        private void onInitA(CallbackInfo ci) { this.absoluteMaxSize = Integer.MAX_VALUE; }
        @ModifyConstant(method = "<init>(Lnet/minecraft/world/level/border/WorldBorder$Settings;)V", constant = @Constant(doubleValue = 5.9999968E7D))
        private double onInitB(double value) { return (Integer.MAX_VALUE - 16.0) * 2.0; }
    }

    @Mixin(WorldBorder.Settings.class)
    public static class WorldBorderSettingsMixin {
        @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = 5.9999968E7D))
        private static double onClassInitA(double value) { return (Integer.MAX_VALUE - 16.0) * 2.0; }
        @ModifyConstant(method = "lambda$static$0", constant = @Constant(doubleValue = 2.9999984E7))
        private static double onClassInitB(double value) { return (double) Integer.MAX_VALUE - 16.0; }
        @ModifyConstant(method = "lambda$static$0", constant = @Constant(doubleValue = -2.9999984E7))
        private static double onClassInitC(double value) { return (double) -Integer.MAX_VALUE + 16.0; }
    }

    @Mixin(WorldBorderCommand.class)
    public static class WorldBorderCommandMixin {
        @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = 5.9999968E7d))
        private static double onClassInitA(double value) { return (Integer.MAX_VALUE - 16.0) * 2.0; }
        @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = 2.9999984E7d))
        private static double onClassInitB(double value) { return (double) Integer.MAX_VALUE - 16.0; }
        @ModifyConstant(method = "register", constant = @Constant(doubleValue = 5.9999968E7d))
        private static double maxBlock(double value) { return (Integer.MAX_VALUE - 16.0) * 2.0; }
        @ModifyConstant(method = "register", constant = @Constant(doubleValue = -5.9999968E7d))
        private static double minBlock(double value) { return ((double) -Integer.MAX_VALUE + 16.0) * 2.0; }
        @ModifyConstant(method = "setSize", constant = @Constant(doubleValue = 5.9999968E7d))
        private static double maxSize(double value) { return (Integer.MAX_VALUE - 16.0) * 2.0; }
        @ModifyConstant(method = "setCenter", constant = @Constant(doubleValue = 2.9999984E7))
        private static double maxBlockA(double value) { return (double) Integer.MAX_VALUE - 16.0; }
    }
}

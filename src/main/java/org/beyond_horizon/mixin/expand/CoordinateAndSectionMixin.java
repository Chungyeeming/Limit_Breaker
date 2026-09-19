package com.inf.farlands.mixin.expand.xyz.pos;

import com.inf.farlands.FarlandsTick;
import com.inf.farlands.util.hash.HashMath;
import com.inf.farlands.util.maps.SectionUtil;
import com.inf.farlands.util.pos.IntBlockPos;
import com.inf.farlands.util.pos.IntSectionPos;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.SectionStorage;
import net.minecraft.world.level.entity.*;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 統合所有座標、區段、包圍盒讀寫、實體區段快取與網路封包位置序列化的 Mixin 集合
 */
public class CoordinateAndSectionUnifiedMixin {

    @Mixin(SectionPos.class)
    public static abstract class SectionPosMixin {
        @Overwrite
        public long asLong() {
            int x = ((SectionPos) (Object) this).x();
            int y = ((SectionPos) (Object) this).y();
            int z = ((SectionPos) (Object) this).z();
            long key = HashMath.hash(x, y, z);
            SectionUtil.put(key, x, y, z);
            return key;
        }

        @Overwrite
        public static long asLong(int x, int y, int z) {
            long key = HashMath.hash(x, y, z);
            SectionUtil.put(key, x, y, z);
            return key;
        }

        @Overwrite
        public static int x(long packed) {
            IntSectionPos p = SectionUtil.get(packed);
            if (p != null) {
                p.lastAccess = FarlandsTick.getNow();
                return p.x;
            }
            return (int) (packed >> 42);
        }

        @Overwrite
        public static int y(long packed) {
            IntSectionPos p = SectionUtil.get(packed);
            if (p != null) {
                p.lastAccess = FarlandsTick.getNow();
                return p.y;
            }
            return (int) (packed << 44 >> 44);
        }

        @Overwrite
        public static int z(long packed) {
            IntSectionPos p = SectionUtil.get(packed);
            if (p != null) {
                p.lastAccess = FarlandsTick.getNow();
                return p.z;
            }
            return (int) (packed << 22 >> 42);
        }

        @Overwrite
        public static SectionPos of(long packed) {
            IntSectionPos p = IntSectionPos.getSectionPos(packed);
            return SectionPos.of(p.x, p.y, p.z);
        }

        @Overwrite
        public static long offset(long packed, Direction direction) {
            return offset(packed, direction.getStepX(), direction.getStepY(), direction.getStepZ());
        }

        @Overwrite
        public static long offset(long packed, int dx, int dy, int dz) {
            IntSectionPos p = IntSectionPos.getSectionPos(packed);
            int nx = p.x + dx, ny = p.y + dy, nz = p.z + dz;
            long key = HashMath.hash(nx, ny, nz);
            SectionUtil.put(key, nx, ny, nz);
            return key;
        }

        @Overwrite
        public static long blockToSection(long levelPos) {
            IntBlockPos bp = IntBlockPos.getBlockPos(levelPos);
            int sx = bp.x >> 4, sy = bp.y >> 4, sz = bp.z >> 4;
            long key = HashMath.hash(sx, sy, sz);
            SectionUtil.put(key, sx, sy, sz);
            return key;
        }

        @Overwrite
        public static long getZeroNode(long packed) {
            IntSectionPos p = IntSectionPos.getSectionPos(packed);
            long key = HashMath.hash(p.x, 0, p.z);
            SectionUtil.put(key, p.x, 0, p.z);
            return key;
        }

        @Overwrite
        public static long getZeroNode(int x, int z) {
            long key = HashMath.hash(x, 0, z);
            SectionUtil.put(key, x, 0, z);
            return key;
        }

        @Shadow public abstract int x();
        @Shadow public abstract int y();
        @Shadow public abstract int z();
        @Shadow public static int sectionRelativeX(short packed) { return 0; }
        @Shadow public static int sectionRelativeY(short packed) { return 0; }
        @Shadow public static int sectionRelativeZ(short packed) { return 0; }

        @Overwrite
        public int relativeToBlockX(short local) {
            return (int) (((long) x() << 4) + (long) sectionRelativeX(local));
        }

        @Overwrite
        public int relativeToBlockY(short local) {
            return (int) (((long) y() << 4) + (long) sectionRelativeY(local));
        }

        @Overwrite
        public int relativeToBlockZ(short local) {
            return (int) (((long) z() << 4) + (long) sectionRelativeZ(local));
        }

        @Overwrite
        public BlockPos relativeToBlockPos(short local) {
            int bx = (int) (((long) x() << 4) + (long) sectionRelativeX(local));
            int by = (int) (((long) y() << 4) + (long) sectionRelativeY(local));
            int bz = (int) (((long) z() << 4) + (long) sectionRelativeZ(local));
            return new BlockPos(bx, by, bz);
        }

        @Overwrite
        public static void aroundAndAtBlockPos(long pos, it.unimi.dsi.fastutil.longs.LongConsumer consumer) {
            IntBlockPos bp = IntBlockPos.getBlockPos(pos);
            SectionPos.aroundAndAtBlockPos(new BlockPos(bp.x, bp.y, bp.z), consumer);
        }
    }

    @Mixin(SectionStorage.class)
    public static class SectionStorageMixin {
        @Redirect(method = "setDirty", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;x(J)I"))
        private int redirectX(long sectionPos) { return IntSectionPos.getSectionPos(sectionPos).x; }

        @Redirect(method = "outsideStoredRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;y(J)I"))
        private int redirectY(long sectionPos) { return IntSectionPos.getSectionPos(sectionPos).y; }

        @Redirect(method = "setDirty", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;z(J)I"))
        private int redirectZ(long sectionPos) { return IntSectionPos.getSectionPos(sectionPos).z; }
    }

    @Mixin(PersistentEntitySectionManager.class)
    public static class PersistentEntitySectionManagerMixin {
        @Redirect(method = "lambda$dumpSections$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;x(J)I"))
        private int redirectSectionX(long sectionKey) { return IntSectionPos.getSectionPos(sectionKey).x; }

        @Redirect(method = "lambda$dumpSections$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;y(J)I"))
        private int redirectSectionY(long sectionKey) { return IntSectionPos.getSectionPos(sectionKey).y; }

        @Redirect(method = "lambda$dumpSections$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;z(J)I"))
        private int redirectSectionZ(long sectionKey) { return IntSectionPos.getSectionPos(sectionKey).z; }
    }

    @Mixin(FriendlyByteBuf.class)
    public static class FriendlyByteBufMixin {
        @Overwrite
        public static void writeBlockPos(ByteBuf buffer, BlockPos pos) {
            buffer.writeInt(pos.getX());
            buffer.writeInt(pos.getY());
            buffer.writeInt(pos.getZ());
        }

        @Overwrite
        public static BlockPos readBlockPos(ByteBuf buffer) {
            return new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
        }
    }

    @Mixin(EntitySectionStorage.class)
    public static abstract class EntitySectionStorageMixin<T extends EntityAccess> {
        @Unique private final Long2IntMap sectionXByKey = new Long2IntOpenHashMap();
        @Unique private final Int2ObjectMap<LongSet> sectionsByX = new Int2ObjectOpenHashMap<>();

        @Inject(method = "createSection", at = @At("TAIL"))
        private void onCreateSection(long sectionPos, CallbackInfoReturnable<EntitySection<T>> cir) {
            int sx = IntSectionPos.getSectionPos(sectionPos).x;
            sectionXByKey.put(sectionPos, sx);
            sectionsByX.computeIfAbsent(sx, k -> new LongOpenHashSet()).add(sectionPos);
        }

        @Inject(method = "remove", at = @At("HEAD"))
        private void onRemove(long sectionId, CallbackInfo ci) {
            int sx = sectionXByKey.remove(sectionId);
            int defaultX = sectionXByKey.defaultReturnValue();
            if (sx == defaultX) return;

            LongSet set = sectionsByX.get(sx);
            if (set == null) return;

            set.remove(sectionId);
            if (set.isEmpty()) sectionsByX.remove(sx);
        }

        @Shadow private Long2ObjectMap<EntitySection<T>> sections;
        @Shadow private LongSortedSet sectionIds;

        @Overwrite
        public void forEachAccessibleNonEmptySection(AABB bounds, AbortableIterationConsumer<EntitySection<T>> consumer) {
            int minSecX = SectionPos.posToSectionCoord(bounds.minX - 2.0);
            int maxSecX = SectionPos.posToSectionCoord(bounds.maxX + 2.0);
            int minSecY = SectionPos.posToSectionCoord(bounds.minY - 4.0);
            int maxSecY = SectionPos.posToSectionCoord(bounds.maxY + 0.0);
            int minSecZ = SectionPos.posToSectionCoord(bounds.minZ - 2.0);
            int maxSecZ = SectionPos.posToSectionCoord(bounds.maxZ + 2.0);

            for (int sx = minSecX; sx <= maxSecX; sx++) {
                LongSet keys = sectionsByX.get(sx);
                if (keys == null) continue;

                for (long key : keys) {
                    IntSectionPos pos = IntSectionPos.getSectionPos(key);
                    if (pos.y < minSecY || pos.y > maxSecY || pos.z < minSecZ || pos.z > maxSecZ) continue;
                    EntitySection<T> section = this.sections.get(key);
                    if (section != null && !section.isEmpty() && section.getStatus().isAccessible() &&
                            consumer.accept(section).shouldAbort()) {
                        return;
                    }
                }
            }
        }

        @Overwrite
        private LongSortedSet getChunkSections(int cx, int cz) {
            LongAVLTreeSet result = new LongAVLTreeSet();
            LongIterator it = this.sectionIds.iterator();
            while (it.hasNext()) {
                long key = it.nextLong();
                IntSectionPos sp = IntSectionPos.getSectionPos(key);
                if (sp.x == cx && sp.z == cz) {
                    result.add(key);
                }
            }
            return result;
        }

        @Overwrite
        private static long getChunkKeyFromSectionKey(long pos) {
            IntSectionPos sp = IntSectionPos.getSectionPos(pos);
            return ChunkPos.pack(sp.x, sp.z);
        }
    }

    @Mixin(ChunkPos.class)
    public static abstract class ChunkPosMixin {
        @Shadow public int x;
        @Shadow public int z;

        @Unique
        private static int shiftToBlockCoord(int coord) {
            long val = (long) coord << 4;
            if (val > Integer.MAX_VALUE - 15) return Integer.MAX_VALUE - 15;
            if (val < Integer.MIN_VALUE + 15) return Integer.MIN_VALUE + 15;
            return (int) val;
        }

        @Overwrite
        public int getMinBlockX() { return shiftToBlockCoord(this.x); }

        @Overwrite
        public int getMinBlockZ() { return shiftToBlockCoord(this.z); }

        @Overwrite
        public static boolean isValid(int x, int z) { return true; }
    }

    @Mixin(BlockPos.MutableBlockPos.class)
    public static class BlockPosMutableBlockPosMixin {
        @Overwrite
        public BlockPos.MutableBlockPos set(long packedPos) {
            IntBlockPos pos = IntBlockPos.getBlockPos(packedPos);
            BlockPos.MutableBlockPos self = (BlockPos.MutableBlockPos) (Object) this;
            return self.set(pos.x, pos.y, pos.z);
        }
    }
}
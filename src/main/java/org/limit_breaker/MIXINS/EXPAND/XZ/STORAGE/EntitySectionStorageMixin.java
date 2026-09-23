package org.limit_breaker.MIXINS.EXPAND.XZ.STORAGE;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import net.minecraft.core.SectionPos;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.phys.AABB;
import org.limit_breaker.UTILITIES.POSITION.IntSectionPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntitySectionStorage.class)
public abstract class EntitySectionStorageMixin<T extends EntityAccess> {

    // 正確 Shadow 原版真實的實體儲存容器，絕不使用 @Unique new 空地圖！
    @Shadow @Final private Long2ObjectMap<EntitySection<T>> sections;
    @Shadow @Final private LongSortedSet sectionIds;

    /**
     * 遍歷可存取實體區段：修復 subSet 溢位崩潰，並精確傳遞實體碰撞箱以恢復拾取與攻擊判定
     */
    @Overwrite
    public void forEachAccessibleNonEmptySection(AABB bounds, AbortableIterationConsumer<EntitySection<T>> consumer) {
        int minSecX = SectionPos.posToSectionCoord(bounds.minX - 2.0);
        int maxSecX = SectionPos.posToSectionCoord(bounds.maxX + 2.0);
        int minSecY = SectionPos.posToSectionCoord(bounds.minY - 4.0);
        int maxSecY = SectionPos.posToSectionCoord(bounds.maxY + 0.0);
        int minSecZ = SectionPos.posToSectionCoord(bounds.minZ - 2.0);
        int maxSecZ = SectionPos.posToSectionCoord(bounds.maxZ + 2.0);

        for (Long2ObjectMap.Entry<EntitySection<T>> entry : this.sections.long2ObjectEntrySet()) {
            EntitySection<T> section = entry.getValue();
            if (section == null || section.isEmpty() || !section.getStatus().isAccessible()) {
                continue;
            }

            long key = entry.getLongKey();
            IntSectionPos pos = IntSectionPos.getSectionPos(key);

            if (pos.x >= minSecX && pos.x <= maxSecX &&
                    pos.y >= minSecY && pos.y <= maxSecY &&
                    pos.z >= minSecZ && pos.z <= maxSecZ) {

                if (consumer.accept(section).shouldAbort()) {
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
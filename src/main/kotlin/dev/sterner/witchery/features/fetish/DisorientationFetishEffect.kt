package dev.sterner.witchery.features.fetish

import dev.sterner.witchery.MobAccessor
import dev.sterner.witchery.core.api.FetishEffect
import dev.sterner.witchery.content.block.effigy.EffigyBlockEntity
import dev.sterner.witchery.content.item.TaglockItem
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.level.Level

class DisorientationFetishEffect : FetishEffect() {


    private fun applyDisorientation(
        level: Level,
        blockEntity: EffigyBlockEntity,
        pos: BlockPos,
        targets: List<LivingEntity>,
        invert: Boolean
    ) {
        if (level.isClientSide) return

        val taggedUUIDs = blockEntity.taglocks.mapNotNull {
            TaglockItem.getLivingEntity(level, it)?.uuid
        }.toSet()

        for (entity in targets) {
            val shouldAffect = if (invert) entity.uuid !in taggedUUIDs else entity.uuid in taggedUUIDs
            if (!shouldAffect) continue

            if (entity is Mob) {
                val accessor = entity as? MobAccessor ?: continue
                if (accessor.`witchery$canBeDisoriented`()) {
                    accessor.`witchery$setDisorientedActive`(true)
                }
            }
        }
    }

    override fun onKnownEntityTick(level: Level, blockEntity: EffigyBlockEntity, pos: BlockPos, known: LivingEntity) {
        applyDisorientation(level, blockEntity, pos, listOf(known), invert = false)
    }

    override fun onUnknownEntityTick(
        level: Level,
        blockEntity: EffigyBlockEntity,
        pos: BlockPos,
        nearby: List<LivingEntity>
    ) {
        applyDisorientation(level, blockEntity, pos, nearby, invert = true)
    }
}

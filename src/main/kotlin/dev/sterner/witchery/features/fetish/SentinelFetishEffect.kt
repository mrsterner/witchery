package dev.sterner.witchery.features.fetish

import dev.sterner.witchery.api.FetishEffect
import dev.sterner.witchery.block.effigy.EffigyBlockEntity
import dev.sterner.witchery.entity.SpectreEntity
import dev.sterner.witchery.item.TaglockItem
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import java.util.*

class SentinelFetishEffect : FetishEffect(2) {

    private fun summonSpectreAtTarget(
        level: ServerLevel,
        blockEntity: EffigyBlockEntity,
        pos: BlockPos,
        target: LivingEntity,
        ignored: Set<UUID>
    ) {
        if (blockEntity.deployedSpectreCount >= 3) return

        val spectre = SpectreEntity.summonSpectre(level, pos, ignoredUUIDs = ignored)
        spectre.target = target
        blockEntity.deployedSpectreCount += 1
    }

    override fun onKnownPlayerNearbyTick(
        level: Level,
        blockEntity: EffigyBlockEntity,
        pos: BlockPos,
        taggedPlayer: Player
    ) {
        if (level !is ServerLevel) return
        summonSpectreAtTarget(level, blockEntity, pos, taggedPlayer, emptySet())
    }

    override fun onKnownEntityTick(
        level: Level,
        blockEntity: EffigyBlockEntity,
        pos: BlockPos,
        known: LivingEntity
    ) {
        if (level !is ServerLevel) return
        summonSpectreAtTarget(level, blockEntity, pos, known, emptySet())
    }

    override fun onUnknownPlayerNearbyTick(
        level: Level,
        blockEntity: EffigyBlockEntity,
        pos: BlockPos,
        nearby: List<Player>
    ) {
        handleUnknowns(level, blockEntity, pos, nearby)
    }

    override fun onUnknownEntityTick(
        level: Level,
        blockEntity: EffigyBlockEntity,
        pos: BlockPos,
        nearby: List<LivingEntity>
    ) {
        handleUnknowns(level, blockEntity, pos, nearby)
    }

    private fun handleUnknowns(
        level: Level,
        blockEntity: EffigyBlockEntity,
        pos: BlockPos,
        nearby: List<LivingEntity>
    ) {
        if (level !is ServerLevel || blockEntity.deployedSpectreCount >= 3) return

        val taggedUUIDs = blockEntity.taglocks.mapNotNull {
            TaglockItem.getLivingEntity(level, it)?.uuid
        }.toSet()

        val targets = nearby.filter { it.uuid !in taggedUUIDs }

        for (target in targets) {
            if (blockEntity.deployedSpectreCount >= 3) break
            summonSpectreAtTarget(level, blockEntity, pos, target, taggedUUIDs)
        }
    }
}

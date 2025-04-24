package dev.sterner.witchery.api

import dev.sterner.witchery.block.effigy.EffigyBlockEntity
import dev.sterner.witchery.block.effigy.EffigyState
import dev.sterner.witchery.item.TaglockItem
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB

open class FetishEffect(var rangeMod: Int = 1) {

    open fun onTickEffect(level: Level, blockEntity: EffigyBlockEntity, state: EffigyState?, pos: BlockPos, taglock: NonNullList<ItemStack>, tickRate: Int = 20) {
        if (level.gameTime % tickRate == 0L) {
            when (state) {
                EffigyState.IDLE -> {}
                EffigyState.PLAYER_UNKNOWN_TAGLOCK_NEAR -> checkUnknownPlayerNearby(level, blockEntity, pos, taglock)
                EffigyState.PLAYER_KNOWN_TAGLOCK_NEAR -> checkKnownPlayerNearby(level, blockEntity, pos, taglock)
                EffigyState.ENTITY_UNKNOWN_TAGLOCK_NEAR -> checkUnknownEntityNearby(level, blockEntity, pos, taglock)
                EffigyState.ANY_KNOWN_TAGLOCK_ENTITY_NEAR -> checkAnyKnownEntityNearby(level, blockEntity, pos, taglock)
                EffigyState.ALL_KNOWN_TAGLOCK_ENTITY_AWAY -> checkAllKnownEntitiesAway(level, blockEntity, pos, taglock)
                null -> {}
            }
        }
    }

    private fun checkUnknownPlayerNearby(level: Level, blockEntity: EffigyBlockEntity,pos: BlockPos, taglocks: NonNullList<ItemStack>) {
        for (taglock in taglocks) {
            val taggedPlayer = TaglockItem.getPlayer(level, taglock)
            val nearby = level.getEntitiesOfClass(Player::class.java, AABB.ofSize(pos.center, 16.0 * rangeMod, 8.0 * rangeMod, 16.0 * rangeMod)) {
                it != taggedPlayer
            }
            if (nearby.isNotEmpty()) {
                onUnknownPlayerNearbyTick(level, blockEntity, pos, nearby)
            }
        }
    }

    open fun onUnknownPlayerNearbyTick(level: Level, blockEntity: EffigyBlockEntity,pos: BlockPos, nearby: List<Player>) {
        all(level, blockEntity, pos, nearby)
    }

    private fun checkKnownPlayerNearby(level: Level, blockEntity: EffigyBlockEntity,pos: BlockPos, taglocks: NonNullList<ItemStack>) {
        for (taglock in taglocks) {
            val taggedPlayer = TaglockItem.getPlayer(level, taglock)
            if (taggedPlayer != null && taggedPlayer.distanceToSqr(pos.center) < 64.0 * rangeMod) {
                onKnownPlayerNearbyTick(level, blockEntity, pos, taggedPlayer)
            }
        }
    }

    open fun onKnownPlayerNearbyTick(level: Level, blockEntity: EffigyBlockEntity,pos: BlockPos, taggedPlayer: Player) {
        all(level, blockEntity, pos, listOf(taggedPlayer))
    }

    private fun checkUnknownEntityNearby(level: Level, blockEntity: EffigyBlockEntity,pos: BlockPos, taglocks: NonNullList<ItemStack>) {
        for (taglock in taglocks) {
            val taggedEntity = TaglockItem.getLivingEntity(level, taglock)
            val nearby = level.getEntitiesOfClass(LivingEntity::class.java, AABB.ofSize(pos.center, 16.0 * rangeMod, 8.0 * rangeMod, 16.0 * rangeMod)) {
                it != taggedEntity
            }
            if (nearby.isNotEmpty()) {
                onUnknownEntityTick(level, blockEntity, pos, nearby)
            }
        }
    }

    open fun onUnknownEntityTick(level: Level, blockEntity: EffigyBlockEntity,pos: BlockPos, nearby: List<LivingEntity>) {
        all(level, blockEntity, pos, nearby)
    }

    private fun checkAnyKnownEntityNearby(level: Level, blockEntity: EffigyBlockEntity, pos: BlockPos, taglocks: NonNullList<ItemStack>) {
        for (taglock in taglocks) {
            val known = TaglockItem.getLivingEntity(level, taglock)
            if (known != null && known.distanceToSqr(pos.center) < 64.0 * rangeMod) {
                onKnownEntityTick(level, blockEntity, pos, known)
            }
        }
    }

    open fun onKnownEntityTick(level: Level, blockEntity: EffigyBlockEntity, pos: BlockPos, known: LivingEntity) {
        all(level, blockEntity, pos, listOf(known))
    }

    private fun checkAllKnownEntitiesAway(level: Level, blockEntity: EffigyBlockEntity, pos: BlockPos, taglocks: NonNullList<ItemStack>) {
        for (taglock in taglocks) {
            val known = TaglockItem.getLivingEntity(level, taglock)
            if (known == null || known.distanceToSqr(pos.center) >= 64.0 * rangeMod) {
                onKnownEntityAwayTick(level,  blockEntity,pos)
            }
        }
    }

    open fun onKnownEntityAwayTick(level: Level, blockEntity: EffigyBlockEntity, pos: BlockPos) {
        all(level, blockEntity, pos, listOf())
    }

    open fun all(level: Level, blockEntity: EffigyBlockEntity, pos: BlockPos, list: List<LivingEntity>) {

    }
}

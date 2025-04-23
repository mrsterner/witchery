package dev.sterner.witchery.api

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

open class FetishEffect {

    open fun onTickEffect(level: Level, state: EffigyState?, pos: BlockPos, taglock: NonNullList<ItemStack>) {
        when (state) {
            EffigyState.IDLE -> {}
            EffigyState.PLAYER_UNKNOWN_TAGLOCK_NEAR -> checkUnknownPlayerNearby(level, pos, taglock)
            EffigyState.PLAYER_KNOWN_TAGLOCK_NEAR -> checkKnownPlayerNearby(level, pos, taglock)
            EffigyState.ENTITY_UNKNOWN_TAGLOCK_NEAR -> checkUnknownEntityNearby(level, pos, taglock)
            EffigyState.ANY_KNOWN_TAGLOCK_ENTITY_NEAR -> checkAnyKnownEntityNearby(level, pos, taglock)
            EffigyState.ALL_KNOWN_TAGLOCK_ENTITY_AWAY -> checkAllKnownEntitiesAway(level, pos, taglock)
            null -> {}
        }
    }

    private fun checkUnknownPlayerNearby(level: Level, pos: BlockPos, taglocks: NonNullList<ItemStack>) {
        for (taglock in taglocks) {
            val taggedPlayer = TaglockItem.getPlayer(level, taglock)
            val nearby = level.getEntitiesOfClass(Player::class.java, AABB.ofSize(pos.center, 16.0, 8.0, 16.0)) {
                it != taggedPlayer
            }
            if (nearby.isNotEmpty()) {
                onUnknownPlayerNearbyTick(level, pos, nearby)
            }
        }
    }

    open fun onUnknownPlayerNearbyTick(level: Level, pos: BlockPos, nearby: List<Player>) {

    }

    private fun checkKnownPlayerNearby(level: Level, pos: BlockPos, taglocks: NonNullList<ItemStack>) {
        for (taglock in taglocks) {
            val taggedPlayer = TaglockItem.getPlayer(level, taglock)
            if (taggedPlayer != null && taggedPlayer.distanceToSqr(pos.center) < 64.0) {
                onKnownPlayerNearbyTick(level, pos, taggedPlayer)
            }
        }
    }

    open fun onKnownPlayerNearbyTick(level: Level, pos: BlockPos, taggedPlayer: Player) {

    }

    private fun checkUnknownEntityNearby(level: Level, pos: BlockPos, taglocks: NonNullList<ItemStack>) {
        for (taglock in taglocks) {
            val taggedEntity = TaglockItem.getLivingEntity(level, taglock)
            val nearby = level.getEntitiesOfClass(LivingEntity::class.java, AABB.ofSize(pos.center, 16.0, 8.0, 16.0)) {
                it != taggedEntity
            }
            if (nearby.isNotEmpty()) {
                onUnknownEntityTick(level, pos, nearby)
            }
        }
    }

    open fun onUnknownEntityTick(level: Level, pos: BlockPos, nearby: List<LivingEntity>) {

    }

    private fun checkAnyKnownEntityNearby(level: Level, pos: BlockPos, taglocks: NonNullList<ItemStack>) {
        for (taglock in taglocks) {
            val known = TaglockItem.getLivingEntity(level, taglock)
            if (known != null && known.distanceToSqr(pos.center) < 64.0) {
                onKnownEntityTick(level, pos, known)
            }
        }
    }

    open fun onKnownEntityTick(level: Level, pos: BlockPos, known: LivingEntity) {

    }

    private fun checkAllKnownEntitiesAway(level: Level, pos: BlockPos, taglocks: NonNullList<ItemStack>) {
        for (taglock in taglocks) {
            val known = TaglockItem.getLivingEntity(level, taglock)
            if (known == null || known.distanceToSqr(pos.center) >= 256.0) {
                onKnownEntityAwayTick(level, pos)
            }
        }
    }

    open fun onKnownEntityAwayTick(level: Level, pos: BlockPos) {

    }
}

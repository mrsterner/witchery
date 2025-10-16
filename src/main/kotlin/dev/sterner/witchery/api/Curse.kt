package dev.sterner.witchery.api

import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

open class Curse {

    protected fun getEffectivenessMultiplier(player: Player): Float {
        return if (WitcheryApi.isWitchy(player)) {
            1.0f
        } else {
            0.3f
        }
    }

    open fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {

    }

    open fun onHurt(level: Level, player: Player, damageSource: DamageSource?, fl: Float, catBoosted: Boolean) {

    }

    open fun breakBlock(level: Level, player: Player, blockState: BlockState, catBoosted: Boolean) {

    }

    open fun placeBlock(level: Level, entity: Player, blockState: BlockState?, catBoosted: Boolean) {

    }

    open fun attackEntity(
        level: Level,
        player: Player,
        target: Entity,
        catBoosted: Boolean
    ) {

    }

    open fun onRemoved(level: Level, player: Player, catBoosted: Boolean) {

    }

    open fun onAdded(level: Level, player: Player, catBoosted: Boolean) {

    }
}
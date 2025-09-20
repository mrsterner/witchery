package dev.sterner.witchery.api

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.EntityHitResult

open class Curse {

    open fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {

    }

    open fun onHurt(level: Level, livingEntity: Player, damageSource: DamageSource?, fl: Float, catBoosted: Boolean) {

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
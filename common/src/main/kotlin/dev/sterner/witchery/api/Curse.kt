package dev.sterner.witchery.api

import dev.sterner.witchery.Witchery
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.EntityHitResult

open class Curse(val id: ResourceLocation) {

    constructor(id: String): this(Witchery.id(id))

    open fun onTickCurse(level: Level, player: Player) {

    }

    open fun onHurt(level: Level, livingEntity: Player, damageSource: DamageSource?, fl: Float) {

    }

    open fun breakBlock(level: Level, serverPlayer: ServerPlayer, blockState: BlockState) {

    }

    open fun placeBlock(level: Level, entity: Player, blockState: BlockState?) {

    }

    open fun attackEntity(level: Level, player: Player, target: net.minecraft.world.entity.Entity, entityHitResult: EntityHitResult) {

    }
}
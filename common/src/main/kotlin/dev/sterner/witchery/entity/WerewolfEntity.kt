package dev.sterner.witchery.entity

import dev.sterner.witchery.block.werewolf_altar.WerewolfAltarBlockEntity
import dev.sterner.witchery.handler.werewolf.WerewolfEventHandler
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import java.util.stream.Stream

class WerewolfEntity(level: Level) : PathfinderMob(WitcheryEntityTypes.WEREWOLF.get(), level) {

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
        }
    }

    override fun doHurtTarget(target: Entity): Boolean {
        if (target is Player && level() is ServerLevel) {
            val serverLevel = level() as ServerLevel
            val box = this.boundingBox.inflate(2.0)

            val stream: Stream<BlockPos> = BlockPos.betweenClosedStream(box)

            val hasAltar = stream.anyMatch { pos -> serverLevel.getBlockEntity(pos) is WerewolfAltarBlockEntity }
            if (hasAltar) {
                WerewolfEventHandler.infectPlayer(target as Player)
            }
        }
        return super.doHurtTarget(target)
    }
}
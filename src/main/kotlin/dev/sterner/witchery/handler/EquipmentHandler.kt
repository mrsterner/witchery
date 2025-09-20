@file:Suppress("DEPRECATION")

package dev.sterner.witchery.handler

import dev.sterner.witchery.data_attachment.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.data_attachment.infusion.InfusionType
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player


object EquipmentHandler {

    fun registerEvents() {
        EntityEvent.LIVING_HURT.register(EquipmentHandler::babaYagaHit)
    }

    private fun babaYagaHit(livingEntity: LivingEntity?, damageSource: DamageSource?, fl: Float): EventResult? {
        if (livingEntity is Player && livingEntity.getItemBySlot(EquipmentSlot.HEAD)
                .`is`(WitcheryItems.BABA_YAGAS_HAT.get())
        ) {
            if (InfusionPlayerAttachment.getPlayerInfusion(livingEntity).type != InfusionType.NONE) {
                val level = livingEntity.level()

                if (level.random.nextFloat() < 0.2f && fl > 0) {
                    if (attemptTeleport(livingEntity)) {

                        level.playSound(
                            null,
                            livingEntity.getX(),
                            livingEntity.getY(),
                            livingEntity.getZ(),
                            SoundEvents.CHORUS_FRUIT_TELEPORT,
                            SoundSource.PLAYERS,
                            0.5f, 1f
                        )
                        livingEntity.resetFallDistance()

                        return EventResult.interruptFalse()
                    }
                }
            }
        }

        return EventResult.pass()
    }

    private fun attemptTeleport(player: Player, distance: Double = 20.0): Boolean {
        val playerPos = player.onPos
        val random = player.level().random

        for (i in 0..15) {
            val targetX = playerPos.x + Mth.nextDouble(random, -distance, distance)
            val targetY = playerPos.y + Mth.nextDouble(random, -distance / 2f, distance / 2f)
            val targetZ = playerPos.z + Mth.nextDouble(random, -distance, distance)

            val mutable: BlockPos.MutableBlockPos =
                BlockPos.MutableBlockPos(targetX.toInt(), targetY.toInt(), targetZ.toInt())

            while (mutable.y > 0 && !player.level().getBlockState(mutable).blocksMotion()) {
                mutable.move(Direction.DOWN)
            }

            val blockBelow = player.level().getBlockState(mutable)
            val blockAbove = player.level().getBlockState(mutable.above())

            if (blockBelow.blocksMotion() && !blockAbove.blocksMotion()) {
                player.teleportTo(mutable.x + 0.5, mutable.y + 1.0, mutable.z + 0.5)
                return true
            }
        }
        return false
    }
}
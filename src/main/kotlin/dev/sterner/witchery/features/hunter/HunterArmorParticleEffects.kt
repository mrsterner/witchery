package dev.sterner.witchery.features.hunter

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

object HunterArmorParticleEffects {

    fun spawnProtectionParticles(player: Player, type: ProtectionType) {
        if (player.level() !is ServerLevel) return
        val level = player.level() as ServerLevel

        val position = player.position()
        val particleCount = when (type) {
            ProtectionType.POTION_REDUCTION -> 15
            ProtectionType.CURSE_REDUCTION -> 20
            ProtectionType.POPPET_DAMAGE -> 25
            ProtectionType.MAGIC_RESISTANCE -> 20
            ProtectionType.CURSE_REFLECTION -> 30
        }

        for (i in 0 until particleCount) {
            val offsetX = (level.random.nextDouble() - 0.5) * 1.5
            val offsetY = level.random.nextDouble() * 2.0
            val offsetZ = (level.random.nextDouble() - 0.5) * 1.5

            val velocityX = (level.random.nextDouble() - 0.5) * 0.1
            val velocityY = level.random.nextDouble() * 0.2
            val velocityZ = (level.random.nextDouble() - 0.5) * 0.1

            level.sendParticles(
                when (type) {
                    ProtectionType.CURSE_REFLECTION -> ParticleTypes.ENCHANTED_HIT
                    ProtectionType.MAGIC_RESISTANCE -> ParticleTypes.CRIT
                    else -> ParticleTypes.WAX_ON
                },
                position.x + offsetX,
                position.y + offsetY,
                position.z + offsetZ,
                1,
                velocityX,
                velocityY,
                velocityZ,
                0.1
            )
        }

        if (HunterArmorDefenseHandler.getHunterArmorPieceCount(player) > 0) {
            spawnShieldEffect(level, position)
        }

        playSoundEffect(level, player, type)
    }

    private fun spawnShieldEffect(level: ServerLevel, position: Vec3) {
        val radius = 1.0
        val points = 20

        for (i in 0 until points) {
            val angle = (i.toDouble() / points) * Math.PI * 2
            val x = position.x + Math.cos(angle) * radius
            val z = position.z + Math.sin(angle) * radius

            for (j in 0..3) {
                val y = position.y + (j * 0.5)
                level.sendParticles(
                    ParticleTypes.WAX_ON,
                    x, y, z,
                    1,
                    0.0, 0.1, 0.0,
                    0.05
                )
            }
        }
    }

    private fun playSoundEffect(level: ServerLevel, player: Player, type: ProtectionType) {
        val sound = when (type) {
            ProtectionType.CURSE_REFLECTION -> SoundEvents.SHIELD_BLOCK
            ProtectionType.MAGIC_RESISTANCE -> SoundEvents.ENCHANTMENT_TABLE_USE
            ProtectionType.POPPET_DAMAGE -> SoundEvents.ARMOR_EQUIP_IRON.value()
            ProtectionType.POTION_REDUCTION -> SoundEvents.BREWING_STAND_BREW
            ProtectionType.CURSE_REDUCTION -> SoundEvents.TOTEM_USE
        }

        level.playSound(
            null,
            player.blockPosition(),
            sound,
            SoundSource.PLAYERS,
            0.5f,
            1.0f + (level.random.nextFloat() - 0.5f) * 0.2f
        )
    }

    fun spawnCurseReflectionEffect(player: Player, target: ServerPlayer) {
        if (player.level() !is ServerLevel) return
        val level = player.level() as ServerLevel

        val startPos = player.position().add(0.0, player.eyeHeight.toDouble(), 0.0)
        val endPos = target.position().add(0.0, target.eyeHeight.toDouble(), 0.0)

        val direction = endPos.subtract(startPos).normalize()
        val distance = startPos.distanceTo(endPos)
        val steps = (distance * 5).toInt().coerceAtLeast(10)

        for (i in 0..steps) {
            val progress = i.toDouble() / steps
            val particlePos = startPos.add(direction.scale(distance * progress))

            level.sendParticles(
                ParticleTypes.WITCH,
                particlePos.x,
                particlePos.y,
                particlePos.z,
                2,
                0.1, 0.1, 0.1,
                0.05
            )

            level.sendParticles(
                ParticleTypes.PORTAL,
                particlePos.x,
                particlePos.y,
                particlePos.z,
                1,
                0.05, 0.05, 0.05,
                0.1
            )
        }

        for (i in 0..20) {
            val offsetX = (level.random.nextDouble() - 0.5) * 0.5
            val offsetY = (level.random.nextDouble() - 0.5) * 0.5
            val offsetZ = (level.random.nextDouble() - 0.5) * 0.5

            level.sendParticles(
                ParticleTypes.SMOKE,
                endPos.x + offsetX,
                endPos.y + offsetY,
                endPos.z + offsetZ,
                1,
                0.0, 0.1, 0.0,
                0.05
            )
        }

        level.playSound(
            null,
            target.blockPosition(),
            SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(),
            SoundSource.PLAYERS,
            1.0f,
            0.8f
        )
    }

    enum class ProtectionType {
        POTION_REDUCTION,
        CURSE_REDUCTION,
        POPPET_DAMAGE,
        MAGIC_RESISTANCE,
        CURSE_REFLECTION
    }
}
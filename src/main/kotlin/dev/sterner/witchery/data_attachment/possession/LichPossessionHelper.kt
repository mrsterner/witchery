package dev.sterner.witchery.data_attachment.possession


import dev.sterner.witchery.api.InventorySlots
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.entity.player_shell.SoulShellPlayerEntity
import dev.sterner.witchery.handler.ability.AbilityCooldownManager
import dev.sterner.witchery.handler.affliction.AfflictionTypes
import dev.sterner.witchery.handler.affliction.LichdomAbility
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.core.particles.ParticleTypes

object LichPossessionHelper {

    fun attemptPossession(player: ServerPlayer, target: Entity): Boolean {
        val afflictionData = AfflictionPlayerAttachment.getData(player)

        if (!afflictionData.isSoulForm()) {
            return false
        }

        when (target) {
            is SoulShellPlayerEntity -> {
                val shellUUID = target.getOriginalUUID().orElse(null)
                if (shellUUID == player.uuid) {
                    mergeSoulWithShell(player, target)
                    AbilityCooldownManager.startCooldown(player, LichdomAbility.SOUL_FORM)
                    return true
                }
            }

            is Mob -> {
                if (canPossessEntity(player, target)) {
                    return startPossession(player, target)
                }
            }
        }

        return false
    }

    private fun canPossessEntity(player: ServerPlayer, target: Mob): Boolean {
        val lichLevel = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.LICHDOM)

        val canPossess = when {
            target.type.`is`(net.minecraft.tags.EntityTypeTags.UNDEAD) -> lichLevel >= 6
            target.type.`is`(net.minecraft.tags.EntityTypeTags.ILLAGER) -> lichLevel >= 8
            target.type.`is`(net.minecraft.tags.EntityTypeTags.RAIDERS) -> lichLevel >= 10
            else -> lichLevel >= 12 && target.maxHealth <= player.maxHealth * 2
        }

        return canPossess && target.health > 0 && !target.isRemoved
    }

    private fun startPossession(player: ServerPlayer, target: Mob): Boolean {
        val success = PossessionManager.startPossessing(player, target)

        if (success) {
            AfflictionPlayerAttachment.batchUpdate(player) {
                withSoulForm(false)
            }

            SoulShellPlayerEntity.disableFlight(player)
            InventorySlots.unlockAll(player)

            player.level().playSound(
                null,
                target.x,
                target.y,
                target.z,
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                1.0f,
                0.5f
            )

            val serverLevel = player.level() as net.minecraft.server.level.ServerLevel
            serverLevel.sendParticles(
                ParticleTypes.PORTAL,
                target.x,
                target.y + 1,
                target.z,
                30,
                0.5, 0.5, 0.5,
                0.1
            )

            AbilityCooldownManager.startCooldown(player, LichdomAbility.SOUL_FORM)
        }

        return success
    }

    private fun mergeSoulWithShell(player: ServerPlayer, shell: SoulShellPlayerEntity) {
        SoulShellPlayerEntity.replaceWithPlayer(player, shell)

        player.teleportTo(shell.x, shell.y, shell.z)

        AfflictionPlayerAttachment.batchUpdate(player) {
            withSoulForm(false)
        }

        SoulShellPlayerEntity.disableFlight(player)
        InventorySlots.unlockAll(player)
        player.onUpdateAbilities()

        shell.discard()

        player.level().playSound(
            null,
            player.x,
            player.y,
            player.z,
            SoundEvents.SOUL_ESCAPE,
            SoundSource.PLAYERS,
            1.0f,
            1.5f
        )

        val serverLevel = player.level() as net.minecraft.server.level.ServerLevel
        serverLevel.sendParticles(
            ParticleTypes.SOUL,
            player.x,
            player.y + 1,
            player.z,
            20,
            0.5, 0.5, 0.5,
            0.1
        )
    }

    fun exitPossession(player: ServerPlayer) {
        val host = PossessionManager.getHost(player)
        if (host != null) {
            PossessionManager.stopPossessing(player, false)

            activateSoulFormFromPossession(player)

            host.hurt(host.damageSources().magic(), host.maxHealth * 0.5f)

            player.level().playSound(
                null,
                host.x,
                host.y,
                host.z,
                SoundEvents.SCULK_SHRIEKER_SHRIEK,
                SoundSource.PLAYERS,
                1.0f,
                0.7f
            )
        }
    }

    private fun activateSoulFormFromPossession(player: ServerPlayer) {
        SoulShellPlayerEntity.enableFlight(player)
        player.abilities.flying = true

        val random = player.random
        val upwardVelocity = 0.2 + random.nextDouble() * 0.1
        player.deltaMovement = player.deltaMovement.add(
            (random.nextDouble() - 0.5) * 0.1,
            upwardVelocity,
            (random.nextDouble() - 0.5) * 0.1
        )
        player.hurtMarked = true

        InventorySlots.lockAll(player)
        player.onUpdateAbilities()

        AfflictionPlayerAttachment.batchUpdate(player) {
            withSoulForm(true)
        }

        val serverLevel = player.level() as net.minecraft.server.level.ServerLevel
        serverLevel.sendParticles(
            ParticleTypes.SOUL_FIRE_FLAME,
            player.x,
            player.y + 1,
            player.z,
            15,
            0.3, 0.3, 0.3,
            0.05
        )
    }
}
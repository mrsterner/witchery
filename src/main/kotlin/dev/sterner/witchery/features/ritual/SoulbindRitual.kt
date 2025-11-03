package dev.sterner.witchery.features.ritual

import dev.sterner.witchery.core.api.InventorySlots
import dev.sterner.witchery.core.api.Ritual
import dev.sterner.witchery.core.api.entity.PlayerShellEntity
import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.content.entity.player_shell.SoulShellPlayerEntity
import dev.sterner.witchery.features.affliction.lich.LichdomSpecificEventHandler
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.possession.PossessionComponentAttachment
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB

class SoulbindRitual : Ritual("soulbind") {

    override fun onStartRitual(
        level: Level,
        blockPos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity
    ): Boolean {
        if (level.isClientSide) return true

        val player = level.server?.playerList?.getPlayer(goldenChalkBlockEntity.targetPlayer!!) ?: return false

        val data = AfflictionPlayerAttachment.getData(player)

        if (!data.isSoulForm() && !data.isVagrant()) {
            return false
        }

        if (data.isVagrant()) {
            val possessionComponent = PossessionComponentAttachment.get(player)
            possessionComponent.stopPossessing(false)
        }

        val shell = level.getEntitiesOfClass(
            PlayerShellEntity::class.java,
            AABB(blockPos).inflate(50.0)
        ) {
            it is SoulShellPlayerEntity && it.getOriginalUUID().orElse(null) == player.uuid
        }.firstOrNull()

        if (shell != null && shell is SoulShellPlayerEntity) {
            LichdomSpecificEventHandler.returnToShell(player, shell)
        } else {
            AfflictionPlayerAttachment.smartUpdate(player) {
                withSoulForm(false).withVagrant(false)
            }

            SoulShellPlayerEntity.disableFlight(player)
            player.abilities.flying = false
            player.onUpdateAbilities()

            InventorySlots.unlockAll(player)
        }

        level.playSound(
            null,
            player.x, player.y, player.z,
            SoundEvents.SOUL_ESCAPE.value(),
            SoundSource.PLAYERS,
            1.0f, 0.5f
        )

        (level as ServerLevel).sendParticles(
            ParticleTypes.SOUL,
            player.x, player.y + 1, player.z,
            30,
            0.5, 0.5, 0.5,
            0.1
        )
        return true
    }
}
package dev.sterner.witchery.entity.player_shell

import dev.sterner.witchery.api.entity.PlayerShellData
import dev.sterner.witchery.api.entity.PlayerShellEntity
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.mixin.PlayerInvoker
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.core.NonNullList
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Containers
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class SoulShellPlayerEntity(level: Level) : PlayerShellEntity(WitcheryEntityTypes.SOUL_SHELL_PLAYER.get(), level) {

    init {
        shellType = ShellType.SOUL
    }

    override fun isPickable(): Boolean {
        return true
    }

    override fun mergeSoulWithShell(player: Player) {
        if (player is ServerPlayer) {
            replaceWithPlayer(player, this)

            player.abilities.mayfly = false
            player.abilities.flying = false
            player.onUpdateAbilities()

            AfflictionPlayerAttachment.batchUpdate(player) {
                withSoulForm(false)
            }

            player.level().playSound(
                null,
                player.x,
                player.y,
                player.z,
                SoundEvents.EVOKER_CAST_SPELL,
                SoundSource.PLAYERS,
                1.0f,
                0.7f
            )

            player.removeEffect(MobEffects.GLOWING)
            player.removeEffect(MobEffects.INVISIBILITY)

            this.discard()
        }
    }

    companion object {

        fun replaceWithPlayer(player: Player, shellEntity: SoulShellPlayerEntity) {
            player.inventory.clearContent()

            insertOrDrop(player, shellEntity.data.mainInventory, player.inventory.items)
            insertOrDrop(player, shellEntity.data.armorInventory, player.inventory.armor)
            insertOrDrop(player, shellEntity.data.offHandInventory, player.inventory.offhand)

            shellEntity.discard()
        }

        private fun insertOrDrop(
            player: Player,
            inventory: NonNullList<ItemStack>,
            playerInv: NonNullList<ItemStack>
        ) {
            for (i in 0 until inventory.size) {
                val stack = inventory[i]
                if (stack.isEmpty) continue

                val playerStack = playerInv[i]
                if (playerStack.isEmpty) {
                    playerInv[i] = stack.copy()
                    inventory[i] = ItemStack.EMPTY
                } else {
                    Containers.dropItemStack(player.level(), player.x, player.y, player.z, stack.copy())
                    inventory[i] = ItemStack.EMPTY
                }
            }
        }
    }
}
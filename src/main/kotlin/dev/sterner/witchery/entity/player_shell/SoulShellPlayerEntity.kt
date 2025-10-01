package dev.sterner.witchery.entity.player_shell

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.InventorySlots
import dev.sterner.witchery.api.entity.PlayerShellEntity
import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment

import dev.sterner.witchery.payload.SpawnSleepingDeathParticleS2CPayload
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.core.NonNullList
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Containers
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.NeoForgeMod
import net.neoforged.neoforge.network.PacketDistributor

class SoulShellPlayerEntity(level: Level) : PlayerShellEntity(WitcheryEntityTypes.SOUL_SHELL_PLAYER.get(), level) {

    private var inventoryDropped = false

    init {
        shellType = ShellType.SOUL
    }

    override fun isPickable(): Boolean {
        return true
    }

    override fun tick() {
        super.tick()

        if (level() is ServerLevel) {
            val server = level() as ServerLevel

            if (server.gameTime % 100 == 0L) {
                val uuid = getOriginalUUID().orElse(null)
                if (uuid == null) {
                    handleShellDeath()
                }
            }
        }
    }

    override fun handleShellDeath() {
        if (!inventoryDropped) {
            dropInventory()
            inventoryDropped = true
        }

        val originalUuid = getOriginalUUID().orElse(null)
        if (originalUuid != null && level() is ServerLevel) {
            val player = (level() as ServerLevel).getPlayerByUUID(originalUuid)
            if (player != null) {
                AfflictionPlayerAttachment.smartUpdate(player) {
                    withSoulForm(true).withVagrant(false)
                }

                if (player is ServerPlayer) {
                    enableFlight(player)
                    player.abilities.flying = true
                    player.onUpdateAbilities()
                }
            }
        }

        PacketDistributor.sendToPlayersTrackingEntity(
            this, SpawnSleepingDeathParticleS2CPayload(
                this.getRandomX(1.5),
                this.randomY,
                this.getRandomZ(1.5)
            )
        )

        this.remove(RemovalReason.KILLED)
    }

    override fun mergeSoulWithShell(player: Player) {
        if (player is ServerPlayer) {
            replaceWithPlayer(player, this)

            disableFlight(player)
            InventorySlots.unlockAll(player)
            player.abilities.flying = false
            player.onUpdateAbilities()

            AfflictionPlayerAttachment.smartUpdate(player) {
                withSoulForm(false).withVagrant(false)
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

            inventoryDropped = true
            this.discard()
        }
    }

    override fun remove(reason: RemovalReason) {
        if (!inventoryDropped && reason != RemovalReason.DISCARDED) {
            dropInventory()
            inventoryDropped = true
        }
        super.remove(reason)
    }

    companion object {

        private val LICH_FLIGHT_MODIFIER_ID = Witchery.id("lich_flight")

        fun enableFlight(player: ServerPlayer) {
            val flightAttribute = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT) ?: return

            if (flightAttribute.getModifier(LICH_FLIGHT_MODIFIER_ID) == null) {
                flightAttribute.addPermanentModifier(
                    AttributeModifier(
                        LICH_FLIGHT_MODIFIER_ID,
                        1.0,
                        AttributeModifier.Operation.ADD_VALUE
                    )
                )
            }
        }

        fun disableFlight(player: ServerPlayer) {
            val flightAttribute = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT) ?: return

            flightAttribute.getModifier(LICH_FLIGHT_MODIFIER_ID)?.let {
                flightAttribute.removeModifier(it)
            }
        }

        fun replaceWithPlayer(player: Player, shellEntity: SoulShellPlayerEntity) {
            player.inventory.clearContent()

            insertOrDrop(player, shellEntity.data.mainInventory, player.inventory.items)
            insertOrDrop(player, shellEntity.data.armorInventory, player.inventory.armor)
            insertOrDrop(player, shellEntity.data.offHandInventory, player.inventory.offhand)

            shellEntity.data.mainInventory.clear()
            shellEntity.data.armorInventory.clear()
            shellEntity.data.offHandInventory.clear()
            shellEntity.data.extraInventory.clear()
        }

        private fun insertOrDrop(
            player: Player,
            inventory: NonNullList<ItemStack>,
            playerInv: NonNullList<ItemStack>
        ) {
            for (i in 0 until inventory.size.coerceAtMost(playerInv.size)) {
                val stack = inventory[i]
                if (stack.isEmpty) continue

                val playerStack = playerInv[i]
                if (playerStack.isEmpty) {
                    playerInv[i] = stack.copy()
                } else {
                    Containers.dropItemStack(player.level(), player.x, player.y, player.z, stack.copy())
                }
                inventory[i] = ItemStack.EMPTY
            }
        }
    }
}
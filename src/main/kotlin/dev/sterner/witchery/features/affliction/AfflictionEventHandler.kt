package dev.sterner.witchery.features.affliction


import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.data_attachment.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.features.affliction.ability.AbilityCooldownManager
import dev.sterner.witchery.features.affliction.ability.AfflictionAbilityHandler
import dev.sterner.witchery.handler.BloodPoolHandler
import dev.sterner.witchery.payload.AfflictionAbilityUseC2SPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.network.PacketDistributor

object AfflictionEventHandler {

    private const val HUMAN_BLOOD_REGEN_RATE = 1000
    private const val HUMAN_BLOOD_REGEN_AMOUNT = 10


    @JvmStatic
    fun tick(player: Player?) {
        if (player !is ServerPlayer) return

        val vampLevel = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.VAMPIRISM)
        if (vampLevel < 1) {
            regenerateHumanBlood(player)
        }

        val any = AfflictionPlayerAttachment.getData(player).getAnyLevel()
        if (any) {
            AbilityCooldownManager.tick(player)
        }
    }

    private fun regenerateHumanBlood(player: ServerPlayer) {
        val humanBloodData = BloodPoolLivingEntityAttachment.getData(player)
        if (humanBloodData.bloodPool < humanBloodData.maxBlood) {
            if (player.tickCount % HUMAN_BLOOD_REGEN_RATE == 0) {
                BloodPoolHandler.increaseBlood(player, HUMAN_BLOOD_REGEN_AMOUNT)
            }
        }
    }

    @JvmStatic
    fun rightClickBlockAbility(
        event: PlayerInteractEvent.RightClickBlock,
        player: Player,
        interactionHand: InteractionHand
    ) {
        if (interactionHand == InteractionHand.OFF_HAND) return

        if (AfflictionAbilityHandler.useSelectedAbility(player)) {
            event.isCanceled = true
        }
    }

    @JvmStatic
    fun interactEntityWithAbility(
        event: PlayerInteractEvent.EntityInteract,
        player: Player?,
        entity: Entity?
    ) {
        if (player !is ServerPlayer || entity !is Entity) return

        val ability = AfflictionAbilityHandler.getSelectedAbility(player) ?: return

        if (AbilityCooldownManager.isOnCooldown(player, ability)) return

        if (ability.use(player, entity)) {
            event.isCanceled = true
        }
    }


    fun clientRightClickAbility(player: Player?, interactionHand: InteractionHand?): Boolean {
        if (player == null || interactionHand == InteractionHand.OFF_HAND) return false

        val abilities = AfflictionAbilityHandler.getAbilities(player)
        val index = AfflictionPlayerAttachment.getData(player).getAbilityIndex()

        val ability = abilities.getOrNull(index) ?: return false

        if (!ability.passive) {
            PacketDistributor.sendToServer(AfflictionAbilityUseC2SPayload(index))
            return true
        }

        return false
    }
}
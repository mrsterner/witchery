package dev.sterner.witchery.handler.affliction

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.InteractionEvent
import dev.architectury.event.events.common.TickEvent
import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.handler.BloodPoolHandler
import dev.sterner.witchery.handler.ability.AbilityCooldownManager
import dev.sterner.witchery.handler.affliction.AfflictionAbilityHandler.getAbilities
import dev.sterner.witchery.handler.affliction.AfflictionAbilityHandler.getSelectedAbility
import dev.sterner.witchery.handler.affliction.AfflictionAbilityHandler.useSelectedAbility
import dev.sterner.witchery.payload.AfflictionAbilityUseC2SPayload
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object AfflictionEventHandler {

    private const val HUMAN_BLOOD_REGEN_RATE = 1000
    private const val HUMAN_BLOOD_REGEN_AMOUNT = 10

    fun registerEvents() {
        TickEvent.PLAYER_PRE.register(::tick)

        InteractionEvent.INTERACT_ENTITY.register(::interactEntityWithAbility)
        InteractionEvent.RIGHT_CLICK_BLOCK.register(::rightClickBlockAbility)
    }

    @JvmStatic
    fun tick(player: Player?) {
        if (player !is ServerPlayer) return
        
        val vampLevel = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.VAMPIRISM)
        if (vampLevel < 1) {
            regenerateHumanBlood(player)
            return
        }

        AbilityCooldownManager.tick(player)
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
    fun rightClickBlockAbility(player: Player, interactionHand: InteractionHand, pos: BlockPos, dir: Direction): EventResult {
        if (interactionHand == InteractionHand.OFF_HAND) return EventResult.pass()

        if (useSelectedAbility(player)) {
            return EventResult.interruptTrue()
        }
        return EventResult.pass()
    }

    @JvmStatic
    fun interactEntityWithAbility(player: Player?, entity: Entity?, hand: InteractionHand): EventResult? {

        if (player !is ServerPlayer || entity !is LivingEntity) return EventResult.pass()

        val ability = getSelectedAbility(player) ?: return EventResult.pass()

        return if (ability.use(player, entity)) EventResult.interruptTrue() else EventResult.pass()
    }


    fun clientRightClickAbility(player: Player?, interactionHand: InteractionHand?): Boolean {
        if (player == null || interactionHand == InteractionHand.OFF_HAND) return false

        val abilities = getAbilities(player)
        val index = AfflictionPlayerAttachment.getData(player).getAbilityIndex()

        val ability = abilities.getOrNull(index) ?: return false

        if (!ability.passive) {
            NetworkManager.sendToServer(AfflictionAbilityUseC2SPayload(index))
            return true
        }

        return false
    }
}
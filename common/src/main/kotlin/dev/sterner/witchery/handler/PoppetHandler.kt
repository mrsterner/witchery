package dev.sterner.witchery.handler

import dev.architectury.event.EventResult
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.stats.Stats
import net.minecraft.tags.DamageTypeTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.gameevent.GameEvent

object PoppetHandler {

    fun deathProtectionPoppet(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity is Player) {
            if (deathProtectionHelper(livingEntity, damageSource)) {
                return EventResult.interruptFalse()
            }
        }
        return EventResult.pass()
    }

    private fun deathProtectionHelper(player: Player, damageSource: DamageSource?): Boolean {
        if (damageSource != null && damageSource.`is`(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false
        } else {
            var itemStack: ItemStack? = null

            for (interactionHand in InteractionHand.entries) {
                val itemStack2: ItemStack = player.getItemInHand(interactionHand)
                if (itemStack2.`is`(WitcheryItems.DEATH_PROTECTION_POPPET.get())) {
                    itemStack = itemStack2.copy()
                    itemStack2.shrink(1)
                    break
                }
            }

            if (itemStack != null) {
                player.health = 4.0f
                player.removeAllEffects()
                player.addEffect(MobEffectInstance(MobEffects.REGENERATION, 900, 1))
                player.addEffect(MobEffectInstance(MobEffects.ABSORPTION, 100, 1))
                player.addEffect(MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0))
                player.level().broadcastEntityEvent(player, 35.toByte())
            }

            return itemStack != null
        }
    }

    fun hasArmorProtectionPoppet(level: ServerLevel, player: ServerPlayer): Boolean {
        return false //TODO implement and consume poppet here
    }
}
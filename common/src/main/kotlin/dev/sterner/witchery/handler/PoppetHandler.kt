package dev.sterner.witchery.handler

import dev.architectury.event.EventResult
import dev.sterner.witchery.item.PoppetItem
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.DamageTypeTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

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
            val itemStack: ItemStack? = consumePoppet(player, WitcheryItems.DEATH_PROTECTION_POPPET.get())

            if (itemStack != null) {
                player.health = 4.0f
                player.removeAllEffects()
                player.addEffect(MobEffectInstance(MobEffects.REGENERATION, 900, 1))
                player.addEffect(MobEffectInstance(MobEffects.ABSORPTION, 100, 1))
                player.addEffect(MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0))
            }

            return itemStack != null
        }
    }

    fun hasArmorProtectionPoppet(level: ServerLevel, player: ServerPlayer): Boolean {
        return false //TODO implement and consume poppet here
    }

    fun hungerProtectionPoppet(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity is Player) {
            if (hungerProtectionPoppetHelper(livingEntity, damageSource)) {
                return EventResult.interruptFalse()
            }
        }
        return EventResult.pass()
    }

    private fun hungerProtectionPoppetHelper(livingEntity: LivingEntity, damageSource: DamageSource?): Boolean {
        if (livingEntity is Player && damageSource != null && damageSource.`is`(DamageTypes.STARVE)) {
            val itemStack: ItemStack? = consumePoppet(livingEntity, WitcheryItems.HUNGER_PROTECTION_POPPET.get())

            if (itemStack != null) {
                livingEntity.health = 10.0f
                livingEntity.foodData.foodLevel = 20
                livingEntity.removeAllEffects()
                livingEntity.level().broadcastEntityEvent(livingEntity, 35.toByte())
            }

            return itemStack != null
        }
        return false
    }

    private fun consumePoppet(livingEntity: LivingEntity, item: Item): ItemStack? {
        var itemStack: ItemStack? = null
        for (interactionHand in InteractionHand.entries) {
            val itemStack2: ItemStack = livingEntity.getItemInHand(interactionHand)
            if (itemStack2.`is`(item)) {
                itemStack = itemStack2.copy()
                itemStack2.shrink(1)
                break
            }
        }
        return itemStack
    }
}
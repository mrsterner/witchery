package dev.sterner.witchery.handler

import dev.architectury.event.EventResult
import dev.sterner.witchery.item.TaglockItem
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
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
                player.playSound(SoundEvents.TOTEM_USE)
            }

            return itemStack != null
        }
    }

    fun hasArmorProtectionPoppet(level: ServerLevel, player: ServerPlayer?): Boolean {
        val itemStack: ItemStack? = player?.let { consumePoppet(it, WitcheryItems.ARMOR_PROTECTION_POPPET.get()) }
        return itemStack != null
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
        var itemStack: ItemStack?
        var consume: Boolean

        val (accessoryConsume, accessoryItem) = AccessoryHandler.check(livingEntity, item)

        itemStack = accessoryItem
        consume = accessoryConsume

        if (!consume) {

            for (interactionHand in InteractionHand.entries) {
                val itemStack2: ItemStack = livingEntity.getItemInHand(interactionHand)

                if (itemStack2.`is`(item)) {
                    if (livingEntity is Player) {
                        val profile = itemStack2.get(DataComponents.PROFILE)
                        if (profile?.gameProfile == livingEntity.gameProfile) {
                            consume = true
                        }
                    } else {
                        val id = itemStack2.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())
                        if (id == livingEntity.stringUUID) {
                            consume = true
                        }
                    }
                }

                if (consume) {
                    itemStack = itemStack2.copy()
                    itemStack2.shrink(1)
                    break
                }
            }
        }

        return itemStack
    }

    fun handleVampiricPoppet(livingEntity: LivingEntity?, original: Float): Float {
        if (livingEntity != null) {
            var itemStack: ItemStack? = AccessoryHandler.checkNoConsume(livingEntity, WitcheryItems.VAMPIRIC_POPPET.get())

            if (itemStack == null) {
                for (interactionHand in InteractionHand.entries) {
                    val handItem: ItemStack = livingEntity.getItemInHand(interactionHand)
                    if (handItem.`is`(WitcheryItems.VAMPIRIC_POPPET.get())) {
                        itemStack = handItem
                        break
                    }
                }
            }

            if (itemStack != null) {
                val maybePlayer = TaglockItem.getPlayer(livingEntity.level(), itemStack)
                val maybeEntity = TaglockItem.getLivingEntity(livingEntity.level(), itemStack)

                if (maybePlayer != null || maybeEntity != null) {
                    val halfDamage = original / 2

                    if (maybePlayer != null) {
                        maybePlayer.hurt(livingEntity.lastDamageSource ?: livingEntity.damageSources().magic(), halfDamage)
                    } else maybeEntity?.hurt(livingEntity.lastDamageSource ?: livingEntity.damageSources().magic(), halfDamage)

                    itemStack.damageValue += 1
                    if (itemStack.damageValue >= itemStack.maxDamage) {
                        itemStack.shrink(1)
                    }
                    return halfDamage
                }
            }
        }

        return original
    }
}
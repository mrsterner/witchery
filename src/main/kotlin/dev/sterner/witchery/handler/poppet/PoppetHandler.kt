package dev.sterner.witchery.handler.poppet

import dev.sterner.witchery.api.PoppetLocation
import dev.sterner.witchery.api.interfaces.PoppetType
import dev.sterner.witchery.api.PoppetUsage
import dev.sterner.witchery.api.WitcheryApi
import dev.sterner.witchery.data_attachment.poppet.CorruptPoppetPlayerAttachment
import dev.sterner.witchery.data_attachment.poppet.PoppetLevelAttachment
import dev.sterner.witchery.handler.AccessoryHandler
import dev.sterner.witchery.item.PoppetItem
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryPoppetRegistry
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.level.Level
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import java.util.*

object PoppetHandler {


    fun onLivingHurt(entity: LivingEntity, damageSource: DamageSource, remainingDamage: Float): Float {
        if (entity !is Player) return remainingDamage

        var modifiedDamage = remainingDamage

        when {
            damageSource.`is`(DamageTypes.STARVE) -> {
                WitcheryPoppetRegistry.HUNGER_PROTECTION.get().let { hungerPoppetType ->
                    if (activatePoppet(entity, hungerPoppetType, damageSource)) {
                        return 0f
                    }
                }
            }
        }

        WitcheryPoppetRegistry.VAMPIRIC.get().let { vampiricPoppet ->
            modifiedDamage = vampiricPoppet.handleDamage(entity, damageSource, modifiedDamage)
        }

        if (damageSource.entity is Player) {
            val attacker = damageSource.entity as Player
            val voodooBonusDamage = handleVoodooPoppet(attacker, entity, modifiedDamage)
            modifiedDamage += voodooBonusDamage
        }

        return modifiedDamage
    }

    private fun handleVoodooPoppet(attacker: Player, victim: LivingEntity, damage: Float): Float {
        val (voodooPoppet, location) = findPoppet(victim, WitcheryPoppetRegistry.VOODOO.get())

        if (voodooPoppet != null && location != null) {
            val bonusDamage = damage * 0.5f

            when (location) {
                PoppetLocation.ACCESSORY -> {
                    voodooPoppet.damageValue += voodooPoppet.maxDamage / 10
                    if (voodooPoppet.damageValue >= voodooPoppet.maxDamage) {
                        AccessoryHandler.checkPoppet(attacker, voodooPoppet.item)
                    }
                }

                PoppetLocation.INVENTORY -> {
                    voodooPoppet.damageValue += voodooPoppet.maxDamage / 10
                    if (voodooPoppet.damageValue >= voodooPoppet.maxDamage) {
                        voodooPoppet.shrink(1)
                    }
                }

                PoppetLocation.WORLD -> {
                    if (attacker.level() is ServerLevel) {
                        val level = attacker.level() as ServerLevel
                        val poppetData = PoppetLevelAttachment.getPoppetData(level)

                        val blockPoppet = poppetData.poppetDataMap.find {
                            it.poppetItemStack == voodooPoppet && isPoppetBoundToLiving(it.poppetItemStack, victim)
                        }

                        if (blockPoppet != null) {
                            voodooPoppet.damageValue += voodooPoppet.maxDamage / 10
                            if (voodooPoppet.damageValue >= voodooPoppet.maxDamage) {
                                blockPoppet.poppetItemStack.shrink(1)
                                PoppetLevelAttachment.updatePoppetItem(
                                    level,
                                    blockPoppet.blockPos,
                                    blockPoppet.poppetItemStack
                                )
                            }
                        }
                    }
                }
            }

            return bonusDamage
        }

        return 0f
    }

    fun onLivingDeath(event: LivingDeathEvent, livingEntity: LivingEntity?, damageSource: DamageSource?) {
        if (livingEntity !is Player || WitcheryApi.isInSpiritWorld(livingEntity)) {
            return
        }

        WitcheryPoppetRegistry.DEATH_PROTECTION.get().let { deathPoppetType ->

            if (activatePoppet(livingEntity, deathPoppetType, damageSource)) {

                livingEntity.health = livingEntity.maxHealth * 0.5f
                livingEntity.invulnerableTime = 60
                livingEntity.removeAllEffects()
                livingEntity.addEffect(MobEffectInstance(MobEffects.REGENERATION, 200, 1))

                if (livingEntity.level() is ServerLevel) {
                    val serverLevel = livingEntity.level() as ServerLevel
                    serverLevel.sendParticles(
                        ParticleTypes.ENCHANTED_HIT,
                        livingEntity.x,
                        livingEntity.y + livingEntity.bbHeight * 0.5,
                        livingEntity.z,
                        20,
                        0.3,
                        0.3,
                        0.3,
                        0.1
                    )
                }
                event.isCanceled = true
                return
            }
        }
    }

    fun activatePoppet(
        owner: LivingEntity,
        poppetType: PoppetType,
        source: DamageSource?
    ): Boolean {
        if (!poppetType.isValidFor(owner, source)) {
            return false
        }

        val damageAmount = poppetType.getDurabilityDamage(PoppetUsage.PROTECTION)

        if (owner is Player) {
            val corruptData = CorruptPoppetPlayerAttachment.getData(owner)
            if (corruptData.corruptedPoppets.contains(poppetType.getRegistryId())) {

                val foundPoppet = findAndHurtPoppet(
                    owner,
                    poppetType,
                    source,
                    damagePoppet = damageAmount
                )

                if (foundPoppet == null) {
                    return false
                }

                if (owner.level() is ServerLevel) {
                    val serverLevel = owner.level() as ServerLevel
                    serverLevel.sendParticles(
                        ParticleTypes.WITCH,
                        owner.x,
                        owner.y + owner.bbHeight * 0.5,
                        owner.z,
                        15,
                        0.3,
                        0.3,
                        0.3,
                        0.05
                    )
                }

                return poppetType.onCorruptedActivate(owner, source)
            }
        }

        val foundPoppet = findAndHurtPoppet(
            owner,
            poppetType,
            source,
            damagePoppet = damageAmount
        )

        if (foundPoppet == null) {
            return false
        }

        if (owner.level() is ServerLevel) {
            val serverLevel = owner.level() as ServerLevel
            serverLevel.sendParticles(
                ParticleTypes.ENCHANTED_HIT,
                owner.x,
                owner.y + owner.bbHeight * 0.5,
                owner.z,
                10,
                0.3,
                0.3,
                0.3,
                0.1
            )
        }

        return poppetType.onActivate(owner, source)
    }

    private fun findAndHurtPoppet(
        owner: LivingEntity,
        poppetType: PoppetType,
        source: DamageSource?,
        damagePoppet: Int = 0
    ): ItemStack? {
        val (accessoryFound, accessoryItem) = AccessoryHandler.checkPoppet(owner, poppetType.item)

        if (accessoryFound && accessoryItem != null) {
            val isBound = isPoppetBoundToLiving(accessoryItem, owner)
            if (isBound) {
                val result = accessoryItem.copy()
                if (damagePoppet > 0) {
                    AccessoryHandler.damageCurioPoppet(owner, poppetType.item, damagePoppet)
                }
                return result
            }
        }

        if (owner is Player) {
            for (hand in InteractionHand.entries) {
                val handItem = owner.getItemInHand(hand)
                if (handItem.`is`(poppetType.item)) {
                    val isBound = isPoppetBoundToLiving(handItem, owner)
                    if (isBound) {
                        val result = handItem.copy()
                        if (damagePoppet > 0) {
                            if (owner.level() is ServerLevel && owner is ServerPlayer) {
                                handItem.hurtAndBreak(damagePoppet, owner.level() as ServerLevel, owner) {
                                    owner.setItemInHand(hand, ItemStack.EMPTY)
                                }
                            } else {
                                handItem.damageValue += damagePoppet
                                if (handItem.damageValue >= handItem.maxDamage) {
                                    owner.setItemInHand(hand, ItemStack.EMPTY)
                                }
                            }
                        }
                        return result
                    }
                }
            }

            for (i in 0 until owner.inventory.containerSize) {
                val invItem = owner.inventory.getItem(i)
                if (invItem.`is`(poppetType.item)) {
                    val isBound = isPoppetBoundToLiving(invItem, owner)
                    if (isBound) {
                        val result = invItem.copy()
                        if (damagePoppet > 0) {
                            if (owner.level() is ServerLevel && owner is ServerPlayer) {
                                invItem.hurtAndBreak(damagePoppet, owner.level() as ServerLevel, owner) {
                                    owner.inventory.setItem(i, ItemStack.EMPTY)
                                }
                            } else {
                                invItem.damageValue += damagePoppet
                                if (invItem.damageValue >= invItem.maxDamage) {
                                    owner.inventory.setItem(i, ItemStack.EMPTY)
                                }
                            }
                        }
                        return result
                    }
                }
            }
        }

        if (owner.level() is ServerLevel) {
            val level = owner.level() as ServerLevel
            val poppetData = PoppetLevelAttachment.getPoppetData(level)

            val blockPoppet = poppetData.poppetDataMap.find {
                val isCorrectItem = it.poppetItemStack.`is`(poppetType.item)
                val isBound = isPoppetBoundToLiving(it.poppetItemStack, owner)
                isCorrectItem && isBound
            }

            if (blockPoppet != null) {
                val result = blockPoppet.poppetItemStack.copy()
                if (damagePoppet > 0) {
                    blockPoppet.poppetItemStack.hurtAndBreak(damagePoppet, level, null) {
                        poppetData.poppetDataMap.remove(blockPoppet)
                    }
                }
                PoppetLevelAttachment.updatePoppetItem(level, blockPoppet.blockPos, blockPoppet.poppetItemStack)
                return result
            }
        }

        return null
    }

    /**
     * Checks if a poppet is bound to the specified living entity.
     */
    private fun isPoppetBoundToLiving(itemStack: ItemStack, livingEntity: LivingEntity?): Boolean {
        if (livingEntity == null) return false

        return if (livingEntity is Player) {
            val profile = itemStack.get(DataComponents.PROFILE)
            profile?.gameProfile == livingEntity.gameProfile
        } else {
            val entityId = itemStack.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())
            entityId == livingEntity.stringUUID
        }
    }

    fun findPoppet(
        owner: LivingEntity,
        poppetType: PoppetType
    ): Pair<ItemStack?, PoppetLocation?> {
        val (accessoryFound, accessoryItem) = AccessoryHandler.checkPoppet(owner, poppetType.item)
        if (accessoryFound && accessoryItem != null && isPoppetBoundToLiving(accessoryItem, owner)) {
            return Pair(accessoryItem, PoppetLocation.ACCESSORY)
        }

        if (owner is Player) {
            for (hand in InteractionHand.entries) {
                val handItem = owner.getItemInHand(hand)
                if (handItem.`is`(poppetType.item) && isPoppetBoundToLiving(handItem, owner)) {
                    return Pair(handItem, PoppetLocation.INVENTORY)
                }
            }
        }

        if (owner.level() is ServerLevel) {
            val level = owner.level() as ServerLevel
            val poppetData = PoppetLevelAttachment.getPoppetData(level)

            val blockPoppet = poppetData.poppetDataMap.find {
                it.poppetItemStack.`is`(poppetType.item) && isPoppetBoundToLiving(it.poppetItemStack, owner)
            }

            if (blockPoppet != null) {
                return Pair(blockPoppet.poppetItemStack, PoppetLocation.WORLD)
            }
        }

        return Pair(null, null)
    }

    /**
     * Gets the player bound to a poppet
     */
    fun getBoundPlayer(level: Level, poppet: ItemStack): Player? {
        val profile = poppet.get(DataComponents.PROFILE)
        if (profile != null) {
            if (level is ServerLevel) {
                return level.server.playerList.getPlayer(profile.gameProfile.id)
            }
        }
        return null
    }

    /**
     * Gets any living entity bound to a poppet
     */
    fun getBoundEntity(level: Level, poppet: ItemStack): LivingEntity? {
        if (level is ServerLevel) {
            val entityId = poppet.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())
            if (entityId != null) {
                try {
                    val uuid = UUID.fromString(entityId)
                    for (entity in level.allEntities) {
                        if (entity is LivingEntity && entity.uuid == uuid) {
                            return entity
                        }
                    }
                } catch (_: IllegalArgumentException) {

                }
            }
        }

        return null
    }

    /**
     * Bind a poppet to a player
     */
    private fun bindPoppetToPlayer(poppet: ItemStack, player: Player): Boolean {
        if (poppet.item !is PoppetItem) return false

        poppet.set(DataComponents.PROFILE, ResolvableProfile(player.gameProfile))
        poppet.remove(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())

        return true
    }

    /**
     * Bind a poppet to any living entity
     */
    fun bindPoppetToEntity(poppet: ItemStack, entity: LivingEntity): Boolean {
        if (poppet.item !is PoppetItem) return false

        if (entity is Player) {
            return bindPoppetToPlayer(poppet, entity)
        } else {
            poppet.set(WitcheryDataComponents.ENTITY_ID_COMPONENT.get(), entity.stringUUID)
            poppet.remove(DataComponents.PROFILE)

            return true
        }
    }
}
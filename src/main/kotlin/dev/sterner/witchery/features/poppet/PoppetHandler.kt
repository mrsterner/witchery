package dev.sterner.witchery.features.poppet

import dev.sterner.witchery.features.misc.AccessoryHandler
import dev.sterner.witchery.core.api.PoppetLocation
import dev.sterner.witchery.core.api.PoppetUsage
import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.core.api.interfaces.PoppetType
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryPoppetRegistry
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import java.util.UUID

object PoppetHandler {

    fun onLivingHurt(entity: LivingEntity, damageSource: DamageSource, remainingDamage: Float): Float {

        when {
            damageSource.`is`(DamageTypes.STARVE) -> {
                WitcheryPoppetRegistry.HUNGER_PROTECTION.get().let { hungerPoppetType ->
                    if (activatePoppet(entity, hungerPoppetType, damageSource)) {
                        return 0f
                    }
                }
            }
        }

        var modifiedDamage = remainingDamage

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

        if (owner is Player) {
            val (accessoryFound, accessoryItem, slot) =
                AccessoryHandler.checkPoppet(owner, poppetType.item)

            if (accessoryFound && accessoryItem != null && slot != null &&
                isPoppetBoundToLiving(accessoryItem, owner)
            ) {
                if (damagePoppet > 0 && owner.level() is ServerLevel) {
                    val level = owner.level() as ServerLevel

                    accessoryItem.hurtAndBreak(damagePoppet, level, owner) {
                        AccessoryHandler.removeAccessory(owner, poppetType.item)
                    }
                }

                return accessoryItem
            }
        }

        if (owner is Player) {
            for (hand in InteractionHand.entries) {
                val handItem = owner.getItemInHand(hand)

                if (handItem.`is`(poppetType.item) && isPoppetBoundToLiving(handItem, owner)) {
                    if (damagePoppet > 0 && owner.level() is ServerLevel) {
                        handItem.hurtAndBreak(damagePoppet, owner.level() as ServerLevel, owner) {
                            owner.setItemInHand(hand, ItemStack.EMPTY)
                        }
                    }
                    return handItem
                }
            }

            for (i in 0 until owner.inventory.containerSize) {
                val invItem = owner.inventory.getItem(i)

                if (invItem.`is`(poppetType.item) && isPoppetBoundToLiving(invItem, owner)) {
                    if (damagePoppet > 0 && owner.level() is ServerLevel) {
                        invItem.hurtAndBreak(damagePoppet, owner.level() as ServerLevel, owner) {
                            owner.inventory.setItem(i, ItemStack.EMPTY)
                        }
                    }
                    return invItem
                }
            }
        }

        if (owner.level() is ServerLevel) {
            val level = owner.level() as ServerLevel
            val poppetData = PoppetLevelAttachment.getPoppetData(level)

            val blockPoppet = poppetData.poppetDataMap.find {
                it.poppetItemStack.`is`(poppetType.item) &&
                        isPoppetBoundToLiving(it.poppetItemStack, owner)
            }

            if (blockPoppet != null) {
                if (damagePoppet > 0) {
                    blockPoppet.poppetItemStack.hurtAndBreak(damagePoppet, level, owner) {
                        poppetData.poppetDataMap.remove(blockPoppet)
                    }
                }

                PoppetLevelAttachment.updatePoppetItem(
                    level,
                    blockPoppet.blockPos,
                    blockPoppet.poppetItemStack
                )

                return blockPoppet.poppetItemStack
            }
        }

        return null
    }

    private fun handleVoodooPoppet(attacker: Player, victim: LivingEntity, damage: Float): Float {
        val (voodooPoppet, location) = findPoppet(victim, WitcheryPoppetRegistry.VOODOO.get())

        if (voodooPoppet != null && location != null) {
            val damageMultiplier = if (victim is Player && !WitcheryApi.isWitchy(victim)) {
                0.15f
            } else {
                0.5f
            }

            val bonusDamage = damage * damageMultiplier
            val durabilityDamage = voodooPoppet.maxDamage / 10

            when (location) {
                PoppetLocation.ACCESSORY -> {
                    if (attacker.level() is ServerLevel) {
                        voodooPoppet.hurtAndBreak(durabilityDamage, attacker.level() as ServerLevel, null) { _ ->
                            AccessoryHandler.checkPoppet(attacker, voodooPoppet.item)
                        }
                    }

                }

                PoppetLocation.INVENTORY -> {
                    if (attacker.level() is ServerLevel) {
                        voodooPoppet.hurtAndBreak(durabilityDamage, attacker.level() as ServerLevel, null) { _ ->
                            voodooPoppet.shrink(1)
                        }
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
                            blockPoppet.poppetItemStack.hurtAndBreak(durabilityDamage, level, null) { _ ->
                                blockPoppet.poppetItemStack.shrink(1)
                            }
                            PoppetLevelAttachment.updatePoppetItem(
                                level,
                                blockPoppet.blockPos,
                                blockPoppet.poppetItemStack
                            )
                        }
                    }
                }
            }

            return bonusDamage
        }

        return 0f
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
        val (accessoryFound, accessoryItem, slot) = AccessoryHandler.checkPoppet(owner, poppetType.item)

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

            for (item in owner.inventory.items) {
                if (item.`is`(poppetType.item) && isPoppetBoundToLiving(item, owner)) {
                    return Pair(item, PoppetLocation.INVENTORY)
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
        if (level is ServerLevel && poppet.has(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())) {
            val entityIdStr = poppet.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get()) ?: return null

            val uuid = try {
                UUID.fromString(entityIdStr)
            } catch (e: IllegalArgumentException) {
                return null
            }

            return level.allEntities
                .filterIsInstance<LivingEntity>()
                .firstOrNull { it.uuid == uuid }
        }

        return null
    }
}
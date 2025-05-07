package dev.sterner.witchery.handler

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.EntityEvent
import dev.sterner.witchery.api.WitcheryApi
import dev.sterner.witchery.item.TaglockItem.Companion.getLivingEntity
import dev.sterner.witchery.item.TaglockItem.Companion.getPlayer
import dev.sterner.witchery.platform.poppet.PoppetLevelAttachment
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.DamageTypeTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

object PoppetHandler {

    fun registerEvents() {
        EntityEvent.LIVING_DEATH.register(::deathProtectionPoppet)
        EntityEvent.LIVING_DEATH.register(::hungerProtectionPoppet)
    }
    /**
     * Handles the Death Protection Poppet's behavior, which can prevent death for a player
     * under certain conditions.
     *
     * @param livingEntity The living entity being damaged, usually a player.
     * @param damageSource The source of the damage causing the event.
     * @return An `EventResult` indicating whether the event was handled.
     */
    fun deathProtectionPoppet(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity is Player && !WitcheryApi.isInSpiritWorld(livingEntity)) {
            if (deathProtectionHelper(livingEntity, damageSource)) {
                return EventResult.interruptFalse()
            }
        }
        return EventResult.pass()
    }

    /**
     * Helper method to process the Death Protection Poppet.
     *
     * @param player The player attempting to use the poppet.
     * @param damageSource The source of the damage.
     * @return `true` if the poppet was consumed and the player was protected; `false` otherwise.
     */
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

    /**
     * Checks if the player has an Armor Protection Poppet available.
     *
     * @param level The server level where the check is performed.
     * @param player The player to check for the poppet.
     * @return `true` if the player or the world contains a valid Armor Protection Poppet; `false` otherwise.
     */
    fun hasArmorProtectionPoppet(level: ServerLevel, player: ServerPlayer?): Boolean {
        val playerPoppet = player?.let { consumePoppet(it, WitcheryItems.ARMOR_PROTECTION_POPPET.get()) }
        if (playerPoppet != null) return true

        val poppetData = PoppetLevelAttachment.getPoppetData(level)
        return poppetData.poppetDataMap.any { data ->
            data.poppetItemStack.`is`(WitcheryItems.ARMOR_PROTECTION_POPPET.get()) &&
                    isPoppetBoundToLiving(data.poppetItemStack, player)
        }
    }

    /**
     * Checks if a poppet is bound to the specified living entity.
     *
     * @param itemStack The poppet item stack.
     * @param livingEntity The living entity to check binding against.
     * @return `true` if the poppet is bound to the entity; `false` otherwise.
     */
    private fun isPoppetBoundToLiving(itemStack: ItemStack, livingEntity: LivingEntity?): Boolean {
        return if (livingEntity is Player) {
            val profile = itemStack.get(DataComponents.PROFILE)
            profile?.gameProfile == livingEntity.gameProfile
        } else {
            itemStack.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get()) == livingEntity?.stringUUID
        }
    }

    /**
     * Handles the Hunger Protection Poppet, preventing starvation damage for players.
     *
     * @param livingEntity The living entity being damaged.
     * @param damageSource The source of starvation damage.
     * @return An `EventResult` indicating whether the event was handled.
     */
    fun hungerProtectionPoppet(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity is Player) {
            if (hungerProtectionPoppetHelper(livingEntity, damageSource)) {
                return EventResult.interruptFalse()
            }
        }
        return EventResult.pass()
    }

    /**
     * Helper method for the Hunger Protection Poppet.
     *
     * @param livingEntity The player using the poppet.
     * @param damageSource The source of starvation damage.
     * @return `true` if the poppet was consumed and the player was protected; `false` otherwise.
     */
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

    /**
     * Attempts to consume a poppet for a specified item and living entity.
     *
     * @param livingEntity The living entity using the poppet.
     * @param item The type of poppet to consume.
     * @return The consumed poppet's item stack, or `null` if none were consumed.
     */
    private fun consumePoppet(livingEntity: LivingEntity, item: Item): ItemStack? {
        var itemStack: ItemStack?
        var consume: Boolean

        val (accessoryConsume, accessoryItem) = AccessoryHandler.checkPoppet(livingEntity, item)
        itemStack = accessoryItem
        consume = accessoryConsume

        if (!consume) {
            for (interactionHand in InteractionHand.entries) {
                val itemStack2: ItemStack = livingEntity.getItemInHand(interactionHand)
                if (itemStack2.`is`(item) && isPoppetBoundToLiving(itemStack2, livingEntity)) {
                    itemStack = itemStack2.copy()
                    itemStack2.shrink(1)
                    consume = true
                    break
                }
            }
        }

        if (!consume && livingEntity.level() is ServerLevel) {
            val level = livingEntity.level() as ServerLevel
            val poppetData = PoppetLevelAttachment.getPoppetData(level)

            val blockPoppet = poppetData.poppetDataMap.find {
                it.poppetItemStack.`is`(item) && isPoppetBoundToLiving(it.poppetItemStack, livingEntity)
            }

            if (blockPoppet != null) {
                itemStack = blockPoppet.poppetItemStack.copy()
                blockPoppet.poppetItemStack.shrink(1)
                PoppetLevelAttachment.updatePoppetItem(level, blockPoppet.blockPos, blockPoppet.poppetItemStack)
            }
        }

        return itemStack
    }

    /**
     * Handles the behavior of the Vampiric Poppet when a LivingEntity takes damage.
     *
     * @param livingEntity the entity taking damage.
     * @param damageSource the source of the damage.
     * @param original the original damage amount.
     * @return the modified damage amount after processing the Vampiric Poppet effects.
     */
    fun handleVampiricPoppet(livingEntity: LivingEntity?, damageSource: DamageSource, original: Float): Float {
        if (livingEntity != null) {
            var itemStack: ItemStack? =
                AccessoryHandler.checkPoppetNoConsume(livingEntity, WitcheryItems.VAMPIRIC_POPPET.get())

            if (itemStack == null) {
                for (interactionHand in InteractionHand.entries) {
                    val handItem: ItemStack = livingEntity.getItemInHand(interactionHand)
                    if (handItem.`is`(WitcheryItems.VAMPIRIC_POPPET.get())) {
                        itemStack = handItem
                        break
                    }
                }

                if (itemStack == null && livingEntity.level() is ServerLevel) {
                    val level = livingEntity.level() as ServerLevel
                    val poppetData = PoppetLevelAttachment.getPoppetData(level)

                    val blockPoppet = poppetData.poppetDataMap.find {
                        it.poppetItemStack.`is`(WitcheryItems.VAMPIRIC_POPPET.get()) &&
                                isPoppetBoundToLiving(it.poppetItemStack, livingEntity)
                    }

                    if (blockPoppet != null) {
                        itemStack = blockPoppet.poppetItemStack.copy()
                        blockPoppet.poppetItemStack.damageValue += 1
                        if (blockPoppet.poppetItemStack.damageValue >= blockPoppet.poppetItemStack.maxDamage) {
                            blockPoppet.poppetItemStack.shrink(1)
                        }
                        PoppetLevelAttachment.updatePoppetItem(level, blockPoppet.blockPos, blockPoppet.poppetItemStack)
                    }
                }
            }

            if (itemStack != null) {
                val maybePlayer = getPlayer(livingEntity.level(), itemStack)
                val maybeEntity = getLivingEntity(livingEntity.level(), itemStack)
                if (maybePlayer != null || maybeEntity != null) {

                    var outDamage = original

                    if (maybeEntity is Player && !WitcheryApi.isWitchy(maybeEntity)) {
                        maybePlayer?.hurt(damageSource, outDamage * 0.75f)
                        maybeEntity.hurt(damageSource, outDamage * 0.25f)
                    } else {
                        outDamage /= 2
                        maybePlayer?.hurt(damageSource, outDamage)
                        maybeEntity?.hurt(damageSource, outDamage)
                    }

                    itemStack.damageValue += 1
                    if (itemStack.damageValue >= itemStack.maxDamage) {
                        itemStack.shrink(1)
                    }
                    return outDamage
                }
            }
        }

        return original
    }

    /**
     * Handles the Voodoo Poppet effect on an ItemEntity, applying motion to bound entities and marking them as "hurt."
     *
     * @param entity the item entity representing the Voodoo Poppet.
     */
    fun handleVoodoo(entity: ItemEntity) {
        val movementVector: Vec3 = entity.deltaMovement
        val itemStack = entity.item

        val boundPlayer = getPlayer(entity.level(), itemStack)
        val boundEntity = getLivingEntity(entity.level(), itemStack)

        if (boundPlayer != null || boundEntity != null) {

            if (movementVector.length() > 0.2) {
                var scaledMovement = movementVector.scale(0.45)
                boundPlayer?.apply {
                    if (WitcheryApi.isWitchy(boundPlayer)) {
                        scaledMovement = scaledMovement.scale(0.75)
                    }
                    addDeltaMovement(scaledMovement)
                    hurtMarked = true
                }
                boundEntity?.apply {
                    addDeltaMovement(scaledMovement)
                    hurtMarked = true
                }
            }

            if (entity.isUnderWater) {
                boundPlayer?.let {
                    VoodooPoppetLivingEntityAttachment.setPoppetData(
                        it,
                        VoodooPoppetLivingEntityAttachment.VoodooPoppetData(true)
                    )
                }
                boundEntity?.let {
                    VoodooPoppetLivingEntityAttachment.setPoppetData(
                        it,
                        VoodooPoppetLivingEntityAttachment.VoodooPoppetData(true)
                    )
                }
            } else {
                boundPlayer?.let {
                    VoodooPoppetLivingEntityAttachment.setPoppetData(
                        it,
                        VoodooPoppetLivingEntityAttachment.VoodooPoppetData(false)
                    )
                }
                boundEntity?.let {
                    VoodooPoppetLivingEntityAttachment.setPoppetData(
                        it,
                        VoodooPoppetLivingEntityAttachment.VoodooPoppetData(false)
                    )
                }
            }

            entity.item.damageValue += 1
            if (entity.item.damageValue >= entity.item.maxDamage) {
                entity.remove(Entity.RemovalReason.DISCARDED)
            }
        }
    }

    /**
     * Handles the interaction of a Voodoo Poppet with fire or lava.
     * Applies fire effects to the bound entities or players and damages the item.
     *
     * @param level the level where the interaction occurs.
     * @param pos the position of the interaction.
     * @param item the Voodoo Poppet item stack.
     * @param player the player interacting with the Voodoo Poppet.
     * @param blockHitResult the result of the block hit interaction.
     * @return the result of the interaction.
     */
    fun handleUseVoodoo(
        level: Level,
        pos: BlockPos,
        item: ItemStack,
        player: Player?,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if (level.getBlockState(blockHitResult.blockPos).`is`(Blocks.LAVA)) {
            val maybePlayer = getPlayer(level, item)
            val maybeEntity = getLivingEntity(level, item)
            if (maybePlayer != null || maybeEntity != null) {

                maybePlayer?.apply {
                    if (WitcheryApi.isWitchy(maybePlayer)) {
                        maybePlayer.remainingFireTicks = 20 * 4
                    } else {
                        maybePlayer.remainingFireTicks = 20 * 2
                    }
                }

                maybeEntity?.remainingFireTicks = 20 * 4
                item.damageValue += 16
                if (item.damageValue >= item.maxDamage) {
                    item.shrink(1)
                }
                return InteractionResult.SUCCESS
            }
        } else if (level.getBlockState(pos).`is`(Blocks.FIRE)) {
            val maybePlayer = getPlayer(level, item)
            val maybeEntity = getLivingEntity(level, item)
            if (maybePlayer != null || maybeEntity != null) {
                maybePlayer?.apply {
                    if (WitcheryApi.isWitchy(maybePlayer)) {
                        maybePlayer.remainingFireTicks = 20 * 2
                    } else {
                        maybePlayer.remainingFireTicks = 20 * 1
                    }
                }
                maybeEntity?.remainingFireTicks = 20 * 2
                item.damageValue += 8
                if (item.damageValue >= item.maxDamage) {
                    item.shrink(1)
                }
                return InteractionResult.SUCCESS
            }
        }
        return InteractionResult.PASS
    }
}
package dev.sterner.witchery.data_attachment.possession

import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.tick.EntityTickEvent

object PossessionManager {

    fun startPossessing(player: Player, host: Mob, simulate: Boolean = false): Boolean {
        if (player.level().isClientSide) return false
        if (!isReadyForPossession(player)) return false

        // Check if mob can be possessed
        val possessableData = PossessionAttachment.getPossessable(host)
        if (!canBePossessedBy(host, player)) return false

        // Fire event (you'll need to create this)
        // val result = PossessionStartEvent.fire(host, player, simulate)
        // if (!result.isAllowed()) return false

        if (!simulate) {
            performPossession(player, host)
        }

        return true
    }

    private fun performPossession(player: Player, host: Mob) {
        val playerData = PossessionAttachment.get(player)
        val possessableData = PossessionAttachment.getPossessable(host)
        val possessedData = PossessionAttachment.getPossessedData(host)

        // Clear previous possessor if any
        possessableData.possessorId?.let {
            possessableData.previousPossessorId = it
        }

        // Transfer inventory if needed
        if (!player.level().isClientSide && player is ServerPlayer) {
            // Transfer inventory based on entity tags
            if (shouldTransferInventory(host)) {
                transferInventory(player, host, toHost = false)
            }

            // Transfer equipment
            if (shouldTransferEquipment(host)) {
                transferEquipment(host, player)
            }

            // Transfer effects
            transferSoulboundEffects(player, host)

            // Handle mounting
            host.vehicle?.let { vehicle ->
                host.stopRiding()
                player.startRiding(vehicle)
            }

            // Handle hunger
            if (isRegularEater(host)) {
                player.foodData.readAdditionalSaveData(possessedData.getHungerData())
            }
        }

        // Set possession state
        playerData.possessedEntityId = host.uuid
        playerData.possessedEntityNetworkId = host.id
        possessableData.possessorId = player.uuid

        // Sync position
        player.teleportTo(host.x, host.y, host.z)
        player.setYRot(host.yRot)
        player.setXRot(host.xRot)

        // Update attributes
        host.getAttribute(Attributes.MOVEMENT_SPEED)?.addTransientModifier(INHERENT_MOB_SLOWNESS)

        // Make persistent
        host.setPersistenceRequired()

        // Clear target
        host.target = null

        // Sync to clients
        if (player is ServerPlayer) {
            PossessionAttachment.syncPlayerPossession(player)
            PossessionAttachment.syncToClient(host)
        }

        // Fire post-possession event
        // PossessionStateChangeEvent.fire(player, host)
    }


    fun stopPossessing(player: Player, transfer: Boolean = !player.isCreative) {
        val playerData = PossessionAttachment.get(player)
        val hostId = playerData.possessedEntityId ?: return

        val entity: Entity? = if (!player.level().isClientSide) {
            (player.level() as? ServerLevel)?.getEntity(hostId)
        } else {
            if (playerData.possessedEntityNetworkId != -1) {
                player.level().getEntity(playerData.possessedEntityNetworkId)
            } else {
                return
            }
        }
        val host = entity as? Mob ?: return

        val possessableData = PossessionAttachment.getPossessable(host)
        val possessedData = PossessionAttachment.getPossessedData(host)

        // Clear possession state
        playerData.possessedEntityId = null
        playerData.previousPossessedUuid = hostId
        possessableData.possessorId = null
        possessableData.previousPossessorId = player.uuid
        playerData.possessedEntityNetworkId = -1

        if (player is ServerPlayer && !player.level().isClientSide) {
            // Handle vehicle transfer
            player.vehicle?.let { vehicle ->
                player.stopRiding()
                host.startRiding(vehicle)
            }

            // Transfer items back
            if (transfer) {
                if (shouldTransferEquipment(host)) {
                    transferEquipment(player, host)
                }
                if (shouldTransferInventory(host)) {
                    transferInventory(player, host, toHost = true)
                }
            }

            // Save hunger data
            if (isRegularEater(host)) {
                player.foodData.addAdditionalSaveData(possessedData.getHungerData())
            }

            // Transfer soulbound effects back
            transferSoulboundEffectsBack(host, player)

            // Clear player effects
            player.removeAllEffects()

            // Reset attributes
            host.getAttribute(Attributes.MOVEMENT_SPEED)?.removeModifier(INHERENT_MOB_SLOWNESS_UUID)

            // Reset states
            host.isSprinting = false
            host.isShiftKeyDown = false
            player.fallDistance = host.fallDistance
        }

        // Sync to clients
        if (player is ServerPlayer) {
            PossessionAttachment.syncPlayerPossession(player)
            PossessionAttachment.syncToClient(host)
        }

        // Fire event
        // PossessionStateChangeEvent.fire(player, null)
    }

    fun getHost(player: Player): Mob? {
        val data = PossessionAttachment.get(player)
        val hostId = data.possessedEntityId ?: return null

        val entity = player.level().getEntity(hostId)
        if (entity == null || entity.isRemoved || entity !is Mob) {
            // Host disappeared, clean up
            data.possessedEntityId = null
            if (player is ServerPlayer) {
                PossessionAttachment.syncPlayerPossession(player)
            }
            return null
        }

        return entity
    }

    fun getPossessor(entity: LivingEntity): Player? {
        val data = PossessionAttachment.getPossessable(entity)
        val possessorId = data.possessorId ?: return null

        return entity.level().players().firstOrNull { it.uuid == possessorId }
    }

    private fun isReadyForPossession(player: Player): Boolean {
        // Check if player is incorporeal/soul form (you'll need to implement this)
        // return !player.isSpectator && RemnantComponent.get(player).isIncorporeal()
        return !player.isSpectator // Simplified for now
    }

    private fun canBePossessedBy(host: Mob, player: Player): Boolean {
        val possessableData = PossessionAttachment.getPossessable(host)
        return !host.isRemoved &&
                host.health > 0 &&
                (possessableData.possessorId == null || possessableData.possessorId == player.uuid)
    }

    private fun shouldTransferInventory(entity: Mob): Boolean {
        // Check if entity has INVENTORY_CARRIERS tag
        // return entity.type.`is`(YourTags.INVENTORY_CARRIERS)
        return false // Implement based on your tags
    }

    private fun shouldTransferEquipment(entity: Mob): Boolean {
        // Check if entity has ITEM_USERS tag
        // return entity.type.`is`(YourTags.ITEM_USERS)
        return false // Implement based on your tags
    }

    private fun isRegularEater(entity: Mob): Boolean {
        // Check if entity has EATERS tag
        // return entity.type.`is`(YourTags.EATERS)
        return false // Implement based on your tags
    }

    private fun transferInventory(player: Player, host: Mob, toHost: Boolean) {
        val possessedData = PossessionAttachment.getPossessedData(host)

        if (toHost) {
            // Player -> Host
            possessedData.dropItems(host)
            val newInventory = OrderedInventory(player.inventory.containerSize)

            for (i in 0 until player.inventory.containerSize) {
                val stack = player.inventory.getItem(i)
                // Check if soulbound (you'll need to implement this)
                if (!isSoulbound(stack)) {
                    newInventory.setItem(i, player.inventory.removeItem(i, Int.MAX_VALUE))
                }
            }

            possessedData.inventory = newInventory
            possessedData.selectedSlot = player.inventory.selected
        } else {
            // Host -> Player
            possessedData.inventory?.let { inv ->
                for (i in 0 until inv.containerSize) {
                    val existing = player.inventory.getItem(i)
                    if (!existing.isEmpty) {
                        player.spawnAtLocation(existing)
                    }
                    player.inventory.setItem(i, inv.removeItem(i, Int.MAX_VALUE))
                }
                player.inventory.selected = possessedData.selectedSlot
            }
            possessedData.inventory = null
        }
    }

    private fun transferEquipment(from: LivingEntity, to: LivingEntity) {
        // Transfer armor and held items
        for (slot in from.armorSlots) {
            to.setItemSlot(slot.equipmentSlot, slot.copy())
            from.setItemSlot(slot.equipmentSlot, ItemStack.EMPTY)
        }

        to.setItemInHand(from.usedItemHand, from.getItemInHand(from.usedItemHand).copy())
        from.setItemInHand(from.usedItemHand, ItemStack.EMPTY)
    }

    private fun transferSoulboundEffects(from: Player, to: Mob) {
        for (effect in from.activeEffects) {
            if (isSoulboundEffect(effect)) {
                to.addEffect(MobEffectInstance(effect))
            }
        }

        // Also transfer all effects from host to player
        for (effect in to.activeEffects) {
            from.addEffect(MobEffectInstance(effect))
        }
    }

    private fun transferSoulboundEffectsBack(from: Mob, to: Player) {
        val effectsToTransfer = from.activeEffects.filter { isSoulboundEffect(it) }
        for (effect in effectsToTransfer) {
            from.removeEffect(effect.effect)
            to.addEffect(MobEffectInstance(effect))
        }
    }

    private fun isSoulbound(stack: ItemStack): Boolean {
        // Implement your soulbinding check
        return false
    }

    private fun isSoulboundEffect(effect: MobEffectInstance): Boolean {
        // Implement your soulbound effect check
        return false
    }

    // Event handlers
    @SubscribeEvent
    fun onEntityTick(event: EntityTickEvent.Post) {
        val entity = event.entity

        // Handle player possession ticking
        if (entity is ServerPlayer) {
            val data = PossessionAttachment.get(entity)

            // Check if player is in spectator mode
            if (entity.isSpectator && data.isPossessionOngoing()) {
                stopPossessing(entity)
            }

            // Handle curing timer
            if (data.isCuring()) {
                if (!data.isPossessionOngoing()) {
                    data.conversionTimer = 0
                } else {
                    data.conversionTimer--
                    if (data.conversionTimer == 0) {
                        finishCuring(entity)
                    }
                }
            }

            // Sync possessed entity state
            getHost(entity)?.let { host ->
                syncPossessedState(entity, host)
            }
        }

        // Handle possessed entity ticking
        if (entity is Mob) {
            getPossessor(entity)?.let { possessor ->
                syncHostWithPossessor(entity, possessor)
            }
        }
    }

    @SubscribeEvent
    fun onLivingDeath(event: LivingDeathEvent) {
        val entity = event.entity

        if (entity is Mob) {
            getPossessor(entity)?.let { possessor ->
                if (possessor is ServerPlayer) {
                    // Handle host death
                    handleHostDeath(possessor, entity, event.source)
                }
            }
        }
    }

    @SubscribeEvent
    fun onEntityJoinLevel(event: EntityJoinLevelEvent) {
        val entity = event.entity

        // Handle possession restoration when entities are loaded
        if (entity is Mob && !entity.level().isClientSide) {
            val possessableData = PossessionAttachment.getPossessable(entity)
            possessableData.possessorId?.let { possessorId ->
                entity.level().players().firstOrNull { it.uuid == possessorId }?.let { possessor ->
                    val playerData = PossessionAttachment.get(possessor)
                    if (playerData.possessedEntityId != entity.uuid) {
                        // Restore possession
                        performPossession(possessor, entity)
                    }
                }
            }
        }
    }

    private fun syncPossessedState(player: Player, host: Mob) {
        // Sync various states from player to host
        host.setOnGround(player.onGround())
        host.isShiftKeyDown = player.isShiftKeyDown
        host.isSprinting = player.isSprinting
        host.absorptionAmount = player.absorptionAmount
        host.fallDistance = 0f // Prevent fall damage on possessed entity
    }

    private fun syncHostWithPossessor(host: Mob, possessor: Player) {
        // Additional syncing if needed
        host.yRot = possessor.yRot
        host.xRot = possessor.xRot
        host.yHeadRot = possessor.yHeadRot
    }

    private fun handleHostDeath(
        possessor: ServerPlayer,
        host: Mob,
        source: net.minecraft.world.damagesource.DamageSource
    ) {
        // Fire event
        // PossessionEvents.HOST_DEATH.fire(possessor, host, source)

        // Handle resurrection or stop possession
        stopPossessing(possessor)

        // Additional death handling
    }

    private fun finishCuring(player: ServerPlayer) {
        getHost(player)?.let { host ->
            // Implement curing logic
            // RemnantComponent.get(player).curePossessed(host)
        }
    }
}
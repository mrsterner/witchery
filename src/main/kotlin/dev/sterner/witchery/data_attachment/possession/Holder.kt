package dev.sterner.witchery.data_attachment.possession

class Holder {

    /*
    package com.yourmod.possession

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.*

object PossessionAttachment {

    val ATTACHMENT_TYPES: DeferredRegister<AttachmentType<*>> =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "yourmod")

    // Attachment for Player entities (PossessionComponent equivalent)
    val PLAYER_POSSESSION = ATTACHMENT_TYPES.register("player_possession") {
        AttachmentType.builder { PlayerPossessionData() }
            .serialize(PlayerPossessionData.CODEC)
            .build()
    }

    // Attachment for LivingEntity (Possessable data)
    val POSSESSABLE = ATTACHMENT_TYPES.register("possessable") {
        AttachmentType.builder { PossessableData() }
            .serialize(PossessableData.CODEC)
            .build()
    }

    // Attachment for PossessedData equivalent
    val POSSESSED_DATA = ATTACHMENT_TYPES.register("possessed_data") {
        AttachmentType.builder { PossessedEntityData() }
            .serialize(PossessedEntityData.CODEC)
            .build()
    }

    // Player possession data (replaces PossessionComponent)
    data class PlayerPossessionData(
        var possessedEntityId: UUID? = null,
        var conversionTimer: Int = 0,
        var previousPossessedUuid: UUID? = null
    ) {
        companion object {
            val CODEC: Codec<PlayerPossessionData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    UUIDUtil.CODEC.optionalFieldOf("possessed_entity_id").forGetter { Optional.ofNullable(it.possessedEntityId) },
                    Codec.INT.fieldOf("conversion_timer").forGetter { it.conversionTimer },
                    UUIDUtil.CODEC.optionalFieldOf("previous_possessed_uuid").forGetter { Optional.ofNullable(it.previousPossessedUuid) }
                ).apply(instance) { possessedId, timer, prevUuid ->
                    PlayerPossessionData(
                        possessedId.orElse(null),
                        timer,
                        prevUuid.orElse(null)
                    )
                }
            }
        }

        fun isPossessionOngoing(): Boolean = possessedEntityId != null

        fun isCuring(): Boolean = conversionTimer > 0
    }

    // Possessable entity data
    data class PossessableData(
        var possessorId: UUID? = null,
        var previousPossessorId: UUID? = null
    ) {
        companion object {
            val CODEC: Codec<PossessableData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    UUIDUtil.CODEC.optionalFieldOf("possessor_id").forGetter { Optional.ofNullable(it.possessorId) },
                    UUIDUtil.CODEC.optionalFieldOf("previous_possessor_id").forGetter { Optional.ofNullable(it.previousPossessorId) }
                ).apply(instance) { possessorId, prevId ->
                    PossessableData(
                        possessorId.orElse(null),
                        prevId.orElse(null)
                    )
                }
            }
        }

        fun isBeingPossessed(): Boolean = possessorId != null
    }

    // Possessed entity inventory/hunger data (replaces PossessedData)
    data class PossessedEntityData(
        var hungerData: CompoundTag? = null,
        var inventory: OrderedInventory? = null,
        var selectedSlot: Int = 0,
        var convertedUnderPossession: Boolean = false
    ) {
        companion object {
            val CODEC: Codec<PossessedEntityData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    CompoundTag.CODEC.optionalFieldOf("hunger_data").forGetter { Optional.ofNullable(it.hungerData) },
                    Codec.INT.fieldOf("selected_slot").forGetter { it.selectedSlot },
                    Codec.BOOL.fieldOf("converted_under_possession").forGetter { it.convertedUnderPossession }
                    // Note: Inventory needs custom serialization
                ).apply(instance) { hunger, slot, converted ->
                    PossessedEntityData(
                        hunger.orElse(null),
                        null, // Will be handled separately
                        slot,
                        converted
                    )
                }
            }
        }

        fun getHungerData(): CompoundTag {
            if (hungerData == null) {
                hungerData = CompoundTag().apply {
                    putInt("foodLevel", 20)
                }
            }
            return hungerData!!
        }

        fun dropItems(entity: Entity) {
            inventory?.let { inv ->
                for (i in 0 until inv.containerSize) {
                    val stack = inv.removeItem(i, Int.MAX_VALUE)
                    if (!stack.isEmpty) {
                        entity.spawnAtLocation(stack)
                    }
                }
            }
        }
    }

    // Extension functions for easy access
    fun get(player: Player): PlayerPossessionData {
        return player.getData(PLAYER_POSSESSION)
    }

    fun getPossessable(entity: LivingEntity): PossessableData {
        return entity.getData(POSSESSABLE)
    }

    fun getPossessedData(entity: Entity): PossessedEntityData {
        return entity.getData(POSSESSED_DATA)
    }

    fun getHost(possessor: Entity): Mob? {
        if (possessor !is Player) return null
        val data = possessor.getExistingData(PLAYER_POSSESSION).orElse(null) ?: return null
        val hostId = data.possessedEntityId ?: return null

        return possessor.level().getEntity(hostId) as? Mob
    }

    // Sync methods
    fun syncToClient(entity: LivingEntity) {
        if (entity.level().isClientSide) return

        val possessableData = entity.getExistingData(POSSESSABLE).orElse(null) ?: return
        val packet = SyncPossessableS2CPayload(entity, possessableData)

        // Send to tracking players
        entity.level().players().forEach { player ->
            if (player is ServerPlayer && player.hasLineOfSight(entity)) {
                player.connection.send(packet)
            }
        }
    }

    fun syncPlayerPossession(player: ServerPlayer) {
        val data = player.getData(PLAYER_POSSESSION)
        val packet = SyncPlayerPossessionS2CPayload(player.uuid, data)
        player.connection.send(packet)
    }
}
     */


    /*
    package com.yourmod.possession

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.EntityTickEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import java.util.*

@EventBusSubscriber(modid = "yourmod")
object PossessionManager {

    private val INHERENT_MOB_SLOWNESS_UUID = UUID.fromString("your-uuid-here")
    private val INHERENT_MOB_SLOWNESS = AttributeModifier(
        INHERENT_MOB_SLOWNESS_UUID,
        "Possessed mob slowness",
        -0.25,
        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
    )

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
        possessableData.possessorId = player.uuid

        // Sync position
        player.teleportTo(host.x, host.y, host.z)
        player.setYRot(host.yRot)
        player.setXRot(host.xRot)

        // Update attributes
        host.getAttribute(Attributes.MOVEMENT_SPEED)?.addTemporaryModifier(INHERENT_MOB_SLOWNESS)

        // Make persistent
        host.isPersistenceRequired = true

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

        val host = player.level().getEntity(hostId) as? Mob ?: return
        val possessableData = PossessionAttachment.getPossessable(host)
        val possessedData = PossessionAttachment.getPossessedData(host)

        // Clear possession state
        playerData.possessedEntityId = null
        playerData.previousPossessedUuid = hostId
        possessableData.possessorId = null
        possessableData.previousPossessorId = player.uuid

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
            host.isSneaking = false
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
        host.isOnGround = player.isOnGround
        host.isSneaking = player.isSneaking
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

    private fun handleHostDeath(possessor: ServerPlayer, host: Mob, source: net.minecraft.world.damagesource.DamageSource) {
        // Fire event
        // PossessionEvents.HOST_DEATH.fire(possessor, host, source)

        // Handle resurrection or stop possession
        stopPossessing(possessor)

        // A
     */


    /*
    package com.yourmod.possession.network

import com.mojang.serialization.Codec
import com.yourmod.possession.PossessionAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import java.util.UUID

// Sync Possessable data (for LivingEntity)
class SyncPossessableS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readNbt()!!)

    constructor(living: LivingEntity, data: PossessionAttachment.PossessableData) : this(CompoundTag().apply {
        putUUID("EntityId", living.uuid)

        PossessionAttachment.PossessableData.CODEC.encodeStart(NbtOps.INSTANCE, data)
            .resultOrPartial { error ->
                println("Error encoding possessable data: $error")
            }
            .ifPresent { put("PossessableData", it) }
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        val level = client.level ?: return
        val entityId = nbt.getUUID("EntityId")

        level.entities.get(entityId)?.let { entity ->
            if (entity is LivingEntity) {
                val dataTag = nbt.getCompound("PossessableData")
                PossessionAttachment.PossessableData.CODEC.parse(NbtOps.INSTANCE, dataTag)
                    .resultOrPartial { error ->
                        println("Error parsing possessable data: $error")
                    }
                    .ifPresent { data ->
                        client.execute {
                            entity.setData(PossessionAttachment.POSSESSABLE, data)
                        }
                    }
            }
        }
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<SyncPossessableS2CPayload> =
            CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath("yourmod", "sync_possessable"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SyncPossessableS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncPossessableS2CPayload(buf) }
            )
    }
}

// Sync Player Possession data
class SyncPlayerPossessionS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readNbt()!!)

    constructor(playerId: UUID, data: PossessionAttachment.PlayerPossessionData) : this(CompoundTag().apply {
        putUUID("PlayerId", playerId)

        PossessionAttachment.PlayerPossessionData.CODEC.encodeStart(NbtOps.INSTANCE, data)
            .resultOrPartial { error ->
                println("Error encoding player possession data: $error")
            }
            .ifPresent { put("PossessionData", it) }
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        val level = client.level ?: return
        val playerId = nbt.getUUID("PlayerId")

        level.players().firstOrNull { it.uuid == playerId }?.let { player ->
            val dataTag = nbt.getCompound("PossessionData")
            PossessionAttachment.PlayerPossessionData.CODEC.parse(NbtOps.INSTANCE, dataTag)
                .resultOrPartial { error ->
                    println("Error parsing player possession data: $error")
                }
                .ifPresent { data ->
                    client.execute {
                        player.setData(PossessionAttachment.PLAYER_POSSESSION, data)

                        // Update camera if this is the local player and possession state changed
                        if (player == client.player) {
                            updateCamera(player, data)
                        }
                    }
                }
        }
    }

    private fun updateCamera(player: Player, data: PossessionAttachment.PlayerPossessionData) {
        val client = Minecraft.getInstance()

        if (data.possessedEntityId != null) {
            // Find the possessed entity and set it as camera
            player.level().getEntity(data.possessedEntityId)?.let { host ->
                if (client.options.cameraType.isFirstPerson && player == client.player) {
                    client.cameraEntity = host
                }
            }
        } else {
            // Reset camera to player
            if (client.cameraEntity != player) {
                client.cameraEntity = player
            }
        }
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<SyncPlayerPossessionS2CPayload> =
            CustomPacketPayload.Type(ResourceLoc
     */


    /*
    package com.yourmod.possession.network

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler
import net.neoforged.neoforge.network.registration.PayloadRegistrar

@EventBusSubscriber(modid = "yourmod", bus = EventBusSubscriber.Bus.MOD)
object PayloadRegistration {

    @SubscribeEvent
    fun registerPayloads(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")

        // Register Possessable sync payload
        registrar.playToClient(
            SyncPossessableS2CPayload.TYPE,
            SyncPossessableS2CPayload.STREAM_CODEC,
            DirectionalPayloadHandler(
                { payload, _ -> payload.handleOnClient() },
                { _, _ -> } // No server handling for S2C
            )
        )

        // Register Player Possession sync payload
        registrar.playToClient(
            SyncPlayerPossessionS2CPayload.TYPE,
            SyncPlayerPossessionS2CPayload.STREAM_CODEC,
            DirectionalPayloadHandler(
                { payload, _ -> payload.handleOnClient() },
                { _, _ -> }
            )
        )

        // Register Possessed Data sync payload
        registrar.playToClient(
            SyncPossessedDataS2CPayload.TYPE,
            SyncPossessedDataS2CPayload.STREAM_CODEC,
            DirectionalPayloadHandler(
                { payload, _ -> payload.handleOnClient() },
                { _, _ -> }
            )
        )

        // Register client-to-server payloads if needed
        // For example, possession request from client
        registrar.playToServer(
            PossessionRequestC2SPayload.TYPE,
            PossessionRequestC2SPayload.STREAM_CODEC,
            DirectionalPayloadHandler(
                { _, _ -> }, // No client handling for C2S
                { payload, context -> payload.handleOnServer(context) }
            )
        )

        registrar.playToServer(
            StopPossessionC2SPayload.TYPE,
            StopPossessionC2SPayload.STREAM_CODEC,
            DirectionalPayloadHandler(
                { _, _ -> },
                { payload, context -> payload.handleOnServer(context) }
            )
        )
    }
     */


    /*
    package com.yourmod.possession.network

import com.yourmod.possession.PossessionManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Mob
import net.neoforged.neoforge.network.handling.IPayloadContext
import java.util.UUID

// Request to start possessing an entity
class PossessionRequestC2SPayload(val targetEntityId: UUID) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readUUID())

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeUUID(targetEntityId)
    }

    fun handleOnServer(context: IPayloadContext) {
        val player = context.player()
        if (player !is ServerPlayer) return

        context.enqueueWork {
            player.level().getEntity(targetEntityId)?.let { entity ->
                if (entity is Mob) {
                    PossessionManager.startPossessing(player, entity)
                }
            }
        }
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<PossessionRequestC2SPayload> =
            CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath("yourmod", "possession_request"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, PossessionRequestC2SPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> PossessionRequestC2SPayload(buf) }
            )
    }
}

// Request to stop possessing
class StopPossessionC2SPayload : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    fun handleOnServer(context: IPayloadContext) {
        val player = context.player()
        if (player !is ServerPlayer) return

        context.enqueueWork {
            PossessionManager.stopPossessing(player)
        }
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<StopPossessionC2SPayload> =
            CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath("yourmod", "stop_possession"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, StopPossessionC2SPayload> =
            StreamCodec.unit(StopPossessionC2SPayload())
    }
}

// Request to start curing
class StartCuringC2SPayload(val cureItem: CompoundTag) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readNbt()!!)

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeNbt(cureItem)
    }

    fun handleOnServer(context: IPayloadContext) {
        val player = context.player()
        if (player !is ServerPlayer) return

        context.enqueueWork {
            PossessionManager.startCuring(player)
        }
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<StartCuringC2SPayload> =
            CustomPacketPayload
     */

    /*
   package com.yourmod.possession

import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

// Extension to PossessionManager for curing functionality
object PossessionCuring {

    fun canBeCured(player: Player, cure: ItemStack): Boolean {
        val host = PossessionManager.getHost(player) ?: return false
        val data = PossessionAttachment.get(player)

        // Check if cure item is valid (you'll need to define UNDEAD_CURES tag)
        // if (!cure.`is`(YourTags.UNDEAD_CURES)) return false

        // Check if host has weakness effect
        if (!host.hasEffect(MobEffects.WEAKNESS)) return false

        // Check if player's remnant component allows curing (you'll need to implement this)
        // return RemnantComponent.get(player).canCurePossessed(host)

        return true // Simplified for now
    }

    fun startCuring(player: Player) {
        if (player.level().isClientSide) return

        val data = PossessionAttachment.get(player)
        val host = PossessionManager.getHost(player) ?: return

        val random = player.random
        data.conversionTimer = random.nextInt(1201) + 2400 // Slightly shorter than villager

        // Remove weakness, add strength
        player.removeEffect(MobEffects.WEAKNESS)
        player.addEffect(MobEffectInstance(
            MobEffects.DAMAGE_BOOST,
            data.conversionTimer,
            0
        ))

        // Play cure sound
        player.level().playSound(
            null,
            player.x + 0.5,
            player.y + 0.5,
            player.z + 0.5,
            SoundEvents.ZOMBIE_VILLAGER_CURE,
            player.soundSource,
            1.0f + random.nextFloat(),
            random.nextFloat() * 0.7f + 0.3f
        )

        // Sync to clients
        if (player is net.minecraft.server.level.ServerPlayer) {
            PossessionAttachment.syncPlayerPossession(player)
        }
    }
}
     */
}
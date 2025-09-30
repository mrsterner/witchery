package dev.sterner.witchery.data_attachment.possession


import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.event.PossessionEvents
import dev.sterner.witchery.api.interfaces.Possessable
import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment

import dev.sterner.witchery.payload.SyncPossessionComponentS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments
import dev.sterner.witchery.registry.WitcheryTags
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.network.PacketDistributor
import java.util.*

object PossessionComponentAttachment {

    val INHERENT_MOB_SLOWNESS_UUID: ResourceLocation = Witchery.id("mob_slowness_modifier")

    val INHERENT_MOB_SLOWNESS: AttributeModifier = AttributeModifier(
        INHERENT_MOB_SLOWNESS_UUID,
        -0.66,
        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
    )

    fun getPossessionData(player: Player): PossessionData {
        return player.getData(WitcheryDataAttachments.PLAYER_POSSESSION)
    }

    fun setPossessionData(player: Player, data: PossessionData) {
        player.setData(WitcheryDataAttachments.PLAYER_POSSESSION, data)
        syncToClient(player)
    }

    fun syncToClient(player: Player) {
        if (player is ServerPlayer) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                SyncPossessionComponentS2CPayload(player.id, getPossessionData(player))
            )
        }
    }

    data class PossessionData(
        var possessedEntityId: Int = -1,
        var possessedEntityUUID: UUID? = null
    ) {
        fun isPossessing(): Boolean = possessedEntityId != -1

        companion object {
            val CODEC: Codec<PossessionData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("possessedEntityId").forGetter { it.possessedEntityId },
                    Codec.STRING.optionalFieldOf("possessedEntityUUID", "")
                        .xmap(
                            { if (it.isEmpty()) null else UUID.fromString(it) },
                            { it?.toString() ?: "" }
                        ).forGetter { it.possessedEntityUUID }
                ).apply(instance) { entityId, uuid ->
                    PossessionData(entityId, uuid)
                }
            }
        }
    }

    class PossessionComponent(private val player: Player) {
        private var possessedEntity: Mob? = null

        fun isPossessionOngoing(): Boolean {
            return this.possessedEntity != null
        }

        fun isReadyForPossession(): Boolean {
            return player.level().isClientSide ||
                    (!player.isSpectator && AfflictionPlayerAttachment.getData(player).isSoulForm())
        }

        fun startPossessing(host: Mob): Boolean {
            if (!isReadyForPossession()) {
                return false
            }


            val result = PossessionEvents.PossessionAttempted(host, player)
            NeoForge.EVENT_BUS.post(result)
            if (result.isCanceled) {
                return false
            }

            val possessable = host as? Possessable ?: return false

            if (!possessable.canBePossessedBy(player)) {
                return false
            }

            startPossessing0(host, possessable)
            return true
        }

        private fun startPossessing0(host: Mob, possessable: Possessable) {
            possessable.setPossessor(null)

            if (!player.level().isClientSide) {
                if (host.type.`is`(WitcheryTags.INVENTORY_CARRIERS)) {
                    PossessedDataAttachment.get(host).moveItems(player.inventory, false)
                }

                if (host.type.`is`(WitcheryTags.ITEM_USERS)) {
                    for (slot in EquipmentSlot.values()) {
                        val stuff: ItemStack = host.getItemBySlot(slot)
                        if (stuff.isEmpty()) {
                            continue
                        }
                        if (!player.getItemBySlot(slot).isEmpty()) {
                            player.spawnAtLocation(stuff, 0.5f)
                        } else {
                            player.setItemSlot(slot, stuff)
                        }
                        host.setItemSlot(slot, ItemStack.EMPTY)
                    }
                }
                for (effect in host.activeEffects) {
                    player.addEffect(MobEffectInstance(effect))
                }

                if (host.type.`is`(WitcheryTags.REGULAR_EATER)) {
                    player.foodData.readAdditionalSaveData(PossessedDataAttachment.get(host).getHungerData())
                }

                host.target = null
            }

            this.possessedEntity = host
            val data = getPossessionData(player)
            data.possessedEntityId = host.id
            data.possessedEntityUUID = host.uuid
            setPossessionData(player, data)

            possessable.setPossessor(player)

            player.copyPosition(host)
            player.refreshDimensions()

            host.playAmbientSound()

            host.setPersistenceRequired()

            val event = PossessionEvents.PossessionStateChange(player, host)
            NeoForge.EVENT_BUS.post(event)
        }

        fun stopPossessing(transfer: Boolean = !player.isCreative) {
            val host = getHost() ?: return

            resetState()
            (host as Possessable).setPossessor(null)

            if (player is ServerPlayer) {
                val vehicle = player.vehicle
                if (vehicle != null) {
                    player.stopRiding()
                    host.startRiding(vehicle)
                }

                if (transfer) {
                    dropEquipment(host, player)
                }

                if (host.type.`is`(WitcheryTags.REGULAR_EATER)) {
                    player.foodData.addAdditionalSaveData(PossessedDataAttachment.get(host).getHungerData())
                }

                host.isSprinting = false
            }
        }

        fun getHost(): Mob? {
            val data = getPossessionData(player)
            if (!data.isPossessing()) {
                return null
            }

            if (possessedEntity != null && !possessedEntity!!.isRemoved) {
                return possessedEntity
            }

            val level = player.level()
            val entity = level.getEntity(data.possessedEntityId)
            if (entity is Mob && entity is Possessable) {
                possessedEntity = entity
                return entity
            }

            if (data.possessedEntityUUID != null && level is ServerLevel) {
                val entityByUUID = level.getEntity(data.possessedEntityUUID!!)
                if (entityByUUID is Mob && entityByUUID is Possessable) {
                    startPossessing(entityByUUID)
                    return entityByUUID
                }
            }

            resetState()
            return null
        }

        private fun resetState() {
            possessedEntity = null
            val data = getPossessionData(player)
            data.possessedEntityId = -1
            data.possessedEntityUUID = null
            setPossessionData(player, data)

            player.refreshDimensions()
            player.airSupply = player.maxAirSupply

            val event = PossessionEvents.PossessionStateChange(player, null)
            NeoForge.EVENT_BUS.post(event)

            if (player is ServerPlayer) {
                player.connection.send(
                    ClientboundUpdateAttributesPacket(player.id, player.attributes.attributesToUpdate)
                )
            }
        }

        fun serverTick() {
            if (player.isSpectator) {
                stopPossessing()
            }
        }

        companion object {
            fun dropEquipment(possessed: LivingEntity, player: ServerPlayer) {
                val event = PossessionEvents.ShouldTransferInventory(player, possessed)

                if (!event.isCanceled) {
                    if (possessed.type.`is`(WitcheryTags.ITEM_USERS)) {
                        for (slot in EquipmentSlot.entries) {
                            val stuff: ItemStack = player.getItemBySlot(slot)
                            if (stuff.isEmpty) {
                                continue
                            }
                            if (!possessed.getItemBySlot(slot).isEmpty()) {
                                possessed.spawnAtLocation(stuff, 0.5f)
                            } else {
                                possessed.setItemSlot(slot, stuff)
                            }
                            player.setItemSlot(slot, ItemStack.EMPTY)
                        }
                    }

                    if (possessed.type.`is`(WitcheryTags.INVENTORY_CARRIERS)) {
                        PossessedDataAttachment.get(possessed).moveItems(player.inventory, true)
                    }

                    player.inventory.dropAll()
                }

                player.removeAllEffects()

                val event2 = PossessionEvents.CleanUpAfterDissociation(player, possessed)
                NeoForge.EVENT_BUS.post(event2)
            }
        }
    }

    fun get(player: Player): PossessionComponent {
        return PossessionComponent(player)
    }
}
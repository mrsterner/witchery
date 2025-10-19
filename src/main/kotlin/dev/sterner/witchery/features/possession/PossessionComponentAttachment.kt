package dev.sterner.witchery.features.possession


import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.api.event.PossessionEvents
import dev.sterner.witchery.core.api.interfaces.Possessable
import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment

import dev.sterner.witchery.network.SyncPossessionComponentS2CPayload
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import dev.sterner.witchery.registry.WitcheryTags
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.EntityTypeTags
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent
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
        var possessedEntityUUID: UUID? = null,
        var curingTimer: Int = -1
    ) {
        fun isPossessing(): Boolean = possessedEntityId != -1
        fun isCuring(): Boolean = curingTimer >= 0

        companion object {
            val CODEC: Codec<PossessionData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("possessedEntityId").forGetter { it.possessedEntityId },
                    Codec.STRING.optionalFieldOf("possessedEntityUUID", "")
                        .xmap(
                            { if (it.isEmpty()) null else UUID.fromString(it) },
                            { it?.toString() ?: "" }
                        ).forGetter { it.possessedEntityUUID },
                    Codec.INT.optionalFieldOf("curingTimer", -1).forGetter { it.curingTimer }
                ).apply(instance) { entityId, uuid, curing ->
                    PossessionData(entityId, uuid, curing)
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

            possessable.setPossessor(null)

            if (!player.level().isClientSide) {
                PossessedDataAttachment.get(host).moveItems(player.inventory, false)

                for (slot in EquipmentSlot.entries) {
                    val stuff: ItemStack = host.getItemBySlot(slot)
                    if (stuff.isEmpty) {
                        continue
                    }
                    if (!player.getItemBySlot(slot).isEmpty) {
                        player.spawnAtLocation(stuff, 0.5f)
                    } else {
                        player.setItemSlot(slot, stuff)
                    }
                    host.setItemSlot(slot, ItemStack.EMPTY)
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
            data.curingTimer = -1
            setPossessionData(player, data)
            possessable.setPossessor(player)

            player.copyPosition(host)
            player.refreshDimensions()

            host.playAmbientSound()

            host.setPersistenceRequired()

            val event = PossessionEvents.PossessionStateChange(player, host)
            NeoForge.EVENT_BUS.post(event)
            return true
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
            data.curingTimer = -1
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
            val data = getPossessionData(player)
            if (data.isCuring() && player is ServerPlayer) {
                if (data.curingTimer > 0) {
                    data.curingTimer--
                    setPossessionData(player, data)

                    if (data.curingTimer % 80 == 0) {
                        val serverLevel = player.level() as ServerLevel
                        serverLevel.sendParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            player.x, player.y + 1, player.z,
                            5,
                            0.5, 0.5, 0.5,
                            0.05
                        )
                    }
                } else {
                    completeCuring(player)
                }
            }
        }

        private fun completeCuring(player: ServerPlayer) {
            val host = getHost()

            stopPossessing(true)

            host?.discard()

            AfflictionPlayerAttachment.smartUpdate(player) {
                withSoulForm(false).withVagrant(false)
            }

            player.level().playSound(
                null,
                player.x, player.y, player.z,
                SoundEvents.ZOMBIE_VILLAGER_CONVERTED,
                SoundSource.PLAYERS,
                1.0f, 1.0f
            )

            val serverLevel = player.level() as ServerLevel
            serverLevel.sendParticles(
                ParticleTypes.HEART,
                player.x, player.y + 1, player.z,
                20,
                0.5, 0.5, 0.5,
                0.1
            )

            player.addEffect(MobEffectInstance(MobEffects.REGENERATION, 20 * 2, 1))
        }

        fun startCuring() {
            val data = getPossessionData(player)
            if (data.isPossessing() && !data.isCuring()) {
                data.curingTimer = CURING_DURATION
                setPossessionData(player, data)

                player.level().playSound(
                    null,
                    player.x, player.y, player.z,
                    SoundEvents.ZOMBIE_VILLAGER_CURE,
                    SoundSource.PLAYERS,
                    1.0f, 0.7f
                )
            }
        }

        companion object {
            private const val CURING_DURATION = 20 * 100

            fun cure(event: LivingEntityUseItemEvent.Finish) {
                val entity = event.entity
                if (entity !is ServerPlayer) return

                val data = getPossessionData(entity)
                if (!data.isPossessing()) return

                val component = get(entity)
                val host = component.getHost() ?: return

                if (!host.type.`is`(EntityTypeTags.UNDEAD)) return

                val item = event.item
                if (!item.`is`(Items.GOLDEN_APPLE) && !item.`is`(Items.ENCHANTED_GOLDEN_APPLE)) return

                if (!entity.hasEffect(MobEffects.WEAKNESS)) return

                component.startCuring()

                entity.removeEffect(MobEffects.WEAKNESS)
            }

            fun dropEquipment(possessed: LivingEntity, player: ServerPlayer) {
                val event = PossessionEvents.ShouldTransferInventory(player, possessed)

                if (!event.isCanceled) {
                    for (slot in EquipmentSlot.entries) {
                        val stuff: ItemStack = player.getItemBySlot(slot)
                        if (stuff.isEmpty) {
                            continue
                        }
                        if (!possessed.getItemBySlot(slot).isEmpty) {
                            possessed.spawnAtLocation(stuff, 0.5f)
                        } else {
                            possessed.setItemSlot(slot, stuff)
                        }
                        player.setItemSlot(slot, ItemStack.EMPTY)
                    }

                    PossessedDataAttachment.get(possessed).moveItems(player.inventory, true)

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
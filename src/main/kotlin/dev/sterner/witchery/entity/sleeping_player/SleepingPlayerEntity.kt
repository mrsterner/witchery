package dev.sterner.witchery.entity.sleeping_player

import com.mojang.authlib.GameProfile
import dev.sterner.witchery.data_attachment.DeathQueueLevelAttachment
import dev.sterner.witchery.data_attachment.ManifestationPlayerAttachment
import dev.sterner.witchery.data_attachment.teleport.TeleportRequest
import dev.sterner.witchery.handler.AccessoryHandler
import dev.sterner.witchery.handler.SleepingPlayerHandler
import dev.sterner.witchery.handler.TeleportQueueHandler
import dev.sterner.witchery.item.TaglockItem
import dev.sterner.witchery.mixin.PlayerInvoker
import dev.sterner.witchery.payload.SpawnSleepingDeathParticleS2CPayload
import dev.sterner.witchery.registry.WitcheryEntityDataSerializers
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryTags
import dev.sterner.witchery.util.WitcheryUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.FluidTags
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.NeoForgeMod
import net.neoforged.neoforge.network.PacketDistributor
import java.util.*
import kotlin.math.max


class SleepingPlayerEntity(level: Level) : Entity(WitcheryEntityTypes.SLEEPING_PLAYER.get(), level) {

    var data = SleepingPlayerData()
    var hurtCounter = 0

    init {
        blocksBuilding = true
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (level() is ServerLevel) {
            hurtCounter++
            entityData.set(HURT_TIME, 10)
            playSound(SoundEvents.PLAYER_HURT)
            var foundPlayer = false

            if (data.resolvableProfile != null) {
                // First, try to find the player in any dimension
                for (serverLevel in level().server!!.allLevels) {
                    val playerUuid = SleepingPlayerHandler.getPlayerFromSleepingUUID(uuid, serverLevel)

                    val player = playerUuid?.let { level().server!!.playerList.getPlayer(it) }
                    if (player != null) {
                        // Create a teleport request with current position and source dimension
                        val teleportRequest = TeleportRequest(
                            player = playerUuid,
                            pos = blockPosition(),
                            chunkPos = ChunkPos(blockPosition()),
                            createdGameTime = level().gameTime,
                            attempts = 0,
                            sourceDimension = player.level().dimension() // Store source dimension
                        )

                        TeleportQueueHandler.addRequest(level() as ServerLevel, teleportRequest)

                        // Update player manifestation data if applicable
                        val manifestationData = ManifestationPlayerAttachment.getData(player)
                        if (manifestationData.manifestationTimer > 0) {
                            ManifestationPlayerAttachment.setData(
                                player,
                                ManifestationPlayerAttachment.Data(manifestationData.hasRiteOfManifestation, 0)
                            )
                        }

                        // Notify player they're being pulled back
                        player.sendSystemMessage(Component.translatable("witchery.message.body_hurt"))

                        foundPlayer = true
                        break
                    }
                }
            }

            // Handle death after too much damage
            if (hurtCounter > 8) {
                if (!foundPlayer && data.resolvableProfile?.id != null && data.resolvableProfile!!.id.isPresent) {
                    // Queue death event for player who couldn't be found
                    DeathQueueLevelAttachment.addDeathToQueue(level() as ServerLevel, data.resolvableProfile!!.id.get())
                }

                // Drop all inventory contents
                dropInventory()

                // Remove from sleeping player map
                SleepingPlayerHandler.removeBySleepingUUID(uuid, level() as ServerLevel)

                // Spawn death particles
                PacketDistributor.sendToPlayersTrackingEntity(
                    this, SpawnSleepingDeathParticleS2CPayload(
                        this.getRandomX(1.5),
                        this.randomY,
                        this.getRandomZ(1.5)
                    )
                )

                // Remove entity
                this.remove(RemovalReason.KILLED)
            }
        }

        return super.hurt(source, amount)
    }

    private fun dropInventory() {
        Containers.dropContents(level(), blockPosition(), data.mainInventory)
        Containers.dropContents(level(), blockPosition(), data.armorInventory)
        Containers.dropContents(level(), blockPosition(), data.offHandInventory)
        Containers.dropContents(level(), blockPosition(), data.extraInventory)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(UUID, Optional.empty())
        builder.define(NAME, "")
        builder.define(EQUIPMENT, NonNullList.withSize(EquipmentSlot.entries.size, ItemStack.EMPTY))
        builder.define(MODEL, 0.toByte())
        builder.define(FACEPLANT, false)
        builder.define(HURT_TIME, 0)
        builder.define(RESOLVEABLE, ResolvableProfile(GameProfile(UUID(0, 0), "")))
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        if (compound.contains("Data")) {
            data = SleepingPlayerData.readNbt(compound.getCompound("Data"), this.registryAccess())
        }

        entityData.set(RESOLVEABLE, data.resolvableProfile!!)
        setEquipment(data.equipment)
        setSleepingModel(data.model)
        setFaceplant(compound.getBoolean("Faceplanted"))
        hurtCounter = compound.getInt("HurtCounter")
        entityData.set(HURT_TIME, compound.getShort("HurtTime").toInt())
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.put("Data", data.writeNbt(this.registryAccess()))
        compound.putBoolean("Faceplanted", isFaceplanted())
        compound.putInt("HurtCounter", hurtCounter)
        compound.putShort("HurtTime", entityData.get(HURT_TIME).toShort())
    }

    override fun tick() {
        super.tick()
        if (!isNoGravity) {
            var motionY = deltaMovement.y - 0.0625

            if (isEyeInFluidType(NeoForgeMod.WATER_TYPE.value())) {
                motionY = if (deltaMovement.y < 0.0) {
                    deltaMovement.y + 0.015
                } else {
                    deltaMovement.y + if (deltaMovement.y < 0.03) 0.0005 else 0.0
                }
            }

            setDeltaMovement(deltaMovement.x * 0.75, motionY.coerceAtLeast(-2.0), deltaMovement.z * 0.75)
            move(MoverType.SELF, deltaMovement)
        }
        if (this.entityData.get(HURT_TIME) > 0) {
            entityData.set(HURT_TIME, entityData.get(HURT_TIME) - 1)
        }
        if (!level().isClientSide) {
            if (hurtCounter > 0) {
                if (level().gameTime % 100 == 0L) {
                    hurtCounter--
                }
            }

            if (level().gameTime % 10 == 0L) {
                val level = level() as? ServerLevel ?: return
                val sleepingUUID = uuid

                val playerUUID = SleepingPlayerHandler.getPlayerFromSleepingUUID(sleepingUUID, level)
                if (playerUUID != null) {
                    val currentPos = blockPosition()
                    val sleepingData = SleepingPlayerHandler.getPlayerFromSleeping(playerUUID, level)

                    if (sleepingData != null && sleepingData.pos != currentPos) {
                        SleepingPlayerHandler.add(playerUUID, sleepingUUID, currentPos, level)
                    }
                }
            }
        }
    }

    fun getSleepingUUID(): Optional<UUID> {
        return entityData.get(UUID)
    }

    fun setSleepingUUID(uuid: UUID?) {
        if (uuid == null) {
            entityData.set(UUID, Optional.empty())
        } else {
            entityData.set(UUID, Optional.of(uuid))
        }
    }


    fun getEquipment(): NonNullList<ItemStack> {
        return entityData.get(EQUIPMENT)
    }

    fun setEquipment(equipment: NonNullList<ItemStack>) {
        entityData.set(EQUIPMENT, equipment)
    }

    fun getSleepingModel(): Byte {
        return entityData.get(MODEL)
    }

    fun setSleepingModel(model: Byte) {
        entityData.set(MODEL, model)
    }

    fun isFaceplanted(): Boolean {
        return entityData.get(FACEPLANT)
    }

    fun setFaceplant(faceplant: Boolean) {
        entityData.set(FACEPLANT, faceplant)
    }

    override fun interactAt(player: Player, vec: Vec3, hand: InteractionHand): InteractionResult {
        if (player.level() is ServerLevel && hand == InteractionHand.MAIN_HAND) {
            val itemStack = player.mainHandItem
            if (itemStack.`is`(WitcheryItems.BONE_NEEDLE.get()) && player.offhandItem.`is`(Items.GLASS_BOTTLE)) {
                val taglock = WitcheryItems.TAGLOCK.get().defaultInstance
                TaglockItem.bindSleepingPlayer(this, taglock)

                WitcheryUtil.addItemToInventoryAndConsume(player, InteractionHand.OFF_HAND, taglock)
                level().playSound(
                    null,
                    BlockPos.containing(vec),
                    SoundEvents.EXPERIENCE_ORB_PICKUP,
                    SoundSource.BLOCKS,
                    0.5f,
                    1.0f
                )
                return InteractionResult.SUCCESS
            }
        }

        return super.interactAt(player, vec, hand)
    }

    override fun isPickable(): Boolean {
        return true
    }

    companion object {

        fun createFromPlayer(player: Player, sleepingPlayerBuilder: SleepingPlayerData): SleepingPlayerEntity {
            val entity = SleepingPlayerEntity(player.level())
            entity.data = sleepingPlayerBuilder
            entity.data.resolvableProfile = (sleepingPlayerBuilder.resolvableProfile)
            entity.setEquipment(sleepingPlayerBuilder.equipment)
            entity.setPos(player.x, max(player.y + 0.2f, player.level().minBuildHeight.toDouble()), player.z)
            entity.yRot = player.yRot

            val playerModeCustomisation: EntityDataAccessor<Byte> = PlayerInvoker.getPlayerModeCustomisationAccessor()
            entity.setSleepingModel(player.entityData.get(playerModeCustomisation))
            if (player.level() is ServerLevel) {
                val serverLevel = player.level() as ServerLevel
                SleepingPlayerHandler.add(player.uuid, entity.uuid, player.blockPosition(), serverLevel)
            }

            return entity
        }

        private fun insertOrDrop(
            player: Player,
            inventory: NonNullList<ItemStack>,
            playerInv: NonNullList<ItemStack>
        ) {
            for (i in 0 until inventory.size) {
                val stack: ItemStack = inventory[i]
                if (stack.isEmpty) {
                    continue
                }

                val playerStack = playerInv[i]

                if (playerStack.isEmpty) {
                    playerInv[i] = stack.copy()
                    inventory[i] = ItemStack.EMPTY
                } else {
                    Containers.dropItemStack(player.level(), player.x, player.y, player.z, stack.copy())
                    inventory[i] = ItemStack.EMPTY
                }
            }
        }

        fun replaceWithPlayer(player: Player, sleepingPlayerEntity: SleepingPlayerEntity) {
            val itemsToKeep = mutableListOf<ItemStack>()
            val armorToKeep = mutableListOf<ItemStack>()

            val charmStack: ItemStack? = AccessoryHandler.checkNoConsume(player, WitcheryItems.DREAMWEAVER_CHARM.get())
            if (charmStack != null) {
                for (armor in player.armorSlots) {
                    armorToKeep.add(armor)
                }
            }
            for (i in 0 until player.inventory.containerSize) {
                val itemStack = player.inventory.getItem(i)

                if (itemStack.`is`(WitcheryTags.FROM_SPIRIT_WORLD_TRANSFERABLE)) {
                    itemsToKeep.add(itemStack.copy())
                }
            }

            player.inventory.clearContent()

            for (item in itemsToKeep) {
                player.inventory.add(item)
            }
            for (armor in armorToKeep) {
                val slot = player.getEquipmentSlotForItem(armor)
                player.setItemSlot(slot, armor)
            }

            insertOrDrop(player, sleepingPlayerEntity.data.mainInventory, player.inventory.items)
            insertOrDrop(player, sleepingPlayerEntity.data.armorInventory, player.inventory.armor)
            insertOrDrop(player, sleepingPlayerEntity.data.offHandInventory, player.inventory.offhand)

            sleepingPlayerEntity.discard()
        }

        val RESOLVEABLE =
            SynchedEntityData.defineId(SleepingPlayerEntity::class.java, WitcheryEntityDataSerializers.RESOLVABLE.get())
        val UUID = SynchedEntityData.defineId(SleepingPlayerEntity::class.java, EntityDataSerializers.OPTIONAL_UUID)
        val NAME = SynchedEntityData.defineId(SleepingPlayerEntity::class.java, EntityDataSerializers.STRING)
        val EQUIPMENT =
            SynchedEntityData.defineId(SleepingPlayerEntity::class.java, WitcheryEntityDataSerializers.INVENTORY.get())
        val MODEL = SynchedEntityData.defineId(SleepingPlayerEntity::class.java, EntityDataSerializers.BYTE)
        val FACEPLANT = SynchedEntityData.defineId(SleepingPlayerEntity::class.java, EntityDataSerializers.BOOLEAN)
        val HURT_TIME = SynchedEntityData.defineId(SleepingPlayerEntity::class.java, EntityDataSerializers.INT)
    }
}
package dev.sterner.witchery.content.entity.player_shell

import dev.sterner.witchery.core.api.entity.PlayerShellEntity
import dev.sterner.witchery.features.misc.DeathQueueLevelAttachment
import dev.sterner.witchery.features.spirit_world.ManifestationPlayerAttachment
import dev.sterner.witchery.core.api.TeleportRequest
import dev.sterner.witchery.features.misc.AccessoryHandler
import dev.sterner.witchery.features.spirit_world.SleepingPlayerHandler
import dev.sterner.witchery.features.misc.TeleportQueueHandler
import dev.sterner.witchery.network.SpawnSleepingDeathParticleS2CPayload
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.registry.WitcheryTags
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.Containers
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.neoforged.neoforge.network.PacketDistributor


class SleepingPlayerEntity(level: Level) : PlayerShellEntity(WitcheryEntityTypes.SLEEPING_PLAYER.get(), level) {

    init {
        shellType = ShellType.SLEEPING
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (level() is ServerLevel) {
            hurtCounter++
            entityData.set(HURT_TIME, 10)
            playSound(SoundEvents.PLAYER_HURT)
            var foundPlayer = false

            if (data.resolvableProfile != null) {

                for (serverLevel in level().server!!.allLevels) {
                    val playerUuid = SleepingPlayerHandler.getPlayerFromSleepingUUID(uuid, serverLevel)

                    val player = playerUuid?.let { level().server!!.playerList.getPlayer(it) }
                    if (player != null) {

                        val teleportRequest = TeleportRequest(
                            player = playerUuid,
                            pos = blockPosition(),
                            chunkPos = ChunkPos(blockPosition()),
                            createdGameTime = level().gameTime,
                            attempts = 0,
                            sourceDimension = player.level().dimension()
                        )

                        TeleportQueueHandler.addRequest(level() as ServerLevel, teleportRequest)


                        val manifestationData = ManifestationPlayerAttachment.getData(player)
                        if (manifestationData.manifestationTimer > 0) {
                            ManifestationPlayerAttachment.setData(
                                player,
                                ManifestationPlayerAttachment.Data(manifestationData.hasRiteOfManifestation, 0)
                            )
                        }

                        player.sendSystemMessage(Component.translatable("witchery.message.body_hurt"))

                        foundPlayer = true
                        break
                    }
                }
            }

            if (hurtCounter > 8) {
                if (!foundPlayer && data.resolvableProfile?.id != null && data.resolvableProfile!!.id.isPresent) {
                    DeathQueueLevelAttachment.addDeathToQueue(level() as ServerLevel, data.resolvableProfile!!.id.get())
                }

                dropInventory()

                SleepingPlayerHandler.removeBySleepingUUID(uuid, level() as ServerLevel)

                PacketDistributor.sendToPlayersTrackingEntity(
                    this, SpawnSleepingDeathParticleS2CPayload(
                        this.getRandomX(1.5),
                        this.randomY,
                        this.getRandomZ(1.5)
                    )
                )

                this.remove(RemovalReason.KILLED)
            }
        }

        return super.hurt(source, amount)
    }

    override fun mergeSoulWithShell(player: Player) {

    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(FACEPLANT, false)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        setFaceplant(compound.getBoolean("Faceplanted"))
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putBoolean("Faceplanted", isFaceplanted())
    }

    override fun tick() {
        super.tick()
        if (!level().isClientSide) {
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

    override fun handleSleepingDamage(
        source: DamageSource,
        amount: Float
    ): Boolean {
        if (data.resolvableProfile != null) {
            for (serverLevel in level().server!!.allLevels) {
                val playerUuid = SleepingPlayerHandler.getPlayerFromSleepingUUID(uuid, serverLevel)
                val player = playerUuid?.let { level().server!!.playerList.getPlayer(it) }

                if (player != null) {
                    val teleportRequest = TeleportRequest(
                        player = playerUuid,
                        pos = blockPosition(),
                        chunkPos = ChunkPos(blockPosition()),
                        createdGameTime = level().gameTime,
                        attempts = 0,
                        sourceDimension = player.level().dimension()
                    )

                    TeleportQueueHandler.addRequest(level() as ServerLevel, teleportRequest)

                    val manifestationData = ManifestationPlayerAttachment.getData(player)
                    if (manifestationData.manifestationTimer > 0) {
                        ManifestationPlayerAttachment.setData(
                            player,
                            ManifestationPlayerAttachment.Data(manifestationData.hasRiteOfManifestation, 0)
                        )
                    }

                    player.sendSystemMessage(Component.translatable("witchery.message.body_hurt"))
                    break
                }
            }
        }
        return super.handleSleepingDamage(source, amount)
    }

    fun isFaceplanted(): Boolean {
        return entityData.get(FACEPLANT)
    }

    fun setFaceplant(faceplant: Boolean) {
        entityData.set(FACEPLANT, faceplant)
    }

    override fun isPickable(): Boolean {
        return true
    }

    companion object {


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

        val FACEPLANT = SynchedEntityData.defineId(SleepingPlayerEntity::class.java, EntityDataSerializers.BOOLEAN)
    }
}
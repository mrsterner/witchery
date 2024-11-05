package dev.sterner.witchery.entity

import dev.sterner.witchery.api.SleepingPlayerData
import dev.sterner.witchery.item.BoneNeedleItem.Companion.addItemToInventoryAndConsume
import dev.sterner.witchery.item.TaglockItem
import dev.sterner.witchery.platform.SleepingPlayerLevelAttachment
import dev.sterner.witchery.registry.WitcheryEntityDataSerializers
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryTags
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.FluidTags
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import java.util.*
import kotlin.math.max


class SleepingPlayerEntity(level: Level) : Entity(WitcheryEntityTypes.SLEEPING_PLAYER.get(), level) {

    var data = SleepingPlayerData()

    init {
        blocksBuilding = true
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(UUID, Optional.empty())
        builder.define(NAME, "")
        builder.define(EQUIPMENT, NonNullList.withSize(EquipmentSlot.entries.size,  ItemStack.EMPTY))
        builder.define(MODEL, 0.toByte())
        builder.define(FACEPLANT, false)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        if (compound.contains("Data")) {
            data = SleepingPlayerData.readNbt(compound.getCompound("Data"), this.registryAccess())
        }

        setEquipment(data.equipment)
        setSleepingModel(data.model)
        setFaceplant(compound.getBoolean("Faceplanted"))
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.put("Data", data.writeNbt(this.registryAccess()))
        compound.putBoolean("Faceplanted", isFaceplanted())
    }

    override fun tick() {
        super.tick()

        if (!isNoGravity) {
            var motionY = deltaMovement.y - 0.0625

            if (isEyeInFluid(FluidTags.WATER)) {
                motionY = if (deltaMovement.y < 0.0) {
                    deltaMovement.y + 0.015
                } else {
                    deltaMovement.y + if (deltaMovement.y < 0.03) 0.0005 else 0.0
                }
            }

            setDeltaMovement(deltaMovement.x * 0.75, motionY.coerceAtLeast(-2.0), deltaMovement.z * 0.75)
            move(MoverType.SELF, deltaMovement)
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

                addItemToInventoryAndConsume(player, InteractionHand.OFF_HAND, taglock)
                level().playSound(null, BlockPos.containing(vec), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5f, 1.0f)
                return InteractionResult.SUCCESS
            }
        }

        return super.interactAt(player, vec, hand)
    }

    override fun isPickable(): Boolean {
        return true
    }

    companion object {

        fun createFromPlayer(player: Player, sleepingPlayerBuilder: SleepingPlayerData) : SleepingPlayerEntity {
            val entity = SleepingPlayerEntity(player.level())
            entity.data = sleepingPlayerBuilder
            entity.data.resolvableProfile = (sleepingPlayerBuilder.resolvableProfile)
            entity.setEquipment(sleepingPlayerBuilder.equipment)
            entity.setPos(player.x, max(player.y + 0.2f, player.level().minBuildHeight.toDouble()), player.z)
            entity.yRot = player.yRot
            entity.setSleepingModel(player.entityData.get(Player.DATA_PLAYER_MODE_CUSTOMISATION))
            if (player.level() is ServerLevel) {
                val serverLevel = player.level() as ServerLevel
                SleepingPlayerLevelAttachment.add(player.uuid, entity.uuid, serverLevel)
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

            for (i in 0 until player.inventory.containerSize) {
                val itemStack = player.inventory.getItem(i)

                if (itemStack.`is`(WitcheryTags.SPIRIT_WORLD_TRANSFERABLE)) {
                    itemsToKeep.add(itemStack.copy())
                }
            }

            player.inventory.clearContent()

            for (item in itemsToKeep) {
                player.inventory.add(item)
            }

            insertOrDrop(player, sleepingPlayerEntity.data.mainInventory, player.inventory.items)
            insertOrDrop(player, sleepingPlayerEntity.data.armorInventory, player.inventory.armor)
            insertOrDrop(player, sleepingPlayerEntity.data.offHandInventory, player.inventory.offhand)

            sleepingPlayerEntity.discard()
        }

        val UUID = SynchedEntityData.defineId(SleepingPlayerEntity::class.java, EntityDataSerializers.OPTIONAL_UUID)
        val NAME = SynchedEntityData.defineId(SleepingPlayerEntity::class.java, EntityDataSerializers.STRING)
        val EQUIPMENT = SynchedEntityData.defineId(SleepingPlayerEntity::class.java, WitcheryEntityDataSerializers.INVENTORY)
        val MODEL = SynchedEntityData.defineId(SleepingPlayerEntity::class.java, EntityDataSerializers.BYTE)
        val FACEPLANT = SynchedEntityData.defineId(SleepingPlayerEntity::class.java, EntityDataSerializers.BOOLEAN)

    }
}
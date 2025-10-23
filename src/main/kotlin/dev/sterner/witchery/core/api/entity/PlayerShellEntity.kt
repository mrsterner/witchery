package dev.sterner.witchery.core.api.entity

import com.mojang.authlib.GameProfile
import dev.sterner.witchery.content.entity.player_shell.SleepingPlayerEntity
import dev.sterner.witchery.content.entity.player_shell.SoulShellPlayerEntity
import dev.sterner.witchery.content.item.TaglockItem
import dev.sterner.witchery.features.misc.DeathQueueLevelAttachment
import dev.sterner.witchery.core.registry.WitcheryEntityDataSerializers
import dev.sterner.witchery.features.spirit_world.SleepingPlayerHandler
import dev.sterner.witchery.network.SpawnSleepingDeathParticleS2CPayload
import dev.sterner.witchery.network.SyncSleepingShellS2CPayload
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.util.WitcheryUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.NeoForgeMod
import net.neoforged.neoforge.network.PacketDistributor
import java.util.*
import kotlin.math.max

abstract class PlayerShellEntity(
    entityType: EntityType<out PlayerShellEntity>,
    level: Level
) : Entity(entityType, level) {

    var data = PlayerShellData()
    var hurtCounter = 0
    protected var shellType: ShellType = ShellType.SLEEPING


    init {
        blocksBuilding = true
    }

    enum class ShellType {
        SLEEPING,
        SOUL
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (level() is ServerLevel && shellType == ShellType.SLEEPING) {
            return handleSleepingDamage(source, amount)
        }

        return super.hurt(source, amount)
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
        }
    }

    open fun handleSleepingDamage(source: DamageSource, amount: Float): Boolean {
        hurtCounter++
        entityData.set(HURT_TIME, 10)
        playSound(SoundEvents.PLAYER_HURT)

        if (hurtCounter > 8) {
            handleShellDeath()
        }

        return true
    }

    open fun handleShellDeath() {
        dropInventory()

        if (shellType == ShellType.SLEEPING) {
            SleepingPlayerHandler.removeBySleepingUUID(uuid, level() as ServerLevel)

            if (data.resolvableProfile?.id != null && data.resolvableProfile!!.id.isPresent) {
                DeathQueueLevelAttachment.addDeathToQueue(level() as ServerLevel, data.resolvableProfile!!.id.get())
            }
        }

        PacketDistributor.sendToPlayersTrackingEntity(
            this, SpawnSleepingDeathParticleS2CPayload(
                this.getRandomX(1.5),
                this.randomY,
                this.getRandomZ(1.5)
            )
        )

        this.remove(RemovalReason.KILLED)
    }

    protected fun dropInventory() {
        Containers.dropContents(level(), blockPosition(), data.mainInventory)
        Containers.dropContents(level(), blockPosition(), data.armorInventory)
        Containers.dropContents(level(), blockPosition(), data.offHandInventory)
        Containers.dropContents(level(), blockPosition(), data.extraInventory)
    }

    override fun interactAt(player: Player, vec: Vec3, hand: InteractionHand): InteractionResult {
        if (player.level() is ServerLevel && hand == InteractionHand.MAIN_HAND) {

            if (shellType == ShellType.SLEEPING) {
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


        }

        return super.interactAt(player, vec, hand)
    }

    abstract fun mergeSoulWithShell(player: Player)

    fun getOriginalUUID(): Optional<UUID> {
        return entityData.get(UUID)
    }

    fun setOriginalUUID(uuid: UUID?) {
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

    fun getModel(): Byte {
        return entityData.get(MODEL)
    }

    fun setModel(model: Byte) {
        entityData.set(MODEL, model)
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
            data = PlayerShellData.readNbt(compound.getCompound("Data"), this.registryAccess())
        }
        entityData.set(RESOLVEABLE, data.resolvableProfile!!)
        setEquipment(data.equipment)
        hurtCounter = compound.getInt("HurtCounter")
        entityData.set(HURT_TIME, compound.getShort("HurtTime").toInt())
        setModel(compound.getByte("Model"))
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.put("Data", data.writeNbt(this.registryAccess()))
        compound.put(
            "d",
            ResolvableProfile.CODEC.encodeStart(NbtOps.INSTANCE, entityData.get(RESOLVEABLE)).resultOrPartial().get()
        )
        compound.putInt("HurtCounter", hurtCounter)
        compound.putShort("HurtTime", entityData.get(HURT_TIME).toShort())
        compound.putByte("Model", getModel())
    }

    companion object {

        @JvmStatic
        private fun <T : PlayerShellEntity> createPlayerShell(
            player: Player,
            factory: (Level) -> T,
            extraInit: ((T) -> Unit)? = null
        ): T {
            val entity = factory(player.level())
            val data = PlayerShellData.fromPlayer(player)

            entity.data = data
            entity.setEquipment(data.equipment)
            entity.setOriginalUUID(player.uuid)
            entity.setPos(
                player.x,
                max(player.y + 0.2f, player.level().minBuildHeight.toDouble()),
                player.z
            )
            entity.yRot = player.yRot
            entity.setModel(data.model)

            extraInit?.invoke(entity)

            PacketDistributor.sendToAllPlayers(SyncSleepingShellS2CPayload(entity))

            if (!data.resolvableProfile!!.isResolved) {
                data.resolvableProfile!!.resolve().thenAccept { resolved ->
                    entity.data.resolvableProfile = resolved
                    PacketDistributor.sendToAllPlayers(SyncSleepingShellS2CPayload(entity))
                }
            }

            return entity
        }

        @JvmStatic
        fun createSleepFromPlayer(player: Player): SleepingPlayerEntity =
            createPlayerShell(player, { SleepingPlayerEntity(it) }) { entity ->
                if (player.level() is ServerLevel) {
                    SleepingPlayerHandler.add(
                        player.uuid,
                        entity.getOriginalUUID().orElse(player.uuid),
                        player.blockPosition(),
                        player.level() as ServerLevel
                    )
                }
            }


        @JvmStatic
        fun createShellFromPlayer(player: Player): SoulShellPlayerEntity =
            createPlayerShell(player, { SoulShellPlayerEntity(it) })


        val RESOLVEABLE =
            SynchedEntityData.defineId(PlayerShellEntity::class.java, WitcheryEntityDataSerializers.RESOLVABLE.get())
        val UUID = SynchedEntityData.defineId(PlayerShellEntity::class.java, EntityDataSerializers.OPTIONAL_UUID)
        val NAME = SynchedEntityData.defineId(PlayerShellEntity::class.java, EntityDataSerializers.STRING)
        val EQUIPMENT =
            SynchedEntityData.defineId(PlayerShellEntity::class.java, WitcheryEntityDataSerializers.INVENTORY.get())
        val MODEL = SynchedEntityData.defineId(PlayerShellEntity::class.java, EntityDataSerializers.BYTE)
        val FACEPLANT = SynchedEntityData.defineId(PlayerShellEntity::class.java, EntityDataSerializers.BOOLEAN)
        val HURT_TIME = SynchedEntityData.defineId(PlayerShellEntity::class.java, EntityDataSerializers.INT)
    }
}
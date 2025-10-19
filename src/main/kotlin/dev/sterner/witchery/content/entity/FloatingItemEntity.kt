package dev.sterner.witchery.content.entity

import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.stats.Stats
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level


class FloatingItemEntity(level: Level) : Entity(WitcheryEntityTypes.FLOATING_ITEM.get(), level) {

    val bobOffs: Float = random.nextFloat() * (Math.PI.toFloat()) * 2.0f
    var age: Int = 0
    private var maxAge: Int = 20 * 60 * 5

    init {
        noPhysics = false
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(DATA_ITEM_STACK, ItemStack.EMPTY)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        age = compound.getInt("age")
        maxAge = compound.getInt("maxAge")


        if (compound.contains("Item", 10)) {
            val compoundTag = compound.getCompound("Item")
            this.setItem(ItemStack.parse(this.registryAccess(), compoundTag).orElse(ItemStack.EMPTY) as ItemStack)
        } else {
            this.setItem(ItemStack.EMPTY)
        }

        if (this.getItem().isEmpty) {
            this.discard()
        }
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putInt("age", age)
        compound.putInt("maxAge", maxAge)
        if (!this.getItem().isEmpty) {
            compound.put("Item", this.getItem().save(this.registryAccess()))
        }
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {
        if (!player.level().isClientSide) {
            val handStack: ItemStack = player.mainHandItem
            if (handStack.isEmpty && hand === InteractionHand.MAIN_HAND) {
                player.setItemInHand(hand, getItem())
                this.remove(RemovalReason.DISCARDED)
                return InteractionResult.CONSUME
            }
        }
        return super.interact(player, hand)
    }

    override fun playerTouch(player: Player) {
        if (!level().isClientSide) {
            val itemStack: ItemStack = this.getItem()
            val item = itemStack.item
            val i = itemStack.count
            if (player.inventory.add(
                    itemStack
                )
            ) {
                player.take(this, i)
                if (itemStack.isEmpty) {
                    this.discard()
                    itemStack.count = i
                }
                player.level().playSound(
                    null, player.x, player.y, player.z, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f,
                    ((player.random.nextFloat() - player.random.nextFloat()) * 0.7f + 1.0f) * 2.0f
                )

                player.awardStat(Stats.ITEM_PICKED_UP[item], i)
            }
        }
    }

    override fun tick() {
        super.tick()
        baseTick()

        age++
        if (age > maxAge) {
            discard()
        }

        this.checkInsideBlocks()
        val movement = deltaMovement
        val nextX = x + movement.x
        val nextY = y + movement.y
        val nextZ = z + movement.z
        val distance = movement.horizontalDistance()

        val xRot: Float = lerpRotation(
            this.xRotO,
            (Mth.atan2(movement.y, distance) * (180f / Math.PI.toFloat()).toDouble()).toFloat()
        )
        val yRot: Float = lerpRotation(
            this.yRotO,
            (Mth.atan2(movement.x, movement.z) * (180f / Math.PI.toFloat()).toDouble()).toFloat()
        )
        setXRot(xRot)
        setYRot(yRot)
        setPos(nextX, nextY, nextZ)
        ProjectileUtil.rotateTowardsMovement(this, 0.2f)
    }

    override fun lerpMotion(pX: Double, pY: Double, pZ: Double) {
        this.setDeltaMovement(pX, pY, pZ)
        this.setOldPosAndRot()
    }

    private fun lerpRotation(currentRotation: Float, targetRotation: Float): Float {
        var current = currentRotation
        while (targetRotation - current < -180.0f) {
            current -= 360.0f
        }

        while (targetRotation - current >= 180.0f) {
            current += 360.0f
        }

        return Mth.lerp(0.2f, current, targetRotation)
    }

    override fun isNoGravity(): Boolean {
        return true
    }

    override fun fireImmune(): Boolean {
        return true
    }

    override fun isAttackable(): Boolean {
        return false
    }

    fun getItem(): ItemStack {
        return getEntityData().get(DATA_ITEM_STACK)
    }

    fun setItem(stack: ItemStack) {
        getEntityData().set(DATA_ITEM_STACK, stack)
    }

    override fun onSyncedDataUpdated(dataAccessor: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(dataAccessor)
        if (DATA_ITEM_STACK == dataAccessor) {
            getItem().entityRepresentation = this
        }
    }

    fun getSpin(partialTick: Float): Float {
        return (this.age.toFloat() + partialTick) / 20.0f + this.bobOffs
    }

    override fun isPickable(): Boolean {
        return true
    }

    companion object {
        val DATA_ITEM_STACK: EntityDataAccessor<ItemStack> = SynchedEntityData.defineId(
            FloatingItemEntity::class.java, EntityDataSerializers.ITEM_STACK
        )
    }
}
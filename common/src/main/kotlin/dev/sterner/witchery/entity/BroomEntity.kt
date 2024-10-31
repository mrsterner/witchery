package dev.sterner.witchery.entity

import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.client.Minecraft
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.Vec3


class BroomEntity(level: Level) : Entity(WitcheryEntityTypes.BROOM.get(), level) {

    private var deltaRotation = 0f
    private var lerpSteps = 0
    private var lerpX = 0.0
    private var lerpY = 0.0
    private var lerpZ = 0.0
    private var lerpYRot = 0.0
    private var lerpXRot = 0.0
    private var inputLeft = false
    private var inputRight = false
    private var inputUp = false
    private var inputDown = false
    private var inputShift = false
    private var inputJump = false

    init {
        this.setPos(x, y, z)
    }

    override fun causeFallDamage(fallDistance: Float, multiplier: Float, source: DamageSource): Boolean {
        return false
    }

    override fun isPickable(): Boolean {
        return true
    }


    override fun readAdditionalSaveData(compound: CompoundTag) {

    }

    override fun addAdditionalSaveData(compound: CompoundTag) {

    }

    override fun getControllingPassenger(): LivingEntity? {
        return if (this.firstPassenger is LivingEntity) this.firstPassenger as LivingEntity else super.getControllingPassenger()
    }

    override fun getPassengerAttachmentPoint(entity: Entity, dimensions: EntityDimensions, partialTick: Float): Vec3 {
        return Vec3(
            0.0,
            (dimensions.height() / 3.0f).toDouble(),
           0.0
        )
            .yRot(-this.yRot * (Math.PI / 180.0).toFloat())
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {
        val interactionResult = super.interact(player, hand)
        return if (interactionResult != InteractionResult.PASS) {
            interactionResult
        } else if (player.isSecondaryUseActive) {
            InteractionResult.PASS
        } else run {
            if (!level().isClientSide) {
                if (player.startRiding(this)) InteractionResult.CONSUME else InteractionResult.PASS
            } else {
                InteractionResult.SUCCESS
            }
        }
    }

    override fun addPassenger(passenger: Entity) {
        super.addPassenger(passenger)
        if (this.isControlledByLocalInstance && this.lerpSteps > 0) {
            this.lerpSteps = 0
            this.absMoveTo(
                this.lerpX, this.lerpY, this.lerpZ,
                yRot, this.lerpXRot.toFloat()
            )
        }
    }

    override fun lerpTo(x: Double, y: Double, z: Double, yRot: Float, xRot: Float, steps: Int) {
        this.lerpX = x
        this.lerpY = y
        this.lerpZ = z
        this.lerpYRot = yRot.toDouble()
        this.lerpXRot = xRot.toDouble()
        this.lerpSteps = 10
    }

    override fun lerpTargetX(): Double {
        return if (this.lerpSteps > 0) this.lerpX else this.x
    }

    override fun lerpTargetY(): Double {
        return if (this.lerpSteps > 0) this.lerpY else this.y
    }

    override fun lerpTargetZ(): Double {
        return if (this.lerpSteps > 0) this.lerpZ else this.z
    }

    override fun lerpTargetXRot(): Float {
        return if (this.lerpSteps > 0) this.lerpXRot.toFloat() else this.xRot
    }

    override fun lerpTargetYRot(): Float {
        return if (this.lerpSteps > 0) this.lerpYRot.toFloat() else this.yRot
    }

    override fun getMotionDirection(): Direction {
        return this.direction.clockWise
    }

    private fun tickLerp() {
        if (this.isControlledByLocalInstance) {
            this.lerpSteps = 0
            this.syncPacketPositionCodec(this.x, this.y, this.z)
        }

        if (this.lerpSteps > 0) {
            this.lerpPositionAndRotationStep(
                this.lerpSteps,
                this.lerpX,
                this.lerpY,
                this.lerpZ,
                this.lerpYRot,
                this.lerpXRot
            )
            this.lerpSteps--
        }
    }

    //Boat code but cool (Boat$controlBoat)
    private fun controlBroom() {

        if (this.isVehicle) {
            var f = 0.0f
            if (this.inputLeft) {
                --this.deltaRotation
            }

            if (this.inputRight) {
                ++this.deltaRotation
            }

            if (this.inputRight != this.inputLeft && !this.inputUp && !this.inputDown) {
                f += 0.1f
            }

            this.yRot += this.deltaRotation
            if (this.inputUp) {
                f += 0.35f
            }

            if (this.inputDown) {
                f -= 0.02f
            }


            if (this.inputJump) {
                this.deltaMovement =
                    deltaMovement.add(0.0, 0.2, 0.0)
            }
            if (inputShift) {
                this.deltaMovement =
                    deltaMovement.add(0.0, -0.2, 0.0)
            }

            this.deltaMovement = deltaMovement.add(
                (Mth.sin(-this.yRot * (Math.PI.toFloat() / 180f)) * f).toDouble() * 2.0,
                0.0,
                (Mth.cos(this.yRot * (Math.PI.toFloat() / 180f)) * f).toDouble() * 2.0
            )
        }
    }

    override fun tick() {
        baseTick()
        if (getHurtTime() > 0) {
            setHurtTime(getHurtTime() - 1)
        }

        if (getDamage()> 0.0f) {
            setDamage(getDamage() - 1)
        }

        val entityPassenger: Entity? = this.controllingPassenger
        if (level().isClientSide()) {
            if (entityPassenger is LivingEntity && entityPassenger == Minecraft.getInstance().player) {
                val player = Minecraft.getInstance().player
                this.isNoGravity = true
                updateInputs(
                    player!!.input.left,
                    player.input.right,
                    player.input.up,
                    player.input.down,
                    player.input.jumping,
                    player.input.shiftKeyDown
                )
            }
        }

        super.tick()
        this.tickLerp()
        if (this.isControlledByLocalInstance) {
            this.updateMotion()
            if (level().isClientSide) {
                this.controlBroom()
            }

            this.move(MoverType.SELF, this.deltaMovement)
        } else {
            this.deltaMovement = Vec3.ZERO
        }

        this.checkInsideBlocks()
    }

    private fun updateInputs(
        leftInputDown: Boolean,
        rightInputDown: Boolean,
        forwardInputDown: Boolean,
        backInputDown: Boolean,
        jumpInputDown: Boolean,
        sneakingInputDown: Boolean
    ) {
        this.inputLeft = leftInputDown
        this.inputRight = rightInputDown
        this.inputUp = forwardInputDown
        this.inputDown = backInputDown
        this.inputJump = jumpInputDown
        this.inputShift = sneakingInputDown
    }



    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (level().isClientSide || this.isRemoved) {
            return true
        } else if (this.isInvulnerableTo(source)) {
            return false
        } else {
            this.setHurtDir(-this.getHurtDir())
            this.setHurtTime(10)
            this.markHurt()
            this.setDamage(this.getDamage() + amount * 10.0f)
            this.gameEvent(GameEvent.ENTITY_DAMAGE, source.entity)
            val bl = source.entity is Player && (source.entity as Player?)!!.abilities.instabuild
            if ((bl || !(this.getDamage() > 40.0f)) && !this.shouldSourceDestroy(source)) {
                if (bl) {
                    this.discard()
                }
            } else {
                this.destroy(source)
            }

            return true
        }
    }

    private fun updateMotion() {
        val d1 = if (this.isNoGravity) 0.0 else -0.04
        val momentum = 0.05f
        val speedMultiplier = 0.4
        val vector3d = this.deltaMovement

        this.setDeltaMovement(
                vector3d.x * momentum,
                vector3d.y + d1,
                vector3d.z * momentum
        )
        this.deltaRotation *= momentum

        if (this.isNoGravity && Mth.abs((deltaMovement.y() / (1.15f + (Mth.abs(deltaMovement.y().toFloat()) / 6f))).toFloat()) > 0f) {
            this.setDeltaMovement(
                deltaMovement.x(), if (Mth.abs(
                        (deltaMovement.y() / (1.15f + (Mth.abs(
                            deltaMovement.y().toFloat()
                        ) / 6f))).toFloat()
                    ) < 0.1f
                ) 0.0 else deltaMovement.y() / (1.15f + (Mth.abs(
                    deltaMovement.y().toFloat()
                ) / 6f)), deltaMovement.z()
            )
        }
        if (deltaMovement.y() > speedMultiplier / 4f) {
            this.setDeltaMovement(deltaMovement.x(), speedMultiplier / 4f, deltaMovement.z())
        }
        if (deltaMovement.y() < -speedMultiplier / 4f) {
            this.setDeltaMovement(
                deltaMovement.x(), -speedMultiplier / 4f, deltaMovement.z()
            )
        }
    }

    override fun onPassengerTurned(entityToUpdate: Entity) {
        entityToUpdate.setYBodyRot(this.yRot)
        val f = Mth.wrapDegrees(entityToUpdate.yRot - this.yRot)
        val g = Mth.clamp(f, -105.0f, 105.0f)
        entityToUpdate.yRotO += g - f
        entityToUpdate.yRot = entityToUpdate.yRot + g - f
        entityToUpdate.yHeadRot = entityToUpdate.yRot
        super.onPassengerTurned(entityToUpdate)
    }

    private fun shouldSourceDestroy(source: DamageSource?): Boolean {
        return false
    }

    private fun destroy(dropItem: Item?) {
        this.kill()
        if (level().gameRules.getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            val itemStack = ItemStack(dropItem)
            itemStack.set(DataComponents.CUSTOM_NAME, this.customName)
            itemStack.set(WitcheryDataComponents.HAS_OINTMENT.get(), true)
            this.spawnAtLocation(itemStack)
        }
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(DATA_ID_HURT, 0)
        builder.define(DATA_ID_HURTDIR, 1)
        builder.define(DATA_ID_DAMAGE, 0.0f)
    }

    fun setHurtTime(hurtTime: Int) {
        entityData.set(DATA_ID_HURT, hurtTime)
    }

    fun setHurtDir(hurtDir: Int) {
        entityData.set(DATA_ID_HURTDIR, hurtDir)
    }

    fun setDamage(damage: Float) {
        entityData.set(DATA_ID_DAMAGE, damage)
    }

    fun getDamage(): Float {
        return entityData.get(DATA_ID_DAMAGE)
    }

    fun getHurtTime(): Int {
        return entityData.get(DATA_ID_HURT)
    }

    fun getHurtDir(): Int {
        return entityData.get(DATA_ID_HURTDIR)
    }

    private fun destroy(source: DamageSource?) {
        this.destroy(WitcheryItems.BROOM.get())
    }


    companion object {
        val DATA_ID_HURT: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            BroomEntity::class.java, EntityDataSerializers.INT
        )
        val DATA_ID_HURTDIR: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            BroomEntity::class.java, EntityDataSerializers.INT
        )
        val DATA_ID_DAMAGE: EntityDataAccessor<Float> = SynchedEntityData.defineId(
            BroomEntity::class.java, EntityDataSerializers.FLOAT
        )
    }
}
package dev.sterner.witchery.content.entity

import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.FlyingMoveControl
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.pathfinder.PathType
import net.minecraft.world.phys.Vec3

class ImpEntity(level: Level) : PathfinderMob(WitcheryEntityTypes.IMP.get(), level) {

    init {
        this.moveControl = FlyingMoveControl(this, 20, true)
        this.setPersistenceRequired()
        this.setPathfindingMalus(PathType.WATER, -1.0f)
        this.setPathfindingMalus(PathType.LAVA, 8.0f)
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0f)
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0f)
    }

    override fun isOnFire(): Boolean {
        return false
    }

    override fun fireImmune(): Boolean {
        return true
    }


    override fun registerGoals() {
        goalSelector.addGoal(2, MeleeAttackGoal(this, 1.0, false))
        goalSelector.addGoal(3, WaterAvoidingRandomStrollGoal(this, 1.0))

        goalSelector.addGoal(5, RandomStrollGoal(this, 0.8))
        goalSelector.addGoal(8, RandomLookAroundGoal(this))
        goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 3.0f, 1.0f))
        goalSelector.addGoal(4, LookAtPlayerGoal(this, Mob::class.java, 8.0f))
        targetSelector.addGoal(1, HurtByTargetGoal(this))
        targetSelector.addGoal(
            2, NearestAttackableTargetGoal(
                this,
                Player::class.java, true
            )
        )
        targetSelector.addGoal(3, NearestAttackableTargetGoal(this, Villager::class.java, true))

        super.registerGoals()
    }


    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FLYING_SPEED, 0.10000000149011612)
                .add(Attributes.MOVEMENT_SPEED, 0.10000000149011612).add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
        }
    }

    override fun createNavigation(level: Level): PathNavigation {
        val flyingPathNavigation = FlyingPathNavigation(this, level)
        flyingPathNavigation.setCanOpenDoors(false)
        flyingPathNavigation.setCanFloat(true)
        flyingPathNavigation.setCanPassDoors(true)
        return flyingPathNavigation
    }

    override fun travel(travelVector: Vec3) {
        if (this.isControlledByLocalInstance) {
            if (this.isInWater) {
                this.moveRelative(0.02f, travelVector)
                this.move(MoverType.SELF, this.deltaMovement)
                this.deltaMovement = deltaMovement.scale(0.800000011920929)
            } else if (this.isInLava) {
                this.moveRelative(0.02f, travelVector)
                this.move(MoverType.SELF, this.deltaMovement)
                this.deltaMovement = deltaMovement.scale(0.5)
            } else {
                this.moveRelative(this.speed, travelVector)
                this.move(MoverType.SELF, this.deltaMovement)
                this.deltaMovement = deltaMovement.scale(0.9100000262260437)
            }
        }

        this.calculateEntityAnimation(false)
    }

    override fun checkFallDamage(y: Double, onGround: Boolean, state: BlockState, pos: BlockPos) {
    }

    override fun getAmbientSound(): SoundEvent {
        return if (this.hasItemInSlot(EquipmentSlot.MAINHAND)) SoundEvents.ALLAY_AMBIENT_WITH_ITEM else SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM
    }

    override fun getHurtSound(damageSource: DamageSource): SoundEvent {
        return SoundEvents.ALLAY_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.ALLAY_DEATH
    }

    override fun getSoundVolume(): Float {
        return 0.4f
    }

    override fun isFlapping(): Boolean {
        return !this.onGround()
    }
}
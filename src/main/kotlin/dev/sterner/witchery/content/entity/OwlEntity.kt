package dev.sterner.witchery.content.entity

import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.BlockTags
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.FlyingMoveControl
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.ai.util.LandRandomPos
import net.minecraft.world.entity.animal.FlyingAnimal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.LeavesBlock
import net.minecraft.world.level.pathfinder.PathType
import net.minecraft.world.phys.Vec3

class OwlEntity(level: Level) : TamableAnimal(WitcheryEntityTypes.OWL.get(), level),
    FlyingAnimal {

    private var flap: Float = 0f
    private var flapSpeed: Float = 0f
    private var oFlapSpeed: Float = 0f
    private var oFlap: Float = 0f
    private var flapping = 1.0f
    private var nextFlap = 1.0f

    init {
        this.moveControl = FlyingMoveControl(this, 10, false)
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0f)
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0f)
        this.setPathfindingMalus(PathType.COCOA, -1.0f)
    }

    override fun registerGoals() {
        goalSelector.addGoal(0, TamableAnimalPanicGoal(1.25))
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(
            1, LookAtPlayerGoal(
                this,
                Player::class.java, 8.0f
            )
        )
        goalSelector.addGoal(2, SitWhenOrderedToGoal(this))
        goalSelector.addGoal(2, FollowOwnerGoal(this, 1.0, 5.0f, 1.0f))
        goalSelector.addGoal(2, OwlWanderGoal(this, 1.0))
        goalSelector.addGoal(3, FollowMobGoal(this, 1.0, 3.0f, 7.0f))
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
        }
    }

    override fun createNavigation(level: Level): PathNavigation {
        val flyingPathNavigation = FlyingPathNavigation(this, level)
        flyingPathNavigation.setCanOpenDoors(false)
        flyingPathNavigation.setCanFloat(true)
        flyingPathNavigation.setCanPassDoors(true)
        return flyingPathNavigation
    }

    override fun aiStep() {
        super.aiStep()
        this.calculateFlapping()
    }

    private fun calculateFlapping() {
        this.oFlap = this.flap
        this.oFlapSpeed = this.flapSpeed
        this.flapSpeed += (if (!this.onGround() && !this.isPassenger) 4 else -1).toFloat() * 0.3f
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0f, 1.0f)
        if (!this.onGround() && this.flapping < 1.0f) {
            this.flapping = 1.0f
        }

        this.flapping *= 0.9f
        val vec3 = this.deltaMovement
        if (!this.onGround() && vec3.y < 0.0) {
            this.deltaMovement = vec3.multiply(1.0, 0.6, 1.0)
        }

        this.flap += this.flapping * 2.0f
    }

    override fun onFlap() {
        this.playSound(SoundEvents.PARROT_FLY, 0.15f, 1.0f)
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0f
    }


    override fun getBreedOffspring(level: ServerLevel, otherParent: AgeableMob): AgeableMob? {
        return null
    }

    override fun isFood(stack: ItemStack): Boolean {
        return false
    }

    override fun isFlying(): Boolean {
        return !this.onGround()
    }

    override fun causeFallDamage(fallDistance: Float, multiplier: Float, source: DamageSource): Boolean {
        return false
    }

    class OwlWanderGoal(pathfinderMob: PathfinderMob, d: Double) :
        WaterAvoidingRandomFlyingGoal(pathfinderMob, d) {
        override fun getPosition(): Vec3? {
            var vec3: Vec3? = null
            if (mob.isInWater) {
                vec3 = LandRandomPos.getPos(this.mob, 15, 15)
            }

            if (mob.random.nextFloat() >= this.probability) {
                vec3 = this.treePos
            }

            return vec3 ?: super.getPosition()
        }

        private val treePos: Vec3?
            get() {
                val blockPos = mob.blockPosition()
                val mutableBlockPos = BlockPos.MutableBlockPos()
                val mutableBlockPos2 = BlockPos.MutableBlockPos()

                for (blockPos2 in BlockPos.betweenClosed(
                    Mth.floor(mob.x - 3.0),
                    Mth.floor(mob.y - 6.0),
                    Mth.floor(mob.z - 3.0),
                    Mth.floor(mob.x + 3.0),
                    Mth.floor(mob.y + 6.0),
                    Mth.floor(mob.z + 3.0)
                )) {
                    if (blockPos != blockPos2) {
                        val blockState =
                            mob.level().getBlockState(mutableBlockPos2.setWithOffset(blockPos2, Direction.DOWN))
                        val bl = blockState.block is LeavesBlock || blockState.`is`(BlockTags.LOGS)
                        if (bl && mob.level().isEmptyBlock(blockPos2) && mob.level()
                                .isEmptyBlock(mutableBlockPos.setWithOffset(blockPos2, Direction.UP))
                        ) {
                            return Vec3.atBottomCenterOf(blockPos2)
                        }
                    }
                }

                return null
            }
    }
}
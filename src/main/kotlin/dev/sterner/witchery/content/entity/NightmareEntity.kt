package dev.sterner.witchery.content.entity

import dev.sterner.witchery.data_attachment.NightmarePlayerAttachment
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.DamageTypeTags
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.target.TargetGoal
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.pathfinder.PathType
import java.util.*

class NightmareEntity(level: Level) : Monster(WitcheryEntityTypes.NIGHTMARE.get(), level) {

    private var intangibleCooldown: Int = 0
    private var ticker = 0

    init {
        this.setPersistenceRequired()
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

    override fun updateInWaterStateAndDoFluidPushing(): Boolean {
        return false
    }

    override fun tick() {
        super.tick()

        if (level() is ServerLevel) {
            if (intangibleCooldown > 0) {
                intangibleCooldown--
            }
            if (intangibleCooldown <= 0) {
                entityData.set(INTANGIBLE, false)
            }

            ticker++
            if (ticker > 20) {
                ticker = 0

                val nightmareWorld = level().server?.getLevel(WitcheryWorldgenKeys.NIGHTMARE)
                if (nightmareWorld != null) {
                    var foundMatchingUUID = false

                    for (player in nightmareWorld.getPlayers { !it.isCreative && !it.isSpectator }) {
                        val data = NightmarePlayerAttachment.getData(player)

                        if (data.nightmareUUID.isPresent && data.nightmareUUID.get() == uuid) {
                            foundMatchingUUID = true
                            break
                        }
                    }

                    if (!foundMatchingUUID) {
                        discard()
                    }
                }
            }
        }
    }

    private fun setIntangible() {
        entityData.set(INTANGIBLE, true)
        intangibleCooldown = 20 * 3
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (entityData.get(INTANGIBLE)) {
            return false
        } else {
            setIntangible()
        }
        return super.hurt(source, amount)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(INTANGIBLE, false)
        builder.define(NIGHTMARE_TARGET, Optional.empty())
        super.defineSynchedData(builder)
    }

    override fun save(compound: CompoundTag): Boolean {
        compound.putBoolean("Intangible", entityData.get(INTANGIBLE))
        compound.putInt("Cooldown", intangibleCooldown)
        return super.save(compound)
    }

    override fun load(compound: CompoundTag) {
        entityData.set(INTANGIBLE, compound.getBoolean("Intangible"))
        intangibleCooldown = compound.getInt("Cooldown")
        super.load(compound)
    }

    override fun isInvulnerableTo(source: DamageSource): Boolean {
        return source.`is`(DamageTypeTags.IS_PROJECTILE) || super.isInvulnerableTo(source)
    }

    override fun registerGoals() {
        goalSelector.addGoal(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.addGoal(1, LookAtPlayerGoal(this, Player::class.java, 3.0f, 1.0f))
        targetSelector.addGoal(1, NightmareTargetGoal(this))

        super.registerGoals()
    }

    override fun doHurtTarget(target: Entity): Boolean {
        val f = getAttributeValue(Attributes.ATTACK_DAMAGE).toFloat()
        val damageSource = damageSources().magic()

        val bl = target.hurt(damageSource, f)
        if (bl) {
            val g = this.getKnockback(target, damageSource)
            if (g > 0.0f && target is LivingEntity) {
                target.knockback(
                    (g * 0.5f).toDouble(), Mth.sin(this.yRot * (Math.PI / 180.0).toFloat()).toDouble(),
                    (-Mth.cos(this.yRot * (Math.PI / 180.0).toFloat())).toDouble()
                )
                this.deltaMovement = deltaMovement.multiply(0.6, 1.0, 0.6)
            }

            this.setLastHurtMob(target)
            this.playAttackSound()
        }

        return bl
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 128.0)
                .add(Attributes.STEP_HEIGHT, 2.0)
        }

        val INTANGIBLE: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            NightmareEntity::class.java, EntityDataSerializers.BOOLEAN
        )

        val NIGHTMARE_TARGET: EntityDataAccessor<Optional<UUID>> = SynchedEntityData.defineId(
            NightmareEntity::class.java, EntityDataSerializers.OPTIONAL_UUID
        )
    }

    open class NightmareTargetGoal(
        mob: Mob
    ) : TargetGoal(mob, false, false) {
        protected var target: LivingEntity? = null

        init {
            this.flags = EnumSet.of(Flag.TARGET)
        }

        override fun canUse(): Boolean {
            if (target == null) {
                this.findTarget()
            }

            return this.target != null
        }

        private fun findTarget() {
            val opt = mob.entityData.get(NIGHTMARE_TARGET)
            if (opt.isPresent) {
                this.target = mob.level().getPlayerByUUID(opt.get())
            }
        }

        override fun start() {
            mob.target = this.target
            super.start()
        }
    }
}
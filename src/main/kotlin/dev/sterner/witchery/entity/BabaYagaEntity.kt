package dev.sterner.witchery.entity

import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.DamageTypeTags
import net.minecraft.tags.FluidTags
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.monster.RangedAttackMob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ThrownPotion
import net.minecraft.world.entity.raid.Raider
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.alchemy.Potions
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.neoforged.neoforge.common.NeoForgeMod
import java.util.function.Predicate
import kotlin.math.sqrt

class BabaYagaEntity(level: Level) : Monster(WitcheryEntityTypes.BABA_YAGA.get(), level), RangedAttackMob {
    private var usingTime = 0
    private var attackPlayersGoal: NearestAttackableBabaTargetGoal<Player>? = null

    override fun registerGoals() {
        super.registerGoals()
        this.attackPlayersGoal =
            NearestAttackableBabaTargetGoal(this, Player::class.java, 10, null)
        this.goalSelector.addGoal(1, FloatGoal(this))
        this.goalSelector.addGoal(2, RangedAttackGoal(this, 1.0, 60, 10.0f))
        this.goalSelector.addGoal(2, WaterAvoidingRandomStrollGoal(this, 1.0))
        this.goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        this.goalSelector.addGoal(3, RandomLookAroundGoal(this))
        this.targetSelector.addGoal(1, HurtByTargetGoal(this))
        this.targetSelector.addGoal(3, this.attackPlayersGoal!!)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(DATA_USING_ITEM, false)
    }

    override fun dropCustomDeathLoot(
        level: ServerLevel,
        damageSource: DamageSource,
        recentlyHit: Boolean
    ) {
        this.spawnAtLocation(WitcheryItems.BABA_YAGAS_HAT.get())
        super.dropCustomDeathLoot(level, damageSource, recentlyHit)
    }

    override fun getAmbientSound(): SoundEvent {
        return SoundEvents.WITCH_AMBIENT
    }

    override fun getHurtSound(damageSource: DamageSource): SoundEvent {
        return SoundEvents.WITCH_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.WITCH_DEATH
    }

    fun setUsingItem(usingItem: Boolean) {
        this.getEntityData().set(DATA_USING_ITEM, usingItem)
    }

    val isDrinkingPotion: Boolean
        get() = this.getEntityData().get<Boolean>(DATA_USING_ITEM)

    override fun aiStep() {
        if (!this.level().isClientSide && this.isAlive) {
            this.attackPlayersGoal!!.setCanAttack(true)

            if (this.isDrinkingPotion) {
                if (this.usingTime-- <= 0) {
                    this.setUsingItem(false)
                    val itemStack = this.mainHandItem
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY)
                    val potionContents = itemStack.get(DataComponents.POTION_CONTENTS)
                    if (itemStack.`is`(Items.POTION) && potionContents != null) {
                        potionContents.forEachEffect { effectInstance: MobEffectInstance ->
                            this.addEffect(
                                effectInstance
                            )
                        }
                    }

                    this.gameEvent(GameEvent.DRINK)
                    this.getAttribute(Attributes.MOVEMENT_SPEED)?.removeModifier(SPEED_MODIFIER_DRINKING.id())
                }
            } else {
                var holder: Holder<Potion?>? = null
                if (this.random.nextFloat() < 0.15f && this.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value()) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                    holder = Potions.WATER_BREATHING
                } else if (this.random.nextFloat() < 0.15f && (this.isOnFire() || this.getLastDamageSource() != null && this.getLastDamageSource()!!
                        .`is`(DamageTypeTags.IS_FIRE))
                    && !this.hasEffect(MobEffects.FIRE_RESISTANCE)
                ) {
                    holder = Potions.FIRE_RESISTANCE
                } else if (this.random.nextFloat() < 0.05f && this.health < this.maxHealth) {
                    holder = Potions.HEALING
                } else if (this.random.nextFloat() < 0.5f && this.target != null && !this.hasEffect(MobEffects.MOVEMENT_SPEED) && this.target!!
                        .distanceToSqr(this) > 121.0
                ) {
                    holder = Potions.SWIFTNESS
                }

                if (holder != null) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, PotionContents.createItemStack(Items.POTION, holder))
                    this.usingTime = this.mainHandItem.getUseDuration(this)
                    this.setUsingItem(true)
                    if (!this.isSilent) {
                        this.level()
                            .playSound(
                                null,
                                this.x,
                                this.y,
                                this.z,
                                SoundEvents.WITCH_DRINK,
                                this.soundSource,
                                1.0f,
                                0.8f + this.random.nextFloat() * 0.4f
                            )
                    }

                    val attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED)
                    attributeInstance?.removeModifier(SPEED_MODIFIER_DRINKING_ID)
                    attributeInstance?.addTransientModifier(SPEED_MODIFIER_DRINKING)
                }
            }

            if (this.random.nextFloat() < 7.5E-4f) {
                this.level().broadcastEntityEvent(this, 15.toByte())
            }
        }

        super.aiStep()
    }

    override fun handleEntityEvent(id: Byte) {
        if (id.toInt() == 15) {
            (0..<this.random.nextInt(35) + 10).forEach { i ->
                this.level()
                    .addParticle(
                        ParticleTypes.WITCH,
                        this.x + this.random.nextGaussian() * 0.13f,
                        this.boundingBox.maxY + 0.5 + this.random.nextGaussian() * 0.13f,
                        this.z + this.random.nextGaussian() * 0.13f,
                        0.0,
                        0.0,
                        0.0
                    )
            }
        } else {
            super.handleEntityEvent(id)
        }
    }

    override fun getDamageAfterMagicAbsorb(damageSource: DamageSource, damageAmount: Float): Float {
        var damageAmount = damageAmount
        damageAmount = super.getDamageAfterMagicAbsorb(damageSource, damageAmount)
        if (damageSource.entity === this) {
            damageAmount = 0.0f
        }

        if (damageSource.`is`(DamageTypeTags.WITCH_RESISTANT_TO)) {
            damageAmount *= 0.15f
        }

        return damageAmount
    }

    override fun performRangedAttack(target: LivingEntity, velocity: Float) {
        if (!this.isDrinkingPotion) {
            val vec3 = target.deltaMovement
            val d = target.x + vec3.x - this.x
            val e = target.eyeY - 1.1f - this.y
            val f = target.z + vec3.z - this.z
            val g = sqrt(d * d + f * f)
            var holder = Potions.HARMING
            if (target is Raider) {
                holder = if (target.health <= 4.0f) {
                    Potions.HEALING
                } else {
                    Potions.REGENERATION
                }

                this.target = null
            } else if (g >= 8.0 && !target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                holder = Potions.SLOWNESS
            } else if (target.health >= 8.0f && !target.hasEffect(MobEffects.POISON)) {
                holder = Potions.POISON
            } else if (g <= 3.0 && !target.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25f) {
                holder = Potions.WEAKNESS
            }

            val thrownPotion = ThrownPotion(this.level(), this)
            thrownPotion.item = PotionContents.createItemStack(Items.SPLASH_POTION, holder)
            thrownPotion.xRot = thrownPotion.xRot - -20.0f
            thrownPotion.shoot(d, e + g * 0.2, f, 0.75f, 8.0f)
            if (!this.isSilent) {
                this.level()
                    .playSound(
                        null,
                        this.x,
                        this.y,
                        this.z,
                        SoundEvents.WITCH_THROW,
                        this.soundSource,
                        1.0f,
                        0.8f + this.random.nextFloat() * 0.4f
                    )
            }

            this.level().addFreshEntity(thrownPotion)
        }
    }

    companion object {
        private val SPEED_MODIFIER_DRINKING_ID: ResourceLocation = ResourceLocation.withDefaultNamespace("drinking")
        private val SPEED_MODIFIER_DRINKING = AttributeModifier(
            SPEED_MODIFIER_DRINKING_ID, -0.25, AttributeModifier.Operation.ADD_VALUE
        )
        private val DATA_USING_ITEM: EntityDataAccessor<Boolean?> =
            SynchedEntityData.defineId<Boolean?>(BabaYagaEntity::class.java, EntityDataSerializers.BOOLEAN)

        fun createAttributes(): AttributeSupplier.Builder {
            return createMonsterAttributes().add(Attributes.MAX_HEALTH, 64.0).add(Attributes.MOVEMENT_SPEED, 0.24)
        }
    }

    class NearestAttackableBabaTargetGoal<T : LivingEntity>(
        mob: BabaYagaEntity,
        targetType: Class<T>,
        randomInterval: Int,
        targetPredicate: Predicate<LivingEntity>?
    ) : NearestAttackableTargetGoal<T>(mob, targetType, randomInterval, true, false, targetPredicate) {

        private var canAttack = true

        fun setCanAttack(active: Boolean) {
            this.canAttack = active
        }

        override fun canUse(): Boolean {
            return this.canAttack && super.canUse()
        }
    }
}
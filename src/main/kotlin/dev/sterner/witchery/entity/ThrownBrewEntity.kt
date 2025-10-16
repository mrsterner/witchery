package dev.sterner.witchery.entity

import dev.sterner.witchery.handler.FamiliarHandler
import dev.sterner.witchery.item.brew.BrewItem
import dev.sterner.witchery.registry.WitcheryEntityTypes
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair
import net.minecraft.core.component.DataComponents
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Containers
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ItemSupplier
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.item.PotionItem
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import kotlin.math.sqrt

class ThrownBrewEntity : ThrowableItemProjectile, ItemSupplier {

    companion object {
        private val DATA_IS_QUARTZ_SPHERE: EntityDataAccessor<Boolean> =
            SynchedEntityData.defineId(ThrownBrewEntity::class.java, EntityDataSerializers.BOOLEAN)
    }

    constructor(level: Level) : super(WitcheryEntityTypes.THROWN_BREW.get(), level)

    constructor(level: Level, shooter: LivingEntity) : super(WitcheryEntityTypes.THROWN_BREW.get(), shooter, level)

    constructor(level: Level, x: Double, y: Double, z: Double) : super(
        WitcheryEntityTypes.THROWN_BREW.get(),
        x,
        y,
        z,
        level
    )

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(DATA_IS_QUARTZ_SPHERE, false)
    }

    fun setIsQuartzSphere(isQuartz: Boolean) {
        entityData.set(DATA_IS_QUARTZ_SPHERE, isQuartz)
    }

    fun isQuartzSphere(): Boolean {
        return entityData.get(DATA_IS_QUARTZ_SPHERE)
    }

    override fun onHitBlock(result: BlockHitResult) {
        super.onHitBlock(result)
    }

    override fun getDefaultItem(): Item {
        return Items.SPLASH_POTION
    }

    override fun onHit(result: HitResult) {
        super.onHit(result)
        if (!level().isClientSide) {
            val itemStack = this.item
            if (itemStack.item is BrewItem) {

                val frog = owner is Player && FamiliarHandler.getFamiliarEntityType(
                    owner!!.uuid,
                    level() as ServerLevel
                ) == EntityType.FROG

                val brew = itemStack.item as BrewItem
                if (result.type == HitResult.Type.BLOCK && brew.predicate.test((result as BlockHitResult).direction)) {
                    applySplash(brew, result, frog)

                    val color = brew.color
                    level().levelEvent(2002, this.blockPosition(), color)
                } else if (result.type != HitResult.Type.BLOCK) {
                    applySplash(brew, result, frog)

                    val color = brew.color
                    level().levelEvent(2002, this.blockPosition(), color)
                } else {
                    Containers.dropItemStack(
                        level(),
                        result.location.x,
                        result.location.y,
                        result.location.z,
                        itemStack
                    )
                }
            } else if (itemStack.item is PotionItem) {
                val potionContents = this.item.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
                this.applySplash(
                    potionContents.allEffects,
                    if (result.type == HitResult.Type.ENTITY) (result as EntityHitResult).entity else null
                )
                val i = if (potionContents.potion().isPresent && potionContents.potion().get().value().hasInstantEffects()) 2007 else 2002
                this.level().levelEvent(i, this.blockPosition(), potionContents.color)
            }
            this.discard()
        }
    }

    private fun applySplash(item: BrewItem, result: HitResult, hasFrog: Boolean) {
        val aABB = this.boundingBox.inflate(4.0, 2.0, 4.0)
        val list = level().getEntitiesOfClass(LivingEntity::class.java, aABB)
        if (list.isNotEmpty()) {
            for (livingEntity in list) {
                item.applyEffectOnEntities(level(), livingEntity, hasFrog)
            }
        }

        item.applyEffectOnHitLocation(level(), result.location, hasFrog)

        if (result.type == HitResult.Type.BLOCK) {
            item.applyEffectOnBlock(level(), result as BlockHitResult, hasFrog)
        }
    }


    private fun applySplash(effects: Iterable<MobEffectInstance>, pEntity: Entity?) {
        val aabb = this.boundingBox.inflate(4.0, 2.0, 4.0)
        val list = this.level().getEntitiesOfClass<LivingEntity?>(LivingEntity::class.java, aabb)
        if (!list.isEmpty()) {
            val entity = this.effectSource

            for (livingEntity in list) {
                if (livingEntity != null && livingEntity.isAffectedByPotions) {
                    val d0 = this.distanceToSqr(livingEntity)
                    if (d0 < 16.0) {
                        val d1: Double = if (livingEntity === pEntity) {
                            1.0
                        } else {
                            1.0 - sqrt(d0) / 4.0
                        }

                        for (mobEffectInstance in effects) {
                            val holder = mobEffectInstance.effect
                            if (holder.value().isInstantenous) {
                                holder.value().applyInstantenousEffect(
                                    this,
                                    this.owner,
                                    livingEntity,
                                    mobEffectInstance.amplifier,
                                    d1
                                )
                            } else {
                                val i =
                                    mobEffectInstance.mapDuration { p_267930_: Int -> (d1 * p_267930_.toDouble() + 0.5).toInt() }
                                val mobEffectInstance1 = MobEffectInstance(
                                    holder,
                                    i,
                                    mobEffectInstance.amplifier,
                                    mobEffectInstance.isAmbient,
                                    mobEffectInstance.isVisible
                                )
                                if (!mobEffectInstance1.endsWithin(20)) {
                                    livingEntity.addEffect(mobEffectInstance1, entity)
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    override fun calculateHorizontalHurtKnockbackDirection(
        entity: LivingEntity,
        damageSource: DamageSource
    ): DoubleDoubleImmutablePair {
        val d = entity.position().x - position().x
        val e = entity.position().z - position().z
        return DoubleDoubleImmutablePair.of(d, e)
    }

    override fun getDefaultGravity(): Double {
        return 0.05
    }
}
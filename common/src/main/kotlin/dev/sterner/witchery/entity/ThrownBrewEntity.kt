package dev.sterner.witchery.entity

import dev.sterner.witchery.item.brew.BrewItem
import dev.sterner.witchery.registry.WitcheryEntityTypes
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.ItemSupplier
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

class ThrownBrewEntity : ThrowableItemProjectile, ItemSupplier {

    constructor(level: Level) : super(WitcheryEntityTypes.THROWN_BREW.get(), level)

    constructor(entityType: EntityType<out ThrownBrewEntity>, level: Level) : super(entityType, level)

    constructor(level: Level, shooter: LivingEntity) : super(WitcheryEntityTypes.THROWN_BREW.get(), shooter, level)

    constructor(level: Level, x: Double, y: Double, z: Double) : super(WitcheryEntityTypes.THROWN_BREW.get(), x, y, z, level)


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
                applySplash(itemStack.item as BrewItem, result)

                val color = (itemStack.item as BrewItem).color
                level().levelEvent(2002, this.blockPosition(), color)
            }
            this.discard()
        }
    }

    private fun applySplash(item: BrewItem, result: HitResult) {
        val aABB = this.boundingBox.inflate(4.0, 2.0, 4.0)
        val list = level().getEntitiesOfClass(LivingEntity::class.java, aABB)
        if (list.isNotEmpty()) {
            for (livingEntity in list) {
                item.applyEffect(level(), livingEntity, result)
            }
        } else {
            item.applyEffect(level(), null, result)
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
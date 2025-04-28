package dev.sterner.witchery.entity

import dev.sterner.witchery.handler.FamiliarHandler
import dev.sterner.witchery.item.brew.BrewItem
import dev.sterner.witchery.platform.FamiliarLevelAttachment
import dev.sterner.witchery.registry.WitcheryEntityTypes
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Containers
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ItemSupplier
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

class ThrownBrewEntity : ThrowableItemProjectile, ItemSupplier {

    constructor(level: Level) : super(WitcheryEntityTypes.THROWN_BREW.get(), level)

    constructor(level: Level, shooter: LivingEntity) : super(WitcheryEntityTypes.THROWN_BREW.get(), shooter, level)

    constructor(level: Level, x: Double, y: Double, z: Double) : super(
        WitcheryEntityTypes.THROWN_BREW.get(),
        x,
        y,
        z,
        level
    )

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
                    applySplash(itemStack.item as BrewItem, result, frog)

                    val color = (itemStack.item as BrewItem).color
                    level().levelEvent(2002, this.blockPosition(), color)
                } else if (result.type != HitResult.Type.BLOCK) {
                    applySplash(itemStack.item as BrewItem, result, frog)

                    val color = (itemStack.item as BrewItem).color
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
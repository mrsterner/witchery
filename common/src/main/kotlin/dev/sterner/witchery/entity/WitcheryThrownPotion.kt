package dev.sterner.witchery.entity

import dev.sterner.witchery.item.brew.BrewItem
import dev.sterner.witchery.item.potion.WitcheryPotionItem
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

class WitcheryThrownPotion : ThrowableItemProjectile, ItemSupplier {

    constructor(level: Level) : super(WitcheryEntityTypes.THROWN_POTION.get(), level)

    constructor(level: Level, shooter: LivingEntity) : super(WitcheryEntityTypes.THROWN_POTION.get(), shooter, level)

    constructor(level: Level, x: Double, y: Double, z: Double) : super(
        WitcheryEntityTypes.THROWN_POTION.get(),
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
            if (itemStack.item is WitcheryPotionItem) {

            }
            this.discard()
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
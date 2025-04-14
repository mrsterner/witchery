package dev.sterner.witchery.entity

import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.item.potion.WitcheryPotionItem
import dev.sterner.witchery.potion.MobEffectPotionEffect
import dev.sterner.witchery.registry.WitcheryDataComponents.DURATION_AMPLIFIER
import dev.sterner.witchery.registry.WitcheryDataComponents.WITCHERY_POTION_CONTENT
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryPotionEffectRegistry
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.ItemSupplier
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import kotlin.math.sqrt

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
                applySplash(itemStack,
                    if (result.type == HitResult.Type.ENTITY) (result as EntityHitResult).entity else null)

                val color = itemStack.get(WITCHERY_POTION_CONTENT.get())?.last()?.color
                color?.let { level().levelEvent(2002, this.blockPosition(), it) }
            }
            this.discard()
        }
    }

    private fun makeAreaOfEffectCloud(potionContents: MutableList<WitcheryPotionIngredient>) {
        val areaEffectCloud = WitcheryAreaEffectCloud(this.level(), this.x, this.y, this.z)
        areaEffectCloud.radius = 3.0f + getRangeBonus(potionContents)
        areaEffectCloud.radiusOnUse = -0.5f
        areaEffectCloud.waitTime = 10
        areaEffectCloud.duration *= potionContents.mapNotNull { it.dispersalModifier.orElse(null)?.lingeringDurationModifier }.maxOrNull() ?: 1
        areaEffectCloud.radiusPerTick = -areaEffectCloud.radius / areaEffectCloud.duration.toFloat()
        areaEffectCloud.setPotionContents(potionContents)
        level().addFreshEntity(areaEffectCloud)
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

    private fun getRangeBonus(potion: List<WitcheryPotionIngredient>): Int {
        return potion.mapNotNull { it.dispersalModifier.orElse(null)?.rangeModifier }
            .maxOrNull() ?: 1
    }

    private fun getRangeBonus(stack: ItemStack): Int {
        val potion = stack.get(WITCHERY_POTION_CONTENT.get()) ?: return 1

        return potion.mapNotNull { it.dispersalModifier.orElse(null)?.rangeModifier }
            .maxOrNull() ?: 1
    }

    private fun applySplash(stack: ItemStack, entity: Entity?) {
        val scaleBonus = getRangeBonus(stack)
        val aABB = this.boundingBox.inflate(4.0 * scaleBonus, 2.0 * scaleBonus, 4.0 * scaleBonus)
        val list = level().getEntitiesOfClass(
            LivingEntity::class.java, aABB
        )
        if (list.isNotEmpty()) {
            val entity2 = this.effectSource

            for (livingEntity in list) {
                if (livingEntity.isAffectedByPotions) {
                    val d = this.distanceToSqr(livingEntity)
                    if (d < 16.0) {
                        val e = if (livingEntity === entity) {
                            1.0
                        } else {
                            1.0 - sqrt(d) / 4.0
                        }

                        if (stack.has(WITCHERY_POTION_CONTENT.get())) {
                            val ingredients = stack.get(WITCHERY_POTION_CONTENT.get())
                            if (ingredients != null) {
                                for ((i, ingredient) in ingredients.withIndex()) {
                                    if (i == 0) continue

                                    val instance = WitcheryPotionEffectRegistry.EFFECTS.get(ingredient.effect.effectId)
                                    if (instance is MobEffectPotionEffect) {
                                        val effectData = stack.get(DURATION_AMPLIFIER.get())
                                        var duration = 0
                                        var amplifier = 0
                                        if (effectData != null) {
                                            duration = effectData[i - 1].duration
                                            amplifier = effectData[i - 1].amplifier
                                        }

                                        val holder = instance.mobEffect
                                        if (holder.value().isInstantenous) {
                                            holder.value().applyInstantenousEffect(
                                                this,
                                                this.owner, livingEntity, amplifier, e
                                            )
                                        } else {

                                            val visible = !ingredients.any { it.generalModifier.orElse(null) == WitcheryPotionIngredient.GeneralModifier.NO_PARTICLE }
                                            val mobEffectInstance2 = MobEffectInstance(
                                                holder,
                                                duration,
                                                amplifier,
                                                false,
                                                visible
                                            )
                                            if (!mobEffectInstance2.endsWithin(20)) {
                                                livingEntity.addEffect(mobEffectInstance2, entity2)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
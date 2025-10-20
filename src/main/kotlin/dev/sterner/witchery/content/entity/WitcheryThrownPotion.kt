package dev.sterner.witchery.content.entity

import dev.sterner.witchery.content.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.content.item.potion.WitcheryPotionItem

import dev.sterner.witchery.core.registry.WitcheryDataComponents.WITCHERY_POTION_CONTENT
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryMobEffects
import dev.sterner.witchery.core.registry.WitcherySpecialPotionEffects
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

    var lingering: Boolean = false

    constructor(level: Level) : super(WitcheryEntityTypes.THROWN_POTION.get(), level)

    constructor(level: Level, shooter: LivingEntity) : super(WitcheryEntityTypes.THROWN_POTION.get(), shooter, level)

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
                val potionContentList = itemStack.get(WITCHERY_POTION_CONTENT.get())

                if (lingering) {
                    potionContentList?.let { potionContent ->
                        makeAreaOfEffectCloud(potionContent.map { it }.toMutableList(), result)
                    }
                } else {
                    applySplash(
                        itemStack,
                        if (result.type == HitResult.Type.ENTITY) (result as EntityHitResult).entity else null,
                        result
                    )
                }

                potionContentList?.let {
                    val color = potionContentList.last().color
                    color.let { level().levelEvent(2002, this.blockPosition(), it) }
                }
            }
            this.discard()
        }
    }

    private fun makeAreaOfEffectCloud(potionContentList: MutableList<WitcheryPotionIngredient>, result: HitResult) {
        val areaEffectCloud = WitcheryAreaEffectCloud(this.level(), this.x, this.y, this.z, hitResult = result)
        areaEffectCloud.radius = 3.0f + getRangeBonus(potionContentList)
        areaEffectCloud.radiusOnUse = -0.5f
        areaEffectCloud.waitTime = 10
        areaEffectCloud.hitResult = result

        val baseDuration = areaEffectCloud.duration
        val lingeringModifier = potionContentList.maxOfOrNull { it.dispersalModifier.lingeringDurationModifier } ?: 1
        areaEffectCloud.duration = baseDuration * lingeringModifier

        areaEffectCloud.radiusPerTick = -areaEffectCloud.radius / areaEffectCloud.duration.toFloat()
        areaEffectCloud.setPotionContents(potionContentList)
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

    companion object {
        fun getRangeBonus(potion: List<WitcheryPotionIngredient>): Int {
            return potion.maxOfOrNull { it.dispersalModifier.rangeModifier } ?: 1
        }

        fun getRangeBonus(stack: ItemStack): Int {
            val potionContentList = stack.get(WITCHERY_POTION_CONTENT.get()) ?: return 1
            return potionContentList.maxOfOrNull { it.dispersalModifier.rangeModifier } ?: 1
        }
    }

    /**
     * Calculate the proper duration for a potion effect based on its base duration and modifiers
     */
    private fun calculateEffectDuration(
        potionContent: WitcheryPotionIngredient,
        effectData: WitcheryPotionIngredient.EffectModifier
    ): Int {
        return ((potionContent.baseDuration + effectData.durationAddition) * effectData.durationMultiplier).toInt()
    }

    private fun applySplash(stack: ItemStack, entity: Entity?, result: HitResult) {
        val scaleBonus = getRangeBonus(stack)
        val aABB = this.boundingBox.inflate(4.0 * scaleBonus, 2.0 * scaleBonus, 4.0 * scaleBonus)
        val list = level().getEntitiesOfClass(
            Entity::class.java, aABB
        )

        if (stack.has(WITCHERY_POTION_CONTENT.get())) {
            val potionContentList = stack.get(WITCHERY_POTION_CONTENT.get())
            if (potionContentList != null) {
                var shouldInvertNext = false

                val globalModifier = WitcheryPotionItem.getMergedEffectModifier(potionContentList)

                for ((i, potionContent) in potionContentList.withIndex()) {
                    if (i == 0) continue

                    val duration = calculateEffectDuration(potionContent, globalModifier)
                    val amplifier = globalModifier.powerAddition

                    if (potionContent.specialEffect.isPresent) {
                        val special =
                            WitcherySpecialPotionEffects.SPECIAL_REGISTRY.get(potionContent.specialEffect.get())
                        special?.onActivated(
                            level(),
                            owner,
                            result,
                            list,
                            WitcheryPotionItem.getMergedDisperseModifier(potionContentList),
                            duration,
                            amplifier
                        )
                    }

                    if (potionContent.generalModifier.contains(WitcheryPotionIngredient.GeneralModifier.INVERT_NEXT)) {
                        shouldInvertNext = true
                        continue
                    }

                    val effect = if (shouldInvertNext) {
                        shouldInvertNext = false
                        WitcheryMobEffects.invertEffect(potionContent.effect)
                    } else {
                        potionContent.effect
                    }

                    if (effect == WitcheryMobEffects.EMPTY) continue

                    if (list.isNotEmpty()) {
                        val livingList = list.filterIsInstance<LivingEntity>()
                        for (livingEntity in livingList) {
                            if (livingEntity.isAffectedByPotions) {
                                val d = this.distanceToSqr(livingEntity)
                                if (d < 16.0) {
                                    val e = if (livingEntity === entity) {
                                        1.0
                                    } else {
                                        1.0 - sqrt(d) / 4.0
                                    }

                                    if (effect.value().isInstantenous) {
                                        effect.value().applyInstantenousEffect(
                                            this, this.owner, livingEntity, amplifier, e
                                        )
                                    } else {
                                        val visible = !potionContentList.any {
                                            it.generalModifier.contains(WitcheryPotionIngredient.GeneralModifier.NO_PARTICLE)
                                        }

                                        val mobEffectInstance = MobEffectInstance(
                                            effect,
                                            duration,
                                            amplifier,
                                            false,
                                            visible
                                        )

                                        if (!mobEffectInstance.endsWithin(20)) {
                                            livingEntity.addEffect(mobEffectInstance, effectSource)
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
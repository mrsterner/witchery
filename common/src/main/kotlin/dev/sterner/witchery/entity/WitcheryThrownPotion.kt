package dev.sterner.witchery.entity

import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.item.potion.WitcheryPotionItem
import dev.sterner.witchery.registry.WitcheryDataComponents.WITCHERY_POTION_CONTENT
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryMobEffects
import dev.sterner.witchery.registry.WitcherySpecialPotionEffects
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
                    potionContentList?.let { potionContent -> makeAreaOfEffectCloud(potionContent.map { it }.toMutableList()) }
                } else {
                    applySplash(itemStack, if (result.type == HitResult.Type.ENTITY) (result as EntityHitResult).entity else null)

                }
                potionContentList?.let {
                    val color = potionContentList.last().color
                    color.let { level().levelEvent(2002, this.blockPosition(), it) }
                }

            }
            this.discard()
        }
    }

    private fun makeAreaOfEffectCloud(potionContentList: MutableList<WitcheryPotionIngredient>) {
        val areaEffectCloud = WitcheryAreaEffectCloud(this.level(), this.x, this.y, this.z)
        areaEffectCloud.radius = 3.0f + getRangeBonus(potionContentList)
        areaEffectCloud.radiusOnUse = -0.5f
        areaEffectCloud.waitTime = 10
        areaEffectCloud.duration *= potionContentList.maxOfOrNull { it.dispersalModifier.lingeringDurationModifier } ?: 1
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

    private fun getRangeBonus(potion: List<WitcheryPotionIngredient>): Int {
        return potion.maxOfOrNull { it.dispersalModifier.rangeModifier } ?: 1
    }

    private fun getRangeBonus(stack: ItemStack): Int {
        val potionContentList = stack.get(WITCHERY_POTION_CONTENT.get()) ?: return 1

        return potionContentList.maxOfOrNull { it.dispersalModifier.rangeModifier } ?: 1
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
                            val potionContentList = stack.get(WITCHERY_POTION_CONTENT.get())
                            if (potionContentList != null) {
                                var shouldInvertNext = false

                                for ((i, potionContent) in potionContentList.withIndex()) {
                                    if (i == 0) continue

                                    if (potionContent.specialEffect.isPresent) {
                                        WitcherySpecialPotionEffects.SPECIALS.get(potionContent.specialEffect.get().id)?.onActivated(level(), owner)
                                    }

                                    if (potionContent.generalModifier.contains(WitcheryPotionIngredient.GeneralModifier.INVERT_NEXT)) {
                                        shouldInvertNext = true
                                    }

                                    val effectData = potionContent.effectModifier
                                    val duration = (potionContent.baseDuration + effectData.durationAddition) * effectData.durationMultiplier
                                    val amplifier = effectData.powerAddition

                                    val effect = if (shouldInvertNext) {
                                        shouldInvertNext = false
                                        WitcheryMobEffects.invertEffect(potionContent.effect)
                                    } else {
                                        potionContent.effect
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
                                            if (effect != WitcheryMobEffects.EMPTY) {
                                                livingEntity.addEffect(mobEffectInstance, entity2)
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
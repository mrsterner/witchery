package dev.sterner.witchery.content.mob_effect

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.api.interfaces.OnRemovedEffect
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes


class ResizeMobEffect(val grow: Boolean, category: MobEffectCategory, color: Int) : MobEffect(category, color),
    OnRemovedEffect {

    override fun applyEffectTick(livingEntity: LivingEntity, amplifier: Int): Boolean {
        return true
    }


    override fun shouldApplyEffectTickThisTick(duration: Int, amplifier: Int): Boolean {
        return true
    }

    override fun onEffectAdded(livingEntity: LivingEntity, amplifier: Int) {
        super.onEffectAdded(livingEntity, amplifier)
        if (!livingEntity.level().isClientSide) {
            livingEntity.attributes.getInstance(Attributes.SCALE)?.let { scaleAttribute ->
                scaleAttribute.modifiers.filter {
                    it.id == Witchery.id("growth") || it.id == Witchery.id("shrink")
                }.forEach {
                    scaleAttribute.removeModifier(it)
                }

                val scaleModification = if (grow) {
                    0.5f * (amplifier + 1)
                } else {
                    -0.5f * (amplifier + 1)
                }

                val modifierName = if (grow) Witchery.id("growth") else Witchery.id("shrink")
                val modifier = AttributeModifier(
                    modifierName,
                    scaleModification.toDouble(),
                    AttributeModifier.Operation.ADD_VALUE
                )

                scaleAttribute.addPermanentModifier(modifier)
            }

        }
    }

    override fun onRemovedEffect(livingEntity: LivingEntity) {
        if (!livingEntity.level().isClientSide) {
            livingEntity.attributes.getInstance(Attributes.SCALE)?.let { scaleAttribute ->
                scaleAttribute.modifiers.filter {
                    it.id == Witchery.id("growth") || it.id == Witchery.id("shrink")
                }.forEach {
                    scaleAttribute.removeModifier(it)
                }
            }
        }
    }
}
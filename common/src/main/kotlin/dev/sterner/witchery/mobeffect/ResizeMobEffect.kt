package dev.sterner.witchery.mobeffect

import dev.sterner.witchery.api.OnRemovedEffect
import dev.sterner.witchery.registry.WitcheryPehkuiScaleTypes
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import virtuoel.pehkui.api.ScaleData
import virtuoel.pehkui.api.ScaleTypes


class ResizeMobEffect(val grow: Boolean, category: MobEffectCategory, color: Int) : MobEffect(category, color), OnRemovedEffect {

    override fun applyEffectTick(livingEntity: LivingEntity, amplifier: Int): Boolean {
        return true
    }


    override fun shouldApplyEffectTickThisTick(duration: Int, amplifier: Int): Boolean {
        return true
    }

    override fun onEffectAdded(livingEntity: LivingEntity, amplifier: Int) {
        super.onEffectAdded(livingEntity, amplifier)
        if (!livingEntity.level().isClientSide) {
            val data: ScaleData = if (grow) {
                 WitcheryPehkuiScaleTypes.GROWING.getScaleData(livingEntity)
            } else {
                WitcheryPehkuiScaleTypes.SHRINKING.getScaleData(livingEntity)
            }
            if (grow) {
                data.scale = getGrowthScale(amplifier)
            } else {
                data.scale = getShrinkScale(amplifier)
            }

            ScaleTypes.BASE.getScaleData(livingEntity).markForSync(true)
            data.markForSync(true)
        }
    }

    private fun getGrowthScale(amplifier: Int): Float {
        return (amplifier + 2).toFloat()
    }

    private fun getShrinkScale(amplifier: Int): Float {
        return 1f / (2f * (amplifier + 1).toFloat())
    }

    override fun onRemovedEffect(livingEntity: LivingEntity) {
        if (!livingEntity.level().isClientSide) {
            val data: ScaleData = if (grow) {
                WitcheryPehkuiScaleTypes.GROWING.getScaleData(livingEntity)
            } else {
                WitcheryPehkuiScaleTypes.SHRINKING.getScaleData(livingEntity)
            }
            data.resetScale(true)
            ScaleTypes.BASE.getScaleData(livingEntity).markForSync(true)
            data.markForSync(true)
        }
    }
}
package dev.sterner.witchery.mobeffect

import dev.sterner.witchery.api.OnRemovedEffect
import dev.sterner.witchery.registry.WitcheryPehkuiScaleTypes
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import virtuoel.pehkui.api.ScaleData
import virtuoel.pehkui.api.ScaleTypes


class ResizeMobEffect(category: MobEffectCategory, color: Int) : MobEffect(category, color), OnRemovedEffect {

    override fun applyEffectTick(livingEntity: LivingEntity, amplifier: Int): Boolean {
        return true
    }


    override fun shouldApplyEffectTickThisTick(duration: Int, amplifier: Int): Boolean {
        return true
    }

    override fun onEffectAdded(livingEntity: LivingEntity, amplifier: Int) {
        super.onEffectAdded(livingEntity, amplifier)
        if (!livingEntity.level().isClientSide) {
            val data: ScaleData = WitcheryPehkuiScaleTypes.GROWING.getScaleData(livingEntity)
            data.scale = getScale(amplifier)
            ScaleTypes.BASE.getScaleData(livingEntity).markForSync(true)
            data.markForSync(true)
        }
    }

    private fun getScale(amplifier: Int): Float {
        return (amplifier + 2).toFloat()
    }

    override fun onRemovedEffect(entity: LivingEntity) {
        if (!entity.level().isClientSide) {
            val data: ScaleData = WitcheryPehkuiScaleTypes.GROWING.getScaleData(entity)
            data.resetScale(true)
            ScaleTypes.BASE.getScaleData(entity).markForSync(true)
            data.markForSync(true)
        }
    }
}
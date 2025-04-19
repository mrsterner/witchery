package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import net.minecraft.world.entity.Entity
import virtuoel.pehkui.api.*


object WitcheryPehkuiScaleTypes {

    val GROWING: ScaleType =
        ScaleRegistries.register(ScaleRegistries.SCALE_TYPES, Witchery.id("growing"), ScaleType.Builder.create().build())

    val SHRINKING: ScaleType = ScaleRegistries.register(
        ScaleRegistries.SCALE_TYPES,
        Witchery.id("shrinking"),
        ScaleType.Builder.create().build()
    )

    private val SHRINKING_MOD: ScaleModifier =
        ScaleRegistries.register(ScaleRegistries.SCALE_MODIFIERS, Witchery.id("shrinking"), object : ScaleModifier() {
            override fun modifyScale(scaleData: ScaleData, modifiedScale: Float, delta: Float): Float {
                return SHRINKING.getScaleData(scaleData.entity).getScale(delta) * modifiedScale
            }
        })
    private val GROWING_MOD: ScaleModifier =
        ScaleRegistries.register(ScaleRegistries.SCALE_MODIFIERS, Witchery.id("growing"), object : ScaleModifier() {
            override fun modifyScale(scaleData: ScaleData, modifiedScale: Float, delta: Float): Float {
                return GROWING.getScaleData(scaleData.entity).getScale(delta) * modifiedScale
            }
        })

    fun init() {
        ScaleTypes.BASE.defaultBaseValueModifiers.add(SHRINKING_MOD)
        ScaleTypes.BASE.defaultBaseValueModifiers.add(GROWING_MOD)
    }
}
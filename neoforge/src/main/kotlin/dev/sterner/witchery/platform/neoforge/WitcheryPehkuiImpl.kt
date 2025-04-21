package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.Witchery
import virtuoel.pehkui.api.*

object WitcheryPehkuiImpl {

    @JvmStatic
    fun getGrowing(): ScaleType {
        return ScaleRegistries.register(
            ScaleRegistries.SCALE_TYPES,
            Witchery.id("growing"),
            ScaleType.Builder.create().build())
    }

    @JvmStatic
    fun getShrinking(): ScaleType {
        return ScaleRegistries.register(
            ScaleRegistries.SCALE_TYPES,
            Witchery.id("shrinking"),
            ScaleType.Builder.create().build()
        )
    }

    @JvmStatic
    fun getGrowingModifier(): ScaleModifier {
        return ScaleRegistries.register(ScaleRegistries.SCALE_MODIFIERS, Witchery.id("growing"), object : ScaleModifier() {
            override fun modifyScale(scaleData: ScaleData, modifiedScale: Float, delta: Float): Float {
                return getGrowing().getScaleData(scaleData.entity).getScale(delta) * modifiedScale
            }
        })
    }

    @JvmStatic
    fun getShrinkingModifier(): ScaleModifier {
        return ScaleRegistries.register(ScaleRegistries.SCALE_MODIFIERS, Witchery.id("shrinking"), object : ScaleModifier() {
            override fun modifyScale(scaleData: ScaleData, modifiedScale: Float, delta: Float): Float {
                return getShrinking().getScaleData(scaleData.entity).getScale(delta) * modifiedScale
            }
        })
    }

    @JvmStatic
    fun init() {
        ScaleTypes.BASE.defaultBaseValueModifiers.add(getShrinkingModifier())
        ScaleTypes.BASE.defaultBaseValueModifiers.add(getGrowingModifier())
    }
}
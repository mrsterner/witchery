package dev.sterner.witchery.platform

import dev.architectury.injectables.annotations.ExpectPlatform
import virtuoel.pehkui.api.ScaleModifier
import virtuoel.pehkui.api.ScaleType

object WitcheryPehkui {

    @JvmStatic
    @ExpectPlatform
    fun getGrowing(): ScaleType {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getShrinking(): ScaleType {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getGrowingModifier(): ScaleModifier {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getShrinkingModifier(): ScaleModifier {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun register() {

    }
}
package dev.sterner.witchery.mixin_logic

import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.item.ItemStack

object ItemRendererMixinLogic {

    fun saveStack(itemStack: ItemStack) {
        WitcheryRenderTypes.setStack(itemStack)
    }

    fun getFoilBuffer(original: Operation<RenderType>): RenderType? {
        if (WitcheryRenderTypes.checkAllBlack()) {
            return WitcheryRenderTypes.GLINT.apply(Witchery.id("textures/misc/all_black.png"))
        }
        return original.call()
    }

    fun getFoilBufferDirect(original: Operation<RenderType>): RenderType? {
        if (WitcheryRenderTypes.checkAllBlack()) {
            return WitcheryRenderTypes.GLINT_DIRECT.apply(Witchery.id("textures/misc/all_black.png"))
        }
        return original.call()
    }
}
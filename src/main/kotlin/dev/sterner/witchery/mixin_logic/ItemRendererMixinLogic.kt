package dev.sterner.witchery.mixin_logic

import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryRenderTypes
import dev.sterner.witchery.registry.WitcheryRenderTypes.GLINT
import dev.sterner.witchery.registry.WitcheryRenderTypes.GLINT_DIRECT
import dev.sterner.witchery.registry.WitcheryRenderTypes.checkAllBlack
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.item.ItemStack

object ItemRendererMixinLogic {

    fun saveStack(itemStack: ItemStack) {
        WitcheryRenderTypes.setStack(itemStack)
    }

    fun getFoilBuffer(original: Operation<RenderType>): RenderType? {
        if (checkAllBlack()) {
            return GLINT.apply(Witchery.id("textures/misc/all_black.png"))
        }
        return original.call()
    }

    fun getFoilBufferDirect(original: Operation<RenderType>): RenderType? {
        if (checkAllBlack()) {
            return GLINT_DIRECT.apply(Witchery.id("textures/misc/all_black.png"))
        }
        return original.call()
    }
}
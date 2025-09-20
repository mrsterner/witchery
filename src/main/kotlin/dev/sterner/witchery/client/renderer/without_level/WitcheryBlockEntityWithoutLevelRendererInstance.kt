package dev.sterner.witchery.client.renderer.without_level

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import org.jetbrains.annotations.NotNull


@JvmRecord
data class WitcheryBlockEntityWithoutLevelRendererInstance(val renderer: BlockEntityWithoutLevelRenderer) :
    IClientItemExtensions {

    @NotNull
    override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer {
        return renderer
    }
}
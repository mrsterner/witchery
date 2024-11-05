package dev.sterner.witchery.neoforge.client

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
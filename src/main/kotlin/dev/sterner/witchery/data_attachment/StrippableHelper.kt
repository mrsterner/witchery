package dev.sterner.witchery.data_attachment

import dev.sterner.witchery.block.ForgeStrippableLogBlock
import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import java.util.function.Supplier

object StrippableHelper {

    @JvmStatic
    fun createStrippableLog(stripped: Supplier<out RotatedPillarBlock>, properties: Properties) =
        Supplier { ForgeStrippableLogBlock(stripped, properties) }
}
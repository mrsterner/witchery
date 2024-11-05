package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.block.trees.ForgeStrippableLogBlock
import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import java.util.function.Supplier

object StrippableHelperImpl {

    @JvmStatic
    fun createStrippableLog(stripped: Supplier<out RotatedPillarBlock>, properties: Properties) =
        Supplier { ForgeStrippableLogBlock(stripped, properties) }
}
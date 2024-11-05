package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.block.trees.StrippableLogBlock
import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import java.util.function.Supplier

object StrippableHelperImpl {

    @JvmStatic
    fun createStrippableLog(
        stripped: Supplier<out RotatedPillarBlock>,
        properties: Properties
    ): Supplier<out StrippableLogBlock> {
        val supp = Supplier { StrippableLogBlock(stripped, properties) }
        return supp
    }
}
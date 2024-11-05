package dev.sterner.witchery.platform

import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.block.trees.StrippableLogBlock
import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import java.util.function.Supplier

object StrippableHelper {

    @JvmStatic
    @ExpectPlatform
    fun createStrippableLog(
        stripped: Supplier<out RotatedPillarBlock>,
        properties: Properties
    ): Supplier<out StrippableLogBlock> {
        throw AssertionError()
    }
}
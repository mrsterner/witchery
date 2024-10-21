package dev.sterner.witchery.integration.wthit

import mcp.mobius.waila.api.IBlockAccessor
import mcp.mobius.waila.api.IBlockComponentProvider
import mcp.mobius.waila.api.IPluginConfig
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState

enum class SneakyBlockProvider : IBlockComponentProvider {
    INSTANCE;

    override fun getOverride(accessor: IBlockAccessor, config: IPluginConfig): BlockState? {
        return Blocks.POPPY.defaultBlockState()
    }
}

package dev.sterner.witchery.block.werewolf_altar

import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class WerewolfAltarBlockEntity(
    blockPos: BlockPos, blockState: BlockState
) : WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.WEREWOLF_ALTAR.get(), blockPos, blockState)
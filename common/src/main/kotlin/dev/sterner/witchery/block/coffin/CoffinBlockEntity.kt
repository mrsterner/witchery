package dev.sterner.witchery.block.coffin

import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState


class CoffinBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.COFFIN.get(), blockPos, blockState)
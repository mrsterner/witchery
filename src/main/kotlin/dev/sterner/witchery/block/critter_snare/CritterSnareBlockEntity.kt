package dev.sterner.witchery.block.critter_snare

import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class CritterSnareBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    BlockEntity(WitcheryBlockEntityTypes.CRITTER_SNARE.get(), blockPos, blockState)
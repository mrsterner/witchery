package dev.sterner.witchery.block.trees

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RotatedPillarBlock
import java.util.function.Supplier

/**
 * Used by Fabric to register it since they have a strippable registry
 *
 * Forge extends this to then do its built-in methods
 */
open class StrippableLogBlock(val strippedLog: Supplier<out Block>, properties: Properties): RotatedPillarBlock(properties) {}
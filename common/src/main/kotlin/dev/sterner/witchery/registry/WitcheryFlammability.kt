package dev.sterner.witchery.registry

import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FireBlock

/**
 * Flammability is weird...
 */
object WitcheryFlammability {

    fun register() {
        val fire = Blocks.FIRE as FireBlock
        //fire.setFlammable(block, igniteOdds, burnOdds)
        fire.setFlammable(WitcheryBlocks.ROWAN_LOG.get(), 5, 5)
        fire.setFlammable(WitcheryBlocks.ROWAN_WOOD.get(), 5, 5)
        fire.setFlammable(WitcheryBlocks.STRIPPED_ROWAN_LOG.get(), 5, 5)
        fire.setFlammable(WitcheryBlocks.STRIPPED_ROWAN_WOOD.get(), 5, 5)
        fire.setFlammable(WitcheryBlocks.ROWAN_LEAVES.get(), 30, 60)
        fire.setFlammable(WitcheryBlocks.ROWAN_BERRY_LEAVES.get(), 30, 60)
        fire.setFlammable(WitcheryBlocks.ROWAN_PLANKS.get(), 5, 20)
        fire.setFlammable(WitcheryBlocks.ROWAN_STAIRS.get(), 5, 20)
        fire.setFlammable(WitcheryBlocks.ROWAN_SLAB.get(), 5, 20)
        fire.setFlammable(WitcheryBlocks.ROWAN_FENCE.get(), 5, 20)
        fire.setFlammable(WitcheryBlocks.ROWAN_FENCE_GATE.get(), 5, 20)

        fire.setFlammable(WitcheryBlocks.ALDER_LOG.get(), 5, 5)
        fire.setFlammable(WitcheryBlocks.ALDER_WOOD.get(), 5, 5)
        fire.setFlammable(WitcheryBlocks.STRIPPED_ALDER_LOG.get(), 5, 5)
        fire.setFlammable(WitcheryBlocks.STRIPPED_ALDER_WOOD.get(), 5, 5)
        fire.setFlammable(WitcheryBlocks.ALDER_LEAVES.get(), 30, 60)
        fire.setFlammable(WitcheryBlocks.ALDER_PLANKS.get(), 5, 20)
        fire.setFlammable(WitcheryBlocks.ALDER_STAIRS.get(), 5, 20)
        fire.setFlammable(WitcheryBlocks.ALDER_SLAB.get(), 5, 20)
        fire.setFlammable(WitcheryBlocks.ALDER_FENCE.get(), 5, 20)
        fire.setFlammable(WitcheryBlocks.ALDER_FENCE_GATE.get(), 5, 20)

        fire.setFlammable(WitcheryBlocks.HAWTHORN_LOG.get(), 5, 5)
        fire.setFlammable(WitcheryBlocks.HAWTHORN_WOOD.get(), 5, 5)
        fire.setFlammable(WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get(), 5, 5)
        fire.setFlammable(WitcheryBlocks.STRIPPED_HAWTHORN_WOOD.get(), 5, 5)
        fire.setFlammable(WitcheryBlocks.HAWTHORN_LEAVES.get(), 30, 60)
        fire.setFlammable(WitcheryBlocks.HAWTHORN_PLANKS.get(), 5, 20)
        fire.setFlammable(WitcheryBlocks.HAWTHORN_STAIRS.get(), 5, 20)
        fire.setFlammable(WitcheryBlocks.HAWTHORN_SLAB.get(), 5, 20)
        fire.setFlammable(WitcheryBlocks.HAWTHORN_FENCE.get(), 5, 20)
        fire.setFlammable(WitcheryBlocks.HAWTHORN_FENCE_GATE.get(), 5, 20)
    }
}
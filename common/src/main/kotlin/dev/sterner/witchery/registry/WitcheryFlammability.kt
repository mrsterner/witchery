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
    }
}
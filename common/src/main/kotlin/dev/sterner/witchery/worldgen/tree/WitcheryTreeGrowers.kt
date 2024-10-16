package dev.sterner.witchery.worldgen.tree

import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys
import net.minecraft.world.level.block.grower.TreeGrower
import java.util.*

object WitcheryTreeGrowers {
    val ROWAN = TreeGrower("rowan",
        Optional.empty(), // Mega Tree
        Optional.of(WitcheryWorldgenKeys.ROWAN_KEY),
        Optional.empty() // Flowers
    )
}
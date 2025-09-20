package dev.sterner.witchery.registry

import net.minecraft.world.level.block.Block

object WitcheryFlammability {

    data class Flame(val block: RegistrySupplier<out Block>, val igniteOdds: Int, val burnOdds: Int)

    val flammableBlocks: List<Flame> = listOf(
        Flame(WitcheryBlocks.BLOOD_STAINED_HAY, 40, 60),

        Flame(WitcheryBlocks.SPANISH_MOSS, 5, 5),
        Flame(WitcheryBlocks.BLOOD_STAINED_WOOL, 5, 5),

        Flame(WitcheryBlocks.ROWAN_LOG, 5, 5),
        Flame(WitcheryBlocks.ROWAN_WOOD, 5, 5),
        Flame(WitcheryBlocks.STRIPPED_ROWAN_LOG, 5, 5),
        Flame(WitcheryBlocks.STRIPPED_ROWAN_WOOD, 5, 5),
        Flame(WitcheryBlocks.ROWAN_LEAVES, 30, 60),
        Flame(WitcheryBlocks.ROWAN_BERRY_LEAVES, 30, 60),
        Flame(WitcheryBlocks.ROWAN_PLANKS, 5, 20),
        Flame(WitcheryBlocks.ROWAN_STAIRS, 5, 20),
        Flame(WitcheryBlocks.ROWAN_SLAB, 5, 20),
        Flame(WitcheryBlocks.ROWAN_FENCE, 5, 20),
        Flame(WitcheryBlocks.ROWAN_FENCE_GATE, 5, 20),

        Flame(WitcheryBlocks.ALDER_LOG, 5, 5),
        Flame(WitcheryBlocks.ALDER_WOOD, 5, 5),
        Flame(WitcheryBlocks.STRIPPED_ALDER_LOG, 5, 5),
        Flame(WitcheryBlocks.STRIPPED_ALDER_WOOD, 5, 5),
        Flame(WitcheryBlocks.ALDER_LEAVES, 30, 60),
        Flame(WitcheryBlocks.ALDER_PLANKS, 5, 20),
        Flame(WitcheryBlocks.ALDER_STAIRS, 5, 20),
        Flame(WitcheryBlocks.ALDER_SLAB, 5, 20),
        Flame(WitcheryBlocks.ALDER_FENCE, 5, 20),
        Flame(WitcheryBlocks.ALDER_FENCE_GATE, 5, 20),

        Flame(WitcheryBlocks.HAWTHORN_LOG, 5, 5),
        Flame(WitcheryBlocks.HAWTHORN_WOOD, 5, 5),
        Flame(WitcheryBlocks.STRIPPED_HAWTHORN_LOG, 5, 5),
        Flame(WitcheryBlocks.STRIPPED_HAWTHORN_WOOD, 5, 5),
        Flame(WitcheryBlocks.HAWTHORN_LEAVES, 30, 60),
        Flame(WitcheryBlocks.HAWTHORN_PLANKS, 5, 20),
        Flame(WitcheryBlocks.HAWTHORN_STAIRS, 5, 20),
        Flame(WitcheryBlocks.HAWTHORN_SLAB, 5, 20),
        Flame(WitcheryBlocks.HAWTHORN_FENCE, 5, 20),
        Flame(WitcheryBlocks.HAWTHORN_FENCE_GATE, 5, 20)
    )
}

package dev.sterner.witchery.fabric.registry

import dev.sterner.witchery.registry.WitcheryBlocks
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry

object WitcheryOxidizables {

    fun register() {
        OxidizableBlocksRegistry.registerOxidizableBlockPair(
            WitcheryBlocks.COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN.get()
        )
        OxidizableBlocksRegistry.registerOxidizableBlockPair(
            WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN.get()
        )
        OxidizableBlocksRegistry.registerOxidizableBlockPair(
            WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN.get()
        )

        OxidizableBlocksRegistry.registerWaxableBlockPair(
            WitcheryBlocks.COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN.get()
        )
        OxidizableBlocksRegistry.registerWaxableBlockPair(
            WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN.get()
        )
        OxidizableBlocksRegistry.registerWaxableBlockPair(
            WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN.get()
        )
        OxidizableBlocksRegistry.registerWaxableBlockPair(
            WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN.get()
        )


        OxidizableBlocksRegistry.registerOxidizableBlockPair(
            WitcheryBlocks.COPPER_CAULDRON.get(),
            WitcheryBlocks.EXPOSED_COPPER_CAULDRON.get()
        )
        OxidizableBlocksRegistry.registerOxidizableBlockPair(
            WitcheryBlocks.EXPOSED_COPPER_CAULDRON.get(),
            WitcheryBlocks.WEATHERED_COPPER_CAULDRON.get()
        )
        OxidizableBlocksRegistry.registerOxidizableBlockPair(
            WitcheryBlocks.WEATHERED_COPPER_CAULDRON.get(),
            WitcheryBlocks.OXIDIZED_COPPER_CAULDRON.get()
        )

        OxidizableBlocksRegistry.registerWaxableBlockPair(
            WitcheryBlocks.COPPER_CAULDRON.get(),
            WitcheryBlocks.WAXED_COPPER_CAULDRON.get()
        )
        OxidizableBlocksRegistry.registerWaxableBlockPair(
            WitcheryBlocks.EXPOSED_COPPER_CAULDRON.get(),
            WitcheryBlocks.WAXED_EXPOSED_COPPER_CAULDRON.get()
        )
        OxidizableBlocksRegistry.registerWaxableBlockPair(
            WitcheryBlocks.WEATHERED_COPPER_CAULDRON.get(),
            WitcheryBlocks.WAXED_WEATHERED_COPPER_CAULDRON.get()
        )
        OxidizableBlocksRegistry.registerWaxableBlockPair(
            WitcheryBlocks.OXIDIZED_COPPER_CAULDRON.get(),
            WitcheryBlocks.WAXED_OXIDIZED_COPPER_CAULDRON.get()
        )



        OxidizableBlocksRegistry.registerOxidizableBlockPair(
            WitcheryBlocks.COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get()
        )
        OxidizableBlocksRegistry.registerOxidizableBlockPair(
            WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get()
        )
        OxidizableBlocksRegistry.registerOxidizableBlockPair(
            WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get()
        )

        OxidizableBlocksRegistry.registerWaxableBlockPair(
            WitcheryBlocks.COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get()
        )
        OxidizableBlocksRegistry.registerWaxableBlockPair(
            WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get()
        )
        OxidizableBlocksRegistry.registerWaxableBlockPair(
            WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get()
        )
        OxidizableBlocksRegistry.registerWaxableBlockPair(
            WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get()
        )
    }
}
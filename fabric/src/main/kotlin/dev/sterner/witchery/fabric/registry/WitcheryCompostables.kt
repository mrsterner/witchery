package dev.sterner.witchery.fabric.registry

import dev.sterner.witchery.registry.WitcheryItems
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry

object WitcheryCompostables {

    fun register() {
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.BELLADONNA_FLOWER.get(), 0.3f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.BELLADONNA_SEEDS.get(), 0.5f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.ENT_TWIG.get(), 0.5f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.GARLIC.get(), 0.4f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.MANDRAKE_ROOT.get(), 0.5f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.MANDRAKE_SEEDS.get(), 0.3f)

        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.WATER_ARTICHOKE_SEEDS.get(), 0.3f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.WATER_ARTICHOKE_GLOBE.get(), 0.5f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.WOLFSBANE.get(), 0.3f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.WOLFSBANE_SEEDS.get(), 0.3f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.WORMWOOD.get(), 0.3f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.WORMWOOD_SEEDS.get(), 0.3f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.ALDER_LEAVES.get(), 0.3f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.HAWTHORN_LEAVES.get(), 0.3f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.ROWAN_LEAVES.get(), 0.3f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.ROWAN_BERRY_LEAVES.get(), 0.3f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.ALDER_SAPLING.get(), 0.3f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.HAWTHORN_SAPLING.get(), 0.3f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.ROWAN_SAPLING.get(), 0.3f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.EMBER_MOSS.get(), 0.3f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.GLINTWEED.get(), 0.3f)
        CompostingChanceRegistry.INSTANCE.add(WitcheryItems.GRASSPER.get(), 0.5f)

    }
}

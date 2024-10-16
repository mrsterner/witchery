package dev.sterner.witchery.registry

import dev.architectury.registry.CreativeTabRegistry
import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component

object WitcheryCreativeModeTabs {

    val TABS = DeferredRegister.create(Witchery.MODID, Registries.CREATIVE_MODE_TAB)

    val MAIN = TABS.register("main") {
        CreativeTabRegistry.create {
            it.title(Component.translatable("witchery.main"))
            it.icon { WitcheryItems.GUIDEBOOK.get().defaultInstance }
            it.displayItems { _, output ->
                output.accept(WitcheryItems.GUIDEBOOK.get())
                output.accept(WitcheryItems.MUTANDIS.get())
                output.accept(WitcheryItems.MUTANDIS_EXTREMIS.get())
                output.accept(WitcheryItems.DEEPSLATE_ALTAR_BLOCK.get())
                output.accept(WitcheryItems.CAULDRON.get())
                output.accept(WitcheryItems.IRON_WITCHES_OVEN.get())
                output.accept(WitcheryItems.IRON_WITCHES_OVEN_FUME_EXTENSION.get())
                output.accept(WitcheryItems.COPPER_WITCHES_OVEN.get())
                output.accept(WitcheryItems.COPPER_WITCHES_OVEN_FUME_EXTENSION.get())

                output.accept(WitcheryItems.ROWAN_LOG.get())
                output.accept(WitcheryItems.ROWAN_WOOD.get())
                output.accept(WitcheryItems.STRIPPED_ROWAN_LOG.get())
                output.accept(WitcheryItems.STRIPPED_ROWAN_WOOD.get())
                output.accept(WitcheryItems.ROWAN_LEAVES.get())
                output.accept(WitcheryItems.ROWAN_BERRY_LEAVES.get())
                output.accept(WitcheryItems.ROWAN_PLANKS.get())
                output.accept(WitcheryItems.ROWAN_STAIRS.get())
                output.accept(WitcheryItems.ROWAN_SLAB.get())
                output.accept(WitcheryItems.ROWAN_FENCE.get())
                output.accept(WitcheryItems.ROWAN_FENCE_GATE.get())
                output.accept(WitcheryItems.ROWAN_DOOR.get())
                output.accept(WitcheryItems.ROWAN_TRAPDOOR.get())
                output.accept(WitcheryItems.ROWAN_PRESSURE_PLATE.get())
                output.accept(WitcheryItems.ROWAN_BUTTON.get())
                output.accept(WitcheryItems.ROWAN_SAPLING.get())
                output.accept(WitcheryItems.ROWAN_SIGN.get())
                output.accept(WitcheryItems.ROWAN_HANGING_SIGN.get())
                // Boat
                // Chest Boat

                output.accept(WitcheryItems.WAYSTONE.get())
                output.accept(WitcheryItems.GLINTWEED.get())
                output.accept(WitcheryItems.EMBER_MOSS.get())
                output.accept(WitcheryItems.SPANISH_MOSS.get())
                output.accept(WitcheryItems.MANDRAKE_SEEDS.get())
                output.accept(WitcheryItems.MANDRAKE_ROOT.get())
                output.accept(WitcheryItems.SNOWBELL_SEEDS.get())
                output.accept(WitcheryItems.ICY_NEEDLE.get())
                output.accept(WitcheryItems.BELLADONNA_SEEDS.get())
                output.accept(WitcheryItems.BELLADONNA_FLOWER.get())
                output.accept(WitcheryItems.WATER_ARTICHOKE_SEEDS.get())
                output.accept(WitcheryItems.WATER_ARTICHOKE_GLOBE.get())
                output.accept(WitcheryItems.WOLFSBANE_SEEDS.get())
                output.accept(WitcheryItems.WOLFSBANE.get())
                output.accept(WitcheryItems.WORMWOOD_SEEDS.get())
                output.accept(WitcheryItems.WORMWOOD.get())
                output.accept(WitcheryItems.GARLIC.get())
                output.accept(WitcheryItems.WOOD_ASH.get())
                output.accept(WitcheryItems.BONE_NEEDLE.get())
                output.accept(WitcheryItems.TAGLOCK.get())
                output.accept(WitcheryItems.DEMON_HEART.get())
                output.accept(WitcheryItems.GYPSUM.get())
                output.accept(WitcheryItems.CLAY_JAR.get())
                output.accept(WitcheryItems.JAR.get())
                output.accept(WitcheryItems.BREATH_OF_THE_GODDESS.get())
                output.accept(WitcheryItems.WHIFF_OF_MAGIC.get())
                output.accept(WitcheryItems.FOUL_FUME.get())
                output.accept(WitcheryItems.TEAR_OF_THE_GODDESS.get())
                output.accept(WitcheryItems.OIL_OF_VITRIOL.get())
                output.accept(WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get())
                output.accept(WitcheryItems.HINT_OF_REBIRTH.get())
                output.accept(WitcheryItems.REEK_OF_MISFORTUNE.get())
                output.accept(WitcheryItems.ODOR_OF_PURITY.get())
                output.accept(WitcheryItems.DROP_OF_LUCK.get())
                output.accept(WitcheryItems.ENDER_DEW.get())
                output.accept(WitcheryItems.DEMONS_BLOOD.get())
                output.accept(WitcheryItems.MELLIFLUOUS_HUNGER.get())
                output.accept(WitcheryItems.FOCUSED_WILL.get())
                output.accept(WitcheryItems.CONDENSED_FEAR.get())
                output.accept(WitcheryItems.RITUAL_CHALK.get())
                output.accept(WitcheryItems.GOLDEN_CHALK.get())
                output.accept(WitcheryItems.INFERNAL_CHALK.get())
                output.accept(WitcheryItems.OTHERWHERE_CHALK.get())
            }
        }
    }
}
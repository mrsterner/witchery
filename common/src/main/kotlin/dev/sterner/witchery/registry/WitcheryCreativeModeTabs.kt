package dev.sterner.witchery.registry

import dev.architectury.registry.CreativeTabRegistry
import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryItems.ALTAR
import dev.sterner.witchery.registry.WitcheryItems.BELLADONNA_FLOWER
import dev.sterner.witchery.registry.WitcheryItems.BONE_NEEDLE
import dev.sterner.witchery.registry.WitcheryItems.BREATH_OF_THE_GODDESS
import dev.sterner.witchery.registry.WitcheryItems.CAULDRON
import dev.sterner.witchery.registry.WitcheryItems.CLAY_JAR
import dev.sterner.witchery.registry.WitcheryItems.DEMON_HEART
import dev.sterner.witchery.registry.WitcheryItems.DROP_OF_LUCK
import dev.sterner.witchery.registry.WitcheryItems.ENDER_DEW
import dev.sterner.witchery.registry.WitcheryItems.EXHALE_OF_THE_HORNED_ONE
import dev.sterner.witchery.registry.WitcheryItems.FOUL_FUME
import dev.sterner.witchery.registry.WitcheryItems.GOLDEN_CHALK
import dev.sterner.witchery.registry.WitcheryItems.GUIDEBOOK
import dev.sterner.witchery.registry.WitcheryItems.GYPSUM
import dev.sterner.witchery.registry.WitcheryItems.HINT_OF_REBIRTH
import dev.sterner.witchery.registry.WitcheryItems.INFERNAL_CHALK
import dev.sterner.witchery.registry.WitcheryItems.JAR
import dev.sterner.witchery.registry.WitcheryItems.MANDRAKE_ROOT
import dev.sterner.witchery.registry.WitcheryItems.MUTANDIS
import dev.sterner.witchery.registry.WitcheryItems.ODOR_OF_PURITY
import dev.sterner.witchery.registry.WitcheryItems.OIL_OF_VITRIOL
import dev.sterner.witchery.registry.WitcheryItems.OTHERWHERE_CHALK
import dev.sterner.witchery.registry.WitcheryItems.OVEN
import dev.sterner.witchery.registry.WitcheryItems.REEK_OF_MISFORTUNE
import dev.sterner.witchery.registry.WitcheryItems.RITUAL_CHALK
import dev.sterner.witchery.registry.WitcheryItems.TEAR_OF_THE_GODDESS
import dev.sterner.witchery.registry.WitcheryItems.WATER_ARTICHOKE_GLOBE
import dev.sterner.witchery.registry.WitcheryItems.WHIFF_OF_MAGIC
import dev.sterner.witchery.registry.WitcheryItems.WOOD_ASH
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component

object WitcheryCreativeModeTabs {

    val TABS = DeferredRegister.create(Witchery.MODID, Registries.CREATIVE_MODE_TAB)

    val MAIN = TABS.register("main") {
        CreativeTabRegistry.create {
            it.title(Component.translatable("witchery.main"))
            it.icon { GUIDEBOOK.get().defaultInstance }
            it.displayItems { _, output ->
                output.accept(GUIDEBOOK.get())
                output.accept(MUTANDIS.get())
                output.accept(ALTAR.get())
                output.accept(CAULDRON.get())
                output.accept(OVEN.get())
                output.accept(MANDRAKE_ROOT.get())
                output.accept(BELLADONNA_FLOWER.get())
                output.accept(WATER_ARTICHOKE_GLOBE.get())
                output.accept(WOOD_ASH.get())
                output.accept(BONE_NEEDLE.get())
                output.accept(DEMON_HEART.get())
                output.accept(GYPSUM.get())
                output.accept(CLAY_JAR.get())
                output.accept(JAR.get())
                output.accept(BREATH_OF_THE_GODDESS.get())
                output.accept(WHIFF_OF_MAGIC.get())
                output.accept(FOUL_FUME.get())
                output.accept(TEAR_OF_THE_GODDESS.get())
                output.accept(OIL_OF_VITRIOL.get())
                output.accept(EXHALE_OF_THE_HORNED_ONE.get())
                output.accept(HINT_OF_REBIRTH.get())
                output.accept(REEK_OF_MISFORTUNE.get())
                output.accept(ODOR_OF_PURITY.get())
                output.accept(DROP_OF_LUCK.get())
                output.accept(ENDER_DEW.get())
                output.accept(RITUAL_CHALK.get())
                output.accept(GOLDEN_CHALK.get())
                output.accept(INFERNAL_CHALK.get())
                output.accept(OTHERWHERE_CHALK.get())
            }
        }
    }
}
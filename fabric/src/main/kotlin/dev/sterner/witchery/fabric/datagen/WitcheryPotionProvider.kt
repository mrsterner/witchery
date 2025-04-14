package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data.ErosionHandler
import dev.sterner.witchery.item.potion.WitcheryPotionEffect
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient.*
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryPotionEffectRegistry
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import java.awt.Color
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class WitcheryPotionProvider(
    dataOutput: FabricDataOutput?,
    registriesFuture: CompletableFuture<HolderLookup.Provider>?
) : FabricCodecDataProvider<WitcheryPotionIngredient>(
    dataOutput,
    registriesFuture,
    PackOutput.Target.DATA_PACK,
    DIRECTORY,
    WitcheryPotionIngredient.CODEC
) {

    companion object {
        val DIRECTORY: String = "potion"
    }

    override fun getName(): String {
        return DIRECTORY
    }

    override fun configure(
        provider: BiConsumer<ResourceLocation, WitcheryPotionIngredient>,
        lookup: HolderLookup.Provider?
    ) {

        makeIngredient(provider, Items.SLIME_BALL)

        makeIngredient(provider,
            Items.BLAZE_POWDER,
            WitcheryPotionEffectRegistry.STRENGTH.get(),
            color = Color(255, 130, 20).rgb,
            capacity = -2
        )

        makeIngredient(provider,
            Items.GLISTERING_MELON_SLICE,
            WitcheryPotionEffectRegistry.INSTANT_HEALTH.get(),
            color = Color(255, 20, 20).rgb,
            capacity = -2
        )

        makeIngredient(provider,
            Items.SPIDER_EYE,
            WitcheryPotionEffectRegistry.POISON.get(),
            color = Color(100, 220, 20).rgb,
            capacity = -2
        )

        makeIngredient(provider,
            Items.GHAST_TEAR,
            WitcheryPotionEffectRegistry.REGENERATION.get(),
            color = Color(200, 200, 200).rgb,
            capacity = -2
        )

        makeIngredient(provider,
            Items.GOLDEN_CARROT,
            WitcheryPotionEffectRegistry.NIGHT_VISION.get(),
            color = Color(250, 180, 100).rgb,
            capacity = -2
        )

        makeIngredient(provider,
            Items.SUGAR,
            WitcheryPotionEffectRegistry.SPEED_BOOST.get(),
            color = Color(150, 180, 255).rgb,
            capacity = -2
        )

        makeIngredient(provider,
            WitcheryItems.MANDRAKE_ROOT.get(),
            altarPower = 50,
            capacity = 2,
            effectModifier = Optional.of(EffectModifier(durationMultiplier = 6))
        )

        makeIngredient(provider,
            WitcheryItems.TEAR_OF_THE_GODDESS.get(),
            altarPower = 100,
            capacity = 2
        )

        makeIngredient(provider,
            WitcheryItems.PHANTOM_VAPOR.get(),
            altarPower = 150,
            capacity = 2
        )
        makeIngredient(provider,
            Items.NETHER_STAR,
            altarPower = 0,
            capacity = 4
        )
        makeIngredient(provider,
            WitcheryItems.PENTACLE.get(),
            altarPower = 0,
            capacity = 8
        )

        makeIngredient(provider,
            Items.GOLD_NUGGET,
            altarPower = 50,
            generalModifier = Optional.of(GeneralModifier.NO_PARTICLE)
        )

        makeIngredient(provider,
            Items.FERMENTED_SPIDER_EYE,
            altarPower = 25,
            generalModifier = Optional.of(GeneralModifier.INVERT_NEXT)
        )
        makeIngredient(provider,
            WitcheryItems.ROWAN_BERRIES.get(),
            altarPower = 50,
            generalModifier = Optional.of(GeneralModifier.DRINK_SPEED_BOOST)
        )
        makeIngredient(provider,
            WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get(),
            altarPower = 50,
            generalModifier = Optional.of(GeneralModifier.DRINK_SPEED_BOOST)
        )
        makeIngredient(provider,
            WitcheryItems.SPANISH_MOSS.get(),
            altarPower = 50,
            generalModifier = Optional.of(GeneralModifier.DRINK_SPEED_BOOST)
        )

        makeIngredient(provider,
            Items.GLOWSTONE_DUST,
            altarPower = 50,
            effectModifier = Optional.of(EffectModifier(powerAddition = 1))
        )

        makeIngredient(provider,
            Items.BLAZE_ROD,
            altarPower = 100,
            effectModifier = Optional.of(EffectModifier(powerAddition = 2))
        )
        makeIngredient(provider,
            WitcheryItems.ATTUNED_STONE.get(),
            altarPower = 150,
            effectModifier = Optional.of(EffectModifier(powerAddition = 4))
        )
        makeIngredient(provider,
            Items.REDSTONE,
            altarPower = 50,
            effectModifier = Optional.of(EffectModifier(durationMultiplier = 2))
        )
        makeIngredient(provider,
            Items.OBSIDIAN,
            altarPower = 100,
            effectModifier = Optional.of(EffectModifier(durationMultiplier = 4))
        )

        makeIngredient(provider,
            Items.GUNPOWDER,
            altarPower = 100,
            type = Type.SPLASH
        )

        makeIngredient(provider,
            Items.COCOA_BEANS,
            altarPower = 100,
            dispersalModifier = Optional.of(DispersalModifier(rangeModifier = 2))
        )

        makeIngredient(provider,
            WitcheryItems.WISPY_COTTON.get(),
            altarPower = 150,
            dispersalModifier = Optional.of(DispersalModifier(rangeModifier = 4))
        )
        makeIngredient(provider,
            WitcheryItems.BELLADONNA_FLOWER.get(),
            altarPower = 50,
            dispersalModifier = Optional.of(DispersalModifier(lingeringDurationModifier = 2))
        )
        makeIngredient(provider,
            Items.LAPIS_LAZULI,
            altarPower = 100,
            dispersalModifier = Optional.of(DispersalModifier(lingeringDurationModifier = 3))
        )
        makeIngredient(provider,
            Items.END_STONE,
            altarPower = 150,
            dispersalModifier = Optional.of(DispersalModifier(lingeringDurationModifier = 4))
        )
    }

    private fun makeIngredient(
        provider: BiConsumer<ResourceLocation, WitcheryPotionIngredient>,
        item: Item,
        effect: WitcheryPotionEffect = WitcheryPotionEffectRegistry.EMPTY.get(),
        altarPower: Int = 200,
        capacity: Int = 0,
        generalModifier: Optional<GeneralModifier> = Optional.empty(),
        effectModifier: Optional<EffectModifier> = Optional.empty(),
        dispersalModifier: Optional<DispersalModifier> = Optional.empty(),
        type: Type = Type.CONSUMABLE,
        color: Int = Color(90, 222, 100).rgb

    ) {
        val fromId = BuiltInRegistries.ITEM.getKey(item)

        provider.accept(Witchery.id(fromId.path),
            WitcheryPotionIngredient(
                item.defaultInstance,
                effect,
                altarPower,
                capacity,
                generalModifier,
                effectModifier,
                dispersalModifier,
                type,
                color
                )
        )
    }
}
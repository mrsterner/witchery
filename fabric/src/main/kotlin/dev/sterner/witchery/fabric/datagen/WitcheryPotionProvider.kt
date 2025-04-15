package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.SpecialPotion
import dev.sterner.witchery.data.ErosionHandler
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient.*
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryMobEffects
import dev.sterner.witchery.registry.WitcherySpecialPotionEffects
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffects
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

        makeIngredient(provider, Items.SLIME_BALL,
            color = Color(200, 255, 50).rgb)

        makeIngredient(provider,
            Items.BLAZE_POWDER,
            MobEffects.DAMAGE_BOOST,
            color = Color(255, 130, 20).rgb,
            capacity = -2
        )

        makeIngredient(provider,
            Items.GLISTERING_MELON_SLICE,
            MobEffects.HEAL,
            color = Color(255, 20, 20).rgb,
            capacity = -2
        )

        makeIngredient(provider,
            Items.SPIDER_EYE,
            MobEffects.POISON,
            color = Color(100, 220, 20).rgb,
            capacity = -2
        )

        makeIngredient(provider,
            Items.GHAST_TEAR,
            MobEffects.REGENERATION,
            color = Color(200, 200, 200).rgb,
            capacity = -2
        )

        makeIngredient(provider,
            Items.GOLDEN_CARROT,
            MobEffects.NIGHT_VISION,
            color = Color(250, 180, 100).rgb,
            capacity = -2
        )

        makeIngredient(provider,
            Items.SUGAR,
            MobEffects.MOVEMENT_SPEED,
            color = Color(150, 180, 255).rgb,
            capacity = -2,
        )

        makeIngredient(provider,
            WitcheryItems.MANDRAKE_ROOT.get(),
            altarPower = 50,
            capacity = 2,
            effectModifier = EffectModifier(durationMultiplier = 6),
            color = Color(200, 60, 50).rgb
        )

        makeIngredient(provider,
            WitcheryItems.TEAR_OF_THE_GODDESS.get(),
            altarPower = 100,
            capacity = 2,
            color = Color(110, 110, 250).rgb
        )

        makeIngredient(provider,
            WitcheryItems.PHANTOM_VAPOR.get(),
            altarPower = 150,
            capacity = 2,
            color = Color(160, 160, 250).rgb
        )
        makeIngredient(provider,
            Items.NETHER_STAR,
            altarPower = 0,
            capacity = 4,
            color = Color(200, 200, 250).rgb
        )
        makeIngredient(provider,
            WitcheryItems.PENTACLE.get(),
            altarPower = 0,
            capacity = 8,
            color = Color(155, 70, 80).rgb
        )

        makeIngredient(provider,
            Items.GOLD_NUGGET,
            altarPower = 50,
            generalModifier = listOf(GeneralModifier.NO_PARTICLE),
            color = Color(255, 160, 20).rgb
        )

        makeIngredient(provider,
            Items.FERMENTED_SPIDER_EYE,
            altarPower = 25,
            generalModifier = listOf(GeneralModifier.INVERT_NEXT),
            color = Color(255, 70, 80).rgb
        )
        makeIngredient(provider,
            WitcheryItems.ROWAN_BERRIES.get(),
            altarPower = 50,
            generalModifier = listOf(GeneralModifier.DRINK_SPEED_BOOST),
            color = Color(255, 70, 20).rgb
        )
        makeIngredient(provider,
            WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get(),
            altarPower = 50,
            generalModifier = listOf(GeneralModifier.DRINK_SPEED_BOOST),
            color = Color(155, 190, 90).rgb
        )
        makeIngredient(provider,
            WitcheryItems.SPANISH_MOSS.get(),
            altarPower = 50,
            generalModifier = listOf(GeneralModifier.DRINK_SPEED_BOOST),
            color = Color(155, 190, 90).rgb
        )

        makeIngredient(provider,
            Items.GLOWSTONE_DUST,
            altarPower = 50,
            effectModifier = EffectModifier(powerAddition = 1),
            color = Color(255, 190, 90).rgb
        )

        makeIngredient(provider,
            Items.BLAZE_ROD,
            altarPower = 100,
            effectModifier = EffectModifier(powerAddition = 2),
            color = Color(255, 150, 90).rgb
        )
        makeIngredient(provider,
            WitcheryItems.ATTUNED_STONE.get(),
            altarPower = 150,
            effectModifier = EffectModifier(powerAddition = 4),
            color = Color(255, 10, 190).rgb
        )
        makeIngredient(provider,
            Items.REDSTONE,
            altarPower = 50,
            effectModifier = EffectModifier(durationMultiplier = 2),
            color = Color(255, 10, 90).rgb
        )
        makeIngredient(provider,
            Items.OBSIDIAN,
            altarPower = 100,
            effectModifier = EffectModifier(durationMultiplier = 4),
            color = Color(50, 20, 100).rgb
        )

        makeIngredient(provider,
            Items.GUNPOWDER,
            altarPower = 100,
            type = Type.SPLASH,
            color = Color(100, 100, 100).rgb
        )

        makeIngredient(provider,
            Items.COCOA_BEANS,
            altarPower = 100,
            dispersalModifier = DispersalModifier(rangeModifier = 2),
            color = Color(150, 100, 10).rgb
        )

        makeIngredient(provider,
            WitcheryItems.WISPY_COTTON.get(),
            altarPower = 150,
            dispersalModifier = DispersalModifier(rangeModifier = 4),
            color = Color(200, 200, 200).rgb
        )
        makeIngredient(provider,
            WitcheryItems.BELLADONNA_FLOWER.get(),
            altarPower = 50,
            dispersalModifier = DispersalModifier(lingeringDurationModifier = 2),
            color = Color(100, 100, 255).rgb
        )
        makeIngredient(provider,
            Items.LAPIS_LAZULI,
            altarPower = 100,
            dispersalModifier = DispersalModifier(lingeringDurationModifier = 3),
            color = Color(100, 100, 255).rgb
        )
        makeIngredient(provider,
            Items.END_STONE,
            altarPower = 150,
            dispersalModifier = DispersalModifier(lingeringDurationModifier = 4),
            color = Color(255, 255, 130).rgb
        )
        makeIngredient(provider,
            Items.DRAGON_BREATH,
            altarPower = 150,
            type = Type.LINGERING,
            color = Color(255, 140, 255).rgb
        )
        makeIngredient(provider,
            Items.ICE,
            altarPower = 50,
            color = Color(100, 150, 255).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.GROW_CROPS.id)
        )
    }

    private fun makeIngredient(
        provider: BiConsumer<ResourceLocation, WitcheryPotionIngredient>,
        item: Item,
        effect: Holder<MobEffect> = WitcheryMobEffects.EMPTY,
        specialPotion: Optional<ResourceLocation> = Optional.empty(),
        baseDuration: Int = 20 * 45,
        altarPower: Int = 200,
        capacity: Int = 0,
        generalModifier: List<GeneralModifier> = listOf(),
        effectModifier: EffectModifier = EffectModifier(0, 0, 1),
        dispersalModifier: DispersalModifier = DispersalModifier(1,1),
        type: Type = Type.CONSUMABLE,
        color: Int

    ) {
        val fromId = BuiltInRegistries.ITEM.getKey(item)

        provider.accept(Witchery.id(fromId.path),
            WitcheryPotionIngredient(
                item.defaultInstance,
                effect,
                specialPotion,
                baseDuration,
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
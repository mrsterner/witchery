package dev.sterner.witchery.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient.*
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryMobEffects
import dev.sterner.witchery.registry.WitcherySpecialPotionEffects
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.common.data.JsonCodecProvider
import java.awt.Color
import java.util.*
import java.util.concurrent.CompletableFuture

class WitcheryPotionProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper
) : JsonCodecProvider<WitcheryPotionIngredient>(
    output,
    PackOutput.Target.DATA_PACK,
    "potion",
    PackType.SERVER_DATA,
    WitcheryPotionIngredient.CODEC,
    lookupProvider,
    Witchery.MODID,
    existingFileHelper
) {

    override fun getName(): String {
        return "potion"
    }

    override fun gather() {
        makeIngredient(
            Items.NETHER_WART,
            capacity = 0,
            color = Color(255, 10, 50).rgb
        )

        makeIngredient(

            Items.BLAZE_POWDER,
            MobEffects.DAMAGE_BOOST,
            color = Color(255, 130, 20).rgb
        )
        makeIngredient(

            Items.GLISTERING_MELON_SLICE,
            MobEffects.HEAL,
            color = Color(255, 20, 20).rgb
        )
        makeIngredient(

            Items.SPIDER_EYE,
            MobEffects.POISON,
            color = Color(100, 220, 20).rgb
        )
        makeIngredient(

            Items.GHAST_TEAR,
            MobEffects.REGENERATION,
            color = Color(200, 200, 200).rgb
        )
        makeIngredient(

            Items.GOLDEN_CARROT,
            MobEffects.NIGHT_VISION,
            color = Color(250, 180, 100).rgb
        )
        makeIngredient(

            Items.SUGAR,
            MobEffects.MOVEMENT_SPEED,
            color = Color(150, 180, 255).rgb
        )
        makeIngredient(

            Items.PRISMARINE_CRYSTALS,
            MobEffects.DIG_SPEED,
            color = Color(60, 220, 190).rgb
        )

        makeIngredient(

            Items.RABBIT_FOOT,
            MobEffects.JUMP,
            color = Color(185, 165, 130).rgb
        )

        makeIngredient(

            Items.TURTLE_SCUTE,
            MobEffects.DAMAGE_RESISTANCE,
            color = Color(40, 175, 95).rgb
        )

        makeIngredient(

            Items.MAGMA_CREAM,
            MobEffects.FIRE_RESISTANCE,
            color = Color(230, 130, 50).rgb
        )

        makeIngredient(

            Items.KELP,
            MobEffects.WATER_BREATHING,
            color = Color(50, 120, 220).rgb
        )

        makeIngredient(

            Items.GLOW_BERRIES,
            MobEffects.GLOWING,
            color = Color(250, 250, 110).rgb
        )

        makeIngredient(

            Items.INK_SAC,
            MobEffects.BLINDNESS,
            color = Color(30, 30, 35).rgb
        )

        makeIngredient(

            Items.CHICKEN,
            MobEffects.HUNGER,
            color = Color(120, 90, 60).rgb
        )

        makeIngredient(

            Items.WITHER_ROSE,
            MobEffects.WITHER,
            color = Color(30, 30, 30).rgb
        )

        makeIngredient(

            Items.ARMADILLO_SCUTE,
            MobEffects.HEALTH_BOOST,
            color = Color(90, 200, 240).rgb
        )

        makeIngredient(

            Items.GOLDEN_APPLE,
            MobEffects.ABSORPTION,
            color = Color(255, 215, 0).rgb
        )

        makeIngredient(

            Items.HONEY_BOTTLE,
            MobEffects.SATURATION,
            color = Color(240, 190, 40).rgb
        )

        makeIngredient(

            Items.SHULKER_SHELL,
            MobEffects.LEVITATION,
            color = Color(200, 180, 200).rgb
        )

        makeIngredient(

            Items.PHANTOM_MEMBRANE,
            MobEffects.SLOW_FALLING,
            color = Color(180, 220, 230).rgb
        )

        makeIngredient(

            Items.POISONOUS_POTATO,
            MobEffects.UNLUCK,
            color = Color(120, 130, 60).rgb
        )

        makeIngredient(

            Items.NAUTILUS_SHELL,
            MobEffects.CONDUIT_POWER,
            color = Color(60, 180, 210).rgb
        )

        makeIngredient(

            Items.COD,
            MobEffects.DOLPHINS_GRACE,
            color = Color(160, 200, 230).rgb
        )

        makeIngredient(

            Items.CROSSBOW,
            MobEffects.BAD_OMEN,
            color = Color(100, 40, 40).rgb
        )

        makeIngredient(

            Items.EMERALD,
            MobEffects.HERO_OF_THE_VILLAGE,
            color = Color(60, 220, 120).rgb
        )

        makeIngredient(

            Items.SOUL_SOIL,
            MobEffects.DARKNESS,
            color = Color(35, 45, 60).rgb
        )
        makeIngredient(

            Items.COBWEB,
            WitcheryMobEffects.REFLECT_ARROW,
            color = Color(200, 200, 255).rgb
        )
        makeIngredient(

            Items.PUFFERFISH,
            WitcheryMobEffects.POISON_WEAPON,
            color = Color(100, 200, 100).rgb
        )
        makeIngredient(

            Items.ROTTEN_FLESH,
            WitcheryMobEffects.DISEASE,
            color = Color(220, 100, 100).rgb
        )
        makeIngredient(

            Items.CLAY,
            WitcheryMobEffects.FORTUNE_TOOL,
            color = Color(130, 100, 255).rgb
        )
        makeIngredient(

            Items.BLUE_ORCHID,
            WitcheryMobEffects.BANE_OF_ARTHROPODS_WEAPON,
            color = Color(180, 100, 255).rgb
        )
        makeIngredient(

            WitcheryItems.WOLFSBANE.get(),
            WitcheryMobEffects.WEREWOLF_BOUND,
            color = Color(250, 180, 30).rgb
        )
        makeIngredient(

            WitcheryItems.MANDRAKE_ROOT.get(),
            altarPower = 50,
            capacity = 1,
            effectModifier = EffectModifier(durationMultiplier = 6),
            color = Color(60, 250, 50).rgb
        )
        makeIngredient(

            WitcheryItems.TEAR_OF_THE_GODDESS.get(),
            altarPower = 100,
            capacity = 1,
            color = Color(110, 110, 250).rgb
        )
        makeIngredient(

            WitcheryItems.PHANTOM_VAPOR.get(),
            altarPower = 150,
            capacity = 1,
            color = Color(160, 160, 250).rgb
        )
        makeIngredient(

            Items.AMETHYST_SHARD,
            altarPower = 250,
            capacity = 1,
            color = Color(180, 100, 250).rgb
        )
        makeIngredient(

            Items.HEART_OF_THE_SEA,
            altarPower = 200,
            capacity = 1,
            color = Color(100, 100, 250).rgb
        )
        makeIngredient(

            Items.NETHER_STAR,
            altarPower = 0,
            capacity = 3,
            color = Color(200, 200, 250).rgb
        )
        makeIngredient(

            WitcheryItems.PENTACLE.get(),
            altarPower = 0,
            capacity = 4,
            color = Color(160, 30, 60).rgb
        )
        makeIngredient(

            Items.GOLD_NUGGET,
            altarPower = 50,
            capacity = 0,
            generalModifier = listOf(GeneralModifier.NO_PARTICLE),
            color = Color(255, 160, 20).rgb
        )
        makeIngredient(

            Items.FERMENTED_SPIDER_EYE,
            altarPower = 25,
            capacity = 0,
            generalModifier = listOf(GeneralModifier.INVERT_NEXT),
            color = Color(255, 70, 80).rgb
        )
        makeIngredient(

            WitcheryItems.ROWAN_BERRIES.get(),
            altarPower = 50,
            capacity = 0,
            generalModifier = listOf(GeneralModifier.DRINK_SPEED_BOOST),
            color = Color(255, 70, 20).rgb
        )
        makeIngredient(

            WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get(),
            altarPower = 50,
            capacity = 0,
            generalModifier = listOf(GeneralModifier.DRINK_SPEED_BOOST),
            color = Color(155, 190, 90).rgb
        )
        makeIngredient(

            WitcheryItems.SPANISH_MOSS.get(),
            altarPower = 50,
            capacity = 0,
            generalModifier = listOf(GeneralModifier.DRINK_SPEED_BOOST),
            color = Color(155, 190, 90).rgb
        )
        makeIngredient(

            Items.GLOWSTONE_DUST,
            altarPower = 50,
            effectModifier = EffectModifier(powerAddition = 1),
            color = Color(255, 190, 90).rgb
        )
        makeIngredient(

            Items.BLAZE_ROD,
            altarPower = 100,
            effectModifier = EffectModifier(powerAddition = 2),
            color = Color(255, 150, 90).rgb
        )
        makeIngredient(

            WitcheryItems.ATTUNED_STONE.get(),
            altarPower = 150,
            effectModifier = EffectModifier(powerAddition = 4),
            color = Color(255, 10, 190).rgb
        )
        makeIngredient(

            Items.REDSTONE,
            altarPower = 50,
            effectModifier = EffectModifier(durationMultiplier = 2),
            color = Color(255, 10, 90).rgb
        )
        makeIngredient(

            Items.OBSIDIAN,
            altarPower = 100,
            effectModifier = EffectModifier(durationMultiplier = 4),
            color = Color(50, 20, 100).rgb
        )
        makeIngredient(

            Items.GUNPOWDER,
            capacity = 0,
            altarPower = 100,
            type = Type.SPLASH,
            color = Color(100, 100, 100).rgb
        )
        makeIngredient(

            Items.COCOA_BEANS,
            altarPower = 100,
            dispersalModifier = DispersalModifier(rangeModifier = 2),
            color = Color(250, 100, 10).rgb
        )
        makeIngredient(

            WitcheryItems.WISPY_COTTON.get(),
            altarPower = 150,
            dispersalModifier = DispersalModifier(rangeModifier = 4),
            color = Color(200, 200, 200).rgb
        )
        makeIngredient(

            WitcheryItems.BELLADONNA_FLOWER.get(),
            altarPower = 50,
            dispersalModifier = DispersalModifier(lingeringDurationModifier = 2),
            color = Color(100, 100, 255).rgb
        )
        makeIngredient(

            Items.LAPIS_LAZULI,
            altarPower = 100,
            dispersalModifier = DispersalModifier(lingeringDurationModifier = 3),
            color = Color(100, 100, 255).rgb
        )
        makeIngredient(

            Items.END_STONE,
            altarPower = 150,
            dispersalModifier = DispersalModifier(lingeringDurationModifier = 4),
            color = Color(255, 255, 130).rgb
        )
        makeIngredient(

            Items.DRAGON_BREATH,
            altarPower = 150,
            type = Type.LINGERING,
            color = Color(255, 140, 255).rgb
        )
        makeIngredient(

            Items.APPLE,
            altarPower = 50,
            color = Color(255, 100, 100).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.HARVEST.id)
        )
        makeIngredient(

            Items.BONE_MEAL,
            altarPower = 250,
            color = Color(255, 255, 255).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.FERTILE.id)
        )
        makeIngredient(

            Items.COAL,
            altarPower = 250,
            color = Color(100, 100, 100).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.EXTINGUISH.id)
        )
        makeIngredient(

            Items.DANDELION,
            altarPower = 200,
            color = Color(255, 255, 100).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.GROW_FLOWERS.id)
        )
        makeIngredient(

            Items.DIRT,
            altarPower = 0,
            color = Color(250, 100, 60).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.TILL_LAND.id)
        )
        makeIngredient(

            Items.LILY_PAD,
            altarPower = 200,
            color = Color(100, 255, 100).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.GROW_LILY.id)
        )
        makeIngredient(

            Items.DEAD_BUSH,
            altarPower = 200,
            color = Color(200, 140, 50).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.PRUNE_LEAVES.id)
        )
        makeIngredient(

            Items.SAND,
            altarPower = 0,
            color = Color(255, 255, 100).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.PART_WATER.id)
        )
        makeIngredient(

            Items.WHEAT_SEEDS,
            altarPower = 0,
            color = Color(100, 255, 100).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.PLANT_DROPPED_SEEDS.id)
        )
        makeIngredient(

            Items.STRING,
            altarPower = 0,
            color = Color(255, 255, 255).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.FELL_TREE.id)
        )
        makeIngredient(

            Items.COBBLESTONE,
            altarPower = 100,
            color = Color(100, 100, 100).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.PART_LAVA.id)
        )
        makeIngredient(

            WitcheryItems.ENT_TWIG.get(),
            altarPower = 350,
            color = Color(250, 120, 30).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.SPROUTING.id)
        )
        makeIngredient(

            Items.SLIME_BALL,
            altarPower = 150,
            color = Color(100, 255, 100).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.PULL.id)
        )
        makeIngredient(

            Items.STICK,
            capacity = -1,
            altarPower = 200,
            color = Color(170, 170, 40).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.PUSH.id)
        )
        makeIngredient(

            Items.ENDER_PEARL,
            capacity = -2,
            altarPower = 1000,
            color = Color(70, 200, 170).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.TELEPORT.id)
        )
        makeIngredient(

            Items.ROSE_BUSH,
            capacity = -2,
            altarPower = 500,
            color = Color(255, 70, 70).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.TAME_ANIMALS.id)
        )
        makeIngredient(

            Items.POPPY,
            capacity = -2,
            altarPower = 500,
            color = Color(255, 59, 59).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.LOVE.id)
        )
        makeIngredient(

            Items.BROWN_MUSHROOM,
            capacity = -3,
            altarPower = 2500,
            color = Color(100, 255, 100).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.GROW.id)
        )
        makeIngredient(

            Items.RED_MUSHROOM,
            capacity = -3,
            altarPower = 2500,
            color = Color(100, 255, 100).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.SHRINK.id)
        )
        makeIngredient(

            WitcheryItems.SPECTRAL_DUST.get(),
            capacity = -3,
            altarPower = 3000,
            color = Color(50, 190, 210).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.SOUL_SEVERANCE.id)
        )
        makeIngredient(

            WitcheryItems.WITCHES_HAT.get(),
            capacity = -8,
            altarPower = 10000,
            color = Color(255, 20, 140).rgb,
            specialPotion = Optional.of(WitcherySpecialPotionEffects.SUMMON_LEONARD.id)
        )
    }

    private fun makeIngredient(
        item: Item,
        effect: Holder<MobEffect> = WitcheryMobEffects.EMPTY,
        specialPotion: Optional<ResourceLocation> = Optional.empty(),
        baseDuration: Int = 20 * 45,
        altarPower: Int = 200,
        capacity: Int = -1,
        generalModifier: List<GeneralModifier> = listOf(),
        effectModifier: EffectModifier = EffectModifier(0, 0, 1),
        dispersalModifier: DispersalModifier = DispersalModifier(1, 1),
        type: Type = Type.CONSUMABLE,
        color: Int

    ) {
        val fromId = BuiltInRegistries.ITEM.getKey(item)

        unconditional(
            Witchery.id(fromId.path),
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
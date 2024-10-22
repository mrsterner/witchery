package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.ritual.CommandType
import dev.sterner.witchery.block.ritual.RitualHelper
import dev.sterner.witchery.recipe.ShapelessRecipeWithComponentsBuilder
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipeBuilder
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipeBuilder
import dev.sterner.witchery.recipe.cauldron.ItemStackWithColor
import dev.sterner.witchery.recipe.distillery.DistilleryCraftingRecipeBuilder
import dev.sterner.witchery.recipe.oven.OvenCookingRecipeBuilder
import dev.sterner.witchery.recipe.ritual.RitualRecipeBuilder
import dev.sterner.witchery.recipe.spinning_wheel.SpinningWheelRecipeBuilder
import dev.sterner.witchery.registry.*
import dev.sterner.witchery.ritual.PushMobsRitual
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.core.component.DataComponentMap
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder
import net.minecraft.tags.ItemTags
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.alchemy.Potions
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.world.item.crafting.ShapelessRecipe
import net.minecraft.world.level.block.Blocks
import java.awt.Color
import java.util.concurrent.CompletableFuture
import kotlin.math.exp

class WitcheryRecipeProvider(output: FabricDataOutput, val registriesFuture: CompletableFuture<HolderLookup.Provider>) :
    FabricRecipeProvider(output, registriesFuture) {

    override fun buildRecipes(exporter: RecipeOutput) {

        val map = DataComponentMap.builder().set(WitcheryDataComponents.HAS_SOUP.get(), true).build()
        val list = NonNullList.create<Ingredient>()
        list.add(Ingredient.of(WitcheryItems.CHALICE.get()))
        list.add(Ingredient.of(WitcheryItems.REDSTONE_SOUP.get()))

        ShapelessRecipeWithComponentsBuilder.create(RecipeCategory.MISC, WitcheryItems.CHALICE.get(), map)
            .offerTo(exporter, Witchery.id("fill_chalice"), list)

        SpinningWheelRecipeBuilder.create()
            .addInput(Items.HAY_BLOCK.defaultInstance)
            .addInput(WitcheryItems.WHIFF_OF_MAGIC.get().defaultInstance)
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.GOLDEN_THREAD.get().defaultInstance)
            .save(exporter, Witchery.id("golden_thread"))

        SpinningWheelRecipeBuilder.create()
            .addInput(Items.WHITE_WOOL.defaultInstance)
            .addInput(WitcheryItems.WHIFF_OF_MAGIC.get().defaultInstance)
            .addInput(WitcheryItems.PHANTOM_VAPOR.get().defaultInstance)
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.IMPREGNATED_FABRIC.get().defaultInstance, 2)
            .save(exporter, Witchery.id("impregnated_fabric"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(Items.REDSTONE.defaultInstance, Color(255,50,50).rgb)
            .addInputWithColor(WitcheryItems.DROP_OF_LUCK.get().defaultInstance, Color(50,50,50).rgb)
            .addInputWithColor(WitcheryItems.WOOL_OF_BAT.get().defaultInstance, Color(150,50,50).rgb)
            .addInputWithColor(WitcheryItems.TONGUE_OF_DOG.get().defaultInstance, Color(255,50,50).rgb)
            .addInputWithColor(WitcheryItems.BELLADONNA_FLOWER.get().defaultInstance, Color(255,50,170).rgb)
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get().defaultInstance, Color(255,50,50).rgb)
            .setOutput(WitcheryItems.REDSTONE_SOUP.get().defaultInstance)
            .setAltarPower(100)
            .save(exporter,  Witchery.id("redstone_soup"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.REDSTONE_SOUP.get().defaultInstance, Color(255,50,50).rgb)
            .addInputWithColor(Items.FEATHER.defaultInstance, Color(250,250,250).rgb)
            .addInputWithColor(PotionContents.createItemStack(Items.POTION, Potions.SWIFTNESS), Color(125,165,250).rgb)
            .addInputWithColor(WitcheryItems.WOOL_OF_BAT.get().defaultInstance, Color(150,50,50).rgb)
            .addInputWithColor(WitcheryItems.BELLADONNA_FLOWER.get().defaultInstance, Color(255,180,10).rgb)
            .setOutput(WitcheryItems.FLYING_OINTMENT.get().defaultInstance)
            .setAltarPower(100)
            .save(exporter,  Witchery.id("flying_ointment"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.REDSTONE_SOUP.get().defaultInstance, Color(255,50,50).rgb)
            .addInputWithColor(WitcheryItems.ATTUNED_STONE.get().defaultInstance, Color(255,50,250).rgb)
            .addInputWithColor(PotionContents.createItemStack(Items.POTION, Potions.REGENERATION), Color(125,165,250).rgb)
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get().defaultInstance, Color(150,50,50).rgb)
            .addInputWithColor(Items.GOLDEN_APPLE.defaultInstance, Color(180,180,0).rgb)
            .addInputWithColor(WitcheryItems.ROWAN_SAPLING.get().defaultInstance, Color(55,250,10).rgb)
            .setOutput(WitcheryItems.SOUL_OF_THE_WORLD.get().defaultInstance)
            .setAltarPower(100)
            .save(exporter,  Witchery.id("soul_of_the_world"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.REDSTONE_SOUP.get().defaultInstance, Color(255,50,50).rgb)
            .addInputWithColor(PotionContents.createItemStack(Items.POTION, Potions.STRONG_HARMING), Color(225,165,50).rgb)
            .addInputWithColor(WitcheryItems.DEMON_HEART.get().defaultInstance, Color(255,50,20).rgb)
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get().defaultInstance, Color(150,50,50).rgb)
            .addInputWithColor(Items.FERMENTED_SPIDER_EYE.defaultInstance, Color(155,150,10).rgb)
            .addInputWithColor(WitcheryItems.REFINED_EVIL.get().defaultInstance, Color(255,100,10).rgb)
            .addInputWithColor(Items.BLAZE_ROD.defaultInstance, Color(255,50,10).rgb)
            .setOutput(WitcheryItems.INFERNAL_ANIMUS.get().defaultInstance)
            .setAltarPower(100)
            .save(exporter,  Witchery.id("infernal_animus"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.REDSTONE_SOUP.get().defaultInstance, Color(255,50,50).rgb)
            .addInputWithColor(PotionContents.createItemStack(Items.POTION, Potions.LONG_SWIFTNESS), Color(60,165,250).rgb)
            .addInputWithColor(Items.ENDER_EYE.defaultInstance, Color(4,250,130).rgb)
            .addInputWithColor(Items.ENDER_EYE.defaultInstance, Color(10,250,90).rgb)
            .addInputWithColor(WitcheryItems.DROP_OF_LUCK.get().defaultInstance, Color(50,50,50).rgb)
            .addInputWithColor(WitcheryItems.WOOL_OF_BAT.get().defaultInstance, Color(255,100,255).rgb)
            .setOutput(WitcheryItems.SPIRIT_OF_OTHERWHERE.get().defaultInstance)
            .setAltarPower(100)
            .save(exporter,  Witchery.id("spirit_of_otherwhere"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.REDSTONE_SOUP.get().defaultInstance, Color(255,50,50).rgb)
            .addInputWithColor(PotionContents.createItemStack(Items.POTION, Potions.LONG_INVISIBILITY), Color(60,165,250).rgb)
            .addInputWithColor(PotionContents.createItemStack(Items.POTION, Potions.LONG_FIRE_RESISTANCE), Color(255,160,60).rgb)
            .addInputWithColor(WitcheryItems.EMBER_MOSS.get().defaultInstance, Color(255,180,90).rgb)
            .addInputWithColor(Items.TORCH.defaultInstance, Color(250,250,50).rgb)
            .addInputWithColor(WitcheryItems.TONGUE_OF_DOG.get().defaultInstance, Color(200,200,255).rgb)
            .setOutput(WitcheryItems.GHOST_OF_THE_LIGHT.get().defaultInstance)
            .setAltarPower(100)
            .save(exporter,  Witchery.id("ghost_of_the_light"))


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.RITUAL_CHALK.get(), 2)
            .pattern("ATA")
            .pattern("AGA")
            .pattern("AGA")
            .define('A', WitcheryItems.WOOD_ASH.get())
            .define('T', WitcheryItems.TEAR_OF_THE_GODDESS.get())
            .define('G', WitcheryItems.GYPSUM.get())
            .unlockedBy("has_wood_ash", has(WitcheryItems.WOOD_ASH.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.CLAY_JAR.get(), 4)
            .pattern(" C ")
            .pattern("CCC")
            .define('C', Items.CLAY_BALL)
            .unlockedBy("has_clay", has(Items.CLAY_BALL))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.IRON_WITCHES_OVEN.get())
            .pattern(" B ")
            .pattern("III")
            .pattern("IBI")
            .define('B', Items.IRON_BARS)
            .define('I', Items.IRON_INGOT)
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.COPPER_WITCHES_OVEN.get())
            .pattern(" T ")
            .pattern("CCC")
            .pattern("CTC")
            .define('T', Items.COPPER_TRAPDOOR)
            .define('C', Items.COPPER_INGOT)
            .unlockedBy("has_copper", has(Items.COPPER_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.IRON_WITCHES_OVEN_FUME_EXTENSION.get())
            .pattern("BLB")
            .pattern("BGB")
            .pattern("IAI")
            .define('B', Items.BUCKET)
            .define('L', Items.LAVA_BUCKET)
            .define('G', Items.GLOWSTONE)
            .define('A', Items.IRON_BARS)
            .define('I', Items.IRON_BLOCK)
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.COPPER_WITCHES_OVEN_FUME_EXTENSION.get())
            .pattern("BLB")
            .pattern("BGB")
            .pattern("IAI")
            .define('B', Items.LIGHTNING_ROD)
            .define('L', Items.LAVA_BUCKET)
            .define('G', Items.GLOWSTONE)
            .define('A', Items.COPPER_TRAPDOOR)
            .define('I', Items.COPPER_BLOCK)
            .unlockedBy("has_copper", has(Items.COPPER_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.DEEPSLATE_ALTAR_BLOCK.get(), 3)
            .pattern("BWE")
            .pattern("DRD")
            .pattern("DRD")
            .define('B', WitcheryItems.BREATH_OF_THE_GODDESS.get())
            .define('E', WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get())
            .define('W', Items.POTION)
            .define('D', Items.DEEPSLATE_BRICKS)
            .define('R', WitcheryTags.ROWAN_LOG_ITEMS)
            .unlockedBy("has_logs", has(WitcheryTags.ROWAN_LOG_ITEMS))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.GUIDEBOOK.get())
            .pattern(" W ")
            .pattern("IBF")
            .pattern(" C ")
            .define('W', WitcheryItems.BELLADONNA_FLOWER.get())
            .define('I', Items.INK_SAC)
            .define('B', Items.BOOK)
            .define('F', Items.FEATHER)
            .define('C', ItemTags.COALS)
            .unlockedBy("has_book", has(Items.BOOK))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, WitcheryItems.ROWAN_STAIRS.get(), 4)
            .pattern("P  ")
            .pattern("PP ")
            .pattern("PPP")
            .define('P', WitcheryItems.ROWAN_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.ROWAN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, WitcheryItems.ROWAN_SLAB.get(), 6)
            .pattern("PPP")
            .define('P', WitcheryItems.ROWAN_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.ROWAN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, WitcheryItems.ROWAN_FENCE.get(), 3)
            .pattern("PSP")
            .pattern("PSP")
            .define('P', WitcheryItems.ROWAN_PLANKS.get())
            .define('S', Items.STICK)
            .unlockedBy("has_planks", has(WitcheryItems.ROWAN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, WitcheryItems.ROWAN_FENCE_GATE.get())
            .pattern("SPS")
            .pattern("SPS")
            .define('P', WitcheryItems.ROWAN_PLANKS.get())
            .define('S', Items.STICK)
            .unlockedBy("has_planks", has(WitcheryItems.ROWAN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, WitcheryItems.ROWAN_PRESSURE_PLATE.get())
            .pattern("PP")
            .define('P', WitcheryItems.ROWAN_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.ROWAN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, WitcheryItems.ROWAN_DOOR.get(), 3)
            .pattern("PP")
            .pattern("PP")
            .pattern("PP")
            .define('P', WitcheryItems.ROWAN_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.ROWAN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, WitcheryItems.ROWAN_TRAPDOOR.get(), 2)
            .pattern("PPP")
            .pattern("PPP")
            .define('P', WitcheryItems.ROWAN_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.ROWAN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.ROWAN_SIGN.get(), 3)
            .pattern("PPP")
            .pattern("PPP")
            .pattern(" S ")
            .define('P', WitcheryItems.ROWAN_PLANKS.get())
            .define('S', Items.STICK)
            .unlockedBy("has_planks", has(WitcheryItems.ROWAN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.ROWAN_HANGING_SIGN.get(), 6)
            .pattern("C C")
            .pattern("LLL")
            .pattern("LLL")
            .define('L', WitcheryItems.STRIPPED_ROWAN_LOG.get())
            .define('C', Items.CHAIN)
            .unlockedBy("has_logs", has(WitcheryTags.ROWAN_LOG_ITEMS))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, WitcheryItems.ROWAN_BOAT.get())
            .pattern("P P")
            .pattern("PPP")
            .define('P', WitcheryItems.ROWAN_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.ROWAN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, WitcheryItems.ALDER_STAIRS.get(), 4)
            .pattern("P  ")
            .pattern("PP ")
            .pattern("PPP")
            .define('P', WitcheryItems.ALDER_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.ALDER_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, WitcheryItems.ALDER_SLAB.get(), 6)
            .pattern("PPP")
            .define('P', WitcheryItems.ALDER_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.ALDER_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, WitcheryItems.ALDER_FENCE.get(), 3)
            .pattern("PSP")
            .pattern("PSP")
            .define('P', WitcheryItems.ALDER_PLANKS.get())
            .define('S', Items.STICK)
            .unlockedBy("has_planks", has(WitcheryItems.ALDER_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, WitcheryItems.ALDER_FENCE_GATE.get())
            .pattern("SPS")
            .pattern("SPS")
            .define('P', WitcheryItems.ALDER_PLANKS.get())
            .define('S', Items.STICK)
            .unlockedBy("has_planks", has(WitcheryItems.ALDER_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, WitcheryItems.ALDER_PRESSURE_PLATE.get())
            .pattern("PP")
            .define('P', WitcheryItems.ALDER_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.ALDER_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, WitcheryItems.ALDER_DOOR.get(), 3)
            .pattern("PP")
            .pattern("PP")
            .pattern("PP")
            .define('P', WitcheryItems.ALDER_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.ALDER_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, WitcheryItems.ALDER_TRAPDOOR.get(), 2)
            .pattern("PPP")
            .pattern("PPP")
            .define('P', WitcheryItems.ALDER_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.ALDER_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.ALDER_SIGN.get(), 3)
            .pattern("PPP")
            .pattern("PPP")
            .pattern(" S ")
            .define('P', WitcheryItems.ALDER_PLANKS.get())
            .define('S', Items.STICK)
            .unlockedBy("has_planks", has(WitcheryItems.ALDER_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.ALDER_HANGING_SIGN.get(), 6)
            .pattern("C C")
            .pattern("LLL")
            .pattern("LLL")
            .define('L', WitcheryItems.STRIPPED_ALDER_LOG.get())
            .define('C', Items.CHAIN)
            .unlockedBy("has_logs", has(WitcheryTags.ALDER_LOG_ITEMS))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, WitcheryItems.ALDER_BOAT.get())
            .pattern("P P")
            .pattern("PPP")
            .define('P', WitcheryItems.ALDER_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.ALDER_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, WitcheryItems.HAWTHORN_STAIRS.get(), 4)
            .pattern("P  ")
            .pattern("PP ")
            .pattern("PPP")
            .define('P', WitcheryItems.HAWTHORN_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.HAWTHORN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, WitcheryItems.HAWTHORN_SLAB.get(), 6)
            .pattern("PPP")
            .define('P', WitcheryItems.HAWTHORN_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.HAWTHORN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, WitcheryItems.HAWTHORN_FENCE.get(), 3)
            .pattern("PSP")
            .pattern("PSP")
            .define('P', WitcheryItems.HAWTHORN_PLANKS.get())
            .define('S', Items.STICK)
            .unlockedBy("has_planks", has(WitcheryItems.HAWTHORN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, WitcheryItems.HAWTHORN_FENCE_GATE.get())
            .pattern("SPS")
            .pattern("SPS")
            .define('P', WitcheryItems.HAWTHORN_PLANKS.get())
            .define('S', Items.STICK)
            .unlockedBy("has_planks", has(WitcheryItems.HAWTHORN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, WitcheryItems.HAWTHORN_PRESSURE_PLATE.get())
            .pattern("PP")
            .define('P', WitcheryItems.HAWTHORN_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.HAWTHORN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, WitcheryItems.HAWTHORN_DOOR.get(), 3)
            .pattern("PP")
            .pattern("PP")
            .pattern("PP")
            .define('P', WitcheryItems.HAWTHORN_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.HAWTHORN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, WitcheryItems.HAWTHORN_TRAPDOOR.get(), 2)
            .pattern("PPP")
            .pattern("PPP")
            .define('P', WitcheryItems.HAWTHORN_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.HAWTHORN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.HAWTHORN_SIGN.get(), 3)
            .pattern("PPP")
            .pattern("PPP")
            .pattern(" S ")
            .define('P', WitcheryItems.HAWTHORN_PLANKS.get())
            .define('S', Items.STICK)
            .unlockedBy("has_planks", has(WitcheryItems.HAWTHORN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.HAWTHORN_HANGING_SIGN.get(), 6)
            .pattern("C C")
            .pattern("LLL")
            .pattern("LLL")
            .define('L', WitcheryItems.STRIPPED_HAWTHORN_LOG.get())
            .define('C', Items.CHAIN)
            .unlockedBy("has_logs", has(WitcheryTags.HAWTHORN_LOG_ITEMS))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, WitcheryItems.HAWTHORN_BOAT.get())
            .pattern("P P")
            .pattern("PPP")
            .define('P', WitcheryItems.HAWTHORN_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.HAWTHORN_PLANKS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.WHITE_IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.WHITE_CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.ORANGE_IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.ORANGE_CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.MAGENTA_IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.MAGENTA_CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.LIGHT_BLUE_IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.LIGHT_BLUE_CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.YELLOW_IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.YELLOW_CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.LIME_IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.LIME_CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.PINK_IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.PINK_CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.GRAY_IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.GRAY_CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.LIGHT_GRAY_IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.LIGHT_GRAY_CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.CYAN_IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.CYAN_CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.PURPLE_IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.PURPLE_CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.BLUE_IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.BLUE_CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.BROWN_IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.BROWN_CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.GREEN_IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.GREEN_CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.RED_IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.RED_CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.BLACK_IRON_CANDELABRA.get())
            .pattern("CCC")
            .pattern("IAI")
            .pattern(" I ")
            .define('C', Items.BLACK_CANDLE)
            .define('I', Items.IRON_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.ATTUNED_STONE.get())
            .pattern("W")
            .pattern("A")
            .pattern("L")
            .define('W', WitcheryItems.WHIFF_OF_MAGIC.get())
            .define('A', Items.AMETHYST_SHARD)
            .define('L', Items.LAVA_BUCKET)
            .unlockedBy("has_amethyst", has(Items.AMETHYST_SHARD))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, WitcheryItems.ARTHANA.get())
            .pattern(" I ")
            .pattern("NEN")
            .pattern(" S ")
            .define('I', Items.GOLD_INGOT)
            .define('N', Items.GOLD_NUGGET)
            .define('E', Items.EMERALD)
            .define('S', Items.BONE)
            .unlockedBy("has_gold", has(Items.GOLD_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WitcheryItems.CHALICE.get())
            .pattern("NAN")
            .pattern("NIN")
            .pattern(" I ")
            .define('I', Items.GOLD_INGOT)
            .define('N', Items.GOLD_NUGGET)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_gold", has(Items.GOLD_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.SPINNING_WHEEL.get())
            .pattern("IIW")
            .pattern("IIH")
            .pattern("PAH")
            .define('H', WitcheryItems.HAWTHORN_LOG.get())
            .define('P', WitcheryItems.HAWTHORN_PLANKS.get())
            .define('I', Items.ITEM_FRAME)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .define('W', ItemTags.WOOL)
            .unlockedBy("has_moss", has(WitcheryItems.SPANISH_MOSS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.POPPET.get())
            .pattern("WSW")
            .pattern("BST")
            .pattern("W W")
            .define('S', WitcheryItems.SPANISH_MOSS.get())
            .define('B', WitcheryItems.BONE_NEEDLE.get())
            .define('W', Items.HAY_BLOCK)
            .define('T', Items.STRING)
            .unlockedBy("has_moss", has(WitcheryItems.SPANISH_MOSS.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.DEATH_PROTECTION_POPPET.get())
            .pattern("DGP")
            .pattern("GCG")
            .pattern(" G ")
            .define('D', WitcheryItems.DROP_OF_LUCK.get())
            .define('P', WitcheryItems.PHANTOM_VAPOR.get())
            .define('C', WitcheryItems.POPPET.get())
            .define('G', Items.GOLD_NUGGET)
            .unlockedBy("has_poppet", has(WitcheryItems.POPPET.get()))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, WitcheryItems.BONE_NEEDLE.get())
            .requires(Items.BONE)
            .requires(Items.FLINT)
            .unlockedBy("has_flint", has(Items.FLINT))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, WitcheryItems.WAYSTONE.get())
            .requires(WitcheryItems.BONE_NEEDLE.get())
            .requires(Items.QUARTZ)
            .unlockedBy("has_flint", has(Items.FLINT))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, WitcheryItems.ROWAN_PLANKS.get(), 4)
            .requires(WitcheryTags.ROWAN_LOG_ITEMS)
            .unlockedBy("has_logs", has(WitcheryTags.ROWAN_LOG_ITEMS))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, WitcheryItems.ROWAN_BUTTON.get())
            .requires(WitcheryItems.ROWAN_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.ROWAN_PLANKS.get()))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, WitcheryItems.ROWAN_CHEST_BOAT.get())
            .requires(WitcheryItems.ROWAN_BOAT.get())
            .requires(Items.CHEST)
            .unlockedBy("has_planks", has(WitcheryItems.ROWAN_PLANKS.get()))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, WitcheryItems.ALDER_PLANKS.get(), 4)
            .requires(WitcheryTags.ALDER_LOG_ITEMS)
            .unlockedBy("has_logs", has(WitcheryTags.ALDER_LOG_ITEMS))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, WitcheryItems.ALDER_BUTTON.get())
            .requires(WitcheryItems.ALDER_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.ALDER_PLANKS.get()))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, WitcheryItems.ALDER_CHEST_BOAT.get())
            .requires(WitcheryItems.ALDER_BOAT.get())
            .requires(Items.CHEST)
            .unlockedBy("has_planks", has(WitcheryItems.ALDER_PLANKS.get()))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, WitcheryItems.HAWTHORN_PLANKS.get(), 4)
            .requires(WitcheryTags.HAWTHORN_LOG_ITEMS)
            .unlockedBy("has_logs", has(WitcheryTags.HAWTHORN_LOG_ITEMS))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, WitcheryItems.HAWTHORN_BUTTON.get())
            .requires(WitcheryItems.HAWTHORN_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.HAWTHORN_PLANKS.get()))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, WitcheryItems.HAWTHORN_CHEST_BOAT.get())
            .requires(WitcheryItems.HAWTHORN_BOAT.get())
            .requires(Items.CHEST)
            .unlockedBy("has_planks", has(WitcheryItems.HAWTHORN_PLANKS.get()))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.WHITE_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.WHITE_DYE)
            .group("dyed_candle")
            .unlockedBy("has_white_dye", has(Items.WHITE_DYE))
            .save(exporter, WitcheryItems.WHITE_IRON_CANDELABRA.id.withSuffix("_dyed"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.ORANGE_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.ORANGE_DYE)
            .group("dyed_candle")
            .unlockedBy("has_orange_dye", has(Items.ORANGE_DYE))
            .save(exporter, WitcheryItems.ORANGE_IRON_CANDELABRA.id.withSuffix("_dyed"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.MAGENTA_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.MAGENTA_DYE)
            .group("dyed_candle")
            .unlockedBy("has_magenta_dye", has(Items.MAGENTA_DYE))
            .save(exporter, WitcheryItems.MAGENTA_IRON_CANDELABRA.id.withSuffix("_dyed"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.LIGHT_BLUE_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.LIGHT_BLUE_DYE)
            .group("dyed_candle")
            .unlockedBy("has_light_blue_dye", has(Items.LIGHT_BLUE_DYE))
            .save(exporter, WitcheryItems.LIGHT_BLUE_IRON_CANDELABRA.id.withSuffix("_dyed"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.YELLOW_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.YELLOW_DYE)
            .group("dyed_candle")
            .unlockedBy("has_yellow_dye", has(Items.YELLOW_DYE))
            .save(exporter, WitcheryItems.YELLOW_IRON_CANDELABRA.id.withSuffix("_dyed"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.LIME_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.LIME_DYE)
            .group("dyed_candle")
            .unlockedBy("has_lime_dye", has(Items.LIME_DYE))
            .save(exporter, WitcheryItems.LIME_IRON_CANDELABRA.id.withSuffix("_dyed"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.PINK_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.PINK_DYE)
            .group("dyed_candle")
            .unlockedBy("has_pink_dye", has(Items.PINK_DYE))
            .save(exporter, WitcheryItems.PINK_IRON_CANDELABRA.id.withSuffix("_dyed"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.GRAY_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.GRAY_DYE)
            .group("dyed_candle")
            .unlockedBy("has_gray_dye", has(Items.GRAY_DYE))
            .save(exporter, WitcheryItems.GRAY_IRON_CANDELABRA.id.withSuffix("_dyed"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.LIGHT_GRAY_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.LIGHT_GRAY_DYE)
            .group("dyed_candle")
            .unlockedBy("has_light_gray_dye", has(Items.LIGHT_GRAY_DYE))
            .save(exporter, WitcheryItems.LIGHT_GRAY_IRON_CANDELABRA.id.withSuffix("_dyed"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.CYAN_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.CYAN_DYE)
            .group("dyed_candle")
            .unlockedBy("has_cyan_dye", has(Items.CYAN_DYE))
            .save(exporter, WitcheryItems.CYAN_IRON_CANDELABRA.id.withSuffix("_dyed"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.PURPLE_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.PURPLE_DYE)
            .group("dyed_candle")
            .unlockedBy("has_purple_dye", has(Items.PURPLE_DYE))
            .save(exporter, WitcheryItems.PURPLE_IRON_CANDELABRA.id.withSuffix("_dyed"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.BLUE_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.BLUE_DYE)
            .group("dyed_candle")
            .unlockedBy("has_blue_dye", has(Items.BLUE_DYE))
            .save(exporter, WitcheryItems.BLUE_IRON_CANDELABRA.id.withSuffix("_dyed"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.BROWN_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.BROWN_DYE)
            .group("dyed_candle")
            .unlockedBy("has_brown_dye", has(Items.BROWN_DYE))
            .save(exporter, WitcheryItems.BROWN_IRON_CANDELABRA.id.withSuffix("_dyed"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.GREEN_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.GREEN_DYE)
            .group("dyed_candle")
            .unlockedBy("has_green_dye", has(Items.GREEN_DYE))
            .save(exporter, WitcheryItems.GREEN_IRON_CANDELABRA.id.withSuffix("_dyed"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.RED_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.RED_DYE)
            .group("dyed_candle")
            .unlockedBy("has_red_dye", has(Items.RED_DYE))
            .save(exporter, WitcheryItems.RED_IRON_CANDELABRA.id.withSuffix("_dyed"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.BLACK_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.BLACK_DYE)
            .group("dyed_candle")
            .unlockedBy("has_black_dye", has(Items.BLACK_DYE))
            .save(exporter, WitcheryItems.BLACK_IRON_CANDELABRA.id.withSuffix("_dyed"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE_MEAL, 6)
            .requires(WitcheryItems.WOOD_ASH.get(), 6)
            .requires(Items.BONE, 1)
            .unlockedBy("has_wood_ash", has(WitcheryItems.WOOD_ASH.get()))
            .save(exporter, "witchery:bone_meal_6")

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE_MEAL, 7)
            .requires(WitcheryItems.WOOD_ASH.get(), 8)
            .requires(Items.BONE, 1)
            .unlockedBy("has_wood_ash", has(WitcheryItems.WOOD_ASH.get()))
            .save(exporter,"witchery:bone_meal_7")

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE_MEAL, 5)
            .requires(WitcheryItems.WOOD_ASH.get(), 4)
            .requires(Items.BONE, 1)
            .unlockedBy("has_wood_ash", has(WitcheryItems.WOOD_ASH.get()))
            .save(exporter,"witchery:bone_meal_5")

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE_MEAL, 4)
            .requires(WitcheryItems.WOOD_ASH.get(), 2)
            .requires(Items.BONE, 1)
            .unlockedBy("has_wood_ash", has(WitcheryItems.WOOD_ASH.get()))
            .save(exporter,"witchery:bone_meal_4")

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(WitcheryItems.CLAY_JAR.get()),
            RecipeCategory.MISC, WitcheryItems.JAR.get(), 0.3f, 200)
            .unlockedBy("has_clay_jar", has(WitcheryItems.CLAY_JAR.get()))
            .save(exporter)


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.DISTILLERY.get())
            .pattern("JIJ")
            .pattern("III")
            .pattern("GAG")
            .define('J', WitcheryItems.JAR.get())
            .define('I', Items.IRON_INGOT)
            .define('G', Items.GOLD_INGOT)
            .define('A', WitcheryItems.ATTUNED_STONE.get())
            .unlockedBy("has_attuned", has(WitcheryItems.ATTUNED_STONE.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.CAULDRON.get())
            .pattern("I I")
            .pattern("III")
            .pattern(" C ")
            .define('I', Items.IRON_INGOT)
            .define('C', Items.CAMPFIRE)
            .unlockedBy("has_iron", has(Items.IRON_INGOT))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.COPPER_CAULDRON.get())
            .pattern("I I")
            .pattern("IBI")
            .pattern(" C ")
            .define('I', Items.COPPER_INGOT)
            .define('B', Items.COPPER_BLOCK)
            .define('C', Items.CAMPFIRE)
            .unlockedBy("has_copper", has(Items.COPPER_INGOT))
            .save(exporter)


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.WITCHES_ROBES.get())
            .pattern("I I")
            .pattern("ISI")
            .pattern("III")
            .define('I', WitcheryItems.IMPREGNATED_FABRIC.get())
            .define('S', WitcheryItems.GOLDEN_THREAD.get())
            .unlockedBy("has_impregnated", has(WitcheryItems.IMPREGNATED_FABRIC.get()))
            .save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.WITCHES_HAT.get())
            .pattern(" I ")
            .pattern("SIS")
            .pattern("IGI")
            .define('I', WitcheryItems.IMPREGNATED_FABRIC.get())
            .define('S', WitcheryItems.GOLDEN_THREAD.get())
            .define('G', Items.GLOWSTONE_DUST)
            .unlockedBy("has_impregnated", has(WitcheryItems.IMPREGNATED_FABRIC.get()))
            .save(exporter)


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.WITCHES_SLIPPERS.get())
            .pattern("S S")
            .pattern("I I")
            .define('I', WitcheryItems.IMPREGNATED_FABRIC.get())
            .define('S', WitcheryItems.GOLDEN_THREAD.get())
            .unlockedBy("has_impregnated", has(WitcheryItems.IMPREGNATED_FABRIC.get()))
            .save(exporter)

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(Items.NETHER_WART.defaultInstance, Color(255,55,50).rgb)
            .addInputWithColor(WitcheryItems.ENT_TWIG.get().defaultInstance, Color(255,100,1).rgb)
            .addInputWithColor(WitcheryItems.MUTANDIS_EXTREMIS.get().defaultInstance, Color(255,1,1).rgb)
            .addOutput(WitcheryItems.MUTATING_SPRING.get())
            .setAltarPower(100)
            .unlockedBy("has_ent_twig", has(WitcheryItems.ENT_TWIG.get()))
            .save(exporter,  Witchery.id("mutating_spring"))

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get().defaultInstance, Color(100,50,50).rgb)
            .addInputWithColor(Items.GOLD_NUGGET.defaultInstance, Color(255,255,50).rgb)
            .addInputWithColor(WitcheryItems.RITUAL_CHALK.get().defaultInstance, Color(255,255,255).rgb)
            .addOutput(WitcheryItems.GOLDEN_CHALK.get())
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter,  Witchery.id("golden_chalk"))

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(Items.NETHER_WART.defaultInstance, Color(255,50,50).rgb)
            .addInputWithColor(WitcheryItems.TEAR_OF_THE_GODDESS.get().defaultInstance, Color(105,50,250).rgb)
            .addInputWithColor(Items.ENDER_PEARL.defaultInstance, Color(50,150,150).rgb)
            .addInputWithColor(WitcheryItems.RITUAL_CHALK.get().defaultInstance, Color(255,255,255).rgb)
            .addOutput(WitcheryItems.OTHERWHERE_CHALK.get())
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter,  Witchery.id("otherwhere_chalk"))

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(Items.NETHER_WART.defaultInstance, Color(255,50,50).rgb)
            .addInputWithColor(Items.BLAZE_POWDER.defaultInstance, Color(205,200,10).rgb)
            .addInputWithColor(WitcheryItems.RITUAL_CHALK.get().defaultInstance,  Color(200,200,200).rgb)
            .addOutput(WitcheryItems.INFERNAL_CHALK.get())
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter,  Witchery.id("infernal_chalk"))

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get().defaultInstance, Color(100,50,50).rgb)
            .addInputWithColor(WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get().defaultInstance, Color(100,150,50).rgb)
            .addInputWithColor(Items.EGG.defaultInstance, Color(150,150,50).rgb)
            .addOutput(WitcheryItems.MUTANDIS.get().defaultInstance, 4)
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.MANDRAKE_ROOT.get()))
            .save(exporter,  Witchery.id("mutandis"))

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(Items.NETHER_WART.defaultInstance, Color(255,50,50).rgb)
            .addInputWithColor(WitcheryItems.MUTANDIS.get().defaultInstance, Color(155,150,50).rgb)
            .addOutput(WitcheryItems.MUTANDIS_EXTREMIS.get())
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter,  Witchery.id("mutandis_extremis"))

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get().defaultInstance, Color(100,50,50).rgb)
            .addInputWithColor(Items.NETHER_WART.defaultInstance, Color(255,50,50).rgb)
            .addInputWithColor(WitcheryItems.TEAR_OF_THE_GODDESS.get().defaultInstance, Color(55,50,250).rgb)
            .addInputWithColor(WitcheryItems.REFINED_EVIL.get().defaultInstance, Color(20,20,20).rgb)
            .addInputWithColor(WitcheryItems.MUTANDIS_EXTREMIS.get().defaultInstance, Color(50,20,20).rgb)
            .addOutput(WitcheryItems.DROP_OF_LUCK.get())
            .setAltarPower(100)
            .unlockedBy("has_refined_evil", has(WitcheryItems.REFINED_EVIL.get()))
            .save(exporter,  Witchery.id("drop_of_luck"))

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get().defaultInstance, Color(100,50,50).rgb)
            .addInputWithColor(WitcheryItems.TEAR_OF_THE_GODDESS.get().defaultInstance, Color(100,150,150).rgb)
            .addInputWithColor(WitcheryItems.PHANTOM_VAPOR.get().defaultInstance, Color(150,150,150).rgb)
            .addInputWithColor(Items.ENDER_PEARL.defaultInstance, Color(50,150,150).rgb)
            .addInputWithColor(Items.WHEAT.defaultInstance, Color(150,150,50).rgb)
            .addInputWithColor(WitcheryItems.MUTANDIS.get().defaultInstance, Color(150,190,50).rgb)
            .addOutput(Items.NETHER_WART.defaultInstance, 1)
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.MANDRAKE_ROOT.get()))
            .save(exporter,  Witchery.id("nether_wart"))

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.OAK_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter,  Witchery.id("exhale_of_the_horned_one"))

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.DARK_OAK_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter,  Witchery.id("exhale_of_the_horned_one2"))

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.BIRCH_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.BREATH_OF_THE_GODDESS.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter,  Witchery.id("breath_of_the_goddess"))

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.CHERRY_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.BREATH_OF_THE_GODDESS.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter,  Witchery.id("breath_of_the_goddess2"))

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.SPRUCE_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.HINT_OF_REBIRTH.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter,  Witchery.id("hint_of_rebirth"))

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.JUNGLE_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.HINT_OF_REBIRTH.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter,  Witchery.id("hint_of_rebirth2"))

        OvenCookingRecipeBuilder(
            Ingredient.of(WitcheryItems.ROWAN_SAPLING.get()),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.WHIFF_OF_MAGIC.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter,  Witchery.id("whiff_of_magic"))

        OvenCookingRecipeBuilder(
            Ingredient.of(WitcheryItems.ALDER_SAPLING.get()),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.REEK_OF_MISFORTUNE.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter,  Witchery.id("reek_of_misfortune"))

        OvenCookingRecipeBuilder(
            Ingredient.of(WitcheryItems.HAWTHORN_SAPLING.get()),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.ODOR_OF_PURITY.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter,  Witchery.id("odor_of_purity"))

        OvenCookingRecipeBuilder(
            Ingredient.of(ItemTags.LOGS),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.FOUL_FUME.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter,  Witchery.id("foul_fume_logs"))


        RitualRecipeBuilder.create()
            .addInputItem(Items.ENDER_PEARL.defaultInstance)
            .addInputItem(WitcheryItems.REFINED_EVIL.get().defaultInstance)
            .addInputItem(WitcheryItems.DEMONS_BLOOD.get().defaultInstance)
            .addInputItem(WitcheryItems.PHANTOM_VAPOR.get().defaultInstance)
            .addOutputEntity(WitcheryEntityTypes.IMP.get())
            .setTicks(20 * 5)
            .setAltarPower(5000)
            .pattern(
                "___OOOOO___",
                "__O_____O__",
                "_O_______O_",
                "O_________O",
                "O_________O",
                "O____G____O",
                "O_________O",
                "O_________O",
                "_O_______O_",
                "__O_____O__",
                "___OOOOO___"
            )
            .define('O', WitcheryBlocks.INFERNAL_CHALK_BLOCK.get())
            .define('G', WitcheryBlocks.GOLDEN_CHALK_BLOCK.get())
            .save(exporter, Witchery.id("summon_imp"))

        RitualRecipeBuilder.create()
            .addInputItem(WitcheryItems.ENDER_DEW.get().defaultInstance)
            .addInputItem(WitcheryItems.WAYSTONE.get().defaultInstance)
            .addInputItem(WitcheryItems.TAGLOCK.get().defaultInstance)
            .addInputItem(Items.IRON_INGOT.defaultInstance)
            .addCommand(CommandType("tp {taglockPlayerOrEntity} {waystonePos}", CommandType.END))
            .setTicks(20 * 2)
            .setAltarPower(3000)
            .pattern(
                "___OOOOO___",
                "__O_____O__",
                "_O_______O_",
                "O_________O",
                "O_________O",
                "O____G____O",
                "O_________O",
                "O_________O",
                "_O_______O_",
                "__O_____O__",
                "___OOOOO___"
            )
            .define('O', WitcheryBlocks.OTHERWHERE_CHALK_BLOCK.get())
            .define('G', WitcheryBlocks.GOLDEN_CHALK_BLOCK.get())
            .save(exporter, Witchery.id("teleport_taglock_to_waystone"))

        RitualRecipeBuilder.create()
            .addInputItem(WitcheryItems.WAYSTONE.get().defaultInstance)
            .addCommand(CommandType("tp {owner} {waystonePos}", CommandType.END))
            .setTicks(20)
            .pattern(
                "__RRR__",
                "_R___R_",
                "R_____R",
                "R__G__R",
                "R_____R",
                "_R___R_",
                "__RRR__"
            )
            .define('R', WitcheryBlocks.OTHERWHERE_CHALK_BLOCK.get())
            .define('G', WitcheryBlocks.GOLDEN_CHALK_BLOCK.get())
            .save(exporter, Witchery.id("teleport_owner_to_waystone"))

        RitualRecipeBuilder.create()
            .addInputItem(Items.COPPER_INGOT.defaultInstance)
            .addInputItem(WitcheryItems.WOOD_ASH.get().defaultInstance)
            .setAltarPower(1000)
            .setTicks(20)
            .pattern(
                "__RRR__",
                "_R___R_",
                "R_____R",
                "R__G__R",
                "R_____R",
                "_R___R_",
                "__RRR__"
            )
            .define('R', WitcheryBlocks.RITUAL_CHALK_BLOCK.get())
            .define('G', WitcheryBlocks.GOLDEN_CHALK_BLOCK.get())
            .addCommand(CommandType("summon lightning_bolt {chalkPos}", CommandType.END))
            .save(exporter, Witchery.id("summon_lightning"))

        RitualRecipeBuilder.create()
            .addInputItem(Items.COPPER_INGOT.defaultInstance)
            .addInputItem(WitcheryItems.WOOD_ASH.get().defaultInstance)
            .addInputItem(WitcheryItems.WAYSTONE.get().defaultInstance)
            .setAltarPower(2000)
            .setTicks(20)
            .pattern(
                "__RRR__",
                "_R___R_",
                "R_____R",
                "R__G__R",
                "R_____R",
                "_R___R_",
                "__RRR__"
            )
            .define('R', WitcheryBlocks.RITUAL_CHALK_BLOCK.get())
            .define('G', WitcheryBlocks.GOLDEN_CHALK_BLOCK.get())
            .addCommand(CommandType("summon lightning_bolt {waystonePos}", CommandType.END))
            .save(exporter, Witchery.id("summon_lightning_on_waystone"))

        RitualRecipeBuilder.create()
            .addInputItem(Items.WOODEN_AXE.defaultInstance)
            .addInputItem(WitcheryItems.WOOD_ASH.get().defaultInstance)
            .setAltarPower(3000)
            .setTicks(20)
            .addCommand(CommandType("time set midnight", CommandType.END))
            .pattern(
                "__RRR__",
                "_R___R_",
                "R_____R",
                "R__G__R",
                "R_____R",
                "_R___R_",
                "__RRR__"
            )
            .define('R', WitcheryBlocks.RITUAL_CHALK_BLOCK.get())
            .define('G', WitcheryBlocks.GOLDEN_CHALK_BLOCK.get())
            .save(exporter, Witchery.id("set_midnight"))

        RitualRecipeBuilder.create()
            .addInputItem(Items.FEATHER.defaultInstance)
            .addInputItem(Items.REDSTONE.defaultInstance)
            .setAltarPower(20)
            .setInfinite(true)
            .pattern(
                "__RRR__",
                "_R___R_",
                "R_____R",
                "R__G__R",
                "R_____R",
                "_R___R_",
                "__RRR__"
            )
            .define('R', WitcheryBlocks.RITUAL_CHALK_BLOCK.get())
            .define('G', WitcheryBlocks.GOLDEN_CHALK_BLOCK.get())
            .setCustomRitual(PushMobsRitual())
            .save(exporter, Witchery.id("push_mobs"))

        val attuned = WitcheryItems.ATTUNED_STONE.get().defaultInstance
        attuned.set(WitcheryDataComponents.ATTUNED.get(), true)

        RitualRecipeBuilder.create()
            .addInputItem(WitcheryItems.ATTUNED_STONE.get().defaultInstance)
            .addInputItem(Items.REDSTONE.defaultInstance)
            .addInputItem(Items.GLOWSTONE_DUST.defaultInstance)
            .addInputItem(WitcheryItems.WOOD_ASH.get().defaultInstance)
            .addOutputItem(attuned)
            .setAltarPower(2500)
            .pattern(
                "__RRR__",
                "_R___R_",
                "R_____R",
                "R__G__R",
                "R_____R",
                "_R___R_",
                "__RRR__"
            )
            .define('R', WitcheryBlocks.RITUAL_CHALK_BLOCK.get())
            .define('G', WitcheryBlocks.GOLDEN_CHALK_BLOCK.get())
            .save(exporter, Witchery.id("charge_attuned"))

        RitualRecipeBuilder.create()
            .addInputItem(WitcheryItems.GHOST_OF_THE_LIGHT.get().defaultInstance)
            .setAltarPower(2000)
            .addCommand(CommandType("witchery infusion setAndKill {owner} light", CommandType.END))
            .pattern(
                "___RRRRR___",
                "__R_____R__",
                "_R__RRR__R_",
                "R__R___R__R",
                "R_R_____R_R",
                "R_R__G__R_R",
                "R_R_____R_R",
                "R__R___R__R",
                "_R__RRR__R_",
                "__R_____R__",
                "___RRRRR___"
            )
            .define('R', WitcheryBlocks.RITUAL_CHALK_BLOCK.get())
            .define('G', WitcheryBlocks.GOLDEN_CHALK_BLOCK.get())
            .save(exporter, Witchery.id("infuse_light"))

        RitualRecipeBuilder.create()
            .addInputItem(WitcheryItems.SPIRIT_OF_OTHERWHERE.get().defaultInstance)
            .setAltarPower(2000)
            .addCommand(CommandType("witchery infusion setAndKill {owner} otherwhere", CommandType.END))
            .pattern(
                "___RRRRR___",
                "__R_____R__",
                "_R__RRR__R_",
                "R__R___R__R",
                "R_R_____R_R",
                "R_R__G__R_R",
                "R_R_____R_R",
                "R__R___R__R",
                "_R__RRR__R_",
                "__R_____R__",
                "___RRRRR___"
            )
            .define('R', WitcheryBlocks.OTHERWHERE_CHALK_BLOCK.get())
            .define('G', WitcheryBlocks.GOLDEN_CHALK_BLOCK.get())
            .save(exporter, Witchery.id("infuse_otherwhere"))

        RitualRecipeBuilder.create()
            .addInputItem(PotionContents.createItemStack(Items.POTION, Potions.STRONG_REGENERATION))
            .setAltarPower(40)
            .addCommand(CommandType("witchery infusion increase {owner} 1", CommandType.TICK))
            .setInfinite(true)
            .pattern(
                "___RRRRR___",
                "__R_____R__",
                "_R__RRR__R_",
                "R__R___R__R",
                "R_R_____R_R",
                "R_R__G__R_R",
                "R_R_____R_R",
                "R__R___R__R",
                "_R__RRR__R_",
                "__R_____R__",
                "___RRRRR___"
            )
            .define('R', WitcheryBlocks.RITUAL_CHALK_BLOCK.get())
            .define('G', WitcheryBlocks.GOLDEN_CHALK_BLOCK.get())
            .save(exporter, Witchery.id("rite_of_charging_infusion"))

        DistilleryCraftingRecipeBuilder.create()
            .addInput(WitcheryItems.FOUL_FUME.get().defaultInstance)
            .addInput(WitcheryItems.WOOD_ASH.get().defaultInstance)
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.OIL_OF_VITRIOL.get().defaultInstance)
            .addOutput(WitcheryItems.GYPSUM.get().defaultInstance)
            .addOutput(Items.SLIME_BALL.defaultInstance)
            .setJarConsumption(1)
            .save(exporter,  Witchery.id("oil_of_vitriol_gypsum"))

        DistilleryCraftingRecipeBuilder.create()
            .addInput(WitcheryItems.BREATH_OF_THE_GODDESS.get().defaultInstance)
            .addInput(Items.LAPIS_LAZULI.defaultInstance)
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.TEAR_OF_THE_GODDESS.get().defaultInstance)
            .addOutput(WitcheryItems.WHIFF_OF_MAGIC.get().defaultInstance)
            .addOutput(Items.SLIME_BALL.defaultInstance)
            .addOutput(WitcheryItems.FOUL_FUME.get().defaultInstance)
            .setJarConsumption(3)
            .save(exporter,  Witchery.id("tear_and_whiff"))

        DistilleryCraftingRecipeBuilder.create()
            .addInput(Items.ENDER_PEARL.defaultInstance)
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.ENDER_DEW.get().defaultInstance, 2)
            .addOutput(WitcheryItems.ENDER_DEW.get().defaultInstance, 2)
            .addOutput(WitcheryItems.ENDER_DEW.get().defaultInstance, 1)
            .addOutput(WitcheryItems.WHIFF_OF_MAGIC.get().defaultInstance)
            .setJarConsumption(6)
            .save(exporter,  Witchery.id("ender_dew"))

        DistilleryCraftingRecipeBuilder.create()
            .addInput(Items.BLAZE_POWDER.defaultInstance)
            .addInput(Items.GUNPOWDER.defaultInstance)
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.REEK_OF_MISFORTUNE.get().defaultInstance)
            .addOutput(Items.GLOWSTONE_DUST.defaultInstance)
            .addOutput(Items.GLOWSTONE_DUST.defaultInstance)
            .setJarConsumption(1)
            .save(exporter,  Witchery.id("reek_of_misfortune_glowstone"))

        DistilleryCraftingRecipeBuilder.create()
            .addInput(Items.PHANTOM_MEMBRANE.defaultInstance)
            .addInput(WitcheryItems.OIL_OF_VITRIOL.get().defaultInstance)
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.PHANTOM_VAPOR.get().defaultInstance, 1)
            .addOutput(WitcheryItems.PHANTOM_VAPOR.get().defaultInstance, 1)
            .addOutput(WitcheryItems.ODOR_OF_PURITY.get().defaultInstance)
            .setJarConsumption(3)
            .save(exporter, Witchery.id("phantom_vapor"))

        DistilleryCraftingRecipeBuilder.create()
            .addInput(WitcheryItems.DEMON_HEART.get().defaultInstance)
            .addInput(WitcheryItems.PHANTOM_VAPOR.get().defaultInstance)
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.DEMONS_BLOOD.get().defaultInstance, 2)
            .addOutput(WitcheryItems.DEMONS_BLOOD.get().defaultInstance, 2)
            .addOutput(WitcheryItems.REFINED_EVIL.get().defaultInstance)
            .setJarConsumption(4)
            .save(exporter, Witchery.id("refined_evil"))

        DistilleryCraftingRecipeBuilder.create()
            .addInput(WitcheryItems.DEMON_HEART.get().defaultInstance)
            .addInput(Items.NETHERRACK.defaultInstance)
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.DEMONS_BLOOD.get().defaultInstance)
            .addOutput(WitcheryItems.DEMONS_BLOOD.get().defaultInstance)
            .addOutput(Items.SOUL_SAND.defaultInstance)
            .setJarConsumption(2)
            .save(exporter,  Witchery.id("demons_blood"))

    }
}
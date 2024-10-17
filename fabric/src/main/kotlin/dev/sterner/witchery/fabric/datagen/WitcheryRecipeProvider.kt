package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.block.ritual.CommandContext
import dev.sterner.witchery.block.ritual.CommandType
import dev.sterner.witchery.block.ritual.RitualHelper
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipeBuilder
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipeBuilder
import dev.sterner.witchery.recipe.cauldron.ItemStackWithColor
import dev.sterner.witchery.recipe.distillery.DistilleryCraftingRecipeBuilder
import dev.sterner.witchery.recipe.oven.OvenCookingRecipeBuilder
import dev.sterner.witchery.recipe.ritual.RitualRecipeBuilder
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryTags
import dev.sterner.witchery.ritual.PushMobsRitual
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder
import net.minecraft.tags.ItemTags
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.world.item.crafting.ShapelessRecipe
import net.minecraft.world.level.block.Blocks
import java.util.concurrent.CompletableFuture
import kotlin.math.exp

class WitcheryRecipeProvider(output: FabricDataOutput, val registriesFuture: CompletableFuture<HolderLookup.Provider>) :
    FabricRecipeProvider(output, registriesFuture) {

    override fun buildRecipes(exporter: RecipeOutput) {

        //TODO remove
        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(Items.APPLE.defaultInstance, 10257594)
            .addInputWithColor(Items.SUGAR.defaultInstance, 16755227)
            .setOutput(Items.HONEY_BOTTLE.defaultInstance)
            .setAltarPower(100)
            .save(exporter)



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
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.ORANGE_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.ORANGE_DYE)
            .group("dyed_candle")
            .unlockedBy("has_orange_dye", has(Items.ORANGE_DYE))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.MAGENTA_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.MAGENTA_DYE)
            .group("dyed_candle")
            .unlockedBy("has_magenta_dye", has(Items.MAGENTA_DYE))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.LIGHT_BLUE_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.LIGHT_BLUE_DYE)
            .group("dyed_candle")
            .unlockedBy("has_light_blue_dye", has(Items.LIGHT_BLUE_DYE))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.YELLOW_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.YELLOW_DYE)
            .group("dyed_candle")
            .unlockedBy("has_yellow_dye", has(Items.YELLOW_DYE))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.LIME_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.LIME_DYE)
            .group("dyed_candle")
            .unlockedBy("has_lime_dye", has(Items.LIME_DYE))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.PINK_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.PINK_DYE)
            .group("dyed_candle")
            .unlockedBy("has_pink_dye", has(Items.PINK_DYE))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.GRAY_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.GRAY_DYE)
            .group("dyed_candle")
            .unlockedBy("has_gray_dye", has(Items.GRAY_DYE))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.LIGHT_GRAY_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.LIGHT_GRAY_DYE)
            .group("dyed_candle")
            .unlockedBy("has_light_gray_dye", has(Items.LIGHT_GRAY_DYE))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.CYAN_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.CYAN_DYE)
            .group("dyed_candle")
            .unlockedBy("has_cyan_dye", has(Items.CYAN_DYE))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.PURPLE_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.PURPLE_DYE)
            .group("dyed_candle")
            .unlockedBy("has_purple_dye", has(Items.PURPLE_DYE))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.BLUE_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.BLUE_DYE)
            .group("dyed_candle")
            .unlockedBy("has_blue_dye", has(Items.BLUE_DYE))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.BROWN_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.BROWN_DYE)
            .group("dyed_candle")
            .unlockedBy("has_brown_dye", has(Items.BROWN_DYE))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.GREEN_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.GREEN_DYE)
            .group("dyed_candle")
            .unlockedBy("has_green_dye", has(Items.GREEN_DYE))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.RED_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.RED_DYE)
            .group("dyed_candle")
            .unlockedBy("has_red_dye", has(Items.RED_DYE))
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WitcheryItems.BLACK_IRON_CANDELABRA.get())
            .requires(WitcheryItems.IRON_CANDELABRA.get())
            .requires(Items.BLACK_DYE)
            .group("dyed_candle")
            .unlockedBy("has_black_dye", has(Items.BLACK_DYE))
            .save(exporter)



        SimpleCookingRecipeBuilder.smelting(Ingredient.of(WitcheryItems.CLAY_JAR.get()),
            RecipeCategory.MISC, WitcheryItems.JAR.get(), 0.3f, 200)
            .unlockedBy("has_clay_jar", has(WitcheryItems.CLAY_JAR.get()))
            .save(exporter)



        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get().defaultInstance, 123456)
            .addInputWithColor(Items.GOLD_NUGGET.defaultInstance, 654321)
            .addInputWithColor(WitcheryItems.RITUAL_CHALK.get().defaultInstance, 321654)
            .addOutput(WitcheryItems.GOLDEN_CHALK.get())
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter)

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(Items.NETHER_WART.defaultInstance, 123456)
            .addInputWithColor(WitcheryItems.TEAR_OF_THE_GODDESS.get().defaultInstance, 654321)
            .addInputWithColor(Items.ENDER_PEARL.defaultInstance, 321654)
            .addInputWithColor(WitcheryItems.RITUAL_CHALK.get().defaultInstance, 654321)
            .addOutput(WitcheryItems.OTHERWHERE_CHALK.get())
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter)

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(Items.NETHER_WART.defaultInstance, 123456)
            .addInputWithColor(Items.BLAZE_POWDER.defaultInstance, 654321)
            .addInputWithColor(WitcheryItems.RITUAL_CHALK.get().defaultInstance, 654321)
            .addOutput(WitcheryItems.INFERNAL_CHALK.get())
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter)

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get().defaultInstance, 123456)
            .addInputWithColor(WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get().defaultInstance, 654321)
            .addInputWithColor(Items.EGG.defaultInstance, 654321)
            .addOutput(WitcheryItems.MUTANDIS.get().defaultInstance, 4)
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.MANDRAKE_ROOT.get()))
            .save(exporter)

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(Items.NETHER_WART.defaultInstance, 123456)
            .addInputWithColor(WitcheryItems.MUTANDIS.get().defaultInstance, 654321)
            .addOutput(WitcheryItems.MUTANDIS_EXTREMIS.get())
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.MANDRAKE_ROOT.get()))
            .save(exporter)



        OvenCookingRecipeBuilder(
            Ingredient.of(Items.OAK_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter)

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.DARK_OAK_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter)

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.BIRCH_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.BREATH_OF_THE_GODDESS.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter)

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.CHERRY_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.BREATH_OF_THE_GODDESS.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter)

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.SPRUCE_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.HINT_OF_REBIRTH.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter)

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.JUNGLE_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.HINT_OF_REBIRTH.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter)

        OvenCookingRecipeBuilder(
            Ingredient.of(ItemTags.LOGS),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.FOUL_FUME.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter)



        //TODO remove
        RitualRecipeBuilder.create()
            .addInputItem(Items.STICK.defaultInstance)
            .addInputItem(WitcheryItems.WAYSTONE.get().defaultInstance)
            .addInputEntity(EntityType.SHEEP)
            .addOutputItem(Items.DIAMOND.defaultInstance)
            .addCommand(CommandType("kill {entity}", CommandType.END, CommandContext.ENTITY))
            .setTicks(20 * 5)
            .pattern(
                "____IIIIIII____",
                "___I_______I___",
                "__I__OOOOO__I__",
                "_I__O_____O__I_",
                "I__O__RRR__O__I",
                "I_O__R___R__O_I",
                "I_O_R_____R_O_I",
                "I_O_R__G__R_O_I",
                "I_O_R_____R_O_I",
                "I_O__R___R__O_I",
                "I__O__RRR__O__I",
                "_I__O_____O__I_",
                "__I__OOOOO__I__",
                "___I_______I___",
                "____IIIIIII____"
            )
            .define('R', Blocks.SHROOMLIGHT)
            .define('I', WitcheryBlocks.INFERNAL_CHALK_BLOCK.get())
            .define('O', WitcheryBlocks.OTHERWHERE_CHALK_BLOCK.get())
            .define('G', WitcheryBlocks.GOLDEN_CHALK_BLOCK.get())
            .save(exporter)

        RitualRecipeBuilder.create()
            .addInputItem(WitcheryItems.WAYSTONE.get().defaultInstance)
            .addCommand(CommandType("tp {owner} {blockPos}", CommandType.END, CommandContext.BLOCKPOS))
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
            .save(exporter)

        RitualRecipeBuilder.create()
            .addInputItem(Items.WOODEN_SWORD.defaultInstance)
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
            .addCommand(CommandType("summon lightning_bolt {blockPos}", CommandType.END, CommandContext.NOTHING))
            .save(exporter)

        RitualRecipeBuilder.create()
            .addInputItem(Items.WOODEN_SWORD.defaultInstance)
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
            .addCommand(CommandType("summon lightning_bolt {blockPos}", CommandType.END, CommandContext.BLOCKPOS))
            .save(exporter)

        RitualRecipeBuilder.create()
            .addInputItem(Items.WOODEN_AXE.defaultInstance)
            .addInputItem(WitcheryItems.WOOD_ASH.get().defaultInstance)
            .setAltarPower(3000)
            .setTicks(20)
            .addCommand(CommandType("time set midnight", CommandType.END, CommandContext.NOTHING))
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
            .save(exporter)

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
            .save(exporter)

        DistilleryCraftingRecipeBuilder.create()
            .addInput(Items.NETHERITE_INGOT.defaultInstance)
            .addInput(Items.APPLE.defaultInstance)
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.OIL_OF_VITRIOL.get().defaultInstance)
            .addOutput(WitcheryItems.BREATH_OF_THE_GODDESS.get().defaultInstance)
            .setJarConsumption(2)
            .save(exporter)
    }
}
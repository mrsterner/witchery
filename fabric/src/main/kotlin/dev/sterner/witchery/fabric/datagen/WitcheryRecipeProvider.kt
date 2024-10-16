package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.block.ritual.CommandContext
import dev.sterner.witchery.block.ritual.CommandType
import dev.sterner.witchery.block.ritual.RitualHelper
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipeBuilder
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipeBuilder
import dev.sterner.witchery.recipe.cauldron.ItemStackWithColor
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
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
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
            .requires(Ingredient.of(
                WitcheryItems.ROWAN_LOG.get(),
                WitcheryItems.ROWAN_WOOD.get(),
                WitcheryItems.STRIPPED_ROWAN_LOG.get(),
                WitcheryItems.STRIPPED_ROWAN_WOOD.get()
            ))
            .unlockedBy("has_logs", has(WitcheryItems.ROWAN_LOG.get())) // Should be an ITEM tag tbh
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, WitcheryItems.ROWAN_BUTTON.get())
            .requires(WitcheryItems.ROWAN_PLANKS.get())
            .unlockedBy("has_planks", has(WitcheryItems.ROWAN_PLANKS.get()))
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



        //TODO remove
        RitualRecipeBuilder.create()
            .addInputItem(Items.STICK.defaultInstance)
            .addInputItem(WitcheryItems.WAYSTONE.get().defaultInstance)
            .addInputEntity(EntityType.SHEEP)
            .addOutputItem(Items.DIAMOND.defaultInstance)
            .addCommand(CommandType("kill {entity}", CommandType.END, CommandContext.ENTITY))
            .setTicks(20 * 5)
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
    }
}
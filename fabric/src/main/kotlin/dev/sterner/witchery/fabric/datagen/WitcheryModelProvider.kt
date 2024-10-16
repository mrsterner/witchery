package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.block.WitcheryCropBlock
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryItems
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.BlockFamily
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.blockstates.MultiVariantGenerator
import net.minecraft.data.models.blockstates.PropertyDispatch
import net.minecraft.data.models.blockstates.Variant
import net.minecraft.data.models.blockstates.VariantProperties
import net.minecraft.data.models.model.ModelTemplates
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.models.model.TexturedModel
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.Property

class WitcheryModelProvider(output: FabricDataOutput?) : FabricModelProvider(output) {

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        generator.createCrossBlockWithDefaultItem(WitcheryBlocks.GLINTWEED.get(), BlockModelGenerators.TintState.NOT_TINTED)
        createCropPlantBlock(generator, WitcheryBlocks.EMBER_MOSS.get())
        generator.createMultiface(WitcheryBlocks.SPANISH_MOSS.get())
        generator.createCropBlock(WitcheryBlocks.MANDRAKE_CROP.get(), WitcheryCropBlock.AGE, 0,1,2,3,4)
        generator.createCropBlock(WitcheryBlocks.BELLADONNAE_CROP.get(), WitcheryCropBlock.AGE, 0,1,2,3,4)
        createCrossCropBlock(generator, WitcheryBlocks.SNOWBELL_CROP.get(), WitcheryCropBlock.AGE, 0,1,2,3,4)
        createCrossCropBlock(generator, WitcheryBlocks.WATER_ARTICHOKE_CROP.get(), WitcheryCropBlock.AGE, 0,1,2,3,4)
        createCrossCropBlock(generator, WitcheryBlocks.WOLFSFBANE_CROP.get(), WitcheryCropBlock.AGE, 0,1,2,3,4)
        generator.createCropBlock(WitcheryBlocks.GARLIC_CROP.get(), WitcheryCropBlock.AGE, 0,1,2,3,4)

        generator.woodProvider(WitcheryBlocks.ROWAN_LOG.get())
            .logWithHorizontal(WitcheryBlocks.ROWAN_LOG.get()).wood(WitcheryBlocks.ROWAN_WOOD.get())

        generator.woodProvider(WitcheryBlocks.STRIPPED_ROWAN_LOG.get())
            .logWithHorizontal(WitcheryBlocks.STRIPPED_ROWAN_LOG.get()).wood(WitcheryBlocks.STRIPPED_ROWAN_WOOD.get())

        generator.createTrivialBlock(WitcheryBlocks.ROWAN_LEAVES.get(), TexturedModel.LEAVES)
        generator.createTrivialBlock(WitcheryBlocks.ROWAN_BERRY_LEAVES.get(), TexturedModel.LEAVES)

        generator.createCrossBlock(WitcheryBlocks.ROWAN_SAPLING.get(), BlockModelGenerators.TintState.NOT_TINTED)
        generator.skipAutoItemBlock(WitcheryBlocks.ROWAN_SAPLING.get())
        generator.createSimpleFlatItemModel(WitcheryBlocks.ROWAN_SAPLING.get())
        generator.createNonTemplateModelBlock(WitcheryBlocks.POTTED_ROWAN_SAPLING.get())
        generator.createHangingSign(WitcheryBlocks.STRIPPED_ROWAN_LOG.get(), WitcheryBlocks.ROWAN_HANGING_SIGN.get(), WitcheryBlocks.ROWAN_WALL_HANGING_SIGN.get())

        val rowanFamily = BlockFamily.Builder(WitcheryBlocks.ROWAN_PLANKS.get())
            .stairs(WitcheryBlocks.ROWAN_STAIRS.get())
            .slab(WitcheryBlocks.ROWAN_SLAB.get())
            .fence(WitcheryBlocks.ROWAN_FENCE.get())
            .fenceGate(WitcheryBlocks.ROWAN_FENCE_GATE.get())
            .door(WitcheryBlocks.ROWAN_DOOR.get())
            .trapdoor(WitcheryBlocks.ROWAN_TRAPDOOR.get())
            .pressurePlate(WitcheryBlocks.ROWAN_PRESSURE_PLATE.get())
            .button(WitcheryBlocks.ROWAN_BUTTON.get())
            .sign(WitcheryBlocks.ROWAN_SIGN.get(), WitcheryBlocks.ROWAN_WALL_SIGN.get())
            .recipeGroupPrefix("wooden")
            .recipeUnlockedBy("has_planks").family
        generator.family(rowanFamily.baseBlock).generateFor(rowanFamily)
    }


    fun createCropPlantBlock(
        generator: BlockModelGenerators,
        crossBlock: Block?
    ) {
        generator.createSimpleFlatItemModel(crossBlock)
        val textureMapping = TextureMapping.crop(TextureMapping.getBlockTexture(crossBlock))
        val resourceLocation = ModelTemplates.CROP.create(crossBlock, textureMapping, generator.modelOutput)
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(crossBlock, resourceLocation))
    }

    fun createCrossCropBlock(generator: BlockModelGenerators, cropBlock: Block, ageProperty: Property<Int>, vararg ageToVisualStageMapping: Int) {
        require(ageProperty.getPossibleValues().size == ageToVisualStageMapping.size)
        val int2ObjectMap: Int2ObjectMap<ResourceLocation> = Int2ObjectOpenHashMap()
        val propertyDispatch = PropertyDispatch.property(ageProperty)
            .generate { integer: Int ->
                val i = ageToVisualStageMapping[integer]
                val resourceLocation = int2ObjectMap.computeIfAbsent(
                    i,
                    Int2ObjectFunction { j: Int ->
                        generator.createSuffixedVariant(
                            cropBlock,
                            "_stage$i",
                            ModelTemplates.CROSS
                        ) { cropTextureLocation: ResourceLocation ->
                            TextureMapping.cross(
                                cropTextureLocation
                            )
                        }
                    } as Int2ObjectFunction<out ResourceLocation>
                )
                Variant.variant().with(
                    VariantProperties.MODEL,
                    resourceLocation
                )
            }
        generator.createSimpleFlatItemModel(cropBlock.asItem())
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(cropBlock).with(propertyDispatch))
    }

    override fun generateItemModels(genetaror: ItemModelGenerators) {
        genetaror.generateFlatItem(WitcheryItems.GUIDEBOOK.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.MUTANDIS.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.MUTANDIS_EXTREMIS.get(), ModelTemplates.FLAT_ITEM)

        genetaror.generateFlatItem(WitcheryItems.MANDRAKE_ROOT.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.ICY_NEEDLE.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WOOD_ASH.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.GYPSUM.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WOLFSBANE.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WORMWOOD.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WORMWOOD_SEEDS.get(), ModelTemplates.FLAT_ITEM)

        genetaror.generateFlatItem(WitcheryItems.CLAY_JAR.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.JAR.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.BREATH_OF_THE_GODDESS.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WHIFF_OF_MAGIC.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.FOUL_FUME.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.TEAR_OF_THE_GODDESS.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.OIL_OF_VITRIOL.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.HINT_OF_REBIRTH.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.REEK_OF_MISFORTUNE.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.ODOR_OF_PURITY.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.DROP_OF_LUCK.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.ENDER_DEW.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.DEMONS_BLOOD.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.CONDENSED_FEAR.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.FOCUSED_WILL.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.MELLIFLUOUS_HUNGER.get(), ModelTemplates.FLAT_ITEM)

        genetaror.generateFlatItem(WitcheryItems.RITUAL_CHALK.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.INFERNAL_CHALK.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.OTHERWHERE_CHALK.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.GOLDEN_CHALK.get(), ModelTemplates.FLAT_ITEM)

        genetaror.generateFlatItem(WitcheryItems.BELLADONNA_FLOWER.get(),  ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WATER_ARTICHOKE_GLOBE.get(),  ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.BONE_NEEDLE.get(),  ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.DEMON_HEART.get(),  ModelTemplates.FLAT_ITEM)

        genetaror.generateFlatItem(WitcheryItems.ROWAN_BOAT.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.ROWAN_CHEST_BOAT.get(), ModelTemplates.FLAT_ITEM)
    }
}
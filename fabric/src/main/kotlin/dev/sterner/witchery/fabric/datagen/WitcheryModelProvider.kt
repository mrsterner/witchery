package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.block.WitcheryCropBlock
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryItems
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.blockstates.MultiVariantGenerator
import net.minecraft.data.models.blockstates.PropertyDispatch
import net.minecraft.data.models.blockstates.Variant
import net.minecraft.data.models.blockstates.VariantProperties
import net.minecraft.data.models.model.ModelTemplates
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.Property

class WitcheryModelProvider(output: FabricDataOutput?) : FabricModelProvider(output) {

    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        generator.createCrossBlockWithDefaultItem(WitcheryBlocks.GLINTWEED.get(), BlockModelGenerators.TintState.NOT_TINTED)
        createCrossBlock(generator, WitcheryBlocks.EMBER_MOSS.get())
        generator.createMultiface(WitcheryBlocks.SPANISH_MOSS.get())
        generator.createCropBlock(WitcheryBlocks.MANDRAKE_CROP.get(), WitcheryCropBlock.AGE, 0,1,2,3,4)
        generator.createCropBlock(WitcheryBlocks.BELLADONNAE_CROP.get(), WitcheryCropBlock.AGE, 0,1,2,3,4)
        createCrossCropBlock(generator, WitcheryBlocks.SNOWBELL_CROP.get(), WitcheryCropBlock.AGE, 0,1,2,3,4)
        generator.createCropBlock(WitcheryBlocks.WATER_ARTICHOKE_CROP.get(), WitcheryCropBlock.AGE, 0,1,2,3,4)
        generator.createCropBlock(WitcheryBlocks.WOLFSFBANE_CROP.get(), WitcheryCropBlock.AGE, 0,1,2,3,4)
        generator.createCropBlock(WitcheryBlocks.GARLIC_CROP.get(), WitcheryCropBlock.AGE, 0,1,2,3,4)

    }

    private fun createCrossBlock(
        generator: BlockModelGenerators,
        crossBlock: Block
    ) {
        val resourceLocation = generator.createSuffixedVariant(crossBlock, "", ModelTemplates.CROP, TextureMapping::crop)
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

        genetaror.generateFlatItem(WitcheryItems.RITUAL_CHALK.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.INFERNAL_CHALK.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.OTHERWHERE_CHALK.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.GOLDEN_CHALK.get(), ModelTemplates.FLAT_ITEM)

        genetaror.generateFlatItem(WitcheryItems.BELLADONNA_FLOWER.get(),  ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WATER_ARTICHOKE_GLOBE.get(),  ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.BONE_NEEDLE.get(),  ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.DEMON_HEART.get(),  ModelTemplates.FLAT_ITEM)
    }
}
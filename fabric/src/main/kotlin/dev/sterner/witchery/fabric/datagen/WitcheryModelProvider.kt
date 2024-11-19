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
        generator.createCrossBlockWithDefaultItem(
            WitcheryBlocks.GLINTWEED.get(),
            BlockModelGenerators.TintState.NOT_TINTED
        )
        createCropPlantBlock(generator, WitcheryBlocks.EMBER_MOSS.get())
        generator.createMultiface(WitcheryBlocks.SPANISH_MOSS.get())
        generator.createCropBlock(WitcheryBlocks.MANDRAKE_CROP.get(), WitcheryCropBlock.AGE, 0, 1, 2, 3, 4)
        generator.createCropBlock(WitcheryBlocks.BELLADONNA_CROP.get(), WitcheryCropBlock.AGE, 0, 1, 2, 3, 4)
        createCrossCropBlock(generator, WitcheryBlocks.SNOWBELL_CROP.get(), WitcheryCropBlock.AGE, 0, 1, 2, 3, 4)
        createCrossCropBlock(generator, WitcheryBlocks.WATER_ARTICHOKE_CROP.get(), WitcheryCropBlock.AGE, 0, 1, 2, 3, 4)
        createCrossCropBlock(generator, WitcheryBlocks.WOLFSFBANE_CROP.get(), WitcheryCropBlock.AGE, 0, 1, 2, 3, 4)
        generator.createCropBlock(WitcheryBlocks.GARLIC_CROP.get(), WitcheryCropBlock.AGE, 0, 1, 2, 3, 4)

        generator.createCrossBlock(WitcheryBlocks.WISPY_COTTON.get(), BlockModelGenerators.TintState.NOT_TINTED)
        generator.skipAutoItemBlock(WitcheryBlocks.WISPY_COTTON.get())
        generator.createSimpleFlatItemModel(WitcheryBlocks.WISPY_COTTON.get())

        generator.createCrossBlock(WitcheryBlocks.DISTURBED_COTTON.get(), BlockModelGenerators.TintState.NOT_TINTED)
        generator.skipAutoItemBlock(WitcheryBlocks.DISTURBED_COTTON.get())
        generator.createSimpleFlatItemModel(WitcheryBlocks.DISTURBED_COTTON.get())

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
        generator.createHangingSign(
            WitcheryBlocks.STRIPPED_ROWAN_LOG.get(),
            WitcheryBlocks.ROWAN_HANGING_SIGN.get(),
            WitcheryBlocks.ROWAN_WALL_HANGING_SIGN.get()
        )

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



        generator.woodProvider(WitcheryBlocks.ALDER_LOG.get())
            .logWithHorizontal(WitcheryBlocks.ALDER_LOG.get()).wood(WitcheryBlocks.ALDER_WOOD.get())

        generator.woodProvider(WitcheryBlocks.STRIPPED_ALDER_LOG.get())
            .logWithHorizontal(WitcheryBlocks.STRIPPED_ALDER_LOG.get()).wood(WitcheryBlocks.STRIPPED_ALDER_WOOD.get())

        generator.createTrivialBlock(WitcheryBlocks.ALDER_LEAVES.get(), TexturedModel.LEAVES)
        generator.createTrivialCube(WitcheryBlocks.BLOOD_STAINED_WOOL.get())

        generator.createCrossBlock(WitcheryBlocks.ALDER_SAPLING.get(), BlockModelGenerators.TintState.NOT_TINTED)
        generator.skipAutoItemBlock(WitcheryBlocks.ALDER_SAPLING.get())
        generator.createSimpleFlatItemModel(WitcheryBlocks.ALDER_SAPLING.get())
        generator.createNonTemplateModelBlock(WitcheryBlocks.POTTED_ALDER_SAPLING.get())
        generator.createHangingSign(
            WitcheryBlocks.STRIPPED_ALDER_LOG.get(),
            WitcheryBlocks.ALDER_HANGING_SIGN.get(),
            WitcheryBlocks.ALDER_WALL_HANGING_SIGN.get()
        )

        val alderFamily = BlockFamily.Builder(WitcheryBlocks.ALDER_PLANKS.get())
            .stairs(WitcheryBlocks.ALDER_STAIRS.get())
            .slab(WitcheryBlocks.ALDER_SLAB.get())
            .fence(WitcheryBlocks.ALDER_FENCE.get())
            .fenceGate(WitcheryBlocks.ALDER_FENCE_GATE.get())
            .door(WitcheryBlocks.ALDER_DOOR.get())
            .trapdoor(WitcheryBlocks.ALDER_TRAPDOOR.get())
            .pressurePlate(WitcheryBlocks.ALDER_PRESSURE_PLATE.get())
            .button(WitcheryBlocks.ALDER_BUTTON.get())
            .sign(WitcheryBlocks.ALDER_SIGN.get(), WitcheryBlocks.ALDER_WALL_SIGN.get())
            .recipeGroupPrefix("wooden")
            .recipeUnlockedBy("has_planks").family
        generator.family(alderFamily.baseBlock).generateFor(alderFamily)



        generator.woodProvider(WitcheryBlocks.HAWTHORN_LOG.get())
            .logWithHorizontal(WitcheryBlocks.HAWTHORN_LOG.get()).wood(WitcheryBlocks.HAWTHORN_WOOD.get())

        generator.woodProvider(WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get())
            .logWithHorizontal(WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get())
            .wood(WitcheryBlocks.STRIPPED_HAWTHORN_WOOD.get())

        generator.createTrivialBlock(WitcheryBlocks.HAWTHORN_LEAVES.get(), TexturedModel.LEAVES)

        generator.createCrossBlock(WitcheryBlocks.HAWTHORN_SAPLING.get(), BlockModelGenerators.TintState.NOT_TINTED)
        generator.skipAutoItemBlock(WitcheryBlocks.HAWTHORN_SAPLING.get())
        generator.createSimpleFlatItemModel(WitcheryBlocks.HAWTHORN_SAPLING.get())
        generator.createNonTemplateModelBlock(WitcheryBlocks.POTTED_HAWTHORN_SAPLING.get())
        generator.createHangingSign(
            WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get(),
            WitcheryBlocks.HAWTHORN_HANGING_SIGN.get(),
            WitcheryBlocks.HAWTHORN_WALL_HANGING_SIGN.get()
        )

        val hawthornFamily = BlockFamily.Builder(WitcheryBlocks.HAWTHORN_PLANKS.get())
            .stairs(WitcheryBlocks.HAWTHORN_STAIRS.get())
            .slab(WitcheryBlocks.HAWTHORN_SLAB.get())
            .fence(WitcheryBlocks.HAWTHORN_FENCE.get())
            .fenceGate(WitcheryBlocks.HAWTHORN_FENCE_GATE.get())
            .door(WitcheryBlocks.HAWTHORN_DOOR.get())
            .trapdoor(WitcheryBlocks.HAWTHORN_TRAPDOOR.get())
            .pressurePlate(WitcheryBlocks.HAWTHORN_PRESSURE_PLATE.get())
            .button(WitcheryBlocks.HAWTHORN_BUTTON.get())
            .sign(WitcheryBlocks.HAWTHORN_SIGN.get(), WitcheryBlocks.HAWTHORN_WALL_SIGN.get())
            .recipeGroupPrefix("wooden")
            .recipeUnlockedBy("has_planks").family
        generator.family(hawthornFamily.baseBlock).generateFor(hawthornFamily)
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

    fun createCrossCropBlock(
        generator: BlockModelGenerators,
        cropBlock: Block,
        ageProperty: Property<Int>,
        vararg ageToVisualStageMapping: Int
    ) {
        require(ageProperty.possibleValues.size == ageToVisualStageMapping.size)
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

        genetaror.generateFlatItem(WitcheryItems.TORN_PAGE.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.MANDRAKE_ROOT.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.ICY_NEEDLE.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WOOD_ASH.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.GYPSUM.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WOLFSBANE.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WORMWOOD.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WORMWOOD_SEEDS.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.REFINED_EVIL.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.TONGUE_OF_DOG.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WOOL_OF_BAT.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.TOE_OF_FROG.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.OWLETS_WING.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.REDSTONE_SOUP.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.GHOST_OF_THE_LIGHT.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.INFERNAL_ANIMUS.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.SPIRIT_OF_OTHERWHERE.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.SOUL_OF_THE_WORLD.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.FLYING_OINTMENT.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.ATTUNED_STONE.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.ENT_TWIG.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.NECROMANTIC_STONE.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.SPECTRAL_DUST.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.ROWAN_BERRIES.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.GOLDEN_THREAD.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WITCHES_ROBES.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WITCHES_SLIPPERS.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WITCHES_HAT.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.HUNTER_HELMET.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.HUNTER_CHESTPLATE.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.HUNTER_LEGGINGS.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WOVEN_CRUOR.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.TOP_HAT.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.DRESS_COAT.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.TROUSERS.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.OXFORD_BOOTS.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.HUNTER_BOOTS.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.POPPET.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.ARMOR_PROTECTION_POPPET.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.HUNGER_PROTECTION_POPPET.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.DEATH_PROTECTION_POPPET.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.VAMPIRIC_POPPET.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.VOODOO_POPPET.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.VOODOO_PROTECTION_POPPET.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.BABA_YAGAS_HAT.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.IMPREGNATED_FABRIC.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.MUTATING_SPRING.get(), ModelTemplates.FLAT_HANDHELD_ITEM)

        genetaror.generateFlatItem(WitcheryItems.WOODEN_OAK_STAKE.get(), ModelTemplates.FLAT_HANDHELD_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WOODEN_HAWTHORN_STAKE.get(), ModelTemplates.FLAT_HANDHELD_ITEM)

        genetaror.generateFlatItem(WitcheryItems.TORMENTED_TWINE.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.FANCIFUL_THREAD.get(), ModelTemplates.FLAT_ITEM)
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
        genetaror.generateFlatItem(WitcheryItems.PHANTOM_VAPOR.get(), ModelTemplates.FLAT_ITEM)

        genetaror.generateFlatItem(WitcheryItems.BREW_OF_LOVE.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.BREW_OF_INK.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.BREW_OF_REVEALING.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.BREW_OF_EROSION.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.BREW_OF_WEBS.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.BREW_OF_WASTING.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.BREW_OF_THE_DEPTHS.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.BREW_OF_FROST.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.BREW_OF_RAISING.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.BREW_OF_SLEEPING.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.FLOWING_SPIRIT_BUCKET.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.BREW_FLOWING_SPIRIT.get(), ModelTemplates.FLAT_ITEM)

        genetaror.generateFlatItem(WitcheryItems.RITUAL_CHALK.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.INFERNAL_CHALK.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.OTHERWHERE_CHALK.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.GOLDEN_CHALK.get(), ModelTemplates.FLAT_ITEM)

        genetaror.generateFlatItem(WitcheryItems.BELLADONNA_FLOWER.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.WATER_ARTICHOKE_GLOBE.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.BONE_NEEDLE.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.DEMON_HEART.get(), ModelTemplates.FLAT_ITEM)

        genetaror.generateFlatItem(WitcheryItems.ROWAN_BOAT.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.ROWAN_CHEST_BOAT.get(), ModelTemplates.FLAT_ITEM)

        genetaror.generateFlatItem(WitcheryItems.ALDER_BOAT.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.ALDER_CHEST_BOAT.get(), ModelTemplates.FLAT_ITEM)

        genetaror.generateFlatItem(WitcheryItems.HAWTHORN_BOAT.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.HAWTHORN_CHEST_BOAT.get(), ModelTemplates.FLAT_ITEM)

        genetaror.generateFlatItem(WitcheryItems.ARTHANA.get(), ModelTemplates.FLAT_HANDHELD_ITEM)
    }
}
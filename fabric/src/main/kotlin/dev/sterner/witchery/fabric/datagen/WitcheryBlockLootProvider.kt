package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.block.MandrakeCropBlock
import dev.sterner.witchery.block.WitcheryCropBlock
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Item
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import java.util.concurrent.CompletableFuture

class WitcheryBlockLootProvider(
    dataOutput: FabricDataOutput,
    registryLookup: CompletableFuture<HolderLookup.Provider>
) : FabricBlockLootTableProvider(dataOutput, registryLookup) {

    override fun generate() {
        dropSelf(WitcheryBlocks.CAULDRON.get())
        dropSelf(WitcheryBlocks.COPPER_CAULDRON.get())
        dropSelf(WitcheryBlocks.WAXED_COPPER_CAULDRON.get())
        dropSelf(WitcheryBlocks.EXPOSED_COPPER_CAULDRON.get())
        dropSelf(WitcheryBlocks.WAXED_EXPOSED_COPPER_CAULDRON.get())
        dropSelf(WitcheryBlocks.WEATHERED_COPPER_CAULDRON.get())
        dropSelf(WitcheryBlocks.WAXED_WEATHERED_COPPER_CAULDRON.get())
        dropSelf(WitcheryBlocks.OXIDIZED_COPPER_CAULDRON.get())
        dropSelf(WitcheryBlocks.WAXED_OXIDIZED_COPPER_CAULDRON.get())
        dropSelf(WitcheryBlocks.SPINNING_WHEEL.get())
        dropSelf(WitcheryBlocks.BRAZIER.get())
        dropSelf(WitcheryBlocks.IRON_WITCHES_OVEN.get())
        dropSelf(WitcheryBlocks.COPPER_WITCHES_OVEN.get())
        dropSelf(WitcheryBlocks.COPPER_WITCHES_OVEN_FUME_EXTENSION.get())
        dropSelf(WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get())
        dropSelf(WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get())
        dropSelf(WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get())
        dropSelf(WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get())
        dropSelf(WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get())
        dropSelf(WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get())
        dropSelf(WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get())
        dropSelf(WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION.get())
        dropSelf(WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN.get())
        dropSelf(WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN.get())
        dropSelf(WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN.get())
        dropSelf(WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN.get())
        dropSelf(WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN.get())
        dropSelf(WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN.get())
        dropSelf(WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN.get())
        dropSelf(WitcheryBlocks.DISTILLERY.get())
        dropSelf(WitcheryBlocks.DEMON_HEART.get())
        dropSelf(WitcheryBlocks.GRAVESTONE.get())
        dropSelf(WitcheryBlocks.SUNLIGHT_COLLECTOR.get())
        dropSelf(WitcheryBlocks.BLOOD_STAINED_WOOL.get())
        dropSelf(WitcheryBlocks.GRASSPER.get())

        dropOther(WitcheryBlocks.SACRIFICIAL_CIRCLE.get(), Blocks.SKELETON_SKULL)

        dropSelf(WitcheryBlocks.IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.WHITE_IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.ORANGE_IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.MAGENTA_IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.LIGHT_BLUE_IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.YELLOW_IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.LIME_IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.PINK_IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.GRAY_IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.LIGHT_GRAY_IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.CYAN_IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.PURPLE_IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.BLUE_IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.BROWN_IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.GREEN_IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.RED_IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.BLACK_IRON_CANDELABRA.get())
        dropSelf(WitcheryBlocks.CHALICE.get())
        dropSelf(WitcheryBlocks.PENTACLE.get())
        dropSelf(WitcheryBlocks.BLOOD_POPPY.get())
        dropSelf(WitcheryBlocks.DREAM_WEAVER.get())
        dropSelf(WitcheryBlocks.DREAM_WEAVER_OF_FLEET_FOOT.get())
        dropSelf(WitcheryBlocks.DREAM_WEAVER_OF_IRON_ARM.get())
        dropSelf(WitcheryBlocks.DREAM_WEAVER_OF_NIGHTMARES.get())
        dropSelf(WitcheryBlocks.DREAM_WEAVER_OF_FASTING.get())
        dropSelf(WitcheryBlocks.DREAM_WEAVER_OF_INTENSITY.get())

        dropSelf(WitcheryBlocks.ROWAN_LOG.get())
        dropSelf(WitcheryBlocks.ROWAN_WOOD.get())
        dropSelf(WitcheryBlocks.STRIPPED_ROWAN_LOG.get())
        dropSelf(WitcheryBlocks.STRIPPED_ROWAN_WOOD.get())
        this.add(
            WitcheryBlocks.ROWAN_LEAVES.get(), createLeavesDrops(
                WitcheryBlocks.ROWAN_LEAVES.get(),
                WitcheryBlocks.ROWAN_SAPLING.get(), 0.05f, 0.0625f, 0.083333336f, 0.1f
            )
        )
        this.add(
            WitcheryBlocks.ROWAN_BERRY_LEAVES.get(), createFruitDroppingLeaves(
                WitcheryBlocks.ROWAN_BERRY_LEAVES.get(),
                WitcheryBlocks.ROWAN_SAPLING.get(),
                WitcheryItems.ROWAN_BERRIES.get(),
                0.05f,
                0.0625f,
                0.083333336f,
                0.1f
            )
        )
        dropSelf(WitcheryBlocks.ROWAN_PLANKS.get())
        dropSelf(WitcheryBlocks.ROWAN_STAIRS.get())
        dropSelf(WitcheryBlocks.ROWAN_SLAB.get())
        dropSelf(WitcheryBlocks.ROWAN_FENCE.get())
        dropSelf(WitcheryBlocks.ROWAN_FENCE_GATE.get())
        this.add(WitcheryBlocks.ROWAN_DOOR.get(), createDoorTable(WitcheryBlocks.ROWAN_DOOR.get()))
        dropSelf(WitcheryBlocks.ROWAN_TRAPDOOR.get())
        dropSelf(WitcheryBlocks.ROWAN_PRESSURE_PLATE.get())
        dropSelf(WitcheryBlocks.ROWAN_BUTTON.get())
        dropSelf(WitcheryBlocks.ROWAN_SAPLING.get())
        this.add(WitcheryBlocks.POTTED_ROWAN_SAPLING.get(), createPotFlowerItemTable(WitcheryItems.ROWAN_SAPLING.get()))
        dropOther(WitcheryBlocks.ROWAN_SIGN.get(), WitcheryItems.ROWAN_SIGN.get())
        dropOther(WitcheryBlocks.ROWAN_WALL_SIGN.get(), WitcheryItems.ROWAN_SIGN.get())
        dropOther(WitcheryBlocks.ROWAN_HANGING_SIGN.get(), WitcheryItems.ROWAN_HANGING_SIGN.get())
        dropOther(WitcheryBlocks.ROWAN_WALL_HANGING_SIGN.get(), WitcheryItems.ROWAN_HANGING_SIGN.get())

        dropSelf(WitcheryBlocks.ALDER_LOG.get())
        dropSelf(WitcheryBlocks.ALDER_WOOD.get())
        dropSelf(WitcheryBlocks.STRIPPED_ALDER_LOG.get())
        dropSelf(WitcheryBlocks.STRIPPED_ALDER_WOOD.get())
        this.add(
            WitcheryBlocks.ALDER_LEAVES.get(), createLeavesDrops(
                WitcheryBlocks.ALDER_LEAVES.get(),
                WitcheryBlocks.ALDER_SAPLING.get(), 0.05f, 0.0625f, 0.083333336f, 0.1f
            )
        )
        dropSelf(WitcheryBlocks.ALDER_PLANKS.get())
        dropSelf(WitcheryBlocks.ALDER_STAIRS.get())
        dropSelf(WitcheryBlocks.ALDER_SLAB.get())
        dropSelf(WitcheryBlocks.ALDER_FENCE.get())
        dropSelf(WitcheryBlocks.ALDER_FENCE_GATE.get())
        this.add(WitcheryBlocks.ALDER_DOOR.get(), createDoorTable(WitcheryBlocks.ALDER_DOOR.get()))
        dropSelf(WitcheryBlocks.ALDER_TRAPDOOR.get())
        dropSelf(WitcheryBlocks.ALDER_PRESSURE_PLATE.get())
        dropSelf(WitcheryBlocks.ALDER_BUTTON.get())
        dropSelf(WitcheryBlocks.ALDER_SAPLING.get())
        this.add(WitcheryBlocks.POTTED_ALDER_SAPLING.get(), createPotFlowerItemTable(WitcheryItems.ALDER_SAPLING.get()))
        dropOther(WitcheryBlocks.ALDER_SIGN.get(), WitcheryItems.ALDER_SIGN.get())
        dropOther(WitcheryBlocks.ALDER_WALL_SIGN.get(), WitcheryItems.ALDER_SIGN.get())
        dropOther(WitcheryBlocks.ALDER_HANGING_SIGN.get(), WitcheryItems.ALDER_HANGING_SIGN.get())
        dropOther(WitcheryBlocks.ALDER_WALL_HANGING_SIGN.get(), WitcheryItems.ALDER_HANGING_SIGN.get())

        dropSelf(WitcheryBlocks.HAWTHORN_LOG.get())
        dropSelf(WitcheryBlocks.HAWTHORN_WOOD.get())
        dropSelf(WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get())
        dropSelf(WitcheryBlocks.STRIPPED_HAWTHORN_WOOD.get())
        this.add(
            WitcheryBlocks.HAWTHORN_LEAVES.get(), createLeavesDrops(
                WitcheryBlocks.HAWTHORN_LEAVES.get(),
                WitcheryBlocks.HAWTHORN_SAPLING.get(), 0.05f, 0.0625f, 0.083333336f, 0.1f
            )
        )
        dropSelf(WitcheryBlocks.HAWTHORN_PLANKS.get())
        dropSelf(WitcheryBlocks.HAWTHORN_STAIRS.get())
        dropSelf(WitcheryBlocks.HAWTHORN_SLAB.get())
        dropSelf(WitcheryBlocks.HAWTHORN_FENCE.get())
        dropSelf(WitcheryBlocks.HAWTHORN_FENCE_GATE.get())
        this.add(WitcheryBlocks.HAWTHORN_DOOR.get(), createDoorTable(WitcheryBlocks.HAWTHORN_DOOR.get()))
        dropSelf(WitcheryBlocks.HAWTHORN_TRAPDOOR.get())
        dropSelf(WitcheryBlocks.HAWTHORN_PRESSURE_PLATE.get())
        dropSelf(WitcheryBlocks.HAWTHORN_BUTTON.get())
        dropSelf(WitcheryBlocks.HAWTHORN_SAPLING.get())
        this.add(
            WitcheryBlocks.POTTED_HAWTHORN_SAPLING.get(),
            createPotFlowerItemTable(WitcheryItems.HAWTHORN_SAPLING.get())
        )
        dropOther(WitcheryBlocks.HAWTHORN_SIGN.get(), WitcheryItems.HAWTHORN_SIGN.get())
        dropOther(WitcheryBlocks.HAWTHORN_WALL_SIGN.get(), WitcheryItems.HAWTHORN_SIGN.get())
        dropOther(WitcheryBlocks.HAWTHORN_HANGING_SIGN.get(), WitcheryItems.HAWTHORN_HANGING_SIGN.get())
        dropOther(WitcheryBlocks.HAWTHORN_WALL_HANGING_SIGN.get(), WitcheryItems.HAWTHORN_HANGING_SIGN.get())

        this.add(
            WitcheryBlocks.EMBER_MOSS.get()
        ) { itemLike: Block ->
            createShearsOnlyDrop(
                itemLike
            )
        }

        this.add(
            WitcheryBlocks.GLINTWEED.get()
        ) { itemLike: Block ->
            createShearsOnlyDrop(
                itemLike
            )
        }

        this.add(
            WitcheryBlocks.SPANISH_MOSS.get()
        ) { itemLike: Block ->
            createShearsOnlyDrop(
                itemLike
            )
        }

        val builder: LootItemCondition.Builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            WitcheryBlocks.MANDRAKE_CROP.get()
        ).setProperties(
            StatePropertiesPredicate.Builder.properties()
                .hasProperty(WitcheryCropBlock.AGE, 4).hasProperty(MandrakeCropBlock.AWAKE, false)
        )
        val otherBuilder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            WitcheryBlocks.MANDRAKE_CROP.get()
        ).setProperties(
            StatePropertiesPredicate.Builder.properties()
                .hasProperty(WitcheryCropBlock.AGE, 4).hasProperty(MandrakeCropBlock.AWAKE, true)
        )
        val builder2: LootItemCondition.Builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            WitcheryBlocks.BELLADONNA_CROP.get()
        ).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WitcheryCropBlock.AGE, 4))
        val builder3: LootItemCondition.Builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            WitcheryBlocks.SNOWBELL_CROP.get()
        ).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WitcheryCropBlock.AGE, 4))
        val builder4: LootItemCondition.Builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            WitcheryBlocks.WATER_ARTICHOKE_CROP.get()
        ).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WitcheryCropBlock.AGE, 4))
        val builder5: LootItemCondition.Builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            WitcheryBlocks.WOLFSFBANE_CROP.get()
        ).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WitcheryCropBlock.AGE, 4))
        val builder6: LootItemCondition.Builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            WitcheryBlocks.WORMWOOD_CROP.get()
        ).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WitcheryCropBlock.AGE, 4))
        val builder7: LootItemCondition.Builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            WitcheryBlocks.GARLIC_CROP.get()
        ).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WitcheryCropBlock.AGE, 4))



        add(
            WitcheryBlocks.MANDRAKE_CROP.get(),
            createMandrakeCropDrops(
                WitcheryBlocks.MANDRAKE_CROP.get(),
                WitcheryItems.MANDRAKE_ROOT.get(),
                WitcheryItems.MANDRAKE_SEEDS.get(),
                builder,
                otherBuilder
            )
        )
        add(
            WitcheryBlocks.BELLADONNA_CROP.get(),
            createCropDrops(
                WitcheryBlocks.BELLADONNA_CROP.get(),
                WitcheryItems.BELLADONNA_FLOWER.get(),
                WitcheryItems.BELLADONNA_SEEDS.get(),
                builder2
            )
        )
        add(
            WitcheryBlocks.WATER_ARTICHOKE_CROP.get(),
            createCropDrops(
                WitcheryBlocks.WATER_ARTICHOKE_CROP.get(),
                WitcheryItems.WATER_ARTICHOKE_GLOBE.get(),
                WitcheryItems.WATER_ARTICHOKE_SEEDS.get(),
                builder4
            )
        )
        add(
            WitcheryBlocks.SNOWBELL_CROP.get(),
            createCropDrops(
                WitcheryBlocks.SNOWBELL_CROP.get(),
                WitcheryItems.ICY_NEEDLE.get(),
                WitcheryItems.SNOWBELL_SEEDS.get(),
                builder3
            )
        )

        add(
            WitcheryBlocks.WOLFSFBANE_CROP.get(),
            createCropDrops(
                WitcheryBlocks.WOLFSFBANE_CROP.get(),
                WitcheryItems.WOLFSBANE.get(),
                WitcheryItems.WOLFSBANE_SEEDS.get(),
                builder5
            )
        )
        add(
            WitcheryBlocks.WORMWOOD_CROP.get(),
            createCropDrops(
                WitcheryBlocks.WORMWOOD_CROP.get(),
                WitcheryItems.WORMWOOD.get(),
                WitcheryItems.WORMWOOD_SEEDS.get(),
                builder6
            )
        )
        add(
            WitcheryBlocks.GARLIC_CROP.get(),
            createCropDrops(
                WitcheryBlocks.GARLIC_CROP.get(),
                WitcheryItems.GARLIC.get(),
                WitcheryItems.GARLIC.get(),
                builder7
            )
        )

    }

    fun createFruitDroppingLeaves(
        leavesBlock: Block,
        saplingBlock: Block,
        fruitItem: Item,
        vararg chances: Float
    ): LootTable.Builder {
        val registryLookup = registries.lookupOrThrow(Registries.ENCHANTMENT)
        return createLeavesDrops(leavesBlock, saplingBlock, *chances).withPool(
            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).`when`(
                this.doesNotHaveShearsOrSilkTouch()
            ).add(
                (applyExplosionCondition(
                    leavesBlock,
                    LootItem.lootTableItem(fruitItem)
                ) as LootPoolSingletonContainer.Builder<*>).`when`(
                    BonusLevelTableCondition.bonusLevelFlatChance(
                        registryLookup.getOrThrow(Enchantments.FORTUNE),
                        *floatArrayOf(0.005f, 0.0055555557f, 0.00625f, 0.008333334f, 0.025f)
                    )
                )
            )
        )
    }

    fun createMandrakeCropDrops(
        cropBlock: Block,
        cropItem: Item,
        seedsItem: Item,
        sleepBuilder: LootItemCondition.Builder,
        awakeBuilder: LootItemCondition.Builder
    ): LootTable.Builder {
        val registryLookup = registries.lookupOrThrow(Registries.ENCHANTMENT)
        return createCropDrops(
            WitcheryBlocks.MANDRAKE_CROP.get(),
            WitcheryItems.MANDRAKE_ROOT.get(),
            WitcheryItems.MANDRAKE_SEEDS.get(),
            sleepBuilder
        ).withPool(
            LootPool.lootPool().setRolls(ConstantValue.exactly(1f)).`when`(awakeBuilder)
                .add(LootItem.lootTableItem(seedsItem)).apply(
                ApplyBonusCount.addBonusBinomialDistributionCount(
                    registryLookup.getOrThrow(Enchantments.FORTUNE),
                    0.5714286F,
                    3
                )
            )
        )
    }
}
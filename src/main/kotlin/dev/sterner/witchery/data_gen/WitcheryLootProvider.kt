package dev.sterner.witchery.data_gen

import dev.sterner.witchery.content.block.MandrakeCropBlock
import dev.sterner.witchery.content.block.WitcheryCropBlock
import dev.sterner.witchery.core.registry.WitcheryBlocks
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.core.HolderLookup
import net.minecraft.core.WritableRegistry
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.EntityLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableSubProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ProblemReporter
import net.minecraft.world.entity.EntityType
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.BedBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.properties.BedPart
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.ValidationContext
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer
import java.util.stream.Collectors
import java.util.stream.Stream


class WitcheryLootProvider(packOutput: PackOutput, provider: CompletableFuture<HolderLookup.Provider>) :
    LootTableProvider(
        packOutput, mutableSetOf(),
        listOf(
            SubProviderEntry({ BlocksLoot(it) }, LootContextParamSets.BLOCK),
            SubProviderEntry({ EntityLoot(it) }, LootContextParamSets.ENTITY),
            SubProviderEntry({ ArchaeologyLoot(it) }, LootContextParamSets.CHEST)
        ), provider
    ) {

    override fun validate(
        writableregistry: WritableRegistry<LootTable?>,
        validationcontext: ValidationContext,
        `problemreporter$collector`: ProblemReporter.Collector
    ) {

    }

    class ArchaeologyLoot(provider: HolderLookup.Provider) :
        LootTableSubProvider {

        override fun generate(output: BiConsumer<ResourceKey<LootTable>, LootTable.Builder>) {
            output.accept(
                ResourceKey.create(
                    Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath("witchery", "archaeology/graveyard_dirt")
                ),
                LootTable.lootTable()
                    .withPool(
                        LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1f))
                            .add(LootItem.lootTableItem(Items.BONE).setWeight(1))
                            .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(1))
                            .add(LootItem.lootTableItem(WitcheryItems.TORN_PAGE.get()).setWeight(1))
                            .add(LootItem.lootTableItem(Items.SKELETON_SKULL).setWeight(1))
                    )
            )
        }
    }

    class EntityLoot(provider: HolderLookup.Provider) :
        EntityLootSubProvider(FeatureFlags.REGISTRY.allFlags(), FeatureFlags.REGISTRY.allFlags(), provider) {

        override fun getKnownEntityTypes(): Stream<EntityType<*>?> {
            return WitcheryEntityTypes.ENTITY_TYPES.entries.stream().map { it.value() }.filter {
                        it != WitcheryEntityTypes.INSANITY.get() &&
                        it != WitcheryEntityTypes.WEREWOLF.get() &&
                        it != WitcheryEntityTypes.AREA_EFFECT_CLOUD.get() &&
                        it != WitcheryEntityTypes.ELLE.get() &&
                        it != WitcheryEntityTypes.THROWN_BREW.get() &&
                        it != WitcheryEntityTypes.BROOM.get() &&
                        it != WitcheryEntityTypes.VAMPIRE.get() &&
                        it != WitcheryEntityTypes.LILITH.get() &&
                        it != WitcheryEntityTypes.PARASITIC_LOUSE.get() &&
                        it != WitcheryEntityTypes.IMP.get()
            }.collect(Collectors.toList()).stream()
        }

        override fun generate() {
            this.add(
                WitcheryEntityTypes.DEATH.get(),
                LootTable.lootTable()
                    .withPool(
                        LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(2f))
                            .add(LootItem.lootTableItem(WitcheryItems.DEATH_HOOD.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f))))
                            .add(LootItem.lootTableItem(WitcheryItems.DEATH_BOOTS.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f))))
                            .add(LootItem.lootTableItem(WitcheryItems.DEATH_SICKLE.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f))))
                            .add(LootItem.lootTableItem(WitcheryItems.DEATH_ROBE.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f))))
                    )
            )

            this.add(
                WitcheryEntityTypes.HORNED_HUNTSMAN.get(), LootTable.lootTable()
                    .withPool(
                        LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(
                                LootItem.lootTableItem(WitcheryItems.HUNTSMAN_SPEAR.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                            )
                    )
            )

            this.add(
                WitcheryEntityTypes.BABA_YAGA.get(), LootTable.lootTable()
                    .withPool(
                        LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(
                                LootItem.lootTableItem(WitcheryItems.BABA_YAGAS_HAT.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                            )
                    )
            )

            this.add(
                WitcheryEntityTypes.MANDRAKE.get(), LootTable.lootTable()
                    .withPool(
                        LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(2.0F))
                            .add(
                                LootItem.lootTableItem(WitcheryItems.MANDRAKE_ROOT.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                            )
                            .add(
                                LootItem.lootTableItem(WitcheryItems.MANDRAKE_SEEDS.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                            )
                    )
            )

            this.add(
                WitcheryEntityTypes.OWL.get(), LootTable.lootTable()
                    .withPool(
                        LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(
                                LootItem.lootTableItem(WitcheryItems.OWLETS_WING.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                            )
                    )
            )


            this.add(
                WitcheryEntityTypes.DEMON.get(), LootTable.lootTable()
                    .withPool(
                        LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(
                                LootItem.lootTableItem(WitcheryItems.DEMON_HEART.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                            )
                    )
            )

            this.add(
                WitcheryEntityTypes.ENT.get(), LootTable.lootTable()
                    .withPool(
                        LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(
                                LootItem.lootTableItem(WitcheryItems.ENT_TWIG.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                            )
                    )
            )
            this.add(
                WitcheryEntityTypes.BANSHEE.get(), LootTable.lootTable()
                    .withPool(
                        LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(
                                LootItem.lootTableItem(WitcheryItems.SPECTRAL_DUST.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                            )
                    )
            )
            this.add(
                WitcheryEntityTypes.SPIRIT.get(), LootTable.lootTable()
                    .withPool(
                        LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(
                                LootItem.lootTableItem(WitcheryItems.SPECTRAL_DUST.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                            )
                    )
            )
            this.add(
                WitcheryEntityTypes.POLTERGEIST.get(), LootTable.lootTable()
                    .withPool(
                        LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(
                                LootItem.lootTableItem(WitcheryItems.SPECTRAL_DUST.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                            )
                    )
            )

            this.add(
                WitcheryEntityTypes.SPECTRAL_PIG.get(), LootTable.lootTable()
                    .withPool(
                        LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(
                                LootItem.lootTableItem(WitcheryItems.SPECTRAL_DUST.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                            )
                    )
            )

            this.add(
                WitcheryEntityTypes.NIGHTMARE.get(), LootTable.lootTable()
                    .withPool(
                        LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(
                                LootItem.lootTableItem(WitcheryItems.MELLIFLUOUS_HUNGER.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                            )
                    )
            )
        }
    }

    class BlocksLoot(provider: HolderLookup.Provider) :
        BlockLootSubProvider(setOf(), FeatureFlags.REGISTRY.allFlags(), provider) {

        override fun getKnownBlocks(): Iterable<Block?> {
            return WitcheryBlocks.BLOCKS.entries.stream().map { it.value() }.filter {
                it != WitcheryBlocks.ALTAR.get() &&
                        it != WitcheryBlocks.COMPONENT.get() &&
                        it != WitcheryBlocks.ALTAR_COMPONENT.get() &&
                        it != WitcheryBlocks.CAULDRON_COMPONENT.get() &&
                        it != WitcheryBlocks.EFFIGY_COMPONENT.get() &&
                        it != WitcheryBlocks.MUSHROOM_LOG_COMPONENT.get() &&
                        it != WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION_COMPONENT.get() &&
                        it != WitcheryBlocks.SACRIFICIAL_CIRCLE_COMPONENT.get() &&
                        it != WitcheryBlocks.SPIRIT_PORTAL_COMPONENT.get() &&
                        it != WitcheryBlocks.WEREWOLF_ALTAR_COMPONENT.get() &&
                        it != WitcheryBlocks.CAULDRON_DUMMY.get() &&
                        it != WitcheryBlocks.INFINITY_EGG.get() &&
                        it != WitcheryBlocks.RITUAL_CHALK_BLOCK.get() &&
                        it != WitcheryBlocks.INFERNAL_CHALK_BLOCK.get() &&
                        it != WitcheryBlocks.OTHERWHERE_CHALK_BLOCK.get() &&
                        it != WitcheryBlocks.GOLDEN_CHALK_BLOCK.get() &&
                        it != WitcheryBlocks.WISPY_COTTON.get() &&
                        it != WitcheryBlocks.SPIRIT_PORTAL.get() &&
                        it != WitcheryBlocks.ANCIENT_TABLET.get() &&
                        it != WitcheryBlocks.DISTURBED_COTTON.get() &&
                        it != WitcheryBlocks.POPPET.get() &&
                        it != WitcheryBlocks.MUSHROOM_LOG.get() &&
                        it != WitcheryBlocks.SUSPICIOUS_GRAVEYARD_DIRT.get() &&
                        it != WitcheryBlocks.ANCIENT_TABLET_COMPONENT.get() &&
                        it != WitcheryBlocks.DISTILLERY_COMPONENT.get()
            }.collect(Collectors.toList())
        }

        override fun generate() {

            fun createCoffinTable(coffin: Block): LootTable.Builder {
                return this.createSinglePropConditionTable<BedPart>(
                    coffin,
                    BedBlock.PART,
                    BedPart.HEAD
                )
            }
            this.add(WitcheryBlocks.COFFIN.get(), createCoffinTable(WitcheryBlocks.COFFIN.get()))

            dropSelf(WitcheryBlocks.DEEPLSTAE_ALTAR_BLOCK.get())
            dropSelf(WitcheryBlocks.CRYSTAL_BALL.get())
            dropSelf(WitcheryBlocks.CENSER.get())
            dropSelf(WitcheryBlocks.PHYLACTERY.get())
            dropSelf(WitcheryBlocks.WITCHS_LADDER.get())
            dropSelf(WitcheryBlocks.WEREWOLF_ALTAR.get())
            dropSelf(WitcheryBlocks.SOUL_CAGE.get())
            dropSelf(WitcheryBlocks.BLOOD_CRUCIBLE.get())
            dropSelf(WitcheryBlocks.SCARECROW.get())
            dropSelf(WitcheryBlocks.CLAY_EFFIGY.get())
            dropSelf(WitcheryBlocks.BEAR_TRAP.get())

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
            dropSelf(WitcheryBlocks.BLOOD_STAINED_HAY.get())
            dropSelf(WitcheryBlocks.GRASSPER.get())
            dropSelf(WitcheryBlocks.CRITTER_SNARE.get())

            dropOther(WitcheryBlocks.SACRIFICIAL_CIRCLE.get(), Blocks.SKELETON_SKULL)
            dropOther(WitcheryBlocks.SOUL_CAGE.get(), WitcheryBlocks.BRAZIER.get())

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
            this.add(
                WitcheryBlocks.POTTED_ROWAN_SAPLING.get(),
                createPotFlowerItemTable(WitcheryItems.ROWAN_SAPLING.get())
            )
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
            this.add(
                WitcheryBlocks.POTTED_ALDER_SAPLING.get(),
                createPotFlowerItemTable(WitcheryItems.ALDER_SAPLING.get())
            )
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
                WitcheryBlocks.LIFE_BLOOD.get()
            ) { itemLike: Block ->
                createShearsOnlyDrop(
                    itemLike
                )
            }

            this.add(
                WitcheryBlocks.LIFE_BLOOD_PLANT.get()
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
                    HAS_SHEARS.or(this.hasSilkTouch()).invert()
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
}
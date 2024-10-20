package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery.MODID
import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlock
import dev.sterner.witchery.block.*
import dev.sterner.witchery.block.altar.AltarBlock
import dev.sterner.witchery.block.altar.AltarBlockComponent
import dev.sterner.witchery.block.arthana.ArthanaBlock
import dev.sterner.witchery.block.cauldron.CauldronBlock
import dev.sterner.witchery.block.cauldron.CauldronBlockComponent
import dev.sterner.witchery.block.distillery.DistilleryBlock
import dev.sterner.witchery.block.distillery.DistilleryCompanionBlock
import dev.sterner.witchery.block.oven.OvenBlock
import dev.sterner.witchery.block.oven.OvenFumeExtensionBlock
import dev.sterner.witchery.block.oven.OvenFumeExtensionBlockComponent
import dev.sterner.witchery.block.ritual.GoldenChalkBlock
import dev.sterner.witchery.block.ritual.RitualChalkBlock
import dev.sterner.witchery.block.signs.CustomCeilingHangingSignBlock
import dev.sterner.witchery.block.signs.CustomStandingSignBlock
import dev.sterner.witchery.block.signs.CustomWallHangingSignBlock
import dev.sterner.witchery.block.signs.CustomWallSignBlock
import dev.sterner.witchery.platform.StrippableHelper
import dev.sterner.witchery.worldgen.tree.WitcheryTreeGrowers
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.BlockSetType
import net.minecraft.world.level.block.state.properties.WoodType
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import java.awt.Color


object WitcheryBlocks {

    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(MODID, Registries.BLOCK)

    val COMPONENT: RegistrySupplier<MultiBlockComponentBlock> = BLOCKS.register("component") {
        MultiBlockComponentBlock(BlockBehaviour.Properties.of())
    }

    val DEEPLSTAE_ALTAR_BLOCK = BLOCKS.register("deepslate_altar_block") {
        AltarCreationBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE))
    }

    val ALTAR: RegistrySupplier<AltarBlock> = BLOCKS.register("altar") {
        AltarBlock(BlockBehaviour.Properties.of())
    }

    val ALTAR_COMPONENT: RegistrySupplier<AltarBlockComponent> = BLOCKS.register("altar_component") {
        AltarBlockComponent(BlockBehaviour.Properties.of())
    }

    val CAULDRON: RegistrySupplier<CauldronBlock> = BLOCKS.register("cauldron") {
        CauldronBlock(
            BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
        )
    }

    val CAULDRON_COMPONENT: RegistrySupplier<CauldronBlockComponent> = BLOCKS.register("cauldron_component") {
        CauldronBlockComponent(
            BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
        )
    }

    val COPPER_CAULDRON: RegistrySupplier<CauldronBlock> = BLOCKS.register("copper_cauldron") {
        CauldronBlock(
            BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
        )
    }

    val EXPOSED_COPPER_CAULDRON: RegistrySupplier<CauldronBlock> = BLOCKS.register("exposed_copper_cauldron") {
        CauldronBlock(
            BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
        )
    }

    val WEATHERED_COPPER_CAULDRON: RegistrySupplier<CauldronBlock> = BLOCKS.register("weathered_copper_cauldron") {
        CauldronBlock(
            BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
        )
    }

    val OXIDIZED_COPPER_CAULDRON: RegistrySupplier<CauldronBlock> = BLOCKS.register("oxidized_copper_cauldron") {
        CauldronBlock(
            BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
        )
    }

    val WAXED_COPPER_CAULDRON: RegistrySupplier<CauldronBlock> = BLOCKS.register("waxed_copper_cauldron") {
        CauldronBlock(
            BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
        )
    }

    val WAXED_EXPOSED_COPPER_CAULDRON: RegistrySupplier<CauldronBlock> =
        BLOCKS.register("waxed_exposed_copper_cauldron") {
            CauldronBlock(
                BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
            )
        }

    val WAXED_WEATHERED_COPPER_CAULDRON: RegistrySupplier<CauldronBlock> =
        BLOCKS.register("waxed_weathered_copper_cauldron") {
            CauldronBlock(
                BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
            )
        }

    val WAXED_OXIDIZED_COPPER_CAULDRON: RegistrySupplier<CauldronBlock> =
        BLOCKS.register("waxed_oxidized_copper_cauldron") {
            CauldronBlock(
                BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
            )
        }

    val DISTILLERY = BLOCKS.register("distillery") {
        DistilleryBlock(BlockBehaviour.Properties.of())
    }

    val DISTILLERY_COMPONENT: RegistrySupplier<DistilleryCompanionBlock> = BLOCKS.register("distillery_component") {
        DistilleryCompanionBlock(
            BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
        )
    }

    val IRON_WITCHES_OVEN: RegistrySupplier<OvenBlock> = BLOCKS.register("iron_witches_oven") {
        OvenBlock(
            BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
        )
    }

    val IRON_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        BLOCKS.register("iron_witches_oven_fume_extension") {
            OvenFumeExtensionBlock(
                BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
            )
        }

    val COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        BLOCKS.register("copper_witches_oven_fume_extension") {
            OvenFumeExtensionBlock(
                BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
            )
        }

    val WAXED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        BLOCKS.register("waxed_copper_witches_oven_fume_extension") {
            OvenFumeExtensionBlock(
                BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
            )
        }

    val EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        BLOCKS.register("exposed_copper_witches_oven_fume_extension") {
            OvenFumeExtensionBlock(
                BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
            )
        }

    val WAXED_EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        BLOCKS.register("waxed_exposed_copper_witches_oven_fume_extension") {
            OvenFumeExtensionBlock(
                BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
            )
        }

    val WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        BLOCKS.register("weathered_copper_witches_oven_fume_extension") {
            OvenFumeExtensionBlock(
                BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
            )
        }

    val WAXED_WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        BLOCKS.register("waxed_weathered_copper_witches_oven_fume_extension") {
            OvenFumeExtensionBlock(
                BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
            )
        }

    val OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        BLOCKS.register("oxidized_copper_witches_oven_fume_extension") {
            OvenFumeExtensionBlock(
                BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
            )
        }

    val WAXED_OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        BLOCKS.register("waxed_oxidized_copper_witches_oven_fume_extension") {
            OvenFumeExtensionBlock(
                BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
            )
        }

    val IRON_WITCHES_OVEN_FUME_EXTENSION_COMPONENT: RegistrySupplier<OvenFumeExtensionBlockComponent> =
        BLOCKS.register("iron_witches_oven_fume_extension_component") {
            OvenFumeExtensionBlockComponent(
                BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
            )
        }

    val COPPER_WITCHES_OVEN: RegistrySupplier<OvenBlock> = BLOCKS.register("copper_witches_oven") {
        OvenBlock(
            BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
        )
    }

    val EXPOSED_COPPER_WITCHES_OVEN: RegistrySupplier<OvenBlock> = BLOCKS.register("exposed_copper_witches_oven") {
        OvenBlock(
            BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
        )
    }

    val WEATHERED_COPPER_WITCHES_OVEN: RegistrySupplier<OvenBlock> = BLOCKS.register("weathered_copper_witches_oven") {
        OvenBlock(
            BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
        )
    }

    val OXIDIZED_COPPER_WITCHES_OVEN: RegistrySupplier<OvenBlock> = BLOCKS.register("oxidized_copper_witches_oven") {
        OvenBlock(
            BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
        )
    }

    val WAXED_COPPER_WITCHES_OVEN: RegistrySupplier<OvenBlock> = BLOCKS.register("waxed_copper_witches_oven") {
        OvenBlock(
            BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
        )
    }

    val WAXED_EXPOSED_COPPER_WITCHES_OVEN: RegistrySupplier<OvenBlock> =
        BLOCKS.register("waxed_exposed_copper_witches_oven") {
            OvenBlock(
                BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
            )
        }

    val WAXED_WEATHERED_COPPER_WITCHES_OVEN: RegistrySupplier<OvenBlock> =
        BLOCKS.register("waxed_weathered_copper_witches_oven") {
            OvenBlock(
                BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
            )
        }

    val WAXED_OXIDIZED_COPPER_WITCHES_OVEN: RegistrySupplier<OvenBlock> =
        BLOCKS.register("waxed_oxidized_copper_witches_oven") {
            OvenBlock(
                BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
            )
        }

    val DEMON_HEART = BLOCKS.register("demon_heart") {
        DemonHeartBlock(BlockBehaviour.Properties.of())
    }

    val INFINITY_EGG = BLOCKS.register("infinity_egg") {
        InfinityEggBlock(BlockBehaviour.Properties.of())
    }

    val STRIPPED_ROWAN_LOG = BLOCKS.register("stripped_rowan_log") {
        RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG))
    }

    val ROWAN_LOG = BLOCKS.register(
        "rowan_log", StrippableHelper.createStrippableLog(
            STRIPPED_ROWAN_LOG,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
        )
    )

    val STRIPPED_ROWAN_WOOD = BLOCKS.register("stripped_rowan_wood") {
        RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD))
    }

    val ROWAN_WOOD = BLOCKS.register(
        "rowan_wood", StrippableHelper.createStrippableLog(
            STRIPPED_ROWAN_WOOD,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)
        )
    )

    val ROWAN_LEAVES = BLOCKS.register("rowan_leaves") {
        LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AZALEA_LEAVES))
    }

    val ROWAN_BERRY_LEAVES = BLOCKS.register("rowan_berry_leaves") {
        LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWERING_AZALEA_LEAVES))
    }

    val ROWAN_PLANKS = BLOCKS.register("rowan_planks") {
        Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
    }

    val ROWAN_STAIRS = BLOCKS.register("rowan_stairs") {
        StairBlock(
            ROWAN_PLANKS.orElseGet { Blocks.OAK_PLANKS }.defaultBlockState(),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)
        )
    }

    val ROWAN_SLAB = BLOCKS.register("rowan_slab") {
        SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB))
    }

    val ROWAN_FENCE = BLOCKS.register("rowan_fence") {
        FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE))
    }

    val ROWAN_WOOD_TYPE = WoodType.register(WoodType("$MODID:rowan", BlockSetType.OAK))

    val ROWAN_FENCE_GATE = BLOCKS.register("rowan_fence_gate") {
        FenceGateBlock(ROWAN_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE))
    }

    val ROWAN_DOOR = BLOCKS.register("rowan_door") {
        DoorBlock(ROWAN_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR))
    }

    val ROWAN_TRAPDOOR = BLOCKS.register("rowan_trapdoor") {
        TrapDoorBlock(ROWAN_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR))
    }

    val ROWAN_PRESSURE_PLATE = BLOCKS.register("rowan_pressure_plate") {
        PressurePlateBlock(ROWAN_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE))
    }

    val ROWAN_BUTTON = BLOCKS.register("rowan_button") {
        ButtonBlock(ROWAN_WOOD_TYPE.setType, 30, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON))
    }

    val ROWAN_SAPLING = BLOCKS.register("rowan_sapling") {
        SaplingBlock(WitcheryTreeGrowers.ROWAN, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING))
    }

    val POTTED_ROWAN_SAPLING = BLOCKS.register("potted_rowan_sapling") {
        FlowerPotBlock(ROWAN_SAPLING.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING))
    }

    val ROWAN_SIGN = BLOCKS.register("rowan_sign") {
        CustomStandingSignBlock(ROWAN_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN))
    }

    val ROWAN_WALL_SIGN = BLOCKS.register("rowan_wall_sign") {
        CustomWallSignBlock(ROWAN_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN))
    }

    val ROWAN_HANGING_SIGN = BLOCKS.register("rowan_hanging_sign") {
        CustomCeilingHangingSignBlock(ROWAN_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN))
    }

    val ROWAN_WALL_HANGING_SIGN = BLOCKS.register("rowan_wall_hanging_sign") {
        CustomWallHangingSignBlock(ROWAN_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN))
    }


    val STRIPPED_ALDER_LOG = BLOCKS.register("stripped_alder_log") {
        RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG))
    }

    val ALDER_LOG = BLOCKS.register(
        "alder_log", StrippableHelper.createStrippableLog(
            STRIPPED_ALDER_LOG,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
        )
    )

    val STRIPPED_ALDER_WOOD = BLOCKS.register("stripped_alder_wood") {
        RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD))
    }

    val ALDER_WOOD = BLOCKS.register(
        "alder_wood", StrippableHelper.createStrippableLog(
            STRIPPED_ALDER_WOOD,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)
        )
    )

    val ALDER_LEAVES = BLOCKS.register("alder_leaves") {
        LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AZALEA_LEAVES))
    }

    val ALDER_PLANKS = BLOCKS.register("alder_planks") {
        Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
    }

    val ALDER_STAIRS = BLOCKS.register("alder_stairs") {
        StairBlock(
            ALDER_PLANKS.orElseGet { Blocks.OAK_PLANKS }.defaultBlockState(),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)
        )
    }

    val ALDER_SLAB = BLOCKS.register("alder_slab") {
        SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB))
    }

    val ALDER_FENCE = BLOCKS.register("alder_fence") {
        FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE))
    }

    val ALDER_WOOD_TYPE = WoodType.register(WoodType("$MODID:alder", BlockSetType.OAK))

    val ALDER_FENCE_GATE = BLOCKS.register("alder_fence_gate") {
        FenceGateBlock(ALDER_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE))
    }

    val ALDER_DOOR = BLOCKS.register("alder_door") {
        DoorBlock(ALDER_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR))
    }

    val ALDER_TRAPDOOR = BLOCKS.register("alder_trapdoor") {
        TrapDoorBlock(ALDER_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR))
    }

    val ALDER_PRESSURE_PLATE = BLOCKS.register("alder_pressure_plate") {
        PressurePlateBlock(ALDER_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE))
    }

    val ALDER_BUTTON = BLOCKS.register("alder_button") {
        ButtonBlock(ALDER_WOOD_TYPE.setType, 30, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON))
    }

    val ALDER_SAPLING = BLOCKS.register("alder_sapling") {
        SaplingBlock(WitcheryTreeGrowers.ALDER, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING))
    }

    val POTTED_ALDER_SAPLING = BLOCKS.register("potted_alder_sapling") {
        FlowerPotBlock(ALDER_SAPLING.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING))
    }

    val ALDER_SIGN = BLOCKS.register("alder_sign") {
        CustomStandingSignBlock(ALDER_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN))
    }

    val ALDER_WALL_SIGN = BLOCKS.register("alder_wall_sign") {
        CustomWallSignBlock(ALDER_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN))
    }

    val ALDER_HANGING_SIGN = BLOCKS.register("alder_hanging_sign") {
        CustomCeilingHangingSignBlock(ALDER_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN))
    }

    val ALDER_WALL_HANGING_SIGN = BLOCKS.register("alder_wall_hanging_sign") {
        CustomWallHangingSignBlock(ALDER_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN))
    }


    val STRIPPED_HAWTHORN_LOG = BLOCKS.register("stripped_hawthorn_log") {
        RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG))
    }

    val HAWTHORN_LOG = BLOCKS.register(
        "hawthorn_log", StrippableHelper.createStrippableLog(
            STRIPPED_HAWTHORN_LOG,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
        )
    )

    val STRIPPED_HAWTHORN_WOOD = BLOCKS.register("stripped_hawthorn_wood") {
        RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD))
    }

    val HAWTHORN_WOOD = BLOCKS.register(
        "hawthorn_wood", StrippableHelper.createStrippableLog(
            STRIPPED_HAWTHORN_WOOD,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)
        )
    )

    val HAWTHORN_LEAVES = BLOCKS.register("hawthorn_leaves") {
        LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AZALEA_LEAVES))
    }

    val HAWTHORN_PLANKS = BLOCKS.register("hawthorn_planks") {
        Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
    }

    val HAWTHORN_STAIRS = BLOCKS.register("hawthorn_stairs") {
        StairBlock(
            HAWTHORN_PLANKS.orElseGet { Blocks.OAK_PLANKS }.defaultBlockState(),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)
        )
    }

    val HAWTHORN_SLAB = BLOCKS.register("hawthorn_slab") {
        SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB))
    }

    val HAWTHORN_FENCE = BLOCKS.register("hawthorn_fence") {
        FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE))
    }

    val HAWTHORN_WOOD_TYPE = WoodType.register(WoodType("$MODID:hawthorn", BlockSetType.OAK))

    val HAWTHORN_FENCE_GATE = BLOCKS.register("hawthorn_fence_gate") {
        FenceGateBlock(HAWTHORN_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE))
    }

    val HAWTHORN_DOOR = BLOCKS.register("hawthorn_door") {
        DoorBlock(HAWTHORN_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR))
    }

    val HAWTHORN_TRAPDOOR = BLOCKS.register("hawthorn_trapdoor") {
        TrapDoorBlock(HAWTHORN_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR))
    }

    val HAWTHORN_PRESSURE_PLATE = BLOCKS.register("hawthorn_pressure_plate") {
        PressurePlateBlock(HAWTHORN_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE))
    }

    val HAWTHORN_BUTTON = BLOCKS.register("hawthorn_button") {
        ButtonBlock(HAWTHORN_WOOD_TYPE.setType, 30, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON))
    }

    val HAWTHORN_SAPLING = BLOCKS.register("hawthorn_sapling") {
        SaplingBlock(WitcheryTreeGrowers.HAWTHORN, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING))
    }

    val POTTED_HAWTHORN_SAPLING = BLOCKS.register("potted_hawthorn_sapling") {
        FlowerPotBlock(HAWTHORN_SAPLING.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING))
    }

    val HAWTHORN_SIGN = BLOCKS.register("hawthorn_sign") {
        CustomStandingSignBlock(HAWTHORN_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN))
    }

    val HAWTHORN_WALL_SIGN = BLOCKS.register("hawthorn_wall_sign") {
        CustomWallSignBlock(HAWTHORN_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN))
    }

    val HAWTHORN_HANGING_SIGN = BLOCKS.register("hawthorn_hanging_sign") {
        CustomCeilingHangingSignBlock(HAWTHORN_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN))
    }

    val HAWTHORN_WALL_HANGING_SIGN = BLOCKS.register("hawthorn_wall_hanging_sign") {
        CustomWallHangingSignBlock(
            HAWTHORN_WOOD_TYPE,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN)
        )
    }


    val GLINTWEED: RegistrySupplier<GlintweedBlock> = BLOCKS.register("glintweed") {
        GlintweedBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .pushReaction(PushReaction.DESTROY)
        )
    }

    val EMBER_MOSS: RegistrySupplier<EmbermossBlock> = BLOCKS.register("ember_moss") {
        EmbermossBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .pushReaction(PushReaction.DESTROY)
        )
    }

    val SPANISH_MOSS: RegistrySupplier<SpanishMossBlock> = BLOCKS.register("spanish_moss") {
        SpanishMossBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT)
                .replaceable()
                .noCollission()
                .randomTicks()
                .strength(0.2F)
                .sound(SoundType.VINE)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY)
        )
    }

    val MANDRAKE_CROP = BLOCKS.register("mandrake") {
        MandrakeCropBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY)
        )
    }

    val BELLADONNAE_CROP = BLOCKS.register("belladonna") {
        BelladonnaCropBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY)
        )
    }

    val SNOWBELL_CROP = BLOCKS.register("snowbell") {
        SnowbellCropBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY)
        )
    }

    val WATER_ARTICHOKE_CROP = BLOCKS.register("water_artichoke") {
        WaterArtichokeCropBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY)
        )
    }

    val WOLFSFBANE_CROP = BLOCKS.register("wolfsbane") {
        WolfsbaneCropBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY)
        )
    }

    val GARLIC_CROP = BLOCKS.register("garlic") {
        GarlicCropBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY)
        )
    }

    val WORMWOOD_CROP = BLOCKS.register("wormwood") {
        WormwoodCropBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY)
        )
    }

    val RITUAL_CHALK_BLOCK = BLOCKS.register("ritual_chalk") {
        RitualChalkBlock(null, 0xFFFFFF, BlockBehaviour.Properties.of())
    }

    val INFERNAL_CHALK_BLOCK = BLOCKS.register("infernal_chalk") {
        RitualChalkBlock(ParticleTypes.FLAME, Color(230, 0, 75).rgb, BlockBehaviour.Properties.of())
    }

    val OTHERWHERE_CHALK_BLOCK = BLOCKS.register("otherwhere_chalk") {
        RitualChalkBlock(ParticleTypes.PORTAL, Color(190, 55, 250).rgb, BlockBehaviour.Properties.of())
    }

    val GOLDEN_CHALK_BLOCK = BLOCKS.register("golden_chalk") {
        GoldenChalkBlock(BlockBehaviour.Properties.of())
    }


    val IRON_CANDELABRA = BLOCKS.register("iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val WHITE_IRON_CANDELABRA = BLOCKS.register("white_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val ORANGE_IRON_CANDELABRA = BLOCKS.register("orange_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val MAGENTA_IRON_CANDELABRA = BLOCKS.register("magenta_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val LIGHT_BLUE_IRON_CANDELABRA = BLOCKS.register("light_blue_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val YELLOW_IRON_CANDELABRA = BLOCKS.register("yellow_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val LIME_IRON_CANDELABRA = BLOCKS.register("lime_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val PINK_IRON_CANDELABRA = BLOCKS.register("pink_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val GRAY_IRON_CANDELABRA = BLOCKS.register("gray_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val LIGHT_GRAY_IRON_CANDELABRA = BLOCKS.register("light_gray_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val CYAN_IRON_CANDELABRA = BLOCKS.register("cyan_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val PURPLE_IRON_CANDELABRA = BLOCKS.register("purple_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val BLUE_IRON_CANDELABRA = BLOCKS.register("blue_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val BROWN_IRON_CANDELABRA = BLOCKS.register("brown_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val GREEN_IRON_CANDELABRA = BLOCKS.register("green_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val RED_IRON_CANDELABRA = BLOCKS.register("red_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val BLACK_IRON_CANDELABRA = BLOCKS.register("black_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val ARTHANA = BLOCKS.register("arthana") {
        ArthanaBlock(BlockBehaviour.Properties.of())
    }

    val CHALICE = BLOCKS.register("chalice") {
        ChaliceBlock(BlockBehaviour.Properties.of())
    }

    val PENTACLE = BLOCKS.register("pentacle") {
        Block(BlockBehaviour.Properties.of().noCollission().noOcclusion())
    }
}
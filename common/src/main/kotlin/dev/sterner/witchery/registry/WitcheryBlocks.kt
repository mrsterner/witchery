package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.Witchery.MODID
import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlock
import dev.sterner.witchery.block.*
import dev.sterner.witchery.block.altar.AltarBlock
import dev.sterner.witchery.block.altar.AltarBlockComponent
import dev.sterner.witchery.block.cauldron.CauldronBlock
import dev.sterner.witchery.block.cauldron.CauldronBlockComponent
import dev.sterner.witchery.block.distillery.DistilleryBlock
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
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.ButtonBlock
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.FenceBlock
import net.minecraft.world.level.block.FenceGateBlock
import net.minecraft.world.level.block.FlowerPotBlock
import net.minecraft.world.level.block.LeavesBlock
import net.minecraft.world.level.block.PressurePlateBlock
import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.block.SaplingBlock
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.TrapDoorBlock
import net.minecraft.world.level.block.grower.TreeGrower
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.BlockSetType
import net.minecraft.world.level.block.state.properties.WoodType
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import java.awt.Color


object WitcheryBlocks {

    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(Witchery.MODID, Registries.BLOCK)

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



    val STRIPPED_ROWAN_LOG = BLOCKS.register("stripped_rowan_log") {
        RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG))
    }

    val ROWAN_LOG = BLOCKS.register("rowan_log", StrippableHelper.createStrippableLog(STRIPPED_ROWAN_LOG,
        BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)))

    val STRIPPED_ROWAN_WOOD = BLOCKS.register("stripped_rowan_wood") {
        RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD))
    }

    val ROWAN_WOOD = BLOCKS.register("rowan_wood", StrippableHelper.createStrippableLog(STRIPPED_ROWAN_WOOD,
        BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)))

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
        StairBlock(ROWAN_PLANKS.orElseGet { Blocks.OAK_PLANKS }.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS))
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
        SaplingBlock(TreeGrower.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING))
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
        WitcheryCropBlock(
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
        WitcheryCropBlock(
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
        WitcheryCropBlock(
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
        WitcheryCropBlock(
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

    val DISTILLERY = BLOCKS.register("distillery") {
        DistilleryBlock(BlockBehaviour.Properties.of())
    }
}
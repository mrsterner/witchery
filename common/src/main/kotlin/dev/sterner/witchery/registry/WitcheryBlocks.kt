package dev.sterner.witchery.registry

import dev.architectury.core.block.ArchitecturyLiquidBlock
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery.MODID
import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlock
import dev.sterner.witchery.block.*
import dev.sterner.witchery.block.altar.AltarBlock
import dev.sterner.witchery.block.altar.AltarBlockComponent
import dev.sterner.witchery.block.arthana.ArthanaBlock
import dev.sterner.witchery.block.bear_trap.BearTrapBlock
import dev.sterner.witchery.block.blood_poppy.BloodPoppyBlock
import dev.sterner.witchery.block.brazier.BrazierBlock
import dev.sterner.witchery.block.cauldron.CauldronBlock
import dev.sterner.witchery.block.cauldron.CauldronBlockComponent
import dev.sterner.witchery.block.cauldron.CopperCauldronBlock
import dev.sterner.witchery.block.coffin.CoffinBlock
import dev.sterner.witchery.block.critter_snare.CritterSnareBlock
import dev.sterner.witchery.block.distillery.DistilleryBlock
import dev.sterner.witchery.block.distillery.DistilleryCompanionBlock
import dev.sterner.witchery.block.dream_weaver.DreamWeaverBlock
import dev.sterner.witchery.block.effigy.EffigyBlock
import dev.sterner.witchery.block.effigy.EffigyCompanionBlock
import dev.sterner.witchery.block.grassper.GrassperBlock
import dev.sterner.witchery.block.oven.*
import dev.sterner.witchery.block.poppet.PoppetBlock
import dev.sterner.witchery.block.ritual.GoldenChalkBlock
import dev.sterner.witchery.block.ritual.RitualChalkBlock
import dev.sterner.witchery.block.sacrificial_circle.SacrificialBlock
import dev.sterner.witchery.block.sacrificial_circle.SacrificialBlockComponent
import dev.sterner.witchery.block.signs.CustomCeilingHangingSignBlock
import dev.sterner.witchery.block.signs.CustomStandingSignBlock
import dev.sterner.witchery.block.signs.CustomWallHangingSignBlock
import dev.sterner.witchery.block.signs.CustomWallSignBlock
import dev.sterner.witchery.block.soul_cage.SoulCageBlock
import dev.sterner.witchery.block.spining_wheel.SpinningWheelBlock
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlock
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlockComponent
import dev.sterner.witchery.block.trees.StrippableLogBlock
import dev.sterner.witchery.block.blood_crucible.BloodCrucibleBlock
import dev.sterner.witchery.block.mushroom_log.MushroomLogBlock
import dev.sterner.witchery.block.mushroom_log.MushroomLogComponent
import dev.sterner.witchery.block.werewolf_altar.WerewolfAltarBlock
import dev.sterner.witchery.block.werewolf_altar.WerewolfAltarComponent
import dev.sterner.witchery.platform.PlatformUtils
import dev.sterner.witchery.platform.StrippableHelper
import dev.sterner.witchery.worldgen.tree.WitcheryTreeGrowers
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockSetType
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument
import net.minecraft.world.level.block.state.properties.WoodType
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import java.awt.Color
import java.util.function.Supplier


object WitcheryBlocks {

    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(MODID, Registries.BLOCK)

    val LANG_HELPER = mutableListOf<String>()

    fun <T : Block> register(name: String, addLang: Boolean = true, item: Supplier<T>): RegistrySupplier<T> {
        if (addLang) {
            LANG_HELPER.add(name)
        }
        return BLOCKS.register(name, item)
    }
    
    val COMPONENT: RegistrySupplier<MultiBlockComponentBlock> = register("component") {
        MultiBlockComponentBlock(BlockBehaviour.Properties.of())
    }

    val DEEPLSTAE_ALTAR_BLOCK: RegistrySupplier<AltarCreationBlock> = register("deepslate_altar_block") {
        AltarCreationBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE))
    }

    val ALTAR: RegistrySupplier<AltarBlock> = register("altar") {
        AltarBlock(
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.DEEPSLATE)
        )
    }

    val ALTAR_COMPONENT: RegistrySupplier<AltarBlockComponent> = register("altar_component") {
        AltarBlockComponent(
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.DEEPSLATE)
        )
    }

    val MUSHROOM_LOG: RegistrySupplier<MushroomLogBlock> = register("mushroom_log") {
        MushroomLogBlock(
            BlockBehaviour.Properties.of().noOcclusion().sound(SoundType.WOOD)
        )
    }

    val MUSHROOM_LOG_COMPONENT: RegistrySupplier<MushroomLogComponent> = register("mushroom_log_component") {
        MushroomLogComponent(
            BlockBehaviour.Properties.of().noOcclusion().sound(SoundType.WOOD)
        )
    }

    val CAULDRON_DUMMY: RegistrySupplier<Block> = register("cauldron_no_logs") {
        Block(BlockBehaviour.Properties.of())
    }

    val CAULDRON: RegistrySupplier<CauldronBlock> = register("cauldron") {
        CauldronBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .sound(SoundType.METAL)
        )
    }

    val CAULDRON_COMPONENT: RegistrySupplier<CauldronBlockComponent> = register("cauldron_component") {
        CauldronBlockComponent(
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .sound(SoundType.METAL)
        )
    }

    val COPPER_CAULDRON: RegistrySupplier<CauldronBlock> = register("copper_cauldron") {
        CopperCauldronBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                .sound(SoundType.METAL).randomTicks()
        )
    }

    val EXPOSED_COPPER_CAULDRON: RegistrySupplier<CauldronBlock> = register("exposed_copper_cauldron") {
        CopperCauldronBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                .sound(SoundType.METAL).randomTicks()
        )
    }

    val WEATHERED_COPPER_CAULDRON: RegistrySupplier<CauldronBlock> = register("weathered_copper_cauldron") {
        CopperCauldronBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                .sound(SoundType.METAL).randomTicks()
        )
    }

    val OXIDIZED_COPPER_CAULDRON: RegistrySupplier<CauldronBlock> = register("oxidized_copper_cauldron") {
        CopperCauldronBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                .sound(SoundType.METAL).randomTicks()
        )
    }

    val WAXED_COPPER_CAULDRON: RegistrySupplier<CauldronBlock> = register("waxed_copper_cauldron") {
        CauldronBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                .sound(SoundType.METAL)
        )
    }

    val WAXED_EXPOSED_COPPER_CAULDRON: RegistrySupplier<CauldronBlock> =
        register("waxed_exposed_copper_cauldron") {
            CauldronBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL)
            )
        }

    val WAXED_WEATHERED_COPPER_CAULDRON: RegistrySupplier<CauldronBlock> =
        register("waxed_weathered_copper_cauldron") {
            CauldronBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL)
            )
        }

    val WAXED_OXIDIZED_COPPER_CAULDRON: RegistrySupplier<CauldronBlock> =
        register("waxed_oxidized_copper_cauldron") {
            CauldronBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL)
            )
        }

    val DISTILLERY = register("distillery") {
        DistilleryBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK))
    }

    val DISTILLERY_COMPONENT: RegistrySupplier<DistilleryCompanionBlock> = register("distillery_component") {
        DistilleryCompanionBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                .sound(SoundType.METAL)
        )
    }

    val COFFIN = register("coffin") {
        CoffinBlock(BlockBehaviour.Properties.of(), DyeColor.BLACK)
    }

    val WEREWOLF_ALTAR = register("werewolf_altar") {
        WerewolfAltarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE))
    }

    val WEREWOLF_ALTAR_COMPONENT: RegistrySupplier<WerewolfAltarComponent> =
        register("werewolf_altar_component") {
            WerewolfAltarComponent(
                BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .sound(SoundType.STONE)
            )
        }

    val IRON_WITCHES_OVEN: RegistrySupplier<OvenBlock> = register("iron_witches_oven") {
        OvenBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .sound(SoundType.METAL)
        )
    }

    val IRON_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        register("iron_witches_oven_fume_extension") {
            OvenFumeExtensionBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .sound(SoundType.METAL)
            )
        }

    val COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        register("copper_witches_oven_fume_extension") {
            CopperOvenFumeExtensionBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL).randomTicks()
            )
        }

    val WAXED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        register("waxed_copper_witches_oven_fume_extension") {
            OvenFumeExtensionBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL)
            )
        }

    val EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        register("exposed_copper_witches_oven_fume_extension") {
            CopperOvenFumeExtensionBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL).randomTicks()
            )
        }

    val WAXED_EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        register("waxed_exposed_copper_witches_oven_fume_extension") {
            OvenFumeExtensionBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL)
            )
        }

    val WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        register("weathered_copper_witches_oven_fume_extension") {
            CopperOvenFumeExtensionBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL).randomTicks()
            )
        }

    val WAXED_WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        register("waxed_weathered_copper_witches_oven_fume_extension") {
            OvenFumeExtensionBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL)
            )
        }

    val OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        register("oxidized_copper_witches_oven_fume_extension") {
            CopperOvenFumeExtensionBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL).randomTicks()
            )
        }

    val WAXED_OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<OvenFumeExtensionBlock> =
        register("waxed_oxidized_copper_witches_oven_fume_extension") {
            OvenFumeExtensionBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL)
            )
        }

    val IRON_WITCHES_OVEN_FUME_EXTENSION_COMPONENT: RegistrySupplier<OvenFumeExtensionBlockComponent> =
        register("iron_witches_oven_fume_extension_component") {
            OvenFumeExtensionBlockComponent(
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .sound(SoundType.METAL)
            )
        }

    val COPPER_WITCHES_OVEN: RegistrySupplier<CopperOvenBlock> = register("copper_witches_oven") {
        CopperOvenBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                .sound(SoundType.METAL).randomTicks()
        )
    }

    val EXPOSED_COPPER_WITCHES_OVEN: RegistrySupplier<CopperOvenBlock> =
        register("exposed_copper_witches_oven") {
            CopperOvenBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL).randomTicks()
            )
        }

    val WEATHERED_COPPER_WITCHES_OVEN: RegistrySupplier<CopperOvenBlock> =
        register("weathered_copper_witches_oven") {
            CopperOvenBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL).randomTicks()
            )
        }

    val OXIDIZED_COPPER_WITCHES_OVEN: RegistrySupplier<CopperOvenBlock> =
        register("oxidized_copper_witches_oven") {
            CopperOvenBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL).randomTicks()
            )
        }

    val WAXED_COPPER_WITCHES_OVEN: RegistrySupplier<OvenBlock> = register("waxed_copper_witches_oven") {
        OvenBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                .sound(SoundType.METAL)
        )
    }

    val WAXED_EXPOSED_COPPER_WITCHES_OVEN: RegistrySupplier<OvenBlock> =
        register("waxed_exposed_copper_witches_oven") {
            OvenBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL)
            )
        }

    val WAXED_WEATHERED_COPPER_WITCHES_OVEN: RegistrySupplier<OvenBlock> =
        register("waxed_weathered_copper_witches_oven") {
            OvenBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL)
            )
        }

    val WAXED_OXIDIZED_COPPER_WITCHES_OVEN: RegistrySupplier<OvenBlock> =
        register("waxed_oxidized_copper_witches_oven") {
            OvenBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .sound(SoundType.METAL)
            )
        }

    val DEMON_HEART: RegistrySupplier<DemonHeartBlock> = register("demon_heart") {
        DemonHeartBlock(BlockBehaviour.Properties.of())
    }

    val INFINITY_EGG: RegistrySupplier<InfinityEggBlock> = register("infinity_egg") {
        InfinityEggBlock(BlockBehaviour.Properties.of())
    }

    val BEAR_TRAP: RegistrySupplier<Block> = register("bear_trap") {
        BearTrapBlock(BlockBehaviour.Properties.of().noOcclusion())
    }

    val STRIPPED_ROWAN_LOG: RegistrySupplier<RotatedPillarBlock> = register("stripped_rowan_log") {
        RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG))
    }

    val ROWAN_LOG: RegistrySupplier<out StrippableLogBlock> = register(
        "rowan_log", true, StrippableHelper.createStrippableLog(
            STRIPPED_ROWAN_LOG,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
        )
    )

    val STRIPPED_ROWAN_WOOD: RegistrySupplier<RotatedPillarBlock> = register("stripped_rowan_wood") {
        RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD))
    }

    val ROWAN_WOOD: RegistrySupplier<out StrippableLogBlock> = register(
        "rowan_wood", true, StrippableHelper.createStrippableLog(
            STRIPPED_ROWAN_WOOD,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)
        )
    )

    val ROWAN_LEAVES: RegistrySupplier<LeavesBlock> = register("rowan_leaves") {
        LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AZALEA_LEAVES))
    }

    val ROWAN_BERRY_LEAVES: RegistrySupplier<LeavesBlock> = register("rowan_berry_leaves") {
        LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWERING_AZALEA_LEAVES))
    }

    val ROWAN_PLANKS: RegistrySupplier<Block> = register("rowan_planks") {
        Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
    }

    val ROWAN_STAIRS: RegistrySupplier<StairBlock> = register("rowan_stairs") {
        StairBlock(
            ROWAN_PLANKS.orElseGet { Blocks.OAK_PLANKS }.defaultBlockState(),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)
        )
    }

    val ROWAN_SLAB: RegistrySupplier<SlabBlock> = register("rowan_slab") {
        SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB))
    }

    val ROWAN_FENCE: RegistrySupplier<FenceBlock> = register("rowan_fence") {
        FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE))
    }

    private val ROWAN_WOOD_TYPE: WoodType = PlatformUtils.registerWoodType(WoodType("$MODID:rowan", BlockSetType.OAK))

    val ROWAN_FENCE_GATE: RegistrySupplier<FenceGateBlock> = register("rowan_fence_gate") {
        FenceGateBlock(ROWAN_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE))
    }

    val ROWAN_DOOR: RegistrySupplier<DoorBlock> = register("rowan_door") {
        DoorBlock(ROWAN_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR))
    }

    val ROWAN_TRAPDOOR: RegistrySupplier<TrapDoorBlock> = register("rowan_trapdoor") {
        TrapDoorBlock(ROWAN_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR))
    }

    val ROWAN_PRESSURE_PLATE: RegistrySupplier<PressurePlateBlock> = register("rowan_pressure_plate") {
        PressurePlateBlock(ROWAN_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE))
    }

    val ROWAN_BUTTON: RegistrySupplier<ButtonBlock> = register("rowan_button") {
        ButtonBlock(ROWAN_WOOD_TYPE.setType, 30, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON))
    }

    val ROWAN_SAPLING: RegistrySupplier<SaplingBlock> = register("rowan_sapling") {
        SaplingBlock(WitcheryTreeGrowers.ROWAN, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING))
    }

    val POTTED_ROWAN_SAPLING: RegistrySupplier<FlowerPotBlock> = register("potted_rowan_sapling") {
        FlowerPotBlock(ROWAN_SAPLING.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING))
    }

    val ROWAN_SIGN: RegistrySupplier<CustomStandingSignBlock> = register("rowan_sign") {
        CustomStandingSignBlock(
            ROWAN_WOOD_TYPE,
            BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(
                NoteBlockInstrument.BASS
            ).noCollission().strength(1.0f).ignitedByLava()
        )
    }

    val ROWAN_WALL_SIGN: RegistrySupplier<CustomWallSignBlock> = register("rowan_wall_sign") {
        CustomWallSignBlock(
            ROWAN_WOOD_TYPE, BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .forceSolidOn()
                .instrument(NoteBlockInstrument.BASS)
                .noCollission()
                .strength(1.0f)
                .ignitedByLava()
        )
    }

    val ROWAN_HANGING_SIGN: RegistrySupplier<CustomCeilingHangingSignBlock> = register("rowan_hanging_sign") {
        CustomCeilingHangingSignBlock(
            ROWAN_WOOD_TYPE, BlockBehaviour.Properties.of()
                .mapColor(Blocks.OAK_LOG.defaultMapColor())
                .forceSolidOn()
                .instrument(NoteBlockInstrument.BASS)
                .noCollission()
                .strength(1.0f)
                .ignitedByLava()
        )
    }

    val ROWAN_WALL_HANGING_SIGN: RegistrySupplier<CustomWallHangingSignBlock> =
        register("rowan_wall_hanging_sign") {
            CustomWallHangingSignBlock(
                ROWAN_WOOD_TYPE, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .forceSolidOn()
                    .instrument(NoteBlockInstrument.BASS)
                    .noCollission()
                    .strength(1.0f)
                    .ignitedByLava()
            )
        }

    val STRIPPED_ALDER_LOG: RegistrySupplier<RotatedPillarBlock> = register("stripped_alder_log") {
        RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG))
    }

    val ALDER_LOG: RegistrySupplier<out StrippableLogBlock> = register(
        "alder_log", true, StrippableHelper.createStrippableLog(
            STRIPPED_ALDER_LOG,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
        )
    )

    val STRIPPED_ALDER_WOOD: RegistrySupplier<RotatedPillarBlock> = register("stripped_alder_wood") {
        RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD))
    }

    val ALDER_WOOD: RegistrySupplier<out StrippableLogBlock> = register(
        "alder_wood", true, StrippableHelper.createStrippableLog(
            STRIPPED_ALDER_WOOD,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)
        )
    )

    val ALDER_LEAVES: RegistrySupplier<LeavesBlock> = register("alder_leaves") {
        LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AZALEA_LEAVES))
    }

    val ALDER_PLANKS: RegistrySupplier<Block> = register("alder_planks") {
        Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
    }

    val ALDER_STAIRS: RegistrySupplier<StairBlock> = register("alder_stairs") {
        StairBlock(
            ALDER_PLANKS.orElseGet { Blocks.OAK_PLANKS }.defaultBlockState(),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)
        )
    }

    val ALDER_SLAB: RegistrySupplier<SlabBlock> = register("alder_slab") {
        SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB))
    }

    val ALDER_FENCE: RegistrySupplier<FenceBlock> = register("alder_fence") {
        FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE))
    }

    private val ALDER_WOOD_TYPE: WoodType = PlatformUtils.registerWoodType(WoodType("$MODID:alder", BlockSetType.OAK))


    val ALDER_FENCE_GATE: RegistrySupplier<FenceGateBlock> = register("alder_fence_gate") {
        FenceGateBlock(ALDER_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE))
    }

    val ALDER_DOOR: RegistrySupplier<DoorBlock> = register("alder_door") {
        DoorBlock(ALDER_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR))
    }

    val ALDER_TRAPDOOR: RegistrySupplier<TrapDoorBlock> = register("alder_trapdoor") {
        TrapDoorBlock(ALDER_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR))
    }

    val ALDER_PRESSURE_PLATE: RegistrySupplier<PressurePlateBlock> = register("alder_pressure_plate") {
        PressurePlateBlock(ALDER_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE))
    }

    val ALDER_BUTTON: RegistrySupplier<ButtonBlock> = register("alder_button") {
        ButtonBlock(ALDER_WOOD_TYPE.setType, 30, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON))
    }

    val ALDER_SAPLING: RegistrySupplier<SaplingBlock> = register("alder_sapling") {
        SaplingBlock(WitcheryTreeGrowers.ALDER, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING))
    }

    val POTTED_ALDER_SAPLING: RegistrySupplier<FlowerPotBlock> = register("potted_alder_sapling") {
        FlowerPotBlock(ALDER_SAPLING.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING))
    }

    val ALDER_SIGN: RegistrySupplier<CustomStandingSignBlock> = register("alder_sign") {
        CustomStandingSignBlock(
            ALDER_WOOD_TYPE,
            BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(
                NoteBlockInstrument.BASS
            ).noCollission().strength(1.0f).ignitedByLava()
        )
    }

    val ALDER_WALL_SIGN: RegistrySupplier<CustomWallSignBlock> = register("alder_wall_sign") {
        CustomWallSignBlock(
            ALDER_WOOD_TYPE,
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .forceSolidOn()
                .instrument(NoteBlockInstrument.BASS)
                .noCollission()
                .strength(1.0f)
                .ignitedByLava()
        )
    }

    val ALDER_HANGING_SIGN: RegistrySupplier<CustomCeilingHangingSignBlock> = register("alder_hanging_sign") {
        CustomCeilingHangingSignBlock(
            ALDER_WOOD_TYPE, BlockBehaviour.Properties.of()
                .mapColor(Blocks.OAK_LOG.defaultMapColor())
                .forceSolidOn()
                .instrument(NoteBlockInstrument.BASS)
                .noCollission()
                .strength(1.0f)
                .ignitedByLava()
        )
    }

    val ALDER_WALL_HANGING_SIGN: RegistrySupplier<CustomWallHangingSignBlock> =
        register("alder_wall_hanging_sign") {
            CustomWallHangingSignBlock(
                ALDER_WOOD_TYPE, BlockBehaviour.Properties.of()
                    .mapColor(Blocks.OAK_LOG.defaultMapColor())
                    .forceSolidOn()
                    .instrument(NoteBlockInstrument.BASS)
                    .noCollission()
                    .strength(1.0f)
                    .ignitedByLava()
            )
        }

    val STRIPPED_HAWTHORN_LOG: RegistrySupplier<RotatedPillarBlock> = register("stripped_hawthorn_log") {
        RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG))
    }

    val HAWTHORN_LOG: RegistrySupplier<out StrippableLogBlock> = register(
        "hawthorn_log", true, StrippableHelper.createStrippableLog(
            STRIPPED_HAWTHORN_LOG,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
        )
    )

    val STRIPPED_HAWTHORN_WOOD: RegistrySupplier<RotatedPillarBlock> = register("stripped_hawthorn_wood") {
        RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD))
    }

    val HAWTHORN_WOOD: RegistrySupplier<out StrippableLogBlock> = register(
        "hawthorn_wood", true, StrippableHelper.createStrippableLog(
            STRIPPED_HAWTHORN_WOOD,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)
        )
    )

    val HAWTHORN_LEAVES: RegistrySupplier<LeavesBlock> = register("hawthorn_leaves") {
        LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AZALEA_LEAVES))
    }

    val HAWTHORN_PLANKS: RegistrySupplier<Block> = register("hawthorn_planks") {
        Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
    }

    val HAWTHORN_STAIRS: RegistrySupplier<StairBlock> = register("hawthorn_stairs") {
        StairBlock(
            HAWTHORN_PLANKS.orElseGet { Blocks.OAK_PLANKS }.defaultBlockState(),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)
        )
    }

    val HAWTHORN_SLAB: RegistrySupplier<SlabBlock> = register("hawthorn_slab") {
        SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB))
    }

    val HAWTHORN_FENCE: RegistrySupplier<FenceBlock> = register("hawthorn_fence") {
        FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE))
    }

    private val HAWTHORN_WOOD_TYPE: WoodType =
        PlatformUtils.registerWoodType(WoodType("$MODID:hawthorn", BlockSetType.OAK))

    val HAWTHORN_FENCE_GATE: RegistrySupplier<FenceGateBlock> = register("hawthorn_fence_gate") {
        FenceGateBlock(HAWTHORN_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE))
    }

    val HAWTHORN_DOOR: RegistrySupplier<DoorBlock> = register("hawthorn_door") {
        DoorBlock(HAWTHORN_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR))
    }

    val HAWTHORN_TRAPDOOR: RegistrySupplier<TrapDoorBlock> = register("hawthorn_trapdoor") {
        TrapDoorBlock(HAWTHORN_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR))
    }

    val HAWTHORN_PRESSURE_PLATE: RegistrySupplier<PressurePlateBlock> = register("hawthorn_pressure_plate") {
        PressurePlateBlock(HAWTHORN_WOOD_TYPE.setType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE))
    }

    val HAWTHORN_BUTTON: RegistrySupplier<ButtonBlock> = register("hawthorn_button") {
        ButtonBlock(HAWTHORN_WOOD_TYPE.setType, 30, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON))
    }

    val HAWTHORN_SAPLING: RegistrySupplier<SaplingBlock> = register("hawthorn_sapling") {
        SaplingBlock(WitcheryTreeGrowers.HAWTHORN, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING))
    }

    val POTTED_HAWTHORN_SAPLING: RegistrySupplier<FlowerPotBlock> = register("potted_hawthorn_sapling") {
        FlowerPotBlock(HAWTHORN_SAPLING.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING))
    }

    val HAWTHORN_SIGN: RegistrySupplier<CustomStandingSignBlock> = register("hawthorn_sign") {
        CustomStandingSignBlock(
            HAWTHORN_WOOD_TYPE,
            BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(
                NoteBlockInstrument.BASS
            ).noCollission().strength(1.0f).ignitedByLava()
        )
    }

    val HAWTHORN_WALL_SIGN: RegistrySupplier<CustomWallSignBlock> = register("hawthorn_wall_sign") {
        CustomWallSignBlock(
            HAWTHORN_WOOD_TYPE, BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .forceSolidOn()
                .instrument(NoteBlockInstrument.BASS)
                .noCollission()
                .strength(1.0f)
                .ignitedByLava()
        )
    }

    val HAWTHORN_HANGING_SIGN: RegistrySupplier<CustomCeilingHangingSignBlock> =
        register("hawthorn_hanging_sign") {
            CustomCeilingHangingSignBlock(
                HAWTHORN_WOOD_TYPE, BlockBehaviour.Properties.of()
                    .mapColor(Blocks.OAK_LOG.defaultMapColor())
                    .forceSolidOn()
                    .instrument(NoteBlockInstrument.BASS)
                    .noCollission()
                    .strength(1.0f)
                    .ignitedByLava()
            )
        }

    val HAWTHORN_WALL_HANGING_SIGN: RegistrySupplier<CustomWallHangingSignBlock> =
        register("hawthorn_wall_hanging_sign") {
            CustomWallHangingSignBlock(
                HAWTHORN_WOOD_TYPE,
                BlockBehaviour.Properties.of()
                    .mapColor(Blocks.OAK_LOG.defaultMapColor())
                    .forceSolidOn()
                    .instrument(NoteBlockInstrument.BASS)
                    .noCollission()
                    .strength(1.0f)
                    .ignitedByLava()
            )
        }

    val GLINTWEED: RegistrySupplier<GlintweedBlock> = register("glintweed") {
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

    val EMBER_MOSS: RegistrySupplier<EmbermossBlock> = register("ember_moss") {
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

    val SPANISH_MOSS: RegistrySupplier<SpanishMossBlock> = register("spanish_moss") {
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

    val MANDRAKE_CROP: RegistrySupplier<MandrakeCropBlock> = register("mandrake") {
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

    val BELLADONNA_CROP: RegistrySupplier<BelladonnaCropBlock> = register("belladonna") {
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

    val SNOWBELL_CROP: RegistrySupplier<SnowbellCropBlock> = register("snowbell") {
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

    val WATER_ARTICHOKE_CROP: RegistrySupplier<WaterArtichokeCropBlock> = register("water_artichoke") {
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

    val WOLFSFBANE_CROP: RegistrySupplier<WolfsbaneCropBlock> = register("wolfsbane") {
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

    val GARLIC_CROP: RegistrySupplier<GarlicCropBlock> = register("garlic") {
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

    val WORMWOOD_CROP: RegistrySupplier<WormwoodCropBlock> = register("wormwood") {
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

    val BLOOD_POPPY: RegistrySupplier<BloodPoppyBlock> = register("blood_poppy") {
        BloodPoppyBlock(MobEffects.NIGHT_VISION, 5.0f, BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    }

    val RITUAL_CHALK_BLOCK: RegistrySupplier<RitualChalkBlock> = register("ritual_chalk") {
        RitualChalkBlock(null, 0xFFFFFF, BlockBehaviour.Properties.of())
    }

    val INFERNAL_CHALK_BLOCK: RegistrySupplier<RitualChalkBlock> = register("infernal_chalk") {
        RitualChalkBlock(ParticleTypes.FLAME, Color(230, 0, 75).rgb, BlockBehaviour.Properties.of())
    }

    val OTHERWHERE_CHALK_BLOCK: RegistrySupplier<RitualChalkBlock> = register("otherwhere_chalk") {
        RitualChalkBlock(ParticleTypes.PORTAL, Color(190, 55, 250).rgb, BlockBehaviour.Properties.of())
    }

    val GOLDEN_CHALK_BLOCK: RegistrySupplier<GoldenChalkBlock> = register("golden_chalk") {
        GoldenChalkBlock(BlockBehaviour.Properties.of())
    }

    val IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val WHITE_IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("white_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val ORANGE_IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("orange_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val MAGENTA_IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("magenta_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val LIGHT_BLUE_IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("light_blue_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val YELLOW_IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("yellow_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val LIME_IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("lime_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val PINK_IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("pink_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val GRAY_IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("gray_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val LIGHT_GRAY_IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("light_gray_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val CYAN_IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("cyan_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val PURPLE_IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("purple_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val BLUE_IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("blue_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val BROWN_IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("brown_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val GREEN_IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("green_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val RED_IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("red_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val BLACK_IRON_CANDELABRA: RegistrySupplier<CandelabraBlock> = register("black_iron_candelabra") {
        CandelabraBlock(BlockBehaviour.Properties.of())
    }

    val ARTHANA: RegistrySupplier<ArthanaBlock> = register("arthana") {
        ArthanaBlock(BlockBehaviour.Properties.of())
    }

    val CHALICE: RegistrySupplier<ChaliceBlock> = register("chalice") {
        ChaliceBlock(BlockBehaviour.Properties.of())
    }

    val PENTACLE: RegistrySupplier<PentacleBlock> = register("pentacle") {
        PentacleBlock(BlockBehaviour.Properties.of().noCollission().noOcclusion())
    }

    val SPINNING_WHEEL: RegistrySupplier<SpinningWheelBlock> = register("spinning_wheel") {
        SpinningWheelBlock(BlockBehaviour.Properties.of().noOcclusion())
    }

    val DREAM_WEAVER: RegistrySupplier<DreamWeaverBlock> = register("dream_weaver") {
        DreamWeaverBlock(BlockBehaviour.Properties.of())
    }

    val DREAM_WEAVER_OF_FLEET_FOOT: RegistrySupplier<DreamWeaverBlock> = register("dream_weaver_of_fleet_foot") {
        DreamWeaverBlock(BlockBehaviour.Properties.of())
    }

    val DREAM_WEAVER_OF_NIGHTMARES: RegistrySupplier<DreamWeaverBlock> = register("dream_weaver_of_nightmares") {
        DreamWeaverBlock(BlockBehaviour.Properties.of())
    }

    val DREAM_WEAVER_OF_INTENSITY: RegistrySupplier<DreamWeaverBlock> = register("dream_weaver_of_intensity") {
        DreamWeaverBlock(BlockBehaviour.Properties.of())
    }

    val DREAM_WEAVER_OF_FASTING: RegistrySupplier<DreamWeaverBlock> = register("dream_weaver_of_fasting") {
        DreamWeaverBlock(BlockBehaviour.Properties.of())
    }

    val DREAM_WEAVER_OF_IRON_ARM: RegistrySupplier<DreamWeaverBlock> = register("dream_weaver_of_iron_arm") {
        DreamWeaverBlock(BlockBehaviour.Properties.of())
    }

    val BLOOD_CRUCIBLE: RegistrySupplier<BloodCrucibleBlock> = register("blood_crucible") {
        BloodCrucibleBlock(BlockBehaviour.Properties.of())
    }

    val WISPY_COTTON: RegistrySupplier<CottonBlock> = register("wispy_cotton") {
        CottonBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .pushReaction(PushReaction.DESTROY)
        )
    }

    val DISTURBED_COTTON: RegistrySupplier<CottonBlock> = register("disturbed_cotton") {
        CottonBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .pushReaction(PushReaction.DESTROY)
        )
    }

    val POPPET: RegistrySupplier<PoppetBlock> = register("poppet") {
        PoppetBlock(BlockBehaviour.Properties.of().noOcclusion())
    }

    val SPIRIT_PORTAL: RegistrySupplier<SpiritPortalBlock> = register("spirit_portal") {
        SpiritPortalBlock(BlockBehaviour.Properties.of().noOcclusion())
    }

    val SPIRIT_PORTAL_COMPONENT: RegistrySupplier<SpiritPortalBlockComponent> = register("spirit_component") {
        SpiritPortalBlockComponent(
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.DEEPSLATE)
        )
    }

    val BRAZIER: RegistrySupplier<BrazierBlock> = register("brazier") {
        BrazierBlock(BlockBehaviour.Properties.of().noOcclusion())
    }

    val SOUL_CAGE: RegistrySupplier<SoulCageBlock> = register("soul_cage") {
        SoulCageBlock(BlockBehaviour.Properties.of().noOcclusion())
    }

    val FLOWING_SPIRIT_BLOCK: RegistrySupplier<LiquidBlock> = register(
        "flowing_spirit_block"
    ) {
        object : ArchitecturyLiquidBlock(
            WitcheryFluids.FLOWING_SPIRIT_STILL,
            Properties.ofFullCopy(Blocks.WATER)
        ) {
            override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
                if (entity is LivingEntity) {
                    entity.addEffect(MobEffectInstance(MobEffects.REGENERATION, 20 * 2, 0))
                }
                super.entityInside(state, level, pos, entity)
            }
        }
    }

    val GRAVESTONE: RegistrySupplier<GravestoneBlock> = register("gravestone") {
        GravestoneBlock(BlockBehaviour.Properties.of())
    }

    val SUSPICIOUS_GRAVEYARD_DIRT: RegistrySupplier<SuspiciousGraveyardDirtBlock> =
        register("suspicious_graveyard_dirt") {
            SuspiciousGraveyardDirtBlock(
                Blocks.COARSE_DIRT,
                SoundEvents.BRUSH_GRAVEL,
                SoundEvents.BRUSH_GRAVEL_COMPLETED,
                BlockBehaviour.Properties.of()
                    .sound(SoundType.SOUL_SAND)
            )
        }

    val SACRIFICIAL_CIRCLE: RegistrySupplier<SacrificialBlock> = register("sacrificial_circle") {
        SacrificialBlock(BlockBehaviour.Properties.of())
    }

    val SACRIFICIAL_CIRCLE_COMPONENT: RegistrySupplier<SacrificialBlockComponent> =
        register("sacrificial_circle_component") {
            SacrificialBlockComponent(
                BlockBehaviour.Properties.of()
            )
        }

    val SUNLIGHT_COLLECTOR: RegistrySupplier<SunCollectorBlock> = register("sunlight_collector") {
        SunCollectorBlock(BlockBehaviour.Properties.of().randomTicks().noOcclusion())
    }

    val BLOOD_STAINED_WOOL: RegistrySupplier<Block> = register("blood_stained_wool") {
        Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL))
    }

    val GRASSPER: RegistrySupplier<GrassperBlock> = register("grassper") {
        GrassperBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noOcclusion()
                .noCollission()
                .instabreak()
                .sound(SoundType.CROP)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY)
        )
    }

    val CRITTER_SNARE: RegistrySupplier<CritterSnareBlock> = register("critter_snare") {
        CritterSnareBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noOcclusion()
                .noCollission()
                .instabreak()
                .sound(SoundType.CROP)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY)
        )
    }

    val WITCHS_LADDER: RegistrySupplier<EffigyBlock> = register("witches_ladder") {
        EffigyBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0f, 3.0f)
                .sound(
                    SoundType.WOOD
                )
                .ignitedByLava()
                .noOcclusion()
        )
    }

    val CLAY_EFFIGY: RegistrySupplier<EffigyBlock> = register("clay_effigy") {
        EffigyBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.CLAY)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0f, 3.0f)
                .sound(
                    SoundType.WOOD
                )
                .noOcclusion()
        )
    }

    val SCARECROW: RegistrySupplier<EffigyBlock> = register("scarecrow") {
        EffigyBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0f, 3.0f)
                .sound(
                    SoundType.WOOD
                )
                .ignitedByLava()
                .noOcclusion()
        )
    }

    val EFFIGY_COMPONENT: RegistrySupplier<EffigyCompanionBlock> = register("effigy_component") {
        EffigyCompanionBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0f, 3.0f)
                .sound(
                    SoundType.WOOD
                ).ignitedByLava()
                .noOcclusion()
        )
    }
}
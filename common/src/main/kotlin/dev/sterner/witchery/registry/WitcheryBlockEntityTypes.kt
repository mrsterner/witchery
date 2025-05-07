package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlockEntity
import dev.sterner.witchery.block.SuspiciousGraveyardDirtBlockEntity
import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.block.arthana.ArthanaBlockEntity
import dev.sterner.witchery.block.bear_trap.BearTrapBlockEntity
import dev.sterner.witchery.block.blood_poppy.BloodPoppyBlockEntity
import dev.sterner.witchery.block.brazier.BrazierBlockEntity
import dev.sterner.witchery.block.cauldron.CauldronBlockEntity
import dev.sterner.witchery.block.coffin.CoffinBlockEntity
import dev.sterner.witchery.block.critter_snare.CritterSnareBlockEntity
import dev.sterner.witchery.block.distillery.DistilleryBlockEntity
import dev.sterner.witchery.block.dream_weaver.DreamWeaverBlockEntity
import dev.sterner.witchery.block.effigy.EffigyBlockEntity
import dev.sterner.witchery.block.grassper.GrassperBlockEntity
import dev.sterner.witchery.block.oven.OvenBlockEntity
import dev.sterner.witchery.block.oven.OvenFumeExtensionBlockEntity
import dev.sterner.witchery.block.poppet.PoppetBlockEntity
import dev.sterner.witchery.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.block.sacrificial_circle.SacrificialBlockEntity
import dev.sterner.witchery.block.signs.CustomHangingSignBE
import dev.sterner.witchery.block.signs.CustomSignBE
import dev.sterner.witchery.block.spining_wheel.SpinningWheelBlockEntity
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlockEntity
import dev.sterner.witchery.block.werewolf_altar.WerewolfAltarBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.HangingSignBlockEntity
import net.minecraft.world.level.block.entity.SignBlockEntity
import net.minecraft.world.level.block.state.BlockState

object WitcheryBlockEntityTypes {

    val BLOCK_ENTITY_TYPES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(Witchery.MODID, Registries.BLOCK_ENTITY_TYPE)

    val MULTI_BLOCK_COMPONENT: RegistrySupplier<BlockEntityType<MultiBlockComponentBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("multi_block_component") {
            BlockEntityType.Builder.of(
                { pos, state -> MultiBlockComponentBlockEntity(pos, state) },
                WitcheryBlocks.COMPONENT.get(),
                WitcheryBlocks.ALTAR_COMPONENT.get(),
                WitcheryBlocks.SACRIFICIAL_CIRCLE_COMPONENT.get(),
                WitcheryBlocks.SPIRIT_PORTAL_COMPONENT.get(),
                WitcheryBlocks.CAULDRON_COMPONENT.get(),
                WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION_COMPONENT.get(),
                WitcheryBlocks.DISTILLERY_COMPONENT.get(),
                WitcheryBlocks.WEREWOLF_ALTAR_COMPONENT.get(),
                WitcheryBlocks.EFFIGY_COMPONENT.get()
            )
                .build(null)
        }

    val ALTAR: RegistrySupplier<BlockEntityType<AltarBlockEntity>> = BLOCK_ENTITY_TYPES.register("altar") {
        BlockEntityType.Builder.of(
            { pos, state -> AltarBlockEntity(pos, state) },
            WitcheryBlocks.ALTAR.get(),
        )
            .build(null)
    }

    val BEAR_TRAP: RegistrySupplier<BlockEntityType<BearTrapBlockEntity>> = BLOCK_ENTITY_TYPES.register("bear_trap") {
        BlockEntityType.Builder.of(
            { pos, state -> BearTrapBlockEntity(pos, state) },
            WitcheryBlocks.BEAR_TRAP.get(),
        )
            .build(null)
    }


    val CAULDRON: RegistrySupplier<BlockEntityType<CauldronBlockEntity>> = BLOCK_ENTITY_TYPES.register("cauldron") {
        BlockEntityType.Builder.of(
            { pos, state -> CauldronBlockEntity(pos, state) },
            WitcheryBlocks.CAULDRON.get(),
            WitcheryBlocks.COPPER_CAULDRON.get(),
            WitcheryBlocks.EXPOSED_COPPER_CAULDRON.get(),
            WitcheryBlocks.WEATHERED_COPPER_CAULDRON.get(),
            WitcheryBlocks.OXIDIZED_COPPER_CAULDRON.get(),
            WitcheryBlocks.WAXED_COPPER_CAULDRON.get(),
            WitcheryBlocks.WAXED_EXPOSED_COPPER_CAULDRON.get(),
            WitcheryBlocks.WAXED_WEATHERED_COPPER_CAULDRON.get(),
            WitcheryBlocks.WAXED_OXIDIZED_COPPER_CAULDRON.get(),
        )
            .build(null)
    }

    val OVEN: RegistrySupplier<BlockEntityType<OvenBlockEntity>> = BLOCK_ENTITY_TYPES.register("oven") {
        BlockEntityType.Builder.of(
            { pos, state -> OvenBlockEntity(pos, state) },
            WitcheryBlocks.IRON_WITCHES_OVEN.get(),
            WitcheryBlocks.COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN.get(),
        )
            .build(null)
    }

    val OVEN_FUME_EXTENSION: RegistrySupplier<BlockEntityType<OvenFumeExtensionBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("oven_fume_extension") {
            BlockEntityType.Builder.of(
                { pos, state -> OvenFumeExtensionBlockEntity(pos, state) },
                WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION.get(),
                WitcheryBlocks.COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            )
                .build(null)
        }

    val GOLDEN_CHALK: RegistrySupplier<BlockEntityType<GoldenChalkBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("golden_chalk") {
            BlockEntityType.Builder.of(
                { pos, state -> GoldenChalkBlockEntity(pos, state) },
                WitcheryBlocks.GOLDEN_CHALK_BLOCK.get(),
            )
                .build(null)
        }

    val CUSTOM_SIGN: RegistrySupplier<BlockEntityType<SignBlockEntity>> = BLOCK_ENTITY_TYPES.register("custom_sign") {
        BlockEntityType.Builder.of(
            { pos, state -> CustomSignBE(pos, state) as SignBlockEntity },
            WitcheryBlocks.ROWAN_SIGN.get(),
            WitcheryBlocks.ROWAN_WALL_SIGN.get(),
            WitcheryBlocks.ALDER_SIGN.get(),
            WitcheryBlocks.ALDER_WALL_SIGN.get(),
            WitcheryBlocks.HAWTHORN_SIGN.get(),
            WitcheryBlocks.HAWTHORN_WALL_SIGN.get()
        ).build(null)
    }

    val CUSTOM_HANGING_SIGN: RegistrySupplier<BlockEntityType<SignBlockEntity>> = BLOCK_ENTITY_TYPES.register("custom_hanging_sign") {
        BlockEntityType.Builder.of(
            { pos, state -> CustomHangingSignBE(pos, state) as SignBlockEntity },
            WitcheryBlocks.ROWAN_HANGING_SIGN.get(),
            WitcheryBlocks.ROWAN_WALL_HANGING_SIGN.get(),
            WitcheryBlocks.ALDER_HANGING_SIGN.get(),
            WitcheryBlocks.ALDER_WALL_HANGING_SIGN.get(),
            WitcheryBlocks.HAWTHORN_HANGING_SIGN.get(),
            WitcheryBlocks.HAWTHORN_WALL_HANGING_SIGN.get()
        ).build(null)
    }

    val DISTILLERY: RegistrySupplier<BlockEntityType<DistilleryBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("distillery") {
            BlockEntityType.Builder.of(
                { pos, state -> DistilleryBlockEntity(pos, state) },
                WitcheryBlocks.DISTILLERY.get()
            )
                .build(null)
        }

    val WEREWOLF_ALTAR: RegistrySupplier<BlockEntityType<WerewolfAltarBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("werewolf_altar") {
            BlockEntityType.Builder.of(
                { pos, state -> WerewolfAltarBlockEntity(pos, state) },
                WitcheryBlocks.WEREWOLF_ALTAR.get()
            )
                .build(null)
        }

    val BLOODY_POPPY = BLOCK_ENTITY_TYPES.register("blood_poppy") {
        BlockEntityType.Builder.of(
            ::BloodPoppyBlockEntity,
            WitcheryBlocks.BLOOD_POPPY.get()
        ).build(null)
    }

    val SPINNING_WHEEL = BLOCK_ENTITY_TYPES.register("spinning_wheel") {
        BlockEntityType.Builder.of(
            ::SpinningWheelBlockEntity,
            WitcheryBlocks.SPINNING_WHEEL.get()
        ).build(null)
    }

    val ARTHANA = BLOCK_ENTITY_TYPES.register("arthana") {
        BlockEntityType.Builder.of(
            ::ArthanaBlockEntity,
            WitcheryBlocks.ARTHANA.get()
        ).build(null)
    }

    val POPPET = BLOCK_ENTITY_TYPES.register("poppet") {
        BlockEntityType.Builder.of(
            ::PoppetBlockEntity,
            WitcheryBlocks.POPPET.get()
        ).build(null)
    }

    val SPIRIT_PORTAL = BLOCK_ENTITY_TYPES.register("spirit_portal") {
        BlockEntityType.Builder.of(
            ::SpiritPortalBlockEntity,
            WitcheryBlocks.SPIRIT_PORTAL.get()
        ).build(null)
    }

    val BRAZIER = BLOCK_ENTITY_TYPES.register("brazier") {
        BlockEntityType.Builder.of(
            ::BrazierBlockEntity,
            WitcheryBlocks.BRAZIER.get()
        ).build(null)
    }

    val DREAM_WEAVER = BLOCK_ENTITY_TYPES.register("dream_weaver") {
        BlockEntityType.Builder.of(
            ::DreamWeaverBlockEntity,
            WitcheryBlocks.DREAM_WEAVER.get(),
            WitcheryBlocks.DREAM_WEAVER_OF_FLEET_FOOT.get(),
            WitcheryBlocks.DREAM_WEAVER_OF_NIGHTMARES.get(),
            WitcheryBlocks.DREAM_WEAVER_OF_IRON_ARM.get(),
            WitcheryBlocks.DREAM_WEAVER_OF_INTENSITY.get(),
            WitcheryBlocks.DREAM_WEAVER_OF_FASTING.get()
        ).build(null)
    }

    val BRUSHABLE_BLOCK = BLOCK_ENTITY_TYPES.register("suspicious_graveyard_dirt") {
        BlockEntityType.Builder.of(
            { pos: BlockPos, blockState: BlockState ->
                SuspiciousGraveyardDirtBlockEntity(
                    pos,
                    blockState
                )
            }, WitcheryBlocks.SUSPICIOUS_GRAVEYARD_DIRT.get()
        ).build(null)
    }

    val SACRIFICIAL_CIRCLE = BLOCK_ENTITY_TYPES.register("sacrificial_circle") {
        BlockEntityType.Builder.of(
            { pos: BlockPos, blockState: BlockState ->
                SacrificialBlockEntity(
                    pos,
                    blockState
                )
            }, WitcheryBlocks.SACRIFICIAL_CIRCLE.get()
        ).build(null)
    }

    val GRASSPER = BLOCK_ENTITY_TYPES.register("grassper") {
        BlockEntityType.Builder.of(
            ::GrassperBlockEntity,
            WitcheryBlocks.GRASSPER.get()
        ).build(null)
    }

    val EFFIGY = BLOCK_ENTITY_TYPES.register("effigy") {
        BlockEntityType.Builder.of(
            ::EffigyBlockEntity,
            WitcheryBlocks.TRENT_EFFIGY.get(),
            WitcheryBlocks.SCARECROW.get(),
            WitcheryBlocks.WITCHS_LADDER.get()
        ).build(null)
    }

    val CRITTER_SNARE = BLOCK_ENTITY_TYPES.register("critter_snare") {
        BlockEntityType.Builder.of(
            ::CritterSnareBlockEntity,
            WitcheryBlocks.CRITTER_SNARE.get()
        ).build(null)
    }

    val COFFIN = BLOCK_ENTITY_TYPES.register("coffin") {
        BlockEntityType.Builder.of(
            ::CoffinBlockEntity,
            WitcheryBlocks.COFFIN.get()
        ).build(null)
    }
}
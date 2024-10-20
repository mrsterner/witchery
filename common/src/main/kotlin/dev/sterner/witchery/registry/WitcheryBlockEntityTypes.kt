package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlockEntity
import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.block.arthana.ArthanaBlockEntity
import dev.sterner.witchery.block.blood_poppy.BloodPoppyBlockEntity
import dev.sterner.witchery.block.cauldron.CauldronBlockEntity
import dev.sterner.witchery.block.distillery.DistilleryBlockEntity
import dev.sterner.witchery.block.oven.OvenBlockEntity
import dev.sterner.witchery.block.oven.OvenFumeExtensionBlockEntity
import dev.sterner.witchery.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.block.signs.CustomHangingSignBE
import dev.sterner.witchery.block.signs.CustomSignBE
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.SignBlockEntity

object WitcheryBlockEntityTypes {

    val BLOCK_ENTITY_TYPES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(Witchery.MODID, Registries.BLOCK_ENTITY_TYPE)

    val MULTI_BLOCK_COMPONENT: RegistrySupplier<BlockEntityType<MultiBlockComponentBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("multi_block_component") {
            BlockEntityType.Builder.of(
                { pos, state -> MultiBlockComponentBlockEntity(pos, state) },
                WitcheryBlocks.COMPONENT.get(),
                WitcheryBlocks.ALTAR_COMPONENT.get(),
                WitcheryBlocks.CAULDRON_COMPONENT.get(),
                WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION_COMPONENT.get(),
                WitcheryBlocks.DISTILLERY_COMPONENT.get()
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

    val CUSTOM_SIGN = BLOCK_ENTITY_TYPES.register("custom_sign") {
        BlockEntityType.Builder.of(
            { pos, state -> CustomSignBE(pos, state) as SignBlockEntity },
            WitcheryBlocks.ROWAN_SIGN.get(),
            WitcheryBlocks.ROWAN_WALL_SIGN.get(),
            WitcheryBlocks.HAWTHORN_SIGN.get(),
            WitcheryBlocks.HAWTHORN_WALL_SIGN.get()
        ).build(null)
    }

    val CUSTOM_HANGING_SIGN = BLOCK_ENTITY_TYPES.register("custom_hanging_sign") {
        BlockEntityType.Builder.of(
            { pos, state -> CustomSignBE(pos, state) as SignBlockEntity },
            WitcheryBlocks.ROWAN_HANGING_SIGN.get(),
            WitcheryBlocks.ROWAN_WALL_HANGING_SIGN.get(),
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

    val ARTHANA = BLOCK_ENTITY_TYPES.register("arthana") {
        BlockEntityType.Builder.of(
            ::ArthanaBlockEntity,
            WitcheryBlocks.ARTHANA.get()
        ).build(null)
    }

    val BLOODY_POPPY = BLOCK_ENTITY_TYPES.register("blood_poppy") {
        BlockEntityType.Builder.of(
            ::BloodPoppyBlockEntity,
            WitcheryBlocks.BLOOD_POPPY.get()
        ).build(null)
    }
}
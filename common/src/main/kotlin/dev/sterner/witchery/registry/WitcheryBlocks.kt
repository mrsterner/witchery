package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlock
import dev.sterner.witchery.block.EmbermossBlock
import dev.sterner.witchery.block.GlintweedBlock
import dev.sterner.witchery.block.MandrakeCropBlock
import dev.sterner.witchery.block.SpanishMossBlock
import dev.sterner.witchery.block.altar.AltarBlock
import dev.sterner.witchery.block.altar.AltarBlockComponent
import dev.sterner.witchery.block.cauldron.CauldronBlock
import dev.sterner.witchery.block.cauldron.CauldronBlockComponent
import dev.sterner.witchery.block.oven.OvenBlock
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction


object WitcheryBlocks {

    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(Witchery.MODID, Registries.BLOCK)

    val COMPONENT: RegistrySupplier<MultiBlockComponentBlock> = BLOCKS.register("component") {
        MultiBlockComponentBlock(BlockBehaviour.Properties.of())
    }

    val ALTAR: RegistrySupplier<AltarBlock> = BLOCKS.register("altar") {
        AltarBlock(BlockBehaviour.Properties.of())
    }

    val ALTAR_COMPONENT: RegistrySupplier<AltarBlockComponent> = BLOCKS.register("altar_component") {
        AltarBlockComponent(BlockBehaviour.Properties.of())
    }

    val CAULDRON: RegistrySupplier<CauldronBlock> = BLOCKS.register("cauldron") {
        CauldronBlock(BlockBehaviour.Properties.of()
            .sound(SoundType.METAL)
        )
    }

    val CAULDRON_COMPONENT: RegistrySupplier<CauldronBlockComponent> = BLOCKS.register("cauldron_component") {
        CauldronBlockComponent(BlockBehaviour.Properties.of()
            .sound(SoundType.METAL)
        )
    }

    val OVEN: RegistrySupplier<OvenBlock> = BLOCKS.register("oven") {
        OvenBlock(BlockBehaviour.Properties.of()
            .sound(SoundType.METAL)
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
                .pushReaction(PushReaction.DESTROY))
    }

    val EMBER_MOSS: RegistrySupplier<EmbermossBlock> = BLOCKS.register("ember_moss") {
        EmbermossBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .pushReaction(PushReaction.DESTROY))
    }

    val SPANISH_MOSS: RegistrySupplier<SpanishMossBlock> = BLOCKS.register("spanish_moss") {
        SpanishMossBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT)
            .replaceable()
            .noCollission()
            .randomTicks()
            .strength(0.2F)
            .sound(SoundType.VINE)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY))
    }

    val MANDRAKE_CROP = BLOCKS.register("mandrake_crop") {
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

    val BELLADONNAE_CROP = BLOCKS.register("belladonna_crop") {
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

    val SNOWBELL_CROP = BLOCKS.register("snowbell_crop") {
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
}
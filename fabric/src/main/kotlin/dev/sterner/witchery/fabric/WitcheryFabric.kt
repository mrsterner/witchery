package dev.sterner.witchery.fabric

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.particle.ColorBubbleParticle
import dev.sterner.witchery.platform.AltarDataAttachment
import dev.sterner.witchery.platform.MutandisDataAttachment
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryFlammability
import dev.sterner.witchery.registry.WitcheryParticleTypes
import dev.sterner.witchery.registry.WitcheryRitualRegistry
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.event.registry.DynamicRegistries
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry


class WitcheryFabric : ModInitializer, ClientModInitializer {

    companion object {
        @Suppress("UnstableApiUsage")
        val MUTANDIS_LEVEL_DATA_TYPE: AttachmentType<MutandisDataAttachment.MutandisDataCodec> =
            AttachmentRegistry.builder<MutandisDataAttachment.MutandisDataCodec>()
                .persistent(MutandisDataAttachment.MutandisDataCodec.CODEC)
                .initializer { MutandisDataAttachment.MutandisDataCodec() }
                .buildAndRegister(MutandisDataAttachment.ID)

        @Suppress("UnstableApiUsage")
        val ALTAR_LEVEL_DATA_TYPE: AttachmentType<AltarDataAttachment.AltarDataCodec> =
            AttachmentRegistry.builder<AltarDataAttachment.AltarDataCodec>()
                .persistent(AltarDataAttachment.AltarDataCodec.CODEC)
                .initializer { AltarDataAttachment.AltarDataCodec() }
                .buildAndRegister(AltarDataAttachment.AltarDataCodec.ID)
    }

    override fun onInitialize() {
        Witchery.init()

        DynamicRegistries.registerSynced(WitcheryRitualRegistry.RITUAL_KEY, WitcheryRitualRegistry.CODEC)

        StrippableBlockRegistry.register(WitcheryBlocks.ROWAN_LOG.get(), WitcheryBlocks.STRIPPED_ROWAN_LOG.get())
        StrippableBlockRegistry.register(WitcheryBlocks.ROWAN_WOOD.get(), WitcheryBlocks.STRIPPED_ROWAN_WOOD.get())

        StrippableBlockRegistry.register(WitcheryBlocks.ALDER_LOG.get(), WitcheryBlocks.STRIPPED_ALDER_LOG.get())
        StrippableBlockRegistry.register(WitcheryBlocks.ALDER_WOOD.get(), WitcheryBlocks.STRIPPED_ALDER_WOOD.get())

        StrippableBlockRegistry.register(WitcheryBlocks.HAWTHORN_LOG.get(), WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get())
        StrippableBlockRegistry.register(WitcheryBlocks.HAWTHORN_WOOD.get(), WitcheryBlocks.STRIPPED_HAWTHORN_WOOD.get())

        WitcheryFlammability.register()

        OxidizableBlocksRegistry.registerOxidizableBlockPair(WitcheryBlocks.COPPER_WITCHES_OVEN.get(), WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN.get())
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN.get(), WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN.get())
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN.get(), WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN.get())

        OxidizableBlocksRegistry.registerWaxableBlockPair(WitcheryBlocks.COPPER_WITCHES_OVEN.get(), WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN.get())
        OxidizableBlocksRegistry.registerWaxableBlockPair(WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN.get(), WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN.get())
        OxidizableBlocksRegistry.registerWaxableBlockPair(WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN.get(), WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN.get())
        OxidizableBlocksRegistry.registerWaxableBlockPair(WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN.get(), WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN.get())


        OxidizableBlocksRegistry.registerOxidizableBlockPair(WitcheryBlocks.COPPER_CAULDRON.get(), WitcheryBlocks.EXPOSED_COPPER_CAULDRON.get())
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WitcheryBlocks.EXPOSED_COPPER_CAULDRON.get(), WitcheryBlocks.WEATHERED_COPPER_CAULDRON.get())
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WitcheryBlocks.WEATHERED_COPPER_CAULDRON.get(), WitcheryBlocks.OXIDIZED_COPPER_CAULDRON.get())

        OxidizableBlocksRegistry.registerWaxableBlockPair(WitcheryBlocks.COPPER_CAULDRON.get(), WitcheryBlocks.WAXED_COPPER_CAULDRON.get())
        OxidizableBlocksRegistry.registerWaxableBlockPair(WitcheryBlocks.EXPOSED_COPPER_CAULDRON.get(), WitcheryBlocks.WAXED_EXPOSED_COPPER_CAULDRON.get())
        OxidizableBlocksRegistry.registerWaxableBlockPair(WitcheryBlocks.WEATHERED_COPPER_CAULDRON.get(), WitcheryBlocks.WAXED_WEATHERED_COPPER_CAULDRON.get())
        OxidizableBlocksRegistry.registerWaxableBlockPair(WitcheryBlocks.OXIDIZED_COPPER_CAULDRON.get(), WitcheryBlocks.WAXED_OXIDIZED_COPPER_CAULDRON.get())



        OxidizableBlocksRegistry.registerOxidizableBlockPair(WitcheryBlocks.COPPER_WITCHES_OVEN_FUME_EXTENSION.get(), WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get())
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(), WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get())
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(), WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get())

        OxidizableBlocksRegistry.registerWaxableBlockPair(WitcheryBlocks.COPPER_WITCHES_OVEN_FUME_EXTENSION.get(), WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get())
        OxidizableBlocksRegistry.registerWaxableBlockPair(WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(), WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get())
        OxidizableBlocksRegistry.registerWaxableBlockPair(WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(), WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get())
        OxidizableBlocksRegistry.registerWaxableBlockPair(WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(), WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get())

    }

    override fun onInitializeClient() {
        Witchery.initClient()

        ParticleFactoryRegistry.getInstance().register(
            WitcheryParticleTypes.COLOR_BUBBLE.get()
        ) { sprite: FabricSpriteProvider? ->
            ColorBubbleParticle.Provider(
                sprite!!
            )
        }
    }
}



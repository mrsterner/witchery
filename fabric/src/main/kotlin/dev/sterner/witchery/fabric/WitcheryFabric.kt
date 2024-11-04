package dev.sterner.witchery.fabric

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.particle.ColorBubbleParticle
import dev.sterner.witchery.client.particle.ZzzParticle
import dev.sterner.witchery.fabric.client.*
import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.fabric.registry.WitcheryFabricEvents
import dev.sterner.witchery.fabric.registry.WitcheryOxidizables
import dev.sterner.witchery.registry.*
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.biome.v1.*
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.fabricmc.fabric.api.event.registry.DynamicRegistries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.levelgen.GenerationStep


class WitcheryFabric : ModInitializer, ClientModInitializer {

    override fun onInitialize() {
        WitcheryFabricAttachmentRegistry.init()
        Witchery.init()
        WitcheryEntityDataSerializers.register()
        LootTableEvents.MODIFY.register(WitcheryFabricEvents::addEntityDrops)

        DynamicRegistries.registerSynced(WitcheryRitualRegistry.RITUAL_KEY, WitcheryRitualRegistry.CODEC)

        ItemGroupEvents.MODIFY_ENTRIES_ALL.register(WitcheryCreativeModeTabs::modifyExistingTabs)

        StrippableBlockRegistry.register(WitcheryBlocks.ROWAN_LOG.get(), WitcheryBlocks.STRIPPED_ROWAN_LOG.get())
        StrippableBlockRegistry.register(WitcheryBlocks.ROWAN_WOOD.get(), WitcheryBlocks.STRIPPED_ROWAN_WOOD.get())

        StrippableBlockRegistry.register(WitcheryBlocks.ALDER_LOG.get(), WitcheryBlocks.STRIPPED_ALDER_LOG.get())
        StrippableBlockRegistry.register(WitcheryBlocks.ALDER_WOOD.get(), WitcheryBlocks.STRIPPED_ALDER_WOOD.get())

        StrippableBlockRegistry.register(WitcheryBlocks.HAWTHORN_LOG.get(), WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get())
        StrippableBlockRegistry.register(WitcheryBlocks.HAWTHORN_WOOD.get(), WitcheryBlocks.STRIPPED_HAWTHORN_WOOD.get())

        WitcheryFlammability.register()
        WitcheryOxidizables.register()

        BiomeModifications.addFeature(BiomeSelectors.tag(ConventionalBiomeTags.IS_PLAINS), GenerationStep.Decoration.VEGETAL_DECORATION, WitcheryWorldgenKeys.WISPY_PLACED_KEY)
        BiomeModifications.addFeature(BiomeSelectors.tag(WitcheryTags.WITCH_CIRCLE_BIOMES), GenerationStep.Decoration.VEGETAL_DECORATION, WitcheryWorldgenKeys.WITCH_CIRCLE_PLACED_KEY)
    }

    override fun onInitializeClient() {
        Witchery.initClient()

        BuiltinItemRendererRegistry.INSTANCE.register(WitcheryItems.SPINNING_WHEEL.get(), SpinningWheelDynamicRenderer())
        BuiltinItemRendererRegistry.INSTANCE.register(WitcheryItems.BROOM.get(), BroomDynamicRenderer())
        BuiltinItemRendererRegistry.INSTANCE.register(WitcheryItems.DREAM_WEAVER_OF_FLEET_FOOT.get(), DreamWeaverDynamicRenderer())
        BuiltinItemRendererRegistry.INSTANCE.register(WitcheryItems.DREAM_WEAVER_OF_FASTING.get(), DreamWeaverDynamicRenderer())
        BuiltinItemRendererRegistry.INSTANCE.register(WitcheryItems.DREAM_WEAVER_OF_IRON_ARM.get(), DreamWeaverDynamicRenderer())
        BuiltinItemRendererRegistry.INSTANCE.register(WitcheryItems.DREAM_WEAVER_OF_NIGHTMARES.get(), DreamWeaverDynamicRenderer())
        BuiltinItemRendererRegistry.INSTANCE.register(WitcheryItems.DREAM_WEAVER_OF_INTENSITY.get(), DreamWeaverDynamicRenderer())
        BuiltinItemRendererRegistry.INSTANCE.register(WitcheryItems.DREAM_WEAVER.get(), DreamWeaverDynamicRenderer())

        ArmorRenderer.register(WitchesRobesArmorRendererFabric(), WitcheryItems.WITCHES_ROBES.get(), WitcheryItems.WITCHES_HAT.get(), WitcheryItems.WITCHES_SLIPPERS.get(), WitcheryItems.BABA_YAGAS_HAT.get())
        ArmorRenderer.register(HunterArmorRendererFabric(), WitcheryItems.HUNTER_HELMET.get(), WitcheryItems.HUNTER_CHESTPLATE.get(), WitcheryItems.HUNTER_LEGGINGS.get(), WitcheryItems.HUNTER_BOOTS.get())

        ParticleFactoryRegistry.getInstance().register(
            WitcheryParticleTypes.COLOR_BUBBLE.get()
        ) { sprite: FabricSpriteProvider? ->
            ColorBubbleParticle.Provider(
                sprite!!
            )
        }

        ParticleFactoryRegistry.getInstance().register(
            WitcheryParticleTypes.ZZZ.get()
        ) { sprite: FabricSpriteProvider? ->
            ZzzParticle.Provider(
                sprite!!
            )
        }
    }
}



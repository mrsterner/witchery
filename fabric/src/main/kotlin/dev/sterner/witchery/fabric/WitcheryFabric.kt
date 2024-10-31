package dev.sterner.witchery.fabric

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.particle.ColorBubbleParticle
import dev.sterner.witchery.fabric.client.*
import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.fabric.registry.WitcheryFabricEvents
import dev.sterner.witchery.fabric.registry.WitcheryOxidizables
import dev.sterner.witchery.platform.AltarDataAttachment
import dev.sterner.witchery.platform.EntSpawnLevelAttachment
import dev.sterner.witchery.platform.MutandisDataAttachment
import dev.sterner.witchery.platform.infusion.InfusionData
import dev.sterner.witchery.platform.infusion.InfusionType
import dev.sterner.witchery.platform.infusion.LightInfusionData
import dev.sterner.witchery.platform.infusion.OtherwhereInfusionData
import dev.sterner.witchery.platform.poppet.PoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetData
import dev.sterner.witchery.registry.*
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.fabricmc.fabric.api.event.registry.DynamicRegistries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.fabricmc.fabric.api.loot.v3.LootTableSource
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry
import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator


class WitcheryFabric : ModInitializer, ClientModInitializer {

    override fun onInitialize() {
        WitcheryFabricAttachmentRegistry.init()
        Witchery.init()

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
    }
}



package dev.sterner.witchery.fabric

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.particle.ColorBubbleParticle
import dev.sterner.witchery.fabric.client.BroomDynamicRenderer
import dev.sterner.witchery.fabric.client.HunterArmorRendererFabric
import dev.sterner.witchery.fabric.client.SpinningWheelDynamicRenderer
import dev.sterner.witchery.fabric.client.WitchesRobesArmorRendererFabric
import dev.sterner.witchery.fabric.registry.WitcheryOxidizables
import dev.sterner.witchery.platform.AltarDataAttachment
import dev.sterner.witchery.platform.MutandisDataAttachment
import dev.sterner.witchery.platform.infusion.InfusionData
import dev.sterner.witchery.platform.infusion.InfusionType
import dev.sterner.witchery.platform.infusion.LightInfusionData
import dev.sterner.witchery.platform.infusion.OtherwhereInfusionData
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

        @Suppress("UnstableApiUsage")
        val INFUSION_PLAYER_DATA_TYPE: AttachmentType<InfusionData> =
            AttachmentRegistry.builder<InfusionData>()
                .persistent(InfusionData.CODEC)
                .initializer { InfusionData(InfusionType.NONE) }
                .buildAndRegister(InfusionData.ID)

        @Suppress("UnstableApiUsage")
        val LIGHT_INFUSION_PLAYER_DATA_TYPE: AttachmentType<LightInfusionData> =
            AttachmentRegistry.builder<LightInfusionData>()
                .persistent(LightInfusionData.CODEC)
                .initializer { LightInfusionData(false, 0) }
                .buildAndRegister(LightInfusionData.ID)

        @Suppress("UnstableApiUsage")
        val OTHERWHERE_INFUSION_PLAYER_DATA_TYPE: AttachmentType<OtherwhereInfusionData> =
            AttachmentRegistry.builder<OtherwhereInfusionData>()
                .persistent(OtherwhereInfusionData.CODEC)
                .initializer { OtherwhereInfusionData(0, 0) }
                .buildAndRegister(OtherwhereInfusionData.ID)
    }

    override fun onInitialize() {
        Witchery.init()

        LootTableEvents.MODIFY.register(::addEntityDrops)

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

    private fun addEntityDrops(resourceKey: ResourceKey<LootTable>?, builder: LootTable.Builder, lootTableSource: LootTableSource, provider: HolderLookup.Provider) {
        if (lootTableSource.isBuiltin && EntityType.WOLF.defaultLootTable.equals(resourceKey)) {
            val pool = LootPool
                .lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.TONGUE_OF_DOG.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.25f))
                )
                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0.0F, 1.0F)))
                .build()
            builder.pool(pool)
        }

        if (lootTableSource.isBuiltin && EntityType.FROG.defaultLootTable.equals(resourceKey)) {
            val pool = LootPool
                .lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.TOE_OF_FROG.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.25f))
                )
                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0.0F, 1.0F)))
                .build()
            builder.pool(pool)
        }

        if (lootTableSource.isBuiltin && EntityType.BAT.defaultLootTable.equals(resourceKey)) {
            val pool = LootPool
                .lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.WOOL_OF_BAT.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.25f))
                )
                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0.0F, 1.0F)))
                .build()
            builder.pool(pool)
        }
    }

    override fun onInitializeClient() {
        Witchery.initClient()

        BuiltinItemRendererRegistry.INSTANCE.register(WitcheryItems.SPINNING_WHEEL.get(), SpinningWheelDynamicRenderer())
        BuiltinItemRendererRegistry.INSTANCE.register(WitcheryItems.BROOM.get(), BroomDynamicRenderer())

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



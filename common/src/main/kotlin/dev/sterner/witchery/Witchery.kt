package dev.sterner.witchery

import com.mojang.logging.LogUtils
import dev.architectury.event.EventResult
import dev.architectury.event.events.client.ClientLifecycleEvent
import dev.architectury.event.events.common.InteractionEvent
import dev.architectury.event.events.common.LootEvent
import dev.architectury.event.events.common.LootEvent.LootTableModificationContext
import dev.architectury.event.events.common.TickEvent.ServerLevelTick
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry
import dev.architectury.registry.client.level.entity.EntityRendererRegistry
import dev.architectury.registry.client.particle.ParticleProviderRegistry
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry
import dev.architectury.registry.client.rendering.ColorHandlerRegistry
import dev.architectury.registry.client.rendering.RenderTypeRegistry
import dev.architectury.registry.item.ItemPropertiesRegistry
import dev.architectury.registry.level.entity.EntityAttributeRegistry
import dev.architectury.registry.menu.MenuRegistry
import dev.sterner.witchery.client.colors.RitualChalkColors
import dev.sterner.witchery.client.model.*
import dev.sterner.witchery.client.particle.ColorBubbleParticle
import dev.sterner.witchery.client.renderer.*
import dev.sterner.witchery.client.screen.AltarScreen
import dev.sterner.witchery.client.screen.DistilleryScreen
import dev.sterner.witchery.client.screen.OvenScreen
import dev.sterner.witchery.data.NaturePowerHandler
import dev.sterner.witchery.entity.MandrakeEntity
import dev.sterner.witchery.item.TaglockItem
import dev.sterner.witchery.platform.MutandisDataAttachment
import dev.sterner.witchery.registry.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.BoatModel
import net.minecraft.client.model.ChestBoatModel
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.HangingSignRenderer
import net.minecraft.client.renderer.blockentity.SignRenderer
import net.minecraft.client.renderer.entity.BoatRenderer
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition
import org.slf4j.Logger


object Witchery {
    const val MODID: String = "witchery"

    val LOGGER: Logger = LogUtils.getLogger()

    @JvmStatic
    fun init() {
        //WitcheryRitualRegistry.RITUALS.register()
        WitcheryBlocks.BLOCKS.register()
        WitcheryBlockEntityTypes.BLOCK_ENTITY_TYPES.register()
        WitcheryItems.ITEMS.register()
        WitcheryEntityTypes.ENTITY_TYPES.register()
        WitcherySounds.SOUNDS.register()
        WitcheryCreativeModeTabs.TABS.register()
        WitcheryParticleTypes.PARTICLES.register()
        WitcheryRecipeTypes.RECIPE_TYPES.register()
        WitcheryRecipeSerializers.RECIPE_SERIALIZERS.register()
        WitcheryMenuTypes.MENU_TYPES.register()
        WitcheryDataComponents.DATA.register()

        WitcheryPayloads.register()

        EntityAttributeRegistry.register(WitcheryEntityTypes.MANDRAKE, MandrakeEntity::createAttributes)

        ClientLifecycleEvent.CLIENT_SETUP.register {
            MenuRegistry.registerScreenFactory(
                WitcheryMenuTypes.OVEN_MENU_TYPE.get(),
                ::OvenScreen
            )
            MenuRegistry.registerScreenFactory(WitcheryMenuTypes.ALTAR_MENU_TYPE.get(), ::AltarScreen)
            MenuRegistry.registerScreenFactory(WitcheryMenuTypes.DISTILLERY_MENU_TYPE.get(), ::DistilleryScreen)
        }

        LootEvent.MODIFY_LOOT_TABLE.register(::addSeeds)
        InteractionEvent.INTERACT_ENTITY.register(::interactEntityTaglock)
        ServerLevelTick.SERVER_LEVEL_POST.register { serverLevel -> MutandisDataAttachment.tick(serverLevel) }

        NaturePowerHandler.registerListener()
    }

    private fun addSeeds(key: ResourceKey<LootTable>?, context: LootTableModificationContext, builtin: Boolean) {
        if (builtin && Blocks.SHORT_GRASS.lootTable.equals(key) || Blocks.TALL_GRASS.lootTable.equals(key)) {
            val pool: LootPool.Builder = LootPool
                .lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.BELLADONNA_SEEDS.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.05f))
                )
            context.addPool(pool)

            val pool2: LootPool.Builder = LootPool
                .lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.WATER_ARTICHOKE_SEEDS.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.05f))
                )
            context.addPool(pool2)

            val pool3: LootPool.Builder = LootPool
                .lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.MANDRAKE_SEEDS.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.05f))
                )
            context.addPool(pool3)

            val pool4: LootPool.Builder = LootPool
                .lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.SNOWBELL_SEEDS.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.05f))
                )
            context.addPool(pool4)
        }
    }

    private fun interactEntityTaglock(
        player: Player,
        entity: Entity?,
        interactionHand: InteractionHand?
    ): EventResult? {
        if (player.mainHandItem.`is`(WitcheryItems.TAGLOCK.get()) && interactionHand == InteractionHand.MAIN_HAND) {
            if (entity is Player) {
                TaglockItem.bindPlayer(entity, player.mainHandItem)
                return EventResult.interruptTrue()
            } else if (entity is LivingEntity) {
                TaglockItem.bindLivingEntity(entity, player.mainHandItem)
                return EventResult.interruptTrue()
            }
        }

        return EventResult.pass()
    }

    @JvmStatic
    @Environment(EnvType.CLIENT)
    fun initClient() {
        EntityModelLayerRegistry.register(AltarClothBlockEntityModel.LAYER_LOCATION) { AltarClothBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(AltarBlockEntityModel.LAYER_LOCATION) { AltarBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(JarModel.LAYER_LOCATION) { JarModel.createBodyLayer() }

        EntityRendererRegistry.register(WitcheryEntityTypes.MANDRAKE) { MandrakeEntityRenderer(it) }
        EntityModelLayerRegistry.register(MandrakeEntityModel.LAYER_LOCATION) { MandrakeEntityModel.createBodyLayer() }

        EntityRendererRegistry.register(WitcheryEntityTypes.CUSTOM_BOAT) { context -> BoatRenderer(context, false) }
        EntityModelLayerRegistry.register(BoatModels.ROWAN_BOAT_LAYER, BoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.ALDER_BOAT_LAYER, BoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.HAWTHORN_BOAT_LAYER, BoatModel::createBodyModel)
        EntityRendererRegistry.register(WitcheryEntityTypes.CUSTOM_CHEST_BOAT) { context -> BoatRenderer(context, true) }
        EntityModelLayerRegistry.register(BoatModels.ROWAN_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.ALDER_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.HAWTHORN_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel)

        EntityRendererRegistry.register(WitcheryEntityTypes.FLOATING_ITEM, ::FloatingItemEntityRenderer)

        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.ALTAR.get(), ::AltarBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.CAULDRON.get(), ::CauldronBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.DISTILLERY.get(), ::DistilleryBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.CUSTOM_SIGN.get(), ::SignRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.CUSTOM_HANGING_SIGN.get(), ::HangingSignRenderer)

        ParticleProviderRegistry.register(WitcheryParticleTypes.COLOR_BUBBLE.get(), ColorBubbleParticle::Provider)

        ItemPropertiesRegistry.register(
            WitcheryItems.WAYSTONE.get(),
            ResourceLocation.fromNamespaceAndPath(MODID, "is_bound")
        ) { itemStack, _, _, _ ->
            var ret = 0f
            val customData = itemStack.get(WitcheryDataComponents.GLOBAL_POS_COMPONENT.get())
            val customData2 = itemStack.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())
            if (TaglockItem.getPlayerProfile(itemStack) != null || customData2 != null) {
                ret = 2.0f
            } else if (customData != null) {
                ret = 1.0f
            }
            ret
        }

        ItemPropertiesRegistry.register(
            WitcheryItems.TAGLOCK.get(),
            ResourceLocation.fromNamespaceAndPath(MODID, "expired")
        ) { itemStack, _, _, _ ->
            var ret = 0f
            val customData = itemStack.get(WitcheryDataComponents.EXPIRED_TAGLOCK.get())
            if (customData != null && customData) {
                ret = 1.0f
            }
            ret
        }

        ColorHandlerRegistry.registerBlockColors(
            RitualChalkColors,
            WitcheryBlocks.RITUAL_CHALK_BLOCK.get(),
            WitcheryBlocks.INFERNAL_CHALK_BLOCK.get(),
            WitcheryBlocks.OTHERWHERE_CHALK_BLOCK.get()
        )

        RenderTypeRegistry.register(
            RenderType.cutout(),
            WitcheryBlocks.GOLDEN_CHALK_BLOCK.get(),
            WitcheryBlocks.RITUAL_CHALK_BLOCK.get(),
            WitcheryBlocks.INFERNAL_CHALK_BLOCK.get(),
            WitcheryBlocks.OTHERWHERE_CHALK_BLOCK.get(),
            WitcheryBlocks.CAULDRON.get(),
            WitcheryBlocks.GLINTWEED.get(),
            WitcheryBlocks.EMBER_MOSS.get(),
            WitcheryBlocks.SPANISH_MOSS.get(),
            WitcheryBlocks.MANDRAKE_CROP.get(),
            WitcheryBlocks.BELLADONNAE_CROP.get(),
            WitcheryBlocks.COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.IRON_WITCHES_OVEN.get(),
            WitcheryBlocks.SNOWBELL_CROP.get(),
            WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.GARLIC_CROP.get(),
            WitcheryBlocks.WORMWOOD_CROP.get(),
            WitcheryBlocks.WOLFSFBANE_CROP.get(),
            WitcheryBlocks.WATER_ARTICHOKE_CROP.get(),
            WitcheryBlocks.ROWAN_LEAVES.get(),
            WitcheryBlocks.ROWAN_BERRY_LEAVES.get(),
            WitcheryBlocks.ROWAN_DOOR.get(),
            WitcheryBlocks.ROWAN_TRAPDOOR.get(),
            WitcheryBlocks.ROWAN_SAPLING.get(),
            WitcheryBlocks.POTTED_ROWAN_SAPLING.get(),
            WitcheryBlocks.ALDER_LEAVES.get(),
            WitcheryBlocks.ALDER_DOOR.get(),
            WitcheryBlocks.ALDER_TRAPDOOR.get(),
            WitcheryBlocks.ALDER_SAPLING.get(),
            WitcheryBlocks.POTTED_ALDER_SAPLING.get(),
            WitcheryBlocks.HAWTHORN_LEAVES.get(),
            WitcheryBlocks.HAWTHORN_DOOR.get(),
            WitcheryBlocks.HAWTHORN_TRAPDOOR.get(),
            WitcheryBlocks.HAWTHORN_SAPLING.get(),
            WitcheryBlocks.POTTED_HAWTHORN_SAPLING.get(),
            WitcheryBlocks.DISTILLERY.get()
        )
    }

    fun id(name: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MODID, name)
    }
}

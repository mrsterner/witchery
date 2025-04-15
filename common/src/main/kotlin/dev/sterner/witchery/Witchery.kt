package dev.sterner.witchery

import com.mojang.logging.LogUtils
import dev.architectury.event.events.client.ClientGuiEvent
import dev.architectury.event.events.client.ClientRawInputEvent
import dev.architectury.event.events.client.ClientTickEvent
import dev.architectury.event.events.common.*
import dev.architectury.event.events.common.LootEvent.LootTableModificationContext
import dev.architectury.event.events.common.LootEvent.MODIFY_LOOT_TABLE
import dev.architectury.event.events.common.TickEvent.ServerLevelTick
import dev.architectury.networking.NetworkManager
import dev.architectury.registry.client.gui.ClientTooltipComponentRegistry
import dev.architectury.registry.client.keymappings.KeyMappingRegistry
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry
import dev.architectury.registry.client.level.entity.EntityRendererRegistry
import dev.architectury.registry.client.particle.ParticleProviderRegistry
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry
import dev.architectury.registry.client.rendering.ColorHandlerRegistry
import dev.architectury.registry.client.rendering.RenderTypeRegistry
import dev.architectury.registry.item.ItemPropertiesRegistry
import dev.architectury.registry.level.entity.EntityAttributeRegistry
import dev.architectury.registry.menu.MenuRegistry
import dev.sterner.witchery.api.BloodPoolComponent
import dev.sterner.witchery.api.SleepingEvent
import dev.sterner.witchery.block.ritual.RitualChalkBlock
import dev.sterner.witchery.block.sacrificial_circle.SacrificialBlockEntity
import dev.sterner.witchery.client.colors.PotionColor
import dev.sterner.witchery.client.colors.RitualChalkColors
import dev.sterner.witchery.client.model.*
import dev.sterner.witchery.client.model.poppet.ArmorPoppetModel
import dev.sterner.witchery.client.model.poppet.HungerPoppetModel
import dev.sterner.witchery.client.model.poppet.VampiricPoppetModel
import dev.sterner.witchery.client.model.poppet.VoodooPoppetModel
import dev.sterner.witchery.client.particle.ColorBubbleParticle
import dev.sterner.witchery.client.particle.SneezeParticle
import dev.sterner.witchery.client.particle.ZzzParticle
import dev.sterner.witchery.client.renderer.*
import dev.sterner.witchery.client.screen.AltarScreen
import dev.sterner.witchery.client.screen.DistilleryScreen
import dev.sterner.witchery.client.screen.OvenScreen
import dev.sterner.witchery.client.screen.SpinningWheelScreen
import dev.sterner.witchery.data.BloodPoolHandler
import dev.sterner.witchery.data.ErosionHandler
import dev.sterner.witchery.data.NaturePowerHandler
import dev.sterner.witchery.data.PotionDataHandler
import dev.sterner.witchery.entity.*
import dev.sterner.witchery.handler.*
import dev.sterner.witchery.handler.vampire.VampireAbilities
import dev.sterner.witchery.handler.vampire.VampireEventHandler
import dev.sterner.witchery.handler.werewolf.WerewolfEventHandler
import dev.sterner.witchery.integration.modonomicon.WitcheryPageRendererRegistry
import dev.sterner.witchery.item.CaneSwordItem
import dev.sterner.witchery.item.TaglockItem
import dev.sterner.witchery.item.WineGlassItem
import dev.sterner.witchery.item.accessories.BitingBeltItem
import dev.sterner.witchery.item.brew.BrewOfSleepingItem
import dev.sterner.witchery.payload.DismountBroomC2SPayload
import dev.sterner.witchery.platform.*
import dev.sterner.witchery.platform.infusion.InfernalInfusionData
import dev.sterner.witchery.platform.infusion.LightInfusionDataAttachment
import dev.sterner.witchery.platform.infusion.OtherwhereInfusionDataAttachment
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment
import dev.sterner.witchery.platform.transformation.VampireChildrenHuntLevelAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import dev.sterner.witchery.registry.*
import dev.sterner.witchery.registry.WitcheryDataComponents.UNSHEETED
import dev.sterner.witchery.registry.WitcheryItems.CANE_SWORD
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft
import net.minecraft.client.model.BoatModel
import net.minecraft.client.model.ChestBoatModel
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.HangingSignRenderer
import net.minecraft.client.renderer.blockentity.SignRenderer
import net.minecraft.client.renderer.entity.*
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.Pig
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition
import org.slf4j.Logger


object Witchery {

    const val MODID: String = "witchery"

    val LOGGER: Logger = LogUtils.getLogger()

    val debugRitualLog: Boolean = false

    @JvmStatic
    fun init() {
        WitcheryCurseRegistry.init()
        WitcheryRitualRegistry.init()
        WitcherySpecialPotionEffects.init()
        WitcheryFluids.FLUIDS.register()
        WitcheryFluids.init()
        WitcheryArmorMaterials.MATERIALS.register()
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
        WitcheryCommands.COMMAND_ARGUMENTS.register()
        WitcheryFeatures.FEATURES.register()
        WitcheryMobEffects.EFFECTS.register()

        WitcheryPayloads.register()

        EntityAttributeRegistry.register(WitcheryEntityTypes.MANDRAKE, MandrakeEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.IMP, ImpEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.DEMON, DemonEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.OWL, OwlEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.ENT, EntEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.BANSHEE, BansheeEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.SPECTRE, SpectreEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.SPECTRAL_PIG, Pig::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.NIGHTMARE, NightmareEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.VAMPIRE, VampireEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.WEREWOLF, WerewolfEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.LILITH, LilithEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.ELLE, ElleEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.PARASITIC_LOUSE, ParasiticLouseEntity::createAttributes)

        MODIFY_LOOT_TABLE.register(::addSeeds)

        NaturePowerHandler.registerListener()
        ErosionHandler.registerListener()
        PotionDataHandler.registerListener()
        BloodPoolHandler.registerListener()

        WitcheryModonomiconLoaders.register()

        MODIFY_LOOT_TABLE.register(::addWitchesHand)
        MODIFY_LOOT_TABLE.register(::addLootInjects)

        VampireEventHandler.registerEvents()
        WerewolfEventHandler.registerEvents()
        CursePlayerAttachment.registerEvents()
        PotionHandler.registerEvents()

        ServerLevelTick.SERVER_LEVEL_POST.register(MutandisLevelAttachment::tick)

        CommandRegistrationEvent.EVENT.register(WitcheryCommands::register)

        EntityEvent.LIVING_DEATH.register(PoppetHandler::deathProtectionPoppet)
        EntityEvent.LIVING_DEATH.register(PoppetHandler::hungerProtectionPoppet)
        EntityEvent.LIVING_DEATH.register(FamiliarLevelAttachment::familiarDeath)
        EntityEvent.LIVING_DEATH.register(CaneSwordItem::harvestBlood)
        EntityEvent.LIVING_HURT.register(EquipmentHandler::babaYagaHit)
        EntityEvent.LIVING_HURT.register(BitingBeltItem::usePotion)
        EntityEvent.ADD.register(BloodPoolLivingEntityAttachment::setBloodOnAdded)

        SleepingEvent.POST.register(DreamWeaverHandler::onWake)

        PlayerEvent.PLAYER_CLONE.register(BrewOfSleepingItem::respawnPlayer)
        PlayerEvent.ATTACK_ENTITY.register(InfusionHandler::leftClickEntity)
        PlayerEvent.PLAYER_RESPAWN.register{ player, _, _ ->
            VampireAbilities.setAbilityIndex(player, -1)
        }

        InteractionEvent.RIGHT_CLICK_BLOCK.register(SacrificialBlockEntity::rightClick)
        InteractionEvent.RIGHT_CLICK_BLOCK.register(LecternHandler::tryAccessGuidebook)
        InteractionEvent.INTERACT_ENTITY.register(WineGlassItem::applyWineOnVillager)
        InteractionEvent.LEFT_CLICK_BLOCK.register(InfusionHandler::leftClickBlock)

        BlockEvent.BREAK.register(EntSpawnLevelAttachment::breakBlock)
        BlockEvent.PLACE.register(RitualChalkBlock::placeInfernal)

        TickEvent.SERVER_POST.register(EntSpawnLevelAttachment::serverTick)
        TickEvent.SERVER_POST.register(TeleportQueueLevelAttachment::processQueue)
        TickEvent.SERVER_POST.register(ManifestationPlayerAttachment::tick)
        TickEvent.SERVER_POST.register(VampireChildrenHuntLevelAttachment::tickHuntAllLevels)
        TickEvent.PLAYER_POST.register(InfernalInfusionData::tick)
        TickEvent.PLAYER_PRE.register(BloodPoolLivingEntityAttachment::tick)
        TickEvent.PLAYER_PRE.register(LightInfusionDataAttachment::tick)
        TickEvent.PLAYER_PRE.register(OtherwhereInfusionDataAttachment::tick)
        TickEvent.PLAYER_PRE.register(NightmarePlayerAttachment::tick)
        TickEvent.PLAYER_PRE.register(TransformationPlayerAttachment::tickBat)
        TickEvent.PLAYER_PRE.register(BarkBeltPlayerAttachment::tick)

        LightningEvent.STRIKE.register(InfernalInfusionData::strikeLightning)

        PlayerEvent.PLAYER_JOIN.register {serverPlayer ->
            val data = DeathQueueLevelAttachment.getData(serverPlayer.serverLevel())
            if (data.killerQueue.contains(serverPlayer.uuid)) {
                serverPlayer.kill()
            }
            VampirePlayerAttachment.sync(serverPlayer, VampirePlayerAttachment.getData(serverPlayer))
            BloodPoolLivingEntityAttachment.sync(serverPlayer, BloodPoolLivingEntityAttachment.getData(serverPlayer))
        }

        LifecycleEvent.SERVER_STARTED.register { addStructure(it) }
    }

    /**
     * Adds Graveyards to Plains and Taiga Villages.
     */
    private fun addStructure(server: MinecraftServer){
        val builtinTemplate: Registry<StructureTemplatePool> = server.registryAccess().registry(Registries.TEMPLATE_POOL).get()
        val builtinProcessor: Registry<StructureProcessorList> = server.registryAccess().registry(Registries.PROCESSOR_LIST).get()

        VillageHelper.addBuildingToPool(builtinTemplate, builtinProcessor,
            ResourceLocation.parse("minecraft:village/plains/houses"),
            "$MODID:village/houses/plains_graveyard",
            2)

        VillageHelper.addBuildingToPool(builtinTemplate, builtinProcessor,
            ResourceLocation.parse("minecraft:village/plains/houses"),
            "$MODID:village/houses/plains_graveyard_2",
            2)

        VillageHelper.addBuildingToPool(builtinTemplate, builtinProcessor,
            ResourceLocation.parse("minecraft:village/plains/houses"),
            "$MODID:village/houses/plains_graveyard_3",
            2)



        VillageHelper.addBuildingToPool(builtinTemplate, builtinProcessor,
            ResourceLocation.parse("minecraft:village/taiga/houses"),
            "$MODID:village/houses/plains_graveyard",
            2)

        VillageHelper.addBuildingToPool(builtinTemplate, builtinProcessor,
            ResourceLocation.parse("minecraft:village/taiga/houses"),
            "$MODID:village/houses/plains_graveyard_2",
            2)

        VillageHelper.addBuildingToPool(builtinTemplate, builtinProcessor,
            ResourceLocation.parse("minecraft:village/taiga/houses"),
            "$MODID:village/houses/plains_graveyard_3",
            2)

    }

    /**
     * Adds the item Witches Hand to The witches loot table at a 50% chance drop
     */
    private fun addWitchesHand(
        resourceKey: ResourceKey<LootTable>?,
        context: LootTableModificationContext,
        isBuiltin: Boolean
    ) {
        if (isBuiltin && EntityType.WITCH.defaultLootTable.equals(resourceKey)) {
            val pool = LootPool
                .lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.WITCHES_HAND.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.5f))
                )
            context.addPool(pool)
        }
    }

    /**
     * Adds Witchery drops to vanilla creatures. Wolf, Frog and Bat
     */
    private fun addLootInjects(
        resourceKey: ResourceKey<LootTable>?,
        context: LootTableModificationContext,
        isBuiltin: Boolean
    ) {

        if (isBuiltin && EntityType.WOLF.defaultLootTable.equals(resourceKey)) {
            val pool = LootPool
                .lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.TONGUE_OF_DOG.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.25f))
                )
            context.addPool(pool)
        }

        if (isBuiltin && EntityType.FROG.defaultLootTable.equals(resourceKey)) {
            val pool = LootPool
                .lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.TOE_OF_FROG.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.25f))
                )
            context.addPool(pool)
        }

        if (isBuiltin && EntityType.BAT.defaultLootTable.equals(resourceKey)) {
            val pool = LootPool
                .lootPool()
                .add(
                    LootItem.lootTableItem(WitcheryItems.WOOL_OF_BAT.get())
                        .`when`(LootItemRandomChanceCondition.randomChance(0.25f))
                )
            context.addPool(pool)
        }
    }

    /**
     * Adds Witchery Seeds to Vanilla short grass and long grass loot table. 5% chance
     */
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

    @JvmStatic
    @Environment(EnvType.CLIENT)
    fun initClient() {
        EntityModelLayerRegistry.register(AltarClothBlockEntityModel.LAYER_LOCATION) { AltarClothBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(AltarBlockEntityModel.LAYER_LOCATION) { AltarBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(SpiritPortalBlockEntityModel.LAYER_LOCATION) { SpiritPortalBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(SpiritPortalPortalModel.LAYER_LOCATION) { SpiritPortalPortalModel.createBodyLayer() }
        EntityModelLayerRegistry.register(JarModel.LAYER_LOCATION) { JarModel.createBodyLayer() }
        EntityModelLayerRegistry.register(ArmorPoppetModel.LAYER_LOCATION) { ArmorPoppetModel.createBodyLayer() }
        EntityModelLayerRegistry.register(HungerPoppetModel.LAYER_LOCATION) { HungerPoppetModel.createBodyLayer() }
        EntityModelLayerRegistry.register(VampiricPoppetModel.LAYER_LOCATION) { VampiricPoppetModel.createBodyLayer() }
        EntityModelLayerRegistry.register(VoodooPoppetModel.LAYER_LOCATION) { VoodooPoppetModel.createBodyLayer() }
        EntityModelLayerRegistry.register(WitchesRobesModel.LAYER_LOCATION) { WitchesRobesModel.createBodyLayer() }
        EntityModelLayerRegistry.register(VampireArmorModel.LAYER_LOCATION) { VampireArmorModel.createBodyLayer() }
        EntityModelLayerRegistry.register(HunterArmorModel.LAYER_LOCATION) { HunterArmorModel.createBodyLayer() }
        EntityModelLayerRegistry.register(SpinningWheelWheelBlockEntityModel.LAYER_LOCATION) { SpinningWheelWheelBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(SpinningWheelBlockEntityModel.LAYER_LOCATION) { SpinningWheelBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(DistilleryGemModel.LAYER_LOCATION) { DistilleryGemModel.createBodyLayer() }
        EntityModelLayerRegistry.register(BroomEntityModel.LAYER_LOCATION) { BroomEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(DreamWeaverBlockEntityModel.LAYER_LOCATION) { DreamWeaverBlockEntityModel.createBodyLayer() }

        EntityRendererRegistry.register(WitcheryEntityTypes.BROOM) { BroomEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.MANDRAKE) { MandrakeEntityRenderer(it) }
        EntityModelLayerRegistry.register(MandrakeEntityModel.LAYER_LOCATION) { MandrakeEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.IMP) { ImpEntityRenderer(it) }
        EntityModelLayerRegistry.register(ImpEntityModel.LAYER_LOCATION) { ImpEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.OWL) { OwlEntityRenderer(it) }
        EntityModelLayerRegistry.register(OwlEntityModel.LAYER_LOCATION) { OwlEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.DEMON) { DemonEntityRenderer(it) }
        EntityModelLayerRegistry.register(DemonEntityModel.LAYER_LOCATION) { DemonEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.ENT) { EntEntityRenderer(it) }
        EntityModelLayerRegistry.register(EntEntityModel.LAYER_LOCATION) { EntEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.BANSHEE) { BansheeEntityRenderer(it) }
        EntityRendererRegistry.register(WitcheryEntityTypes.SPECTRE) { SpectreEntityRenderer(it) }
        EntityModelLayerRegistry.register(BansheeEntityModel.LAYER_LOCATION) { BansheeEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(SpectreEntityModel.LAYER_LOCATION) { SpectreEntityModel.createBodyLayer() }

        EntityRendererRegistry.register(WitcheryEntityTypes.PARASITIC_LOUSE) { ParasiticLouseEntityRenderer(it) }
        EntityModelLayerRegistry.register(ParasiticLouseEntityModel.LAYER_LOCATION) { ParasiticLouseEntityModel.createBodyLayer() }

        EntityRendererRegistry.register(WitcheryEntityTypes.VAMPIRE) { VampireEntityRenderer(it) }
        EntityModelLayerRegistry.register(VampireEntityModel.LAYER_LOCATION) { VampireEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.WEREWOLF) { WerewolfEntityRenderer(it) }
        EntityModelLayerRegistry.register(WerewolfEntityModel.LAYER_LOCATION) { WerewolfEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.NIGHTMARE) { NightmareEntityRenderer(it) }
        EntityModelLayerRegistry.register(NightmareEntityModel.LAYER_LOCATION) { NightmareEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.LILITH) { LilithEntityRenderer(it) }
        EntityModelLayerRegistry.register(LilithEntityModel.LAYER_LOCATION) { LilithEntityModel.createBodyLayer() }
        EntityRendererRegistry.register(WitcheryEntityTypes.ELLE) { ElleEntityRenderer(it) }

        EntityRendererRegistry.register(WitcheryEntityTypes.CUSTOM_BOAT) { context -> BoatRenderer(context, false) }
        EntityModelLayerRegistry.register(BoatModels.ROWAN_BOAT_LAYER, BoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.ALDER_BOAT_LAYER, BoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.HAWTHORN_BOAT_LAYER, BoatModel::createBodyModel)
        EntityRendererRegistry.register(WitcheryEntityTypes.CUSTOM_CHEST_BOAT) { context ->
            BoatRenderer(
                context,
                true
            )
        }
        EntityModelLayerRegistry.register(BoatModels.ROWAN_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.ALDER_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.HAWTHORN_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel)

        EntityRendererRegistry.register(WitcheryEntityTypes.FLOATING_ITEM, ::FloatingItemEntityRenderer)
        EntityRendererRegistry.register(WitcheryEntityTypes.THROWN_BREW, ::ThrownItemRenderer)
        EntityRendererRegistry.register(WitcheryEntityTypes.THROWN_POTION, ::ThrownItemRenderer)
        EntityRendererRegistry.register(WitcheryEntityTypes.SLEEPING_PLAYER, ::SleepingPlayerEntityRenderer)
        EntityRendererRegistry.register(WitcheryEntityTypes.SPECTRAL_PIG, ::SpectralPigRenderer)
        EntityRendererRegistry.register(
            WitcheryEntityTypes.AREA_EFFECT_CLOUD
        ) { context: EntityRendererProvider.Context ->
            NoopRenderer(
                context
            )
        }

        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.ALTAR.get(), ::AltarBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.CAULDRON.get(), ::CauldronBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.DISTILLERY.get(), ::DistilleryBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.BRAZIER.get(), ::BrazierBlockEntityRenderer)
        BlockEntityRendererRegistry.register(
            WitcheryBlockEntityTypes.SPINNING_WHEEL.get(),
            ::SpinningWheelBlockEntityRenderer
        )
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.CUSTOM_SIGN.get(), ::SignRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.CUSTOM_HANGING_SIGN.get(), ::HangingSignRenderer)
        BlockEntityRendererRegistry.register(
            WitcheryBlockEntityTypes.DREAM_WEAVER.get(),
            ::DreamWeaverBlockEntityRenderer
        )
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.POPPET.get(), ::PoppetBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.SPIRIT_PORTAL.get(), ::SpiritPortalBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.BRUSHABLE_BLOCK.get(), ::SuspiciousGraveyardDirtBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.SACRIFICIAL_CIRCLE.get(), ::SacrificialCircleBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.GRASSPER.get(), ::GrassperBlockEntityRenderer)
        BlockEntityRendererRegistry.register(WitcheryBlockEntityTypes.CRITTER_SNARE.get(), ::CritterSnareBlockEntityRenderer)


        ClientTooltipComponentRegistry.register(BloodPoolComponent::class.java, BloodPoolComponent::getClientTooltipComponent)

        ParticleProviderRegistry.register(WitcheryParticleTypes.COLOR_BUBBLE.get(), ColorBubbleParticle::Provider)
        ParticleProviderRegistry.register(WitcheryParticleTypes.ZZZ.get(), ZzzParticle::Provider)
        ParticleProviderRegistry.register(WitcheryParticleTypes.SNEEZE.get(), SneezeParticle::SneezeProvider)

        MenuRegistry.registerScreenFactory(WitcheryMenuTypes.OVEN_MENU_TYPE.get(), ::OvenScreen)
        MenuRegistry.registerScreenFactory(WitcheryMenuTypes.ALTAR_MENU_TYPE.get(), ::AltarScreen)
        MenuRegistry.registerScreenFactory(WitcheryMenuTypes.DISTILLERY_MENU_TYPE.get(), ::DistilleryScreen)
        MenuRegistry.registerScreenFactory(WitcheryMenuTypes.SPINNING_WHEEL_MENU_TYPE.get(), ::SpinningWheelScreen)

        WitcheryPageRendererRegistry.register()

        ClientGuiEvent.RENDER_HUD.register(InfusionHandler::renderInfusionHud)
        ClientGuiEvent.RENDER_HUD.register(ManifestationPlayerAttachment::renderHud)
        ClientGuiEvent.RENDER_HUD.register(VampireEventHandler::renderHud)
        ClientGuiEvent.RENDER_HUD.register(BarkBeltPlayerAttachment::renderHud)

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

        ItemPropertiesRegistry.register(
            WitcheryItems.CHALICE.get(),
            ResourceLocation.fromNamespaceAndPath(MODID, "has_soup")
        ) { stack, _, _, _ ->
            val data = stack.get(WitcheryDataComponents.HAS_SOUP.get()) ?: return@register 0f
            if (data)
                return@register 1f
            0f
        }

        ItemPropertiesRegistry.register(
            WitcheryItems.WINE_GLASS.get(),
            ResourceLocation.fromNamespaceAndPath(MODID, "blood")
        ) { stack, _, _, _ ->
            stack.get(WitcheryDataComponents.BLOOD.get()) ?: return@register 0f
            return@register 1f
        }

        ItemPropertiesRegistry.register(
            WitcheryItems.QUARTZ_SPHERE.get(),
            ResourceLocation.fromNamespaceAndPath(MODID, "has_sun")
        ) { stack, _, _, _ ->
            stack.get(WitcheryDataComponents.HAS_SUN.get()) ?: return@register 0f
            return@register 1f
        }

        ItemPropertiesRegistry.register(
            CANE_SWORD.get(),
            ResourceLocation.fromNamespaceAndPath(MODID, "unsheeted")
        ) { stack, _, _, _ ->
            val bl = stack.has(UNSHEETED.get())
            if (bl) {
                val bl2 = stack.get(UNSHEETED.get())!!
                if (bl2) {
                    return@register 1f
                }
            }

            return@register 0f
        }

        ColorHandlerRegistry.registerBlockColors(
            RitualChalkColors,
            WitcheryBlocks.RITUAL_CHALK_BLOCK.get(),
            WitcheryBlocks.INFERNAL_CHALK_BLOCK.get(),
            WitcheryBlocks.OTHERWHERE_CHALK_BLOCK.get(),
            WitcheryBlocks.SACRIFICIAL_CIRCLE.get()
        )

        ColorHandlerRegistry.registerItemColors(
            PotionColor,
            WitcheryItems.WITCHERY_POTION.get()
        )

        RenderTypeRegistry.register(
            RenderType.translucent(),
            WitcheryFluids.FLOWING_FLOWING_SPIRIT.get(),
            WitcheryFluids.FLOWING_SPIRIT_STILL.get()
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
            WitcheryBlocks.BELLADONNA_CROP.get(),
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
            WitcheryBlocks.DISTILLERY.get(),
            WitcheryBlocks.DEMON_HEART.get(),
            WitcheryBlocks.BLOOD_POPPY.get(),
            WitcheryBlocks.ARTHANA.get(),
            WitcheryBlocks.CHALICE.get(),
            WitcheryBlocks.DISTURBED_COTTON.get(),
            WitcheryBlocks.WISPY_COTTON.get(),
            WitcheryBlocks.SACRIFICIAL_CIRCLE_COMPONENT.get(),
            WitcheryBlocks.SACRIFICIAL_CIRCLE.get(),
            WitcheryBlocks.SUNLIGHT_COLLECTOR.get(),
            WitcheryBlocks.GRASSPER.get(),
            WitcheryBlocks.FLOWING_SPIRIT_BLOCK.get(),
            WitcheryBlocks.BRAZIER.get(),
            WitcheryBlocks.WITCHS_LADDER.get(),
            WitcheryBlocks.TRENT_EFFIGY.get(),
            WitcheryBlocks.SCARECROW.get(),
            WitcheryBlocks.CRITTER_SNARE.get()
        )

        KeyMappingRegistry.register(WitcheryKeyMappings.BROOM_DISMOUNT_KEYMAPPING)
        ClientRawInputEvent.MOUSE_SCROLLED.register(VampireAbilities::scroll)

        ClientTickEvent.CLIENT_POST.register(ClientTickEvent.Client { minecraft: Minecraft? ->
            while (WitcheryKeyMappings.BROOM_DISMOUNT_KEYMAPPING.consumeClick()) {
                minecraft?.player?.stopRiding()
                if (minecraft?.player != null) {
                    NetworkManager.sendToServer(DismountBroomC2SPayload())
                }
            }
        })
    }

    fun id(name: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MODID, name)
    }

    fun logDebugRitual(message: String) {
        if (debugRitualLog) {
            println(message)
        }
    }
}

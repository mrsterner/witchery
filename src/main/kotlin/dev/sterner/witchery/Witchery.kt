package dev.sterner.witchery

import com.mojang.logging.LogUtils
import dev.sterner.witchery.content.entity.*
import dev.sterner.witchery.core.registry.WitcheryAttributes
import dev.sterner.witchery.core.registry.*
import dev.sterner.witchery.features.tarot.TarotEffectEventHandler
import dev.sterner.witchery.integration.modonomicon.WitcheryPageRendererRegistry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.animal.Pig
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.fml.loading.FMLLoader
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import org.slf4j.Logger


@Mod(Witchery.MODID)
class Witchery(modEventBus: IEventBus, modContainer: ModContainer) {

    init {
        modEventBus.addListener(::onEntityAttributeCreation)
        modEventBus.addListener(WitcheryPayloads::onRegisterPayloadHandlers)

        WitcheryArmorMaterials.MATERIALS.register(modEventBus)
        WitcheryBlocks.BLOCKS.register(modEventBus)
        WitcheryBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus)
        WitcheryCommands.COMMAND_ARGUMENTS.register(modEventBus)
        WitcheryCreativeModeTabs.TABS.register(modEventBus)
        WitcheryCurseRegistry.register(modEventBus)
        WitcheryDataComponents.DATA.register(modEventBus)
        WitcheryEntityDataSerializers.SERIALIZERS.register(modEventBus)
        WitcheryEntityTypes.ENTITY_TYPES.register(modEventBus)
        WitcheryFeatures.FEATURES.register(modEventBus)
        WitcheryAttributes.attributes.register(modEventBus)
        WitcheryFetishEffects.register(modEventBus)
        WitcheryFluids.register(modEventBus)
        WitcheryItems.ITEMS.register(modEventBus)
        WitcheryMenuTypes.MENU_TYPES.register(modEventBus)
        WitcheryDataAttachments.ATTACHMENT_TYPES.register(modEventBus)
        WitcheryMobEffects.EFFECTS.register(modEventBus)
        WitcheryModonomiconLoaders.register()
        WitcheryParticleTypes.PARTICLES.register(modEventBus)
        WitcheryPoppetRegistry.register(modEventBus)
        WitcheryRecipeTypes.RECIPE_TYPES.register(modEventBus)
        WitcheryRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus)
        WitcheryRitualRegistry.register(modEventBus)
        WitcheryBrazierRegistry.register(modEventBus)
        WitcherySounds.SOUNDS.register(modEventBus)
        WitcheryVillagers.POIS.register(modEventBus)
        WitcheryVillagers.PROFESSIONS.register(modEventBus)
        WitcheryStructureProcessors.STRUCTURE_PROCESSOR_TYPES.register(modEventBus)
        WitcherySpecialPotionEffects.register(modEventBus)
        WitcheryTarotEffects.register(modEventBus)

        WitcheryLootInjects.LOOT_MODIFIERS.register(modEventBus)

        WitcheryPageRendererRegistry.register()

        NeoForge.EVENT_BUS.register(WitcheryNeoForgeEvents)
        NeoForge.EVENT_BUS.register(TarotEffectEventHandler)

        modContainer.registerConfig(ModConfig.Type.COMMON, WitcheryConfig.SPEC)
    }

    @SubscribeEvent
    fun onEntityAttributeCreation(event: EntityAttributeCreationEvent) {
        event.put(WitcheryEntityTypes.MANDRAKE.get(), MandrakeEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.IMP.get(), ImpEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.DEMON.get(), DemonEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.OWL.get(), OwlEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.ENT.get(), EntEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.BANSHEE.get(), BansheeEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.POLTERGEIST.get(), PoltergeistEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.SPIRIT.get(), PoltergeistEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.SPECTRAL_PIG.get(), Pig.createAttributes().build())
        event.put(WitcheryEntityTypes.NIGHTMARE.get(), NightmareEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.VAMPIRE.get(), VampireEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.WEREWOLF.get(), WerewolfEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.LILITH.get(), LilithEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.COVEN_WITCH.get(), CovenWitchEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.ELLE.get(), ElleEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.PARASITIC_LOUSE.get(), ParasiticLouseEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.INSANITY.get(), InsanityEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.BABA_YAGA.get(), BabaYagaEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.DEATH.get(), DeathEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.HORNED_HUNTSMAN.get(), HornedHuntsmanEntity.createAttributes().build())
    }

    companion object {
        const val MODID: String = "witchery"
        val LOGGER: Logger = LogUtils.getLogger()

        fun id(path: String) = ResourceLocation.fromNamespaceAndPath(MODID, path)
        val debugRitualLog: Boolean = !FMLEnvironment.production


        fun logDebugRitual(message: String) {
            if (debugRitualLog) {
                println(message)
            }
        }

        fun useDebugBoxRender(): Boolean {
            return !FMLLoader.isProduction()
        }
    }
}


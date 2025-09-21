package dev.sterner.witchery

import com.mojang.logging.LogUtils
import dev.sterner.witchery.data_attachment.PlatformUtils
import dev.sterner.witchery.entity.BabaYagaEntity
import dev.sterner.witchery.entity.BansheeEntity
import dev.sterner.witchery.entity.CovenWitchEntity
import dev.sterner.witchery.entity.DeathEntity
import dev.sterner.witchery.entity.DemonEntity
import dev.sterner.witchery.entity.ElleEntity
import dev.sterner.witchery.entity.EntEntity
import dev.sterner.witchery.entity.HornedHuntsmanEntity
import dev.sterner.witchery.entity.ImpEntity
import dev.sterner.witchery.entity.InsanityEntity
import dev.sterner.witchery.entity.LilithEntity
import dev.sterner.witchery.entity.MandrakeEntity
import dev.sterner.witchery.entity.NightmareEntity
import dev.sterner.witchery.entity.OwlEntity
import dev.sterner.witchery.entity.ParasiticLouseEntity
import dev.sterner.witchery.entity.SpectreEntity
import dev.sterner.witchery.entity.VampireEntity
import dev.sterner.witchery.entity.WerewolfEntity
import dev.sterner.witchery.registry.WitcheryArmorMaterials
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryCommands
import dev.sterner.witchery.registry.WitcheryCreativeModeTabs
import dev.sterner.witchery.registry.WitcheryCurseRegistry
import dev.sterner.witchery.registry.WitcheryDamageSources
import dev.sterner.witchery.registry.WitcheryDataAttachments
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryEntityDataSerializers
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryFeatures
import dev.sterner.witchery.registry.WitcheryFetishEffects
import dev.sterner.witchery.registry.WitcheryFlammability
import dev.sterner.witchery.registry.WitcheryFluids
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryKeyMappings
import dev.sterner.witchery.registry.WitcheryLootInjects
import dev.sterner.witchery.registry.WitcheryMenuTypes
import dev.sterner.witchery.registry.WitcheryMobEffects
import dev.sterner.witchery.registry.WitcheryModonomiconLoaders
import dev.sterner.witchery.registry.WitcheryParticleTypes
import dev.sterner.witchery.registry.WitcheryPayloads
import dev.sterner.witchery.registry.WitcheryPoppetRegistry
import dev.sterner.witchery.registry.WitcheryRecipeSerializers
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import dev.sterner.witchery.registry.WitcheryRenderTypes
import dev.sterner.witchery.registry.WitcheryRitualRegistry
import dev.sterner.witchery.registry.WitcherySounds
import dev.sterner.witchery.registry.WitcherySpecialPotionEffects
import net.minecraft.core.NonNullList
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.animal.Pig
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ResolvableProfile
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import org.slf4j.Logger

@Mod(Witchery.MODID)
class Witchery(modEventBus: IEventBus, modContainer: ModContainer) {

    init {
        modEventBus.addListener(::commonSetup)
        modEventBus.addListener(::onEntityAttributeCreation)
        modEventBus.addListener(WitcheryPayloads::onRegisterPayloadHandlers)

        WitcheryArmorMaterials.MATERIALS.register(modEventBus)
        WitcheryBlocks.BLOCKS.register(modEventBus)
        WitcheryBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus)
        WitcheryCommands.COMMAND_ARGUMENTS.register(modEventBus)
        WitcheryCreativeModeTabs.TABS.register(modEventBus)
        WitcheryCurseRegistry.CURSES.register(modEventBus)
        WitcheryDataComponents.DATA.register(modEventBus)
        WitcheryEntityDataSerializers.SERIALIZERS.register(modEventBus)
        WitcheryEntityTypes.ENTITY_TYPES.register(modEventBus)
        WitcheryFeatures.FEATURES.register(modEventBus)
        WitcheryFetishEffects.FETISH_EFFECTS.register(modEventBus)
        WitcheryFluids.register(modEventBus)
        WitcheryItems.ITEMS.register(modEventBus)
        WitcheryMenuTypes.MENU_TYPES.register(modEventBus)
        WitcheryDataAttachments.ATTACHMENT_TYPES.register(modEventBus)
        WitcheryMobEffects.EFFECTS.register(modEventBus)
        WitcheryModonomiconLoaders.register()
        WitcheryParticleTypes.PARTICLES.register(modEventBus)
        WitcheryPoppetRegistry.POPPETS.register(modEventBus)
        WitcheryRecipeTypes.RECIPE_TYPES.register(modEventBus)
        WitcheryRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus)
        WitcheryRitualRegistry.RITUALS.register(modEventBus)
        WitcherySounds.SOUNDS.register(modEventBus)
        WitcherySpecialPotionEffects.SPECIALS.register(modEventBus)

        NeoForge.EVENT_BUS.register(WitcheryNeoForgeEvents)
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC)
    }

    private fun commonSetup(event: FMLCommonSetupEvent) {

    }

    @SubscribeEvent
    fun onEntityAttributeCreation(event: EntityAttributeCreationEvent) {
        event.put(WitcheryEntityTypes.MANDRAKE.get(), MandrakeEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.IMP.get(), ImpEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.DEMON.get(), DemonEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.OWL.get(), OwlEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.ENT.get(), EntEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.BANSHEE.get(), BansheeEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.SPECTRE.get(), SpectreEntity.createAttributes().build())
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
        val debugRitualLog: Boolean = PlatformUtils.isDevEnv()


        fun logDebugRitual(message: String) {
            if (debugRitualLog) {
                println(message)
            }
        }
    }
}


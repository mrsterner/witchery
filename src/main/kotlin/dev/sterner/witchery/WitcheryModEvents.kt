package dev.sterner.witchery

import dev.sterner.witchery.core.registry.WitcheryAttributes
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.core.registry.WitcheryCurseRegistry
import dev.sterner.witchery.core.registry.WitcheryFetishEffects
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.registry.WitcheryKeyMappings
import dev.sterner.witchery.core.registry.WitcheryPoppetRegistry
import dev.sterner.witchery.core.registry.WitcheryRitualRegistry
import dev.sterner.witchery.core.registry.WitcherySpecialPotionEffects
import dev.sterner.witchery.core.registry.WitcheryTarotEffects
import dev.sterner.witchery.core.registry.WitcheryVillagers
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.npc.VillagerTrades
import net.minecraft.world.item.Items
import net.minecraft.world.item.trading.ItemCost
import net.minecraft.world.item.trading.MerchantOffer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent
import net.neoforged.neoforge.event.village.VillagerTradesEvent
import net.neoforged.neoforge.registries.NewRegistryEvent

@EventBusSubscriber(modid = Witchery.MODID)
object WitcheryModEvents {

    @SubscribeEvent
    fun registerRegistries(event: NewRegistryEvent) {
        event.register(WitcheryPoppetRegistry.POPPET_REGISTRY)
        event.register(WitcheryCurseRegistry.CURSES_REGISTRY)
        event.register(WitcheryRitualRegistry.RITUAL_REGISTRY)
        event.register(WitcherySpecialPotionEffects.SPECIAL_REGISTRY)
        event.register(WitcheryFetishEffects.FETISH_REGISTRY)
        event.register(WitcheryTarotEffects.TAROT_REGISTRY)
    }

    @SubscribeEvent
    fun onRegisterCapabilities(event: RegisterCapabilitiesEvent) {
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            WitcheryBlockEntityTypes.CAULDRON.get()
        ) { be, _ ->
            be.fluidTank
        }
    }

    @SubscribeEvent
    fun onRegisterKey(event: RegisterKeyMappingsEvent) {
        event.register(WitcheryKeyMappings.BROOM_DISMOUNT_KEYMAPPING)
        event.register(WitcheryKeyMappings.OPEN_ABILITY_SELECTION)
        event.register(WitcheryKeyMappings.UTILITY_BUTTON)
    }

    @SubscribeEvent
    fun modifyAttributes(event: EntityAttributeModificationEvent) {
        event.add(EntityType.PLAYER, WitcheryAttributes.VAMPIRE_BAT_FORM_DURATION)
        event.add(EntityType.PLAYER, WitcheryAttributes.VAMPIRE_SUN_RESISTANCE)
        event.add(EntityType.PLAYER, WitcheryAttributes.VAMPIRE_DRINK_SPEED)
    }


}
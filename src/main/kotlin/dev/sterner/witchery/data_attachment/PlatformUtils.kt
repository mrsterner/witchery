package dev.sterner.witchery.data_attachment

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.mixin.ArgumentTypeInfosInvoker
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.WoodType
import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.common.NeoForgeMod
import top.theillusivec4.curios.api.CuriosApi

object PlatformUtils {

    @JvmStatic
    fun isDevEnv(): Boolean {
        return !FMLEnvironment.production
    }

    @JvmStatic
    fun allEquippedAccessories(livingEntity: Player): List<ItemStack> {
        val curioInventory = CuriosApi.getCuriosInventory(livingEntity).orElse(null) ?: return emptyList()

        val equippedCurios = curioInventory.equippedCurios
        val equippedItems = mutableListOf<ItemStack>()

        for (slotIndex in 0 until equippedCurios.slots) {
            val itemStack = equippedCurios.getStackInSlot(slotIndex)
            if (!itemStack.isEmpty) {
                equippedItems.add(itemStack)
            }
        }

        return equippedItems
    }

    @JvmStatic
    fun tryEnableBatFlight(player: Player) {
        if (!player.isCreative && !player.isSpectator) {
            val flightAttribute = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT)
            if (flightAttribute != null && flightAttribute.value <= 0) {
                flightAttribute.addPermanentModifier(
                    AttributeModifier(
                        Witchery.id("bat_flight"),
                        1.0,
                        AttributeModifier.Operation.ADD_VALUE
                    )
                )
            }

            if (!player.onGround()) {
                player.abilities.flying = true
                player.onUpdateAbilities()
            }
        }
    }


    @JvmStatic
    fun tryDisableBatFlight(player: Player) {
        if (!player.isCreative && !player.isSpectator) {
            val flightAttribute = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT)
            flightAttribute?.removeModifier(Witchery.id("bat_flight"))

            player.abilities.flying = false
            player.onUpdateAbilities()
        }
    }


    @JvmStatic
    fun registerWoodType(woodType: WoodType): WoodType {
        return WoodType.register(woodType)
    }

    @JvmStatic
    fun getByClass(): MutableMap<Class<*>, ArgumentTypeInfo<*, *>> {
        return ArgumentTypeInfosInvoker.getByClass()
    }
}
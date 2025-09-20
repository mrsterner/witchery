package dev.sterner.witchery.data_attachment

import dev.sterner.witchery.item.BoneNeedleItem
import dev.sterner.witchery.item.HunterArmorItem
import dev.sterner.witchery.item.VampireArmorItem
import dev.sterner.witchery.item.WitchesRobesItem
import dev.sterner.witchery.item.accessories.*
import dev.sterner.witchery.mixin.ArgumentTypeInfosInvoker
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.WoodType
import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLEnvironment
import top.theillusivec4.curios.api.CuriosApi

object PlatformUtils {

    @JvmStatic
    fun isDevEnv(): Boolean {
        return !FMLEnvironment.production
    }

    @JvmStatic
    fun isModLoaded(modId: String?): Boolean {
        return ModList.get().isLoaded(modId)
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
            if (!player.onGround()) {
                player.abilities.flying = true
                player.abilities.mayfly = true
                player.onUpdateAbilities()
            } else {
                player.abilities.mayfly = true
                player.abilities.flying = false
                player.onUpdateAbilities()
            }
        }
    }


    @JvmStatic
    fun tryDisableBatFlight(player: Player) {
        if (!player.isCreative && !player.isSpectator) {
            player.abilities.flying = false
            player.abilities.mayfly = false
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
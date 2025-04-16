package dev.sterner.witchery.platform.neoforge

import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.item.BoneNeedleItem
import dev.sterner.witchery.item.accessories.*
import dev.sterner.witchery.neoforge.item.HunterArmorItemNeoForge
import dev.sterner.witchery.neoforge.item.VampireArmorItemNeoForge
import dev.sterner.witchery.neoforge.item.WitchesRobesItemNeoForge
import dev.sterner.witchery.neoforge.item.curios.*
import dev.sterner.witchery.neoforge.mixin.ArgumentTypeInfosInvoker
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.commands.synchronization.ArgumentTypeInfos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.WoodType
import net.neoforged.fml.ModList
import top.theillusivec4.curios.api.CuriosApi
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler

object PlatformUtilsImpl {

    @JvmStatic
    fun isModLoaded(modId: String?): Boolean {
        return ModList.get().isLoaded(modId)
    }

    @JvmStatic
    fun getBoneNeedle(): BoneNeedleItem {
        return BoneNeedleItemForge(Item.Properties())
    }


    @JvmStatic
    fun getBarkBeltItem(): BarkBeltItem {
        return BarkBeltItemNeoForge(Item.Properties())
    }

    @JvmStatic
    fun getMoonCharmItem(): MoonCharmItem {
        return MoonCharmItemNeoForge(Item.Properties())
    }

    @JvmStatic
    fun getBatwingPendantItem(): BatwingPendantItem {
        return BatwingPendantItemNeoForge(Item.Properties())
    }

    @JvmStatic
    fun getBitingBeltItem(): BitingBeltItem {
        return BitingBeltItemNeoForge(Item.Properties())
    }

    @JvmStatic
    fun getBloodstonePendantItem(): BloodstonePendantItem {
        return BloodstonePendantItemNeoForge(Item.Properties())
    }

    @JvmStatic
    fun getSunstonePendantItem(): SunstonePendantItem {
        return SunstonePendantItemNeoForge(Item.Properties())
    }

    @JvmStatic
    fun getDreamweaverCharmItem(): DreamweaverCharmItem {
        return DreamCharmItemNeoForge(Item.Properties())
    }

    @JvmStatic
    fun witchesRobes(
        witchesRobes: RegistrySupplier<ArmorMaterial>,
        chestplate: ArmorItem.Type,
        properties: Item.Properties
    ): ArmorItem {
        return WitchesRobesItemNeoForge(witchesRobes, chestplate, properties)
    }

    @JvmStatic
    fun hunterArmor(
        witchesRobes: RegistrySupplier<ArmorMaterial>,
        chestplate: ArmorItem.Type,
        properties: Item.Properties
    ): ArmorItem {
        return HunterArmorItemNeoForge(witchesRobes, chestplate, properties)
    }

    @JvmStatic
    fun dapper(
        dapper: RegistrySupplier<ArmorMaterial>,
        chestplate: ArmorItem.Type,
        properties: Item.Properties
    ): ArmorItem {
        return VampireArmorItemNeoForge(dapper, chestplate, properties)
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
            player.abilities.flying = true
            player.abilities.mayfly = true
            player.onUpdateAbilities()
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
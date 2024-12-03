package dev.sterner.witchery.platform.neoforge

import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.item.BoneNeedleItem
import dev.sterner.witchery.neoforge.curios.*
import dev.sterner.witchery.neoforge.item.HunterArmorItemNeoForge
import dev.sterner.witchery.neoforge.item.VampireArmorItemNeoForge
import dev.sterner.witchery.neoforge.item.WitchesRobesItemNeoForge
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.fml.ModList
import net.neoforged.neoforge.items.IItemHandlerModifiable
import top.theillusivec4.curios.api.CuriosApi


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
    fun barkBelt(
        properties: Item.Properties
    ): Item {
        return BarkBeltItemNeoforge(properties)
    }

    @JvmStatic
    fun batwingPendant(
        properties: Item.Properties
    ): Item {
        return BatwingPendantItemNeoforge(properties)
    }

    @JvmStatic
    fun bitingBelt(
        properties: Item.Properties
    ): Item {
        return BitingBeltItemNeoforge(properties)
    }

    @JvmStatic
    fun bloodstonePendant(
        properties: Item.Properties
    ): Item {
        return BloodstonePendantItemNeoforge(properties)
    }

    @JvmStatic
    fun sunstonePendant(
        properties: Item.Properties
    ): Item {
        return SunstonePendantItemNeoforge(properties)
    }

    @JvmStatic
    fun dreamweaverCharm(
        properties: Item.Properties
    ): Item {
        return DreamweaverCharmItemNeoforge(properties)
    }

    @JvmStatic
    fun getAllEquippedAccessories(living: LivingEntity): List<ItemStack> {
        val optional = CuriosApi.getCuriosInventory(living)
        val stacks = ArrayList<ItemStack>()
        if (optional.isPresent) {
            val handler: IItemHandlerModifiable = optional.get().equippedCurios
            for (i in 0 until handler.slots) {
                stacks.add(handler.getStackInSlot(i))
            }
        }
        return stacks
    }
}
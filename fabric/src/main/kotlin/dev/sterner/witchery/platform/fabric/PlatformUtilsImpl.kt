package dev.sterner.witchery.platform.fabric

import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.item.BoneNeedleItem
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Item


object PlatformUtilsImpl {

    @JvmStatic
    fun isModLoaded(modId: String?): Boolean {
        return FabricLoader.getInstance().isModLoaded(modId)
    }

    @JvmStatic
    fun getBoneNeedle() : BoneNeedleItem {
        return BoneNeedleItemFabric(Item.Properties())
    }

    @JvmStatic
    fun witchesRobes(witchesRobes: RegistrySupplier<ArmorMaterial>, chestplate: ArmorItem.Type, properties: Item.Properties): ArmorItem {
        return ArmorItem(witchesRobes, chestplate, properties)
    }
}
package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.item.BoneNeedleItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.neoforged.fml.ModList

object PlatformUtilsImpl {

    @JvmStatic
    fun isModLoaded(modId: String?): Boolean {
        return ModList.get().isLoaded(modId)
    }

    @JvmStatic
    fun getBoneNeedle() : BoneNeedleItem {
        return BoneNeedleItemForge(Item.Properties())
    }
}
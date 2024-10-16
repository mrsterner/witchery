package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.item.BoneNeedleItem
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties


object PlatformUtilsImpl {

    @JvmStatic
    fun isModLoaded(modId: String?): Boolean {
        return FabricLoader.getInstance().isModLoaded(modId)
    }

    @JvmStatic
    fun getBoneNeedle() : BoneNeedleItem {
        return BoneNeedleItemFabric(Item.Properties())
    }
}
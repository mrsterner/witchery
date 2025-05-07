package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.EtherealEntityAttachment
import net.minecraft.world.entity.LivingEntity

object EtherealEntityAttachmentImpl {
    @JvmStatic
    fun getData(livingEntity: LivingEntity): EtherealEntityAttachment.Data {
        return livingEntity.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.ETHEREAL_DATA_TYPE)
    }

    @JvmStatic
    fun setData(livingEntity: LivingEntity, data: EtherealEntityAttachment.Data) {
        livingEntity.setAttached(WitcheryFabricAttachmentRegistry.ETHEREAL_DATA_TYPE, data)
        EtherealEntityAttachment.sync(livingEntity, data)
    }
}
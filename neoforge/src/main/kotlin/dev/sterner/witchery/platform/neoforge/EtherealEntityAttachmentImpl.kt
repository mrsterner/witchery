package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import dev.sterner.witchery.platform.EtherealEntityAttachment
import net.minecraft.world.entity.LivingEntity

object EtherealEntityAttachmentImpl {
    @JvmStatic
    fun getData(livingEntity: LivingEntity): EtherealEntityAttachment.Data {
        return livingEntity.getData(WitcheryNeoForgeAttachmentRegistry.ETHEREAL_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(livingEntity: LivingEntity, data: EtherealEntityAttachment.Data) {
        livingEntity.setData(WitcheryNeoForgeAttachmentRegistry.ETHEREAL_DATA_ATTACHMENT, data)
        EtherealEntityAttachment.sync(livingEntity, data)
    }
}
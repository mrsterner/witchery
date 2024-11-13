package dev.sterner.witchery.platform.transformation.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.BLOOD_LIVING_ENTITY_DATA_ATTACHMENT
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import net.minecraft.world.entity.LivingEntity

object BloodPoolLivingEntityAttachmentImpl {

    @JvmStatic
    fun getData(livingEntity: LivingEntity): BloodPoolLivingEntityAttachment.Data {
        return livingEntity.getData(BLOOD_LIVING_ENTITY_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(livingEntity: LivingEntity, data: BloodPoolLivingEntityAttachment.Data) {
        livingEntity.setData(BLOOD_LIVING_ENTITY_DATA_ATTACHMENT, data)
        BloodPoolLivingEntityAttachment.sync(livingEntity, data)
    }
}
package dev.sterner.witchery.platform.transformation.fabric

import dev.sterner.witchery.data.BloodPoolHandler
import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity

object BloodPoolLivingEntityAttachmentImpl {

    @JvmStatic
    fun getData(livingEntity: LivingEntity): BloodPoolLivingEntityAttachment.Data {
        return livingEntity.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.BLOOD_LIVING_DATA_TYPE)
    }

    @JvmStatic
    fun setData(livingEntity: LivingEntity, data: BloodPoolLivingEntityAttachment.Data) {
        livingEntity.setAttached(WitcheryFabricAttachmentRegistry.BLOOD_LIVING_DATA_TYPE, data)
        BloodPoolLivingEntityAttachment.sync(livingEntity, data)
    }
}
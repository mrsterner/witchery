package dev.sterner.witchery.platform.voodoo

import dev.sterner.witchery.neoforge.WitcheryNeoForge.POPPET_DATA_ATTACHMENT
import dev.sterner.witchery.platform.poppet.PoppetData
import dev.sterner.witchery.platform.poppet.PoppetDataAttachment
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object PoppetDataAttachmentImpl {

    @JvmStatic
    fun getPoppetData(livingEntity: LivingEntity): PoppetData{
        return livingEntity.getData(POPPET_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setPoppetData(livingEntity: LivingEntity, data: PoppetData) {
        livingEntity.setData(POPPET_DATA_ATTACHMENT, data)
        if (livingEntity is Player) {
            PoppetDataAttachment.sync(livingEntity, data)
        }
    }
}
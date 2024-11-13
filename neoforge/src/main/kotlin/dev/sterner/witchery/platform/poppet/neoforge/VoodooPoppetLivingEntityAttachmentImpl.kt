package dev.sterner.witchery.platform.poppet.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object VoodooPoppetLivingEntityAttachmentImpl {

    @JvmStatic
    fun setPoppetData(livingEntity: LivingEntity, data: VoodooPoppetLivingEntityAttachment.VoodooPoppetData) {
        livingEntity.setData(WitcheryNeoForgeAttachmentRegistry.VOODOO_POPPET_DATA_ATTACHMENT, data)
        if (livingEntity is Player) {
            VoodooPoppetLivingEntityAttachment.sync(livingEntity, data)
        }
    }

    @JvmStatic
    fun getPoppetData(livingEntity: LivingEntity): VoodooPoppetLivingEntityAttachment.VoodooPoppetData {
        return livingEntity.getData(WitcheryNeoForgeAttachmentRegistry.VOODOO_POPPET_DATA_ATTACHMENT)
    }
}
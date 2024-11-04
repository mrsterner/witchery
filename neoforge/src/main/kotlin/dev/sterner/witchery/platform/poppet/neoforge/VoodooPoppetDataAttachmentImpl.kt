package dev.sterner.witchery.platform.poppet.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import dev.sterner.witchery.platform.poppet.VoodooPoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetDataAttachment
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object VoodooPoppetDataAttachmentImpl {

    @JvmStatic
    fun setPoppetData(livingEntity: LivingEntity, data: VoodooPoppetData){
        livingEntity.setData(WitcheryNeoForgeAttachmentRegistry.VOODOO_POPPET_DATA_ATTACHMENT, data)
        if (livingEntity is Player) {
            VoodooPoppetDataAttachment.sync(livingEntity, data)
        }
    }

    @JvmStatic
    fun getPoppetData(livingEntity: LivingEntity): VoodooPoppetData {
        return livingEntity.getData(WitcheryNeoForgeAttachmentRegistry.VOODOO_POPPET_DATA_ATTACHMENT)
    }
}
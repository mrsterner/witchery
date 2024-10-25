package dev.sterner.witchery.platform.poppet.fabric

import dev.sterner.witchery.fabric.WitcheryFabric.Companion.VOODOO_POPPET_DATA_TYPE
import dev.sterner.witchery.platform.poppet.VoodooPoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetDataAttachment
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object VoodooPoppetDataAttachmentImpl {

    @JvmStatic
    fun getPoppetData(livingEntity: LivingEntity): VoodooPoppetData{
        return livingEntity.getAttachedOrCreate(VOODOO_POPPET_DATA_TYPE)
    }

    @JvmStatic
    fun setPoppetData(livingEntity: LivingEntity, data: VoodooPoppetData) {
        livingEntity.setAttached(VOODOO_POPPET_DATA_TYPE, data)
        if (livingEntity is Player) {
            VoodooPoppetDataAttachment.sync(livingEntity, data)
        }
    }
}
package dev.sterner.witchery.platform.poppet.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object VoodooPoppetLivingEntityAttachmentImpl {

    @JvmStatic
    fun getPoppetData(livingEntity: LivingEntity): VoodooPoppetLivingEntityAttachment.VoodooPoppetData {
        return livingEntity.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.VOODOO_POPPET_DATA_TYPE)
    }

    @JvmStatic
    fun setPoppetData(livingEntity: LivingEntity, data: VoodooPoppetLivingEntityAttachment.VoodooPoppetData) {
        livingEntity.setAttached(WitcheryFabricAttachmentRegistry.VOODOO_POPPET_DATA_TYPE, data)
        if (livingEntity is Player) {
            VoodooPoppetLivingEntityAttachment.sync(livingEntity, data)
        }
    }
}
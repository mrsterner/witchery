package dev.sterner.witchery.platform.poppet.fabric

import dev.sterner.witchery.fabric.WitcheryFabric.Companion.POPPET_DATA_TYPE
import dev.sterner.witchery.fabric.WitcheryFabric.Companion.VOODOO_POPPET_DATA_TYPE
import dev.sterner.witchery.platform.poppet.PoppetData
import dev.sterner.witchery.platform.poppet.PoppetDataAttachment
import dev.sterner.witchery.platform.poppet.VoodooPoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetDataAttachment
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object PoppetDataAttachmentImpl {

    @JvmStatic
    fun getPoppetData(livingEntity: LivingEntity): PoppetData{
        return livingEntity.getAttachedOrCreate(POPPET_DATA_TYPE)
    }

    @JvmStatic
    fun setPoppetData(livingEntity: LivingEntity, data: PoppetData) {
        livingEntity.setAttached(POPPET_DATA_TYPE, data)
        if (livingEntity is Player) {
            PoppetDataAttachment.sync(livingEntity, data)
        }
    }
}